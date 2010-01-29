package gnusmail.filters;

public final class Attachments extends Filter {
	@Override
	public String getValueForHeader(String header) {
		try {
			if (mess.hasAttachments()) return "True";
			return "False";
		} catch (Exception e){
			return "?";
		}
	}

}
