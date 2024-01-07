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

import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.ui.ValueBoxBase;

/**
 * An event handler implementation that imposes a regular expression constraint
 * on widgets that are derived from {@link ValueBoxBase}. The input of any value
 * that doesn't match the regular expression will be prevented.
 *
 * @author eso
 */
public abstract class ValueBoxConstraint implements KeyPressHandler {
	/**
	 * @see KeyPressHandler#onKeyPress(KeyPressEvent)
	 */
	@Override
	public void onKeyPress(KeyPressEvent event) {
		ValueBoxBase<?> valueBox = (ValueBoxBase<?>) event.getSource();
		String text = valueBox.getText();
		char input = event.getCharCode();
		int cursor = valueBox.getCursorPos();
		int selection = valueBox.getSelectionLength();

		if (input != 0) {
			String newText = text.substring(0, cursor) + input +
				text.substring(cursor + selection);

			if (!isValid(newText)) {
				valueBox.cancelKey();
			}
		}
	}

	/**
	 * Checks whether a new text string is valid according to this constraint.
	 *
	 * @param newText The new text to validate
	 * @return TRUE if the given text is valid, FALSE if not
	 */
	protected abstract boolean isValid(String newText);

	/**
	 * A constraint implementation that validates values by checking them
	 * against minimum and maximum integer values.
	 *
	 * @author eso
	 */
	public static class IntRangeConstraint extends ValueBoxConstraint {
		private final int minimumValue;

		private final int maximumValue;

		/**
		 * Creates a new instance.
		 *
		 * @param min The minimum value
		 * @param max The maximum value
		 */
		public IntRangeConstraint(int min, int max) {
			minimumValue = min;
			maximumValue = max;
		}

		/**
		 * @see ValueBoxConstraint#isValid(String)
		 */
		@Override
		protected boolean isValid(String newText) {
			boolean valid = false;

			try {
				int newValue = Integer.parseInt(newText);

				valid = newValue >= minimumValue && newValue <= maximumValue;
			} catch (Exception e) {
				// continue and return FALSE
			}

			return valid;
		}
	}

	/**
	 * A constraint implementation that validates values by applying a regular
	 * expression.
	 *
	 * @author eso
	 */
	public static class RegExConstraint extends ValueBoxConstraint {
		private final String constraintRegEx;

		/**
		 * Creates a new instance.
		 *
		 * @param regEx The regular expression of the constraint
		 */
		public RegExConstraint(String regEx) {
			constraintRegEx = regEx;
		}

		/**
		 * Returns the valid.
		 *
		 * @param newText The valid
		 * @return The valid
		 */
		@Override
		protected boolean isValid(String newText) {
			return newText.matches(constraintRegEx);
		}
	}
}
