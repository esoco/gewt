//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// This file is a part of the 'gewt' project.
// Copyright 2019 Elmar Sonnenschein, esoco GmbH, Flensburg, Germany
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
import de.esoco.ewt.style.StyleData;

import de.esoco.lib.property.Alignment;
import de.esoco.lib.property.LayoutType;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

import static de.esoco.lib.property.StyleProperties.SPLITTER_SIZE;

/**
 * A panel that contains two components that are separated by a draggable
 * separator that allows to dynamically distribute the area available to the
 * child components. The alignment values in the {@link StyleData} objects of
 * added components will define their placement similar to {@link DockLayout}.
 * In GWT the center widget must always be added last to a split panel.
 *
 * @author eso
 */
public class SplitPanel extends FixedLayoutPanel {

	/**
	 * Creates a new instance.
	 *
	 * @param parent The parent container
	 * @param style  The panel style
	 */
	public SplitPanel(Container parent, StyleData style) {
		super(EWT
			.getLayoutFactory()
			.createLayout(parent, style, LayoutType.SPLIT));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void addWidget(HasWidgets container, Widget widget, StyleData styleData) {
		((SplitPanelLayout) getLayout()).addWidget(container, widget,
			styleData);
	}

	/**
	 * The default layout for this panel.
	 *
	 * @author eso
	 */
	public static class SplitPanelLayout extends GenericLayout {

		/**
		 * Implements the adding of widgets to the layout container widget.
		 *
		 * @see Panel#addWidget(HasWidgets, Widget, StyleData)
		 */
		public void addWidget(HasWidgets container, Widget widget,
			StyleData styleData) {
			SplitLayoutPanel splitLayoutPanel = (SplitLayoutPanel) container;

			Alignment verticalAlign = styleData.getVerticalAlignment();

			if (verticalAlign == Alignment.BEGIN ||
				verticalAlign == Alignment.END) {
				widget.setHeight("100%");
			} else {
				widget.setWidth("100%");
			}

			if (!DockLayout.addDockLayoutPanelWidget(widget, splitLayoutPanel,
				styleData, true)) {
				splitLayoutPanel.setWidgetToggleDisplayAllowed(widget, true);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public HasWidgets createLayoutContainer(Container container,
			StyleData style) {
			return new SplitLayoutPanel(style.getIntProperty(SPLITTER_SIZE,
				5));
		}
	}
}
