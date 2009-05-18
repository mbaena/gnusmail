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
    private URLName url;
    private String protocol;
    private String mbox = "INBOX";

    public Connection(){
        System.out.println("Creando conexion...");
        username = ConfigurationManager.getProperty("username");
        password = ConfigurationManager.getProperty("password");
        hostname = ConfigurationManager.getProperty("hostname");
        protocol = ConfigurationManager.getProperty("protocol");
        //System.out.println(protocol+"://"+username+":"+password+"@"+hostname);
        try {
        	login(protocol+"://"+username+":"+password+"@"+hostname);
		}catch (MessagingException e){
			System.out.println("Error de conexion");
			e.printStackTrace();
		}
    	
    }
    
    public Connection(String cadena) {
    	URLName url= new URLName(cadena);
    	this.hostname = url.getHost();
        this.username = url.getUsername();
        this.password = url.getPassword();	    
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

    /** Gets the  url */
    public URLName getUrl() {
        return url;
    }

    /** Method for checking if the user is logged in. */
    public boolean isLoggedIn() {
        return (store !=null && store.isConnected());
    }
      
    /** Method used to login to the mail host. */
    public void login(String cad) throws MessagingException {
		url = new URLName(cad);
		this.protocol = url.getProtocol();
		if (session == null) {
		    Properties props = null;
		    try {
			// configure the jvm to use thte jsse security
			Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
			Security.setProperty( "ssl.SocketFactory.provider", "gnusmail.core.cnx.DummySSLSocketFactory");   
			
			// crea el properties para la sesion
			props = System.getProperties();
						
			// activa esta sesion para usar SSL sobre conexiones IMAP
			props.setProperty("mail.imap.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
			
			// Cambia a conexiones IMAP normales en caso de fallo.
			props.setProperty("mail.imap.socketFactory.fallback", "true");		
			
		    } catch (SecurityException sex) {
		    	props = new Properties();
		    }
		    session = Session.getInstance(props);
		    
		}
		
		store = new IMAPSSLStore(session, url);
       
		try {
			System.out.print("Conectando... ");
			store.connect();
			System.out.println("CONECTADO!!");
		} catch (MessagingException e){
            System.out.println(e.getMessage());
			throw(e);
			//return;
		}
		try {			
			if (url.getFile()==null) 
				folder = (IMAPFolder) store.getFolder(mbox);
			else 
				folder = (IMAPFolder) store.getFolder(url.getFile());
			
			folder.open(Folder.READ_WRITE);
		} catch (Exception e){
			folder=null;
			System.out.println("\nCarpeta '"+url.getFile()+"' no encontrada!!!");
		}
		this.username = url.getUsername();
		this.hostname = url.getHost();
		this.password = url.getPassword();
		
		guardarDatosConexion();				
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
        if (folder!=null) folder.close(true);
        store.close();
        store = null;
        session = null;
    }

    
    public void mostrar_mens() throws javax.mail.MessagingException{
		if(( folder.getType() & Folder.HOLDS_MESSAGES)!=0) {
		      System.out.println();
		      if( folder.hasNewMessages()) System.out.println( "Tiene Nuevos Mensajes");
			      
		      System.out.println("Total de Mensajes:  "+folder.getMessageCount());
		      System.out.println("Mensajes Nuevos:    "+folder.getNewMessageCount());
		      System.out.println("Mensajes no leidos: "+folder.getUnreadMessageCount());
		}
    }
    
    /** Imprime los atributos del correo n */
    public void mostrarAtributos(int mail_id) throws Exception {
		Folder buzon = folder;//miconexion.getFolder();
		
		System.out.println("Mostrando atributos del mensaje "+ mail_id +"...");
		System.out.println("--------------------------------------------");
		if (mail_id>0 && mail_id<=buzon.getMessageCount()) {
			MensajeInfo msj = new MensajeInfo(buzon.getMessage(mail_id));
			
			System.out.println("De: "+ msj.getFrom());
			System.out.println("Para: "+ msj.getTo());
			System.out.println("Bcc: "+ msj.getBcc());
			System.out.println("Cc: "+ msj.getCc());
			System.out.println("Fecha Envio: "+ msj.getSentDate());
			System.out.println("Fecha Recepcion: "+	msj.getReceivedDate());
			System.out.println("Asunto: " + msj.getSubject());
			System.out.println("Message-Id: " + msj.getMessageId());
			System.out.println();
		} else
			System.out.println("No existe el correo!!");			    
    }
    
    /** Muestra por pantalla las cabeceras de los correos de la carpeta actual */
    public void mostrarCorreos(String nombre_carpeta) throws Exception {	
		Folder buzon_entrada = (getStore()).getFolder(nombre_carpeta);
		int num_mes = buzon_entrada.getMessageCount();
		MensajeInfo mensaje;
		System.out.println("Abriendo carpeta "+ nombre_carpeta +"...");	
		
		buzon_entrada.open(Folder.READ_WRITE);
		
		System.out.println("Num	De:			Asunto:				Fecha		Adjunto");
		System.out.println("----------------------------------------------------------------------------------------");
	
		for (int i=1; i <= num_mes; i++){
			mensaje = new MensajeInfo(buzon_entrada.getMessage(i));
			
			System.out.print(mensaje.getNum() + "	");
			System.out.print(mensaje.getFrom()+"	");
			System.out.print(mensaje.getSubject() + "		");
			System.out.print(mensaje.getDate()+"	");
			if (mensaje.hasAttachments()) System.out.print("Si");
			else System.out.print("No");
			System.out.println();
		}
		buzon_entrada.close(true);
    }
    
    /** Imprime el contenido del correo, si es texto plano */
    public void leerCorreo(int n) throws Exception{
		Folder buzon = folder;

		if (n>0 && n<=buzon.getMessageCount()){
			MensajeInfo msj = new MensajeInfo(buzon.getMessage(n));
			
			System.out.print("------------------------------------------" +
					"Message Text------------------------------------\n");
			System.out.println(msj.getBody());
			System.out.println();
			
		} else {
			System.out.println("No existe el correo!!");
		}	    
    }
    
    public Folder[] getCarpetas() throws MessagingException {
    	
    	Folder f[];
		f = (store.getFolder("INBOX")).list("*");
		
		return f;
    	
    }
    
    /** Lista por pantalla las carpetas del Store  
     * @throws MessagingException */
    public void listarCarpetas() throws MessagingException {	    
     	Folder rf = folder;
     	    	
		abrirFolder(rf, false, "");
		if ((rf.getType() & Folder.HOLDS_FOLDERS) != 0) {
			Folder[] f = rf.list();				//Obtiene solo los subdirectorios directos
			for (int i = 0; i < f.length; i++)
			abrirFolder(f[i], true, "    ");
		}
		//return rf.list("*");
    }    
    
    public void abrirFolder(Folder folder, boolean recurse, String tab) throws MessagingException{
		System.out.println(tab + "Nombre completo: " + folder.getFullName());
		System.out.println(tab + "URL: " + folder.getURLName());
		if (true) {
			if (!folder.isSubscribed())	System.out.println(tab + "No Suscrito");
			if ((folder.getType() & Folder.HOLDS_MESSAGES) != 0) {
				if (folder.hasNewMessages())
					System.out.println(tab + "Tiene Nuevos Mensajes");
				System.out.println(tab + "Total Mensajes: " +folder.getMessageCount());
				System.out.println(tab + "Mensajes nuevos: " +folder.getNewMessageCount());
				System.out.println(tab + "Mensajes no leidos: " +folder.getUnreadMessageCount());
			}
		}
		System.out.println();
		if ((folder.getType() & Folder.HOLDS_FOLDERS) != 0) {
			if (recurse) {
				Folder[] f = folder.list();
				for (int i=0; i < f.length; i++)
					abrirFolder(f[i], recurse, tab + " ");
		
			}//if
		}//if
	}//dumpFolder
    
    /** Mueve el correo msg de la carpeta actual a la carpeta destino */
    public void moverCorreo(MensajeInfo msg, Folder destino){
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
    public void borrarMensaje(int posicion, Folder carpeta) throws MessagingException {
    	Message m = carpeta.getMessage(posicion);
		m.setFlag(Flags.Flag.DELETED, true); // set the DELETED flag
    }   
	    
} //fin clase Conexion