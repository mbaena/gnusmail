package gnusmail.filters;

import gnusmail.core.cnx.MensajeInfo;

public final class Folder extends Filter {

	@Override
	public String getNombreFiltro() {
		// TODO Auto-generated method stub
		return "Folder";
	}

	@Override
	public String aplicarFiltro(MensajeInfo mess){
		// TODO Auto-generated method stub
		if (mess.getFolder()== null) return "?";
		return (String)mess.getFolder().toString();
	}

}
