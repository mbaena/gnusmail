/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gnusmail.filters;

import gnusmail.Languages.Language;
import gnusmail.core.cnx.MensajeInfo;
import gnusmail.languagefeatures.StopWordsProvider;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;
import javax.mail.MessagingException;

/**
 * This class decides the laguage of a document by counting the number of coincidences with
 * a given list of stopwords.
 * Different approaches are to be tested in the future
 * @author jmcarmona
 */
public class LanguageDetection extends Filter {
    static Map<Language, List<String>> frequentWordsByLanguage;

    @Override
    public String getName() {
        return "Languae Detection";
    }

    @Override
    public String applyTo(MensajeInfo mess) {
        String res = null;
        try {
        if (frequentWordsByLanguage == null) {
            fillFrequentWordsList();
        }
        Map<Language,Integer> coincidencesByLanguage = new TreeMap<Language, Integer>();
        for (Language lang : frequentWordsByLanguage.keySet()) {
            coincidencesByLanguage.put(lang,numberOfCoincidences(mess.getBody(),
                    frequentWordsByLanguage.get(lang)));
        }
        } catch (MessagingException e) {
        } catch (IOException e2) {
        }
        return res;
    }

    private void fillFrequentWordsList() {
        frequentWordsByLanguage = StopWordsProvider.getInstance().getStopwordsMap();
    }

    private int numberOfCoincidences(String body, List<String> words) {
        body = body.toLowerCase();
        int coincidences = 0;
        StringTokenizer st = new StringTokenizer(body); //TODO delimiters
        while (st.hasMoreTokens()) {
            if (words.contains(st.nextElement())) {
                coincidences++;
            }
        }
        return coincidences;
    }

}
