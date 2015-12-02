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
package de.esoco.ewt.component;

import de.esoco.ewt.layout.GenericLayout;

import com.google.gwt.user.client.ui.DeckLayoutPanel;


/********************************************************************
 * A panel that can contain multiple components of which only one is visible at
 * a time like the cards in a deck of cards. The currently visible component can
 * be set with the method {@link #showComponent(int)}. As the layout of a deck
 * panel is defined by it's implementation setting a layout on an instance has
 * no effect.
 *
 * @author eso
 */
public class DeckPanel extends Panel
{
	//~ Constructors -----------------------------------------------------------

	/***************************************
	 * Creates a new instance.
	 */
	public DeckPanel()
	{
		super(new DeckLayoutPanel());
	}

	//~ Methods ----------------------------------------------------------------

	/***************************************
	 * Returns the index of the currently visible component.
	 *
	 * @return The index of the currently visible component
	 */
	public int getCurrentComponent()
	{
		return ((DeckLayoutPanel) getWidget()).getVisibleWidgetIndex();
	}

	/***************************************
	 * Overridden to do nothing because the layout is defined by the panel
	 * implementation.
	 *
	 * @see Container#setLayout(GenericLayout)
	 */
	@Override
	public void setLayout(GenericLayout rLayout)
	{
	}

	/***************************************
	 * Makes a certain component of this panel visible.
	 *
	 * @param nIndex The index of the component to show
	 */
	public void showComponent(int nIndex)
	{
		((DeckLayoutPanel) getWidget()).showWidget(nIndex);
	}
}
