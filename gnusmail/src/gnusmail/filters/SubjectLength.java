package gnusmail.filters;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.MessagingException;

/**
 *
 * @author jmcarmona
 */
public class SubjectLength extends Filter {

    @Override
    public String getValueForHeader(String header) {
        int res = 0;
        try {
            res = mess.getSubject().length();
        } catch (MessagingException ex) {
            Logger.getLogger(SubjectLength.class.getName()).log(Level.SEVERE, null, ex);
        }
        return res + "";
    }

}
