package gnusmail.filters;

import gnusmail.core.cnx.MessageInfo;

import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.MessagingException;

/**
 * 
 * @author jmcarmona
 */
public class NumberOfReceivers extends SingleNumericAttFilter {

	@Override
	protected double getSingleValue(MessageInfo messageInfo)
			throws MessagingException {
		int res = new StringTokenizer(mess.getTo(), ",").countTokens();
		return res;
	}

}
