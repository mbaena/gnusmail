package gnusmail.filters;

import gnusmail.core.cnx.MensajeInfo;

public abstract class Filter {

	abstract public String getName();
	
	abstract public String applyTo(MensajeInfo mess, String initialFolderName);	
}
