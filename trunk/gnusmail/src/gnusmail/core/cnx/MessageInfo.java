package gnusmail.core.cnx;

import java.text.*;
import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Address;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.*;

public class MessageInfo implements Comparable<MessageInfo> {

	public Message message;

	public MessageInfo(Message message) {
		this.message = message;

	}

	public String getMessageId() {// throws MessagingException {
		String[] res;
		try {
			res = message.getHeader("Message-Id");
		} catch (MessagingException e) {
			return "?";
		}
		return res[0];
	}

	public String getBcc() throws MessagingException {
		String res;
		try {
			res = formatAddresses(message
					.getRecipients(Message.RecipientType.BCC));
		} catch (Exception e) {
			res = "?";
		}
		return res;
	}

	public String getBody() throws MessagingException, IOException {
		Object content = message.getContent();
		String result = "";
		if (message.isMimeType("text/plain")) {
			result = (String) content;
		} else if (message.isMimeType("multipart/alternative")) {
			Multipart mp = (Multipart) message.getContent();
			int numParts = mp.getCount();
			for (int i = 0; i < numParts; ++i) {
				if (mp.getBodyPart(i).isMimeType("text/plain")) {
					try {
						result = (String) mp.getBodyPart(i).getContent();
					} catch (Exception e) {
						System.out.println("Exception (message body)");
					}
				}
			}
			result = "";
		} else if (message.isMimeType("multipart/*")) {
			Multipart mp = (Multipart) content;
			if (mp.getBodyPart(0).isMimeType("text/plain")) {
				result = (String) mp.getBodyPart(0).getContent();
			} else {
				result = "";
			}
		} else {
			result = "";
		}
		result = new String(result.getBytes(), "UTF-8");
		return result;
	}

	public String getCc() {
		String res;
		try {
			res = formatAddresses(message
					.getRecipients(Message.RecipientType.CC));
		} catch (MessagingException e) {
			res = "?";
		}
		return res;
	}

	/** Returs sent date (of, if it's null, reception date */
	public String getDateAsStr() throws MessagingException {
		Date date;
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yy");
		if ((date = message.getSentDate()) != null) {
			return (df.format(date));
		} else if ((date = message.getReceivedDate()) != null) {
			return (df.format(date));
		} else {
			return "";
		}
	}

	public Date getDate() throws MessagingException {
		Date date;
		if ((date = message.getSentDate()) != null) {
			return date;
		} else if ((date = message.getReceivedDate()) != null) {
			return date;
		} else {
			return null;
		}
	}

	public String getFrom() throws MessagingException {
		return formatAddresses(message.getFrom());
	}

	public String getReplyTo() throws MessagingException {
		Address[] a = message.getReplyTo();
		if (a.length > 0) {
			return ((InternetAddress) a[0]).getAddress();
		} else {
			return "";
		}
	}

	public Message getMessage() {
		return message;
	}

	public int getNum() {
		return ((message.getMessageNumber()));
	}

	public String getReceivedDate() throws MessagingException {
		if (hasReceivedDate()) {
			return (message.getReceivedDate().toString());
		} else {
			return "";
		}
	}

	public String getSentDate() throws MessagingException {
		if (hasSentDate()) {
			return (message.getSentDate().toString());
		} else {
			return "";
		}
	}

	public String getSubject() throws MessagingException {
		if (hasSubject()) {
			return message.getSubject();
		} else {
			return "";
		}
	}

	public String getTo() throws MessagingException {
		try {
			return formatAddresses(message
					.getRecipients(Message.RecipientType.TO));
		} catch (AddressException e) {
			return "UnknownAddress";
		}
	}

	public boolean hasAttachments() throws IOException, MessagingException {
		boolean hasAttachments = false;
		if (message.isMimeType("multipart/*")) {
			Multipart mp = (Multipart) message.getContent();
			if (mp.getCount() > 1) {
				hasAttachments = true;
			}
		}
		return hasAttachments;
	}

	public boolean hasBcc() throws MessagingException {
		return (message.getRecipients(Message.RecipientType.BCC) != null);
	}

	public boolean hasCc() throws MessagingException {
		return (message.getRecipients(Message.RecipientType.CC) != null);
	}

	public boolean hasDate() throws MessagingException {
		return (hasSentDate() || hasReceivedDate());
	}

	public boolean hasFrom() throws MessagingException {
		return (message.getFrom() != null);
	}

	public boolean hasMimeType(String mimeType) throws MessagingException {
		return message.isMimeType(mimeType);
	}

