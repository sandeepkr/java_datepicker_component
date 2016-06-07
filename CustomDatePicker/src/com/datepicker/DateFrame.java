package com.datepicker;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Box.Filler;
import javax.swing.border.EmptyBorder;
/**
 * This class provides the undecorated frame that houses components of date picker
 * apart from date textField and datePicker launcher calendar button
 * @author Sandeep Kumar
 *
 */
public class DateFrame extends JFrame implements MouseListener {
	private static final long serialVersionUID = -8688566852320142351L;
	private JLabel monthLabel;
	private JLabel yearLabel;
	protected JButton prevMonthButton;
	protected JButton nextMonthButton;
	private JButton prevYearButton;
	private JButton nextYearButton;
	private DateFrame parentFrame;
	protected NextMonthButtonListener nextMonthListener;
	protected PrevMonthButtonListener prevMonthListener;
	protected Calendar calendar;
	public CustomDatePicker parentPanel;
	private DayPanel dayPanel;
	private int selectedMonth, selectedYear;
	protected int selectedCell = -1;
	protected SimpleDateFormat monthFormat;
	protected SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
	protected SimpleDateFormat dayFormat = new SimpleDateFormat("d");
	public int selectedCellRow = -1, selectedDay;
	public int selectedCellColumn = -1;
	
