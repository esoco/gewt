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
package de.esoco.ewt.impl.gwt.code;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;


/********************************************************************
 * TODO: DOCUMENT ME!
 *
 * @author Matthew Horridge, Stanford University, Bio-Medical Informatics
 *         Research Group, Date: 18/03/2014
 */
public class AutoCompletionResult implements Serializable, IsSerializable
{
	//~ Static fields/initializers ---------------------------------------------

	private static final long serialVersionUID = 1L;

	private static final AutoCompletionResult EMPTY_RESULT =
		new AutoCompletionResult();

	//~ Instance fields --------------------------------------------------------

	private List<AutoCompletionChoice> rChoices;

	private EditorPosition rFromPosition;

	//~ Constructors -----------------------------------------------------------

	/***************************************
	 * Creates a new instance.
	 */
	public AutoCompletionResult()
	{
		this(new ArrayList<AutoCompletionChoice>(), new EditorPosition(0, 0));
	}

	/***************************************
	 * Creates a new instance.
	 *
	 * @param rChoices      TODO: DOCUMENT ME!
	 * @param rFromPosition TODO: DOCUMENT ME!
	 */
	public AutoCompletionResult(
		List<AutoCompletionChoice> rChoices,
		EditorPosition			   rFromPosition)
	{
		this.rChoices	   = new ArrayList<AutoCompletionChoice>(rChoices);
		this.rFromPosition = rFromPosition;
	}

	//~ Static methods ---------------------------------------------------------

	/***************************************
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public static AutoCompletionResult emptyResult()
	{
		return EMPTY_RESULT;
	}

	//~ Methods ----------------------------------------------------------------

	/***************************************
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o)
	{
		if (o == this)
		{
			return true;
		}

		if (!(o instanceof AutoCompletionResult))
		{
			return false;
		}

		AutoCompletionResult other = (AutoCompletionResult) o;

		return this.rFromPosition.equals(other.rFromPosition) &&
			   this.rChoices.equals(other.rChoices);
	}

	/***************************************
	 * Returns the choices.
	 *
	 * @return The choices
	 */
	public List<AutoCompletionChoice> getChoices()
	{
		return new ArrayList<AutoCompletionChoice>(rChoices);
	}

	/***************************************
	 * Returns the from position.
	 *
	 * @return The from position
	 */
	public EditorPosition getFromPosition()
	{
		return rFromPosition;
	}

	/***************************************
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		return "AutoCompletionResult".hashCode() + rChoices.hashCode() +
			   rFromPosition.hashCode();
	}
}
