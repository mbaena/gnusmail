package gnusmail.filters;

import gnusmail.core.cnx.MessageInfo;
import javax.mail.MessagingException;



public final class Cc extends SingleAttFilter {

	@Override
	protected String getSingleValue(MessageInfo messageInfo)
			throws MessagingException {
		String cc = messageInfo.getBcc();
		if (cc.equals("")) cc = "None";
		return cc;
	}

}
