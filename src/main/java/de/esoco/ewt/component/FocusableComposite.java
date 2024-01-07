//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// This file is a part of the 'gewt' project.
// Copyright 2018 Elmar Sonnenschein, esoco GmbH, Flensburg, Germany
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
import de.esoco.ewt.style.StyleData;

import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

/**
 * A composite that can receive the input focus.
 *
 * @author eso
 */
public abstract class FocusableComposite extends Composite {

	/**
	 * Creates a new instance.
	 *
	 * @param layout The composite layout
	 */
	protected FocusableComposite(GenericLayout layout) {
		super(layout);
	}

	/**
	 * Requests that the component gets the input focus.
	 */
	public void requestFocus() {
		((Focusable) getWidget()).setFocus(true);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IsWidget createWidget(StyleData style) {
		return new FocusPanel((Widget) super.createWidget(style));
	}
}
