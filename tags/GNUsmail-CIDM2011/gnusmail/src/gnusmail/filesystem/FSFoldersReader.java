package gnusmail.filesystem;

import java.io.File;
import java.util.Iterator;

/**
 *
 * @author jmcarmona
 */
public class FSFoldersReader implements Iterable<File> {
	String baseFolder;
	int limit;

	public FSFoldersReader(String baseFolder, int limit) {
		this.baseFolder = baseFolder;
		this.limit = limit;

	}


	public Iterator<File> iterator() {
		return new FSFolderIterator(baseFolder, this.limit);
	}

}
