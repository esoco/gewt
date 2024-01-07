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
package de.esoco.ewt.event;

import com.google.gwt.dom.client.NativeEvent;

/**
 * Instances of this class contain EWT event information. There is no
 * hierarchical event tree in EWT, all event information will be propagated in
 * EWTEvent objects. Which fields of an event actually contain information
 * depends on the type of event that occurred (e.g. pointer coordinates will
 * only be available from pointer-related events).
 *
 * @author eso
 */
public final class EwtEvent {

	private final Object source;

	private final Object element;

	private final EventType eventType;

	private final NativeEvent nativeEvent;

	private boolean canceled;

	/**
	 * Internal constructor, instance creation is controlled by the factory
	 * method {@link #getEvent(Object, Object, EventType, NativeEvent)}.
	 *
	 * @param source      The source of the event
	 * @param element     The element affected by this event or NULL for none
	 * @param type        The event type
	 * @param nativeEvent The native event that occurred
	 */
	private EwtEvent(Object source, Object element, EventType type,
		NativeEvent nativeEvent) {
		this.source = source;
		this.element = element;
		this.eventType = type;
		this.nativeEvent = nativeEvent;
	}

	/**
	 * Factory method that returns a new event instance containing the given
	 * parameters.
	 *
	 * @param source      The source of the event
	 * @param element     The element affected by this event or NULL for none
	 * @param type        The event type
	 * @param nativeEvent The native event that occurred
	 * @return An event instance containing the given parameters
	 */
	public static EwtEvent getEvent(Object source, Object element,
		EventType type, NativeEvent nativeEvent) {
		return new EwtEvent(source, element, type, nativeEvent);
	}

	/**
	 * Maps a GWT key code to the corresponding {@link KeyCode} instance.
	 *
	 * @param event The native GWT event to map the key code of
	 * @return The corresponding EWT key code instance
	 */
	public static KeyCode mapGwtKeyCode(NativeEvent event) {
		char c = (char) event.getCharCode();
		KeyCode keyCode = null;

		if (c != 0) {
			keyCode = KeyCode.forChar(c);
		}

		if (keyCode == null) {
			int code = event.getKeyCode();

			keyCode = KeyCode.forCode(code);

			if (keyCode == null) {
				keyCode = KeyCode.forChar((char) code);
			}
		}

		return keyCode;
	}

	/**
	 * Maps a GWT modifier key combination to the corresponding instance of
	 * {@link ModifierKeys}.
	 *
	 * @param event The native GWT event to map the modifiers of
	 * @return The corresponding EWT key modifiers instance
	 */
	public static ModifierKeys mapGwtModifiers(NativeEvent event) {
		int bits = 0;

		if (event.getAltKey()) {
			bits |= ModifierKeys.ALT_BIT;
		}

		if (event.getCtrlKey()) {
			bits |= ModifierKeys.CTRL_BIT;
		}

		if (event.getMetaKey()) {
			bits |= ModifierKeys.META_BIT;
		}

		if (event.getShiftKey()) {
			bits |= ModifierKeys.SHIFT_BIT;
		}

		return ModifierKeys.valueOf(bits);
	}

	/**
	 * Cancels the event to prevent further processing. What exactly this call
	 * does and for which event types it has an effect depends on the
	 * underlying
	 * implementation. In general it can be assumed that it will work for input
	 * events like keyboard input and that it allows to prevent the input to go
	 * into the component that caused the event.
	 */
	public void cancel() {
		if (nativeEvent != null) {
			nativeEvent.preventDefault();
		}

		canceled = true;
	}

	/**
	 * Returns the element that is affected by this event. What exactly such an
	 * element is depends on the event source. Typically this will be an
	 * element
	 * of components that are composed of multiple elements, like lists or
	 * trees.
	 *
	 * @return The element affected by this event (NULL for none)
	 */
	public Object getElement() {
		return element;
	}

	/**
	 * Returns the typed character in case of keyboard events. For KEY_TYPED
	 * events this method will always return a valid character but for
	 * KEY_PRESSED and KEY_RELEASED events which can also occur for
	 * non-printable characters the result of this method will be zero.
	 *
	 * @return The typed character of the keyboard event or 0 (zero) if not
	 * available
	 */
	public char getKeyChar() {
		return nativeEvent != null ? (char) nativeEvent.getCharCode() : 0;
	}

	/**
	 * Returns the key code in case of KEY_PRESSED and KEY_RELEASED events. For
	 * KEY_TYPED events this method will always return KeyCode.NONE.
	 *
	 * @return The key code of the keyboard event or KeyCode.NONE if not
	 * available
	 */
	public KeyCode getKeyCode() {
		return nativeEvent != null ? mapGwtKeyCode(nativeEvent) : KeyCode.NONE;
	}

	/**
	 * Return the state of the modifier keys at event time.
	 *
	 * @return One of the enumerated instances of the {@link ModifierKeys}
	 * class
	 * describing the modifier key states
	 */
	public ModifierKeys getModifiers() {
		return nativeEvent != null ?
		       mapGwtModifiers(nativeEvent) :
		       ModifierKeys.NONE;
	}

	/**
	 * Returns the pointer button that caused the pointer event.
	 *
	 * @return The number of the pointer button pressed, starting at 1
	 */
	public int getPointerButton() {
		int button = 0;

		if (nativeEvent != null) {
			int nativeButtons = nativeEvent.getButton();

			if ((nativeButtons & NativeEvent.BUTTON_LEFT) != 0) {
				button = 1;
			} else if ((nativeButtons & NativeEvent.BUTTON_RIGHT) != 0) {
				button = 2;
			} else if ((nativeButtons & NativeEvent.BUTTON_MIDDLE) != 0) {
				button = 3;
			}
		}

		return button;
	}

	/**
	 * Returns the horizontal screen position of the pointer at event time (for
	 * pointer-related events).
	 *
	 * @return The horizontal pointer position or {@link Integer#MIN_VALUE} if
	 * not available
	 */
	public int getPointerX() {
		return nativeEvent != null ?
		       nativeEvent.getClientX() :
		       Integer.MIN_VALUE;
	}

	/**
	 * Returns the vertical screen position of the pointer at event time (for
	 * pointer-related events).
	 *
	 * @return The vertical pointer position or {@link Integer#MIN_VALUE} if
	 * not
	 * available
	 */
	public int getPointerY() {
		return nativeEvent != null ?
		       nativeEvent.getClientY() :
		       Integer.MIN_VALUE;
	}

	/**
	 * Returns the object that caused this event.
	 *
	 * @return The event source
	 */
	public Object getSource() {
		return source;
	}

	/**
	 * Returns the type of this event.
	 *
	 * @return The event type
	 */
	public EventType getType() {
		return eventType;
	}

	/**
	 * Returns TRUE if this event has been canceled by a previous call to the
	 * method {@link #cancel()}.
	 *
	 * @return TRUE if this event has been canceled
	 */
	public boolean isCanceled() {
		return canceled;
	}

	/**
	 * Returns a string description of this event.
	 *
	 * @return A string describing this instance
	 */
	@Override
	public String toString() {
		return "EWTEvent[" + eventType + ',' + source + ',' + getPointerX() +
			',' + getPointerY() + ',' + getModifiers() + ',' + getKeyCode() +
			']';
	}
}
