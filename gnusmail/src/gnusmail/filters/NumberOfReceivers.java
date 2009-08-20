/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gnusmail.filters;

import gnusmail.core.cnx.MessageInfo;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.MessagingException;

/**
 *
 * @author jmcarmona
 */
public class NumberOfReceivers extends Filter {

    @Override
    public String getName() {
        return "Number of receivers";
    }

    @Override
    public String applyTo(MessageInfo mess) {
       int res = 0;
        try {
            res = new StringTokenizer(mess.getTo(), ",").countTokens();
        } catch (MessagingException ex) {
            Logger.getLogger(NumberOfReceivers.class.getName()).log(Level.SEVERE, null, ex);
        }
       return res + "";
    }

}
