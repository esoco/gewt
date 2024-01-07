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

/**
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
public class TabPanel extends SwitchPanel {

	/**
	 * Creates a new instance.
	 *
	 * @param parent The parent container
	 * @param style  The panel style
	 */
	public TabPanel(Container parent, StyleData style) {
		super(EWT
			.getLayoutFactory()
			.createLayout(parent, style, LayoutType.TABS));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	ComponentEventDispatcher createEventDispatcher() {
		return new TabPanelEventDispatcher();
	}

	/**
	 * The default layout for this panel.
	 *
	 * @author eso
	 */
	public static class TabPanelLayout extends SwitchPanelLayout {

		private GwtTabPanel tabPanel;

		private UserInterfaceContext context;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void addPage(Component tabComponent, String groupTitle,
			boolean closeable) {
			Widget tabContent = tabComponent.getWidget();

			groupTitle = context.expandResource(groupTitle);

			if (closeable) {
				Grid tabWidgets = new Grid(1, 2);
				Button closeButton = new Button("x");

				tabWidgets.setWidget(0, 0, new Label(groupTitle));
				tabWidgets.setWidget(0, 1, closeButton);
				closeButton.addClickHandler(new TabCloseHandler(tabContent));

				tabPanel.add(tabContent, tabWidgets);
			} else {
				tabPanel.add(tabContent, groupTitle);
			}

			if (tabPanel.getWidgetCount() == 1) {
				tabPanel.selectTab(0);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public HasWidgets createLayoutContainer(Container container,
			StyleData style) {
			this.context = container.getContext();
			tabPanel = new GwtTabPanel(2, Unit.EM);

			return tabPanel;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int getPageCount() {
			return tabPanel.getWidgetCount();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int getPageIndex(Component groupComponent) {
			return tabPanel.getWidgetIndex(groupComponent.getWidget());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int getSelectionIndex() {
			return tabPanel.getSelectedIndex();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void setPageTitle(int index, String title) {
			tabPanel.setTabText(index, title);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void setSelection(int index) {
			tabPanel.selectTab(index);
		}

		/**
		 * An event handler for the closing of tabs.
		 *
		 * @author eso
		 */
		class TabCloseHandler implements ClickHandler {

			private final Widget tabWidget;

			/**
			 * Creates a new instance.
			 *
			 * @param tabWidget The widget of the tab to close
			 */
			public TabCloseHandler(Widget tabWidget) {
				this.tabWidget = tabWidget;
			}

			/**
			 * @see ClickHandler#onClick(ClickEvent)
			 */
			@Override
			public void onClick(ClickEvent event) {
				int widgetIndex = tabPanel.getWidgetIndex(tabWidget);
				int selectedTab = tabPanel.getSelectedIndex();

				tabPanel.remove(tabWidget);

				if (widgetIndex == selectedTab &&
					tabPanel.getWidgetCount() > 0) {
					tabPanel.selectTab(selectedTab > 0 ? selectedTab - 1 : 0);
				}
			}
		}
	}

	/**
	 * Dispatcher for tab panel-specific events.
	 *
	 * @author eso
	 */
	class TabPanelEventDispatcher extends ComponentEventDispatcher
		implements SelectionHandler<Integer> {

		/**
		 * @see SelectionHandler#onSelection(SelectionEvent)
		 */
		@Override
		public void onSelection(SelectionEvent<Integer> event) {
			notifyEventHandler(EventType.SELECTION);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		@SuppressWarnings("unchecked")
		protected HandlerRegistration initEventDispatching(Widget widget,
			EventType eventType) {
			if (eventType == EventType.SELECTION &&
				widget instanceof HasSelectionHandlers) {
				return ((HasSelectionHandlers<Integer>) widget).addSelectionHandler(
					this);
			}

			return super.initEventDispatching(widget, eventType);
		}
	}
}
