//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// This file is a part of the 'gewt' project.
// Copyright 2019 Elmar Sonnenschein, esoco GmbH, Flensburg, Germany
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
package de.esoco.ewt.js.event;

import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

import de.esoco.ewt.js.JsObject;


/********************************************************************
 * JsType declaration for <a
 * href="https://developer.mozilla.org/en-US/docs/Web/API/Event">Event</a>.
 *
 * @author eso
 */
@JsType(isNative  = true, namespace = JsPackage.GLOBAL)
public class Event extends JsObject
{
	//~ Enums ------------------------------------------------------------------

	/********************************************************************
	 * Enumeration of JavaScript event types.
	 */
	@JsType
	public enum Type
	{
		ABORT, BEFOREINPUT, BLUR, CLICK, CLOSE, COMPOSITIONSTART,
		COMPOSITIONUPDATE, COMPOSITIONEND, DBLCLICK, DRAG, DRAGEND, DRAGENTER,
		DRAGEXIT, DRAGLEAVE, DRAGOVER, DRAGSTART, DROP, ERROR, FOCUS, FOCUSIN,
		FOCUSOUT, INPUT, KEYDOWN, KEYPRESS, KEYUP, LOAD, MESSAGE, MOUSEDOWN,
		MOUSEENTER, MOUSELEAVE, MOUSEMOVE, MOUSEOUT, MOUSEOVER, MOUSEUP, OPEN,
		RESIZE, SCROLL, SELECT, UNLOAD, WHEEL;

		//~ Methods ------------------------------------------------------------

		/***************************************
		 * Returns the JavaScript event type string.
		 *
		 * @return The event type string
		 */
		public String toTypeString()
		{
			return name().toLowerCase();
		}
	}

	//~ Instance fields --------------------------------------------------------

	/**
	 * @see <a
	 *      href="https://developer.mozilla.org/en-US/docs/Web/API/Event/type">
	 *      Event.type</a>
	 */
	@JsProperty
	public String type;

	//~ Methods ----------------------------------------------------------------

	/***************************************
	 * @see <a
	 *      href="https://developer.mozilla.org/en-US/docs/Web/API/Event/target">
	 *      Event.target</a>
	 */
	@JsProperty
	public native Object getTarget();

	/***************************************
	 * Returns the event type as the corresponding enum constant.
	 *
	 * @return The event type
	 */
	@JsOverlay
	public final Type getType()
	{
		return Type.valueOf(type.toUpperCase());
	}

	/***************************************
	 * @see <a
	 *      href="https://developer.mozilla.org/en-US/docs/Web/API/Event/bubbles">
	 *      Event.bubbles</a>
	 */
	@JsProperty
	public native boolean isBubbles();

	/***************************************
	 * @see <a
	 *      href="https://developer.mozilla.org/en-US/docs/Web/API/Event/cancelable">
	 *      Event.cancelable</a>
	 */
	@JsProperty
	public native boolean isCancelable();

	/***************************************
	 * @see <a
	 *      href="https://developer.mozilla.org/en-US/docs/Web/API/Event/stopPropagation">
	 *      Event.preventDefault()</a>
	 */
	@JsMethod
	public native void preventDefault();

	/***************************************
	 * @see <a
	 *      href="https://developer.mozilla.org/en-US/docs/Web/API/Event/stopImmediatePropagation">
	 *      Event.stopImmediatePropagation()</a>
	 */
	@JsMethod
	public native void stopImmediatePropagation();

	/***************************************
	 * @see <a
	 *      href="https://developer.mozilla.org/en-US/docs/Web/API/Event/stopPropagation">
	 *      Event.stopPropagation()</a>
	 */
	@JsMethod
	public native void stopPropagation();
}
