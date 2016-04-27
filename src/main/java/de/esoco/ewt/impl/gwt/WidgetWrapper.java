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
package de.esoco.ewt.impl.gwt;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;


/********************************************************************
 * A wrapper for widget instances that can be extended to implement additional
 * interface methods that provide functionality that is not available directly
 * in the original widget. The Wrapper only wraps implementations of the
 * interface {@link IsWidget} but the method {@link #getWidget()} gives access
 * to the actual widget implementation.
 *
 * @author eso
 */
public class WidgetWrapper<W extends Widget> implements IsWidget
{
	//~ Instance fields --------------------------------------------------------

	private W rWidget;

	//~ Constructors -----------------------------------------------------------

	/***************************************
	 * Creates a new instance.
	 *
	 * @param rWidget The widget to be wrapped by this instance
	 */
	public WidgetWrapper(W rWidget)
	{
		this.rWidget = rWidget;
	}

	//~ Methods ----------------------------------------------------------------

	/***************************************
	 * {@inheritDoc}
	 */
	@Override
	public Widget asWidget()
	{
		return rWidget;
	}

	/***************************************
	 * Returns the wrapped GWT widget.
	 *
	 * @return The wrapped widget
	 */
	protected final W getWidget()
	{
		return rWidget;
	}
}
