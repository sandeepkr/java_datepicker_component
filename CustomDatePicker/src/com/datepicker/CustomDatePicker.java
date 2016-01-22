package com.datepicker;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * This class is prime layer of the custom datepicker component. 
 * It is the outermost wrapper class for the component.
 * It houses textfield for date display and button for launching date picker
 * 
 * @author Sandeep Kumar
 *
 */
public class CustomDatePicker extends JPanel {
	private static final long serialVersionUID = -2129833407446406625L;
	//name of the custom property for this component; to be used for listening to property changes
	public static final String DATE_CHANGED = "Date Changed";
	Date oldDateValue, newDateValue;
	//instance of datepicker component
	public CustomDatePicker parentPanel;
	//parent container of the component
	private Container container;
	//undecorated frame that contains calendar panel
	private DateFrame dateFrame;
	//other member states
	private Locale dateLocale;
	private Date selectedDate;
	private ObservableDate objDate;
	public SimpleDateFormat dateFormat;
	
	/**
	 * Constructor method for initializing properties.
	 * @param container - parent component that contains the date picker
	 * @param defaultValue - date that will be shown on textfield prior to user selection
	 * @param locale - locale of the date and months and days names
	 * @param selectedFormat- date format for displaying the date in text field
	 */
	public CustomDatePicker(Container container, Date defaultValue, Locale locale,
			SimpleDateFormat selectedFormat) {
		this.container = container;
		this.dateFormat = selectedFormat;
		//creating instance of observable date
		this.objDate = new ObservableDate();
		//textfield with observable date 
		DateTextField textfield = new DateTextField(objDate);
		//adding observer to the textfield
		this.objDate.addObserver(textfield);
		textfield.setColumns(12);
		Font bigFont = textfield.getFont().deriveFont(Font.PLAIN, 15f);
		textfield.setFont(bigFont);
		textfield.setEditable(false);
		this.setSelectedDate(defaultValue == null ? new Date() : defaultValue);
		this.setLocale(locale == null ? Locale.getDefault() : locale);
		this.parentPanel = this;
		FlowLayout layout = (FlowLayout) this.getLayout();
		layout.setHgap(0);
		this.setBackground(new Color(235, 235, 235));
		this.add(textfield);
		JButton button = new JButton(new ImageIcon(CustomDatePicker.class
				.getResource("/4calendar.png")));
		button.setAlignmentX(CENTER_ALIGNMENT);
		button.setAlignmentY(CENTER_ALIGNMENT);
		button.setPreferredSize(new Dimension(25, 23));
		button.setOpaque(false);
		button.setContentAreaFilled(false);
		button.addActionListener(new DateButtonListener());
		add(button);
		//adding listener to move datepicker along with parent container
		DatePickerComponentListener compListener = new DatePickerComponentListener();
		this.container.addComponentListener(compListener);
		setVisible(true);
	}

	/**
	 * setter for dateLocale property
	 * @param locale - value to be set
	 */
	public void setLocale(Locale locale) {
		this.dateLocale = locale;
	}

	/**
	 * getter for dateLocale property
	 * @return - value of the dateLocale property
	 */
	public Locale getLocale() {
		return this.dateLocale;
	}
	
	/**
	 * setter for selectedDate property
	 * @param date - value to be set
	 */
	public void setSelectedDate(Date date) {
		oldDateValue = this.getSelectedDate();
		this.selectedDate = date;
		newDateValue = this.getSelectedDate();
		//update the value of observable date property
		this.objDate.setDisplayStrDate(this.dateFormat
				.format(this.selectedDate));
		//trigger property change for any propertyChangeListeners associated with the component
		firePropertyChange(DATE_CHANGED, oldDateValue, newDateValue);
	}

	public Date getSelectedDate() {
		return this.selectedDate;
	}

	@Override
	public Dimension getPreferredSize() {
		Dimension size = new Dimension(190, 30);
		return size;
	}
	/**
	 * This class is a componentListener to track movement of parent container and adjust placement of datepicker relative to it.
	 * @author Sandeep Kumar
	 *
	 */
	class DatePickerComponentListener implements ComponentListener {

		public void componentHidden(ComponentEvent arg0) {

		}
		
		/**
		 * This method handles displacement of datepicker frame with respect to parent container
		 * @param arg0 - componentEvent that activated the method
		 */
		public void componentMoved(ComponentEvent arg0) {
			//get new location of parentPanel-(textfield button combo)
			Point pt = new Point(parentPanel.getLocation());
			SwingUtilities.convertPointToScreen(pt, parentPanel);
			if (dateFrame != null) {
				//change location of dateFrame 
				dateFrame.setLocation(new Point(pt.x - 5, pt.y + 30));
			}
		}

		public void componentResized(ComponentEvent arg0) {

		}

		public void componentShown(ComponentEvent arg0) {

		}

	}
	
	/**
	 * This class implements actionListener for handling button event of displaying and hiding dateFrame
	 * 
	 * @author Sandeep Kumar
	 *
	 */
	class DateButtonListener implements ActionListener {
		private boolean toggle = true;
		/**
		 * This method hides/displays datePicker's dateFrame on calendar button click
		 * 
		 * @param e - event of button click
		 */
		public void actionPerformed(ActionEvent e) {
			if (dateFrame == null) {
				dateFrame = new DateFrame(parentPanel.getSelectedDate(),
						parentPanel);
			}
			if (!toggle) {
				parentPanel.dateFrame.dispose();
			}
			parentPanel.dateFrame.setVisible(toggle);
			toggle = !toggle;
		}

	}
}
