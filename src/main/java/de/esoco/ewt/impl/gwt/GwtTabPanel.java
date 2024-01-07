//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// This file is a part of the 'gewt' project.
// Copyright 2018 Elmar Sonnenschein, esoco GmbH, Flensburg, Germany
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
package de.esoco.ewt.impl.gwt;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * A {@link TabLayoutPanel} that shows scroll buttons for the tabs if necessary.
 * Based on <a href="http://devnotesblog.wordpress
 * .com/2010/06/17/scrollable-gwt-tablayoutpanel/"> this web article</a>.
 */
public class GwtTabPanel extends TabLayoutPanel {

	private static final int DEFAULT_SCROLL_WIDTH = 20;

	private LayoutPanel mainPanel;

	private FlowPanel tabBar;

	private Image scrollLeftButton;

	private Image scrollRightButton;

	private HandlerRegistration windowResizeHandler;

	/**
	 * Creates a new instance.
	 *
	 * @param barHeight The tab bar height
	 * @param barUnit   The unit of the tab bar height
	 */
	public GwtTabPanel(double barHeight, Unit barUnit) {
		super(barHeight, barUnit);

		// The main widget wrapped by this composite, which is a LayoutPanel
		// with the tab bar & the tab content
		mainPanel = (LayoutPanel) getWidget();

		// Find the tab bar, which is the first flow panel in the LayoutPanel
		for (Widget widget : mainPanel) {
			if (widget instanceof FlowPanel) {
				tabBar = (FlowPanel) widget;

				break;
			}
		}

		initScrollButtons();
	}

	/**
	 * Parses a position value from the CSS left value of a DOM element's
	 * style.
	 *
	 * @param element The DOM element
	 * @return The position integer value
	 */
	private static int parsePosition(Element element) {
		String cssLeft = element.getStyle().getLeft();
		int position;

		try {
			for (int i = 0; i < cssLeft.length(); i++) {
				char c = cssLeft.charAt(i);

				if (c != '-' && !(c >= '0' && c <= '9')) {
					cssLeft = cssLeft.substring(0, i);
				}
			}

			position = Integer.parseInt(cssLeft);
		} catch (NumberFormatException e) {
			position = 0;
		}

		return position;
	}

	/**
	 * @see TabLayoutPanel#insert(Widget, Widget, int)
	 */
	@Override
	public void insert(final Widget widget, Widget tab, int beforeIndex) {
		super.insert(widget, tab, beforeIndex);

		checkShowScrollButtons(widget.getParent());
	}

	/**
	 * Queries the tab bar visibility.
	 *
	 * @return TRUE if the tab bar is currently visible
	 */
	public boolean isTabBarVisible() {
		return tabBar.isVisible();
	}

