//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// This file is a part of the 'gewt' project.
// Copyright 2016 Elmar Sonnenschein, esoco GmbH, Flensburg, Germany
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//	  http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
package de.esoco.ewt.impl.gwt;

import de.esoco.ewt.UserInterfaceContext;

import de.esoco.lib.property.DateAttribute;

import java.util.Date;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTMLTable.CellFormatter;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.datepicker.client.CalendarModel;
import com.google.gwt.user.datepicker.client.DateBox;
import com.google.gwt.user.datepicker.client.DatePicker;
import com.google.gwt.user.datepicker.client.DefaultCalendarView;
import com.google.gwt.user.datepicker.client.MonthSelector;

/**
 * A {@link DatePicker} subclass that uses an extended {@link MonthSelector}
 * with functionality like year selection and time input.
 *
 * @author e.sonnenschein
 */
public class GwtDatePicker extends DatePicker implements DateAttribute {

	private static final GewtResources RES = GewtResources.INSTANCE;

	private static final GewtCss CSS = RES.css();

	private MonthAndTimeSelector monthAndTimeSelector;

	private DateBox dateBox;

	/**
	 * Creates a new instance.
	 *
	 * @param context  The user interface context
	 * @param dateTime TRUE to pick date and time, FALSE for date only
	 */
	public GwtDatePicker(UserInterfaceContext context, boolean dateTime) {
		super(new MonthAndTimeSelector(context, dateTime),
			new DefaultCalendarView(), new CalendarModel());

		monthAndTimeSelector = (MonthAndTimeSelector) getMonthSelector();
		monthAndTimeSelector.setDatePicker(this);

		GwtTimePicker timePicker = monthAndTimeSelector.getTimePicker();

		if (timePicker != null) {
			timePicker.addValueChangeHandler(new ValueChangeHandler<Integer>() {
				@Override
				public void onValueChange(ValueChangeEvent<Integer> event) {
					timePickerValueChanged(event);
				}
			});
		}

		setYearAndMonthDropdownVisible(true);
		setYearArrowsVisible(true);
	}

	/**
	 * Returns the current date of this instance. If it has been created in
	 * date
	 * and time mode the result will also contain the selected time.
	 *
	 * @return The date
	 */
	@Override
	@SuppressWarnings("deprecation")
	public Date getDate() {
		Date date = getValue();

		if (date != null) {
			// DatePicker returns 12:00:00 ?!
			date.setHours(0);
			date.setMinutes(0);
			date.setSeconds(0);
		}

		return monthAndTimeSelector.applyTime(date);
	}

	/**
	 * Sets the date.
	 *
	 * @param date The new date
	 */
	@Override
	public void setDate(Date date) {
		if (date != null) {
			setCurrentMonth(date);
		}

		monthAndTimeSelector.setTime(date);
		setValue(date);
	}

	/**
	 * If this instance has a time picker this method can be used to set a date
	 * box that needs to be updated if the time value has changed. If not set a
	 * {@link ValueChangeEvent} will be fired instead.
	 *
	 * @param dateBox The dateBox value
	 */
	public final void setDateBox(DateBox dateBox) {
		this.dateBox = dateBox;
	}

	/**
	 * Handles a {@link ValueChangeEvent} from a {@link GwtTimePicker}.
	 *
	 * @param event The event that occurred
	 */
	void timePickerValueChanged(ValueChangeEvent<Integer> event) {
		if (dateBox == null) {
			ValueChangeEvent.fire(GwtDatePicker.this, getDate());
		} else {
			dateBox.setValue(getDate(), true);
		}
	}

	/**
	 * A {@link MonthSelector} subclass with extended functionality like year
	 * selection and (optional) time input.
	 */

	static class MonthAndTimeSelector extends MonthSelector {

		private static final int COL_PREV_YEAR = 0;

		private static final int COL_PREV_MONTH = 1;

		private static final int COL_MONTH = 2;

		private static final int COL_NEXT_MONTH = 3;

		private static final int COL_NEXT_YEAR = 4;

		private GwtDatePicker datePicker;

		private GwtTimePicker timePicker;

		private Label monthLabel = new Label();

		private PushButton prevMonthButton = new PushButton();

		private PushButton nextMonthButton = new PushButton();

		private PushButton prevYearButton = new PushButton();

		private PushButton nextYearButton = new PushButton();

		/**
		 * Creates a new instance.
		 *
		 * @param context  The user interface context
		 * @param withTime TRUE for month and time selection,
		 */
		public MonthAndTimeSelector(UserInterfaceContext context,
			boolean withTime) {
			if (withTime) {
				timePicker = new GwtTimePicker(context);
			}
		}

		/**
		 * @see MonthSelector#getModel()
		 */
		@Override
		public CalendarModel getModel() {
			return datePicker != null ? datePicker.getModel() : null;
		}

		/**
		 * Overridden to provide access to click handler.
		 *
		 * @see MonthSelector#addMonths(int)
		 */
		@Override
		protected void addMonths(int monthAdd) {
			super.addMonths(monthAdd);
		}

