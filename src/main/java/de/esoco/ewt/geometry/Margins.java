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

	private int left;

	private int right;

	private int top;

	private int bottom;

	/**
	 * Default Constructor for zero margins.
	 */
	public Margins() {
	}

	/**
	 * Creates equal margins for all sides.
	 *
	 * @param margin The margin
	 */
	public Margins(int margin) {
		this(margin, margin, margin, margin);
	}

	/**
	 * Copy constructor.
	 *
	 * @param margins The margins object to copy the values from
	 */
	public Margins(Margins margins) {
		this.left = margins.left;
		this.right = margins.right;
		this.top = margins.top;
		this.bottom = margins.bottom;
	}

	/**
	 * Creates equal horizontal and vertical margins.
	 *
	 * @param horizontalMargin The horizontal margins to the left and right
	 * @param verticalMargin   The vertical margins at the top and bottom
	 */
	public Margins(int horizontalMargin, int verticalMargin) {
		this(horizontalMargin, horizontalMargin, verticalMargin,
			verticalMargin);
	}

	/**
	 * Creates different margins for each side.
	 *
	 * @param left   The margin to the left
	 * @param right  The margin to the right
	 * @param top    The margin at the top
	 * @param bottom The margin at the bottom
	 */
	public Margins(int left, int right, int top, int bottom) {
		this.left = left;
		this.right = right;
		this.top = top;
		this.bottom = bottom;
	}

	/**
	 * Returns a new instance that is the sum of this instance and the given
	 * other margins object.
	 *
	 * @param other The other margins object
	 * @return The new margins instance
	 */
	public final Margins add(Margins other) {
		Margins margins = new Margins(this);

		margins.left += other.left;
		margins.right += other.right;
		margins.top += other.top;
		margins.bottom += other.bottom;

		return margins;
	}

	/**
	 * Checks if this margins object is equal to another.
	 *
	 * @param other The other object to compare with.
	 * @return TRUE if the objects are equal
	 */
	@Override
	public boolean equals(Object other) {
		if (other == this) {
			return true;
		}

		if (!(other instanceof Margins)) {
			return false;
		}

		Margins m = (Margins) other;

		return left == m.left && right == m.right && top == m.top &&
			bottom == m.bottom;
	}

	/**
	 * Returns the bottom value.
	 *
	 * @return The bottom value
	 */
	public final int getBottom() {
		return bottom;
	}

	/**
	 * Returns the left value.
	 *
	 * @return The left value
	 */
	public final int getLeft() {
		return left;
	}

	/**
	 * Returns the right value.
	 *
	 * @return The right value
	 */
	public final int getRight() {
		return right;
	}

	/**
	 * Returns the top value.
	 *
	 * @return The top value
	 */
	public final int getTop() {
		return top;
	}

	/**
	 * Calculates the hash code of this instance.
	 *
	 * @return The integer hash code
	 */
	@Override
	public int hashCode() {
		return (((17 + left) * 37 + right) * 37 + top) * 37 + bottom;
	}

	/**
	 * Calculates the total height of this instance which is the sum of top and
	 * bottom margins.
	 *
	 * @return The margins total height
	 */
	public final int height() {
		return top + bottom;
	}

	/**
	 * Returns a parseable string representation of this instances parameters.
	 * The format of the returned string is "left,right,top,bottom".
	 *
	 * @return The parameter string
	 */
	public String paramString() {
		return left + "," + right + "," + top + "," + bottom;
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
		return left + right;
	}
}
