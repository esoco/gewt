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
/*
 * Copyright 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package de.esoco.ewt.impl.gwt;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Widget;


/********************************************************************
 * A widget that displays progress on an arbitrary scale.
 *
 * <h3>CSS Style Rules</h3>
 *
 * <ul class='css'>
 *   <li>.gwt-ProgressBar-shell { primary style }</li>
 *   <li>.gwt-ProgressBar-shell .gwt-ProgressBar-bar { the actual progress bar }
 *   </li>
 *   <li>.gwt-ProgressBar-shell .gwt-ProgressBar-text { text on the bar }</li>
 *   <li>.gwt-ProgressBar-shell .gwt-ProgressBar-text-firstHalf { applied to
 *     text when progress is less than 50 percent }</li>
 *   <li>.gwt-ProgressBar-shell .gwt-ProgressBar-text-secondHalf { applied to
 *     text when progress is greater than 50 percent }</li>
 * </ul>
 */
public class GwtProgressBar extends Widget // implements ResizableWidget
{
	//~ Instance fields --------------------------------------------------------

	/** The bar element that displays the progress. */
	private Element aBarElement;

	/** The current progress. */
	private double curProgress;

	/** The maximum progress. */
	private double maxProgress;

	/** The minimum progress. */
	private double minProgress;

	/** A boolean that determines if the text is visible. */
	private boolean textVisible = true;

	/** The element that displays text on the page. */
	private Element aTextElement;

	/** The current text formatter. */
	private TextFormatter textFormatter;

	//~ Constructors -----------------------------------------------------------

	/***************************************
	 * Create a progress bar with default range of 0 to 100.
	 */
	public GwtProgressBar()
	{
		this(0.0, 100.0, 0.0);
	}

	/***************************************
	 * Create a progress bar with an initial progress and a default range of 0
	 * to 100.
	 *
	 * @param curProgress the current progress
	 */
	public GwtProgressBar(double curProgress)
	{
		this(0.0, 100.0, curProgress);
	}

	/***************************************
	 * Create a progress bar within the given range.
	 *
	 * @param minProgress the minimum progress
	 * @param maxProgress the maximum progress
	 */
	public GwtProgressBar(double minProgress, double maxProgress)
	{
		this(minProgress, maxProgress, 0.0);
	}

	/***************************************
	 * Create a progress bar within the given range starting at the specified
	 * progress amount.
	 *
	 * @param minProgress the minimum progress
	 * @param maxProgress the maximum progress
	 * @param curProgress the current progress
	 */
	public GwtProgressBar(double minProgress,
						  double maxProgress,
						  double curProgress)
	{
		this(minProgress, maxProgress, curProgress, null);
	}

	/***************************************
	 * Create a progress bar within the given range starting at the specified
	 * progress amount.
	 *
	 * @param minProgress   the minimum progress
	 * @param maxProgress   the maximum progress
	 * @param curProgress   the current progress
	 * @param textFormatter the text formatter
	 */
	public GwtProgressBar(double		minProgress,
						  double		maxProgress,
						  double		curProgress,
						  TextFormatter textFormatter)
	{
		this.minProgress = minProgress;
		this.maxProgress = maxProgress;
		this.curProgress = curProgress;

		setTextFormatter(textFormatter);

		// Create the outer shell
		Element aShellElement = DOM.createDiv();

		setElement(aShellElement);
		aShellElement.getStyle().setProperty("position", "relative");
		setStyleName("gwt-ProgressBar-shell");

		// Create the bar element
		aBarElement = DOM.createDiv();
		DOM.appendChild(getElement(), aBarElement);
		aBarElement.getStyle().setProperty("height", "100%");
		aBarElement.setPropertyString("className", "gwt-ProgressBar-bar");

		// Create the text element
		aTextElement = DOM.createDiv();
		DOM.appendChild(getElement(), aTextElement);
		aTextElement.getStyle().setProperty("position", "absolute");
		aTextElement.getStyle().setProperty("top", "0px");
		aTextElement.setPropertyString("className",
									   "gwt-ProgressBar-text-firstHalf");

		// Set the current progress
		setProgress(curProgress);
	}

	//~ Methods ----------------------------------------------------------------

	/***************************************
	 * Get the maximum progress.
	 *
	 * @return the maximum progress
	 */
	public double getMaxProgress()
	{
		return maxProgress;
	}

	/***************************************
	 * Get the minimum progress.
	 *
	 * @return the minimum progress
	 */
	public double getMinProgress()
	{
		return minProgress;
	}

	/***************************************
	 * Get the current percent complete, relative to the minimum and maximum
	 * values. The percent will always be between 0.0 - 1.0.
	 *
	 * @return the current percent complete
	 */
	public double getPercent()
	{
		// If we have no range
		if (maxProgress <= minProgress)
		{
			return 0.0;
		}

		// Calculate the relative progress
		double percent =
			(curProgress - minProgress) / (maxProgress - minProgress);

		return Math.max(0.0, Math.min(1.0, percent));
	}

	/***************************************
	 * Get the current progress.
	 *
	 * @return the current progress
	 */
	public double getProgress()
	{
		return curProgress;
	}

	/***************************************
	 * Get the text formatter.
	 *
	 * @return the text formatter
	 */
	public TextFormatter getTextFormatter()
	{
		return textFormatter;
	}

