package gnusmail.filters;

import gnusmail.core.cnx.MessageInfo;

import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Header;
import javax.mail.MessagingException;

/**
 * 
 * @author jmcarmona
 */
public class IsDistributionList extends SingleAttFilter {

	@Override
	protected String getSingleValue(MessageInfo messageInfo)
			throws MessagingException {
		boolean isList = false;
		Enumeration en = mess.getMessage().getAllHeaders();
		while (en.hasMoreElements() && !isList) {
			Header h = (Header) en.nextElement();
			isList = h.getName().contains("List-");
		}
		if (isList) {
			return "Yes";
		} else {
			return "No";
		}
	}
}
