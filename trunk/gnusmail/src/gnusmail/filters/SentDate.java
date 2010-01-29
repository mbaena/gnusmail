package gnusmail.filters;

public final class SentDate extends Filter {

	@Override
	public String getValueForHeader(String header) {
		try {
			return mess.getSentDate();
		} catch (Exception e){
			return "?";
		}
	}
}
