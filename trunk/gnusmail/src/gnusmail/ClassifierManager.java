package gnusmail;

import gnusmail.core.CSVManager;
import gnusmail.core.ConfigManager;
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
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Message;
import javax.mail.internet.MimeMessage;

import moa.core.InstancesHeader;
import moa.core.Measurement;
import moa.evaluation.ClassificationPerformanceEvaluator;
import moa.evaluation.EWMAClassificationPerformanceEvaluator;
import moa.evaluation.WindowClassificationPerformanceEvaluator;
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
	static CSVManager csvmanager;
	private FilterManager filterManager;

	public ClassifierManager() {
		try {
			csvmanager = new CSVManager();
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
			//It's necessary to have a dataset.arff
			r = new BufferedReader(new FileReader(ConfigManager.DATASET_FILE));
			dataSet = new Instances(r, 0); // Only the headers are needed
			dataSet.setClass(dataSet.attribute("Folder"));
			r.close();
			Classifier model = null;
			try {
				FileInputStream fe = new FileInputStream(ConfigManager.MODEL_FILE);
				ObjectInputStream fie = new ObjectInputStream(fe);
				model = (Classifier) fie.readObject();
			} catch (FileNotFoundException e) {
			}
			UpdateableClassifier updateableModel = (UpdateableClassifier) model;
			MessageReader reader = new MessageReaderFactory().createReader(connection, limit);
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
			FileOutputStream f = new FileOutputStream(ConfigManager.MODEL_FILE);
			ObjectOutputStream fis = new ObjectOutputStream(f);
			fis.writeObject(updateableModel);
			fis.close();
			Writer w = new BufferedWriter(new FileWriter(ConfigManager.DATASET_FILE));
			Instances h = new Instances(dataSet);
			w.write(h.toString());
			w.write("\n");
			w.close();
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

	/**
	 * Like incrementallyTrainModelFromMailServer, but reading from a CSV file
	 */
	public void incrementallyTrainModelFromDataSet() {
		Classifier model = new NaiveBayesUpdateable();
		System.out.println("Training model...");
		CSVLoader csvdata = new CSVLoader();
		try {
			csvdata.setSource(new File(CSVManager.FILE_CSV));
			dataSet = csvdata.getDataSet();
			dataSet.setClass(dataSet.attribute("Folder"));
			UpdateableClassifier updateableClassifier = (UpdateableClassifier) model;
			for (Enumeration instances = dataSet.enumerateInstances(); instances.hasMoreElements();) {
				Instance instance = (Instance) instances.nextElement();
				updateableClassifier.updateClassifier(instance);
			}
			model.buildClassifier(dataSet);
		} catch (Exception e) {
			return;
		}
		try {
			FileOutputStream f = new FileOutputStream(ConfigManager.MODEL_FILE);
			ObjectOutputStream fis = new ObjectOutputStream(f);
			fis.writeObject(model);
			fis.close();
			Writer w = new BufferedWriter(new FileWriter(ConfigManager.DATASET_FILE));
			Instances h = new Instances(dataSet);
			w.write(h.toString());
			w.write("\n");
			w.close();
		} catch (FileNotFoundException e) {
			System.out.println("File " +
					ConfigManager.MODEL_FILE.getAbsolutePath() +
					" not found");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void EvaluatePrecuential(Connection connection, int limit) {
		BufferedReader r = null;

		try {
			r = new BufferedReader(new FileReader(ConfigManager.DATASET_FILE));
			dataSet = new Instances(r, 0); // Only the headers are needed
			dataSet.setClass(dataSet.attribute("Folder"));
			r.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String moaClassifierClassName = ConfigManager.getProperty("moaClassifierClassName");
		String moaPrecuentialEvaluatorClassName = ConfigManager.getProperty("moaPrecuentialEvaluatorClassName");

		moa.classifiers.Classifier learner = null;
		ClassificationPerformanceEvaluator evaluator = null;
		try {
			learner = (moa.classifiers.Classifier) Class.forName(moaClassifierClassName).newInstance();
			evaluator = (ClassificationPerformanceEvaluator) Class.forName(moaPrecuentialEvaluatorClassName).newInstance();
			if (evaluator instanceof WindowClassificationPerformanceEvaluator) {
				((WindowClassificationPerformanceEvaluator) evaluator).setWindowWidth(Integer.parseInt(ConfigManager.getProperty("windowWidth")));
			}
			if (evaluator instanceof EWMAClassificationPerformanceEvaluator) {
				((EWMAClassificationPerformanceEvaluator) evaluator).setalpha(Double.parseDouble(ConfigManager.getProperty("alphaOption")));
			}
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

		InstancesHeader instancesHeader = new InstancesHeader(dataSet);
		learner.setModelContext(instancesHeader);

		MessageReader reader = new MessageReaderFactory().createReader(connection, limit);
		List<Double> tasas = new ArrayList<Double>();
		System.out.println("Evaluate prec. Empieza bucle");
		for (Message msg : reader) {
			try {
				MessageInfo msgInfo = new MessageInfo(msg);
				/*if (!msgInfo.getFolderAsString().toLowerCase().contains("inbox") &&
				!msgInfo.getFolderAsString().toLowerCase().contains("deleted") &&
				!msgInfo.getFolderAsString().toLowerCase().contains("sent")&&
				!msgInfo.getFolderAsString().toLowerCase().contains("junk")&&
				!msgInfo.getFolderAsString().toLowerCase().contains("attachment") ) {*/
				//if (!msg.getFolder().isOpen()) msg.getFolder().open(Folder.READ_ONLY);
				System.out.println("Folder: " + msgInfo.getFolderAsString());
				Instance trainInst = filterManager.makeInstance(msgInfo, dataSet);
				Instance testInst = (Instance) trainInst.copy();
				int trueClass = (int) trainInst.classValue();
				testInst.setClassMissing();
				double[] prediction = learner.getVotesForInstance(testInst);
				String predStr = "[";
				for (double d : prediction) {
					predStr += d + ",";
				}
				System.out.println(trueClass + " " + predStr + "]");
				evaluator.addClassificationAttempt(trueClass, prediction, testInst.weight());
				for (Measurement measurement : evaluator.getPerformanceMeasurements()) {
					System.out.print(measurement.getName() + ": Tasa: " + measurement.getValue() + "\t");
					if (measurement.getName().contains("correct")) {
						tasas.add(measurement.getValue());
					}
				}
				System.out.println();
				learner.trainOnInstance(trainInst);

				imprimirTasasErrorAFichero(tasas);


				//msg.getFolder().close(false);
				//	}
				} catch (Exception ex) {
				Logger.getLogger(ClassifierManager.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}

	public void trainModel() {
		Classifier model = new NaiveBayesUpdateable();
		System.out.println("Training model...");
		CSVLoader csvdata = new CSVLoader();
		try {
			csvdata.setSource(new File(CSVManager.FILE_CSV));
			dataSet = csvdata.getDataSet();
			dataSet.setClass(dataSet.attribute("Folder"));
			model.buildClassifier(dataSet);
		} catch (Exception e) {
			return;
		}
		//System.out.println(model);
		try {
			FileOutputStream f = new FileOutputStream(ConfigManager.MODEL_FILE);
			ObjectOutputStream fis = new ObjectOutputStream(f);
			fis.writeObject(model);
			fis.close();
			Writer w = new BufferedWriter(new FileWriter(ConfigManager.DATASET_FILE));
			Instances h = new Instances(dataSet);
			w.write(h.toString());
			w.write("\n");
			w.close();
		} catch (FileNotFoundException e) {
			System.out.println("File " +
					ConfigManager.MODEL_FILE.getAbsolutePath() +
					" not found");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void classifyMail(MimeMessage mimeMessage) throws Exception {
		MessageInfo msg = new MessageInfo(mimeMessage);
		Reader r = new BufferedReader(new FileReader(ConfigManager.DATASET_FILE));
		dataSet = new Instances(r, 0); // Only the headers are necessary
		dataSet.setClass(dataSet.attribute("Folder"));
		r.close();
		Instance inst = filterManager.makeInstance(msg, dataSet);
		Classifier model;
		System.out.println(inst);
		if (!ConfigManager.MODEL_FILE.exists()) {
			trainModel();
		}
		FileInputStream fe = new FileInputStream(ConfigManager.MODEL_FILE);
		ObjectInputStream fie = new ObjectInputStream(fe);
		model = (Classifier) fie.readObject();

		System.out.println("\nClassifying...\n");
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
		System.out.println("------------------------------");
		System.out.println("\nThe most probable folder is: " + att.value(biggest_index));
	}

	void updateModelWithMessage(MimeMessage mimeMessage) {
		Reader r = null;
		try {
			MessageInfo msg = new MessageInfo(mimeMessage);
			System.out.println("Updating model with message, which folder is " + msg.getFolderAsString());
			r = new BufferedReader(new FileReader(ConfigManager.DATASET_FILE));
			dataSet = new Instances(r, 0); // Only the headers are necessary
			dataSet.setClass(dataSet.attribute("Folder"));
			r.close();
			Instance inst = filterManager.makeInstance(msg, dataSet);
			Classifier model;
			FileInputStream fe = new FileInputStream(ConfigManager.MODEL_FILE);
			ObjectInputStream fie = new ObjectInputStream(fe);
			model = (Classifier) fie.readObject();
			UpdateableClassifier updateableModel = (UpdateableClassifier) model;
			updateableModel.updateClassifier(inst);
			FileOutputStream f = new FileOutputStream(ConfigManager.MODEL_FILE);
			ObjectOutputStream fis = new ObjectOutputStream(f);
			fis.writeObject(updateableModel);
			fis.close();
			System.out.println("Model updated");
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

	private void imprimirTasasErrorAFichero(List<Double> tasas) {
		try {
			// Create file
			FileWriter fstream = new FileWriter("tases");
			BufferedWriter out = new BufferedWriter(fstream);
			for (double d : tasas) {
				out.write(d + "\n");
			}
			//Close the output stream
			out.close();
		} catch (Exception e) {//Catch exception if any
			System.err.println("No se pueden imprimir tasas");
		}
	}
}

