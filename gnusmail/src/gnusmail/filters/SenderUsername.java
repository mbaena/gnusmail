package gnusmail.filters;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.MessagingException;

/**
 *
 * @author jmcarmona
 */
public class SenderUsername extends Filter {

	@Override
	public String getValueForHeader(String header) {
		String res = "?";
		try {
			String from = mess.getFrom();
			String[] fields = from.split("@");
			if (fields.length > 0) {
				res = fields[0];
			}
		} catch (MessagingException ex) {
			Logger.getLogger(SenderUsername.class.getName()).log(Level.SEVERE, null, ex);
		}
		return res;
	}
}
