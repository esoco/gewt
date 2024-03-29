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
package de.esoco.ewt.component;

import de.esoco.ewt.UserInterfaceContext;
import de.esoco.ewt.impl.gwt.GwtDatePicker;
import de.esoco.ewt.impl.gwt.ValueBoxWrapper;
import de.esoco.ewt.impl.gwt.WidgetFactory;
import de.esoco.ewt.style.StyleData;
import de.esoco.ewt.style.StyleFlag;

import de.esoco.lib.property.DateAttribute;

import java.util.Date;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.datepicker.client.DateBox;

/**
 * An input field that allows to input or select a date value.
 *
 * @author eso
 */
public class DateField extends TextControl implements DateAttribute {

	/**
	 * Returns the currently selected date value of this component.
	 *
	 * @return The current date value (will be NULL if edit field is empty)
	 */
	@Override
	public Date getDate() {
		Date date = null;

		if (getDateWidget().getTextBox().getText().length() > 0) {
			date = getDateWidget().getDatePicker().getValue();
		}

		return date;
	}

	/**
	 * Returns the date of the month that is currently displayed by the date
	 * picker of this instance.
	 *
	 * @return The date of the selected month
	 */
	public Date getMonth() {
		return getDateWidget().getDatePicker().getCurrentMonth();
	}

	/**
	 * Returns the editable state of this component.
	 *
	 * @return TRUE if the component allows editing, FALSE if it is readonly
	 */
	@Override
	public boolean isEditable() {
		return !getDateWidget().getTextBox().isReadOnly();
	}

	/**
	 * @see TextControl#isEnabled()
	 */
	@Override
	public boolean isEnabled() {
		return getTextBox().isEnabled();
	}

	/**
	 * @see TextControl#setColumns(int)
	 */
	@Override
	public void setColumns(int columns) {
		getDateWidget().getTextBox().setVisibleLength(columns);
	}

	/**
	 * Sets the date value of this component.
	 *
	 * @param date The new date value or NULL for an empty edit field
	 */
	@Override
	public void setDate(Date date) {
		if (date == null) {
			getDateWidget().getTextBox().setText("");
		} else {
			getDateWidget().setValue(date);
		}
	}

	/**
	 * Sets the editable state of this component.
	 *
	 * @param editable TRUE to make the component editable, FALSE to make it
	 *                 readonly
	 */
	@Override
	public void setEditable(boolean editable) {
		getDateWidget().getTextBox().setReadOnly(!editable);
	}

	/**
	 * @see TextControl#setEnabled(boolean)
	 */
	@Override
	public void setEnabled(boolean enabled) {
		getDateWidget().setEnabled(enabled);
	}

	/**
	 * Sets the month to be displayed by the date picker of this instance.
	 *
	 * @param date A date of the new month to be displayed
	 */
	public void setMonth(Date date) {
		if (date != null) {
			getDateWidget().getDatePicker().setCurrentMonth(date);
		}
	}

	/**
	 * Overridden to return the result of {@link DateBox#getTextBox()}.
	 *
	 * @see TextControl#getTextBox()
	 */
	@Override
	protected IsTextControlWidget getTextBox() {
		return new ValueBoxWrapper(getDateWidget().getTextBox());
	}

	/**
	 * Internal method to return the date widget of this instance.
	 *
	 * @return The date widget
	 */
	private DateBox getDateWidget() {
		return (DateBox) getWidget();
	}

	/**
	 * Widget factory for this component.
	 *
	 * @author eso
	 */
	public static class DateFieldWidgetFactory
		implements WidgetFactory<DateBox> {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public DateBox createWidget(Component component, StyleData style) {
			return new DateFieldWidget(component.getContext(),
				style.hasFlag(StyleFlag.DATE_TIME));
		}
	}

	/**
	 * A simple {@link DateBox} subclass that implements {@link Focusable} and
	 * uses a {@link GwtDatePicker} for the date selection.
	 *
	 * @author eso
	 */
	private static class DateFieldWidget extends DateBox implements Focusable {

		/**
		 * Creates a new instance
		 *
		 * @param context  The user interface context
		 * @param dateTime TRUE to pick date and time, FALSE for date only
		 */
		public DateFieldWidget(UserInterfaceContext context,
			boolean dateTime) {
			super(new GwtDatePicker(context, dateTime), null,
				new DefaultFormat(
				DateTimeFormat.getFormat(dateTime ?
				                         PredefinedFormat.DATE_TIME_MEDIUM :
				                         PredefinedFormat.DATE_MEDIUM)));

			((GwtDatePicker) getDatePicker()).setDateBox(this);
		}

		/**
		 * @see DateBox#hideDatePicker()
		 */
		@Override
		public void hideDatePicker() {
			super.hideDatePicker();

			if (isDatePickerShowing()) {
				setValue(((GwtDatePicker) getDatePicker()).getDate());
			}
		}

		/**
		 * @see DateBox#showDatePicker()
		 */
		@Override
		public void showDatePicker() {
			if (!isDatePickerShowing()) {
				((GwtDatePicker) getDatePicker()).setDate(getValue());
			}

			super.showDatePicker();
		}
	}
}
