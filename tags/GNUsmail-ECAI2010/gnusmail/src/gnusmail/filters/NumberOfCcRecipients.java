package gnusmail.filters;

import gnusmail.core.cnx.MessageInfo;
import javax.mail.MessagingException;

/**
 *
 * @author jmcarmona
 */
public class NumberOfCcRecipients extends SingleNumericAttFilter {

	@Override
	protected double getSingleValue(MessageInfo messageInfo) throws MessagingException {
		String ccs = messageInfo.getCc();
		int length = ccs.split(",").length;
		return length;
	}

}
