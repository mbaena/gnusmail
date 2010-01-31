package gnusmail.filters;

import gnusmail.core.cnx.MessageInfo;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.MessagingException;

/**
 *
 * @author jmcarmona
 */
public class SizeOfEmail extends SingleNumericAttFilter {

	@Override
	protected double getSingleValue(MessageInfo messageInfo)
			throws MessagingException {
        return mess.size();
    }

}
