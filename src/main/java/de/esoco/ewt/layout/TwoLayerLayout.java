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
package de.esoco.ewt.layout;

import de.esoco.ewt.EWT;
import de.esoco.ewt.component.Container;
import de.esoco.ewt.style.StyleData;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

/**
 * A base class for layouts that contain two layers of GWT panels. It wraps a
 * content panel inside another panel. All widget manipulations will be
 * forwarded to the the content panel while the outer panel defines the layout
 * of the container this layout is set on. The default implementation inherited
 * by subclasses creates a {@link FlowPanel} inside the subclass-specific layout
 * container.
 *
 * @author eso
 */
public abstract class TwoLayerLayout extends GenericLayout {

	private Panel aContentPanel;

	/**
	 * Creates a new instance.
	 */
	public TwoLayerLayout() {
	}

	/**
	 * Overridden to add the given widget to the inner layer, i.e. the content
	 * panel instead of the enclosing container.
	 *
	 * @see GenericLayout#addWidget(HasWidgets, Widget, StyleData, int)
	 */
	@Override
	public void addWidget(HasWidgets rContainer, Widget rWidget,
		StyleData rStyleData, int nIndex) {
		super.addWidget(aContentPanel, rWidget, rStyleData, nIndex);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void clear(HasWidgets rContainer) {
		super.clear(aContentPanel);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public HasWidgets createLayoutContainer(Container rContainer,
		StyleData rContainerStyle) {
		HasWidgets rLayoutPanel =
			createLayoutWidget(rContainer, rContainerStyle);

		aContentPanel = createContentPanel(rLayoutPanel);
		aContentPanel.setStyleName(EWT.CSS.ewtContentPanel());
		rLayoutPanel.add(aContentPanel);

		return rLayoutPanel;
	}

	/**
	 * Overridden to remove the given widget from the inner layer, i.e. the
	 * content panel instead of the enclosing container.
	 *
	 * @see GenericLayout#removeWidget(HasWidgets, Widget)
	 */
	@Override
	public void removeWidget(HasWidgets rContainer, Widget rWidget) {
		super.removeWidget(aContentPanel, rWidget);
	}

	/**
	 * Creates the inner panel that will contain the content widgets. The
	 * default implementation adds a {@link FlowPanel} but subclasses can
	 * override this method to create a different panel if necessary.
	 *
	 * @param rLayoutPanel The outer panel that the content panel should be
	 *                     added to
	 * @return The inner content panel
	 */
	protected Panel createContentPanel(HasWidgets rLayoutPanel) {
		return new FlowPanel();
	}

	/**
	 * Must be implemented to create the outer panel of this instance.
	 *
	 * @param rContainer      The container to create the layout panel for
	 * @param rContainerStyle The style data for the container
	 * @return The widget for the outer layout
	 */
	protected abstract HasWidgets createLayoutWidget(Container rContainer,
		StyleData rContainerStyle);
}
