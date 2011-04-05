package gnusmail.filters;

import gnusmail.core.cnx.MessageInfo;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.MessagingException;

/**
 * 
 * @author jmcarmona
 */
public class SenderUsername extends SingleAttFilter {

	@Override
	protected String getSingleValue(MessageInfo messageInfo)
			throws MessagingException {
		String res = null;
		String from = messageInfo.getFrom();
		String[] fields = from.split("@");
		if (fields.length > 0) {
			res = fields[0];
		}
		if (res == null) {
			throw new MessagingException();
		}
		return res;
	}
}
