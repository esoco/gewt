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

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Represents a CodeMirror editor position, which is identified by the
 * zero-based line number and column number.
 *
 * @author Matthew Horridge, Stanford University, Bio-Medical Informatics
 * Research Group, Date: 19/03/2014
 */
public class EditorPosition implements Serializable, IsSerializable {
	private static final long serialVersionUID = 1L;

	private int line;

	private int Column;

	/**
	 * Creates a new instance.
	 *
	 * @param line   TODO: DOCUMENT ME!
	 * @param column TODO: DOCUMENT ME!
	 */
	public EditorPosition(int line, int column) {
		this.line = line;
		this.Column = column;
	}

	/**
	 * Creates a new instance.
	 */
	private EditorPosition() {
	}

	/**
	 * Extracts an {@link EditorPosition} from the specified JavaScriptObject.
	 *
	 * @param object The object. Not {@code null}.
	 * @return The corresponding {@link EditorPosition}
	 * @throws java.lang.NullPointerException is {@code object} is
	 *                                        {@code null}.
	 */
	public static EditorPosition fromJavaScriptObject(JavaScriptObject object) {
		int line = getIntFromJavaScriptObject(object, "line", 0);
		int ch = getIntFromJavaScriptObject(object, "ch", 0);

		return new EditorPosition(line, ch);
	}

	/**
	 * Returns the int from java script object.
	 *
	 * @param object       The int from java script object
	 * @param propertyName The int from java script object
	 * @param defaultValue The int from java script object
	 * @return The int from java script object
	 */
	private static native int getIntFromJavaScriptObject(
		JavaScriptObject object, String propertyName, int defaultValue) /*-{
		if(object[propertyName] == undefined) {
			return defaultValue;
		}
		return object[propertyName];
	}-*/;

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}

		if (!(o instanceof EditorPosition)) {
			return false;
		}

		EditorPosition other = (EditorPosition) o;

		return this.line == other.line && this.Column == other.Column;
	}

	/**
	 * Returns the column number.
	 *
	 * @return The column number
	 */
	public int getColumnNumber() {
		return Column;
	}

	/**
	 * Returns the line number.
	 *
	 * @return The line number
	 */
	public int getLineNumber() {
		return line;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return "EditorPosition".hashCode() + line + Column * 13;
	}

	/**
	 * Consverts this {@code EditorPosition} to a {@code JavaScriptObject}.
	 *
	 * @return The {@code JavaScriptObject} that is equivalent to this
	 * {@code EditorPosition}.
	 */
	public JavaScriptObject toJavaScriptObject() {
		return toJavaScriptObject(line, Column);
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getClass().getSimpleName();
//				Objects.toStringHelper("EditorPosition")
//					  .add("lineNumber", lineNumber)
//					  .add("columnNumber", columnNumber)
//					  .toString();
	}

	/**
	 * Create a JavaScriptObject that CodeMirror can use.
	 *
	 * @param line The line number.
	 * @param ch   The column number.
	 * @return The JavaScriptObject position object.
	 */
	private native JavaScriptObject toJavaScriptObject(int line, int ch) /*-{
		return {
			line: line,
			ch: ch
		};
	}-*/;
}
