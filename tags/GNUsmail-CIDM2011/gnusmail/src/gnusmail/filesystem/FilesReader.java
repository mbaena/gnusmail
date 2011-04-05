package gnusmail.filesystem;

import java.io.File;
import java.util.Iterator;

/**
 *
 * @author jmcarmona
 */
public class FilesReader implements Iterable<File> {
	private String baseFolder;
	private boolean recursive;
	private int limit;

	public FilesReader(String baseFolder, boolean recursive, int limit) {
		this.baseFolder = baseFolder;
		this.recursive = recursive;
		this.limit = limit;
	}


	public Iterator<File> iterator() {
		return new FolderMessagesIterator(baseFolder, recursive, limit);
	}

}
