package gnusmail.filters;

import gnusmail.core.cnx.MessageInfo;
import javax.mail.MessagingException;


public final class To extends SingleAttFilter {

	@Override
	protected String getSingleValue(MessageInfo messageInfo)
			throws MessagingException {
		String to =  messageInfo.getTo();
		String splits[] = to.split(",");
		String ret = "None";
		if (splits.length > 0) ret = splits[0];
		return ret;
	}

}
