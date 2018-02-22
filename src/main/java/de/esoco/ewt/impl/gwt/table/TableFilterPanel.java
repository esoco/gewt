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

import static de.esoco.lib.model.FilterableDataModel.CONSTRAINT_AND_PREFIX;
import static de.esoco.lib.model.FilterableDataModel.CONSTRAINT_COMPARISON_CHARS;
import static de.esoco.lib.model.FilterableDataModel.CONSTRAINT_OR_PREFIX;
import static de.esoco.lib.model.FilterableDataModel.CONSTRAINT_SEPARATOR;
import static de.esoco.lib.model.FilterableDataModel.CONSTRAINT_SEPARATOR_ESCAPE;
import static de.esoco.lib.model.FilterableDataModel.NULL_CONSTRAINT_VALUE;
import static de.esoco.lib.property.ContentProperties.ALLOWED_VALUES;
import static de.esoco.lib.property.ContentProperties.INPUT_CONSTRAINT;
import static de.esoco.lib.property.ContentProperties.VALUE_RESOURCE_PREFIX;


/********************************************************************
 * A panel that contains the filter functionality for a table.
 *
 * @author eso
 */
class TableFilterPanel extends Composite implements ClickHandler, KeyUpHandler,
													ChangeHandler
{
	//~ Static fields/initializers ---------------------------------------------

	private static final int COL_FILTER_JOIN    = 0;
	private static final int COL_FILTER_COLUMN  = COL_FILTER_JOIN + 1;
	private static final int COL_FILTER_COMPARE = COL_FILTER_COLUMN + 1;
	private static final int COL_FILTER_VALUE   = COL_FILTER_COMPARE + 1;
	private static final int COL_FILTER_BUTTON  = COL_FILTER_VALUE + 1;

	private static final DateTimeFormat FILTER_DATE_FORMAT =
		DateTimeFormat.getFormat(FilterableDataModel.CONSTRAINT_DATE_FORMAT_PATTERN);

	//~ Instance fields --------------------------------------------------------

	private final GwtTable rTable;

	private FlexTable aFilterPanel = new FlexTable();
	private TextBox   aFilterInput = new TextBox();

	private ToggleButton aFilterButton	    =
		new ToggleButton(new Image(GwtTable.RES.imFilter()));
	private PushButton   aClearFilterButton =
		new PushButton(new Image(GwtTable.RES.imCancel()));

	private List<ColumnDefinition> aFilterColumns =
		new ArrayList<ColumnDefinition>();

	private PopupPanel aFilterPopup;
	private FlexTable  aFilterCriteriaPanel;
	private Timer	   aFilterInputTimer;

	private KeyDownHandler aComplexFilterKeyDownHandler = null;
	private KeyUpHandler   aComplexFilterKeyUpHandler   = null;

	//~ Constructors -----------------------------------------------------------

	/***************************************
	 * Creates a new instance.
	 *
	 * @param rTable The {@link GwtTable} this instance belongs to
	 */
	TableFilterPanel(GwtTable rTable)
	{
		this.rTable = rTable;

		aClearFilterButton.setTitle(expand("$ttClearTableFilter"));
		aFilterInput.setTitle(expand("$ttTableFilterValue"));

		aFilterPanel.setWidget(0, 0, aFilterButton);
		aFilterPanel.setWidget(0, 1, aFilterInput);
		aFilterPanel.setWidget(0, 2, aClearFilterButton);

		aFilterInput.setWidth("100%");
		aFilterInput.setStylePrimaryName(GwtTable.CSS.ewtTableFilterValue());
		aFilterInput.addKeyUpHandler(this);

		aFilterPanel.setWidth("100%");
		aFilterPanel.getCellFormatter().setWidth(0, 1, "100%");
		aFilterPanel.setStylePrimaryName(GwtTable.CSS.ewtFilter());

		aFilterButton.setStylePrimaryName(GwtTable.CSS.ewtTableFilterButton());
		aFilterButton.addClickHandler(this);
		aClearFilterButton.addClickHandler(this);

		initWidget(aFilterPanel);
	}

	//~ Methods ----------------------------------------------------------------

	/***************************************
	 * Processes filter popup column change events.
	 *
	 * @see ChangeHandler#onChange(ChangeEvent)
	 */
	@Override
	public void onChange(ChangeEvent rEvent)
	{
		int nRow =
			findFilterCriterionRow(COL_FILTER_COLUMN,
								   (Widget) rEvent.getSource());

		ColumnDefinition rColumn = getSelectedFilterColumn(nRow);

		aFilterCriteriaPanel.setWidget(nRow,
									   COL_FILTER_VALUE,
									   createInputWidget(rColumn, null));
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

			if (rSource == aFilterButton)
			{
				handleComplexFilterButton();
			}
			else if (rSource == aClearFilterButton)
			{
				resetFilter();
			}
		}
	}

	/***************************************
	 * Event handling for keyboard input in the filter text box.
	 *
	 * @see KeyUpHandler#onKeyUp(KeyUpEvent)
	 */
	@Override
	public void onKeyUp(KeyUpEvent rEvent)
	{
		if (rTable.isEnabled())
		{
			int nKeyCode = rEvent.getNativeKeyCode();

			switch (nKeyCode)
			{
				case KeyCodes.KEY_ESCAPE:
					resetInputValue(aFilterInput);

					break;

				default:

					if (aFilterInputTimer == null)
					{
						aFilterInputTimer =
							new Timer()
							{
								@Override
								public void run()
								{
									applyGlobalFilter(aFilterInput.getText());
								}
							};
					}

					aFilterInputTimer.schedule(500);
			}
		}
	}

	/***************************************
	 * Sets the enabled state of the widgets in this panel.
	 *
	 * @param bEnabled The new enabled state
	 */
	public void setEnabled(boolean bEnabled)
	{
		aFilterInput.setEnabled(bEnabled);
		aFilterButton.setEnabled(bEnabled);
		aClearFilterButton.setEnabled(bEnabled);
	}

	/***************************************
	 * Sets the focus to the filter input field.
	 *
	 * @param bFocused The focused state to set
	 */
	public void setFocus(boolean bFocused)
	{
		aFilterInput.setFocus(bFocused);
	}

	/***************************************
	 * Adds a filter column to this panel.
	 *
	 * @param rColumn The definition of the filter column
	 */
	void addFilterColumn(ColumnDefinition rColumn)
	{
		aFilterColumns.add(rColumn);
	}

	/***************************************
	 * Sets the global filter constraint for all filterable columns of the data
	 * model.
	 *
	 * @param sConstraint The constraint to set
	 */
	void applyGlobalFilter(String sConstraint)
	{
		FilterableDataModel<?> rModel			 = getSearchableModel();
		String				   sNumberConstraint = null;

		sConstraint = sConstraint.trim();

		if (sConstraint.length() == 0)
		{
			sConstraint = null;
		}
		else
		{
			boolean bHasComparison =
				CONSTRAINT_COMPARISON_CHARS.indexOf(sConstraint.charAt(0)) >= 0;

			if (!bHasComparison)
			{
				if (sConstraint.indexOf('*') == -1)
				{
					try
					{
						Integer.parseInt(sConstraint);
						sNumberConstraint =
							CONSTRAINT_OR_PREFIX + "=" + sConstraint;
					}
					catch (Exception e)
					{
						// leave number constraint as null
					}

					sConstraint = sConstraint + "*";
				}

				sConstraint = "=" + sConstraint;
			}

			sConstraint = CONSTRAINT_OR_PREFIX + sConstraint;
		}

		for (ColumnDefinition rColumn : aFilterColumns)
		{
			String sColumnId = rColumn.getId();
			String sDatatype = rColumn.getDatatype();

			if (String.class.getSimpleName().equals(sDatatype))
			{
				rModel.setFilter(sColumnId, sConstraint);
			}
			else if (Integer.class.getSimpleName().equals(sDatatype))
			{
				rModel.setFilter(sColumnId, sNumberConstraint);
			}
		}

		updateTable();
		rTable.setSelection(-1);
	}

	/***************************************
	 * Handles keyboard input in the value field of a complex filter criterion.
	 *
	 * @param rEvent The event that occurred.
	 */
	void handleComplexFilterKeyDown(KeyDownEvent rEvent)
	{
		if (rTable.isEnabled() && !rTable.isBusy())
		{
			int nKeyCode = rEvent.getNativeKeyCode();

			switch (nKeyCode)
			{
				case KeyCodes.KEY_TAB:

					if (!rEvent.isAnyModifierKeyDown())
					{
						int nRow =
							findFilterCriterionRow(COL_FILTER_VALUE,
												   (Widget) rEvent.getSource());

						if (nRow == aFilterCriteriaPanel.getRowCount() - 2)
						{
							addFilterRow(-1, ' ', null, false);
						}
					}

					break;
			}
		}
	}

	/***************************************
	 * Handles keyboard input in the value field of a complex filter criterion.
	 *
	 * @param rEvent The event that occurred.
	 */
	void handleComplexFilterKeyUp(KeyUpEvent rEvent)
	{
		if (rTable.isEnabled() && !rTable.isBusy())
		{
			int nKeyCode = rEvent.getNativeKeyCode();

			switch (nKeyCode)
			{
				case KeyCodes.KEY_ENTER:
					hideComplexFilterPopup(true);

					break;

				case KeyCodes.KEY_ESCAPE:
					hideComplexFilterPopup(false);

					break;
			}
		}
	}

	/***************************************
	 * Removes all filter constraints from the data model.
	 */
	void removeFilter()
	{
		getSearchableModel().removeAllFilters();
		resetInputValue(aFilterInput);

		updateTable();
	}

	/***************************************
	 * Removes all filter columns.
	 */
	void resetFilterColumns()
	{
		aFilterColumns.clear();
	}

	/***************************************
	 * Updates this filter panel from a data model.
	 *
	 * @param rModel The model to read the filter constraints from
	 */
	void update(FilterableDataModel<?> rModel)
	{
		String  sSimpleFilter	    = searchSimpleFilter(rModel);
		boolean bHasConstraints     = rModel.getFilters().size() != 0;
		boolean bEnableSimpleFilter =
			(sSimpleFilter != null || !bHasConstraints);

		aFilterInput.setEnabled(bEnableSimpleFilter);
		aFilterInput.setText(sSimpleFilter);

		if (bEnableSimpleFilter)
		{
			aFilterInput.removeStyleDependentName("disabled");
		}
		else
		{
			aFilterInput.addStyleDependentName("disabled");
			setFilterButtonActive(bHasConstraints);
		}
	}

	/***************************************
	 * Adds the rows of existing filter criteria to the complex filter panel.
	 *
	 * @param rConstraints The mapping from column IDs to raw filter criteria
	 */
	private void addComplexFilterCriteriaRows(Map<String, String> rConstraints)
	{
		for (Entry<String, String> rColumnConstraint : rConstraints.entrySet())
		{
			int nColumn = getColumnIndex(rColumnConstraint.getKey());

			String sColumnConstraint = rColumnConstraint.getValue();

			String[] aValues = sColumnConstraint.split(CONSTRAINT_SEPARATOR);

			for (String sConstraint : aValues)
			{
				if (sConstraint.length() > 2)
				{
					boolean bOr = sConstraint.charAt(0) == CONSTRAINT_OR_PREFIX;

					char cComparison = sConstraint.charAt(1);

					sConstraint =
						sConstraint.substring(2)
								   .replaceAll(CONSTRAINT_SEPARATOR_ESCAPE,
											   CONSTRAINT_SEPARATOR);
					addFilterRow(nColumn, cComparison, sConstraint, bOr);
				}
			}
		}
	}

	/***************************************
	 * Adds the widgets for a certain filter criterion to the filter criteria
	 * panel.
	 *
	 * @param nColumnIndex The index of the column to be filtered or -1 for the
	 *                     default
	 * @param cComparison  The comparison character for the new row or ' '
	 *                     (space) for the default
	 * @param sFilterValue The current filter value or NULL for the default
	 * @param bOrTerm      TRUE for OR, FALSE for AND
	 */
	private void addFilterRow(int	  nColumnIndex,
							  char    cComparison,
							  String  sFilterValue,
							  boolean bOrTerm)
	{
		ListBox aJoinList	   = null;
		int     nRow		   = aFilterCriteriaPanel.getRowCount() - 1;
		boolean bAdditionalRow = nRow != 0;

		aFilterCriteriaPanel.insertRow(nRow);

		if (nColumnIndex < 0)
		{
			nColumnIndex = 0;
		}

		if (bAdditionalRow)
		{
			aJoinList = createFilterRowJoinList(bOrTerm);
			aFilterCriteriaPanel.setWidget(nRow, COL_FILTER_JOIN, aJoinList);
		}

		ListBox			 aColumnList = createFilterRowColumnList(nColumnIndex);
		ColumnDefinition rColumn     = aFilterColumns.get(nColumnIndex);

		Widget  rValueInput  = createInputWidget(rColumn, sFilterValue);
		ListBox rComparisons = createComparisonsWidget(cComparison);

		rValueInput.addStyleName(GwtTable.CSS.ewtTableFilterValue());
		aColumnList.addKeyUpHandler(getComplexFilterKeyUpHandler());

		aFilterCriteriaPanel.setWidget(nRow, COL_FILTER_COLUMN, aColumnList);
		aFilterCriteriaPanel.setWidget(nRow, COL_FILTER_COMPARE, rComparisons);
		aFilterCriteriaPanel.setWidget(nRow, COL_FILTER_VALUE, rValueInput);

		if (rValueInput instanceof ListBox)
		{
			((ListBox) rValueInput).addKeyDownHandler(getComplexFilterKeyDownHandler());
			((ListBox) rValueInput).addKeyUpHandler(getComplexFilterKeyUpHandler());
		}

		if (bAdditionalRow)
		{
			aFilterCriteriaPanel.setWidget(nRow,
										   COL_FILTER_BUTTON,
										   createEditFilterListButton(false));
		}

		if (aJoinList != null)
		{
			aJoinList.setFocus(true);
		}
		else
		{
			aColumnList.setFocus(true);
		}
	}

	/***************************************
	 * Adds the key event handlers to a widget.
	 *
	 * @param rWidget The widget to add the handlers to
	 */
	private void addKeyHandlers(FocusWidget rWidget)
	{
		rWidget.addKeyDownHandler(getComplexFilterKeyDownHandler());
		rWidget.addKeyUpHandler(getComplexFilterKeyUpHandler());
	}

	/***************************************
	 * Applies the complex filter criteria that have been entered into the
	 * filter popup.
	 */
	private void applyComplexFilter()
	{
		FilterableDataModel<?> rModel = getSearchableModel();

		int     nFilterRows  = aFilterCriteriaPanel.getRowCount() - 1;
		boolean bHasCriteria = false;

		rModel.removeAllFilters();

		for (int nRow = 0; nRow < nFilterRows; nRow++)
		{
			Widget rWidget =
				aFilterCriteriaPanel.getWidget(nRow, COL_FILTER_VALUE);

			ColumnDefinition rColumn   = getSelectedFilterColumn(nRow);
			String			 sColumnId = rColumn.getId();
			boolean			 bOrTerm   = isOrTerm(nRow);

			String sFilter = getFilterConstraint(rColumn, rWidget);

			if (sFilter.length() > 0)
			{
				String sCurrentFilter = rModel.getFilter(sColumnId);

				char cPrefix =
					bOrTerm ? CONSTRAINT_OR_PREFIX : CONSTRAINT_AND_PREFIX;

				sFilter =
					cPrefix + getSelectedFilterComparison(nRow) +
					sFilter.replaceAll(CONSTRAINT_SEPARATOR,
									   CONSTRAINT_SEPARATOR_ESCAPE);

				if (sCurrentFilter != null && sCurrentFilter.length() > 0)
				{
					sFilter = sCurrentFilter + CONSTRAINT_SEPARATOR + sFilter;
				}

				rModel.setFilter(sColumnId, sFilter);
				bHasCriteria = true;
			}
		}

		if (bHasCriteria)
		{
			updateTable();
		}
		else
		{
			resetFilter();
		}
	}

	/***************************************
	 * Returns a new {@link CheckBox} widget for a certain column.
	 *
	 * @param  sInitialValue The initial value or NULL for none
	 *
	 * @return The new check box
	 */
	@SuppressWarnings("boxing")
	private CheckBox createCheckBox(String sInitialValue)
	{
		final CheckBox aCheckBox = new CheckBox();

		if (sInitialValue != null &&
			sInitialValue.length() > 0 &&
			!NULL_CONSTRAINT_VALUE.equals(sInitialValue))
		{
			try
			{
				aCheckBox.setValue(Boolean.parseBoolean(sInitialValue));
			}
			catch (Exception e)
			{
				// ignore and leave at default value
			}
		}

		addKeyHandlers(aCheckBox);

		return aCheckBox;
	}

	/***************************************
	 * Creates a widget that contains the comparisons for a table filter.
	 *
	 * @param  cInitialValue The initial character to select or -1 for none
	 *
	 * @return The new widget
	 */
	private ListBox createComparisonsWidget(char cInitialValue)
	{
		ListBox aComparisons = new ListBox();
		int     nChars		 = CONSTRAINT_COMPARISON_CHARS.length();
		int     nSelection   = 0;

		if (cInitialValue != ' ')
		{
			nSelection = CONSTRAINT_COMPARISON_CHARS.indexOf(cInitialValue);
		}

		aComparisons.setTitle(expand("$ttTableFilterComparison"));
		aComparisons.setVisibleItemCount(1);

		for (int i = 0; i < nChars; i++)
		{
			aComparisons.addItem("" + CONSTRAINT_COMPARISON_CHARS.charAt(i));
		}

		aComparisons.setSelectedIndex(nSelection);
		aComparisons.addKeyUpHandler(getComplexFilterKeyUpHandler());

		return aComparisons;
	}

	/***************************************
	 * Returns a new {@link DateBox} widget for a certain column.
	 *
	 * @param  sInitialValue The initial value or NULL for none
	 *
	 * @return The new date box
	 */
	private DateBox createDateBox(String sInitialValue)
	{
		DateTimeFormat rDisplayFormat =
			DateTimeFormat.getFormat(PredefinedFormat.DATE_MEDIUM);

		final DateBox aDateBox =
			new DateBox(new DatePicker(),
						null,
						new DefaultFormat(rDisplayFormat));

		if (sInitialValue != null &&
			sInitialValue.length() > 0 &&
			!NULL_CONSTRAINT_VALUE.equals(sInitialValue))
		{
			try
			{
				aDateBox.setValue(FILTER_DATE_FORMAT.parse(sInitialValue));
			}
			catch (Exception e)
			{
				aDateBox.getTextBox().setText("ERR: invalid date");
			}
		}

		addKeyHandlers(aDateBox.getTextBox());

		return aDateBox;
	}

	/***************************************
	 * Creates and initializes the button at the end of filter criteria rows.
	 *
	 * @param  bAdd TRUE for an add button, FALSE for a remove button
	 *
	 * @return The new button
	 */
	private PushButton createEditFilterListButton(final boolean bAdd)
	{
		PushButton aButton =
			new PushButton(new Image(bAdd ? GwtTable.RES.imAdd()
										  : GwtTable.RES.imCancel()));

		aButton.setTitle(expand(bAdd ? "$ttTableFilterAdd"
									 : "$ttTableFilterRemove"));

		aButton.addClickHandler(new ClickHandler()
			{
				@Override
				public void onClick(ClickEvent rEvent)
				{
					handleFilterListChangeButton(rEvent, bAdd);
				}
			});

		return aButton;
	}

	/***************************************
	 * Creates the button panel for the filter popup.
	 *
	 * @return The panel containing the buttons
	 */
	private Panel createFilterPopupButtonPanel()
	{
		HorizontalPanel aButtonPanel  = new HorizontalPanel();
		PushButton	    aApplyButton  =
			new PushButton(new Image(GwtTable.RES.imOk()));
		PushButton	    aCancelButton =
			new PushButton(new Image(GwtTable.RES.imCancel()));

		aApplyButton.setTitle(expand("$ttTableFilterApply"));
		aCancelButton.setTitle(expand("$ttTableFilterCancel"));
		aButtonPanel.add(aApplyButton);
		aButtonPanel.add(aCancelButton);

		aApplyButton.addClickHandler(new ClickHandler()
			{
				@Override
				public void onClick(ClickEvent rEvent)
				{
					hideComplexFilterPopup(true);
				}
			});

		aCancelButton.addClickHandler(new ClickHandler()
			{
				@Override
				public void onClick(ClickEvent rEvent)
				{
					hideComplexFilterPopup(false);
				}
			});

		return aButtonPanel;
	}

	/***************************************
	 * Creates a listbox to select a model column for filtering.
	 *
	 * @param  nSelection The initially selected row in the listbox
	 *
	 * @return The new listbox
	 */
	private ListBox createFilterRowColumnList(int nSelection)
	{
		ListBox aColumnList = new ListBox();

		for (ColumnDefinition rColumn : aFilterColumns)
		{
			String sTitle  = rColumn.getTitle();
			String sColumn = expand(sTitle);

			aColumnList.addItem(sColumn);
		}

		int nColumns = aColumnList.getItemCount();

		if (nColumns > 0)
		{
			aColumnList.setSelectedIndex(nSelection % nColumns);
		}

		aColumnList.addChangeHandler(this);

		return aColumnList;
	}

	/***************************************
	 * Creates a list box containing the possible joins for filter criteria
	 * rows.
	 *
	 * @param  bOr TRUE to preselect the OR instead of AND
	 *
	 * @return The new list box
	 */
	private ListBox createFilterRowJoinList(boolean bOr)
	{
		ListBox aJoinList = new ListBox();

		aJoinList.addItem(expand("$itmTableFilterJoinAnd"));
		aJoinList.addItem(expand("$itmTableFilterJoinOr"));

		if (bOr)
		{
			aJoinList.setSelectedIndex(1);
		}

		return aJoinList;
	}

	/***************************************
	 * Returns a new {@link ListBox} widget for a column with a set of allowed
	 * values.
	 *
	 * @param  rColumn       The column definition
	 * @param  rValues       The allowed values for the column
	 * @param  sInitialValue The initially selected value or NULL for none
	 *
	 * @return The new list box
	 */
	private ListBox createFilterValueListBox(ColumnDefinition rColumn,
											 String[]		  rValues,
											 String			  sInitialValue)
	{
		ListBox		 aListBox     = new ListBox();
		List<String> aConstraints = new ArrayList<String>();

		String sPrefix = rColumn.getProperty(VALUE_RESOURCE_PREFIX, "");

		List<String> aValues = Arrays.asList(rValues);

		for (String sValue : aValues)
		{
			aConstraints.add(sValue);

			if (!sValue.startsWith("$"))
			{
				sValue = sPrefix + TextConvert.capitalizedIdentifier(sValue);
			}

			aListBox.addItem(expand(sValue));
		}

		if (sInitialValue != null)
		{
			int nIndex = aValues.indexOf(sInitialValue);

			if (nIndex >= 0)
			{
				aListBox.setSelectedIndex(nIndex);
			}
		}

		addKeyHandlers(aListBox);

		return aListBox;
	}

	/***************************************
	 * Returns a new input widget for a certain column.
	 *
	 * @param  rColumn       The column definition
	 * @param  sInitialValue The initial value for the widget or NULL for none
	 *
	 * @return The new input widget
	 */
	private Widget createInputWidget(
		ColumnDefinition rColumn,
		String			 sInitialValue)
	{
		String[] rAllowedValues = getAllowedValues(rColumn);
		String   sDatatype	    = rColumn.getDatatype();
		Widget   rWidget;

		if (rAllowedValues != null)
		{
			rWidget =
				createFilterValueListBox(rColumn,
										 rAllowedValues,
										 sInitialValue);
		}
		else if (Date.class.getSimpleName().equals(sDatatype))
		{
			rWidget = createDateBox(sInitialValue);
		}
		else if (Boolean.class.getSimpleName().equals(sDatatype))
		{
			rWidget = createCheckBox(sInitialValue);
		}
		else
		{
			rWidget = createTextBox(rColumn, sInitialValue);
		}

		return rWidget;
	}

	/***************************************
	 * Returns a new {@link TextBox} widget for a certain column.
	 *
	 * @param  rColumn       The column definition
	 * @param  sInitialValue The initial value or NULL for none
	 *
	 * @return The new text box
	 */
	private TextBox createTextBox(
		ColumnDefinition rColumn,
		String			 sInitialValue)
	{
		TextBox aTextBox    = new TextBox();
		String  sConstraint = rColumn.getProperty(INPUT_CONSTRAINT, null);

		aTextBox.setValue(sInitialValue);

		if (sConstraint != null)
		{
			aTextBox.addKeyPressHandler(new RegExConstraint(sConstraint));
		}

		addKeyHandlers(aTextBox);

		return aTextBox;
	}

	/***************************************
	 * Internal helper method to return the expanded string for a certain
	 * resource key.
	 *
	 * @param  sResource The resource to expand
	 *
	 * @return The string for the resource
	 */
	private String expand(String sResource)
	{
		return rTable.getContext().expandResource(sResource);
	}

	/***************************************
	 * Searches for the row in the filter criteria panel that contains a certain
	 * widget in a particular column.
	 *
	 * @param  nColumn The column
	 * @param  rWidget The widget
	 *
	 * @return The row containing the widget or -1 if not found
	 */
	private int findFilterCriterionRow(int nColumn, Widget rWidget)
	{
		int     nRowCount = aFilterCriteriaPanel.getRowCount();
		int     nRow	  = 0;
		boolean bFound    = false;

		while (!bFound && nRow < nRowCount)
		{
			if (nColumn < aFilterCriteriaPanel.getCellCount(nRow))
			{
				Widget rRowWidget =
					aFilterCriteriaPanel.getWidget(nRow, nColumn);

				if (rRowWidget instanceof DateBox)
				{
					rRowWidget = ((DateBox) rRowWidget).getTextBox();
				}

				bFound = (rRowWidget == rWidget);
			}

			if (!bFound)
			{
				nRow++;
			}
		}

		return nRow < nRowCount ? nRow : -1;
	}

	/***************************************
	 * Returns the allowed string values for a table column. The returned array
	 * will be NULL if no value constraint is set on the column.
	 *
	 * @param  rColumn The column definition
	 *
	 * @return An array containing the allowed values or NULL for none
	 */
	private String[] getAllowedValues(ColumnDefinition rColumn)
	{
		String[] aAllowedValues = null;
		String   sValues	    = rColumn.getProperty(ALLOWED_VALUES, null);

		if (sValues != null)
		{
			aAllowedValues = sValues.split(",");
		}

		return aAllowedValues;
	}

	/***************************************
	 * Returns the index of the column with a certain ID.
	 *
	 * @param  sColumnId The column ID to search the index of
	 *
	 * @return The column index or -1 if not found
	 */
	private int getColumnIndex(String sColumnId)
	{
		int nColumn = aFilterColumns.size() - 1;

		while (nColumn >= 0 &&
			   !aFilterColumns.get(nColumn).getId().equals(sColumnId))
		{
			nColumn--;
		}

		return nColumn;
	}

	/***************************************
	 * Initializes and returns the key up event handler for the complex filter
	 * panel.
	 *
	 * @return The key up handler
	 */
	private KeyDownHandler getComplexFilterKeyDownHandler()
	{
		if (aComplexFilterKeyDownHandler == null)
		{
			aComplexFilterKeyDownHandler =
				new KeyDownHandler()
				{
					@Override
					public void onKeyDown(KeyDownEvent rEvent)
					{
						handleComplexFilterKeyDown(rEvent);
					}
				};
		}

		return aComplexFilterKeyDownHandler;
	}

	/***************************************
	 * Initializes and returns the key up event handler for the complex filter
	 * panel.
	 *
	 * @return The key up handler
	 */
	private KeyUpHandler getComplexFilterKeyUpHandler()
	{
		if (aComplexFilterKeyUpHandler == null)
		{
			aComplexFilterKeyUpHandler =
				new KeyUpHandler()
				{
					@Override
					public void onKeyUp(KeyUpEvent rEvent)
					{
						handleComplexFilterKeyUp(rEvent);
					}
				};
		}

		return aComplexFilterKeyUpHandler;
	}

	/***************************************
	 * Returns the constraint value for a certain filter value input.
	 *
	 * @param  rColumn      The column to return the constraint for
	 * @param  rValueWidget The widget to read the value from
	 *
	 * @return The constraint value string
	 */
	private String getFilterConstraint(
		ColumnDefinition rColumn,
		Widget			 rValueWidget)
	{
		String sValue = "";

		if (rValueWidget instanceof TextBox)
		{
			sValue = ((TextBox) rValueWidget).getText();
		}
		else if (rValueWidget instanceof CheckBox)
		{
			sValue = ((CheckBox) rValueWidget).getValue().toString();
		}
		else if (rValueWidget instanceof DateBox)
		{
			Date rDate = ((DateBox) rValueWidget).getValue();

			if (rDate != null)
			{
				sValue = FILTER_DATE_FORMAT.format(rDate);
			}
			else
			{
				sValue = NULL_CONSTRAINT_VALUE;
			}
		}
		else if (rValueWidget instanceof ListBox)
		{
			ListBox rListBox    = (ListBox) rValueWidget;
			int     nConstraint = rListBox.getSelectedIndex();

			if (nConstraint >= 0)
			{
				sValue = getAllowedValues(rColumn)[nConstraint];
			}
		}

		return sValue;
	}

	/***************************************
	 * Returns the searchable data model of this panel's table.
	 *
	 * @return The searchable model of the table
	 */
	private FilterableDataModel<?> getSearchableModel()
	{
		return (FilterableDataModel<?>) rTable.getData();
	}

	/***************************************
	 * Returns the currently selected column in a certain row of the complex
	 * filter popup.
	 *
	 * @param  nRow The row to return the column for
	 *
	 * @return The column definition for the given row
	 */
	private ColumnDefinition getSelectedFilterColumn(int nRow)
	{
		ListBox rColumnList =
			(ListBox) aFilterCriteriaPanel.getWidget(nRow, COL_FILTER_COLUMN);

		ColumnDefinition rColumn =
			aFilterColumns.get(rColumnList.getSelectedIndex());

		return rColumn;
	}

	/***************************************
	 * Returns the currently selected comparison in a certain row of the complex
	 * filter popup.
	 *
	 * @param  nRow The row to return the comparison for
	 *
	 * @return The comparison for the given row
	 */
	private String getSelectedFilterComparison(int nRow)
	{
		ListBox rComparisons =
			(ListBox) aFilterCriteriaPanel.getWidget(nRow, COL_FILTER_COMPARE);
		String  sComparison  =
			rComparisons.getItemText(rComparisons.getSelectedIndex());

		return sComparison;
	}

	/***************************************
	 * Handles clicks on the complex filter button.
	 */
	private void handleComplexFilterButton()
	{
		Map<String, String> rConstraints = getSearchableModel().getFilters();

		aFilterPopup		 = new DecoratedPopupPanel(true, true);
		aFilterCriteriaPanel = new FlexTable();

		aFilterPopup.addStyleName(GwtTable.CSS.ewtTableFilterPopup());

		Panel	   aButtonPanel     = createFilterPopupButtonPanel();
		PushButton aAddFilterButton = createEditFilterListButton(true);
		int		   nButtonRow	    = 0;

		FlexCellFormatter rCellFormatter =
			aFilterCriteriaPanel.getFlexCellFormatter();

		aFilterCriteriaPanel.setWidget(nButtonRow, 0, aButtonPanel);
		aFilterCriteriaPanel.setWidget(nButtonRow, 1, aAddFilterButton);
		rCellFormatter.setColSpan(nButtonRow, 0, COL_FILTER_BUTTON);

		initComplexFilterInput(rConstraints);

		aFilterPopup.setAnimationEnabled(true);
		aFilterPopup.setGlassEnabled(true);
		aFilterPopup.setWidget(aFilterCriteriaPanel);
		aFilterPopup.addCloseHandler(new CloseHandler<PopupPanel>()
			{
				@Override
				public void onClose(CloseEvent<PopupPanel> rEvent)
				{
					if (rEvent.isAutoClosed())
					{
						hideComplexFilterPopup(false);
					}
				}
			});

		aFilterPopup.setPopupPositionAndShow(new PositionCallback()
			{
				@Override
				public void setPosition(int nWidth, int nHeight)
				{
					updateFilterPopupPosition(nHeight);
				}
			});

		setFocus(aFilterCriteriaPanel.getWidget(0, COL_FILTER_COLUMN));
		setFilterButtonActive(true);
	}

	/***************************************
	 * Handles clicks on a button that manipulates the filter criteria list.
	 *
	 * @param rEvent The click event that occurred
	 * @param bAdd   TRUE to add a row, FALSE to remove the row of the button
	 */
	private void handleFilterListChangeButton(ClickEvent rEvent, boolean bAdd)
	{
		if (bAdd)
		{
			addFilterRow(-1, ' ', null, false);
		}
		else
		{
			int nRow =
				findFilterCriterionRow(COL_FILTER_BUTTON,
									   (Widget) rEvent.getSource());

			aFilterCriteriaPanel.removeRow(nRow);
		}

		updateFilterPopupPosition(aFilterPopup.getOffsetHeight());
	}

	/***************************************
	 * Hides the complex filter popup panel and optionally applies the filter
	 * criteria.
	 *
	 * @param bApply TRUE to apply the filter criteria, FALSE to discard them
	 */
	private void hideComplexFilterPopup(boolean bApply)
	{
		if (aFilterPopup != null)
		{
			aFilterPopup.hide();
		}

		if (bApply)
		{
			applyComplexFilter();
		}
		else if (getSearchableModel().getFilters().isEmpty())
		{
			resetFilter();
		}
	}

	/***************************************
	 * Initializes the input components for complex filter rows.
	 *
	 * @param rConstraints The constraints to initialize the filter rows from
	 *                     (empty for none)
	 */
	private void initComplexFilterInput(Map<String, String> rConstraints)
	{
		if (aFilterButton.isDown() || rConstraints.isEmpty())
		{
			aFilterInput.setText("");
			aFilterInput.setEnabled(false);
			aFilterInput.addStyleDependentName("disabled");
			addFilterRow(-1, ' ', null, false);
		}
		else
		{
			addComplexFilterCriteriaRows(rConstraints);
		}
	}

	/***************************************
	 * Checks whether the selected join in a complex filter row is a logical OR
	 * or AND term.
	 *
	 * @param  nRow The complex filter row
	 *
	 * @return TRUE for an OR term
	 */
	private boolean isOrTerm(int nRow)
	{
		boolean bOrTerm = false;
		ListBox rJoins  =
			(ListBox) aFilterCriteriaPanel.getWidget(nRow, COL_FILTER_JOIN);

		if (rJoins != null)
		{
			bOrTerm = (rJoins.getSelectedIndex() == 1);
		}

		return bOrTerm;
	}

	/***************************************
	 * Resets all current filter criteria and the enables the generic filter
	 * input field.
	 */
	private void resetFilter()
	{
		rTable.setSelection(-1);
		aFilterInput.setText(null);
		aFilterInput.setEnabled(true);
		aFilterInput.removeStyleDependentName("disabled");
		setFilterButtonActive(false);
		removeFilter();
	}

	/***************************************
	 * Resets the input value of a certain widget.
	 *
	 * @param rWidget The widget to reset the value of
	 */
	private void resetInputValue(Widget rWidget)
	{
		if (rWidget instanceof HasValue)
		{
			((HasValue<?>) rWidget).setValue(null);
		}
		else if (rWidget instanceof ListBox)
		{
			((ListBox) rWidget).setSelectedIndex(0);
		}
	}

	/***************************************
	 * Analyzes the constraints of a {@link FilterableDataModel} to check
	 * whether they represent a single search term for all searchable columns.
	 *
	 * @param  rDataModel The data model to get the filter criteria from
	 *
	 * @return The single filter criterion or NULL for none
	 */
	private String searchSimpleFilter(DataModel<?> rDataModel)
	{
		String sFilter = null;

		if (rDataModel instanceof FilterableDataModel)
		{
			FilterableDataModel<?> rSearchableModel =
				(FilterableDataModel<?>) rDataModel;

			for (String sCriterion : rSearchableModel.getFilters().values())
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
	 * Sets the filter button active to indicate that filtering is used.
	 *
	 * @param bActive TRUE to activate
	 */
	private void setFilterButtonActive(boolean bActive)
	{
		aFilterButton.setDown(bActive);

		if (bActive)
		{
			aFilterButton.addStyleDependentName("active");
		}
		else
		{
			aFilterButton.removeStyleDependentName("active");
		}
	}

	/***************************************
	 * Sets the focus to a certain widget.
	 *
	 * @param rWidget The widget to set the focus on
	 */
	private void setFocus(Widget rWidget)
	{
		if (rWidget instanceof Focusable)
		{
			((Focusable) rWidget).setFocus(true);
		}
		else if (rWidget instanceof DateBox)
		{
			((DateBox) rWidget).setFocus(true);
		}
	}

	/***************************************
	 * Adjusts the position of the filter popup panel.
	 *
	 * @param nPopupHeight The current height of the popup panel
	 */
	private void updateFilterPopupPosition(int nPopupHeight)
	{
		aFilterPopup.setPopupPosition(getAbsoluteLeft(),
									  getAbsoluteTop() - nPopupHeight);
	}

	/***************************************
	 * Updates the table after a filter change.
	 */
	private void updateTable()
	{
		rTable.collapseAllNodes();
		rTable.setFirstRow(0);
		rTable.update();
	}
}
