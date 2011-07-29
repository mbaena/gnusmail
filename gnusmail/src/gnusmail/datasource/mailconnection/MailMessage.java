package gnusmail.datasource.mailconnection;

import gnusmail.datasource.Document;
import gnusmail.filters.Filter;
import gnusmail.filters.FilterManager;

import java.io.IOException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.Message;
import javax.mail.MessagingException;

import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;



public class MailMessage extends Document {
	private Message message;
	
	public MailMessage(Message msg) {
		this.message = msg;
	}
	
	public Message getMessage() {
		return message;
	}

	@Override
	public Date getDate() {
		try {
			return message.getReceivedDate();
		} catch (MessagingException e) {
			return null;
		}
	}

	@Override
	public String getLabel() {
	   String res = ((MIMEMessageWithFolder) message).getFolderAsStr();
	   return res;
	}

	

	@Override
	public String getDocId() {
		return message.getMessageNumber() + "";
	}

	@Override
	public double getLength() {
		double length = 0.0;
		try {
			length =  new MessageInfo(message).getBody().length() * 1.0;
		} catch (MessagingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return length;
	}

	@Override
	public String getText() {
		String res = "";
		try {
			res =  new MessageInfo(message).getBody();
		} catch (MessagingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return res;
	}

	
}
