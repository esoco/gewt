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
package de.esoco.ewt.impl.gwt;

/**
 * A simple format interface that defines how to convert values objects into
 * strings.
 *
 * @author eso
 */
public interface ValueFormat {

	/**
	 * Invokes {@link Object#toString()} on input objects.
	 */
	public static final ValueFormat TO_STRING = new ValueFormat() {
		@Override
		public String format(Object value) {
			return value.toString();
		}
	};

	/**
	 * Formats a value object into a string.
	 *
	 * @param value The value
	 * @return A string representing the value
	 */
	public String format(Object value);

	/**
	 * A format that converts objects with the {@link Object#toString()} method
	 * but also limits the length of the string.
	 *
	 * @author eso
	 */
	public static class StringLengthFormat implements ValueFormat {

		private final int maxLength;

		/**
		 * Creates a new instance.
		 *
		 * @param maxLength The maximum length for strings.
		 */
		public StringLengthFormat(int maxLength) {
			this.maxLength = maxLength;
		}

		/**
		 * @see ValueFormat#format(Object)
		 */
		@Override
		public String format(Object value) {
			String result = value.toString();

			if (result.length() > maxLength) {
				result = result.substring(0, maxLength - 1) + '\u2026';
			}

			return result;
		}

		/**
		 * Returns the maximum length of string formatted by this instance.
		 *
		 * @return The maximum string length
		 */
		public final int getMaxLength() {
			return maxLength;
		}
	}
}
