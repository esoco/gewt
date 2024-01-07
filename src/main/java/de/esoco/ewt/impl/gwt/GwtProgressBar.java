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

import de.esoco.ewt.component.ProgressBar.IsProgressBarWidget;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Widget;

/**
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
public class GwtProgressBar extends Widget implements IsProgressBarWidget {

	private Element barElement;

	private Element textElement;

	private int progress;

	private int minimum;

	private int maximum;

	private boolean showText = true;

	private TextFormatter textFormatter;

	/**
	 * Create a progress bar with default range of 0 to 100.
	 */
	public GwtProgressBar() {
		this(0);
	}

	/**
	 * Create a progress bar with an initial progress and a default range of 0
	 * to 100.
	 *
	 * @param progress the current progress
	 */
	public GwtProgressBar(int progress) {
		this(0, 100, progress);
	}

	/**
	 * Create a progress bar within the given range.
	 *
	 * @param minimum the minimum progress
	 * @param maximum the maximum progress
	 */
	public GwtProgressBar(int minimum, int maximum) {
		this(minimum, maximum, 0);
	}

	/**
	 * Create a progress bar within the given range starting at the specified
	 * progress amount.
	 *
	 * @param minimum  the minimum progress
	 * @param maximum  the maximum progress
	 * @param progress the current progress
	 */
	public GwtProgressBar(int minimum, int maximum, int progress) {
		this(minimum, maximum, progress, null);
	}

	/**
	 * Create a progress bar within the given range starting at the specified
	 * progress amount.
	 *
	 * @param minimum       the minimum progress
	 * @param maximum       the maximum progress
	 * @param progress      the current progress
	 * @param textFormatter the text formatter
	 */
	public GwtProgressBar(int minimum, int maximum, int progress,
		TextFormatter textFormatter) {
		this.minimum = minimum;
		this.maximum = maximum;
		this.progress = progress;

		setTextFormatter(textFormatter);

		// Create the outer shell
		Element shellElement = DOM.createDiv();

		setElement(shellElement);
		shellElement.getStyle().setProperty("position", "relative");
		setStyleName("gwt-ProgressBar-shell");

		// Create the bar element
		barElement = DOM.createDiv();
		DOM.appendChild(getElement(), barElement);
		barElement.getStyle().setProperty("height", "100%");
		barElement.setPropertyString("className", "gwt-ProgressBar-bar");

		// Create the text element
		textElement = DOM.createDiv();
		DOM.appendChild(getElement(), textElement);
		textElement.getStyle().setProperty("position", "absolute");
		textElement.getStyle().setProperty("top", "0px");
		textElement.setPropertyString("className",
			"gwt-ProgressBar-text-firstHalf");

		// Set the current progress
		setProgress(progress);
	}

	/**
	 * Get the maximum value.
	 *
	 * @return the maximum value
	 */
	@Override
	public int getMaximum() {
		return maximum;
	}

	/**
	 * Get the minimum value.
	 *
	 * @return the minimum value
	 */
	@Override
	public int getMinimum() {
		return minimum;
	}

	/**
	 * Get the current percent complete, relative to the minimum and maximum
	 * values. The percent will always be between 0.0 - 1.0.
	 *
	 * @return the current percent complete
	 */
	public int getPercent() {
		int percent = 0;

		if (maximum > minimum) {
			percent = (progress - minimum) * 100 / (maximum - minimum);
			percent = Math.max(0, Math.min(100, percent));
		}

		return percent;
	}

	/**
	 * Get the current progress.
	 *
	 * @return the current progress
	 */
	@Override
	public int getProgress() {
		return progress;
	}

	/**
	 * Get the text formatter.
	 *
	 * @return the text formatter
	 */
	public TextFormatter getTextFormatter() {
		return textFormatter;
	}

	/**
	 * Check whether the text is visible or not.
	 *
	 * @return true if the text is visible
	 */
	public boolean isTextVisible() {
		return showText;
	}

	/**
	 * This method is called when the dimensions of the parent element change.
	 * Subclasses should override this method as needed.
	 *
	 * <p>Move the text to the center of the progress bar.</p>
	 *
	 * @param width  the new client width of the element
	 * @param height the new client height of the element
	 */
	public void onResize(int width, int height) {
		if (showText) {
			int textWidth = textElement.getPropertyInt("offsetWidth");
			int left = (width / 2) - (textWidth / 2);

			textElement.getStyle().setProperty("left", left + "px");
		}
	}

	/**
	 * Redraw the progress bar when something changes the layout.
	 */
	public void redraw() {
		if (isAttached()) {
			Element element = getElement();
			int width = element.getPropertyInt("clientWidth");
			int height = element.getPropertyInt("clientHeight");

			onResize(width, height);
		}
	}

	/**
	 * Set the maximum progress. If the minimum progress is more than the
	 * current progress, the current progress is adjusted to be within the new
	 * range.
	 *
	 * @param maximum the maximum progress
	 */
	@Override
	public void setMaximum(int maximum) {
		this.maximum = maximum;
		progress = Math.min(progress, maximum);

		resetProgress();
	}

	/**
	 * Set the minimum progress. If the minimum progress is more than the
	 * current progress, the current progress is adjusted to be within the new
	 * range.
	 *
	 * @param minimum the minimum progress
	 */
	@Override
	public void setMinimum(int minimum) {
		this.minimum = minimum;
		progress = Math.max(progress, minimum);

		resetProgress();
	}

	/**
	 * Set the current progress.
	 *
	 * @param progress the current progress
	 */
	@Override
	public void setProgress(int progress) {
		this.progress = Math.max(minimum, Math.min(maximum, progress));

		// Calculate percent complete
		int percent = getPercent();

		barElement.getStyle().setProperty("width", percent + "%");
		textElement.setPropertyString("innerHTML", generateText(percent));

		// Set the style depending on the size of the bar
		if (percent < 50) {
			textElement.setPropertyString("className",
				"gwt-ProgressBar-text gwt-ProgressBar-text-firstHalf");
		} else {
			textElement.setPropertyString("className",
				"gwt-ProgressBar-text gwt-ProgressBar-text-secondHalf");
		}

		// Realign the text
		redraw();
	}

	/**
	 * Set the text formatter.
	 *
	 * @param textFormatter the text formatter
	 */
	public void setTextFormatter(TextFormatter textFormatter) {
		this.textFormatter = textFormatter;
	}

	/**
	 * Sets whether the text is visible over the bar.
	 *
	 * @param isVisible True to show text, false to hide it
	 */
	public void setTextVisible(boolean isVisible) {
		this.showText = isVisible;

		if (this.showText) {
			textElement.getStyle().setProperty("display", "");
			redraw();
		} else {
			textElement.getStyle().setProperty("display", "none");
		}
	}

	/**
	 * Generate the text to display within the progress bar. Override this
	 * function to change the default progress percent to a more informative
	 * message, such as the number of kilobytes downloaded.
	 *
	 * @param percent curProgress the current progress
	 * @return the text to display in the progress bar
	 */
	protected String generateText(int percent) {
		if (textFormatter != null) {
			return textFormatter.getText(this, percent);
		} else {
			return percent + "%";
		}
	}

	/**
	 * Get the bar element.
	 *
	 * @return the bar element
	 */
	protected Element getBarElement() {
		return barElement;
	}

	/**
	 * Get the text element.
	 *
	 * @return the text element
	 */
	protected Element getTextElement() {
		return textElement;
	}

	/**
	 * This method is called immediately after a widget becomes attached to the
	 * browser's document.
	 */
	@Override
	protected void onLoad() {
		// Reset the position attribute of the parent element
		getElement().getStyle().setProperty("position", "relative");
//		ResizableWidgetCollection.get().add(this);
		redraw();
	}

	/**
	 * @see Widget#onUnload()
	 */
	@Override
	protected void onUnload() {
//		ResizableWidgetCollection.get().remove(this);
	}

	/**
	 * Reset the progress text based on the current min and max progress range.
	 */
	protected void resetProgress() {
		setProgress(getProgress());
	}

	/**
	 * A formatter used to format the text displayed in the progress bar
	 * widget.
	 */
	public static interface TextFormatter {

		/**
		 * Generate the text to display in the ProgressBar based on the current
		 * value.
		 *
		 * <p>Override this method to change the text displayed within the
		 * ProgressBar.</p>
		 *
		 * @param progressBar the progress bar
		 * @param progress    the current progress
		 * @return the text to display in the progress bar
		 */
		public String getText(GwtProgressBar progressBar, int progress);
	}
}
