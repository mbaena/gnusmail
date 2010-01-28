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
import java.io.InputStreamReader;
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
import moa.options.ClassOption;
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
	public void incrementallyTrainModel(MessageReader reader) {
		int seenMails = 0;
		int goodClassifications = 0;
		BufferedReader r = null;
		dataSet = null;
		List<Double> rates = new ArrayList<Double>();
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
			for (Message msg : reader) {
				try {
					MessageInfo msgInfo = new MessageInfo(msg);
					//if (!msg.getFolder().isOpen()) msg.getFolder().open(Folder.READ_ONLY);
					Instance inst = filterManager.makeInstance(msgInfo, dataSet);
					double predictedClass = model.classifyInstance(inst);
					double trueClass = inst.classValue();
					if (predictedClass == trueClass) {
						goodClassifications++;
					}
					seenMails++;
					double rate = goodClassifications * 100.0 / seenMails;
					System.out.println("Correct answers rate: " + rate + "%");
					rates.add(rate);

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
			printRateToFile(rates);
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
				String opciones[] = model.getOptions();
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

	public void EvaluatePrecuential(MessageReader reader, String moaClassifier) {
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

		// Evaluator Factory
		ClassOption evaluatorOption = new ClassOption("evaluator", 'e',
				"Evaluator to use.", ClassificationPerformanceEvaluator.class, "WindowClassificationPerformanceEvaluator");
		evaluatorOption.setValueViaCLIString(ConfigManager.getProperty("moaPrecuentialEvaluator"));
		ClassificationPerformanceEvaluator evaluator = (ClassificationPerformanceEvaluator) evaluatorOption.materializeObject(null, null);
		//evaluator.prepareForUse(); TODO (in moa)
		if (evaluator instanceof WindowClassificationPerformanceEvaluator) {
			((WindowClassificationPerformanceEvaluator) evaluator).setWindowWidth(Integer.parseInt(ConfigManager.getProperty("windowWidth")));
		}
		if (evaluator instanceof EWMAClassificationPerformanceEvaluator) {
			((EWMAClassificationPerformanceEvaluator) evaluator).setalpha(Double.parseDouble(ConfigManager.getProperty("alphaOption")));
		}

		// Learner Factory 
		if (moaClassifier == null) {
			moaClassifier = ConfigManager.getProperty("moaClassifier");
		}
		ClassOption learnerOption = new ClassOption("learner", 'l',
				"Classifier to train.", moa.classifiers.Classifier.class, "NaiveBayes");
		learnerOption.setValueViaCLIString(moaClassifier);
		moa.classifiers.Classifier learner = (moa.classifiers.Classifier) learnerOption.materializeObject(null, null);
		learner.prepareForUse();

		try {
			System.out.println("\n**MOA**\nLearner: " + learner);
			System.out.println("\nEvaluator: " + evaluator + "\n**MOA**\n");
			System.out.println("Press a key to continue..."); //We want to see the model
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			String aux = br.readLine();
		} catch (Exception ex) {
			System.out.println("Can't print model. Is sizeofag.jar accessible?");
		}


		InstancesHeader instancesHeader = new InstancesHeader(dataSet);
		System.out.println(instancesHeader);
		System.out.println("Pulse una tecla para continuar (lo de arriba son las instancias headers");

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		try {
			String aux = br.readLine();
		} catch (IOException ex) {
			Logger.getLogger(ClassifierManager.class.getName()).log(Level.SEVERE, null, ex);
		}

		learner.setModelContext(instancesHeader);

		List<Double> tasas = new ArrayList<Double>();

		Measurement[] listMs = evaluator.getPerformanceMeasurements();
		int posCorrect = 0;
		for (int i = 0; i < listMs.length; i++) {
			if (listMs[i].getName().contains("correct")) {
				posCorrect = i;
			}
		}

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
				evaluator.addClassificationAttempt(trueClass, prediction, testInst.weight());
				listMs = evaluator.getPerformanceMeasurements();
				tasas.add(listMs[posCorrect].getValue());

				for (Measurement measurement : listMs) {
					System.out.print(measurement.getName() + ": Tasa: " + measurement.getValue() + "\t");
				}

				System.out.println();
				learner.trainOnInstance(trainInst);
				System.out.println(learner);
				System.out.println("Press a key to continue..."); //We want to see the model
				//BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
				//String aux = br.readLine();
				printRateToFile(tasas);

			} catch (Exception ex) {
				Logger.getLogger(ClassifierManager.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		//prin(tasas);
	}

	public void trainModel() {
		Classifier model = new NaiveBayesUpdateable();
		System.out.println("Training model...");
		CSVLoader csvdata = new CSVLoader();
		try {
			csvdata.setSource(new File(CSVManager.FILE_CSV));
			dataSet =
					csvdata.getDataSet();
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
		dataSet =
				new Instances(r, 0); // Only the headers are necessary
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
		model =
				(Classifier) fie.readObject();

		System.out.println("\nClassifying...\n");
		double[] res = model.distributionForInstance(inst);
		Attribute att = dataSet.attribute("Folder");
		double biggest = 0;
		int biggest_index = 0;
		for (int i = 0; i <
				res.length; i++) {
			System.out.println("\nDestination folder will be " + att.value(i) +
					" with probability: " + res[i]);
			if (res[i] > biggest) {
				biggest_index = i;
				biggest =
						res[i];
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
			r =
					new BufferedReader(new FileReader(ConfigManager.DATASET_FILE));
			dataSet =
					new Instances(r, 0); // Only the headers are necessary
			dataSet.setClass(dataSet.attribute("Folder"));
			r.close();
			Instance inst = filterManager.makeInstance(msg, dataSet);
			Classifier model;

			FileInputStream fe = new FileInputStream(ConfigManager.MODEL_FILE);
			ObjectInputStream fie = new ObjectInputStream(fe);
			model =
					(Classifier) fie.readObject();
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

	private void printRateToFile(List<Double> tasas) {
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

