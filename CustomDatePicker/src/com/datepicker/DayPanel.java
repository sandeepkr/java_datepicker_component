package com.datepicker;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.MatteBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

/**
 * This class forms the core of datePicker component as it lays out the date numbers for each month in a grid inside DateFrame
 * It extends box as it helps in handling JTable better
 * @author Sandeep Kumar
 *
 */
public class DayPanel extends Box {
	private static final long serialVersionUID = 8234529413859809327L;
	private Calendar cal;
	//parent DateFrame which will contain this dayPanel grid
	private DateFrame dateFrame;
	//to be used for creating date grid
	private JTable dayTable;
	int totalDays, prevCounter, nextCounter;
	//7 days in a week so column count is 7
	final int COLUMN_COUNT = 7;
	
	/**
	 * Constructor method to instantiate the dayPanel grid
	 * @param dateFrame - the parent dateFrame which will contain this panel
	 */
	public DayPanel(DateFrame dateFrame) {
		super(BoxLayout.Y_AXIS);
		this.dateFrame = dateFrame;
		this.setBackground(new Color(253, 253, 253));
		//creating a JTable of 6x7 cells 
		this.dayTable = new JTable(6, 7) {
			private static final long serialVersionUID = 1L;
			// making the cells non-editable
			public boolean isCellEditable(int row, int column) {
				return false;
			};
		};
		
		// setting up JTable properties
		dayTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		dayTable.setRowHeight(26);
		dayTable.getColumnModel().getColumn(0).setPreferredWidth(25);
		dayTable.getColumnModel().getColumn(1).setPreferredWidth(25);
		dayTable.getColumnModel().getColumn(2).setPreferredWidth(25);
		dayTable.getColumnModel().getColumn(3).setPreferredWidth(25);
		dayTable.getColumnModel().getColumn(4).setPreferredWidth(25);
		dayTable.getColumnModel().getColumn(5).setPreferredWidth(25);
		dayTable.getColumnModel().getColumn(6).setPreferredWidth(25);
		dayTable.setIntercellSpacing(new Dimension(0, 0));
		MatteBorder border = new MatteBorder(1, 1, 1, 1, new Color(211, 211,
				211));
		dayTable.setBorder(border);
		dayTable.setGridColor(new Color(211, 211, 211));
		dayTable.setTableHeader(null);
		//enabling selection of 1 cell at a time
		dayTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		dayTable.setCellSelectionEnabled(true);
		//set background color for the selected cell
		dayTable.setSelectionBackground(new Color(0, 150, 201));
		dayTable.setSelectionForeground(Color.WHITE);
		dayTable.setFont(new Font("Verdana", Font.PLAIN, 12));
		//add listener to JTable selectionModel to handle navigation of month & year if next/prev month dates are selected
		ListSelectionModel cellSelectionModel = dayTable.getSelectionModel();
		cellSelectionModel
				.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		DayPanelSelectionListener listener = new DayPanelSelectionListener();
		//add listeners to handle whenever a selection is made;same listener to get intersection of row and column
		//listSelectionListener will handle selection made on different row
		cellSelectionModel.addListSelectionListener(listener);
		//columnModelListener will handle selection made on different column
		dayTable.getColumnModel().addColumnModelListener(listener);
		//create custom renderer for JTable
		CustomRenderer cr = new CustomRenderer(dayTable
				.getDefaultRenderer(Object.class), new Color(211, 211, 211),
				new Color(211, 211, 211), new Color(211, 211, 211), new Color(
						211, 211, 211));
		//set the custom renderer to be default renderer for JTable
		dayTable.setDefaultRenderer(Object.class, cr);
		//create dayPanel with the current date passed to datePicker component
		this.createDayPanel(this.dateFrame.parentPanel.getSelectedDate());
		this.add(dayTable);

	}

