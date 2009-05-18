/*
 * Esta clase es la encargada 
 * de gestionar el objeto Properties
 * q contiene el estado actual de 
 * la configuración del sistema
 */

package gnusmail.core;

import java.util.*;
import java.io.*;

public class ConfigurationManager {
    public final static String CONF_FOLDER = System.getProperty("user.home") + "/.gnusmail/";
	public final static String CONF_FILE = "gnusmail.properties";
    public final static File MODEL_FILE = new File(CONF_FOLDER + "model.bin");
    public final static File DATASET_FILE = new File(CONF_FOLDER + "dataset.arff");
    private static Properties properties = loadProperties();
    
	private static Properties loadProperties() {
		Properties props = new Properties();
	    try {
		  InputStream f = ConfigurationManager.class.getClassLoader().getResourceAsStream("gnusmail/" + CONF_FILE);
	      System.out.println("Cargando configuracion de usuario...\n");
	      props.load(f);
          System.out.println(props.getProperty("genusmail.filters.WordFrequency"));
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
	
	/* Almacena en el parametro pasado todos 
	 * los filtros activos al momento de la llamada 
	 */
	public static String[] getFilters(){
		String filters = properties.getProperty("filters");
		String[] filterList = filters.split(" ");
		return filterList;
	}
		
	/* Lista por pantalla todas las propiedades y sus valores */
	public static void listarPropiedades(){
		/* Imprimimos los pares clave = valor */
	      properties.list(System.out);
	      System.out.println();
	}
	
	
	/* Añade un par propiedad-valor */
	public static void añadirPropiedad(String clave, String valor){
		//System.out.println("Cambiando Properties "+clave+" "+valor+"... ");
		properties.setProperty(clave, valor);
	}
	
	public static void grabarFichero(){
		try {		
			FileOutputStream f = new FileOutputStream(CONF_FOLDER + CONF_FILE);
			System.out.println("Grabando fichero de Properties...\n");
			properties.store(f,"#########################################\n" +
								"#   Fichero de configuracion\n" +
								"#########################################");
		} catch (IOException ioe) {
			System.out.println("Error al escribir en fichero!!");
		}
	}

	public static String getProperty(String key) {
		return properties.getProperty(key);
	}

	public static void setProperty(String key, String value) {
		properties.setProperty(key, value);
	}
}
