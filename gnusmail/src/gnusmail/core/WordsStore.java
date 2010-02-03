package gnusmail.core;

import gnusmail.Languages.Language;
import gnusmail.core.cnx.MessageInfo;
import gnusmail.languagefeatures.TFIDFSummary;
import gnusmail.languagefeatures.TermFrequencyManager;
import gnusmail.languagefeatures.Token;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import javax.mail.Folder;
import javax.mail.MessagingException;

/**
 * This classes manages a map word -> number of instances.
 * The class WordCount manages the number of instances
 * @author jmcarmona
 */
public class WordsStore {
	TermFrequencyManager termFrequencyManager;
	public final static String tokenPattern = " \t\n\r\f.,;:?¿!¡\"()'=[]{}/<>-*0123456789ªº%&*@_|’";
	public final static String configFolder = System.getProperty("user.home") + "/.gnusmail/";
	public final static String STOPWORDS_RESOURCE_ES = "/resources/spanish-stopwords.data";
	public final static String STOPWORDS_RESOURCE_EN = "/resources/english-stopwords.data";
	public final static int MAX_NUM_ATTRIBUTES_BY_FOLDER = 20;
	int numAnalyzedDocuments = 0;
	List<String> frequentWords = null;
	Map<Language, List<String>> stopWords;

	public TermFrequencyManager getTermFrequencyManager() {
		return termFrequencyManager;
	}

	public void setTermFrequencyManager(TermFrequencyManager termFrequencyManager) {
		this.termFrequencyManager = termFrequencyManager;
	}

	public void addTokenizedString(List<Token> tokens, String folderName) {
		Map<String, WordCount> wordCount = new TreeMap<String, WordCount>();
		int numberOfTokens = tokens.size();
		for (Token token : tokens) {
			String stemmedForm = token.getStemmedForm();
			if (!stopWords.get(token.getLanguage()).contains(stemmedForm) &&
					!stopWords.get(token.getLanguage()).contains(token.getLowerCaseForm()) &&
					stemmedForm.length() > 2) {
				if (!wordCount.containsKey(stemmedForm)) {
					wordCount.put(stemmedForm, new WordCount(stemmedForm, 1));
				} else {
					WordCount wc = wordCount.get(stemmedForm);
					wc.setCount(wc.getCount() + 1);
				}
			}

		}
		for (String word : wordCount.keySet()) {
			termFrequencyManager.addTermAppearancesInDocumentForFolder(word,
					wordCount.get(word).getCount(),
					folderName);
			termFrequencyManager.addNewDocumentForWord(word, folderName);
		}
		//We update the number of words for this folder
		termFrequencyManager.addNumberOfWordsPerFolder(folderName, numberOfTokens);
		numAnalyzedDocuments++;
	}

	public WordsStore() {
		termFrequencyManager = new TermFrequencyManager();
		readStopWordsFile();
	}

	public List<String> getFrequentWords() {
		//For each folder, we store the most frequent non-stopword terms
		//We compute the frequent words only once
		if (this.frequentWords == null) {
			Set<String> wordsToReturn = new TreeSet<String>();
			for (String folder : termFrequencyManager.getTfidfByFolder().keySet()) {
				int index = 0;
				Map<String, TFIDFSummary> tfidSummaries =
						termFrequencyManager.getTfidfByFolder().get(folder);
				ArrayList<TFIDFSummary> tfidfSummariesList = new ArrayList<TFIDFSummary>(tfidSummaries.size());
				for (String term : tfidSummaries.keySet()) {
					tfidfSummariesList.add(tfidSummaries.get(term));
				}
				Collections.sort(tfidfSummariesList);

				//while (index < tfidfSummariesList.size()) {
				while (index < tfidfSummariesList.size() && index < MAX_NUM_ATTRIBUTES_BY_FOLDER) {
					TFIDFSummary ts = tfidfSummariesList.get(tfidfSummariesList.size() - 1 - index);
					wordsToReturn.add(ts.getTerm());
					index++;
				}
			}

			this.frequentWords = new ArrayList<String>(wordsToReturn);
		}
		System.out.println("Numero de palabras frecuentes: " + this.frequentWords.size());
		for (String w : frequentWords) System.out.println("FreqWord " + w);
		return this.frequentWords;
	}

