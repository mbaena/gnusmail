package gnusmail.languagefeatures;

import gnusmail.Languages.Language;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class tokenizes an email, taking into account different separator symbols
 * @author jmcarmona
 */
public class EmailTokenizer {

	String body;
	List<Token> tokens;
	Language lang = null;
	public final static String tokenPattern = " \t\n\r\f.,;:?¿!¡\"()'=[]{}/<>-*`0123456789ªº%&*@_|’\\#";
	//public final static String alphabPattern = "[a-zA-Z]";
	public final static String alphabPattern = "\\w";

	public EmailTokenizer(String emailBody) {
		this.body = emailBody.toLowerCase();
		tokens = new LinkedList<Token>();
	}

	public EmailTokenizer(String emailBody, Language lang) {
		this.body = emailBody;
		tokens = new LinkedList<Token>();
		this.lang = lang;
	}

	public List<Token> tokenize() {
		Pattern patAlph = Pattern.compile(alphabPattern);
		Matcher m = null;
		//int limit = 1000;
		if (body != null) {
			StringTokenizer st = new StringTokenizer(body, tokenPattern);
			while (st.hasMoreElements()) {
				Token token = new Token(st.nextToken());
				if (lang != null) {
					token.setLanguage(lang);
				}
				m = patAlph.matcher(token.getLowerCaseForm());
				boolean isOK = m.find();
				if (token.originalForm.length() > 0 && isOK) {
					tokens.add(token);
				}
			}
		}
		return tokens;
	}
}
