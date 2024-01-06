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
 * Unmodifiable representation of a 2-dimensional screen rectangle.
 *
 * @author eso
 */
public class Rectangle {

	private int h;

	private int w;

	private int x;

	private int y;

	/**
	 * Copy constructor.
	 *
	 * @param rOther The point to copy
	 */
	public Rectangle(Rectangle rOther) {
		this.x = rOther.x;
		this.y = rOther.y;
		this.w = rOther.w;
		this.h = rOther.h;
	}

	/**
	 * Creates a new point.
	 *
	 * @param x The x position
	 * @param y The y position
	 * @param w The width
	 * @param h The height
	 */
	public Rectangle(int x, int y, int w, int h) {
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
	}

	/**
	 * Default constructor for serialization.
	 */
	Rectangle() {
	}

	/**
	 * Checks whether or not this rectangle contains a certain point.
	 *
	 * @param x The x coordinate of the point
	 * @param y The y coordinate of the point
	 * @return TRUE if this rectangle contains the given point
	 */
	public boolean contains(int x, int y) {
		return (x >= this.x && x < this.x + this.w) &&
			(y >= this.y && y < this.y + this.h);
	}

	/**
	 * Checks if another object is a rectangle with the same values.
	 *
	 * @see Object#equals(Object)
	 */
	@Override
	public boolean equals(Object rOther) {
		if (rOther instanceof Rectangle) {
			Rectangle o = (Rectangle) rOther;

			return x == o.x && y == o.y && w == o.w && h == o.h;
		}

		return false;
	}

	/**
	 * Returns the y position.
	 *
	 * @return The y position
	 */
	public final int getHeight() {
		return h;
	}

	/**
	 * Returns the location of the rectangle.
	 *
	 * @return A new Point instance containing the rectangle's location
	 */
	public final Point getLocation() {
		return new Point(x, y);
	}

	/**
	 * Returns the size of the rectangle.
	 *
	 * @return A new Size instance containing the rectangle's size
	 */
	public final Size getSize() {
		return new Size(w, h);
	}

	/**
	 * Returns the x position.
	 *
	 * @return The x position
	 */
	public final int getWidth() {
		return w;
	}

	/**
	 * Returns the x position.
	 *
	 * @return The x position
	 */
	public final int getX() {
		return x;
	}

	/**
	 * Returns the y position.
	 *
	 * @return The y position
	 */
	public final int getY() {
		return y;
	}

	/**
	 * Calculates the hash code of this instance.
	 *
	 * @see Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return ((((((17 + x) * 37) + y) * 37) + w) * 37) + h;
	}

	/**
	 * Returns a string representation of this instance. The value is for
	 * debugging purposes only and should not be used for parsing.
	 *
	 * @return A string description of this instance
	 */
	@Override
	public String toString() {
		return "Rectangle[" + x + "," + y + "," + w + "," + h + "]";
	}
}
