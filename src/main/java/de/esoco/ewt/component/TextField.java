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
package de.esoco.ewt.component;

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
	//~ Constructors -----------------------------------------------------------

	/***************************************
	 * Creates a new instance.
	 *
	 * @param bPassword TRUE for a text field for hidden password input
	 */
	public TextField(boolean bPassword)
	{
		super(createTextBox(bPassword));
	}

	//~ Static methods ---------------------------------------------------------

	/***************************************
	 * Helper method to create the correct GWT text component.
	 *
	 * @param  bPassword TRUE for a text field for hidden password input
	 *
	 * @return A new GWT text box instance
	 */
	private static TextBoxBase createTextBox(boolean bPassword)
	{
		if (bPassword)
		{
			return new PasswordTextBox();
		}
		else
		{
			return new GwtTextField();
		}
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
