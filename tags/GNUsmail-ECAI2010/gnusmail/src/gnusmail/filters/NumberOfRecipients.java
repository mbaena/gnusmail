package gnusmail.filters;

import gnusmail.core.cnx.MessageInfo;
import javax.mail.MessagingException;

/**
 *
 * @author jmcarmona
 */
public class NumberOfRecipients extends SingleNumericAttFilter {

	@Override
	protected double getSingleValue(MessageInfo messageInfo) throws MessagingException {
		String to = messageInfo.getTo();
		int length = to.split(",").length;
		return length;
	}


}
