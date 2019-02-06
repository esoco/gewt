//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// chaintag source file
// Copyright (c) 2019 by Elmar Sonnenschein / esoco GmbH
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
package de.esoco.ewt.js.html;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;


/********************************************************************
 * JsType declaration for <a
 * href="https://developer.mozilla.org/en-US/docs/Web/API/HTMLElement">
 * HTMLElement</a>.
 *
 * @author eso
 */
@JsType(isNative  = true, namespace = JsPackage.GLOBAL)
public class HTMLElement extends Node
{
	//~ Methods ----------------------------------------------------------------

	/***************************************
	 * Returns the shadow root document of this node.
	 *
	 * @see <a
	 *      href="https://developer.mozilla.org/en-US/docs/Web/API/HTMLElement/getShadowRoot">
	 *      HTMLElement.getShadowRoot()</a>
	 */
	public native Document getShadowRoot();

	/***************************************
	 * @see <a
	 *      href="https://developer.mozilla.org/en-US/docs/Web/API/HTMLElement/hidden">
	 *      HTMLElement.hidden</a>
	 */
	public native boolean isHidden();

	/***************************************
	 * @see <a
	 *      href="https://developer.mozilla.org/en-US/docs/Web/API/HTMLElement/hidden">
	 *      HTMLElement.hidden</a>
	 */
	public native void setHidden(boolean bHidden);
}
