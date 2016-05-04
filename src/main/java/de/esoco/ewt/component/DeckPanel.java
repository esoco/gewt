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

import de.esoco.ewt.style.StyleData;

import com.google.gwt.user.client.ui.DeckLayoutPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;


/********************************************************************
 * A panel that can contain multiple components of which only one is visible at
 * a time like the cards in a deck of cards. The currently visible component can
 * be set with the method {@link #showComponent(int)}. As the layout of a deck
 * panel is defined by it's implementation setting a layout on an instance has
 * no effect.
 *
 * @author eso
 */
public class DeckPanel extends GroupPanel
{
	//~ Constructors -----------------------------------------------------------

	/***************************************
	 * Creates a new instance.
	 */
	public DeckPanel()
	{
		super(new DeckPanelLayout());
	}

	//~ Methods ----------------------------------------------------------------

	/***************************************
	 * Overridden to reset the base class version to the original implementation
	 * to support simple addition of components to deck panels which ignore the
	 * additional parameters of {@link #addGroup(Component, String, boolean)}
	 * anyway.
	 *
	 * @see Panel#addWidget(HasWidgets, Widget, StyleData)
	 */
	@Override
	void addWidget(HasWidgets rContainer, Widget rWidget, StyleData rStyleData)
	{
		getLayout().addWidget(getContainerWidget(), rWidget, rStyleData, -1);
	}

	//~ Inner Classes ----------------------------------------------------------

	/********************************************************************
	 * The default layout for this panel.
	 *
	 * @author eso
	 */
	public static class DeckPanelLayout extends GroupPanelLayout
	{
		//~ Instance fields ----------------------------------------------------

		private DeckLayoutPanel aDeckLayoutPanel;

		//~ Methods ------------------------------------------------------------

		/***************************************
		 * {@inheritDoc}
		 */
		@Override
		public void addGroup(Component rGroupComponent,
							 String    sGroupTitle,
							 boolean   bCloseable)
		{
			aDeckLayoutPanel.add(rGroupComponent.getWidget());
		}

		/***************************************
		 * {@inheritDoc}
		 */
		@Override
		public HasWidgets createLayoutContainer(
			Container rContainer,
			StyleData rStyle)
		{
			aDeckLayoutPanel = new DeckLayoutPanel();

			return aDeckLayoutPanel;
		}

		/***************************************
		 * {@inheritDoc}
		 */
		@Override
		public int getGroupCount()
		{
			return aDeckLayoutPanel.getWidgetCount();
		}

		/***************************************
		 * {@inheritDoc}
		 */
		@Override
		public int getGroupIndex(Component rGroupComponent)
		{
			return aDeckLayoutPanel.getWidgetIndex(rGroupComponent.getWidget());
		}

		/***************************************
		 * {@inheritDoc}
		 */
		@Override
		public int getSelectionIndex()
		{
			return aDeckLayoutPanel.getVisibleWidgetIndex();
		}

		/***************************************
		 * {@inheritDoc}
		 */
		@Override
		public void setGroupTitle(int nIndex, String sTitle)
		{
			// ignored
		}

		/***************************************
		 * {@inheritDoc}
		 */
		@Override
		public void setSelection(int nIndex)
		{
			aDeckLayoutPanel.showWidget(nIndex);
		}
	}
}
