package gnusmail.filters;

import gnusmail.core.cnx.MessageInfo;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.MessagingException;

/**
 * 
 * @author jmcarmona
 */
public class ReceiverUsername extends SingleAttFilter {

	@Override
	protected String getSingleValue(MessageInfo messageInfo)
			throws MessagingException {
		String res = null;
		String to = messageInfo.getTo();
		String[] fields = to.split("@");
		if (fields.length > 0) {
			res = fields[0];
		}
		if (res==null) {
			throw new MessagingException();
		}
		return res;
	}

}
