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

import java.util.Date;

import com.google.gwt.i18n.client.DateTimeFormat;

import static com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat.DATE_FULL;
import static com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat.DATE_LONG;
import static com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat.DATE_MEDIUM;
import static com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat.DATE_SHORT;
import static com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat.DATE_TIME_FULL;
import static com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat.DATE_TIME_LONG;
import static com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat.DATE_TIME_MEDIUM;
import static com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat.DATE_TIME_SHORT;
import static com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat.TIME_FULL;
import static com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat.TIME_LONG;
import static com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat.TIME_MEDIUM;
import static com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat.TIME_SHORT;


/********************************************************************
 * A format
 *
 * @author eso
 */
public class GwtDateTimeFormat implements ValueFormat
{
	//~ Static fields/initializers ---------------------------------------------

	/** {@link DateTimeFormat#getShortDateTimeFormat()} */
	public static final ValueFormat SHORT_DATE_TIME =
		new GwtDateTimeFormat(DateTimeFormat.getFormat(DATE_TIME_SHORT));

	/** {@link DateTimeFormat#getShortDateFormat()} */
	public static final ValueFormat SHORT_DATE =
		new GwtDateTimeFormat(DateTimeFormat.getFormat(DATE_SHORT));

	/** {@link DateTimeFormat#getShortTimeFormat()} */
	public static final ValueFormat SHORT_TIME =
		new GwtDateTimeFormat(DateTimeFormat.getFormat(TIME_SHORT));

	/** {@link DateTimeFormat#getMediumDateTimeFormat()} */
	public static final ValueFormat MEDIUM_DATE_TIME =
		new GwtDateTimeFormat(DateTimeFormat.getFormat(DATE_TIME_MEDIUM));

	/** {@link DateTimeFormat#getMediumDateFormat()} */
	public static final ValueFormat MEDIUM_DATE =
		new GwtDateTimeFormat(DateTimeFormat.getFormat(DATE_MEDIUM));

	/** {@link DateTimeFormat#getMediumTimeFormat()} */
	public static final ValueFormat MEDIUM_TIME =
		new GwtDateTimeFormat(DateTimeFormat.getFormat(TIME_MEDIUM));

	/** {@link DateTimeFormat#getLongDateTimeFormat()} */
	public static final ValueFormat LONG_DATE_TIME =
		new GwtDateTimeFormat(DateTimeFormat.getFormat(DATE_TIME_LONG));

	/** {@link DateTimeFormat#getLongDateFormat()} */
	public static final ValueFormat LONG_DATE =
		new GwtDateTimeFormat(DateTimeFormat.getFormat(DATE_LONG));

	/** {@link DateTimeFormat#getLongTimeFormat()} */
	public static final ValueFormat LONG_TIME =
		new GwtDateTimeFormat(DateTimeFormat.getFormat(TIME_LONG));

	/** {@link DateTimeFormat#getFullDateTimeFormat()} */
	public static final ValueFormat FULL_DATE_TIME =
		new GwtDateTimeFormat(DateTimeFormat.getFormat(DATE_TIME_FULL));

	/** {@link DateTimeFormat#getFullDateFormat()} */
	public static final ValueFormat FULL_DATE =
		new GwtDateTimeFormat(DateTimeFormat.getFormat(DATE_FULL));

	/** {@link DateTimeFormat#getFullTimeFormat()} */
	public static final ValueFormat FULL_TIME =
		new GwtDateTimeFormat(DateTimeFormat.getFormat(TIME_FULL));

	//~ Instance fields --------------------------------------------------------

	private DateTimeFormat rFormat;

	//~ Constructors -----------------------------------------------------------

	/***************************************
	 * Creates a new instance.
	 *
	 * @param rFormat The GWT date time format to use
	 */
	public GwtDateTimeFormat(DateTimeFormat rFormat)
	{
		this.rFormat = rFormat;
	}

	//~ Methods ----------------------------------------------------------------

	/***************************************
	 * Formats the input object with the {@link DateTimeFormat} of this
	 * instance. . Unsupported input values (including NULL) will cause an
	 * exception.
	 *
	 * @param  rValue Either a date object or a string that must contain the
	 *                long value of the date to format
	 *
	 * @return The string representation of the date value
	 */
	@Override
	public String format(Object rValue)
	{
		Date rDate;

		if (rValue instanceof String)
		{
			rDate = new Date(Long.parseLong((String) rValue));
		}
		else
		{
			rDate = (Date) rValue;
		}

		return rFormat.format(rDate);
	}
}
