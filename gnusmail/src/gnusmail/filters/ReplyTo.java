package gnusmail.filters;

import gnusmail.core.cnx.MensajeInfo;

public final class ReplyTo extends Filter {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	public String getName(){
		return "ReplyTo";
	}
	
	public String applyTo(MensajeInfo mess, String initialFolderName){
		try{
//			String res = csvmng.getValue(this.getNombreFiltro(), mess);
			return mess.getReplyTo();
		} catch (Exception e){
			return "?";
		}
		
	}
}
