package gnusmail;

import gnusmail.core.ConfigManager;
import gnusmail.core.cnx.Connection;
import gnusmail.core.cnx.MessageInfo;

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

	public void showAttibutes(int mail_id) {
		System.out.println("MainManager.Show attributes");
		try {
			connection.showAttributes(mail_id);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void listFolders() {
		System.out.println("MainManager.List folders");
		try {
			connection.listFolders();
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void listMails(int limit) {
		Iterable<Message> reader;
		reader = new MessageReader(connection, limit);
		System.out.println("List mails");
		for (Message msg : reader) {
			System.out.println("New message in reader");
			MessageInfo msgInfo = new MessageInfo(msg);
			try {
				System.out.println(msgInfo.getReceivedDate() + " " + msgInfo.getSubject());
			} catch (MessagingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
		MessageReader reader = getMessageReader();
		for (Message msg: reader) {
			// TODO new incrementalWriteToFile in filterManager
			MessageInfo msgInfo = new MessageInfo(msg);
			Instance inst = filterManager.makeInstance(msgInfo);
			instances.add(inst);
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
		MessageReader reader = getMessageReader();
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
	public void classifyMail(MimeMessage msg) {
		try {
			classifierManager.classifyMail(msg);
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
	public void updateModelWithMail(MimeMessage msg) {
		try {
			if (!ConfigManager.MODEL_FILE.exists()) {
				//initiallyTrainModel();
			}
			classifierManager.updateModelWithMessage(msg);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

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
		MessageReader reader = getMessageReader();
		filterManager.extractAttributeHeaders(reader);
		List<Double> rates = classifierManager.evaluatePrecuential(reader, moaClassifier);
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

	private MessageReader getMessageReader() {
		MessageReader reader = null;
		if (readMailsFromFile) {
			reader = MessageReaderFactory.createReader(this.maildir, 5000); //Para no limitar el n. de mensajes por carpeta
		} else {
			reader = MessageReaderFactory.createReader(connection, 2);
		}
		return reader;
	}

	public void setTasasFileName(String tasasFileName) {
		this.tasasFileName = tasasFileName;
	}

}
