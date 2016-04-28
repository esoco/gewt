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

import de.esoco.ewt.EWT;
import de.esoco.ewt.UserInterfaceContext;
import de.esoco.ewt.build.ContainerBuilder;
import de.esoco.ewt.layout.GenericLayout;
import de.esoco.ewt.style.StyleData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.InsertPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;


/********************************************************************
 * The base class for GEWT containers. It wraps an implementation of the GWT
 * interface {@link HasWidgets}.
 */
public abstract class Container extends Component
{
	//~ Instance fields --------------------------------------------------------

	private GenericLayout rLayout;
	private HasWidgets    rContainer;

	private int nNewComponentPosition = -1;

	private List<Component> aComponents = new ArrayList<Component>();

	private List<Component> aComponentList =
		Collections.unmodifiableList(aComponents);

	//~ Methods ----------------------------------------------------------------

	/***************************************
	 * Removes all components from this container. Depending on the
	 * implementation this call may invalidate the components. To prevent errors
	 * no methods should be invoked on a component after it has been removed
	 * from it's container.
	 */
	public void clear()
	{
		rLayout.clear(rContainer);
		aComponents.clear();
	}

	/***************************************
	 * Returns the index of a certain child component. This method will only
	 * work correctly for indexed containers for which the {@link #isIndexed()}
	 * returns TRUE.
	 *
	 * @param  rComponent The child component to return the index of
	 *
	 * @return The component's index or -1 if not found or not supported
	 */
	public int getComponentIndex(Component rComponent)
	{
		Widget rContainerWidget = getWidget();
		int    nIndex		    = -1;

		if (rContainerWidget instanceof InsertPanel)
		{
			nIndex =
				((InsertPanel) rContainerWidget).getWidgetIndex(rComponent
																.getWidget());
		}

		return nIndex;
	}

	/***************************************
	 * Returns a list of the components of this container. The components are in
	 * the order in which they have been added to the container.
	 *
	 * @return The list of components
	 */
	public final List<Component> getComponents()
	{
		return aComponentList;
	}

	/***************************************
	 * Returns the layout used by the container to arrange and size it's
	 * components.
	 *
	 * @return The layout of the container
	 */
	public GenericLayout getLayout()
	{
		return rLayout;
	}

	/***************************************
	 * Returns the position at which new components will be added to this
	 * container.
	 *
	 * @return The position index for new components or -1 if new components are
	 *         added as the last child of the container
	 *
	 * @see    #setNewComponentPosition(int)
	 */
	public int getNewComponentPosition()
	{
		return nNewComponentPosition;
	}

	/***************************************
	 * EWT-internal method to add a component to this container. Application
	 * code should never invoke this method but use a {@link ContainerBuilder}
	 * instance instead. The container builder will then invoke this method.
	 *
	 * @param    rComponent The component to add
	 * @param    rStyleData The style data for the component
	 *
	 * @category GEWT
	 */
	public final void internalAddComponent(
		Component rComponent,
		StyleData rStyleData)
	{
		rComponent.setParent(this);
		rComponent.applyStyle(rStyleData);
		addWidget(rContainer, rComponent.getWidget(), rStyleData);

		if (nNewComponentPosition >= 0)
		{
			aComponents.add(nNewComponentPosition, rComponent);
		}
		else
		{
			aComponents.add(rComponent);
		}
	}

	/***************************************
	 * Returns the indexed.
	 *
	 * @return The indexed
	 */
	public boolean isIndexed()
	{
		return (getWidget() instanceof InsertPanel);
	}

	/***************************************
	 * Removes a certain component from this container. Depending on the
	 * implementation this call may invalidate the component. To prevent errors
	 * no methods should be invoked on a component after it has been removed
	 * from it's container.
	 *
	 * @param rComponent The component to remove
	 */
	public void removeComponent(Component rComponent)
	{
		rLayout.removeWidget(rContainer, rComponent.getWidget());
		aComponents.remove(rComponent);
	}

	/***************************************
	 * Sets the layout to be used by the container to arrange and size it's
	 * components.
	 *
	 * @param rLayout The layout to use
	 */
	public void setLayout(GenericLayout rLayout)
	{
		this.rLayout = EWT.getLayoutMapper().mapLayout(this, rLayout);
	}

	/***************************************
	 * Sets the position at which new components will be added to this
	 * container. Afterwards new components will be added before any other
	 * component at that index. This also implies that subsequently added
	 * components will be added before each other until the new component
	 * position is changed. This method will only work correctly for indexed
	 * containers for which the method {@link #isIndexed()} returns TRUE.
	 *
	 * @param nPosition The position index for new components or -1 to add new
	 *                  components as the last child of the container
	 */
	public void setNewComponentPosition(int nPosition)
	{
		nNewComponentPosition = nPosition;
	}

	/***************************************
	 * Overridden to first check if the layout creates a container widget. A
	 * final check for a correct container type of the widget will be performed
	 * be the overridden method {@link #setWidget(Widget)}.
	 *
	 * @see Component#createWidget(UserInterfaceContext, StyleData)
	 */
	@Override
	protected IsWidget createWidget(
		UserInterfaceContext rContext,
		StyleData			 rStyle)
	{
		rContainer = rLayout.createLayoutContainer(rContext, rStyle);

		return (IsWidget) rContainer;
	}

	/***************************************
	 * Returns the container widget of this instance.
	 *
	 * @return   The container widget
	 *
	 * @category GEWT
	 */
	protected final HasWidgets getContainer()
	{
		return rContainer;
	}

	/***************************************
	 * Internal method to add a GWT widget to this container. Subclasses may
	 * override this method to implement a different strategy of adding
	 * components to their container implementation. This default implementation
	 * either lets the container's layout add the widget by invoking the method
	 * {@link GenericLayout#addWidget(HasWidgets, Widget, StyleData, int)} or,
	 * if no layout has been set, invokes the method {@link
	 * HasWidgets#add(Widget)} of the widget container.
	 *
	 * @param rContainer The container to add the widget to
	 * @param rWidget    The widget to add
	 * @param rStyleData The style data defining the layout position of the
	 *                   widget
	 */
	void addWidget(HasWidgets rContainer, Widget rWidget, StyleData rStyleData)
	{
		rLayout.addWidget(rContainer,
						  rWidget,
						  rStyleData,
						  nNewComponentPosition);
	}

	/***************************************
	 * Overridden to also set the container of this instance.
	 *
	 * @see Component#setWidget(IsWidget)
	 */
	@Override
	void setWidget(IsWidget rIsWidget)
	{
		Widget rContainerWidget = rIsWidget.asWidget();

		assert rContainerWidget instanceof HasWidgets : "Container widget must implement HasWidgets";

		super.setWidget(rIsWidget);

		rContainer = (HasWidgets) rContainerWidget;

		if (rLayout == null)
		{
			rLayout = new ImplicitLayout();
		}
	}

	//~ Inner Classes ----------------------------------------------------------

	/********************************************************************
	 * A simple layout implementation that will be used if no explicit layout
	 * has been set.
	 *
	 * @author eso
	 */
	private class ImplicitLayout extends GenericLayout
	{
		//~ Methods ------------------------------------------------------------

		/***************************************
		 * {@inheritDoc}
		 */
		@Override
		public HasWidgets createLayoutContainer(
			UserInterfaceContext rContext,
			StyleData			 rContainerStyle)
		{
			return rContainer;
		}
	}
}
