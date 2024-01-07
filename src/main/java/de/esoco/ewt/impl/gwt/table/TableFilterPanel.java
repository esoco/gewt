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

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DecoratedPopupPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.PopupPanel.PositionCallback;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;
import com.google.gwt.user.datepicker.client.DateBox.DefaultFormat;
import com.google.gwt.user.datepicker.client.DatePicker;
import de.esoco.ewt.impl.gwt.ValueBoxConstraint.RegExConstraint;
import de.esoco.lib.model.ColumnDefinition;
import de.esoco.lib.model.DataModel;
import de.esoco.lib.model.FilterableDataModel;
import de.esoco.lib.text.TextConvert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static de.esoco.lib.model.FilterableDataModel.CONSTRAINT_AND_PREFIX;
import static de.esoco.lib.model.FilterableDataModel.CONSTRAINT_COMPARISON_CHARS;
import static de.esoco.lib.model.FilterableDataModel.CONSTRAINT_OR_PREFIX;
import static de.esoco.lib.model.FilterableDataModel.CONSTRAINT_SEPARATOR;
import static de.esoco.lib.model.FilterableDataModel.CONSTRAINT_SEPARATOR_ESCAPE;
import static de.esoco.lib.model.FilterableDataModel.NULL_CONSTRAINT_VALUE;
import static de.esoco.lib.property.ContentProperties.ALLOWED_VALUES;
import static de.esoco.lib.property.ContentProperties.INPUT_CONSTRAINT;
import static de.esoco.lib.property.ContentProperties.VALUE_RESOURCE_PREFIX;

/**
 * A panel that contains the filter functionality for a table.
 *
 * @author eso
 */
