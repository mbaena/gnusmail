package gnusmail.core.cnx;

import gnusmail.core.ConfigurationManager;

import java.security.Security;
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

/** Esta clase almacena la informacion del usuario */
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
		System.out.println("Creando conexion...");
		username = ConfigurationManager.getProperty("username");
		password = ConfigurationManager.getProperty("password");
		hostname = ConfigurationManager.getProperty("hostname");
		protocol = ConfigurationManager.getProperty("protocol");
		System.out.println(protocol + "://" + username + ":" + "@" + hostname);
		try {
			login();
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
			if (url.getFile() == null) {
				folder = (IMAPFolder) store.getFolder(mbox);
			} else {
				folder = (IMAPFolder) store.getFolder(url.getFile());
			}
		} catch (Exception e) {
			folder = null;
			System.out.println("\nFolder '" + url.getFile() + "' not found!!!");
		}
		try {
			login();
			guardarDatosConexion();
		} catch (MessagingException e) {
			System.out.println("Connection error");
			e.printStackTrace();
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
				// configure the jvm to use thte jsse security
				Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
				Security.setProperty("ssl.SocketFactory.provider", "gnusmail.core.cnx.DummySSLSocketFactory");

				props = System.getProperties();

				props.setProperty("mail.imap.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

				props.setProperty("mail.imap.socketFactory.fallback", "true");

			} catch (SecurityException sex) {
				props = new Properties();
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

	/** Guarda los datos de conexion del usuario en una linea con formato
	 *  protocolo, username, password, hostname */
	private void guardarDatosConexion() {
		ConfigurationManager.setProperty("username", username);
		ConfigurationManager.setProperty("password", password);
		ConfigurationManager.setProperty("hostname", hostname);
		ConfigurationManager.setProperty("protocol", protocol);
		ConfigurationManager.grabarFichero();
	}

	/** Logout from the mail host. */
	public void logout() throws MessagingException {
		if (folder != null) {
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

	/** Imprime los atributos del correo n */
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

	/** Muestra por pantalla las cabeceras de los correos de la carpeta actual */
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

	/** Imprime el contenido del correo, si es texto plano */
	public void leerCorreo(int n) throws Exception {
		Folder auxFolder = folder;

		if (n > 0 && n <= auxFolder.getMessageCount()) {
			MessageInfo msj = new MessageInfo(auxFolder.getMessage(n));

			System.out.print("------------------------------------------" +
					"Message Text------------------------------------\n");
			System.out.println(msj.getBody());
			System.out.println();

		} else {
		}
	}

	public Folder[] getCarpetas() throws MessagingException {
		if (isLoggedIn()) {
			System.out.println("Is logged in");
		} else {
			login();
			System.out.println("Not logged in");
		}
		Folder f[];
		f = (store.getFolder("INBOX")).list("*");

		return f;

	}

	/** Lista por pantalla las carpetas del Store
	 * @throws MessagingException */
	public void listFolders() throws MessagingException {
		Folder rf = folder;

		openFolder(rf, false, "");
		if ((rf.getType() & Folder.HOLDS_FOLDERS) != 0) {
			Folder[] f = rf.list();				//Obtiene solo los subdirectorios directos
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
				System.out.println(tab + "Non Subscritoed");
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
	/** Mueve el correo msg de la carpeta actual a la carpeta destino */
	public void moveMail(MessageInfo msg, Folder destino) {
		Message[] msjs = new Message[1];
		msjs[0] = msg.message;

		try {
			folder.copyMessages(msjs, destino);
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