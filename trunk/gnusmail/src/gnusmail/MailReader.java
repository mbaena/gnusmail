/*
 * Copyright 2011 Universidad de Málaga.  All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 3 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 3 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 3 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Universidad de Málaga, 29071 Malaga, Spain or visit
 * www.uma.es if you need additional information or have any questions.
 * 
 */
package gnusmail;

import gnusmail.core.cnx.Connection;
import gnusmail.core.cnx.Document;
import gnusmail.core.cnx.MailMessage;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;

//TODO: deberia haber una factory para crear MailReader y otros tipos de readers
public class MailReader implements DocumentReader {
	// Used to keep track of partially unprocessed folders. When a folder
	// is totally processed, it's connection can be closed
	// Map<String, Integer> mailsToReadFromFolders;

	private class ComparableMessage implements Comparable<ComparableMessage> {

		private Message message;
		private Date date;

		public ComparableMessage(Message msg) {
			this.message = msg;
			try {
				this.date = msg.getReceivedDate();
			} catch (MessagingException e) {
				e.printStackTrace();
			}
		}

		public Message getMessage() {
			return message;
		}

		public int compareTo(ComparableMessage o) {
			return this.date.compareTo(o.date);
		}

		public Folder getFolder() {
			return message.getFolder();
		}
	}

	private class LimitedMessageReaderIterator implements Iterator<Document> {

		int limOpenFolders = 5;
		List<Folder> openFolders = new ArrayList<Folder>();
		TreeSet<ComparableMessage> message_list;
		int numberOfNexts = 0;

		// TODO imprimir cuantos mensajes hay?
		public LimitedMessageReaderIterator(Connection connection, int limit) {
			System.out.println("El limite es " + limit);
			message_list = new TreeSet<ComparableMessage>();
			long total_msgs = 0;
			Folder[] folders = null;
			try {
				folders = connection.getFolders();
				// folders = cleanFolders(folders);
			} catch (MessagingException e) {
				e.printStackTrace();
			}
			System.out.println("Transversing folders");
			for (Folder folder : folders) {
				if (!folder.getName().toLowerCase().contains("sent")
						&& !folder.getName().toLowerCase().contains("antiguos")
						&& !folder.getName().toLowerCase().contains("trash")) {
					try {
						if (!folder.isOpen()) {
							if (openFolders.size() == limOpenFolders) {
								if (openFolders.get(0).isOpen()) {
									openFolders.get(0).close(false);
								}
								openFolders.remove(0);
							}
							openFolders.add(folder);
							folder.open(javax.mail.Folder.READ_ONLY);
						}
						int msg_count = folder.getMessageCount();
						total_msgs += msg_count;
						if (msg_count <= 0) {
							continue;
						}
						int first_msg = msg_count - limit + 1;
						if (limit > 0 && first_msg < 1) {
							first_msg = 1;
						} else if (limit <= 0) {
							first_msg = 1;
						}
						Message msg = folder.getMessage(first_msg);
						ComparableMessage comparableMsg = new ComparableMessage(
								msg);
						message_list.add(comparableMsg);
					} catch (MessagingException e) {
						e.printStackTrace();
					}
				} else {
					System.out.println("Desechada: " + folder.getFullName());
				}
			}
			while (openFolders.size() > 0) {
				if (openFolders.get(0).isOpen()) {
					try {
						openFolders.get(0).close(false);
					} catch (MessagingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				openFolders.remove(0);
			}
			System.out.println("End of transversing folders");
		}

		public boolean hasNext() {
			if (message_list.isEmpty()) {
				while (openFolders.size() > 0) {
					if (openFolders.get(0).isOpen()) {
						try {
							openFolders.get(0).close(false);
						} catch (MessagingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					openFolders.remove(0);
				}
			}
			return !message_list.isEmpty();
		}

		public Document next() {
			numberOfNexts++;
			System.out.println("---------- Seen messages " + numberOfNexts);
			ComparableMessage comparableMsg = message_list.pollFirst();
			Message msg = comparableMsg.getMessage();
			Folder folder = msg.getFolder();
			int number = msg.getMessageNumber();
			try {
				if (!folder.isOpen()) {
					if (openFolders.size() == limOpenFolders) {
						if (openFolders.get(0).isOpen()) {
							openFolders.get(0).close(false);
						}
						openFolders.remove(0);
					}
					openFolders.add(folder);
					folder.open(javax.mail.Folder.READ_ONLY);
				}
				if (number < folder.getMessageCount()) {
					Message nextMsg = folder.getMessage(number + 1);
					ComparableMessage nextComparableMsg = new ComparableMessage(
							nextMsg);
					message_list.add(nextComparableMsg);
				}
			} catch (MessagingException e) {
				e.printStackTrace();
			}
			return new MailMessage(msg);
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}

	}

	private Connection connection;
	private int limit;

	public MailReader() {

	}

	public MailReader(Connection connection, int limit) {
		this.connection = connection;
		this.limit = limit;
	}

	public Iterator<Document> iterator() {
		Iterator<Document> iterator;
		iterator = new LimitedMessageReaderIterator(connection, limit);
		return iterator;
	}
}
