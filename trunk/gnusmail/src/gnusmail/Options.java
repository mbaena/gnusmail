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

	public Options() {
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
	}

	public void run() {
		if (url != null) {
			mainManager = new MainManager(url);
		} else {
			mainManager = new MainManager();
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

	public void setURL(String arg) {
		this.url = arg;
	}

	public void setShowAttributes(int mail_id) {
		this.showAttributes = mail_id;
	}

	public void setAttributeExtraction(boolean b) {
		this.attributeExtraction = b;
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
		ConfigManager.añadirPropiedad("genusmail.filters." + clave, valor);
		ConfigManager.grabarFichero();
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
}
