package gnusmail.core.cnx;

import javax.mail.Folder;
import javax.mail.MessagingException;

/**
 *
 * @author jmcarmona
 */
public interface IMailConnection {

	Folder[] getFolders() throws MessagingException;

}
