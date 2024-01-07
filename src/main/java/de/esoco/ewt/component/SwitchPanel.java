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

import de.esoco.ewt.layout.GenericLayout;
import de.esoco.ewt.style.StyleData;

import de.esoco.lib.property.SingleSelection;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;

/**
 * A base class for panels that switch between pages of components in some way
 * like tabs or stacks.
 *
 * @author eso
 */
public abstract class SwitchPanel extends FixedLayoutPanel
	implements SingleSelection {

	/**
	 * Creates a new instance.
	 *
	 * @param layout The layout for this instance
	 * @throws IllegalArgumentException If the layout isn't an instance of
	 *                                  {@link SwitchPanelLayout}
	 */
	public SwitchPanel(GenericLayout layout) {
		super(layout);

		if (!(layout instanceof SwitchPanelLayout)) {
			throw new IllegalArgumentException(
				getClass().getSimpleName() + " Layout must be a subclass of " +
					SwitchPanelLayout.class.getSimpleName());
		}
	}

	/**
	 * Adds a new page to this panel.
	 *
	 * @param component The component to be placed in a page
	 * @param pageTitle The title of the page
	 * @param closeable TRUE if the page can be closed by the user
	 */
	public void addPage(Component component, String pageTitle,
		boolean closeable) {
		getLayout().addPage(component, pageTitle, closeable);
	}

	/**
	 * Overridden to return the layout after a cast to
	 * {@link SwitchPanelLayout}.
	 *
	 * @see FixedLayoutPanel#getLayout()
	 */
	@Override
	public SwitchPanelLayout getLayout() {
		return (SwitchPanelLayout) super.getLayout();
	}

	/**
	 * Returns the number of pages in this instance.
	 *
	 * @return The page count
	 */
	public int getPageCount() {
		return getLayout().getPageCount();
	}

	/**
	 * Returns the index of the page in which a certain component is displayed.
	 *
	 * @param pageComponent The component to return the index of
	 * @return The page index or -1 if the given component is not a page
	 */
	public int getPageIndex(Component pageComponent) {
		return getLayout().getPageIndex(pageComponent);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getSelectionIndex() {
		return getLayout().getSelectionIndex();
	}

	/**
	 * Sets the title of a particular page.
	 *
	 * @param index The page index
	 * @param title The new title
	 */
	public void setPageTitle(int index, String title) {
		getLayout().setPageTitle(index, title);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setSelection(int index) {
		getLayout().setSelection(index);
	}

	/**
	 * Overridden to do nothing. Page components must be added by invoking the
	 * method {@link #addPage(Component, String, boolean)}.
	 *
	 * @see Panel#addWidget(HasWidgets, Widget, StyleData)
	 */
	@Override
	void addWidget(HasWidgets container, Widget widget, StyleData styleData) {
	}

	/**
	 * A base class for the default layouts of subclasses.
	 *
	 * @author eso
	 */
	public static abstract class SwitchPanelLayout extends GenericLayout
		implements SingleSelection {

		/**
		 * Adds a new page component to this panel.
		 *
		 * @param pageComponent The component to be placed in a page
		 * @param pageTitle     The title of the page
		 * @param closeable     TRUE if the page can be closed by the user
		 */
		public abstract void addPage(Component pageComponent, String pageTitle,
			boolean closeable);

		/**
		 * Returns the number of pages contained in this instance.
		 *
		 * @return The page count
		 */
		public abstract int getPageCount();

		/**
		 * Returns the index of the page in which a certain component is
		 * displayed.
		 *
		 * @param pageComponent The component
		 * @return The page index or -1 if the given component is not in a page
		 */
		public abstract int getPageIndex(Component pageComponent);

		/**
		 * Sets the title of a particular page.
		 *
		 * @param index The page index
		 * @param title The new title
		 */
		public abstract void setPageTitle(int index, String title);
	}
}
