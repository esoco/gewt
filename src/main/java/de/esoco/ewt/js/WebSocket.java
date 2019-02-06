//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// chaintag source file
// Copyright (c) 2019 by Elmar Sonnenschein / esoco GmbH
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
package de.esoco.ewt.js;

import de.esoco.ewt.js.event.EventTarget;

import jsinterop.annotations.JsConstructor;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;


/********************************************************************
 * JsInterop wrapper for <a
 * href="https://developer.mozilla.org/en-US/docs/Web/API/WebSocket">
 * WebSocket</a>.
 *
 * @author eso
 */
@JsType(isNative  = true, namespace = JsPackage.GLOBAL)
public class WebSocket extends EventTarget
{
	//~ Enums ------------------------------------------------------------------

	/********************************************************************
	 * @see <a
	 *      href="https://developer.mozilla.org/en-US/docs/Web/API/WebSocket/readyState">
	 *      WebSocket.readyState</a>
	 */
	@JsType
	public enum ReadyState { CONNECTING, OPEN, CLOSING, CLOSED }

	//~ Instance fields --------------------------------------------------------

	/**
	 * @see <a
	 *      href="https://developer.mozilla.org/en-US/docs/Web/API/WebSocket/binaryType">
	 *      WebSocket.binaryType</a>
	 */
	@JsProperty
	public String binaryType;

	/**
	 * @see <a
	 *      href="https://developer.mozilla.org/en-US/docs/Web/API/WebSocket/readyState">
	 *      WebSocket.readyState</a>
	 */
	@JsProperty
	public int readyState;

	/**
	 * @see <a
	 *      href="https://developer.mozilla.org/en-US/docs/Web/API/WebSocket/bufferedAmount">
	 *      WebSocket.bufferedAmount</a>
	 */
	@JsProperty
	public long bufferedAmount;

	//~ Constructors -----------------------------------------------------------

	/***************************************
	 * Creates a new instance.
	 *
	 * @see <a
	 *      href="https://developer.mozilla.org/en-US/docs/Web/API/WebSocket/WebSocket">
	 *      WebSocket(url)</a>
	 */
	@JsConstructor
	public WebSocket(String sUrl)
	{
	}

	//~ Methods ----------------------------------------------------------------

	/***************************************
	 * @see <a
	 *      href="https://developer.mozilla.org/en-US/docs/Web/API/WebSocket/close">
	 *      WebSocket.close()</a>
	 */
	@JsMethod
	public native void close();

	/***************************************
	 * @see <a
	 *      href="https://developer.mozilla.org/en-US/docs/Web/API/WebSocket/protocol">
	 *      WebSocket.protocol</a>
	 */
	@JsProperty
	public native String getProtocol();

	/***************************************
	 * Returns the {@link #readyState} as the corresponding enum constant.
	 *
	 * @see <a
	 *      href="https://developer.mozilla.org/en-US/docs/Web/API/WebSocket/readyState">
	 *      WebSocket.readyState</a>
	 */
	@JsOverlay
	public final ReadyState getReadyState()
	{
		return ReadyState.values()[readyState];
	}

	/***************************************
	 * @see <a
	 *      href="https://developer.mozilla.org/en-US/docs/Web/API/WebSocket/url">
	 *      WebSocket.url</a>
	 */
	@JsProperty
	public native String getUrl();

	/***************************************
	 * @see <a
	 *      href="https://developer.mozilla.org/en-US/docs/Web/API/WebSocket/send">
	 *      WebSocket.send()</a>
	 */
	@JsMethod
	public native void send(String data);
}
