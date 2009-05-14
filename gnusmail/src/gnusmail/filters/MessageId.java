package gnusmail.filters;

import gnusmail.core.cnx.MensajeInfo;

public final class MessageId extends Filter {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	@Override
	public String getNombreFiltro() {
		return "MessageId";
	}

	@Override
	public String aplicarFiltro(MensajeInfo mess) {
		//String res = csvmng.getValue(this.getNombreFiltro(), mess);
		return mess.getMessageId();
	}



}
