/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gnusmail.languagefeatures;

import gnusmail.Languages.Language;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class encapsulates a token, and has method to return several variations of
 * it, such as lower case, or stemmed form.
 * @author jmcarmona
 */
public class Token {
    String originalForm;
    Language lang;

    public Token(String originalForm) {
        this.originalForm = originalForm;
    }

    public String getBasicForm() {
        return originalForm;
    }

    public String getLowerCaseForm() {
        return originalForm.toLowerCase();
    }

    public String getStemmedForm() {
        throw new UnsupportedOperationException("Not still implemented");
    }

   

}
