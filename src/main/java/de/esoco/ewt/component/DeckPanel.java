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
import de.esoco.ewt.impl.gwt.WidgetFactory;
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
	//~ Static fields/initializers ---------------------------------------------

	static
	{
		EWT.registerComponentWidgetFactory(DeckPanel.class,
										   new DeckPanelWidgetFactory(),
										   false);
	}

	//~ Constructors -----------------------------------------------------------

	/***************************************
	 * Creates a new instance.
	 */
	public DeckPanel()
	{
	}

	//~ Methods ----------------------------------------------------------------

	/***************************************
	 * {@inheritDoc}
	 */
	@Override
	public void addGroup(Component rGroupComponent,
						 String    sGroupTitle,
						 boolean   bCloseable)
	{
		getDeckPanel().add(rGroupComponent.getWidget());
	}

	/***************************************
	 * {@inheritDoc}
	 */
	@Override
	public int getGroupCount()
	{
		return getDeckPanel().getWidgetCount();
	}

	/***************************************
	 * {@inheritDoc}
	 */
	@Override
	public int getGroupIndex(Component rGroupComponent)
	{
		return getDeckPanel().getWidgetIndex(rGroupComponent.getWidget());
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
	public void setGroupTitle(int nIndex, String sTitle)
	{
		// ignored because now title is displayed
	}

	/***************************************
	 * {@inheritDoc}
	 */
	@Override
	public void setSelection(int nIndex)
	{
		getDeckPanel().showWidget(nIndex);
	}

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
		getLayout().addWidget(getDeckPanel(), rWidget, rStyleData, -1);
	}

	/***************************************
	 * Returns the {@link DeckLayoutPanel} widget of this instance.
	 *
	 * @return The deck panel
	 */
	private DeckLayoutPanel getDeckPanel()
	{
		return (DeckLayoutPanel) getWidget();
	}

	//~ Inner Classes ----------------------------------------------------------

	/********************************************************************
	 * Widget factory for this component.
	 *
	 * @author eso
	 */
	public static class DeckPanelWidgetFactory implements WidgetFactory<Widget>
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
			return new DeckLayoutPanel();
		}
	}
}
