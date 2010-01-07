package gnusmail.filesystem;

import java.io.File;
import java.util.Iterator;

/**
 *
 * @author jmcarmona
 */
public class FSFoldersReader implements Iterable<File> {
	String baseFolder;

	public FSFoldersReader(String baseFolder) {
		this.baseFolder = baseFolder;
	}


	public Iterator<File> iterator() {
		return new FSFolderIterator(baseFolder);
	}

}
