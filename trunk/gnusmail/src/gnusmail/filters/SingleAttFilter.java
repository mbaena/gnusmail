package gnusmail.filters;

import gnusmail.core.cnx.MessageInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import javax.mail.MessagingException;

import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;

public abstract class SingleAttFilter extends Filter {

	private Attribute attribute;
	private TreeSet<String> attValues;
	
	public SingleAttFilter() {
		attValues = new TreeSet<String>();
	}
		
	@Override
	public List<Attribute> getAttributes() {
		// New nominal attribute
		FastVector attValuesF = new FastVector();
		System.out.println("Atributo " + getName());
		for (String value: attValues) {
			attValuesF.addElement(value);
		}
		attribute = new Attribute(this.getName(), attValuesF);
		ArrayList<Attribute> attList = new ArrayList<Attribute>();
		attList.add(attribute);
		return attList;
	}

	@Override
	public void updateAttValues(MessageInfo msgInfo) {
		try {
			String value = getSingleValue(msgInfo);
			attValues.add(value);
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
