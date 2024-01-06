//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// This file is a part of the 'gewt' project.
// Copyright 2015 Elmar Sonnenschein, esoco GmbH, Flensburg, Germany
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
package de.esoco.ewt.geometry;

/**
 * Unmodifiable representation of a 2-dimensional size value.
 *
 * @author eso
 */
public class Size {
	/**
	 * Constant for a size with values 0,0
	 */
	public static final Size ZERO_SIZE = new Size(0, 0);

	private int w;

	private int h;

	/**
	 * Copy constructor.
	 *
	 * @param rOther The size to copy
	 */
	public Size(Size rOther) {
		this.w = rOther.w;
		this.h = rOther.h;
	}

	/**
	 * Creates a new size.
	 *
	 * @param w The width
	 * @param h The height
	 */
	public Size(int w, int h) {
		this.w = w;
		this.h = h;
	}

	/**
	 * Default constructor for serialization.
	 */
	Size() {
	}

	/**
	 * Checks if another object is a size with the same dimensions.
	 *
	 * @see Object#equals(Object)
	 */
	@Override
	public boolean equals(Object rOther) {
		if (rOther instanceof Size) {
			Size o = (Size) rOther;

			return w == o.w && h == o.h;
		} else {
			return false;
		}
	}

	/**
	 * Returns the height.
	 *
	 * @return The height
	 */
	public final int getHeight() {
		return h;
	}

	/**
	 * Returns the width.
	 *
	 * @return The width
	 */
	public final int getWidth() {
		return w;
	}

	/**
	 * Calculates the hashcode of this instance.
	 *
	 * @see Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return ((17 + w) * 37) + h;
	}

	/**
	 * Returns a string representation of this instance. The value is for
	 * debugging purposes only and should not be used for parsing.
	 *
	 * @return A string description of this instance
	 */
	@Override
	public String toString() {
		return "Size[" + w + "," + h + "]";
	}
}
