package gnusmail.filters;

import gnusmail.Languages.Language;
import gnusmail.core.WordsStore;
import gnusmail.core.cnx.MessageInfo;

import gnusmail.languagefeatures.EmailTokenizer;
import gnusmail.languagefeatures.LanguageDetection;
import gnusmail.languagefeatures.Token;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.MessagingException;

/**
 *
 * @author jmcarmona
 */
public class WordFrequency extends Filter {
	Set<String> stringsEsteDocumento;
	static List<String> palabrasAAnalizar;


	@Override
	public String getName() {
		return "WordFrequency";
	}

	/**
	 * Esta funcion lee una lista de palabras que deben ser usadas como filtro en el cuerpo
	 * @return
	 */
	private static List<String> getWordsToAnalyze() {
		List<String> res = new ArrayList<String>();
		if (palabrasAAnalizar == null) {
			try {
				FileInputStream fstream = new FileInputStream(WordsStore.WORDS_FILE); //TODO Esto no lo deberia leer de fichero
				// Get the object of DataInputStream
				DataInputStream in = new DataInputStream(fstream);
				BufferedReader br = new BufferedReader(new InputStreamReader(in));
				String strLine;
				//Read File Line By Line
				while ((strLine = br.readLine()) != null) {
					// Print the content on the console
					if (strLine.length() > 2) //We only use words with 3 or more letters
					{
						res.add(strLine);
					}
				}
				//Close the input stream
				in.close();
			} catch (Exception e) {//Catch exception if any
				System.err.println("Error: " + e.getMessage());
			}
			palabrasAAnalizar = res;
		} else {
			res = palabrasAAnalizar;
		}
		return res;
	}

	@Override
	public List<String> getAssociatedHeaders() {
		return getWordsToAnalyze();
	}

	@Override
	public void initializeWithMessage(MessageInfo mess) {
		if (stringsEsteDocumento == null) {
			try {
				stringsEsteDocumento = new TreeSet<String>();
				//Extraemos las palabras del cuerpo y la cabecera
				String body = mess.getBody() + " " + mess.getSubject();
				Language lang = new LanguageDetection().detectLanguage(body);
				EmailTokenizer et = new EmailTokenizer(body);
				List<Token> tokens = et.tokenize();
				for (Token token : tokens) {
					token.setLanguage(lang);
					stringsEsteDocumento.add(token.getStemmedForm());
				}
			} catch (IOException ex) {
				Logger.getLogger(WordFrequency.class.getName()).log(Level.SEVERE, null, ex);
			} catch (MessagingException ex) {
				Logger.getLogger(WordFrequency.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}

	@Override
	public String getValueForHeader(String header) {
		if (stringsEsteDocumento.contains(header)) {
			return "True";
		} else {
			return "False";
		}
	}
}
