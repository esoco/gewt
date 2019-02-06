//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// chaintag source file
// Copyright (c) 2019 by Elmar Sonnenschein / esoco GmbH
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
package de.esoco.ewt.js;

import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsOverlay;


/********************************************************************
 * Interface of a JavaScript consuming function. As JavaScript is untyped there
 * is no notion of "void" types. Therefore this interface provides a default
 * implementation of a JavaScript unary function and an abstract consuming
 * method without return value.
 *
 * @author eso
 */
@FunctionalInterface
@JsFunction
public interface JsConsumer<T>
{
	//~ Methods ----------------------------------------------------------------

	/***************************************
	 * The consuming method to implement.
	 *
	 * @param rValue The value to consume
	 */
	void accept(T rValue);

	/***************************************
	 * Default JavaScript function method that always returns NULL.
	 *
	 * @param  rValue The value to consume
	 *
	 * @return Always NULL
	 */
	@JsOverlay
	default Object apply(T rValue)
	{
		accept(rValue);

		return null;
	}
}
