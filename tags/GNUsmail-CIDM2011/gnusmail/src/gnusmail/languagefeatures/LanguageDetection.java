package gnusmail.languagefeatures;

import gnusmail.Languages.Language;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;

/**
 * This class tries to detect the language of a given document.
 * The language is given by the max number of coincidences with the stopword
 * list of each language
 * @author jmcarmona
 */
public class LanguageDetection {
	//Global object containing the stopwords for every language

	private static final Language DEFAULT_LANGUAGE = Language.SPANISH;
	static Map<Language, List<String>> frequentWordsByLanguage;

	/**
	 * This method inspect the string passed as input,
	 * and returns a Language object representing the asserted language
	 * @param string
	 * @return
	 */
	public Language detectLanguage(String string) {
		/*Language langToReturn = null;

		if (frequentWordsByLanguage == null) {
			fillFrequentWordsList();
		}
		Map<Language, Integer> coincidencesByLanguage = new TreeMap<Language, Integer>();
		for (Language lang : frequentWordsByLanguage.keySet()) {
			coincidencesByLanguage.put(lang, numberOfCoincidences(string,
					frequentWordsByLanguage.get(lang)));
		}
		int maxNumberOfCoindidences = 0;
		for (Language langAux : coincidencesByLanguage.keySet()) {
			if (coincidencesByLanguage.get(langAux) > maxNumberOfCoindidences) {
				langToReturn = langAux;
				maxNumberOfCoindidences = coincidencesByLanguage.get(langAux);
			}
		}

		//If no language was found, we return a default value;
		if (langToReturn == null) {
			langToReturn = DEFAULT_LANGUAGE;
		}
		return langToReturn;*/
		return Language.ENGLISH;
	}

	private void fillFrequentWordsList() {
		frequentWordsByLanguage = StopWordsProvider.getInstance().getStopwordsMap();
	}

	private int numberOfCoincidences(String body, List<String> words) {
		int coincidences = 0;
		if (body != null) {
			body = body.toLowerCase();
			StringTokenizer st = new StringTokenizer(body); //TODO delimiters
			while (st.hasMoreTokens()) {
				if (words.contains(st.nextElement())) {
					coincidences++;
				}
			}
		}
		return coincidences;
	}
}