class TableFilterPanel extends Composite
	implements ClickHandler, KeyUpHandler, ChangeHandler {

	private static final int COL_FILTER_JOIN = 0;

	private static final int COL_FILTER_COLUMN = COL_FILTER_JOIN + 1;

	private static final int COL_FILTER_COMPARE = COL_FILTER_COLUMN + 1;

	private static final int COL_FILTER_VALUE = COL_FILTER_COMPARE + 1;

	private static final int COL_FILTER_BUTTON = COL_FILTER_VALUE + 1;

	private static final DateTimeFormat FILTER_DATE_FORMAT =
		DateTimeFormat.getFormat(
			FilterableDataModel.CONSTRAINT_DATE_FORMAT_PATTERN);

	private final GwtTable table;

	private final FlexTable filterPanel = new FlexTable();

	private final TextBox filterInput = new TextBox();

	private final ToggleButton filterButton =
		new ToggleButton(new Image(GwtTable.RES.imFilter()));

	private final PushButton clearFilterButton =
		new PushButton(new Image(GwtTable.RES.imCancel()));

	private final List<ColumnDefinition> filterColumns =
		new ArrayList<ColumnDefinition>();

	private PopupPanel filterPopup;

	private FlexTable filterCriteriaPanel;

	private Timer filterInputTimer;

	private KeyDownHandler complexFilterKeyDownHandler = null;

	private KeyUpHandler complexFilterKeyUpHandler = null;

	/**
	 * Creates a new instance.
	 *
	 * @param table The {@link GwtTable} this instance belongs to
	 */
	TableFilterPanel(GwtTable table) {
		this.table = table;

		clearFilterButton.setTitle(expand("$ttClearTableFilter"));
		filterInput.setTitle(expand("$ttTableFilterValue"));

		filterPanel.setWidget(0, 0, filterButton);
		filterPanel.setWidget(0, 1, filterInput);
		filterPanel.setWidget(0, 2, clearFilterButton);

		filterInput.setWidth("100%");
		filterInput.setStylePrimaryName(GwtTable.CSS.ewtTableFilterValue());
		filterInput.addKeyUpHandler(this);

		filterPanel.setWidth("100%");
		filterPanel.getCellFormatter().setWidth(0, 1, "100%");
		filterPanel.setStylePrimaryName(GwtTable.CSS.ewtFilter());

		filterButton.setStylePrimaryName(GwtTable.CSS.ewtTableFilterButton());
		filterButton.addClickHandler(this);
		clearFilterButton.addClickHandler(this);

		initWidget(filterPanel);
	}

	/**
	 * Processes filter popup column change events.
	 *
	 * @see ChangeHandler#onChange(ChangeEvent)
	 */
	@Override
	public void onChange(ChangeEvent event) {
		int row = findFilterCriterionRow(COL_FILTER_COLUMN,
			(Widget) event.getSource());

		ColumnDefinition column = getSelectedFilterColumn(row);

		filterCriteriaPanel.setWidget(row, COL_FILTER_VALUE,
			createInputWidget(column, null));
	}

	/**
	 * @see ClickHandler#onClick(ClickEvent)
	 */
	@Override
	public void onClick(ClickEvent event) {
		if (table.canHandleInput()) {
			Object source = event.getSource();

			if (source == filterButton) {
				handleComplexFilterButton();
			} else if (source == clearFilterButton) {
				resetFilter();
			}
		}
	}

	/**
	 * Event handling for keyboard input in the filter text box.
	 *
	 * @see KeyUpHandler#onKeyUp(KeyUpEvent)
	 */
	@Override
	public void onKeyUp(KeyUpEvent event) {
		if (table.isEnabled()) {
			int keyCode = event.getNativeKeyCode();

			switch (keyCode) {
				case KeyCodes.KEY_ESCAPE:
					resetInputValue(filterInput);

					break;

				default:

					if (filterInputTimer == null) {
						filterInputTimer = new Timer() {
							@Override
							public void run() {
								applyGlobalFilter(filterInput.getText());
							}
						};
					}

					filterInputTimer.schedule(500);
			}
		}
	}

	/**
	 * Sets the enabled state of the widgets in this panel.
	 *
	 * @param enabled The new enabled state
	 */
	public void setEnabled(boolean enabled) {
		filterInput.setEnabled(enabled);
		filterButton.setEnabled(enabled);
		clearFilterButton.setEnabled(enabled);
	}

	/**
	 * Sets the focus to the filter input field.
	 *
	 * @param focused The focused state to set
	 */
	public void setFocus(boolean focused) {
		filterInput.setFocus(focused);
	}

	/**
	 * Adds a filter column to this panel.
	 *
	 * @param column The definition of the filter column
	 */
	void addFilterColumn(ColumnDefinition column) {
		filterColumns.add(column);
	}

	/**
	 * Sets the global filter constraint for all filterable columns of the data
	 * model.
	 *
	 * @param constraint The constraint to set
	 */
	void applyGlobalFilter(String constraint) {
		FilterableDataModel<?> model = getSearchableModel();
		String numberConstraint = null;

		constraint = constraint.trim();

		if (constraint.length() == 0) {
			constraint = null;
		} else {
			boolean hasComparison =
				CONSTRAINT_COMPARISON_CHARS.indexOf(constraint.charAt(0)) >= 0;

			if (!hasComparison) {
				if (constraint.indexOf('*') == -1) {
					try {
						Integer.parseInt(constraint);
						numberConstraint =
							CONSTRAINT_OR_PREFIX + "=" + constraint;
					} catch (Exception e) {
						// leave number constraint as null
					}

					constraint = constraint + "*";
				}

				constraint = "=" + constraint;
			}

			constraint = CONSTRAINT_OR_PREFIX + constraint;
		}

		for (ColumnDefinition column : filterColumns) {
			String columnId = column.getId();
			String datatype = column.getDatatype();

			if (String.class.getSimpleName().equals(datatype)) {
				model.setFilter(columnId, constraint);
			} else if (Integer.class.getSimpleName().equals(datatype)) {
				model.setFilter(columnId, numberConstraint);
			}
		}

		updateTable();
		table.setSelection(-1);
	}

	/**
	 * Handles keyboard input in the value field of a complex filter criterion.
	 *
	 * @param event The event that occurred.
	 */
	void handleComplexFilterKeyDown(KeyDownEvent event) {
		if (table.isEnabled() && !table.isBusy()) {
			int keyCode = event.getNativeKeyCode();

			switch (keyCode) {
				case KeyCodes.KEY_TAB:

					if (!event.isAnyModifierKeyDown()) {
						int row = findFilterCriterionRow(COL_FILTER_VALUE,
							(Widget) event.getSource());

						if (row == filterCriteriaPanel.getRowCount() - 2) {
							addFilterRow(-1, ' ', null, false);
						}
					}

					break;
			}
		}
	}

	/**
	 * Handles keyboard input in the value field of a complex filter criterion.
	 *
	 * @param event The event that occurred.
	 */
	void handleComplexFilterKeyUp(KeyUpEvent event) {
		if (table.isEnabled() && !table.isBusy()) {
			int keyCode = event.getNativeKeyCode();

			switch (keyCode) {
				case KeyCodes.KEY_ENTER:
					hideComplexFilterPopup(true);

					break;

				case KeyCodes.KEY_ESCAPE:
					hideComplexFilterPopup(false);

					break;
			}
		}
	}

	/**
	 * Removes all filter constraints from the data model.
	 */
	void removeFilter() {
		getSearchableModel().removeAllFilters();
		resetInputValue(filterInput);

		updateTable();
	}

	/**
	 * Removes all filter columns.
	 */
	void resetFilterColumns() {
		filterColumns.clear();
	}

	/**
	 * Updates this filter panel from a data model.
	 *
	 * @param model The model to read the filter constraints from
	 */
	void update(FilterableDataModel<?> model) {
		String simpleFilter = searchSimpleFilter(model);
		boolean hasConstraints = model.getFilters().size() != 0;
		boolean enableSimpleFilter = (simpleFilter != null || !hasConstraints);

		filterInput.setEnabled(enableSimpleFilter);
		filterInput.setText(simpleFilter);

		if (enableSimpleFilter) {
			filterInput.removeStyleDependentName("disabled");
		} else {
			filterInput.addStyleDependentName("disabled");
			setFilterButtonActive(hasConstraints);
		}
	}

	/**
	 * Adds the rows of existing filter criteria to the complex filter panel.
	 *
	 * @param constraints The mapping from column IDs to raw filter criteria
	 */
	private void addComplexFilterCriteriaRows(Map<String, String> constraints) {
		for (Entry<String, String> constraint : constraints.entrySet()) {
			int column = getColumnIndex(constraint.getKey());

			String columnConstraint = constraint.getValue();

			String[] elements = columnConstraint.split(CONSTRAINT_SEPARATOR);

			for (String constraintElement : elements) {
				if (constraintElement.length() > 2) {
					boolean or =
						constraintElement.charAt(0) == CONSTRAINT_OR_PREFIX;

					char comparison = constraintElement.charAt(1);

					constraintElement = constraintElement
						.substring(2)
						.replaceAll(CONSTRAINT_SEPARATOR_ESCAPE,
							CONSTRAINT_SEPARATOR);
					addFilterRow(column, comparison, constraintElement, or);
				}
			}
		}
	}

	/**
	 * Adds the widgets for a certain filter criterion to the filter criteria
	 * panel.
	 *
	 * @param columnIndex The index of the column to be filtered or -1 for the
	 *                    default
	 * @param comparison  The comparison character for the new row or ' '
	 *                    (space) for the default
	 * @param filterValue The current filter value or NULL for the default
	 * @param orTerm      TRUE for OR, FALSE for AND
	 */
	private void addFilterRow(int columnIndex, char comparison,
		String filterValue, boolean orTerm) {
		ListBox joinList = null;
		int row = filterCriteriaPanel.getRowCount() - 1;
		boolean additionalRow = row != 0;

		filterCriteriaPanel.insertRow(row);

		if (columnIndex < 0) {
			columnIndex = 0;
		}

		if (additionalRow) {
			joinList = createFilterRowJoinList(orTerm);
			filterCriteriaPanel.setWidget(row, COL_FILTER_JOIN, joinList);
		}

		ListBox columnList = createFilterRowColumnList(columnIndex);
		ColumnDefinition column = filterColumns.get(columnIndex);

		Widget valueInput = createInputWidget(column, filterValue);
		ListBox comparisons = createComparisonsWidget(comparison);

		valueInput.addStyleName(GwtTable.CSS.ewtTableFilterValue());
		columnList.addKeyUpHandler(getComplexFilterKeyUpHandler());

		filterCriteriaPanel.setWidget(row, COL_FILTER_COLUMN, columnList);
		filterCriteriaPanel.setWidget(row, COL_FILTER_COMPARE, comparisons);
		filterCriteriaPanel.setWidget(row, COL_FILTER_VALUE, valueInput);

		if (valueInput instanceof ListBox) {
			((ListBox) valueInput).addKeyDownHandler(
				getComplexFilterKeyDownHandler());
			((ListBox) valueInput).addKeyUpHandler(
				getComplexFilterKeyUpHandler());
		}

		if (additionalRow) {
			filterCriteriaPanel.setWidget(row, COL_FILTER_BUTTON,
				createEditFilterListButton(false));
		}

		if (joinList != null) {
			joinList.setFocus(true);
		} else {
			columnList.setFocus(true);
		}
	}

	/**
	 * Adds the key event handlers to a widget.
	 *
	 * @param widget The widget to add the handlers to
	 */
	private void addKeyHandlers(FocusWidget widget) {
		widget.addKeyDownHandler(getComplexFilterKeyDownHandler());
		widget.addKeyUpHandler(getComplexFilterKeyUpHandler());
	}

	/**
	 * Applies the complex filter criteria that have been entered into the
	 * filter popup.
	 */
	private void applyComplexFilter() {
		FilterableDataModel<?> model = getSearchableModel();

		int filterRows = filterCriteriaPanel.getRowCount() - 1;
		boolean hasCriteria = false;

		model.removeAllFilters();

		for (int row = 0; row < filterRows; row++) {
			Widget widget =
				filterCriteriaPanel.getWidget(row, COL_FILTER_VALUE);

			ColumnDefinition column = getSelectedFilterColumn(row);
			String columnId = column.getId();
			boolean orTerm = isOrTerm(row);

			String filter = getFilterConstraint(column, widget);

			if (filter.length() > 0) {
				String currentFilter = model.getFilter(columnId);

				char prefix =
					orTerm ? CONSTRAINT_OR_PREFIX : CONSTRAINT_AND_PREFIX;

				filter = prefix + getSelectedFilterComparison(row) +
					filter.replaceAll(CONSTRAINT_SEPARATOR,
						CONSTRAINT_SEPARATOR_ESCAPE);

				if (currentFilter != null && currentFilter.length() > 0) {
					filter = currentFilter + CONSTRAINT_SEPARATOR + filter;
				}

				model.setFilter(columnId, filter);
				hasCriteria = true;
			}
		}

		if (hasCriteria) {
			updateTable();
		} else {
			resetFilter();
		}
	}

	/**
	 * Returns a new {@link CheckBox} widget for a certain column.
	 *
	 * @param initialValue The initial value or NULL for none
	 * @return The new check box
	 */
	@SuppressWarnings("boxing")
	private CheckBox createCheckBox(String initialValue) {
		final CheckBox checkBox = new CheckBox();

		if (initialValue != null && initialValue.length() > 0 &&
			!NULL_CONSTRAINT_VALUE.equals(initialValue)) {
			try {
				checkBox.setValue(Boolean.parseBoolean(initialValue));
			} catch (Exception e) {
				// ignore and leave at default value
			}
		}

		addKeyHandlers(checkBox);

		return checkBox;
	}

	/**
	 * Creates a widget that contains the comparisons for a table filter.
	 *
	 * @param initialValue The initial character to select or -1 for none
	 * @return The new widget
	 */
	private ListBox createComparisonsWidget(char initialValue) {
		ListBox comparisons = new ListBox();
		int chars = CONSTRAINT_COMPARISON_CHARS.length();
		int selection = 0;

		if (initialValue != ' ') {
			selection = CONSTRAINT_COMPARISON_CHARS.indexOf(initialValue);
		}

		comparisons.setTitle(expand("$ttTableFilterComparison"));
		comparisons.setVisibleItemCount(1);

		for (int i = 0; i < chars; i++) {
			comparisons.addItem("" + CONSTRAINT_COMPARISON_CHARS.charAt(i));
		}

		comparisons.setSelectedIndex(selection);
		comparisons.addKeyUpHandler(getComplexFilterKeyUpHandler());

		return comparisons;
	}

	/**
	 * Returns a new {@link DateBox} widget for a certain column.
	 *
	 * @param initialValue The initial value or NULL for none
	 * @return The new date box
	 */
	private DateBox createDateBox(String initialValue) {
		DateTimeFormat displayFormat =
			DateTimeFormat.getFormat(PredefinedFormat.DATE_MEDIUM);

		final DateBox dateBox = new DateBox(new DatePicker(), null,
			new DefaultFormat(displayFormat));

		if (initialValue != null && initialValue.length() > 0 &&
			!NULL_CONSTRAINT_VALUE.equals(initialValue)) {
			try {
				dateBox.setValue(FILTER_DATE_FORMAT.parse(initialValue));
			} catch (Exception e) {
				dateBox.getTextBox().setText("ERR: invalid date");
			}
		}

		addKeyHandlers(dateBox.getTextBox());

		return dateBox;
	}

	/**
	 * Creates and initializes the button at the end of filter criteria rows.
	 *
	 * @param add TRUE for an add button, FALSE for a remove button
	 * @return The new button
	 */
	private PushButton createEditFilterListButton(final boolean add) {
		PushButton button = new PushButton(
			new Image(add ? GwtTable.RES.imAdd() : GwtTable.RES.imCancel()));

		button.setTitle(
			expand(add ? "$ttTableFilterAdd" : "$ttTableFilterRemove"));

		button.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				handleFilterListChangeButton(event, add);
			}
		});

		return button;
	}

	/**
	 * Creates the button panel for the filter popup.
	 *
	 * @return The panel containing the buttons
	 */
	private Panel createFilterPopupButtonPanel() {
		HorizontalPanel buttonPanel = new HorizontalPanel();
		PushButton applyButton =
			new PushButton(new Image(GwtTable.RES.imOk()));
		PushButton cancelButton =
			new PushButton(new Image(GwtTable.RES.imCancel()));

		applyButton.setTitle(expand("$ttTableFilterApply"));
		cancelButton.setTitle(expand("$ttTableFilterCancel"));
		buttonPanel.add(applyButton);
		buttonPanel.add(cancelButton);

		applyButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				hideComplexFilterPopup(true);
			}
		});

		cancelButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				hideComplexFilterPopup(false);
			}
		});

		return buttonPanel;
	}

	/**
	 * Creates a listbox to select a model column for filtering.
	 *
	 * @param selection The initially selected row in the listbox
	 * @return The new listbox
	 */
	private ListBox createFilterRowColumnList(int selection) {
		ListBox columnList = new ListBox();

		for (ColumnDefinition column : filterColumns) {
			String title = column.getTitle();
			String col = expand(title);

			columnList.addItem(col);
		}

		int columns = columnList.getItemCount();

		if (columns > 0) {
			columnList.setSelectedIndex(selection % columns);
		}

		columnList.addChangeHandler(this);

		return columnList;
	}

	/**
	 * Creates a list box containing the possible joins for filter criteria
	 * rows.
	 *
	 * @param or TRUE to preselect the OR instead of AND
	 * @return The new list box
	 */
	private ListBox createFilterRowJoinList(boolean or) {
		ListBox joinList = new ListBox();

		joinList.addItem(expand("$itmTableFilterJoinAnd"));
		joinList.addItem(expand("$itmTableFilterJoinOr"));

		if (or) {
			joinList.setSelectedIndex(1);
		}

		return joinList;
	}

	/**
	 * Returns a new {@link ListBox} widget for a column with a set of allowed
	 * values.
	 *
	 * @param column       The column definition
	 * @param values       The allowed values for the column
	 * @param initialValue The initially selected value or NULL for none
	 * @return The new list box
	 */
	private ListBox createFilterValueListBox(ColumnDefinition column,
		String[] values, String initialValue) {
		ListBox listBox = new ListBox();
		List<String> constraints = new ArrayList<>();

		String prefix = column.getProperty(VALUE_RESOURCE_PREFIX, "");

		List<String> valueList = Arrays.asList(values);

		for (String value : valueList) {
			constraints.add(value);

			if (!value.startsWith("$")) {
				value = prefix + TextConvert.capitalizedIdentifier(value);
			}

			listBox.addItem(expand(value));
		}

		if (initialValue != null) {
			int index = valueList.indexOf(initialValue);

			if (index >= 0) {
				listBox.setSelectedIndex(index);
			}
		}

		addKeyHandlers(listBox);

		return listBox;
	}

	/**
	 * Returns a new input widget for a certain column.
	 *
	 * @param column       The column definition
	 * @param initialValue The initial value for the widget or NULL for none
	 * @return The new input widget
	 */
	private Widget createInputWidget(ColumnDefinition column,
		String initialValue) {
		String[] allowedValues = getAllowedValues(column);
		String datatype = column.getDatatype();
		Widget widget;

		if (allowedValues != null) {
			widget =
				createFilterValueListBox(column, allowedValues, initialValue);
		} else if (Date.class.getSimpleName().equals(datatype)) {
			widget = createDateBox(initialValue);
		} else if (Boolean.class.getSimpleName().equals(datatype)) {
			widget = createCheckBox(initialValue);
		} else {
			widget = createTextBox(column, initialValue);
		}

		return widget;
	}

	/**
	 * Returns a new {@link TextBox} widget for a certain column.
	 *
	 * @param column       The column definition
	 * @param initialValue The initial value or NULL for none
	 * @return The new text box
	 */
	private TextBox createTextBox(ColumnDefinition column,
		String initialValue) {
		TextBox textBox = new TextBox();
		String constraint = column.getProperty(INPUT_CONSTRAINT, null);

		textBox.setValue(initialValue);

		if (constraint != null) {
			textBox.addKeyPressHandler(new RegExConstraint(constraint));
		}

		addKeyHandlers(textBox);

		return textBox;
	}

	/**
	 * Internal helper method to return the expanded string for a certain
	 * resource key.
	 *
	 * @param resource The resource to expand
	 * @return The string for the resource
	 */
	private String expand(String resource) {
		return table.getContext().expandResource(resource);
	}

	/**
	 * Searches for the row in the filter criteria panel that contains a
	 * certain
	 * widget in a particular column.
	 *
	 * @param column The column
	 * @param widget The widget
	 * @return The row containing the widget or -1 if not found
	 */
	private int findFilterCriterionRow(int column, Widget widget) {
		int rowCount = filterCriteriaPanel.getRowCount();
		int row = 0;
		boolean found = false;

		while (!found && row < rowCount) {
			if (column < filterCriteriaPanel.getCellCount(row)) {
				Widget rowWidget = filterCriteriaPanel.getWidget(row, column);

				if (rowWidget instanceof DateBox) {
					rowWidget = ((DateBox) rowWidget).getTextBox();
				}

				found = (rowWidget == widget);
			}

			if (!found) {
				row++;
			}
		}

		return row < rowCount ? row : -1;
	}

	/**
	 * Returns the allowed string values for a table column. The returned array
	 * will be NULL if no value constraint is set on the column.
	 *
	 * @param column The column definition
	 * @return An array containing the allowed values or NULL for none
	 */
	private String[] getAllowedValues(ColumnDefinition column) {
		String[] allowedValues = null;
		String values = column.getProperty(ALLOWED_VALUES, null);

		if (values != null) {
			allowedValues = values.split(",");
		}

		return allowedValues;
	}

	/**
	 * Returns the index of the column with a certain ID.
	 *
	 * @param columnId The column ID to search the index of
	 * @return The column index or -1 if not found
	 */
	private int getColumnIndex(String columnId) {
		int column = filterColumns.size() - 1;

		while (column >= 0 &&
			!filterColumns.get(column).getId().equals(columnId)) {
			column--;
		}

		return column;
	}

	/**
	 * Initializes and returns the key up event handler for the complex filter
	 * panel.
	 *
	 * @return The key up handler
	 */
	private KeyDownHandler getComplexFilterKeyDownHandler() {
		if (complexFilterKeyDownHandler == null) {
			complexFilterKeyDownHandler = new KeyDownHandler() {
				@Override
				public void onKeyDown(KeyDownEvent event) {
					handleComplexFilterKeyDown(event);
				}
			};
		}

		return complexFilterKeyDownHandler;
	}

	/**
	 * Initializes and returns the key up event handler for the complex filter
	 * panel.
	 *
	 * @return The key up handler
	 */
	private KeyUpHandler getComplexFilterKeyUpHandler() {
		if (complexFilterKeyUpHandler == null) {
			complexFilterKeyUpHandler = new KeyUpHandler() {
				@Override
				public void onKeyUp(KeyUpEvent event) {
					handleComplexFilterKeyUp(event);
				}
			};
		}

		return complexFilterKeyUpHandler;
	}

	/**
	 * Returns the constraint value for a certain filter value input.
	 *
	 * @param column      The column to return the constraint for
	 * @param valueWidget The widget to read the value from
	 * @return The constraint value string
	 */
	private String getFilterConstraint(ColumnDefinition column,
		Widget valueWidget) {
		String value = "";

		if (valueWidget instanceof TextBox) {
			value = ((TextBox) valueWidget).getText();
		} else if (valueWidget instanceof CheckBox) {
			value = ((CheckBox) valueWidget).getValue().toString();
		} else if (valueWidget instanceof DateBox) {
			Date date = ((DateBox) valueWidget).getValue();

			if (date != null) {
				value = FILTER_DATE_FORMAT.format(date);
			} else {
				value = NULL_CONSTRAINT_VALUE;
			}
		} else if (valueWidget instanceof ListBox) {
			ListBox listBox = (ListBox) valueWidget;
			int constraint = listBox.getSelectedIndex();

			if (constraint >= 0) {
				value = getAllowedValues(column)[constraint];
			}
		}

		return value;
	}

	/**
	 * Returns the searchable data model of this panel's table.
	 *
	 * @return The searchable model of the table
	 */
	private FilterableDataModel<?> getSearchableModel() {
		return (FilterableDataModel<?>) table.getData();
	}

	/**
	 * Returns the currently selected column in a certain row of the complex
	 * filter popup.
	 *
	 * @param row The row to return the column for
	 * @return The column definition for the given row
	 */
	private ColumnDefinition getSelectedFilterColumn(int row) {
		ListBox columnList =
			(ListBox) filterCriteriaPanel.getWidget(row, COL_FILTER_COLUMN);

		ColumnDefinition column =
			filterColumns.get(columnList.getSelectedIndex());

		return column;
	}

	/**
	 * Returns the currently selected comparison in a certain row of the
	 * complex
	 * filter popup.
	 *
	 * @param row The row to return the comparison for
	 * @return The comparison for the given row
	 */
	private String getSelectedFilterComparison(int row) {
		ListBox comparisons =
			(ListBox) filterCriteriaPanel.getWidget(row, COL_FILTER_COMPARE);
		String comparison =
			comparisons.getItemText(comparisons.getSelectedIndex());

		return comparison;
	}

	/**
	 * Handles clicks on the complex filter button.
	 */
	private void handleComplexFilterButton() {
		Map<String, String> constraints = getSearchableModel().getFilters();

		filterPopup = new DecoratedPopupPanel(true, true);
		filterCriteriaPanel = new FlexTable();

		filterPopup.addStyleName(GwtTable.CSS.ewtTableFilterPopup());

		Panel buttonPanel = createFilterPopupButtonPanel();
		PushButton addFilterButton = createEditFilterListButton(true);
		int buttonRow = 0;

		FlexCellFormatter cellFormatter =
			filterCriteriaPanel.getFlexCellFormatter();

		filterCriteriaPanel.setWidget(buttonRow, 0, buttonPanel);
		filterCriteriaPanel.setWidget(buttonRow, 1, addFilterButton);
		cellFormatter.setColSpan(buttonRow, 0, COL_FILTER_BUTTON);

		initComplexFilterInput(constraints);

		filterPopup.setAnimationEnabled(true);
		filterPopup.setGlassEnabled(true);
		filterPopup.setWidget(filterCriteriaPanel);
		filterPopup.addCloseHandler(new CloseHandler<PopupPanel>() {
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				if (event.isAutoClosed()) {
					hideComplexFilterPopup(false);
				}
			}
		});

		filterPopup.setPopupPositionAndShow(new PositionCallback() {
			@Override
			public void setPosition(int width, int height) {
				updateFilterPopupPosition(height);
			}
		});

		setFocus(filterCriteriaPanel.getWidget(0, COL_FILTER_COLUMN));
		setFilterButtonActive(true);
	}

	/**
	 * Handles clicks on a button that manipulates the filter criteria list.
	 *
	 * @param event The click event that occurred
	 * @param add   TRUE to add a row, FALSE to remove the row of the button
	 */
	private void handleFilterListChangeButton(ClickEvent event, boolean add) {
		if (add) {
			addFilterRow(-1, ' ', null, false);
		} else {
			int row = findFilterCriterionRow(COL_FILTER_BUTTON,
				(Widget) event.getSource());

			filterCriteriaPanel.removeRow(row);
		}

		updateFilterPopupPosition(filterPopup.getOffsetHeight());
	}

	/**
	 * Hides the complex filter popup panel and optionally applies the filter
	 * criteria.
	 *
	 * @param apply TRUE to apply the filter criteria, FALSE to discard them
	 */
	private void hideComplexFilterPopup(boolean apply) {
		if (filterPopup != null) {
			filterPopup.hide();
		}

		if (apply) {
			applyComplexFilter();
		} else if (getSearchableModel().getFilters().isEmpty()) {
			resetFilter();
		}
	}

	/**
	 * Initializes the input components for complex filter rows.
	 *
	 * @param constraints The constraints to initialize the filter rows from
	 *                    (empty for none)
	 */
	private void initComplexFilterInput(Map<String, String> constraints) {
		if (filterButton.isDown() || constraints.isEmpty()) {
			filterInput.setText("");
			filterInput.setEnabled(false);
			filterInput.addStyleDependentName("disabled");
			addFilterRow(-1, ' ', null, false);
		} else {
			addComplexFilterCriteriaRows(constraints);
		}
	}

	/**
	 * Checks whether the selected join in a complex filter row is a logical OR
	 * or AND term.
	 *
	 * @param row The complex filter row
	 * @return TRUE for an OR term
	 */
	private boolean isOrTerm(int row) {
		boolean orTerm = false;
		ListBox joins =
			(ListBox) filterCriteriaPanel.getWidget(row, COL_FILTER_JOIN);

		if (joins != null) {
			orTerm = (joins.getSelectedIndex() == 1);
		}

		return orTerm;
	}

	/**
	 * Resets all current filter criteria and the enables the generic filter
	 * input field.
	 */
	private void resetFilter() {
		table.setSelection(-1);
		filterInput.setText(null);
		filterInput.setEnabled(true);
		filterInput.removeStyleDependentName("disabled");
		setFilterButtonActive(false);
		removeFilter();
	}

	/**
	 * Resets the input value of a certain widget.
	 *
	 * @param widget The widget to reset the value of
	 */
	private void resetInputValue(Widget widget) {
		if (widget instanceof HasValue) {
			((HasValue<?>) widget).setValue(null);
		} else if (widget instanceof ListBox) {
			((ListBox) widget).setSelectedIndex(0);
		}
	}

	/**
	 * Analyzes the constraints of a {@link FilterableDataModel} to check
	 * whether they represent a single search term for all searchable columns.
	 *
	 * @param dataModel The data model to get the filter criteria from
	 * @return The single filter criterion or NULL for none
	 */
	private String searchSimpleFilter(DataModel<?> dataModel) {
		String filter = null;

		if (dataModel instanceof FilterableDataModel) {
			FilterableDataModel<?> searchableModel =
				(FilterableDataModel<?>) dataModel;

			for (String criterion : searchableModel.getFilters().values()) {
				if (criterion.endsWith("*")) {
					criterion = criterion.substring(0, criterion.length() - 1);
				}

				if (filter == null) {
					filter = criterion;
				} else if (!filter.equals(criterion)) {
					filter = null;

					break;
				}
			}

			if (filter != null) {
				// a common filter criterion must always begin with the OR
				// prefix and the comparison character
				if (filter.length() >= 2 &&
					filter.charAt(0) == CONSTRAINT_OR_PREFIX) {
					filter = filter.substring(2);
				} else {
					filter = null;
				}
			}
		}

		return filter;
	}

	/**
	 * Sets the filter button active to indicate that filtering is used.
	 *
	 * @param active TRUE to activate
	 */
	private void setFilterButtonActive(boolean active) {
		filterButton.setDown(active);

		if (active) {
			filterButton.addStyleDependentName("active");
		} else {
			filterButton.removeStyleDependentName("active");
		}
	}

	/**
	 * Sets the focus to a certain widget.
	 *
	 * @param widget The widget to set the focus on
	 */
	private void setFocus(Widget widget) {
		if (widget instanceof Focusable) {
			((Focusable) widget).setFocus(true);
		} else if (widget instanceof DateBox) {
			((DateBox) widget).setFocus(true);
		}
	}

	/**
	 * Adjusts the position of the filter popup panel.
	 *
	 * @param popupHeight The current height of the popup panel
	 */
	private void updateFilterPopupPosition(int popupHeight) {
		filterPopup.setPopupPosition(getAbsoluteLeft(),
			getAbsoluteTop() - popupHeight);
	}

	/**
	 * Updates the table after a filter change.
	 */
	private void updateTable() {
		table.collapseAllNodes();
		table.setFirstRow(0);
		table.update();
	}
}
