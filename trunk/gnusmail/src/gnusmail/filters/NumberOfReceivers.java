package gnusmail.filters;

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
    public String getValueForHeader(String header) {
       int res = 0;
        try {
            res = new StringTokenizer(mess.getTo(), ",").countTokens();
        } catch (MessagingException ex) {
            Logger.getLogger(NumberOfReceivers.class.getName()).log(Level.SEVERE, null, ex);
        }
       return res + "";
    }

}
