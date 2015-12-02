//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// This file is a part of the 'gewt' project.
// Copyright 2015 Elmar Sonnenschein, esoco GmbH, Flensburg, Germany
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

import de.esoco.ewt.UserInterfaceContext;
import de.esoco.ewt.impl.gwt.GwtDateTimeFormat;
import de.esoco.ewt.impl.gwt.ValueFormat;

import de.esoco.lib.model.ColumnDefinition;
import de.esoco.lib.model.DataModel;
import de.esoco.lib.model.SortableDataModel;
import de.esoco.lib.model.SortableDataModel.SortMode;
import de.esoco.lib.property.UserInterfaceProperties.ContentType;
import de.esoco.lib.text.TextConvert;

import java.sql.Time;
import java.sql.Timestamp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

import static de.esoco.lib.property.UserInterfaceProperties.CONTENT_TYPE;
import static de.esoco.lib.property.UserInterfaceProperties.HAS_IMAGES;
import static de.esoco.lib.property.UserInterfaceProperties.MAX_CHARS;
import static de.esoco.lib.property.UserInterfaceProperties.MIN_CHARS;
import static de.esoco.lib.property.UserInterfaceProperties.SEARCHABLE;
import static de.esoco.lib.property.UserInterfaceProperties.SORTABLE;


/********************************************************************
 * Header implementation for {@link GwtTable}.
 *
 * @author eso
 */
