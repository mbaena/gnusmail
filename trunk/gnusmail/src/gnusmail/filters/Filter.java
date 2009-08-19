package gnusmail.filters;

import gnusmail.core.cnx.MessageInfo;

public abstract class Filter {

	abstract public String getName();
	
	abstract public String applyTo(MessageInfo mess);
}
