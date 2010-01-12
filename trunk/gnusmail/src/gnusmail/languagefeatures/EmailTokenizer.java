package gnusmail.languagefeatures;

import gnusmail.Languages.Language;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * This class tokenizes an email, taking into account different separator symbols
 * @author jmcarmona
 */
public class EmailTokenizer {

	String body;
	List<Token> tokens;
	Language lang = null;
	public final static String tokenPattern = " \t\n\r\f.,;:?¿!¡\"()'=[]{}/<>-*0123456789ªº%&*@_|’\\#";

	public EmailTokenizer(String emailBody) {
		this.body = emailBody;
		tokens = new LinkedList<Token>();
	}

	public EmailTokenizer(String emailBody, Language lang) {
		this.body = emailBody;
		tokens = new LinkedList<Token>();
		this.lang = lang;
	}

	public List<Token> tokenize() {
		int limit = 1000;
		if (body != null) {
			StringTokenizer st = new StringTokenizer(body, tokenPattern);
			while (st.hasMoreElements() && tokens.size() < limit) {
				Token token = new Token(st.nextToken());
				if (lang != null) {
					token.setLanguage(lang);
				}
				if (token.originalForm.length() > 0) {
					tokens.add(token);
				}
			}
		}
		return tokens;
	}
}
