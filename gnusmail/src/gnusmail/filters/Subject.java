package gnusmail.filters;

import javax.mail.MessagingException;

public final class Subject extends Filter {
	
	public String getValueForHeader(String header) {
        try {
			return mess.getSubject();
		} catch (MessagingException e) {
			return "?";
		}
	}
}