	/**
	 * @deprecated
	 */
	/*public void writeToFile() {
		FileWriter outFile = null;
		Set<String> wordsToWrite = new TreeSet<String>();

		try {
			outFile = new FileWriter(WORDS_FILE);
			PrintWriter out = new PrintWriter(outFile);
			//For each folder, we store the most frequent non-stopword terms
			for (String folder : termFrequencyManager.getTfidfByFolder().keySet()) {
				int index = 0;
				Map<String, TFIDFSummary> tfidSummaries =
						termFrequencyManager.getTfidfByFolder().get(folder);
				ArrayList<TFIDFSummary> tfidfSummariesList = new ArrayList<TFIDFSummary>(tfidSummaries.size());
				for (String term : tfidSummaries.keySet()) {
					tfidfSummariesList.add(tfidSummaries.get(term));
				}
				Collections.sort(tfidfSummariesList);

				while (index < tfidfSummariesList.size()) {
					TFIDFSummary ts = tfidfSummariesList.get(tfidSummaries.size() - 1 - index);
					System.out.println(ts);
					wordsToWrite.add(ts.getTerm());
					index++;
				}
			}
			for (String word : wordsToWrite) {
				out.println(word);
			}

			out.close();
		} catch (IOException ex) {
			Logger.getLogger(WordsStore.class.getName()).log(Level.SEVERE, null, ex);
		} finally {
			try {
				outFile.close();
			} catch (IOException ex) {
				Logger.getLogger(WordsStore.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}*/

	/**
	 * This function creates a list with the maxMessagesPerFolder newer messages
	 * of a given folder
	 * @param buzon
	 * @param maxMessagesPerFolder
	 * @return
	 * @throws javax.mail.MessagingException
	 */
	private List<MessageInfo> createLastMessagesList(Folder folder, int maxMessagesPerFolder) throws MessagingException {
		List<MessageInfo> messages = new ArrayList<MessageInfo>();
		List<MessageInfo> messagesToReturn = new ArrayList<MessageInfo>();
		if (folder.getMessageCount() > 0) {
			for (int i = 1; i <= folder.getMessageCount(); i++) {
				MessageInfo msj = new MessageInfo(folder.getMessage(i));
				messages.add(msj);
			}//for
			Collections.sort(messages);
			for (int i = 0; i < maxMessagesPerFolder && i < messages.size(); i++) {
				messagesToReturn.add(messages.get(messages.size() - 1 - i));
			}
		}
		return messagesToReturn;
	}

	private void readStopWordsFile() {
		List<String> res = new ArrayList<String>();
		try {
			// Open the file that is the first
			// command line parameter
			InputStream is = WordsStore.class.getResourceAsStream(STOPWORDS_RESOURCE_ES);
			// Get the object of DataInputStream
			DataInputStream in = new DataInputStream(is);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			//Read File Line By Line
			List<String> wordsEs = new ArrayList<String>();
			while ((strLine = br.readLine()) != null) {
				// Print the content on the console
				wordsEs.add(strLine);
			}
			//Close the input stream
			in.close();
			is = WordsStore.class.getResourceAsStream(STOPWORDS_RESOURCE_ES);
			// Get the object of DataInputStream
			in = new DataInputStream(is);
			br = new BufferedReader(new InputStreamReader(in));
			//Read File Line By Line
			List<String> wordsEn = new ArrayList<String>();
			while ((strLine = br.readLine()) != null) {
				// Print the content on the console
				wordsEn.add(strLine);
			}
			//Close the input stream
			in.close();
			stopWords = new TreeMap<Language, List<String>>();
			stopWords.put(Language.SPANISH, wordsEs);
			stopWords.put(Language.ENGLISH, wordsEn);
		} catch (Exception e) {//Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
	}

	/**
	 * @deprecated 
	 * @param reader
	 */
	/*public void readWordsList(MessageReader reader) {
		int numberOfMessages = 0;
		Map<String, Integer> folderMap = new TreeMap<String, Integer>();
		for (Message msg : reader) { //Esto a WordsFrequency
			//TODO mirar por que esta esto
			//if (numberOfMessages == MAX_MESSAGES_PER_FOLDER) break;
			MessageInfo msgInfo = new MessageInfo(msg);
			String folder = msgInfo.getFolderAsString();
			addTokenizedString(WordFrequency.tokenizeMessageInfo(msgInfo), folder);
			numberOfMessages++;
			if (numberOfMessages % 10 == 0) {
				System.out.println(msgInfo.getFolderAsString() + " Number of messages " + numberOfMessages);
			}
			try {
				folderMap.put(folder, folderMap.get(folder) + 1);
			} catch (NullPointerException e) {
				folderMap.put(folder, 1);
			}
		}

		for (String folder : folderMap.keySet()) { //Esto a wordsfreqency, pero en el futuro metodo get atributes
			termFrequencyManager.updateWordCountPorFolder(folder);
			termFrequencyManager.setNumberOfDocumentsByFolder(folder, folderMap.get(folder));
		}

		System.out.println("Write to file...");
		writeToFile();
		System.out.println("Written to file...");
	}*/
}
