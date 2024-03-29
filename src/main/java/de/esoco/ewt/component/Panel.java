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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Widget;

/**
 * A panel that can contain arbitrary child widgets.
 *
 * @author eso
 */
public class Panel extends Container {

	/**
	 * Overridden to sink GWT events so that events will be created.
	 *
	 * @see Container#createEventDispatcher()
	 */
	@Override
	ComponentEventDispatcher createEventDispatcher() {
		return new PanelEventDispatcher();
	}

	/**
	 * A panel event dispatcher that implements action events through DOM
	 * events
	 * if not supported natively by a panel widget.
	 *
	 * @author eso
	 */
	class PanelEventDispatcher extends ComponentEventDispatcher {

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected HandlerRegistration initEventDispatching(Widget widget,
			EventType eventType) {
			HandlerRegistration handler;

			if (eventType == EventType.ACTION &&
				!(widget instanceof HasClickHandlers)) {
				// enable click events for panel widgets without native support
				handler = widget.addDomHandler(this, ClickEvent.getType());
			} else {
				handler = super.initEventDispatching(widget, eventType);
			}

			return handler;
		}
	}
}
