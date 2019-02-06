//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// chaintag source file
// Copyright (c) 2019 by Elmar Sonnenschein / esoco GmbH
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
package de.esoco.ewt.js.event;

import jsinterop.annotations.JsFunction;


/********************************************************************
 * JsType declaration for <a
 * href="https://developer.mozilla.org/en-US/docs/Web/API/EventListener">
 * EventListener</a>.
 *
 * @author eso
 */
@FunctionalInterface
@JsFunction
public interface EventListener
{
	//~ Methods ----------------------------------------------------------------

	/***************************************
	 * @see <a
	 *      href="https://developer.mozilla.org/en-US/docs/Web/API/EventListener/handleEvent">
	 *      EventListener.handleEvent()</a>
	 */
	void handleEvent(Event rEvent);
}
