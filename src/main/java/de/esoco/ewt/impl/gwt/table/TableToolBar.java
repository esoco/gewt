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


/********************************************************************
 * The toolbar
 *
 * @author eso
 */
class TableToolBar extends Composite implements ClickHandler
{
	//~ Instance fields --------------------------------------------------------

	private final GwtTable rTable;

	private TableFilterPanel aFilterPanel = null;

	private FlexTable aToolBarTable = new FlexTable();
	private HTML	  aCountLabel   = new HTML();

	private PushButton   aPrevPageButton;
	private PushButton   aNextPageButton;
	private PushButton   aStartButton;
	private PushButton   aEndButton;
	private PushButton   aCollapseAllButton;
	private PushButton   aExpandAllButton;
	private ToggleButton aLockSizeButton;
	private PushButton   aDownloadButton;
	private PushButton   aClearSelectionButton;

	//~ Constructors -----------------------------------------------------------

	/***************************************
	 * Creates a new instance.
	 *
	 * @param rTable The parent table of this tool bar
	 */
	public TableToolBar(GwtTable rTable)
	{
		this.rTable = rTable;

		UserInterfaceContext rContext = rTable.getContext();

		boolean bFilterPanel = rTable.getData() instanceof FilterableDataModel;

		Grid aNavButtons   = new Grid(1, 5);
		Grid aRightButtons = new Grid(1, 3);

		initToolBarButtons(rContext);

		aNavButtons.setWidget(0, 0, aStartButton);
		aNavButtons.setWidget(0, 1, aPrevPageButton);
		aNavButtons.setWidget(0, 2, aCountLabel);
		aNavButtons.setWidget(0, 3, aNextPageButton);
		aNavButtons.setWidget(0, 4, aEndButton);
		aNavButtons.addStyleName(GwtTable.CSS.ewtNavButtons());

		if (rTable.getData() instanceof Downloadable)
		{
			aRightButtons.setWidget(0, 0, aDownloadButton);
		}

		aRightButtons.setWidget(0, 1, aLockSizeButton);
		aRightButtons.setWidget(0, 2, aClearSelectionButton);

		if (bFilterPanel)
		{
			initFilterPanel(rTable);
		}

		if (rTable.isHierarchical())
		{
			initTreeControls();
		}

		aToolBarTable.setWidget(1, 1, aNavButtons);
		aToolBarTable.setWidget(1, 2, aRightButtons);
		aToolBarTable.getCellFormatter().setWidth(1, 1, "100%");

		aToolBarTable.setStylePrimaryName(GwtTable.CSS.ewtToolbar());

		initWidget(aToolBarTable);
	}

	//~ Methods ----------------------------------------------------------------

	/***************************************
	 * Returns the filter panel of this toolbar.
	 *
	 * @return The filter panel or NULL if the data model is not searchable
	 */
	public final TableFilterPanel getFilterPanel()
	{
		return aFilterPanel;
	}

	/***************************************
	 * @see ClickHandler#onClick(ClickEvent)
	 */
	@Override
	public void onClick(ClickEvent rEvent)
	{
		if (rTable.canHandleInput())
		{
			Object rSource = rEvent.getSource();

			if (rSource == aClearSelectionButton)
			{
				rTable.setSelection(-1);
			}
			else if (rSource == aDownloadButton)
			{
				rTable.initiateDownload();
			}
			else if (rSource == aLockSizeButton)
			{
				rTable.setHeightLocked(isHeightLocked());
			}
			else
			{
				rTable.setRowUnselected(rTable.getSelectedRow());

				if (rSource == aCollapseAllButton)
				{
					rTable.collapseAllNodes();
				}
				else if (rSource == aExpandAllButton)
				{
					rTable.expandAllNodes();
				}
				else
				{
					handleNavigationButton(rSource);
				}
			}
		}
	}

	/***************************************
	 * Resets all filter criteria in the model.
	 */
	public void resetFilterCriteria()
	{
		if (aFilterPanel != null)
		{
			aFilterPanel.removeFilter();
		}
	}

	/***************************************
	 * Sets the enabled state of this toolbar.
	 *
	 * @param bEnabled The new enabled state
	 */
	public void setEnabled(boolean bEnabled)
	{
		if (bEnabled)
		{
			updateNavigationButtons();
		}
		else
		{
			aPrevPageButton.setEnabled(false);
			aNextPageButton.setEnabled(false);
			aStartButton.setEnabled(false);
			aEndButton.setEnabled(false);
		}

		if (aCollapseAllButton != null)
		{
			aCollapseAllButton.setEnabled(bEnabled);
			aExpandAllButton.setEnabled(bEnabled);
		}

		aClearSelectionButton.setEnabled(bEnabled &&
										 rTable.getSelectionIndex() >= 0);

		if (aFilterPanel != null)
		{
			aFilterPanel.setEnabled(bEnabled);
		}
	}

	/***************************************
	 * Returns the height locked.
	 *
	 * @return The height locked
	 */
	boolean isHeightLocked()
	{
		return !aLockSizeButton.isDown();
	}

	/***************************************
	 * Sets the enabled state of the clear selection button.
	 *
	 * @param bEnabled The enabled state
	 */
	void setClearSelectionButtonEnabled(boolean bEnabled)
	{
		aClearSelectionButton.setEnabled(bEnabled);
	}

