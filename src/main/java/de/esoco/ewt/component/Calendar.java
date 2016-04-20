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
import de.esoco.ewt.event.EventType;
import de.esoco.ewt.impl.gwt.GwtDatePicker;
import de.esoco.ewt.impl.gwt.WidgetFactory;
import de.esoco.ewt.style.StyleData;
import de.esoco.ewt.style.StyleFlag;

import de.esoco.lib.property.DateAttribute;

import java.util.Date;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Widget;


/********************************************************************
 * A calendar component that allows to display and choose a date value.
 *
 * @author eso
 */
public class Calendar extends Component implements DateAttribute
{
	//~ Static fields/initializers ---------------------------------------------

	static
	{
		EWT.registerComponentWidgetFactory(Calendar.class,
										   new CalendarWidgetFactory(),
										   false);
	}

	//~ Methods ----------------------------------------------------------------

	/***************************************
	 * Adds a style name to a certain date.
	 *
	 * @param rDate  The date to set the style for
	 * @param sStyle The style to set
	 */
	public void addDateStyle(Date rDate, String sStyle)
	{
		getDatePicker().addStyleToDates(sStyle, rDate);
	}

	/***************************************
	 * Returns the currently selected date value of this component.
	 *
	 * @return The current date value
	 */
	@Override
	public Date getDate()
	{
		return getDatePicker().getDate();
	}

	/***************************************
	 * Returns the date of the month that is currently displayed by this
	 * calendar.
	 *
	 * @return The date of the current month
	 */
	public Date getMonth()
	{
		return getDatePicker().getCurrentMonth();
	}

	/***************************************
	 * Clears the highlighting of a date.
	 *
	 * @param rDate  The date to clear the highlight of
	 * @param sStyle nType The highlight type
	 */
	public void removeDateStyle(Date rDate, String sStyle)
	{
		getDatePicker().removeStyleFromDates(sStyle, rDate);
	}

	/***************************************
	 * Sets the date that is highlighted by this component.
	 *
	 * @param rDate The new date value or NULL to remove the date highlight
	 */
	@Override
	public void setDate(Date rDate)
	{
		getDatePicker().setDate(rDate);
	}

	/***************************************
	 * Sets the month to be displayed by this calendar.
	 *
	 * @param rDate A date of the new month to be displayed
	 */
	public void setMonth(Date rDate)
	{
		if (rDate != null)
		{
			getDatePicker().setCurrentMonth(rDate);
		}
	}

	/***************************************
	 * @see Component#createEventDispatcher()
	 */
	@Override
	ComponentEventDispatcher createEventDispatcher()
	{
		return new CalendarEventDispatcher();
	}

	/***************************************
	 * Internal method to return the date widget of this instance.
	 *
	 * @return The date widget
	 */
	private GwtDatePicker getDatePicker()
	{
		return (GwtDatePicker) getWidget();
	}

	//~ Inner Classes ----------------------------------------------------------

	/********************************************************************
	 * Widget factory for this component.
	 *
	 * @author eso
	 */
	public static class CalendarWidgetFactory
		implements WidgetFactory<GwtDatePicker>
	{
		//~ Methods ------------------------------------------------------------

		/***************************************
		 * {@inheritDoc}
		 */
		@Override
		public GwtDatePicker createWidget(
			UserInterfaceContext rContext,
			StyleData			 rStyle)
		{
			return new GwtDatePicker(rContext,
									 rStyle.hasFlag(StyleFlag.DATE_TIME));
		}
	}

	/********************************************************************
	 * Dispatcher for calendar-specific events.
	 *
	 * @author eso
	 */
	class CalendarEventDispatcher extends ComponentEventDispatcher
		implements ValueChangeHandler<Date>
	{
		//~ Methods ------------------------------------------------------------

		/***************************************
		 * @see ValueChangeHandler#onValueChange(ValueChangeEvent)
		 */
		@Override
		public void onValueChange(ValueChangeEvent<Date> rEvent)
		{
			notifyEventHandler(EventType.VALUE_CHANGED);
		}

		/***************************************
		 * @see ComponentEventDispatcher#initEventDispatching(Widget)
		 */
		@Override
		void initEventDispatching(Widget rWidget)
		{
			super.initEventDispatching(rWidget);

			getDatePicker().addValueChangeHandler(this);
		}
	}
}
