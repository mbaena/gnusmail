package gnusmail;

import gnusmail.core.cnx.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;

public class MessageReader implements Iterable<Message> {
	//Used to keep track of partially unprocessed folders. When a folder
	//is totally processed, it's connection can be closed
	//Map<String, Integer> mailsToReadFromFolders;

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

		@Override
		public int compareTo(ComparableMessage o) {
			return this.date.compareTo(o.date);
		}

		public Folder getFolder() {
			return message.getFolder();
		}
	}

	private class LimitedMessageReaderFromFSIterator implements Iterator<Message> {

		public boolean hasNext() {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		public Message next() {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		public void remove() {
			throw new UnsupportedOperationException("Not supported yet.");
		}
	
	}

	private class LimitedMessageReaderIterator implements Iterator<Message> {

		int limOpenFolders = 15;
		List<Folder> openFolders = new ArrayList<Folder>();
		TreeSet<ComparableMessage> message_list;
		int numberOfNexts = 0;

		public LimitedMessageReaderIterator(Connection connection, int limit) {
			message_list = new TreeSet<ComparableMessage>();
			long total_msgs = 0;
			Folder[] folders = null;
			try {
				folders = connection.getFolders();
			} catch (MessagingException e) {
				e.printStackTrace();
			}
			System.out.println("Transversing folders");
			for (Folder folder : folders) {
				if (!folder.getName().contains("INBOX.Sent") && !folder.getName().contains("antiguos")) {
					try {
						if (!folder.isOpen()) {
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
						ComparableMessage comparableMsg = new ComparableMessage(msg);
						message_list.add(comparableMsg);
					} catch (MessagingException e) {
						e.printStackTrace();
					} finally {
						try {
							folder.close(false);
						} catch (MessagingException e) {
							e.printStackTrace();
						}
					}
				}
			}
			System.out.println("End of transversing folders");
		}

		@Override
		public boolean hasNext() {
			return !message_list.isEmpty();
		}

		@Override
		public Message next() {
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
					ComparableMessage nextComparableMsg = new ComparableMessage(nextMsg);
					message_list.add(nextComparableMsg);
				}
			} catch (MessagingException e) {
				e.printStackTrace();
			} finally {
				/*try {
				folder.close(false);
				} catch (MessagingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				}*/
			}
			return msg;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
	private Connection connection;
	private int limit;


	public MessageReader() {

	}

	public MessageReader(Connection connection, int limit) {
		this.connection = connection;
		this.limit = limit;
	}

	@Override
	public Iterator<Message> iterator() {
		Iterator<Message> iterator;
		iterator = new LimitedMessageReaderIterator(connection, limit);
		return iterator;
	}
}

