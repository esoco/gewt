//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// This file is a part of the 'gewt' project.
// Copyright 2016 Elmar Sonnenschein, esoco GmbH, Flensburg, Germany
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
import de.esoco.ewt.impl.gwt.ValueBoxWrapper;
import de.esoco.ewt.impl.gwt.WidgetFactory;
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
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestBox.DefaultSuggestionDisplay;
import com.google.gwt.user.client.ui.SuggestBox.SuggestionDisplay;
import com.google.gwt.user.client.ui.Widget;

/**
 * A combo box component that has an editable text field and a popup menu with a
 * list of values to choose from. If an instance is initialized with the style
 * flag {@link StyleFlag#MULTISELECT} it will allow to enter and edit multiple
 * values which will be displayed as a list of tags. In that case the value
 * modification methods like {@link #addValue(String)} are used to modify the
 * set of tags and {@link #getValues()} to query the tags.
 *
 * @author eso
 */
public class ComboBox extends TextControl
	implements KeyDownHandler, DoubleClickHandler {

	private Set<String> defaultSuggestions = new LinkedHashSet<String>();

	private boolean multiselect;

	/**
	 * Adds a choice to the drop-down list.
	 *
	 * @param value The choice value to add
	 */
	public void addChoice(String value) {
		MultiWordSuggestOracle oracle = getSuggestOracle();

		defaultSuggestions.add(value);
		oracle.add(value);
		oracle.setDefaultSuggestionsFromText(defaultSuggestions);
	}

	/**
	 * Adds multiple choices to the drop-down list.
	 *
	 * @param values The choice values to add
	 */
	public void addChoices(Collection<String> values) {
		MultiWordSuggestOracle oracle = getSuggestOracle();

		defaultSuggestions.addAll(values);
		oracle.addAll(values);
		oracle.setDefaultSuggestionsFromText(defaultSuggestions);
	}

	/**
	 * Adds a value string to a multiselection instance or sets the text in
	 * single selection.
	 *
	 * @param value The value to add
	 */
	public void addValue(String value) {
		if (multiselect) {
			((GwtTagField) getWidget()).addTag(value);
		} else {
			setText(value);
		}
	}

	/**
	 * Adds multiple values to a multiselection instance or sets the text in
	 * single selection.
	 *
	 * @param values The values to add
	 */
	public void addValues(Collection<String> values) {
		if (multiselect) {
			((GwtTagField) getWidget()).addTags(values);
		} else {
			setText(values.toString());
		}
	}

	/**
	 * Removes all choices from the drop-down list.
	 */
	public void clearChoices() {
		MultiWordSuggestOracle oracle = getSuggestOracle();

		defaultSuggestions.clear();
		oracle.clear();
		oracle.setDefaultSuggestionsFromText(defaultSuggestions);
	}

	/**
	 * Clears all values in a multiselection instance or the text in single
	 * selection.
	 */
	public void clearValues() {
		if (multiselect) {
			((GwtTagField) getWidget()).clearTags();
		} else {
			setText("");
		}
	}

	/**
	 * Returns a set of the currently selected values of a multiselection
	 * instance or the text in single selection.
	 *
	 * @return The values
	 */
	public Set<String> getValues() {
		Set<String> values;

		if (multiselect) {
			values = ((GwtTagField) getWidget()).getTags();
		} else {
			values = new LinkedHashSet<>();
			values.add(getText());
		}

		return values;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initWidget(Container parent, StyleData style) {
		super.initWidget(parent, style);

		multiselect = style.hasFlag(StyleFlag.MULTISELECT);

		IsTextControlWidget textBox = getTextBox();

		textBox.addKeyDownHandler(this);
		textBox.addDoubleClickHandler(this);
	}

	/**
	 * @see DoubleClickHandler#onDoubleClick(DoubleClickEvent)
	 */
	@Override
	public void onDoubleClick(DoubleClickEvent event) {
		getSuggestBox().showSuggestionList();
	}

	/**
	 * @see KeyDownHandler#onKeyDown(KeyDownEvent)
	 */
	@Override
	public void onKeyDown(KeyDownEvent event) {
		SuggestionDisplay display = getSuggestBox().getSuggestionDisplay();

		if (display instanceof DefaultSuggestionDisplay) {
			DefaultSuggestionDisplay defaultDisplay =
				(DefaultSuggestionDisplay) display;

			if (event.isDownArrow() || event.isUpArrow()) {
				if (!defaultDisplay.isSuggestionListShowing()) {
					getSuggestBox().showSuggestionList();
				}
			} else if (event.getNativeKeyCode() == KeyCodes.KEY_ESCAPE) {
				defaultDisplay.hideSuggestions();
			}
		}
	}

	/**
	 * Removes a value from a multiselection instance. For single selection
	 * instances this call will be ignored, use the {@link #setText(String)}
	 * method instead.
	 *
	 * @param value The value to remove
	 */
	public void removeValue(String value) {
		if (multiselect) {
			((GwtTagField) getWidget()).removeTag(value);
		}
	}

	/**
	 * @see TextControl#setColumns(int)
	 */
	@Override
	public void setColumns(int columns) {
		getTextBox().setVisibleLength(columns);
	}

	/**
	 * @see TextControl#getTextBox()
	 */
	@Override
	protected IsTextControlWidget getTextBox() {
		return new ValueBoxWrapper(getSuggestBox().getValueBox());
	}

	/**
	 * Returns the {@link SuggestBox} instance used by the underlying GWT
	 * widget.
	 *
	 * @return The suggest box
	 */
	private SuggestBox getSuggestBox() {
		Widget widget = getWidget();

		return multiselect ?
		       ((GwtTagField) widget).getSuggestBox() :
		       (SuggestBox) widget;
	}

	/**
	 * Returns the {@link MultiWordSuggestOracle} of this instance.
	 *
	 * @return The suggest oracle
	 */
	private MultiWordSuggestOracle getSuggestOracle() {
		return (MultiWordSuggestOracle) getSuggestBox().getSuggestOracle();
	}

	/**
	 * Widget factory for this component.
	 *
	 * @author eso
	 */
	public static class ComboBoxWidgetFactory implements WidgetFactory<Widget> {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Widget createWidget(Component component, StyleData style) {
			Widget widget;

			if (style.hasFlag(StyleFlag.MULTISELECT)) {
				widget = new GwtTagField();
			} else {
				widget = new SuggestBox(new MultiWordSuggestOracle());
			}

			return widget;
		}
	}
}
