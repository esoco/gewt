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

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasWidgets;

import static de.esoco.lib.property.LayoutProperties.VERTICAL_ALIGN;


/********************************************************************
 * The default layout factory implementation for GEWT.
 *
 * @author eso
 */
public class DefaultLayoutFactory implements LayoutFactory
{
	//~ Static fields/initializers ---------------------------------------------

	private static String sGridRowStyleName    = "row";
	private static String sGridColumnStyleName = "col";

	//~ Static methods ---------------------------------------------------------

	/***************************************
	 * Allows to set the style names for the layouts {@link LayoutType#GRID_ROW}
	 * and {@link LayoutType#GRID_COLUMN}.
	 *
	 * @param sRowStyleName    The new grid row style name
	 * @param sColumnStyleName The new grid column style name
	 */
	public static void setGridStyleNames(
		String sRowStyleName,
		String sColumnStyleName)
	{
		sGridRowStyleName    = sRowStyleName;
		sGridColumnStyleName = sColumnStyleName;
	}

	//~ Methods ----------------------------------------------------------------

	/***************************************
	 * {@inheritDoc}
	 */
	@Override
	public GenericLayout createLayout(Container  rParentContainer,
									  StyleData  rContainerStyle,
									  LayoutType eLayout)
	{
		GenericLayout aLayout;

		switch (eLayout)
		{
			case SPLIT:
				aLayout = new SplitPanelLayout();
				break;

			case DECK:

				Alignment eVAlign =
					rContainerStyle.getProperty(VERTICAL_ALIGN, Alignment.FILL);

				if (eVAlign == Alignment.FILL)
				{
					aLayout = new DeckLayoutPanelLayout();
				}
				else
				{
					aLayout = new DeckPanelLayout();
				}

				break;

			case STACK:
				aLayout = new StackPanelLayout();
				break;

			case TABS:
				aLayout = new TabPanelLayout();
				break;

			case FILL:
				aLayout = new FillLayout();
				break;

			case GRID_ROW:
				aLayout = new FlowLayout(sGridRowStyleName);
				break;

			case GRID_COLUMN:
				aLayout = new FlowLayout(sGridColumnStyleName);
				break;

			case FLOW:
			case GRID:
			case CARD:
			case LIST_ITEM:
				aLayout = new FlowLayout();
				break;

			case FLEX:
				aLayout = new CssStyleLayout(EWT.CSS.ewtFlexboxPanel());
				break;

			case CSS_GRID:
				aLayout = new CssStyleLayout(EWT.CSS.ewtCssGridPanel());
				break;

			case LIST:
				aLayout = new ListLayout();
				break;

			case FORM:
				aLayout = new FormLayout();
				break;

			case GROUP:
				aLayout = new GroupLayout();
				break;

			case MENU:
				aLayout = new MenuLayout();
				break;

			case HEADER:
			case CONTENT:
			case FOOTER:
				aLayout = new ContentLayout(eLayout);
				break;

			default:
				throw new IllegalStateException("Unsupported Layout " +
												eLayout);
		}

		return aLayout;
	}

	//~ Inner Classes ----------------------------------------------------------

	/********************************************************************
	 * A generic layout implementation that creates a simple panel that is
	 * controlled by CSS styles (like CSS grid or flexbox).
	 *
	 * <p>If the panel needs a specific value of it's 'display' property that
	 * must be set in the CSS for the given style name. This is because the GWT
	 * setVisible() implementation toggles 'display' between 'none' and it's CSS
	 * default; setting it programmatically therefore wouldn't have an effect
	 * because it would be overridden by invocations of setVisible().</p>
	 *
	 * @author eso
	 */
	static class CssStyleLayout extends GenericLayout
	{
		//~ Instance fields ----------------------------------------------------

		private String sCssStyleName;

		//~ Constructors -------------------------------------------------------

		/***************************************
		 * Creates a new instance.
		 *
		 * @param sStyleName The CSS style name of this instance
		 */
		public CssStyleLayout(String sStyleName)
		{
			sCssStyleName = sStyleName;
		}

		//~ Methods ------------------------------------------------------------

		/***************************************
		 * {@inheritDoc}
		 */
		@Override
		public HasWidgets createLayoutContainer(
			Container rContainer,
			StyleData rStyle)
		{
			return new FlowPanel()
			{
				@Override
				protected void onAttach()
				{
					super.onAttach();

					addStyleName(sCssStyleName);
				}
			};
		}
	}
}
