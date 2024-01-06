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

import de.esoco.ewt.style.StyleData;

import de.esoco.lib.property.Alignment;
import de.esoco.lib.property.ContentAlignment;
import de.esoco.lib.property.Orientation;
import de.esoco.lib.property.PropertyName;

import com.google.gwt.dom.client.Style;

import static de.esoco.lib.property.LayoutProperties.COLUMN;
import static de.esoco.lib.property.LayoutProperties.COLUMN_SPAN;
import static de.esoco.lib.property.LayoutProperties.HORIZONTAL_ALIGN;
import static de.esoco.lib.property.LayoutProperties.LAYOUT_AREA;
import static de.esoco.lib.property.LayoutProperties.ROW;
import static de.esoco.lib.property.LayoutProperties.ROW_SPAN;
import static de.esoco.lib.property.LayoutProperties.VERTICAL_ALIGN;

/**
 * A CSS grid layout implementation. It provides fluent builder methods like
 * {@link #rowGap(String)} that can be invoked successively to set optional grid
 * properties.
 *
 * @author eso
 */
public class GridLayout extends FluentCssLayout<GridLayout> {

	private boolean bInline = false;

	private String sTemplateAreas = null;

	private String sRowGap = null;

	private String sColGap = null;

	private String sRowTemplate = null;

	private String sColTemplate = null;

	private String sAutoRows = null;

	private String sAutoCols = null;

	private Alignment eHorizontalItemAlignment = null;

	private Alignment eVerticalItemAlignment = null;

	private ContentAlignment eHorizontalGridAlignment = null;

	private ContentAlignment eVerticalGridAlignment = null;

	private Orientation eFlowDirection = null;

	private boolean bDenseFlow = false;

	/**
	 * Creates a new instance.
	 */
	public GridLayout() {
	}

	/**
	 * A factory method to create a new uninitialized instance.
	 *
	 * @return The new grid layout
	 */
	public static GridLayout grid() {
		return new GridLayout();
	}

	/**
	 * A factory method to create a new instance with certain column
	 * definitions.
	 *
	 * @param sColumns The column definitions (@see {@link #columns(String)})
	 * @return The new grid layout
	 */
	public static GridLayout grid(String sColumns) {
		return new GridLayout().columns(sColumns);
	}

	/**
	 * A factory method to create a new instance with certain row and column
	 * definitions.
	 *
	 * @param sRows    The row definitions (@see {@link #rows(String)})
	 * @param sColumns The column definitions (@see {@link #columns(String)})
	 * @return The new grid layout
	 */
	public static GridLayout grid(String sRows, String sColumns) {
		return new GridLayout().rows(sRows).columns(sColumns);
	}

	/**
	 * Sets the template areas property that define the grid layout
	 * ('grid-template-areas'). The argument strings each define a row of the
	 * grid template.
	 *
	 * @param aAreas The template areas for the grid rows
	 * @return This instance for fluent invocation
	 */
	public final GridLayout areas(String... aAreas) {
		StringBuilder aAreasDefinition = new StringBuilder();

		for (String sArea : aAreas) {
			aAreasDefinition.append('\'').append(sArea).append('\'');
		}

		return _with(() -> sTemplateAreas = aAreasDefinition.toString());
	}

	/**
	 * Sets the size of automatically generated rows in implicit grids
	 * ('grid-auto-columns').
	 *
	 * @param sSize The auto-row size
	 * @return This instance for fluent invocation
	 */
	public final GridLayout autoCols(String sSize) {
		return _with(() -> sAutoCols = sSize);
	}

	/**
	 * Sets the automatic flow of grid items ('grid-auto-flow').
	 *
	 * @param eDirection The flow direction
	 * @param bDense     TRUE to fill in cells earlier in the grid if smaller
	 *                   items come up later (caution, may change order of
	 *                   items
	 *                   and therefore affect accessibility)
	 * @return This instance for fluent invocation
	 */
	public final GridLayout autoFlow(Orientation eDirection, boolean bDense) {
		return _with(() -> {
			eFlowDirection = eDirection;
			bDenseFlow = bDense;
		});
	}

	/**
	 * Sets the size of automatically generated rows in implicit grids
	 * ('grid-auto-rows').
	 *
	 * @param sSize The auto-row size
	 * @return This instance for fluent invocation
	 */
	public final GridLayout autoRows(String sSize) {
		return _with(() -> sAutoRows = sSize);
	}

	/**
	 * Sets the gap between columns.
	 *
	 * @param sGap The gap as a valid HTML unit
	 * @return This instance for fluent invocation
	 */
	public final GridLayout colGap(String sGap) {
		return _with(() -> sColGap = sGap);
	}

	/**
	 * Sets the template for column sizing ('grid-template-columns').
	 *
	 * @param sTemplate The column sizing template
	 * @return This instance for fluent invocation
	 */
	public final GridLayout columns(String sTemplate) {
		return _with(() -> sColTemplate = sTemplate);
	}

	/**
	 * Sets the gap between rows and columns.
	 *
	 * @param sGap The gap as a valid HTML unit
	 * @return This instance for fluent invocation
	 * @see #colGap(String)
	 * @see #rowGap(String)
	 */
	public final GridLayout gaps(String sGap) {
		return rowGap(sGap).colGap(sGap);
	}

