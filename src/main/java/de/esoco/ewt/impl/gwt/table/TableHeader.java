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
package de.esoco.ewt.impl.gwt.table;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLTable.Cell;
import com.google.gwt.user.client.ui.HTMLTable.ColumnFormatter;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import de.esoco.ewt.UserInterfaceContext;
import de.esoco.ewt.impl.gwt.GwtDateTimeFormat;
import de.esoco.ewt.impl.gwt.ValueFormat;
import de.esoco.lib.model.ColumnDefinition;
import de.esoco.lib.model.DataModel;
import de.esoco.lib.model.SortableDataModel;
import de.esoco.lib.property.ContentType;
import de.esoco.lib.property.SortDirection;
import de.esoco.lib.text.TextConvert;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static de.esoco.lib.property.ContentProperties.CONTENT_TYPE;
import static de.esoco.lib.property.StyleProperties.HAS_IMAGES;
import static de.esoco.lib.property.StyleProperties.MAX_CHARS;
import static de.esoco.lib.property.StyleProperties.MIN_CHARS;
import static de.esoco.lib.property.StyleProperties.SEARCHABLE;
import static de.esoco.lib.property.StyleProperties.SORTABLE;

/**
 * Header implementation for {@link GwtTable}.
 *
 * @author eso
 */
