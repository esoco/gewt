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
import de.esoco.ewt.geometry.Margins;
import de.esoco.ewt.style.AlignedPosition;
import de.esoco.ewt.style.Alignment;
import de.esoco.ewt.style.StyleData;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.ui.HTMLTable.CellFormatter;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;


/********************************************************************
 * A generic layout implementation similar to the AWT BorderLayout but with the
 * additional possibility to add components to the edges. The position of a
 * layout element can be set as a constraint either by one of the enumerated
 * style data constants in the class {@link AlignedPosition}. This
 * implementation is based on a GWT {@link FlexTable}. A simpler version that
 * uses a layout panel instead is provided by the class {@link DockLayout}.
 *
 * @author eso
 */
public class EdgeLayout extends GenericLayout
{
	//~ Instance fields --------------------------------------------------------

	private int nGap;

	private Widget[][] aWidgets = new Widget[3][3];

	//~ Constructors -----------------------------------------------------------

	/***************************************
	 * Creates a new EdgeLayout object with specific gaps between the layout
	 * cells.
	 *
	 * @param nGap The horizontal and vertical gap between components
	 */
	public EdgeLayout(int nGap)
	{
		this.nGap = nGap;
	}

	/***************************************
	 * Creates a new EdgeLayout object with certain gaps between the layout
	 * cells. In GEWT currently only the horizontal gap value will be used
	 * because of the limitations of the underlying GWT widgets.
	 *
	 * @param nHorizontalGap nGapW The horizontal gap between components
	 * @param nVerticalGap   nGapY The vertical gap between components
	 */
	public EdgeLayout(int nHorizontalGap, int nVerticalGap)
	{
		this.nGap = nHorizontalGap;
	}

	/***************************************
	 * Creates a new EdgeLayout object with certain margins and gaps between the
	 * layout cells. In GEWT currently only the horizontal gap value will be
	 * used because of the limitations of the underlying GWT widgets.
	 *
	 * @param rMargins       The margins
	 * @param nHorizontalGap nGapW The horizontal gap between components
	 * @param nVerticalGap   nGapY The vertical gap between components
	 */
	public EdgeLayout(Margins rMargins, int nHorizontalGap, int nVerticalGap)
	{
		this.nGap = nHorizontalGap;
	}

	//~ Methods ----------------------------------------------------------------

	/***************************************
	 * Adds the widget with the alignment defined in the style data.
	 *
	 * @see GenericLayout#addWidget(HasWidgets, Widget, StyleData, int)
	 */
	@Override
	public void addWidget(HasWidgets rContainer,
						  Widget	 rWidget,
						  StyleData  rStyleData,
						  int		 nIndex)
	{
		Alignment		  eVAlignment    = rStyleData.getVerticalAlignment();
		Alignment		  eHAlignment    = rStyleData.getHorizontalAlignment();
		EdgeLayoutTable   rTable		 = (EdgeLayoutTable) rContainer;
		CellFormatter     rCellFormatter = rTable.getCellFormatter();
		FlexCellFormatter rFlexFormatter = rTable.getFlexCellFormatter();

		int nRow = 0;
		int nCol = 0;

		switch (eVAlignment)
		{
			case BEGIN:

				if (countWidgets(0, true) == 0)
				{
					rTable.insertRow(0);
				}

				break;

			case CENTER:

				if (countWidgets(0, true) > 0)
				{
					nRow++;
				}

				rWidget.setHeight("100%");
				rCellFormatter.setHeight(nRow, nCol, "100%");

				break;

			case END:

				if (countWidgets(2, true) == 0)
				{
					rTable.insertRow(rTable.getRowCount());
				}

				nRow = rTable.getRowCount() - 1;

				break;

			default:
				assert false : "Unsupported vertical alignment: " +
					   eVAlignment;
		}

		switch (eHAlignment)
		{
			case BEGIN:

				if (countWidgets(0, false) == 0)
				{
					insertColumn(rTable, 0);
				}

				break;

			case CENTER:

				if (countWidgets(0, false) > 0)
				{
					nCol++;
				}

				rWidget.setWidth("100%");
				rCellFormatter.setWidth(nRow, nCol, "100%");

				break;

			case END:

				if (countWidgets(2, false) == 0)
				{
					insertColumn(rTable, rTable.getCellCount(0));
				}

				nCol = rTable.getCellCount(0) - 1;

				break;

			default:
				assert false : "Unsupported horizontal alignment: " +
					   eHAlignment;
		}

		rTable.setWidget(nRow, nCol, rWidget);
		aWidgets[eVAlignment.ordinal()][eHAlignment.ordinal()] = rWidget;

		if (eVAlignment == Alignment.CENTER && eHAlignment == Alignment.CENTER)
		{
			rTable.setCenterWidget(rWidget);
		}

		setCellSpans(rFlexFormatter, true);
		setCellAlignment(rStyleData, rCellFormatter, nRow, nCol);
		// TODO: check column spans
	}

