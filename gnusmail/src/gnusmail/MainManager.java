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
import gnusmail.core.cnx.Connection;
import gnusmail.core.cnx.Document;
import gnusmail.core.cnx.MessageInfo;
import gnusmail.filters.MultilabelFolder;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import weka.core.Instance;
import weka.core.Instances;

public class MainManager {

	private Connection connection;
	private ClassifierManager classifierManager;
	private FilterManager filterManager;
	private boolean readMailsFromFile = false;
	private String maildir;
	private String tasasFileName = "tasas";

	public MainManager() {
		filterManager = new FilterManager();
		classifierManager = new ClassifierManager(filterManager);
	}

	public MainManager(String url) {
		this();
		try {
			connect(url);
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	//TODO toda configuracion de punto de acceso, fuera de aqui
	public void setDataset(Instances dataSet) {
		classifierManager.setDataSet(dataSet);
	}

	public boolean isReadMailsFromFile() {
		return readMailsFromFile;
	}

	public void setReadMailsFromFile(String maildir) {
		System.out.println("Main Manager: set read mail from file " + readMailsFromFile);
		this.readMailsFromFile = true;
		if (maildir == null) {
			this.maildir = ConfigManager.MAILDIR.getAbsolutePath();
		} else {
			this.maildir = maildir;
		}
	}

	/** Connects to URL
	 * @throws Exception */
	//TODO no deberia estar aqui
	private void connect(String url) throws MessagingException {
		if (url != null) {
			try {
				connection = new Connection(url);
				// connection.login(url);
				connection.login();
				System.out.println("Connected!");
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("Unable to connect to the requested host!");
			}
			if (connection.getFolder() != null) {
				connection.show_mens();
			}
		} else {
			if (connection == null) {
				connection = new Connection();
			}
		}
	}

	
	public void mailsInFolder() {
		try {
			connection.showMessages("INBOX");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void openMail(int mail_id) {
		try {
			connection.readMail(mail_id);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void extractAttributes(String datasetFileName) {
		System.out.println("Mainmanager.extract attributes");
		filterManager.extractAttributeHeaders(getMessageReader());
		Instances instances = new Instances(filterManager.getDataset());
		DocumentReader reader = getMessageReader();
		for (Document doc: reader) {
			// TODO new incrementalWriteToFile in filterManager
			Instance inst = doc.toWekaInstance();
			instances.add(inst);
		}
		for (gnusmail.filters.Filter f : filterManager.filterList) {
			if (f instanceof MultilabelFolder) {
				((MultilabelFolder)f).writeToHierarchicalFile();
			}
		}
		filterManager.writeToFile(instances, datasetFileName);			
	}

	/**
	 * This method trains the model incrementally, using every available mail. 
	 * First, it creates an initial model, and then iterates over the mail set,
	 * using each message to update the model
	 */
	public void trainModel() {
		classifierManager.trainModel();
	}

	public void incrementallyTrainModel(String wekaClassifier) {
		//initiallyTrainModel();
		Logger.getLogger(MainManager.class.getName()).log(Level.INFO, "Incrementally Train Model");
		DocumentReader reader = getMessageReader();
		filterManager.extractAttributeHeaders(reader);
		List<Double> rates = classifierManager.incrementallyTrainModel(reader, wekaClassifier);
		printRateToFile(rates, tasasFileName);
		System.out.println("Fin");
	}

/*	public void trainModelFromFile() {
		System.out.println("TrainModel from file");
		initiallyTrainModel();
		classifierManager.incrementallyTrainModelFromDataSet();
	}
*/
	public void classifyDocument(Document doc) {
		try {
			classifierManager.classifyDocument(doc);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * This method uses the MimeMessage passed as parameter to update the 
	 * classifier. If no classifier is found, an initial model is created
	 * (using a very limited number of messages from those available in 
	 * the mailbox
	 * @param msg
	 */
	public void updateModelWithDocument(Document doc) {
		try {
			if (!ConfigManager.MODEL_FILE.exists()) {
				//initiallyTrainModel();
			}
			classifierManager.updateModelWithDocument(doc);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	//TODO: sacar todo lo relativo al punto de acceso
	public void close() {
		if ((connection != null) && (connection.isLoggedIn())) {
			try {
				connection.logout();
			} catch (MessagingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void evaluateWithMOA(String moaClassifier) {
		Logger.getLogger(MainManager.class.getName()).log(Level.INFO, "Evaluate with moa");
		DocumentReader reader = getMessageReader();
		filterManager.extractAttributeHeaders(reader);
		List<Double> rates = classifierManager.evaluatePrequential(reader, moaClassifier);
		printRateToFile(rates, tasasFileName);
	}

	private void printRatesByFolderToFile(Map<String, List<Double>> rates, String fileName) {
		try {
			// Create file
			FileWriter fstream = new FileWriter(fileName);
			BufferedWriter out = new BufferedWriter(fstream);
			String header = "";
			int minLength = Integer.MAX_VALUE;
			for (String folder : rates.keySet()) {
				header += folder + " ";
				if (rates.get(folder).size() < minLength) {
					minLength = rates.get(folder).size();
				}
			}

			header = header.substring(0, header.length()-1);
			out.write(header + "\n");
			for (int i = 0; i < minLength; i++) {
				String line = "";
				for (String folder : rates.keySet()) {
					line +=  rates.get(folder).get(i) + " ";
				}
				line = line.substring(0, line.length() - 1);
				out.write(line + "\n");
			}

			out.close();
		} catch (Exception e) {//Catch exception if any
			System.err.println("No se pueden imprimir tasas a " + fileName);
			e.printStackTrace();
		}
	}

	private static void printRateToFile(List<Double> rates, String fileName) {
		try {
			// Create file
			FileWriter fstream = new FileWriter(fileName);
			BufferedWriter out = new BufferedWriter(fstream);
			for (double d : rates) {
				out.write(d + "\n");
			}
			out.close();
		} catch (Exception e) {//Catch exception if any
			System.err.println("No se pueden imprimir tasas a " + fileName);
			e.printStackTrace();
		}
	}

	private DocumentReader getMessageReader() {
		DocumentReader reader = null;
		if (readMailsFromFile) {
			reader = MessageReaderFactory.createReader(this.maildir, 5000); //Para no limitar el n. de mensajes por carpeta
		} else {
			reader = MessageReaderFactory.createReader(connection, 2000);
		}
		return reader;
	}

	public void setTasasFileName(String tasasFileName) {
		this.tasasFileName = tasasFileName;
	}

}
