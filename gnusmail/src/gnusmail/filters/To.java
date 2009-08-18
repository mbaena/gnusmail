package gnusmail.filters;

import javax.mail.MessagingException;

import gnusmail.core.cnx.MensajeInfo;

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
	
	public String applyTo(MensajeInfo mess) {
		//String res = csvmng.getValue(this.getNombreFiltro(), mess);
        try {
            return  mess.getFrom();
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			return "?";
		}
	}
}
