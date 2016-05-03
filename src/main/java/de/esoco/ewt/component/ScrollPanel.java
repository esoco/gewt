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

import de.esoco.ewt.UserInterfaceContext;
import de.esoco.ewt.event.EventType;
import de.esoco.ewt.layout.GenericLayout;
import de.esoco.ewt.style.StyleData;
import de.esoco.ewt.style.StyleFlag;

import com.google.gwt.event.dom.client.HasScrollHandlers;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.user.client.ui.CustomScrollPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;


/********************************************************************
 * A scroll panel that allows to scroll it's content.
 *
 * @author eso
 */
public class ScrollPanel extends FixedLayoutPanel
{
	//~ Constructors -----------------------------------------------------------

	/***************************************
	 * Creates a new instance.
	 */
	public ScrollPanel()
	{
		super(new ScrollPanelLayout());
	}

	//~ Methods ----------------------------------------------------------------

	/***************************************
	 * Overridden to check for scrollbar styles.
	 *
	 * @see Component#applyStyle(StyleData)
	 */
	@Override
	public void applyStyle(StyleData rStyle)
	{
		com.google.gwt.user.client.ui.ScrollPanel rScrollPanel =
			(com.google.gwt.user.client.ui.ScrollPanel) getWidget();

		super.applyStyle(rStyle);

		if (rStyle.hasFlag(StyleFlag.SCROLLBAR_HORIZONTAL_ON) ||
			rStyle.hasFlag(StyleFlag.SCROLLBAR_VERTICAL_ON))
		{
			rScrollPanel.setAlwaysShowScrollBars(true);
		}
	}

	/***************************************
	 * @see Component#createEventDispatcher()
	 */
	@Override
	ComponentEventDispatcher createEventDispatcher()
	{
		return new ScrollEventDispatcher();
	}

	//~ Inner Classes ----------------------------------------------------------

	/********************************************************************
	 * The default layout for this panel.
	 *
	 * @author eso
	 */
	public static class ScrollPanelLayout extends GenericLayout
	{
		//~ Methods ------------------------------------------------------------

		/***************************************
		 * {@inheritDoc}
		 */
		@Override
		public HasWidgets createLayoutContainer(
			UserInterfaceContext rContext,
			StyleData			 rStyle)
		{
			return new CustomScrollPanel();
		}
	}

	/********************************************************************
	 * Dispatcher for scroll events.
	 *
	 * @author eso
	 */
	class ScrollEventDispatcher extends ComponentEventDispatcher
		implements ScrollHandler
	{
		//~ Methods ------------------------------------------------------------

		/***************************************
		 * @see ScrollHandler#onScroll(ScrollEvent)
		 */
		@Override
		public void onScroll(ScrollEvent rEvent)
		{
			notifyEventHandler(EventType.VALUE_CHANGED, rEvent);
		}

		/***************************************
		 * @see ComponentEventDispatcher#initEventDispatching(Widget)
		 */
		@Override
		void initEventDispatching(Widget rWidget)
		{
			super.initEventDispatching(rWidget);

			if (rWidget instanceof HasScrollHandlers)
			{
				((HasScrollHandlers) rWidget).addScrollHandler(this);
			}
		}
	}
}