	/**
	 * Sets the horizontal alignment of the grid container in it's parent.
	 *
	 * @param eAlignment The horizontal grid alignment
	 * @return This instance for fluent invocation
	 */
	public final GridLayout hAlign(ContentAlignment eAlignment) {
		return _with(() -> eHorizontalGridAlignment = eAlignment);
	}

	/**
	 * Sets the horizontal alignment of the items in the grid cells.
	 *
	 * @param eAlignment The horizontal item alignment
	 * @return This instance for fluent invocation
	 */
	public final GridLayout hAlignItems(Alignment eAlignment) {
		return _with(() -> eHorizontalItemAlignment = eAlignment);
	}

	/**
	 * Enables inline rendering (display = 'inline-grid').
	 *
	 * @return This instance for fluent invocation
	 */
	public final GridLayout inline() {
		return _with(() -> bInline = true);
	}

	/**
	 * Sets the gap between rows.
	 *
	 * @param sGap The gap as a valid HTML unit
	 * @return This instance for fluent invocation
	 */
	public final GridLayout rowGap(String sGap) {
		return _with(() -> sRowGap = sGap);
	}

	/**
	 * Sets the template for row sizing ('grid-template-rows').
	 *
	 * @param sTemplate The row sizing template
	 * @return This instance for fluent invocation
	 */
	public final GridLayout rows(String sTemplate) {
		return _with(() -> sRowTemplate = sTemplate);
	}

	/**
	 * Sets the vertical alignment of the grid container in it's parent.
	 *
	 * @param eAlignment The vertical grid alignment
	 * @return This instance for fluent invocation
	 */
	public final GridLayout vAlign(ContentAlignment eAlignment) {
		return _with(() -> eVerticalGridAlignment = eAlignment);
	}

	/**
	 * Sets the vertical alignment of the items in the grid cells.
	 *
	 * @param eAlignment The vertical item alignment
	 * @return This instance for fluent invocation
	 */
	public final GridLayout vAlignItems(Alignment eAlignment) {
		return _with(() -> eVerticalItemAlignment = eAlignment);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void applyLayoutStyle(StyleData rStyleData, Style rStyle) {
		rStyle.setProperty("display", bInline ? "inline-grid" : "grid");

		setStyleProperty("gridRowGap", rStyle, sRowGap);
		setStyleProperty("gridColumnGap", rStyle, sColGap);

		setStyleProperty("gridTemplateAreas", rStyle, sTemplateAreas);
		setStyleProperty("gridTemplateRows", rStyle, sRowTemplate);
		setStyleProperty("gridTemplateColumns", rStyle, sColTemplate);

		setStyleProperty("gridAutoRows", rStyle, sAutoRows);
		setStyleProperty("gridAutoColumns", rStyle, sAutoCols);

		setStyleProperty("justifyItems", rStyle, eHorizontalItemAlignment);
		setStyleProperty("alignItems", rStyle, eVerticalItemAlignment);
		setStyleProperty("justifyContent", rStyle, eHorizontalGridAlignment);
		setStyleProperty("alignContent", rStyle, eVerticalGridAlignment);

		if (eFlowDirection != null) {
			String sAutoFlow =
				eFlowDirection == Orientation.HORIZONTAL ? "row" : "column";

			if (bDenseFlow) {
				sAutoFlow += " dense";
			}

			setStyleProperty("gridAutoFlow", rStyle, sAutoFlow);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void applyWidgetStyle(StyleData rStyleData, Style rStyle) {
		String sGridArea = rStyleData.getProperty(LAYOUT_AREA, null);

		if (sGridArea != null) {
			setStyleProperty("gridArea", rStyle, sGridArea);
		} else {
			applyGridSize(rStyle, "gridRow", rStyleData, ROW, ROW_SPAN);
			applyGridSize(rStyle, "gridColumn", rStyleData, COLUMN,
				COLUMN_SPAN);
		}

		setStyleProperty(HORIZONTAL_ALIGN, rStyleData, "justifySelf", rStyle);
		setStyleProperty(VERTICAL_ALIGN, rStyleData, "alignSelf", rStyle);
	}

	/**
	 * Applies the grid properties of a component style data to a widget's CSS
	 * style.
	 *
	 * @param rStyle            The widget style
	 * @param sCssProperty      The CSS property to set
	 * @param rStyleData        The component style
	 * @param rPositionProperty The property to read the grid position from
	 * @param rSpanProperty     The property to read the grid span from
	 */
	private void applyGridSize(Style rStyle, String sCssProperty,
		StyleData rStyleData, PropertyName<Integer> rPositionProperty,
		PropertyName<Integer> rSpanProperty) {
		int nPosition = rStyleData.getProperty(rPositionProperty, -1);
		int nSpan = rStyleData.getProperty(rSpanProperty, 1);

		if (nSpan > 1 && nPosition <= 0) {
			throw new IllegalArgumentException(
				"Grid posistion needed if grid span is set");
		} else if (nSpan <= 0) {
			throw new IllegalArgumentException("Invalid grid span: " + nSpan);
		}

		if (nPosition > 0) {
			rStyle.setProperty(sCssProperty, nPosition + " / span " + nSpan);
		}
	}
}
