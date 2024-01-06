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
package de.esoco.ewt.build;

import de.esoco.ewt.component.Container;
import de.esoco.ewt.style.StyleData;

/**
 * An abstract container builder subclass that can be used to implement the
 * management of EWT containers. Other than {@link ContainerBuilder} this class
 * is intended to be subclassed to create wrappers for the management of
 * application containers.
 *
 * @author eso
 */
public abstract class ContainerManager<C extends Container>
	extends ContainerBuilder<C> {

	private StyleData rBaseStyle;

	/**
	 * Creates a new instance that manages a certain container type.
	 */
	public ContainerManager() {
		super(null);
	}

	/**
	 * Builds a child container manager in the container of this instance by
	 * invoking it's {@link #buildIn(ContainerBuilder, StyleData)} method.
	 *
	 * @param rChildManager The child container manager to build
	 * @param rStyle        The style data for the appearance and
	 *                         positioning of
	 *                      the new child container
	 */
	public final void build(ContainerManager<?> rChildManager,
		StyleData rStyle) {
		rChildManager.buildIn(this, rStyle);
	}

	/**
	 * Builds a new panel in the container of the given container builder.
	 * Invokes the methods
	 * {@link #createContainer(ContainerBuilder, StyleData)}
	 * and {@link #addComponents()}. The {@link StyleData} parameter must
	 * contain the necessary layout constraints for the placements of the new
	 * container in the parent.
	 *
	 * @param rParentBuilder The parent container builder to build the panel
	 *                       with
	 * @param rStyle         The style data for the appearance and positioning
	 *                       of the new container
	 */
	public void buildIn(ContainerBuilder<?> rParentBuilder, StyleData rStyle) {
		rBaseStyle = rStyle;

		ContainerBuilder<C> aBuilder = createContainer(rParentBuilder, rStyle);

		setContainer(aBuilder.getContainer());
		setParent(aBuilder.getParent());

		addComponents();
	}

	/**
	 * Returns the style data with which this this instance has been created.
	 *
	 * @return The base style data
	 */
	public final StyleData getBaseStyle() {
		return rBaseStyle;
	}

	/**
	 * Re-builds the UI of this instance by clearing the managed container and
	 * invoking {@link #addComponents()}.
	 */
	public void rebuild() {
		getContainer().clear();
		addComponents();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return getClass().getSimpleName() + "[" + getContainer() + "]";
	}

	/**
	 * Must be implemented by subclasses to add the components in the container
	 * by invoking the corresponding add methods in this instance.
	 */
	protected abstract void addComponents();

	/**
	 * Must be implemented by subclasses to create a new container of the
	 * managed type with the given parent container builder.
	 *
	 * @param rBuilder   The container builder to build the panel with
	 * @param rStyleData The style data for the new panel
	 * @return The builder of the new container
	 */
	protected abstract ContainerBuilder<C> createContainer(
		ContainerBuilder<?> rBuilder, StyleData rStyleData);
}
