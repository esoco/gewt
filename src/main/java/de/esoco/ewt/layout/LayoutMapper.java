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

/**
 * An interface for the mapping of EWT layouts to other layout instances after
 * their creation. EWT extensions can use this to replace default layouts with
 * their own instances. This should be used if a {@link LayoutFactory} is not
 * sufficient because a layout needs initialization.
 *
 * @author eso
 */
public interface LayoutMapper {

	/**
	 * Checks whether the given layout for a particular target container should
	 * be mapped to a different layout instance and returns that if applicable.
	 *
	 * @param container The target container for the layout
	 * @param layout    The original layout for the container
	 * @return Either a new (mapped) layout instance or the original layout if
	 * no mapping is necessary
	 */
	public GenericLayout mapLayout(Container container, GenericLayout layout);

	/**
	 * A default layout mapper implementation that always returns the original
	 * layout.
	 *
	 * @author eso
	 */
	public static class IdentityLayoutMapper implements LayoutMapper {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GenericLayout mapLayout(Container container,
			GenericLayout layout) {
			return layout;
		}
	}
}
