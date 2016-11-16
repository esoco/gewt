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

import de.esoco.ewt.event.EventType;
import de.esoco.ewt.style.StyleData;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HasHTML;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Widget;


/********************************************************************
 * A button subclass that implements a selectable checkbox.
 *
 * @author eso
 */
public class CheckBox extends SelectableButton
{
	//~ Methods ----------------------------------------------------------------

	/***************************************
	 * @see SelectableButton#isSelected()
	 */
	@Override
	@SuppressWarnings("unchecked")
	public boolean isSelected()
	{
		return ((HasValue<Boolean>) getWidget()).getValue().booleanValue();
	}

	/***************************************
	 * Sets the selected state of this button (if supported by the underlying
	 * GWT widget).
	 *
	 * @param bSelected The new selected state
	 */
	@Override
	@SuppressWarnings({ "boxing", "unchecked" })
	public void setSelected(boolean bSelected)
	{
		((HasValue<Boolean>) getWidget()).setValue(bSelected);
	}

	/***************************************
	 * @see Component#createEventDispatcher()
	 */
	@Override
	ComponentEventDispatcher createEventDispatcher()
	{
		return new CheckBoxEventDispatcher();
	}

	//~ Inner Classes ----------------------------------------------------------

	/********************************************************************
	 * Widget factory for this component.
	 *
	 * @author eso
	 */
	public static class CheckBoxWidgetFactory<W extends Widget & Focusable & HasHTML & HasValue<Boolean>>
		extends ButtonWidgetFactory<W>
	{
		//~ Methods ------------------------------------------------------------

		/***************************************
		 * {@inheritDoc}
		 */
		@Override
		@SuppressWarnings("unchecked")
		public W createWidget(Component rComponent, StyleData rStyle)
		{
			return (W) new com.google.gwt.user.client.ui.CheckBox();
		}
	}

	/********************************************************************
	 * Dispatcher for list-specific events.
	 *
	 * @author eso
	 */
	class CheckBoxEventDispatcher extends ComponentEventDispatcher
	{
		//~ Methods ------------------------------------------------------------

		/***************************************
		 * {@inheritDoc}
		 */
		@Override
		public void onClick(ClickEvent rEvent)
		{
			notifyEventHandler(EventType.POINTER_CLICKED, rEvent);
		}

		/***************************************
		 * {@inheritDoc}
		 */
		@Override
		public void onValueChange(ValueChangeEvent<Object> rEvent)
		{
			notifyEventHandler(EventType.ACTION);
		}

		/***************************************
		 * {@inheritDoc}
		 */
		@Override
		@SuppressWarnings("unchecked")
		protected void initEventDispatching(
			Widget    rWidget,
			EventType eEventType)
		{
			super.initEventDispatching(rWidget, eEventType);

			if (eEventType == EventType.ACTION &&
				rWidget instanceof HasValueChangeHandlers<?>)
			{
				((HasValueChangeHandlers<Object>) rWidget)
				.addValueChangeHandler(this);
			}
		}
	}
}
