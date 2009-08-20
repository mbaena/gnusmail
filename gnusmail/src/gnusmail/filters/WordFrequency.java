/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gnusmail.filters;

import gnusmail.Languages.Language;
//import org.apache.lucene.analysis.snowball.SnowballAnalyzer;
import gnusmail.core.WordStore;
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
    Set<String> stringsInCurrentDocument;
    public static WordStore wordStore;
    static List<String> wordsToAnalyze;

    public WordFrequency() {
        wordStore = new WordStore();
    }

    public String getPalabraAMirar() {
        return palabraAMirar;
    }

    public void setWordToCheck(String palabraAMirar) {
        this.palabraAMirar = palabraAMirar;
    }

    @Override
    public String getName() {
        return "Word frequency";
    }

    @Override
    public String applyTo(MessageInfo mess) {

        String res = "";
        try {
            if (stringsInCurrentDocument == null) {
                stringsInCurrentDocument = new TreeSet<String>();
                //Extraemos las palabras del cuerpo y la cabecera
                String body = mess.getBody() + " " + mess.getSubject();
                Language lang = new LanguageDetection().detectLanguage(body);
                EmailTokenizer et = new EmailTokenizer(body, lang);
                List<Token> tokens = et.tokenize();
                for (Token token : tokens) {
                    stringsInCurrentDocument.add(token.getStemmedForm());
                }
            }

            if (stringsInCurrentDocument.contains(getPalabraAMirar())) {
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
     * This methods initializes the list of frequent words into memory.
     * A file with this words has to exist (WordStore.WORDS_FILE)
     * @return
     */
    public static List<String> getWordsToAnalyze() {
        List<String> res = new ArrayList<String>();
        if (wordsToAnalyze == null) {
            try {
                FileInputStream fstream = new FileInputStream(WordStore.WORDS_FILE);
                DataInputStream in = new DataInputStream(fstream);
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                String strLine;
                while ((strLine = br.readLine()) != null) {
                    res.add(strLine);
                }
                in.close();
            } catch (IOException e) {
                System.out.println("Problem trying to access file: " +
                        WordStore.WORDS_FILE.getAbsolutePath());
            }
            wordsToAnalyze = res;
        } else {
            res = wordsToAnalyze;
        }
        return res;
    }
}
