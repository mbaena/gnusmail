package gnusmail;

import gnusmail.core.ConfigManager;
import gnusmail.core.WordsStore;
import gnusmail.core.cnx.Connection;
import gnusmail.core.cnx.MessageInfo;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;
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
	private boolean readMailsFromFile = false;
	private String maildir;
	private String tasasFileName = "tasas";

	public MainManager() {
		classifierManager = new ClassifierManager();
		filterManager = new FilterManager();
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

	public void extractAttributes() {
		System.out.println("Mainmanager.extract attributes");
		try {
			filterManager.extractAttributes(getMessageReader()); //TODO aqui se le pasaria el word wordstore
			filterManager.writeToFile();
		} catch (Exception e1) {
			filterManager.writeToFile();
			e1.printStackTrace();
		}

	}

	//Nuevo metodo
	/**
	 * Nuevo metodo
	 * Este metodo devuelve un conjunto de nombres de atributos con sus valores asociados,
	 * incluyendo la informacion de las palabras frecuentes
	 * @param ws
	 * @return
	 */
	public Map<String, List<String>> extractAttributeHeaders(WordsStore ws) {
		Map<String, List<String>> headerValues = new TreeMap<String, List<String>>();
		String[] atributos = null;
		Map<String, Integer> folderMap = new TreeMap<String, Integer>();
		for (Message msg : getMessageReader()) { //Esto a WordsFrequency
			MessageInfo msgInfo = new MessageInfo(msg);
			String folder = msgInfo.getFolderAsString();
			ws.addTokenizedString(msgInfo, folder);
			try {
				folderMap.put(folder, folderMap.get(folder) + 1);
				atributos = FilterManager.getMessageAttributes(msgInfo, false);
			} catch (MessagingException ex) {
				Logger.getLogger(MainManager.class.getName()).log(Level.SEVERE, null, ex);
			} catch (NullPointerException e) {
				folderMap.put(folder, 1);
			}
			String[] filters = ConfigManager.getFiltersWithoutWords(); //esto, como parametro TODO

			//Actualizamos el mapa
			Vector<String> headerNames = FilterManager.expandFilters(filters);
			for (int i = 0; i < atributos.length; i++) {
				String atributo = headerNames.get(i);
				String valor = atributos[i];
				if (!headerValues.containsKey(atributo)) {
					headerValues.put(atributo, new ArrayList<String>());
				}
				headerValues.get(atributo).add(valor);
			}
			//csvmanager.addCSVRegister(atributos, expandFilters(filters));
		}

		for (String folder : folderMap.keySet()) { //Esto a wordsfreqency, pero en el futuro metodo get atributes
			ws.getTermFrequencyManager().updateWordCountPorFolder(folder);
			ws.getTermFrequencyManager().setNumberOfDocumentsByFolder(folder, folderMap.get(folder));
		}

		for (String word : ws.getFrequentWords()) {
			headerValues.put(word, new ArrayList<String>());
			headerValues.get(word).add("True");
			headerValues.get(word).add("False");
		}
		return headerValues;
		//TODO actualizar los atributos de palabra
		//ws.writeToFile();

	}

	public void extractFrequentWords() {
		System.out.println("Mainmanager.extract frequent words");
		WordsStore wordStore = new WordsStore();
		wordStore.readWordsList(getMessageReader());
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
		MessageReader reader = getMessageReader();
		List<Double> rates = classifierManager.incrementallyTrainModel(reader);
		printRateToFile(rates, tasasFileName);
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
		Logger.getLogger(MainManager.class.getName()).log(Level.INFO, "Evaluate with moa");
		MessageReader reader = getMessageReader();
		filterManager.saveAttributesForInitialModel(connection, 100, 1);
		List<Double> rates = classifierManager.evaluatePrecuential(reader, moaClassifier);
		printRateToFile(rates, tasasFileName);
	}

	private static void printRateToFile(List<Double> rates, String fileName) {
		try {
			// Create file
			FileWriter fstream = new FileWriter(fileName);
			BufferedWriter out = new BufferedWriter(fstream);
			for (double d : rates) {
				out.write(d + "\n");
			}
//Close the output stream

			out.close();
		} catch (Exception e) {//Catch exception if any
			System.err.println("No se pueden imprimir tasas");
		}
	}

	private MessageReader getMessageReader() {
		MessageReader reader = null;
		if (readMailsFromFile) {
			reader = MessageReaderFactory.createReader(this.maildir);
		} else {
			reader = MessageReaderFactory.createReader(connection, 2);
		}
		return reader;
	}

	void studyHeaders() {
		Map<String, Map<String, Integer>> headers = new TreeMap<String, Map<String, Integer>>();
		MessageReader reader = getMessageReader();
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
			if (mapa.size() > 0) {
				System.out.println(name + ":");
			}
			for (String value : mapa.keySet()) {

				if (mapa.get(value) > 10) {
					System.out.println("\t" + value + " -> " + mapa.get(value));
				}
			}
		}
	}

	public void setTasasFileName(String tasasFileName) {
		this.tasasFileName = tasasFileName;
	}
}
