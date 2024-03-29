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

import de.esoco.ewt.event.EventType;
import de.esoco.ewt.style.StyleData;
import de.esoco.ewt.style.StyleFlag;

/**
 * A combo box that combines a text field with a list of values. The values can
 * be selected from the list to be displayed and (optionally) edited in the text
 * field.
 *
 * <p>Supported event types:</p>
 *
 * <ul>
 *   <li>{@link EventType#ACTION ACTION}: when the value changes (either because
 *     of popup selection or by confirming keyboard input)</li>
 *   <li>{@link EventType#VALUE_CHANGED VALUE_CHANGED}: if the contents of the
 *     text field is edited</li>
 * </ul>
 *
 * <p>Supported style flags:</p>
 *
 * <ul>
 *   <li>{@link StyleFlag#READ_ONLY READ_ONLY}: to prevent text editing (i.e.
 *     only list selections will be possible)</li>
 * </ul>
 *
 * @author eso
 */
public class ListBox extends ListControl {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initWidget(Container parent, StyleData style) {
		super.initWidget(parent, style);

		getGwtListBox().setVisibleItemCount(1);
	}
}
