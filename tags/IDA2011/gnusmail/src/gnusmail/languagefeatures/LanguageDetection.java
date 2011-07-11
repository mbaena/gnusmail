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
