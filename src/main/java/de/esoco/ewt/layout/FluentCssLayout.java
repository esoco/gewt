//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// This file is a part of the 'gewt' project.
// Copyright 2018 Elmar Sonnenschein, esoco GmbH, Flensburg, Germany
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

import de.esoco.lib.property.Fluent;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;

/**
 * A base class for CSS-based layouts that implement a fluent interface for the
 * layout configuration. Subclasses must implement the method
 * {@link #applyLayoutStyle(StyleData, Style)} and typically also
 * {@link #addWidget(HasWidgets, com.google.gwt.user.client.ui.Widget,
 * StyleData, int)} to set the style that are CSS speci
 *
 * @author eso
 */
public abstract class FluentCssLayout<L extends FluentCssLayout<L>>
	extends GenericLayout implements Fluent<L> {

	/**
	 * Creates a new instance.
	 */
	public FluentCssLayout() {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addWidget(HasWidgets container, Widget widget,
		StyleData styleData, int index) {
		super.addWidget(container, widget, styleData, index);

		Style style = widget.getElement().getStyle();

		applyWidgetStyle(styleData, style);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public HasWidgets createLayoutContainer(Container container,
		StyleData styleData) {
		FlowPanel panel = new FlowPanel();
		Style style = panel.getElement().getStyle();

		applyLayoutStyle(styleData, style);

		return panel;
	}

	/**
	 * Must be implemented to apply the CSS properties for the layout
	 * parameters
	 * to the style of the layout container.
	 *
	 * @param styleData The style data of the EWT container
	 * @param style     The style of the layout container
	 */
	protected abstract void applyLayoutStyle(StyleData styleData, Style style);

	/**
	 * Must be implemented to apply the CSS layout properties for a widget when
	 * it is added by {@link #addWidget(HasWidgets, Widget, StyleData, int)}.
	 *
	 * @param styleData The style data of the EWT component
	 * @param style     The target style to set the layout properties on
	 */
	protected abstract void applyWidgetStyle(StyleData styleData, Style style);
}
