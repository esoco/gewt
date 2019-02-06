//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// chaintag source file
// Copyright (c) 2019 by Elmar Sonnenschein / esoco GmbH
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
package de.esoco.ewt.js.event;

import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;


/********************************************************************
 * JsType declaration for <a
 * href="https://developer.mozilla.org/en-US/docs/Web/API/Event">Event</a>.
 *
 * @author eso
 */
@JsType(isNative  = true, namespace = JsPackage.GLOBAL)
public class Event
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
