package gnusmail.filters;

import gnusmail.core.cnx.MessageInfo;

import java.io.IOException;

import javax.mail.MessagingException;

/**
 *
 * @author jmcarmona
 */
public class BodyLength extends SingleNumericAttFilter {

	@Override
	protected double getSingleValue(MessageInfo messageInfo)
			throws MessagingException {
        double size = 0;
        try {
            size = mess.getBody().length();
        } catch (IOException ex) {
            throw new MessagingException();
        }
        return size;
	}

}
