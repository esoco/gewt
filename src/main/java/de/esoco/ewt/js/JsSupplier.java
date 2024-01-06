//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// chaintag source file
// Copyright (c) 2019 by Elmar Sonnenschein / esoco GmbH
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
package de.esoco.ewt.js;

import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsOverlay;

/**
 * Interface of a JavaScript supplying function. As JavaScript is untyped there
 * is no notion of "void" types. Therefore this interface provides a default
 * implementation of a JavaScript unary function and an abstract supplying
 * method without input value.
 *
 * @author eso
 */
@FunctionalInterface
@JsFunction
public interface JsSupplier<T> {

	/**
	 * Default JavaScript function method that ignores the input.
	 *
	 * @param rValue The value to consume
	 * @return Always NULL
	 */
	@JsOverlay
	default T apply(Object rValue) {
		return get();
	}

	/**
	 * The supplying method to implement.
	 *
	 * @return The supplied value
	 */
	T get();
}
