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
package gnusmail.core.cnx;

import gnusmail.core.ConfigManager;

import java.security.GeneralSecurityException;
import java.util.Properties;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.URLName;

import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPSSLStore;
import com.sun.mail.imap.IMAPStore;
import com.sun.mail.util.MailSSLSocketFactory;

public class Connection {

	private IMAPFolder folder;
	private String hostname;
	private String username;
	private String password;
	private Session session;
	private IMAPStore store;
	//private URLName url;
	private String protocol;
	private String mbox = "INBOX";

	public Connection() {
		System.out.println("Creating connection...");
		username = ConfigManager.getProperty("username");
		password = ConfigManager.getProperty("password");
		hostname = ConfigManager.getProperty("hostname");
		protocol = ConfigManager.getProperty("protocol");
		System.out.println(protocol + "://" + username + ":" + "@" + hostname);
		try {
			login();
			folder = (IMAPFolder) store.getFolder(mbox);
		} catch (MessagingException e) {
			System.out.println("Connection Error");
			e.printStackTrace();
		}

	}

	public Connection(String string) {
		URLName url = new URLName(string);
		this.hostname = url.getHost();
		this.username = url.getUsername();
		this.password = url.getPassword();
		this.protocol = url.getProtocol();
		try {
			login();
			saveConnectionData();
		} catch (MessagingException e) {
			System.out.println("Connection error");
			e.printStackTrace();
		}
		try {
			if (url.getFile() == null) {
				folder = (IMAPFolder) store.getFolder(mbox);
			} else {
				folder = (IMAPFolder) store.getFolder(url.getFile());
			}
		} catch (Exception e) {
			folder = null;
			System.out.println("\nFolder '" + url.getFile() + "' not found!!!");
		}
	}

	/** Returns the javax.mail.Folder object. */
	public IMAPFolder getFolder() {
		return folder;
	}

	/** Returns the number of messages in the folder. */
	public int getMessageCount() throws MessagingException {
		return folder.getMessageCount();
	}

	/** Gets the hostname */
	public String getHostname() {
		return hostname;
	}

	/** Sets the hostname */
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	/** Gets the username */
	public String getUsername() {
		return username;
	}

	/** Sets the username */
	public void setUsername(String username) {
		this.username = username;
	}

	/** Gets the password */
	public String getPassword() {
		return password;
	}

	/** Sets the password */
	public void setPassword(String password) {
		this.password = password;
	}

	/** Gets the protocol name */
	public String getProtocol() {
		return protocol;
	}

	/** Gets the session */
	public Session getSession() {
		return session;
	}

	/** Sets the session  */
	public void setSession(Session session) {
		this.session = session;
	}

	/** Gets the store */
	public IMAPStore getStore() {
		return store;
	}

	/** Sets the store */
	public void setStore(IMAPSSLStore store) {
		this.store = store;
	}

	/** Method for checking if the user is logged in. */
	public boolean isLoggedIn() {
		return (store != null && store.isConnected());
	}

