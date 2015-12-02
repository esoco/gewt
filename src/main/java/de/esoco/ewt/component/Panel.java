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

import de.esoco.ewt.layout.GenericLayout;

import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.HasWidgets;


/********************************************************************
 * A panel that can contain arbitrary child widgets.
 *
 * @author eso
 */
public class Panel extends Container
{
	//~ Constructors -----------------------------------------------------------

	/***************************************
	 * Creates a new instance that wraps a certain GWT panel.
	 *
	 * @param rPanel The panel to wrap
	 */
	public Panel(com.google.gwt.user.client.ui.Panel rPanel)
	{
		super(rPanel);
	}

	/***************************************
	 * Creates a new instance based on a certain layout.
	 *
	 * @param rLayout The layout
	 */
	public Panel(GenericLayout rLayout)
	{
		super(rLayout);
	}

	/***************************************
	 * Internal constructor for subclassing.
	 */
	Panel()
	{
	}

	/***************************************
	 * Internal constructor for subclassing.
	 *
	 * @param rWidgetContainer The widget container of this instance
	 */
	Panel(HasWidgets rWidgetContainer)
	{
		super(rWidgetContainer);
	}

	//~ Methods ----------------------------------------------------------------

	/***************************************
	 * Overridden to sink GWT events so that events will be created.
	 *
	 * @see Container#createEventDispatcher()
	 */
	@Override
	ComponentEventDispatcher createEventDispatcher()
	{
		getWidget().sinkEvents(Event.ONCLICK);

		return super.createEventDispatcher();
	}
}
