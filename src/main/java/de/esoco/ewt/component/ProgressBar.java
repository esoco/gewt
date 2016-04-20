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

import de.esoco.ewt.EWT;
import de.esoco.ewt.UserInterfaceContext;
import de.esoco.ewt.event.EventType;
import de.esoco.ewt.impl.gwt.GwtProgressBar;
import de.esoco.ewt.impl.gwt.WidgetFactory;
import de.esoco.ewt.style.StyleData;
import de.esoco.ewt.style.StyleFlag;

import com.google.gwt.user.client.ui.Widget;


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
	//~ Static fields/initializers ---------------------------------------------

	static
	{
		EWT.registerComponentWidgetFactory(ProgressBar.class,
										   new ProgressBarWidgetFactory(),
										   false);
	}

	//~ Methods ----------------------------------------------------------------

	/***************************************
	 * Returns the maximum value.
	 *
	 * @return The maximum value
	 */
	public int getMaximum()
	{
		return (int) getBarWidget().getMaxProgress();
	}

	/***************************************
	 * Returns the minimum value.
	 *
	 * @return The minimum value
	 */
	public int getMinimum()
	{
		return (int) getBarWidget().getMinProgress();
	}

	/***************************************
	 * @see AbstractBar#getValue()
	 */
	public int getValue()
	{
		return (int) getBarWidget().getProgress();
	}

	/***************************************
	 * Sets the maximum value.
	 *
	 * @param nValue The new maximum value
	 */
	public void setMaximum(int nValue)
	{
		getBarWidget().setMaxProgress(nValue);
	}

	/***************************************
	 * Sets the minimum value.
	 *
	 * @param nValue The new minimum value
	 */
	public void setMinimum(int nValue)
	{
		getBarWidget().setMinProgress(nValue);
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
	private GwtProgressBar getBarWidget()
	{
		return (GwtProgressBar) getWidget();
	}

	//~ Inner Classes ----------------------------------------------------------

	/********************************************************************
	 * Widget factory for this component.
	 *
	 * @author eso
	 */
	public static class ProgressBarWidgetFactory
		implements WidgetFactory<Widget>
	{
		//~ Methods ------------------------------------------------------------

		/***************************************
		 * {@inheritDoc}
		 */
		@Override
		public Widget createWidget(
			UserInterfaceContext rContext,
			StyleData			 rStyle)
		{
			return new GwtProgressBar();
		}
	}
}
