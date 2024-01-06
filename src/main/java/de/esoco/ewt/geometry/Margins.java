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
 * Simple immutable class that holds integer margins for rectangular areas.
 *
 * @author eso
 */
public class Margins {

	/**
	 * Constant for zero margins
	 */
	public static final Margins ZERO_MARGINS = new Margins();

	private int nLeft;

	private int nRight;

	private int nTop;

	private int nBottom;

	/**
	 * Default Constructor for zero margins.
	 */
	public Margins() {
	}

	/**
	 * Creates equal margins for all sides.
	 *
	 * @param nMargin The margin
	 */
	public Margins(int nMargin) {
		this(nMargin, nMargin, nMargin, nMargin);
	}

	/**
	 * Copy constructor.
	 *
	 * @param rMargins The margins object to copy the values from
	 */
	public Margins(Margins rMargins) {
		this.nLeft = rMargins.nLeft;
		this.nRight = rMargins.nRight;
		this.nTop = rMargins.nTop;
		this.nBottom = rMargins.nBottom;
	}

	/**
	 * Creates equal horizontal and vertical margins.
	 *
	 * @param nHorizontalMargin The horizontal margins to the left and right
	 * @param nVerticalMargin   The vertical margins at the top and bottom
	 */
	public Margins(int nHorizontalMargin, int nVerticalMargin) {
		this(nHorizontalMargin, nHorizontalMargin, nVerticalMargin,
			nVerticalMargin);
	}

	/**
	 * Creates different margins for each side.
	 *
	 * @param nLeft   The margin to the left
	 * @param nRight  The margin to the right
	 * @param nTop    The margin at the top
	 * @param nBottom The margin at the bottom
	 */
	public Margins(int nLeft, int nRight, int nTop, int nBottom) {
		this.nLeft = nLeft;
		this.nRight = nRight;
		this.nTop = nTop;
		this.nBottom = nBottom;
	}

	/**
	 * Returns a new instance that is the sum of this instance and the given
	 * other margins object.
	 *
	 * @param rOther The other margins object
	 * @return The new margins instance
	 */
	public final Margins add(Margins rOther) {
		Margins aMargins = new Margins(this);

		aMargins.nLeft += rOther.nLeft;
		aMargins.nRight += rOther.nRight;
		aMargins.nTop += rOther.nTop;
		aMargins.nBottom += rOther.nBottom;

		return aMargins;
	}

	/**
	 * Checks if this margins object is equal to another.
	 *
	 * @param rOther The other object to compare with.
	 * @return TRUE if the objects are equal
	 */
	@Override
	public boolean equals(Object rOther) {
		if (rOther == this) {
			return true;
		}

		if (!(rOther instanceof Margins)) {
			return false;
		}

		Margins m = (Margins) rOther;

		return nLeft == m.nLeft && nRight == m.nRight && nTop == m.nTop &&
			nBottom == m.nBottom;
	}

	/**
	 * Returns the bottom value.
	 *
	 * @return The bottom value
	 */
	public final int getBottom() {
		return nBottom;
	}

	/**
	 * Returns the left value.
	 *
	 * @return The left value
	 */
	public final int getLeft() {
		return nLeft;
	}

	/**
	 * Returns the right value.
	 *
	 * @return The right value
	 */
	public final int getRight() {
		return nRight;
	}

	/**
	 * Returns the top value.
	 *
	 * @return The top value
	 */
	public final int getTop() {
		return nTop;
	}

	/**
	 * Calculates the hash code of this instance.
	 *
	 * @return The integer hash code
	 */
	@Override
	public int hashCode() {
		return (((17 + nLeft) * 37 + nRight) * 37 + nTop) * 37 + nBottom;
	}

	/**
	 * Calculates the total height of this instance which is the sum of top and
	 * bottom margins.
	 *
	 * @return The margins total height
	 */
	public final int height() {
		return nTop + nBottom;
	}

	/**
	 * Returns a parseable string representation of this instances parameters.
	 * The format of the returned string is "left,right,top,bottom".
	 *
	 * @return The parameter string
	 */
	public String paramString() {
		return nLeft + "," + nRight + "," + nTop + "," + nBottom;
	}

	/**
	 * Returns a string description of this margins object.
	 *
	 * @return A string describing the margins
	 */
	@Override
	public String toString() {
		return "Margins[" + paramString() + "]";
	}

	/**
	 * Calculates the total width of this instance which is the sum of left and
	 * right margins.
	 *
	 * @return The margins total width
	 */
	public final int width() {
		return nLeft + nRight;
	}
}
