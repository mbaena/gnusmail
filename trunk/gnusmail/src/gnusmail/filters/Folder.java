package gnusmail.filters;

import gnusmail.core.cnx.MessageInfo;

import javax.mail.MessagingException;

public final class Folder extends SingleAttFilter {

	@Override
	protected String getSingleValue(MessageInfo messageInfo)
			throws MessagingException {
		String folder = mess.getFolderAsString();
		return folder;
	}
}
