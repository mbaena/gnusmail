package gnusmail.datasource;

import gnusmail.datasource.mailconnection.Connection;

import javax.mail.MessagingException;

public class MailDataSource extends DataSource {
	public static int IMAP_CONECTION = 0;
	public static int FILESYSTEM = 1;
	private DocumentReader reader = null;
	private Connection connection;
	
	private void connect(String url) throws MessagingException {
		if (url != null) {
			try {
				connection = new Connection(url);
				connection.login();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		} else {
			if (connection == null) {
				connection = new Connection();
			}
		}
	}
	
	public MailDataSource(int method, String url, int limit) {
		if (method == MailDataSource.IMAP_CONECTION) {
			try {
				connect(url);
				reader = MessageReaderFactory.createReader(connection, limit);
			} catch (MessagingException e) {
				e.printStackTrace();
			}			
		} else if (method == MailDataSource.FILESYSTEM) {
			System.out.println("Fatory: " + url);
			reader = MessageReaderFactory.createReader(url, limit);
		}
	
			
	}
	
	@Override
	public DocumentReader getDocumentReader() {
		return reader;
	}

}
