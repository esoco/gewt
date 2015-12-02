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

/********************************************************************
 * TODO: DOCUMENT ME!
 *
 * @author Matthew Horridge, Stanford University, Bio-Medical Informatics
 *         Research Group, Date: 18/03/2014
 */
public class NullAutoCompletionHandler implements AutoCompletionHandler
{
	//~ Methods ----------------------------------------------------------------

	/***************************************
	 * @see de.esoco.ewt.impl.gwt.code.AutoCompletionHandler#getCompletions(java.lang.String,
	 *      de.esoco.ewt.impl.gwt.code.EditorPosition, int,
	 *      de.esoco.ewt.impl.gwt.code.AutoCompletionCallback)
	 */
	@Override
	public void getCompletions(String				  text,
							   EditorPosition		  caretPosition,
							   int					  caretIndex,
							   AutoCompletionCallback callback)
	{
		callback.completionsReady(AutoCompletionResult.emptyResult());
	}
}
