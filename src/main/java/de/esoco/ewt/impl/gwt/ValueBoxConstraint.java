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


/********************************************************************
 * An event handler implementation that imposes a regular expression constraint
 * on widgets that are derived from {@link ValueBoxBase}. The input of any value
 * that doesn't match the regular expression will be prevented.
 *
 * @author eso
 */
public abstract class ValueBoxConstraint implements KeyPressHandler
{
	/***************************************
	 * @see KeyPressHandler#onKeyPress(KeyPressEvent)
	 */
	@Override
	public void onKeyPress(KeyPressEvent rEvent)
	{
		ValueBoxBase<?> rValueBox  = (ValueBoxBase<?>) rEvent.getSource();
		String		    sText	   = rValueBox.getText();
		char		    cInput     = rEvent.getCharCode();
		int			    nCursor    = rValueBox.getCursorPos();
		int			    nSelection = rValueBox.getSelectionLength();

		if (cInput != 0)
		{
			String sNewText =
				sText.substring(0, nCursor) + cInput +
				sText.substring(nCursor + nSelection);

			if (!isValid(sNewText))
			{
				rValueBox.cancelKey();
			}
		}
	}

	/***************************************
	 * Checks whether a new text string is valid according to this constraint.
	 *
	 * @param  sNewText The new text to validate
	 *
	 * @return TRUE if the given text is valid, FALSE if not
	 */
	protected abstract boolean isValid(String sNewText);

	/********************************************************************
	 * A constraint implementation that validates values by checking them
	 * against minimum and maximum integer values.
	 *
	 * @author eso
	 */
	public static class IntRangeConstraint extends ValueBoxConstraint
	{
		private final int nMinimumValue;
		private final int nMaximumValue;

		/***************************************
		 * Creates a new instance.
		 *
		 * @param nMin The minimum value
		 * @param nMax The maximum value
		 */
		public IntRangeConstraint(int nMin, int nMax)
		{
			nMinimumValue = nMin;
			nMaximumValue = nMax;
		}

		/***************************************
		 * @see ValueBoxConstraint#isValid(String)
		 */
		@Override
		protected boolean isValid(String sNewText)
		{
			boolean bValid = false;

			try
			{
				int nNewValue = Integer.parseInt(sNewText);

				bValid =
					nNewValue >= nMinimumValue && nNewValue <= nMaximumValue;
			}
			catch (Exception e)
			{
				// continue and return FALSE
			}

			return bValid;
		}
	}

	/********************************************************************
	 * A constraint implementation that validates values by applying a regular
	 * expression.
	 *
	 * @author eso
	 */
	public static class RegExConstraint extends ValueBoxConstraint
	{
		private final String sConstraintRegEx;

		/***************************************
		 * Creates a new instance.
		 *
		 * @param sRegEx The regular expression of the constraint
		 */
		public RegExConstraint(String sRegEx)
		{
			sConstraintRegEx = sRegEx;
		}

		/***************************************
		 * Returns the valid.
		 *
		 * @param  sNewText The valid
		 *
		 * @return The valid
		 */
		@Override
		protected boolean isValid(String sNewText)
		{
			return sNewText.matches(sConstraintRegEx);
		}
	}
}
