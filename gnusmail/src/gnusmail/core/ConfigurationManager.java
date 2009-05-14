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
	final String FICHERO_CONFIGURACION = "/local/home/jmcarmona/clasificador.properties";
    Properties propiedades;
    
    /* Carga del fichero de propiedades */
	public ConfigurationManager() {	    
	    try {
	      FileInputStream f = new FileInputStream(FICHERO_CONFIGURACION);
	      System.out.println("Cargando configuracion de usuario...\n");

	      propiedades = new Properties();
	      propiedades.load(f);
          System.out.println(propiedades.getProperty("genusmail.filters.WordFrequency"));
	      f.close();    

	    } catch (Exception e) {
	      /* Manejo de excepciones */
	    	System.out.println("Fichero de Propiedades no valido!!");
	    }
	}
	
	/* Almacena en el parametro pasado todos 
	 * los filtros activos al momento de la llamada 
	 */
	public void getFiltrosActivos(Vector<String> filtros){
			Enumeration<?> e = propiedades.propertyNames();
			String nombre;
			
			while( e.hasMoreElements() ) {
		        nombre = (String) e.nextElement() ;
                if ( verValor(nombre) )
					filtros.addElement(nombre);
		     }		
	}
		
	/* Lista por pantalla todas las propiedades y sus valores */
	public void listarPropiedades(){
		/* Imprimimos los pares clave = valor */
	      propiedades.list(System.out);
	      System.out.println();
	}
	
	/* Devuelve el valor de una propiedad */
	public boolean verValor(String clave) {
		boolean valor = Boolean.valueOf(
		        propiedades.getProperty(clave)); 

		return valor;		
	}
	
	/* Añade un par propiedad-valor */
	public void añadirPropiedad(String clave, String valor){
		//System.out.println("Cambiando Properties "+clave+" "+valor+"... ");
		propiedades.setProperty(clave, valor);
	}
	
	public void grabarFichero(){
		try {		
			FileOutputStream f = new FileOutputStream(FICHERO_CONFIGURACION);
			System.out.println("Grabando fichero de Properties...\n");
			propiedades.store(f,"#########################################\n" +
								"#   Fichero de configuracion\n" +
								"#########################################");
		} catch (IOException ioe) {
			System.out.println("Error al escribir en fichero!!");
		}
	}
}
