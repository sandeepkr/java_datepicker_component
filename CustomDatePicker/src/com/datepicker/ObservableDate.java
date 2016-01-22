package com.datepicker;

import java.util.Observable;
import java.util.Observer;

import javax.swing.JTextField;

/**
 * Provides observable date property. This is to define the property that will
 * be observed by different observers
 * 
 * @author Sandeep Kumar
 * 
 */
public class ObservableDate extends Observable {
	private String displayStrDate;

	public String getDisplayStrDate() {
		return displayStrDate;
	}
	/**
	 * This method updates the observable date property and notifies all observers about the change
	 * @param displayDate - new value of the observable date property
	 */
	public void setDisplayStrDate(String displayDate) {
		this.displayStrDate = displayDate;
		setChanged();
		notifyObservers(this.displayStrDate);
	}
}

/**
 * This class provides an observer textField which will observe the observable
 * date property
 * 
 * @author Sandeep Kumar
 * 
 */
class DateTextField extends JTextField implements Observer {
	private static final long serialVersionUID = -9121215994812342536L;
	private ObservableDate observedObj = null;

	public DateTextField(ObservableDate obj) {
		//setting the observable property instance so that it can be checked for validity on update
		this.observedObj = obj;
	}

	/**
	 * This method updates the textfield value whenever observable date property
	 *@param obs - the observable property
	 *@param arg - the new value of the property
	 */
	public void update(Observable obs, Object arg) {
		//checking if the trigger is observable date only and not some other property
		if (obs == this.observedObj) {
			String strDate = (String) arg;
			setText(strDate);
		}
	}
}