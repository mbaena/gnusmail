package gnusmail.core;

import gnusmail.Languages.Language;
import gnusmail.Options;
import gnusmail.core.cnx.Connection;
import gnusmail.core.cnx.MessageInfo;
import gnusmail.filesystem.FSFoldersReader;
import gnusmail.filesystem.MessageFromFileReader;
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
	public final static File STOPWORDS_FILE_EN = new File(configFolder + "/english-stopwords.data");
	public final static File STOPWORDS_FILE_ES = new File(configFolder + "/spanish-stopwords.data");
	public final static double PROP_DOCUMENTS = 0.45;
	public final static int MIN_DOCUMENTS = 3;
	public final static int MAX_NUM_ATTRIBUTES = 200;
	int numAnalyzedDocuments = 0;
	Map<Language, List<String>> stopWords;

	public void addTokenizedString(MessageInfo str, String folderName) {
		Map<String, WordCount> wordCount = new TreeMap<String, WordCount>();
		String body = null;
		try {
			body = str.getBody() + " " + str.getSubject();
		} catch (MessagingException ex) {
			System.out.println("MessagingException: couldn't add " + str);
		} catch (IOException ex) {
			System.out.println("IOException: couldn't add " + str);
		}
		Language lang = new LanguageDetection().detectLanguage(body);
		EmailTokenizer et = new EmailTokenizer(body);
		List<Token> tokens = et.tokenize();
		int numberOfTokens = tokens.size();
		for (Token token : tokens) {
			token.setLanguage(lang); //language for stemming
			String stemmedForm = token.getStemmedForm();
			if (!stopWords.get(lang).contains(stemmedForm)) {
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
		System.out.println((d2.getTime() - d1.getTime()) + " " + (d3.getTime() - d2.getTime()) + " " + wordCount.size() + " "+ tokens.size());
	}

	public WordsStore() {
		termFrequencyManager = new TermFrequencyManager();
		readStopWordsFile();
	}

	public void writeToFile() {
		FileWriter outFile = null;
		Set<String> wordsToWrite = new TreeSet<String>();

		try {
			outFile = new FileWriter(WORDS_FILE);
			PrintWriter out = new PrintWriter(outFile);
			//For each folder, we store the most frequent non-stopword terms
			for (String folder : termFrequencyManager.getTfidfByFolder().keySet()) {
				int index = 0;
				List<TFIDFSummary> tfidSummaries =
						termFrequencyManager.getTfidfByFolder().get(folder);
				Collections.sort(tfidSummaries);

				while (index < 10 && index < tfidSummaries.size()) {
					TFIDFSummary ts = tfidSummaries.get(tfidSummaries.size() - 1 - index);
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
			FileInputStream fstream = new FileInputStream(STOPWORDS_FILE_ES);
			// Get the object of DataInputStream
			DataInputStream in = new DataInputStream(fstream);
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
			fstream = new FileInputStream(STOPWORDS_FILE_EN);
			// Get the object of DataInputStream
			in = new DataInputStream(fstream);
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

	public void readWordsList(Connection myConnection) {
		if (Options.getInstance().isReadMailsFromFileSystem()) {
			readWordsListFromFS();
		} else {
			readWordsListFromConnection(myConnection);
		}
	}

	public void readWordsListFromConnection(Connection myConnection) {
		Folder[] folders;
		try {
			folders = myConnection.getFolders();

			for (int i = 0; i < folders.length; i++) {
				if (!folders[i].getFullName().contains(".Sent")) {
					readWordsListForIMAPFolder(folders[i]);
				}
			}
			writeToFile();
		} catch (MessagingException e) {
			e.printStackTrace();
		} catch (Exception e) {
			System.out.println();
			e.printStackTrace();
		}
	}

	public void readWordsListForIMAPFolder(Folder folder) {
		if (folder != null) {
			try {
				if (!folder.isOpen()) {
					folder.open(javax.mail.Folder.READ_WRITE);
				}
				List<MessageInfo> lastMessagesInFolder = createLastMessagesList(folder, MAX_MESSAGES_PER_FOLDER);
				for (MessageInfo msj : lastMessagesInFolder) {
					addTokenizedString(msj, folder.getName());
				}//for
				termFrequencyManager.updateWordCountPorFolder(folder.getName());
				termFrequencyManager.setNumberOfDocumentsByFolder(folder.getName(), lastMessagesInFolder.size());
				if (folder.isOpen()) {
					folder.close(false); //Cerramos el buzon
				}
			} catch (MessagingException e) {
				e.printStackTrace();
			}
		}//if
	}

	private void readWordsListFromFS() {
		System.out.println("Read words list from FS");
		FSFoldersReader fReader = new FSFoldersReader(ConfigManager.MAILDIR.getAbsolutePath());
		for (File folder : fReader) {
			System.out.println("Read words list from fs: " + folder.getAbsolutePath());
			readWordsListFromFSFolder(folder);
		}
		System.out.println("Write to file...");
		writeToFile();
		System.out.println("Written to file...");
	}

	private void readWordsListFromFSFolder(File folder) {
		MessageFromFileReader mReader = new MessageFromFileReader(folder.getAbsolutePath(), false);
		int numberOfMessages = 0;
		System.out.println("Folder : " + folder.getAbsolutePath());
		for (Message mes : mReader) {
			Date d = new Date();
			addTokenizedString(new MessageInfo(mes), folder.getName());
			Date d2 = new Date();
			numberOfMessages++;
			if (numberOfMessages % 10 == 0) {
				System.out.println(folder.getAbsolutePath() + " Number of messages " + numberOfMessages);
				System.out.println("Dura " + (d2.getTime() - d.getTime()));
			}
			if (numberOfMessages == 50) break;
		}
		termFrequencyManager.updateWordCountPorFolder(folder.getName());
		termFrequencyManager.setNumberOfDocumentsByFolder(folder.getName(), numberOfMessages);
	}
}
