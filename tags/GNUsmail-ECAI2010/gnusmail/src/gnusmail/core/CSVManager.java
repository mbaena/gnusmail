package gnusmail.core;

import com.Ostermiller.util.CSVParser;
import com.Ostermiller.util.CSVPrinter;
import com.Ostermiller.util.LabeledCSVParser;
import gnusmail.core.cnx.MessageInfo;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.Vector;

public class CSVManager {

	CSVParser reader;
	CSVPrinter writer;
	LabeledCSVParser lcp;
	String[][] allValues;
	String[] headers;
	int numRegisters;
	public final static String FILE_CSV = System.getProperty("user.home") +
			"/.gnusmail/atributos.csv";

	public CSVManager() throws IOException {
		File file = new File(FILE_CSV);
		if (!file.exists()) {
			file.createNewFile();
		}
		reader = new CSVParser(new FileInputStream(FILE_CSV));
		lcp = new LabeledCSVParser(reader);
		allValues = lcp.getAllValues();
		headers = getHeaders();
		reader.close();
	}

	public String getValue(String header, MessageInfo msj) throws Exception {
		int index = searchPosition(header, headers);
		int j = 0;
		while ((allValues[j][0] != null) &&
				(msj.getMessageId().compareTo(allValues[j][0]) != 0) &&
				(j < allValues.length)) {
			j++;
		}
		if (j > allValues.length) {
			return "";
		}
		return allValues[j][index];
	}

	/**
	 * If the register was found, the row number is returned.
	 * In other case, -1 is returned
	*/
	  public int searchRegister(String[] register) throws IOException {
		if (allValues != null) {
			int searchPosition = searchPosition("genusmail.filters.MessageId", headers);
			if (searchPosition >= 0) {

				for (int i = 0; i < allValues.length; i++) {
					for (int j = 0; j < register.length; j++) {
						if (register[j].compareTo(allValues[i][searchPosition]) == 0) {
							return i;
						}
					}
				}
			}
		}
		return -1;
	}

	public void addCSVRegister(String[] newReg, Vector<String> activeFilters) throws IOException {
		String[] newRegisterAux;
		if (headers == null) {
			headers = new String[newReg.length];
			for (int i = 0; i < newReg.length; i++) {
				headers[i] = getFilterName(activeFilters.get(i));
			}
			reorderHeaders();
		}
		//Si el nuevo registro no está, lo añadimos a la matriz q representa el CSV
		if (searchRegister(newReg) < 0) {
			newRegisterAux = reorderAttributes(newReg, activeFilters);
			updateStructure(newRegisterAux);
		}
	}

	/** Puts Folder as first attribute*/
	private void reorderHeaders() {
		int j = 0;
		String[] res = new String[headers.length];

		res[0] = "Folder";
		for (int i = 1; i < res.length; i++) {
			if (headers[j].compareTo("Folder") == 0) {
				j++;
			}
			res[i] = headers[j];
			j++;
		}
		headers = res.clone();
	}

	/** Deletes substring "genusmail.filters."  */
	private String getFilterName(String longName) {
		StringTokenizer str = new StringTokenizer(longName, ".");
		String res = "";

		int max = str.countTokens();
		for (int j = 1; j <= max; j++) {
			res = str.nextToken(".");
		}
		return res;
	}

	public void writeToFile() throws IOException {
		System.out.println("Imprimiendo atributos a " + FILE_CSV);
		FileWriter fw = new FileWriter(FILE_CSV);
		writer = new CSVPrinter(fw, false, true);
		writer.setAlwaysQuote(true);
		if (allValues != null) {
			writer.writeln(headers);
			writer.writeln(allValues);
		}
	}


	private String[] reorderAttributes(String[] register, Vector<String> filters) throws IOException {
		String[] res;
		int j;
		if ((headers != null) && (register.length < headers.length)) {
			res = new String[headers.length];
		} else {
			res = new String[register.length];
		}
		for (int z = 0; z < res.length; z++) {
			res[z] = "?";
		}
		for (int i = 0; i < register.length; i++) {
			String atrib = filters.get(i);
			if (headers != null) {
				j = searchPosition(atrib, headers);
			} else {
				j = i;
			}
			if (j == -1) {
				addHeader(atrib);
				j = headers.length - 1;
			}

			res[j] = register[i];

		}
		return res;
	}

	private int searchPosition(String cadena, String[] array) {
		int pos = 0;
		while ((pos < array.length) && (pos >= 0) && (cadena.compareTo("gnusmail.filters." + array[pos]) != 0) && (cadena.compareTo(array[pos]) != 0)) { //Por las palabras sueltas
			pos++;
		}
		if (pos >= array.length) {
			return -1;
		}
		return pos;
	}

	private void addHeader(String nombre) {
		String[] res = new String[headers.length + 1];
		for (int i = 0; i < headers.length; i++) {
			res[i] = headers[i];
		}
		try {
			res[headers.length] = ((gnusmail.filters.Filter) Class.forName(nombre).newInstance()).getName();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			res[headers.length] = nombre;
		}
		this.headers = res.clone();
	}

	/** Updates the content of the CSV object*/
	private void updateStructure(String[] newItem) {
		int i, j;
		if (headers != null) {
			j = headers.length;
		} else {
			j = newItem.length;
		}

		if (allValues != null) {
			i = allValues.length + 1;
		} else {
			i = 1;
		}

		String[][] structure = new String[i][j];
		if (allValues != null) {
			for (i = 0; i < allValues.length; i++) {
				for (j = 0; j < allValues[0].length; j++) {
					structure[i][j] = allValues[i][j];
				}
				while (j < structure[0].length) {
					structure[i][j] = "?";
					j++;
				}//while
			}//for
		}
		i = structure.length - 1;
		for (j = 0; j < structure[0].length; j++) {
			structure[i][j] = newItem[j];

		}
		allValues = structure.clone();
	}

	private String[] getHeaders() throws IOException {
		return lcp.getLabels();
	}

	private void printHeaders() throws IOException {
		for (int i = 0; i < headers.length; i++) {
			System.out.print(headers[i]);
			if (i < headers.length - 1) {
				System.out.print(",");
			}
		}
		System.out.println();
	}

	@SuppressWarnings("unused")
	private void print() throws IOException {
		printHeaders();
		for (int i = 0; i < allValues.length; i++) {
			for (int j = 0; j < allValues[i].length; j++) {
				System.out.print(allValues[i][j]);
				if (j < allValues[i].length - 1) {
					System.out.print(",");
				}
			}
			System.out.println();
		}
		System.out.println();
	}
}
