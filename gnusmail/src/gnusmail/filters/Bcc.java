package gnusmail.filters;

import javax.mail.MessagingException;


public final class Bcc extends Filter {
	@Override
	public String getValueForHeader(String header) {
		try {
			String bcc = mess.getBcc();
			if (bcc.equals("")) bcc = "None";
			return bcc;
		} catch (MessagingException e) {
			return "?";
		}
	}
}
