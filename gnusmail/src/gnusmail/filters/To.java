package gnusmail.filters;

import javax.mail.MessagingException;

import gnusmail.core.cnx.MessageInfo;

public final class To extends Filter {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	public String getName(){
		return "To";
	}
	
	public String applyTo(MessageInfo mess) {
		try {
            return  mess.getTo();
		} catch (MessagingException e) {
			return "?";
		}
	}
}
