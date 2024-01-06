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

	private MonthAndTimeSelector aMonthAndTimeSelector;

	private DateBox rDateBox;

	/**
	 * Creates a new instance.
	 *
	 * @param rContext  The user interface context
	 * @param bDateTime TRUE to pick date and time, FALSE for date only
	 */
	public GwtDatePicker(UserInterfaceContext rContext, boolean bDateTime) {
		super(new MonthAndTimeSelector(rContext, bDateTime),
			new DefaultCalendarView(), new CalendarModel());

		aMonthAndTimeSelector = (MonthAndTimeSelector) getMonthSelector();
		aMonthAndTimeSelector.setDatePicker(this);

		GwtTimePicker rTimePicker = aMonthAndTimeSelector.getTimePicker();

		if (rTimePicker != null) {
			rTimePicker.addValueChangeHandler(
				new ValueChangeHandler<Integer>() {
					@Override
					public void onValueChange(
						ValueChangeEvent<Integer> rEvent) {
						timePickerValueChanged(rEvent);
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
		Date rDate = getValue();

		if (rDate != null) {
			// DatePicker returns 12:00:00 ?!
			rDate.setHours(0);
			rDate.setMinutes(0);
			rDate.setSeconds(0);
		}

		return aMonthAndTimeSelector.applyTime(rDate);
	}

	/**
	 * Sets the date.
	 *
	 * @param rDate The new date
	 */
	@Override
	public void setDate(Date rDate) {
		if (rDate != null) {
			setCurrentMonth(rDate);
		}

		aMonthAndTimeSelector.setTime(rDate);
		setValue(rDate);
	}

	/**
	 * If this instance has a time picker this method can be used to set a date
	 * box that needs to be updated if the time value has changed. If not set a
	 * {@link ValueChangeEvent} will be fired instead.
	 *
	 * @param rDateBox The dateBox value
	 */
	public final void setDateBox(DateBox rDateBox) {
		this.rDateBox = rDateBox;
	}

	/**
	 * Handles a {@link ValueChangeEvent} from a {@link GwtTimePicker}.
	 *
	 * @param rEvent The event that occurred
	 */
	void timePickerValueChanged(ValueChangeEvent<Integer> rEvent) {
		if (rDateBox == null) {
			ValueChangeEvent.fire(GwtDatePicker.this, getDate());
		} else {
			rDateBox.setValue(getDate(), true);
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

		private GwtDatePicker rDatePicker;

		private GwtTimePicker aTimePicker;

		private Label aMonthLabel = new Label();

		private PushButton aPrevMonthButton = new PushButton();

		private PushButton aNextMonthButton = new PushButton();

		private PushButton aPrevYearButton = new PushButton();

		private PushButton aNextYearButton = new PushButton();

		/**
		 * Creates a new instance.
		 *
		 * @param rContext  The user interface context
		 * @param bWithTime TRUE for month and time selection,
		 */
		public MonthAndTimeSelector(UserInterfaceContext rContext,
			boolean bWithTime) {
			if (bWithTime) {
				aTimePicker = new GwtTimePicker(rContext);
			}
		}

		/**
		 * @see MonthSelector#getModel()
		 */
		@Override
		public CalendarModel getModel() {
			return rDatePicker != null ? rDatePicker.getModel() : null;
		}

		/**
		 * Overridden to provide access to click handler.
		 *
		 * @see MonthSelector#addMonths(int)
		 */
		@Override
		protected void addMonths(int nMonthAdd) {
			super.addMonths(nMonthAdd);
		}

		/**
		 * @see MonthSelector#getDatePicker()
		 */
		@Override
		protected DatePicker getDatePicker() {
			return rDatePicker;
		}

		/**
		 * Returns the time picker component or NULL if none exits.
		 *
		 * @return The time picker component
		 */
		protected GwtTimePicker getTimePicker() {
			return aTimePicker;
		}

		/**
		 * @see MonthSelector#refresh()
		 */
		@Override
		protected void refresh() {
			if (rDatePicker != null) {
				aMonthLabel.setText(getModel().formatCurrentMonthAndYear());
			}
		}

		/**
		 * @see MonthSelector#setup()
		 */
		@Override
		protected void setup() {
			aPrevMonthButton.getUpFace().setHTML("&lsaquo;");
			aPrevMonthButton.setStyleName("datePickerPreviousButton");
			aPrevMonthButton.addClickHandler(new MonthChangeClickHandler(-1));

			aNextMonthButton.getUpFace().setHTML("&rsaquo;");
			aNextMonthButton.setStyleName("datePickerNextButton");
			aNextMonthButton.addClickHandler(new MonthChangeClickHandler(1));

			aPrevYearButton.getUpFace().setHTML("&laquo;");
			aPrevYearButton.setStyleName("datePickerPreviousButton");
			aPrevYearButton.addClickHandler(new MonthChangeClickHandler(-12));

			aNextYearButton.getUpFace().setHTML("&raquo;");
			aNextYearButton.setStyleName("datePickerNextButton");
			aNextYearButton.addClickHandler(new MonthChangeClickHandler(12));

			aMonthLabel.setStyleName("datePickerMonth");
			aMonthLabel.addDoubleClickHandler(new DoubleClickHandler() {
				@Override
				public void onDoubleClick(DoubleClickEvent rEvent) {
					resetDate();
				}
			});

			Grid aGrid = new Grid(1, 5);

			aGrid.setStyleName(CSS.datePickerMonthSelector());
			aGrid.setWidget(0, COL_PREV_YEAR, aPrevYearButton);
			aGrid.setWidget(0, COL_PREV_MONTH, aPrevMonthButton);
			aGrid.setWidget(0, COL_MONTH, aMonthLabel);
			aGrid.setWidget(0, COL_NEXT_MONTH, aNextMonthButton);
			aGrid.setWidget(0, COL_NEXT_YEAR, aNextYearButton);

			CellFormatter rFormatter = aGrid.getCellFormatter();

			rFormatter.setStyleName(0, COL_MONTH, CSS.datePickerMonth());
			rFormatter.setWidth(0, COL_PREV_YEAR, "1");
			rFormatter.setWidth(0, COL_PREV_MONTH, "1");
			rFormatter.setWidth(0, COL_MONTH, "100%");
			rFormatter.setWidth(0, COL_NEXT_MONTH, "1");
			rFormatter.setWidth(0, COL_NEXT_YEAR, "1");

			if (aTimePicker != null) {
				Grid aPanel = new Grid(2, 1);

				aPanel.setWidth("100%");
				aPanel.setWidget(0, 0, aTimePicker);
				aPanel.setWidget(1, 0, aGrid);
				aPanel
					.getCellFormatter()
					.addStyleName(0, 0, CSS.ewtTimePicker());
				aPanel
					.getCellFormatter()
					.setHorizontalAlignment(0, 0,
						HasHorizontalAlignment.ALIGN_CENTER);

				aGrid = aPanel;
			}

			initWidget(aGrid);
		}

		/**
		 * If this instance contains a time picker this method sets the input
		 * time in a new instance of the given date object and returns it.
		 *
		 * @param rDate The date to apply the time to
		 * @return The resulting date (a new instance if modified)
		 */
		Date applyTime(Date rDate) {
			if (aTimePicker != null && rDate != null) {
				rDate = aTimePicker.applyTime(rDate);
			}

			return rDate;
		}

		/**
		 * Sets the {@link GwtDatePicker} this instance is associated with.
		 *
		 * @param rDatePicker The new date picker
		 */
		void setDatePicker(GwtDatePicker rDatePicker) {
			this.rDatePicker = rDatePicker;
		}

		/**
		 * Sets the time values if this instance contains a time picker.
		 *
		 * @param rDate The date containing the new time
		 */
		void setTime(Date rDate) {
			if (aTimePicker != null) {
				aTimePicker.setTime(rDate);
			}
		}

		/**
		 * Resets the displayed date to today.
		 */
		private void resetDate() {
			Date aToday = new Date();

			rDatePicker.setCurrentMonth(aToday);
			rDatePicker.setValue(aToday, true);
		}

		/**
		 * Standard click handler for the month change buttons.
		 *
		 * @author e.sonnenschein
		 */
		public class MonthChangeClickHandler implements ClickHandler {

			private int nMonthAdd;

			/**
			 * Creates a new instance.
			 *
			 * @param nMonthAdd The number of month to add
			 */
			public MonthChangeClickHandler(int nMonthAdd) {
				this.nMonthAdd = nMonthAdd;
			}

			/**
			 * @see ClickHandler#onClick(ClickEvent)
			 */
			@Override
			public void onClick(ClickEvent rEvent) {
				addMonths(nMonthAdd);
			}
		}
	}
}
