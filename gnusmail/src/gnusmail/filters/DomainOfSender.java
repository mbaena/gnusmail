package gnusmail.filters;

import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.MessagingException;

/**
 *
 * @author jmcarmona
 */
public class DomainOfSender extends Filter {
    @Override
    public String getValueForHeader(String header) {
        String res = "";
        try {
            StringTokenizer st = new StringTokenizer(mess.getFrom(), "@");
            while (st.hasMoreTokens()) {
                res = st.nextToken();
            }
        } catch (MessagingException ex) {
            Logger.getLogger(DomainOfSender.class.getName()).log(Level.SEVERE, null, ex);
        }
        return res;
    }

}
