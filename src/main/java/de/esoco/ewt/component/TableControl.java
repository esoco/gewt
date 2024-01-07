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
package de.esoco.ewt.component;

import de.esoco.ewt.event.EventType;
import de.esoco.ewt.impl.gwt.GewtEventDispatcher;
import de.esoco.ewt.impl.gwt.WidgetFactory;
import de.esoco.ewt.impl.gwt.table.GwtTable;
import de.esoco.ewt.style.StyleData;

import de.esoco.lib.model.ColumnDefinition;
import de.esoco.lib.model.DataModel;
import de.esoco.lib.property.SingleSelection;
import de.esoco.lib.property.TitleAttribute;
import de.esoco.lib.property.UserInterfaceProperties;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * Base class for GEWT table controls.
 *
 * @author eso
 */
public abstract class TableControl extends Control
	implements SingleSelection, TitleAttribute {

	private IsTableControlWidget table;

	/**
	 * Overridden to also apply table-specific styles.
	 *
	 * @see Control#applyStyle(StyleData)
	 */
	@Override
	public void applyStyle(StyleData style) {
		super.applyStyle(style);

		int rows = style.getIntProperty(UserInterfaceProperties.TABLE_ROWS, 0);

		if (rows > 0) {
			table.setVisibleRowCount(rows);
		}
	}

	/**
	 * Returns the table's column data model. Will be NULL if no columns have
	 * been set yet through the method {@link #setColumns(DataModel)}.
	 *
	 * @return The column data model of this table
	 */
	public DataModel<ColumnDefinition> getColumns() {
		return table.getColumns();
	}

	/**
	 * Returns the table's data model. Will be NULL if no data model has been
	 * set yet through the method {@link #setData(DataModel)}.
	 *
	 * @return The data model of this table
	 */
	public DataModel<?> getData() {
		return table.getData();
	}

	/**
	 * Returns the currently selected row data model or NULL if no row is
	 * selected.
	 *
	 * @return The currently selected row data model or NULL for none
	 */
	public DataModel<?> getSelection() {
		return table.getSelection();
	}

	/**
	 * @see SingleSelection#getSelectionIndex()
	 */
	@Override
	public int getSelectionIndex() {
		return ((SingleSelection) getWidget()).getSelectionIndex();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getTitle() {
		return table.getTableTitle();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initWidget(Container parent, StyleData style) {
		super.initWidget(parent, style);

		table = (IsTableControlWidget) getWidget();
		table.setEventDispatcher(new GewtEventDispatcherImpl());
	}

	/**
	 * @see Component#isEnabled()
	 */
	@Override
	public boolean isEnabled() {
		return table.isEnabled();
	}

	/**
	 * @see Component#repaint()
	 */
	@Override
	public void repaint() {
		table.repaint();
	}

	/**
	 * Sets the table columns. The columns are defined in form of a data model
	 * that must contain one data element for each table column.
	 *
	 * @param columnModel The table column data model
	 */
	public void setColumns(DataModel<ColumnDefinition> columnModel) {
		table.setColumns(columnModel);
	}

	/**
	 * Sets the table's data model. The data elements of the model will be
	 * displayed as the rows of the table. They must also implement the
	 * interface {@link DataModel} and provide the table cells as their
	 * elements.
	 *
	 * @param dataModel The data model for this table
	 */
	public void setData(DataModel<? extends DataModel<?>> dataModel) {
		table.setData(dataModel);
	}

	/**
	 * @see Component#setEnabled(boolean)
	 */
	@Override
	public void setEnabled(boolean enabled) {
		table.setEnabled(enabled);
	}

	/**
	 * @see SingleSelection#setSelection(int)
	 */
	@Override
	public void setSelection(int index) {
		setSelection(index, false);
	}

	/**
	 * Sets the selection of this table and optionally fires the associated
	 * events.
	 *
	 * @param index     The selection index
	 * @param fireEvent TRUE to fire a selection event
	 */
	public void setSelection(int index, boolean fireEvent) {
		table.setSelection(index, fireEvent);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setTitle(String title) {
		table.setTableTitle(title);
	}

	/**
	 * @see Component#createEventDispatcher()
	 */
	@Override
	protected ComponentEventDispatcher createEventDispatcher() {
		return new TableEventDispatcher();
	}

	/**
	 * The interface for table widgets.
	 *
	 * @author eso
	 */
	public static interface IsTableControlWidget
		extends IsWidget, Focusable, HasEnabled, SingleSelection {

		/**
		 * Returns the column model.
		 *
		 * @return The column model
		 */
		public DataModel<ColumnDefinition> getColumns();

		/**
		 * Returns the model of the table data.
		 *
		 * @return The data model
		 */
		public DataModel<?> getData();

		/**
		 * Returns the data model of the current selection.
		 *
		 * @return The current selection
		 */
		public DataModel<?> getSelection();

		/**
		 * Returns the table title.
		 *
		 * @return The table title
		 */
		public String getTableTitle();

		/**
		 * Renders the table from updated content.
		 */
		public void repaint();

		/**
		 * Sets the table columns from a data model of column definitions.
		 *
		 * @param columnModel The column data model
		 */
		public void setColumns(DataModel<ColumnDefinition> columnModel);

		/**
		 * Sets the table data to a data model that contains data models for
		 * each table row.
		 *
		 * @param dataModel The table data model
		 */
		public void setData(DataModel<? extends DataModel<?>> dataModel);

		/**
		 * Sets the event dispatcher to be used to notify event listeners.
		 *
		 * @param eventDispatcher The event dispatcher
		 */
		public void setEventDispatcher(GewtEventDispatcher eventDispatcher);

		/**
		 * Sets the selection and optionally fires a selection event.
		 *
		 * @param index     The index of the new selection
		 * @param fireEvent TRUE to fire a selection event
		 */
		public void setSelection(int index, boolean fireEvent);

		/**
		 * Sets the table title.
		 *
		 * @param tableTitle The new table title
		 */
		public void setTableTitle(String tableTitle);

		/**
		 * Sets the visible row count.
		 *
		 * @param count The new visible row count
		 */
		public void setVisibleRowCount(int count);
	}

	/**
	 * Widget factory for subclasses.
	 *
	 * @author eso
	 */
	public static class TableControlWidgetFactory
		implements WidgetFactory<IsTableControlWidget> {

		private boolean hierarchical;

		/**
		 * Creates a new instance.
		 *
		 * @param hierarchical TRUE for a tree-table
		 */
		public TableControlWidgetFactory(boolean hierarchical) {
			this.hierarchical = hierarchical;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public IsTableControlWidget createWidget(Component component,
			StyleData style) {
			return new GwtTable(component.getContext(), hierarchical);
		}
	}

	/**
	 * A table-specific event dispatcher.
	 *
	 * @author eso
	 */
	class TableEventDispatcher extends ComponentEventDispatcher {

		/**
		 * Overridden to generate {@link EventType#POINTER_CLICKED} events
		 * instead of the default {@link EventType#ACTION} because the
		 * latter is
		 * generated directly by {@link GwtTable}.
		 *
		 * @param event The click event
		 */
		@Override
		public void onClick(ClickEvent event) {
			notifyEventHandler(EventType.POINTER_CLICKED, event);
		}
	}
}
