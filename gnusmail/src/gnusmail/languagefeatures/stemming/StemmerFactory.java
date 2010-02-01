package gnusmail.languagefeatures.stemming;

import gnusmail.Languages.Language;

/**
 *
 * @author jmcarmona
 */
public class StemmerFactory {
	private static EnglishStemmer englishStemmer = new EnglishStemmer();
	private static SpanishStemmer spanishStemmer = new SpanishStemmer();
    public static IStemmer getStemmer(Language lang) {
        if (lang.equals(Language.ENGLISH)) {
            return englishStemmer;
        } else if (lang.equals(Language.SPANISH)) {
            return spanishStemmer;
        } else {
           return new TrivialStemmer();
        }
    }

}
