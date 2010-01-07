/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gnusmail.filesystem;

import gnusmail.MessageReader;
import gnusmail.core.cnx.Connection;
import gnusmail.core.cnx.MIMEMessageWithFolder;
import gnusmail.core.cnx.MessageInfo;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

/**
 *
 * @author jmcarmona
 */
public class MessageFromFileReader extends MessageReader implements Iterable<Message> {

	private class MessagesFromFileIterator implements Iterator<Message> {
		FolderMessagesIterator it;

		public MessagesFromFileIterator(String baseFolder, boolean recursive) {
			FilesReader fr = new FilesReader(baseFolder, recursive);
			it = (FolderMessagesIterator) fr.iterator();
		}

		public boolean hasNext() {
			boolean res = it.hasNext();
			return res;
		}

		public Message next() {
			Message res = null;
			File f = it.next();
			while (f == null && it.hasNext()) {
				f = it.next();
			}
			if (f != null) {
				res = convertFileToMessage(f);
			}
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
			String parentFolder = f.getParentFile().getAbsolutePath();
			InputStream is = null;
			MessageInfo ms = null;
			Message m = null;
			try {
				Session s = null;
				is = new FileInputStream(f);
				m = new MIMEMessageWithFolder(s, is);
				((MIMEMessageWithFolder)m).setFolderAsStr(parentFolder);
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


	public MessageFromFileReader(String baseFolder) {
		this.baseFolder = baseFolder;
		recursive = true;
	}

	public MessageFromFileReader(String baseFolder, boolean recursive) {
		this.baseFolder = baseFolder;
		this.recursive = recursive;
	}

	@Override
	public Iterator<Message> iterator() {
		return new MessagesFromFileIterator(baseFolder, recursive);
	}
}
