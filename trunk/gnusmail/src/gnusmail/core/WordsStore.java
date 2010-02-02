package gnusmail.core;

import gnusmail.Languages.Language;
import gnusmail.MessageReader;
import gnusmail.Options;
import gnusmail.core.cnx.Connection;
import gnusmail.core.cnx.MessageInfo;
import gnusmail.filesystem.FSFoldersReader;
import gnusmail.filesystem.MessageFromFileReader;
import gnusmail.filters.WordFrequency;
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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;

/**
 * This classes manages a map word -> number of instances.
 * The class WordCount manages the number of instances
 * @author jmcarmona
 */
public class WordsStore {
	//Max number of messages to extract information from

	private final int MAX_MESSAGES_PER_FOLDER = 50;
	TermFrequencyManager termFrequencyManager;
	public final static String tokenPattern = " \t\n\r\f.,;:?¿!¡\"()'=[]{}/<>-*0123456789ªº%&*@_|’";
	public final static String configFolder = System.getProperty("user.home") + "/.gnusmail/";
	public final static File WORDS_FILE = new File(configFolder + "/wordlist.data");
	public final static String STOPWORDS_RESOURCE_ES = "/resources/spanish-stopwords.data";
	public final static String STOPWORDS_RESOURCE_EN = "/resources/english-stopwords.data";
	public final static double PROP_DOCUMENTS = 0.45;
	public final static int MIN_DOCUMENTS = 3;
	public final static int MAX_NUM_ATTRIBUTES = 400;
	int numAnalyzedDocuments = 0;
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
		Date d1 = new Date();
		for (String word : wordCount.keySet()) {
			termFrequencyManager.addTermAppearancesInDocumentForFolder(word,
					wordCount.get(word).getCount(),
					folderName);
			termFrequencyManager.addNewDocumentForWord(word, folderName);
		}
		Date d2 = new Date();
		//We update the number of words for this folder
		termFrequencyManager.addNumberOfWordsPerFolder(folderName, numberOfTokens);
		Date d3 = new Date();
		numAnalyzedDocuments++;
	}

	public WordsStore() {
		termFrequencyManager = new TermFrequencyManager();
		readStopWordsFile();
	}

	public List<String> getFrequentWords() {
		//For each folder, we store the most frequent non-stopword terms
		Set<String> wordsToReturn = new TreeSet<String>();
		for (String folder : termFrequencyManager.getTfidfByFolder().keySet()) {
			int index = 0;
			Map<String, TFIDFSummary> tfidSummaries =
					termFrequencyManager.getTfidfByFolder().get(folder);
			ArrayList<TFIDFSummary> tfidfSummariesList = new ArrayList<TFIDFSummary>(tfidSummaries.size());
			for (String term: tfidSummaries.keySet()) {
				tfidfSummariesList.add(tfidSummaries.get(term));
			}
			Collections.sort(tfidfSummariesList);

			while (index < tfidfSummariesList.size()) {
				TFIDFSummary ts = tfidfSummariesList.get(tfidfSummariesList.size() - 1 - index);
				wordsToReturn.add(ts.getTerm());
				index++;
			}
		}

		return new ArrayList<String>(wordsToReturn);
	}

	/**
	 * @deprecated
	 */
	public void writeToFile() {
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
				for (String term: tfidSummaries.keySet()) {
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
	}

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

	public void readWordsList(MessageReader reader) {
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
	}
}
