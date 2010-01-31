package gnusmail.filters;

import gnusmail.core.cnx.MessageInfo;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.MessagingException;

/**
 *
 * @author jmcarmona
 */
public class SubjectLength extends SingleNumericAttFilter {

	@Override
	protected double getSingleValue(MessageInfo messageInfo)
			throws MessagingException {
        return messageInfo.getSubject().length();
	}

}
