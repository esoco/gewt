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

import de.esoco.ewt.event.EventType;
import de.esoco.ewt.impl.gwt.GwtProgressBar;
import de.esoco.ewt.impl.gwt.WidgetFactory;
import de.esoco.ewt.style.StyleData;
import de.esoco.ewt.style.StyleFlag;

import com.google.gwt.user.client.ui.IsWidget;


/********************************************************************
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
 * <p>Supported style flags:</p>
 *
 * <ul>
 *   <li>{@link StyleFlag#VERTICAL VERTICAL}: (currently not supported in GEWT)
 *     for a vertical progress bar (default is horizontal)</li>
 * </ul>
 *
 * @author eso
 */
public class ProgressBar extends Component
{
	//~ Methods ----------------------------------------------------------------

	/***************************************
	 * Returns the maximum value.
	 *
	 * @return The maximum value
	 */
	public int getMaximum()
	{
		return getBarWidget().getMaximum();
	}

	/***************************************
	 * Returns the minimum value.
	 *
	 * @return The minimum value
	 */
	public int getMinimum()
	{
		return getBarWidget().getMinimum();
	}

	/***************************************
	 * Returns the current value.
	 *
	 * @return The current integer value
	 */
	public int getValue()
	{
		return getBarWidget().getProgress();
	}

	/***************************************
	 * Sets the maximum value.
	 *
	 * @param nValue The new maximum value
	 */
	public void setMaximum(int nValue)
	{
		getBarWidget().setMaximum(nValue);
	}

	/***************************************
	 * Sets the minimum value.
	 *
	 * @param nValue The new minimum value
	 */
	public void setMinimum(int nValue)
	{
		getBarWidget().setMinimum(nValue);
	}

	/***************************************
	 * Sets the integer value.
	 *
	 * @param nValue The new integer value
	 */
	public void setValue(int nValue)
	{
		getBarWidget().setProgress(nValue);
	}

	/***************************************
	 * Returns the bar widget.
	 *
	 * @return The bar widget
	 */
	private IsProgressBarWidget getBarWidget()
	{
		return (IsProgressBarWidget) getWidget();
	}

	//~ Inner Interfaces -------------------------------------------------------

	/********************************************************************
	 * The interface of progress bar widgets
	 *
	 * @author eso
	 */
	public static interface IsProgressBarWidget extends IsWidget
	{
		//~ Methods ------------------------------------------------------------

		/***************************************
		 * Returns the maximum.
		 *
		 * @return The maximum
		 */
		public int getMaximum();

		/***************************************
		 * Returns the minimum.
		 *
		 * @return The minimum
		 */
		public int getMinimum();

		/***************************************
		 * Returns the current progress value.
		 *
		 * @return The progress
		 */
		public int getProgress();

		/***************************************
		 * Sets the maximum.
		 *
		 * @param nMaximum The new maximum
		 */
		public void setMaximum(int nMaximum);

		/***************************************
		 * Sets the minimum.
		 *
		 * @param nMinimum The new minimum
		 */
		public void setMinimum(int nMinimum);

		/***************************************
		 * Sets the current progress value.
		 *
		 * @param nProgress The new progress value
		 */
		public void setProgress(int nProgress);
	}

	//~ Inner Classes ----------------------------------------------------------

	/********************************************************************
	 * Widget factory for this component.
	 *
	 * @author eso
	 */
	public static class ProgressBarWidgetFactory
		implements WidgetFactory<IsProgressBarWidget>
	{
		//~ Methods ------------------------------------------------------------

		/***************************************
		 * {@inheritDoc}
		 */
		@Override
		public IsProgressBarWidget createWidget(
			Component rComponent,
			StyleData rStyle)
		{
			return new GwtProgressBar();
		}
	}
}
