/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gnusmail.core;

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
}
