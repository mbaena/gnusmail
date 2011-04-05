/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gnusmail;

import javax.mail.Message;

/**
 *
 * @author jmcarmona
 */
public class SortableMessage implements Comparable<SortableMessage> {

	Message mess;

	public SortableMessage(Message mess) {
		this.mess = mess;
	}

	public Message getMesssage() {
		return mess;
	}

	public int compareTo(SortableMessage arg0) {
		int res = 0;
		try {
			res = this.mess.getSentDate().compareTo(arg0.getMesssage().getSentDate());
		} catch (Exception ex) {
			
		}
		return res;
	}
}
