package gnusmail.filters;

import gnusmail.core.cnx.MessageInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.mail.MessagingException;

import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.ProtectedProperties;
import weka.core.converters.ArffLoader;

public abstract class SingleAttFilter extends Filter {

	private Attribute attribute;
	private FastVector attValues;
	
	public SingleAttFilter() {
		attValues = new FastVector();
	}
		
	@Override
	public List<Attribute> getAttributes() {
		// New nominal attribute
		attribute = new Attribute(this.getName(), new FastVector(), new ProtectedProperties(new Properties()));
		ArrayList<Attribute> attList = new ArrayList<Attribute>();
		attList.add(attribute);
		return attList;
	}

	@Override
	public void updateAttValues(MessageInfo msgInfo) {
		try {
			attValues.addElement(getSingleValue(msgInfo));
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void updateInstance(Instance inst, MessageInfo messageInfo) {
		try {
			int index = attribute.indexOfValue(getSingleValue(messageInfo));
			inst.setValue(attribute, index);
		} catch (MessagingException e) {
			inst.setMissing(attribute);
		}
	}
	
	protected abstract String getSingleValue(MessageInfo messageInfo) throws MessagingException;
	
}
