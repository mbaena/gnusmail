package gnusmail.filters;
import gnusmail.core.cnx.MessageInfo;
import java.util.ArrayList;
import java.util.List;

public abstract class Filter {
	MessageInfo mess;

	/**
	 * Get the name of this filter
	 * @return
	 */
	public String getName() {
		return this.getClass().getSimpleName();
	}

	/**
	 * Returns a list with the headers associated to this filter
	 * @return
	 */
	public List<String> getAssociatedHeaders() {
		List<String> headers = new ArrayList<String>();
		headers.add(getClass().getSimpleName());
		return headers;
	}

	public void initializeWithMessage(MessageInfo mess) {
		this.mess = mess;
	}

	/**
	 * This function retrieves the value of the given header
	 * @param header
	 * @return
	 */
	abstract public String getValueForHeader(String header);
}