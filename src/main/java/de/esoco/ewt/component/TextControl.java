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

import de.esoco.ewt.UserInterfaceContext;
import de.esoco.ewt.event.EventType;
import de.esoco.ewt.impl.gwt.ValueBoxConstraint.RegExConstraint;
import de.esoco.ewt.style.StyleData;
import de.esoco.ewt.style.StyleFlag;

import de.esoco.lib.property.TextAttribute;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.HasDoubleClickHandlers;
import com.google.gwt.event.dom.client.HasKeyDownHandlers;
import com.google.gwt.event.dom.client.HasKeyPressHandlers;
import com.google.gwt.event.dom.client.HasKeyUpHandlers;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

/**
 * A base class for components that handle text editing.
 *
 * <p>Supported style flags:</p>
 *
 * <ul>
 *   <li>{@link StyleFlag#RESOURCE}: if text should be treated as a resource and
 *     expanded with {@link UserInterfaceContext#expandResource(String)}.</li>
 * </ul>
 *
 * <p>Supported event types:</p>
 *
 * <ul>
 *   <li>{@link EventType#ACTION ACTION}: when the text input is
 *     finished/confirmed through the keyboard.</li>
 *   <li>{@link EventType#VALUE_CHANGED VALUE_CHANGED}: when the contents of the
 *     text component is edited.</li>
 * </ul>
 *
 * @author eso
 */
public abstract class TextControl extends Control implements TextAttribute {

	private HandlerRegistration constraintHandler = null;

	private boolean resourceValue = false;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void applyStyle(StyleData style) {
		super.applyStyle(style);

		resourceValue = style.hasFlag(StyleFlag.RESOURCE);
	}

	/**
	 * Returns the position of the input caret.
	 *
	 * @return The caret position
	 */
	public int getCaretPosition() {
		return getTextBox().getCursorPos();
	}

	/**
	 * Returns the text of this component.
	 *
	 * @return The text
	 */
	@Override
	public String getText() {
		return getTextBox().getText();
	}

	/**
	 * Returns the editable state of this component.
	 *
	 * @return TRUE if the component allows editing, FALSE if it is readonly
	 */
	public boolean isEditable() {
		return !getTextBox().isReadOnly();
	}

	/**
	 * Sets the position of the input caret.
	 *
	 * @param position The new caret position
	 */
	public void setCaretPosition(int position) {
		getTextBox().setCursorPos(position);
	}

	/**
	 * Allows to set a minimum width by providing the corresponding text column
	 * count. This may not be supported by all implementations.
	 *
	 * @param columns The minimum number of text columns to display
	 */
	public abstract void setColumns(int columns);

	/**
	 * Sets the editable state of this component.
	 *
	 * @param editable TRUE if the object shall be editable, FALSE to set it to
	 *                 be readonly
	 */
	public void setEditable(boolean editable) {
		getTextBox().setReadOnly(!editable);
	}

	/**
	 * Sets a regular expression as an input constraint. Any input that doesn't
	 * match the expression will be rejected.
	 *
	 * @param constraint The input constraint or NULL for none
	 */
	public void setInputConstraint(String constraint) {
		if (constraintHandler != null) {
			constraintHandler.removeHandler();
			constraintHandler = null;
		}

		if (constraint != null) {
			constraintHandler = getTextBox().addKeyPressHandler(
				new RegExConstraint(constraint));
		}
	}

	/**
	 * Sets the placeholder string that will be display in an empty text
	 * component.
	 *
	 * @param placeholder The placeholder string
	 */
	public void setPlaceholder(String placeholder) {
		getWidget()
			.getElement()
			.setPropertyString("placeholder",
				getContext().expandResource(placeholder));
	}

	/**
	 * Sets the text of this component.
	 *
	 * @param text The new text
	 */
	@Override
	public final void setText(String text) {
		if (resourceValue) {
			text = getContext().expandResource(text);
		}

		setWidgetText(text);
	}