	/**
	 * This class provides Listener for handling cell selection
	 * 
	 * @author Sandeep Kumar
	 *
	 */
	class DayPanelSelectionListener implements ListSelectionListener,
			TableColumnModelListener {
		
		/**
		 * This is method is fired whenever the row selection is changed
		 * @param event - the ListSelectionEvent source activating the row selection
		 */
		public void valueChanged(ListSelectionEvent event) {
			if (!event.getValueIsAdjusting()) {
				//check if the event source is correct and index of the row whose selection may have changed is also valid(>=0)
				if (event.getSource() == dayTable.getSelectionModel()
						&& event.getFirstIndex() >= 0) {
					if (dateFrame.selectedCellRow != dayTable.getSelectedRow()) {
						//update the date value only if new selected row is different from previously selected row
						updateValue((TableModel) dayTable.getModel());
					}
				}
			}
		}

		/**
		 * This is common method that updates the value as per change in selection of either row or column
		 * @param model - the JTable model
		 */
		private void updateValue(TableModel model) {
			// Determine the selected item
			//check if either the selected row has changed or selected column has changed
			if (dateFrame.selectedCellColumn != dayTable.getSelectedColumn()
					|| dateFrame.selectedCellRow != dayTable.getSelectedRow()) {
				//update the current selected cell row and column values
				dateFrame.selectedCellRow = dayTable.getSelectedRow();
				dateFrame.selectedCellColumn = dayTable.getSelectedColumn();
				//update selectedDay by fetching value from model using selectedCellRow and selectedCellColumn
				dateFrame.selectedDay = (Integer) model
						.getValueAt(dateFrame.selectedCellRow,
								dateFrame.selectedCellColumn);
			}
			//if selected cell date belongs to current rendered month
			if(getCounter(dateFrame.selectedCellRow,
					dateFrame.selectedCellColumn) >= prevCounter && getCounter(dateFrame.selectedCellRow,
					dateFrame.selectedCellColumn) < nextCounter) {
				cal = Calendar.getInstance();
				int year = dateFrame.getSelectedYear();
				cal.set(year, dateFrame.getSelectedMonth() - 1,
						dateFrame.selectedDay, 0, 0);
				//update the selected date
				dateFrame.parentPanel.setSelectedDate(cal.getTime());
			}
			//if cell clicked actually refers to date of prev month
			else if (getCounter(dateFrame.selectedCellRow,
					dateFrame.selectedCellColumn) < prevCounter) {
				dateFrame.prevMonthButton.doClick();
			} 
			//if cell clicked actually refers to date of next month
			else if (getCounter(dateFrame.selectedCellRow,
					dateFrame.selectedCellColumn) >= nextCounter) {
				dateFrame.nextMonthButton.doClick();
			}
		}

		public void columnAdded(TableColumnModelEvent arg0) {

		}

		public void columnMarginChanged(ChangeEvent arg0) {

		}

		public void columnMoved(TableColumnModelEvent arg0) {

		}

		public void columnRemoved(TableColumnModelEvent arg0) {

		}
		
		/**
		 * This is method is fired whenever the column selection is changed
		 * @param event - the ListSelectionEvent source activating the column selection
		 */
		public void columnSelectionChanged(ListSelectionEvent event) {
			if (!event.getValueIsAdjusting()) {
				//check if the event source is correct and index of the row whose selection may have changed is also valid(>=0)
				if (event.getSource() == dayTable.getColumnModel()
						.getSelectionModel()
						&& event.getFirstIndex() >= 0) {
					if (dateFrame.selectedCellColumn != dayTable
							.getSelectedColumn()) {
						//update the date value only if new selected column is different from previously selected column
						updateValue((TableModel) dayTable.getModel());
					}
				}
			}

		}

	}

	/**
	 * This method calculates one counter variable for given row and column value
	 * @param row - row index value
	 * @param column - column index value
	 * @return - counter variable
	 */
	public int getCounter(int row, int column) {
		return row * COLUMN_COUNT + column;
	}

