/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gnusmail.filters;
import org.apache.lucene.analysis.snowball.SnowballAnalyzer;
import gnusmail.core.WordStore;
import gnusmail.core.cnx.MensajeInfo;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.Folder;
import javax.mail.MessagingException;

/**
 *
 * @author jmcarmona
 */
public class WordFrequency extends Filter {

    String palabraAMirar;
    Set<String> stringsEsteDocumento;
    SnowballAnalyzer sbstemmer;
    public static WordStore wordStore;
    static List<String> palabrasAAnalizar;


    public WordFrequency() {
        wordStore = new WordStore();    	
    }
    
    public String getPalabraAMirar() {
        return palabraAMirar;
    }

    public void setPalabraAMirar(String palabraAMirar) {
        this.palabraAMirar = palabraAMirar;
    }

    @Override
    public String getName() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String applyTo(MensajeInfo mess, String initialFolderName) {
        String res = "";
        try {
            if (stringsEsteDocumento == null) {
                stringsEsteDocumento = new TreeSet<String>();
                String body = mess.getBody();
                StringTokenizer st = new StringTokenizer(body, WordStore.patronToken);
                while (st.hasMoreTokens()) {
                    String token = st.nextToken();
                    token = token.toLowerCase();
                    stringsEsteDocumento.add(token);
                   
                }
            }

            if (stringsEsteDocumento.contains(getPalabraAMirar())) {
                System.out.println("Retornaremos true");
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
    public static List<String> leerPalabrasAAnalizar() {
        List<String> res = new ArrayList<String>();
        if (palabrasAAnalizar == null) {
            try {
                // Open the file that is the first
                // command line parameter
                FileInputStream fstream = new FileInputStream(WordStore.FICH_WORDS);
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
