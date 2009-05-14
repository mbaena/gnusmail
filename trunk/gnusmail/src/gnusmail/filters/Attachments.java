package gnusmail.filters;

import gnusmail.core.cnx.MensajeInfo;

public final class Attachments extends Filter{

	@Override
	public String getNombreFiltro() {
		return "Attachments";
	}

	@Override
	public String aplicarFiltro(MensajeInfo mess) {
		//String res = csvmng.getValue(this.getNombreFiltro(), mess);
		try {
			if (mess.hasAttachments()) return "True";
			return "False";
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
