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
package de.esoco.ewt.impl.gwt.table;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import de.esoco.lib.model.Callback;
import de.esoco.lib.model.DataModel;
import de.esoco.lib.model.HierarchicalDataModel;
import de.esoco.lib.model.RemoteDataModel;

/**
 * A widget that represents a tree node.
 *
 * @author eso
 */
class TreeNode extends Composite
	implements ClickHandler, Callback<RemoteDataModel<DataModel<?>>> {
	private final GwtTable table;

	private final TreeNode parent;

	private final TreeNode previous;

	private final int index;

	private final int level;

	private final HTML spacing = new HTML("&nbsp;");

	private final Image nodeControl = new Image(GwtTable.RES.imTreeLeaf());

	private final HTML cellText = new HTML("");

	private int directChildren;

	private int visibleChildren = 0;

	private DataModel<?> rowModel;

	private boolean expanded = false;

	private boolean changing = false;

	/**
	 * Creates a new instance.
	 *
	 * @param table    The table this node belongs to
	 * @param parent   The parent cell or NULL for root level cells
	 * @param previous The previous cell in the same level or NULL for the
	 *                    first
	 *                 cell
	 */
	TreeNode(GwtTable table, TreeNode parent, TreeNode previous) {
		this.table = table;
		this.parent = parent;
		this.previous = previous;

		index = previous != null ? previous.index + 1 : 0;
		level = parent != null ? parent.level + 1 : 0;

		FlowPanel panel = new FlowPanel();

		panel.add(spacing);
		panel.add(nodeControl);
		panel.add(cellText);

		nodeControl.addClickHandler(this);

		panel.setStylePrimaryName(GwtTable.CSS.ewtTreeNode());
		spacing.addStyleName(GwtTable.CSS.ewtTreeNode());
		nodeControl.addStyleName(GwtTable.CSS.ewtTreeNode());
		cellText.addStyleName(GwtTable.CSS.ewtTreeNode());

		initWidget(panel);
	}

	/**
	 * Returns the count of this node's direct child nodes.
	 *
	 * @return The count of direct children
	 */
	public final int getDirectChildren() {
		return directChildren;
	}

	/**
	 * Returns the parent of this node.
	 *
	 * @return The parent node
	 */
	public final TreeNode getParentNode() {
		return parent;
	}

	/**
	 * Returns the row data model of this node.
	 *
	 * @return The row data model
	 */
	public final DataModel<?> getRowModel() {
		return rowModel;
	}

	/**
	 * Returns the count of this node's visible child nodes.
	 *
	 * @return The count of visible children
	 */
	public final int getVisibleChildren() {
		return visibleChildren;
	}

	/**
	 * Checks whether this node is expanded or not.
	 *
	 * @return TRUE if this node is currently expanded
	 */
	public final boolean isExpanded() {
		return expanded;
	}

	/**
	 * Handles clicks on node images.
	 *
	 * @see ClickHandler#onClick(ClickEvent)
	 */
	@Override
	public void onClick(ClickEvent event) {
		event.stopPropagation();

		if (!changing) {
			if (expanded) {
				table.collapseNode(this);
			} else {
				changing = true;
				table.expandNode(this);
			}
		}
	}

	/**
	 * Handles an error on a query of node children.
	 *
	 * @see Callback#onError(Throwable)
	 */
	@Override
	public void onError(Throwable e) {
		changing = false;
		table.onError(e);
	}

	/**
	 * Handles the successful query of node children.
	 *
	 * @see Callback#onSuccess(Object)
	 */
	@Override
	public void onSuccess(RemoteDataModel<DataModel<?>> childModels) {
		table.hideBusyIndicator();
		table.addChildRows(this, childModels);
	}

	/**
	 * Recursively calculates the absolute index of a certain tree cell with
	 * consideration of it's hierarchy. This takes into account whether parent
	 * and previous cells are expanded or collapsed.
	 *
	 * @return The absolute index of the cell
	 */
	int getAbsoluteIndex() {
		int result;

		if (previous != null) {
			result = previous.getAbsoluteIndex() + 1;
			result += previous.visibleChildren;
		} else if (parent != null) {
			result = parent.getAbsoluteIndex() + 1;
		} else {
			result = 0;
		}

		return result;
	}

	/**
	 * Updates the hierarchical parameters of this node.
	 *
	 * @param expanded TRUE to expand the node, FALSE to collapse
	 */
	void setExpanded(boolean expanded) {
		updateVisibleChildren(expanded ? directChildren : -visibleChildren);
		nodeControl.setResource(expanded ?
		                        GwtTable.RES.imTreeCollapse() :
		                        GwtTable.RES.imTreeExpand());
		this.expanded = expanded;
		changing = false;
	}

	/**
	 * Updates this cell with the given values.
	 *
	 * @param row  The data model of the row represented by this cell
	 * @param text The new text for the cell
	 */
	void update(DataModel<?> row, String text) {
		rowModel = row;

		if (row instanceof HierarchicalDataModel<?>) {
			DataModel<? extends DataModel<?>> children =
				((HierarchicalDataModel<?>) row).getChildModels();

			directChildren = children != null ? children.getElementCount() : 0;
		}

		cellText.setHTML("&nbsp;" + (text != null ? text : ""));
		spacing.setWidth(level + "em");

		nodeControl.setResource(directChildren > 0 ?
		                        GwtTable.RES.imTreeExpand() :
		                        GwtTable.RES.imTreeLeaf());
	}

	/**
	 * Recursively modifies the number of visible children of this node and
	 * it's
	 * parents.
	 *
	 * @param change The number of children that has changed
	 */
	void updateVisibleChildren(int change) {
		visibleChildren += change;

		if (parent != null) {
			parent.updateVisibleChildren(change);
		}
	}
}
