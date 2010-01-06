package gnusmail.filters;

import gnusmail.core.cnx.MessageInfo;


public final class Folder extends Filter {

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Folder";
	}

	@Override
	public String applyTo(MessageInfo mess) {
		// TODO Auto-generated method stub
		/*if (mess.getFolder() == null) {
			return "?";
		}
		try {
			System.out.println(mess.getFolder().getURLName());
		} catch (MessagingException ex) {
			Logger.getLogger(Folder.class.getName()).log(Level.SEVERE, null, ex);
		}
		try {
			if (!mess.hasAttachments()) {
				System.out.println("..............................");
				mess.print(System.out);
			}
		} catch (IOException ex) {
			Logger.getLogger(Folder.class.getName()).log(Level.SEVERE, null, ex);
		} catch (MessagingException ex) {
			Logger.getLogger(Folder.class.getName()).log(Level.SEVERE, null, ex);
		}
		return (String) mess.getFolder().toString();*/
		return mess.getFolderAsString();
	}
}
