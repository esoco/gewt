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
import de.esoco.ewt.impl.gwt.WidgetFactory;
import de.esoco.ewt.style.StyleData;

import de.esoco.lib.property.MultiSelection;
import de.esoco.lib.property.SingleSelection;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.HasChangeHandlers;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;


/********************************************************************
 * Base class for components that contain a list of strings. This control cannot
 * be instantiated directly but is a base class for list-based controls like
 * {@link List}.
 *
 * @author eso
 */
public abstract class ListControl extends Control implements SingleSelection,
															 MultiSelection
{
	//~ Methods ----------------------------------------------------------------

	/***************************************
	 * Adds a new item to the end of the list.
	 *
	 * @param sItem The item text
	 */
	public void add(String sItem)
	{
		getGwtListBox().addItem(getContext().expandResource(sItem));
	}

	/***************************************
	 * Adds a new item at a certain position in the list.
	 *
	 * @param sItem  sText The item text
	 * @param nIndex The position to insert the item after
	 */
	public void add(String sItem, int nIndex)
	{
		getGwtListBox().insertItem(getContext().expandResource(sItem), nIndex);
	}

	/***************************************
	 * Returns the text of a particular item.
	 *
	 * @param  nIndex The position of the item
	 *
	 * @return The item text
	 */
	public String getItem(int nIndex)
	{
		return getGwtListBox().getItemText(nIndex);
	}

	/***************************************
	 * Returns the number of items in the list.
	 *
	 * @return The item count
	 */
	public int getItemCount()
	{
		return getGwtListBox().getItemCount();
	}

	/***************************************
	 * Convenience method to returns the currently selected list item.
	 *
	 * @return The currently selected item or NULL for none
	 */
	public String getSelectedItem()
	{
		int nIndex = getSelectionIndex();

		return nIndex >= 0 ? getItem(nIndex) : null;
	}

	/***************************************
	 * Returns the index of the (first) selected list item.
	 *
	 * @return The selection index
	 */
	@Override
	public int getSelectionIndex()
	{
		return getGwtListBox().getSelectedIndex();
	}

	/***************************************
	 * Returns the indices of the currently selected values.
	 *
	 * @return The selection indices
	 */
	@Override
	public int[] getSelectionIndices()
	{
		IsListControlWidget rListBox = getGwtListBox();

		int   nItemCount = rListBox.getItemCount();
		int[] aIndices   = new int[nItemCount];
		int   nIndex     = 0;

		for (int i = 0; i < nItemCount; i++)
		{
			if (rListBox.isItemSelected(i))
			{
				aIndices[nIndex++] = i;
			}
		}

		int[] aResult = new int[nIndex];

		for (int i = 0; i < nIndex; i++)
		{
			aResult[i] = aIndices[i];
		}

		return aResult;
	}

	/***************************************
	 * Removes a certain item from the list.
	 *
	 * @param nIndex The position of the item to be removed
	 */
	public void remove(int nIndex)
	{
		getGwtListBox().removeItem(nIndex);
	}

	/***************************************
	 * Removes all items from the list.
	 */
	public void removeAll()
	{
		getGwtListBox().clear();
	}

	/***************************************
	 * Sets the index of the currently selected list element. For subclasses
	 * that allow complete deselection an index value of -1 will deselect all
	 * elements.
	 *
	 * @param nIndex The index of the new selected element or -1 for none
	 */
	@Override
	public void setSelection(int nIndex)
	{
		getGwtListBox().setSelectedIndex(nIndex);
	}

	/***************************************
	 * Selects the elements with the indices contained in the argument array.
	 *
	 * @param rIndices An array with the indices of the elements to select
	 */
	@Override
	public void setSelection(int[] rIndices)
	{
		IsListControlWidget rListBox = getGwtListBox();

		setSelection(-1);

		for (int nIndex : rIndices)
		{
			if (nIndex < rListBox.getItemCount())
			{
				rListBox.setItemSelected(nIndex, true);
			}
		}
	}

	/***************************************
	 * Selects all elements within the given range.
	 *
	 * @param nStart The start index (inclusive)
	 * @param nEnd   The end index (inclusive)
	 */
	@Override
	public void setSelection(int nStart, int nEnd)
	{
		IsListControlWidget rListBox = getGwtListBox();

		setSelection(-1);

		for (int i = nStart; i <= nEnd; i++)
		{
			rListBox.setItemSelected(i, true);
		}
	}

	/***************************************
	 * @see Component#createEventDispatcher()
	 */
	@Override
	ComponentEventDispatcher createEventDispatcher()
	{
		return new ListEventDispatcher();
	}

	/***************************************
	 * Returns the GWT {@link ListBox} instance of this component.
	 *
	 * @return The GWT list box
	 */
	IsListControlWidget getGwtListBox()
	{
		return (IsListControlWidget) getWidget();
	}

	//~ Inner Interfaces -------------------------------------------------------

	/********************************************************************
	 * Contains the required methods for list widgets.
	 *
	 * @author eso
	 */
	public static interface IsListControlWidget extends IsWidget, Focusable
	{
		//~ Methods ------------------------------------------------------------

		/***************************************
		 * @see com.google.gwt.user.client.ui.ListBox#addItem(String)
		 */
		public void addItem(String sItem);

		/***************************************
		 * @see com.google.gwt.user.client.ui.ListBox#clear()
		 */
		public void clear();

		/***************************************
		 * @see com.google.gwt.user.client.ui.ListBox#getItemCount()
		 */
		public int getItemCount();

		/***************************************
		 * @see com.google.gwt.user.client.ui.ListBox#getItemText(int)
		 */
		public String getItemText(int nIndex);

		/***************************************
		 * @see com.google.gwt.user.client.ui.ListBox#getSelectedIndex()
		 */
		public int getSelectedIndex();

		/***************************************
		 * @see com.google.gwt.user.client.ui.ListBox#getVisibleItemCount()
		 */
		public int getVisibleItemCount();

		/***************************************
		 * @see com.google.gwt.user.client.ui.ListBox#insertItem(String, int)
		 */
		public void insertItem(String sItem, int nIndex);

		/***************************************
		 * @see com.google.gwt.user.client.ui.ListBox#isItemSelected(int)
		 */
		public boolean isItemSelected(int nIndex);

		/***************************************
		 * @see com.google.gwt.user.client.ui.ListBox#removeItem(int)
		 */
		public void removeItem(int nIndex);

		/***************************************
		 * @see com.google.gwt.user.client.ui.ListBox#setItemSelected(int, boolean)
		 */
		public void setItemSelected(int nIndex, boolean bSelected);

		/***************************************
		 * @see com.google.gwt.user.client.ui.ListBox#setMultipleSelect(boolean)
		 */
		public void setMultipleSelect(boolean bHasMultiSelect);

		/***************************************
		 * @see com.google.gwt.user.client.ui.ListBox#setSelectedIndex(int)
		 */
		public void setSelectedIndex(int nIndex);

		/***************************************
		 * @see com.google.gwt.user.client.ui.ListBox#setVisibleItemCount(int)
		 */
		public void setVisibleItemCount(int nCount);
	}

	//~ Inner Classes ----------------------------------------------------------

	/********************************************************************
	 * A subclass of {@link ListBox} that implements the {@link
	 * IsListControlWidget} interface.
	 *
	 * @author eso
	 */
	public static class GwtListBox extends ListBox
		implements IsListControlWidget
	{
	}

	/********************************************************************
	 * Widget factory for this component.
	 *
	 * @author eso
	 */
	public static class ListControlWidgetFactory
		implements WidgetFactory<IsListControlWidget>
	{
		//~ Methods ------------------------------------------------------------

		/***************************************
		 * {@inheritDoc}
		 */
		@Override
		public IsListControlWidget createWidget(
			Component rComponent,
			StyleData rStyle)
		{
			return new GwtListBox();
		}
	}

	/********************************************************************
	 * Dispatcher for list-specific events.
	 *
	 * @author eso
	 */
	class ListEventDispatcher extends ComponentEventDispatcher
		implements ChangeHandler
	{
		//~ Methods ------------------------------------------------------------

		/***************************************
		 * {@inheritDoc}
		 */
		@Override
		public void onChange(ChangeEvent rEvent)
		{
			notifyEventHandler(EventType.SELECTION, rEvent);
		}

		/***************************************
		 * {@inheritDoc}
		 */
		@Override
		public void onClick(ClickEvent rEvent)
		{
			// remove default click handling to prevent action events
			// on selection
		}

		/***************************************
		 * {@inheritDoc}
		 */
		@Override
		public void onValueChange(ValueChangeEvent<Object> rEvent)
		{
			if (hasHandlerFor(EventType.SELECTION))
			{
				notifyEventHandler(EventType.SELECTION, rEvent);
			}
			else
			{
				super.onValueChange(rEvent);
			}
		}

		/***************************************
		 * {@inheritDoc}
		 */
		@Override
		@SuppressWarnings("unchecked")
		protected HandlerRegistration initEventDispatching(
			Widget    rWidget,
			EventType eEventType)
		{
			HandlerRegistration rHandler = null;

			if (eEventType == EventType.SELECTION)
			{
				if (rWidget instanceof HasChangeHandlers)
				{
					rHandler =
						((HasChangeHandlers) rWidget).addChangeHandler(this);
				}
				else if (rWidget instanceof HasValueChangeHandlers)
				{
					rHandler =
						((HasValueChangeHandlers<Object>) rWidget)
						.addValueChangeHandler(this);
				}
			}

			if (rHandler == null)
			{
				rHandler = super.initEventDispatching(rWidget, eEventType);
			}

			return rHandler;
		}
	}
}
