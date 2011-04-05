package gnusmail.filters;

import gnusmail.core.cnx.MessageInfo;

import javax.mail.MessagingException;


public final class Bcc extends SingleAttFilter {
	
	@Override
	protected String getSingleValue(MessageInfo messageInfo)
			throws MessagingException {
		String bcc = messageInfo.getBcc();
		if (bcc.equals("")) bcc = "None";
		return bcc;
	}
}
