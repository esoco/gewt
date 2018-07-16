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
package de.esoco.ewt.layout;

import de.esoco.ewt.component.Container;
import de.esoco.ewt.style.StyleData;

import de.esoco.lib.property.Alignment;
import de.esoco.lib.property.ContentAlignment;
import de.esoco.lib.property.Fluent;
import de.esoco.lib.property.HasCssName;
import de.esoco.lib.property.Orientation;
import de.esoco.lib.property.PropertyName;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;

import static de.esoco.lib.property.LayoutProperties.COLUMN;
import static de.esoco.lib.property.LayoutProperties.COLUMN_SPAN;
import static de.esoco.lib.property.LayoutProperties.HORIZONTAL_ALIGN;
import static de.esoco.lib.property.LayoutProperties.LAYOUT_AREA;
import static de.esoco.lib.property.LayoutProperties.ROW;
import static de.esoco.lib.property.LayoutProperties.ROW_SPAN;
import static de.esoco.lib.property.LayoutProperties.VERTICAL_ALIGN;


/********************************************************************
 * A CSS grid layout implementation. It provides fluent builder methods like
 * {@link #rowGap(String)} that can be invoked successively to set optional grid
 * properties.
 *
 * @author eso
 */
public class GridLayout extends GenericLayout implements Fluent<GridLayout>
{
	//~ Instance fields --------------------------------------------------------

	private boolean bInline		   = false;
	private String  sTemplateAreas = null;

	private String sRowGap = null;
	private String sColGap = null;

	private String sRowTemplate = null;
	private String sColTemplate = null;

	private String sAutoRows = null;
	private String sAutoCols = null;

	private Alignment eHorizontalItemAlignment = null;
	private Alignment eVerticalItemAlignment   = null;

	private ContentAlignment eHorizontalGridAlignment = null;
	private ContentAlignment eVerticalGridAlignment   = null;

	private Orientation eFlowDirection = null;
	private boolean     bDenseFlow     = false;

	//~ Constructors -----------------------------------------------------------

	/***************************************
	 * Creates a new instance.
	 */
	public GridLayout()
	{
	}

	//~ Methods ----------------------------------------------------------------

	/***************************************
	 * {@inheritDoc}
	 */
	@Override
	public void addWidget(HasWidgets rContainer,
						  Widget	 rWidget,
						  StyleData  rStyleData,
						  int		 nIndex)
	{
		super.addWidget(rContainer, rWidget, rStyleData, nIndex);

		String sGridArea = rStyleData.getProperty(LAYOUT_AREA, null);
		Style  rStyle    = rWidget.getElement().getStyle();

		if (sGridArea != null)
		{
			setGridProperty(rStyle, "gridArea", sGridArea);
		}
		else
		{
			applyGridSize(rStyle, "gridRow", rStyleData, ROW, ROW_SPAN);
			applyGridSize(rStyle,
						  "gridColumn",
						  rStyleData,
						  COLUMN,
						  COLUMN_SPAN);
		}

		setGridProperty(rStyle,
						"justifySelf",
						rStyleData.getProperty(HORIZONTAL_ALIGN, null));
		setGridProperty(rStyle,
						"alignSelf",
						rStyleData.getProperty(VERTICAL_ALIGN, null));
	}

	/***************************************
	 * Sets the template areas that define the grid layout
	 * ('grid-template-areas'). This value typically consists of multiple
	 * strings, each defining a row of the grid template. This can be achieved
	 * to surround these row strings by single quotes (') in the argument
	 * string.
	 *
	 * @param  sAreas The grid template area definition
	 *
	 * @return This instance for fluent invocation
	 */
	public GridLayout areas(String sAreas)
	{
		return _with(() -> sTemplateAreas = sAreas);
	}

	/***************************************
	 * Sets the size of automatically generated rows in implicit grids
	 * ('grid-auto-columns').
	 *
	 * @param  sSize The auto-row size
	 *
	 * @return This instance for fluent invocation
	 */
	public GridLayout autoCols(String sSize)
	{
		return _with(() -> sAutoCols = sSize);
	}

