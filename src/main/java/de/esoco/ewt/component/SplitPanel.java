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

import de.esoco.ewt.layout.DockLayout;
import de.esoco.ewt.layout.GenericLayout;
import de.esoco.ewt.style.Alignment;
import de.esoco.ewt.style.StyleData;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.Widget;


/********************************************************************
 * A panel that contains two components that are separated by a draggable
 * separator that allows to dynamically distribute the area available to the
 * child components. The alignment values in the {@link StyleData} objects of
 * added components will define their placement similar to {@link DockLayout}.
 * In GWT the center widget must always be added last to a split panel.
 *
 * @author eso
 */
public class SplitPanel extends Panel
{
	//~ Constructors -----------------------------------------------------------

	/***************************************
	 * Creates a new instance.
	 */
	public SplitPanel()
	{
		super(new SplitLayoutPanel(5));
	}

	//~ Methods ----------------------------------------------------------------

	/***************************************
	 * Overridden to throw an exception.
	 *
	 * @see Panel#setLayout(GenericLayout)
	 */
	@Override
	public void setLayout(GenericLayout rLayout)
	{
		throw new UnsupportedOperationException("Layout of " +
												getClass().getSimpleName() +
												" cannot be changed");
	}

	/***************************************
	 * {@inheritDoc}
	 */
	@Override
	void addWidget(HasWidgets rContainer, Widget rWidget, StyleData rStyleData)
	{
		SplitLayoutPanel rSplitLayoutPanel = (SplitLayoutPanel) rContainer;

		Alignment eVerticalAlign = rStyleData.getVerticalAlignment();

		if (eVerticalAlign == Alignment.BEGIN ||
			eVerticalAlign == Alignment.END)
		{
			rWidget.setHeight("100%");
		}
		else
		{
			rWidget.setWidth("100%");
		}

		if (!DockLayout.addDockLayoutPanelWidget(rWidget,
												 rSplitLayoutPanel,
												 rStyleData,
												 true))
		{
			rSplitLayoutPanel.setWidgetToggleDisplayAllowed(rWidget, true);
		}
	}
}