	/**
	 * @see TabLayoutPanel#remove(Widget)
	 */
	@Override
	public boolean remove(Widget widget) {
		boolean removed = super.remove(widget);

		checkShowScrollButtons(null);

		return removed;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void selectTab(int index, boolean fireEvents) {
		super.selectTab(index, fireEvents);

		if (index >= 0 && index < getWidgetCount()) {
			scrollTo(getTabWidget(index).getParent());
		}
	}

	/**
	 * Sets the tab bar visibility.
	 *
	 * @param visible TRUE to make the tab bar visible
	 */
	public void setTabBarVisible(boolean visible) {
		tabBar.setVisible(visible);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setTabHTML(int index, String html) {
		super.setTabHTML(index, html);
		checkShowScrollButtons(null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setTabText(int index, String text) {
		super.setTabText(index, text);
		checkShowScrollButtons(null);
	}

	/**
	 * @see TabLayoutPanel#onLoad()
	 */
	@Override
	protected void onLoad() {
		super.onLoad();

		if (windowResizeHandler == null) {
			windowResizeHandler = Window.addResizeHandler(new ResizeHandler() {
				@Override
				public void onResize(ResizeEvent event) {
					checkShowScrollButtons(null);
				}
			});
		}
	}

	/**
	 * @see TabLayoutPanel#onUnload()
	 */
	@Override
	protected void onUnload() {
		super.onUnload();

		if (windowResizeHandler != null) {
			windowResizeHandler.removeHandler();
			windowResizeHandler = null;
		}
	}

	/**
	 * Checks whether the scroll buttons should be visible.
	 *
	 * @param scrollToTab An optional tab widget to scroll to when scroll
	 *                    buttons are shown or NULL for none
	 */
	void checkShowScrollButtons(final Widget scrollToTab) {
		// Defer size calculations until sizes are available, when calculating
		// immediately after add(), all size methods return zero
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				showScrollButtons(isTabScrollingNecessary());

				if (scrollToTab != null) {
					Scheduler.get().scheduleDeferred(new ScheduledCommand() {
						@Override
						public void execute() {
							scrollTo(scrollToTab);
						}
					});
				}
			}
		});
	}

	/**
	 * Scrolls the tab bar a certain amount.
	 *
	 * @param diff The difference of the X coordinate to scroll to
	 */
	void scrollTabBar(final int diff) {
		if (isTabScrollingNecessary() && diff != 0) {
			Widget lastTab = getLastTab();
			int newLeft = parsePosition(tabBar.getElement()) + diff;

			int right = getRightOfWidget(lastTab);
			int width = getTabBarWidth();

			// Prevent scrolling the last tab too far away form the right
			// border,
			// or the first tab further than the left border position
			if (newLeft <= 0 &&
				(width - newLeft < (right + DEFAULT_SCROLL_WIDTH))) {
				tabBar.getElement().getStyle().setLeft(newLeft, Unit.PX);
			}
		}
	}

	/**
	 * Scrolls to a certain tab bar widget.
	 *
	 * @param tab The widget to scroll to
	 */
	void scrollTo(Widget tab) {
		if (isTabScrollingNecessary()) {
			int tabBarLeft = parsePosition(tabBar.getElement());
			int tabBarWidth = getTabBarWidth();
			int widgetLeft = tab.getElement().getOffsetLeft() + tabBarLeft;
			int widgetRight = getRightOfWidget(tab) + tabBarLeft;
			int scrollDiff = 0;

			if (widgetRight > tabBarWidth) {
				scrollDiff = tabBarWidth - widgetRight;
				widgetLeft += scrollDiff;
			}

			if (widgetLeft < 0) {
				scrollDiff = -widgetLeft;
			}

			scrollTabBar(scrollDiff);
		}
	}

	/**
	 * Shows or hides the tab scroll buttons.
	 *
	 * @param show The new visibility of the scroll buttons
	 */
	void showScrollButtons(boolean show) {
		boolean visible = scrollRightButton.isVisible();
		int buttonWidth = scrollRightButton.getWidth();

		if (visible && !show) {
			int tabBarWidth = getRightOfWidget(getLastTab());

			tabBar.getElement().getStyle().setLeft(0, Unit.PX);
			mainPanel.setWidgetLeftWidth(tabBar, 0, Unit.PX, tabBarWidth,
				Unit.PX);
		}

		if (show) {
			int mainPanelWidth = mainPanel.getOffsetWidth();
			int tabBarWidth = mainPanelWidth - buttonWidth * 2;
			int rightButtonX = mainPanelWidth - buttonWidth;

			mainPanel.setWidgetLeftWidth(scrollRightButton, rightButtonX,
				Unit.PX, buttonWidth, Unit.PX);
			mainPanel.setWidgetLeftWidth(tabBar, buttonWidth, Unit.PX,
				tabBarWidth, Unit.PX);
		}

		scrollRightButton.setVisible(show);
		scrollLeftButton.setVisible(show);
	}

	/**
	 * Returns the last tab in the tab bar.
	 *
	 * @return The last tab or NULL for none
	 */
	private Widget getLastTab() {
		Widget lastTab = null;

		if (tabBar.getWidgetCount() != 0) {
			lastTab = tabBar.getWidget(tabBar.getWidgetCount() - 1);
		}

		return lastTab;
	}

	/**
	 * Returns the integer coordinate of the right side of a widget relative to
	 * it's parent.
	 *
	 * @param widget The widget to calculate the right coordinate of
	 * @return The right coordinate of the widget
	 */
	private int getRightOfWidget(Widget widget) {
		Element element = widget.getElement();

		return element.getOffsetLeft() + element.getOffsetWidth();
	}

	/**
	 * Returns the tab bar width.
	 *
	 * @return The tab bar width
	 */
	private int getTabBarWidth() {
//		return tabBar.getElement().getParentElement().getClientWidth();
		return mainPanel.getOffsetWidth() -
			2 * scrollLeftButton.getOffsetWidth();
	}

	/**
	 * Create and attach the scroll button images with a click handler
	 */
	private void initScrollButtons() {
		scrollLeftButton = new Image(GewtResources.INSTANCE.imLeft());
		scrollRightButton = new Image(GewtResources.INSTANCE.imRight());

		int buttonWidth = scrollLeftButton.getWidth();

		mainPanel.insert(scrollLeftButton, 0);
		mainPanel.insert(scrollRightButton, 0);
		setScrollEventHandlers(scrollLeftButton, DEFAULT_SCROLL_WIDTH);
		setScrollEventHandlers(scrollRightButton, -DEFAULT_SCROLL_WIDTH);
		scrollLeftButton.setVisible(false);
		scrollRightButton.setVisible(false);

		mainPanel.setWidgetLeftWidth(scrollLeftButton, 0, Unit.PX, buttonWidth,
			Unit.PX);
		mainPanel.setWidgetTopHeight(scrollLeftButton, 6, Unit.PX,
			scrollLeftButton.getHeight(), Unit.PX);
		mainPanel.setWidgetLeftWidth(scrollRightButton, 0, Unit.PX,
			buttonWidth,
			Unit.PX);
		mainPanel.setWidgetTopHeight(scrollRightButton, 6, Unit.PX,
			scrollRightButton.getHeight(), Unit.PX);
	}

	/**
	 * Returns whether scrolling of the tabs is necessary.
	 *
	 * @return TRUE if tabs scrolling is necessary
	 */
	private boolean isTabScrollingNecessary() {
		Widget lastTab = getLastTab();
		boolean necessary = false;

		if (lastTab != null) {
			int lastTabRight = getRightOfWidget(lastTab);
			int maxWidth = mainPanel.getOffsetWidth();

			necessary = lastTabRight > maxWidth;
		}

		return necessary;
	}

	/**
	 * Creates the event handlers for a tab bar scroll button.
	 *
	 * @param scrollButton The scroll button
	 * @param diff         The scroll amount
	 */
	private void setScrollEventHandlers(Image scrollButton, final int diff) {
		final Timer timer = new Timer() {
			@Override
			public void run() {
				scrollTabBar(diff);
			}
		};

		scrollButton.addMouseDownHandler(new MouseDownHandler() {
			@Override
			public void onMouseDown(MouseDownEvent event) {
				timer.scheduleRepeating(100);
			}
		});

		scrollButton.addMouseUpHandler(new MouseUpHandler() {
			@Override
			public void onMouseUp(MouseUpEvent event) {
				timer.cancel();
			}
		});

		scrollButton.addMouseOutHandler(new MouseOutHandler() {
			@Override
			public void onMouseOut(MouseOutEvent event) {
				timer.cancel();
			}
		});

		scrollButton.addDoubleClickHandler(new DoubleClickHandler() {
			@Override
			public void onDoubleClick(DoubleClickEvent event) {
				timer.cancel();
				scrollTo(event.getSource() == scrollLeftButton ?
				         tabBar.getWidget(0) :
				         getLastTab());
			}
		});
	}
}
