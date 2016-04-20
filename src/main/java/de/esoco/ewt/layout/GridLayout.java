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

import de.esoco.ewt.UserInterfaceContext;
import de.esoco.ewt.component.Component;
import de.esoco.ewt.component.Container;
import de.esoco.ewt.style.StyleData;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.ui.HTMLTable.CellFormatter;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;


/********************************************************************
 * Simple grid layout similar to the AWT GridLayout.
 */
public class GridLayout extends GenericLayout
{
	//~ Instance fields --------------------------------------------------------

	private int nGridCount;

	private boolean bIsColumnCount;
	private int     nGap;

	//~ Constructors -----------------------------------------------------------

	/***************************************
	 * Creates a new GridLayout instance with a single row of components.
	 */
	public GridLayout()
	{
		this(1, false);
	}

	/***************************************
	 * Creates a new GridLayout instance with a certain number of columns. The
	 * number of rows will be calculated from the number of components in the
	 * container.
	 *
	 * @param  nColumns The number of grid columns
	 *
	 * @throws IllegalArgumentException If the count value is <= 0
	 */
	public GridLayout(int nColumns)
	{
		this(nColumns, true);
	}

	/***************************************
	 * Creates a new GridLayout instance with a certain number of columns or
	 * rows. The boolean parameter defines if the count value contains the
	 * number of columns (TRUE) or rows (FALSE). In each case the other number
	 * will be calculated from the number of components in the container.
	 *
	 * @param  nGridCount     The number of grid columns or rows
	 * @param  bIsColumnCount TRUE for a fixed column count or FALSE for a fixed
	 *                        row count
	 *
	 * @throws IllegalArgumentException If the grid count value is <= 0
	 */
	public GridLayout(int nGridCount, boolean bIsColumnCount)
	{
		this(nGridCount, bIsColumnCount, 0);
	}

	/***************************************
	 * Creates a new GridLayout instance with a certain number of columns or
	 * rows and gaps between the grid cells. The boolean parameter defines if
	 * the nCount value contains the row or column count. In each case the other
	 * number will be calculated from the number of components in the container.
	 *
	 * @param  nGridCount     The number of grid columns or rows
	 * @param  bIsColumnCount TRUE for a fixed column count or FALSE for a fixed
	 *                        row count
	 * @param  nGap           The gap between components
	 *
	 * @throws IllegalArgumentException If the grid count value is <= 0
	 */
	public GridLayout(int nGridCount, boolean bIsColumnCount, int nGap)
	{
		this.nGap = nGap;

		if (nGridCount <= 0)
		{
			throw new IllegalArgumentException("Grid count must be > 0");
		}

		this.nGridCount     = nGridCount;
		this.bIsColumnCount = bIsColumnCount;
	}

	//~ Methods ----------------------------------------------------------------

	/***************************************
	 * Adds a style name to the cell in which the last component has been added
	 * to this layout.
	 *
	 * @param    rContainer The container to set the cell style in
	 * @param    sStyle     The style name to add
	 *
	 * @category GEWT
	 */
	public void addCellStyle(Container rContainer, String sStyle)
	{
		FlexTable   rTable = (FlexTable) rContainer.getWidget();
		CellAddress rCell  = getCell(rTable, false);

		rTable.getCellFormatter().addStyleName(rCell.nRow, rCell.nCol, sStyle);
	}

	/***************************************
	 * Overridden to set the widget into the next grid cell according to the
	 * settings of this grid layout.
	 *
	 * @see GenericLayout#addWidget(HasWidgets, Widget, StyleData, int)
	 */
	@Override
	public void addWidget(HasWidgets rContainer,
						  Widget	 rWidget,
						  StyleData  rStyleData,
						  int		 nIndex)
	{
		FlexTable   rTable    = (FlexTable) rContainer;
		CellAddress rNextCell = getCell(rTable, true);

		rTable.setWidget(rNextCell.nRow, rNextCell.nCol, rWidget);
		setCellAlignment(rStyleData,
						 rTable.getCellFormatter(),
						 rNextCell.nRow,
						 rNextCell.nCol);
	}

