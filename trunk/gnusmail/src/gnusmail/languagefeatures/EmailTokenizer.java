/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gnusmail.languagefeatures;

import gnusmail.Languages.Language;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * This class tokenizes an email, taking into account different separator symbols
 * @author jmcarmona
 */
public class EmailTokenizer {

    String body;
    List<Token> tokens;
    Language lang = null;
    public final static String tokenPattern = " \t\n\r\f.,;:?¿!¡\"()'=[]{}/<>-*0123456789ªº%&*@_|’\\#";

    public EmailTokenizer(String emailBody) {
        this.body = emailBody;
        tokens = new LinkedList<Token>();
    }

    public EmailTokenizer(String emailBody, Language lang) {
        this.body = emailBody;
        tokens = new LinkedList<Token>();
        this.lang = lang;
    }

    public List<Token> tokenize() {
        StringTokenizer st = new StringTokenizer(body, tokenPattern);
        while (st.hasMoreElements()) {
            Token token = new Token(st.nextToken());
            if (lang != null) {
                token.setLanguage(lang);
            }
            tokens.add(token);
        }
        return tokens;
    }
}
