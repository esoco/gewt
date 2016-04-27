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
import de.esoco.ewt.event.EventType;
import de.esoco.ewt.impl.gwt.GwtSpinner;
import de.esoco.ewt.impl.gwt.WidgetFactory;
import de.esoco.ewt.style.StyleData;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Widget;


/********************************************************************
 * A component that allows to enter or modify integer values.
 *
 * @author eso
 */
public class Spinner extends Control
{
	//~ Static fields/initializers ---------------------------------------------

	static
	{
		EWT.registerWidgetFactory(Spinner.class,
										   new SpinnerWidgetFactory(),
										   false);
	}

	//~ Instance fields --------------------------------------------------------

	private GwtSpinner aGwtSpinner;

	//~ Methods ----------------------------------------------------------------

	/***************************************
	 * Returns the increment for value modifications.
	 *
	 * @return The increment value
	 */
	public final int getIncrement()
	{
		return aGwtSpinner.getIncrement();
	}

	/***************************************
	 * Returns the maximum value.
	 *
	 * @return The maximum value
	 */
	public final int getMaximum()
	{
		return aGwtSpinner.getMaximum();
	}

	/***************************************
	 * Returns the minimum value.
	 *
	 * @return The minimum value
	 */
	public final int getMinimum()
	{
		return aGwtSpinner.getMinimum();
	}

	/***************************************
	 * Returns the current value.
	 *
	 * @return The current value
	 */
	public final int getValue()
	{
		return aGwtSpinner.getValue();
	}

	/***************************************
	 * {@inheritDoc}
	 */
	@Override
	public void initWidget(UserInterfaceContext rContext, StyleData rStyle)
	{
		super.initWidget(rContext, rStyle);

		aGwtSpinner = (GwtSpinner) getWidget();
	}

	/***************************************
	 * Sets the increment for value modifications.
	 *
	 * @param nIncrement The increment value
	 */
	public final void setIncrement(int nIncrement)
	{
		aGwtSpinner.setIncrement(nIncrement);
	}

	/***************************************
	 * Sets the maximum value.
	 *
	 * @param nMaximum The maximum value
	 */
	public final void setMaximum(int nMaximum)
	{
		aGwtSpinner.setMaximum(nMaximum);
	}

	/***************************************
	 * Sets the minimum value.
	 *
	 * @param nMinimum The minimum value
	 */
	public final void setMinimum(int nMinimum)
	{
		aGwtSpinner.setMinimum(nMinimum);
	}

	/***************************************
	 * Sets the value of this component.
	 *
	 * @param nValue The value
	 */
	public final void setValue(int nValue)
	{
		aGwtSpinner.setValue(nValue);
	}

	/***************************************
	 * @see Component#createEventDispatcher()
	 */
	@Override
	ComponentEventDispatcher createEventDispatcher()
	{
		return new SpinnerEventDispatcher();
	}

	//~ Inner Classes ----------------------------------------------------------

	/********************************************************************
	 * Widget factory for this component.
	 *
	 * @author eso
	 */
	public static class SpinnerWidgetFactory implements WidgetFactory<Widget>
	{
		//~ Methods ------------------------------------------------------------

		/***************************************
		 * {@inheritDoc}
		 */
		@Override
		public Widget createWidget(
			Component rComponent,
			StyleData			 rStyle)
		{
			return new GwtSpinner(0, 100, 1);
		}
	}

	/********************************************************************
	 * Dispatcher for list-specific events.
	 *
	 * @author eso
	 */
	class SpinnerEventDispatcher extends ComponentEventDispatcher
		implements ValueChangeHandler<Integer>
	{
		//~ Methods ------------------------------------------------------------

		/***************************************
		 * @see ValueChangeHandler#onValueChange(ValueChangeEvent)
		 */
		@Override
		public void onValueChange(ValueChangeEvent<Integer> rEvent)
		{
			notifyEventHandler(EventType.VALUE_CHANGED);
		}

		/***************************************
		 * @see ComponentEventDispatcher#initEventDispatching(Widget)
		 */
		@Override
		void initEventDispatching(Widget rWidget)
		{
			super.initEventDispatching(rWidget);

			((GwtSpinner) rWidget).addValueChangeHandler(this);
		}
	}
}
