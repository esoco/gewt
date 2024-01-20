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

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import de.esoco.ewt.EWT;
import de.esoco.ewt.component.Container;
import de.esoco.ewt.component.DeckPanel.DeckLayoutPanelLayout;
import de.esoco.ewt.component.DeckPanel.DeckPanelLayout;
import de.esoco.ewt.component.SplitPanel.SplitPanelLayout;
import de.esoco.ewt.component.StackPanel.StackPanelLayout;
import de.esoco.ewt.component.TabPanel.TabPanelLayout;
import de.esoco.ewt.style.StyleData;
import de.esoco.lib.property.Alignment;
import de.esoco.lib.property.LayoutType;

import static de.esoco.lib.property.LayoutProperties.VERTICAL_ALIGN;

/**
 * The default layout factory implementation for GEWT.
 *
 * @author eso
 */
public class DefaultLayoutFactory implements LayoutFactory {

	private static String gridRowStyleName = "row";

	private static String gridColumnStyleName = "col";

	/**
	 * Allows to set the style names for the layouts
	 * {@link LayoutType#GRID_ROW}
	 * and {@link LayoutType#GRID_COLUMN}.
	 *
	 * @param rowStyleName    The new grid row style name
	 * @param columnStyleName The new grid column style name
	 */
	public static void setGridStyleNames(String rowStyleName,
		String columnStyleName) {
		gridRowStyleName = rowStyleName;
		gridColumnStyleName = columnStyleName;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GenericLayout createLayout(Container parentContainer,
		StyleData containerStyle, LayoutType layoutType) {
		GenericLayout layout;

		switch (layoutType) {
			case SPLIT:
				layout = new SplitPanelLayout();
				break;

			case DECK:

				Alignment align =
					containerStyle.getProperty(VERTICAL_ALIGN, Alignment.FILL);

				if (align == Alignment.FILL) {
					layout = new DeckLayoutPanelLayout();
				} else {
					layout = new DeckPanelLayout();
				}

				break;

			case STACK:
				layout = new StackPanelLayout();
				break;

			case TABS:
				layout = new TabPanelLayout();
				break;

			case FILL:
				layout = new FillLayout();
				break;

			case GRID_ROW:
				layout = new FlowLayout(gridRowStyleName);
				break;

			case GRID_COLUMN:
				layout = new FlowLayout(gridColumnStyleName);
				break;

			case FLOW:
			case GRID:
			case CARD:
			case LIST_ITEM:
				layout = new FlowLayout();
				break;

			case FLEX:
				layout = new CssStyleLayout(EWT.CSS.ewtFlexLayout());
				break;

			case CSS_GRID:
				layout = new CssStyleLayout(EWT.CSS.ewtGridLayout());
				break;

			case LIST:
				layout = new ListLayout();
				break;

			case FORM:
				layout = new FormLayout();
				break;

			case GROUP:
				layout = new GroupLayout();
				break;

			case MENU:
				layout = new MenuLayout();
				break;

			case HEADER:
			case CONTENT:
			case FOOTER:
				layout = new ContentLayout(layoutType);
				break;

			default:
				throw new IllegalStateException(
					"Unsupported Layout " + layoutType);
		}

		return layout;
	}

	/**
	 * A generic layout implementation that creates a simple panel that is
	 * controlled by CSS styles (like CSS grid or flexbox).
	 *
	 * <p>If the panel needs a specific value of it's 'display' property that
	 * must be set in the CSS for the given style name. This is because the GWT
	 * setVisible() implementation toggles 'display' between 'none' and it's
	 * CSS
	 * default; setting it programmatically therefore wouldn't have an effect
	 * because it would be overridden by invocations of setVisible().</p>
	 *
	 * @author eso
	 */
	static class CssStyleLayout extends GenericLayout {

		private final String cssStyleName;

		/**
		 * Creates a new instance.
		 *
		 * @param styleName The CSS style name of this instance
		 */
		public CssStyleLayout(String styleName) {
			cssStyleName = styleName;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public HasWidgets createLayoutContainer(Container container,
			StyleData style) {
			return new FlowPanel() {
				@Override
				protected void onAttach() {
					super.onAttach();

					addStyleName(cssStyleName);
				}
			};
		}
	}
}
