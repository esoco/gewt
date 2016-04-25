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

import de.esoco.ewt.impl.gwt.WidgetWrapper;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Panel;


/********************************************************************
 * The base class for all EWT views.
 *
 * @author eso
 */
public abstract class View extends Container
{
	//~ Constructors -----------------------------------------------------------

	/***************************************
	 * Creates a new instance.
	 *
	 * @see Container#Container(Panel)
	 */
	public View(Panel rPanel)
	{
		setWidgetWrapper(new WidgetWrapper(rPanel));
	}

	/***************************************
	 * @see Container#Container()
	 */
	View()
	{
	}

	//~ Methods ----------------------------------------------------------------

	/***************************************
	 * Returns the view title.
	 *
	 * @return The title string
	 */
	public String getTitle()
	{
		return Window.getTitle();
	}

	/***************************************
	 * Packs this view for display on the screen. This method exists mainly for
	 * compatibility with other EWT implementations and has no function on GWT.
	 */
	public void pack()
	{
	}

	/***************************************
	 * Sets the view title.
	 *
	 * @param sTitle The new title
	 */
	public void setTitle(String sTitle)
	{
		Window.setTitle(sTitle);
	}
}
