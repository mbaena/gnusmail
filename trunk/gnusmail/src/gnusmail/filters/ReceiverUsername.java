package gnusmail.filters;

import gnusmail.core.cnx.MessageInfo;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.MessagingException;

/**
 *
 * @author jmcarmona
 */
public class ReceiverUsername extends Filter {

	@Override
	public String getName() {
		return "ReceiverUsername";
	}

	@Override
	public String applyTo(MessageInfo mess) {
		String res = "?";
		try {
			String to = mess.getTo();
			String from = mess.getFrom();
			String[] fields = to.split("@");
			if (fields.length > 0) {
				res = fields[0];
			}
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>" + to + " " + res + " " + from);
		} catch (MessagingException ex) {
			Logger.getLogger(SenderUsername.class.getName()).log(Level.SEVERE, null, ex);
		}
		return res;
	}

}
