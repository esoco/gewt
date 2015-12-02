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

import de.esoco.lib.model.Callback;
import de.esoco.lib.model.DataModel;
import de.esoco.lib.model.HierarchicalDataModel;
import de.esoco.lib.model.RemoteDataModel;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;


/********************************************************************
 * A widget that represents a tree node.
 *
 * @author eso
 */
class TreeNode extends Composite
	implements ClickHandler, Callback<RemoteDataModel<DataModel<?>>>
{
	private final GwtTable rTable;
	private final TreeNode rParent;
	private final TreeNode rPrevious;

	private int nIndex;
	private int nLevel;
	private int nDirectChildren;
	private int nVisibleChildren = 0;

	private DataModel<?> rRowModel;
	private boolean		 bExpanded = false;
	private boolean		 bChanging = false;

	private HTML  aSpacing     = new HTML("&nbsp;");
	private Image aNodeControl = new Image(GwtTable.RES.imTreeLeaf());
	private HTML  aCellText    = new HTML("");

	/***************************************
	 * Creates a new instance.
	 *
	 * @param rTable    The table this node belongs to
	 * @param rParent   The parent cell or NULL for root level cells
	 * @param rPrevious The previous cell in the same level or NULL for the
	 *                  first cell
	 */
	TreeNode(GwtTable rTable, TreeNode rParent, TreeNode rPrevious)
	{
		this.rTable    = rTable;
		this.rParent   = rParent;
		this.rPrevious = rPrevious;

		nIndex = rPrevious != null ? rPrevious.nIndex + 1 : 0;
		nLevel = rParent != null ? rParent.nLevel + 1 : 0;

		FlowPanel aPanel = new FlowPanel();

		aPanel.add(aSpacing);
		aPanel.add(aNodeControl);
		aPanel.add(aCellText);

		aNodeControl.addClickHandler(this);

		aPanel.setStylePrimaryName(GwtTable.CSS.ewtTreeNode());
		aSpacing.addStyleName(GwtTable.CSS.ewtTreeNode());
		aNodeControl.addStyleName(GwtTable.CSS.ewtTreeNode());
		aCellText.addStyleName(GwtTable.CSS.ewtTreeNode());

		initWidget(aPanel);
	}

	/***************************************
	 * Returns the count of this node's direct child nodes.
	 *
	 * @return The count of direct children
	 */
	public final int getDirectChildren()
	{
		return nDirectChildren;
	}

	/***************************************
	 * Returns the parent of this node.
	 *
	 * @return The parent node
	 */
	public final TreeNode getParentNode()
	{
		return rParent;
	}

	/***************************************
	 * Returns the row data model of this node.
	 *
	 * @return The row data model
	 */
	public final DataModel<?> getRowModel()
	{
		return rRowModel;
	}

	/***************************************
	 * Returns the count of this node's visible child nodes.
	 *
	 * @return The count of visible children
	 */
	public final int getVisibleChildren()
	{
		return nVisibleChildren;
	}

	/***************************************
	 * Checks whether this node is expanded or not.
	 *
	 * @return TRUE if this node is currently expanded
	 */
	public final boolean isExpanded()
	{
		return bExpanded;
	}

	/***************************************
	 * Handles clicks on node images.
	 *
	 * @see ClickHandler#onClick(ClickEvent)
	 */
	@Override
	public void onClick(ClickEvent rEvent)
	{
		rEvent.stopPropagation();

		if (!bChanging)
		{
			if (bExpanded)
			{
				rTable.collapseNode(this);
			}
			else
			{
				bChanging = true;
				rTable.expandNode(this);
			}
		}
	}

	/***************************************
	 * Handles an error on a query of node children.
	 *
	 * @see Callback#onError(Throwable)
	 */
	@Override
	public void onError(Throwable e)
	{
		bChanging = false;
		rTable.onError(e);
	}

	/***************************************
	 * Handles the successful query of node children.
	 *
	 * @see Callback#onSuccess(Object)
	 */
	@Override
	public void onSuccess(RemoteDataModel<DataModel<?>> rChildModels)
	{
		rTable.hideBusyIndicator();
		rTable.addChildRows(this, rChildModels);
	}

	/***************************************
	 * Recursively calculates the absolute index of a certain tree cell with
	 * consideration of it's hierarchy. This takes into account whether parent
	 * and previous cells are expanded or collapsed.
	 *
	 * @return The absolute index of the cell
	 */
	int getAbsoluteIndex()
	{
		int nResult;

		if (rPrevious != null)
		{
			nResult =  rPrevious.getAbsoluteIndex() + 1;
			nResult += rPrevious.nVisibleChildren;
		}
		else if (rParent != null)
		{
			nResult = rParent.getAbsoluteIndex() + 1;
		}
		else
		{
			nResult = 0;
		}

		return nResult;
	}

	/***************************************
	 * Updates the hierarchical parameters of this node.
	 *
	 * @param bExpanded TRUE to expand the node, FALSE to collapse
	 */
	void setExpanded(boolean bExpanded)
	{
		updateVisibleChildren(bExpanded ? nDirectChildren : -nVisibleChildren);
		aNodeControl.setResource(bExpanded ? GwtTable.RES.imTreeCollapse()
										   : GwtTable.RES.imTreeExpand());
		this.bExpanded = bExpanded;
		bChanging	   = false;
	}

	/***************************************
	 * Updates this cell with the given values.
	 *
	 * @param rRow  The data model of the row represented by this cell
	 * @param sText The new text for the cell
	 */
	void update(DataModel<?> rRow, String sText)
	{
		rRowModel = rRow;

		if (rRow instanceof HierarchicalDataModel<?>)
		{
			DataModel<? extends DataModel<?>> rChildren =
				((HierarchicalDataModel<?>) rRow).getChildModels();

			nDirectChildren =
				rChildren != null ? rChildren.getElementCount() : 0;
		}

		int nSpacing = nLevel;

		aCellText.setHTML("&nbsp;" + (sText != null ? sText : ""));
		aSpacing.setWidth(nSpacing + "em");

		aNodeControl.setResource(nDirectChildren > 0
								 ? GwtTable.RES.imTreeExpand()
								 : GwtTable.RES.imTreeLeaf());
	}

	/***************************************
	 * Recursively modifies the number of visible children of this node and it's
	 * parents.
	 *
	 * @param nChange The number of children that has changed
	 */
	void updateVisibleChildren(int nChange)
	{
		nVisibleChildren += nChange;

		if (rParent != null)
		{
			rParent.updateVisibleChildren(nChange);
		}
	}
}
