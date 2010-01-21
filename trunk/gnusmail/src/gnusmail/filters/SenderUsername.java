/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gnusmail.filters;

import gnusmail.core.cnx.MessageInfo;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.MessagingException;

/**
 *
 * @author jmcarmona
 */
public class SenderUsername extends Filter {

	@Override
	public String getName() {
		return "SenderUsername";
	}

	@Override
	public String applyTo(MessageInfo mess) {
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
