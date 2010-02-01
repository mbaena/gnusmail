package gnusmail.languagefeatures;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * This class mantains a list of tfidf summaries by folder
 * 
 * @author jmcarmona
 */
public class TermFrequencyManager {

	Map<String, Map<String, TFIDFSummary>> tfidfByFolder;
	Map<String, Long> numberOfWordsByFolder;
	Map<String, Integer> numberOfDocumentsByFolder;

	public Map<String, Integer> getNumberOfDocumentsByFolder() {
		return numberOfDocumentsByFolder;
	}

	public void setNumberOfDocumentsByFolder(String folder,
			int numberOfDocumentsByFolder) {
		if (this.getNumberOfDocumentsByFolder() == null) {
			this.numberOfDocumentsByFolder = new TreeMap<String, Integer>();
		}
		this.numberOfDocumentsByFolder.put(folder, numberOfDocumentsByFolder);
		if (tfidfByFolder.containsKey(folder)) {
			Map<String, TFIDFSummary> termSummaries = tfidfByFolder.get(folder);
			for (String term : termSummaries.keySet()) {
				TFIDFSummary tfidf = termSummaries.get(term);
				tfidf
						.setTotalNumberOfDocumentsInThisFolder(numberOfDocumentsByFolder);
			}
		}
	}

	public Map<String, Long> getNumberOfWordsByFolder() {
		return numberOfWordsByFolder;
	}

	public void setNumberOfWordsByFolder(Map<String, Long> numberOfWordsByFolder) {
		this.numberOfWordsByFolder = numberOfWordsByFolder;
	}

	public Map<String, Map<String, TFIDFSummary>> getTfidfByFolder() {
		return tfidfByFolder;
	}

	public void setTfidfByFolder(
			Map<String, Map<String, TFIDFSummary>> tfidfByFolder) {
		this.tfidfByFolder = tfidfByFolder;
	}

	public TermFrequencyManager() {
		tfidfByFolder = new TreeMap<String, Map<String, TFIDFSummary>>();
	}

	/**
	 * This method must be called once per folder
	 * 
	 * @param appearances
	 *            how many times does term appear in a document
	 * @param folder
	 * @param term
	 */
	public void addTermAppearancesInDocumentForFolder(String term,
			int appearances, String folder) {
		if (!tfidfByFolder.containsKey(folder)) {
			tfidfByFolder.put(folder, new TreeMap<String, TFIDFSummary>());
		}
		TFIDFSummary summary = locateTermInList(term, tfidfByFolder.get(folder));
		summary.addNewAppearances(appearances);
	}

	/**
	 * Like addTermAppearancesInDocumentForFolder, but adding appearances one by
	 * one
	 * 
	 * @param term
	 * @param folder
	 */
	public void addSingleTermAppearanceInDocumentForFolder(String term,
			String folder) {
		if (!tfidfByFolder.containsKey(folder)) {
			tfidfByFolder.put(folder, new TreeMap<String, TFIDFSummary>());
		}
		TFIDFSummary summary = locateTermInList(term, tfidfByFolder.get(folder));
		summary.addNewAppearances(1);
	}

	private TFIDFSummary locateTermInList(String term,
			Map<String, TFIDFSummary> termSummaries) {
		TFIDFSummary res = termSummaries.get(term);
		if (res == null) {
			res = new TFIDFSummary();
			res.setTerm(term);
			termSummaries.put(term, res);
		}
		return res;
	}

	public void addNewDocumentForWord(String term, String folder) {
		TFIDFSummary tfidf = locateTermInList(term, this.tfidfByFolder
				.get(folder));
		tfidf.addNewDocumentAppearance();
	}

	public void addNumberOfWordsPerFolder(String folder, int numberOfWords) {
		if (getNumberOfWordsByFolder() == null) {
			numberOfWordsByFolder = new TreeMap<String, Long>();
		}
		if (!getNumberOfWordsByFolder().containsKey(folder)) {
			numberOfWordsByFolder.put(folder, new Long(0));
		}
		numberOfWordsByFolder.put(folder, numberOfWordsByFolder.get(folder)
				+ numberOfWords);
	}

	public void updateWordCountPorFolder(String folder) {
		Map<String, TFIDFSummary> termSummaries = tfidfByFolder.get(folder);
		if (termSummaries != null) {
			for (String term : termSummaries.keySet()) {				
				TFIDFSummary tfidf = termSummaries.get(term);
				tfidf.setTotalNumberOfWordsInThisFolder(numberOfWordsByFolder.get(folder));
			}
			System.out.println("Word count succesfully update for " + folder);
		} else {
			System.out.println("Warning: coulnd't update words from " + folder);
		}
	}
}
