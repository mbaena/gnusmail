/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gnusmail.core;

import gnusmail.Languages.Language;
import gnusmail.core.cnx.Connection;
import gnusmail.core.cnx.MensajeInfo;

import gnusmail.languagefeatures.EmailTokenizer;
import gnusmail.languagefeatures.LanguageDetection;
import gnusmail.languagefeatures.TFIDFSummary;
import gnusmail.languagefeatures.TermFrequencyManager;
import gnusmail.languagefeatures.Token;
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
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.Folder;
import javax.mail.MessagingException;

import org.apache.lucene.analysis.snowball.SnowballAnalyzer;

/**
 * Esta clase gestiona un mapa palabras -> numero de apariciones. Se ha creado la 
 * clase WordCount para gestionar el
 * numero de apariciones (y por si en el futuro se emplea algun metodo diferente
 * a simplemente contar el numero de palabras). tfidf,e tc...
 * @author jmcarmona
 */
public class WordStore {

    TermFrequencyManager termFrequencyManager;
    public final static String patronToken = " \t\n\r\f.,;:?¿!¡\"()'=[]{}/<>-*0123456789ªº%&*@_|’";
    public final static String directorio = System.getProperty("user.home") + "/.gnusmail/";
    public final static File FICH_WORDS = new File(directorio + "/wordlist.data");
    public final static File FICH_STOPWORDS = new File(directorio + "/wordlist.data");
    public final static double PROP_DOCUMENTOS = 0.25;
    public final static int MIN_DOCUMENTOS = 5;
    public final static int MAX_NUM_ATRIBUTOS = 20;
    int numDocumentosAnalizados = 0;
    String stopWordsArray[];
    SnowballAnalyzer sbAna;

    public void addTokenizedString(MensajeInfo str, String buzon) {
        Map<String, WordCount> wordCount = new TreeMap<String, WordCount>();
        String body = null;
        try {
            body = str.getBody() + " " + str.getSubject();
        } catch (MessagingException ex) {
            Logger.getLogger(WordStore.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(WordStore.class.getName()).log(Level.SEVERE, null, ex);
        }
        Language lang = new LanguageDetection().detectLanguage(body);
        EmailTokenizer et = new EmailTokenizer(body);
        List<Token> tokens = et.tokenize();
        for (Token token : tokens) {
            token.setLanguage(lang); //language for stemming
            String stemmedForm = token.getStemmedForm();
            if (!wordCount.containsKey(stemmedForm)) {
                wordCount.put(stemmedForm, new WordCount(stemmedForm, 1));
            } else {
                WordCount wc = wordCount.get(stemmedForm);
                wc.setCount(wc.getCount() + 1);
            }

        }
        for (String word : wordCount.keySet()) {
            termFrequencyManager.addTermAppearancesInDocumentForFolder(word,
                    wordCount.get(word).getCount(),
                    buzon);
            termFrequencyManager.addNewDocumentForWord(word, buzon);
        }
        numDocumentosAnalizados++;
    }

    public WordStore() {
        //wordCount = new TreeMap<String, WordCount>();
        termFrequencyManager = new TermFrequencyManager();
    // leerFicheroStopWords().toArray();
    //ssbAna = new SnowballAnalyzer("Spanish", stopWordsArray);
    }

    public void writeToFile() {
        List<String> stopWords = leerFicheroStopWords();
        FileWriter outFile = null;
        System.out.println("Guardando palabras");
        try {
            outFile = new FileWriter(FICH_WORDS);
            PrintWriter out = new PrintWriter(outFile);
            //For each folder, we store the most frequent non-stopword terms
            for (String folder : termFrequencyManager.getTfidfByFolder().keySet()) {
                int contador = 0;
                System.out.println("Folder " + folder + " size " + termFrequencyManager.getTfidfByFolder().
                        get(folder).size());

                while (contador < 10 && contador < termFrequencyManager.getTfidfByFolder().
                        get(folder).size()) {

                    TFIDFSummary ts = termFrequencyManager.getTfidfByFolder().
                            get(folder).get(contador);
                    if (!stopWords.contains(ts.getTerm())) {
                        System.out.println(ts);
                        contador++;
                    }
                }
            }

            /* Collection<WordCount> coll = wordCount.values();
            List<WordCount> list = new ArrayList<WordCount>(coll);
            Collections.sort(list);
            //int size = list.size();
            
            
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
            System.out.println("La maxima era " + list.get(list.size() - 1));*/
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
                if (!carpetas[i].getFullName().contains(".Sent")) {
                    System.out.println("Extrayendo informacion de palabres de  " + carpetas[i].getFullName());
                    leerListaPalabrasCarpeta(carpetas[i]);
                } else {
                    System.out.println("No es necesario extraer palabras de " + carpetas[i].getFullName());
                }

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
                    //String body = msj.getBody();
                    addTokenizedString(msj, buzon.getName());
                }//for

            } catch (MessagingException e) {
                System.out.println("Folder " + buzon.getFullName() + " no encontrado al leer palabras");
            //e.printStackTrace();
            }
        }//if
    }
}
