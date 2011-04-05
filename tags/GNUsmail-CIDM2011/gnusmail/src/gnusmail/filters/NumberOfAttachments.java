package gnusmail.filters;

import gnusmail.core.cnx.MessageInfo;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.MessagingException;

/**
 *
 * @author jmcarmona
 */
public class NumberOfAttachments extends SingleNumericAttFilter {

	@Override
	protected double getSingleValue(MessageInfo messageInfo)
			throws MessagingException {
        int res = 0;
        try {
            res = messageInfo.numberOfAttachments();
        } catch (IOException ex) {
        	throw new MessagingException();
        }
        return res;
	}

}
