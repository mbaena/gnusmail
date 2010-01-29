package gnusmail;

import gnusmail.core.CSVManager;
import gnusmail.core.ConfigManager;
import gnusmail.core.cnx.Connection;
import gnusmail.core.cnx.MessageInfo;
import gnusmail.filters.Filter;
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

	CSVManager csvmanager;

	public CSVManager getCsvmanager() {
		return csvmanager;
	}

	public void setCsvmanager(CSVManager csvmanager) {
		this.csvmanager = csvmanager;
	}

	public FilterManager() {
		try {
			csvmanager = new CSVManager();
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
					csvmanager.addCSVRegister(attributes, expandFilters(filters));
				}// for

			} catch (MessagingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				System.out.println("Writing Error");
				e.printStackTrace();
			}
		}// if

	}

	public void saveAttributes(Connection myConnection) {
		Folder[] folders;
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
		MessageReader reader = new MessageReaderFactory().createReader(connection, limit);
		//MessageReader reader = new MessageReader(connection, limit);
		String[] atributos;
		Iterator<Message> iterator = reader.iterator();
		int messagesRetrieved = 0;
		while (iterator.hasNext() && messagesRetrieved <= messagesToRetrieve) {
			messagesRetrieved++;
			System.out.println("Messages retrieved " + messagesRetrieved);
			MessageInfo msgInfo = new MessageInfo(iterator.next());
			try {
				atributos = getMessageAttributes(msgInfo);
				String[] filtros = ConfigManager.getFilters();
				csvmanager.addCSVRegister(atributos, expandFilters(filtros));
			} catch (IOException ex) {
				Logger.getLogger(FilterManager.class.getName()).log(Level.SEVERE, null, ex);
			} catch (MessagingException ex) {
				Logger.getLogger(FilterManager.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		try {
			csvmanager.writeToFile();
		} catch (IOException ex) {
			Logger.getLogger(FilterManager.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	//Aqui se tendrian que extraer (cabeceras) de atributos, no innstancias.
	public void extractAttributes(MessageReader reader) {
		String[] atributos;
		int num = 0;
		for (Message msg : reader) {
			MessageInfo msgInfo = new MessageInfo(msg);
			try {
				atributos = getMessageAttributes(msgInfo);
				String[] filters = ConfigManager.getFilters(); //esto, como parametro TODO
				/**
				 * 
				 */
				csvmanager.addCSVRegister(atributos, expandFilters(filters));
				num++;
				if (num % 100 == 0) System.out.println("_Num de mensajes " + num);
				if (num == 200) break;
			} catch (IOException ex) {
				Logger.getLogger(FilterManager.class.getName()).log(Level.SEVERE, null, ex);
			} catch (MessagingException ex) {
				Logger.getLogger(FilterManager.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		//al final de aqui , todos los filtros con sus cabeceras y valores
		//writeToFile();
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
			// gnusmail.filters.X -> X
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
	 *  Extracts Attributes for a given message.
	 *  A connection is opened and closed for each mail, as the number of
	 *  open folders is limited (and we cannot predict it, since we are
	 * iterating over the mails chronologically):w
	 *
	 */
	public static String[] getMessageAttributes(MessageInfo msj) throws MessagingException {
		if (msj.getFolder() != null) { //The folder can be null when the message is read from console
			if (!msj.getFolder().isOpen()) {
				msj.getFolder().open(Folder.READ_ONLY);
			}
		}
		Vector<String> res = new Vector<String>();

		String[] filtersName = ConfigManager.getFilters();
		Vector<Filter> filters = new Vector<Filter>();
		for (String sfiltro : filtersName) {
			try {
				filters.add((Filter) Class.forName(sfiltro).newInstance());
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}

		//TODO deben funcionar todos como wordstore, para que todos funcionen igual
		for (Filter filter : filters) {
			filter.initializeWithMessage(msj);
			List<String> associatedHeaders = filter.getAssociatedHeaders();
			for (String header : associatedHeaders) {
				String elemento = filter.getValueForHeader(header);
				res.addElement(elemento);
			}
		}

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
			e.printStackTrace();
		}
	}

	/**
	 * This method fills the word to check by the filter WordFrequency
	 * filtro WordFrecuency
	 * TODO move this method anywhere else!!
	 * @param filtros
	 * @return
	 */
	public static Vector<String> expandFilters(String[] filters) {
		Vector<String> res = new Stack<String>();
		for (String fName: filters) {
			try {
				Filter filter = (Filter) Class.forName(fName).newInstance();
				for (String header : filter.getAssociatedHeaders()) {
					res.add(header);
				}
			} catch (InstantiationException ex) {
				Logger.getLogger(FilterManager.class.getName()).log(Level.SEVERE, null, ex);
			} catch (IllegalAccessException ex) {
				Logger.getLogger(FilterManager.class.getName()).log(Level.SEVERE, null, ex);
			} catch (ClassNotFoundException ex) {
				Logger.getLogger(FilterManager.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		return res;
	}
}
