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

import de.esoco.ewt.EWT;
import de.esoco.ewt.UserInterfaceContext;
import de.esoco.ewt.event.EventType;
import de.esoco.ewt.impl.gwt.GwtTabPanel;
import de.esoco.ewt.style.StyleData;

import de.esoco.lib.property.LayoutType;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasWidgets;
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
 * @author eso
 */
public class TabPanel extends SwitchPanel
{
	//~ Constructors -----------------------------------------------------------

	/***************************************
	 * Creates a new instance.
	 *
	 * @param rParent The parent container
	 * @param rStyle  The panel style
	 */
	public TabPanel(Container rParent, StyleData rStyle)
	{
		super(EWT.getLayoutFactory()
			  .createLayout(rParent, rStyle, LayoutType.TABS));
	}

	//~ Methods ----------------------------------------------------------------

	/***************************************
	 * {@inheritDoc}
	 */
	@Override
	ComponentEventDispatcher createEventDispatcher()
	{
		return new TabPanelEventDispatcher();
	}

	//~ Inner Classes ----------------------------------------------------------

	/********************************************************************
	 * The default layout for this panel.
	 *
	 * @author eso
	 */
	public static class TabPanelLayout extends SwitchPanelLayout
	{
		//~ Instance fields ----------------------------------------------------

		private GwtTabPanel			 aTabPanel;
		private UserInterfaceContext rContext;

		//~ Methods ------------------------------------------------------------

		/***************************************
		 * {@inheritDoc}
		 */
		@Override
		public void addPage(Component rTabComponent,
							String    sGroupTitle,
							boolean   bCloseable)
		{
			Widget rTabContent = rTabComponent.getWidget();

			sGroupTitle = rContext.expandResource(sGroupTitle);

			if (bCloseable)
			{
				Grid   aTabWidgets  = new Grid(1, 2);
				Button aCloseButton = new Button("x");

				aTabWidgets.setWidget(0, 0, new Label(sGroupTitle));
				aTabWidgets.setWidget(0, 1, aCloseButton);
				aCloseButton.addClickHandler(new TabCloseHandler(rTabContent));

				aTabPanel.add(rTabContent, aTabWidgets);
			}
			else
			{
				aTabPanel.add(rTabContent, sGroupTitle);
			}

			if (aTabPanel.getWidgetCount() == 1)
			{
				aTabPanel.selectTab(0);
			}
		}

		/***************************************
		 * {@inheritDoc}
		 */
		@Override
		public HasWidgets createLayoutContainer(
			Container rContainer,
			StyleData rStyle)
		{
			this.rContext = rContainer.getContext();
			aTabPanel     = new GwtTabPanel(2, Unit.EM);

			return aTabPanel;
		}

		/***************************************
		 * {@inheritDoc}
		 */
		@Override
		public int getPageCount()
		{
			return aTabPanel.getWidgetCount();
		}

		/***************************************
		 * {@inheritDoc}
		 */
		@Override
		public int getPageIndex(Component rGroupComponent)
		{
			return aTabPanel.getWidgetIndex(rGroupComponent.getWidget());
		}

		/***************************************
		 * {@inheritDoc}
		 */
		@Override
		public int getSelectionIndex()
		{
			return aTabPanel.getSelectedIndex();
		}

		/***************************************
		 * {@inheritDoc}
		 */
		@Override
		public void setPageTitle(int nIndex, String sTitle)
		{
			aTabPanel.setTabText(nIndex, sTitle);
		}

		/***************************************
		 * {@inheritDoc}
		 */
		@Override
		public void setSelection(int nIndex)
		{
			aTabPanel.selectTab(nIndex);
		}

		//~ Inner Classes ------------------------------------------------------

		/********************************************************************
		 * An event handler for the closing of tabs.
		 *
		 * @author eso
		 */
		class TabCloseHandler implements ClickHandler
		{
			//~ Instance fields ------------------------------------------------

			private final Widget rTabWidget;

			//~ Constructors ---------------------------------------------------

			/***************************************
			 * Creates a new instance.
			 *
			 * @param rTabWidget The widget of the tab to close
			 */
			public TabCloseHandler(Widget rTabWidget)
			{
				this.rTabWidget = rTabWidget;
			}

			//~ Methods --------------------------------------------------------

			/***************************************
			 * @see ClickHandler#onClick(ClickEvent)
			 */
			@Override
			public void onClick(ClickEvent rEvent)
			{
				int nWidgetIndex = aTabPanel.getWidgetIndex(rTabWidget);
				int nSelectedTab = aTabPanel.getSelectedIndex();

				aTabPanel.remove(rTabWidget);

				if (nWidgetIndex == nSelectedTab &&
					aTabPanel.getWidgetCount() > 0)
				{
					aTabPanel.selectTab(nSelectedTab > 0 ? nSelectedTab - 1
														 : 0);
				}
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
		 * {@inheritDoc}
		 */
		@Override
		@SuppressWarnings("unchecked")
		protected HandlerRegistration initEventDispatching(
			Widget    rWidget,
			EventType eEventType)
		{
			if (eEventType == EventType.SELECTION &&
				rWidget instanceof HasSelectionHandlers)
			{
				return ((HasSelectionHandlers<Integer>) rWidget)
					   .addSelectionHandler(this);
			}

			return super.initEventDispatching(rWidget, eEventType);
		}
	}
}
