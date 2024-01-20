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
package de.esoco.ewt.impl.gwt;

import de.esoco.ewt.EWT;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.google.gwt.user.client.ui.Widget;

/**
 * A GWT implementation of a field that allows to select and optionally edit
 * tags (short string labels).
 *
 * @author eso
 */
public class GwtTagField extends Composite
	implements Focusable, HasEnabled, KeyDownHandler, ClickHandler,
	SelectionHandler<Suggestion>, HasValueChangeHandlers<Set<String>> {

	private final FlowPanel mainPanel;

	private final SuggestBox tagInput;

	private Set<String> deletedTags;

	/**
	 * Creates a new instance.
	 */
	public GwtTagField() {
		mainPanel = new FlowPanel();
		tagInput = new SuggestBox(new MultiWordSuggestOracle());

		initWidget(mainPanel);

		setStylePrimaryName(EWT.CSS.ewtTagField());

		addDomHandler(this, ClickEvent.getType());

		tagInput.addSelectionHandler(this);
		tagInput.getValueBox().addKeyDownHandler(this);
		tagInput.setStylePrimaryName(EWT.CSS.ewtTagInput());

		mainPanel.add(tagInput);
	}

	/**
	 * Adds a tag string.
	 *
	 * @param tag The tag to add
	 */
	public void addTag(String tag) {
		TagDisplay tagDisplay = new TagDisplay(tag);

		mainPanel.insert(tagDisplay, mainPanel.getWidgetCount() - 1);
	}

	/**
	 * Adds multiple tag strings.
	 *
	 * @param tags values The tags to add
	 */
	public void addTags(Collection<String> tags) {
		for (String tag : tags) {
			addTag(tag);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public HandlerRegistration addValueChangeHandler(
		ValueChangeHandler<Set<String>> handler) {
		return addHandler(handler, ValueChangeEvent.getType());
	}

	/**
	 * Removes all tags.
	 */
	public void clearTags() {
		while (mainPanel.getWidgetCount() > 1) {
			mainPanel.remove(0);
		}
	}

	/**
	 * Returns the {@link SuggestBox} used by this instance.
	 *
	 * @return The suggest box of this instance
	 */
	public final SuggestBox getSuggestBox() {
		return tagInput;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getTabIndex() {
		return tagInput.getTabIndex();
	}

	/**
	 * Returns a set containing the currently selected tags in the order in
	 * which they are displayed.
	 *
	 * @return A set of the current tag strings
	 */
	public Set<String> getTags() {
		Set<String> tags = new LinkedHashSet<>(mainPanel.getWidgetCount() - 1);

		for (Widget child : mainPanel) {
			if (child instanceof TagDisplay) {
				tags.add(((TagDisplay) child).getText());
			}
		}

		return tags;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEnabled() {
		return tagInput.isEnabled();
	}

	/**
	 * @see ClickHandler#onClick(ClickEvent)
	 */
	@Override
	public void onClick(ClickEvent event) {
		tagInput.getValueBox().setFocus(true);

		if (event.getSource() == this) {
			setTagsSelected(false);
		}
	}

	/**
	 * Handles the enter and backspace keys to add or remove the last tag.
	 *
	 * @see KeyDownHandler#onKeyDown(KeyDownEvent)
	 */
	@Override
	public void onKeyDown(KeyDownEvent event) {
		if (tagInput.isEnabled()) {
			String input = tagInput.getValue();

			if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
				addTagFromInput(input);
			} else if (event.getNativeKeyCode() == KeyCodes.KEY_BACKSPACE &&
				input.equals("") && mainPanel.getWidgetCount() > 1) {
				Widget tag =
					mainPanel.getWidget(mainPanel.getWidgetCount() - 2);

				deleteTag((TagDisplay) tag);
			} else if (event.isControlKeyDown() &&
				event.getNativeKeyCode() == KeyCodes.KEY_Z) {
				// undo delete
				if (deletedTags != null) {
					for (String tag : deletedTags) {
						addTag(tag);
					}

					deletedTags = null;
					ValueChangeEvent.fire(this, getTags());
				}
			}
		}
	}

	/**
	 * Adds a new tag when a suggested value has been selected.
	 *
	 * @see SelectionHandler#onSelection(SelectionEvent)
	 */
	@Override
	public void onSelection(SelectionEvent<Suggestion> event) {
		addTagFromInput(event.getSelectedItem().getReplacementString());
	}

	/**
	 * Removes a tag.
	 *
	 * @param tag The name of the tag to remove
	 */
	public void removeTag(String tag) {
		for (Widget child : mainPanel) {
			if (child instanceof TagDisplay &&
				tag.equals(((TagDisplay) child).getText())) {
				mainPanel.remove(child);

				break;
			}
		}
	}

	/**
	 * @see Focusable#setAccessKey(char)
	 */
	@Override
	public void setAccessKey(char key) {
		tagInput.setAccessKey(key);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setEnabled(boolean enabled) {
		tagInput.setEnabled(enabled);

		if (enabled) {
			removeStyleDependentName("disabled");
		} else {
			addStyleDependentName("disabled");
		}
	}

	/**
	 * @see Focusable#setFocus(boolean)
	 */
	@Override
	public void setFocus(boolean focused) {
		tagInput.setFocus(focused);
	}

	/**
	 * @see Focusable#setTabIndex(int)
	 */
	@Override
	public void setTabIndex(int index) {
		tagInput.setTabIndex(index);
	}

	/**
	 * Deletes all selected tags.
	 */
	void deleteSelectedTags() {
		Set<Widget> deletedTagDisplays = new HashSet<>();

		deletedTags = new HashSet<>();

		for (Widget child : mainPanel) {
			if (child instanceof TagDisplay) {
				TagDisplay tagDisplay = (TagDisplay) child;

				if (tagDisplay.isSelected()) {
					deletedTagDisplays.add(child);
					deletedTags.add(((TagDisplay) child).getText());
				}
			}
		}

		for (Widget child : deletedTagDisplays) {
			mainPanel.remove(child);
		}

		ValueChangeEvent.fire(this, getTags());
	}

	/**
	 * Deletes a tag widget from this instance after querying the user for
	 * confirmation.
	 *
	 * @param tagDisplay The tag widget to delete
	 */
	void deleteTag(TagDisplay tagDisplay) {
		deletedTags = new HashSet<>();
		deletedTags.add(tagDisplay.getText());

		mainPanel.remove(tagDisplay);
		ValueChangeEvent.fire(this, getTags());
	}

	/**
	 * Sets the selected state for all tags.
	 *
	 * @param selected The new selected state
	 */
	void setTagsSelected(boolean selected) {
		for (Widget child : mainPanel) {
			if (child instanceof TagDisplay) {
				((TagDisplay) child).setSelected(selected);
			}
		}
	}

	/**
	 * Internal method to add a tag from an input value.
	 *
	 * @param input The input value
	 */
	private void addTagFromInput(String input) {
		if (input.length() > 0) {
			addTag(input);
			tagInput.setValue("");
			ValueChangeEvent.fire(this, getTags());
		}
	}

	/**
	 * A display widget for a single tag.
	 *
	 * @author eso
	 */
	private class TagDisplay extends Grid implements ClickHandler {

		private Label deleteWidget;

		private boolean selected;

		/**
		 * Creates a new instance with a certain tag text
		 *
		 * @param text The tag text
		 */
		public TagDisplay(String text) {
			super(1, 2);

			deleteWidget = new Label();
			setText(0, 0, text);
			setWidget(0, 1, deleteWidget);
			setSelected(false);

			setStylePrimaryName(EWT.CSS.ewtTagDisplay());
			addClickHandler(this);
		}

		/**
		 * Returns the text that is displayed.
		 *
		 * @return The displayed text
		 */
		public String getText() {
			return getText(0, 0);
		}

		/**
		 * Returns the selected state of this instance.
		 *
		 * @return The selected state
		 */
		public final boolean isSelected() {
			return selected;
		}

		/**
		 * @see ClickHandler#onClick(ClickEvent)
		 */
		@Override
		public void onClick(ClickEvent event) {
			if (tagInput.isEnabled()) {
				if (selected) {
					if (getCellForEvent(event).getCellIndex() == 1) {
						deleteSelectedTags();
					}
				} else {
					if (!event.isControlKeyDown()) {
						setTagsSelected(false);
					}

					setSelected(true);
				}
			}

			// prevent click handling in parent
			event.stopPropagation();
		}

		/**
		 * Sets the selected.
		 *
		 * @param selected The new selected
		 */
		void setSelected(boolean selected) {
			this.selected = selected;

			if (selected) {
				deleteWidget.setText("\u00D7"); // multiplication sign '×'
				addStyleDependentName("selected");
			} else {
				deleteWidget.setText("");
				removeStyleDependentName("selected");
			}
		}
	}
}
