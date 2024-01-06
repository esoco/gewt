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

/**
 * TODO: DOCUMENT ME!
 *
 * @author Matthew Horridge, Stanford University, Bio-Medical Informatics
 * Research Group, Date: 18/03/2014
 */
public interface AutoCompletionHandler {

	/**
	 * Called to get the completions for the specified editor text.
	 *
	 * @param sText          The text. Not {@code null}.
	 * @param rCaretPosition The caret position, containing line and column
	 *                       information. Not {@code null}.
	 * @param nCaretIndex    The caret index with respect to the editor text.
	 *                       Not {@code null}.
	 * @param rCallback      A callback that should be used to signal that
	 *                       auto-completion results are ready. Not
	 *                       {@code null}.
	 */
	void getCompletions(String sText, EditorPosition rCaretPosition,
		int nCaretIndex, AutoCompletionCallback rCallback);
}
