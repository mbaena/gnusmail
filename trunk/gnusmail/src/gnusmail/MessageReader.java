package gnusmail;

import gnusmail.core.cnx.Connection;


import java.util.Iterator;
import java.util.TreeSet;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;

public class MessageReader implements Iterable<Message>{
	private class ComparableMessage implements Comparable<ComparableMessage>{

		private Message message;

		public ComparableMessage(Message msg) {
			this.message = msg;
		}

		public Message getMessage() {
			return message;
		}

		@Override
		public int compareTo(ComparableMessage o) {
			int value =  0;
			try {
				value = this.message.getReceivedDate().compareTo(o.message.getReceivedDate());
			} catch (MessagingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return value;
		}
		
		
		
	}

	private class MessageReaderIterator implements Iterator<Message> {
		
		TreeSet<ComparableMessage> message_list;
		public MessageReaderIterator(Connection connection) {
			message_list = new TreeSet<ComparableMessage>();
			Folder[] folders;
			try {
				folders = connection.getCarpetas();
				for (Folder folder : folders) {
					Message msg = folder.getMessage(1); // this is the first message
					ComparableMessage comparableMsg = new ComparableMessage(msg);
					message_list.add(comparableMsg);
				}
			} catch (MessagingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

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
				if (number < folder.getMessageCount()) {
					Message nextMsg = folder.getMessage(number+1);
					ComparableMessage nextComparableMsg = new ComparableMessage(nextMsg);
					message_list.add(nextComparableMsg);
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

