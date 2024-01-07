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

/**
 * TODO: DOCUMENT ME!
 *
 * @author Matthew Horridge, Stanford University, Bio-Medical Informatics
 * Research Group, Date: 18/03/2014
 */
public class AutoCompletionChoice implements Serializable, IsSerializable {
	private static final long serialVersionUID = 1L;

	private String text;

	private String displayText;

	private String cssClassName;

	private EditorPosition replaceTextFrom;

	private EditorPosition replaceTextTo;

	/**
	 * Creates a new instance.
	 *
	 * @param text            TODO: DOCUMENT ME!
	 * @param displayText     TODO: DOCUMENT ME!
	 * @param cssClassName    TODO: DOCUMENT ME!
	 * @param replaceTextFrom TODO: DOCUMENT ME!
	 * @param replaceTextTo   TODO: DOCUMENT ME!
	 */
	public AutoCompletionChoice(String text, String displayText,
		String cssClassName, EditorPosition replaceTextFrom,
		EditorPosition replaceTextTo) {
		this.text = text;
		this.displayText = displayText;
		this.cssClassName = cssClassName;
		this.replaceTextFrom = replaceTextFrom;
		this.replaceTextTo = replaceTextTo;
	}

	/**
	 * For serialization purposes only
	 */
	AutoCompletionChoice() {
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}

		if (!(o instanceof AutoCompletionChoice)) {
			return false;
		}

		AutoCompletionChoice other = (AutoCompletionChoice) o;

		return this.text.equals(other.text) &&
			this.displayText.equals(other.displayText) &&
			this.cssClassName.equals(other.cssClassName) &&
			this.replaceTextFrom.equals(other.replaceTextFrom) &&
			this.replaceTextTo.equals(other.replaceTextTo);
	}

	/**
	 * Returns the css class name.
	 *
	 * @return The css class name
	 */
	public String getCssClassName() {
		return cssClassName;
	}

	/**
	 * Returns the display text.
	 *
	 * @return The display text
	 */
	public String getDisplayText() {
		return displayText;
	}

	/**
	 * Returns the replace text from.
	 *
	 * @return The replace text from
	 */
	public EditorPosition getReplaceTextFrom() {
		return replaceTextFrom;
	}

	/**
	 * Returns the replace text to.
	 *
	 * @return The replace text to
	 */
	public EditorPosition getReplaceTextTo() {
		return replaceTextTo;
	}

	/**
	 * Returns the text.
	 *
	 * @return The text
	 */
	public String getText() {
		return text;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return "AutoCompletionChoice".hashCode() + text.hashCode() +
			displayText.hashCode() + cssClassName.hashCode() +
			replaceTextFrom.hashCode() + replaceTextTo.hashCode();
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getClass().getSimpleName();
//				Objects.toStringHelper("AutoCompletionChoice").add("text",
//				text)
//					  .add("displayText", displayText)
//					  .add("cssClassName", cssClassName)
//					  .add("replaceFrom", replaceTextFrom)
//					  .add("replaceTo", replaceTextTo)
//					  .toString();
	}
}