	/**
	 * This method updates cellCounters whenever current month changes
	 * prevCounter indicates how many prev month days; through cellCounter till which previous month dates are to be rendered
	 * nextCounter indicates how many next month days; through cellCounter from which next month dates are to be rendered
	 * @param date - to determine new month which is to be rendered
	 */
	public void updateCellCounters(Date date) {
		if (!this.dateFrame.parentPanel.getSelectedDate().equals(date)) {
			cal = Calendar.getInstance();
			cal.setTime(date);
			cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE));
			//get total number of dates of this month to be rendered
			totalDays = cal.get(Calendar.DATE);
			cal.set(Calendar.DAY_OF_MONTH, cal
					.getActualMinimum(Calendar.DAY_OF_MONTH));
			//find which day is 1st of this month 
			int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK); 
			// if sunday is starting day of month then a whole row of previous month to be rendered; hence prevCounter=7 else remaining days
			prevCounter = (dayOfWeek - 1) == 0 ? 7 : dayOfWeek - 1;
			//next month rendering to begin after prevMonth dates and current month dates
			nextCounter = totalDays + prevCounter;
		}
	}

	/**
	 * This class is to implement custom tableCellRenderer to alter the rendering routine of JTable
	 * Aim is to change background of different date cell types: current/prev/next month cells
	 * Also to change font color of weekend/weekday date cells
	 * 
	 * @author Sandeep Kumar
	 *
	 */
	class CustomRenderer implements TableCellRenderer {
		TableCellRenderer render;
		Border b;
		int cellCounter = 1;
		Calendar c;
		
		/**
		 * Constructor method to instantiate cellRenderer member states
		 * @param r - the rendering instance
		 * @param top - color for top border of cell
		 * @param left - color for left border of cell
		 * @param bottom - color for bottom border of cell
		 * @param right - color for right border of cell
		 */
		public CustomRenderer(TableCellRenderer r, Color top, Color left,
				Color bottom, Color right) {
			render = r;
			setOpaque(false);
			b = BorderFactory.createCompoundBorder();
			b = BorderFactory.createCompoundBorder(b, BorderFactory
					.createMatteBorder(2, 0, 0, 0, top));
			b = BorderFactory.createCompoundBorder(b, BorderFactory
					.createMatteBorder(0, 2, 0, 0, left));
			b = BorderFactory.createCompoundBorder(b, BorderFactory
					.createMatteBorder(0, 0, 2, 0, bottom));
			b = BorderFactory.createCompoundBorder(b, BorderFactory
					.createMatteBorder(0, 0, 0, 2, right));
		}

		/**
		 * This method is called for rendering cells of table first time as well as when any selection change of cells happen
		 * @param table - parent table whose cells rendering it is to handle
		 * @param value - value assigned to the cell at specified position(rows and columns) which is being rendered
		 * @param isSelected - when any cell is selected, it becomes true
		 * @param hasFocus - if the cell is focused, it becomes true
		 * @param row - the row index of cell being drawn
		 * @param column - the column index of cell being drawn
		 */
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			//fetch the cell component that is to be rendered
			JLabel cell = (JLabel) render.getTableCellRendererComponent(table,
					value, isSelected, hasFocus, row, column);
			cell.setHorizontalAlignment(SwingConstants.CENTER);
			Border border = null;
			//calculate cellCounter for current row and column; it determines if the cell is in current, prev or next month
			cellCounter = row * COLUMN_COUNT + column;
			if (isSelected && hasFocus) {
				//add a border to the selected and focused cell
				border = b;
			}
			else {
				//if cell is not selected then border is just an empty one
				border = BorderFactory.createEmptyBorder(0, 0, 0, 0);
			}
			//add border to the cell
			cell.setBorder(border);
			if (!isSelected && !hasFocus) {
				//if the date cell is not selected then render with common table background
				cell.setBackground(UIManager.getColor("Table.background"));
				//grey out cells which does not belong to dates of this month
				if ((cellCounter < prevCounter) || (cellCounter >= nextCounter)) {
					cell.setForeground(Color.LIGHT_GRAY);
				} else {
					//mark first and last column dates as red for indicating weekends
					if (column == 0 || column == 6) {
						cell.setForeground(Color.RED);
					} else {
						//mark dates black for current month weekdays
						cell.setForeground(Color.BLACK);
					}
				}
				cellCounter++;
				if (cellCounter > 42) {
					//cell renderer is triggered whenever change occurs so reset cellCounter if while rendering it reaches 42(total no of cells)
					cellCounter = 1;
				}

			}

			return cell;
		}
	}
	
	/**
	 * This method is common point method which receives notification from prev/next buttons to update DayPanel dates 
	 * @param date- the date which will fetch days of the month to be arranged in dayPanel
	 */
	public void updateDayPanel(Date date) {
		createDayPanel(date);
	}
	
	/**
	 * This method does calculation of dates from selected date,month and year to decide cell values to be assigned
	 * @param date - the current selected date
	 */
	private void createDayPanel(Date date) {
		cal = Calendar.getInstance();
		//clear calendar instance because of bug in java 7 Calendar API
		cal.clear();
		cal.setTime(date);
		//for finding current month dates to be assigned to cells: prevCounter till nextCounter
		dateFrame.setSelectedYear(cal.get(Calendar.YEAR));
		dateFrame.setSelectedMonth(cal.get(Calendar.MONTH) + 1);
		cal.set(Calendar.DAY_OF_MONTH, cal
				.getActualMaximum(Calendar.DAY_OF_MONTH));
		int currentMonthMaxDate = cal.get(Calendar.DAY_OF_MONTH);
		cal.set(Calendar.DAY_OF_MONTH, cal
				.getActualMinimum(Calendar.DAY_OF_MONTH));
		//update cell counters
		updateCellCounters(cal.getTime());
		//for finding previous month dates to be assigned to cells; 1 to prevCounter cells
		Calendar cal1 = Calendar.getInstance();
		cal1.clear();
		int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
		cal1.setTime(date);
		cal1.set(Calendar.MONTH, cal.get(Calendar.MONTH) - 1);
		cal1.set(Calendar.DATE, cal1.getActualMaximum(Calendar.DATE));
		//find previous month max date
		int prevMonthMaxDate = cal1.get(Calendar.DAY_OF_MONTH);
		//Calendar.DAY_OF_WEEK starts with 1 so subtracting 7 days from prev month max date if current month begins on 1st day of week
		//else subtracting dayOFWeek to find beginning date for cells upto prevCounter
		cal1.add(Calendar.DATE, -((dayOfWeek == 1) ? 8 : dayOfWeek) + 1);
		int prevMonthBegDate = cal1.get(Calendar.DAY_OF_MONTH) + 1;
		//variable to handle setting prev month date values
		boolean isPrevMonthDate = true;
		int value = 0, occurence = 0, prevDateWrittenCount = 0;
		// handling scenario where last_date_of_to_be_displayed_month <
		// selected_date_of_currently_displaying_month
		if (dateFrame.selectedDay > currentMonthMaxDate) {
			dateFrame.selectedDay = currentMonthMaxDate;
		}
		for (int week = 0; week < 6; week++) {
			for (int day = 0; day < 7; day++) {
				//prev month date values setup
				if (isPrevMonthDate) {
					//if 1st day of current month is sunday then setup 7 prev month dates
					if (dayOfWeek == 1) {
						cal1.add(Calendar.DATE, 1);
						value = cal1.get(Calendar.DAY_OF_MONTH);
						prevDateWrittenCount++;
						if (prevDateWrittenCount == 8) {
							value = cal.get(Calendar.DAY_OF_MONTH);
							//maximum of 7 dates of previous month only can be setup; hence disabling boolean variable
							isPrevMonthDate = !isPrevMonthDate;
						}
					} else {
						if (day + 1 == dayOfWeek) {
							value = cal.get(Calendar.DAY_OF_MONTH);
							//disable previous month date setup if the 1st day of current month has come
							isPrevMonthDate = !isPrevMonthDate;
						} else if (day + 1 < dayOfWeek) {
							// continue previous month date setup if 1st day of current month has not come yet
							cal1.add(Calendar.DATE, 1);
							value = cal1.get(Calendar.DAY_OF_MONTH);
						} else {
							dayTable.setValueAt("", week, day);
						}
					}
				} 
				//current month date values setup
				else {
					cal.add(Calendar.DATE, 1);
					value = cal.get(Calendar.DAY_OF_MONTH);
				}
				//handling case when selectedDay occurs twice in the dayPanel;ensure only correct cell is highlighted in rendering
				//eg., if 2nd of a month is selected then it can occur in current month as well as next month date setup
				if ((value == dateFrame.selectedDay)) {
					//increase the occurrence counter
					occurence++;
					// prev/next month date will never be greater than 15 as 42
					// cells present(min days:28 for any month)
					//occurence will be 1 if value> 15 and (value> prev month beginning date rendered OR greater than max date of prev month)
					//for march value can be greater than max date of prev month feb
					//in all other case occurence will be 2 if value >15
					//if value<15 and occurence is 1 that means this date is in current month and hence the correct cell to be selected
					if ((value > 15 && occurence == (prevMonthBegDate > dateFrame.selectedDay
							|| dateFrame.selectedDay > prevMonthMaxDate ? 1 : 2))
							|| (value < 15 && occurence == 1)) {
						// set the selectedCellRow and selectedCellColumn variables that determine cellCounter state
						dateFrame.selectedCellRow = week;
						dateFrame.selectedCellColumn = day;
						//clear previous selection and ensure new cell is selected
						dayTable.changeSelection(week, day, false, false);
					}

				}
				// add date value to respective cell in dayPanel JTable
				dayTable.setValueAt(value, week, day);
			}
		}

	}

}