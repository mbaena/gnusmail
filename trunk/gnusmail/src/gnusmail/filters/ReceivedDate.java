package gnusmail.filters;

import javax.mail.MessagingException;

public final class ReceivedDate extends Filter {

	@Override
	public String getValueForHeader(String header) {
		try {
			return mess.getReceivedDate();
		} catch (MessagingException e) {
			return "?";
		}
	}

}
