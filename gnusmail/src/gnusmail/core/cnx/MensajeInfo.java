package gnusmail.core.cnx;

import java.text.*;
import java.io.*;
import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;

/** Usada para almacenar informacion del mensaje. */
public class MensajeInfo {
    public Message message;

    /** Metodo que mapea un Message en la clase MensajeInfo */
    public MensajeInfo (Message message){
        this.message = message;
    }
    
    /** Devuelve el campo Message-Id */
    public String getMessageId() {//throws MessagingException {
    	String[] res;
        try {
        	res = message.getHeader("Message-Id");        	
        } catch (MessagingException e) {
        	return "?";
        }
        return res[0];
    }
    
    /** Devuelve el campo Bcc: */
    public String getBcc() throws MessagingException {
    	String res;
    	try {
    		res = formatAddresses(
    	            message.getRecipients(Message.RecipientType.BCC)); 
    	} catch (Exception e) {
    		res ="?";
    	}
        return res;
    }
    
    /** Devuelve el cuerpo del mensaje (si es solo texto). */
    public String getBody() throws MessagingException, IOException {
        Object content = message.getContent();
        String result = "";
        
        if (message.isMimeType("text/plain")) {
            result = (String)content;
        } else if (message.isMimeType("multipart/alternative")) {
        	Multipart mp = (Multipart)message.getContent();
            int numParts = mp.getCount();
            for (int i = 0; i < numParts; ++i) {
                if (mp.getBodyPart(i).isMimeType("text/plain"))
                    try {
                    result = (String)mp.getBodyPart(i).getContent();
                    } catch (Exception e) {
                        System.out.println("Excepcion cogiendo cuerpo de correo");
                    }
            }
            result = "";
        } else if (message.isMimeType("multipart/*")) { 
        	Multipart mp = (Multipart)content;
            if (mp.getBodyPart(0).isMimeType("text/plain"))
                result = (String)mp.getBodyPart(0).getContent();
            else result = "";
        } else result = "";
        result = new String(result.getBytes(), "UTF-8");
        return result;
    }
    
    /** Devuelve el campo Cc: */
    public String getCc() {
    	String res;
    	try {
    		res = formatAddresses(
    	            message.getRecipients(Message.RecipientType.CC)); 
    	} catch (MessagingException e){
    		res ="?";
    	}
        return res;
    }
    
    /** Devuelve la fecha de envio del mensaje 
		(o la de recepcion si la de envio es null. */
    public String getDate() throws MessagingException {
        Date date;
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yy");
        if ((date = message.getSentDate()) != null)
            return (df.format(date));
        else if ((date = message.getReceivedDate()) != null)
            return (df.format(date));
        else
            return "";
     }
       
    /** Devuelve el campo From: */
    public String getFrom() throws MessagingException {
        return formatAddresses(message.getFrom());
    }

    /** Devuelve la/s direccion/es de ReplyTo: */
    public String getReplyTo() throws MessagingException {
	Address[] a = message.getReplyTo();
	if (a.length > 0)
	    return ((InternetAddress)a[0]).getAddress();
	else
	    return "";
    }
    
    /** Devuelve el objeto javax.mail.Message */
    public Message getMessage() {
        return message;
    }
    
    /** Devuelve el numero de mensaje */
    public int getNum() {
        return ((message.getMessageNumber()));
    }
    
    /** Devuelve el campo received date */
    public String getReceivedDate() throws MessagingException {
        if (hasReceivedDate())
            return (message.getReceivedDate().toString());
        else
            return "";
    }
    
    /** Devuelve el campo Sentdate */
    public String getSentDate() throws MessagingException {
        if (hasSentDate())
            return (message.getSentDate().toString()); 
        else
            return "";
    }
    
    /** Devuelve el campo Subject: */
    public String getSubject() throws MessagingException {
        if (hasSubject())
            return message.getSubject();
        else
            return "";
    }
    
