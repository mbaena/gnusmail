package gnusmail;

import gnusmail.core.cnx.Connection;


import java.util.Date;
import java.util.Iterator;
import java.util.TreeSet;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;

public class MessageReader implements Iterable<Message>{
	private class ComparableMessage implements Comparable<ComparableMessage>{

		private Message message;
		private Date date;

		public ComparableMessage(Message msg) {
			this.message = msg;
			try {
				this.date = msg.getReceivedDate();
			} catch (MessagingException e) {
				// TODO Auto-generated catch block
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
		
		
		
	}

	private class MessageReaderIterator implements Iterator<Message> {
		
		TreeSet<ComparableMessage> message_list;
		public MessageReaderIterator(Connection connection) {
			message_list = new TreeSet<ComparableMessage>();
			long total_msgs = 0;
			Folder[] folders = null;
			try {
				folders = connection.getCarpetas();
			} catch (MessagingException e) {
				e.printStackTrace();
			}
			for (Folder folder : folders) {
				try {
				if (!folder.isOpen()) {
						folder.open(javax.mail.Folder.READ_ONLY);
				}
				System.out.println(folder.getFullName());
				int msg_count = folder.getMessageCount();
				total_msgs += msg_count;
				if (msg_count<=0) {
					continue;
				}
				Message msg = folder.getMessage(1); // this is the first message
				ComparableMessage comparableMsg = new ComparableMessage(msg);
				message_list.add(comparableMsg);
				folder.close(false);
				} catch (MessagingException e) {
					e.printStackTrace();
				}
			}
			System.out.println("Total: " + total_msgs);
		}
		@Override
		public boolean hasNext() {
			return !message_list.isEmpty();
		}

		@Override
		public Message next() {
			ComparableMessage comparableMsg = message_list.pollFirst();
			Message msg = comparableMsg.getMessage();
			Folder folder = msg.getFolder();
			int number = msg.getMessageNumber();
			try {
				if (!folder.isOpen()) {
					folder.open(javax.mail.Folder.READ_ONLY);
				}
				if (number < folder.getMessageCount()) {
					
					Message nextMsg = folder.getMessage(number+1);
					ComparableMessage nextComparableMsg = new ComparableMessage(nextMsg);
					message_list.add(nextComparableMsg);
					folder.close(false);
				}
			} catch (MessagingException e) {
				e.printStackTrace();
			}
			return msg;
		}
		
		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
		

	}
	
	private Connection connection;
	
	public MessageReader(Connection connection) {
		this.connection = connection;
	}
	
	@Override
	public Iterator<Message> iterator() {
		return new MessageReaderIterator(connection);
	}
	
}

