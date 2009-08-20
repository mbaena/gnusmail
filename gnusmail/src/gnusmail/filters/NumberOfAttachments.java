/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gnusmail.filters;

import gnusmail.core.cnx.MessageInfo;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.MessagingException;

/**
 *
 * @author jmcarmona
 */
public class NumberOfAttachments extends Filter {

    @Override
    public String getName() {
        return "Number of attachments";
    }

    @Override
    public String applyTo(MessageInfo mess) {
        int res = 0;
        try {
            res = mess.numberOfAttachments();
        } catch (MessagingException ex) {
            Logger.getLogger(NumberOfAttachments.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(NumberOfAttachments.class.getName()).log(Level.SEVERE, null, ex);
        }
        return res + "";
    }

}
