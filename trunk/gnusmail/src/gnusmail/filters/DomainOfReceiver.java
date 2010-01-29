package gnusmail.filters;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.MessagingException;

/**
 *
 * @author jmcarmona
 */
public class DomainOfReceiver extends Filter {
	@Override
	public String getValueForHeader(String header) {
        String res = "?";
        try {
			String to = mess.getTo();
			String fields[] = to.split("@");
			if (fields.length > 0)
				res = fields[1];
        } catch (MessagingException ex) {
            Logger.getLogger(DomainOfSender.class.getName()).log(Level.SEVERE, null, ex);
        }
        return res;
	}

}