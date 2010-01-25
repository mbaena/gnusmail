package gnusmail;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import gnusmail.core.ConfigManager;
import javax.mail.Folder;

public class Options {

	private MainManager mainManager;
	private String url;
	private int showAttributes;
	private boolean attributeExtraction;
	private boolean modelTraining;
	private boolean listFolders;
	private boolean listMailsInFolder;
	private boolean mailClassification;
	private int openMail;
	private boolean listMails;
	private int listMailsLimit;
	private boolean extractWords;
	private boolean updateModelWithMail;
	private boolean readMailsFromFileSystem;
	private static Options instance;
	private boolean moaTraining;
	private boolean studyHeaders;
	private String moaClassifier;

	public static Options getInstance() {
		if (instance == null) {
			instance = new Options();
		}
		return instance;
	}

	private Options() {
		this.url = null;
		this.showAttributes = -1;
		this.attributeExtraction = false;
		this.modelTraining = false;
		this.listFolders = false;
		this.listMails = false;
		this.listMailsLimit = 0;
		this.listMailsInFolder = false;
		this.mailClassification = false;
		this.openMail = -1;
		this.extractWords = false;
		this.updateModelWithMail = false;
		this.readMailsFromFileSystem = false;
		this.moaTraining = false;
		studyHeaders = false;
	}

	public void run() {
		System.out.println("Read Mails from file system: " + readMailsFromFileSystem);
		if (url != null && !Options.getInstance().isReadMailsFromFileSystem()) {
			System.out.println("Case 1");
			mainManager = new MainManager(url);
		} else {
			System.out.println("Case 2");
			mainManager = new MainManager();
		}
		if (this.readMailsFromFileSystem) {
			mainManager.setReadMailsFromFile(true);
		}
		if (this.showAttributes >= 0) {
			mainManager.showAttibutes(this.showAttributes);
		}
		if (this.attributeExtraction) {
			mainManager.extractAttributes();
		}
		if (this.modelTraining) {
			mainManager.trainModel();
		}

		if (this.moaTraining) {
			mainManager.evaluateWithMOA(moaClassifier);
		}

		if (this.listFolders) {
			mainManager.listFolders();
		}
		if (this.listMails) {
			mainManager.listMails(this.listMailsLimit);
		}
		if (this.listMailsInFolder) {
			mainManager.mailsInFolder();
		}
		if (this.extractWords) {
			mainManager.extractFrequentWords();
		}

		if (this.studyHeaders) {
			mainManager.studyHeaders();
		}


		if (this.updateModelWithMail) {
			MimeMessage msg;
			try {
				System.out.println("Ready to Read Message:");
				msg = new MimeMessage(null, System.in); // std. input
				mainManager.updateModelWithMail(msg);
			} catch (MessagingException e) {
				e.printStackTrace();
			}
		}

		if (this.mailClassification) {
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
				mainManager.classifyMail(msg);
			} catch (MessagingException e) {
				e.printStackTrace();
			}
		}
		if (this.openMail > 0) {
			//		mainManager = new MainManager("imaps://jmcarmona:@albireo.lcc.uma.es/INBOX.Drafts");
			mainManager.openMail(this.openMail);
		}
		mainManager.close();

	}

	public boolean isReadMailsFromFileSystem() {
		return readMailsFromFileSystem;
	}

	public void setReadMailsFromFileSystem(boolean readMailsFromFileSystem) {
		System.out.println("Options: Set Read Mail From FS: "
				+ readMailsFromFileSystem);
		this.readMailsFromFileSystem = readMailsFromFileSystem;
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

	public void setAttributeExtraction(boolean b) {
		this.attributeExtraction = b;
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

	public boolean isExtractWords() {
		return extractWords;
	}

	public void setExtractWords(boolean extractWords) {
		this.extractWords = extractWords;
	}

	public void setUpdateModelWithMail() {
		this.updateModelWithMail = true;
	}

	public void setMoaClassifier(String arg) {
		this.moaClassifier = arg;
	}
}
