/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gnusmail.languagefeatures;

/**
 *
 * @author jmcarmona
 */
public abstract class Stemmer {
    /**
     * This method stems a given text. The text should contain only
     * lower case letters; if not, the result is undefined 
     * @return
     */
    public abstract String stemText(String textToStem);
}
