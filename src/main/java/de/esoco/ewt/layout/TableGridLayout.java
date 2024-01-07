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

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.ui.HTMLTable.CellFormatter;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;
import de.esoco.ewt.component.Component;
import de.esoco.ewt.component.Container;
import de.esoco.ewt.style.StyleData;

/**
 * A table-based grid layout.
 */
public class TableGridLayout extends GenericLayout {

	private int gridCount;

	private final boolean isColumnCount;

	private final int gap;

	/**
	 * Creates a new GridLayout instance with a single row of components.
	 */
	public TableGridLayout() {
		this(1, false);
	}

	/**
	 * Creates a new GridLayout instance with a certain number of columns. The
	 * number of rows will be calculated from the number of components in the
	 * container.
	 *
	 * @param columns The number of grid columns
	 * @throws IllegalArgumentException If the count value is &lt;= 0
	 */
	public TableGridLayout(int columns) {
		this(columns, true);
	}

	/**
	 * Creates a new GridLayout instance with a certain number of columns or
	 * rows. The boolean parameter defines if the count value contains the
	 * number of columns (TRUE) or rows (FALSE). In each case the other number
	 * will be calculated from the number of components in the container.
	 *
	 * @param gridCount     The number of grid columns or rows
	 * @param isColumnCount TRUE for a fixed column count or FALSE for a fixed
	 *                      row count
	 * @throws IllegalArgumentException If the grid count value is &lt;= 0
	 */
	public TableGridLayout(int gridCount, boolean isColumnCount) {
		this(gridCount, isColumnCount, 0);
	}

	/**
	 * Creates a new GridLayout instance with a certain number of columns or
	 * rows and gaps between the grid cells. The boolean parameter defines if
	 * the count value contains the row or column count. In each case the other
	 * number will be calculated from the number of components in the
	 * container.
	 *
	 * @param gridCount     The number of grid columns or rows
	 * @param isColumnCount TRUE for a fixed column count or FALSE for a fixed
	 *                      row count
	 * @param gap           The gap between components
	 * @throws IllegalArgumentException If the grid count value is &lt;= 0
	 */
	public TableGridLayout(int gridCount, boolean isColumnCount, int gap) {
		this.gap = gap;

		if (gridCount <= 0) {
			throw new IllegalArgumentException("Grid count must be > 0");
		}

		this.gridCount = gridCount;
		this.isColumnCount = isColumnCount;
	}

	/**
	 * Adds a style name to the cell in which the last component has been added
	 * to this layout.
	 *
	 * @param container The container to set the cell style in
	 * @param style     The style name to add
	 */
	public void addCellStyle(Container container, String style) {
		FlexTable table = (FlexTable) container.getWidget();
		CellAddress cell = getCell(table, false);

		table.getCellFormatter().addStyleName(cell.row, cell.col, style);
	}

	/**
	 * Overridden to set the widget into the next grid cell according to the
	 * settings of this grid layout.
	 *
	 * @see GenericLayout#addWidget(HasWidgets, Widget, StyleData, int)
	 */
	@Override
	public void addWidget(HasWidgets container, Widget widget,
		StyleData styleData, int index) {
		FlexTable table = (FlexTable) container;
		CellAddress nextCell = getCell(table, true);

		table.setWidget(nextCell.row, nextCell.col, widget);
		setCellAlignment(styleData, table.getCellFormatter(), nextCell.row,
			nextCell.col);
	}

	/**
	 * Adds or removes a style name for the cell of a certain component.
	 *
	 * @param container The container to modify the cell style in
	 * @param component The component
	 * @param style     The style name to add or remove
	 * @param add       TRUE to add the style, FALSE to remove
	 */
	public void changeCellStyle(Container container, Component component,
		String style, boolean add) {
		FlexTable table = (FlexTable) container.getWidget();
		int rows = table.getRowCount();

		for (int row = 0; row < rows; row++) {
			int rowCells = table.getCellCount(row);

			for (int col = 0; col < rowCells; col++) {
				if (table.getWidget(row, col) == component.getWidget()) {
					CellFormatter cellFormatter = table.getCellFormatter();

					// always remove first to prevent duplication
					cellFormatter.removeStyleName(row, col, style);

					if (add) {
						cellFormatter.addStyleName(row, col, style);
					}

					return;
				}
			}
		}
	}

