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
 * Unmodifiable representation of a 2-dimensional screen coordinate.
 *
 * @author eso
 */
public class Point {
	private int x;

	private int y;

	/**
	 * Copy constructor.
	 *
	 * @param other The point to copy
	 */
	public Point(Point other) {
		this.x = other.x;
		this.y = other.y;
	}

	/**
	 * Creates a new point.
	 *
	 * @param x The x position
	 * @param y The y position
	 */
	public Point(int x, int y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Default constructor for serialization.
	 */
	Point() {
	}

	/**
	 * Checks if another object is a point with the same coordinates.
	 *
	 * @see Object#equals(Object)
	 */
	@Override
	public boolean equals(Object other) {
		if (other instanceof Point) {
			Point o = (Point) other;

			return x == o.x && y == o.y;
		} else {
			return false;
		}
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
	 * Calculates the hashcode of this instance.
	 *
	 * @see Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return ((17 + x) * 37) + y;
	}

	/**
	 * Returns a string representation of this instance. The value is for
	 * debugging purposes only and should not be used for parsing.
	 *
	 * @return A string description of this instance
	 */
	@Override
	public String toString() {
		return "Point[" + x + "," + y + "]";
	}
}
