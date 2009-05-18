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
	public String getName() {
		return "MessageId";
	}

	@Override
	public String applyTo(MensajeInfo mess, String initialFolderName) {
		//String res = csvmng.getValue(this.getNombreFiltro(), mess);
		return mess.getMessageId();
	}



}
