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

import de.esoco.ewt.impl.gwt.GwtTagField;
import de.esoco.ewt.style.StyleData;
import de.esoco.ewt.style.StyleFlag;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestBox.DefaultSuggestionDisplay;
import com.google.gwt.user.client.ui.SuggestBox.SuggestionDisplay;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;


/********************************************************************
 * A combo box component that has an editable text field and a popup menu with a
 * list of values to choose from. If an instance is initialized with the style
 * flag {@link StyleFlag#MULTISELECT} it will allow to enter and edit multiple
 * values which will be displayed as a list of tags. In that case the value
 * modification methods like {@link #addValue(String)} are used to modify the
 * set of tags and {@link #getValues()} to query the tags.
 *
 * @author eso
 */
public class ComboBox extends TextComponent implements KeyDownHandler,
													   DoubleClickHandler
{
	//~ Instance fields --------------------------------------------------------

	private Set<String> aDefaultSuggestions = new LinkedHashSet<String>();

	private boolean bMultiselect;

	//~ Constructors -----------------------------------------------------------

	/***************************************
	 * Creates a new instance with a certain style.
	 *
	 * @param rStyleData The style data
	 */
	public ComboBox(StyleData rStyleData)
	{
		super(createWidget(rStyleData));

		bMultiselect = rStyleData.hasFlag(StyleFlag.MULTISELECT);

		TextBox rTextBox = getTextBox();

		rTextBox.addKeyDownHandler(this);
		rTextBox.addDoubleClickHandler(this);
	}

	//~ Static methods ---------------------------------------------------------

	/***************************************
	 * Creates the widget for a new instance based on the given style data.
	 *
	 * @param  rStyleData The style data
	 *
	 * @return The new widget
	 */
	private static Focusable createWidget(StyleData rStyleData)
	{
		Focusable rWidget;

		if (rStyleData.hasFlag(StyleFlag.MULTISELECT))
		{
			rWidget = new GwtTagField();
		}
		else
		{
			rWidget = new SuggestBox(new MultiWordSuggestOracle());
		}

		return rWidget;
	}

	//~ Methods ----------------------------------------------------------------

	/***************************************
	 * Adds a choice to the drop-down list.
	 *
	 * @param sValue The choice value to add
	 */
	public void addChoice(String sValue)
	{
		MultiWordSuggestOracle rOracle = getSuggestOracle();

		aDefaultSuggestions.add(sValue);
		rOracle.add(sValue);
		rOracle.setDefaultSuggestionsFromText(aDefaultSuggestions);
	}

	/***************************************
	 * Adds multiple choices to the drop-down list.
	 *
	 * @param rValues The choice values to add
	 */
	public void addChoices(Collection<String> rValues)
	{
		MultiWordSuggestOracle rOracle = getSuggestOracle();

		aDefaultSuggestions.addAll(rValues);
		rOracle.addAll(rValues);
		rOracle.setDefaultSuggestionsFromText(aDefaultSuggestions);
	}

	/***************************************
	 * Adds a value string to a multiselection instance or sets the text in
	 * single selection.
	 *
	 * @param sValue The value to add
	 */
	public void addValue(String sValue)
	{
		if (bMultiselect)
		{
			((GwtTagField) getWidget()).addTag(sValue);
		}
		else
		{
			setText(sValue);
		}
	}

	/***************************************
	 * Adds multiple values to a multiselection instance or sets the text in
	 * single selection.
	 *
	 * @param rValues The values to add
	 */
	public void addValues(Collection<String> rValues)
	{
		if (bMultiselect)
		{
			((GwtTagField) getWidget()).addTags(rValues);
		}
		else
		{
			setText(rValues.toString());
		}
	}

	/***************************************
	 * Removes all choices from the drop-down list.
	 */
	public void clearChoices()
	{
		MultiWordSuggestOracle rOracle = getSuggestOracle();

		aDefaultSuggestions.clear();
		rOracle.clear();
		rOracle.setDefaultSuggestionsFromText(aDefaultSuggestions);
	}

	/***************************************
	 * Clears all values in a multiselection instance or the text in single
	 * selection.
	 */
	public void clearValues()
	{
		if (bMultiselect)
		{
			((GwtTagField) getWidget()).clearTags();
		}
		else
		{
			setText("");
		}
	}

	/***************************************
	 * Returns a set of the currently selected values of a multiselection
	 * instance or the text in single selection.
	 *
	 * @return The values
	 */
	public Set<String> getValues()
	{
		Set<String> rValues;

		if (bMultiselect)
		{
			rValues = ((GwtTagField) getWidget()).getTags();
		}
		else
		{
			rValues = new LinkedHashSet<>();
			rValues.add(getText());
		}

		return rValues;
	}

	/***************************************
	 * @see DoubleClickHandler#onDoubleClick(DoubleClickEvent)
	 */
	@Override
	public void onDoubleClick(DoubleClickEvent rEvent)
	{
		getSuggestBox().showSuggestionList();
	}

	/***************************************
	 * @see KeyDownHandler#onKeyDown(KeyDownEvent)
	 */
	@Override
	public void onKeyDown(KeyDownEvent rEvent)
	{
		SuggestionDisplay rDisplay = getSuggestBox().getSuggestionDisplay();

		if (rDisplay instanceof DefaultSuggestionDisplay)
		{
			DefaultSuggestionDisplay rDefaultDisplay =
				(DefaultSuggestionDisplay) rDisplay;

			if (rEvent.isDownArrow() || rEvent.isUpArrow())
			{
				if (!rDefaultDisplay.isSuggestionListShowing())
				{
					getSuggestBox().showSuggestionList();
				}
			}
			else if (rEvent.getNativeKeyCode() == KeyCodes.KEY_ESCAPE)
			{
				rDefaultDisplay.hideSuggestions();
			}
		}
	}

	/***************************************
	 * Removes a value from a multiselection instance. For single selection
	 * instances this call will be ignored, use the {@link #setText(String)}
	 * method instead.
	 *
	 * @param sValue The value to remove
	 */
	public void removeValue(String sValue)
	{
		if (bMultiselect)
		{
			((GwtTagField) getWidget()).removeTag(sValue);
		}
	}

	/***************************************
	 * @see TextComponent#setColumns(int)
	 */
	@Override
	public void setColumns(int nColumns)
	{
		getTextBox().setVisibleLength(nColumns);
	}

	/***************************************
	 * @see TextComponent#getTextBox()
	 */
	@Override
	protected TextBox getTextBox()
	{
		return ((TextBox) getSuggestBox().getValueBox());
	}

	/***************************************
	 * Returns the {@link SuggestBox} instance used by the underlying GWT
	 * widget.
	 *
	 * @return The suggest box
	 */
	private SuggestBox getSuggestBox()
	{
		Widget rWidget = getWidget();

		return bMultiselect ? ((GwtTagField) rWidget).getSuggestBox()
							: (SuggestBox) rWidget;
	}

	/***************************************
	 * Returns the {@link MultiWordSuggestOracle} of this instance.
	 *
	 * @return The suggest oracle
	 */
	private MultiWordSuggestOracle getSuggestOracle()
	{
		return (MultiWordSuggestOracle) getSuggestBox().getSuggestOracle();
	}
}
