//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// This file is a part of the 'gewt' project.
// Copyright 2017 Elmar Sonnenschein, esoco GmbH, Flensburg, Germany
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

import de.esoco.ewt.UserInterfaceContext;
import de.esoco.ewt.style.ViewStyle;

import de.esoco.lib.property.TitleAttribute;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.IsWidget;


/********************************************************************
 * The base class for all EWT views.
 *
 * @author eso
 */
public abstract class View extends Container implements TitleAttribute
{
	//~ Instance fields --------------------------------------------------------

	private final UserInterfaceContext rContext;
	private final ViewStyle			   rViewStyle;

	//~ Constructors -----------------------------------------------------------

	/***************************************
	 * Creates a new instance.
	 *
	 * @param rContext   The user interface context
	 * @param rViewPanel The panel to place the view component in
	 * @param rStyle     The view style
	 */
	public View(UserInterfaceContext rContext,
				IsWidget			 rViewPanel,
				ViewStyle			 rStyle)
	{
		this(rContext, rStyle);

		setWidget(rViewPanel);
	}

	/***************************************
	 * Creates a new instance.
	 *
	 * @param rContext The user interface context
	 * @param rStyle   rViewStyle The view style
	 */
	View(UserInterfaceContext rContext, ViewStyle rStyle)
	{
		this.rContext   = rContext;
		this.rViewStyle = rStyle;
	}

	//~ Methods ----------------------------------------------------------------

	/***************************************
	 * Overridden to return the actual UI context.
	 *
	 * @see Container#getContext()
	 */
	@Override
	public UserInterfaceContext getContext()
	{
		return rContext;
	}

	/***************************************
	 * Returns the view title.
	 *
	 * @return The title string
	 */
	@Override
	public String getTitle()
	{
		return Window.getTitle();
	}

	/***************************************
	 * Returns the style of this view.
	 *
	 * @return The view style
	 */
	public final ViewStyle getViewStyle()
	{
		return rViewStyle;
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
	@Override
	public void setTitle(String sTitle)
	{
		Window.setTitle(sTitle);
	}
}
