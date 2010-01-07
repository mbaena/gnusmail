package gnusmail.core.cnx;

import java.io.InputStream;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;


/**
 * This is an extended class for MimeMessage. The folder (as a String) is included,
 * since the original imap folder cannot be retrieved from the file.
 * @author jmcarmona
 */
public class MIMEMessageWithFolder extends MimeMessage {
	String folderAsStr;

	public String getFolderAsStr() {
		return folderAsStr;
	}

	public void setFolderAsStr(String folderAsStr) {
		this.folderAsStr = folderAsStr;
	}

	public MIMEMessageWithFolder(Session arg0, InputStream arg1) throws MessagingException {
		super(arg0, arg1);
	}
}
