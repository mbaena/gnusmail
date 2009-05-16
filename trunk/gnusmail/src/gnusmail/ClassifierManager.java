package gnusmail;

import gnusmail.core.ClaseCSV;
import gnusmail.core.ConfigurationManager;
import gnusmail.core.cnx.MensajeInfo;

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

import javax.mail.internet.MimeMessage;

import weka.classifiers.Classifier;
import weka.classifiers.bayes.BayesNet;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.CSVLoader;

public class ClassifierManager {
    static Instances dataSet;
    static ClaseCSV csvmanager;
    private FilterManager filterManager;
    //static Classifier model;

    public ClassifierManager() {
        try {
			csvmanager = new ClaseCSV();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} //Creamos el manejador de ficheros CSV
        filterManager = new FilterManager();
    }
    public void entrenarModelo() {
    	// TODO: on-line training
    	// IMAP fetch command with (BODY[HEADER.FIELDS (DATE)])
        Classifier model = new BayesNet();
        //J48 j48 = new J48();
        //j48.setBinarySplits(true);
        //Classifier model = j48;
        //Classifier model = new weka.classifiers.functions.RBFNetwork();

        System.out.println("Entrenando modelo...");

        CSVLoader csvdata = new CSVLoader();
        try {
            // MANOLO: ¿Por qué la llamada a escribirFichero aquí?
            // MIGUE: Por si hay información en memoria q aún no esté en disco,
            // aunq ya no tiene sentido xq hacemos llamadas con un solo parametro.
            //csvmanager.escribirFichero();

            File f = new File(ClaseCSV.FILE_CSV);
            System.out.println("La ruta es " + f.getAbsolutePath());
            csvdata.setSource(new File(ClaseCSV.FILE_CSV));
            dataSet = csvdata.getDataSet();
            dataSet.setClass(dataSet.attribute("Folder"));
            model.buildClassifier(dataSet);

        } catch (Exception e) {
            System.out.println("Imposible entrenar modelo");
            e.printStackTrace();
            return;
        }
        System.out.println(model);
        try {
            FileOutputStream f = new FileOutputStream(ConfigurationManager.fich_modelo);
            ObjectOutputStream fis = new ObjectOutputStream(f);
            fis.writeObject(model);
            fis.close();


            Writer w = new BufferedWriter(new FileWriter(ConfigurationManager.FICH_DATASET));
            Instances h = new Instances(dataSet);
            w.write(h.toString());
            w.write("\n");
            w.close();

        } catch (FileNotFoundException e) {
            System.out.println("Fichero " + ConfigurationManager.fich_modelo.getAbsolutePath() + " no encontrado");
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void clasificarCorreo(MimeMessage mimeMessage) throws Exception {
        MensajeInfo msg = new MensajeInfo(mimeMessage);   	
        Reader r = new BufferedReader(new FileReader(ConfigurationManager.FICH_DATASET));
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

        if (!ConfigurationManager.fich_modelo.exists()) {
            entrenarModelo();
        }

        FileInputStream fe = new FileInputStream(ConfigurationManager.fich_modelo);
        ObjectInputStream fie = new ObjectInputStream(fe);
        model = (Classifier) fie.readObject();

        System.out.println("\nClasificando...\n");
        //distributionForInstance: da la predicción...
        double[] res = model.distributionForInstance(inst);
        Attribute att = dataSet.attribute("Folder");

        double mayor = 0;
        int indice_mayor = 0;
        for (int i = 0; i < res.length; i++) {
            System.out.println("\nLa carpeta destino sería: " + att.value(i) +
                    " con probabilidad: " + res[i]);
            if (res[i] > mayor) {
                indice_mayor = i;
                mayor = res[i];
            }
        }
        System.out.println("\nLa carpeta con mayor probabilidad es: " + att.value(indice_mayor));
    //msg.crearCabecera("Genusmail", att.value(indice_mayor));
    //msg.imprimir(System.out);
    }

}
