/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gnusmail.languagefeatures;

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
   public final static String patronToken = " \t\n\r\f.,;:?¿!¡\"()'=[]{}/<>-*0123456789ªº%&*@_|’";


    public EmailTokenizer(String emailBody) {
        this.body = emailBody;
        tokens = new LinkedList<Token>();
    }

    public  List<Token> tokenize() {
        StringTokenizer st = new StringTokenizer(body, patronToken);
        while (st.hasMoreElements()) {
            tokens.add(new Token(st.nextToken()));
        }
        return tokens;
    }
}
