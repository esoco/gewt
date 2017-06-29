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
package de.esoco.ewt.layout;

import de.esoco.ewt.component.Container;
import de.esoco.ewt.style.StyleData;

import de.esoco.lib.property.Alignment;

import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;


/********************************************************************
 * A dock layout that allows to add components on the four sides or in the
 * center. The center component must be added last. This implementation is based
 * on a GWT {@link DockLayoutPanel}.
 *
 * @author eso
 */
public class DockLayout extends GenericLayout
{
	//~ Instance fields --------------------------------------------------------

	private boolean bScrollable;
	private boolean bPixelUnits;

	//~ Constructors -----------------------------------------------------------

	/***************************************
	 * Creates a new instance.
	 *
	 * @param bPixelUnits TRUE to set border areas in pixels, FALSE to use EM
	 * @param bScrollable TRUE if the content of the layout should be scrollable
	 */
	public DockLayout(boolean bPixelUnits, boolean bScrollable)
	{
		this.bPixelUnits = bPixelUnits;
		this.bScrollable = bScrollable;
	}

	//~ Static methods ---------------------------------------------------------

	/***************************************
	 * Internal method to add a widget to a certain {@link DockLayoutPanel}
	 * instance. The widget position will be determined from it's style data. In
	 * GWT it is important to add the center widget as the last widget.
	 *
	 * @param  rWidget     The widget to add
	 * @param  rDockPanel  The target dock layout panel
	 * @param  rStyleData  The style data of the widget
	 * @param  bScrollable TRUE if the element widgets should be marked as
	 *                     scrollable with overflow set to auto
	 *
	 * @return TRUE if the added widget was the center widget (and therefore the
	 *         last widget to be added)
	 */
	public static boolean addDockLayoutPanelWidget(Widget		   rWidget,
												   DockLayoutPanel rDockPanel,
												   StyleData	   rStyleData,
												   boolean		   bScrollable)
	{
		Alignment eVerticalAlign   = rStyleData.getVerticalAlignment();
		Alignment eHorizontalAlign = rStyleData.getHorizontalAlignment();
		String    sLayoutPosition  = "Center";
		boolean   bCenterWidget    = false;

		double fHeight = rStyleData.getHeight();

		switch (eVerticalAlign)
		{
			case BEGIN:
				sLayoutPosition = "Top";
				rDockPanel.addNorth(rWidget, fHeight);

				break;

			case END:
				sLayoutPosition = "Bottom";
				rDockPanel.addSouth(rWidget, fHeight);

				break;

			default:

				double fWidth = rStyleData.getWidth();

				switch (eHorizontalAlign)
				{
					case BEGIN:
						sLayoutPosition = "Left";
						rDockPanel.addWest(rWidget, fWidth);

						break;

					case END:
						sLayoutPosition = "Right";
						rDockPanel.addEast(rWidget, fWidth);

						break;

					default:
						rDockPanel.add(rWidget);
						bCenterWidget = true;
				}
		}

		rWidget.getElement().getParentElement()
			   .addClassName("ewt-Layout" + sLayoutPosition);

		if (bScrollable)
		{
			// set parent DIV of center widget to automatic scrollbars
			rWidget.getElement().getParentElement().getStyle()
				   .setOverflow(Overflow.AUTO);
		}

		return bCenterWidget;
	}

	//~ Methods ----------------------------------------------------------------

	/***************************************
	 * Adds the widget with the alignment defined in the style data. Horizontal
	 * alignment has precedence over vertical alignment.
	 *
	 * @see GenericLayout#addWidget(HasWidgets, Widget, StyleData, int)
	 */
	@Override
	public void addWidget(HasWidgets rContainer,
						  Widget	 rWidget,
						  StyleData  rStyleData,
						  int		 nIndex)
	{
		addDockLayoutPanelWidget(rWidget,
								 (DockLayoutPanel) rContainer,
								 rStyleData,
								 bScrollable);
	}

	/***************************************
	 * {@inheritDoc}
	 */
	@Override
	public Panel createLayoutContainer(
		Container rContainer,
		StyleData rContainerStyle)
	{
		DockLayoutPanel aPanel =
			new DockLayoutPanel(bPixelUnits ? Unit.PX : Unit.EM);

		aPanel.addStyleName("ewt-DockLayout");

		return aPanel;
	}
}
