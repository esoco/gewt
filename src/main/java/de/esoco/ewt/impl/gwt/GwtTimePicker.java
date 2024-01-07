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

/**
 * A composite that combines a {@link DatePicker} widget with input fields for
 * time input.
 *
 * @author eso
 */
public class GwtTimePicker extends Composite
	implements HasValueChangeHandlers<Integer>, ValueChangeHandler<Integer> {

	private static final GewtResources RES = GewtResources.INSTANCE;

	private static final GewtCss CSS = RES.css();

	Image clockImage;

	private GwtSpinner hoursField;

	private GwtSpinner minutesField;

	/**
	 * Creates a new instance.
	 */
	public GwtTimePicker(UserInterfaceContext context) {
		HorizontalPanel timePanel = new HorizontalPanel();
		Image clockImage = new Image(RES.imClock());
		Label separator = new Label(":");

		hoursField = new GwtSpinner(0, 23, 1, new TimeValueBox());
		minutesField = new GwtSpinner(0, 59, 5, new TimeValueBox());

		clockImage.setTitle(context.expandResource("$ttGewtDatePickerTime"));
		hoursField.setTitle(context.expandResource("$ttGewtDatePickerHour"));
		minutesField.setTitle(
			context.expandResource("$ttGewtDatePickerMinute"));

		clockImage.addDoubleClickHandler(new DoubleClickHandler() {
			@Override
			public void onDoubleClick(DoubleClickEvent event) {
				setTime(new Date());
			}
		});

		hoursField.addValueChangeHandler(this);
		minutesField.addValueChangeHandler(this);

		timePanel.add(clockImage);
		timePanel.add(hoursField);
		timePanel.add(separator);
		timePanel.add(minutesField);
		timePanel.setCellVerticalAlignment(clockImage,
			HorizontalPanel.ALIGN_MIDDLE);
		timePanel.setCellVerticalAlignment(separator,
			HorizontalPanel.ALIGN_MIDDLE);

		initWidget(timePanel);

		clockImage.addStyleName(CSS.ewtIcon());
		setStylePrimaryName(CSS.ewtTimePicker());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public HandlerRegistration addValueChangeHandler(
		ValueChangeHandler<Integer> handler) {
		return addHandler(handler, ValueChangeEvent.getType());
	}

	/**
	 * Returns the currently displayed time with the calendar date of the date
	 * parameter.
	 *
	 * @param date The calendar date to apply the time to
	 * @return A new date object with the combined date and time
	 */
	@SuppressWarnings("deprecation")
	public Date applyTime(Date date) {
		if (date != null) {
			date = CalendarUtil.copyDate(date);
			date.setHours(hoursField.getValue());
			date.setMinutes(minutesField.getValue());
		}

		return date;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onValueChange(ValueChangeEvent<Integer> event) {
		ValueChangeEvent.fire(this, event.getValue());
	}

	/**
	 * Sets the time of this widget to the time of the given date object.
	 *
	 * @param date The new time
	 * @return The modified date
	 */
	@SuppressWarnings("deprecation")
	public Date setTime(Date date) {
		int hours = 0;
		int minutes = 0;

		if (date != null) {
			hours = date.getHours();
			minutes = date.getMinutes();
		}

		hoursField.setValue(hours);
		minutesField.setValue(minutes);

		return date;
	}

	/**
	 * An integer value box with a custom number format.
	 *
	 * @author eso
	 */
	static class TimeValueBox extends ValueBox<Integer> {

		private static final NumberFormat VALUE_FORMAT =
			NumberFormat.getFormat("00");

		private static final Renderer<Integer> RENDERER =
			new AbstractRenderer<Integer>() {
				@Override
				public String render(Integer value) {
					return value != null ? VALUE_FORMAT.format(value) : "";
				}
			};

		/**
		 * Creates a new instance.
		 */
		public TimeValueBox() {
			super(Document.get().createTextInputElement(), RENDERER,
				IntegerParser.instance());
		}
	}
}
