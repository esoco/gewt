//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// This file is a part of the 'gewt' project.
// Copyright 2017 Elmar Sonnenschein, esoco GmbH, Flensburg, Germany
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

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.ui.HTMLTable.CellFormatter;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;
import de.esoco.ewt.EWT;
import de.esoco.ewt.component.Container;
import de.esoco.ewt.geometry.Margins;
import de.esoco.ewt.style.AlignedPosition;
import de.esoco.ewt.style.StyleData;
import de.esoco.lib.property.Alignment;

/**
 * A generic layout implementation similar to the AWT BorderLayout but with the
 * additional possibility to add components to the edges. The position of a
 * layout element can be set as a constraint either by one of the enumerated
 * style data constants in the class {@link AlignedPosition}. This
 * implementation is based on a GWT {@link FlexTable}. A simpler version that
 * uses a layout panel instead is provided by the class {@link DockLayout}.
 *
 * @author eso
 */
public class EdgeLayout extends GenericLayout {

	private final int gap;

	private final Widget[][] widgets = new Widget[3][3];

	/**
	 * Creates a new EdgeLayout object with specific gaps between the layout
	 * cells.
	 *
	 * @param gap The horizontal and vertical gap between components
	 */
	public EdgeLayout(int gap) {
		this.gap = gap;
	}

	/**
	 * Creates a new EdgeLayout object with certain gaps between the layout
	 * cells. In GEWT currently only the horizontal gap value will be used
	 * because of the limitations of the underlying GWT widgets.
	 *
	 * @param horizontalGap gapW The horizontal gap between components
	 * @param verticalGap   gapY The vertical gap between components
	 */
	public EdgeLayout(int horizontalGap, int verticalGap) {
		this.gap = horizontalGap;
	}

	/**
	 * Creates a new EdgeLayout object with certain margins and gaps between
	 * the
	 * layout cells. In GEWT currently only the horizontal gap value will be
	 * used because of the limitations of the underlying GWT widgets.
	 *
	 * @param margins       The margins
	 * @param horizontalGap gapW The horizontal gap between components
	 * @param verticalGap   gapY The vertical gap between components
	 */
	public EdgeLayout(Margins margins, int horizontalGap, int verticalGap) {
		this.gap = horizontalGap;
	}

	/**
	 * Adds the widget with the alignment defined in the style data.
	 *
	 * @see GenericLayout#addWidget(HasWidgets, Widget, StyleData, int)
	 */
	@Override
	public void addWidget(HasWidgets container, Widget widget,
		StyleData styleData, int index) {
		Alignment vAlign = styleData.getVerticalAlignment();
		Alignment hAlign = styleData.getHorizontalAlignment();
		EdgeLayoutTable table = (EdgeLayoutTable) container;
		CellFormatter cellFormatter = table.getCellFormatter();
		FlexCellFormatter flexFormatter = table.getFlexCellFormatter();

		int row = 0;
		int col = 0;

		switch (vAlign) {
			case BEGIN:

				if (countWidgets(0, true) == 0) {
					table.insertRow(0);
				}

				break;

			case CENTER:

				if (countWidgets(0, true) > 0) {
					row++;
				}

				widget.setHeight("100%");
				cellFormatter.setHeight(row, col, "100%");

				break;

			case END:

				if (countWidgets(2, true) == 0) {
					table.insertRow(table.getRowCount());
				}

				row = table.getRowCount() - 1;

				break;

			default:
				assert false : "Unsupported vertical alignment: " + vAlign;
		}

		switch (hAlign) {
			case BEGIN:

				if (countWidgets(0, false) == 0) {
					insertColumn(table, 0);
				}

				break;

			case CENTER:

				if (countWidgets(0, false) > 0) {
					col++;
				}

				widget.setWidth("100%");
				cellFormatter.setWidth(row, col, "100%");

				break;

			case END:

				if (countWidgets(2, false) == 0) {
					insertColumn(table, table.getCellCount(0));
				}

				col = table.getCellCount(0) - 1;

				break;

			default:
				assert false : "Unsupported horizontal alignment: " + hAlign;
		}

		table.setWidget(row, col, widget);
		widgets[vAlign.ordinal()][hAlign.ordinal()] = widget;

		if (vAlign == Alignment.CENTER && hAlign == Alignment.CENTER) {
			table.setCenterWidget(widget);
		}

		setCellSpans(flexFormatter);
		setCellAlignment(styleData, cellFormatter, row, col);
		// TODO: check column spans
	}

