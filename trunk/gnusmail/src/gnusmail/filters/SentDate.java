package gnusmail.filters;

import gnusmail.core.cnx.MensajeInfo;

public final class SentDate extends Filter {

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "SentDate";
	}

	@Override
	public String applyTo(MensajeInfo mess, String initialFolderName) {
		try {
			//String res = csvmng.getValue(this.getNombreFiltro(), mess);
			return mess.getSentDate();
		} catch (Exception e){
			return "?";
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
