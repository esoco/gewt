//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// This file is a part of the 'gewt' project.
// Copyright 2015 Elmar Sonnenschein, esoco GmbH, Flensburg, Germany
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
import com.google.gwt.event.dom.client.KeyCodes;


/********************************************************************
 * Instances of this class contain EWT event information. There is no
 * hierarchical event tree in EWT, all event information will be propagated in
 * EWTEvent objects. Which fields of an event actually contain information
 * depends on the type of event that occurred (e.g. pointer coordinates will
 * only be available from pointer-related events).
 *
 * @author eso
 */
public final class EWTEvent
{
	private final Object	  rSource;
	private final Object	  rElement;
	private final EventType   rEventType;
	private final NativeEvent rNativeEvent;

	private boolean bCanceled;

	/***************************************
	 * Internal constructor, instance creation is controlled by the factory
	 * method {@link #getEvent(Object, Object, EventType, int, int, KeyCode,
	 * ModifierKeys)}.
	 *
	 * @param rSource      The source of the event
	 * @param rElement     The element affected by this event or NULL for none
	 * @param rType        The event type
	 * @param rNativeEvent The native event that occurred
	 */
	private EWTEvent(Object		 rSource,
					 Object		 rElement,
					 EventType   rType,
					 NativeEvent rNativeEvent)
	{
		this.rSource	  = rSource;
		this.rElement     = rElement;
		this.rEventType   = rType;
		this.rNativeEvent = rNativeEvent;
	}

	/***************************************
	 * Factory method that returns a new event instance containing the given
	 * parameters.
	 *
	 * @param  rSource      The source of the event
	 * @param  rElement     The element affected by this event or NULL for none
	 * @param  rType        The event type
	 * @param  rNativeEvent The native event that occurred
	 *
	 * @return An event instance containing the given parameters
	 */
	public static EWTEvent getEvent(Object		rSource,
									Object		rElement,
									EventType   rType,
									NativeEvent rNativeEvent)
	{
		return new EWTEvent(rSource, rElement, rType, rNativeEvent);
	}

	/***************************************
	 * Maps a GWT key code to the corresponding {@link KeyCode} instance.
	 *
	 * @param    rEvent The native GWT event to map the key code of
	 *
	 * @return   The corresponding EWT key code instance
	 *
	 * @category GEWT
	 */
	public static KeyCode mapGwtKeyCode(NativeEvent rEvent)
	{
		int nGwtKeyCode = rEvent.getKeyCode();

		switch (nGwtKeyCode)
		{
			case KeyCodes.KEY_SHIFT:
				return KeyCode.SHIFT;

			case KeyCodes.KEY_ALT:
				return KeyCode.ALT;

			case KeyCodes.KEY_CTRL:
				return KeyCode.CONTROL;

			case KeyCodes.KEY_BACKSPACE:
				return KeyCode.BACKSPACE;

			case KeyCodes.KEY_DELETE:
				return KeyCode.DELETE;

			case KeyCodes.KEY_DOWN:
				return KeyCode.DOWN;

			case KeyCodes.KEY_END:
				return KeyCode.END;

			case KeyCodes.KEY_ENTER:
				return KeyCode.ENTER;

			case KeyCodes.KEY_ESCAPE:
				return KeyCode.ESCAPE;

			case KeyCodes.KEY_HOME:
				return KeyCode.HOME;

			case KeyCodes.KEY_LEFT:
				return KeyCode.LEFT;

			case KeyCodes.KEY_PAGEDOWN:
				return KeyCode.PAGE_DOWN;

			case KeyCodes.KEY_PAGEUP:
				return KeyCode.PAGE_UP;

			case KeyCodes.KEY_RIGHT:
				return KeyCode.RIGHT;

			case KeyCodes.KEY_TAB:
				return KeyCode.TAB;

			case KeyCodes.KEY_UP:
				return KeyCode.UP;

			default:

				KeyCode eKeyCode = KeyCode.forCode(nGwtKeyCode);

				if (eKeyCode == null)
				{
					eKeyCode = KeyCode.forChar((char) nGwtKeyCode);
				}

				return eKeyCode;
		}
	}

	/***************************************
	 * Maps a GWT modifier key combination to the corresponding instance of
	 * {@link ModifierKeys}.
	 *
	 * @param    rEvent The native GWT event to map the modifiers of
	 *
	 * @return   The corresponding EWT key modifiers instance
	 *
	 * @category GEWT
	 */
	public static ModifierKeys mapGwtModifiers(NativeEvent rEvent)
	{
		int nBits = 0;

		if (rEvent.getAltKey())
		{
			nBits |= ModifierKeys.ALT_BIT;
		}

		if (rEvent.getCtrlKey())
		{
			nBits |= ModifierKeys.CTRL_BIT;
		}

		if (rEvent.getMetaKey())
		{
			nBits |= ModifierKeys.META_BIT;
		}

		if (rEvent.getShiftKey())
		{
			nBits |= ModifierKeys.SHIFT_BIT;
		}

		return ModifierKeys.valueOf(nBits);
	}

