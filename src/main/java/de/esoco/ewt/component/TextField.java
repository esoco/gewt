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
import de.esoco.ewt.impl.gwt.WidgetFactory;
import de.esoco.ewt.style.StyleData;
import de.esoco.ewt.style.StyleFlag;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.TextBoxBase;


/********************************************************************
 * A text component subclass that allows single-line text input.
 *
 * <p>Supported style flags:</p>
 *
 * <ul>
 *   <li>{@link StyleFlag#PASSWORD PASSWORD}: for hidden password input</li>
 * </ul>
 *
 * @author eso
 */
public class TextField extends TextComponent
{
	//~ Static fields/initializers ---------------------------------------------

	static
	{
		EWT.registerWidgetFactory(TextField.class,
										   new TextFieldWidgetFactory(),
										   false);
	}

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
	 * @see TextComponent#setColumns(int)
	 */
	@Override
	public void setColumns(int nColumns)
	{
		((TextBox) getWidget()).setVisibleLength(nColumns);
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
	public static class TextFieldWidgetFactory
		implements WidgetFactory<TextBoxBase>
	{
		//~ Methods ------------------------------------------------------------

		/***************************************
		 * {@inheritDoc}
		 */
		@Override
		public TextBoxBase createWidget(
			UserInterfaceContext rContext,
			StyleData			 rStyle)
		{
			TextBoxBase aTextBox;

			if (rStyle.hasFlag(StyleFlag.PASSWORD))
			{
				aTextBox = new PasswordTextBox();
			}
			else
			{
				aTextBox = new GwtTextField();
			}

			return aTextBox;
		}
	}

	/********************************************************************
	 * A text area subclass that propagates the on paste event.
	 *
	 * @author eso
	 */
	static class GwtTextField extends TextBox
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
		 * @see com.google.gwt.user.client.ui.TextArea#onBrowserEvent(Event)
		 */
		@Override
		public void onBrowserEvent(Event rEvent)
		{
			super.onBrowserEvent(rEvent);

			switch (DOM.eventGetType(rEvent))
			{
				case Event.ONPASTE:
					Scheduler.get()
							 .scheduleDeferred(new ScheduledCommand()
						{
							@Override
							public void execute()
							{
								ValueChangeEvent.fire(GwtTextField.this,
													  getText());
							}
						});


					break;
			}
		}
	}
}
