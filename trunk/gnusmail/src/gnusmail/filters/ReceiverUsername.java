package gnusmail.filters;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.MessagingException;

/**
 *
 * @author jmcarmona
 */
public class ReceiverUsername extends Filter {

	@Override
	public String getValueForHeader(String header) {
		String res = "?";
		try {
			String to = mess.getTo();
			String[] fields = to.split("@");
			if (fields.length > 0) {
				res = fields[0];
			}
		} catch (MessagingException ex) {
			Logger.getLogger(SenderUsername.class.getName()).log(Level.SEVERE, null, ex);
		}
		return res;
	}

}
