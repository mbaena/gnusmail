package gnusmail.filters;

import gnusmail.core.cnx.MessageInfo;

import java.util.StringTokenizer;
import javax.mail.MessagingException;

/**
 *
 * @author jmcarmona
 */
public class DomainOfSender extends SingleAttFilter {

	@Override
	protected String getSingleValue(MessageInfo messageInfo)
			throws MessagingException {
        String res=null;
        StringTokenizer st = new StringTokenizer(messageInfo.getFrom(), "@");
        while (st.hasMoreTokens()) {
            res = st.nextToken();
        }
        if (res==null){
        	throw new MessagingException();
        }
        return res;
	}

}
