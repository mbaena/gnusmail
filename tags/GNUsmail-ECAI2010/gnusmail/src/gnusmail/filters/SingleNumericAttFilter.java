package gnusmail.filters;

import gnusmail.core.cnx.MessageInfo;

import java.util.ArrayList;
import java.util.List;
import javax.mail.MessagingException;
import weka.core.Attribute;
import weka.core.Instance;

public abstract class SingleNumericAttFilter extends Filter {

	private Attribute attribute;
	
	public SingleNumericAttFilter() {
		attribute = new Attribute(this.getName());
	}
		
	@Override
	public List<Attribute> getAttributes() {
		ArrayList<Attribute> attList = new ArrayList<Attribute>();
		attList.add(attribute);
		return attList;
	}

	@Override
	public void updateAttValues(MessageInfo msgInfo) {
	}

	@Override
	public void updateInstance(Instance inst, MessageInfo messageInfo) {
		try {
			inst.setValue(attribute, getSingleValue(messageInfo));
		} catch (MessagingException e) {
			inst.setMissing(attribute);
		}
	}
	
	protected abstract double getSingleValue(MessageInfo messageInfo) throws MessagingException;


}
