package gnusmail.filters;

import javax.mail.MessagingException;

import gnusmail.core.cnx.MensajeInfo;

public final class ReceivedDate extends Filter {

	@Override
	public String getNombreFiltro() {
		// TODO Auto-generated method stub
		return "ReceivedDate";
	}

	@Override
	public String aplicarFiltro(MensajeInfo mess) {
		// TODO Auto-generated method stub
		try {
			return mess.getReceivedDate();
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			return "?";
		}
	}

}
