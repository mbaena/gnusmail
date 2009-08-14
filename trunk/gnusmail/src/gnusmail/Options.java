package gnusmail;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import gnusmail.core.ConfigurationManager;


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
	}
	
	public void run() {
		if (url != null ) {
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
            System.out.println("Procediendo a extraer frequent words");
            mainManager.extractFrequentWords();
        }

        if (this.mailClassification) {
            MimeMessage msg;
			try {
				msg = new MimeMessage(null, System.in); // std. input
	        	mainManager.classifyMail(msg);
			} catch (MessagingException e) {
				e.printStackTrace();
			} 
        }
        if (this.openMail > 0) {
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
        ConfigurationManager.añadirPropiedad("genusmail.filters." + clave, valor);
        ConfigurationManager.grabarFichero();
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

    

}
