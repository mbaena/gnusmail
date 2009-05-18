package gnusmail.filters;

//import javax.mail.*;

import gnusmail.core.cnx.MensajeInfo;

public final class Cc extends Filter{

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	public String getName(){
		return "Cc";
	}
	
	public String applyTo(MensajeInfo mess) {		
		String res;// = csvmng.getValue(this.getNombreFiltro(), mess);
		try {
			res= mess.getCc();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			res= "?";
		}
		return res;
	}
}
