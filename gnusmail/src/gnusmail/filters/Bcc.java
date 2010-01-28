package gnusmail.filters;

import javax.mail.MessagingException;

import gnusmail.core.cnx.MessageInfo;

public final class Bcc extends Filter {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	public String getName(){
		return "Bcc";
	}
	
	public String applyTo(MessageInfo mess){
		//String res = csvmng.getValue(this.getNombreFiltro(), mess);
		try {
			String bcc = mess.getBcc();
			if (bcc == "") bcc = "None";
			return bcc;
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			return "?";
		}
	}
}
