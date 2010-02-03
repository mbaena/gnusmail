package gnusmail;

import gnusmail.core.ConfigManager;
import gnusmail.core.cnx.MessageInfo;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
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

/**
 * TODO
 * @author jmcarmona
 */
public class ClassifierManager {

	private Instances dataSet;
	private FilterManager filterManager;

	public Instances getDataSet() {
		return dataSet;
	}

	public void setDataSet(Instances dataSet) {
		this.dataSet = dataSet;
		this.dataSet.setClass(dataSet.attribute("Folder"));
	}

	public ClassifierManager(FilterManager filterManager) {
		this.filterManager = filterManager;
	}

	/**
	 * This method reads the messages in chronological order, and updates
	 * the underlying model with each message
	 * @return 
	 */
	public List<Double> incrementallyTrainModel(MessageReader reader, String wekaClassifier) {
		int seenMails = 0;
		int goodClassifications = 0;
		Map<String, Integer> messagesViewedByFolder = new TreeMap<String, Integer>();
		Map<String, Integer> correctClassificationsByFolder = new TreeMap<String, Integer>();
		List<Double> rates = new ArrayList<Double>();
		try {
			Classifier model = null;
			model = (Classifier) Class.forName(wekaClassifier).newInstance();
			try {
				model.buildClassifier(filterManager.getDataset()); // Add attributes information
			} catch (Exception ex) {
				Logger.getLogger(ClassifierManager.class.getName()).log(Level.SEVERE, null, ex);
			}
			UpdateableClassifier updateableModel = (UpdateableClassifier) model;
			for (Message msg : reader) { //TODO: esto en mainmanager,
				try {
					MessageInfo msgInfo = new MessageInfo(msg);
					String folder = msgInfo.getFolderAsString();
					Instance inst = filterManager.makeInstance(msgInfo);
					double predictedClass = model.classifyInstance(inst);
					double trueClass = inst.classValue();

					//Statistics update: total number
					if (predictedClass == trueClass) {
						goodClassifications++;
					}
					seenMails++;

					double rate = goodClassifications * 100.0 / seenMails;
					System.out.println("Correct answers rate: " + rate + "%");
					rates.add(rate);

					//Statistics update by folder
					if (!messagesViewedByFolder.containsKey(folder)) {
						messagesViewedByFolder.put(folder, 0);
					}
					if (!correctClassificationsByFolder.containsKey(folder)) {
						correctClassificationsByFolder.put(folder, 0);
					}
					int currentViewedMessages = messagesViewedByFolder.get(folder);
					messagesViewedByFolder.put(folder, currentViewedMessages + 1);
					if (predictedClass == trueClass) {
						int currentGuessed = correctClassificationsByFolder.get(folder);
						correctClassificationsByFolder.put(folder, currentGuessed + 1);
					}
					String strCorrectByFolder = "";
					for (String f : messagesViewedByFolder.keySet()) {
						int totalForThisFolder = messagesViewedByFolder.get(f);
						int guessedForThisFolder = correctClassificationsByFolder.get(f);
						strCorrectByFolder += "\t" + f + ": " + guessedForThisFolder + "/" + totalForThisFolder +
								"(" + (guessedForThisFolder * 100.0 / totalForThisFolder) + "%); \n";
					}
					System.out.println("Correct answers rate by folder: " + strCorrectByFolder);

					updateableModel.updateClassifier(inst);
				} catch (Exception ex) {
					Logger.getLogger(ClassifierManager.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
			FileOutputStream f = new FileOutputStream(ConfigManager.MODEL_FILE);
			ObjectOutputStream fis = new ObjectOutputStream(f);
			fis.writeObject(updateableModel);
			fis.close();
		} catch (InstantiationException ex) {
			Logger.getLogger(ClassifierManager.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IllegalAccessException ex) {
			Logger.getLogger(ClassifierManager.class.getName()).log(Level.SEVERE, null, ex);
		} catch (ClassNotFoundException ex) {
			Logger.getLogger(ClassifierManager.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IOException ex) {
			Logger.getLogger(ClassifierManager.class.getName()).log(Level.SEVERE, null, ex);
		}
		return rates;
	}

	/**
	 * Like incrementallyTrainModelFromMailServer, but reading from a CSV file
	 */
	public void incrementallyTrainModelFromDataSet() {
		Classifier model = new NaiveBayesUpdateable();
		System.out.println("Training model...");
		try {
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
		} catch (FileNotFoundException e) {
			System.out.println("File " + ConfigManager.MODEL_FILE.getAbsolutePath() + " not found");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public List<Double> evaluatePrecuential(MessageReader reader, String moaClassifier) {
		Map<String, Integer> messagesViewedByFolder = new TreeMap<String, Integer>();
		Map<String, Integer> correctClassificationsByFolder = new TreeMap<String, Integer>();
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
		} catch (Exception ex) {
			System.out.println("Can't print model. Is sizeofag.jar accessible?");
		}

		InstancesHeader instancesHeader = new InstancesHeader(filterManager.getDataset());
		learner.setModelContext(instancesHeader);

		List<Double> tasas = new ArrayList<Double>();

		Measurement[] listMs = evaluator.getPerformanceMeasurements();
		int posCorrect = 0;
		for (int i = 0; i < listMs.length; i++) {
			if (listMs[i].getName().contains("correct")) {
				posCorrect = i;
			}
		}

		int nmess = 0;
		for (Message msg : reader) {
			nmess++;
			if (nmess % 100 == 0) {
				System.out.println("Number of messages: " + nmess);
			}
			try {
				MessageInfo msgInfo = new MessageInfo(msg);
				System.out.println("Folder: " + msgInfo.getFolderAsString());
				String folder = msgInfo.getFolderAsString();
				if (!messagesViewedByFolder.containsKey(folder)) {
					messagesViewedByFolder.put(folder, 0);
				}
				if (!correctClassificationsByFolder.containsKey(folder)) {
					correctClassificationsByFolder.put(folder, 0);
				}
				int totalThisFolder = messagesViewedByFolder.get(folder);
				int correctThisFolder = messagesViewedByFolder.get(folder);
				Instance trainInst = filterManager.makeInstance(msgInfo);
				Instance testInst = (Instance) trainInst.copy();
				int trueClass = (int) trainInst.classValue();
				testInst.setClassMissing();
				double[] prediction = learner.getVotesForInstance(testInst);

				//Update statistics
				if (learner.correctlyClassifies(testInst)) {
					correctClassificationsByFolder.put(folder, correctThisFolder + 1);

				}
				messagesViewedByFolder.put(folder, totalThisFolder + 1);
				String strCorrectByFolder = "";
				for (String f : messagesViewedByFolder.keySet()) {
					int totalForThisFolder = messagesViewedByFolder.get(f);
					int guessedForThisFolder = correctClassificationsByFolder.get(f);
					strCorrectByFolder += "\t" + f + ": " + guessedForThisFolder + "/" + totalForThisFolder +
							"(" + (guessedForThisFolder * 100.0 / totalForThisFolder) + "%); \n";
				}
				System.out.println("Correct answers rate by folder: " + strCorrectByFolder);

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

				//msg.getFolder().close(false);
				//	}
			} catch (Exception ex) {
				Logger.getLogger(ClassifierManager.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		return tasas;
	}

	public void trainModel() {
		Classifier model = new NaiveBayesUpdateable();
		System.out.println("Training model...");
		try {
			model.buildClassifier(dataSet);
		} catch (Exception e) {
			return;
		}

		try {
			FileOutputStream f = new FileOutputStream(ConfigManager.MODEL_FILE);
			ObjectOutputStream fis = new ObjectOutputStream(f);
			fis.writeObject(model);
			fis.close();
		} catch (FileNotFoundException e) {
			System.out.println("File " + ConfigManager.MODEL_FILE.getAbsolutePath() + " not found");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void classifyMail(MimeMessage mimeMessage) throws Exception {
		MessageInfo msg = new MessageInfo(mimeMessage);
		Instance inst = filterManager.makeInstance(msg);
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
		for (int i = 0; i < res.length; i++) {
			System.out.println("\nDestination folder will be " + att.value(i) + " with probability: " + res[i]);
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
			System.out.println("Updating model with message, which folder is " +
					msg.getFolderAsString());
			Instance inst = filterManager.makeInstance(msg);
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
}

