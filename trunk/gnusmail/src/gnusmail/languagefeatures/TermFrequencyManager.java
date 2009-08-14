/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gnusmail.languagefeatures;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Esta clase mantiene una lista de sumarios tfidf por carpeta
 * @author jmcarmona
 */
public class TermFrequencyManager {
    Map<String, List<TFIDFSummary>> tfidfByFolder;

    public Map<String, List<TFIDFSummary>> getTfidfByFolder() {
        return tfidfByFolder;
    }

    public void setTfidfByFolder(Map<String, List<TFIDFSummary>> tfidfByFolder) {
        this.tfidfByFolder = tfidfByFolder;
    }

    

    public TermFrequencyManager() {
        tfidfByFolder = new TreeMap<String, List<TFIDFSummary>>();
    }

    /**
     * This method must be called once per folder
     * @param appearances how many times does term appear in a document
     * @param folder
     * @param term
     */
    public void addTermAppearancesInDocumentForFolder(String term, int appearances, String folder) {
        if (!tfidfByFolder.containsKey(folder)) {
            tfidfByFolder.put(folder, new LinkedList<TFIDFSummary>());
        }
        TFIDFSummary summary = locateTermInList(term, tfidfByFolder.get(folder));
        summary.addNewAppearances(appearances);
    }

    /**
     * Like addTermAppearancesInDocumentForFolder, but adding appearances one by one
     * @param term
     * @param folder
     */
    public void addSingleTermAppearanceInDocumentForFolder(String term, String folder) {
        if (!tfidfByFolder.containsKey(folder)) {
            tfidfByFolder.put(folder, new LinkedList<TFIDFSummary>());
        }
        TFIDFSummary summary = locateTermInList(term, tfidfByFolder.get(folder));
        summary.addNewAppearances(1);
    }

    private TFIDFSummary locateTermInList(String term, List<TFIDFSummary> list) {
        boolean found = false;
        int counter = 0;
        TFIDFSummary res = null;

        while (!found && counter < list.size()) {
            if (list.get(counter).getTerm().equals(term)) {
                found = true;
                res = list.get(counter);
            }
            counter++;
        }
        if (res == null) {
            res = new TFIDFSummary();
            res.setTerm(term);
            list.add(res);
        }
        return res;
    }

    public void addNewDocumentForWord(String term, String folder) {
        TFIDFSummary tfidf = locateTermInList(term, this.tfidfByFolder.get(folder));
        tfidf.addNewDocumentAppearance();
    }




}