	/***************************************
	 * Sets the automatic flow of grid items ('grid-auto-flow').
	 *
	 * @param  eDirection The flow direction
	 * @param  bDense     TRUE to fill in cells earlier in the grid if smaller
	 *                    items come up later (caution, may change order of
	 *                    items and therefore affect accessibility)
	 *
	 * @return This instance for fluent invocation
	 */
	public GridLayout autoFlow(Orientation eDirection, boolean bDense)
	{
		return _with(() ->
		 			{
		 				eFlowDirection = eDirection;
		 				bDenseFlow     = bDense;
					 });
	}

	/***************************************
	 * Sets the size of automatically generated rows in implicit grids
	 * ('grid-auto-rows').
	 *
	 * @param  sSize The auto-row size
	 *
	 * @return This instance for fluent invocation
	 */
	public GridLayout autoRows(String sSize)
	{
		return _with(() -> sAutoRows = sSize);
	}

	/***************************************
	 * Sets the gap between columns.
	 *
	 * @param  sGap The gap as a valid HTML unit
	 *
	 * @return This instance for fluent invocation
	 */
	public GridLayout colGap(String sGap)
	{
		return _with(() -> sColGap = sGap);
	}

	/***************************************
	 * Sets the template for column sizing ('grid-template-columns').
	 *
	 * @param  sTemplate The column sizing template
	 *
	 * @return This instance for fluent invocation
	 */
	public GridLayout columns(String sTemplate)
	{
		return _with(() -> sColTemplate = sTemplate);
	}

	/***************************************
	 * {@inheritDoc}
	 */
	@Override
	public HasWidgets createLayoutContainer(
		Container rContainer,
		StyleData rStyleData)
	{
		FlowPanel aPanel = new FlowPanel();
		Style     rStyle = aPanel.getElement().getStyle();

		rStyle.setProperty("display", bInline ? "inline-grid" : "grid");

		setGridProperty(rStyle, "gridRowGap", sRowGap);
		setGridProperty(rStyle, "gridColGap", sColGap);

		setGridProperty(rStyle, "gridTemplateAreas", sTemplateAreas);
		setGridProperty(rStyle, "gridTemplateRows", sRowTemplate);
		setGridProperty(rStyle, "gridTemplateColumns", sColTemplate);

		setGridProperty(rStyle, "gridAutoRows", sAutoRows);
		setGridProperty(rStyle, "gridAutoColumns", sAutoCols);

		setGridProperty(rStyle, "justifyItems", eHorizontalItemAlignment);
		setGridProperty(rStyle, "alignItems", eVerticalItemAlignment);
		setGridProperty(rStyle, "justifyContent", eHorizontalGridAlignment);
		setGridProperty(rStyle, "alignContent", eVerticalGridAlignment);

		if (eFlowDirection != null)
		{
			String sAutoFlow =
				eFlowDirection == Orientation.HORIZONTAL ? "row" : "column";

			if (bDenseFlow)
			{
				sAutoFlow += " dense";
			}

			setGridProperty(rStyle, "gridAutoFlow", sAutoFlow);
		}

		return aPanel;
	}

	/***************************************
	 * Sets the gap between rows and columns.
	 *
	 * @param  sGap The gap as a valid HTML unit
	 *
	 * @return This instance for fluent invocation
	 *
	 * @see    #colGap(String)
	 * @see    #rowGap(String)
	 */
	public GridLayout gap(String sGap)
	{
		return rowGap(sGap).colGap(sGap);
	}

	/***************************************
	 * Sets the horizontal alignment of the grid container in it's parent.
	 *
	 * @param  eAlignment The horizontal grid alignment
	 *
	 * @return This instance for fluent invocation
	 */
	public GridLayout hAlignGrid(ContentAlignment eAlignment)
	{
		return _with(() -> eHorizontalGridAlignment = eAlignment);
	}