	/**
	 * Returns the {@link IsTextControlWidget} implementation of this instance.
	 *
	 * @return The text box
	 */
	protected IsTextControlWidget getTextBox() {
		return (IsTextControlWidget) getWidget();
	}

	/**
	 * Implements setting the widget text and will be invoked from
	 * {@link #setText(String)}.
	 */
	protected void setWidgetText(String text) {
		getTextBox().setText(text);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	ComponentEventDispatcher createEventDispatcher() {
		return new TextEventDispatcher();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void setProperty(String property) {
		if (resourceValue) {
			super.setProperty(property);
		} else {
			setText(property);
		}
	}

	/**
	 * The interface that needs to be provided by all text control widgets
	 * implementations.
	 *
	 * @author eso
	 */
	public static interface IsTextControlWidget
		extends IsWidget, HasText, Focusable, HasEnabled, HasKeyPressHandlers,
		HasKeyDownHandlers, HasDoubleClickHandlers {

		/**
		 * Returns the position of the input caret.
		 *
		 * @return The caret position
		 */
		public int getCursorPos();

		/**
		 * Returns the currently selected text. The returned string will be
		 * empty if no selection exists.
		 *
		 * @return The selected text
		 */
		public String getSelectedText();

		/**
		 * Returns the editable state of this component.
		 *
		 * @return TRUE if the component is readonly
		 */
		public boolean isReadOnly();

		/**
		 * Sets the position of the input caret.
		 *
		 * @param position The new caret position
		 */
		public void setCursorPos(int position);

		/**
		 * Sets the editable state of this component.
		 *
		 * @param readOnly TRUE if the object shall be readonly
		 */
		public void setReadOnly(boolean readOnly);

		/**
		 * Sets the selection of the text. A length of zero will remove the
		 * selection.
		 *
		 * @param start  The start index of the selection
		 * @param length The length of the selection
		 * @throws IndexOutOfBoundsException If the given selection doesn't fit
		 *                                   the current text
		 */
		public void setSelectionRange(int start, int length);

		/**
		 * Sets the number of text columns to be displayed by this instance.
		 *
		 * @param columns The column count
		 */
		public void setVisibleLength(int columns);
	}

	/**
	 * Dispatcher for text-specific events.
	 *
	 * @author eso
	 */
	class TextEventDispatcher extends ComponentEventDispatcher
		implements ValueChangeHandler<Object> {

		/**
		 * Overridden to send {@link EventType#POINTER_CLICKED} instead of
		 * ACTION.
		 *
		 * @param event The click event
		 */
		@Override
		public void onClick(ClickEvent event) {
			notifyEventHandler(EventType.POINTER_CLICKED, event);
		}

		/**
		 * Overridden to do nothing.
		 *
		 * @param event The event
		 */
		@Override
		public void onDoubleClick(DoubleClickEvent event) {
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void onKeyUp(KeyUpEvent event) {
			if (hasHandlerFor(EventType.KEY_RELEASED)) {
				super.onKeyUp(event);
			}

			handleKeyUp(event);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void onValueChange(ValueChangeEvent<Object> event) {
			notifyEventHandler(EventType.VALUE_CHANGED);
		}

		/**
		 * Handles a key up event for the text component.
		 *
		 * @param event The key up event
		 */
		protected void handleKeyUp(KeyUpEvent event) {
			if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
				notifyEventHandler(EventType.ACTION, event);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		@SuppressWarnings("unchecked")
		protected HandlerRegistration initEventDispatching(Widget widget,
			EventType eventType) {
			if (eventType == EventType.ACTION &&
				widget instanceof HasKeyUpHandlers) {
				return ((HasKeyUpHandlers) widget).addKeyUpHandler(this);
			} else if (eventType == EventType.VALUE_CHANGED &&
				widget instanceof HasValueChangeHandlers) {
				// register generic event handler to support subclasses like
				// DateField
				return ((HasValueChangeHandlers<Object>) widget).addValueChangeHandler(
					this);
			} else {
				return super.initEventDispatching(widget, eventType);
			}
		}
	}
}
