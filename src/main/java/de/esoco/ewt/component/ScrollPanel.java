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
package de.esoco.ewt.component;

import de.esoco.ewt.event.EventType;
import de.esoco.ewt.layout.GenericLayout;
import de.esoco.ewt.style.StyleData;
import de.esoco.ewt.style.StyleFlag;

import com.google.gwt.event.dom.client.HasScrollHandlers;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.CustomScrollPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;

/**
 * A scroll panel that allows to scroll it's content.
 *
 * @author eso
 */
public class ScrollPanel extends FixedLayoutPanel {

	/**
	 * Creates a new instance.
	 */
	public ScrollPanel() {
		super(new ScrollPanelLayout());
	}

	/**
	 * Overridden to check for scrollbar styles.
	 *
	 * @see Component#applyStyle(StyleData)
	 */
	@Override
	public void applyStyle(StyleData style) {
		com.google.gwt.user.client.ui.ScrollPanel scrollPanel =
			(com.google.gwt.user.client.ui.ScrollPanel) getWidget();

		super.applyStyle(style);

		if (style.hasFlag(StyleFlag.SCROLLBAR_HORIZONTAL_ON) ||
			style.hasFlag(StyleFlag.SCROLLBAR_VERTICAL_ON)) {
			scrollPanel.setAlwaysShowScrollBars(true);
		}
	}

	/**
	 * @see Component#createEventDispatcher()
	 */
	@Override
	ComponentEventDispatcher createEventDispatcher() {
		return new ScrollEventDispatcher();
	}

	/**
	 * The default layout for this panel.
	 *
	 * @author eso
	 */
	public static class ScrollPanelLayout extends GenericLayout {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public HasWidgets createLayoutContainer(Container container,
			StyleData style) {
			return new CustomScrollPanel();
		}
	}

	/**
	 * Dispatcher for scroll events.
	 *
	 * @author eso
	 */
	class ScrollEventDispatcher extends ComponentEventDispatcher
		implements ScrollHandler {

		/**
		 * @see ScrollHandler#onScroll(ScrollEvent)
		 */
		@Override
		public void onScroll(ScrollEvent event) {
			notifyEventHandler(EventType.VALUE_CHANGED, event);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected HandlerRegistration initEventDispatching(Widget widget,
			EventType eventType) {
			if (eventType == EventType.VALUE_CHANGED &&
				widget instanceof HasScrollHandlers) {
				return ((HasScrollHandlers) widget).addScrollHandler(this);
			} else {
				return super.initEventDispatching(widget, eventType);
			}
		}
	}
}
