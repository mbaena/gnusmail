package gnusmail;

import gnusmail.core.CSVClass;
import gnusmail.core.ConfigurationManager;
import gnusmail.core.cnx.Connection;
import gnusmail.core.cnx.MessageInfo;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Message;
import javax.mail.internet.MimeMessage;
import weka.classifiers.Classifier;
import weka.classifiers.UpdateableClassifier;
import weka.classifiers.bayes.NaiveBayesUpdateable;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.CSVLoader;

/**
 * TODO
 * @author jmcarmona
 */
public class ClassifierManager {

	static Instances dataSet;
	static CSVClass csvmanager;
	private FilterManager filterManager;

	public ClassifierManager() {
		try {
			csvmanager = new CSVClass();
		} catch (IOException e) {
			e.printStackTrace();
		}
		filterManager = new FilterManager();
	}

	/**
	 * This method reads the messages in chronological order, and updates
	 * the underlying model with each message
	 */
	public void incrementallyTrainModel(Connection connection, int limit) {
		BufferedReader r = null;
		dataSet = null;
		try {
			//Hace falta tener un dataset.arff
			r = new BufferedReader(new FileReader(ConfigurationManager.DATASET_FILE));
			dataSet = new Instances(r, 0); // Only the headers are needed
			dataSet.setClass(dataSet.attribute("Folder"));
			r.close();
			Classifier model = null;
			try {
				FileInputStream fe = new FileInputStream(ConfigurationManager.MODEL_FILE);
				ObjectInputStream fie = new ObjectInputStream(fe);
				model = (Classifier) fie.readObject();
			} catch (FileNotFoundException e) {
			}
			NaiveBayesUpdateable updateableModel = (NaiveBayesUpdateable) model;
			MessageReader reader = new MessageReader(connection, limit);
			for (Message msg : reader) {
				try {
					MessageInfo msgInfo = new MessageInfo(msg);
					//if (!msg.getFolder().isOpen()) msg.getFolder().open(Folder.READ_ONLY);
					Instance inst = filterManager.makeInstance(msgInfo, dataSet);
					updateableModel.updateClassifier(inst);
				//msg.getFolder().close(false);
				} catch (Exception ex) {
					Logger.getLogger(ClassifierManager.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
			FileOutputStream f = new FileOutputStream(ConfigurationManager.MODEL_FILE);
			ObjectOutputStream fis = new ObjectOutputStream(f);
			fis.writeObject(updateableModel);
			fis.close();
		} catch (ClassNotFoundException ex) {
			Logger.getLogger(ClassifierManager.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IOException ex) {
			Logger.getLogger(ClassifierManager.class.getName()).log(Level.SEVERE, null, ex);
		} finally {
			try {
				r.close();
			} catch (IOException ex) {
				Logger.getLogger(ClassifierManager.class.getName()).log(Level.SEVERE, null, ex);
			}
		}

	}

	public void trainModel() {
		// IMAP fetch command with (BODY[HEADER.FIELDS (DATE)])
		Classifier model = new NaiveBayesUpdateable();
		//J48 j48 = new J48();
		//j48.setBinarySplits(true);
		//Classifier model = j48;
		//Classifier model = new weka.classifiers.functions.RBFNetwork();

		System.out.println("Training model...");


		CSVLoader csvdata = new CSVLoader();
		try {
			File f = new File(CSVClass.FILE_CSV);
			csvdata.setSource(new File(CSVClass.FILE_CSV));
			dataSet = csvdata.getDataSet();
			dataSet.setClass(dataSet.attribute("Folder"));
			model.buildClassifier(dataSet);

		} catch (Exception e) {
			return;
		}
		System.out.println(model);
		try {
			FileOutputStream f = new FileOutputStream(ConfigurationManager.MODEL_FILE);
			ObjectOutputStream fis = new ObjectOutputStream(f);
			fis.writeObject(model);
			fis.close();


			Writer w = new BufferedWriter(new FileWriter(ConfigurationManager.DATASET_FILE));
			Instances h = new Instances(dataSet);
			w.write(h.toString());
			w.write("\n");
			w.close();

		} catch (FileNotFoundException e) {
			System.out.println("File " +
					ConfigurationManager.MODEL_FILE.getAbsolutePath() +
					" not found");
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void MessageInfo(MimeMessage mimeMessage) throws Exception {
		MessageInfo msg = new MessageInfo(mimeMessage);
		Reader r = new BufferedReader(new FileReader(ConfigurationManager.DATASET_FILE));
		dataSet = new Instances(r, 0); // Sólo necesitamos las cabeceras de los atributos
		dataSet.setClass(dataSet.attribute("Folder"));
		r.close();

		Instance inst = filterManager.makeInstance(msg, dataSet);
		/*dataSet.add(inst);

		Writer w = new BufferedWriter(new FileWriter(FICH_DATASET));
		Instances h = new Instances(dataSet,0);
		w.write(h.toString());
		w.write("\n");
		w.close();*/

		Classifier model;
		//System.out.println(inst);

		if (!ConfigurationManager.MODEL_FILE.exists()) {
			trainModel();
		}

		FileInputStream fe = new FileInputStream(ConfigurationManager.MODEL_FILE);
		ObjectInputStream fie = new ObjectInputStream(fe);
		model = (Classifier) fie.readObject();

		System.out.println("\nClassyfying...\n");
		//distributionForInstance: da la predicción...
		double[] res = model.distributionForInstance(inst);
		Attribute att = dataSet.attribute("Folder");

		double biggest = 0;
		int biggest_index = 0;
		for (int i = 0; i < res.length; i++) {
			System.out.println("\nDestination folder will be " + att.value(i) +
					" with probability: " + res[i]);
			if (res[i] > biggest) {
				biggest_index = i;
				biggest = res[i];
			}
		}
		System.out.println("\nThe most probable folder is: " + att.value(biggest_index));
	//msg.crearCabecera("Genusmail", att.value(indice_mayor));
	//msg.imprimir(System.out);
	}

	void updateModelWithMessage(MimeMessage mimeMessage) {
		Reader r = null;
		try {
			MessageInfo msg = new MessageInfo(mimeMessage);
			r = new BufferedReader(new FileReader(ConfigurationManager.DATASET_FILE));
			dataSet = new Instances(r, 0); // Sólo necesitamos las cabeceras de los atributos
			dataSet.setClass(dataSet.attribute("Folder"));
			r.close();
			Instance inst = filterManager.makeInstance(msg, dataSet);
			Classifier model;
			//System.out.println(inst);

			FileInputStream fe = new FileInputStream(ConfigurationManager.MODEL_FILE);
			ObjectInputStream fie = new ObjectInputStream(fe);
			model = (Classifier) fie.readObject();
			UpdateableClassifier updateableModel = (UpdateableClassifier) model;
			updateableModel.updateClassifier(inst);

			FileOutputStream f = new FileOutputStream(ConfigurationManager.MODEL_FILE);
			ObjectOutputStream fis = new ObjectOutputStream(f);
			fis.writeObject(updateableModel);
			fis.close();
		} catch (Exception ex) {
			Logger.getLogger(ClassifierManager.class.getName()).log(Level.SEVERE, null, ex);
		} finally {
			try {
				r.close();
			} catch (IOException ex) {
				Logger.getLogger(ClassifierManager.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}
}
