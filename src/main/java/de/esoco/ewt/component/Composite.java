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

import de.esoco.ewt.build.ContainerBuilder;
import de.esoco.ewt.layout.GenericLayout;
import de.esoco.ewt.style.StyleData;

import com.google.gwt.user.client.ui.IsWidget;

/**
 * Base class for components that are composed from a combination of other
 * components.
 *
 * @author eso
 */
public abstract class Composite extends Component {

	private Panel contentPanel = new Panel();

	/**
	 * Creates a new instance with a certain layout.
	 *
	 * @param layout The layout of the composite container
	 */
	protected Composite(GenericLayout layout) {
		contentPanel.setLayout(layout);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initWidget(Container parent, StyleData style) {
		super.initWidget(parent, style);

		build(new ContainerBuilder<Panel>(contentPanel));
	}

	/**
	 * Must be implemented by subclasses to build their content with the given
	 * builder.
	 *
	 * @param builder The builder to add the content components with
	 */
	protected abstract void build(ContainerBuilder<?> builder);

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IsWidget createWidget(StyleData style) {
		contentPanel.initWidget(getParent(), style);

		contentPanel.addStyleName("CompositeContent");

		return contentPanel.getWidget();
	}
}
