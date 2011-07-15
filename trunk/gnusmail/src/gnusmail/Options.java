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
import gnusmail.datasource.DataSource;
import gnusmail.datasource.MailDataSource;
import gnusmail.datasource.MessageReaderFactory;

public class Options {

	//private MainManager mainManager;
	private boolean modelTraining;
	private boolean listMailsInFolder;
	private int openMail;
	private String maildir;
	private static Options instance;
	private boolean moaTraining;
	private String moaClassifier;
	private String wekaClassifier = "NaiveBayesUpdateable";
	private boolean incrementallyTraining;
	private String ratesFileName;
	private boolean attributeExtraction;
	private String datasetFileName;
	
	private DataSource dataSource;
	private int datasetType;
	private String url;
	private MainManager mainManager;
	
	
	public static Options getInstance() {
		if (instance == null) {
			instance = new Options();
		}
		return instance;
	}
	
	public void setDatasetType(int datasetType) {
		this.datasetType = datasetType;
	}

	private Options() {
		this.url = null;
		this.modelTraining = false;
		this.listMailsInFolder = false;
		this.openMail = -1;
		this.moaTraining = false;
		this.incrementallyTraining = false;		
		this.mainManager = new MainManager();
	}

	public void run() {
		if (datasetType == DataSource.EMAIL_FROM_FILESYSTEM) {
			dataSource = new MailDataSource(MailDataSource.EMAIL_FROM_FILESYSTEM, url, 5000); //TODO
		} else {//if (datasetType == DataSource.EMAIL_FROM_IMAPSERVER) {
			dataSource = new MailDataSource(MailDataSource.EMAIL_FROM_IMAPSERVER, url, 5000); //TODO
		}
		mainManager.setDataSource(dataSource);
		
		if (this.attributeExtraction) {
			mainManager.extractAttributes(this.datasetFileName);
		}
		if (this.ratesFileName != null) {
			mainManager.setRatesFileName(ratesFileName);
		}
		if (this.modelTraining) {
			if (this.incrementallyTraining) {
				mainManager.incrementallyTrainModel(wekaClassifier);
			}
		}

		if (this.moaTraining) {
			mainManager.evaluateWithMOA(moaClassifier);
		}

		/*if (this.listMailsInFolder) {
			mainManager.mailsInFolder();
		}*/
		

		/*if (this.mailClassification) {
			MimeMessage msg;
			try {
				System.out.println("Ready to Read Message:");
				msg = new MimeMessage(null, System.in); // std. input
				System.out.println("Mirando el header folder " + msg.getHeader("Folder")[0]);
				Folder folder = msg.getFolder();
				if (folder != null) {
					System.out.println("Read folder is " + msg.getFolder().getName());
				} else {
					System.out.println("Era null el folder, Mensch");
				}
				mainManager.classifyDocument(msg);
			} catch (MessagingException e) {
				e.printStackTrace();
			}
		}*/
		/*if (this.openMail > 0) {
			mainManager.openMail(this.openMail);
		}*/
		/*mainManager.close();*/

	}

	public boolean isIncrementallyTraining() {
		return incrementallyTraining;
	}

	public void setIncrementallyTraining(boolean incrementallyTraining) {
		this.incrementallyTraining = incrementallyTraining;
	}

	
	public void setURL(String arg) {
		this.url = arg;
	}

	/*public void setShowAttributes(int mail_id) {
		this.showAttributes = mail_id;
	}*/

	public void setTasasFileName(String ratesFileName) {
		this.ratesFileName = ratesFileName;
	}

	public void setMoaTraining(boolean b) {
		this.moaTraining = b;
	}

	public void setModelTraining(boolean b) {
		this.modelTraining = b;
	}

	/*public void setListFolders(boolean b) {
		this.listFolders = b;
	}*/

	public void setListMailsInFolder(boolean b) {
		this.listMailsInFolder = b;
	}

	/*public void setMailClassification(boolean b) {
		this.mailClassification = b;
	}*/

	public void setProperties(String clave, String valor) {
		ConfigManager.addProperty("genusmail.filters." + clave, valor);
		ConfigManager.saveFile();
	}

	public void setOpenMail(int mail_id) {
		this.openMail = mail_id;
	}

	/*public void setListMails(boolean b, int limit) {
		this.listMailsLimit = limit;
		this.listMails = b;
	}*/

	/*public void setUpdateModelWithMail() {
		this.updateModelWithMail = true;
	}*/

	public String getWekaClassifier() {
		return wekaClassifier;
	}

	public void setWekaClassifier(String wekaClassifier) {
		this.wekaClassifier = wekaClassifier;
	}

	public void setMoaClassifier(String arg) {
		this.moaClassifier = arg;
	}

	public void setAttributeExtraction(boolean bool) {
		this.attributeExtraction = bool;
	}

	public void setDatasetFileName(String arg) {
		this.datasetFileName = arg;
	}
}
