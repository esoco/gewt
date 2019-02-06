//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// chaintag source file
// Copyright (c) 2019 by Elmar Sonnenschein / esoco GmbH
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
package de.esoco.ewt.js;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;


/********************************************************************
 * JSInterop wrapper for <a
 * href="https://developer.mozilla.org/docs/Web/JavaScript/Reference/Global_Objects/Object">
 * Object</a>.
 *
 * @author eso
 */
@JsType(isNative  = true, name = "Object", namespace = JsPackage.GLOBAL)
public class JsObject
{
	//~ Static methods ---------------------------------------------------------

	/***************************************
	 * @see <a
	 *      href="https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Object/getOwnPropertyNames">
	 *      Object.getOwnPropertyNames</a>
	 */
	public static native String[] getOwnPropertyNames(Object rObject);

	//~ Methods ----------------------------------------------------------------

	/***************************************
	 * @see <a
	 *      href="https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Object/hasOwnProperty">
	 *      Object.hasOwnProperty</a>
	 */
	public native boolean hasOwnProperty(String sName);
}