	/**
	 * @see GenericLayout#clear(HasWidgets)
	 */
	@Override
	public void clear(HasWidgets container) {
		super.clear(container);

		((FlexTable) container).removeAllRows();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Panel createLayoutContainer(Container container,
		StyleData containerStyle) {
		FlexTable table = new LayoutTable();

		table.setCellSpacing(gap);

		return table;
	}

	/**
	 * Returns the number of rows or columns of this layout.
	 *
	 * @return The column count
	 */
	public int getGridCount() {
		return gridCount;
	}

	/**
	 * Returns TRUE if the count number defines the layout columns.
	 *
	 * @return TRUE if the count is the number of columns, FALSE if it's the
	 * number of rows
	 */
	public boolean isColumnCount() {
		return isColumnCount;
	}

	/**
	 * Sets the number of columns that the last component added to this
	 * layout's
	 * container will span. This grid layout must belong to the given container
	 * or else the result will be undefined. This method must be invoked
	 * directly after the component that shall span the columns had been added.
	 *
	 * @param container The container to join the columns in
	 * @param count     The number of columns to join
	 */
	public void joinColumns(Container container, int count) {
		FlexTable table = (FlexTable) container.getWidget();
		CellAddress cell = getCell(table, false);

		table.getFlexCellFormatter().setColSpan(cell.row, cell.col, count);
	}

	/**
	 * Sets the number of rows that the last component added to this layout's
	 * container will span. This grid layout must belong to the given container
	 * or else the result will be undefined. This method must be invoked
	 * directly after the component that shall span the rows had been added.
	 *
	 * @param container The container to join the rows in
	 * @param count     The number of rows to join
	 */
	public void joinRows(Container container, int count) {
		FlexTable table = (FlexTable) container.getWidget();
		CellAddress cell = getCell(table, false);

		table.getFlexCellFormatter().setRowSpan(cell.row, cell.col, count);
	}

	/**
	 * Sets the size of the cell in which the last component has been added to
	 * this layout.
	 *
	 * @param container The container to set the component cell size of
	 * @param width     The width of the component's cell or NULL for none
	 * @param height    The height of the component's cell or NULL for none
	 */
	public void setCellSize(Container container, String width, String height) {
		FlexTable table = (FlexTable) container.getWidget();
		CellAddress cell = getCell(table, false);

		if (width != null) {
			table.getFlexCellFormatter().setWidth(cell.row, cell.col, width);
		}

		if (height != null) {
			table.getFlexCellFormatter().setHeight(cell.row, cell.col, height);
		}
	}

	/**
	 * Sets the grid count, interpreted as columns or rows depending on the
	 * layout configuration.
	 *
	 * @param count The new grid count
	 */
	public final void setGridCount(int count) {
		gridCount = count;
	}

	/**
	 * Internal helper method to determine the row and column of the last added
	 * or next cell to fill.
	 *
	 * @param table The table to determine the cell of
	 * @param next  TRUE for the next, FALSE for the last cell
	 * @return The address of the cell
	 */
	private CellAddress getCell(FlexTable table, boolean next) {
		FlexCellFormatter cellFormatter = table.getFlexCellFormatter();
		int rows = table.getRowCount();
		int cellCount = next ? 0 : -1;

		for (int row = 0; row < rows; row++) {
			int rowCells = table.getCellCount(row);

			for (int col = 0; col < rowCells; col++) {
				cellCount += cellFormatter.getColSpan(row, col);
			}
		}

		int col = 0;
		int row;

		if (isColumnCount) {
			row = cellCount / gridCount;
		} else {
			row = cellCount % gridCount;
		}

		if (row < rows) {
			col = table.getCellCount(row);

			if (!next) {
				col--;
			}
		}

		return new CellAddress(row, col);
	}

	/**
	 * Internal implementation of the layout container.
	 *
	 * @author eso
	 */
	static class LayoutTable extends FlexTable
		implements RequiresResize, ProvidesResize {

		/**
		 * @see RequiresResize#onResize()
		 */
		@Override
		public void onResize() {
			for (Widget widget : this) {
				if (widget instanceof RequiresResize) {
					((RequiresResize) widget).onResize();
				}
			}
		}
	}

	/**
	 * Internal helper class to contain a cell address.
	 *
	 * @author eso
	 */
	private static class CellAddress {

		int row;

		int col;

		/**
		 * Creates a new instance.
		 *
		 * @param row The row
		 * @param col The column
		 */
		public CellAddress(int row, int col) {
			this.row = row;
			this.col = col;
		}
	}
}
