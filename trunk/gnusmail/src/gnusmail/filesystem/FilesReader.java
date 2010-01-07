package gnusmail.filesystem;

import java.io.File;
import java.util.Iterator;

/**
 *
 * @author jmcarmona
 */
public class FilesReader implements Iterable<File> {
	String baseFolder;
	boolean recursive;

	public FilesReader(String baseFolder, boolean recursive) {
		this.baseFolder = baseFolder;
		this.recursive = recursive;
	}


	public Iterator<File> iterator() {
		return new FolderMessagesIterator(baseFolder, recursive);
	}

}