	/***************************************
	 * Cancels the event to prevent further processing. What exactly this call
	 * does and for which event types it has an effect depends on the underlying
	 * implementation. In general it can be assumed that it will work for input
	 * events like keyboard input and that it allows to prevent the input to go
	 * into the component that caused the event.
	 */
	public void cancel()
	{
		if (rNativeEvent != null)
		{
			rNativeEvent.preventDefault();
		}

		bCanceled = true;
	}

	/***************************************
	 * Returns the element that is affected by this event. What exactly such an
	 * element is depends on the event source. Typically this will be an element
	 * of components that are composed of multiple elements, like lists or
	 * trees.
	 *
	 * @return The element affected by this event (NULL for none)
	 */
	public final Object getElement()
	{
		return rElement;
	}

	/***************************************
	 * Returns the typed character in case of keyboard events. For KEY_TYPED
	 * events this method will always return a valid character but for
	 * KEY_PRESSED and KEY_RELEASED events which can also occur for
	 * non-printable characters the result of this method will be zero.
	 *
	 * @return The typed character of the keyboard event or 0 (zero) if not
	 *         available
	 */
	public final char getKeyChar()
	{
		return rNativeEvent != null ? (char) rNativeEvent.getCharCode() : 0;
	}

	/***************************************
	 * Returns the key code in case of KEY_PRESSED and KEY_RELEASED events. For
	 * KEY_TYPED events this method will always return KeyCode.NONE.
	 *
	 * @return The key code of the keyboard event or KeyCode.NONE if not
	 *         available
	 */
	public final KeyCode getKeyCode()
	{
		return rNativeEvent != null ? mapGwtKeyCode(rNativeEvent)
									: KeyCode.NONE;
	}

	/***************************************
	 * Return the state of the modifier keys at event time.
	 *
	 * @return One of the enumerated instances of the {@link ModifierKeys} class
	 *         describing the modifier key states
	 */
	public ModifierKeys getModifiers()
	{
		return rNativeEvent != null ? mapGwtModifiers(rNativeEvent)
									: ModifierKeys.NONE;
	}

	/***************************************
	 * Returns the pointer button that caused the pointer event.
	 *
	 * @return The number of the pointer button pressed, starting at 1
	 */
	public final int getPointerButton()
	{
		int nButton = 0;

		if (rNativeEvent != null)
		{
			int nNativeButtons = rNativeEvent.getButton();

			if ((nNativeButtons & NativeEvent.BUTTON_LEFT) != 0)
			{
				nButton = 1;
			}
			else if ((nNativeButtons & NativeEvent.BUTTON_RIGHT) != 0)
			{
				nButton = 2;
			}
			else if ((nNativeButtons & NativeEvent.BUTTON_MIDDLE) != 0)
			{
				nButton = 3;
			}
		}

		return nButton;
	}

	/***************************************
	 * Returns the horizontal screen position of the pointer at event time (for
	 * pointer-related events).
	 *
	 * @return The horizontal pointer position or {@link Integer#MIN_VALUE} if
	 *         not available
	 */
	public final int getPointerX()
	{
		return rNativeEvent != null ? rNativeEvent.getClientX()
									: Integer.MIN_VALUE;
	}

	/***************************************
	 * Returns the vertical screen position of the pointer at event time (for
	 * pointer-related events).
	 *
	 * @return The vertical pointer position or {@link Integer#MIN_VALUE} if not
	 *         available
	 */
	public final int getPointerY()
	{
		return rNativeEvent != null ? rNativeEvent.getClientY()
									: Integer.MIN_VALUE;
	}

	/***************************************
	 * Returns the object that caused this event.
	 *
	 * @return The event source
	 */
	public final Object getSource()
	{
		return rSource;
	}

	/***************************************
	 * Returns the type of this event.
	 *
	 * @return The event type
	 */
	public final EventType getType()
	{
		return rEventType;
	}

	/***************************************
	 * Returns TRUE if this event has been canceled by a previous call to the
	 * method {@link #cancel()}.
	 *
	 * @return TRUE if this event has been canceled
	 */
	public boolean isCanceled()
	{
		return bCanceled;
	}

	/***************************************
	 * Returns a string description of this event.
	 *
	 * @return A string describing this instance
	 */
	@Override
	public String toString()
	{
		return "EWTEvent[" + rEventType + ',' + rSource + ',' + getPointerX() +
			   ',' + getPointerY() + ',' + getModifiers() + ',' + getKeyCode() +
			   ']';
	}
}
