package gnusmail.filesystem;

import gnusmail.MessageReader;
import gnusmail.SortableMessage;
import gnusmail.core.cnx.MIMEMessageWithFolder;
import gnusmail.core.cnx.MessageInfo;
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
public class MessageFromFileReader extends MessageReader implements Iterable<Message> {

	private class MessagesFromFileIterator implements Iterator<Message> {

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

		public Message next() {
			Message res = messages.get(index).getMesssage();
			index++;
			return res;
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
				if (baseFolderLength < parentFolder.length()) {
					((MIMEMessageWithFolder) m).setFolderAsStr(parentFolder.substring(baseFolderLength, parentFolder.length()));
				} else {
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
	public Iterator<Message> iterator() {
		return new MessagesFromFileIterator(baseFolder, recursive, limit);
	}
}
