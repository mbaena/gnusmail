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
/*
 * Esta clase es la encargada 
 * de gestionar el objeto Properties
 * q contiene el estado actual de 
 * la configuración del sistema
 */
package gnusmail.core;

import gnusmail.filters.FilterManager;
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
	//public final static File MAILDIR = new File(CONF_FOLDER + "maildirln/farmer-d");
	public final static File MAILDIR = new File("/local/home/users/jmcarmona/Chrome_Downloads/maildir/beck-s");
	private static Properties properties = loadProperties();


	private static Properties loadProperties() {
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
		String[] filterList = filters.split(" +");
		return filterList;
	}

	public static String[] getFiltersWithoutWords() {
		String filters = properties.getProperty("filters");
		String[] filterList = filters.split(" ");
		List<String> list = new ArrayList<String>();
		for (int i = 0; i < filterList.length; i++) {
			if (!filterList[i].contains("WordFrequency")) {
				list.add(filterList[i]);
			}
		}
		return list.toArray(new String[list.size()]);
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
