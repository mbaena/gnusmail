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
public class BodyLength extends Filter {

    @Override
    public String getName() {
       return "Body Length";
    }

    @Override
    public String applyTo(MessageInfo mess) {
        String size = "0";
        try {
            size = mess.getBody().length() + "";
        } catch (MessagingException ex) {
            Logger.getLogger(BodyLength.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(BodyLength.class.getName()).log(Level.SEVERE, null, ex);
        }
        return size;
    }

}
