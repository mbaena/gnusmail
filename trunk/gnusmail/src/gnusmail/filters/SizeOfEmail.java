package gnusmail.filters;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.MessagingException;

/**
 *
 * @author jmcarmona
 */
public class SizeOfEmail extends Filter {

    @Override
    public String getValueForHeader(String header) {
        int size = 0;
        try {
            size = mess.size();
        } catch (MessagingException ex) {
            Logger.getLogger(SizeOfEmail.class.getName()).log(Level.SEVERE, null, ex);
        }
        return size + "";
    }

}
