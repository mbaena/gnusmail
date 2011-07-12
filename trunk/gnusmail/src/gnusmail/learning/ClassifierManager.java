/*
 * Copyright 2011 Universidad de Málaga.  All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 3 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 3 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 3 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Universidad de Málaga, 29071 Malaga, Spain or visit
 * www.uma.es if you need additional information or have any questions.
 * 
 */
package gnusmail.learning;

import gnusmail.core.ConfigManager;
import gnusmail.datasource.DocumentReader;
import gnusmail.datasource.mailconnection.Document;
import gnusmail.filters.FilterManager;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import moa.core.InstancesHeader;
import moa.evaluation.ClassificationPerformanceEvaluator;
import moa.evaluation.EWMAClassificationPerformanceEvaluator;
import moa.evaluation.WindowClassificationPerformanceEvaluator;
import moa.options.AbstractOptionHandler;
import moa.options.ClassOption;
import moa.options.FloatOption;
import moa.options.IntOption;
import weka.classifiers.Classifier;
import weka.classifiers.UpdateableClassifier;
import weka.classifiers.bayes.NaiveBayesUpdateable;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;

/**
 * TODO
 * 
 * @author jmcarmona, mbaena
 */
public class ClassifierManager {

	private Instances dataSet;
	private FilterManager filterManager;
	
	private ClassificationPerformanceEvaluator getEvaluator() {
		// Evaluator Factory
		ClassOption evaluatorOption = new ClassOption("evaluator", 'e',
				"Evaluator to use.", ClassificationPerformanceEvaluator.class,
				"WindowClassificationPerformanceEvaluator");
		evaluatorOption.setValueViaCLIString(ConfigManager
				.getProperty("moaPrecuentialEvaluator"));
		ClassificationPerformanceEvaluator evaluator = (ClassificationPerformanceEvaluator) evaluatorOption
				.materializeObject(null, null);
		if (evaluator instanceof AbstractOptionHandler) {
			((AbstractOptionHandler) evaluator).prepareForUse();
		}
		if (evaluator instanceof WindowClassificationPerformanceEvaluator) {
			((WindowClassificationPerformanceEvaluator) evaluator).widthOption = 
				new IntOption("width",
			            'w', "Size of Window", Integer.parseInt(ConfigManager
								.getProperty("windowWidth")));
		}
		if (evaluator instanceof EWMAClassificationPerformanceEvaluator) {
			((EWMAClassificationPerformanceEvaluator) evaluator).alphaOption =
				new FloatOption("alpha",
			            'a', "Fading factor or exponential smoothing factor", Double.parseDouble(ConfigManager
								.getProperty("alphaOption")));
		}
		return evaluator;
	}
	
	private moa.classifiers.Classifier getMoaLearner(String moaClassifier) {
		if (moaClassifier == null) {
			moaClassifier = ConfigManager.getProperty("moaClassifier");
		}
		ClassOption learnerOption = new ClassOption("learner", 'l',
				"Classifier to train.", moa.classifiers.Classifier.class,
				"NaiveBayes");
		learnerOption.setValueViaCLIString(moaClassifier);
		moa.classifiers.Classifier learner = (moa.classifiers.Classifier) learnerOption
				.materializeObject(null, null);
		learner.prepareForUse();
		return learner;
	}

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
	 * This method reads the messages in chronological order, and updates the
	 * underlying model with each message
	 * 
	 * @return
	 */
	public List<Double> incrementallyTrainModel(DocumentReader reader,
			String wekaClassifier) {
		List<Double> successes = new ArrayList<Double>();
		try {
			Classifier model = null;
			model = (Classifier) Class.forName(wekaClassifier).newInstance();
			try {
				model.buildClassifier(filterManager.getDataset()); 
			} catch (Exception ex) {
				Logger.getLogger(ClassifierManager.class.getName()).log(
						Level.SEVERE, null, ex);
			}
			UpdateableClassifier updateableModel = (UpdateableClassifier) model;
			for (Document doc : reader) {
				double predictedClass = 0.0;
				try {
					Instance inst = doc.toWekaInstance();
					predictedClass = model.classifyInstance(inst);
					double trueClass = inst.classValue();
					successes.add((predictedClass == trueClass) ? 1.0 : 0.0);
					updateableModel.updateClassifier(inst);
				} catch (Exception ex) {
					Logger.getLogger(ClassifierManager.class.getName()).log(
							Level.SEVERE, null, ex);
				}
			}
			FileOutputStream f = new FileOutputStream(ConfigManager.MODEL_FILE);
			ObjectOutputStream fis = new ObjectOutputStream(f);
			fis.writeObject(updateableModel);
			fis.close();
		} catch (Exception ex) {
			Logger.getLogger(ClassifierManager.class.getName()).log(
					Level.SEVERE, null, ex);
		}
		return successes;
	}

	
	/**
	 * This method is used to evaluate a MOA classifier over a data stream
	 * @param reader
	 * @param moaClassifier
	 * @return
	 */
	public List<Double> evaluatePrequential(DocumentReader reader,
			String moaClassifier) {
		ClassificationPerformanceEvaluator evaluator = getEvaluator();		
		moa.classifiers.Classifier learner = getMoaLearner(moaClassifier);	
		InstancesHeader instancesHeader = new InstancesHeader(filterManager
				.getDataset());
		learner.setModelContext(instancesHeader);

		List<Double> successes = new ArrayList<Double>();
	
		for (Document doc: reader) {
			Instance trainInst = doc.toWekaInstance();
			Instance testInst = (Instance) trainInst.copy();
			double[] prediction = learner.getVotesForInstance(testInst);
			evaluator.addResult(testInst, prediction);
			learner.trainOnInstance(trainInst);
			try {
			} catch (Exception ex) {
				Logger.getLogger(ClassifierManager.class.getName()).log(
						Level.SEVERE, null, ex);
			}
		}
		return successes;
	}

	/**
	 * Batch training
	 */
	public void trainModel() {
		Classifier model = new NaiveBayesUpdateable();
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
			System.out
					.println("File "
							+ ConfigManager.MODEL_FILE.getAbsolutePath()
							+ " not found");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void classifyDocument(Document document) throws Exception {
		Instance inst = document.toWekaInstance();
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
			System.out.println("\nDestination folder will be " + att.value(i)
					+ " with probability: " + res[i]);
			if (res[i] > biggest) {
				biggest_index = i;
				biggest = res[i];
			}

		}
		System.out.println("------------------------------");
		System.out.println("\nThe most probable folder is: "
				+ att.value(biggest_index));
	}

	public void updateModelWithDocument(Document document) {
		Reader r = null;
		try {
			Instance inst = document.toWekaInstance();
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
			Logger.getLogger(ClassifierManager.class.getName()).log(
					Level.SEVERE, null, ex);
		} finally {
			try {
				r.close();
			} catch (IOException ex) {
				Logger.getLogger(ClassifierManager.class.getName()).log(
						Level.SEVERE, null, ex);
			}
		}

	}
}
