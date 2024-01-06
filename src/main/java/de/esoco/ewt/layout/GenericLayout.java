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

import de.esoco.lib.property.HasCssName;
import de.esoco.lib.property.PropertyName;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.HTMLTable.CellFormatter;
import com.google.gwt.user.client.ui.HasHorizontalAlignment.HorizontalAlignmentConstant;
import com.google.gwt.user.client.ui.HasVerticalAlignment.VerticalAlignmentConstant;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.InsertPanel;
import com.google.gwt.user.client.ui.Widget;

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
	 * @param rContainer The container to add the widget to
	 * @param rWidget    The widget to add
	 * @param rStyleData The style defining the layout position of the widget
	 * @param nIndex     The index to add the widget add (if supported by the
	 *                   implementation) or -1 to add as the last widget
	 */
	public void addWidget(HasWidgets rContainer, Widget rWidget,
		StyleData rStyleData, int nIndex) {
		if (nIndex >= 0 && rContainer instanceof InsertPanel) {
			((InsertPanel) rContainer).insert(rWidget, nIndex);
		} else {
			rContainer.add(rWidget);
		}
	}

	/**
	 * Removes all widgets from this layout's container.
	 *
	 * @param rContainer The container to clear
	 */
	public void clear(HasWidgets rContainer) {
		rContainer.clear();
	}

	/**
	 * Creates a new GWT container widget that represents this layout. This
	 * method is only intended to be used internally by the GEWT framework.
	 *
	 * @param rContainer The GEWT container to create the widget for
	 * @param rStyleData The style of the container widget
	 * @return A new widget container
	 */
	public abstract HasWidgets createLayoutContainer(Container rContainer,
		StyleData rStyleData);

	/**
	 * Removes a certain widget from a widget container according to this
	 * layout
	 * and the given style data. This default implementation just invokes the
	 * method {@link HasWidgets#remove(Widget)}. Subclasses may override this
	 * method to implement a different layout strategy.
	 *
	 * @param rContainer The container to remove the widget from
	 * @param rWidget    The widget to remove
	 */
	public void removeWidget(HasWidgets rContainer, Widget rWidget) {
		rContainer.remove(rWidget);
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
	 * @param sProperty The property name
	 * @param rStyle    The element style
	 * @param sValue    The property value
	 */
	protected void setStyleProperty(String sProperty, Style rStyle,
		String sValue) {
		if (sValue != null) {
			rStyle.setProperty(sProperty, sValue);
		}
	}

	/**
	 * Sets a CSS name property on an element style if it is not NULL.
	 *
	 * @param sProperty The property name
	 * @param rStyle    The element style
	 * @param rValue    The property value
	 */
	protected void setStyleProperty(String sProperty, Style rStyle,
		HasCssName rValue) {
		if (rValue != null) {
			rStyle.setProperty(sProperty, rValue.getCssName());
		}
	}

	/**
	 * Sets a style property from a component style property if it exists.
	 *
	 * @param rProperty  The property to get from the component style
	 * @param rStyleData The component style
	 * @param sProperty  The name of the target property
	 * @param rStyle     The target style
	 */
	protected void setStyleProperty(PropertyName<?> rProperty,
		StyleData rStyleData, String sProperty, Style rStyle) {
		Object rValue = rStyleData.getProperty(rProperty, null);

		if (rValue != null) {
			setStyleProperty(sProperty, rStyle, rValue.toString());
		}
	}

	/**
	 * Method for table-based subclasses to set the cell alignment according to
	 * a style data object.
	 *
	 * @param rStyleData     The style data
	 * @param rCellFormatter The cell formatter of the table
	 * @param nRow           The row of the cell
	 * @param nCol           The column of the cell
	 */
	void setCellAlignment(StyleData rStyleData, CellFormatter rCellFormatter,
		int nRow, int nCol) {
		HorizontalAlignmentConstant rHAlign =
			rStyleData.mapHorizontalAlignment();
		VerticalAlignmentConstant rVAlign = rStyleData.mapVerticalAlignment();

		if (rHAlign != null) {
			rCellFormatter.setHorizontalAlignment(nRow, nCol, rHAlign);
		}

		if (rVAlign != null) {
			rCellFormatter.setVerticalAlignment(nRow, nCol, rVAlign);
		}
	}
}
