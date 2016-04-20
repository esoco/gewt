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
package de.esoco.ewt.layout;

import de.esoco.ewt.UserInterfaceContext;
import de.esoco.ewt.style.StyleData;

import com.google.gwt.dom.client.Document;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.InsertPanel.ForIsWidget;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;


/********************************************************************
 * A panel layout that contains a menu or navigation structure.
 *
 * @author eso
 */
public class MenuLayout extends GenericLayout
{
	//~ Constructors -----------------------------------------------------------

	/***************************************
	 * Creates a new instance.
	 */
	public MenuLayout()
	{
	}

	//~ Methods ----------------------------------------------------------------

	/***************************************
	 * {@inheritDoc}
	 */
	@Override
	public HasWidgets createLayoutContainer(
		UserInterfaceContext rContext,
		StyleData			 rContainerStyle)
	{
		return new MenuPanel();
	}

	//~ Inner Classes ----------------------------------------------------------

	/********************************************************************
	 * A GWT panel implementation that is based on the HTML NAV element.
	 *
	 * @author eso
	 */
	public class MenuPanel extends ComplexPanel implements ForIsWidget
	{
		//~ Constructors -------------------------------------------------------

		/***************************************
		 * Creates a new instance.
		 */
		public MenuPanel()
		{
			setElement(Document.get().createElement("nav"));
		}

		//~ Methods ------------------------------------------------------------

		/***************************************
		 * Adds a new child widget to the panel.
		 *
		 * @param w the widget to be added
		 */
		@Override
		@SuppressWarnings("deprecation")
		public void add(Widget w)
		{
			add(w, getElement());
		}

		/***************************************
		 * {@inheritDoc}
		 */
		@Override
		public void clear()
		{
			super.clear();
			getElement().removeAllChildren();
		}

		/***************************************
		 * {@inheritDoc}
		 */
		@Override
		public void insert(IsWidget w, int beforeIndex)
		{
			insert(asWidgetOrNull(w), beforeIndex);
		}

		/***************************************
		 * {@inheritDoc}
		 */
		@Override
		@SuppressWarnings("deprecation")
		public void insert(Widget w, int beforeIndex)
		{
			insert(w, getElement(), beforeIndex, true);
		}
	}
}
