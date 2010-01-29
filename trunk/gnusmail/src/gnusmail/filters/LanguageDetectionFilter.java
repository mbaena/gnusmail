package gnusmail.filters;

import gnusmail.languagefeatures.LanguageDetection;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.MessagingException;

/**
 * This class decides the laguage of a document by counting the number of coincidences with
 * a given list of stopwords.
 * Different approaches are to be tested in the future
 * @author jmcarmona
 */
public class LanguageDetectionFilter extends Filter {
    @Override
    public String getValueForHeader(String header)  {
        String body = "";
        try {
            body = mess.getBody();
        } catch (MessagingException ex) {
            Logger.getLogger(LanguageDetectionFilter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(LanguageDetectionFilter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new LanguageDetection().detectLanguage(body).getLanguageName();
    }
}
