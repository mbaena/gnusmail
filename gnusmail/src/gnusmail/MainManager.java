package gnusmail;

import gnusmail.core.ConfigurationManager;
import gnusmail.core.WordStore;
import gnusmail.core.cnx.Connection;
import gnusmail.core.cnx.MessageInfo;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

public class MainManager {
	private Connection connection;
	private ClassifierManager classifierManager;
	private FilterManager filterManager;
	private String url;

	public MainManager(String url) {
		try {
			connect(url);
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		classifierManager = new ClassifierManager();
		filterManager = new FilterManager();
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
		try {
			connection.showAttributes(mail_id);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void listFolders() {
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
		for (Message msg : reader) {
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
			connection.leerCorreo(mail_id);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void extractAttributes() {
		try {
			filterManager.saveAttributesInOrder(connection, 100);
			filterManager.writeToFile();
		} catch (Exception e1) {
			filterManager.writeToFile();
			e1.printStackTrace();
		}


	}

	public void extractFrequentWords() {
		WordStore wordStore = new WordStore();
		wordStore.readWordsList(connection);
	}

	/**
	 * This method constructs an initial model, using a restricted number of 
	 * examples (5). This model will be updated incrementally using the rest of 
	 * emails, in chronological order
	 */
	private void initiallyTrainModel() {
		System.out.println("TrainModel");
		filterManager.saveAttributesForInitialModel(connection, 100, 5);
		classifierManager.trainModel();

	}

	/**
	 * This method trains the model incrementally, using every available mail. 
	 * First, it creates an initial model, and then iterates over the mail set,
	 * using each message to update the model
	 */
	public void trainModel() {
		initiallyTrainModel();
		classifierManager.incrementallyTrainModel(connection, 1000);
	}

	public void classifyMail(MimeMessage msg) {
		try {
			classifierManager.MessageInfo(msg);
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
			if (!ConfigurationManager.MODEL_FILE.exists()) {
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
}
