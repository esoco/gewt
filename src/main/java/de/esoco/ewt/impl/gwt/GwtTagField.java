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


/********************************************************************
 * A GWT implementation of a field that allows to select and optionally edit
 * tags (short string labels).
 *
 * @author eso
 */
public class GwtTagField extends Composite
	implements Focusable, HasEnabled, KeyDownHandler, ClickHandler,
			   SelectionHandler<Suggestion>, HasValueChangeHandlers<Set<String>>
{
	//~ Instance fields --------------------------------------------------------

	private final FlowPanel  aMainPanel;
	private final SuggestBox aTagInput;

	private Set<String> aDeletedTags;

	//~ Constructors -----------------------------------------------------------

	/***************************************
	 * Creates a new instance.
	 */
	public GwtTagField()
	{
		aMainPanel = new FlowPanel();
		aTagInput  = new SuggestBox(new MultiWordSuggestOracle());

		initWidget(aMainPanel);

		setStylePrimaryName(EWT.CSS.ewtTagField());

		addDomHandler(this, ClickEvent.getType());

		aTagInput.addSelectionHandler(this);
		aTagInput.getValueBox().addKeyDownHandler(this);
		aTagInput.setStylePrimaryName(EWT.CSS.ewtTagInput());

		aMainPanel.add(aTagInput);
	}

	//~ Methods ----------------------------------------------------------------

	/***************************************
	 * Adds a tag string.
	 *
	 * @param sTag The tag to add
	 */
	public void addTag(String sTag)
	{
		TagDisplay aTagDisplay = new TagDisplay(sTag);

		aMainPanel.insert(aTagDisplay, aMainPanel.getWidgetCount() - 1);
	}

	/***************************************
	 * Adds multiple tag strings.
	 *
	 * @param rTags rValues The tags to add
	 */
	public void addTags(Collection<String> rTags)
	{
		for (String sTag : rTags)
		{
			addTag(sTag);
		}
	}

	/***************************************
	 * {@inheritDoc}
	 */
	@Override
	public HandlerRegistration addValueChangeHandler(
		ValueChangeHandler<Set<String>> rHandler)
	{
		return addHandler(rHandler, ValueChangeEvent.getType());
	}

	/***************************************
	 * Removes all tags.
	 */
	public void clearTags()
	{
		while (aMainPanel.getWidgetCount() > 1)
		{
			aMainPanel.remove(0);
		}
	}

	/***************************************
	 * Returns the {@link SuggestBox} used by this instance.
	 *
	 * @return The suggest box of this instance
	 */
	public final SuggestBox getSuggestBox()
	{
		return aTagInput;
	}

	/***************************************
	 * {@inheritDoc}
	 */
	@Override
	public int getTabIndex()
	{
		return aTagInput.getTabIndex();
	}

	/***************************************
	 * Returns a set containing the currently selected tags in the order in
	 * which they are displayed.
	 *
	 * @return A set of the current tag strings
	 */
	public Set<String> getTags()
	{
		Set<String> aTags =
			new LinkedHashSet<>(aMainPanel.getWidgetCount() - 1);

		for (Widget rChild : aMainPanel)
		{
			if (rChild instanceof TagDisplay)
			{
				aTags.add(((TagDisplay) rChild).getText());
			}
		}

		return aTags;
	}

	/***************************************
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEnabled()
	{
		return aTagInput.isEnabled();
	}

	/***************************************
	 * @see ClickHandler#onClick(ClickEvent)
	 */
	@Override
	public void onClick(ClickEvent rEvent)
	{
		aTagInput.getValueBox().setFocus(true);

		if (rEvent.getSource() == this)
		{
			setTagsSelected(false);
		}
	}

	/***************************************
	 * Handles the enter and backspace keys to add or remove the last tag.
	 *
	 * @see KeyDownHandler#onKeyDown(KeyDownEvent)
	 */
	@Override
	public void onKeyDown(KeyDownEvent rEvent)
	{
		if (aTagInput.isEnabled())
		{
			String sInput = aTagInput.getValue();

			if (rEvent.getNativeKeyCode() == KeyCodes.KEY_ENTER)
			{
				addTagFromInput(sInput);
			}
			else if (rEvent.getNativeKeyCode() == KeyCodes.KEY_BACKSPACE &&
					 sInput.equals("") &&
					 aMainPanel.getWidgetCount() > 1)
			{
				Widget rTag =
					aMainPanel.getWidget(aMainPanel.getWidgetCount() - 2);

				deleteTag((TagDisplay) rTag);
			}
			else if (rEvent.isControlKeyDown() &&
					 rEvent.getNativeKeyCode() == KeyCodes.KEY_Z)
			{
				// undo delete
				if (aDeletedTags != null)
				{
					for (String sTag : aDeletedTags)
					{
						addTag(sTag);
					}

					aDeletedTags = null;
					ValueChangeEvent.fire(this, getTags());
				}
			}
		}
	}

	/***************************************
	 * Adds a new tag when a suggested value has been selected.
	 *
	 * @see SelectionHandler#onSelection(SelectionEvent)
	 */
	@Override
	public void onSelection(SelectionEvent<Suggestion> rEvent)
	{
		addTagFromInput(rEvent.getSelectedItem().getReplacementString());
	}

	/***************************************
	 * Removes a tag.
	 *
	 * @param sTag The name of the tag to remove
	 */
	public void removeTag(String sTag)
	{
		for (Widget rChild : aMainPanel)
		{
			if (rChild instanceof TagDisplay &&
				sTag.equals(((TagDisplay) rChild).getText()))
			{
				aMainPanel.remove(rChild);

				break;
			}
		}
	}

	/***************************************
	 * @see Focusable#setAccessKey(char)
	 */
	@Override
	public void setAccessKey(char cKey)
	{
		aTagInput.setAccessKey(cKey);
	}

	/***************************************
	 * {@inheritDoc}
	 */
	@Override
	public void setEnabled(boolean bEnabled)
	{
		aTagInput.setEnabled(bEnabled);

		if (bEnabled)
		{
			removeStyleDependentName("disabled");
		}
		else
		{
			addStyleDependentName("disabled");
		}
	}

	/***************************************
	 * @see Focusable#setFocus(boolean)
	 */
	@Override
	public void setFocus(boolean bFocused)
	{
		aTagInput.setFocus(bFocused);
	}

	/***************************************
	 * @see Focusable#setTabIndex(int)
	 */
	@Override
	public void setTabIndex(int nIndex)
	{
		aTagInput.setTabIndex(nIndex);
	}

	/***************************************
	 * Deletes all selected tags.
	 */
	void deleteSelectedTags()
	{
		Set<Widget> aDeletedTagDisplays = new HashSet<>();

		aDeletedTags = new HashSet<>();

		for (Widget rChild : aMainPanel)
		{
			if (rChild instanceof TagDisplay)
			{
				TagDisplay rTagDisplay = (TagDisplay) rChild;

				if (rTagDisplay.isSelected())
				{
					aDeletedTagDisplays.add(rChild);
					aDeletedTags.add(((TagDisplay) rChild).getText());
				}
			}
		}

		for (Widget rChild : aDeletedTagDisplays)
		{
			aMainPanel.remove(rChild);
		}

		ValueChangeEvent.fire(this, getTags());
	}

	/***************************************
	 * Deletes a tag widget from this instance after querying the user for
	 * confirmation.
	 *
	 * @param rTagDisplay The tag widget to delete
	 */
	void deleteTag(TagDisplay rTagDisplay)
	{
		aDeletedTags = new HashSet<>();
		aDeletedTags.add(rTagDisplay.getText());

		aMainPanel.remove(rTagDisplay);
		ValueChangeEvent.fire(this, getTags());
	}

	/***************************************
	 * Sets the selected state for all tags.
	 *
	 * @param bSelected The new selected state
	 */
	void setTagsSelected(boolean bSelected)
	{
		for (Widget rChild : aMainPanel)
		{
			if (rChild instanceof TagDisplay)
			{
				((TagDisplay) rChild).setSelected(bSelected);
			}
		}
	}

	/***************************************
	 * Internal method to add a tag from an input value.
	 *
	 * @param sInput The input value
	 */
	private void addTagFromInput(String sInput)
	{
		if (sInput.length() > 0)
		{
			addTag(sInput);
			aTagInput.setValue("");
			ValueChangeEvent.fire(this, getTags());
		}
	}

	//~ Inner Classes ----------------------------------------------------------

	/********************************************************************
	 * A display widget for a single tag.
	 *
	 * @author eso
	 */
	private class TagDisplay extends Grid implements ClickHandler
	{
		//~ Instance fields ----------------------------------------------------

		private Label   rDeleteWidget;
		private boolean bSelected;

		//~ Constructors -------------------------------------------------------

		/***************************************
		 * Creates a new instance with a certain tag text
		 *
		 * @param sText The tag text
		 */
		public TagDisplay(String sText)
		{
			super(1, 2);

			rDeleteWidget = new Label();
			setText(0, 0, sText);
			setWidget(0, 1, rDeleteWidget);
			setSelected(false);

			setStylePrimaryName(EWT.CSS.ewtTagDisplay());
			addClickHandler(this);
		}

		//~ Methods ------------------------------------------------------------

		/***************************************
		 * Returns the text that is displayed.
		 *
		 * @return The displayed text
		 */
		public String getText()
		{
			return getText(0, 0);
		}

		/***************************************
		 * Returns the selected state of this instance.
		 *
		 * @return The selected state
		 */
		public final boolean isSelected()
		{
			return bSelected;
		}

		/***************************************
		 * @see ClickHandler#onClick(ClickEvent)
		 */
		@Override
		public void onClick(ClickEvent rEvent)
		{
			if (aTagInput.isEnabled())
			{
				if (bSelected)
				{
					if (getCellForEvent(rEvent).getCellIndex() == 1)
					{
						deleteSelectedTags();
					}
				}
				else
				{
					if (!rEvent.isControlKeyDown())
					{
						setTagsSelected(false);
					}

					setSelected(true);
				}
			}

			// prevent click handling in parent
			rEvent.stopPropagation();
		}

		/***************************************
		 * Sets the selected.
		 *
		 * @param bSelected The new selected
		 */
		void setSelected(boolean bSelected)
		{
			this.bSelected = bSelected;

			if (bSelected)
			{
				rDeleteWidget.setText("\u00D7"); // multiplication sign '×'
				addStyleDependentName("selected");
			}
			else
			{
				rDeleteWidget.setText("");
				removeStyleDependentName("selected");
			}
		}
	}
}