	/***************************************
	 * Adds or removes a style name for the cell of a certain component.
	 *
	 * @param    rContainer The container to modify the cell style in
	 * @param    rComponent The component
	 * @param    sStyle     The style name to add or remove
	 * @param    bAdd       TRUE to add the style, FALSE to remove
	 *
	 * @category GEWT
	 */
	public void changeCellStyle(Container rContainer,
								Component rComponent,
								String    sStyle,
								boolean   bAdd)
	{
		FlexTable rTable = (FlexTable) rContainer.getWidget();
		int		  nRows  = rTable.getRowCount();

		for (int nRow = 0; nRow < nRows; nRow++)
		{
			int nRowCells = rTable.getCellCount(nRow);

			for (int nCol = 0; nCol < nRowCells; nCol++)
			{
				if (rTable.getWidget(nRow, nCol) == rComponent.getWidget())
				{
					CellFormatter rCellFormatter = rTable.getCellFormatter();

					// always remove first to prevent duplication
					rCellFormatter.removeStyleName(nRow, nCol, sStyle);

					if (bAdd)
					{
						rCellFormatter.addStyleName(nRow, nCol, sStyle);
					}

					return;
				}
			}
		}
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
	public HasWidgets createLayoutContainer(
		UserInterfaceContext rContext,
		StyleData			 rContainerStyle)
	{
		FlexTable aContainer = new GridLayoutTable();

		aContainer.setCellSpacing(nGap);

		return aContainer;
	}

	/***************************************
	 * Returns the number of rows or columns of this layout.
	 *
	 * @return The column count
	 */
	public int getGridCount()
	{
		return nGridCount;
	}

	/***************************************
	 * Returns TRUE if the count number defines the layout columns.
	 *
	 * @return TRUE if the count is the number of columns, FALSE if it's the
	 *         number of rows
	 */
	public boolean isColumnCount()
	{
		return bIsColumnCount;
	}

	/***************************************
	 * Sets the number of columns that the last component added to this layout's
	 * container will span. This grid layout must belong to the given container
	 * or else the result will be undefined. This method must be invoked
	 * directly after the component that shall span the columns had been added.
	 *
	 * @param    rContainer The container to join the columns in
	 * @param    nCount     The number of columns to join
	 *
	 * @category GEWT
	 */
	public void joinColumns(Container rContainer, int nCount)
	{
		FlexTable   rTable = (FlexTable) rContainer.getWidget();
		CellAddress rCell  = getCell(rTable, false);

		rTable.getFlexCellFormatter()
			  .setColSpan(rCell.nRow, rCell.nCol, nCount);
	}

	/***************************************
	 * Sets the number of rows that the last component added to this layout's
	 * container will span. This grid layout must belong to the given container
	 * or else the result will be undefined. This method must be invoked
	 * directly after the component that shall span the rows had been added.
	 *
	 * @param    rContainer The container to join the rows in
	 * @param    nCount     The number of rows to join
	 *
	 * @category GEWT
	 */
	public void joinRows(Container rContainer, int nCount)
	{
		FlexTable   rTable = (FlexTable) rContainer.getWidget();
		CellAddress rCell  = getCell(rTable, false);

		rTable.getFlexCellFormatter()
			  .setRowSpan(rCell.nRow, rCell.nCol, nCount);
	}

	/***************************************
	 * Sets the size of the cell in which the last component has been added to
	 * this layout.
	 *
	 * @param    rContainer The container to set the component cell size of
	 * @param    sWidth     The width of the component's cell or NULL for none
	 * @param    sHeight    The height of the component's cell or NULL for none
	 *
	 * @category GEWT
	 */
	public void setCellSize(Container rContainer, String sWidth, String sHeight)
	{
		FlexTable   rTable = (FlexTable) rContainer.getWidget();
		CellAddress rCell  = getCell(rTable, false);

		if (sWidth != null)
		{
			rTable.getFlexCellFormatter()
				  .setWidth(rCell.nRow, rCell.nCol, sWidth);
		}

		if (sHeight != null)
		{
			rTable.getFlexCellFormatter()
				  .setHeight(rCell.nRow, rCell.nCol, sHeight);
		}
	}

	/***************************************
	 * Sets the grid count, interpreted as columns or rows depending on the
	 * layout configuration.
	 *
	 * @param    nCount The new grid count
	 *
	 * @category GEWT
	 */
	public final void setGridCount(int nCount)
	{
		nGridCount = nCount;
	}

	/***************************************
	 * Internal helper method to determine the row and column of the last added
	 * or next cell to fill.
	 *
	 * @param  rTable The table to determine the cell of
	 * @param  bNext  TRUE for the next, FALSE for the last cell
	 *
	 * @return The address of the cell
	 */
	private CellAddress getCell(FlexTable rTable, boolean bNext)
	{
		FlexCellFormatter rCellFormatter = rTable.getFlexCellFormatter();
		int				  nRows			 = rTable.getRowCount();
		int				  nCellCount     = bNext ? 0 : -1;

		for (int nRow = 0; nRow < nRows; nRow++)
		{
			int nRowCells = rTable.getCellCount(nRow);

			for (int nCol = 0; nCol < nRowCells; nCol++)
			{
				nCellCount += rCellFormatter.getColSpan(nRow, nCol);
			}
		}

		int nCol = 0;
		int nRow;

		if (bIsColumnCount)
		{
			nRow = nCellCount / nGridCount;
		}
		else
		{
			nRow = nCellCount % nGridCount;
		}

		if (nRow < nRows)
		{
			nCol = rTable.getCellCount(nRow);

			if (!bNext)
			{
				nCol--;
			}
		}

		return new CellAddress(nRow, nCol);
	}

	//~ Inner Classes ----------------------------------------------------------

	/********************************************************************
	 * Internal implementation of the grid layout container.
	 *
	 * @author eso
	 */
	static class GridLayoutTable extends FlexTable implements RequiresResize,
															  ProvidesResize
	{
		//~ Methods ------------------------------------------------------------

		/***************************************
		 * @see RequiresResize#onResize()
		 */
		@Override
		public void onResize()
		{
			for (Widget rWidget : this)
			{
				if (rWidget instanceof RequiresResize)
				{
					((RequiresResize) rWidget).onResize();
				}
			}
		}
	}

	/********************************************************************
	 * Internal helper class to contain a cell address.
	 *
	 * @author eso
	 */
	private static class CellAddress
	{
		//~ Instance fields ----------------------------------------------------

		int nRow;
		int nCol;

		//~ Constructors -------------------------------------------------------

		/***************************************
		 * Creates a new instance.
		 *
		 * @param nRow The row
		 * @param nCol The column
		 */
		public CellAddress(int nRow, int nCol)
		{
			this.nRow = nRow;
			this.nCol = nCol;
		}
	}
}