		/**
		 * @see MonthSelector#getDatePicker()
		 */
		@Override
		protected DatePicker getDatePicker() {
			return datePicker;
		}

		/**
		 * Returns the time picker component or NULL if none exits.
		 *
		 * @return The time picker component
		 */
		protected GwtTimePicker getTimePicker() {
			return timePicker;
		}

		/**
		 * @see MonthSelector#refresh()
		 */
		@Override
		protected void refresh() {
			if (datePicker != null) {
				monthLabel.setText(getModel().formatCurrentMonthAndYear());
			}
		}

		/**
		 * @see MonthSelector#setup()
		 */
		@Override
		protected void setup() {
			prevMonthButton.getUpFace().setHTML("&lsaquo;");
			prevMonthButton.setStyleName("datePickerPreviousButton");
			prevMonthButton.addClickHandler(new MonthChangeClickHandler(-1));

			nextMonthButton.getUpFace().setHTML("&rsaquo;");
			nextMonthButton.setStyleName("datePickerNextButton");
			nextMonthButton.addClickHandler(new MonthChangeClickHandler(1));

			prevYearButton.getUpFace().setHTML("&laquo;");
			prevYearButton.setStyleName("datePickerPreviousButton");
			prevYearButton.addClickHandler(new MonthChangeClickHandler(-12));

			nextYearButton.getUpFace().setHTML("&raquo;");
			nextYearButton.setStyleName("datePickerNextButton");
			nextYearButton.addClickHandler(new MonthChangeClickHandler(12));

			monthLabel.setStyleName("datePickerMonth");
			monthLabel.addDoubleClickHandler(new DoubleClickHandler() {
				@Override
				public void onDoubleClick(DoubleClickEvent event) {
					resetDate();
				}
			});

			Grid grid = new Grid(1, 5);

			grid.setStyleName(CSS.datePickerMonthSelector());
			grid.setWidget(0, COL_PREV_YEAR, prevYearButton);
			grid.setWidget(0, COL_PREV_MONTH, prevMonthButton);
			grid.setWidget(0, COL_MONTH, monthLabel);
			grid.setWidget(0, COL_NEXT_MONTH, nextMonthButton);
			grid.setWidget(0, COL_NEXT_YEAR, nextYearButton);

			CellFormatter formatter = grid.getCellFormatter();

			formatter.setStyleName(0, COL_MONTH, CSS.datePickerMonth());
			formatter.setWidth(0, COL_PREV_YEAR, "1");
			formatter.setWidth(0, COL_PREV_MONTH, "1");
			formatter.setWidth(0, COL_MONTH, "100%");
			formatter.setWidth(0, COL_NEXT_MONTH, "1");
			formatter.setWidth(0, COL_NEXT_YEAR, "1");

			if (timePicker != null) {
				Grid panel = new Grid(2, 1);

				panel.setWidth("100%");
				panel.setWidget(0, 0, timePicker);
				panel.setWidget(1, 0, grid);
				panel
					.getCellFormatter()
					.addStyleName(0, 0, CSS.ewtTimePicker());
				panel
					.getCellFormatter()
					.setHorizontalAlignment(0, 0,
						HasHorizontalAlignment.ALIGN_CENTER);

				grid = panel;
			}

			initWidget(grid);
		}

		/**
		 * If this instance contains a time picker this method sets the input
		 * time in a new instance of the given date object and returns it.
		 *
		 * @param date The date to apply the time to
		 * @return The resulting date (a new instance if modified)
		 */
		Date applyTime(Date date) {
			if (timePicker != null && date != null) {
				date = timePicker.applyTime(date);
			}

			return date;
		}

		/**
		 * Sets the {@link GwtDatePicker} this instance is associated with.
		 *
		 * @param datePicker The new date picker
		 */
		void setDatePicker(GwtDatePicker datePicker) {
			this.datePicker = datePicker;
		}

		/**
		 * Sets the time values if this instance contains a time picker.
		 *
		 * @param date The date containing the new time
		 */
		void setTime(Date date) {
			if (timePicker != null) {
				timePicker.setTime(date);
			}
		}

		/**
		 * Resets the displayed date to today.
		 */
		private void resetDate() {
			Date today = new Date();

			datePicker.setCurrentMonth(today);
			datePicker.setValue(today, true);
		}

		/**
		 * Standard click handler for the month change buttons.
		 *
		 * @author e.sonnenschein
		 */
		public class MonthChangeClickHandler implements ClickHandler {

			private int monthAdd;

			/**
			 * Creates a new instance.
			 *
			 * @param monthAdd The number of month to add
			 */
			public MonthChangeClickHandler(int monthAdd) {
				this.monthAdd = monthAdd;
			}

			/**
			 * @see ClickHandler#onClick(ClickEvent)
			 */
			@Override
			public void onClick(ClickEvent event) {
				addMonths(monthAdd);
			}
		}
	}
}
