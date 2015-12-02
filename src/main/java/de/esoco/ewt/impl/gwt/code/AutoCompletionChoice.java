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

import com.google.gwt.user.client.rpc.IsSerializable;


/********************************************************************
 * TODO: DOCUMENT ME!
 *
 * @author Matthew Horridge, Stanford University, Bio-Medical Informatics
 *         Research Group, Date: 18/03/2014
 */
public class AutoCompletionChoice implements Serializable, IsSerializable
{
	private static final long serialVersionUID = 1L;

	private String sText;

	private String sDisplayText;

	private String sCssClassName;

	private EditorPosition rReplaceTextFrom;

	private EditorPosition rReplaceTextTo;

	/***************************************
	 * Creates a new instance.
	 *
	 * @param sText            TODO: DOCUMENT ME!
	 * @param sDisplayText     TODO: DOCUMENT ME!
	 * @param sCssClassName    TODO: DOCUMENT ME!
	 * @param rReplaceTextFrom TODO: DOCUMENT ME!
	 * @param rReplaceTextTo   TODO: DOCUMENT ME!
	 */
	public AutoCompletionChoice(String		   sText,
								String		   sDisplayText,
								String		   sCssClassName,
								EditorPosition rReplaceTextFrom,
								EditorPosition rReplaceTextTo)
	{
		this.sText			  = sText;
		this.sDisplayText     = sDisplayText;
		this.sCssClassName    = sCssClassName;
		this.rReplaceTextFrom = rReplaceTextFrom;
		this.rReplaceTextTo   = rReplaceTextTo;
	}

	/***************************************
	 * For serialization purposes only
	 */
	AutoCompletionChoice()
	{
	}

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

		if (!(o instanceof AutoCompletionChoice))
		{
			return false;
		}

		AutoCompletionChoice other = (AutoCompletionChoice) o;

		return this.sText.equals(other.sText) &&
			   this.sDisplayText.equals(other.sDisplayText) &&
			   this.sCssClassName.equals(other.sCssClassName) &&
			   this.rReplaceTextFrom.equals(other.rReplaceTextFrom) &&
			   this.rReplaceTextTo.equals(other.rReplaceTextTo);
	}

	/***************************************
	 * Returns the css class name.
	 *
	 * @return The css class name
	 */
	public String getCssClassName()
	{
		return sCssClassName;
	}

	/***************************************
	 * Returns the display text.
	 *
	 * @return The display text
	 */
	public String getDisplayText()
	{
		return sDisplayText;
	}

	/***************************************
	 * Returns the replace text from.
	 *
	 * @return The replace text from
	 */
	public EditorPosition getReplaceTextFrom()
	{
		return rReplaceTextFrom;
	}

	/***************************************
	 * Returns the replace text to.
	 *
	 * @return The replace text to
	 */
	public EditorPosition getReplaceTextTo()
	{
		return rReplaceTextTo;
	}

	/***************************************
	 * Returns the text.
	 *
	 * @return The text
	 */
	public String getText()
	{
		return sText;
	}

	/***************************************
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		return "AutoCompletionChoice".hashCode() + sText.hashCode() +
			   sDisplayText.hashCode() + sCssClassName.hashCode() +
			   rReplaceTextFrom.hashCode() + rReplaceTextTo.hashCode();
	}

	/***************************************
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return getClass().getSimpleName();
//				Objects.toStringHelper("AutoCompletionChoice").add("text", sText)
//					  .add("displayText", sDisplayText)
//					  .add("cssClassName", sCssClassName)
//					  .add("replaceFrom", rReplaceTextFrom)
//					  .add("replaceTo", rReplaceTextTo)
//					  .toString();
	}
}
