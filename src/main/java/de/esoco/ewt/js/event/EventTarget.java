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
	public native void addEventListener(String type,
		EventListener<Event> listener);

	/**
	 * Adds an event listener for an enumerated event type.
	 *
	 * @param type     The event type
	 * @param listener The listener to add
	 */
	@JsOverlay
	public final void addEventListener(Event.Type type,
		EventListener<Event> listener) {
		addEventListener(type.toTypeString(), listener);
	}

	/**
	 * @see <a href="https://developer.mozilla
	 * .org/en-US/docs/Web/API/EventTarget/dispatchEvent">
	 * EventTarget.dispatchEvent()</a>
	 */
	public native void dispatchEvent(Event event);

	/**
	 * Removes an event listener for an enumerated event type.
	 *
	 * @param type     The event type
	 * @param listener The listener to remove
	 */
	@JsOverlay
	public final void removeEventListener(Event.Type type,
		EventListener<Event> listener) {
		removeEventListener(type.toTypeString(), listener);
	}

	/**
	 * @see <a href="https://developer.mozilla
	 * .org/en-US/docs/Web/API/EventTarget/removeEventListener">
	 * EventTarget.removeEventListener()</a>
	 */
	public native void removeEventListener(String type,
		EventListener<Event> listener);
}
