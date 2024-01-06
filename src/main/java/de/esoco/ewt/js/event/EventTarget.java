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

import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

import de.esoco.ewt.js.JsObject;

/**
 * JsType declaration for <a
 * href="https://developer.mozilla.org/en-US/docs/Web/API/EventTarget">
 * EventTarget</a>.
 *
 * @author eso
 */
@JsType(isNative = true, namespace = JsPackage.GLOBAL)
public class EventTarget extends JsObject {

	/**
	 * @see <a href="https://developer.mozilla
	 * .org/en-US/docs/Web/API/EventTarget/addEventListener">
	 * EventTarget.addEventListener()</a>
	 */
	public native void addEventListener(String sType,
		EventListener<Event> fListener);

	/**
	 * Adds an event listener for an enumerated event type.
	 *
	 * @param eType     The event type
	 * @param fListener The listener to add
	 */
	@JsOverlay
	public final void addEventListener(Event.Type eType,
		EventListener<Event> fListener) {
		addEventListener(eType.toTypeString(), fListener);
	}

	/**
	 * @see <a href="https://developer.mozilla
	 * .org/en-US/docs/Web/API/EventTarget/dispatchEvent">
	 * EventTarget.dispatchEvent()</a>
	 */
	public native void dispatchEvent(Event rEvent);

	/**
	 * Removes an event listener for an enumerated event type.
	 *
	 * @param eType     The event type
	 * @param fListener The listener to remove
	 */
	@JsOverlay
	public final void removeEventListener(Event.Type eType,
		EventListener<Event> fListener) {
		removeEventListener(eType.toTypeString(), fListener);
	}

	/**
	 * @see <a href="https://developer.mozilla
	 * .org/en-US/docs/Web/API/EventTarget/removeEventListener">
	 * EventTarget.removeEventListener()</a>
	 */
	public native void removeEventListener(String sType,
		EventListener<Event> fListener);
}
