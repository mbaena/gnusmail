/*
 * Esta clase es la encargada 
 * de gestionar el objeto Properties
 * q contiene el estado actual de 
 * la configuración del sistema
 */
package gnusmail.core;

import gnusmail.filters.WordFrequency;
import java.util.*;
import java.io.*;

class ConsoleEraser extends Thread {

	private boolean running = true;

	@Override
	public void run() {
		while (running) {
			System.out.print("\b ");
		}
	}

	public synchronized void halt() {
		running = false;
	}
}

public class ConfigManager {

	public final static String CONF_FOLDER = System.getProperty("user.home") + "/.gnusmail/";
	public final static String CONF_FILE = "gnusmail.properties";
	public final static File MODEL_FILE = new File(CONF_FOLDER + "model.bin");
	public final static File DATASET_FILE = new File(CONF_FOLDER + "dataset.arff");
	public final static File MAILDIR = new File(CONF_FOLDER + "maildirln");
	private static Properties properties = loadProperties();
	private static List<String> classificationAttributes;

	public static List<String> getClassificationAttributes() {
		if (classificationAttributes == null) {
			String[] filters = getFilters();
			classificationAttributes = new ArrayList<String>();
			for (String filter : filters) {
				if (!filter.contains("WordFrequency")) {
					classificationAttributes.add(filter);
				} else {
					for (String word : WordFrequency.getWordsToAnalyze()) {
						classificationAttributes.add(word);
					}
				}
			}
		}
		return classificationAttributes;

	}

	private static Properties loadProperties() {
		/*ConsoleEraser consoleEraser = new ConsoleEraser();
		System.out.print("Please enter password for given account: ");
		BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
		consoleEraser.start();
		String pass = "null";
		try {
		pass = stdin.readLine();
		} catch (IOException ex) {
		Logger.getLogger(ConfigurationManager.class.getName()).log(Level.SEVERE, null, ex);
		}
		consoleEraser.halt();
		System.out.print("\b");
		 */
		Properties props = new Properties();
		try {
			InputStream f = ConfigManager.class.getClassLoader().
					getResourceAsStream("gnusmail/" + CONF_FILE);
			props.load(f);
			f.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		String fileName = CONF_FOLDER + CONF_FILE;
		try {
			FileInputStream f = new FileInputStream(fileName);
			props.load(f);
			f.close();
		} catch (FileNotFoundException e) {
			File f = new File(fileName);
			try {
				File folder = new File(CONF_FOLDER);
				if (!folder.exists()) {
					folder.mkdir();
				}
				f.createNewFile();
				FileOutputStream fileOutputStream = new FileOutputStream(f);
				props.store(fileOutputStream, "");
				fileOutputStream.close();
			} catch (IOException e1) {

				e1.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return props;
	}

	/*
	 * returns an array with the current active filters
	 */
	public static String[] getFilters() {
		String filters = properties.getProperty("filters");
		String[] filterList = filters.split(" ");
		return filterList;
	}

	public static void listProperties() {
		properties.list(System.out);
		System.out.println();
	}

	public static void addProperty(String clave, String valor) {
		properties.setProperty(clave, valor);
	}

	public static void saveFile() {
		try {
			FileOutputStream f = new FileOutputStream(CONF_FOLDER + CONF_FILE);
			properties.store(f, "#########################################\n" +
					"#   Configuration file\n" +
					"#########################################");
		} catch (IOException ioe) {
			System.out.println("Error when writing into config file!!");
		}
	}

	public static String getProperty(String key) {
		return properties.getProperty(key);
	}

	public static void setProperty(String key, String value) {
		properties.setProperty(key, value);
	}
}
