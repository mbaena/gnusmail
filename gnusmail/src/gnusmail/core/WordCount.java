/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gnusmail.core;

/**
 *
 * @author jmcarmona
 */
class WordCount implements Comparable {
    String word;
    int count;

    public WordCount(String word, int count) {
        this.word = word;
        this.count = count;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final WordCount other = (WordCount) obj;
        if ((this.word == null) ? (other.word != null) : !this.word.equals(other.word)) {
            return false;
        }
        if (this.count != other.count) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + (this.word != null ? this.word.hashCode() : 0);
        hash = 17 * hash + this.count;
        return hash;
    }

    


    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }



    public int compareTo(Object o) {
        int res = 0;
        if (o instanceof WordCount) {
            WordCount wc = (WordCount) o;
            if (getCount() < wc.getCount()) {
                res = -1;
            } else if (getCount() > wc.getCount()) {
                res = 1;
            }
        }
        return res;
    }

    @Override
    public String toString() {
        return getWord() + " " + getCount();
    }
}
