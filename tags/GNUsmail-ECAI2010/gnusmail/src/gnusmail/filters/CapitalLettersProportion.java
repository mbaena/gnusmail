package gnusmail.filters;

import gnusmail.core.cnx.MessageInfo;

import java.io.IOException;
import javax.mail.MessagingException;

/**
 *
 * @author jmcarmona
 */
public class CapitalLettersProportion extends SingleNumericAttFilter {

	@Override
	protected double getSingleValue(MessageInfo messageInfo)
			throws MessagingException {
        int capitals = 0;
        int total = 1;
        try {
            String body = messageInfo.getBody();
            total = body.length();
            int index = 0;
            while (index < body.length()) {
                char letter = body.charAt(index);
                if (letter >= 'A' && letter <= 'Z') {
                    capitals++;
                }
                index++;
            }
        } catch (IOException ex) {
        	throw new MessagingException();
        }
        double proporcion = 0.0;
        if (total > 0) {
            proporcion = (100.0 * capitals) / (1.0 * total);
        }
        return proporcion;	
        }
}
