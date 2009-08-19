package gnusmail.filters;

import gnusmail.core.cnx.MessageInfo;

public final class Folder extends Filter {

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Folder";
	}

	@Override
	public String applyTo(MessageInfo mess){
		// TODO Auto-generated method stub
		if (mess.getFolder()== null) return "?";
		return (String)mess.getFolder().toString();
	}

}
