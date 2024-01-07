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

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.HTMLTable.CellFormatter;
import com.google.gwt.user.client.ui.HasHorizontalAlignment.HorizontalAlignmentConstant;
import com.google.gwt.user.client.ui.HasVerticalAlignment.VerticalAlignmentConstant;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.InsertPanel;
import com.google.gwt.user.client.ui.Widget;
import de.esoco.ewt.component.Container;
import de.esoco.ewt.style.StyleData;
import de.esoco.lib.property.HasCssName;
import de.esoco.lib.property.PropertyName;

/**
 * Base class for all EWT layouts.
 *
 * @author eso
 */
public abstract class GenericLayout {

	/**
	 * Adds a certain widget to a widget container according to this layout and
	 * the given style data. This default implementation ignores the style data
	 * and either invokes {@link HasWidgets#remove(Widget)} or, depending on
	 * the
	 * index and container type, {@link InsertPanel#insert(Widget, int)}.
	 * Subclasses may override this method to implement a different layout
	 * strategy.
	 *
	 * @param container The container to add the widget to
	 * @param widget    The widget to add
	 * @param styleData The style defining the layout position of the widget
	 * @param index     The index to add the widget add (if supported by the
	 *                  implementation) or -1 to add as the last widget
	 */
	public void addWidget(HasWidgets container, Widget widget,
		StyleData styleData, int index) {
		if (index >= 0 && container instanceof InsertPanel) {
			((InsertPanel) container).insert(widget, index);
		} else {
			container.add(widget);
		}
	}

	/**
	 * Removes all widgets from this layout's container.
	 *
	 * @param container The container to clear
	 */
	public void clear(HasWidgets container) {
		container.clear();
	}

	/**
	 * Creates a new GWT container widget that represents this layout. This
	 * method is only intended to be used internally by the GEWT framework.
	 *
	 * @param container The GEWT container to create the widget for
	 * @param styleData The style of the container widget
	 * @return A new widget container
	 */
	public abstract HasWidgets createLayoutContainer(Container container,
		StyleData styleData);

	/**
	 * Removes a certain widget from a widget container according to this
	 * layout
	 * and the given style data. This default implementation just invokes the
	 * method {@link HasWidgets#remove(Widget)}. Subclasses may override this
	 * method to implement a different layout strategy.
	 *
	 * @param container The container to remove the widget from
	 * @param widget    The widget to remove
	 */
	public void removeWidget(HasWidgets container, Widget widget) {
		container.remove(widget);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return getClass().getSimpleName();
	}

	/**
	 * Sets a string property on an element style if it is not NULL.
	 *
	 * @param property The property name
	 * @param style    The element style
	 * @param value    The property value
	 */
	protected void setStyleProperty(String property, Style style,
		String value) {
		if (value != null) {
			style.setProperty(property, value);
		}
	}

	/**
	 * Sets a CSS name property on an element style if it is not NULL.
	 *
	 * @param property The property name
	 * @param style    The element style
	 * @param value    The property value
	 */
	protected void setStyleProperty(String property, Style style,
		HasCssName value) {
		if (value != null) {
			style.setProperty(property, value.getCssName());
		}
	}

	/**
	 * Sets a style property from a component style property if it exists.
	 *
	 * @param propertyName The property to get from the component style
	 * @param styleData    The component style
	 * @param property     The name of the target property
	 * @param style        The target style
	 */
	protected void setStyleProperty(PropertyName<?> propertyName,
		StyleData styleData, String property, Style style) {
		Object value = styleData.getProperty(propertyName, null);

		if (value != null) {
			setStyleProperty(property, style, value.toString());
		}
	}

	/**
	 * Method for table-based subclasses to set the cell alignment according to
	 * a style data object.
	 *
	 * @param styleData     The style data
	 * @param cellFormatter The cell formatter of the table
	 * @param row           The row of the cell
	 * @param col           The column of the cell
	 */
	void setCellAlignment(StyleData styleData, CellFormatter cellFormatter,
		int row, int col) {
		HorizontalAlignmentConstant hAlign =
			styleData.mapHorizontalAlignment();
		VerticalAlignmentConstant vAlign = styleData.mapVerticalAlignment();

		if (hAlign != null) {
			cellFormatter.setHorizontalAlignment(row, col, hAlign);
		}

		if (vAlign != null) {
			cellFormatter.setVerticalAlignment(row, col, vAlign);
		}
	}
}
