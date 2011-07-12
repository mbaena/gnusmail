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
package gnusmail;

import gnusmail.core.ConfigManager;
import gnusmail.core.cnx.Document;
import gnusmail.core.cnx.MessageInfo;
import gnusmail.filters.Filter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.Message;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffSaver;

/**
 * TODO
 * 
 * @author jmcarmona
 */
public class FilterManager {

	Instances dataset;
	List<Filter> filterList;

	public FilterManager() {
		this(ConfigManager.getFilters());
	}

	public FilterManager(String[] filterClassNames) {
		filterList = new ArrayList<Filter>();
		for (String fName : filterClassNames) {
			try {
				Filter filter = (Filter) Class.forName(fName).newInstance();
				filterList.add(filter);
			} catch (InstantiationException ex) {
				Logger.getLogger(FilterManager.class.getName()).log(
						Level.SEVERE, null, ex);
			} catch (IllegalAccessException ex) {
				Logger.getLogger(FilterManager.class.getName()).log(
						Level.SEVERE, null, ex);
			} catch (ClassNotFoundException ex) {
				Logger.getLogger(FilterManager.class.getName()).log(
						Level.SEVERE, null, ex);
			}
		}
	}

	/**
	 * Nuevo metodo Este metodo devuelve un conjunto de nombres de atributos con
	 * sus valores asociados, incluyendo la informacion de las palabras
	 * frecuentes
	 * 
	 * @param ws
	 * @return
	 */
	public Instances extractAttributeHeaders(DocumentReader reader) {

		for (Document doc: reader) {
			for (Filter filter : filterList) {
				filter.updateAttValues(doc);
			}
		}

		ArrayList<Attribute> attInfo = new ArrayList<Attribute>();
		for (Filter filter : filterList) {
			for (Attribute att : filter.getAttributes()) {
				attInfo.add(att);
			}
		}

		dataset = new Instances("gnusmail", attInfo, 0);
		try {
			dataset.setClass(dataset.attribute("Folder"));
		} catch (NullPointerException e) {
			System.out
					.println("Folder attribute not found; probably handling a multilabel dataset");
		}
		return dataset;
	}

	/**
	 * Extracts Attributes for a given message. A connection is opened and
	 * closed for each mail, as the number of open folders is limited (and we
	 * cannot predict it, since we are iterating over the mails
	 * chronologically):w
	 * 
	 */
	public Instance makeInstance(Document document) {
		if (dataset == null) {
			Logger.getLogger(FilterManager.class.getName()).log(Level.SEVERE,
					"Dataset is null");
			return null;
		}

		Instance inst = new DenseInstance(dataset.numAttributes());
		inst.setDataset(dataset);
		for (Filter filter : filterList) {
			filter.updateInstance(inst, document);
		}

		return inst;
		/*
		 * String[] sres = new String[res.size()]; if (msj.getFolder() != null)
		 * { msj.getFolder().close(false); }
		 */
	}

	public void writeToFile(Instances instances) {
		writeToFile(instances, null);
	}

	public void writeToFile(Instances instances, String filename) {
		if (instances == null) {
			Logger.getLogger(FilterManager.class.getName()).log(Level.SEVERE,
					"Dataset is null");
			return;
		}
		if (filename == null) {
			filename = ConfigManager.CONF_FOLDER + "dataset.arff";
		}
		File file = new File(filename);
		ArffSaver arffSaver = new ArffSaver();
		arffSaver.setInstances(instances);
		try {
			arffSaver.setFile(file);
			arffSaver.writeBatch();
		} catch (IOException e) {
			Logger.getLogger(FilterManager.class.getName()).log(Level.SEVERE,
					null, e);
		}
	}

	public Instances getDataset() {
		return dataset;
	}

}
