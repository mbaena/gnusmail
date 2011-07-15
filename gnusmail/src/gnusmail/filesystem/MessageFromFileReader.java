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
package gnusmail.filesystem;

import gnusmail.SortableMessage;
import gnusmail.datasource.Document;
import gnusmail.datasource.DocumentReader;
import gnusmail.datasource.mailconnection.MIMEMessageWithFolder;
import gnusmail.datasource.mailconnection.MailMessage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;

/**
 * TODO paramtro
 * @author jmcarmona
 */
public class MessageFromFileReader implements DocumentReader {

	private class MessagesFromFileIterator implements Iterator<Document> {

		int index = 0;
		FolderMessagesIterator it;
		List<SortableMessage> messages = null;
		Map<String, Integer> cuentasPorCarpeta = new TreeMap<String, Integer>();
		private int baseFolderLength;

		private void createMessagesList() {
			System.out.print("Initializing message list...");
			SortedSet<SortableMessage> sortedMessages = new TreeSet<SortableMessage>();
			while (it.hasNext()) {
				File f = it.next();
				if (f != null) {
					Message msg = convertFileToMessage(f);
					String folder = ((MIMEMessageWithFolder) msg).getFolderAsStr();
					sortedMessages.add(new SortableMessage(msg));
					if (!cuentasPorCarpeta.containsKey(folder)) {
						cuentasPorCarpeta.put(folder, 0);
					}
					cuentasPorCarpeta.put(folder, cuentasPorCarpeta.get(folder) + 1);
				}
			}
			this.messages = new ArrayList(sortedMessages);
			for (String fold : cuentasPorCarpeta.keySet()) {
				System.out.println(fold + " tiene " + cuentasPorCarpeta.get(fold));
			}
			System.out.println("done. We have : " + sortedMessages.size());
		}

		public MessagesFromFileIterator(String baseFolder, boolean recursive, int limit) {
			this.baseFolderLength = baseFolder.length() + 1;
			FilesReader fr = new FilesReader(baseFolder, recursive, limit);
			it = (FolderMessagesIterator) fr.iterator();
			createMessagesList();
		}

		public boolean hasNext() {
			boolean res = index < messages.size();
			return res;
		}

		public Document next() {
			Message res = messages.get(index).getMesssage();
			index++;
			Document doc = new MailMessage(res);
			return doc;
		}

		public void remove() {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		/**
		 * This method accepts a file, and transforms it to a MessageInfo,
		 * if possible
		 * @param f
		 * @return
		 */
		//TODO habria que hacer una clase que heredara de Message, y que tuviera una carpeta
		private Message convertFileToMessage(File f) {
			String parentFolder = f.getParentFile().getPath();
			InputStream is = null;
			Message m = null;
			try {
				Session s = null;
				is = new FileInputStream(f);
				m = new MIMEMessageWithFolder(s, is);
				System.out.println("MessageFromFileRead");
				if (baseFolderLength < parentFolder.length()) {
					System.out.println("Case 1");
					((MIMEMessageWithFolder) m).setFolderAsStr(parentFolder.substring(baseFolderLength, parentFolder.length()));
					//((MIMEMessageWithFolder) m).setFolderAsStr(parentFolder);
					System.out.println(((MIMEMessageWithFolder) m).getFolderAsStr());
				} else {
					System.out.println("Case 2");
					((MIMEMessageWithFolder) m).setFolderAsStr("/");
				}				
			} catch (MessagingException ex) {
				Logger.getLogger(MessageFromFileReader.class.getName()).log(Level.SEVERE, null, ex);
			} catch (FileNotFoundException ex) {
				Logger.getLogger(MessageFromFileReader.class.getName()).log(Level.SEVERE, null, ex);
			} finally {
				try {
					is.close();
				} catch (IOException ex) {
					Logger.getLogger(MessageFromFileReader.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
			return m;
		}
	}
	private String baseFolder;
	private boolean recursive = false;
	private int limit = 0;

	public MessageFromFileReader(String baseFolder, int limit) {
		this.baseFolder = baseFolder;
		this.recursive = true;
		this.limit = limit;

	}

	public MessageFromFileReader(String baseFolder, boolean recursive, int limit) {
		this.baseFolder = baseFolder;
		this.recursive = recursive;
		this.limit = limit;
	}

	@Override
	public Iterator<Document> iterator() {
		return new MessagesFromFileIterator(baseFolder, recursive, limit);
	}
}
