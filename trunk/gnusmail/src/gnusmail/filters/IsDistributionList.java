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
public class IsDistributionList extends Filter {

	@Override
	public String getName() {
		return "IsDistributionList";
	}

	@Override
	public String applyTo(MessageInfo mess) {
		boolean isList = false;
		try {
			Enumeration en = mess.getMessage().getAllHeaders();
			while (en.hasMoreElements() && !isList) {
				Header h = (Header) en.nextElement();
				isList = h.getName().contains("List-");
			}
		} catch (MessagingException ex) {
			Logger.getLogger(IsDistributionList.class.getName()).log(Level.SEVERE, null, ex);
		}
		if (isList) {
			return "Yes";
		} else {
			return "No";
		}
	}
}