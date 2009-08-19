package gnusmail.filters;

import gnusmail.core.cnx.MessageInfo;

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
	public String applyTo(MessageInfo mess) {
		//String res = csvmng.getValue(this.getNombreFiltro(), mess);
		return mess.getMessageId();
	}



}
