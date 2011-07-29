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
package gnusmail.datasource.mailconnection;

import java.io.InputStream;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;


/**
 * This is an extended class for MimeMessage. The folder (as a String) is included,
 * since the original imap folder cannot be retrieved from the file.
 * @author jmcarmona
 */
public class MIMEMessageWithFolder extends MimeMessage {
	String folderAsStr;

	public String getFolderAsStr() {
		return folderAsStr;
	}

	public void setFolderAsStr(String folderAsStr) {
		this.folderAsStr = folderAsStr;
	}

	public MIMEMessageWithFolder(Session arg0, InputStream arg1) throws MessagingException {
		super(arg0, arg1);
	}
}