	/**
	 * Constructor method to instantiate dateFrame
	 * 
	 * @param date - the default date (month) which will be displayed
	 * @param parent - parent datePicker layer that will be bound to this dateFrame
	 */
	public DateFrame(Date date, CustomDatePicker parent) {
		this.parentPanel = parent;
		this.parentFrame = this;
		this.monthFormat = new SimpleDateFormat("MMMM", this.parentPanel
				.getLocale());
		this.calendar = Calendar.getInstance(parent.getLocale());
		getContentPane().setLayout(
				new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		this.parentPanel.setSelectedDate(date);
		this.selectedDay = calendar.get(Calendar.DATE);
		//initialize member states for different components
		initComponents();
	}
	
	//getters and setters
	
	public void setSelectedMonth(int month) {
		this.selectedMonth = month;
	}

	public int getSelectedMonth() {
		return this.selectedMonth;
	}

	public void setSelectedYear(int year) {
		this.selectedYear = year;
	}

	public int getSelectedYear() {
		return this.selectedYear;
	}

	private void initComponents() {
		this.setAlwaysOnTop(isAlwaysOnTop());
		this.setUndecorated(true);
		//add a border
		this.getRootPane().setBorder(
				BorderFactory.createMatteBorder(1, 1, 1, 1, Color.BLACK));
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, -11,
				0));
		//define dimensions of dateFrame's month and year labels row
		buttonPanel.setPreferredSize(new Dimension(190, 30));
		//previous month button properties
		this.prevMonthListener = new PrevMonthButtonListener();
		prevMonthButton = customButton("<", this.prevMonthListener,
				"Previous Month", this);
		buttonPanel.add(prevMonthButton);
		//Month label for displaying name of month
		this.monthLabel = getMonthLabel(this.parentPanel.getSelectedDate());
		this.monthLabel.setBorder(new EmptyBorder(0, 10, 0, 10));
		buttonPanel.add(monthLabel);
		//next month button properties
		this.nextMonthListener = new NextMonthButtonListener();
		nextMonthButton = customButton(">", this.nextMonthListener,
				"Next Month", this);
		buttonPanel.add(nextMonthButton);
		//adding space for handling movement of year buttons due to length of month names
		JLabel space = new JLabel();
		space.setBorder(new EmptyBorder(0, 12, 0, 12));
		buttonPanel.add(space);
		//previous year button properties
		prevYearButton = customButton("<", new PrevYearButtonListener(),
				"Previous Year", this);
		buttonPanel.add(prevYearButton);
		//year label for displaying year in YYYY format 
		this.yearLabel = new JLabel(createYearLabel(this.parentPanel
				.getSelectedDate()));
		this.yearLabel.setBorder(new EmptyBorder(0, 10, 0, 10));
		buttonPanel.add(yearLabel);
		//next year button properties
		nextYearButton = customButton(">", new NextYearButtonListener(),
				"Next Year", this);
		buttonPanel.add(nextYearButton);
		//add month & year buttons to DateFrame pane
		this.getContentPane().add(buttonPanel);
		//add 3 character labels of 7 days to the DateFrame pane
		this.getContentPane().add(createDayHeaderPanel());
		this.dayPanel = new DayPanel(this);
		this.getContentPane().add(dayPanel);
		//add greedy glue to adjust for proper alignment of components within DateFrame
		Box.Filler glue = (Filler) Box.createVerticalGlue();
		glue.changeShape(glue.getMinimumSize(), new Dimension(0,
				Short.MAX_VALUE), // make glue greedy
				glue.getMaximumSize());
		this.add(glue);
		this.pack();
		this.setLocationRelativeTo(this.parentPanel);
		//setting location of dateFrame relative to datePicker for 1st time; later datePicker's componentListener will handle movement
		Point pt = new Point(this.parentPanel.getLocation());
		SwingUtilities.convertPointToScreen(pt, this.parentPanel);
		this.setLocation(new Point(pt.x - 5, pt.y + 30));
	}
	
	/**
	 * This method creates 3 character labels for 7 days as per the locale
	 * It can be further modified to choose first day of week as per locale (different countries have different starting day of week)
	 * @return- JPanel containing day labels in particular locale
	 */
	private JPanel createDayHeaderPanel() {
		/* get the days from the calendar */
		final int firstDayOfWeek = calendar.getFirstDayOfWeek();
		//create a map of all days of week- calendar gives day names as key and day number as value
		final Map<String, Integer> dayIndexes = calendar.getDisplayNames(
				Calendar.DAY_OF_WEEK, Calendar.LONG, parentPanel.getLocale());
		//convert above map to have key as day number and value as day name
		final Map<Integer, String> idxDays = new HashMap<Integer, String>();
		for (final String dayNm : dayIndexes.keySet()) {
			idxDays.put(dayIndexes.get(dayNm), dayNm);
		}
		//create panel that will be returned
		JPanel dayHeaderPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 6,
				0));
		dayHeaderPanel.setPreferredSize(getPreferredSize());
		dayHeaderPanel.setBackground(new Color(249, 249, 249));
		for (int i = 0; i < 7; i++) {
			int dayIdx = (firstDayOfWeek + i) % 7;
			//make first day- sunday ; can be changed to adapt other countries starting day
			if (dayIdx == 0) {
				dayIdx = 7;
			}
			String day = idxDays.get(Integer.valueOf(dayIdx));
			//get 3 character substring from day names
			day = convertFirstUpperChar(day.substring(0, 3));
			final JLabel wd = new JLabel(day);
			wd.setOpaque(false);
			wd.setHorizontalAlignment(SwingConstants.CENTER);
			wd.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
			//add day label to panel
			dayHeaderPanel.add(wd);
		}
		return dayHeaderPanel;
	}
	
	/**
	 * This method makes first character of given string in upperCase
	 * @param s - string to be converted
	 * @return - modified string with upperCase first character
	 */
	public static String convertFirstUpperChar(final String s) {
		return new StringBuilder().append(s.substring(0, 1).toUpperCase())
				.append(s.substring(1)).toString();
	}
	
	/**
	 * This method returns month label after creating it and adjusting its properties
	 * @param date - 
	 * @return - month label
	 */
	private JLabel getMonthLabel(Date date) {
		this.monthLabel = new JLabel(createMonthLabel(date), JLabel.LEFT);
		this.monthLabel.setPreferredSize(new Dimension(85, 25));
		this.monthLabel.setHorizontalAlignment(SwingConstants.CENTER);
		return this.monthLabel;
	}
	
	/**
	 * This method updates label of month
	 * @param date - to determine new month for updating text of month label
	 */
	private void updateMonthLabel(Date date) {
		this.monthLabel.setText(this.createMonthLabel(date));
	}

	/**
	 * This method creates Label for month
	 * @param date - to determine month
	 * @return - label of month name
	 */
	private String createMonthLabel(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		String month = this.monthFormat.format(calendar.getTime());
		return month;
	}
	
	/**
	 * This method creates year label
	 * @param date - to determine the year
	 * @return - year label
	 */
	private String createYearLabel(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		String year = this.yearFormat.format(calendar.getTime());
		return year;
	}
	
	/**
	 * This method updates label of year
	 * @param date - to determine new year for updating text of year label
	 */
	public void updateYearLabel(Date date) {
		this.yearLabel.setText(createYearLabel(date));
	}

	/**
	 * This method creates custom prev and next buttons 
	 * @param text - text to display on button
	 * @param listener- actionListener for the button
	 * @param toolTipText - on hover text to be displayed; can be modified to handle different locale
	 * @param frame - frame to add mouse listener to change color of button on hover
	 * @return
	 */
	public static JButton customButton(String text, ActionListener listener,
			String toolTipText, DateFrame frame) {
		JButton button = new JButton(text);
		button.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
		button.setFocusPainted(false);
		button.setOpaque(false);
		button.setContentAreaFilled(false);
		button.addMouseListener(frame);
		button.addActionListener(listener);
		button.setBorderPainted(false);
		button.setToolTipText(toolTipText);
		button.setMargin(new Insets(2, 2, 2, 2));
		return button;
	}

	@Override
	public Dimension getPreferredSize() {
		// set up size for dayHeaderPanel
		Dimension size = new Dimension(200, 205);
		return size;
	}

	/**
	 * This class implements actionListener for previous month button
	 * @author Sandeep Kumar
	 *
	 */
	class PrevMonthButtonListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			parentFrame.calendar.setTime(parentFrame.parentPanel
					.getSelectedDate());
			int currentMonth = parentFrame.calendar.get(Calendar.MONTH);
			if (currentMonth == 0) {
				parentFrame.prevYearButton.doClick();
			}
			parentFrame.calendar.add(Calendar.MONTH, -1);
			parentFrame.setSelectedMonth(parentFrame.calendar
					.get(Calendar.MONTH));
			parentFrame.updateMonthLabel(parentFrame.calendar.getTime());
			//if prev month button activates the method, date will be same
			//otherwise it was activated by selection of prev month date on dayPanel
			if(parentFrame.selectedDay != parentFrame.calendar.get(Calendar.DAY_OF_MONTH)) {
				parentFrame.calendar.set(parentFrame.getSelectedYear(), parentFrame.getSelectedMonth(), parentFrame.selectedDay);
			}
			parentFrame.parentPanel.setSelectedDate(parentFrame.calendar
					.getTime());
			parentFrame.dayPanel.updateDayPanel(parentFrame.parentPanel
					.getSelectedDate());
		}

	}
	
	/**
	 * This class implements actionListener for next month button
	 * @author Sandeep Kumar
	 *
	 */
	class NextMonthButtonListener implements ActionListener {

		public void actionPerformed(ActionEvent arg0) {
			parentFrame.calendar.setTime(parentFrame.parentPanel
					.getSelectedDate());
			int currentMonth = parentFrame.calendar.get(Calendar.MONTH);
			if (currentMonth == 11) {
				parentFrame.nextYearButton.doClick();
			}
			parentFrame.calendar.add(Calendar.MONTH, +1);
			parentFrame.setSelectedMonth(parentFrame.calendar
					.get(Calendar.MONTH));
			parentFrame.updateMonthLabel(parentFrame.calendar.getTime());
			//if next month button activates the method, date will be same
			//otherwise it was activated by selection of next month date on dayPanel
			if(parentFrame.selectedDay != parentFrame.calendar.get(Calendar.DAY_OF_MONTH)) {
				parentFrame.calendar.set(parentFrame.getSelectedYear(), parentFrame.getSelectedMonth(), parentFrame.selectedDay);
			}
			parentFrame.parentPanel.setSelectedDate(parentFrame.calendar
					.getTime());
			parentFrame.dayPanel.updateDayPanel(parentFrame.parentPanel
					.getSelectedDate());
		}

	}
	
	/**
	 * This class implements actionListener for previous year button
	 * @author Sandeep Kumar
	 *
	 */
	class PrevYearButtonListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			parentFrame.calendar.setTime(parentFrame.parentPanel
					.getSelectedDate());
			parentFrame.calendar.add(Calendar.YEAR, -1);
			parentFrame
					.setSelectedYear(parentFrame.calendar.get(Calendar.YEAR));
			parentFrame.parentPanel.setSelectedDate(parentFrame.calendar
					.getTime());
			parentFrame.updateYearLabel(parentFrame.calendar.getTime());
			parentFrame.dayPanel.updateDayPanel(parentFrame.parentPanel
					.getSelectedDate());
		}

	}
	
	/**
	 * This class implements actionListener for next year button
	 * @author Sandeep Kumar
	 *
	 */
	class NextYearButtonListener implements ActionListener {

		public void actionPerformed(ActionEvent arg0) {
			parentFrame.calendar.setTime(parentFrame.parentPanel
					.getSelectedDate());
			parentFrame.calendar.add(Calendar.YEAR, +1);
			parentFrame
					.setSelectedYear(parentFrame.calendar.get(Calendar.YEAR));
			parentFrame.parentPanel.setSelectedDate(parentFrame.calendar
					.getTime());
			parentFrame.updateYearLabel(parentFrame.calendar.getTime());
			parentFrame.dayPanel.updateDayPanel(parentFrame.parentPanel
					.getSelectedDate());
		}

	}

	public void mouseClicked(MouseEvent arg0) {

	}
	
	/**
	 * This method handles hover effect on mouse enter on prev & next buttons
	 * @param e - event of Mouse enter
	 */
	public void mouseEntered(MouseEvent e) {
		Object source = e.getSource();
		if (source instanceof JButton) {
			JButton btn = (JButton) source;
			if (btn == prevMonthButton || btn == nextMonthButton
					|| btn == prevYearButton || btn == nextYearButton) {
				btn.setForeground(Color.GRAY);
			}
		}
	}
	
	/**
	 * This method handles hover effect on mouse exit on prev & next buttons
	 * @param e - event of mouse exit
	 */
	public void mouseExited(MouseEvent e) {
		Object source = e.getSource();
		if (source instanceof JButton) {
			JButton btn = (JButton) source;
			if (btn == prevMonthButton || btn == nextMonthButton
					|| btn == prevYearButton || btn == nextYearButton) {
				btn.setForeground(Color.BLACK);
			}
		}
	}

	public void mousePressed(MouseEvent arg0) {

	}


	public void mouseReleased(MouseEvent arg0) {

	}

}
