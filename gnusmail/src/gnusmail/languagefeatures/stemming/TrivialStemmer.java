/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gnusmail.languagefeatures.stemming;

/**
 * This stemmer is used when the language is not known. Then, an empty transformation
 * is performed
 * @author jmcarmona
 */
public class TrivialStemmer implements IStemmer {

    public String extactRoot(String receivedWord) {
        return receivedWord;
    }

}
