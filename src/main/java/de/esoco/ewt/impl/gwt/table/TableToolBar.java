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

import de.esoco.lib.model.Downloadable;
import de.esoco.lib.model.FilterableDataModel;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.ToggleButton;

/**
 * The toolbar
 *
 * @author eso
 */
class TableToolBar extends Composite implements ClickHandler {

	private final GwtTable table;

	private TableFilterPanel filterPanel = null;

	private FlexTable toolBarTable = new FlexTable();

	private HTML countLabel = new HTML();

	private PushButton prevPageButton;

	private PushButton nextPageButton;

	private PushButton startButton;

	private PushButton endButton;

	private PushButton collapseAllButton;

	private PushButton expandAllButton;

	private ToggleButton lockSizeButton;

	private PushButton downloadButton;

	private PushButton clearSelectionButton;

	/**
	 * Creates a new instance.
	 *
	 * @param table The parent table of this tool bar
	 */
	public TableToolBar(GwtTable table) {
		this.table = table;

		UserInterfaceContext context = table.getContext();

		boolean filterPanel = table.getData() instanceof FilterableDataModel;

		Grid navButtons = new Grid(1, 5);
		Grid rightButtons = new Grid(1, 3);

		initToolBarButtons(context);

		navButtons.setWidget(0, 0, startButton);
		navButtons.setWidget(0, 1, prevPageButton);
		navButtons.setWidget(0, 2, countLabel);
		navButtons.setWidget(0, 3, nextPageButton);
		navButtons.setWidget(0, 4, endButton);
		navButtons.addStyleName(GwtTable.CSS.ewtNavButtons());

		if (table.getData() instanceof Downloadable) {
			rightButtons.setWidget(0, 0, downloadButton);
		}

		rightButtons.setWidget(0, 1, lockSizeButton);
		rightButtons.setWidget(0, 2, clearSelectionButton);

		if (filterPanel) {
			initFilterPanel(table);
		}

		if (table.isHierarchical()) {
			initTreeControls();
		}

		toolBarTable.setWidget(1, 1, navButtons);
		toolBarTable.setWidget(1, 2, rightButtons);
		toolBarTable.getCellFormatter().setWidth(1, 1, "100%");

		toolBarTable.setStylePrimaryName(GwtTable.CSS.ewtToolbar());

		initWidget(toolBarTable);
	}

	/**
	 * Returns the filter panel of this toolbar.
	 *
	 * @return The filter panel or NULL if the data model is not searchable
	 */
	public final TableFilterPanel getFilterPanel() {
		return filterPanel;
	}

	/**
	 * @see ClickHandler#onClick(ClickEvent)
	 */
	@Override
	public void onClick(ClickEvent event) {
		if (table.canHandleInput()) {
			Object source = event.getSource();

			if (source == clearSelectionButton) {
				table.setSelection(-1);
			} else if (source == downloadButton) {
				table.initiateDownload();
			} else if (source == lockSizeButton) {
				table.setHeightLocked(isHeightLocked());
			} else {
				table.setRowUnselected(table.getSelectedRow());

				if (source == collapseAllButton) {
					table.collapseAllNodes();
				} else if (source == expandAllButton) {
					table.expandAllNodes();
				} else {
					handleNavigationButton(source);
				}
			}
		}
	}

	/**
	 * Resets all filter criteria in the model.
	 */
	public void resetFilterCriteria() {
		if (filterPanel != null) {
			filterPanel.removeFilter();
		}
	}

	/**
	 * Sets the enabled state of this toolbar.
	 *
	 * @param enabled The new enabled state
	 */
	public void setEnabled(boolean enabled) {
		if (enabled) {
			updateNavigationButtons();
		} else {
			prevPageButton.setEnabled(false);
			nextPageButton.setEnabled(false);
			startButton.setEnabled(false);
			endButton.setEnabled(false);
		}

		if (collapseAllButton != null) {
			collapseAllButton.setEnabled(enabled);
			expandAllButton.setEnabled(enabled);
		}

		clearSelectionButton.setEnabled(
			enabled && table.getSelectionIndex() >= 0);

		if (filterPanel != null) {
			filterPanel.setEnabled(enabled);
		}
	}

	/**
	 * Returns the height locked.
	 *
	 * @return The height locked
	 */
	boolean isHeightLocked() {
		return !lockSizeButton.isDown();
	}

	/**
	 * Sets the enabled state of the clear selection button.
	 *
	 * @param enabled The enabled state
	 */
	void setClearSelectionButtonEnabled(boolean enabled) {
		clearSelectionButton.setEnabled(enabled);
	}