class TableHeader extends Composite
	implements ClickHandler, MouseMoveHandler, MouseUpHandler,
	MouseOutHandler {

	private static final int MIN_COLUMN_WIDTH = 5;

	private final GwtTable gwtTable;

	private final Grid headerTable = new Grid(1, 1);

	private final List<ColumnHeader> columnHeaders = new ArrayList<>();

	private final List<ValueFormat> columnFormats = new ArrayList<>();

	private final List<String> columnStyles = new ArrayList<>();

	DataModel<ColumnDefinition> columns;

	private int resizeColumn = -1;

	private int resizePosition = 0;

	private boolean wasResizeClick = false;

	private HandlerRegistration mouseMoveHandler;

	private HandlerRegistration mouseUpHandler;

	private HandlerRegistration mouseOutHandler;

	/**
	 * Creates a new instance.
	 *
	 * @param gwtTable The table this instance belongs to
	 */
	public TableHeader(GwtTable gwtTable) {
		this.gwtTable = gwtTable;

		initWidget(headerTable);

		headerTable.setWidth("100%");
		headerTable.addClickHandler(this);
		headerTable.setStylePrimaryName(GwtTable.CSS.ewtHeader());
	}

	/**
	 * Returns the number of table columns.
	 *
	 * @return The column count
	 */
	public final int getColumnCount() {
		return columns.getElementCount();
	}

	/**
	 * Returns the column definition for a certain column index.
	 *
	 * @param column The column index
	 * @return The column definition
	 */
	public final ColumnDefinition getColumnDefinition(int column) {
		return columns.getElement(column);
	}

	/**
	 * Returns the value format for a certain column.
	 *
	 * @param column The column index
	 * @return The column format
	 */
	public final ValueFormat getColumnFormat(int column) {
		return columnFormats.get(column);
	}

	/**
	 * @see ClickHandler#onClick(ClickEvent)
	 */
	@Override
	public void onClick(ClickEvent event) {
		if (wasResizeClick) {
			// button release at the end of a column resize causes click event
			wasResizeClick = false;
		} else if (gwtTable.canHandleInput()) {
			Cell cell = headerTable.getCellForEvent(event);

			if (cell != null) {
				changeSorting(cell.getCellIndex());
			}
		}
	}

	/**
	 * @see MouseMoveHandler#onMouseMove(MouseMoveEvent)
	 */
	@Override
	public void onMouseMove(MouseMoveEvent event) {
		if (resizeColumn >= 0) {
			int mouseX = event.getClientX();

			changeColumnWidth(resizeColumn, mouseX - resizePosition);
			resizePosition = mouseX;
		}
	}

	/**
	 * @see MouseOutHandler#onMouseOut(MouseOutEvent)
	 */
	@Override
	public void onMouseOut(MouseOutEvent event) {
		setResizeColumn(-1, -1);
	}

	/**
	 * @see MouseUpHandler#onMouseUp(MouseUpEvent)
	 */
	@Override
	public void onMouseUp(MouseUpEvent event) {
		setResizeColumn(-1, -1);
	}

	/**
	 * Calculates the average width of a character in data cells.
	 *
	 * @return The character width
	 */
	int calcCharWidth() {
		FlexTable dataTable = gwtTable.getDataTable();
		int charWidth;

		boolean hasRows = dataTable.getRowCount() > 0;
		Widget widget = null;

		if (hasRows) {
			// save existing widget if already initialized
			widget = dataTable.getWidget(0, 0);
		}

		HorizontalPanel panel = new HorizontalPanel();
		Label label = new Label("N");

		panel.add(label);
		dataTable.setWidget(0, 0, panel);
		charWidth = label.getOffsetWidth();

		if (hasRows) {
			dataTable.setWidget(0, 0, widget);
		} else {
			dataTable.removeRow(0);
		}

		return charWidth;
	}

	/**
	 * Calculates the column width based on the available table width.
	 */
	void calcColumnWidths() {
		int charWidth = calcCharWidth();
		int columnCount = columns.getElementCount();
		int remainingWidth = gwtTable.getDataWidth();
		int variableColumns = 0;

		for (int col = 0; col < columnCount; col++) {
			ColumnDefinition column = columns.getElement(col);
			ColumnHeader header = columnHeaders.get(col);

			String datatype = column.getDatatype();
			int minWidth = column.getIntProperty(MIN_CHARS, 0);
			int maxWidth = column.getIntProperty(MAX_CHARS, 0);
			int columnWidth = 0;

			if (column.getProperty(CONTENT_TYPE, null) ==
				ContentType.DATE_TIME ||
				Timestamp.class.getName().endsWith(datatype)) {
				columnWidth = 12;
			} else if (Date.class.getName().endsWith(datatype) ||
				Time.class.getName().endsWith(datatype)) {
				columnWidth = 8;
			} else if (Integer.class.getName().endsWith(datatype)) {
				columnWidth = 10;
			} else if (Enum.class.getName().endsWith(datatype)) {
				columnWidth = column.hasFlag(HAS_IMAGES) ? 3 : 10;
			} else if (maxWidth > 0) {
				columnWidth = maxWidth;
			}

			if (columnWidth != 0) {
				columnWidth = checkColumnWidth(columnWidth, minWidth,
					maxWidth);

				if (col == 0 && gwtTable.isHierarchical()) {
					columnWidth += 3;
				}

				columnWidth *= charWidth;

				header.setColumnWidth(columnWidth);
				remainingWidth -= columnWidth;
			} else {
				header.setColumnWidth(-1);
				variableColumns++;
			}
		}

		if (variableColumns > 0) {
			distributeRemainingWidth(variableColumns, remainingWidth,
				charWidth);
		}

		// let the last column fill the remaining space
		columnHeaders.get(columnCount - 1).htmlColumnWidth = null;
	}

	/**
	 * Returns the style of a certain column.
	 *
	 * @param column The column index
	 * @return The column style
	 */
	final String getColumnStyle(int column) {
		return columnStyles.get(column);
	}

	/**
	 * Initializes the table columns from the column data model. This will also
	 * set the filterable columns in the filter panel if it is available.
	 *
	 * @param filterPanel The table's filter panel or NULL for none
	 */
	void initColumns(TableFilterPanel filterPanel) {
		UserInterfaceContext context = gwtTable.getContext();

		int columnIndex = 0;

		columnHeaders.clear();
		columnFormats.clear();
		headerTable.removeRow(0);
		headerTable.resize(1, columns.getElementCount());
		headerTable
			.getRowFormatter()
			.setStylePrimaryName(0, GwtTable.CSS.ewtHeader());

		filterPanel.resetFilterColumns();

		for (ColumnDefinition column : columns) {
			String title = context.expandResource(column.getTitle());

			if (title.startsWith("col")) {
				title = title.substring(3);
			}

			ValueFormat columnFormat = getColumnFormat(column);
			ColumnHeader header = new ColumnHeader(title, columnIndex);

			setColumnStyle(columnIndex, column);
			headerTable.setWidget(0, columnIndex++, header);

			columnHeaders.add(header);
			columnFormats.add(columnFormat);

			if (column.hasFlag(SEARCHABLE)) {
				filterPanel.addFilterColumn(column);
			} else {
				header.addStyleName(GwtTable.CSS.ewtLimited());
			}
		}
	}

	/**
	 * Sets the widths of the table columns.
	 */
	void setAllColumnWidths() {
		ColumnFormatter headerColumnFormatter =
			headerTable.getColumnFormatter();

		ColumnFormatter dataColumnFormatter =
			gwtTable.getDataTable().getColumnFormatter();

		int columnCount = columns.getElementCount();

		for (int col = 0; col < columnCount; col++) {
			String columnWidth = columnHeaders.get(col).getColumnWidth();

			if (columnWidth != null) {
				headerColumnFormatter.setWidth(col, columnWidth);
				dataColumnFormatter.setWidth(col, columnWidth);
			}
		}
	}

	/**
	 * Sets the data model that contains information about the table columns.
	 *
	 * @param newColumns The table columns data model
	 * @return TRUE if the columns have changed
	 */
	boolean setColumns(DataModel<ColumnDefinition> newColumns) {
		boolean changed = columns == null || !columns.equals(newColumns);

		if (changed) {
			columnStyles.clear();
			columns = newColumns;

			for (ColumnDefinition column : columns) {
				String style = column.getId();
				String datatype = column.getDatatype();

				style = TextConvert.lastElementOf(style).toLowerCase();

				if (!datatype.equals("String")) {
					style = style + " " + datatype;
				}

				columnStyles.add(style);
			}
		}

		return changed;
	}

	/**
	 * Package-internal method to set or clear the current resize column. Will
	 * be invoked from the {@link ColumnHeader} class.
	 *
	 * @param column The header of the currently resized table column
	 * @param mouseX The starting X position of the resize
	 */
	final void setResizeColumn(int column, int mouseX) {
		String noSelectStyle = GwtTable.CSS.ewtNoSelect();
		boolean columnResize = column >= 0;

		resizeColumn = column;
		resizePosition = mouseX;

		// only start if table not busy but always end to prevent resizing from
		// remaining active due to external table update events
		if (columnResize) {
			if (gwtTable.canHandleInput()) {
				FocusPanel focusPanel = gwtTable.getFocusPanel();

				gwtTable.getDataTable().addStyleName(noSelectStyle);

				mouseMoveHandler = focusPanel.addMouseMoveHandler(this);
				mouseUpHandler = focusPanel.addMouseUpHandler(this);
				mouseOutHandler = focusPanel.addMouseOutHandler(this);
				wasResizeClick = true;
			}
		} else if (mouseMoveHandler != null) {
			mouseMoveHandler.removeHandler();
			mouseUpHandler.removeHandler();
			mouseOutHandler.removeHandler();

			mouseMoveHandler = null;
			mouseUpHandler = null;
			mouseOutHandler = null;

			gwtTable.getDataTable().removeStyleName(noSelectStyle);
		}
	}

	/**
	 * Changes the width of a certain table column and of the preceding column
	 * if necessary.
	 *
	 * @param column The column index
	 * @param change The new column width in pixels
	 */
	private void changeColumnWidth(int column, int change) {
		int currentWidth = getColumnWidth(column);

		if (change > 0 || currentWidth > MIN_COLUMN_WIDTH) {
			int newWidth = Math.max(currentWidth + change, MIN_COLUMN_WIDTH);

			setColumnWidth(column, newWidth + "px");
		}
	}

	/**
	 * Changes the sorting for a certain column.
	 *
	 * @param col The column to change the sorting for
	 */
	private void changeSorting(int col) {
		ColumnDefinition column = columns.getElement(col);
		DataModel<? extends DataModel<?>> data = gwtTable.getData();

		if (column.hasFlag(SORTABLE) && data instanceof SortableDataModel<?>) {
			SortableDataModel<?> model = (SortableDataModel<?>) data;
			String columnId = column.getId();
			SortDirection sortDirection = model.getSortDirection(columnId);

			ColumnHeader header = (ColumnHeader) headerTable.getWidget(0, col);

			if (sortDirection == SortDirection.ASCENDING) {
				sortDirection = SortDirection.DESCENDING;
			} else if (sortDirection == SortDirection.DESCENDING) {
				sortDirection = null;
			} else {
				sortDirection = SortDirection.ASCENDING;
			}

			header.setSortIndicator(sortDirection);
			model.setSortDirection(columnId, sortDirection);
			gwtTable.update();
		}
	}

	/**
	 * Checks a column width against the minimum and maximum width of the
	 * column.
	 *
	 * @param columnWidth The current column width
	 * @param minWidth    The minimum width
	 * @param maxWidth    The maximum width
	 * @return The adjusted column width
	 */
	private int checkColumnWidth(int columnWidth, int minWidth, int maxWidth) {
		if (columnWidth < minWidth) {
			columnWidth = minWidth;
		}

		if (maxWidth > 0 && columnWidth > maxWidth) {
			columnWidth = maxWidth;
		}

		return columnWidth;
	}

	/**
	 * Distributes the remaining table width over all variable columns.
	 *
	 * @param variableColumns The number of variable-width columns
	 * @param remainingWidth  The remaining width in pixels to distribute
	 * @param charWidth       The average character width
	 */
	private void distributeRemainingWidth(int variableColumns,
		int remainingWidth, int charWidth) {
		int columnWidth = remainingWidth / variableColumns;
		int remainder = remainingWidth % variableColumns;
		int lastColumn = getColumnCount() - 1;

		for (int col = 0; col <= lastColumn && remainingWidth > 0; col++) {
			ColumnHeader header = columnHeaders.get(col);

			if (header.getColumnWidth() == null) {
				ColumnDefinition column = columns.getElement(col);

				int minWidth = column.getIntProperty(MIN_CHARS, 0) * charWidth;

				columnWidth =
					checkColumnWidth(columnWidth, minWidth, columnWidth);

				// add the remainder to the first column
				remainingWidth -= columnWidth + remainder;
				header.setColumnWidth(columnWidth + remainder);
				remainder = 0;
			}
		}
	}

	/**
	 * Returns the appropriate formatting object for a certain column.
	 *
	 * @param column The column to return the format for
	 * @return The format object
	 */
	private ValueFormat getColumnFormat(ColumnDefinition column) {
		String datatype = column.getDatatype();
		ValueFormat format = ValueFormat.TO_STRING;

		// TODO: allow to define column-specific formats

		if (datatype != null) {
			if (column.getProperty(CONTENT_TYPE, null) ==
				ContentType.DATE_TIME ||
				Timestamp.class.getName().endsWith(datatype)) {
				format = GwtDateTimeFormat.SHORT_DATE_TIME;
			} else if (Date.class.getName().endsWith(datatype)) {
				format = GwtDateTimeFormat.SHORT_DATE;
			} else if (Time.class.getName().endsWith(datatype)) {
				format = GwtDateTimeFormat.SHORT_TIME;
			}
		}

		return format;
	}

	/**
	 * Returns the width of a certain table column.
	 *
	 * @param column The column index
	 * @return The column width in pixels
	 */
	private int getColumnWidth(int column) {
		String htmlWidth = columnHeaders.get(column).getColumnWidth();
		int width;

		if (htmlWidth != null) {
			width = Integer.parseInt(
				htmlWidth.substring(0, htmlWidth.length() - 2));
		} else {
			width = headerTable
				.getColumnFormatter()
				.getElement(column)
				.getOffsetWidth();
		}

		return width;
	}

	/**
	 * Sets the style of a certain column depending on user interface
	 * properties
	 * of the column definition.
	 *
	 * @param columnIndex The column index
	 * @param column      The column definition
	 */
	private void setColumnStyle(int columnIndex, ColumnDefinition column) {
		ColumnFormatter headerColumnFormatter =
			headerTable.getColumnFormatter();
		ColumnFormatter dataColumnFormatter =
			gwtTable.getDataTable().getColumnFormatter();

		String style = TextConvert.capitalizedLastElementOf(column.getId());

		headerColumnFormatter.setStyleName(columnIndex, style);
		dataColumnFormatter.setStyleName(columnIndex, style);
	}

	/**
	 * Sets the width of a certain column.
	 *
	 * @param column The column index
	 * @param width  The new column width
	 */
	private void setColumnWidth(int column, String width) {
		columnHeaders.get(column).htmlColumnWidth = width;

		headerTable.getColumnFormatter().setWidth(column, width);
		gwtTable.getDataTable().getColumnFormatter().setWidth(column, width);
	}

	/**
	 * A composite that is used as the column header.
	 *
	 * @author eso
	 */
	class ColumnHeader extends Composite
		implements MouseDownHandler, DoubleClickHandler {

		private final int columnIndex;

		private final Image sortIndicator;

		private final Label titleLabel;

		private String defaultHtmlColumnWidth;

		private String htmlColumnWidth;

		/**
		 * Creates a new instance.
		 *
		 * @param title The column title
		 * @param index The index of this instance in the table
		 */
		ColumnHeader(String title, int index) {
			this.columnIndex = index;

			FlowPanel titlePanel = new FlowPanel();
			HTML resizer = new HTML();

			titleLabel = new Label(title);
			sortIndicator = new Image();

			sortIndicator.addStyleName(GwtTable.CSS.ewtSortIndicator());

			resizer.setHTML("&nbsp;");
			resizer.addStyleName(GwtTable.CSS.ewtResizer());
			resizer.addDoubleClickHandler(this);
			resizer.addMouseDownHandler(this);

			if (index > 0) {
				titlePanel.add(resizer);
			}

			titlePanel.add(sortIndicator);
			titlePanel.add(titleLabel);

			initWidget(titlePanel);
			setSortIndicator(null);
		}

		/**
		 * Returns the title label widget of this header.
		 *
		 * @return The title label
		 */
		public final Label getTitleLabel() {
			return titleLabel;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void onDoubleClick(DoubleClickEvent event) {
			int column = columnIndex - 1;
			String defaultWidth =
				columnHeaders.get(column).defaultHtmlColumnWidth;

			TableHeader.this.setColumnWidth(column, defaultWidth);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void onMouseDown(MouseDownEvent event) {
			setResizeColumn(columnIndex - 1, event.getClientX());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return "Column[" + titleLabel.getText() + ", " + htmlColumnWidth +
				"]";
		}

		/**
		 * Returns the index of this header's column.
		 *
		 * @return The column index
		 */
		final int getColumnIndex() {
			return columnIndex;
		}

		/**
		 * Returns the column width in pixels in HTML format.
		 *
		 * @return The column width
		 */
		final String getColumnWidth() {
			return htmlColumnWidth;
		}

		/**
		 * Sets the column width in pixels.
		 *
		 * @param width The column width in pixels or -1 if to be calculated
		 */
		final void setColumnWidth(int width) {
			htmlColumnWidth = width >= 0 ? htmlColumnWidth + "px" : null;
			defaultHtmlColumnWidth = htmlColumnWidth;
		}

		/**
		 * Sets the sort indicator.
		 *
		 * @param direction The new sort indicator
		 */
		void setSortIndicator(SortDirection direction) {
			if (direction != null) {
				sortIndicator.setResource(direction == SortDirection.ASCENDING ?
				                          GwtTable.RES.imSortAscending() :
				                          GwtTable.RES.imSortDescending());
				sortIndicator.setVisible(true);
			} else {
				sortIndicator.setVisible(false);
			}

			sortIndicator.addStyleName(GwtTable.CSS.ewtSortIndicator());
		}
	}
}
