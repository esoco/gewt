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

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;


/********************************************************************
 * The interface for factories that produce widget instances for a certain GEWT
 * component type. Implementations of the interface method can either return
 * subclasses of {@link Widget} or some kind of widget wrapper that implements
 * the interface {@link IsWidget}. The latter can be used to implement
 * additional interfaces that are not directly available in the wrapped widget.
 * The needed interfaces are documented in the GWT widget factories of the
 * original GEWT components which should typically be subclassed by new
 * factories.
 *
 * @author eso
 */
public interface WidgetFactory<W extends IsWidget>
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
