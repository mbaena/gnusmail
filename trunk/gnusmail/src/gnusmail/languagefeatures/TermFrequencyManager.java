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
        }
        return res;
    }




}
