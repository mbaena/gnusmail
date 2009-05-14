/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gnusmail.filters;

import com.rapidminer.operator.reducer.SnowballStemmer;
import gnusmail.core.WordStore;
import gnusmail.core.cnx.MensajeInfo;
import java.io.IOException;
import java.util.Set;
import java.util.StringTokenizer;
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
    SnowballStemmer sbstemmer;

    public String getPalabraAMirar() {
        return palabraAMirar;
    }

    public void setPalabraAMirar(String palabraAMirar) {
        this.palabraAMirar = palabraAMirar;
    }

    @Override
    public String getNombreFiltro() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String aplicarFiltro(MensajeInfo mess) {
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
}
