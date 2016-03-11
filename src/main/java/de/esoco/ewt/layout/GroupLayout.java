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

import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.HasWidgets;


/********************************************************************
 * A layout that groups components with an optional title/caption.
 *
 * @author eso
 */
public class GroupLayout extends TwoLayerLayout
{
	//~ Constructors -----------------------------------------------------------

	/***************************************
	 * Creates a new instance that is based on a GWT {@link CaptionPanel}.
	 */
	public GroupLayout()
	{
	}

	//~ Methods ----------------------------------------------------------------

	/***************************************
	 * {@inheritDoc}
	 */
	@Override
	protected HasWidgets createLayoutPanel()
	{
		return new CaptionPanel();
	}
}
