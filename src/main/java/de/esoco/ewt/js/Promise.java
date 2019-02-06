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
package de.esoco.ewt.js;

import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;


/********************************************************************
 * Generically typed JSInterop wrapper for <a
 * href="https://developer.mozilla.org/docs/Web/JavaScript/Reference/Global_Objects/Promise">
 * Promise</a>.
 *
 * @author eso
 */
@JsType(isNative  = true, namespace = JsPackage.GLOBAL)
public class Promise<T>
{
	//~ Methods ----------------------------------------------------------------

	/***************************************
	 * @see <a
	 *      href="https://developer.mozilla.org/docs/Web/JavaScript/Reference/Global_Objects/Promise/catch">
	 *      Promise.catch()</a>
	 */
	@JsMethod(name = "catch")
	public native Promise<T> doCatch(JsConsumer<Object> fOnReject);

	/***************************************
	 * @see <a
	 *      href="https://developer.mozilla.org/docs/Web/JavaScript/Reference/Global_Objects/Promise/finally">
	 *      Promise.finally()</a>
	 */
	@JsMethod(name = "finally")
	public native Promise<T> doFinally(JsRunnable fOnResolve);

	/***************************************
	 * @see <a
	 *      href="https://developer.mozilla.org/docs/Web/JavaScript/Reference/Global_Objects/Promise/then">
	 *      Promise.then()</a>
	 */
	public native Promise<T> then(JsConsumer<T> fOnResolve);

	/***************************************
	 * @see <a
	 *      href="https://developer.mozilla.org/docs/Web/JavaScript/Reference/Global_Objects/Promise/then">
	 *      Promise.then()</a>
	 */
	public native Promise<T> then(
		JsConsumer<T>	   fOnResolve,
		JsConsumer<Object> fOnReject);
}
