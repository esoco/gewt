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
package de.esoco.ewt.component;

import de.esoco.ewt.event.EventType;
import de.esoco.ewt.impl.gwt.WidgetFactory;
import de.esoco.ewt.style.StyleData;

import de.esoco.lib.model.DataModel;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SimpleHtmlSanitizer;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;

/**
 * A tree component.
 *
 * @author eso
 */
public class Tree extends Control {

	/**
	 * Returns the currently selected elements of this tree. The return
	 * value is
	 * an array of all elements in the tree model that have been selected.
	 * If no
	 * selection exists it will be empty.
	 *
	 * @return The selected model elements
	 */
	public Object[] getSelection() {
		TreeItem item =
			((com.google.gwt.user.client.ui.Tree) getWidget()).getSelectedItem();

		if (item != null) {
			return new Object[] { item.getUserObject() };
		} else {
			return new Object[] {};
		}
	}

	/**
	 * Sets the data model of the tree. The data elements of the model will be
	 * displayed as the top level items of the tree hierarchy. Model elements
	 * that also implement the interface DataModel will be displayed as
	 * nodes of
	 * the tree with their elements as sub-nodes. Elements that don't have
	 * children or elements that don't implement DataModel will be shown as
	 * leafs with no further children.
	 *
	 * @param dataModel A data model that contains the root items of the tree
	 */
	public void setData(DataModel<?> dataModel) {
		com.google.gwt.user.client.ui.Tree tree =
			(com.google.gwt.user.client.ui.Tree) getWidget();

		tree.clear();

		for (int i = 0; i < dataModel.getElementCount(); i++) {
			Object element = dataModel.getElement(i);

			tree.addItem(createTreeItem(element));
		}
	}

	/**
	 * @see Control#createEventDispatcher()
	 */
	@Override
	ComponentEventDispatcher createEventDispatcher() {
		return new TreeEventDispatcher();
	}

	/**
	 * Internal method to create a new tree item that corresponds to the
	 * contents of the given data element. If the data element is an
	 * instance of
	 * {@link DataModel} it's data elements will be added recursively as child
	 * items of the returned tree item.
	 *
	 * @param element The data element to create the tree item for
	 * @return A new tree item, containing child items as necessary
	 */
	private TreeItem createTreeItem(Object element) {
		SafeHtml html =
			SimpleHtmlSanitizer.getInstance().sanitize(element.toString());

		TreeItem treeItem = new TreeItem(html);

		treeItem.setUserObject(element);

		if (element instanceof DataModel<?>) {
			DataModel<?> children = (DataModel<?>) element;

			for (int i = 0; i < children.getElementCount(); i++) {
				treeItem.addItem(createTreeItem(children.getElement(i)));
			}
		}

		return treeItem;
	}

	/**
	 * Widget factory for this component.
	 *
	 * @author eso
	 */
	public static class TreeWidgetFactory implements WidgetFactory<Widget> {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Widget createWidget(Component component, StyleData style) {
			return new com.google.gwt.user.client.ui.Tree();
		}
	}

	/**
	 * Dispatcher for tree-specific events.
	 *
	 * @author eso
	 */
	class TreeEventDispatcher extends ComponentEventDispatcher
		implements SelectionHandler<TreeItem> {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void onSelection(SelectionEvent<TreeItem> event) {
			notifyEventHandler(EventType.SELECTION);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected HandlerRegistration initEventDispatching(Widget widget,
			EventType eventType) {
			if (eventType == EventType.SELECTION &&
				widget instanceof com.google.gwt.user.client.ui.Tree) {
				return ((com.google.gwt.user.client.ui.Tree) widget).addSelectionHandler(
					this);
			} else {
				return super.initEventDispatching(widget, eventType);
			}
		}
	}
}
