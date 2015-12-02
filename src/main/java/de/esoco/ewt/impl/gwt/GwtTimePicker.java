//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// This file is a part of the 'gewt' project.
// Copyright 2015 Elmar Sonnenschein, esoco GmbH, Flensburg, Germany
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

import java.util.Date;

import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.text.client.IntegerParser;
import com.google.gwt.text.shared.AbstractRenderer;
import com.google.gwt.text.shared.Renderer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ValueBox;
import com.google.gwt.user.datepicker.client.CalendarUtil;
import com.google.gwt.user.datepicker.client.DatePicker;


/********************************************************************
 * A composite that combines a {@link DatePicker} widget with input fields for
 * time input.
 *
 * @author eso
 */
public class GwtTimePicker extends Composite
	implements HasValueChangeHandlers<Integer>, ValueChangeHandler<Integer>
{
	//~ Static fields/initializers ---------------------------------------------

	private static final GewtResources RES = GewtResources.INSTANCE;
	private static final GewtCss	   CSS = RES.css();

	//~ Instance fields --------------------------------------------------------

	Image			   aClockImage;
	private GwtSpinner aHoursField;
	private GwtSpinner aMinutesField;

	//~ Constructors -----------------------------------------------------------

	/***************************************
	 * Creates a new instance.
	 *
	 * @param rContext
	 */
	public GwtTimePicker(UserInterfaceContext rContext)
	{
		HorizontalPanel aTimePanel  = new HorizontalPanel();
		Image		    aClockImage = new Image(RES.imClock());
		Label		    aSeparator  = new Label(":");

		aHoursField   = new GwtSpinner(0, 23, 1, new TimeValueBox());
		aMinutesField = new GwtSpinner(0, 59, 5, new TimeValueBox());

		aClockImage.setTitle(rContext.expandResource("$ttGewtDatePickerTime"));
		aHoursField.setTitle(rContext.expandResource("$ttGewtDatePickerHour"));
		aMinutesField.setTitle(rContext.expandResource("$ttGewtDatePickerMinute"));

		aClockImage.addDoubleClickHandler(new DoubleClickHandler()
			{
				@Override
				public void onDoubleClick(DoubleClickEvent rEvent)
				{
					setTime(new Date());
				}
			});

		aHoursField.addValueChangeHandler(this);
		aMinutesField.addValueChangeHandler(this);

		aTimePanel.add(aClockImage);
		aTimePanel.add(aHoursField);
		aTimePanel.add(aSeparator);
		aTimePanel.add(aMinutesField);
		aTimePanel.setCellVerticalAlignment(aClockImage,
											HorizontalPanel.ALIGN_MIDDLE);
		aTimePanel.setCellVerticalAlignment(aSeparator,
											HorizontalPanel.ALIGN_MIDDLE);

		initWidget(aTimePanel);

		aClockImage.addStyleName(CSS.ewtIcon());
		setStylePrimaryName(CSS.ewtTimePicker());
	}

	//~ Methods ----------------------------------------------------------------

	/***************************************
	 * {@inheritDoc}
	 */
	@Override
	public HandlerRegistration addValueChangeHandler(
		ValueChangeHandler<Integer> rHandler)
	{
		return addHandler(rHandler, ValueChangeEvent.getType());
	}

	/***************************************
	 * Returns the currently displayed time with the calendar date of the date
	 * parameter.
	 *
	 * @param  rDate The calendar date to apply the time to
	 *
	 * @return A new date object with the combined date and time
	 */
	@SuppressWarnings("deprecation")
	public Date applyTime(Date rDate)
	{
		if (rDate != null)
		{
			rDate = CalendarUtil.copyDate(rDate);
			rDate.setHours(aHoursField.getValue());
			rDate.setMinutes(aMinutesField.getValue());
		}

		return rDate;
	}

	/***************************************
	 * {@inheritDoc}
	 */
	@Override
	public void onValueChange(ValueChangeEvent<Integer> rEvent)
	{
		ValueChangeEvent.fire(this, rEvent.getValue());
	}

	/***************************************
	 * Sets the time of this widget to the time of the given date object.
	 *
	 * @param  rDate The new time
	 *
	 * @return The modified date
	 */
	@SuppressWarnings("deprecation")
	public Date setTime(Date rDate)
	{
		int nHours   = 0;
		int nMinutes = 0;

		if (rDate != null)
		{
			nHours   = rDate.getHours();
			nMinutes = rDate.getMinutes();
		}

		aHoursField.setValue(nHours);
		aMinutesField.setValue(nMinutes);

		return rDate;
	}

	//~ Inner Classes ----------------------------------------------------------

	/********************************************************************
	 * An integer value box with a custom number format.
	 *
	 * @author eso
	 */
	static class TimeValueBox extends ValueBox<Integer>
	{
		//~ Static fields/initializers -----------------------------------------

		private static final NumberFormat VALUE_FORMAT =
			NumberFormat.getFormat("00");

		private static final Renderer<Integer> RENDERER =
			new AbstractRenderer<Integer>()
			{
				@Override
				public String render(Integer rValue)
				{
					return rValue != null ? VALUE_FORMAT.format(rValue) : "";
				}
			};

		//~ Constructors -------------------------------------------------------

		/***************************************
		 * Creates a new instance.
		 */
		public TimeValueBox()
		{
			super(Document.get().createTextInputElement(),
				  RENDERER,
				  IntegerParser.instance());
		}
	}
}
