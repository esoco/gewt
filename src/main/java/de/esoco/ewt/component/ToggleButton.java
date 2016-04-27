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
import de.esoco.ewt.style.StyleData;

import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HasHTML;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Widget;


/********************************************************************
 * A button component that can be toggled between two visual states.
 *
 * @author eso
 */
public class ToggleButton extends SelectableButton
{
	//~ Static fields/initializers ---------------------------------------------

	static
	{
		EWT.registerWidgetFactory(ToggleButton.class,
										   new ToggleButtonWidgetFactory<>(),
										   false);
	}

	//~ Methods ----------------------------------------------------------------

	/***************************************
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings({ "unchecked", "boxing" })
	public boolean isSelected()
	{
		return ((HasValue<Boolean>) getWidget()).getValue();
	}

	/***************************************
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings({ "unchecked", "boxing" })
	public void setSelected(boolean bSelected)
	{
		((HasValue<Boolean>) getWidget()).setValue(bSelected);
	}

	//~ Inner Classes ----------------------------------------------------------

	/********************************************************************
	 * Widget factory for this component.
	 *
	 * @author eso
	 */
	public static class ToggleButtonWidgetFactory<W extends Widget & Focusable & HasHTML & HasValue<Boolean>>
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
			return (W) new com.google.gwt.user.client.ui.ToggleButton();
		}
	}
}
