/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gnusmail.languagefeatures;

import gnusmail.Languages.Language;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author jmcarmona
 */
public class StopWordsProvider {
    static private final String MAIN_FOLDER = System.getProperty("user.home") + "/.gnusmail/";

    static StopWordsProvider instance;
    static final String SPANISH_FILE = MAIN_FOLDER + "spanish-stopwords.data";
    static final String ENGLISH_FILE = MAIN_FOLDER + "english-stopwords.data";
    public Map<Language, List<String>> stopwordsMap;

        public Map<Language, List<String>> getStopwordsMap() {
        return stopwordsMap;
    }

    public void setStopwordsMap(Map<Language, List<String>> stopwordsMap) {
        this.stopwordsMap = stopwordsMap;
    }

    

    protected StopWordsProvider() {
        List<String> swSpanish = new ArrayList<String>();
        List<String> swEnglish = new ArrayList<String>();

        swSpanish = loadSWList(SPANISH_FILE);
        swEnglish =loadSWList(ENGLISH_FILE);

        stopwordsMap = new TreeMap<Language, List<String>>();
        stopwordsMap.put(Language.ENGLISH, swEnglish);
        stopwordsMap.put(Language.SPANISH, swSpanish);
        
    }

    public static StopWordsProvider getInstance() {
        if (instance == null) {
            instance = new StopWordsProvider();
        }
        return instance;
    }

    /**
     * This function loads a list of stopwords from a given file (ie for a given
     * languave
     * @param swList
     * @param file
     */
    private List<String> loadSWList(String file) {
        List<String> swList = new ArrayList<String>();
        swList = new ArrayList<String>();
        try {
            FileInputStream fstream = new FileInputStream(file);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            while ((strLine = br.readLine()) != null) {
                swList.add(strLine);
            }
            in.close();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
       return swList;

    }

    private int countWordsAppearingInList(String string, List<String> wordList) {
        int count = 0;
        String strLower = string.toLowerCase();
        for (String stopword : wordList) {
            if (strLower.contains(stopword)) {
                count++;
            }
        }
        return count;
    }


    public int countWordsFromLanguage(String string, Language lang) {
        return countWordsAppearingInList(string, stopwordsMap.get(lang));
    }

    public String removeStopWords(String string, Language lang) {
        for (String stopword : stopwordsMap.get(lang)) {
            Pattern pat = Pattern.compile("\\b" + stopword + "\\b");
            Matcher matcher = pat.matcher(string);
            string = matcher.replaceAll("");
        }
        return string;
    }
}
