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
import de.esoco.ewt.impl.gwt.WidgetFactory;
import de.esoco.ewt.impl.gwt.code.GwtCodeMirror;
import de.esoco.ewt.style.StyleData;
import de.esoco.ewt.style.StyleFlag;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;

import static de.esoco.lib.property.ContentProperties.MIME_TYPE;

/**
 * A multiline text input component.
 *
 * @author eso
 */
public class TextArea extends TextControl {

	/**
	 * Returns the position of the input caret.
	 *
	 * @return The caret position
	 */
	@Override
	public int getCaretPosition() {
		return getTextArea().getCursorPos();
	}

	/**
	 * Returns the text of this component.
	 *
	 * @return The text
	 */
	@Override
	public String getText() {
		return getTextArea().getText();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initWidget(Container parent, StyleData style) {
		super.initWidget(parent, style);

		if (style.hasFlag(StyleFlag.WRAP)) {
			setLineWrapping(true);
		} else if (style.hasFlag(StyleFlag.NO_WRAP)) {
			setLineWrapping(false);
		}
	}

	/**
	 * Returns the editable state of this component.
	 *
	 * @return TRUE if the component allows editing, FALSE if it is readonly
	 */
	@Override
	public boolean isEditable() {
		return !getTextArea().isReadOnly();
	}

	/**
	 * Checks whether text lines are wrapped automatically by this component.
	 *
	 * @return The line wrapping state
	 */
	public boolean isLineWrapping() {
		String wrapAttr = getWidget().getElement().getAttribute("wrap");

		// return TRUE if on or not set
		return !"off".equals(wrapAttr);
	}

	/**
	 * Sets the position of the input caret.
	 *
	 * @param position The new caret position
	 */
	@Override
	public void setCaretPosition(int position) {
		getTextArea().setCursorPos(position);
	}

	/**
	 * @see TextControl#setColumns(int)
	 */
	@Override
	public void setColumns(int columns) {
		getTextArea().setCharacterWidth(columns);
	}

	/**
	 * Sets the editable state of this component.
	 *
	 * @param editable TRUE if the object shall be editable, FALSE to set it to
	 *                 be readonly
	 */
	@Override
	public void setEditable(boolean editable) {
		getTextArea().setReadOnly(!editable);
	}

	/**
	 * Sets whether text lines should be wrapped automatically by this
	 * component.
	 *
	 * @param wrap TRUE to enabled automatic wrapping
	 */
	public void setLineWrapping(boolean wrap) {
		getWidget().getElement().setAttribute("wrap", wrap ? "on" : "off");
	}

	/**
	 * Allows to set a minimum height by providing the corresponding text row
	 * count. This may not be supported by all implementations.
	 *
	 * @param rows The minimum number of text rows to display
	 */
	public void setRows(int rows) {
		getTextArea().setVisibleLines(rows);
		getElement().getStyle().setProperty("minHeight", rows + "em");
	}

	/**
	 * Implements setting the widget text and will be invoked from
	 * {@link #setText(String)}.
	 */
	@Override
	protected void setWidgetText(String text) {
		getTextArea().setText(text);
	}

	/**
	 * @see Component#createEventDispatcher()
	 */
	@Override
	ComponentEventDispatcher createEventDispatcher() {
		return new TextAreaEventDispatcher();
	}

	/**
	 * Returns the widget of this component as a {@link IsTextArea} interface.
	 *
	 * @return The text area widget
	 */
	IsTextArea getTextArea() {
		return (IsTextArea) getWidget();
	}

	/**
	 * An interface that describes the properties of text areas.
	 *
	 * @author eso
	 */
	public static interface IsTextArea extends IsTextControlWidget {

		/**
		 * Returns the text of this component.
		 *
		 * @return The text
		 */
		@Override
		public String getText();

		/**
		 * @see TextControl#setColumns(int)
		 */
		public void setCharacterWidth(int columns);

		/**
		 * Sets the text of this component.
		 *
		 * @param text The new text
		 */
		@Override
		public void setText(String text);

		/**
		 * Sets the row count.
		 *
		 * @param rows The minimum number of text rows to display
		 */
		public void setVisibleLines(int rows);
	}

	/**
	 * Widget factory for this component.
	 *
	 * @author eso
	 */
	public static class TextAreaWidgetFactory<W extends IsTextArea>
		implements WidgetFactory<W> {

		/**
		 * {@inheritDoc}
		 */
		@Override
		@SuppressWarnings("unchecked")
		public W createWidget(Component component, StyleData style) {
			String mimeType = style.getProperty(MIME_TYPE, null);
			IsTextArea widget = null;

			if (mimeType != null && !mimeType.equalsIgnoreCase("text/plain")) {
				String mode = null;
				int index = mimeType.indexOf("x-");

				if (index >= 0) {
					mode = mimeType.substring(index + 2);
				} else if (mimeType.toLowerCase().startsWith("text/")) {
					mode = mimeType.substring(5);
				}

				if (mode != null) {
					widget = new GwtCodeMirror(mode);
				}
			}

			if (widget == null) {
				widget = new GwtTextArea();
			}

			return (W) widget;
		}
	}

	/**
	 * A text area subclass that propagates the on paste event.
	 *
	 * @author eso
	 */
	static class GwtTextArea extends com.google.gwt.user.client.ui.TextArea
		implements IsTextArea {

		/**
		 * Creates a new instance.
		 */
		GwtTextArea() {
			sinkEvents(Event.ONPASTE);
		}

		/**
		 * Overridden to fire a value change event if text is pasted in to this
		 * text area.
		 *
		 * @see com.google.gwt.user.client.ui.TextArea#onBrowserEvent(Event)
		 */
		@Override
		public void onBrowserEvent(Event event) {
			super.onBrowserEvent(event);

			switch (DOM.eventGetType(event)) {
				case Event.ONPASTE:
					Scheduler.get().scheduleDeferred(new ScheduledCommand() {
						@Override
						public void execute() {
							ValueChangeEvent.fire(GwtTextArea.this, getText());
						}
					});

					break;
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void setVisibleLength(int columns) {
			// ignored for TextArea
		}
	}

	/**
	 * Dispatcher for text-specific events.
	 *
	 * @author eso
	 */
	class TextAreaEventDispatcher extends TextEventDispatcher {

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void handleKeyUp(KeyUpEvent event) {
			if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
				if (event.isControlKeyDown()) {
					notifyEventHandler(EventType.ACTION, event);
				}
			} else {
				super.handleKeyUp(event);
			}
		}
	}
}
