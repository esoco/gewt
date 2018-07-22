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

import de.esoco.ewt.build.ContainerBuilder;
import de.esoco.ewt.layout.GenericLayout;
import de.esoco.ewt.style.StyleData;

import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;


/********************************************************************
 * Base class for components that are composed from a combination of other
 * components.
 *
 * @author eso
 */
public abstract class Composite extends Component
{
	//~ Instance fields --------------------------------------------------------

	private Panel   aContentPanel = new Panel();
	private boolean bFocusable;

	//~ Constructors -----------------------------------------------------------

	/***************************************
	 * Creates a new instance with a certain layout.
	 *
	 * @param rLayout    The layout of the composite container
	 * @param bFocusable TRUE if this composite should be focusable to produce
	 *                   input events
	 */
	protected Composite(GenericLayout rLayout, boolean bFocusable)
	{
		this.bFocusable = bFocusable;

		aContentPanel.setLayout(rLayout);
	}

	//~ Methods ----------------------------------------------------------------

	/***************************************
	 * {@inheritDoc}
	 */
	@Override
	public void initWidget(Container rParent, StyleData rStyle)
	{
		super.initWidget(rParent, rStyle);

		build(new ContainerBuilder<Panel>(aContentPanel));
	}

	/***************************************
	 * Must be implemented by subclasses to build their content with the given
	 * builder.
	 *
	 * @param rBuilder The builder to add the content components with
	 */
	protected abstract void build(ContainerBuilder<?> rBuilder);

	/***************************************
	 * {@inheritDoc}
	 */
	@Override
	protected IsWidget createWidget(StyleData rStyle)
	{
		aContentPanel.initWidget(getParent(), rStyle);

		Widget rWidget = aContentPanel.getWidget();

		if (bFocusable)
		{
			rWidget = new FocusPanel(rWidget);
		}

		return rWidget;
	}
}
