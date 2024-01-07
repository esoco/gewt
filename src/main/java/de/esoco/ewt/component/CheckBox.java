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
import de.esoco.ewt.style.StyleData;

import de.esoco.lib.property.StateProperties;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HasHTML;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Widget;

/**
 * A button subclass that implements a selectable checkbox.
 *
 * @author eso
 */
public class CheckBox extends SelectableButton {

	/**
	 * @see SelectableButton#isSelected()
	 */
	@Override
	@SuppressWarnings("unchecked")
	public boolean isSelected() {
		return ((HasValue<Boolean>) getWidget()).getValue().booleanValue();
	}

	/**
	 * Sets the selected state of this button (if supported by the underlying
	 * GWT widget).
	 *
	 * @param selected The new selected state
	 */
	@Override
	@SuppressWarnings({ "boxing", "unchecked" })
	public void setSelected(boolean selected) {
		((HasValue<Boolean>) getWidget()).setValue(selected);
	}

	/**
	 * @see Component#createEventDispatcher()
	 */
	@Override
	ComponentEventDispatcher createEventDispatcher() {
		return new CheckBoxEventDispatcher();
	}

	/**
	 * Widget factory for this component.
	 *
	 * @author eso
	 */
	public static class CheckBoxWidgetFactory<W extends Widget & Focusable & HasHTML & HasValue<Boolean>>
		extends ButtonWidgetFactory<W> {

		/**
		 * {@inheritDoc}
		 */
		@Override
		@SuppressWarnings("unchecked")
		public W createWidget(Component component, StyleData style) {
			return (W) new com.google.gwt.user.client.ui.CheckBox();
		}
	}

	/**
	 * Dispatcher for list-specific events.
	 *
	 * @author eso
	 */
	class CheckBoxEventDispatcher extends ComponentEventDispatcher {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void onClick(ClickEvent event) {
			StyleData style = getStyle();

			if (style != null &&
				style.hasFlag(StateProperties.NO_EVENT_PROPAGATION)) {
				event.stopPropagation();
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void onValueChange(ValueChangeEvent<Object> event) {
			notifyEventHandler(EventType.ACTION);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		@SuppressWarnings("unchecked")
		protected HandlerRegistration initEventDispatching(Widget widget,
			EventType eventType) {
			if (eventType == EventType.ACTION &&
				widget instanceof HasValueChangeHandlers<?>) {
				if (widget instanceof HasClickHandlers) {
					((HasClickHandlers) widget).addClickHandler(this);
				}

				return ((HasValueChangeHandlers<Object>) widget).addValueChangeHandler(
					this);
			} else {
				return super.initEventDispatching(widget, eventType);
			}
		}
	}
}
