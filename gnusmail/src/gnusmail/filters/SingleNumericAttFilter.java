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

import gnusmail.core.cnx.MessageInfo;

import java.util.ArrayList;
import java.util.List;
import javax.mail.MessagingException;
import weka.core.Attribute;
import weka.core.Instance;

public abstract class SingleNumericAttFilter extends Filter {

	private Attribute attribute;
	
	public SingleNumericAttFilter() {
		attribute = new Attribute(this.getName());
	}
		
	@Override
	public List<Attribute> getAttributes() {
		ArrayList<Attribute> attList = new ArrayList<Attribute>();
		attList.add(attribute);
		return attList;
	}

	@Override
	public void updateAttValues(MessageInfo msgInfo) {
	}

	@Override
	public void updateInstance(Instance inst, MessageInfo messageInfo) {
		try {
			inst.setValue(attribute, getSingleValue(messageInfo));
		} catch (MessagingException e) {
			inst.setMissing(attribute);
		}
	}
	
	protected abstract double getSingleValue(MessageInfo messageInfo) throws MessagingException;


}
