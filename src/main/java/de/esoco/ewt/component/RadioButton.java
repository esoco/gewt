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

import de.esoco.ewt.style.StyleData;


/********************************************************************
 * A radio button component that allows only one button in the same container to
 * be selected at the same time.
 *
 * @author eso
 */
public class RadioButton extends CheckBox
{
	//~ Methods ----------------------------------------------------------------

	/***************************************
	 * {@inheritDoc}
	 */
	@Override
	public void initWidget(Container rParent, StyleData rStyle)
	{
		super.initWidget(rParent, rStyle);

		String sId = rParent.getId();

		((com.google.gwt.user.client.ui.RadioButton) getWidget()).setName(sId);
	}

	//~ Inner Classes ----------------------------------------------------------

	/********************************************************************
	 * Widget factory for this component.
	 *
	 * @author eso
	 */
	public static class RadioButtonWidgetFactory
		extends CheckBoxWidgetFactory<com.google.gwt.user.client.ui.RadioButton>
	{
		//~ Methods ------------------------------------------------------------

		/***************************************
		 * {@inheritDoc}
		 */
		@Override
		public com.google.gwt.user.client.ui.RadioButton createWidget(
			Component rComponent,
			StyleData rStyle)
		{
			return new com.google.gwt.user.client.ui.RadioButton("");
		}
	}
}
