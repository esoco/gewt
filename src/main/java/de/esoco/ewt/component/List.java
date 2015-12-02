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

import de.esoco.ewt.event.EventType;
import de.esoco.ewt.style.StyleData;
import de.esoco.ewt.style.StyleFlag;

import com.google.gwt.user.client.ui.ListBox;


/********************************************************************
 * A scrollable list of selectable elements.
 *
 * <p>Supported event types:</p>
 *
 * <ul>
 *   <li>{@link EventType#SELECTION SELECTION}: if the selection changes</li>
 *   <li>{@link EventType#ACTION ACTION}: if the user uses the platform's
 *     default selection function (e.g. pressing the enter key)</li>
 * </ul>
 *
 * <p>Supported style flags:</p>
 *
 * <ul>
 *   <li>{@link StyleFlag#MULTISELECT MULTISELECT}: to enable multi-selection
 *     mode</li>
 * </ul>
 *
 * @author eso
 */
public class List extends ListControl
{
	//~ Constructors -----------------------------------------------------------

	/***************************************
	 * Creates a new instance.
	 *
	 * @see ListControl#ListControl(int, StyleData)
	 */
	public List(StyleData rStyleData)
	{
		super(10, rStyleData);
	}

	//~ Methods ----------------------------------------------------------------

	/***************************************
	 * Returns the number of visible items.
	 *
	 * @return The number of visible items
	 */
	public int getVisibleItems()
	{
		return ((ListBox) getWidget()).getVisibleItemCount();
	}

	/***************************************
	 * Sets the number of visible items.
	 *
	 * @param nItems The number of visible items
	 */
	public void setVisibleItems(int nItems)
	{
		((ListBox) getWidget()).setVisibleItemCount(nItems);
	}
}
