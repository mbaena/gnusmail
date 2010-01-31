package gnusmail.filters;
import gnusmail.core.cnx.MessageInfo;
import java.util.List;

import weka.core.Attribute;
import weka.core.Instance;

public abstract class Filter {
	/**
	 * Get the name of this filter
	 * @return
	 */
	public String getName() {
		return this.getClass().getSimpleName();
	}

	/**
	 * This function update values for the given instance from messageInfo
	 * @param inst, messageInfo
	 * @return
	 */
	abstract public void updateInstance(Instance inst, MessageInfo messageInfo);

	abstract public List<Attribute> getAttributes();

	abstract public void updateAttValues(MessageInfo msgInfo);
}