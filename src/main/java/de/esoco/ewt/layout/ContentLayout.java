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
package de.esoco.ewt.layout;

import de.esoco.ewt.component.Container;
import de.esoco.ewt.style.StyleData;

import de.esoco.lib.property.Layout;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.LayoutPanel;


/********************************************************************
 * A layout class for typical content area in application. The default
 * implementation always creates a {@link LayoutPanel} container but extended
 * layout factories may create different containers based on the layout type
 * returned by the method {@link #getLayoutType()}.
 *
 * @author eso
 */
public class ContentLayout extends GenericLayout
{
	//~ Instance fields --------------------------------------------------------

	private final Layout eLayoutType;

	//~ Constructors -----------------------------------------------------------

	/***************************************
	 * Creates a new instance.
	 *
	 * @param eLayoutType The type of this content layout
	 */
	public ContentLayout(Layout eLayoutType)
	{
		this.eLayoutType = eLayoutType;
	}

	//~ Methods ----------------------------------------------------------------

	/***************************************
	 * {@inheritDoc}
	 */
	@Override
	public HasWidgets createLayoutContainer(
		Container rContainer,
		StyleData rStyle)
	{
		return new LayoutPanel();
	}

	/***************************************
	 * Returns the layout value.
	 *
	 * @return The layout value
	 */
	public final Layout getLayoutType()
	{
		return eLayoutType;
	}
}
