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
package gnusmail.filters;

import gnusmail.core.ConfigManager;
import gnusmail.core.cnx.MessageInfo;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;

public class MultilabelFolder extends Filter {
	private Set<String> existingFolders;
	List<Attribute> attList;
	private Map<String, Attribute> attrMap;
	
	public MultilabelFolder() {
		this.existingFolders = new TreeSet<String>();
		attList = new ArrayList<Attribute>();
		attrMap = new TreeMap<String, Attribute>();		
	}

	@Override
	public List<Attribute> getAttributes() {
		attrMap = new TreeMap<String, Attribute>();
		for (String word : this.existingFolders) {
			FastVector values = new FastVector();
			values.addElement("1");
			values.addElement("0");
			Attribute att = new Attribute(word, values);
			attList.add(att);
			attrMap.put(word, att);
		}
		return attList;
	}

	@Override
	public void updateAttValues(MessageInfo msgInfo) {
		String mainFolder = msgInfo.getFolderAsString();
		List<String> listOfFolders = extractFoldersFromHierarchy(mainFolder);
		for (String folder : listOfFolders) {
			this.existingFolders.add(folder);
		}
	}
	

	@Override
	public void updateInstance(Instance inst, MessageInfo messageInfo) {
		List<String> listOfFolders = extractFoldersFromHierarchy(messageInfo.getFolderAsString());
        for (Attribute att : attList) {
        	if (listOfFolders.contains(att.name())) { 
				inst.setValue(att, "1");
			} else {
				inst.setValue(att, "0");
			}
		}
	}
	
	private List<String> extractFoldersFromHierarchy(String folder) {
		String separator = "\\.";
		String folders[] = folder.split(separator);
		List<String> multilabelFolders = new ArrayList<String>();
		int length = 0;
		for (int i = 0; i < folders.length; i++) {
			length += folders[i].length();
			String s = folder.substring(0, length + i);
			multilabelFolders.add(s);			
		}
		return multilabelFolders;
	}

	public void writeToFile() {
		String filename = ConfigManager.CONF_FOLDER + "labels.xml";
		try {
			Writer output = new BufferedWriter(new FileWriter(new File(filename)));
			output.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<labels xmlns=\"http://mulan.sourceforge.net/labels\">\n");
			for (String folder : this.existingFolders) {
				output.write("\t<label name=\"" + folder + "\"/>\n");
			}
			output.write("</labels>");
			output.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public void writeToHierarchicalFile() {
		String filename = ConfigManager.CONF_FOLDER + "labels.xml";
		try {
			Writer output = new BufferedWriter(new FileWriter(new File(filename)));
			output.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<labels xmlns=\"http://mulan.sourceforge.net/labels\">\n");
			Iterator<String> iter = this.existingFolders.iterator();
			if (iter.hasNext()) {
				String folder = iter.next();
				recursiveLabels(iter, output, folder, "\t");
				output.write("\t<label name=\"INBOX.DUMMY\"/>\n");				
			}
			output.write("</labels>");
			output.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private String recursiveLabels(Iterator<String> iter, Writer output, String parentFolder, String tab) throws IOException {
		output.write(tab + "<label name=\"" + parentFolder + "\">\n");
		int count = 0;
		boolean readNextFolder = true; 
		String folder = null;
		while (iter.hasNext()) {
			if (readNextFolder) {
				folder = iter.next();
			} else {
				readNextFolder = true;
			}
			if (folder.startsWith(parentFolder)) {
			    folder = recursiveLabels(iter, output, folder, tab+"\t");
			    if (folder!=null) {
			    	readNextFolder = false;
			    }
			} else {
				break;
			}
			count++;
		}	
		if (count==1) {
			output.write(tab+"\t<label name=\"" + parentFolder + ".DUMMY\"/>\n");			
		}
		output.write(tab + "</label>\n");
		return folder;
	}

}
