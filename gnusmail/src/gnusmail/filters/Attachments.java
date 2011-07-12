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

import java.io.IOException;

import gnusmail.core.cnx.Document;
import gnusmail.core.cnx.MailMessage;
import gnusmail.core.cnx.MessageInfo;

import javax.mail.Message;
import javax.mail.MessagingException;

public final class Attachments extends SingleAttFilter {

	@Override
	protected String getSingleValue(Document document)
			throws MessagingException {
		String res = "False";
		if (document instanceof MailMessage) {
			Message m = ((MailMessage) document).getMessage();
			MessageInfo mi = new MessageInfo(m);
			try {
				if (mi.hasAttachments()) res = "True";
			} catch (IOException e) {
				e.printStackTrace();
			}
		}		
		return res;

		
	}

}
