/*
 * Copyright 2011 Universidad de Málaga.  All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 3 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 3 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 3 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Universidad de Málaga, 29071 Malaga, Spain or visit
 * www.uma.es if you need additional information or have any questions.
 * 
 */
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
