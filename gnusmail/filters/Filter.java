package gnusmail.filters;

import gnusmail.core.cnx.MensajeInfo;
//import csvMng.ClaseCSV;

public abstract class Filter {

	abstract public String getNombreFiltro();
	
	abstract public String aplicarFiltro(MensajeInfo mess);	
}
