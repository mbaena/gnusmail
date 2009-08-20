/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gnusmail.languagefeatures.stemming;

import gnusmail.Languages.Language;

/**
 *
 * @author jmcarmona
 */
public class StemmerFactory {
    public static IStemmer getStemmer(Language lang) {
        if (lang.equals(Language.ENGLISH)) {
            return new EnglishStemmer();
        } else if (lang.equals(Language.SPANISH)) {
            return new SpanishStemmer();
        } else {
           return new TrivialStemmer();
        }
    }

}
