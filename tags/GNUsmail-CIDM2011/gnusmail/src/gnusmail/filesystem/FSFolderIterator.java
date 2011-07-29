package gnusmail.filesystem;

import java.io.File;

/**
 *
 * @author jmcarmona
 */
public class FSFolderIterator extends FolderMessagesIterator {

	public FSFolderIterator(String baseFolder, int limit) {
		super(baseFolder, limit);
		System.out.println("Tras la creacionk, num de folders "  + foldersToBeOpened.size());
	}


	@Override
	public File next() {
		File res = null;
		if (foldersToBeOpened.size() > 0) {
			res = foldersToBeOpened.get(0);
			expandFolder(foldersToBeOpened.get(0));
			foldersToBeOpened.remove(0);
			if (filesToRetrieve.size() > 0) {
				res = filesToRetrieve.get(0);
				filesToRetrieve.remove(0);
			}
		}
		System.out.println("FSFolderiterator : next");
		return res;
	}

	@Override
	public boolean hasNext() {
		return foldersToBeOpened.size() > 0;// || filesToRetrieve.size() > 0;
	}
	protected void expandFolder(File folder) {
		if (foldersToBeOpened != null && foldersToBeOpened.size() > 0) {
			File[] listOfFiles = folder.listFiles();
			for (int i = 0; i < listOfFiles.length; i++) {
				File auxFile = listOfFiles[i];
				if (auxFile.isDirectory() && !forbidden(auxFile.getAbsolutePath())) {
					foldersToBeOpened.add(auxFile);
				}
			}
		} else {
			System.out.println("Imposible expandir");
		}

	}

	private boolean forbidden(String absolutePath) {
		boolean isSent =  absolutePath.toLowerCase().contains("sent");
		boolean isDeleted =  absolutePath.toLowerCase().contains("deleted");
		return isSent  || isDeleted;
	}

}