	/***************************************
	 * @see GenericLayout#clear(HasWidgets)
	 */
	@Override
	public void clear(HasWidgets rContainer)
	{
		super.clear(rContainer);

		((FlexTable) rContainer).removeAllRows();
	}

	/***************************************
	 * {@inheritDoc}
	 */
	@Override
	public Panel createLayoutContainer(
		Container rContainer,
		StyleData rContainerStyle)
	{
		EdgeLayoutTable aTable = new EdgeLayoutTable();

		aTable.setCellSpacing(nGap);
		aTable.setCellPadding(0);
		aTable.setStylePrimaryName(EWT.CSS.ewtEdgeLayout());

		return aTable;
	}

	/***************************************
	 * @see GenericLayout#removeWidget(HasWidgets, Widget)
	 */
	@Override
	public void removeWidget(HasWidgets rContainer, Widget rWidget)
	{
		for (int nRow = 0; nRow < 3; nRow++)
		{
			for (int nCol = 0; nCol < 3; nCol++)
			{
				if (aWidgets[nRow][nCol] == rWidget)
				{
					aWidgets[nRow][nCol] = null;
				}
			}
		}

		setCellSpans(((FlexTable) rContainer).getFlexCellFormatter(), true);

		// TODO: implement deletion of rows and columns

		super.removeWidget(rContainer, rWidget);
	}

	/***************************************
	 * Counts the widgets in a certain row or column.
	 *
	 * @param  nIndex  The row or column index to count the widgets of
	 * @param  bForRow TRUE for a row, FALSE for a column
	 *
	 * @return The number of widgets in the column
	 */
	private int countWidgets(int nIndex, boolean bForRow)
	{
		int nCount = 0;

		for (int i = 0; i < 3; i++)
		{
			if ((bForRow && aWidgets[nIndex][i] != null) ||
				(!bForRow && aWidgets[i][nIndex] != null))
			{
				nCount++;
			}
		}

		return nCount;
	}

	/***************************************
	 * Returns the maximum widget count for all rows or columns.
	 *
	 * @param  bForRows TRUE to count rows, FALSE for columns
	 *
	 * @return The maximum widget count
	 */
	private int getMaxWidgetCount(boolean bForRows)
	{
		int nMax = 0;

		for (int i = 0; i < 3; i++)
		{
			nMax = Math.max(nMax, countWidgets(i, bForRows));
		}

		return nMax;
	}

	/***************************************
	 * Inserts a column into a {@link FlexTable}.
	 *
	 * @param rTable        The table
	 * @param nBeforeColumn The column to insert before
	 */
	private void insertColumn(FlexTable rTable, int nBeforeColumn)
	{
		int nCount = rTable.getRowCount();

		for (int i = 0; i < nCount; i++)
		{
			rTable.insertCell(i, nBeforeColumn);
		}
	}

	/***************************************
	 * Sets the table cell spans for asymmetric widget distributions.
	 *
	 * @param rCellFormatter The table cell formatter
	 * @param bForRows       TRUE to set the spans on rows, FALSE for columns
	 */
	private void setCellSpans(
		FlexCellFormatter rCellFormatter,
		boolean			  bForRows)
	{
		int nMaxWidgets = getMaxWidgetCount(bForRows);
		int nCenter     = countWidgets(0, !bForRows) > 0 ? 1 : 0;
		int nIndex	    = 0;

		for (int i = 0; i < 3; i++)
		{
			int nWidgets = countWidgets(i, bForRows);

			if (nWidgets < nMaxWidgets)
			{
				if (bForRows)
				{
					if (aWidgets[i][1] != null)
					{
						rCellFormatter.setColSpan(nIndex, nCenter, nMaxWidgets);
					}
				}
				else
				{
					if (aWidgets[1][i] != null)
					{
						rCellFormatter.setRowSpan(nCenter, nIndex, nMaxWidgets);
					}
				}
			}

			if (i > 0 || nWidgets > 0)
			{
				nIndex++;
			}
		}
	}

	//~ Inner Classes ----------------------------------------------------------

	/********************************************************************
	 * An inner class that implements the resize handling for the center widget.
	 *
	 * @author eso
	 */
	static class EdgeLayoutTable extends FlexTable implements RequiresResize,
															  ProvidesResize
	{
		//~ Instance fields ----------------------------------------------------

		private RequiresResize rCenterWidget;

		//~ Methods ------------------------------------------------------------

		/***************************************
		 * @see RequiresResize#onResize()
		 */
		@Override
		public void onResize()
		{
			if (rCenterWidget != null)
			{
				rCenterWidget.onResize();
			}
		}

		/***************************************
		 * Sets the center widget.
		 *
		 * @param rWidget The new center widget
		 */
		public void setCenterWidget(Widget rWidget)
		{
			if (rWidget instanceof RequiresResize)
			{
				rCenterWidget = (RequiresResize) rWidget;
			}
		}
	}
}
