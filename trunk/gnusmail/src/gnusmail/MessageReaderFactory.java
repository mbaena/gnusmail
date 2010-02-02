package gnusmail;

import gnusmail.core.cnx.Connection;
import gnusmail.filesystem.MessageFromFileReader;

/**
 *
 * @author jmcarmona
 */
public class MessageReaderFactory {
	public static MessageReader createReader(Connection connection, int limit) {
		return new MessageReader(connection, limit);
	}

	public static MessageReader createReader(String maildir, int limit) {
		return new MessageFromFileReader(maildir, limit);
	}
	
}