	/***************************************
	 * Updates the navigation button states.
	 */
	void updateNavigationButtons()
	{
		int     nFirstRow = rTable.getFirstRow();
		boolean bHasPrev  = nFirstRow != 0;
		boolean bHasNext  =
			nFirstRow + rTable.getVisibleRowCount() <
			rTable.getData().getElementCount();

		aStartButton.setEnabled(bHasPrev);
		aPrevPageButton.setEnabled(bHasPrev);
		aNextPageButton.setEnabled(bHasNext);
		aEndButton.setEnabled(bHasNext);
	}

	/***************************************
	 * Updates the navigation position and row count display.
	 *
	 * @param nRowCount    The number of rows in the data
	 * @param nVisibleRows The number of displayed rows
	 * @param nFirstRow    The index of the first visible row
	 */
	void updatePosition(int nRowCount, int nVisibleRows, int nFirstRow)
	{
		int nLastRow = nFirstRow + nVisibleRows - 1;

		if (nLastRow == 0)
		{
			nFirstRow = 0;
		}

		aCountLabel.setHTML("" + nFirstRow + "&nbsp;-&nbsp;" + nLastRow +
							"/" + nRowCount);
	}

	/***************************************
	 * Handles click events for navigation buttons.
	 *
	 * @param rButton The navigation button
	 */
	private void handleNavigationButton(Object rButton)
	{
		int nTableRows = rTable.getVisibleRowCount();
		int nFirstRow  = rTable.getFirstRow();

		rTable.setRowUnselected(rTable.getSelectedRow());
		rTable.setSelectedRow(-1);

		if (rButton == aEndButton)
		{
			rTable.setFirstRow(rTable.getData().getElementCount());
		}
		else if (rButton == aStartButton)
		{
			rTable.setFirstRow(0);
		}

		if (rButton == aNextPageButton)
		{
			rTable.setFirstRow(nFirstRow + nTableRows);
		}
		else if (rButton == aPrevPageButton)
		{
			rTable.setFirstRow(nFirstRow - nTableRows);
		}

		rTable.update();
	}

	/***************************************
	 * Initializes the filter panel for a searchable data model.
	 *
	 * @param rTable The parent table of this toolbar and the filter panel
	 */
	private void initFilterPanel(GwtTable rTable)
	{
		FlexCellFormatter rCellFormatter = aToolBarTable.getFlexCellFormatter();

		aFilterPanel = new TableFilterPanel(rTable);
		aToolBarTable.setWidget(0, 0, aFilterPanel);
		rCellFormatter.setColSpan(0, 0, 3);
	}

	/***************************************
	 * Creates an initializes the toolbar buttons.
	 *
	 * @param rContext The user interface context
	 */
	private void initToolBarButtons(UserInterfaceContext rContext)
	{
		aPrevPageButton		  =
			new PushButton(new Image(GwtTable.RES.imLeft()));
		aNextPageButton		  =
			new PushButton(new Image(GwtTable.RES.imRight()));
		aStartButton		  =
			new PushButton(new Image(GwtTable.RES.imBack()));
		aEndButton			  =
			new PushButton(new Image(GwtTable.RES.imForward()));
		aLockSizeButton		  =
			new ToggleButton(new Image(GwtTable.RES.imLock()),
							 new Image(GwtTable.RES.imUnlock()));
		aDownloadButton		  =
			new PushButton(new Image(GwtTable.RES.imDownload()));
		aClearSelectionButton =
			new PushButton(new Image(GwtTable.RES.imClearSelection()));

		aPrevPageButton.setTitle(rContext.expandResource("$ttPrevTablePage"));
		aNextPageButton.setTitle(rContext.expandResource("$ttNextTablePage"));
		aStartButton.setTitle(rContext.expandResource("$ttFirstTablePage"));
		aEndButton.setTitle(rContext.expandResource("$ttLastTablePage"));
		aLockSizeButton.setTitle(rContext.expandResource("$ttLockTableSize"));
		aDownloadButton.setTitle(rContext.expandResource("$ttDownloadTableContent"));
		aClearSelectionButton.setTitle(rContext.expandResource("$ttClearSelection"));

		aPrevPageButton.addClickHandler(this);
		aNextPageButton.addClickHandler(this);
		aStartButton.addClickHandler(this);
		aEndButton.addClickHandler(this);
		aLockSizeButton.addClickHandler(this);
		aDownloadButton.addClickHandler(this);
		aClearSelectionButton.addClickHandler(this);

		aClearSelectionButton.setEnabled(false);
	}

	/***************************************
	 * Initializes the controls for tree tables.
	 */
	private void initTreeControls()
	{
		Grid aTreeButtons = new Grid(1, 2);

		aCollapseAllButton =
			new PushButton(new Image(GwtTable.RES.imTreeCollapse()));
		aExpandAllButton   =
			new PushButton(new Image(GwtTable.RES.imTreeExpand()));

		aTreeButtons.setWidget(0, 0, aCollapseAllButton);
		aTreeButtons.setWidget(0, 1, aExpandAllButton);

		aToolBarTable.setWidget(1, 0, aTreeButtons);

		aCollapseAllButton.addClickHandler(this);
		aExpandAllButton.addClickHandler(this);
	}
}
