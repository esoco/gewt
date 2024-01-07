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

import de.esoco.ewt.impl.gwt.GwtSpinner;
import de.esoco.ewt.impl.gwt.WidgetFactory;
import de.esoco.ewt.style.StyleData;

import com.google.gwt.user.client.ui.Widget;

/**
 * A component that allows to enter or modify integer values.
 *
 * @author eso
 */
public class Spinner extends Control {

	private GwtSpinner gwtSpinner;

	/**
	 * Returns the increment for value modifications.
	 *
	 * @return The increment value
	 */
	public final int getIncrement() {
		return gwtSpinner.getIncrement();
	}

	/**
	 * Returns the maximum value.
	 *
	 * @return The maximum value
	 */
	public final int getMaximum() {
		return gwtSpinner.getMaximum();
	}

	/**
	 * Returns the minimum value.
	 *
	 * @return The minimum value
	 */
	public final int getMinimum() {
		return gwtSpinner.getMinimum();
	}

	/**
	 * Returns the current value.
	 *
	 * @return The current value
	 */
	public final int getValue() {
		return gwtSpinner.getValue();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initWidget(Container parent, StyleData style) {
		super.initWidget(parent, style);

		gwtSpinner = (GwtSpinner) getWidget();
	}

	/**
	 * Sets the increment for value modifications.
	 *
	 * @param increment The increment value
	 */
	public final void setIncrement(int increment) {
		gwtSpinner.setIncrement(increment);
	}

	/**
	 * Sets the maximum value.
	 *
	 * @param maximum The maximum value
	 */
	public final void setMaximum(int maximum) {
		gwtSpinner.setMaximum(maximum);
	}

	/**
	 * Sets the minimum value.
	 *
	 * @param minimum The minimum value
	 */
	public final void setMinimum(int minimum) {
		gwtSpinner.setMinimum(minimum);
	}

	/**
	 * Sets the value of this component.
	 *
	 * @param value The value
	 */
	public final void setValue(int value) {
		gwtSpinner.setValue(value);
	}

	/**
	 * Widget factory for this component.
	 *
	 * @author eso
	 */
	public static class SpinnerWidgetFactory implements WidgetFactory<Widget> {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Widget createWidget(Component component, StyleData style) {
			return new GwtSpinner(0, 100, 1);
		}
	}
}
