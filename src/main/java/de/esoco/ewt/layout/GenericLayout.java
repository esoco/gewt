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

import de.esoco.ewt.component.Container;
import de.esoco.ewt.style.StyleData;

import com.google.gwt.user.client.ui.HTMLTable.CellFormatter;
import com.google.gwt.user.client.ui.HasHorizontalAlignment.HorizontalAlignmentConstant;
import com.google.gwt.user.client.ui.HasVerticalAlignment.VerticalAlignmentConstant;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.InsertPanel;
import com.google.gwt.user.client.ui.Widget;


/********************************************************************
 * Base class for all EWT layouts.
 *
 * @author eso
 */
public abstract class GenericLayout
{
	//~ Methods ----------------------------------------------------------------

	/***************************************
	 * Creates a new GWT container widget that represents this layout. This
	 * method is only intended to be used internally by the GEWT framework.
	 *
	 * @param    rContainer The GEWT container to create the widget for
	 * @param    rStyle     The style of the container widget
	 *
	 * @return   A new widget container
	 *
	 * @category GEWT
	 */
	public abstract HasWidgets createLayoutContainer(
		Container rContainer,
		StyleData rStyle);

	/***************************************
	 * Adds a certain widget to a widget container according to this layout and
	 * the given style data. This default implementation ignores the style data
	 * and either invokes {@link HasWidgets#remove(Widget)} or, depending on the
	 * index and container type, {@link InsertPanel#insert(Widget, int)}.
	 * Subclasses may override this method to implement a different layout
	 * strategy.
	 *
	 * @param    rContainer The container to add the widget to
	 * @param    rWidget    The widget to add
	 * @param    rStyle     The style defining the layout position of the widget
	 * @param    nIndex     The index to add the widget add (if supported by the
	 *                      implementation) or -1 to add as the last widget
	 *
	 * @category GEWT
	 */
	public void addWidget(HasWidgets rContainer,
						  Widget	 rWidget,
						  StyleData  rStyle,
						  int		 nIndex)
	{
		if (nIndex >= 0 && rContainer instanceof InsertPanel)
		{
			((InsertPanel) rContainer).insert(rWidget, nIndex);
		}
		else
		{
			rContainer.add(rWidget);
		}
	}

	/***************************************
	 * Removes all widgets from this layout's container.
	 *
	 * @param rContainer The container to clear
	 */
	public void clear(HasWidgets rContainer)
	{
		rContainer.clear();
	}

	/***************************************
	 * Removes a certain widget from a widget container according to this layout
	 * and the given style data. This default implementation just invokes the
	 * method {@link HasWidgets#remove(Widget)}. Subclasses may override this
	 * method to implement a different layout strategy.
	 *
	 * @param    rContainer The container to remove the widget from
	 * @param    rWidget    The widget to remove
	 *
	 * @category GEWT
	 */
	public void removeWidget(HasWidgets rContainer, Widget rWidget)
	{
		rContainer.remove(rWidget);
	}

	/***************************************
	 * {@inheritDoc}
	 */
	@Override
	public String toString()
	{
		return getClass().getSimpleName();
	}

	/***************************************
	 * Method for subclasses to set the cell alignment in GWT tables according
	 * to the style flags in a style data object.
	 *
	 * @param rStyle         The style data
	 * @param rCellFormatter The cell formatter of the table
	 * @param nRow           The row of the cell
	 * @param nCol           The column of the cell
	 */
	protected void setCellAlignment(StyleData	  rStyle,
									CellFormatter rCellFormatter,
									int			  nRow,
									int			  nCol)
	{
		HorizontalAlignmentConstant rHAlign = rStyle.mapHorizontalAlignment();
		VerticalAlignmentConstant   rVAlign = rStyle.mapVerticalAlignment();

		if (rHAlign != null)
		{
			rCellFormatter.setHorizontalAlignment(nRow, nCol, rHAlign);
		}

		if (rVAlign != null)
		{
			rCellFormatter.setVerticalAlignment(nRow, nCol, rVAlign);
		}
	}
}
