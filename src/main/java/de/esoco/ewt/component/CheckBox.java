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

import de.esoco.ewt.event.EventType;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Widget;


/********************************************************************
 * A button subclass that implements a selectable checkbox.
 *
 * @author eso
 */
public class CheckBox extends SelectableButton
{
	/***************************************
	 * Creates a new instance.
	 */
	public CheckBox()
	{
		this(new com.google.gwt.user.client.ui.CheckBox());
	}

	/***************************************
	 * Subclass constructor.
	 *
	 * @param rButtonWidget The button widget
	 */
	protected CheckBox(com.google.gwt.user.client.ui.CheckBox rButtonWidget)
	{
		super(rButtonWidget);
	}

	/***************************************
	 * @see SelectableButton#isSelected()
	 */
	@Override
	public boolean isSelected()
	{
		return ((com.google.gwt.user.client.ui.CheckBox) getWidget()).getValue()
																	 .booleanValue();
	}

	/***************************************
	 * Sets the selected state of this button (if supported by the underlying
	 * GWT widget).
	 *
	 * @param bSelected The new selected state
	 */
	@Override
	@SuppressWarnings("boxing")
	public void setSelected(boolean bSelected)
	{
		((com.google.gwt.user.client.ui.CheckBox) getWidget()).setValue(bSelected);
	}

	/***************************************
	 * @see Component#createEventDispatcher()
	 */
	@Override
	ComponentEventDispatcher createEventDispatcher()
	{
		return new CheckBoxEventDispatcher();
	}

	/********************************************************************
	 * Dispatcher for list-specific events.
	 *
	 * @author eso
	 */
	class CheckBoxEventDispatcher extends ComponentEventDispatcher
		implements ValueChangeHandler<Boolean>
	{
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
		public void onValueChange(ValueChangeEvent<Boolean> rEvent)
		{
			notifyEventHandler(EventType.ACTION);
		}

		/***************************************
		 * {@inheritDoc}
		 */
		@Override
		void initEventDispatching(Widget rWidget)
		{
			super.initEventDispatching(rWidget);

			((com.google.gwt.user.client.ui.CheckBox) rWidget)
			.addValueChangeHandler(this);
		}
	}
}
