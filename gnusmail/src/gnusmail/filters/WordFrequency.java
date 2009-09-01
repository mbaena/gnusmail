/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gnusmail.filters;
import gnusmail.Languages.Language;
//import org.apache.lucene.analysis.snowball.SnowballAnalyzer;
import gnusmail.core.WordsStore;
import gnusmail.core.cnx.MessageInfo;

import gnusmail.languagefeatures.EmailTokenizer;
import gnusmail.languagefeatures.LanguageDetection;
import gnusmail.languagefeatures.Token;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.MessagingException;

/**
 *
 * @author jmcarmona
 */
public class WordFrequency extends Filter {

    String palabraAMirar;
    Set<String> stringsEsteDocumento;
    public static WordsStore wordStore;
    static List<String> palabrasAAnalizar;


    public WordFrequency() {
        wordStore = new WordsStore();
    }
    
    public String getPalabraAMirar() {
        return palabraAMirar;
    }

    public void setWordToCheck(String palabraAMirar) {
        this.palabraAMirar = palabraAMirar;
    }

    @Override
    public String getName() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String applyTo(MessageInfo mess) {
        
        String res = "";
        try {
           /* if (stringsEsteDocumento == null) {
                stringsEsteDocumento = new TreeSet<String>();
                String body = mess.getBody();
                StringTokenizer st = new StringTokenizer(body, WordStore.tokenPattern);
                while (st.hasMoreTokens()) {
                    String token = st.nextToken();
                    token = token.toLowerCase();
                    stringsEsteDocumento.add(token);
                   
                }
            }*/
            if (stringsEsteDocumento == null) {
                stringsEsteDocumento = new TreeSet<String>();
                //Extraemos las palabras del cuerpo y la cabecera
                String body = mess.getBody() + " " + mess.getSubject();
                Language lang = new LanguageDetection().detectLanguage(body);
                EmailTokenizer et = new EmailTokenizer(body);
                List<Token> tokens = et.tokenize();
                for (Token token : tokens) {
                    token.setLanguage(lang);
                    stringsEsteDocumento.add(token.getStemmedForm());
                }
            }

            if (stringsEsteDocumento.contains(getPalabraAMirar())) {
                return "True";
            } else {
                return "False";
            }
        } catch (MessagingException ex) {
            Logger.getLogger(WordFrequency.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(WordFrequency.class.getName()).log(Level.SEVERE, null, ex);
        }
        return res;
    }
    
    /**
     * Esta funcion lee una lista de palabras que deben ser usadas como filtro en el cuerpo
     * @return
     */
    public static List<String> getWordsToAnalyze() {
        List<String> res = new ArrayList<String>();
        if (palabrasAAnalizar == null) {
            try {
                // Open the file that is the first
                // command line parameter
                FileInputStream fstream = new FileInputStream(WordsStore.WORDS_FILE);
                // Get the object of DataInputStream
                DataInputStream in = new DataInputStream(fstream);
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                String strLine;
                //Read File Line By Line
                while ((strLine = br.readLine()) != null) {
                    // Print the content on the console
                    res.add(strLine);
                }
                //Close the input stream
                in.close();
            } catch (Exception e) {//Catch exception if any
                System.err.println("Error: " + e.getMessage());
            }
            palabrasAAnalizar = res;
        } else {
            res = palabrasAAnalizar;
        }
        return res;
    }

}
