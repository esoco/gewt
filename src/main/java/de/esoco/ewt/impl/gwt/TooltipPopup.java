//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// This file is a part of the 'gewt' project.
// Copyright 2016 Elmar Sonnenschein, esoco GmbH, Flensburg, Germany
//
// Licensed under the Apache License, Version 3.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//	  http://www.apache.org/licenses/LICENSE-3.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
package de.esoco.ewt.impl.gwt;

import de.esoco.ewt.EWT;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;


/********************************************************************
 * A widget-based tooltip popup.
 *
 * @author eso
 * @see    "http://groups.google.com/group/google-web-toolkit/msg/dcfc19a3534f7715"
 */
public class TooltipPopup extends PopupPanel
{
	//~ Static fields/initializers ---------------------------------------------

	/** The default delay, in milliseconds, */
	private static final int DEFAULT_SHOW_DELAY = 500;

	//~ Instance fields --------------------------------------------------------

	/** The delay, in milliseconds, to display the tooltip */
	private int showDelay;

	/**
	 * The delay, in milliseconds, to hide the tooltip, after it is displayed
	 */
	private int hideDelay;

	/** The timer to show the tool tip */
	private Timer showTimer;

	/** The timer to hide the tool tip */
	private Timer hideTimer;

	//~ Constructors -----------------------------------------------------------

	/***************************************
	 * Creates a new Tool Tip with the default show delay and no auto hiding
	 *
	 * @param sender    The widget to create the tool tip for
	 * @param relLeft   The left offset from the <code>sender</code>
	 * @param relTop    The top offset from the <code>sender</code>
	 * @param text      The tool tip text to display
	 * @param useRelTop If true, then use the relative top offset. If not, then
	 *                  just use the sender's offset height.
	 */
	public TooltipPopup(Widget		 sender,
						int			 relLeft,
						int			 relTop,
						final String text,
						boolean		 useRelTop)
	{
		super(true);

		this.showTimer = null;
		this.hideTimer = null;

		this.showDelay = DEFAULT_SHOW_DELAY;
		this.hideDelay = -1;

		HTML contents = new HTML(text);

		add(contents);

		int left = getPageScrollLeft() + sender.getAbsoluteLeft() + relLeft;
		int top  = getPageScrollTop() + sender.getAbsoluteTop();

		if (useRelTop)
		{
			top += relTop;
		}
		else
		{
			top += sender.getOffsetHeight() + 1;
		}

		setPopupPosition(left, top);
		addStyleName(EWT.CSS.ewtTooltip());
	}

	/***************************************
	 * Creates a new Tool Tip
	 *
	 * @param sender    The widget to create the tool tip for
	 * @param relLeft   The left offset from the <code>sender</code>
	 * @param relTop    The top offset from the <code>sender</code>
	 * @param text      The tool tip text to display
	 * @param useRelTop If true, then use the relative top offset. If not, then
	 *                  just use the senders offset height.
	 * @param showDelay The delay, in milliseconds, before the popup is
	 *                  displayed
	 * @param hideDelay The delay, in milliseconds, before the popup is hidden
	 * @param styleName The style name to apply to the popup
	 */
	public TooltipPopup(Widget		 sender,
						int			 relLeft,
						int			 relTop,
						final String text,
						boolean		 useRelTop,
						final int    showDelay,
						final int    hideDelay,
						final String styleName)
	{
		this(sender, relLeft, relTop, text, useRelTop);

		this.showDelay = showDelay;
		this.hideDelay = hideDelay;

		removeStyleName(EWT.CSS.ewtTooltip());
		addStyleName(styleName);
	}

	//~ Methods ----------------------------------------------------------------

	/***************************************
	 * @see PopupPanel#hide()
	 */
	@Override
	public void hide()
	{
		super.hide();

		// Cancel the show timer if necessary
		if (this.showTimer != null)
		{
			this.showTimer.cancel();
		}

		// Cancel the hide timer if necessary
		if (this.hideTimer != null)
		{
			this.hideTimer.cancel();
		}
	}

	/***************************************
	 * @see PopupPanel#show()
	 */
	@Override
	public void show()
	{
		// Set delay to show if specified
		if (this.showDelay > 0)
		{
			this.showTimer =
				new Timer()
				{
					@Override
					public void run()
					{
						TooltipPopup.this.showTooltip();
					}
				};
			this.showTimer.schedule(this.showDelay);
		}

		// Otherwise, show the dialog now
		else
		{
			showTooltip();
		}

		// Set delay to hide if specified
		if (this.hideDelay > 0)
		{
			this.hideTimer =
				new Timer()
				{
					@Override
					public void run()
					{
						TooltipPopup.this.hide();
					}
				};
			this.hideTimer.schedule(this.showDelay + this.hideDelay);
		}
	}

	/***************************************
	 * Get the offset for the horizontal scroll
	 *
	 * @return The offset
	 */
	private int getPageScrollLeft()
	{
		Element rBodyElement = RootPanel.getBodyElement();

		return DOM.getParent(rBodyElement).getAbsoluteLeft();
	}

	/***************************************
	 * Get the offset for the vertical scroll
	 *
	 * @return The offset
	 */
	private int getPageScrollTop()
	{
		Element rBodyElement = RootPanel.getBodyElement();

		return DOM.getParent(rBodyElement).getAbsoluteTop();
	}

	/***************************************
	 * Show the tool tip now
	 */
	private void showTooltip()
	{
		super.show();
	}
}
