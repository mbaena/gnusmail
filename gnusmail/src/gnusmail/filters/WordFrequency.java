package gnusmail.filters;

import gnusmail.Languages.Language;
import gnusmail.core.WordsStore;
import gnusmail.core.cnx.MessageInfo;

import gnusmail.languagefeatures.EmailTokenizer;
import gnusmail.languagefeatures.LanguageDetection;
import gnusmail.languagefeatures.Token;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.MessagingException;

import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;

/**
 * 
 * @author jmcarmona
 */
public class WordFrequency extends Filter {

	private TreeMap<String, Integer> folderMap;
	private WordsStore ws;
	static List<String> palabrasAAnalizar;
	List<Attribute> attList;
	private Map<String, Attribute> attrMap;

	public WordFrequency() {
		folderMap = new TreeMap<String, Integer>();
		ws = new WordsStore();
		attList = new ArrayList<Attribute>();
	}

	@Override
	public String getName() {
		return "WordFrequency";
	}

	@Override
	public List<Attribute> getAttributes() {
		attrMap = new TreeMap<String, Attribute>();
		for (String folder : folderMap.keySet()) { // Esto a wordsfreqency, pero
			// en el futuro metodo get
			// atributes
			ws.getTermFrequencyManager().updateWordCountPorFolder(folder);
			ws.getTermFrequencyManager().setNumberOfDocumentsByFolder(folder,
					folderMap.get(folder));
		}
		for (String word : ws.getFrequentWords()) {
			FastVector values = new FastVector();
			values.addElement("True");
			values.addElement("False");
			Attribute att = new Attribute(word, values);
			attList.add(att);
			attrMap.put(word, att);
		}
		return attList;
	}

	@Override
	public void updateAttValues(MessageInfo msgInfo) {
		String folder = msgInfo.getFolderAsString();
		List<Token> tokens = tokenizeMessageInfo(msgInfo);
		ws.addTokenizedString(tokens, folder);
		try {
			folderMap.put(folder, folderMap.get(folder) + 1);
		} catch (NullPointerException e) {
			folderMap.put(folder, 1);
		}
	}

	@Override
	public void updateInstance(Instance inst, MessageInfo messageInfo) {
		Set<String> stringsEsteDocumento = new HashSet<String>();
		List<Token> tokens = tokenizeMessageInfo(messageInfo);
		for (Token token : tokens) {
			String stemmedForm = token.getStemmedForm();
			if (stemmedForm.length() > 2) {
				stringsEsteDocumento.add(stemmedForm);
			}
		}

		for (Attribute att : attList) {
			if (stringsEsteDocumento.contains(att.name())) { //TODO esto es lento...mejorar
				inst.setValue(att, "True");
			} else {
				inst.setValue(att, "False");
			}
		}
	}

	private static List<Token> tokenizeMessageInfo(MessageInfo messageInfo) {
		String body = null;
		try {
			// Extraemos las palabras del cuerpo y la cabecera
			String subject = messageInfo.getSubject();
			String b = messageInfo.getBody();
			//body = messageInfo.getBody() + " " + messageInfo.getSubject(); //Lo desechamos
			body = b + subject;
		} catch (IOException ex) {
			Logger.getLogger(WordFrequency.class.getName()).log(Level.SEVERE,
					null, ex);
		} catch (MessagingException ex) {
			Logger.getLogger(WordFrequency.class.getName()).log(Level.SEVERE,
					null, ex);
		}
		Language lang = new LanguageDetection().detectLanguage(body);
		EmailTokenizer et = new EmailTokenizer(body, lang);
		return et.tokenize();
	}
}
