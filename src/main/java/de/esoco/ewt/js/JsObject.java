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

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

/**
 * JSInterop wrapper for <a href="https://developer.mozilla
 * .org/docs/Web/JavaScript/Reference/Global_Objects/Object"> Object</a>.
 *
 * @author eso
 */
@JsType(isNative = true, name = "Object", namespace = JsPackage.GLOBAL)
public class JsObject {

	/**
	 * @see <a href="https://developer.mozilla
	 * .org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Object
	 * /getOwnPropertyNames"> Object.getOwnPropertyNames</a>
	 */
	public static native String[] getOwnPropertyNames(Object rObject);

	/**
	 * @see <a href="https://developer.mozilla
	 * .org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Object
	 * /hasOwnProperty"> Object.hasOwnProperty</a>
	 */
	public native boolean hasOwnProperty(String sName);

	/**
	 * @see <a href="https://developer.mozilla
	 * .org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Object
	 * /toString"> Object.toString</a>
	 */
	@Override
	public native String toString();
}
