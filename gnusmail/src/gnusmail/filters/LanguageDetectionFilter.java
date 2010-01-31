package gnusmail.filters;

import gnusmail.core.cnx.MessageInfo;
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
public class LanguageDetectionFilter extends SingleAttFilter {

	@Override
	protected String getSingleValue(MessageInfo messageInfo)
			throws MessagingException {
        String body = "";
        try {
            body = mess.getBody();
        } catch (IOException ex) {
            throw new MessagingException();
        }
        return new LanguageDetection().detectLanguage(body).getLanguageName();
	}
}
