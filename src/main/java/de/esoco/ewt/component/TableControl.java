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
package de.esoco.ewt.component;

import de.esoco.ewt.event.EventType;
import de.esoco.ewt.impl.gwt.table.GwtTable;
import de.esoco.ewt.style.StyleData;

import de.esoco.lib.model.ColumnDefinition;
import de.esoco.lib.model.DataModel;
import de.esoco.lib.property.SingleSelection;
import de.esoco.lib.property.UserInterfaceProperties;

import com.google.gwt.event.dom.client.ClickEvent;


/********************************************************************
 * Base class for GEWT table controls.
 *
 * @author eso
 */
public abstract class TableControl extends Control implements SingleSelection
{
	//~ Instance fields --------------------------------------------------------

	private GwtTable aGwtTable;

	//~ Constructors -----------------------------------------------------------

	/***************************************
	 * Creates a new instance.
	 *
	 * @param bHierarchical TRUE for a hierarchical (tree) table
	 */
	protected TableControl(boolean bHierarchical)
	{
		super(new GwtTable(bHierarchical));

		aGwtTable = (GwtTable) getWidget();
		aGwtTable.setEventDispatcher(new GewtEventDispatcherImpl());
	}

	//~ Methods ----------------------------------------------------------------

	/***************************************
	 * Overridden to also apply table-specific styles.
	 *
	 * @see Control#applyStyle(StyleData)
	 */
	@Override
	public void applyStyle(StyleData rStyle)
	{
		super.applyStyle(rStyle);

		int nRows =
			rStyle.getIntProperty(UserInterfaceProperties.TABLE_ROWS, 0);

		if (nRows != 0)
		{
			aGwtTable.setVisibleRowCount(nRows);
		}
	}

	/***************************************
	 * Returns the table's column data model. Will be NULL if no columns have
	 * been set yet through the method {@link #setColumns(DataModel)}.
	 *
	 * @return The column data model of this table
	 */
	public DataModel<ColumnDefinition> getColumns()
	{
		return aGwtTable.getColumns();
	}

	/***************************************
	 * Returns the table's data model. Will be NULL if no data model has been
	 * set yet through the method {@link #setData(DataModel)}.
	 *
	 * @return The data model of this table
	 */
	public DataModel<?> getData()
	{
		return aGwtTable.getData();
	}

	/***************************************
	 * Returns the currently selected row data model or NULL if no row is
	 * selected.
	 *
	 * @return The currently selected row data model or NULL for none
	 */
	public DataModel<?> getSelection()
	{
		return aGwtTable.getSelection();
	}

	/***************************************
	 * @see SingleSelection#getSelectionIndex()
	 */
	@Override
	public int getSelectionIndex()
	{
		return ((SingleSelection) getWidget()).getSelectionIndex();
	}

	/***************************************
	 * @see Component#isEnabled()
	 */
	@Override
	public boolean isEnabled()
	{
		return aGwtTable.isEnabled();
	}

	/***************************************
	 * @see Component#repaint()
	 */
	@Override
	public void repaint()
	{
		aGwtTable.repaint();
	}

	/***************************************
	 * Sets the table columns. The columns are defined in form of a data model
	 * that must contain one data element for each table column.
	 *
	 * @param rColumnModel The table column data model
	 */
	public void setColumns(DataModel<ColumnDefinition> rColumnModel)
	{
		aGwtTable.setColumns(rColumnModel);
	}

	/***************************************
	 * Sets the table's data model. The data elements of the model will be
	 * displayed as the rows of the table. They must also implement the
	 * interface {@link DataModel} and provide the table cells as their
	 * elements.
	 *
	 * @param rDataModel The data model for this table
	 */
	public void setData(DataModel<? extends DataModel<?>> rDataModel)
	{
		aGwtTable.setData(rDataModel);
	}

	/***************************************
	 * @see Component#setEnabled(boolean)
	 */
	@Override
	public void setEnabled(boolean bEnabled)
	{
		aGwtTable.setEnabled(bEnabled);
	}

	/***************************************
	 * @see SingleSelection#setSelection(int)
	 */
	@Override
	public void setSelection(int nIndex)
	{
		setSelection(nIndex, false);
	}

	/***************************************
	 * Sets the selection of this table and optionally fires the associated
	 * events.
	 *
	 * @param nIndex     The selection index
	 * @param bFireEvent TRUE to fire a selection event
	 */
	public void setSelection(int nIndex, boolean bFireEvent)
	{
		aGwtTable.setSelection(nIndex, bFireEvent);
	}

	/***************************************
	 * @see Component#createEventDispatcher()
	 */
	@Override
	protected ComponentEventDispatcher createEventDispatcher()
	{
		return new TableEventDispatcher();
	}

	/***************************************
	 * Overridden to set the context at the internal {@link GwtTable} instance.
	 *
	 * @see Component#setParent(Container)
	 */
	@Override
	protected void setParent(Container rParent)
	{
		super.setParent(rParent);

		aGwtTable.setContext(rParent.getContext());
	}

	//~ Inner Classes ----------------------------------------------------------

	/********************************************************************
	 * A table-specific event dispatcher.
	 *
	 * @author eso
	 */
	class TableEventDispatcher extends ComponentEventDispatcher
	{
		//~ Methods ------------------------------------------------------------

		/***************************************
		 * Overridden to generate {@link EventType#POINTER_CLICKED} events
		 * instead of the default {@link EventType#ACTION} because the latter is
		 * generated directly by {@link GwtTable}.
		 *
		 * @see ComponentEventDispatcher#onClick(ClickEvent)
		 */
		@Override
		public void onClick(ClickEvent rEvent)
		{
			notifyEventHandler(EventType.POINTER_CLICKED, rEvent);
		}
	}
}
