package gnusmail;

import gnusmail.core.ConfigManager;
import gnusmail.core.WordsStore;
import gnusmail.core.cnx.Connection;
import gnusmail.core.cnx.MessageInfo;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Header;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

public class MainManager {

	private Connection connection;
	private ClassifierManager classifierManager;
	private FilterManager filterManager;
	private String url;
	private boolean readMailsFromFile = false;

	public MainManager(String url) {
		if (!Options.getInstance().isReadMailsFromFileSystem()) {
			try {
				connect(url);
			} catch (MessagingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		classifierManager = new ClassifierManager();
		filterManager = new FilterManager();
	}

	public boolean isReadMailsFromFile() {
		return readMailsFromFile;
	}

	public void setReadMailsFromFile(boolean readMailsFromFile) {
		System.out.println("Main Manager: set read mail from file " + readMailsFromFile);
		this.readMailsFromFile = readMailsFromFile;
	}

	public MainManager() {
		this(null);
	}

	/** Connects to URL
	 * @throws Exception */
	private void connect(String url) throws MessagingException {
		this.url = url;
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

	public void extractAttributes() {
		System.out.println("Mainmanager.extract attributes");
		try {
			filterManager.extractAttributes(connection, 500);
			filterManager.writeToFile();
		} catch (Exception e1) {
			filterManager.writeToFile();
			e1.printStackTrace();
		}


	}

	public void extractFrequentWords() {
		System.out.println("Mainmanager.extract frequent words");
		WordsStore wordStore = new WordsStore();
		wordStore.readWordsList(connection);
	}

	/**
	 * This method constructs an initial model, using a restricted number of 
	 * examples (5). This model will be updated incrementally using the rest of 
	 * emails, in chronological order
	 */
	private void initiallyTrainModel() {
		System.out.println("TrainModel (init)");
		filterManager.saveAttributesForInitialModel(connection, 5, 1000);
		classifierManager.trainModel();

	}

	/**
	 * This method trains the model incrementally, using every available mail. 
	 * First, it creates an initial model, and then iterates over the mail set,
	 * using each message to update the model
	 */
	public void trainModel() {
		classifierManager.trainModel();
	}

	public void incrementallyTrainModel() {
		initiallyTrainModel();
		classifierManager.incrementallyTrainModel(null, 1000);
		System.out.println("Fin");
	}

	public void trainModelFromFile() {
		System.out.println("TrainModel from file");
		initiallyTrainModel();
		classifierManager.incrementallyTrainModelFromDataSet();
	}

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
				initiallyTrainModel();
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
		System.out.println("Evaluate with moa");
		filterManager.saveAttributesForInitialModel(connection, 100, 1);
		classifierManager.EvaluatePrecuential(connection, 1000, moaClassifier);
	}

	void studyHeaders() {
		Map<String, Map<String, Integer>> headers = new TreeMap<String, Map<String, Integer>>();
		MessageReader reader = new MessageReaderFactory().createReader(connection, 1000);
		List<Double> tasas = new ArrayList<Double>();
		System.out.println("Analizando correos...");
		int num = 0;
		for (Message msg : reader) {
			num++;
			try {
				Enumeration enumer = msg.getAllHeaders();
				while (enumer.hasMoreElements()) {
					Header h = (Header) enumer.nextElement();
					String name = h.getName();
					String value = h.getValue();
					if (!headers.containsKey(name)) {
						headers.put(name, new TreeMap<String, Integer>());
					}
					if (!headers.get(name).containsKey(value)) {
						headers.get(name).put(value, 0);
					}
					headers.get(name).put(value, 1 + headers.get(name).get(value));
				}
			} catch (MessagingException ex) {
				Logger.getLogger(MainManager.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		for (String name : headers.keySet()) {
			Map<String, Integer> mapa = headers.get(name);
			if (mapa.size() > 0) System.out.println(name + ":");
			for (String value : mapa.keySet()) {

				if (mapa.get(value) > 10) {
					System.out.println("\t" + value + " -> " + mapa.get(value));
				}
			}
		}
	}
}
