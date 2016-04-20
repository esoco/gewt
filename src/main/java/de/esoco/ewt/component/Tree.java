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

import de.esoco.ewt.EWT;
import de.esoco.ewt.UserInterfaceContext;
import de.esoco.ewt.event.EventType;
import de.esoco.ewt.impl.gwt.WidgetFactory;
import de.esoco.ewt.style.StyleData;

import de.esoco.lib.model.DataModel;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SimpleHtmlSanitizer;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;


/********************************************************************
 * A tree component.
 *
 * @author eso
 */
public class Tree extends Control
{
	//~ Static fields/initializers ---------------------------------------------

	static
	{
		EWT.registerComponentWidgetFactory(Tree.class,
										   new TreeWidgetFactory(),
										   false);
	}

	//~ Methods ----------------------------------------------------------------

	/***************************************
	 * Returns the currently selected elements of this tree. The return value is
	 * an array of all elements in the tree model that have been selected. If no
	 * selection exists it will be empty.
	 *
	 * @return The selected model elements
	 */
	public Object[] getSelection()
	{
		TreeItem rItem =
			((com.google.gwt.user.client.ui.Tree) getWidget())
			.getSelectedItem();

		if (rItem != null)
		{
			return new Object[] { rItem.getUserObject() };
		}
		else
		{
			return new Object[] {};
		}
	}

	/***************************************
	 * Sets the data model of the tree. The data elements of the model will be
	 * displayed as the top level items of the tree hierarchy. Model elements
	 * that also implement the interface DataModel will be displayed as nodes of
	 * the tree with their elements as sub-nodes. Elements that don't have
	 * children or elements that don't implement DataModel will be shown as
	 * leafs with no further children.
	 *
	 * @param rDataModel A data model that contains the root items of the tree
	 */
	public void setData(DataModel<?> rDataModel)
	{
		com.google.gwt.user.client.ui.Tree rTree =
			(com.google.gwt.user.client.ui.Tree) getWidget();

		rTree.clear();

		for (int i = 0; i < rDataModel.getElementCount(); i++)
		{
			Object rElement = rDataModel.getElement(i);

			rTree.addItem(createTreeItem(rElement));
		}
	}

	/***************************************
	 * @see Control#createEventDispatcher()
	 */
	@Override
	ComponentEventDispatcher createEventDispatcher()
	{
		return new TreeEventDispatcher();
	}

	/***************************************
	 * Internal method to create a new tree item that corresponds to the
	 * contents of the given data element. If the data element is an instance of
	 * {@link DataModel} it's data elements will be added recursively as child
	 * items of the returned tree item.
	 *
	 * @param  rElement The data element to create the tree item for
	 *
	 * @return A new tree item, containing child items as necessary
	 */
	private TreeItem createTreeItem(Object rElement)
	{
		SafeHtml aHtml =
			SimpleHtmlSanitizer.getInstance().sanitize(rElement.toString());

		TreeItem aTreeItem = new TreeItem(aHtml);

		aTreeItem.setUserObject(rElement);

		if (rElement instanceof DataModel<?>)
		{
			DataModel<?> rChildren = (DataModel<?>) rElement;

			for (int i = 0; i < rChildren.getElementCount(); i++)
			{
				aTreeItem.addItem(createTreeItem(rChildren.getElement(i)));
			}
		}

		return aTreeItem;
	}

	//~ Inner Classes ----------------------------------------------------------

	/********************************************************************
	 * Widget factory for this component.
	 *
	 * @author eso
	 */
	public static class TreeWidgetFactory implements WidgetFactory<Widget>
	{
		//~ Methods ------------------------------------------------------------

		/***************************************
		 * {@inheritDoc}
		 */
		@Override
		public Widget createWidget(
			UserInterfaceContext rContext,
			StyleData			 rStyle)
		{
			return new com.google.gwt.user.client.ui.Tree();
		}
	}

	/********************************************************************
	 * Dispatcher for tree-specific events.
	 *
	 * @author eso
	 */
	class TreeEventDispatcher extends ComponentEventDispatcher
		implements SelectionHandler<TreeItem>
	{
		//~ Methods ------------------------------------------------------------

		/***************************************
		 * @see SelectionHandler#onSelection(SelectionEvent)
		 */
		@Override
		public void onSelection(SelectionEvent<TreeItem> rEvent)
		{
			notifyEventHandler(EventType.SELECTION);
		}

		/***************************************
		 * @see ControlEventDispatcher#initEventDispatching(Widget)
		 */
		@Override
		void initEventDispatching(Widget rWidget)
		{
			super.initEventDispatching(rWidget);

			((com.google.gwt.user.client.ui.Tree) rWidget).addSelectionHandler(this);
		}
	}
}
