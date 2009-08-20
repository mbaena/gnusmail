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
public class SubjectLength extends Filter {

    @Override
    public String getName() {
        return "Subject length";
    }

    @Override
    public String applyTo(MessageInfo mess) {
        int res = 0;
        try {
            res = mess.getSubject().length();
        } catch (MessagingException ex) {
            Logger.getLogger(SubjectLength.class.getName()).log(Level.SEVERE, null, ex);
        }
        return res + "";
    }

}
