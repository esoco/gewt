//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// chaintag source file
// Copyright (c) 2019 by Elmar Sonnenschein / esoco GmbH
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
package de.esoco.ewt.js;

/**
 * Interface of a unary JavaScript function.
 *
 * @author eso
 */
@FunctionalInterface
@jsinterop.annotations.JsFunction
public interface JsFunction<T> {

	/**
	 * The function application method.
	 *
	 * @param rValue The value to process
	 * @return The result value
	 */
	T apply(Object rValue);
}
