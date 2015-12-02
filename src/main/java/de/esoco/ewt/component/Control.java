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
package de.esoco.ewt.component;

import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.Widget;


/********************************************************************
 * Controls are non-container components that generate events from user
 * interaction.
 *
 * @author eso
 */
public abstract class Control extends Component
{
	/***************************************
	 * @see Component#Component()
	 */
	protected Control()
	{
	}

	/***************************************
	 * @see Component#Component(Widget)
	 */
	protected Control(Focusable rWidget)
	{
		super((Widget) rWidget);
	}

	/***************************************
	 * Requests that the component gets the input focus. It is not guaranteed
	 * that the component will actually have or get the focus after invoking
	 * this method.
	 */
	public void requestFocus()
	{
		((Focusable) getWidget()).setFocus(true);
	}
}
