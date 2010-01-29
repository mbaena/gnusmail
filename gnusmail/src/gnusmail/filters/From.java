package gnusmail.filters;

import javax.mail.MessagingException;

public final class From extends Filter {
	
	public String getValueForHeader(String header) {
		try {
			return mess.getFrom();
		} catch (MessagingException e) {
			return "?";
		}
	}
}
