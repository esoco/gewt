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

import de.esoco.ewt.impl.gwt.GwtDatePicker;
import de.esoco.ewt.impl.gwt.WidgetFactory;
import de.esoco.ewt.style.StyleData;
import de.esoco.ewt.style.StyleFlag;

import de.esoco.lib.property.DateAttribute;

import java.util.Date;

/**
 * A calendar component that allows to display and choose a date value.
 *
 * @author eso
 */
public class Calendar extends Component implements DateAttribute {

	/**
	 * Adds a style name to a certain date.
	 *
	 * @param date  The date to set the style for
	 * @param style The style to set
	 */
	public void addDateStyle(Date date, String style) {
		getDatePicker().addStyleToDates(style, date);
	}

	/**
	 * Returns the currently selected date value of this component.
	 *
	 * @return The current date value
	 */
	@Override
	public Date getDate() {
		return getDatePicker().getDate();
	}

	/**
	 * Returns the date of the month that is currently displayed by this
	 * calendar.
	 *
	 * @return The date of the current month
	 */
	public Date getMonth() {
		return getDatePicker().getCurrentMonth();
	}

	/**
	 * Clears the highlighting of a date.
	 *
	 * @param date  The date to clear the highlight of
	 * @param style nType The highlight type
	 */
	public void removeDateStyle(Date date, String style) {
		getDatePicker().removeStyleFromDates(style, date);
	}

	/**
	 * Sets the date that is highlighted by this component.
	 *
	 * @param date The new date value or NULL to remove the date highlight
	 */
	@Override
	public void setDate(Date date) {
		getDatePicker().setDate(date);
	}

	/**
	 * Sets the month to be displayed by this calendar.
	 *
	 * @param date A date of the new month to be displayed
	 */
	public void setMonth(Date date) {
		if (date != null) {
			getDatePicker().setCurrentMonth(date);
		}
	}

	/**
	 * Internal method to return the date widget of this instance.
	 *
	 * @return The date widget
	 */
	private GwtDatePicker getDatePicker() {
		return (GwtDatePicker) getWidget();
	}

	/**
	 * Widget factory for this component.
	 *
	 * @author eso
	 */
	public static class CalendarWidgetFactory
		implements WidgetFactory<GwtDatePicker> {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GwtDatePicker createWidget(Component component,
			StyleData style) {
			return new GwtDatePicker(component.getContext(),
				style.hasFlag(StyleFlag.DATE_TIME));
		}
	}
}