	/**
	 * @see GenericLayout#clear(HasWidgets)
	 */
	@Override
	public void clear(HasWidgets container) {
		super.clear(container);

		((FlexTable) container).removeAllRows();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Panel createLayoutContainer(Container container,
		StyleData containerStyle) {
		EdgeLayoutTable table = new EdgeLayoutTable();

		table.setCellSpacing(gap);
		table.setCellPadding(0);
		table.setStylePrimaryName(EWT.CSS.ewtEdgeLayout());

		return table;
	}

	/**
	 * @see GenericLayout#removeWidget(HasWidgets, Widget)
	 */
	@Override
	public void removeWidget(HasWidgets container, Widget widget) {
		for (int row = 0; row < 3; row++) {
			for (int col = 0; col < 3; col++) {
				if (widgets[row][col] == widget) {
					widgets[row][col] = null;
				}
			}
		}

		setCellSpans(((FlexTable) container).getFlexCellFormatter());

		// TODO: implement deletion of rows and columns

		super.removeWidget(container, widget);
	}

	/**
	 * Counts the widgets in a certain row or column.
	 *
	 * @param index  The row or column index to count the widgets of
	 * @param forRow TRUE for a row, FALSE for a column
	 * @return The number of widgets in the column
	 */
	private int countWidgets(int index, boolean forRow) {
		int count = 0;

		for (int i = 0; i < 3; i++) {
			if ((forRow && widgets[index][i] != null) ||
				(!forRow && widgets[i][index] != null)) {
				count++;
			}
		}

		return count;
	}

	/**
	 * Returns the maximum widget count for all rows or columns.
	 *
	 * @param forRows TRUE to count rows, FALSE for columns
	 * @return The maximum widget count
	 */
	private int getMaxWidgetCount(boolean forRows) {
		int max = 0;

		for (int i = 0; i < 3; i++) {
			max = Math.max(max, countWidgets(i, forRows));
		}

		return max;
	}

	/**
	 * Inserts a column into a {@link FlexTable}.
	 *
	 * @param table        The table
	 * @param beforeColumn The column to insert before
	 */
	private void insertColumn(FlexTable table, int beforeColumn) {
		int count = table.getRowCount();

		for (int i = 0; i < count; i++) {
			table.insertCell(i, beforeColumn);
		}
	}

	/**
	 * Sets the table cell spans for asymmetric widget distributions.
	 *
	 * @param cellFormatter The table cell formatter
	 */
	private void setCellSpans(FlexCellFormatter cellFormatter) {
		int maxWidgets = getMaxWidgetCount(true);
		int center = countWidgets(0, false) > 0 ? 1 : 0;
		int index = 0;

		for (int i = 0; i < 3; i++) {
			int widgetCount = countWidgets(i, true);

			if (widgetCount < maxWidgets && widgets[i][1] != null) {
				cellFormatter.setColSpan(index, center, maxWidgets);
			}

			if (i > 0 || widgetCount > 0) {
				index++;
			}
		}
	}

	/**
	 * An inner class that implements the resize handling for the center
	 * widget.
	 *
	 * @author eso
	 */
	static class EdgeLayoutTable extends FlexTable
		implements RequiresResize, ProvidesResize {

		private RequiresResize centerWidget;

		/**
		 * @see RequiresResize#onResize()
		 */
		@Override
		public void onResize() {
			if (centerWidget != null) {
				centerWidget.onResize();
			}
		}

		/**
		 * Sets the center widget.
		 *
		 * @param widget The new center widget
		 */
		public void setCenterWidget(Widget widget) {
			if (widget instanceof RequiresResize) {
				centerWidget = (RequiresResize) widget;
			}
		}
	}
}
