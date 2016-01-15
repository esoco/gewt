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

import de.esoco.ewt.event.EventType;
import de.esoco.ewt.impl.gwt.GwtTabPanel;
import de.esoco.ewt.style.StyleFlag;

import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;


/********************************************************************
 * A panel containing multiple components that are arranged in pages which can
 * be displayed by clicking on a tab at the border of the tab panel. The default
 * placement of the tabs is at the top of the panel but can be set to bottom by
 * providing the style flag TAB_BOTTOM in the style data when creating a
 * TabPanel (if supported by the EWT implementation). Because the layout of a
 * tab panel is defined by it's implementation setting a layout on an instance
 * has no effect.
 *
 * <p>The components for the panel tabs must be created with the panel as their
 * parent or else errors may occur. Afterwards they must be added to this panel
 * with the {@link #addTab(Component, String) addTab()} method.</p>
 *
 * <p>Supported style flags:</p>
 *
 * <ul>
 *   <li>{@link StyleFlag#TAB_BOTTOM TAB_BOTTOM}: place the tab bar at the
 *     bottom of the panel (not yet supported on GWT).</li>
 *   <li>{@link StyleFlag#TAB_CLOSE_BUTTON TAB_CLOSE_BUTTON}: add a close button
 *     to each tab label.</li>
 * </ul>
 *
 * @author eso
 */
public class TabPanel extends GroupPanel
{
	//~ Constructors -----------------------------------------------------------

	/***************************************
	 * Creates a new instance.
	 */
	public TabPanel()
	{
		super(new GwtTabPanel(2, Unit.EM));
	}

	//~ Methods ----------------------------------------------------------------

	/***************************************
	 * {@inheritDoc}
	 */
	@Override
	public void addGroup(Component rComponent,
						 String    sTabTitle,
						 boolean   bCloseable)
	{
		Widget rTabContent = rComponent.getWidget();

		GwtTabPanel rTabPanel = getGwtTabPanel();

		sTabTitle = getContext().expandResource(sTabTitle);

		if (bCloseable)
		{
			Grid   aTabWidgets  = new Grid(1, 2);
			Button aCloseButton = new Button("x");

			aTabWidgets.setWidget(0, 0, new Label(sTabTitle));
			aTabWidgets.setWidget(0, 1, aCloseButton);
			aCloseButton.addClickHandler(new TabCloseHandler(rTabContent));

			rTabPanel.add(rTabContent, aTabWidgets);
		}
		else
		{
			rTabPanel.add(rTabContent, sTabTitle);
		}

		rTabContent.getElement().getParentElement().getStyle()
				   .setOverflow(Overflow.AUTO);

		if (rTabPanel.getWidgetCount() == 1)
		{
			rTabPanel.selectTab(0);
		}
	}

	/***************************************
	 * {@inheritDoc}
	 */
	@Override
	public int getGroupCount()
	{
		return getGwtTabPanel().getWidgetCount();
	}

	/***************************************
	 * {@inheritDoc}
	 */
	@Override
	public int getGroupIndex(Component rTabComponent)
	{
		return getGwtTabPanel().getWidgetIndex(rTabComponent.getWidget());
	}

	/***************************************
	 * {@inheritDoc}
	 */
	@Override
	public int getSelectionIndex()
	{
		return getGwtTabPanel().getSelectedIndex();
	}

	/***************************************
	 * Queries the tab bar visibility.
	 *
	 * @return TRUE if the tab bar is currently visible
	 */
	public boolean isTabBarVisible()
	{
		return getGwtTabPanel().isTabBarVisible();
	}

	/***************************************
	 * {@inheritDoc}
	 */
	@Override
	public void setGroupTitle(int nIndex, String sTitle)
	{
		getGwtTabPanel().setTabText(nIndex, sTitle);
	}

	/***************************************
	 * {@inheritDoc}
	 */
	@Override
	public void setSelection(int nIndex)
	{
		getGwtTabPanel().selectTab(nIndex);
	}

	/***************************************
	 * Sets the tab bar visibility.
	 *
	 * @param bVisible TRUE to make the tab bar visible
	 */
	public void setTabBarVisible(boolean bVisible)
	{
		getGwtTabPanel().setTabBarVisible(bVisible);
	}

	/***************************************
	 * {@inheritDoc}
	 */
	@Override
	ComponentEventDispatcher createEventDispatcher()
	{
		return new TabPanelEventDispatcher();
	}

	/***************************************
	 * Returns the GWT tab panel of this instance.
	 *
	 * @return The GWT tab panel
	 */
	private GwtTabPanel getGwtTabPanel()
	{
		return (GwtTabPanel) getWidget();
	}

	//~ Inner Classes ----------------------------------------------------------

	/********************************************************************
	 * An event handler for the closing of tabs.
	 *
	 * @author eso
	 */
	class TabCloseHandler implements ClickHandler
	{
		//~ Instance fields ----------------------------------------------------

		private final Widget rTabWidget;

		//~ Constructors -------------------------------------------------------

		/***************************************
		 * Creates a new instance.
		 *
		 * @param rTabWidget The widget of the tab to close
		 */
		public TabCloseHandler(Widget rTabWidget)
		{
			this.rTabWidget = rTabWidget;
		}

		//~ Methods ------------------------------------------------------------

		/***************************************
		 * @see ClickHandler#onClick(ClickEvent)
		 */
		@Override
		public void onClick(ClickEvent rEvent)
		{
			GwtTabPanel rTabPanel = (GwtTabPanel) getWidget();

			int nWidgetIndex = rTabPanel.getWidgetIndex(rTabWidget);
			int nSelectedTab = rTabPanel.getSelectedIndex();

			rTabPanel.remove(rTabWidget);

			if (nWidgetIndex == nSelectedTab && rTabPanel.getWidgetCount() > 0)
			{
				rTabPanel.selectTab(nSelectedTab > 0 ? nSelectedTab - 1 : 0);
			}
		}
	}

	/********************************************************************
	 * Dispatcher for tab panel-specific events.
	 *
	 * @author eso
	 */
	class TabPanelEventDispatcher extends ComponentEventDispatcher
		implements SelectionHandler<Integer>
	{
		//~ Methods ------------------------------------------------------------

		/***************************************
		 * @see SelectionHandler#onSelection(SelectionEvent)
		 */
		@Override
		public void onSelection(SelectionEvent<Integer> rEvent)
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

			((GwtTabPanel) rWidget).addSelectionHandler(this);
		}
	}
}
