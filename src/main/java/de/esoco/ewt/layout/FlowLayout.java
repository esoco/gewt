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

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * A flow layout implementation.
 *
 * @author eso
 */
public class FlowLayout extends GenericLayout {

	private final Boolean horizontal;

	private String layoutStyleName = null;

	/**
	 * Creates a new instance that is based on a GWT {@link FlowPanel}.
	 */
	public FlowLayout() {
		horizontal = null;
	}

	/**
	 * Creates a new instance with an additional layout style name.
	 *
	 * @param styleName The additional style name
	 */
	public FlowLayout(String styleName) {
		this();

		layoutStyleName = styleName;
	}

	/**
	 * Creates a new instance that is based on a GWT {@link HorizontalPanel} or
	 * a {@link VerticalPanel}.
	 *
	 * @param horizontal TRUE for horizontal, FALSE for vertical orientation
	 */
	public FlowLayout(boolean horizontal) {
		this.horizontal = Boolean.valueOf(horizontal);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Panel createLayoutContainer(Container container,
		StyleData containerStyle) {
		Panel panel;

		if (horizontal == null) {
			panel = new FlowPanel();
		} else if (horizontal == Boolean.TRUE) {
			panel = new HorizontalPanel();
		} else {
			panel = new VerticalPanel();
		}

		panel.addStyleName("ewt-FlowLayout");

		if (layoutStyleName != null) {
			panel.addStyleName(layoutStyleName);
		}

		return panel;
	}
}
