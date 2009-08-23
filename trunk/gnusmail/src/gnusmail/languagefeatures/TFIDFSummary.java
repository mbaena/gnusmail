/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gnusmail.languagefeatures;

import java.security.InvalidParameterException;

/**
 * Un sumario de cuántas veces aparece un término, y en cuantos documentos diferentes
 * @author jmcarmona
 */
public class TFIDFSummary implements Comparable {

	String term;
	int numberOfDocuments;
	int termFrequency;
	long totalNumberOfWordsInThisFolder;
	int totalNumberOfDocumentsInThisFolder;

	public int getTotalNumberOfDocumentsInThisFolder() {
		return totalNumberOfDocumentsInThisFolder;
	}

	public void setTotalNumberOfDocumentsInThisFolder(int totalNumberOfDocumentsInThisFolder) {
		this.totalNumberOfDocumentsInThisFolder = totalNumberOfDocumentsInThisFolder;
	}

	public long getTotalNumberOfWordsInThisFolder() {
		return totalNumberOfWordsInThisFolder;
	}

	public void setTotalNumberOfWordsInThisFolder(long totalNumberOfWordsInThisFolder) {
		this.totalNumberOfWordsInThisFolder = totalNumberOfWordsInThisFolder;
	}

	public int getNumberOfDocumentsWithThisTerm() {
		return numberOfDocuments;
	}

	public void setNumberOfDocumentsWithThisTerm(int numberOfDocuments) {
		this.numberOfDocuments = numberOfDocuments;
	}

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}

	public int getTermFrequency() {
		return termFrequency;
	}

	public void setTermFrequency(int termFrequency) {
		this.termFrequency = termFrequency;
	}

	public void addNewAppearances(int number) {
		if (number >= 1) {
			termFrequency += number;
		}
	}

	public void addNewDocumentAppearance() {
		numberOfDocuments++;
	}

	/**
	 * This method retrieves the tf-idf score of the current term. 
	 */
	public double getTFIDFScore() {
		return getTFScore() * getIDFScore();
	}

	/**
	 * Term Frecuency, normalized by the number of words in this folder:
	 * @return
	 */
	public double getTFScore() {
		return (1.0 * getTermFrequency()) /
				(1.0 * getTotalNumberOfWordsInThisFolder());
	}

	public double getIDFScore() {
		return Math.log((1.0 * getTotalNumberOfDocumentsInThisFolder()) /
				(1*0 * getNumberOfDocumentsWithThisTerm() + 1));
	}

	public int compareTo(Object o) {
		if (!(o instanceof TFIDFSummary)) {
			throw new InvalidParameterException();
		} else {
			TFIDFSummary other = (TFIDFSummary) o;
			if (getTFIDFScore() < other.getTFIDFScore()) {
				return -1;
			} else if (getTFIDFScore() > other.getTFIDFScore()) {
				return 1;
			} else {
				return 0;
			}
		}
	}

	@Override
	public String toString() {
		return this.term + " <" + this.getTFIDFScore() + "> " +
				this.getNumberOfDocumentsWithThisTerm() + " " + this.getTermFrequency();
	}
}
