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

	private GwtSpinner aGwtSpinner;

	/**
	 * Returns the increment for value modifications.
	 *
	 * @return The increment value
	 */
	public final int getIncrement() {
		return aGwtSpinner.getIncrement();
	}

	/**
	 * Returns the maximum value.
	 *
	 * @return The maximum value
	 */
	public final int getMaximum() {
		return aGwtSpinner.getMaximum();
	}

	/**
	 * Returns the minimum value.
	 *
	 * @return The minimum value
	 */
	public final int getMinimum() {
		return aGwtSpinner.getMinimum();
	}

	/**
	 * Returns the current value.
	 *
	 * @return The current value
	 */
	public final int getValue() {
		return aGwtSpinner.getValue();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initWidget(Container rParent, StyleData rStyle) {
		super.initWidget(rParent, rStyle);

		aGwtSpinner = (GwtSpinner) getWidget();
	}

	/**
	 * Sets the increment for value modifications.
	 *
	 * @param nIncrement The increment value
	 */
	public final void setIncrement(int nIncrement) {
		aGwtSpinner.setIncrement(nIncrement);
	}

	/**
	 * Sets the maximum value.
	 *
	 * @param nMaximum The maximum value
	 */
	public final void setMaximum(int nMaximum) {
		aGwtSpinner.setMaximum(nMaximum);
	}

	/**
	 * Sets the minimum value.
	 *
	 * @param nMinimum The minimum value
	 */
	public final void setMinimum(int nMinimum) {
		aGwtSpinner.setMinimum(nMinimum);
	}

	/**
	 * Sets the value of this component.
	 *
	 * @param nValue The value
	 */
	public final void setValue(int nValue) {
		aGwtSpinner.setValue(nValue);
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
		public Widget createWidget(Component rComponent, StyleData rStyle) {
			return new GwtSpinner(0, 100, 1);
		}
	}
}
