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

import com.google.gwt.dom.client.Style;
import de.esoco.ewt.style.StyleData;
import de.esoco.lib.property.Alignment;
import de.esoco.lib.property.ContentAlignment;
import de.esoco.lib.property.Orientation;
import de.esoco.lib.property.PropertyName;

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

	private boolean inline = false;

	private String templateAreas = null;

	private String rowGap = null;

	private String colGap = null;

	private String rowTemplate = null;

	private String colTemplate = null;

	private String autoRows = null;

	private String autoCols = null;

	private Alignment horizontalItemAlignment = null;

	private Alignment verticalItemAlignment = null;

	private ContentAlignment horizontalGridAlignment = null;

	private ContentAlignment verticalGridAlignment = null;

	private Orientation flowDirection = null;

	private boolean denseFlow = false;

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
	 * @param columns The column definitions (@see {@link #columns(String)})
	 * @return The new grid layout
	 */
	public static GridLayout grid(String columns) {
		return new GridLayout().columns(columns);
	}

	/**
	 * A factory method to create a new instance with certain row and column
	 * definitions.
	 *
	 * @param rows    The row definitions (@see {@link #rows(String)})
	 * @param columns The column definitions (@see {@link #columns(String)})
	 * @return The new grid layout
	 */
	public static GridLayout grid(String rows, String columns) {
		return new GridLayout().rows(rows).columns(columns);
	}

	/**
	 * Sets the template areas property that define the grid layout
	 * ('grid-template-areas'). The argument strings each define a row of the
	 * grid template.
	 *
	 * @param areas The template areas for the grid rows
	 * @return This instance for fluent invocation
	 */
	public final GridLayout areas(String... areas) {
		StringBuilder areasDefinition = new StringBuilder();

		for (String area : areas) {
			areasDefinition.append('\'').append(area).append('\'');
		}

		return _with(() -> templateAreas = areasDefinition.toString());
	}

	/**
	 * Sets the size of automatically generated rows in implicit grids
	 * ('grid-auto-columns').
	 *
	 * @param size The auto-row size
	 * @return This instance for fluent invocation
	 */
	public final GridLayout autoCols(String size) {
		return _with(() -> autoCols = size);
	}

	/**
	 * Sets the automatic flow of grid items ('grid-auto-flow').
	 *
	 * @param direction The flow direction
	 * @param dense     TRUE to fill in cells earlier in the grid if smaller
	 *                  items come up later (caution, may change order of items
	 *                  and therefore affect accessibility)
	 * @return This instance for fluent invocation
	 */
	public final GridLayout autoFlow(Orientation direction, boolean dense) {
		return _with(() -> {
			flowDirection = direction;
			denseFlow = dense;
		});
	}

	/**
	 * Sets the size of automatically generated rows in implicit grids
	 * ('grid-auto-rows').
	 *
	 * @param size The auto-row size
	 * @return This instance for fluent invocation
	 */
	public final GridLayout autoRows(String size) {
		return _with(() -> autoRows = size);
	}

	/**
	 * Sets the gap between columns.
	 *
	 * @param gap The gap as a valid HTML unit
	 * @return This instance for fluent invocation
	 */
	public final GridLayout colGap(String gap) {
		return _with(() -> colGap = gap);
	}

	/**
	 * Sets the template for column sizing ('grid-template-columns').
	 *
	 * @param template The column sizing template
	 * @return This instance for fluent invocation
	 */
	public final GridLayout columns(String template) {
		return _with(() -> colTemplate = template);
	}

	/**
	 * Sets the gap between rows and columns.
	 *
	 * @param gap The gap as a valid HTML unit
	 * @return This instance for fluent invocation
	 * @see #colGap(String)
	 * @see #rowGap(String)
	 */
	public final GridLayout gaps(String gap) {
		return rowGap(gap).colGap(gap);
	}

	/**
	 * Sets the horizontal alignment of the grid container in its parent.
	 *
	 * @param alignment The horizontal grid alignment
	 * @return This instance for fluent invocation
	 */
	public final GridLayout hAlign(ContentAlignment alignment) {
		return _with(() -> horizontalGridAlignment = alignment);
	}

	/**
	 * Sets the horizontal alignment of the items in the grid cells.
	 *
	 * @param alignment The horizontal item alignment
	 * @return This instance for fluent invocation
	 */
	public final GridLayout horizontalAlignItems(Alignment alignment) {
		return _with(() -> horizontalItemAlignment = alignment);
	}

	/**
	 * Enables inline rendering (display = 'inline-grid').
	 *
	 * @return This instance for fluent invocation
	 */
	public final GridLayout inline() {
		return _with(() -> inline = true);
	}

	/**
	 * Sets the gap between rows.
	 *
	 * @param gap The gap as a valid HTML unit
	 * @return This instance for fluent invocation
	 */
	public final GridLayout rowGap(String gap) {
		return _with(() -> rowGap = gap);
	}

	/**
	 * Sets the template for row sizing ('grid-template-rows').
	 *
	 * @param template The row sizing template
	 * @return This instance for fluent invocation
	 */
	public final GridLayout rows(String template) {
		return _with(() -> rowTemplate = template);
	}

	/**
	 * Sets the vertical alignment of the grid container in its parent.
	 *
	 * @param alignment The vertical grid alignment
	 * @return This instance for fluent invocation
	 */
	public final GridLayout vAlign(ContentAlignment alignment) {
		return _with(() -> verticalGridAlignment = alignment);
	}

	/**
	 * Sets the vertical alignment of the items in the grid cells.
	 *
	 * @param alignment The vertical item alignment
	 * @return This instance for fluent invocation
	 */
	public final GridLayout verticalAlignItems(Alignment alignment) {
		return _with(() -> verticalItemAlignment = alignment);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void applyLayoutStyle(StyleData styleData, Style style) {
		style.setProperty("display", inline ? "inline-grid" : "grid");

		setStyleProperty("gridRowGap", style, rowGap);
		setStyleProperty("gridColumnGap", style, colGap);

		setStyleProperty("gridTemplateAreas", style, templateAreas);
		setStyleProperty("gridTemplateRows", style, rowTemplate);
		setStyleProperty("gridTemplateColumns", style, colTemplate);

		setStyleProperty("gridAutoRows", style, autoRows);
		setStyleProperty("gridAutoColumns", style, autoCols);

		setStyleProperty("justifyItems", style, horizontalItemAlignment);
		setStyleProperty("alignItems", style, verticalItemAlignment);
		setStyleProperty("justifyContent", style, horizontalGridAlignment);
		setStyleProperty("alignContent", style, verticalGridAlignment);

		if (flowDirection != null) {
			String autoFlow =
				flowDirection == Orientation.HORIZONTAL ? "row" : "column";

			if (denseFlow) {
				autoFlow += " dense";
			}

			setStyleProperty("gridAutoFlow", style, autoFlow);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void applyWidgetStyle(StyleData styleData, Style style) {
		String gridArea = styleData.getProperty(LAYOUT_AREA, null);

		if (gridArea != null) {
			setStyleProperty("gridArea", style, gridArea);
		} else {
			applyGridSize(style, "gridRow", styleData, ROW, ROW_SPAN);
			applyGridSize(style, "gridColumn", styleData, COLUMN, COLUMN_SPAN);
		}

		setStyleProperty(HORIZONTAL_ALIGN, styleData, "justifySelf", style);
		setStyleProperty(VERTICAL_ALIGN, styleData, "alignSelf", style);
	}

	/**
	 * Applies the grid properties of a component style data to a widget's CSS
	 * style.
	 *
	 * @param style            The widget style
	 * @param cssProperty      The CSS property to set
	 * @param styleData        The component style
	 * @param positionProperty The property to read the grid position from
	 * @param spanProperty     The property to read the grid span from
	 */
	private void applyGridSize(Style style, String cssProperty,
		StyleData styleData, PropertyName<Integer> positionProperty,
		PropertyName<Integer> spanProperty) {
		int position = styleData.getProperty(positionProperty, -1);
		int span = styleData.getProperty(spanProperty, 1);

		if (span > 1 && position <= 0) {
			throw new IllegalArgumentException(
				"Grid posistion needed if grid span is set");
		} else if (span <= 0) {
			throw new IllegalArgumentException("Invalid grid span: " + span);
		}

		if (position > 0) {
			style.setProperty(cssProperty, position + " / span " + span);
		}
	}
}