	/** Method used to login to the mail host. */
	public void login() throws MessagingException {
		if (session == null) {
			Properties props = null;
			try {
				
				props = System.getProperties();
				MailSSLSocketFactory sf = new MailSSLSocketFactory();
				sf.setTrustAllHosts(true);
				// or
				// sf.setTrustedHosts(new String[] { "my-server" });
				props.put("mail.imaps.ssl.enable", "true");
				// also use following for additional safety
				//props.put("mail.smtp.ssl.checkserveridentity", "true");
				props.put("mail.imaps.ssl.socketFactory", sf);

			} catch (SecurityException sex) {
				props = new Properties();
			} catch (GeneralSecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			session = Session.getInstance(props);

		}
		URLName url = new URLName(protocol + "://" + username + ":" + password + "@" + hostname);
		store = new IMAPSSLStore(session, url);

		try {
			System.out.println("Connecting... ");
			store.connect();
		//System.out.println("CONECTADO!!");
		} catch (MessagingException e) {
			System.out.println(e.getMessage());
			throw (e);
		//return;
		}
	}

	private void saveConnectionData() {
		ConfigManager.setProperty("username", username);
		ConfigManager.setProperty("password", password);
		ConfigManager.setProperty("hostname", hostname);
		ConfigManager.setProperty("protocol", protocol);
		ConfigManager.saveFile();
	}

	/** Logout from the mail host. */
	public void logout() throws MessagingException {
		if (folder != null && folder.isOpen()) {
			folder.close(true);
		}
		store.close();
		store = null;
		session = null;
	}

	public void show_mens() throws javax.mail.MessagingException {
		if ((folder.getType() & Folder.HOLDS_MESSAGES) != 0) {
			System.out.println();
			if (folder.hasNewMessages()) {
				System.out.println("You have new mail");
			}

			System.out.println("Message count:  " + folder.getMessageCount());
			System.out.println("New Messages:    " + folder.getNewMessageCount());
			System.out.println("Unread messages: " + folder.getUnreadMessageCount());
		}
	}

	public void showAttributes(int mail_id) throws Exception {
		Folder buzon = folder;//miconexion.getFolder();

		System.out.println("Showing attributes for message no.  " + mail_id + "...");
		System.out.println("--------------------------------------------");
		if (buzon == null) {
			login();
		}
		if (mail_id > 0 && mail_id <= buzon.getMessageCount()) {
			MessageInfo msj = new MessageInfo(buzon.getMessage(mail_id));

			System.out.println("From: " + msj.getFrom());
			System.out.println("To: " + msj.getTo());
			System.out.println("Bcc: " + msj.getBcc());
			System.out.println("Cc: " + msj.getCc());
			System.out.println("Sent Date: " + msj.getSentDate());
			System.out.println("Reception date: " + msj.getReceivedDate());
			System.out.println("Subject: " + msj.getSubject());
			System.out.println("Message-Id: " + msj.getMessageId());
			System.out.println();
		} else {
			System.out.println("Non-existent message");
		}
	}

	public void showMessages(String nombre_carpeta) throws Exception {
		Folder inbox = (getStore()).getFolder(nombre_carpeta);
		int num_mes = inbox.getMessageCount();
		MessageInfo message;
		System.out.println("Opening folder " + nombre_carpeta + "...");

		inbox.open(Folder.READ_WRITE);

		System.out.println("Num	From:			Subject:				Date		Attachements");
		System.out.println("----------------------------------------------------------------------------------------");

		for (int i = 1; i <= num_mes; i++) {
			message = new MessageInfo(inbox.getMessage(i));

			System.out.print(message.getNum() + "	");
			System.out.print(message.getFrom() + "	");
			System.out.print(message.getSubject() + "		");
			System.out.print(message.getDateAsStr() + "	");
			if (message.hasAttachments()) {
				System.out.print("Yes");
			} else {
				System.out.print("No");
			}
			System.out.println();
		}
		inbox.close(true);
	}

	public void readMail(int n) throws Exception {
		Folder auxFolder = folder;
		Folder folders[] = getFolders();
		auxFolder = folders[0];
		if (!auxFolder.isOpen()) {
			auxFolder.open(Folder.READ_ONLY);
		}

		if (auxFolder != null) {
			if (n > 0 && n <= auxFolder.getMessageCount()) {
				MessageInfo msj = new MessageInfo(auxFolder.getMessage(n));

				System.out.print("------------------------------------------" +
						"Message Text------------------------------------\n");
				System.out.println(msj.getBody());
				System.out.println(msj.getFolder());
				msj.print(System.out);
				System.out.println();

			} 
		}
	}

	public Folder[] getFolders() throws MessagingException {
		if (!isLoggedIn()) {
			login();
		}
		return (store.getFolder("INBOX")).list("*");
	}

	public void listFolders() throws MessagingException {
		Folder rf = folder;

		openFolder(rf, false, "");
		if ((rf.getType() & Folder.HOLDS_FOLDERS) != 0) {
			Folder[] f = rf.list();				//Direct subfolders only
			for (int i = 0; i < f.length; i++) {
				openFolder(f[i], true, "    ");
			}
		}
	//return rf.list("*");
	}

	public void openFolder(Folder folder, boolean recurse, String tab) throws MessagingException {
		System.out.println(tab + "Complet name: " + folder.getFullName());
		System.out.println(tab + "URL: " + folder.getURLName());
		if (true) {
			if (!folder.isSubscribed()) {
				System.out.println(tab + "Not Subscribed");
			}
			if ((folder.getType() & Folder.HOLDS_MESSAGES) != 0) {
				if (folder.hasNewMessages()) {
					System.out.println(tab + "You have new mail");
				}
				System.out.println(tab + "Message count: " + folder.getMessageCount());
				System.out.println(tab + "New messages: " + folder.getNewMessageCount());
				System.out.println(tab + "Unread messages: " + folder.getUnreadMessageCount());
			}
		}
		System.out.println();
		if ((folder.getType() & Folder.HOLDS_FOLDERS) != 0) {
			if (recurse) {
				Folder[] f = folder.list();
				for (int i = 0; i < f.length; i++) {
					openFolder(f[i], recurse, tab + " ");
				}

			}//if
		}//if
	}//dumpFolder

	public void moveMail(MessageInfo msg, Folder destination) {
		Message[] msjs = new Message[1];
		msjs[0] = msg.message;

		try {
			folder.copyMessages(msjs, destination);
			msg.message.setFlag(Flags.Flag.DELETED, true);
			folder.expunge();
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/** Marca para su borrado de la carpeta folder el mensaje en la posicion indicada */
	public void deleteMessage(int posicion, Folder carpeta) throws MessagingException {
		Message m = carpeta.getMessage(posicion);
		m.setFlag(Flags.Flag.DELETED, true); // set the DELETED flag
	}
} //fin clase Conexion