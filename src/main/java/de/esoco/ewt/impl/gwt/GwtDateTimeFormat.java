//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// This file is a part of the 'gewt' project.
// Copyright 2017 Elmar Sonnenschein, esoco GmbH, Flensburg, Germany
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
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;

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

	/** {@link PredefinedFormat#DATE_TIME_SHORT} */
	public static final ValueFormat SHORT_DATE_TIME =
		new GwtDateTimeFormat(DateTimeFormat.getFormat(DATE_TIME_SHORT));

	/** {@link PredefinedFormat#DATE_SHORT} */
	public static final ValueFormat SHORT_DATE =
		new GwtDateTimeFormat(DateTimeFormat.getFormat(DATE_SHORT));

	/** {@link PredefinedFormat#TIME_SHORT} */
	public static final ValueFormat SHORT_TIME =
		new GwtDateTimeFormat(DateTimeFormat.getFormat(TIME_SHORT));

	/** {@link PredefinedFormat#DATE_TIME_MEDIUM} */
	public static final ValueFormat MEDIUM_DATE_TIME =
		new GwtDateTimeFormat(DateTimeFormat.getFormat(DATE_TIME_MEDIUM));

	/** {@link PredefinedFormat#DATE_MEDIUM} */
	public static final ValueFormat MEDIUM_DATE =
		new GwtDateTimeFormat(DateTimeFormat.getFormat(DATE_MEDIUM));

	/** {@link PredefinedFormat#TIME_MEDIUM} */
	public static final ValueFormat MEDIUM_TIME =
		new GwtDateTimeFormat(DateTimeFormat.getFormat(TIME_MEDIUM));

	/** {@link PredefinedFormat#DATE_TIME_LONG} */
	public static final ValueFormat LONG_DATE_TIME =
		new GwtDateTimeFormat(DateTimeFormat.getFormat(DATE_TIME_LONG));

	/** {@link PredefinedFormat#DATE_LONG} */
	public static final ValueFormat LONG_DATE =
		new GwtDateTimeFormat(DateTimeFormat.getFormat(DATE_LONG));

	/** {@link PredefinedFormat#TIME_LONG} */
	public static final ValueFormat LONG_TIME =
		new GwtDateTimeFormat(DateTimeFormat.getFormat(TIME_LONG));

	/** {@link PredefinedFormat#DATE_TIME_FULL} */
	public static final ValueFormat FULL_DATE_TIME =
		new GwtDateTimeFormat(DateTimeFormat.getFormat(DATE_TIME_FULL));

	/** {@link PredefinedFormat#DATE_FULL} */
	public static final ValueFormat FULL_DATE =
		new GwtDateTimeFormat(DateTimeFormat.getFormat(DATE_FULL));

	/** {@link PredefinedFormat#TIME_FULL} */
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
