package gnusmail.filters;

public final class Cc extends Filter {

	@Override
	public String getValueForHeader(String header) {
		String res;
		try {
			res= mess.getCc();
		} catch (Exception e) {
			res= "?";
		}
		if (res.equals("")) res = "None";
		return res;
	}
}
