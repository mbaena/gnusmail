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
package gnusmail.filters;

import gnusmail.Languages.Language;
import gnusmail.core.WordsStore;
import gnusmail.core.cnx.Document;
import gnusmail.core.cnx.MailMessage;
import gnusmail.languagefeatures.DocumentTokenizer;
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

	private TreeMap<String, Integer> labelMap;
	private WordsStore ws;
	static List<String> wordsToAnalyze;
	List<Attribute> attList;
	private Map<String, Attribute> attrMap;

	public WordFrequency() {
		labelMap = new TreeMap<String, Integer>();
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
		for (String folder : labelMap.keySet()) { // Esto a wordsfreqency, pero
			ws.getTermFrequencyManager().updateWordCountPorFolder(folder);
			ws.getTermFrequencyManager().setNumberOfDocumentsByFolder(folder,
					labelMap.get(folder));
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
	public void updateAttValues(Document doc) {
		String label = doc.getLabel();
		List<Token> tokens = tokenizeDocument(doc);
		ws.addTokenizedString(tokens, label);
		try {
			labelMap.put(label, labelMap.get(label) + 1);
		} catch (NullPointerException e) {
			labelMap.put(label, 1);
		}
	}

	@Override
	public void updateInstance(Instance inst, Document doc) {
		Set<String> stringThisDocument = new HashSet<String>();
		List<Token> tokens = tokenizeDocument(doc);
		for (Token token : tokens) {
			String stemmedForm = token.getStemmedForm();
			if (stemmedForm.length() > 2) {
				stringThisDocument.add(stemmedForm);
			}
		}

		for (Attribute att : attList) {
			if (stringThisDocument.contains(att.name())) { //TODO esto es lento...mejorar
				inst.setValue(att, "True");
			} else {
				inst.setValue(att, "False");
			}
		}
	}

	private static List<Token> tokenizeDocument(Document doc) {
		String body = null;
		try {
			// Extraemos las palabras del cuerpo y la cabecera
			String subject = "";
			if (doc instanceof MailMessage) {
			  subject = ((MailMessage)doc).getMessage().getSubject();
			}
			String b = doc.getText();
			body = b + subject;
		} catch (MessagingException ex) {
			Logger.getLogger(WordFrequency.class.getName()).log(Level.SEVERE,
					null, ex);
		}
		Language lang = new LanguageDetection().detectLanguage(body);
		DocumentTokenizer et = new DocumentTokenizer(body, lang);
		return et.tokenize();
	}
}