	public boolean hasReceivedDate() throws MessagingException {
		return (message.getReceivedDate() != null);
	}

	public boolean hasSentDate() throws MessagingException {
		return (message.getSentDate() != null);
	}

	public boolean hasSubject() throws MessagingException {
		return (message.getSubject() != null);
	}

	public boolean hasTo() throws MessagingException {
		return (message.getRecipients(Message.RecipientType.TO) != null);
	}

	public Folder getFolder() {
		return message.getFolder();
	}

	/**
	 * This method returns the folder of a message, as a string. If the message
	 * comes from an IMAP mail, the folder header is read. If the message comes
	 * from a parsed file, the wrapper class MIMEMessageWithFolder is used.
	 * 
	 * @return
	 */
	public String getFolderAsString() {
		if (message instanceof MIMEMessageWithFolder) // return
														// ((MIMEMessageWithFolder)message).getFolderAsStr();
		{
			MIMEMessageWithFolder msg = (MIMEMessageWithFolder) message;
			return msg.getFolderAsStr();
		} else {
			return getFolderAsStringFromIMAPMAil();
		}
	}

	/**
	 * If this MessageInfo is the result of reading a message from console, no
	 * Folder info will be available, hence getFolder will return null. This
	 * method returns the name getFolder().getName(), if possible, or the
	 * contents of a header 'Folder', that will have to be included when it's
	 * necessary to know the folder (that's when we update the classifier model
	 * with a mail read from stdinput).
	 * 
	 * TODO
	 * 
	 * @return
	 */
	public String getFolderAsStringFromIMAPMAil() {
		String res = "?";
		/*
		 * Folder folder = message.getFolder(); if (folder != null) { String
		 * fullName = folder.getFullName(); String fields[] =
		 * fullName.split("@"); if (fields.length > 1) { res = fields[1]; } else
		 * { res = fields[0]; } res = res.replace(" ", "_"); res =
		 * res.replace("INBOX.", ""); //res = res.replace("LCC.", "");
		 * //Provisional, coge solo la primera subcadena String fieldsRes[] =
		 * res.split("\\."); res = fieldsRes[0];
		 * 
		 * } else { String headerFolder[] = null; try { headerFolder =
		 * message.getHeader("Folder"); } catch (MessagingException ex) {
		 * Logger.getLogger(MessageInfo.class.getName()).log(Level.SEVERE, null,
		 * ex); } if (headerFolder != null && headerFolder.length > 0) { res =
		 * headerFolder[0]; } } return res;
		 */
		return message.getFolder().getFullName();
	}

	public void print(OutputStream os) {
		try {
			message.writeTo(os);
		} catch (Exception e) {
			System.out.println("ERROR: Impossible to print message!!");
			e.printStackTrace();
		}

	}

	public void createHeader(String nombre, String contenido) {
		try {
			message.addHeader(nombre, contenido);
		} catch (MessagingException e) {
			System.out.println("Impossible to add new header");
			// e.printStackTrace();
		}

	}

	private String formatAddresses(Address[] addrs) {
		if (addrs == null) {
			return "";
		}
		StringBuffer strBuf = new StringBuffer(getDisplayAddress(addrs[0]));
		for (int i = 1; i < addrs.length; i++) {
			strBuf.append(", ").append(getDisplayAddress(addrs[i]));
		}
		return strBuf.toString();
	}

	private String getDisplayAddress(Address a) {
		String pers = null;
		String addr = null;
		pers = ((InternetAddress) a).getPersonal();
		if (a instanceof InternetAddress && (pers != null)) {
			// addr = pers +
			// "  "+"&lt;"+((InternetAddress)a).getAddress()+"&gt;";
			addr = ((InternetAddress) a).getAddress();
		} else {
			addr = a.toString();
		}
		return addr;
	}

	public int compareTo(MessageInfo o) {
		int res = 0;
		MessageInfo mi = (MessageInfo) o;
		try {
			res = getDate().compareTo(mi.getDate());
		} catch (MessagingException ex) {
			Logger.getLogger(MessageInfo.class.getName()).log(Level.SEVERE,
					null, ex);
		}
		return res;
	}

	public int size() throws MessagingException {
		return message.getSize();
	}

	public int numberOfAttachments() throws MessagingException, IOException {
		if (message.isMimeType("multipart/*")) {
			Multipart mp = (Multipart) message.getContent();
			return mp.getCount();
		}
		return 1;
	}
}
