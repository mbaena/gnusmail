package gnusmail.filters;

public final class ReplyTo extends Filter {

	public String getValueForHeader(String header) {
		try{
			return mess.getReplyTo();
		} catch (Exception e){
			return "?";
		}
		
	}
}
