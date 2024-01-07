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
package de.esoco.ewt.component;

import de.esoco.ewt.layout.GenericLayout;

/**
 * A base class for panels that have a fixed layout that cannot be changed after
 * creation. The layout of this instance must be set as a constructor argument.
 * The method {@link #setLayout(GenericLayout)} is overridden to thrown an
 * exception.
 *
 * @author eso
 */
public abstract class FixedLayoutPanel extends Panel {

	/**
	 * Creates a new instance.
	 *
	 * @param layout The fixed layout for this instance
	 */
	public FixedLayoutPanel(GenericLayout layout) {
		super.setLayout(layout);
	}

	/**
	 * Overridden to throw an exception as the fixed layout is defined by the
	 * constructor.
	 *
	 * @see Panel#setLayout(GenericLayout)
	 */
	@Override
	public void setLayout(GenericLayout layout) {
		throw new UnsupportedOperationException(
			"Layout must be set through constructor");
	}
}
