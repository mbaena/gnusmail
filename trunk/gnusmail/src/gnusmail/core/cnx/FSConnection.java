package gnusmail.core.cnx;

import javax.mail.Folder;
import javax.mail.MessagingException;

/**
 *
 * @author jmcarmona
 */
public class FSConnection implements IMailConnection {

	public Folder[] getFolders() throws MessagingException {
		throw new UnsupportedOperationException("Not supported yet.");
	}

}
