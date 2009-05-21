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

    public int getNumberOfDocuments() {
        return numberOfDocuments;
    }

    public void setNumberOfDocuments(int numberOfDocuments) {
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

    public double getTFIDFScore() {
        return getTermFrequency() / getNumberOfDocuments();
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
}
