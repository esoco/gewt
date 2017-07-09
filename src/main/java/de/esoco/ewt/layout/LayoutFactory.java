//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// This file is a part of the 'gewt' project.
// Copyright 2016 Elmar Sonnenschein, esoco GmbH, Flensburg, Germany
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
package de.esoco.ewt.layout;

import de.esoco.ewt.component.Container;
import de.esoco.ewt.style.StyleData;

import de.esoco.lib.property.LayoutType;


/********************************************************************
 * A factory interface that creates container layouts. EWT extensions can use
 * this to replace default layouts with their own instances. Some layouts may
 * require initialization and can therefore only be replaced after they have
 * been created. For this purpose extensions may also need to implement the
 * {@link LayoutMapper} interface.
 *
 * @author eso
 */
public interface LayoutFactory
{
	//~ Methods ----------------------------------------------------------------

	/***************************************
	 * Checks whether the given layout for a particular target container should
	 * be mapped to a different layout instance and returns that if applicable.
	 *
	 * @param  rParentContainer The parent of the container for which the layout
	 *                          is created
	 * @param  rContainerStyle  rContainer The target container for the layout
	 * @param  eLayout          The original layout for the container
	 *
	 * @return Either a new (mapped) layout instance or the original layout if
	 *         no mapping is necessary
	 */
	public GenericLayout createLayout(Container rParentContainer,
									  StyleData rContainerStyle,
									  LayoutType    eLayout);
}
