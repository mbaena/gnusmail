package gnusmail.filters;

import gnusmail.core.cnx.MessageInfo;
import javax.mail.MessagingException;


public final class To extends SingleAttFilter {

	@Override
	protected String getSingleValue(MessageInfo messageInfo)
			throws MessagingException {
		return messageInfo.getTo();
	}

}
