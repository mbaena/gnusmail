package gnusmail;

import gnusmail.core.WordStore;
import gnusmail.core.cnx.Connection;
import gnusmail.core.cnx.MensajeInfo;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

public class MainManager {

    private Connection connection;
    private ClassifierManager classifierManager;
    private FilterManager filterManager;
    private String url;

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
        this.url = url;
        if (url != null) {
            try {
                connection = new Connection(url);
                // connection.login(url);
                connection.login();
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
                connection = new Connection();
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
        try {
            connection.listarCarpetas();
        } catch (MessagingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void listMails(int limit) {
        Iterable<Message> reader;
        reader = new MessageReader(connection, limit);
        for (Message msg : reader) {
            MensajeInfo msgInfo = new MensajeInfo(msg);
            try {
                System.out.println(msgInfo.getReceivedDate() + " " + msgInfo.getSubject());
            } catch (MessagingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

    public void mailsInFolder() {
        try {
            connection.mostrarCorreos("INBOX");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void openMail(int mail_id) {
        try {
            connection.mostrarAtributos(mail_id);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void extractAttributes() {
        try {
            //connection.logout(); //por que?
            //connection = null;
            //connect(url);

          //  filterManager.saveAtributos(connection);
            filterManager.saveAttributesInOrder(connection, 100);
            //connection = null;
            System.out.println("Vamos a escribir a fichero");
            filterManager.escribirFichero();
            System.out.println("Atributos salvados");
        } catch (Exception e1) {
            filterManager.escribirFichero();
            e1.printStackTrace();
        }


    }

    public void extractFrequentWords() {
        WordStore wordStore = new WordStore();
        wordStore.readWordsList(connection);
    }

    public void trainModel() {
        System.out.println("TrainModel");
        classifierManager.trainModel();
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
