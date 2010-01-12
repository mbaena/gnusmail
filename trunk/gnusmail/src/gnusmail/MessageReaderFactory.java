package gnusmail;

import gnusmail.core.ConfigManager;
import gnusmail.core.cnx.Connection;
import gnusmail.filesystem.MessageFromFileReader;

/**
 *
 * @author jmcarmona
 */
public class MessageReaderFactory {
	public MessageReader createReader(Connection connection, int limit) {
		boolean readFromFS = Options.getInstance().isReadMailsFromFileSystem();
		if (!readFromFS) {
			return new MessageReader(connection, limit);
		} else {
			return new MessageFromFileReader(ConfigManager.MAILDIR.getAbsolutePath());
		}
	}

}
