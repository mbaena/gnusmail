package gnusmail.filters;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.MessagingException;

/**
 *
 * @author jmcarmona
 */
public class CapitalLettersProportion extends Filter {
	@Override
	public String getValueForHeader(String header) {
        int capitals = 0;
        int total = 1;
        try {
            String body = mess.getBody();
            total = body.length();
            int index = 0;
            while (index < body.length()) {
                char letter = body.charAt(index);
                if (letter >= 'A' && letter <= 'Z') {
                    capitals++;
                }
                index++;
            }
        } catch (MessagingException ex) {
            Logger.getLogger(CapitalLettersProportion.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CapitalLettersProportion.class.getName()).log(Level.SEVERE, null, ex);
        }
        double proporcion = 0.0;
        if (total > 0) {
            proporcion = (100.0 * capitals) / (1.0 * total);
        }
        return "" + proporcion;
	}
}
