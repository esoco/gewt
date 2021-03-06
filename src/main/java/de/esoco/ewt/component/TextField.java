//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// This file is a part of the 'gewt' project.
// Copyright 2018 Elmar Sonnenschein, esoco GmbH, Flensburg, Germany
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

import de.esoco.ewt.impl.gwt.WidgetFactory;
import de.esoco.ewt.style.StyleData;

import de.esoco.lib.property.StyleProperties;
import de.esoco.lib.property.TextFieldStyle;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;

import static de.esoco.lib.property.StyleProperties.TEXT_FIELD_STYLE;


/********************************************************************
 * A text component subclass that allows single-line text input.
 *
 * <p>Supported styles:</p>
 *
 * <ul>
 *   <li>{@link StyleProperties#TEXT_FIELD_STYLE}: A rendering style from {@link
 *     TextFieldStyle}.</li>
 * </ul>
 *
 * @author eso
 */
public class TextField extends TextControl
{
	//~ Methods ----------------------------------------------------------------

	/***************************************
	 * Returns the currently selected text. The returned string will be empty if
	 * no selection exists.
	 *
	 * @return The selected text
	 */
	public String getSelectedText()
	{
		return getTextBox().getSelectedText();
	}

	/***************************************
	 * @see TextControl#setColumns(int)
	 */
	@Override
	public void setColumns(int nColumns)
	{
		getTextBox().setVisibleLength(nColumns);
	}

	/***************************************
	 * Sets the selection of the text. A length of zero will remove the
	 * selection.
	 *
	 * @param  nStart  The start of the selection
	 * @param  nLength The length of the selection
	 *
	 * @throws IndexOutOfBoundsException If the given selection doesn't fit the
	 *                                   current text
	 */
	public void setSelection(int nStart, int nLength)
	{
		getTextBox().setSelectionRange(nStart, nLength);
	}

	//~ Inner Classes ----------------------------------------------------------

	/********************************************************************
	 * Widget factory for this component.
	 *
	 * @author eso
	 */
	public static class TextFieldWidgetFactory<W extends IsTextControlWidget>
		implements WidgetFactory<W>
	{
		//~ Methods ------------------------------------------------------------

		/***************************************
		 * {@inheritDoc}
		 */
		@Override
		@SuppressWarnings("unchecked")
		public W createWidget(Component rComponent, StyleData rStyle)
		{
			TextFieldStyle eStyle =
				rStyle.getProperty(TEXT_FIELD_STYLE, TextFieldStyle.DEFAULT);

			IsTextControlWidget aTextBox;

			if (eStyle == TextFieldStyle.PASSWORD)
			{
				aTextBox = new GwtPasswordBox();
			}
			else
			{
				aTextBox = new GwtTextField();
			}

			return (W) aTextBox;
		}
	}

	/********************************************************************
	 * A text area subclass that propagates the on paste event.
	 *
	 * @author eso
	 */
	static class GwtPasswordBox extends PasswordTextBox
		implements IsTextControlWidget
	{
		//~ Constructors -------------------------------------------------------

		/***************************************
		 * Creates a new instance.
		 */
		GwtPasswordBox()
		{
			sinkEvents(Event.ONPASTE);
		}

		//~ Methods ------------------------------------------------------------

		/***************************************
		 * {@inheritDoc}
		 */
		@Override
		public void onBrowserEvent(Event rEvent)
		{
			super.onBrowserEvent(rEvent);

			switch (DOM.eventGetType(rEvent))
			{
				case Event.ONPASTE:
					Scheduler.get()
							 .scheduleDeferred(
		 						new ScheduledCommand()
		 						{
		 							@Override
		 							public void execute()
		 							{
		 								ValueChangeEvent.fire(
		 									GwtPasswordBox.this,
		 									getText());
		 							}
		 						});


					break;
			}
		}
	}

	/********************************************************************
	 * A text area subclass that propagates the on paste event.
	 *
	 * @author eso
	 */
	static class GwtTextField extends TextBox implements IsTextControlWidget
	{
		//~ Constructors -------------------------------------------------------

		/***************************************
		 * Creates a new instance.
		 */
		GwtTextField()
		{
			sinkEvents(Event.ONPASTE);
		}

		//~ Methods ------------------------------------------------------------

		/***************************************
		 * {@inheritDoc}
		 */
		@Override
		public void onBrowserEvent(Event rEvent)
		{
			super.onBrowserEvent(rEvent);

			switch (DOM.eventGetType(rEvent))
			{
				case Event.ONPASTE:
					Scheduler.get()
							 .scheduleDeferred(
		 						new ScheduledCommand()
		 						{
		 							@Override
		 							public void execute()
		 							{
		 								ValueChangeEvent.fire(
		 									GwtTextField.this,
		 									getText());
		 							}
		 						});


					break;
			}
		}
	}
}
