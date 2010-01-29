package gnusmail.filters;

import javax.mail.MessagingException;

public final class To extends Filter {
	
	public String getValueForHeader(String header) {
		try {
            return  mess.getTo();
		} catch (MessagingException e) {
			return "?";
		}
	}
}
