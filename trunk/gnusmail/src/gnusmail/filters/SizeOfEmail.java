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
public class SizeOfEmail extends Filter {

    @Override
    public String getName() {
        return "Size of email";
    }

    @Override
    public String applyTo(MessageInfo mess) {
        int size = 0;
        try {
            size = mess.size();
        } catch (MessagingException ex) {
            Logger.getLogger(SizeOfEmail.class.getName()).log(Level.SEVERE, null, ex);
        }
        return size + "";
    }

}
