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

import de.esoco.ewt.layout.GenericLayout;
import de.esoco.ewt.style.StyleData;

import de.esoco.lib.property.SingleSelection;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;


/********************************************************************
 * A base class for panels that group components in some way like tabs or
 * stacks.
 *
 * @author eso
 */
public abstract class GroupPanel extends Panel implements SingleSelection
{
	//~ Methods ----------------------------------------------------------------

	/***************************************
	 * Adds a new group component to this panel.
	 *
	 * @param rGroupComponent The component to be placed in a group
	 * @param sGroupTitle     The title of the group
	 * @param bCloseable      TRUE if the group can be closed by the user
	 */
	public abstract void addGroup(Component rGroupComponent,
								  String    sGroupTitle,
								  boolean   bCloseable);

	/***************************************
	 * Returns the number of groups contained in this instance.
	 *
	 * @return The group count
	 */
	public abstract int getGroupCount();

	/***************************************
	 * Returns the index of the group in which a certain component is displayed.
	 *
	 * @param  rGroupComponent The component
	 *
	 * @return The group index or -1 if the given component is not in a group
	 */
	public abstract int getGroupIndex(Component rGroupComponent);

	/***************************************
	 * Sets the title of a particular group.
	 *
	 * @param nIndex The group index
	 * @param sTitle The new title
	 */
	public abstract void setGroupTitle(int nIndex, String sTitle);

	/***************************************
	 * Overridden to do nothing because the layout is defined by the panel
	 * implementation.
	 *
	 * @see Panel#setLayout(GenericLayout)
	 */
	@Override
	public void setLayout(GenericLayout rLayout)
	{
	}

	/***************************************
	 * Overridden to do nothing. Group components must be added by invoking the
	 * method {@link #addGroup(Component, String, boolean)}.
	 *
	 * @see Panel#addWidget(HasWidgets, Widget, StyleData)
	 */
	@Override
	void addWidget(HasWidgets rContainer, Widget rWidget, StyleData rStyleData)
	{
	}
}
