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
import de.esoco.ewt.style.StyleData;

import de.esoco.lib.property.Layout;

import com.google.gwt.user.client.ui.DeckLayoutPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IndexedPanel;
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
public class DeckPanel extends SwitchPanel
{
	//~ Constructors -----------------------------------------------------------

	/***************************************
	 * Creates a new instance.
	 *
	 * @param rParent The parent container
	 * @param rStyle  The panel style
	 */
	public DeckPanel(Container rParent, StyleData rStyle)
	{
		super(EWT.getLayoutFactory()
			  .createLayout(rParent, rStyle, Layout.DECK));
	}

	//~ Methods ----------------------------------------------------------------

	/***************************************
	 * Overridden to reset the base class version to the original implementation
	 * to support simple addition of components to deck panels which ignore the
	 * additional parameters of {@link #addPage(Component, String, boolean)}
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
	 * TODO: DOCUMENT ME!
	 *
	 * @author eso
	 */
	public static abstract class AbstractDeckPanelLayout<T extends IndexedPanel & HasWidgets>
		extends SwitchPanelLayout
	{
		//~ Instance fields ----------------------------------------------------

		private T rDeckPanel;

		//~ Methods ------------------------------------------------------------

		/***************************************
		 * {@inheritDoc}
		 */
		@Override
		public void addPage(Component rGroupComponent,
							String    sGroupTitle,
							boolean   bCloseable)
		{
			rDeckPanel.add(rGroupComponent.getWidget());
		}

		/***************************************
		 * {@inheritDoc}
		 */
		@Override
		public HasWidgets createLayoutContainer(
			Container rContainer,
			StyleData rStyle)
		{
			rDeckPanel = createDeckPanel(rContainer, rStyle);

			return rDeckPanel;
		}

		/***************************************
		 * {@inheritDoc}
		 */
		@Override
		public int getPageCount()
		{
			return rDeckPanel.getWidgetCount();
		}

		/***************************************
		 * {@inheritDoc}
		 */
		@Override
		public int getPageIndex(Component rGroupComponent)
		{
			return rDeckPanel.getWidgetIndex(rGroupComponent.getWidget());
		}

		/***************************************
		 * {@inheritDoc}
		 */
		@Override
		public void setPageTitle(int nIndex, String sTitle)
		{
			// ignored
		}

		/***************************************
		 * Must be implemented to create the deck panel widget.
		 *
		 * @see #createLayoutContainer(Container, StyleData)
		 */
		abstract T createDeckPanel(Container rContainer, StyleData rStyle);

		/***************************************
		 * Returns the deck panel widget of this instance.
		 *
		 * @return The deck panel widget
		 */
		final T getDeckPanel()
		{
			return rDeckPanel;
		}
	}

	/********************************************************************
	 * A deck layout implementation that is based on {@link DeckLayoutPanel}.
	 *
	 * @author eso
	 */
	public static class DeckLayoutPanelLayout
		extends AbstractDeckPanelLayout<DeckLayoutPanel>
	{
		//~ Methods ------------------------------------------------------------

		/***************************************
		 * {@inheritDoc}
		 */
		@Override
		public DeckLayoutPanel createDeckPanel(
			Container rContainer,
			StyleData rStyle)
		{
			return new DeckLayoutPanel();
		}

		/***************************************
		 * {@inheritDoc}
		 */
		@Override
		public int getSelectionIndex()
		{
			return getDeckPanel().getVisibleWidgetIndex();
		}

		/***************************************
		 * {@inheritDoc}
		 */
		@Override
		public void setSelection(int nIndex)
		{
			getDeckPanel().showWidget(nIndex);
		}
	}

	/********************************************************************
	 * A deck layout implementation that is based on {@link
	 * com.google.gwt.user.client.ui.DeckPanel}.
	 *
	 * @author eso
	 */
	public static class DeckPanelLayout
		extends AbstractDeckPanelLayout<com.google.gwt.user.client.ui.DeckPanel>
	{
		//~ Methods ------------------------------------------------------------

		/***************************************
		 * {@inheritDoc}
		 */
		@Override
		public com.google.gwt.user.client.ui.DeckPanel createDeckPanel(
			Container rContainer,
			StyleData rStyle)
		{
			return new com.google.gwt.user.client.ui.DeckPanel();
		}

		/***************************************
		 * {@inheritDoc}
		 */
		@Override
		public int getSelectionIndex()
		{
			return getDeckPanel().getVisibleWidget();
		}

		/***************************************
		 * {@inheritDoc}
		 */
		@Override
		public void setSelection(int nIndex)
		{
			getDeckPanel().showWidget(nIndex);
		}
	}
}
