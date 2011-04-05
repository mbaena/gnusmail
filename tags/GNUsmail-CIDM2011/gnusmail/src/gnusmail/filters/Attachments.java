package gnusmail.filters;

import java.io.IOException;

import gnusmail.core.cnx.MessageInfo;

import javax.mail.MessagingException;

public final class Attachments extends SingleAttFilter {

	@Override
	protected String getSingleValue(MessageInfo messageInfo)
			throws MessagingException {
		try {
			if (messageInfo.hasAttachments()) return "True";
		} catch (IOException e) {
			throw new MessagingException();
		}
		return "False";
	}

}