	/**
	 * Updates the navigation button states.
	 */
	void updateNavigationButtons() {
		int firstRow = table.getFirstRow();
		boolean hasPrev = firstRow != 0;
		boolean hasNext = firstRow + table.getVisibleRowCount() <
			table.getData().getElementCount();

		startButton.setEnabled(hasPrev);
		prevPageButton.setEnabled(hasPrev);
		nextPageButton.setEnabled(hasNext);
		endButton.setEnabled(hasNext);
	}

	/**
	 * Updates the navigation position and row count display.
	 *
	 * @param rowCount    The number of rows in the data
	 * @param visibleRows The number of displayed rows
	 * @param firstRow    The index of the first visible row
	 */
	void updatePosition(int rowCount, int visibleRows, int firstRow) {
		int lastRow = firstRow + visibleRows - 1;

		if (lastRow == 0) {
			firstRow = 0;
		}

		countLabel.setHTML(
			"" + firstRow + "&nbsp;-&nbsp;" + lastRow + "/" + rowCount);
	}

	/**
	 * Handles click events for navigation buttons.
	 *
	 * @param button The navigation button
	 */
	private void handleNavigationButton(Object button) {
		int tableRows = table.getVisibleRowCount();
		int firstRow = table.getFirstRow();

		table.setRowUnselected(table.getSelectedRow());
		table.setSelectedRow(-1);

		if (button == endButton) {
			table.setFirstRow(table.getData().getElementCount());
		} else if (button == startButton) {
			table.setFirstRow(0);
		}

		if (button == nextPageButton) {
			table.setFirstRow(firstRow + tableRows);
		} else if (button == prevPageButton) {
			table.setFirstRow(firstRow - tableRows);
		}

		table.update();
	}

	/**
	 * Initializes the filter panel for a searchable data model.
	 *
	 * @param table The parent table of this toolbar and the filter panel
	 */
	private void initFilterPanel(GwtTable table) {
		FlexCellFormatter cellFormatter = toolBarTable.getFlexCellFormatter();

		filterPanel = new TableFilterPanel(table);
		toolBarTable.setWidget(0, 0, filterPanel);
		cellFormatter.setColSpan(0, 0, 3);
	}

	/**
	 * Creates an initializes the toolbar buttons.
	 *
	 * @param context The user interface context
	 */
	private void initToolBarButtons(UserInterfaceContext context) {
		prevPageButton = new PushButton(new Image(GwtTable.RES.imLeft()));
		nextPageButton = new PushButton(new Image(GwtTable.RES.imRight()));
		startButton = new PushButton(new Image(GwtTable.RES.imBack()));
		endButton = new PushButton(new Image(GwtTable.RES.imForward()));
		lockSizeButton = new ToggleButton(new Image(GwtTable.RES.imLock()),
			new Image(GwtTable.RES.imUnlock()));
		downloadButton = new PushButton(new Image(GwtTable.RES.imDownload()));
		clearSelectionButton =
			new PushButton(new Image(GwtTable.RES.imClearSelection()));

		prevPageButton.setTitle(context.expandResource("$ttPrevTablePage"));
		nextPageButton.setTitle(context.expandResource("$ttNextTablePage"));
		startButton.setTitle(context.expandResource("$ttFirstTablePage"));
		endButton.setTitle(context.expandResource("$ttLastTablePage"));
		lockSizeButton.setTitle(context.expandResource("$ttLockTableSize"));
		downloadButton.setTitle(
			context.expandResource("$ttDownloadTableContent"));
		clearSelectionButton.setTitle(
			context.expandResource("$ttClearSelection"));

		prevPageButton.addClickHandler(this);
		nextPageButton.addClickHandler(this);
		startButton.addClickHandler(this);
		endButton.addClickHandler(this);
		lockSizeButton.addClickHandler(this);
		downloadButton.addClickHandler(this);
		clearSelectionButton.addClickHandler(this);

		clearSelectionButton.setEnabled(false);
	}

	/**
	 * Initializes the controls for tree tables.
	 */
	private void initTreeControls() {
		Grid treeButtons = new Grid(1, 2);

		collapseAllButton =
			new PushButton(new Image(GwtTable.RES.imTreeCollapse()));
		expandAllButton =
			new PushButton(new Image(GwtTable.RES.imTreeExpand()));

		treeButtons.setWidget(0, 0, collapseAllButton);
		treeButtons.setWidget(0, 1, expandAllButton);

		toolBarTable.setWidget(1, 0, treeButtons);

		collapseAllButton.addClickHandler(this);
		expandAllButton.addClickHandler(this);
	}
}
