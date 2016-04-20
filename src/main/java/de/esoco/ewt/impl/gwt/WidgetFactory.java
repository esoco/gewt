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
package de.esoco.ewt.impl.gwt;

import de.esoco.ewt.UserInterfaceContext;
import de.esoco.ewt.style.StyleData;

import com.google.gwt.user.client.ui.Widget;


/********************************************************************
 * A factory that produces widget instances for a certain component type.
 *
 * @author eso
 */
public interface WidgetFactory<W extends Widget>
{
	//~ Methods ----------------------------------------------------------------

	/***************************************
	 * Creates a new widget instance based on the given style.
	 *
	 * @param  rContext The user interface context to create the widget in
	 * @param  rStyle   The style of the new widget
	 *
	 * @return The new widget instance
	 */
	W createWidget(UserInterfaceContext rContext, StyleData rStyle);
}
