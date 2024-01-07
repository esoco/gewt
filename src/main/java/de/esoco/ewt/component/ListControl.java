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
import de.esoco.ewt.event.EventType;
import de.esoco.ewt.impl.gwt.WidgetFactory;
import de.esoco.ewt.style.StyleData;
import de.esoco.lib.property.MultiSelection;
import de.esoco.lib.property.SingleSelection;

/**
 * Base class for components that contain a list of strings. This control cannot
 * be instantiated directly but is a base class for list-based controls like
 * {@link List}.
 *
 * @author eso
 */
public abstract class ListControl extends Control
	implements SingleSelection, MultiSelection {

	/**
	 * Adds a new item to the end of the list.
	 *
	 * @param item The item text
	 */
	public void add(String item) {
		getGwtListBox().addItem(getContext().expandResource(item));
	}

	/**
	 * Adds a new item at a certain position in the list.
	 *
	 * @param item  text The item text
	 * @param index The position to insert the item after
	 */
	public void add(String item, int index) {
		getGwtListBox().insertItem(getContext().expandResource(item), index);
	}

	/**
	 * Returns the text of a particular item.
	 *
	 * @param index The position of the item
	 * @return The item text
	 */
	public String getItem(int index) {
		return getGwtListBox().getItemText(index);
	}

	/**
	 * Returns the number of items in the list.
	 *
	 * @return The item count
	 */
	public int getItemCount() {
		return getGwtListBox().getItemCount();
	}

	/**
	 * Convenience method to returns the currently selected list item.
	 *
	 * @return The currently selected item or NULL for none
	 */
	public String getSelectedItem() {
		int index = getSelectionIndex();

		return index >= 0 ? getItem(index) : null;
	}

	/**
	 * Returns the index of the (first) selected list item.
	 *
	 * @return The selection index
	 */
	@Override
	public int getSelectionIndex() {
		return getGwtListBox().getSelectedIndex();
	}

	/**
	 * Returns the indices of the currently selected values.
	 *
	 * @return The selection indices
	 */
	@Override
	public int[] getSelectionIndices() {
		IsListControlWidget listBox = getGwtListBox();

		int itemCount = listBox.getItemCount();
		int[] indices = new int[itemCount];
		int index = 0;

		for (int i = 0; i < itemCount; i++) {
			if (listBox.isItemSelected(i)) {
				indices[index++] = i;
			}
		}

		int[] result = new int[index];

		System.arraycopy(indices, 0, result, 0, index);

		return result;
	}

	/**
	 * Removes a certain item from the list.
	 *
	 * @param index The position of the item to be removed
	 */
	public void remove(int index) {
		getGwtListBox().removeItem(index);
	}

	/**
	 * Removes all items from the list.
	 */
	public void removeAll() {
		getGwtListBox().clear();
	}

	/**
	 * Sets the index of the currently selected list element. For subclasses
	 * that allow complete deselection an index value of -1 will deselect all
	 * elements.
	 *
	 * @param index The index of the new selected element or -1 for none
	 */
	@Override
	public void setSelection(int index) {
		getGwtListBox().setSelectedIndex(index);
	}

	/**
	 * Selects the elements with the indices contained in the argument array.
	 *
	 * @param indices An array with the indices of the elements to select
	 */
	@Override
	public void setSelection(int[] indices) {
		IsListControlWidget listBox = getGwtListBox();

		setSelection(-1);

		for (int index : indices) {
			if (index < listBox.getItemCount()) {
				listBox.setItemSelected(index, true);
			}
		}
	}

	/**
	 * Selects all elements within the given range.
	 *
	 * @param start The start index (inclusive)
	 * @param end   The end index (inclusive)
	 */
	@Override
	public void setSelection(int start, int end) {
		IsListControlWidget listBox = getGwtListBox();

		setSelection(-1);

		for (int i = start; i <= end; i++) {
			listBox.setItemSelected(i, true);
		}
	}

	/**
	 * @see Component#createEventDispatcher()
	 */
	@Override
	ComponentEventDispatcher createEventDispatcher() {
		return new ListEventDispatcher();
	}

	/**
	 * Returns the GWT {@link ListBox} instance of this component.
	 *
	 * @return The GWT list box
	 */
	IsListControlWidget getGwtListBox() {
		return (IsListControlWidget) getWidget();
	}

	/**
	 * Contains the required methods for list widgets.
	 *
	 * @author eso
	 */
	public interface IsListControlWidget extends IsWidget, Focusable {

		/**
		 * @see com.google.gwt.user.client.ui.ListBox#addItem(String)
		 */
		void addItem(String item);

		/**
		 * @see com.google.gwt.user.client.ui.ListBox#clear()
		 */
		void clear();

		/**
		 * @see com.google.gwt.user.client.ui.ListBox#getItemCount()
		 */
		int getItemCount();

		/**
		 * @see com.google.gwt.user.client.ui.ListBox#getItemText(int)
		 */
		String getItemText(int index);

		/**
		 * @see com.google.gwt.user.client.ui.ListBox#getSelectedIndex()
		 */
		int getSelectedIndex();

		/**
		 * @see com.google.gwt.user.client.ui.ListBox#getVisibleItemCount()
		 */
		int getVisibleItemCount();

		/**
		 * @see com.google.gwt.user.client.ui.ListBox#insertItem(String, int)
		 */
		void insertItem(String item, int index);

		/**
		 * @see com.google.gwt.user.client.ui.ListBox#isItemSelected(int)
		 */
		boolean isItemSelected(int index);

		/**
		 * @see com.google.gwt.user.client.ui.ListBox#removeItem(int)
		 */
		void removeItem(int index);

		/**
		 * @see com.google.gwt.user.client.ui.ListBox#setItemSelected(int,
		 * boolean)
		 */
		void setItemSelected(int index, boolean selected);

		/**
		 * @see com.google.gwt.user.client.ui.ListBox#setMultipleSelect(boolean)
		 */
		void setMultipleSelect(boolean hasMultiSelect);

		/**
		 * @see com.google.gwt.user.client.ui.ListBox#setSelectedIndex(int)
		 */
		void setSelectedIndex(int index);

		/**
		 * @see com.google.gwt.user.client.ui.ListBox#setVisibleItemCount(int)
		 */
		void setVisibleItemCount(int count);
	}

	/**
	 * A subclass of {@link ListBox} that implements the
	 * {@link IsListControlWidget} interface.
	 *
	 * @author eso
	 */
	public static class GwtListBox extends ListBox
		implements IsListControlWidget {
	}

	/**
	 * Widget factory for this component.
	 *
	 * @author eso
	 */
	public static class ListControlWidgetFactory
		implements WidgetFactory<IsListControlWidget> {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public IsListControlWidget createWidget(Component component,
			StyleData style) {
			return new GwtListBox();
		}
	}

	/**
	 * Dispatcher for list-specific events.
	 *
	 * @author eso
	 */
	class ListEventDispatcher extends ComponentEventDispatcher
		implements ChangeHandler {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void onChange(ChangeEvent event) {
			notifyEventHandler(EventType.SELECTION, event);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void onClick(ClickEvent event) {
			// remove default click handling to prevent action events
			// on selection
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void onValueChange(ValueChangeEvent<Object> event) {
			if (hasHandlerFor(EventType.SELECTION)) {
				notifyEventHandler(EventType.SELECTION, event);
			} else {
				super.onValueChange(event);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		@SuppressWarnings("unchecked")
		protected HandlerRegistration initEventDispatching(Widget widget,
			EventType eventType) {
			HandlerRegistration handler = null;

			if (eventType == EventType.SELECTION) {
				if (widget instanceof HasChangeHandlers) {
					handler =
						((HasChangeHandlers) widget).addChangeHandler(this);
				} else if (widget instanceof HasValueChangeHandlers) {
					handler =
						((HasValueChangeHandlers<Object>) widget).addValueChangeHandler(
							this);
				}
			}

			if (handler == null) {
				handler = super.initEventDispatching(widget, eventType);
			}

			return handler;
		}
	}
}
