package gnusmail.datasource;

import gnusmail.filters.FilterManager;

import java.util.Date;

import weka.core.Instance;

public abstract class Document {
	public Instance toWekaInstance(FilterManager fm) {
		return fm.makeInstance(this);		
	}
	
	
	public abstract Date getDate();
	public abstract String getLabel();
	public abstract double getLength();
	public abstract String getText();
	public abstract String getDocId();

}
