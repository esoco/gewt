//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// chaintag source file
// Copyright (c) 2018 by Elmar Sonnenschein / esoco GmbH
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
package de.esoco.ewt.js;

import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsOverlay;


/********************************************************************
 * Interface of a JavaScript function without input or output. As JavaScript is
 * untyped there is no notion of "void" types. Therefore this interface provides
 * a default implementation of a JavaScript unary function and an abstract
 * {@link #run()} method without input or return value.
 *
 * @author eso
 */
@FunctionalInterface
@JsFunction
public interface JsRunnable
{
	//~ Methods ----------------------------------------------------------------

	/***************************************
	 * Default JavaScript function method that ignores the input and always
	 * returns NULL.
	 *
	 * @param  rValue The value to consume
	 *
	 * @return Always NULL
	 */
	@JsOverlay
	default Object apply(Object rValue)
	{
		run();

		return null;
	}

	/***************************************
	 * The consuming method to implement.
	 */
	void run();
}
