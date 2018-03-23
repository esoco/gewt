//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// This file is a part of the 'gewt' project.
// Copyright 2018 Elmar Sonnenschein, esoco GmbH, Flensburg, Germany
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

import de.esoco.ewt.component.Component;
import de.esoco.ewt.style.StyleData;


/********************************************************************
 * An interface for the global handling of widget style updates. Registered with
 * {@link Component#setWidgetStyleHandler(WidgetStyleHandler)}.
 *
 * @author eso
 */
public interface WidgetStyleHandler
{
	//~ Methods ----------------------------------------------------------------

	/***************************************
	 * Will be invoked to apply a new style to a component widget.
	 *
	 * @param rComponent rWidget The component
	 * @param rStyle     The new style
	 */
	public void applyWidgetStyle(Component rComponent, StyleData rStyle);
}
