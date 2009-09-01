package gnusmail;

import gnusmail.core.CSVClass;
import gnusmail.core.ConfigManager;
import gnusmail.core.cnx.Connection;
import gnusmail.core.cnx.MessageInfo;
import gnusmail.filters.Filter;
import gnusmail.filters.WordFrequency;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;

/**
 * TODO
 * @author jmcarmona
 */
public class FilterManager {

	CSVClass csvmanager;

	public FilterManager() {
		try {
			csvmanager = new CSVClass();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Saves the attributes of every message in the given folder in the CSV file
	 */
	public void saveFolderAttributes(Folder folder) {
		String[] attributes;
		if (folder != null) {
			try {
				if (!folder.isOpen()) {
					folder.open(javax.mail.Folder.READ_WRITE);
				}

				for (int i = 1; i <= folder.getMessageCount(); i++) {
					MessageInfo msj = new MessageInfo(folder.getMessage(i));
					attributes = getMessageAttributes(msj);
					String[] filters = ConfigManager.getFilters();
					csvmanager.addRegistro(attributes, expandFilters(filters));
				}// for

			} catch (MessagingException e) {
				System.out.println("Folder " + folder.getFullName() + " not found");
				e.printStackTrace();
			} catch (IOException e) {
				System.out.println("Writing Error");
				e.printStackTrace();
			}
		}// if

	}

	public void saveAttributes(Connection myConnection) {
		Folder[] folders;
		System.out.println("Extracting information from messages...");
		try {
			folders = myConnection.getFolders();

			for (int i = 0; i < folders.length; i++) {
				if (!folders[i].getFullName().contains(".Sent")) {
					saveFolderAttributes(folders[i]);
				} else {
					//
				}
			}

		} catch (MessagingException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * This method extracts attributes to be used by the initial model,
	 * when working in an incremental fashion
	 * @param connection
	 * @param limit
	 * @param messagesToRetrieve
	 */
	public void saveAttributesForInitialModel(Connection connection, int limit,
			int messagesToRetrieve) {
		MessageReader reader = new MessageReader(connection, limit);
		String[] atributos;
		Iterator<Message> iterator = reader.iterator();
		int messagesRetrieved = 0;
		while (iterator.hasNext() && messagesRetrieved <= messagesToRetrieve) {
			messagesRetrieved++;
			MessageInfo msgInfo = new MessageInfo(iterator.next());
			try {
				atributos = getMessageAttributes(msgInfo);
				String[] filtros = ConfigManager.getFilters();
				csvmanager.addRegistro(atributos, expandFilters(filtros));
			} catch (IOException ex) {
				Logger.getLogger(FilterManager.class.getName()).log(Level.SEVERE, null, ex);
			} catch (MessagingException ex) {
				Logger.getLogger(FilterManager.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}

	public void saveAttributesInOrder(Connection connection, int limit) {
		MessageReader reader = new MessageReader(connection, limit);
		String[] atributos;
		for (Message msg : reader) {
			MessageInfo msgInfo = new MessageInfo(msg);
			try {
				System.out.println("Attributes from: " + msgInfo.getMessageId() + " " + msgInfo.getDateAsStr());
				atributos = getMessageAttributes(msgInfo);
				// el Vector filtros contiene todos los filtros activos
				String[] filtros = ConfigManager.getFilters();
				/* Y los escribimos en el fichero CSV */
				csvmanager.addRegistro(atributos, expandFilters(filtros));
			} catch (IOException ex) {
				Logger.getLogger(FilterManager.class.getName()).log(Level.SEVERE, null, ex);
			} catch (MessagingException ex) {
				Logger.getLogger(FilterManager.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}

	/**
	 * This method creates a new instance with the extracted data, to be
	 * classified later
	 */
	public Instance makeInstance(MessageInfo msg, Instances dataSet)
			throws Exception {
		return makeInstance(msg, "INBOX", dataSet);
	}

	/**
	 * This method creates a new instance with the extracted data, to be
	 * classified later
	 */
	public Instance makeInstance(MessageInfo msg, String initialFolderName,
			Instances dataSet) throws Exception {
		String[] atribs = getMessageAttributes(msg);
		Instance inst = new Instance(atribs.length);
		List<String> filtros = ConfigManager.getClassificationAttributes();
		inst.setDataset(dataSet);

		int i = 0;
		for (String filtro : filtros) {
			// Este bucle toma el nombre del filtro, eliminando
			// la cadena "genusmail.filters." del pricipio
			StringTokenizer str = new StringTokenizer(filtro, ".");
			String p = "";
			int max = str.countTokens();
			for (int j = 1; j <= max; j++) {
				p = str.nextToken(".");
			}
			Attribute messageAtt = dataSet.attribute(p);
			if (messageAtt.indexOfValue(atribs[i]) == -1) {
				inst.setMissing(messageAtt);
			} else {
				inst.setValue(messageAtt, (double) messageAtt.indexOfValue(atribs[i]));
			}
			i++;
		}
		return inst;
	}

	/**
	 * TODO: move this method anywhere else!!
	 * Extrae los atributos del correo de la carpeta actual (según los filtros
	 * activos en el Properties)
	 * TODO: se abre y se cierra conexiones a las carpetas por cada correo. Esto se hace
	 * porque si el numero posible de conexiones es limitado, y si leemos los
	 * emails por orden de fecha, no podemos controlar cuantas conexiones tenemos
	 * abiertas.
	 */
	public static String[] getMessageAttributes(MessageInfo msj) throws MessagingException {
		if (msj.getFolder() != null) { //The folder can be null when the message is read from console
			if (!msj.getFolder().isOpen()) {
				msj.getFolder().open(Folder.READ_ONLY);
			}
		}
		System.out.println("   Analyzing " + msj.getMessageId());
		// res es un vector q contendrá el contenido de todos los atributos
		// activos
		Vector<String> res = new Vector<String>();

		// filtros contiene todos los filtros activos
		String[] filtersName = ConfigManager.getFilters();
		Vector<Filter> filters = new Vector<Filter>();
		for (String sfiltro : filtersName) {
			try {
				filters.add((Filter) Class.forName(sfiltro).newInstance());
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		for (Filter filter : filters) {
			if (filter instanceof WordFrequency) {
				List<String> words = WordFrequency.getWordsToAnalyze();
				try {
					for (String word : words) {
						WordFrequency wordsFilter = (WordFrequency) filter;
						wordsFilter.setWordToCheck(word);
						String elemento = wordsFilter.applyTo(msj);
						res.addElement(elemento);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				try {
					String element = filter.applyTo(msj);
					res.addElement(element);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		// Interesa devolver un array de String, no un Vector
		String[] sres = new String[res.size()];
		if (msj.getFolder() != null) {
			msj.getFolder().close(false);
		}
		return res.toArray(sres);


	}

	public void writeToFile() {
		try {
			csvmanager.writeToFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Esta clase rellena las palabras que se van a mirar en caso de tener el
	 * filtro WordFrecuency
	 * TODO move this method anywhere else!!
	 * @param filtros
	 * @return
	 */
	public static Vector<String> expandFilters(String[] filters) {
		Vector<String> res = new Stack<String>();
		for (String s : filters) {
			if (s.contains("WordFrequency")) {
				for (String word : WordFrequency.getWordsToAnalyze()) {
					res.add(word);
				}
			} else {
				res.add(s);
			}
		}
		return res;
	}
}