	/***************************************
	 * Sets the horizontal alignment of the items in the grid cells.
	 *
	 * @param  eAlignment The horizontal item alignment
	 *
	 * @return This instance for fluent invocation
	 */
	public GridLayout hAlignItems(Alignment eAlignment)
	{
		return _with(() -> eHorizontalItemAlignment = eAlignment);
	}

	/***************************************
	 * Sets the grid style to inline rendering (display = 'inline-grid').
	 *
	 * @return This instance for fluent invocation
	 */
	public GridLayout inline()
	{
		return _with(() -> bInline = true);
	}

	/***************************************
	 * Sets the gap between rows.
	 *
	 * @param  sGap The gap as a valid HTML unit
	 *
	 * @return This instance for fluent invocation
	 */
	public GridLayout rowGap(String sGap)
	{
		return _with(() -> sRowGap = sGap);
	}

	/***************************************
	 * Sets the template for row sizing ('grid-template-rows').
	 *
	 * @param  sTemplate The row sizing template
	 *
	 * @return This instance for fluent invocation
	 */
	public GridLayout rows(String sTemplate)
	{
		return _with(() -> sRowTemplate = sTemplate);
	}

	/***************************************
	 * Sets the vertical alignment of the grid container in it's parent.
	 *
	 * @param  eAlignment The vertical grid alignment
	 *
	 * @return This instance for fluent invocation
	 */
	public GridLayout vAlignGrid(ContentAlignment eAlignment)
	{
		return _with(() -> eVerticalGridAlignment = eAlignment);
	}

	/***************************************
	 * Sets the vertical alignment of the items in the grid cells.
	 *
	 * @param  eAlignment The vertical item alignment
	 *
	 * @return This instance for fluent invocation
	 */
	public GridLayout vAlignItems(Alignment eAlignment)
	{
		return _with(() -> eVerticalItemAlignment = eAlignment);
	}

	/***************************************
	 * Applies the grid properties of a component style data to a widget's CSS
	 * style.
	 *
	 * @param rStyle            The widget style
	 * @param sCssProperty      The CSS property to set
	 * @param rStyleData        The component style
	 * @param rPositionProperty The property to read the grid position from
	 * @param rSpanProperty     The property to read the grid span from
	 */
	private void applyGridSize(Style				 rStyle,
							   String				 sCssProperty,
							   StyleData			 rStyleData,
							   PropertyName<Integer> rPositionProperty,
							   PropertyName<Integer> rSpanProperty)
	{
		int nPosition = rStyleData.getProperty(rPositionProperty, -1);
		int nSpan     = rStyleData.getProperty(rSpanProperty, 1);

		if (nSpan > 1 && nPosition < 0)
		{
			throw new IllegalArgumentException("Grid posistion needed if grid span is set");
		}
		else if (nSpan <= 0)
		{
			throw new IllegalArgumentException("Invalid grid span: " + nSpan);
		}

		if (nPosition >= 0)
		{
			rStyle.setProperty(sCssProperty, nPosition + " / span " + nSpan);
		}
	}

	/***************************************
	 * Sets a string property on an element style.
	 *
	 * @param rStyle    The element style
	 * @param sProperty The property name
	 * @param sValue    The property value
	 */
	private void setGridProperty(Style rStyle, String sProperty, String sValue)
	{
		if (sValue != null)
		{
			rStyle.setProperty(sProperty, sValue);
		}
	}

	/***************************************
	 * Sets a CSS name property on an element style.
	 *
	 * @param rStyle    The element style
	 * @param sProperty The property name
	 * @param rValue    The property value
	 */
	private void setGridProperty(Style		rStyle,
								 String		sProperty,
								 HasCssName rValue)
	{
		if (rValue != null)
		{
			rStyle.setProperty(sProperty, rValue.getCssName());
		}
	}
}
