package gnusmail.languagefeatures;

import gnusmail.Languages.Language;
import gnusmail.languagefeatures.stemming.StemmerFactory;

/**
 * This class encapsulates a token, and has method to return several variations of
 * it, such as lower case, or stemmed form.
 * @author jmcarmona
 */
public class Token {

    String originalForm;
    Language language;

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    

    public Token(String originalForm) {
        this.originalForm = originalForm;
    }

    public String getBasicForm() {
        return originalForm;
    }

    public String getLowerCaseForm() {
        return originalForm.toLowerCase();
    }

    /**
     * This function obtains a stemmed form of the lower case of the original form
     * of the word, attending to the detected language
     * @return
     */
    public String getStemmedForm() {
        String toStem = getLowerCaseForm().replaceAll("\\.|:|,|\\(|\\)", "");
       // String toStem = getLowerCaseForm();
        return StemmerFactory.getStemmer(language).extactRoot(toStem);
    }

    @Override
    public String toString() {
        return "[" + originalForm + " " + getStemmedForm() + " " + language.getLanguageName() + "]";
    }
}
