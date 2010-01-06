package gnusmail.filters;

import javax.mail.MessagingException;

import gnusmail.core.cnx.MessageInfo;

public final class Subject extends Filter {
	/**
	 * @param args
	 */
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	public String getName(){
		return "Subject";
	}
	
	public String applyTo(MessageInfo mess){
		//String res = csvmng.getValue(this.getNombreFiltro(), mess);
        try {
			return mess.getSubject();
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			return "?";
		}
	}
}