package gnusmail;

import gnusmail.core.WordStore;
import gnusmail.core.cnx.Conection;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

public class MainManager {

    private Conection connection;
	private ClassifierManager classifierManager;
	private FilterManager filterManager;
	
    public MainManager(String url) {
		try {
			connect(url);
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		classifierManager = new ClassifierManager();
		filterManager = new FilterManager();
	}

    public MainManager() {
    	this(null);
	}

	/** Connects to URL 
     * @throws Exception */
    private void connect(String url) throws MessagingException {
        if (url != null) {
            try {
                connection = new Conection(url);
                connection.login(url);
                System.out.println("Connected!");
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Unable to connect to the requested host!");
            }
            if (connection.getFolder() != null) {
                connection.mostrar_mens();
            }
        } else {
            if (connection == null) {
                connection = new Conection();
            }        	
        }
    }

	public void showAttibutes(int mail_id) {
        try {
			connection.mostrarAtributos(mail_id);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	public void listFolders() {
		// TODO Auto-generated method stub
		
	}

	public void mailsInFolder() {
		// TODO Auto-generated method stub
		
	}

	public void openMail(int openMail) {
		// TODO Auto-generated method stub
		
	}
	

	public void extractAttributes() {
        try {
        	WordStore wordStore = new WordStore();
            wordStore.leerListaPalabras(connection);
            System.out.println("Salvando atributos...");
            filterManager.saveAtributos(connection);
            System.out.println("Atributos salvados");
        } catch (Exception e1) {
            filterManager.escribirFichero();
            e1.printStackTrace();
        }        	

		
	}

	public void trainModel() {
        classifierManager.entrenarModelo();        			
	}

	public void classifyMail(MimeMessage msg) {
        try {
			classifierManager.clasificarCorreo(msg);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}        	
	}

	public void close() {
        if ((connection != null) && (connection.isLoggedIn())) {
            try {
				connection.logout();
			} catch (MessagingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
	}


}
