/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gnusmail.core;

import gnusmail.core.cnx.Connection;
import gnusmail.core.cnx.MensajeInfo;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.Folder;
import javax.mail.MessagingException;

import org.apache.lucene.analysis.snowball.SnowballAnalyzer;

/**
 *
 * @author jmcarmona
 */
public class WordStore {

    Map<String, WordCount> wordCount;
    public final static String patronToken = " \t\n\r\f.,;:?¿!¡\"()'=[]{}/<>-*0123456789ªº%&*@_|’";
    public final static String directorio = System.getProperty("user.home") + "/.genusmail/";
    public final static File FICH_WORDS = new File(directorio + "/wordlist.data");
    public final static File FICH_STOPWORDS = new File(directorio + "/wordlist.data");
    public final static double PROP_DOCUMENTOS = 0.25;
    public final static int MIN_DOCUMENTOS = 5;
    public final static int MAX_NUM_ATRIBUTOS = 200;
    int numDocumentosAnalizados = 0;
    String stopWordsArray[];
    SnowballAnalyzer sbAna;

    public void addTokenizedString(String str) {
        Set<String> stringsEsteDocumento = new TreeSet<String>();

        StringTokenizer st = new StringTokenizer(str, patronToken);
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            token = token.toLowerCase();

            stringsEsteDocumento.add(token);
        }
        for (String tokenSet : stringsEsteDocumento) {
            if (!wordCount.containsKey(tokenSet)) {
                wordCount.put(tokenSet, new WordCount(tokenSet, 1));
            } else {
                WordCount wc = wordCount.get(tokenSet);
                wc.setCount(wc.getCount() + 1);
            }
        }
        numDocumentosAnalizados++;
    }

    public WordStore() {
        wordCount = new TreeMap<String, WordCount>();
        // leerFicheroStopWords().toArray();
        //ssbAna = new SnowballAnalyzer("Spanish", stopWordsArray);
    }

    public void writeToFile() {
        List<String> stopWords = leerFicheroStopWords();
        FileWriter outFile = null;
        System.out.println("Guardando palabras");
        try {
            Collection<WordCount> coll = wordCount.values();
            List<WordCount> list = new ArrayList<WordCount>(coll);
            Collections.sort(list);
            //int size = list.size();
            outFile = new FileWriter(FICH_WORDS);
            PrintWriter out = new PrintWriter(outFile);
            // Also could be written as follows on one line
            // Printwriter out = new PrintWriter(new FileWriter(args[0]));
            // Write text to file
            int i = 0;
            int palabrasanadidas = 0;
            while (palabrasanadidas <= MAX_NUM_ATRIBUTOS && i < list.size()) {
                if (list.get(i).getCount() > MIN_DOCUMENTOS && !stopWords.contains(list.get(i))) {
                    out.println(list.get(i).getWord());
                    palabrasanadidas++;
                }
                i++;

            }
            System.out.println("La maxima era " + list.get(list.size() - 1));
            out.close();
        } catch (IOException ex) {
            Logger.getLogger(WordStore.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                outFile.close();
            } catch (IOException ex) {
                Logger.getLogger(WordStore.class.getName()).log(Level.SEVERE, null, ex);
            }
        }


    }

    private List<String> leerFicheroStopWords() {
        List<String> res = new ArrayList<String>();
        try {
            // Open the file that is the first
            // command line parameter
            FileInputStream fstream = new FileInputStream(FICH_STOPWORDS);
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
        return res;
    }

    public void leerListaPalabras(Connection miconexion) {
        Folder[] carpetas;
        System.out.println("Extrayendo informacion de palabras de los correos...");
        try {
            carpetas = miconexion.getCarpetas();

            for (int i = 0; i < carpetas.length; i++) {
                System.out.println("Extrayendo informacion de palabres de  " + carpetas[i].getFullName());
                leerListaPalabrasCarpeta(carpetas[i]);
            }
            System.out.println("Extraida la info de las palabras");
            writeToFile();

        } catch (MessagingException e) {
            System.out.println("Imposible obtener Carpetas de usuario");
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println();
            e.printStackTrace();
        }
    }

    public void leerListaPalabrasCarpeta(Folder buzon) {
        if (buzon != null) {
            try {
                if (!buzon.isOpen()) {
                    buzon.open(javax.mail.Folder.READ_WRITE);
                }

                for (int i = 1; i <= buzon.getMessageCount(); i++) {
                    MensajeInfo msj = new MensajeInfo(buzon.getMessage(i));
                    String body = msj.getBody();
                    addTokenizedString(body);
                }//for

            } catch (MessagingException e) {
                System.out.println("Folder " + buzon.getFullName() + " no encontrado al leer palabras");
            //e.printStackTrace();
            } catch (IOException e) {
                System.out.println("Error al escribir en CSV");
                e.printStackTrace();
            }
        }//if
    }

}
