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

import jsinterop.annotations.JsConstructor;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

import de.esoco.ewt.js.event.Event.Type;
import de.esoco.ewt.js.event.EventTarget;
import de.esoco.ewt.js.event.MessageEvent;

/**
 * JsInterop wrapper for <a
 * href="https://developer.mozilla.org/en-US/docs/Web/API/WebSocket">
 * WebSocket</a>.
 *
 * @author eso
 */
@JsType(isNative = true, namespace = JsPackage.GLOBAL)
public class WebSocket extends EventTarget {

	/**
	 * @see <a href="https://developer.mozilla
	 * .org/en-US/docs/Web/API/WebSocket/readyState"> WebSocket.readyState</a>
	 */
	@JsType
	public enum ReadyState {CONNECTING, OPEN, CLOSING, CLOSED}

	/**
	 * @see <a href="https://developer.mozilla
	 * .org/en-US/docs/Web/API/WebSocket/binaryType"> WebSocket.binaryType</a>
	 */
	@JsProperty
	public String binaryType;

	/**
	 * @see <a href="https://developer.mozilla
	 * .org/en-US/docs/Web/API/WebSocket/readyState"> WebSocket.readyState</a>
	 */
	@JsProperty
	public int readyState;

	/**
	 * @see <a href="https://developer.mozilla
	 * .org/en-US/docs/Web/API/WebSocket/bufferedAmount">
	 * WebSocket.bufferedAmount</a>
	 */
	@JsProperty
	public long bufferedAmount;

	/**
	 * Creates a new instance.
	 *
	 * @see <a href="https://developer.mozilla
	 * .org/en-US/docs/Web/API/WebSocket/WebSocket"> WebSocket(url)</a>
	 */
	@JsConstructor
	public WebSocket(String sUrl) {
	}

	/**
	 * @see <a href="https://developer.mozilla
	 * .org/en-US/docs/Web/API/WebSocket/close"> WebSocket.close()</a>
	 */
	@JsMethod
	public native void close();

	/**
	 * @see <a href="https://developer.mozilla
	 * .org/en-US/docs/Web/API/WebSocket/protocol"> WebSocket.protocol</a>
	 */
	@JsProperty
	public native String getProtocol();

	/**
	 * Returns the {@link #readyState} as the corresponding enum constant.
	 *
	 * @see <a href="https://developer.mozilla
	 * .org/en-US/docs/Web/API/WebSocket/readyState"> WebSocket.readyState</a>
	 */
	@JsOverlay
	public final ReadyState getReadyState() {
		return ReadyState.values()[readyState];
	}

	/**
	 * @see <a href="https://developer.mozilla
	 * .org/en-US/docs/Web/API/WebSocket/url"> WebSocket.url</a>
	 */
	@JsProperty
	public native String getUrl();

	/**
	 * Adds an event handler for web socket messages.
	 *
	 * @param fHandler The event handling consumer
	 */
	@JsOverlay
	public final void onMessage(JsConsumer<MessageEvent> fHandler) {
		addEventListener(Type.MESSAGE, e -> fHandler.accept((MessageEvent) e));
	}

	/**
	 * @see <a href="https://developer.mozilla
	 * .org/en-US/docs/Web/API/WebSocket/send"> WebSocket.send()</a>
	 */
	@JsMethod
	public native void send(String data);
}
