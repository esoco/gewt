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
import de.esoco.ewt.EWT;
import de.esoco.ewt.UserInterfaceContext;
import de.esoco.ewt.component.Table;
import de.esoco.ewt.component.TableControl;
import de.esoco.ewt.component.TableControl.IsTableControlWidget;
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
import de.esoco.lib.model.FilterableDataModel;
import de.esoco.lib.model.HierarchicalDataModel;
import de.esoco.lib.model.RemoteDataModel;
import de.esoco.lib.property.Flags;
import de.esoco.lib.property.SingleSelection;

import java.util.Collection;

import static de.esoco.lib.property.StyleProperties.HAS_IMAGES;

/**
 * A GWT-implementation of a composite that can display tabular data. It also
 * supports the display of hierarchical data.
 */
public class GwtTable extends Composite
	implements IsTableControlWidget, HasAllFocusHandlers, HasAllKeyHandlers,
	HasClickHandlers, HasDoubleClickHandlers, ClickHandler, KeyDownHandler,
	RequiresResize, Callback<RemoteDataModel<DataModel<?>>> {

	static final GewtResources RES = GewtResources.INSTANCE;

	static final GewtCss CSS = RES.css();

	private static final int HEADER_ROW = 0;

	private static final int DATA_ROW = 1;

	private static final int TOOLBAR_ROW = 2;

	private static final int INFO_TIMER_MILLISECONDS = 500;

	private final boolean hierarchical;

	private final UserInterfaceContext context;

	private final Grid mainPanel = new Grid(3, 1);

	private final ScrollPanel scrollPanel = new CustomScrollPanel();

	private final FocusPanel focusPanel = new FocusPanel();

	private final TableHeader header = new TableHeader(this);

	private final FlexTable dataTable = new FlexTable();

	private boolean enabled;

	private GewtEventDispatcher eventDispatcher;

	private TableToolBar toolBar = null;

	private DataModel<? extends DataModel<?>> data;

	private DecoratedPopupPanel infoPopupPanel = null;

	private Timer infoTimer;

	private Timer doubleClickTimer;

	private int busyIndicatorCount = 0;

	private int firstRow = 0;

	private int visibleDataRows = 0;

	private int tableRows = -1;

	private boolean updateInProgress = false;

	private boolean columnsChanged = false;

	private int dataWidth = 0;

	private int dataHeight = 0;

	private DataModel<?> currentSelection;

	private int selectedRow = -1;

	private int newSelection = -1;

	/**
	 * Creates a new instance.
	 *
	 * @param context      The user interface context
	 * @param hierarchical If TRUE the table will display instances of
	 *                     {@link HierarchicalDataModel} as a tree
	 */
	public GwtTable(UserInterfaceContext context, boolean hierarchical) {
		this.context = context;
		this.hierarchical = hierarchical;

		dataTable.addClickHandler(this);
		dataTable.addStyleName(CSS.ewtDataTable());

		scrollPanel.setWidget(dataTable);
		scrollPanel.setAlwaysShowScrollBars(false);

		mainPanel.setWidget(HEADER_ROW, 0, header);
		mainPanel.setWidget(DATA_ROW, 0, scrollPanel);
		mainPanel.getCellFormatter().setWidth(DATA_ROW, 0, "100%");
		mainPanel.getCellFormatter().setHeight(DATA_ROW, 0, "100%");
		mainPanel.setCellSpacing(0);
		mainPanel.setCellPadding(0);

		focusPanel.setWidget(mainPanel);
		focusPanel.addKeyDownHandler(this);

		setToMaximumSize(dataTable, scrollPanel, mainPanel, focusPanel);

		// place the focus panel in a table-based panel to force 100% height
		// because FocusPanel is based on div
		DockPanel tablePanel = new DockPanel();

		tablePanel.add(focusPanel, DockPanel.CENTER);
		tablePanel.setCellWidth(focusPanel, "100%");
		tablePanel.setCellHeight(focusPanel, "100%");

		initWidget(tablePanel);
		setStylePrimaryName(CSS.ewtTable());
	}

	/**
	 * @see HasAllFocusHandlers#addBlurHandler(BlurHandler)
	 */
	@Override
	public HandlerRegistration addBlurHandler(BlurHandler handler) {
		return focusPanel.addBlurHandler(handler);
	}

	/**
	 * @see HasClickHandlers#addClickHandler(ClickHandler)
	 */
	@Override
	public HandlerRegistration addClickHandler(ClickHandler handler) {
		return focusPanel.addClickHandler(handler);
	}

	/**
	 * @see HasDoubleClickHandlers#addDoubleClickHandler(DoubleClickHandler)
	 */
	@Override
	public HandlerRegistration addDoubleClickHandler(
		DoubleClickHandler handler) {
		return dataTable.addDoubleClickHandler(handler);
	}

	/**
	 * @see HasAllFocusHandlers#addFocusHandler(FocusHandler)
	 */
	@Override
	public HandlerRegistration addFocusHandler(FocusHandler handler) {
		return focusPanel.addFocusHandler(handler);
	}

	/**
	 * @see HasAllKeyHandlers#addKeyDownHandler(KeyDownHandler)
	 */
	@Override
	public HandlerRegistration addKeyDownHandler(KeyDownHandler handler) {
		return focusPanel.addKeyDownHandler(handler);
	}

	/**
	 * @see HasAllKeyHandlers#addKeyPressHandler(KeyPressHandler)
	 */
	@Override
	public HandlerRegistration addKeyPressHandler(KeyPressHandler handler) {
		return focusPanel.addKeyPressHandler(handler);
	}

	/**
	 * @see HasAllKeyHandlers#addKeyUpHandler(KeyUpHandler)
	 */
	@Override
	public HandlerRegistration addKeyUpHandler(KeyUpHandler handler) {
		return focusPanel.addKeyUpHandler(handler);
	}

	/**
	 * @see Table#getColumns()
	 */
	@Override
	public final DataModel<ColumnDefinition> getColumns() {
		return header.columns;
	}

	/**
	 * Returns the user interface context of this table.
	 *
	 * @return The user interface context
	 */
	public final UserInterfaceContext getContext() {
		return context;
	}

	/**
	 * @see Table#getData()
	 */
	@Override
	public final DataModel<? extends DataModel<?>> getData() {
		return data;
	}

	/**
	 * @see TableControl#getSelection()
	 */
	@Override
	public DataModel<?> getSelection() {
		DataModel<?> selection = null;

		if (selectedRow >= 0 && data != null) {
			if (hierarchical) {
				int row = selectedRow;
				TreeNode node = (TreeNode) dataTable.getWidget(row, 0);

				selection = node.getRowModel();
			} else if (data.getElementCount() > 0) {
				int sel = getSelectionIndex();

				selection = data.getElement(sel);
			}
		}

		return selection;
	}

	/**
	 * @see SingleSelection#getSelectionIndex()
	 */
	@Override
	public final int getSelectionIndex() {
		return selectedRow >= 0 ? selectedRow + firstRow : -1;
	}

	/**
	 * @see Focusable#getTabIndex()
	 */
	@Override
	public final int getTabIndex() {
		return focusPanel.getTabIndex();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getTableTitle() {
		// not supported
		return "";
	}

	/**
	 * Returns the visible row count.
	 *
	 * @return The visible row count
	 */
	public final int getVisibleRowCount() {
		return tableRows;
	}

	/**
	 * Checks whether the table is currently busy with updating the table
	 * display and therefore displaying a busy indicator.
	 *
	 * @return TRUE if the table is currently busy
	 */
	public final boolean isBusy() {
		return busyIndicatorCount != 0;
	}

	/**
	 * Returns the enabled state of this table.
	 *
	 * @return The enabled state
	 */
	@Override
	public final boolean isEnabled() {
		return enabled;
	}

	/**
	 * Check whether this is a hierarchical table.
	 *
	 * @return TRUE for a hierarchical table
	 */
	public final boolean isHierarchical() {
		return hierarchical;
	}

	/**
	 * @see ClickHandler#onClick(ClickEvent)
	 */
	@Override
	public void onClick(final ClickEvent event) {
		if (canHandleInput()) {
			final Cell cell = dataTable.getCellForEvent(event);

			if (doubleClickTimer != null) {
				doubleClickTimer.cancel();
				doubleClickTimer = null;
				setSelection(cell, false);
				eventDispatcher.dispatchEvent(EventType.ACTION,
					event.getNativeEvent());
			} else {
				doubleClickTimer = new Timer() {
					@Override
					public void run() {
						doubleClickTimer = null;
						setSelection(cell, true);
					}
				};
				doubleClickTimer.schedule(EWT.getDoubleClickInterval());
			}
		}
	}

	/**
	 * Error handling for remote model invocations.
	 *
	 * @see Callback#onError(Throwable)
	 */
	@Override
	public void onError(Throwable e) {
		hideBusyIndicator();

		String message = context.expandResource("$msgTableModelError") + ": " +
			context.expandResource(e.getMessage());

		showInfo(new Label(message), true);
		updateInProgress = false;
	}

	/**
	 * Event handling for keyboard input in the focus panel.
	 *
	 * @see KeyDownHandler#onKeyDown(KeyDownEvent)
	 */
	@Override
	public void onKeyDown(KeyDownEvent event) {
		if (canHandleInput()) {
			handleNavigationKey(event);
		}
	}

	/**
	 * @see RequiresResize#onResize()
	 */
	@Override
	public void onResize() {
		if (mainPanel.getOffsetHeight() > 0) {
			setHeightLocked(false);

			setRowUnselected(selectedRow);

			if (newSelection == -1 && selectedRow >= 0) {
				newSelection = firstRow + selectedRow;
			}

			collapseAllNodes();
			deferredUpdate(false);
		}
	}

	/**
	 * Response handling for remote model invocations.
	 *
	 * @see Callback#onSuccess(Object)
	 */
	@Override
	public void onSuccess(RemoteDataModel<DataModel<?>> remoteModel) {
		hideBusyIndicator();

		boolean successfulUpdate = updateDisplay();

		updateInProgress = false;

		if (!successfulUpdate) {
			update();
		}
	}

	/**
	 * Performs a display update after changes to the table data. Depending on
	 * the type of the data model the model may first need to be updated
	 * asynchronously.
	 */
	@Override
	public void repaint() {
		if (tableRows == -1) {
			update();
		}
	}

	/**
	 * @see Focusable#setAccessKey(char)
	 */
	@Override
	public void setAccessKey(char key) {
		focusPanel.setAccessKey(key);
	}

	/**
	 * Sets the data model that contains information about the table columns.
	 *
	 * @param newColumns The table columns data model
	 */
	@Override
	public void setColumns(DataModel<ColumnDefinition> newColumns) {
		// always keep TRUE column change state in case of multiple invocations
		columnsChanged = header.setColumns(newColumns) || columnsChanged;
	}

	/**
	 * @see Table#setData(DataModel)
	 */
	@Override
	public void setData(DataModel<? extends DataModel<?>> newData) {
		if (newData != data) {
			data = newData;

			if (data instanceof RemoteDataModel) {
				firstRow = ((RemoteDataModel<?>) data).getWindowStart();
			} else {
				firstRow = 0;
			}

			if (toolBar == null) {
				// the toolbar depends on model features, so it can only be
				// created
				// after the model is available
				toolBar = new TableToolBar(this);
				mainPanel.setWidget(TOOLBAR_ROW, 0, toolBar);
				mainPanel
					.getCellFormatter()
					.setVerticalAlignment(TOOLBAR_ROW, 0,
						HasVerticalAlignment.ALIGN_BOTTOM);
			}
		}

		updateFilterPanel();
		update();
	}

	/**
	 * Sets the enabled state.
	 *
	 * @param enabled The new enabled state
	 */
	@Override
	public final void setEnabled(boolean enabled) {
		this.enabled = enabled;

		if (enabled) {
			removeStyleDependentName("disabled");
		} else {
			addStyleDependentName("disabled");
		}

		if (toolBar != null) {
			toolBar.setEnabled(enabled);
		}
	}

	/**
	 * Sets the event dispatcher to be used to notify event listeners.
	 *
	 * @param eventDispatcher The event dispatcher
	 */
	@Override
	public void setEventDispatcher(GewtEventDispatcher eventDispatcher) {
		this.eventDispatcher = eventDispatcher;
	}

	/**
	 * @see Focusable#setFocus(boolean)
	 */
	@Override
	public void setFocus(boolean focused) {
		if (toolBar != null && toolBar.getFilterPanel() != null) {
			toolBar.getFilterPanel().setFocus(true);
		} else {
			focusPanel.setFocus(focused);
		}
	}

	/**
	 * @see SingleSelection#setSelection(int)
	 */
	@Override
	public void setSelection(int row) {
		setSelection(row, true);
	}

	/**
	 * Sets the selection of this table
	 *
	 * @param row        The selected row or -1 for no selection
	 * @param fireEvents TRUE to fire a selection event
	 */
	@Override
	public void setSelection(int row, boolean fireEvents) {
		if (row == -1 && selectedRow != -1 || row != -1 && selectedRow == -1 ||
			row != firstRow + selectedRow) {
			setRowUnselected(selectedRow);
			currentSelection = null;

			if (row >= 0) {
				if (!isBusy()) {
					int newSelectedRow = row - firstRow;

					if (newSelectedRow >= 0 &&
						newSelectedRow < visibleDataRows) {
						setRowSelected(newSelectedRow, fireEvents);
						currentSelection = getSelection();
					} else {
						firstRow = row;
						selectedRow = row - firstRow;

						update();
					}
				} else {
					newSelection = row;
				}
			} else {
				setRowSelected(-1, fireEvents);
			}

			toolBar.setClearSelectionButtonEnabled(selectedRow != -1);
		}
	}

	/**
	 * @see Focusable#setTabIndex(int)
	 */
	@Override
	public void setTabIndex(int index) {
		focusPanel.setTabIndex(index);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setTableTitle(String tableTitle) {
		// not supported
	}

	/**
	 * Sets the visible row count or -1 to calculate the number of rows that
	 * fit
	 * into the table height.
	 *
	 * @param count The new visible row count
	 */
	@Override
	public final void setVisibleRowCount(int count) {
		if (count != tableRows) {
			collapseAllNodes();

			int rows = dataTable.getRowCount();
			int lastRow = Math.min(tableRows, rows) - 1;
			int newLastRow = count - 1;

			for (int row = lastRow; row > newLastRow; row--) {
				dataTable.removeRow(row);
			}

			tableRows = visibleDataRows = count;

			update();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "GwtTable[" + getStyleName() + "]";
	}

	/**
	 * Performs a display update after changes to the table data. Depending on
	 * the type of the data model the model may first need to be updated
	 * asynchronously.
	 */
	public void update() {
		if (data != null && !updateInProgress) {
			setRowUnselected(selectedRow);
			collapseAllNodes();

			// invoke update later to wait for the table to resize
			Scheduler.get().scheduleDeferred(new ScheduledCommand() {
				@Override
				public void execute() {
					deferredUpdate(true);
				}
			});
		}
	}

	/**
	 * Updates the filter panel from the data model.
	 */
	protected void updateFilterPanel() {
		TableFilterPanel filterPanel = toolBar.getFilterPanel();

		if (filterPanel != null) {
			filterPanel.update((FilterableDataModel<?>) data);
		}
	}

	/**
	 * Adds the rows for children of an expanded node.
	 *
	 * @param parentNode  The parent node to add the child nodes to
	 * @param childModels The data model containing the child data models
	 */
	void addChildRows(TreeNode parentNode,
		DataModel<? extends DataModel<?>> childModels) {
		TreeNode prevNode = null;
		int nodeIndex = parentNode.getAbsoluteIndex();
		int row = nodeIndex + 1;
		int lastRow = tableRows - 1;

		for (DataModel<?> child : childModels) {
			if (visibleDataRows++ < tableRows) {
				// remove empty rows at the end until minimum row count
				dataTable.removeRow(lastRow);
			}

			dataTable.insertRow(row);
			prevNode = initDataRow(parentNode, prevNode, row);
			fillRow(child, row++);
		}

		parentNode.setExpanded(true);
		updateRowStyles(row);

		if (selectedRow > nodeIndex) {
			setRowSelected(selectedRow + parentNode.getDirectChildren(), true);
		}
	}

	/**
	 * A helper method to check whether the table is currently ready to handle
	 * input events.
	 *
	 * @return TRUE if the table currently can handle input
	 */
	boolean canHandleInput() {
		return enabled && !isBusy();
	}

	/**
	 * Collapses all nodes in a hierarchical table.
	 */
	void collapseAllNodes() {
		if (hierarchical) {
			for (int row = dataTable.getRowCount() - 1; row >= 0; row--) {
				TreeNode node = (TreeNode) dataTable.getWidget(row, 0);

				if (node != null) {
					collapseNode(node);
				}
			}
		}
	}

	/**
	 * Collapses a certain node in a hierarchical table.
	 *
	 * @param node The tree cell to collapse
	 */
	void collapseNode(TreeNode node) {
		if (node.isExpanded()) {
			int nodeIndex = node.getAbsoluteIndex();
			int row = nodeIndex + 1;
			int lastRow = tableRows - 1;
			int children = node.getVisibleChildren();

			if (selectedRow > nodeIndex) {
				setRowUnselected(selectedRow);
			}

			for (int i = 0; i < children; i++) {
				dataTable.removeRow(row);

				if (--visibleDataRows < tableRows) {
					clearRow(lastRow);
				}
			}

			node.setExpanded(false);
			updateRowStyles(row);
		}
	}

	/**
	 * Executes a display update when invoked from a scheduled command. Invoked
	 * indirectly by {@link #update()}.
	 *
	 * @param newData TRUE to indicate that new data needs to be retrieved from
	 *                the data model
	 */
	void deferredUpdate(boolean newData) {
		if (!updateInProgress && !isBusy()) {
			boolean waitForRemoteData = false;

			updateInProgress = true;

			try {
				if (columnsChanged) {
					resetColumns();
				}

				calcTableSize();

				if (tableRows > 0) {
					checkBounds();
					initDataRows();

					if (data instanceof RemoteDataModel) {
						@SuppressWarnings("unchecked")
						RemoteDataModel<DataModel<?>> remoteModel =
							(RemoteDataModel<DataModel<?>>) data;

						getRemoteData(remoteModel, firstRow, tableRows, this);
						waitForRemoteData = true;
					} else {
						updateDisplay();
					}
				}
			} finally {
				if (!waitForRemoteData) {
					updateInProgress = false;
				}
			}
		}
	}

	/**
	 * Expands all nodes in a hierarchical table.
	 */
	void expandAllNodes() {
		if (hierarchical) {
			for (int row = visibleDataRows - 1; row >= 0; row--) {
				expandNode((TreeNode) dataTable.getWidget(row, 0));
			}
		}
	}

	/**
	 * Expands a certain node in a hierarchical table.
	 *
	 * @param node The tree cell to expand
	 */
	void expandNode(final TreeNode node) {
		if (!node.isExpanded() && node.getDirectChildren() > 0) {
			if (selectedRow > node.getAbsoluteIndex()) {
				setRowUnselected(selectedRow);
			}

			final DataModel<? extends DataModel<?>> childModels =
				((HierarchicalDataModel<?>) node.getRowModel()).getChildModels();

			if (childModels != null) {
				if (childModels instanceof RemoteDataModel) {
					@SuppressWarnings("unchecked")
					RemoteDataModel<DataModel<?>> remoteChildModel =
						(RemoteDataModel<DataModel<?>>) childModels;

					int children = remoteChildModel.getElementCount();

					getRemoteData(remoteChildModel, 0, children, node);
				} else {
					addChildRows(node, childModels);
				}
			}
		}
	}

	/**
	 * Package-internal method that returns the data table of this instance.
	 *
	 * @return The data table
	 */
	final FlexTable getDataTable() {
		return dataTable;
	}

	/**
	 * Returns the width in pixels of the data area of this table.
	 *
	 * @return The data width
	 */
	final int getDataWidth() {
		return dataWidth;
	}

	/**
	 * Returns the index of the first row of the data model that is currently
	 * displayed.
	 *
	 * @return The first row value
	 */
	final int getFirstRow() {
		return firstRow;
	}

	/**
	 * Package-internal method to query the focus panel of this instance.
	 *
	 * @return The focus panel
	 */
	final FocusPanel getFocusPanel() {
		return focusPanel;
	}

	/**
	 * Package-internal method to query the scroll panel of this instance.
	 *
	 * @return The scroll panel
	 */
	final ScrollPanel getScrollPanel() {
		return scrollPanel;
	}

	/**
	 * Package-internal method to return the currently selected row relative to
	 * the visible table (not to the data model).
	 *
	 * @return The selected row or -1 for no selection
	 */
	int getSelectedRow() {
		return selectedRow;
	}

	/**
	 * Package-internal method to check whether this instance supports
	 * filtering.
	 *
	 * @return TRUE if filters are available
	 */
	final boolean hasFilters() {
		return toolBar != null && toolBar.getFilterPanel() != null;
	}

	/**
	 * Hides the indicator for long-time operations.
	 */
	void hideBusyIndicator() {
		busyIndicatorCount--;

		if (busyIndicatorCount == 0) {
			if (infoTimer != null) {
				infoTimer.cancel();
				infoTimer = null;
			}

			hideInfo();
		}
	}

	/**
	 * Initiates the download of the table data for the current filter
	 * criteria.
	 */
	void initiateDownload() {
		if (data instanceof Downloadable) {
			final Downloadable remoteModel = (Downloadable) data;

			Scheduler.get().scheduleDeferred(new ScheduledCommand() {
				@Override
				public void execute() {
					showBusyIndicator();
					remoteModel.prepareDownload("tabledata.xls",
						data.getElementCount(), new Callback<String>() {
							@Override
							public void onError(Throwable error) {
								GwtTable.this.onError(error);
							}

							@Override
							public void onSuccess(String downloadUrl) {
								hideBusyIndicator();
								EWT.openHiddenUrl(downloadUrl);
							}
						});
				}
			});
		}
	}

	/**
	 * Sets the index of the first row of the data model to be displayed.
	 *
	 * @param newFirst The new first row
	 */
	final void setFirstRow(int newFirst) {
		firstRow = newFirst;
	}

	/**
	 * Locks or unlock the height of a hierarchical table. A locked table will
	 * not change it's height if nodes are expanded but will display scrollbars
	 * instead.
	 *
	 * @param locked TRUE to lock the table size, FALSE to unlock
	 */
	void setHeightLocked(boolean locked) {
		scrollPanel.setHeight(locked ? getDataTableHeight() + "px" : "100%");
	}

	/**
	 * Sets the style of a row to be selected.
	 *
	 * @param row       The row to set to be selected
	 * @param fireEvent TRUE to dispatch a selection event
	 */
	void setRowSelected(int row, boolean fireEvent) {
		if (row >= -1 && row < visibleDataRows) {
			if (row != -1) {
				RowFormatter rowFormatter = dataTable.getRowFormatter();

				rowFormatter.addStyleName(row, CSS.ewtSelected());
			}

			if (selectedRow != row) {
				selectedRow = row;

				if (fireEvent) {
					eventDispatcher.dispatchEvent(EventType.SELECTION, null);
				}
			}
		}
	}

	/**
	 * Sets the style of a row to be unselected.
	 *
	 * @param row The row to set to be unselected
	 */
	void setRowUnselected(int row) {
		if (row >= 0 && row < visibleDataRows) {
			RowFormatter rowFormatter = dataTable.getRowFormatter();

			rowFormatter.removeStyleName(row, CSS.ewtSelected());
		}
	}

	/**
	 * Package-internal method to set the currently selected row relative to
	 * the
	 * visible table (not to the data model).
	 *
	 * @param row The selected row or -1 for no selection
	 */
	void setSelectedRow(int row) {
		selectedRow = row;
	}

	/**
	 * Shows a busy indicator for ongoing operations if necessary.
	 */
	void showBusyIndicator() {
		busyIndicatorCount++;

		if (infoTimer == null && getOffsetWidth() > 0) {
			infoTimer = new Timer() {
				@Override
				public void run() {
					if (busyIndicatorCount > 0) {
						showInfo(new Image(RES.imBusy()), false);
					}
				}
			};
			infoTimer.schedule(INFO_TIMER_MILLISECONDS);
		}
	}

	/**
	 * Calculates the sizes of the table elements.
	 */
	private void calcTableSize() {
		if (Window.Navigator.getUserAgent().toLowerCase().contains("msie")) {
			int height = mainPanel.getOffsetHeight();

			if (height == 0) {
				height = getParent().getAbsoluteTop() +
					getParent().getOffsetHeight() - mainPanel.getAbsoluteTop() -
					header.getOffsetHeight() - toolBar.getOffsetHeight() * 2;
			}

			if (height > 0) {
				mainPanel.setHeight(height + "px");
			}
		}

		int width = scrollPanel.getElement().getClientWidth();
		int height = getDataTableHeight();

		if (width > 0 && height > 0 &&
			(dataWidth != width || dataHeight != height)) {
			dataHeight = height;
			dataWidth = width;

			header.calcColumnWidths();
			setHeightLocked(toolBar.isHeightLocked());

			initDataRow(null, null, 0);

			int rowHeight =
				dataTable.getRowFormatter().getElement(0).getOffsetHeight();

			dataTable.removeRow(0);

			if (rowHeight > 0) {
				tableRows = dataHeight / rowHeight;
				visibleDataRows = tableRows;
			}
		}
	}

	/**
	 * Changes the state of a tree node.
	 *
	 * @param collapse TRUE to collapse the node, FALSE to expand
	 */
	private void changeNodeState(boolean collapse) {
		if (hierarchical && selectedRow >= 0) {
			int row = selectedRow;
			TreeNode node = (TreeNode) dataTable.getWidget(row, 0);

			if (collapse) {
				if (!node.isExpanded() && node.getParent() != null) {
					node = node.getParentNode();
				}

				collapseNode(node);
			} else {
				expandNode(node);
			}
		}
	}

	/**
	 * Checks the table window bounds and adjusts the visible row parameters if
	 * necessary.
	 */
	private void checkBounds() {
		if (tableRows > 0) {
			int rows = data.getElementCount();
			int prevFirst = firstRow;

			if (rows > 0) {
				if (firstRow >= rows) {
					firstRow = rows - 1;
				}

				if (firstRow < 0) {
					firstRow = 0;
				}
			}

			if (selectedRow >= 0) {
				// set to selection to prevent page change on boundary rounding
				// that makes the selection invisible
				firstRow = firstRow + selectedRow;
			}

			firstRow = firstRow / tableRows * tableRows;

			if (selectedRow >= 0) {
				selectedRow += prevFirst - firstRow;

				if (selectedRow < 0 || selectedRow >= visibleDataRows) {
					selectedRow = -1;
				}
			}
		}
	}

	/**
	 * Clears a certain row. If the row doesn't exist it will be added to the
	 * table.
	 *
	 * @param row The table row to clear
	 */
	private void clearRow(int row) {
		CellFormatter cellFormatter = dataTable.getCellFormatter();
		int columns = header.getColumnCount();

		for (int column = 0; column < columns; column++) {
			dataTable.setHTML(row, column, "&nbsp;");
			cellFormatter.removeStyleName(row, column,
				header.getColumnStyle(column));
		}

		setEmptyRowStyle(row);
	}

	/**
	 * Displays a dialog that allows to copy the complete text of a row
	 * (currently unused).
	 */
	@SuppressWarnings("unused")
	private void copyRowText() {
		StringBuilder rowText = new StringBuilder();
		int lastCol = header.getColumnCount() - 1;

		for (int col = 0; col <= lastCol; col++) {
			rowText.append(dataTable.getText(selectedRow, col));

			if (col < lastCol) {
				rowText.append(',');
			}
		}

		Window.prompt("Kopieren", rowText.toString());
	}

	/**
	 * Fills a certain table row with the values from a row data model.
	 *
	 * @param row The row data model
	 * @param row The row index
	 */
	private void fillRow(DataModel<?> rowModel, int row) {
		RowFormatter rowFormatter = dataTable.getRowFormatter();
		CellFormatter cellFormatter = dataTable.getCellFormatter();
		int columns = header.getColumnCount();

		for (int col = 0; col < columns; col++) {
			if (hierarchical && col == 0) {
				TreeNode node = (TreeNode) dataTable.getWidget(row, col);

				if (node != null) {
					node.update(rowModel, getCellValue(rowModel, col));
				}
			} else {
				ColumnDefinition column = header.getColumnDefinition(col);
				String cellStyle = header.getColumnStyle(col);

				if (column.hasFlag(HAS_IMAGES)) {
					setCellImage(row, col, rowModel.getElement(col));
					cellFormatter.setHorizontalAlignment(row, col,
						HasHorizontalAlignment.ALIGN_CENTER);
				} else {
					dataTable.setText(row, col, getCellValue(rowModel, col));
				}

				cellFormatter.setStyleName(row, col, cellStyle);
			}
		}

		rowFormatter.setStyleName(row, CSS.ewtTableRow());

		if ((row & 0x1) != 0) {
			rowFormatter.addStyleName(row, CSS.ewtOdd());
		}

		if (rowModel instanceof Flags<?>) {
			Collection<?> flags = ((Flags<?>) rowModel).getFlags();

			if (!flags.isEmpty()) {
				for (Object flag : flags) {
					rowFormatter.addStyleName(row, flag.toString());
				}
			}
		}
	}

	/**
	 * Fills all visible rows with data.
	 *
	 * @return The index of the row after the last filled row
	 */
	private int fillRows() {
		int row = 0;

		while (row < visibleDataRows) {
			DataModel<?> rowModel = data.getElement(firstRow + row);

			if (rowModel.equals(currentSelection)) {
				selectedRow = row;
			}

			fillRow(rowModel, row++);
		}

		return row;
	}

	/**
	 * Returns the cell value for a certain column in a row data model.
	 *
	 * @param row    The row data model to read the value from
	 * @param column The column to return the value for
	 * @return The cell value string
	 */
	private String getCellValue(DataModel<?> row, int column) {
		Object cellValue = row.getElement(column);
		String result = null;

		if (cellValue != null) {
			ValueFormat format = header.getColumnFormat(column);
			String value = format.format(cellValue).trim();
			int lineEnd = value.indexOf('\n');

			if (lineEnd >= 0) {
				value = value.substring(0, lineEnd);
			}

			result = context.expandResource(value);
		}

		return result;
	}

	/**
	 * This is a workaround for a bug in (at least) IE8. IE8 is unable to
	 * calculate the correct offsetHeight of some DOM elements under some
	 * circumstances (usually DIV elements). To calculate the table height
	 * scrollbar.getOffsetHeight was used. That did not return the proper value
	 * in IE8 browsers. This workaround should work as long as the overall
	 * layout of the table doesn't change.
	 *
	 * @return the height of the table.
	 */
	private int getDataTableHeight() {
		return mainPanel.getOffsetHeight() - header.getOffsetHeight() -
			toolBar.getOffsetHeight();
	}

	/**
	 * Sends a data request to a remote data model.
	 *
	 * @param remoteModel The remote data model
	 * @param startRow    The first row to request
	 * @param rows        startRow The number of rows to request
	 * @param callback    The callback to invoke after completion
	 */
	private void getRemoteData(final RemoteDataModel<DataModel<?>> remoteModel,
		final int startRow, final int rows,
		final Callback<RemoteDataModel<DataModel<?>>> callback) {
		showBusyIndicator();

		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				remoteModel.setWindow(startRow, rows, callback);
			}
		});
	}

	/**
	 * Increments the visible rows by one page.
	 *
	 * @return The selected row after the paging
	 */
	private int goPageDown() {
		int newSelection = firstRow + selectedRow;

		if (selectedRow == -1) {
			firstRow += tableRows;
			update();
		} else if (selectedRow < visibleDataRows - 1) {
			newSelection = firstRow + visibleDataRows - 1;
		} else if (firstRow + tableRows < data.getElementCount()) {
			newSelection = firstRow + visibleDataRows - 1 + tableRows;
		}

		return newSelection;
	}

	/**
	 * Decrements the visible rows by one page.
	 *
	 * @return The selected row after the paging
	 */
	private int goPageUp() {
		int newSelection = firstRow + selectedRow;

		if (selectedRow == -1) {
			firstRow -= tableRows;
			update();
		} else if (selectedRow > 0) {
			newSelection = firstRow;
		} else if (firstRow > 0) {
			newSelection = Math.max(firstRow - tableRows, 0);
		}

		return newSelection;
	}

	/**
	 * Scrolls the visible rows down by one row.
	 *
	 * @return The new selected row
	 */
	private int goRowDown() {
		int newSelection = selectedRow;

		if (selectedRow == -1) {
			newSelection = firstRow;
		} else if (selectedRow < visibleDataRows - 1 ||
			firstRow + tableRows < data.getElementCount()) {
			newSelection = firstRow + selectedRow + 1;
		}

		return newSelection;
	}

	/**
	 * Scrolls the visible rows up by one row.
	 *
	 * @return The new selected row
	 */
	private int goRowUp() {
		int newSelection = firstRow + selectedRow;

		if (selectedRow == -1) {
			newSelection = firstRow + visibleDataRows - 1;
		} else if (selectedRow > 0) {
			newSelection--;
		} else if (firstRow > 0) {
			newSelection = firstRow - 1;
		}

		return newSelection;
	}

	/**
	 * Sets the visible rows to the first row.
	 *
	 * @return The new selected row
	 */
	private int goToFirst() {
		if (selectedRow == -1 || selectedRow > 0) {
			return firstRow;
		} else {
			return 0;
		}
	}

	/**
	 * Sets the visible rows to show the last row.
	 *
	 * @return The new selected row
	 */
	private int goToLast() {
		int rows = data.getElementCount();

		if (selectedRow < visibleDataRows - 1) {
			return firstRow + visibleDataRows - 1;
		} else {
			return rows - 1;
		}
	}

	/**
	 * Handles a navigation key in a key event.
	 *
	 * @param event The event that occurred
	 * @return TRUE if the event was caused by a navigation key and has
	 * therefore been handled
	 */
	private boolean handleNavigationKey(KeyDownEvent event) {
		int newSelection = firstRow + selectedRow;
		int keyCode = event.getNativeKeyCode();
		boolean collapse = event.isLeftArrow();
		boolean expand = event.isRightArrow();
		boolean navigationKey = true;

		if (collapse || expand) {
			if (event.isControlKeyDown()) {
				if (expand) {
					expandAllNodes();
				} else {
					collapseAllNodes();
				}
			} else {
				changeNodeState(collapse);
			}
		} else if (event.isUpArrow()) {
			newSelection = goRowUp();
		} else if (event.isDownArrow()) {
			newSelection = goRowDown();
		} else if (keyCode == KeyCodes.KEY_PAGEUP) {
			newSelection = goPageUp();
		} else if (keyCode == KeyCodes.KEY_PAGEDOWN) {
			newSelection = goPageDown();
		} else if (keyCode == KeyCodes.KEY_HOME) {
			newSelection = goToFirst();
		} else if (keyCode == KeyCodes.KEY_END) {
			newSelection = goToLast();
		} else {
			navigationKey = false;
		}

		setSelection(newSelection);

		return navigationKey;
	}

	/**
	 * Hides an information widget that had previously been shown with the
	 * method {@link #showInfo(Widget, boolean)}.
	 */
	private void hideInfo() {
		if (infoPopupPanel != null) {
			infoPopupPanel.hide();
			infoPopupPanel = null;
		}
	}

	/**
	 * Initializes a certain table row.
	 *
	 * @param parent   The parent tree cell for hierarchical tables or NULL for
	 *                 none
	 * @param previous The previous tree cell for hierarchical tables or NULL
	 *                 for none
	 * @param row      The row to initialize
	 * @return The created tree cell for hierarchical tables or NULL for none
	 */
	private TreeNode initDataRow(TreeNode parent, TreeNode previous, int row) {
		int columns = header.getColumnCount();
		TreeNode treeCell = null;

		for (int col = 0; col < columns; col++) {
			if (hierarchical && col == 0) {
				treeCell = new TreeNode(this, parent, previous);
				dataTable.setWidget(row, col, treeCell);
			} else {
				dataTable.setHTML(row, col, "&nbsp;");
			}
		}

		setEmptyRowStyle(row);

		return treeCell;
	}

	/**
	 * Initializes the data rows of the table.
	 */
	private void initDataRows() {
		TreeNode prevNode = null;
		int max = tableRows - 1;

		for (int row = 0; row <= max; row++) {
			prevNode = initDataRow(null, prevNode, row);
		}
	}

	/**
	 * Initializes new visible rows.
	 *
	 * @param prevVisibleRows The previous visible row count
	 */
	private void initNewVisibleRows(int prevVisibleRows) {
		while (prevVisibleRows < visibleDataRows) {
			int initRow = prevVisibleRows;
			TreeNode node = null;

			if (prevVisibleRows > 0) {
				node = (TreeNode) dataTable.getWidget(initRow - 1, 0);
			}

			initDataRow(null, node, initRow);
			prevVisibleRows++;
		}
	}

	/**
	 * Re-initializes the columns of this table.
	 */
	private void resetColumns() {
		dataWidth = dataHeight = 0;

		dataTable.removeAllRows();
		header.initColumns(toolBar.getFilterPanel());

		columnsChanged = false;
	}

	/**
	 * Sets a cell image from the raw cell value.
	 *
	 * @param row       The row of the cell
	 * @param col       The column of the cell
	 * @param cellValue The raw cell value to create the image from
	 */
	private void setCellImage(int row, int col, Object cellValue) {
		if (cellValue != null) {
			String value = cellValue.toString();

			String imageName =
				new StringBuilder(value).insert(1, "im").toString();

			de.esoco.ewt.graphics.Image cellImage =
				context.createImage(imageName);

			if (cellImage instanceof ImageRef) {
				Image image = ((ImageRef) cellImage).getGwtImage();

				image.setTitle(context.expandResource(value));
				dataTable.setWidget(row, col, image);
			} else if (!imageName.endsWith("Null")) {
				GWT.log("No image for " + imageName);
			}
		}
	}

	/**
	 * Sets the table styles of an empty row.
	 *
	 * @param row The index of the row to set the style of
	 */
	private void setEmptyRowStyle(int row) {
		RowFormatter rowFormatter = dataTable.getRowFormatter();

		rowFormatter.setStyleName(row, CSS.ewtTableRow());
		rowFormatter.addStyleName(row, CSS.ewtEmpty());
	}

	/**
	 * Sets the selection to the row of a certain cell.
	 *
	 * @param cell      The cell to select (NULL values will be ignored)
	 * @param fireEvent TRUE to fire a selection event
	 */
	private void setSelection(Cell cell, boolean fireEvent) {
		if (cell != null) {
			int row = cell.getRowIndex();

			if (row >= 0 && row < visibleDataRows) {
				setSelection(firstRow + row, fireEvent);
			}
		}
	}

	/**
	 * Sets the argument widgets to an HTML size of 100%.
	 *
	 * @param widgets The widgets to set the size of
	 */
	private void setToMaximumSize(Widget... widgets) {
		for (Widget widget : widgets) {
			widget.setSize("100%", "100%");
		}
	}

	/**
	 * Displays an information widget above the table.
	 *
	 * @param infoWidget The widget to display
	 * @param glassPanel TRUE to show the info on top of a glass panel
	 */
	private void showInfo(Widget infoWidget, boolean glassPanel) {
		if (getOffsetWidth() > 0) {
			infoPopupPanel = new DecoratedPopupPanel(true);
			infoPopupPanel.setWidget(infoWidget);
			infoPopupPanel.setGlassEnabled(glassPanel);
			infoPopupPanel.setPopupPositionAndShow(
				new PopupPanel.PositionCallback() {
					@Override
					public void setPosition(int popupWidth, int popupHeight) {
						int x = getAbsoluteLeft() +
							(getOffsetWidth() - popupWidth) / 2;
						int y = getAbsoluteTop() +
							(getOffsetHeight() - popupHeight) / 2;

						infoPopupPanel.setPopupPosition(x, y);
					}
				});
		}
	}

	/**
	 * Internal method to update the display of the table data.
	 *
	 * @return TRUE if the update was successful, false if it is necessary
	 * to be
	 * re-executed with updated parameters (only for remote data models)
	 */
	private boolean updateDisplay() {
		if (dataHeight > 0) {
			checkBounds();

			int prevRows = visibleDataRows;
			int count = data.getElementCount();
			int rows = Math.min(count - firstRow, tableRows);
			int dataTableRows = dataTable.getRowCount();
			int newSelectedRow = selectedRow;

			visibleDataRows = Math.min(tableRows, count - firstRow);

			if (data instanceof RemoteDataModel) {
				RemoteDataModel<?> remoteModel = (RemoteDataModel<?>) data;
				int availableElements = remoteModel.getAvailableElementCount();

				visibleDataRows = Math.min(visibleDataRows, availableElements);

				if (availableElements == 0 &&
					remoteModel.getWindowStart() > 0) {
					return false;
				}
			}

			toolBar.updatePosition(count, rows, firstRow + 1);
			initNewVisibleRows(prevRows);
			header.setAllColumnWidths();

			int emptyRow = fillRows();

			while (emptyRow < tableRows) {
				clearRow(emptyRow++);
			}

			while (dataTableRows > tableRows) {
				dataTable.removeRow(--dataTableRows);
			}

			if (newSelectedRow >= visibleDataRows) {
				newSelectedRow = visibleDataRows - 1;
			}

			setRowSelected(newSelectedRow, false);
			toolBar.updateNavigationButtons();

			if (selectedRow >= 0) {
				currentSelection = getSelection();
			}

			if (newSelection >= 0) {
				Scheduler.get().scheduleDeferred(new ScheduledCommand() {
					@Override
					public void execute() {
						setSelection(newSelection);
						newSelection = -1;
					}
				});
			}
		}

		return true;
	}

	/**
	 * Updates the odd/even row styles for all visible rows beginning with a
	 * certain starting row.
	 *
	 * @param row The starting row
	 */
	private void updateRowStyles(int row) {
		RowFormatter rowFormatter = dataTable.getRowFormatter();
		int max = visibleDataRows;
		boolean odd = (row % 2) == 1;

		while (row < max) {
			if (odd) {
				rowFormatter.addStyleName(row, CSS.ewtOdd());
			} else {
				rowFormatter.removeStyleName(row, CSS.ewtOdd());
			}

			odd = !odd;
			row++;
		}
	}
}
