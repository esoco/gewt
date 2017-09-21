//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// This file is a part of the 'gewt' project.
// Copyright 2017 Elmar Sonnenschein, esoco GmbH, Flensburg, Germany
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

import de.esoco.ewt.EWT;
import de.esoco.ewt.UserInterfaceContext;
import de.esoco.ewt.component.Table;
import de.esoco.ewt.component.TableControl;
import de.esoco.ewt.event.EventType;
import de.esoco.ewt.graphics.ImageRef;
import de.esoco.ewt.impl.gwt.GewtCss;
import de.esoco.ewt.impl.gwt.GewtEventDispatcher;
import de.esoco.ewt.impl.gwt.GewtResources;
import de.esoco.ewt.impl.gwt.ValueFormat;

import de.esoco.lib.model.Callback;
import de.esoco.lib.model.ColumnDefinition;
import de.esoco.lib.model.DataModel;
import de.esoco.lib.model.Downloadable;
import de.esoco.lib.model.HierarchicalDataModel;
import de.esoco.lib.model.RemoteDataModel;
import de.esoco.lib.model.SearchableDataModel;
import de.esoco.lib.property.Flags;
import de.esoco.lib.property.SingleSelection;

import java.util.Collection;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.HasAllFocusHandlers;
import com.google.gwt.event.dom.client.HasAllKeyHandlers;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.HasDoubleClickHandlers;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.CustomScrollPanel;
import com.google.gwt.user.client.ui.DecoratedPopupPanel;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTMLTable.Cell;
import com.google.gwt.user.client.ui.HTMLTable.CellFormatter;
import com.google.gwt.user.client.ui.HTMLTable.RowFormatter;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

import static de.esoco.lib.model.SearchableDataModel.CONSTRAINT_OR_PREFIX;
import static de.esoco.lib.property.StyleProperties.HAS_IMAGES;


/********************************************************************
 * A GWT-implementation of a composite that can display tabular data. It also
 * supports the display of hierarchical data.
 */
