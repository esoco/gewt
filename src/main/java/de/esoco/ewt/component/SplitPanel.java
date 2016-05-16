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
import de.esoco.ewt.layout.DockLayout;
import de.esoco.ewt.layout.GenericLayout;
import de.esoco.ewt.style.Alignment;
import de.esoco.ewt.style.StyleData;

import de.esoco.lib.property.Layout;

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
public class SplitPanel extends FixedLayoutPanel
{
	//~ Constructors -----------------------------------------------------------

	/***************************************
	 * Creates a new instance.
	 *
	 * @param rParent The parent container
	 * @param rStyle  The panel style
	 */
	public SplitPanel(Container rParent, StyleData rStyle)
	{
		super(EWT.getLayoutFactory()
			  .createLayout(rParent, rStyle, Layout.SPLIT));
	}

	//~ Methods ----------------------------------------------------------------

	/***************************************
	 * {@inheritDoc}
	 */
	@Override
	void addWidget(HasWidgets rContainer, Widget rWidget, StyleData rStyleData)
	{
		((SplitPanelLayout) getLayout()).addWidget(rContainer,
												   rWidget,
												   rStyleData);
	}

	//~ Inner Classes ----------------------------------------------------------

	/********************************************************************
	 * The default layout for this panel.
	 *
	 * @author eso
	 */
	public static class SplitPanelLayout extends GenericLayout
	{
		//~ Methods ------------------------------------------------------------

		/***************************************
		 * Implements the adding of widgets to the layout container widget.
		 *
		 * @see Panel#addWidget(HasWidgets, Widget, StyleData)
		 */
		public void addWidget(HasWidgets rContainer,
							  Widget	 rWidget,
							  StyleData  rStyleData)
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

		/***************************************
		 * {@inheritDoc}
		 */
		@Override
		public HasWidgets createLayoutContainer(
			Container rContainer,
			StyleData rStyle)
		{
			return new SplitLayoutPanel(5);
		}
	}
}
