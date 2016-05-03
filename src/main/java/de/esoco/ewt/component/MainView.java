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

import de.esoco.ewt.UserInterfaceContext;
import de.esoco.ewt.impl.gwt.GewtResources;
import de.esoco.ewt.layout.DockLayout;
import de.esoco.ewt.layout.GenericLayout;
import de.esoco.ewt.style.StyleData;

import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.Widget;


/********************************************************************
 * A GWT-specific main view implementation that wraps a root panel.
 *
 * @author eso
 */
public class MainView extends View
{
	//~ Instance fields --------------------------------------------------------

	private final UserInterfaceContext rContext;

	//~ Constructors -----------------------------------------------------------

	/***************************************
	 * Creates a new instance that wraps a certain root panel.
	 *
	 * @param rContext The user interface context this view belongs to
	 */
	public MainView(UserInterfaceContext rContext)
	{
		this.rContext = rContext;

		setLayout(new DockLayout(false, false));
	}

	//~ Methods ----------------------------------------------------------------

	/***************************************
	 * @see Component#getContext()
	 */
	@Override
	public UserInterfaceContext getContext()
	{
		return rContext;
	}

	/***************************************
	 * @see Container#setLayout(GenericLayout)
	 */
	@Override
	public void setLayout(GenericLayout rLayout)
	{
		RootLayoutPanel rRootLayoutPanel = RootLayoutPanel.get();
		Widget		    rWidget			 = getWidget();

		if (rWidget != null)
		{
			rRootLayoutPanel.remove(rWidget);
		}

		super.setLayout(rLayout);
		setWidget(createWidget(rContext, StyleData.DEFAULT));

		rWidget = getWidget();
		setDefaultStyleName(GewtResources.INSTANCE.css().ewtMainView());
		rRootLayoutPanel.add(rWidget);
	}
}
