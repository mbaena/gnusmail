package gnusmail.filters;

import gnusmail.core.cnx.MessageInfo;

import javax.mail.MessagingException;

/**
 * 
 * @author jmcarmona
 */
public class DomainOfReceiver extends SingleAttFilter {

	@Override
	protected String getSingleValue(MessageInfo messageInfo)
			throws MessagingException {
		String res = null;
		String to = mess.getTo();
		String fields[] = to.split("@");
		if (fields.length > 0)
			res = fields[1];
        if (res==null){
        	throw new MessagingException();
        }
		return res;
	}

}
