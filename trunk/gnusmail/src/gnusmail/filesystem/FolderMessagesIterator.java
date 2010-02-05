package gnusmail.filesystem;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import weka.classifiers.UpdateableClassifier;
import weka.classifiers.bayes.DMNBtext;
import weka.classifiers.bayes.NaiveBayesMultinomialUpdateable;

/**
 *
 * @author jmcarmona
 */
public class FolderMessagesIterator implements Iterator<File> {

	private boolean recursive;
	private int limitMessages = 50;
	private String baseFolder;
	protected List<File> foldersToBeOpened;
	protected List<File> filesToRetrieve;

	public String getBaseFolder() {
		return baseFolder;
	}

	public void setBaseFolder(String baseFolder) {
		this.baseFolder = baseFolder;
	}

	public FolderMessagesIterator(String baseFolder, int limitMessages) {
		this.baseFolder = baseFolder;
		this.foldersToBeOpened = new ArrayList<File>();
		this.foldersToBeOpened.add(new File(baseFolder));
		this.filesToRetrieve = new ArrayList<File>();
		this.recursive = true;
		this.limitMessages = limitMessages;
	}

	public FolderMessagesIterator(String baseFolder, boolean recursive, int limitMessages) {
		System.out.println("create Folder Messages Iterator " + baseFolder + " " + recursive);
		this.baseFolder = baseFolder;
		this.foldersToBeOpened = new ArrayList<File>();
		this.foldersToBeOpened.add(new File(baseFolder));
		this.filesToRetrieve = new ArrayList<File>();
		this.recursive = recursive;
		this.limitMessages = limitMessages;
		//Alternativa: sin while
		while (filesToRetrieve.size() == 0 && foldersToBeOpened.size() > 0) {
			expandFolder(foldersToBeOpened.get(0));
			foldersToBeOpened.remove(0);
		}
	}

	public boolean hasNext() {
		return filesToRetrieve.size() > 0;// || filesToRetrieve.size() > 0;
	}

	public File next() {
		File res = null;
		if (filesToRetrieve.size() > 0) {
			res = filesToRetrieve.get(0);
			filesToRetrieve.remove(0);
		} else if (foldersToBeOpened.size() > 0 && recursive) {
			expandFolder(foldersToBeOpened.get(0));
			foldersToBeOpened.remove(0);
			if (filesToRetrieve.size() > 0) {
				res = filesToRetrieve.get(0);
				filesToRetrieve.remove(0);
			}
		}

		//We try to get new messages before the next call to next()
		while (filesToRetrieve.size() == 0 && foldersToBeOpened.size() > 0) {
			expandFolder(foldersToBeOpened.get(0));
			foldersToBeOpened.remove(0);
		}
		return res;
	}

	public void remove() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	private void expandFolder(File folder) {
		int openedMessages = 0;
		if (foldersToBeOpened != null && foldersToBeOpened.size() > 0) {
			File[] listOfFiles = folder.listFiles();
			for (int i = 0; i < listOfFiles.length; i++) {
				File auxFile = listOfFiles[i];
				if (auxFile.isFile() && auxFile != null && openedMessages < limitMessages) {
					openedMessages++;
					filesToRetrieve.add(auxFile);
				} else if (auxFile.isDirectory() && mustBeOpened(auxFile)) {
					foldersToBeOpened.add(auxFile);
				}
			}
		} else {
			System.out.println("WARNING: Imposible expandir " + folder);
		}

	}

	/**
	 * This function filters folders with 1 or 2 mails, and topical folders
	 * @param auxFile
	 * @return
	 */
	private boolean mustBeOpened(File auxFile) {
		String name = auxFile.getName().toLowerCase();
		int numOfMails = auxFile.listFiles().length;
		boolean topical =  name.contains("sent") || name.contains("inbox") ||
				name.contains("trash") || name.contains("all_documents") ||
				name.contains("drafts") || name.contains("discussion_threads") || name.contains("deleted");
		return numOfMails > 2 && !topical;
	}
}