class TableHeader extends Composite implements ClickHandler, MouseMoveHandler,
											   MouseUpHandler, MouseOutHandler
{
	//~ Static fields/initializers ---------------------------------------------

	private static final int MIN_COLUMN_WIDTH = 5;

	//~ Instance fields --------------------------------------------------------

	private GwtTable rTable;

	private Grid aHeaderTable = new Grid(1, 1);

	DataModel<ColumnDefinition> rColumns;
	private List<ColumnHeader>  aColumnHeaders = new ArrayList<ColumnHeader>();
	private List<ValueFormat>   aColumnFormats = new ArrayList<ValueFormat>();
	private List<String>	    aColumnStyles  = new ArrayList<String>();

	private int     nResizeColumn   = -1;
	private int     nResizePosition = 0;
	private boolean bWasResizeClick = false;

	private HandlerRegistration rMouseMoveHandler;
	private HandlerRegistration rMouseUpHandler;
	private HandlerRegistration rMouseOutHandler;

	//~ Constructors -----------------------------------------------------------

	/***************************************
	 * Creates a new instance.
	 *
	 * @param rTable The table this instance belongs to
	 */
	public TableHeader(GwtTable rTable)
	{
		this.rTable = rTable;

		initWidget(aHeaderTable);

		aHeaderTable.setWidth("100%");
		aHeaderTable.addClickHandler(this);
		aHeaderTable.setStylePrimaryName(GwtTable.CSS.ewtHeader());
	}

	//~ Methods ----------------------------------------------------------------

	/***************************************
	 * Returns the number of table columns.
	 *
	 * @return The column count
	 */
	public final int getColumnCount()
	{
		return rColumns.getElementCount();
	}

	/***************************************
	 * Returns the column definition for a certain column index.
	 *
	 * @param  nColumn The column index
	 *
	 * @return The column definition
	 */
	public final ColumnDefinition getColumnDefinition(int nColumn)
	{
		return rColumns.getElement(nColumn);
	}

	/***************************************
	 * Returns the value format for a certain column.
	 *
	 * @param  nColumn The column index
	 *
	 * @return The column format
	 */
	public final ValueFormat getColumnFormat(int nColumn)
	{
		return aColumnFormats.get(nColumn);
	}

	/***************************************
	 * @see ClickHandler#onClick(ClickEvent)
	 */
	@Override
	public void onClick(ClickEvent rEvent)
	{
		if (bWasResizeClick)
		{
			// button release at the end of a column resize causes click event
			bWasResizeClick = false;
		}
		else if (rTable.canHandleInput())
		{
			Cell rCell = aHeaderTable.getCellForEvent(rEvent);

			if (rCell != null)
			{
				changeSorting(rCell.getCellIndex());
			}
		}
	}

	/***************************************
	 * @see MouseMoveHandler#onMouseMove(MouseMoveEvent)
	 */
	@Override
	public void onMouseMove(MouseMoveEvent rEvent)
	{
		if (nResizeColumn >= 0)
		{
			int nMouseX = rEvent.getClientX();

			changeColumnWidth(nResizeColumn, nMouseX - nResizePosition);
			nResizePosition = nMouseX;
		}
	}

	/***************************************
	 * @see MouseOutHandler#onMouseOut(MouseOutEvent)
	 */
	@Override
	public void onMouseOut(MouseOutEvent rEvent)
	{
		setResizeColumn(-1, -1);
	}

	/***************************************
	 * @see MouseUpHandler#onMouseUp(MouseUpEvent)
	 */
	@Override
	public void onMouseUp(MouseUpEvent rEvent)
	{
		setResizeColumn(-1, -1);
	}

	/***************************************
	 * Calculates the average width of a character in data cells.
	 *
	 * @return The character width
	 */
	int calcCharWidth()
	{
		FlexTable rDataTable = rTable.getDataTable();
		int		  nCharWidth;

		boolean bHasRows = rDataTable.getRowCount() > 0;
		Widget  rWidget  = null;

		if (bHasRows)
		{
			// save existing widget if already initialized
			rWidget = rDataTable.getWidget(0, 0);
		}

		HorizontalPanel aPanel = new HorizontalPanel();
		Label		    aLabel = new Label("N");

		aPanel.add(aLabel);
		rDataTable.setWidget(0, 0, aPanel);
		nCharWidth = aLabel.getOffsetWidth();

		if (bHasRows)
		{
			rDataTable.setWidget(0, 0, rWidget);
		}
		else
		{
			rDataTable.removeRow(0);
		}

		return nCharWidth;
	}

	/***************************************
	 * Calculates the column width based on the available table width.
	 */
	void calcColumnWidths()
	{
		int nCharWidth		 = calcCharWidth();
		int nColumnCount     = rColumns.getElementCount();
		int nRemainingWidth  = rTable.getDataWidth();
		int nVariableColumns = 0;

		for (int nCol = 0; nCol < nColumnCount; nCol++)
		{
			ColumnDefinition rColumn = rColumns.getElement(nCol);
			ColumnHeader     rHeader = aColumnHeaders.get(nCol);

			String sDatatype    = rColumn.getDatatype();
			int    nMinWidth    = rColumn.getIntProperty(MIN_CHARS, 0);
			int    nMaxWidth    = rColumn.getIntProperty(MAX_CHARS, 0);
			int    nColumnWidth = 0;

			if (rColumn.getProperty(CONTENT_TYPE, null) ==
				ContentType.DATE_TIME ||
				Timestamp.class.getName().endsWith(sDatatype))
			{
				nColumnWidth = 12;
			}
			else if (Date.class.getName().endsWith(sDatatype) ||
					 Time.class.getName().endsWith(sDatatype))
			{
				nColumnWidth = 8;
			}
			else if (Integer.class.getName().endsWith(sDatatype))
			{
				nColumnWidth = 10;
			}
			else if (Enum.class.getName().endsWith(sDatatype))
			{
				nColumnWidth = rColumn.hasFlag(HAS_IMAGES) ? 3 : 10;
			}
			else if (nMaxWidth > 0)
			{
				nColumnWidth = nMaxWidth;
			}

			if (nColumnWidth != 0)
			{
				nColumnWidth =
					checkColumnWidth(nColumnWidth, nMinWidth, nMaxWidth);

				if (nCol == 0 && rTable.isHierarchical())
				{
					nColumnWidth += 3;
				}

				nColumnWidth *= nCharWidth;

				rHeader.setColumnWidth(nColumnWidth);
				nRemainingWidth -= nColumnWidth;
			}
			else
			{
				rHeader.setColumnWidth(-1);
				nVariableColumns++;
			}
		}

		if (nVariableColumns > 0)
		{
			distributeRemainingWidth(nVariableColumns,
									 nRemainingWidth,
									 nCharWidth);
		}

		// let the last column fill the remaining space
		aColumnHeaders.get(nColumnCount - 1).sWidth = null;
	}

	/***************************************
	 * Returns the style of a certain column.
	 *
	 * @param  nColumn The column index
	 *
	 * @return The column style
	 */
	final String getColumnStyle(int nColumn)
	{
		return aColumnStyles.get(nColumn);
	}

	/***************************************
	 * Initializes the table columns from the column data model. This will also
	 * set the filterable columns in the filter panel if it is available.
	 *
	 * @param  rFilterPanel The table's filter panel or NULL for none
	 *
	 * @return TRUE if the columns model contain at least one text format column
	 */
	boolean initColumns(TableFilterPanel rFilterPanel)
	{
		UserInterfaceContext rContext = rTable.getContext();

		int     nColumn		    = 0;
		boolean bHasTextColumns = false;

		aColumnHeaders.clear();
		aColumnFormats.clear();
		aHeaderTable.removeRow(0);
		aHeaderTable.resize(1, rColumns.getElementCount());
		aHeaderTable.getRowFormatter()
					.setStylePrimaryName(0, GwtTable.CSS.ewtHeader());

		for (ColumnDefinition rColumn : rColumns)
		{
			String sTitle = rContext.expandResource(rColumn.getTitle());

			if (sTitle.startsWith("col"))
			{
				sTitle = sTitle.substring(3);
			}

			String	    sDatatype     = rColumn.getDatatype();
			ValueFormat rColumnFormat = getColumnFormat(rColumn);

			ColumnHeader aHeader = new ColumnHeader(sTitle, nColumn);

			setColumnStyle(nColumn, rColumn);
			aHeaderTable.setWidget(0, nColumn++, aHeader);

			aColumnHeaders.add(aHeader);
			aColumnFormats.add(rColumnFormat);

			if (rFilterPanel != null && rColumn.hasFlag(SEARCHABLE))
			{
				rFilterPanel.addFilterColumn(rColumn);

				if (!bHasTextColumns)
				{
					bHasTextColumns =
						String.class.getName().endsWith(sDatatype);
				}
			}
			else
			{
				aHeader.addStyleName(GwtTable.CSS.limited());
			}
		}

		return bHasTextColumns;
	}

	/***************************************
	 * Sets the widths of the table columns.
	 */
	void setAllColumnWidths()
	{
		ColumnFormatter rHeaderColumnFormatter =
			aHeaderTable.getColumnFormatter();

		ColumnFormatter rDataColumnFormatter =
			rTable.getDataTable().getColumnFormatter();

		int nColumns = rColumns.getElementCount();

		for (int nCol = 0; nCol < nColumns; nCol++)
		{
			String sColumnWidth = aColumnHeaders.get(nCol).getColumnWidth();

			if (sColumnWidth != null)
			{
				rHeaderColumnFormatter.setWidth(nCol, sColumnWidth);
				rDataColumnFormatter.setWidth(nCol, sColumnWidth);
			}
		}
	}

	/***************************************
	 * Sets the data model that contains information about the table columns.
	 *
	 * @param  rNewColumns The table columns data model
	 *
	 * @return TRUE if the columns have changed
	 */
	boolean setColumns(DataModel<ColumnDefinition> rNewColumns)
	{
		boolean bChanged = rColumns == null || !rColumns.equals(rNewColumns);

		if (bChanged)
		{
			aColumnStyles.clear();
			rColumns = rNewColumns;

			for (ColumnDefinition rColumn : rColumns)
			{
				String sStyle    = rColumn.getId();
				String sDatatype = rColumn.getDatatype();

				sStyle = TextConvert.lastElementOf(sStyle).toLowerCase();

				if (!sDatatype.equals("String"))
				{
					sStyle = sStyle + " " + sDatatype;
				}

				aColumnStyles.add(sDatatype);
			}
		}

		return bChanged;
	}

	/***************************************
	 * Package-internal method to set or clear the current resize column. Will
	 * be invoked from the {@link ColumnHeader} class.
	 *
	 * @param nColumn The header of the currently resized table column
	 * @param nMouseX The starting X position of the resize
	 */
	final void setResizeColumn(int nColumn, int nMouseX)
	{
		String  sNoSelectStyle = GwtTable.CSS.ewtNoSelect();
		boolean bColumnResize  = nColumn >= 0;

		nResizeColumn   = nColumn;
		nResizePosition = nMouseX;

		// only start if table not busy but always end to prevent resizing from
		// remaining active due to external table update events
		if (bColumnResize)
		{
			if (rTable.canHandleInput())
			{
				FocusPanel rFocusPanel = rTable.getFocusPanel();

				rTable.getDataTable().addStyleName(sNoSelectStyle);

				rMouseMoveHandler = rFocusPanel.addMouseMoveHandler(this);
				rMouseUpHandler   = rFocusPanel.addMouseUpHandler(this);
				rMouseOutHandler  = rFocusPanel.addMouseOutHandler(this);
				bWasResizeClick   = true;
			}
		}
		else if (rMouseMoveHandler != null)
		{
			rMouseMoveHandler.removeHandler();
			rMouseUpHandler.removeHandler();
			rMouseOutHandler.removeHandler();

			rMouseMoveHandler = null;
			rMouseUpHandler   = null;
			rMouseOutHandler  = null;

			rTable.getDataTable().removeStyleName(sNoSelectStyle);
		}
	}

	/***************************************
	 * Changes the width of a certain table column and of the preceding column
	 * if necessary.
	 *
	 * @param nColumn The column index
	 * @param nChange The new column width in pixels
	 */
	private void changeColumnWidth(int nColumn, int nChange)
	{
		int nCurrentWidth = getColumnWidth(nColumn);

		if (nChange > 0 || nCurrentWidth > MIN_COLUMN_WIDTH)
		{
			int nNewWidth = Math.max(nCurrentWidth + nChange, MIN_COLUMN_WIDTH);

			setColumnWidth(nColumn, nNewWidth + "px");
		}
	}

	/***************************************
	 * Changes the sorting for a certain column.
	 *
	 * @param nCol The column to change the sorting for
	 */
	private void changeSorting(int nCol)
	{
		ColumnDefinition				  rColumn = rColumns.getElement(nCol);
		DataModel<? extends DataModel<?>> rData   = rTable.getData();

		if (rColumn.hasFlag(SORTABLE) && rData instanceof SortableDataModel<?>)
		{
			SortableDataModel<?> rModel    = (SortableDataModel<?>) rData;
			String				 sColumnId = rColumn.getId();
			SortMode			 rSortMode = rModel.getSortMode(sColumnId);

			ColumnHeader rHeader =
				(ColumnHeader) aHeaderTable.getWidget(0, nCol);

			if (rSortMode == SortMode.ASCENDING)
			{
				rSortMode = SortMode.DESCENDING;
			}
			else if (rSortMode == SortMode.DESCENDING)
			{
				rSortMode = null;
			}
			else
			{
				rSortMode = SortMode.ASCENDING;
			}

			rHeader.setSortIndicator(rSortMode);
			rModel.setSortMode(sColumnId, rSortMode);
			rTable.update();
		}
	}

	/***************************************
	 * Checks a column width against the minimum and maximum width of the
	 * column.
	 *
	 * @param  nColumnWidth The current column width
	 * @param  nMinWidth    The minimum width
	 * @param  nMaxWidth    The maximum width
	 *
	 * @return The adjusted column width
	 */
	private int checkColumnWidth(int nColumnWidth, int nMinWidth, int nMaxWidth)
	{
		if (nColumnWidth < nMinWidth)
		{
			nColumnWidth = nMinWidth;
		}

		if (nMaxWidth > 0 && nColumnWidth > nMaxWidth)
		{
			nColumnWidth = nMaxWidth;
		}

		return nColumnWidth;
	}

	/***************************************
	 * Distributes the remaining table width over all variable columns.
	 *
	 * @param nVariableColumns The number of variable-width columns
	 * @param nRemainingWidth  The remaining width in pixels to distribute
	 * @param nCharWidth       The average character width
	 */
	private void distributeRemainingWidth(int nVariableColumns,
										  int nRemainingWidth,
										  int nCharWidth)
	{
		int nColumnWidth = nRemainingWidth / nVariableColumns;
		int nRemainder   = nRemainingWidth % nVariableColumns;
		int nLastColumn  = getColumnCount() - 1;

		for (int nCol = 0; nCol <= nLastColumn && nRemainingWidth > 0; nCol++)
		{
			ColumnHeader rHeader = aColumnHeaders.get(nCol);

			if (rHeader.getColumnWidth() == null)
			{
				ColumnDefinition rColumn = rColumns.getElement(nCol);

				int nMinWidth =
					rColumn.getIntProperty(MIN_CHARS, 0) * nCharWidth;

				nColumnWidth =
					checkColumnWidth(nColumnWidth, nMinWidth, nColumnWidth);

				// add the remainder to the first column
				nRemainingWidth -= nColumnWidth + nRemainder;
				rHeader.setColumnWidth(nColumnWidth + nRemainder);
				nRemainder = 0;
			}
		}
	}

	/***************************************
	 * Returns the appropriate formatting object for a certain column.
	 *
	 * @param  rColumn The column to return the format for
	 *
	 * @return The format object
	 */
	private ValueFormat getColumnFormat(ColumnDefinition rColumn)
	{
		String	    sDatatype = rColumn.getDatatype();
		ValueFormat rFormat   = ValueFormat.TO_STRING;

		// TODO: allow to define column-specific formats

		if (sDatatype != null)
		{
			if (rColumn.getProperty(CONTENT_TYPE, null) ==
				ContentType.DATE_TIME ||
				Timestamp.class.getName().endsWith(sDatatype))
			{
				rFormat = GwtDateTimeFormat.SHORT_DATE_TIME;
			}
			else if (Date.class.getName().endsWith(sDatatype))
			{
				rFormat = GwtDateTimeFormat.SHORT_DATE;
			}
			else if (Time.class.getName().endsWith(sDatatype))
			{
				rFormat = GwtDateTimeFormat.SHORT_TIME;
			}
		}

		return rFormat;
	}

	/***************************************
	 * Returns the width of a certain table column.
	 *
	 * @param  nColumn The column index
	 *
	 * @return The column width in pixels
	 */
	private int getColumnWidth(int nColumn)
	{
		String sHtmlWidth = aColumnHeaders.get(nColumn).getColumnWidth();
		int    nWidth;

		if (sHtmlWidth != null)
		{
			nWidth =
				Integer.parseInt(sHtmlWidth.substring(0,
													  sHtmlWidth.length() - 2));
		}
		else
		{
			nWidth =
				aHeaderTable.getColumnFormatter().getElement(nColumn)
							.getOffsetWidth();
		}

		return nWidth;
	}

	/***************************************
	 * Sets the style of a certain column depending on user interface properties
	 * of the column definition.
	 *
	 * @param nColumn The column index
	 * @param rColumn The column definition
	 */
	private void setColumnStyle(int nColumn, ColumnDefinition rColumn)
	{
		ColumnFormatter rHeaderColumnFormatter =
			aHeaderTable.getColumnFormatter();
		ColumnFormatter rDataColumnFormatter   =
			rTable.getDataTable().getColumnFormatter();

		String sStyle = TextConvert.capitalizedLastElementOf(rColumn.getId());

		rHeaderColumnFormatter.setStyleName(nColumn, sStyle);
		rDataColumnFormatter.setStyleName(nColumn, sStyle);
	}

	/***************************************
	 * Sets the width of a certain column.
	 *
	 * @param nColumn The column index
	 * @param sWidth  The new column width
	 */
	private void setColumnWidth(int nColumn, String sWidth)
	{
		aColumnHeaders.get(nColumn).sWidth = sWidth;

		aHeaderTable.getColumnFormatter().setWidth(nColumn, sWidth);
		rTable.getDataTable().getColumnFormatter().setWidth(nColumn, sWidth);
	}

	//~ Inner Classes ----------------------------------------------------------

	/********************************************************************
	 * A composite that is used as the column header.
	 *
	 * @author eso
	 */
	class ColumnHeader extends Composite implements MouseDownHandler,
													DoubleClickHandler
	{
		//~ Instance fields ----------------------------------------------------

		private int    nColumnIndex;
		private String sDefaultWidth;
		private String sWidth;

		private Image aSortIndicator;
		private Label aTitleLabel;
		private HTML  aResizer;

		//~ Constructors -------------------------------------------------------

		/***************************************
		 * Creates a new instance.
		 *
		 * @param sTitle The column title
		 * @param nIndex The index of this instance in the table
		 */
		ColumnHeader(String sTitle, int nIndex)
		{
			this.nColumnIndex = nIndex;

			FlowPanel aTitlePanel = new FlowPanel();

			aTitleLabel    = new Label(sTitle);
			aResizer	   = new HTML();
			aSortIndicator = new Image();

			aSortIndicator.addStyleName(GwtTable.CSS.ewtSortIndicator());

			aResizer.setHTML("&nbsp;");
			aResizer.addStyleName(GwtTable.CSS.ewtResizer());
			aResizer.addDoubleClickHandler(this);
			aResizer.addMouseDownHandler(this);

			if (nIndex > 0)
			{
				aTitlePanel.add(aResizer);
			}

			aTitlePanel.add(aSortIndicator);
			aTitlePanel.add(aTitleLabel);

			initWidget(aTitlePanel);
			setSortIndicator(null);
		}

		//~ Methods ------------------------------------------------------------

		/***************************************
		 * Returns the title label widget of this header.
		 *
		 * @return The title label
		 */
		public final Label getTitleLabel()
		{
			return aTitleLabel;
		}

		/***************************************
		 * {@inheritDoc}
		 */
		@Override
		public void onDoubleClick(DoubleClickEvent rEvent)
		{
			int    nColumn		 = nColumnIndex - 1;
			String sDefaultWidth = aColumnHeaders.get(nColumn).sDefaultWidth;

			TableHeader.this.setColumnWidth(nColumn, sDefaultWidth);
		}

		/***************************************
		 * {@inheritDoc}
		 */
		@Override
		public void onMouseDown(MouseDownEvent rEvent)
		{
			setResizeColumn(nColumnIndex - 1, rEvent.getClientX());
		}

		/***************************************
		 * {@inheritDoc}
		 */
		@Override
		public String toString()
		{
			return "Column[" + aTitleLabel.getText() + ", " + sWidth + "]";
		}

		/***************************************
		 * Returns the index of this header's column.
		 *
		 * @return The column index
		 */
		final int getColumnIndex()
		{
			return nColumnIndex;
		}

		/***************************************
		 * Returns the column width in pixels in HTML format.
		 *
		 * @return The column width
		 */
		final String getColumnWidth()
		{
			return sWidth;
		}

		/***************************************
		 * Sets the column width in pixels.
		 *
		 * @param nWidth The column width in pixels or -1 if to be calculated
		 */
		final void setColumnWidth(int nWidth)
		{
			sWidth		  = nWidth >= 0 ? nWidth + "px" : null;
			sDefaultWidth = sWidth;
		}

		/***************************************
		 * Sets the sort indicator.
		 *
		 * @param rMode The new sort indicator
		 */
		void setSortIndicator(SortMode rMode)
		{
			if (rMode != null)
			{
				aSortIndicator.setResource(rMode == SortMode.ASCENDING
										   ? GwtTable.RES.imSortAscending()
										   : GwtTable.RES.imSortDescending());
				aSortIndicator.setVisible(true);
			}
			else
			{
				aSortIndicator.setVisible(false);
			}

			aSortIndicator.addStyleName(GwtTable.CSS.ewtSortIndicator());
		}
	}
}
