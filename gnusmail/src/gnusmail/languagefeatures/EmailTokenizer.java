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
