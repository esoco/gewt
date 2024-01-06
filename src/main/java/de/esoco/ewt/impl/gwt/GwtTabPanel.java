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

	private LayoutPanel rMainPanel;

	private FlowPanel rTabBar;

	private Image rScrollLeftButton;

	private Image rScrollRightButton;

	private HandlerRegistration rWindowResizeHandler;

	/**
	 * Creates a new instance.
	 *
	 * @param fBarHeight The tab bar height
	 * @param eBarUnit   The unit of the tab bar height
	 */
	public GwtTabPanel(double fBarHeight, Unit eBarUnit) {
		super(fBarHeight, eBarUnit);

		// The main widget wrapped by this composite, which is a LayoutPanel
		// with the tab bar & the tab content
		rMainPanel = (LayoutPanel) getWidget();

		// Find the tab bar, which is the first flow panel in the LayoutPanel
		for (Widget rWidget : rMainPanel) {
			if (rWidget instanceof FlowPanel) {
				rTabBar = (FlowPanel) rWidget;

				break;
			}
		}

		initScrollButtons();
	}

	/**
	 * Parses a position value from the CSS left value of a DOM element's
	 * style.
	 *
	 * @param rElement The DOM element
	 * @return The position integer value
	 */
	private static int parsePosition(Element rElement) {
		String sCssLeft = rElement.getStyle().getLeft();
		int nPosition;

		try {
			for (int i = 0; i < sCssLeft.length(); i++) {
				char c = sCssLeft.charAt(i);

				if (c != '-' && !(c >= '0' && c <= '9')) {
					sCssLeft = sCssLeft.substring(0, i);
				}
			}

			nPosition = Integer.parseInt(sCssLeft);
		} catch (NumberFormatException e) {
			nPosition = 0;
		}

		return nPosition;
	}

	/**
	 * @see TabLayoutPanel#insert(Widget, Widget, int)
	 */
	@Override
	public void insert(final Widget rWidget, Widget rTab, int nBeforeIndex) {
		super.insert(rWidget, rTab, nBeforeIndex);

		checkShowScrollButtons(rWidget.getParent());
	}

	/**
	 * Queries the tab bar visibility.
	 *
	 * @return TRUE if the tab bar is currently visible
	 */
	public boolean isTabBarVisible() {
		return rTabBar.isVisible();
	}

	/**
	 * @see TabLayoutPanel#remove(Widget)
	 */
	@Override
	public boolean remove(Widget rWidget) {
		boolean bRemoved = super.remove(rWidget);

		checkShowScrollButtons(null);

		return bRemoved;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void selectTab(int nIndex, boolean bFireEvents) {
		super.selectTab(nIndex, bFireEvents);

		if (nIndex >= 0 && nIndex < getWidgetCount()) {
			scrollTo(getTabWidget(nIndex).getParent());
		}
	}

	/**
	 * Sets the tab bar visibility.
	 *
	 * @param bVisible TRUE to make the tab bar visible
	 */
	public void setTabBarVisible(boolean bVisible) {
		rTabBar.setVisible(bVisible);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setTabHTML(int rIndex, String rHtml) {
		super.setTabHTML(rIndex, rHtml);
		checkShowScrollButtons(null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setTabText(int rIndex, String rText) {
		super.setTabText(rIndex, rText);
		checkShowScrollButtons(null);
	}

	/**
	 * @see TabLayoutPanel#onLoad()
	 */
	@Override
	protected void onLoad() {
		super.onLoad();

		if (rWindowResizeHandler == null) {
			rWindowResizeHandler =
				Window.addResizeHandler(new ResizeHandler() {
				@Override
				public void onResize(ResizeEvent rEvent) {
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

		if (rWindowResizeHandler != null) {
			rWindowResizeHandler.removeHandler();
			rWindowResizeHandler = null;
		}
	}

	/**
	 * Checks whether the scroll buttons should be visible.
	 *
	 * @param rScrollToTab An optional tab widget to scroll to when scroll
	 *                     buttons are shown or NULL for none
	 */
	void checkShowScrollButtons(final Widget rScrollToTab) {
		// Defer size calculations until sizes are available, when calculating
		// immediately after add(), all size methods return zero
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				showScrollButtons(isTabScrollingNecessary());

				if (rScrollToTab != null) {
					Scheduler.get().scheduleDeferred(new ScheduledCommand() {
						@Override
						public void execute() {
							scrollTo(rScrollToTab);
						}
					});
				}
			}
		});
	}

	/**
	 * Scrolls the tab bar a certain amount.
	 *
	 * @param nDiff The difference of the X coordinate to scroll to
	 */
	void scrollTabBar(final int nDiff) {
		if (isTabScrollingNecessary() && nDiff != 0) {
			Widget rLastTab = getLastTab();
			int nNewLeft = parsePosition(rTabBar.getElement()) + nDiff;

			int nRight = getRightOfWidget(rLastTab);
			int nWidth = getTabBarWidth();

			// Prevent scrolling the last tab too far away form the right
			// border,
			// or the first tab further than the left border position
			if (nNewLeft <= 0 &&
				(nWidth - nNewLeft < (nRight + DEFAULT_SCROLL_WIDTH))) {
				rTabBar.getElement().getStyle().setLeft(nNewLeft, Unit.PX);
			}
		}
	}

	/**
	 * Scrolls to a certain tab bar widget.
	 *
	 * @param rTab The widget to scroll to
	 */
	void scrollTo(Widget rTab) {
		if (isTabScrollingNecessary()) {
			int nTabBarLeft = parsePosition(rTabBar.getElement());
			int nTabBarWidth = getTabBarWidth();
			int nWidgetLeft = rTab.getElement().getOffsetLeft() + nTabBarLeft;
			int nWidgetRight = getRightOfWidget(rTab) + nTabBarLeft;
			int nScrollDiff = 0;

			if (nWidgetRight > nTabBarWidth) {
				nScrollDiff = nTabBarWidth - nWidgetRight;
				nWidgetLeft += nScrollDiff;
			}

			if (nWidgetLeft < 0) {
				nScrollDiff = -nWidgetLeft;
			}

			scrollTabBar(nScrollDiff);
		}
	}

	/**
	 * Shows or hides the tab scroll buttons.
	 *
	 * @param bShow The new visibility of the scroll buttons
	 */
	void showScrollButtons(boolean bShow) {
		boolean bVisible = rScrollRightButton.isVisible();
		int nButtonWidth = rScrollRightButton.getWidth();

		if (bVisible && !bShow) {
			int nTabBarWidth = getRightOfWidget(getLastTab());

			rTabBar.getElement().getStyle().setLeft(0, Unit.PX);
			rMainPanel.setWidgetLeftWidth(rTabBar, 0, Unit.PX, nTabBarWidth,
				Unit.PX);
		}

		if (bShow) {
			int nMainPanelWidth = rMainPanel.getOffsetWidth();
			int nTabBarWidth = nMainPanelWidth - nButtonWidth * 2;
			int nRightButtonX = nMainPanelWidth - nButtonWidth;

			rMainPanel.setWidgetLeftWidth(rScrollRightButton, nRightButtonX,
				Unit.PX, nButtonWidth, Unit.PX);
			rMainPanel.setWidgetLeftWidth(rTabBar, nButtonWidth, Unit.PX,
				nTabBarWidth, Unit.PX);
		}

		rScrollRightButton.setVisible(bShow);
		rScrollLeftButton.setVisible(bShow);
	}

	/**
	 * Returns the last tab in the tab bar.
	 *
	 * @return The last tab or NULL for none
	 */
	private Widget getLastTab() {
		Widget rLastTab = null;

		if (rTabBar.getWidgetCount() != 0) {
			rLastTab = rTabBar.getWidget(rTabBar.getWidgetCount() - 1);
		}

		return rLastTab;
	}

	/**
	 * Returns the integer coordinate of the right side of a widget relative to
	 * it's parent.
	 *
	 * @param rWidget The widget to calculate the right coordinate of
	 * @return The right coordinate of the widget
	 */
	private int getRightOfWidget(Widget rWidget) {
		Element rElement = rWidget.getElement();

		return rElement.getOffsetLeft() + rElement.getOffsetWidth();
	}

	/**
	 * Returns the tab bar width.
	 *
	 * @return The tab bar width
	 */
	private int getTabBarWidth() {
//		return rTabBar.getElement().getParentElement().getClientWidth();
		return rMainPanel.getOffsetWidth() -
			2 * rScrollLeftButton.getOffsetWidth();
	}

	/**
	 * Create and attach the scroll button images with a click handler
	 */
	private void initScrollButtons() {
		rScrollLeftButton = new Image(GewtResources.INSTANCE.imLeft());
		rScrollRightButton = new Image(GewtResources.INSTANCE.imRight());

		int nButtonWidth = rScrollLeftButton.getWidth();

		rMainPanel.insert(rScrollLeftButton, 0);
		rMainPanel.insert(rScrollRightButton, 0);
		setScrollEventHandlers(rScrollLeftButton, DEFAULT_SCROLL_WIDTH);
		setScrollEventHandlers(rScrollRightButton, -DEFAULT_SCROLL_WIDTH);
		rScrollLeftButton.setVisible(false);
		rScrollRightButton.setVisible(false);

		rMainPanel.setWidgetLeftWidth(rScrollLeftButton, 0, Unit.PX,
			nButtonWidth, Unit.PX);
		rMainPanel.setWidgetTopHeight(rScrollLeftButton, 6, Unit.PX,
			rScrollLeftButton.getHeight(), Unit.PX);
		rMainPanel.setWidgetLeftWidth(rScrollRightButton, 0, Unit.PX,
			nButtonWidth, Unit.PX);
		rMainPanel.setWidgetTopHeight(rScrollRightButton, 6, Unit.PX,
			rScrollRightButton.getHeight(), Unit.PX);
	}

	/**
	 * Returns whether scrolling of the tabs is necessary.
	 *
	 * @return TRUE if tabs scrolling is necessary
	 */
	private boolean isTabScrollingNecessary() {
		Widget rLastTab = getLastTab();
		boolean bNecessary = false;

		if (rLastTab != null) {
			int nLastTabRight = getRightOfWidget(rLastTab);
			int nMaxWidth = rMainPanel.getOffsetWidth();

			bNecessary = nLastTabRight > nMaxWidth;
		}

		return bNecessary;
	}

	/**
	 * Creates the event handlers for a tab bar scroll button.
	 *
	 * @param rScrollButton The scroll button
	 * @param nDiff         The scroll amount
	 */
	private void setScrollEventHandlers(Image rScrollButton, final int nDiff) {
		final Timer aTimer = new Timer() {
			@Override
			public void run() {
				scrollTabBar(nDiff);
			}
		};

		rScrollButton.addMouseDownHandler(new MouseDownHandler() {
			@Override
			public void onMouseDown(MouseDownEvent rEvent) {
				aTimer.scheduleRepeating(100);
			}
		});

		rScrollButton.addMouseUpHandler(new MouseUpHandler() {
			@Override
			public void onMouseUp(MouseUpEvent rEvent) {
				aTimer.cancel();
			}
		});

		rScrollButton.addMouseOutHandler(new MouseOutHandler() {
			@Override
			public void onMouseOut(MouseOutEvent rEvent) {
				aTimer.cancel();
			}
		});

		rScrollButton.addDoubleClickHandler(new DoubleClickHandler() {
			@Override
			public void onDoubleClick(DoubleClickEvent rEvent) {
				aTimer.cancel();
				scrollTo(rEvent.getSource() == rScrollLeftButton ?
				         rTabBar.getWidget(0) :
				         getLastTab());
			}
		});
	}
}