public class GwtTable extends Composite
	implements SingleSelection, Focusable, HasAllFocusHandlers,
			   HasAllKeyHandlers, HasClickHandlers, HasDoubleClickHandlers,
			   ClickHandler, KeyDownHandler, RequiresResize,
			   Callback<RemoteDataModel<DataModel<?>>>
{
	//~ Static fields/initializers ---------------------------------------------

	static final GewtResources RES = GewtResources.INSTANCE;
	static final GewtCss	   CSS = RES.css();

	private static final int HEADER_ROW  = 0;
	private static final int DATA_ROW    = 1;
	private static final int TOOLBAR_ROW = 2;

	private static final int INFO_TIMER_MILLISECONDS = 500;

	//~ Instance fields --------------------------------------------------------

	private boolean bEnabled;
	private boolean bHierarchical;

	private UserInterfaceContext rContext;
	private GewtEventDispatcher  rEventDispatcher;

	private Grid	    aMainPanel   = new Grid(3, 1);
	private ScrollPanel aScrollPanel = new CustomScrollPanel();
	private FocusPanel  aFocusPanel  = new FocusPanel();
	private TableHeader aHeader		 = new TableHeader(this);
	private FlexTable   aDataTable   = new FlexTable();

	private TableToolBar aToolBar = null;

	private DataModel<? extends DataModel<?>> rData;

	private DecoratedPopupPanel aInfoPopupPanel   = null;
	private Timer			    aInfoTimer;
	private Timer			    aDoubleClickTimer;

	private int nBusyIndicatorCount = 0;
	private int nFirstRow		    = 0;
	private int nVisibleDataRows    = 0;
	private int nTableRows		    = -1;

	private boolean bUpdateInProgress = false;
	private boolean bColumnsChanged   = false;

	private int nDataWidth  = 0;
	private int nDataHeight = 0;

	private DataModel<?> rCurrentSelection;
	private int			 nSelectedRow  = -1;
	private int			 nNewSelection = -1;

	//~ Constructors -----------------------------------------------------------

	/***************************************
	 * Creates a new instance.
	 *
	 * @param bHierarchical If TRUE the table will display instances of {@link
	 *                      HierarchicalDataModel} as a tree
	 */
	public GwtTable(boolean bHierarchical)
	{
		this.bHierarchical = bHierarchical;

		aDataTable.addClickHandler(this);
		aDataTable.addStyleName(CSS.ewtDataTable());

		aScrollPanel.setWidget(aDataTable);
		aScrollPanel.setAlwaysShowScrollBars(false);

		aMainPanel.setWidget(HEADER_ROW, 0, aHeader);
		aMainPanel.setWidget(DATA_ROW, 0, aScrollPanel);
		aMainPanel.getCellFormatter().setWidth(DATA_ROW, 0, "100%");
		aMainPanel.getCellFormatter().setHeight(DATA_ROW, 0, "100%");
		aMainPanel.setCellSpacing(0);
		aMainPanel.setCellPadding(0);

		aFocusPanel.setWidget(aMainPanel);
		aFocusPanel.addKeyDownHandler(this);

		setToMaximumSize(aDataTable, aScrollPanel, aMainPanel, aFocusPanel);

		// place the focus panel in a table-based panel to force 100% height
		// because FocusPanel is based on div
		DockPanel aTablePanel = new DockPanel();

		aTablePanel.add(aFocusPanel, DockPanel.CENTER);
		aTablePanel.setCellWidth(aFocusPanel, "100%");
		aTablePanel.setCellHeight(aFocusPanel, "100%");

		initWidget(aTablePanel);
		setStylePrimaryName(CSS.ewtTable());
	}

	//~ Methods ----------------------------------------------------------------

	/***************************************
	 * @see HasAllFocusHandlers#addBlurHandler(BlurHandler)
	 */
	@Override
	public HandlerRegistration addBlurHandler(BlurHandler rHandler)
	{
		return aFocusPanel.addBlurHandler(rHandler);
	}

	/***************************************
	 * @see HasClickHandlers#addClickHandler(ClickHandler)
	 */
	@Override
	public HandlerRegistration addClickHandler(ClickHandler rHandler)
	{
		return aFocusPanel.addClickHandler(rHandler);
	}

	/***************************************
	 * @see HasDoubleClickHandlers#addDoubleClickHandler(DoubleClickHandler)
	 */
	@Override
	public HandlerRegistration addDoubleClickHandler(
		DoubleClickHandler rHandler)
	{
		return aDataTable.addDoubleClickHandler(rHandler);
	}

	/***************************************
	 * @see HasAllFocusHandlers#addFocusHandler(FocusHandler)
	 */
	@Override
	public HandlerRegistration addFocusHandler(FocusHandler rHandler)
	{
		return aFocusPanel.addFocusHandler(rHandler);
	}

	/***************************************
	 * @see HasAllKeyHandlers#addKeyDownHandler(KeyDownHandler)
	 */
	@Override
	public HandlerRegistration addKeyDownHandler(KeyDownHandler rHandler)
	{
		return aFocusPanel.addKeyDownHandler(rHandler);
	}

	/***************************************
	 * @see HasAllKeyHandlers#addKeyPressHandler(KeyPressHandler)
	 */
	@Override
	public HandlerRegistration addKeyPressHandler(KeyPressHandler rHandler)
	{
		return aFocusPanel.addKeyPressHandler(rHandler);
	}

	/***************************************
	 * @see HasAllKeyHandlers#addKeyUpHandler(KeyUpHandler)
	 */
	@Override
	public HandlerRegistration addKeyUpHandler(KeyUpHandler rHandler)
	{
		return aFocusPanel.addKeyUpHandler(rHandler);
	}

	/***************************************
	 * @see Table#getColumns()
	 */
	public final DataModel<ColumnDefinition> getColumns()
	{
		return aHeader.rColumns;
	}

	/***************************************
	 * Returns the user interface context of this table.
	 *
	 * @return The user interface context
	 */
	public final UserInterfaceContext getContext()
	{
		return rContext;
	}

	/***************************************
	 * @see Table#getData()
	 */
	public final DataModel<? extends DataModel<?>> getData()
	{
		return rData;
	}

	/***************************************
	 * @see TableControl#getSelection()
	 */
	public DataModel<?> getSelection()
	{
		DataModel<?> rSelection = null;

		if (nSelectedRow >= 0 && rData != null)
		{
			if (bHierarchical)
			{
				int		 nRow  = nSelectedRow;
				TreeNode rNode = (TreeNode) aDataTable.getWidget(nRow, 0);

				rSelection = rNode.getRowModel();
			}
			else if (rData.getElementCount() > 0)
			{
				int nSelection = getSelectionIndex();

				rSelection = rData.getElement(nSelection);
			}
		}

		return rSelection;
	}

	/***************************************
	 * @see SingleSelection#getSelectionIndex()
	 */
	@Override
	public final int getSelectionIndex()
	{
		return nSelectedRow >= 0 ? nSelectedRow + nFirstRow : -1;
	}

	/***************************************
	 * @see Focusable#getTabIndex()
	 */
	@Override
	public final int getTabIndex()
	{
		return aFocusPanel.getTabIndex();
	}

	/***************************************
	 * Returns the visible row count.
	 *
	 * @return The visible row count
	 */
	public final int getVisibleRowCount()
	{
		return nTableRows;
	}

	/***************************************
	 * Checks whether the table is currently busy with updating the table
	 * display and therefore displaying a busy indicator.
	 *
	 * @return TRUE if the table is currently busy
	 */
	public final boolean isBusy()
	{
		return nBusyIndicatorCount != 0;
	}

	/***************************************
	 * Returns the enabled state of this table.
	 *
	 * @return The enabled state
	 */
	public final boolean isEnabled()
	{
		return bEnabled;
	}

	/***************************************
	 * Check whether this is a hierarchical table.
	 *
	 * @return TRUE for a hierarchical table
	 */
	public final boolean isHierarchical()
	{
		return bHierarchical;
	}

	/***************************************
	 * @see ClickHandler#onClick(ClickEvent)
	 */
	@Override
	public void onClick(final ClickEvent rEvent)
	{
		if (canHandleInput())
		{
			final Cell rCell = aDataTable.getCellForEvent(rEvent);

			if (aDoubleClickTimer != null)
			{
				aDoubleClickTimer.cancel();
				aDoubleClickTimer = null;
				setSelection(rCell, false);
				rEventDispatcher.dispatchEvent(EventType.ACTION,
											   rEvent.getNativeEvent());
			}
			else
			{
				aDoubleClickTimer =
					new Timer()
					{
						@Override
						public void run()
						{
							aDoubleClickTimer = null;
							setSelection(rCell, true);
						}
					};
				aDoubleClickTimer.schedule(EWT.getDoubleClickInterval());
			}
		}
	}

	/***************************************
	 * Error handling for remote model invocations.
	 *
	 * @see Callback#onError(Throwable)
	 */
	@Override
	public void onError(Throwable e)
	{
		hideBusyIndicator();

		String sMessage =
			rContext.expandResource("$msgTableModelError") +
			": " + rContext.expandResource(e.getMessage());

		showInfo(new Label(sMessage), true);
		bUpdateInProgress = false;
	}

	/***************************************
	 * Event handling for keyboard input in the focus panel.
	 *
	 * @see KeyDownHandler#onKeyDown(KeyDownEvent)
	 */
	@Override
	public void onKeyDown(KeyDownEvent rEvent)
	{
		if (canHandleInput())
		{
			handleNavigationKey(rEvent);
		}
	}

	/***************************************
	 * @see RequiresResize#onResize()
	 */
	@Override
	public void onResize()
	{
		setHeightLocked(false);
		setRowUnselected(nSelectedRow);
		collapseAllNodes();
		deferredUpdate(false);
	}

	/***************************************
	 * Response handling for remote model invocations.
	 *
	 * @see Callback#onSuccess(Object)
	 */
	@Override
	public void onSuccess(RemoteDataModel<DataModel<?>> rRemoteModel)
	{
		hideBusyIndicator();

		boolean bSuccessfulUpdate = updateDisplay();

		bUpdateInProgress = false;

		if (!bSuccessfulUpdate)
		{
			update();
		}
	}

	/***************************************
	 * Performs a display update after changes to the table data. Depending on
	 * the type of the data model the model may first need to be updated
	 * asynchronously.
	 */
	public void repaint()
	{
		if (nTableRows == -1)
		{
			update();
		}
	}

	/***************************************
	 * @see Focusable#setAccessKey(char)
	 */
	@Override
	public void setAccessKey(char cKey)
	{
		aFocusPanel.setAccessKey(cKey);
	}

	/***************************************
	 * Sets the data model that contains information about the table columns.
	 *
	 * @param rNewColumns The table columns data model
	 */
	public void setColumns(DataModel<ColumnDefinition> rNewColumns)
	{
		// always keep TRUE column change state in case of multiple invocations
		bColumnsChanged = aHeader.setColumns(rNewColumns) || bColumnsChanged;
	}

	/***************************************
	 * Sets the user interface context.
	 *
	 * @param rContext The new context
	 */
	public final void setContext(UserInterfaceContext rContext)
	{
		this.rContext = rContext;
	}

	/***************************************
	 * @see Table#setData(DataModel)
	 */
	public void setData(DataModel<? extends DataModel<?>> rData)
	{
		this.rData = rData;

		if (aToolBar == null)
		{
			// the toolbar depends on model features, so it can only be created
			// after the model is available
			aToolBar = new TableToolBar(this);
			aMainPanel.setWidget(TOOLBAR_ROW, 0, aToolBar);
			aMainPanel.getCellFormatter()
					  .setVerticalAlignment(TOOLBAR_ROW,
											0,
											HasVerticalAlignment.ALIGN_BOTTOM);
		}

		update();
	}

	/***************************************
	 * Sets the enabled state.
	 *
	 * @param bEnabled The new enabled state
	 */
	public final void setEnabled(boolean bEnabled)
	{
		this.bEnabled = bEnabled;

		if (bEnabled)
		{
			removeStyleDependentName("disabled");
		}
		else
		{
			addStyleDependentName("disabled");
		}

		if (aToolBar != null)
		{
			aToolBar.setEnabled(bEnabled);
		}
	}

	/***************************************
	 * Sets the event dispatcher to be used to notify event listeners.
	 *
	 * @param rEventDispatcher The event dispatcher
	 */
	public void setEventDispatcher(GewtEventDispatcher rEventDispatcher)
	{
		this.rEventDispatcher = rEventDispatcher;
	}

	/***************************************
	 * @see Focusable#setFocus(boolean)
	 */
	@Override
	public void setFocus(boolean bFocused)
	{
		if (aToolBar != null && aToolBar.getFilterPanel() != null)
		{
			aToolBar.getFilterPanel().setFocus(true);
		}
		else
		{
			aFocusPanel.setFocus(bFocused);
		}
	}

	/***************************************
	 * @see SingleSelection#setSelection(int)
	 */
	@Override
	public void setSelection(int nRow)
	{
		setSelection(nRow, true);
	}

	/***************************************
	 * Sets the selection of this table
	 *
	 * @param nRow        The selected row or -1 for no selection
	 * @param bFireEvents TRUE to fire a selection event
	 */
	public void setSelection(int nRow, boolean bFireEvents)
	{
		if (nRow == -1 && nSelectedRow != -1 ||
			nRow != -1 && nSelectedRow == -1 ||
			nRow != nFirstRow + nSelectedRow)
		{
			setRowUnselected(nSelectedRow);
			rCurrentSelection = null;

			if (nRow >= 0)
			{
				if (!isBusy())
				{
					int nNewSelectedRow = nRow - nFirstRow;

					if (nNewSelectedRow >= 0 &&
						nNewSelectedRow < nVisibleDataRows)
					{
						setRowSelected(nNewSelectedRow, bFireEvents);
						rCurrentSelection = getSelection();
					}
					else
					{
						nFirstRow    = nRow;
						nSelectedRow = nRow - nFirstRow;

						update();
					}
				}
				else
				{
					nNewSelection = nRow;
				}
			}
			else
			{
				setRowSelected(-1, bFireEvents);
			}

			aToolBar.setClearSelectionButtonEnabled(nSelectedRow != -1);
		}
	}

	/***************************************
	 * @see Focusable#setTabIndex(int)
	 */
	@Override
	public void setTabIndex(int nIndex)
	{
		aFocusPanel.setTabIndex(nIndex);
	}

	/***************************************
	 * Sets the visible row count or -1 to calculate the number of rows that fit
	 * into the table height.
	 *
	 * @param nCount The new visible row count
	 */
	public final void setVisibleRowCount(int nCount)
	{
		if (nCount != nTableRows)
		{
			collapseAllNodes();

			int nRows	    = aDataTable.getRowCount();
			int nLastRow    = Math.min(nTableRows, nRows) - 1;
			int nNewLastRow = nCount - 1;

			for (int nRow = nLastRow; nRow > nNewLastRow; nRow--)
			{
				aDataTable.removeRow(nRow);
			}

			nTableRows = nVisibleDataRows = nCount;

			update();
		}
	}

	/***************************************
	 * {@inheritDoc}
	 */
	@Override
	public String toString()
	{
		return "GwtTable[" + getStyleName() + "]";
	}

	/***************************************
	 * Performs a display update after changes to the table data. Depending on
	 * the type of the data model the model may first need to be updated
	 * asynchronously.
	 */
	public void update()
	{
		if (rData != null && !bUpdateInProgress)
		{
			setRowUnselected(nSelectedRow);
			collapseAllNodes();

			// invoke update later to wait for the table to resize
			Scheduler.get()
					 .scheduleDeferred(new ScheduledCommand()
				{
					@Override
					public void execute()
					{
						deferredUpdate(true);
					}
				});
		}
	}

	/***************************************
	 * Adds the rows for children of an expanded node.
	 *
	 * @param rParentNode  The parent node to add the child nodes to
	 * @param rChildModels The data model containing the child data models
	 */
	void addChildRows(
		TreeNode						  rParentNode,
		DataModel<? extends DataModel<?>> rChildModels)
	{
		TreeNode rPrevNode  = null;
		int		 nNodeIndex = rParentNode.getAbsoluteIndex();
		int		 nRow	    = nNodeIndex + 1;
		int		 nLastRow   = nTableRows - 1;

		for (DataModel<?> rChild : rChildModels)
		{
			if (nVisibleDataRows++ < nTableRows)
			{
				// remove empty rows at the end until minimum row count
				aDataTable.removeRow(nLastRow);
			}

			aDataTable.insertRow(nRow);
			rPrevNode = initDataRow(rParentNode, rPrevNode, nRow);
			fillRow(rChild, nRow++);
		}

		rParentNode.setExpanded(true);
		updateRowStyles(nRow);

		if (nSelectedRow > nNodeIndex)
		{
			setRowSelected(nSelectedRow + rParentNode.getDirectChildren(),
						   true);
		}
	}

	/***************************************
	 * A helper method to check whether the table is currently ready to handle
	 * input events.
	 *
	 * @return TRUE if the table currently can handle input
	 */
	boolean canHandleInput()
	{
		return bEnabled && !isBusy();
	}

	/***************************************
	 * Collapses all nodes in a hierarchical table.
	 */
	void collapseAllNodes()
	{
		if (bHierarchical)
		{
			for (int nRow = aDataTable.getRowCount() - 1; nRow >= 0; nRow--)
			{
				TreeNode rNode = (TreeNode) aDataTable.getWidget(nRow, 0);

				if (rNode != null)
				{
					collapseNode(rNode);
				}
			}
		}
	}

	/***************************************
	 * Collapses a certain node in a hierarchical table.
	 *
	 * @param rNode The tree cell to collapse
	 */
	void collapseNode(TreeNode rNode)
	{
		if (rNode.isExpanded())
		{
			int nNodeIndex = rNode.getAbsoluteIndex();
			int nRow	   = nNodeIndex + 1;
			int nLastRow   = nTableRows - 1;
			int nChildren  = rNode.getVisibleChildren();

			if (nSelectedRow > nNodeIndex)
			{
				setRowUnselected(nSelectedRow);
			}

			for (int i = 0; i < nChildren; i++)
			{
				aDataTable.removeRow(nRow);

				if (--nVisibleDataRows < nTableRows)
				{
					clearRow(nLastRow);
				}
			}

			rNode.setExpanded(false);
			updateRowStyles(nRow);
		}
	}

	/***************************************
	 * Executes a display update when invoked from a scheduled command. Invoked
	 * indirectly by {@link #update()}.
	 *
	 * @param bNewData TRUE to indicate that new data needs to be retrieved from
	 *                 the data model
	 */
	void deferredUpdate(boolean bNewData)
	{
		if (!bUpdateInProgress && !isBusy())
		{
			boolean bWaitForRemoteData = false;

			bUpdateInProgress = true;

			try
			{
				if (bColumnsChanged)
				{
					resetColumns();
				}

				calcTableSize();

				if (nTableRows > 0)
				{
					checkBounds();
					initDataRows();

					if (rData instanceof RemoteDataModel)
					{
						@SuppressWarnings("unchecked")
						RemoteDataModel<DataModel<?>> rRemoteModel =
							(RemoteDataModel<DataModel<?>>) rData;

						getRemoteData(rRemoteModel,
									  nFirstRow,
									  nTableRows,
									  this);
						bWaitForRemoteData = true;
					}
					else
					{
						updateDisplay();
					}
				}
			}
			finally
			{
				if (!bWaitForRemoteData)
				{
					bUpdateInProgress = false;
				}
			}
		}
	}

	/***************************************
	 * Expands all nodes in a hierarchical table.
	 */
	void expandAllNodes()
	{
		if (bHierarchical)
		{
			for (int nRow = nVisibleDataRows - 1; nRow >= 0; nRow--)
			{
				expandNode((TreeNode) aDataTable.getWidget(nRow, 0));
			}
		}
	}

	/***************************************
	 * Expands a certain node in a hierarchical table.
	 *
	 * @param rNode The tree cell to expand
	 */
	void expandNode(final TreeNode rNode)
	{
		if (!rNode.isExpanded() && rNode.getDirectChildren() > 0)
		{
			if (nSelectedRow > rNode.getAbsoluteIndex())
			{
				setRowUnselected(nSelectedRow);
			}

			final DataModel<? extends DataModel<?>> rChildModels =
				((HierarchicalDataModel<?>) rNode.getRowModel())
				.getChildModels();

			if (rChildModels != null)
			{
				if (rChildModels instanceof RemoteDataModel)
				{
					@SuppressWarnings("unchecked")
					RemoteDataModel<DataModel<?>> rRemoteChildModel =
						(RemoteDataModel<DataModel<?>>) rChildModels;

					int nChildren = rRemoteChildModel.getElementCount();

					getRemoteData(rRemoteChildModel, 0, nChildren, rNode);
				}
				else
				{
					addChildRows(rNode, rChildModels);
				}
			}
		}
	}

	/***************************************
	 * Package-internal method that returns the data table of this instance.
	 *
	 * @return The data table
	 */
	final FlexTable getDataTable()
	{
		return aDataTable;
	}

	/***************************************
	 * Returns the width in pixels of the data area of this table.
	 *
	 * @return The data width
	 */
	final int getDataWidth()
	{
		return nDataWidth;
	}

	/***************************************
	 * Returns the index of the first row of the data model that is currently
	 * displayed.
	 *
	 * @return The first row value
	 */
	final int getFirstRow()
	{
		return nFirstRow;
	}

	/***************************************
	 * Package-internal method to query the focus panel of this instance.
	 *
	 * @return The focus panel
	 */
	final FocusPanel getFocusPanel()
	{
		return aFocusPanel;
	}

	/***************************************
	 * Package-internal method to query the scroll panel of this instance.
	 *
	 * @return The scroll panel
	 */
	final ScrollPanel getScrollPanel()
	{
		return aScrollPanel;
	}

	/***************************************
	 * Package-internal method to return the currently selected row relative to
	 * the visible table (not to the data model).
	 *
	 * @return The selected row or -1 for no selection
	 */
	int getSelectedRow()
	{
		return nSelectedRow;
	}

	/***************************************
	 * Package-internal method to check whether this instance supports
	 * filtering.
	 *
	 * @return TRUE if filters are available
	 */
	final boolean hasFilters()
	{
		return aToolBar != null && aToolBar.getFilterPanel() != null;
	}

	/***************************************
	 * Hides the indicator for long-time operations.
	 */
	void hideBusyIndicator()
	{
		nBusyIndicatorCount--;

		if (nBusyIndicatorCount == 0)
		{
			if (aInfoTimer != null)
			{
				aInfoTimer.cancel();
				aInfoTimer = null;
			}

			hideInfo();
		}
	}

	/***************************************
	 * Initiates the download of the table data for the current filter criteria.
	 */
	void initiateDownload()
	{
		if (rData instanceof Downloadable)
		{
			final Downloadable rRemoteModel = (Downloadable) rData;

			Scheduler.get()
					 .scheduleDeferred(new ScheduledCommand()
				{
					@Override
					public void execute()
					{
						showBusyIndicator();
						rRemoteModel.prepareDownload("tabledata.xls",
													 rData.getElementCount(),
							new Callback<String>()
							{
								@Override
								public void onError(Throwable eError)
								{
									GwtTable.this.onError(eError);
								}

								@Override
								public void onSuccess(String sDownloadUrl)
								{
									hideBusyIndicator();
									EWT.openHiddenUrl(sDownloadUrl);
								}
							});
					}
				});
		}
	}

	/***************************************
	 * Sets the index of the first row of the data model to be displayed.
	 *
	 * @param nNewFirst The new first row
	 */
	final void setFirstRow(int nNewFirst)
	{
		nFirstRow = nNewFirst;
	}

	/***************************************
	 * Locks or unlock the height of a hierarchical table. A locked table will
	 * not change it's height if nodes are expanded but will display scrollbars
	 * instead.
	 *
	 * @param bLocked TRUE to lock the table size, FALSE to unlock
	 */
	void setHeightLocked(boolean bLocked)
	{
		aScrollPanel.setHeight(bLocked ? getDataTableHeight() + "px" : "100%");
	}

	/***************************************
	 * Sets the style of a row to be selected.
	 *
	 * @param nRow       The row to set to be selected
	 * @param bFireEvent TRUE to dispatch a selection event
	 */
	void setRowSelected(int nRow, boolean bFireEvent)
	{
		if (nRow >= -1 && nRow < nVisibleDataRows)
		{
			if (nRow != -1)
			{
				RowFormatter rRowFormatter = aDataTable.getRowFormatter();

				rRowFormatter.addStyleName(nRow, CSS.ewtSelected());
			}

			if (nSelectedRow != nRow)
			{
				nSelectedRow = nRow;

				if (bFireEvent)
				{
					rEventDispatcher.dispatchEvent(EventType.SELECTION, null);
				}
			}
		}
	}

	/***************************************
	 * Sets the style of a row to be unselected.
	 *
	 * @param nRow The row to set to be unselected
	 */
	void setRowUnselected(int nRow)
	{
		if (nRow >= 0 && nRow < nVisibleDataRows)
		{
			RowFormatter rRowFormatter = aDataTable.getRowFormatter();

			rRowFormatter.removeStyleName(nRow, CSS.ewtSelected());
		}
	}

	/***************************************
	 * Package-internal method to set the currently selected row relative to the
	 * visible table (not to the data model).
	 *
	 * @param nRow The selected row or -1 for no selection
	 */
	void setSelectedRow(int nRow)
	{
		nSelectedRow = nRow;
	}

	/***************************************
	 * Shows a busy indicator for ongoing operations if necessary.
	 */
	void showBusyIndicator()
	{
		nBusyIndicatorCount++;

		if (aInfoTimer == null && getOffsetWidth() > 0)
		{
			aInfoTimer =
				new Timer()
				{
					@Override
					public void run()
					{
						if (nBusyIndicatorCount > 0)
						{
							showInfo(new Image(RES.imBusy()), false);
						}
					}
				};
			aInfoTimer.schedule(INFO_TIMER_MILLISECONDS);
		}
	}

	/***************************************
	 * Calculates the sizes of the table elements.
	 */
	private void calcTableSize()
	{
		if (Window.Navigator.getUserAgent().toLowerCase().contains("msie"))
		{
			int nHeight = aMainPanel.getOffsetHeight();

			if (nHeight == 0)
			{
				nHeight =
					getParent().getAbsoluteTop() +
					getParent().getOffsetHeight() -
					aMainPanel.getAbsoluteTop() - aHeader.getOffsetHeight() -
					aToolBar.getOffsetHeight() * 2;
			}

			if (nHeight > 0)
			{
				aMainPanel.setHeight(nHeight + "px");
			}
		}

		int nWidth  = aScrollPanel.getElement().getClientWidth();
		int nHeight = getDataTableHeight();

		if (nWidth > 0 &&
			nHeight > 0 &&
			(nDataWidth != nWidth || nDataHeight != nHeight))
		{
			nDataHeight = nHeight;
			nDataWidth  = nWidth;

			aHeader.calcColumnWidths();
			setHeightLocked(aToolBar.isHeightLocked());

			initDataRow(null, null, 0);

			int nRowHeight =
				aDataTable.getRowFormatter().getElement(0).getOffsetHeight();

			aDataTable.removeRow(0);

			if (nRowHeight > 0)
			{
				nTableRows		 = nDataHeight / nRowHeight;
				nVisibleDataRows = nTableRows;
			}
		}
	}

	/***************************************
	 * Changes the state of a tree node.
	 *
	 * @param bCollapse TRUE to collapse the node, FALSE to expand
	 */
	private void changeNodeState(boolean bCollapse)
	{
		if (bHierarchical && nSelectedRow >= 0)
		{
			int		 nRow  = nSelectedRow;
			TreeNode rNode = (TreeNode) aDataTable.getWidget(nRow, 0);

			if (bCollapse)
			{
				if (!rNode.isExpanded() && rNode.getParent() != null)
				{
					rNode = rNode.getParentNode();
				}

				collapseNode(rNode);
			}
			else
			{
				expandNode(rNode);
			}
		}
	}

	/***************************************
	 * Checks the table window bounds and adjusts the visible row parameters if
	 * necessary.
	 */
	private void checkBounds()
	{
		int nRows	   = rData.getElementCount();
		int nPrevFirst = nFirstRow;

		if (nRows > 0)
		{
			if (nFirstRow >= nRows)
			{
				nFirstRow = nRows - 1;
			}

			if (nFirstRow < 0)
			{
				nFirstRow = 0;
			}
		}

		if (nTableRows > 0)
		{
			nFirstRow = nFirstRow / nTableRows * nTableRows;

			if (nSelectedRow >= 0)
			{
				nSelectedRow += nPrevFirst - nFirstRow;

				if (nSelectedRow < 0 || nSelectedRow >= nVisibleDataRows)
				{
					nSelectedRow = -1;
				}
			}
		}
	}

	/***************************************
	 * Clears a certain row. If the row doesn't exist it will be added to the
	 * table.
	 *
	 * @param nRow The table row to clear
	 */
	private void clearRow(int nRow)
	{
		CellFormatter rCellFormatter = aDataTable.getCellFormatter();
		int			  nColumns		 = aHeader.getColumnCount();

		for (int nColumn = 0; nColumn < nColumns; nColumn++)
		{
			aDataTable.setHTML(nRow, nColumn, "&nbsp;");
			rCellFormatter.removeStyleName(nRow,
										   nColumn,
										   aHeader.getColumnStyle(nColumn));
		}

		setEmptyRowStyle(nRow);
	}

	/***************************************
	 * Displays a dialog that allows to copy the complete text of a row
	 * (currently unused).
	 */
	@SuppressWarnings("unused")
	private void copyRowText()
	{
		StringBuilder aRowText = new StringBuilder();
		int			  nLastCol = aHeader.getColumnCount() - 1;

		for (int nCol = 0; nCol <= nLastCol; nCol++)
		{
			aRowText.append(aDataTable.getText(nSelectedRow, nCol));

			if (nCol < nLastCol)
			{
				aRowText.append(',');
			}
		}

		Window.prompt("Kopieren", aRowText.toString());
	}

	/***************************************
	 * Fills a certain table row with the values from a row data model.
	 *
	 * @param rRow The row data model
	 * @param nRow The row index
	 */
	private void fillRow(DataModel<?> rRow, int nRow)
	{
		RowFormatter  rRowFormatter  = aDataTable.getRowFormatter();
		CellFormatter rCellFormatter = aDataTable.getCellFormatter();
		int			  nColumns		 = aHeader.getColumnCount();

		for (int nCol = 0; nCol < nColumns; nCol++)
		{
			if (bHierarchical && nCol == 0)
			{
				TreeNode rNode = (TreeNode) aDataTable.getWidget(nRow, nCol);

				if (rNode != null)
				{
					rNode.update(rRow, getCellValue(rRow, nCol));
				}
			}
			else
			{
				ColumnDefinition rColumn    = aHeader.getColumnDefinition(nCol);
				String			 sCellStyle = aHeader.getColumnStyle(nCol);

				if (rColumn.hasFlag(HAS_IMAGES))
				{
					setCellImage(nRow, nCol, rRow.getElement(nCol));
					rCellFormatter.setHorizontalAlignment(nRow,
														  nCol,
														  HasHorizontalAlignment.ALIGN_CENTER);
				}
				else
				{
					aDataTable.setText(nRow, nCol, getCellValue(rRow, nCol));
				}

				rCellFormatter.setStyleName(nRow, nCol, sCellStyle);
			}
		}

		rRowFormatter.setStyleName(nRow, CSS.ewtTableRow());

		if ((nRow & 0x1) != 0)
		{
			rRowFormatter.addStyleName(nRow, CSS.ewtOdd());
		}

		if (rRow instanceof Flags<?>)
		{
			Collection<?> rFlags = ((Flags<?>) rRow).getFlags();

			if (rFlags.size() > 0)
			{
				for (Object rFlag : rFlags)
				{
					rRowFormatter.addStyleName(nRow, rFlag.toString());
				}
			}
		}
	}

	/***************************************
	 * Fills all visible rows with data.
	 *
	 * @return The index of the row after the last filled row
	 */
	private int fillRows()
	{
		int nRow = 0;

		while (nRow < nVisibleDataRows)
		{
			DataModel<?> rRow = rData.getElement(nFirstRow + nRow);

			if (rRow.equals(rCurrentSelection))
			{
				nSelectedRow = nRow;
			}

			fillRow(rRow, nRow++);
		}

		return nRow;
	}

	/***************************************
	 * Returns the cell value for a certain column in a row data model.
	 *
	 * @param  rRow    The row data model to read the value from
	 * @param  nColumn The column to return the value for
	 *
	 * @return The cell value string
	 */
	private String getCellValue(DataModel<?> rRow, int nColumn)
	{
		Object rCellValue = rRow.getElement(nColumn);
		String sResult    = null;

		if (rCellValue != null)
		{
			ValueFormat rFormat  = aHeader.getColumnFormat(nColumn);
			String	    sValue   = rFormat.format(rCellValue).trim();
			int		    nLineEnd = sValue.indexOf('\n');

			if (nLineEnd >= 0)
			{
				sValue = sValue.substring(0, nLineEnd);
			}

			sResult = rContext.expandResource(sValue);
		}

		return sResult;
	}

	/***************************************
	 * This is a workaround for a bug in (at least) IE8. IE8 is unable to
	 * calculate the correct offsetHeight of some DOM elements under some
	 * circumstances (usually DIV elements). To calculate the table height
	 * aScrollbar.getOffsetHeight was used. That did not return the proper value
	 * in IE8 browsers. This workaround should work as long as the overall
	 * layout of the table doesn't change.
	 *
	 * @return the height of the table.
	 */
	private int getDataTableHeight()
	{
		return aMainPanel.getOffsetHeight() - aHeader.getOffsetHeight() -
			   aToolBar.getOffsetHeight();
	}

	/***************************************
	 * Sends a data request to a remote data model.
	 *
	 * @param rRemoteModel The remote data model
	 * @param nStartRow    The first row to request
	 * @param nRows        nStartRow The number of rows to request
	 * @param rCallback    The callback to invoke after completion
	 */
	private void getRemoteData(
		final RemoteDataModel<DataModel<?>>			  rRemoteModel,
		final int									  nStartRow,
		final int									  nRows,
		final Callback<RemoteDataModel<DataModel<?>>> rCallback)
	{
		showBusyIndicator();

		Scheduler.get()
				 .scheduleDeferred(new ScheduledCommand()
			{
				@Override
				public void execute()
				{
					rRemoteModel.setWindowSize(nRows);
					rRemoteModel.setWindow(nStartRow, rCallback);
				}
			});
	}

	/***************************************
	 * Increments the visible rows by one page.
	 *
	 * @return The selected row after the paging
	 */
	private int goPageDown()
	{
		int nNewSelection = nFirstRow + nSelectedRow;

		if (nSelectedRow == -1)
		{
			nFirstRow += nTableRows;
			update();
		}
		else if (nSelectedRow < nVisibleDataRows - 1)
		{
			nNewSelection = nFirstRow + nVisibleDataRows - 1;
		}
		else if (nFirstRow + nTableRows < rData.getElementCount())
		{
			nNewSelection = nFirstRow + nVisibleDataRows - 1 + nTableRows;
		}

		return nNewSelection;
	}

	/***************************************
	 * Decrements the visible rows by one page.
	 *
	 * @return The selected row after the paging
	 */
	private int goPageUp()
	{
		int nNewSelection = nFirstRow + nSelectedRow;

		if (nSelectedRow == -1)
		{
			nFirstRow -= nTableRows;
			update();
		}
		else if (nSelectedRow > 0)
		{
			nNewSelection = nFirstRow;
		}
		else if (nFirstRow > 0)
		{
			nNewSelection = Math.max(nFirstRow - nTableRows, 0);
		}

		return nNewSelection;
	}

	/***************************************
	 * Scrolls the visible rows down by one row.
	 *
	 * @return The new selected row
	 */
	private int goRowDown()
	{
		int nNewSelection = nSelectedRow;

		if (nSelectedRow == -1)
		{
			nNewSelection = nFirstRow;
		}
		else if (nSelectedRow < nVisibleDataRows - 1 ||
				 nFirstRow + nTableRows < rData.getElementCount())
		{
			nNewSelection = nFirstRow + nSelectedRow + 1;
		}

		return nNewSelection;
	}

	/***************************************
	 * Scrolls the visible rows up by one row.
	 *
	 * @return The new selected row
	 */
	private int goRowUp()
	{
		int nNewSelection = nFirstRow + nSelectedRow;

		if (nSelectedRow == -1)
		{
			nNewSelection = nFirstRow + nVisibleDataRows - 1;
		}
		else if (nSelectedRow > 0)
		{
			nNewSelection--;
		}
		else if (nFirstRow > 0)
		{
			nNewSelection = nFirstRow - 1;
		}

		return nNewSelection;
	}

	/***************************************
	 * Sets the visible rows to the first row.
	 *
	 * @return The new selected row
	 */
	private int goToFirst()
	{
		if (nSelectedRow == -1 || nSelectedRow > 0)
		{
			return nFirstRow;
		}
		else
		{
			return 0;
		}
	}

	/***************************************
	 * Sets the visible rows to show the last row.
	 *
	 * @return The new selected row
	 */
	private int goToLast()
	{
		int nRows = rData.getElementCount();

		if (nSelectedRow < nVisibleDataRows - 1)
		{
			return nFirstRow + nVisibleDataRows - 1;
		}
		else
		{
			return nRows - 1;
		}
	}

	/***************************************
	 * Handles a navigation key in a key event.
	 *
	 * @param  rEvent The event that occurred
	 *
	 * @return TRUE if the event was caused by a navigation key and has
	 *         therefore been handled
	 */
	private boolean handleNavigationKey(KeyDownEvent rEvent)
	{
		int     nNewSelection  = nFirstRow + nSelectedRow;
		int     nKeyCode	   = rEvent.getNativeKeyCode();
		boolean bCollapse	   = rEvent.isLeftArrow();
		boolean bExpand		   = rEvent.isRightArrow();
		boolean bNavigationKey = true;

		if (bCollapse || bExpand)
		{
			if (rEvent.isControlKeyDown())
			{
				if (bExpand)
				{
					expandAllNodes();
				}
				else
				{
					collapseAllNodes();
				}
			}
			else
			{
				changeNodeState(bCollapse);
			}
		}
		else if (rEvent.isUpArrow())
		{
			nNewSelection = goRowUp();
		}
		else if (rEvent.isDownArrow())
		{
			nNewSelection = goRowDown();
		}
		else if (nKeyCode == KeyCodes.KEY_PAGEUP)
		{
			nNewSelection = goPageUp();
		}
		else if (nKeyCode == KeyCodes.KEY_PAGEDOWN)
		{
			nNewSelection = goPageDown();
		}
		else if (nKeyCode == KeyCodes.KEY_HOME)
		{
			nNewSelection = goToFirst();
		}
		else if (nKeyCode == KeyCodes.KEY_END)
		{
			nNewSelection = goToLast();
		}
		else
		{
			bNavigationKey = false;
		}

		setSelection(nNewSelection);

		return bNavigationKey;
	}

	/***************************************
	 * Hides an information widget that had previously been shown with the
	 * method {@link #showInfo(Widget, boolean)}.
	 */
	private void hideInfo()
	{
		if (aInfoPopupPanel != null)
		{
			aInfoPopupPanel.hide();
			aInfoPopupPanel = null;
		}
	}

	/***************************************
	 * Initializes a certain table row.
	 *
	 * @param  rParent   The parent tree cell for hierarchical tables or NULL
	 *                   for none
	 * @param  rPrevious The previous tree cell for hierarchical tables or NULL
	 *                   for none
	 * @param  nRow      The row to initialize
	 *
	 * @return The created tree cell for hierarchical tables or NULL for none
	 */
	private TreeNode initDataRow(TreeNode rParent, TreeNode rPrevious, int nRow)
	{
		int		 nColumns  = aHeader.getColumnCount();
		TreeNode aTreeCell = null;

		for (int nCol = 0; nCol < nColumns; nCol++)
		{
			if (bHierarchical && nCol == 0)
			{
				aTreeCell = new TreeNode(this, rParent, rPrevious);
				aDataTable.setWidget(nRow, nCol, aTreeCell);
			}
			else
			{
				aDataTable.setHTML(nRow, nCol, "&nbsp;");
			}
		}

		setEmptyRowStyle(nRow);

		return aTreeCell;
	}

	/***************************************
	 * Initializes the data rows of the table.
	 */
	private void initDataRows()
	{
		TreeNode rPrevNode = null;
		int		 nMax	   = nTableRows - 1;

		for (int nRow = 0; nRow <= nMax; nRow++)
		{
			rPrevNode = initDataRow(null, rPrevNode, nRow);
		}
	}

	/***************************************
	 * Initializes new visible rows.
	 *
	 * @param nPrevVisibleRows The previous visible row count
	 */
	private void initNewVisibleRows(int nPrevVisibleRows)
	{
		while (nPrevVisibleRows < nVisibleDataRows)
		{
			int		 nInitRow = nPrevVisibleRows;
			TreeNode rNode    = null;

			if (nPrevVisibleRows > 0)
			{
				rNode = (TreeNode) aDataTable.getWidget(nInitRow - 1, 0);
			}

			initDataRow(null, rNode, nInitRow);
			nPrevVisibleRows++;
		}
	}

	/***************************************
	 * Re-initializes the columns of this table.
	 */
	private void resetColumns()
	{
		TableFilterPanel rFilterPanel  = aToolBar.getFilterPanel();
		String			 sCommonFilter = searchCommonFilterCriterion();

		nDataWidth = nDataHeight = 0;

		aDataTable.removeAllRows();

		SearchableDataModel<?> rSearchableModel = null;

		if (rFilterPanel != null)
		{
			rSearchableModel = (SearchableDataModel<?>) rData;
			rFilterPanel.reset();
		}

		boolean bHasTextColumns = aHeader.initColumns(rFilterPanel);

		if (rFilterPanel != null)
		{
			rFilterPanel.init(rSearchableModel, sCommonFilter, bHasTextColumns);
		}

		bColumnsChanged = false;
	}

	/***************************************
	 * Analyzes the constraints of a {@link SearchableDataModel} to check
	 * whether they represent a common search term for all searchable columns.
	 *
	 * @return The general filter criterion or NULL for none
	 */
	private String searchCommonFilterCriterion()
	{
		String sFilter = null;

		if (rData instanceof SearchableDataModel)
		{
			SearchableDataModel<?> rSearchableModel =
				(SearchableDataModel<?>) rData;

			for (String sCriterion : rSearchableModel.getConstraints().values())
			{
				if (sCriterion.endsWith("*"))
				{
					sCriterion =
						sCriterion.substring(0, sCriterion.length() - 1);
				}

				if (sFilter == null)
				{
					sFilter = sCriterion;
				}
				else if (!sFilter.equals(sCriterion))
				{
					sFilter = null;

					break;
				}
			}

			if (sFilter != null)
			{
				// a common filter criterion must always begin with the OR
				// prefix and the comparison character
				if (sFilter.length() >= 2 &&
					sFilter.charAt(0) == CONSTRAINT_OR_PREFIX)
				{
					sFilter = sFilter.substring(2);
				}
				else
				{
					sFilter = null;
				}
			}
		}

		return sFilter;
	}

	/***************************************
	 * Sets a cell image from the raw cell value.
	 *
	 * @param nRow       The row of the cell
	 * @param nCol       The column of the cell
	 * @param rCellValue The raw cell value to create the image from
	 */
	private void setCellImage(int nRow, int nCol, Object rCellValue)
	{
		if (rCellValue != null)
		{
			String		  sCellValue = rCellValue.toString();
			StringBuilder aImageName = new StringBuilder(sCellValue);

			aImageName.insert(1, "im");

			String sImage = aImageName.toString();

			de.esoco.ewt.graphics.Image aCellImage =
				rContext.createImage(sImage);

			if (aCellImage instanceof ImageRef)
			{
				Image rImage = ((ImageRef) aCellImage).getGwtImage();

				rImage.setTitle(rContext.expandResource(sCellValue));
				aDataTable.setWidget(nRow, nCol, rImage);
			}
			else if (!sImage.endsWith("Null"))
			{
				GWT.log("No image for " + sImage);
			}
		}
	}

	/***************************************
	 * Sets the table styles of an empty row.
	 *
	 * @param nRow The index of the row to set the style of
	 */
	private void setEmptyRowStyle(int nRow)
	{
		RowFormatter rRowFormatter = aDataTable.getRowFormatter();

		rRowFormatter.setStyleName(nRow, CSS.ewtTableRow());
		rRowFormatter.addStyleName(nRow, CSS.ewtEmpty());
	}

	/***************************************
	 * Sets the selection to the row of a certain cell.
	 *
	 * @param rCell      The cell to select (NULL values will be ignored)
	 * @param bFireEvent TRUE to fire a selection event
	 */
	private void setSelection(Cell rCell, boolean bFireEvent)
	{
		if (rCell != null)
		{
			int nRow = rCell.getRowIndex();

			if (nRow >= 0 && nRow < nVisibleDataRows)
			{
				setSelection(nFirstRow + nRow, bFireEvent);
			}
		}
	}

	/***************************************
	 * Sets the argument widgets to an HTML size of 100%.
	 *
	 * @param rWidgets The widgets to set the size of
	 */
	private void setToMaximumSize(Widget... rWidgets)
	{
		for (Widget rWidget : rWidgets)
		{
			rWidget.setSize("100%", "100%");
		}
	}

	/***************************************
	 * Displays an information widget above the table.
	 *
	 * @param rInfoWidget The widget to display
	 * @param bGlassPanel TRUE to show the info on top of a glass panel
	 */
	private void showInfo(Widget rInfoWidget, boolean bGlassPanel)
	{
		if (getOffsetWidth() > 0)
		{
			aInfoPopupPanel = new DecoratedPopupPanel(true);
			aInfoPopupPanel.setWidget(rInfoWidget);
			aInfoPopupPanel.setGlassEnabled(bGlassPanel);
			aInfoPopupPanel.setPopupPositionAndShow(new PopupPanel.PositionCallback()
				{
					@Override
					public void setPosition(int nPopupWidth, int nPopupHeight)
					{
						int x =
							getAbsoluteLeft() +
							(getOffsetWidth() - nPopupWidth) / 2;
						int y =
							getAbsoluteTop() +
							(getOffsetHeight() - nPopupHeight) / 2;

						aInfoPopupPanel.setPopupPosition(x, y);
					}
				});
		}
	}

	/***************************************
	 * Internal method to update the display of the table data.
	 *
	 * @return TRUE if the update was successful, false if it is necessary to be
	 *         re-executed with updated parameters (only for remote data models)
	 */
	private boolean updateDisplay()
	{
		if (nDataHeight > 0)
		{
			checkBounds();

			int nPrevRows	    = nVisibleDataRows;
			int nCount		    = rData.getElementCount();
			int nRows		    = Math.min(nCount - nFirstRow, nTableRows);
			int nDataTableRows  = aDataTable.getRowCount();
			int nNewSelectedRow = nSelectedRow;

			nVisibleDataRows = Math.min(nTableRows, nCount - nFirstRow);

			if (rData instanceof RemoteDataModel)
			{
				RemoteDataModel<?> rRemoteModel		  =
					(RemoteDataModel<?>) rData;
				int				   nAvailableElements =
					rRemoteModel.getAvailableElementCount();

				nVisibleDataRows =
					Math.min(nVisibleDataRows, nAvailableElements);

				if (nAvailableElements == 0 &&
					rRemoteModel.getWindowStart() > 0)
				{
					return false;
				}
			}

			aToolBar.updatePosition(nCount, nRows, nFirstRow + 1);
			initNewVisibleRows(nPrevRows);
			aHeader.setAllColumnWidths();

			int nEmptyRow = fillRows();

			while (nEmptyRow < nTableRows)
			{
				clearRow(nEmptyRow++);
			}

			while (nDataTableRows > nTableRows)
			{
				aDataTable.removeRow(--nDataTableRows);
			}

			if (nNewSelectedRow >= nVisibleDataRows)
			{
				nNewSelectedRow = nVisibleDataRows - 1;
			}

			setRowSelected(nNewSelectedRow, false);
			aToolBar.updateNavigationButtons();

			if (nSelectedRow >= 0)
			{
				rCurrentSelection = getSelection();
			}

			if (nNewSelection >= 0)
			{
				Scheduler.get()
						 .scheduleDeferred(new ScheduledCommand()
					{
						@Override
						public void execute()
						{
							setSelection(nNewSelection);
							nNewSelection = -1;
						}
					});
			}
		}

		return true;
	}

	/***************************************
	 * Updates the odd/even row styles for all visible rows beginning with a
	 * certain starting row.
	 *
	 * @param nRow The starting row
	 */
	private void updateRowStyles(int nRow)
	{
		RowFormatter rRowFormatter = aDataTable.getRowFormatter();
		int			 nMax		   = nVisibleDataRows;
		boolean		 bOdd		   = (nRow % 2) == 1;

		while (nRow < nMax)
		{
			if (bOdd)
			{
				rRowFormatter.addStyleName(nRow, CSS.ewtOdd());
			}
			else
			{
				rRowFormatter.removeStyleName(nRow, CSS.ewtOdd());
			}

			bOdd = !bOdd;
			nRow++;
		}
	}
}
