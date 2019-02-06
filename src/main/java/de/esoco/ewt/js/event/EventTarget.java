//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// chaintag source file
// Copyright (c) 2019 by Elmar Sonnenschein / esoco GmbH
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
package de.esoco.ewt.js.event;

import de.esoco.ewt.js.JsObject;

import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;


/********************************************************************
 * JsType declaration for <a
 * href="https://developer.mozilla.org/en-US/docs/Web/API/EventTarget">
 * EventTarget</a>.
 *
 * @author eso
 */
@JsType(isNative  = true, namespace = JsPackage.GLOBAL)
public class EventTarget extends JsObject
{
	//~ Methods ----------------------------------------------------------------

	/***************************************
	 * @see <a
	 *      href="https://developer.mozilla.org/en-US/docs/Web/API/EventTarget/addEventListener">
	 *      EventTarget.addEventListener()</a>
	 */
	public native void addEventListener(String sType, EventListener fListener);

	/***************************************
	 * Adds an event listener for an enumerated event type.
	 *
	 * @param eType     The event type
	 * @param fListener The listener to add
	 */
	@JsOverlay
	public final void addEventListener(
		Event.Type    eType,
		EventListener fListener)
	{
		addEventListener(eType.toTypeString(), fListener);
	}

	/***************************************
	 * @see <a
	 *      href="https://developer.mozilla.org/en-US/docs/Web/API/EventTarget/dispatchEvent">
	 *      EventTarget.dispatchEvent()</a>
	 */
	public native void dispatchEvent(Event rEvent);

	/***************************************
	 * Removes an event listener for an enumerated event type.
	 *
	 * @param eType     The event type
	 * @param fListener The listener to remove
	 */
	@JsOverlay
	public final void removeEventListener(
		Event.Type    eType,
		EventListener fListener)
	{
		removeEventListener(eType.toTypeString(), fListener);
	}

	/***************************************
	 * @see <a
	 *      href="https://developer.mozilla.org/en-US/docs/Web/API/EventTarget/removeEventListener">
	 *      EventTarget.removeEventListener()</a>
	 */
	public native void removeEventListener(
		String		  sType,
		EventListener fListener);
}
