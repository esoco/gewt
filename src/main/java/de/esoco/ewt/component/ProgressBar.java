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
package de.esoco.ewt.component;

import de.esoco.ewt.event.EventType;
import de.esoco.ewt.impl.gwt.GwtProgressBar;
import de.esoco.ewt.impl.gwt.WidgetFactory;
import de.esoco.ewt.style.StyleData;

import com.google.gwt.user.client.ui.IsWidget;

/**
 * A component that displays a horizontal or vertical bar representing an
 * integer value.
 *
 * <p>Supported event types:</p>
 *
 * <ul>
 *   <li>{@link EventType#VALUE_CHANGED VALUE_CHANGED}: when the progress bar
 *     value has changed</li>
 * </ul>
 *
 * @author eso
 */
public class ProgressBar extends Component {

	/**
	 * Returns the maximum value.
	 *
	 * @return The maximum value
	 */
	public int getMaximum() {
		return getBarWidget().getMaximum();
	}

	/**
	 * Returns the minimum value.
	 *
	 * @return The minimum value
	 */
	public int getMinimum() {
		return getBarWidget().getMinimum();
	}

	/**
	 * Returns the current value.
	 *
	 * @return The current integer value
	 */
	public int getValue() {
		return getBarWidget().getProgress();
	}

	/**
	 * Sets the maximum value.
	 *
	 * @param value The new maximum value
	 */
	public void setMaximum(int value) {
		getBarWidget().setMaximum(value);
	}

	/**
	 * Sets the minimum value.
	 *
	 * @param value The new minimum value
	 */
	public void setMinimum(int value) {
		getBarWidget().setMinimum(value);
	}

	/**
	 * Sets the integer value.
	 *
	 * @param value The new integer value
	 */
	public void setValue(int value) {
		getBarWidget().setProgress(value);
	}

	/**
	 * Returns the bar widget.
	 *
	 * @return The bar widget
	 */
	private IsProgressBarWidget getBarWidget() {
		return (IsProgressBarWidget) getWidget();
	}

	/**
	 * The interface of progress bar widgets
	 *
	 * @author eso
	 */
	public static interface IsProgressBarWidget extends IsWidget {

		/**
		 * Returns the maximum.
		 *
		 * @return The maximum
		 */
		public int getMaximum();

		/**
		 * Returns the minimum.
		 *
		 * @return The minimum
		 */
		public int getMinimum();

		/**
		 * Returns the current progress value.
		 *
		 * @return The progress
		 */
		public int getProgress();

		/**
		 * Sets the maximum.
		 *
		 * @param maximum The new maximum
		 */
		public void setMaximum(int maximum);

		/**
		 * Sets the minimum.
		 *
		 * @param minimum The new minimum
		 */
		public void setMinimum(int minimum);

		/**
		 * Sets the current progress value.
		 *
		 * @param progress The new progress value
		 */
		public void setProgress(int progress);
	}

	/**
	 * Widget factory for this component.
	 *
	 * @author eso
	 */
	public static class ProgressBarWidgetFactory
		implements WidgetFactory<IsProgressBarWidget> {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public IsProgressBarWidget createWidget(Component component,
			StyleData style) {
			return new GwtProgressBar();
		}
	}
}
