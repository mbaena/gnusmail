package gnusmail.filters;

public final class MessageId extends Filter {

	@Override
	public String getValueForHeader(String header) {
		return mess.getMessageId();
	}



}
