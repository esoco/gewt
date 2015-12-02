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
package de.esoco.ewt.layout;

import de.esoco.ewt.style.StyleData;

import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Widget;


/********************************************************************
 * A layout implementation similar to the SWT FillLayout. It arranges components
 * in a row or column and distributes them over the available space.
 *
 * @author eso
 */
@SuppressWarnings("unused")
public class FillLayout extends GenericLayout
{
	//~ Instance fields --------------------------------------------------------

	private final boolean bHorizontal;
	private final int     nGap;

	//~ Constructors -----------------------------------------------------------

	/***************************************
	 * Creates a new FillLayout with either horizontal or vertical layout of
	 * components and no gaps or margins.
	 *
	 * @param bHorizontal TRUE for horizontal orientation, FALSE for vertical
	 */
	public FillLayout(boolean bHorizontal)
	{
		this(bHorizontal, 0);
	}

	/***************************************
	 * Creates a new FillLayout with horizontal arrangement of components and
	 * certain gaps between components.
	 *
	 * @param nGap The distance between components
	 */
	public FillLayout(int nGap)
	{
		this(true, nGap);
	}

	/***************************************
	 * Creates a new FillLayout with a certain orientation, margins around the
	 * layout area and gaps between components.
	 *
	 * @param bHorizontal TRUE for horizontal orientation, FALSE for vertical
	 * @param nGap        The distance between components
	 */
	public FillLayout(boolean bHorizontal, int nGap)
	{
		this.bHorizontal = bHorizontal;
		this.nGap		 = nGap;
	}

	//~ Methods ----------------------------------------------------------------

	/***************************************
	 * @see GenericLayout#addWidget(HasWidgets, Widget, StyleData, int)
	 */
	@Override
	public void addWidget(HasWidgets rContainer,
						  Widget	 rWidget,
						  StyleData  rStyleData,
						  int		 nIndex)
	{
		super.addWidget(rContainer, rWidget, rStyleData, nIndex);

		// set parent DIV of widget to automatic scrollbar display
		rWidget.getElement().getParentElement().getStyle()
			   .setOverflow(Overflow.AUTO);
	}

	/***************************************
	 * Returns a {@link LayoutPanel} instance.
	 *
	 * @see GenericLayout#createLayoutContainer()
	 */
	@Override
	public HasWidgets createLayoutContainer()
	{
		LayoutPanel aPanel = new LayoutPanel();

		return aPanel;
	}
}
