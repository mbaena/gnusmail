/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gnusmail.filters;

import gnusmail.Languages.Language;
import gnusmail.core.cnx.MessageInfo;
import gnusmail.languagefeatures.LanguageDetection;
import gnusmail.languagefeatures.StopWordsProvider;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;
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
    static Map<Language, List<String>> frequentWordsByLanguage;

    @Override
    public String getName() {
        return "Language Detection";
    }

    @Override
    public String applyTo(MessageInfo mess) {
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