	/***************************************
	 * Check whether the text is visible or not.
	 *
	 * @return true if the text is visible
	 */
	public boolean isTextVisible()
	{
		return textVisible;
	}

	/***************************************
	 * This method is called when the dimensions of the parent element change.
	 * Subclasses should override this method as needed.
	 *
	 * <p>Move the text to the center of the progress bar.</p>
	 *
	 * @param width  the new client width of the element
	 * @param height the new client height of the element
	 */
	public void onResize(int width, int height)
	{
		if (textVisible)
		{
			int textWidth = aTextElement.getPropertyInt("offsetWidth");
			int left	  = (width / 2) - (textWidth / 2);

			aTextElement.getStyle().setProperty("left", left + "px");
		}
	}

	/***************************************
	 * Redraw the progress bar when something changes the layout.
	 */
	public void redraw()
	{
		if (isAttached())
		{
			Element rElement = getElement();
			int     width    = rElement.getPropertyInt("clientWidth");
			int     height   = rElement.getPropertyInt("clientHeight");

			onResize(width, height);
		}
	}

	/***************************************
	 * Set the maximum progress. If the minimum progress is more than the
	 * current progress, the current progress is adjusted to be within the new
	 * range.
	 *
	 * @param maxProgress the maximum progress
	 */
	public void setMaxProgress(double maxProgress)
	{
		this.maxProgress = maxProgress;
		curProgress		 = Math.min(curProgress, maxProgress);

		resetProgress();
	}

	/***************************************
	 * Set the minimum progress. If the minimum progress is more than the
	 * current progress, the current progress is adjusted to be within the new
	 * range.
	 *
	 * @param minProgress the minimum progress
	 */
	public void setMinProgress(double minProgress)
	{
		this.minProgress = minProgress;
		curProgress		 = Math.max(curProgress, minProgress);

		resetProgress();
	}

	/***************************************
	 * Set the current progress.
	 *
	 * @param curProgress the current progress
	 */
	public void setProgress(double curProgress)
	{
		this.curProgress =
			Math.max(minProgress, Math.min(maxProgress, curProgress));

		// Calculate percent complete
		int percent = (int) (100 * getPercent());

		aBarElement.getStyle().setProperty("width", percent + "%");
		aTextElement.setPropertyString("innerHTML", generateText(curProgress));

		// Set the style depending on the size of the bar
		if (percent < 50)
		{
			aTextElement.setPropertyString("className",
										   "gwt-ProgressBar-text gwt-ProgressBar-text-firstHalf");
		}
		else
		{
			aTextElement.setPropertyString("className",
										   "gwt-ProgressBar-text gwt-ProgressBar-text-secondHalf");
		}

		// Realign the text
		redraw();
	}

	/***************************************
	 * Set the text formatter.
	 *
	 * @param textFormatter the text formatter
	 */
	public void setTextFormatter(TextFormatter textFormatter)
	{
		this.textFormatter = textFormatter;
	}

	/***************************************
	 * Sets whether the text is visible over the bar.
	 *
	 * @param isVisible True to show text, false to hide it
	 */
	public void setTextVisible(boolean isVisible)
	{
		this.textVisible = isVisible;

		if (this.textVisible)
		{
			aTextElement.getStyle().setProperty("display", "");
			redraw();
		}
		else
		{
			aTextElement.getStyle().setProperty("display", "none");
		}
	}

	/***************************************
	 * Generate the text to display within the progress bar. Override this
	 * function to change the default progress percent to a more informative
	 * message, such as the number of kilobytes downloaded.
	 *
	 * @param  curProgress the current progress
	 *
	 * @return the text to display in the progress bar
	 */
	protected String generateText(double curProgress)
	{
		if (textFormatter != null)
		{
			return textFormatter.getText(this, curProgress);
		}
		else
		{
			return (int) (100 * getPercent()) + "%";
		}
	}

	/***************************************
	 * Get the bar element.
	 *
	 * @return the bar element
	 */
	protected Element getBarElement()
	{
		return aBarElement;
	}

	/***************************************
	 * Get the text element.
	 *
	 * @return the text element
	 */
	protected Element getTextElement()
	{
		return aTextElement;
	}

	/***************************************
	 * This method is called immediately after a widget becomes attached to the
	 * browser's document.
	 */
	@Override
	protected void onLoad()
	{
		// Reset the position attribute of the parent element
		getElement().getStyle().setProperty("position", "relative");
//		ResizableWidgetCollection.get().add(this);
		redraw();
	}

	/***************************************
	 * @see Widget#onUnload()
	 */
	@Override
	protected void onUnload()
	{
//		ResizableWidgetCollection.get().remove(this);
	}

	/***************************************
	 * Reset the progress text based on the current min and max progress range.
	 */
	protected void resetProgress()
	{
		setProgress(getProgress());
	}

	//~ Inner Classes ----------------------------------------------------------

	/********************************************************************
	 * A formatter used to format the text displayed in the progress bar widget.
	 */
	public static abstract class TextFormatter
	{
		//~ Methods ------------------------------------------------------------

		/***************************************
		 * Generate the text to display in the ProgressBar based on the current
		 * value.
		 *
		 * <p>Override this method to change the text displayed within the
		 * ProgressBar.</p>
		 *
		 * @param  bar         the progress bar
		 * @param  curProgress the current progress
		 *
		 * @return the text to display in the progress bar
		 */
		protected abstract String getText(
			GwtProgressBar bar,
			double		   curProgress);
	}
}
