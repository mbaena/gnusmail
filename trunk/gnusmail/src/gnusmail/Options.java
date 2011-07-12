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

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

public class Options {

	private MainManager mainManager;
	private String url;
	private int showAttributes;
	private boolean modelTraining;
	private boolean listFolders;
	private boolean listMailsInFolder;
	private boolean mailClassification;
	private int openMail;
	private boolean listMails;
	private int listMailsLimit;
	private boolean updateModelWithMail;
	private boolean readMailsFromFileSystem;
	private String maildir;
	private static Options instance;
	private boolean moaTraining;
	private boolean studyHeaders;
	private String moaClassifier;
	private String wekaClassifier = "NaiveBayesUpdateable";
	private boolean incrementallyTraining;
	private String tasasFileName;
	private boolean attributeExtraction;
	private String datasetFileName;

	public static Options getInstance() {
		if (instance == null) {
			instance = new Options();
		}
		return instance;
	}

	private Options() {
		this.url = null;
		this.showAttributes = -1;
		this.modelTraining = false;
		this.listFolders = false;
		this.listMails = false;
		this.listMailsLimit = 0;
		this.listMailsInFolder = false;
		this.mailClassification = false;
		this.openMail = -1;
		this.readMailsFromFileSystem = false;
		this.updateModelWithMail = false;
		this.moaTraining = false;
		this.incrementallyTraining = false;
		studyHeaders = false;
	}

	public void run() {
		System.out.println("Read Mails from file system: " + readMailsFromFileSystem);
		if (!readMailsFromFileSystem) {
			mainManager = new MainManager(url);
		} else {
			mainManager = new MainManager();
		}
		if (this.readMailsFromFileSystem) {
			mainManager.setReadMailsFromFile(this.maildir);
		}
		/*if (this.showAttributes >= 0) {
			mainManager.showAttibutes(this.showAttributes);
		}*/
		if (this.attributeExtraction) {
			mainManager.extractAttributes(this.datasetFileName);
		}
		if (this.tasasFileName != null) {
			mainManager.setTasasFileName(tasasFileName);
		}
		if (this.modelTraining) {
			//Instances dataSet = mainManager.extractAttributeHeaders(new WordsStore());
			//mainManager.setDataset(dataSet);
			if (this.incrementallyTraining) {
				mainManager.incrementallyTrainModel(wekaClassifier);
			} else {
				//	mainManager.trainModel();
			}
		}

		if (this.moaTraining) {
			//Instances dataSet = mainManager.extractAttributeHeaders(new WordsStore());
			//mainManager.setDataset(dataSet);
			mainManager.evaluateWithMOA(moaClassifier);
		}

		/*if (this.listFolders) {
			mainManager.listFolders();
		}*/
		/*if (this.listMails) {
			mainManager.listMails(this.listMailsLimit);
		}*/
		if (this.listMailsInFolder) {
			mainManager.mailsInFolder();
		}

		/*if (this.studyHeaders) {
			//mainManager.studyHeaders();
		}*/


		/*if (this.updateModelWithMail) {
			MimeMessage msg;
			try {
				System.out.println("Ready to Read Message:");
				msg = new MimeMessage(null, System.in); // std. input
				mainManager.updateModelWithDocument(msg);
			} catch (MessagingException e) {
				e.printStackTrace();
			}
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
		if (this.openMail > 0) {
			//		mainManager = new MainManager("imaps://jmcarmona:@albireo.lcc.uma.es/INBOX.Drafts");
			mainManager.openMail(this.openMail);
		}
		mainManager.close();

	}

	public boolean isIncrementallyTraining() {
		return incrementallyTraining;
	}

	public void setIncrementallyTraining(boolean incrementallyTraining) {
		this.incrementallyTraining = incrementallyTraining;
	}

	public void setReadMailsFromFileSystem(String maildir) {
		System.out.println("Options: Set Read Mail From FS: " + readMailsFromFileSystem);
		this.readMailsFromFileSystem = true;
		this.maildir = maildir;
	}

	public void setStudyHeaders(boolean studyHeaders) {
		this.studyHeaders = studyHeaders;
	}

	public void setURL(String arg) {
		this.url = arg;
	}

	public void setShowAttributes(int mail_id) {
		this.showAttributes = mail_id;
	}

	public void setTasasFileName(String tasasFileName) {
		this.tasasFileName = tasasFileName;
	}

	public void setMoaTraining(boolean b) {
		this.moaTraining = b;
	}

	public void setModelTraining(boolean b) {
		this.modelTraining = b;
	}

	public void setListFolders(boolean b) {
		this.listFolders = b;
	}

	public void setListMailsInFolder(boolean b) {
		this.listMailsInFolder = b;
	}

	public void setMailClassification(boolean b) {
		this.mailClassification = b;
	}

	public void setProperties(String clave, String valor) {
		ConfigManager.addProperty("genusmail.filters." + clave, valor);
		ConfigManager.saveFile();
	}

	public void setOpenMail(int mail_id) {
		this.openMail = mail_id;
	}

	public void setListMails(boolean b, int limit) {
		this.listMailsLimit = limit;
		this.listMails = b;
	}

	public void setUpdateModelWithMail() {
		this.updateModelWithMail = true;
	}

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
