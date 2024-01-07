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

/**
 * A dock layout that allows to add components on the four sides or in the
 * center. The center component must be added last. This implementation is based
 * on a GWT {@link DockLayoutPanel}.
 *
 * @author eso
 */
public class DockLayout extends GenericLayout {

	private boolean scrollable;

	private boolean pixelUnits;

	/**
	 * Creates a new instance.
	 *
	 * @param pixelUnits TRUE to set border areas in pixels, FALSE to use EM
	 * @param scrollable TRUE if the content of the layout should be scrollable
	 */
	public DockLayout(boolean pixelUnits, boolean scrollable) {
		this.pixelUnits = pixelUnits;
		this.scrollable = scrollable;
	}

	/**
	 * Internal method to add a widget to a certain {@link DockLayoutPanel}
	 * instance. The widget position will be determined from it's style data .
	 * In GWT it is important to add the center widget as the last widget.
	 *
	 * @param widget     The widget to add
	 * @param dockPanel  The target dock layout panel
	 * @param styleData  The style data of the widget
	 * @param scrollable TRUE if the element widgets should be marked as
	 *                   scrollable with overflow set to auto
	 * @return TRUE if the added widget was the center widget (and therefore
	 * the
	 * last widget to be added)
	 */
	public static boolean addDockLayoutPanelWidget(Widget widget,
		DockLayoutPanel dockPanel, StyleData styleData, boolean scrollable) {
		Alignment verticalAlign = styleData.getVerticalAlignment();
		Alignment horizontalAlign = styleData.getHorizontalAlignment();
		String layoutPosition = "Center";
		boolean centerWidget = false;

		double height = styleData.getHeight();

		switch (verticalAlign) {
			case BEGIN:
				layoutPosition = "Top";
				dockPanel.addNorth(widget, height);

				break;

			case END:
				layoutPosition = "Bottom";
				dockPanel.addSouth(widget, height);

				break;

			default:

				double width = styleData.getWidth();

				switch (horizontalAlign) {
					case BEGIN:
						layoutPosition = "Left";
						dockPanel.addWest(widget, width);

						break;

					case END:
						layoutPosition = "Right";
						dockPanel.addEast(widget, width);

						break;

					default:
						dockPanel.add(widget);
						centerWidget = true;
				}
		}

		widget
			.getElement()
			.getParentElement()
			.addClassName("ewt-Layout" + layoutPosition);

		if (scrollable) {
			// set parent DIV of center widget to automatic scrollbars
			widget
				.getElement()
				.getParentElement()
				.getStyle()
				.setOverflow(Overflow.AUTO);
		}

		return centerWidget;
	}

	/**
	 * Adds the widget with the alignment defined in the style data. Horizontal
	 * alignment has precedence over vertical alignment.
	 *
	 * @see GenericLayout#addWidget(HasWidgets, Widget, StyleData, int)
	 */
	@Override
	public void addWidget(HasWidgets container, Widget widget,
		StyleData styleData, int index) {
		addDockLayoutPanelWidget(widget, (DockLayoutPanel) container,
			styleData,
			scrollable);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Panel createLayoutContainer(Container container,
		StyleData containerStyle) {
		DockLayoutPanel panel =
			new DockLayoutPanel(pixelUnits ? Unit.PX : Unit.EM);

		panel.addStyleName("ewt-DockLayout");

		return panel;
	}
}
