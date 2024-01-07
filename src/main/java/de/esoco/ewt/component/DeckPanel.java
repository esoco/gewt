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
package de.esoco.ewt.component;

import com.google.gwt.user.client.ui.DeckLayoutPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IndexedPanel;
import com.google.gwt.user.client.ui.Widget;
import de.esoco.ewt.EWT;
import de.esoco.ewt.style.StyleData;
import de.esoco.lib.property.LayoutType;

/**
 * A panel that can contain multiple components of which only one is visible at
 * a time like the cards in a deck of cards. The currently visible component can
 * be set with the method {@link #setSelection(int)}. As the layout of a deck
 * panel is defined by it's implementation setting a layout on an instance has
 * no effect.
 *
 * @author eso
 */
public class DeckPanel extends SwitchPanel {

	/**
	 * Creates a new instance.
	 *
	 * @param parent The parent container
	 * @param style  The panel style
	 */
	public DeckPanel(Container parent, StyleData style) {
		super(EWT
			.getLayoutFactory()
			.createLayout(parent, style, LayoutType.DECK));
	}

	/**
	 * Overridden to reset the base class version to the original
	 * implementation
	 * to support simple addition of components to deck panels which ignore the
	 * additional parameters of {@link #addPage(Component, String, boolean)}
	 * anyway.
	 *
	 * @see Panel#addWidget(HasWidgets, Widget, StyleData)
	 */
	@Override
	void addWidget(HasWidgets container, Widget widget, StyleData styleData) {
		getLayout().addWidget(getContainerWidget(), widget, styleData, -1);
	}

	/**
	 * The base class for GWT deck panel layouts.
	 *
	 * @author eso
	 */
	public static abstract class AbstractDeckPanelLayout<T extends IndexedPanel & HasWidgets>
		extends SwitchPanelLayout {

		private T deckPanel;

		@Override
		public void addPage(Component groupComponent, String groupTitle,
			boolean closeable) {
			deckPanel.add(groupComponent.getWidget());
		}

		@Override
		public HasWidgets createLayoutContainer(Container container,
			StyleData style) {
			deckPanel = createDeckPanel(container, style);

			return deckPanel;
		}

		@Override
		public int getPageCount() {
			return deckPanel.getWidgetCount();
		}

		@Override
		public int getPageIndex(Component groupComponent) {
			return deckPanel.getWidgetIndex(groupComponent.getWidget());
		}

		@Override
		public void setPageTitle(int index, String title) {
			// ignored
		}

		/**
		 * Must be implemented to create the deck panel widget.
		 *
		 * @see #createLayoutContainer(Container, StyleData)
		 */
		abstract T createDeckPanel(Container container, StyleData style);

		/**
		 * Returns the deck panel widget of this instance.
		 *
		 * @return The deck panel widget
		 */
		final T getDeckPanel() {
			return deckPanel;
		}
	}

	/**
	 * A deck layout implementation that is based on {@link DeckLayoutPanel}.
	 *
	 * @author eso
	 */
	public static class DeckLayoutPanelLayout
		extends AbstractDeckPanelLayout<DeckLayoutPanel> {

		@Override
		public DeckLayoutPanel createDeckPanel(Container container,
			StyleData style) {
			return new DeckLayoutPanel();
		}

		@Override
		public int getSelectionIndex() {
			return getDeckPanel().getVisibleWidgetIndex();
		}

		@Override
		public void setSelection(int index) {
			getDeckPanel().showWidget(index);
		}
	}

	/**
	 * A deck layout implementation that is based on
	 * {@link com.google.gwt.user.client.ui.DeckPanel}.
	 *
	 * @author eso
	 */
	public static class DeckPanelLayout extends
		AbstractDeckPanelLayout<com.google.gwt.user.client.ui.DeckPanel> {

		@Override
		public com.google.gwt.user.client.ui.DeckPanel createDeckPanel(
			Container container, StyleData style) {
			return new com.google.gwt.user.client.ui.DeckPanel();
		}

		@Override
		public int getSelectionIndex() {
			return getDeckPanel().getVisibleWidget();
		}

		@Override
		public void setSelection(int index) {
			getDeckPanel().showWidget(index);
		}
	}
}
