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

import de.esoco.ewt.EWT;
import de.esoco.ewt.UserInterfaceContext;
import de.esoco.ewt.impl.gwt.GwtDatePicker;
import de.esoco.ewt.impl.gwt.WidgetFactory;
import de.esoco.ewt.style.StyleData;
import de.esoco.ewt.style.StyleFlag;

import de.esoco.lib.property.DateAttribute;

import java.util.Date;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.TextBoxBase;
import com.google.gwt.user.datepicker.client.DateBox;


/********************************************************************
 * An input field that allows to input or select a date value.
 *
 * @author eso
 */
public class DateField extends TextComponent implements DateAttribute
{
	//~ Static fields/initializers ---------------------------------------------

	static
	{
		EWT.registerWidgetFactory(DateField.class,
										   new DateFieldWidgetFactory(),
										   false);
	}

	//~ Methods ----------------------------------------------------------------

	/***************************************
	 * Returns the currently selected date value of this component.
	 *
	 * @return The current date value (will be NULL if edit field is empty)
	 */
	@Override
	public Date getDate()
	{
		Date rDate = null;

		if (getDateWidget().getTextBox().getText().length() > 0)
		{
			rDate = getDateWidget().getDatePicker().getValue();
		}

		return rDate;
	}

	/***************************************
	 * Returns the date of the month that is currently displayed by the date
	 * picker of this instance.
	 *
	 * @return The date of the selected month
	 */
	public Date getMonth()
	{
		return getDateWidget().getDatePicker().getCurrentMonth();
	}

	/***************************************
	 * Returns the editable state of this component.
	 *
	 * @return TRUE if the component allows editing, FALSE if it is readonly
	 */
	@Override
	public boolean isEditable()
	{
		return !getDateWidget().getTextBox().isReadOnly();
	}

	/***************************************
	 * @see TextComponent#isEnabled()
	 */
	@Override
	public boolean isEnabled()
	{
		return getTextBox().isEnabled();
	}

	/***************************************
	 * @see TextComponent#setColumns(int)
	 */
	@Override
	public void setColumns(int nColumns)
	{
		getDateWidget().getTextBox().setVisibleLength(nColumns);
	}

	/***************************************
	 * Sets the date value of this component.
	 *
	 * @param rDate The new date value or NULL for an empty edit field
	 */
	@Override
	public void setDate(Date rDate)
	{
		if (rDate == null)
		{
			getDateWidget().getTextBox().setText("");
		}
		else
		{
			getDateWidget().setValue(rDate);
		}
	}

	/***************************************
	 * Sets the editable state of this component.
	 *
	 * @param bEditable TRUE to make the component editable, FALSE to make it
	 *                  readonly
	 */
	@Override
	public void setEditable(boolean bEditable)
	{
		getDateWidget().getTextBox().setReadOnly(!bEditable);
	}

	/***************************************
	 * @see TextComponent#setEnabled(boolean)
	 */
	@Override
	public void setEnabled(boolean bEnabled)
	{
		getDateWidget().setEnabled(bEnabled);
	}

	/***************************************
	 * Sets the month to be displayed by the date picker of this instance.
	 *
	 * @param rDate A date of the new month to be displayed
	 */
	public void setMonth(Date rDate)
	{
		if (rDate != null)
		{
			getDateWidget().getDatePicker().setCurrentMonth(rDate);
		}
	}

	/***************************************
	 * Overridden to return the result of {@link DateBox#getTextBox()}.
	 *
	 * @see TextComponent#getTextBox()
	 */
	@Override
	protected TextBoxBase getTextBox()
	{
		return getDateWidget().getTextBox();
	}

	/***************************************
	 * Internal method to return the date widget of this instance.
	 *
	 * @return The date widget
	 */
	private DateBox getDateWidget()
	{
		return (DateBox) getWidget();
	}

	//~ Inner Classes ----------------------------------------------------------

	/********************************************************************
	 * Widget factory for this component.
	 *
	 * @author eso
	 */
	public static class DateFieldWidgetFactory implements WidgetFactory<DateBox>
	{
		//~ Methods ------------------------------------------------------------

		/***************************************
		 * {@inheritDoc}
		 */
		@Override
		public DateBox createWidget(
			UserInterfaceContext rContext,
			StyleData			 rStyle)
		{
			return new DateFieldWidget(rContext,
									   rStyle.hasFlag(StyleFlag.DATE_TIME));
		}
	}

	/********************************************************************
	 * A simple {@link DateBox} subclass that implements {@link Focusable} and
	 * uses a {@link GwtDatePicker} for the date selection.
	 *
	 * @author eso
	 */
	private static class DateFieldWidget extends DateBox implements Focusable
	{
		//~ Constructors -------------------------------------------------------

		/***************************************
		 * Creates a new instance
		 *
		 * @param rContext  The user interface context
		 * @param bDateTime TRUE to pick date and time, FALSE for date only
		 */
		public DateFieldWidget(UserInterfaceContext rContext, boolean bDateTime)
		{
			super(new GwtDatePicker(rContext, bDateTime),
				  null,
				  new DefaultFormat(DateTimeFormat.getFormat(bDateTime
															 ? PredefinedFormat.DATE_TIME_MEDIUM
															 : PredefinedFormat.DATE_MEDIUM)));

			((GwtDatePicker) getDatePicker()).setDateBox(this);
		}

		//~ Methods ------------------------------------------------------------

		/***************************************
		 * @see DateBox#hideDatePicker()
		 */
		@Override
		public void hideDatePicker()
		{
			super.hideDatePicker();

			if (isDatePickerShowing())
			{
				setValue(((GwtDatePicker) getDatePicker()).getDate());
			}
		}

		/***************************************
		 * @see DateBox#showDatePicker()
		 */
		@Override
		public void showDatePicker()
		{
			if (!isDatePickerShowing())
			{
				((GwtDatePicker) getDatePicker()).setDate(getValue());
			}

			super.showDatePicker();
		}
	}
}