    /** Devuelve el campo To: */
    public String getTo() throws MessagingException {
        return formatAddresses( message.getRecipients(Message.RecipientType.TO));
    }
    
    /** Metodo que comprueba si el mensaje tiene attachments. */
    public boolean hasAttachments() throws IOException, MessagingException {
        boolean hasAttachments = false;
        if (message.isMimeType("multipart/*")) {
	    Multipart mp = (Multipart)message.getContent();
            if (mp.getCount() > 1)
                hasAttachments = true;
        }            
        return hasAttachments;
    }
    
    /** Metodo que comprueba si el mensaje tiene campo Bcc: */
    public boolean hasBcc() throws MessagingException {
        return (message.getRecipients(Message.RecipientType.BCC) != null);
    }
    
    /** Metodo que comprueba si el mensaje tiene campo Cc: */
    public boolean hasCc() throws MessagingException {
        return (message.getRecipients(Message.RecipientType.CC) != null);
    }
    
    /** Metodo que comprueba si el mensaje tiene algun campo date */
    public boolean hasDate() throws MessagingException {
        return (hasSentDate() || hasReceivedDate());
    }
    
    /** Metodo que comprueba si el mensaje tiene un campo From: */
    public boolean hasFrom() throws MessagingException {
        return (message.getFrom() != null);
    }
    
    /** Metodo que comprueba si el mensaje tiene el tipo mime deseado */
    public boolean hasMimeType(String mimeType) throws MessagingException {
        return message.isMimeType(mimeType);
    }
    
    /** Metodo que comprueba si el mensaje tiene el campo received date */
    public boolean hasReceivedDate() throws MessagingException {
        return (message.getReceivedDate() != null);
    }
    
    /** Metodo que comprueba si el mensaje tiene campo sent date */
    public boolean hasSentDate() throws MessagingException {
        return (message.getSentDate() != null);
    }
    
    /** Metodo que comprueba if el mensaje tiene campo subject */
    public boolean hasSubject() throws MessagingException {
        return (message.getSubject() != null);
    }
    
    /** Metodo que comprueba si el mensaje tiene un campo To: */
    public boolean hasTo() throws MessagingException {
        return (message.getRecipients(Message.RecipientType.TO) != null);
    }
    
    /** Metodo que devuelve la carpeta q contiene el mensaje */
    public Folder getFolder(){
    	return message.getFolder();    	
    }
    
    /** Metodo que imprime el mensaje en el OutputStream pasado como argumento */
    public void imprimir(OutputStream os) {
    	try {
			message.writeTo(os);			
		} catch (Exception e) {
			System.out.println("ERROR: Imposible imprimir mensaje!!");
			//e.printStackTrace();
		}    	
        	
    }
    
    /** Metodo que añade al mensaje una cabecera llamada nombre y q contenga contenido */
    public void crearCabecera(String nombre, String contenido) {
    	try {
			message.addHeader(nombre, contenido);
		} catch (MessagingException e) {
			System.out.println("Imposible añadir nueva cabecera");
			//e.printStackTrace();
		}
    	
    }
    
    /** Metodo interno para formatear las direcciones del msg header */
    private String formatAddresses(Address[] addrs) {
        if (addrs == null)
            return "";
        StringBuffer strBuf = new StringBuffer(getDisplayAddress(addrs[0]));
        for (int i = 1; i < addrs.length; i++) {
            strBuf.append(", ").append(getDisplayAddress(addrs[i]));
        }
        return strBuf.toString();
    }

    /** Metodo interno que devuelve una cadena formateada para mostrar un msg header */
    private String getDisplayAddress(Address a) {
        String pers = null;
        String addr = null;
        pers = ((InternetAddress)a).getPersonal();
        if (a instanceof InternetAddress && (pers != null)) {
//		addr = pers + "  "+"&lt;"+((InternetAddress)a).getAddress()+"&gt;";
		addr = ((InternetAddress)a).getAddress();
        } else 
		addr = a.toString();
        return addr;
    }

}
