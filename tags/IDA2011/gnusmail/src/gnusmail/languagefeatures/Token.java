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
import gnusmail.languagefeatures.stemming.StemmerFactory;

/**
 * This class encapsulates a token, and has method to return several variations of
 * it, such as lower case, or stemmed form.
 * @author jmcarmona
 */
public class Token {

    String originalForm;
    Language language;

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    

    public Token(String originalForm) {
        this.originalForm = originalForm;
    }

    public String getBasicForm() {
        return originalForm;
    }

    public String getLowerCaseForm() {
        return originalForm.toLowerCase();
    }

    /**
     * This function obtains a stemmed form of the lower case of the original form
     * of the word, attending to the detected language
     * @return
     */
    public String getStemmedForm() {
        String toStem = originalForm.replaceAll("\\.|:|,|\\(|\\)", "");
       // String toStem = getLowerCaseForm();
		String stemmed = StemmerFactory.getStemmer(language).extactRoot(toStem);
		return stemmed.toLowerCase();
    }

    @Override
    public String toString() {
        return "[" + originalForm + " " + getStemmedForm() + " " + language.getLanguageName() + "]";
    }
}
