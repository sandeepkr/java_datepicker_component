package com.datepicker;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * This is a demo class that provides an example of how to use the custom date
 * picker component
 * 
 * @author Sandeep Kumar
 * 
 */

public class CustomDatePickerDemo {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// to allow GUI updates to be done in the event dispatch thread
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JFrame frame = new JFrame("DatePicker Demo ");
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				Calendar cal = Calendar.getInstance();
				cal.add(Calendar.MONTH, 2);
				Date date = cal.getTime();
				CustomDatePicker datePicker = new CustomDatePicker(frame, date,
						Locale.US,
						new SimpleDateFormat("dd/MM/yyyy", Locale.US));
				//adding propertyChangeListener to datepicker to get latest value of selected date
				datePicker.addPropertyChangeListener(
						CustomDatePicker.DATE_CHANGED,
						new PropertyChangeListener() {

							@Override
							public void propertyChange(PropertyChangeEvent evt) {
								Date currentDateValue = (Date) evt
										.getNewValue();
								System.out.println("hello! date selected is: "
										+ new SimpleDateFormat("dd/MM/yyyy",
												Locale.US)
												.format((currentDateValue)));
							}

						});
				frame.getContentPane().add(BorderLayout.CENTER, datePicker);
				frame.pack();
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
			}
		});

	}
}
