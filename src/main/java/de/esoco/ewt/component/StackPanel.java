//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// This file is a part of the 'gewt' project.
// Copyright 2017 Elmar Sonnenschein, esoco GmbH, Flensburg, Germany
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
import de.esoco.ewt.event.EventType;
import de.esoco.ewt.graphics.ImageRef;
import de.esoco.ewt.impl.gwt.GewtResources;
import de.esoco.ewt.style.AlignedPosition;
import de.esoco.ewt.style.StyleData;

import de.esoco.lib.property.LayoutType;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.StackLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent.HasSelectionChangedHandlers;


/********************************************************************
 * A panel that can consist of multiple components that are arranged in a
 * vertical stack. Only one of the components will be visible at a time while
 * for the other components only a collapsed title will be displayed. By click
 * on such a title the corresponding component can be made visible.
 *
 * <p>Components that are added to a stack panel must have been created with the
 * panel as their parent, else unpredictable results may occur. They must be
 * added to this panel through the {@link #addPage(Component, String, boolean)}
 * method afterwards, else they may not appear at all or the stack may be
 * displayed incorrectly.</p>
 *
 * <p>Although a stack panel is a container it doesn't make sense to set a
 * layout on it because the contained components are laid out in stacks
 * automatically. Setting a layout may cause unpredictable results.</p>
 *
 * @author eso
 */
public class StackPanel extends SwitchPanel
{
	//~ Constructors -----------------------------------------------------------

	/***************************************
	 * Creates a new instance.
	 *
	 * @param rParent The parent container
	 * @param rStyle  The panel style
	 */
	public StackPanel(Container rParent, StyleData rStyle)
	{
		super(EWT.getLayoutFactory()
			  .createLayout(rParent, rStyle, LayoutType.STACK));
	}

	//~ Methods ----------------------------------------------------------------

	/***************************************
	 * {@inheritDoc}
	 */
	@Override
	ComponentEventDispatcher createEventDispatcher()
	{
		return new StackPanelEventDispatcher();
	}

	//~ Inner Classes ----------------------------------------------------------

	/********************************************************************
	 * The default layout for this panel.
	 *
	 * @author eso
	 */
	public static class StackPanelLayout extends SwitchPanelLayout
	{
		//~ Instance fields ----------------------------------------------------

		private StackLayoutPanel     aStackLayoutPanel;
		private UserInterfaceContext rContext;

		//~ Methods ------------------------------------------------------------

		/***************************************
		 * {@inheritDoc}
		 */
		@Override
		public void addPage(Component rGroupComponent,
							String    sGroupTitle,
							boolean   bCloseable)
		{
			Widget rWidget = rGroupComponent.getWidget();
			String sHeader = createHeader(rContext, sGroupTitle);

			aStackLayoutPanel.add(rWidget, sHeader, true, 2);

			if (aStackLayoutPanel.getWidgetCount() == 1)
			{
				aStackLayoutPanel.showWidget(0);
			}
		}

		/***************************************
		 * {@inheritDoc}
		 */
		@Override
		public HasWidgets createLayoutContainer(
			Container rContainer,
			StyleData rStyle)
		{
			this.rContext     = rContainer.getContext();
			aStackLayoutPanel = new StackLayoutPanel(Unit.EM);

			return aStackLayoutPanel;
		}

		/***************************************
		 * {@inheritDoc}
		 */
		@Override
		public int getPageCount()
		{
			return aStackLayoutPanel.getWidgetCount();
		}

		/***************************************
		 * {@inheritDoc}
		 */
		@Override
		public int getPageIndex(Component rGroupComponent)
		{
			return aStackLayoutPanel.getWidgetIndex(rGroupComponent
													.getWidget());
		}

		/***************************************
		 * {@inheritDoc}
		 */
		@Override
		public int getSelectionIndex()
		{
			return aStackLayoutPanel.getVisibleIndex();
		}

		/***************************************
		 * {@inheritDoc}
		 */
		@Override
		public void setPageTitle(int nIndex, String sTitle)
		{
			aStackLayoutPanel.setHeaderHTML(nIndex,
											createHeader(rContext, sTitle));
		}

		/***************************************
		 * {@inheritDoc}
		 */
		@Override
		public void setSelection(int nIndex)
		{
			aStackLayoutPanel.showWidget(nIndex);
		}

		/***************************************
		 * Creates the HTML string for the header of a switch panel element.
		 *
		 * @param  rContext     The user interface context for resource lookups
		 * @param  sHeaderTitle The header title
		 *
		 * @return The HTML string for the header
		 */
		private String createHeader(
			UserInterfaceContext rContext,
			String				 sHeaderTitle)
		{
			String   sTitle = rContext.expandResource(sHeaderTitle);
			ImageRef rImage =
				(ImageRef) rContext.createImage(GewtResources.INSTANCE
												.imRight());

			String sTitleHtml =
				createImageLabel(sTitle,
								 rImage,
								 AlignedPosition.RIGHT,
								 HasHorizontalAlignment.ALIGN_LEFT,
								 null);

			return sTitleHtml;
		}
	}

	/********************************************************************
	 * Dispatcher for tab panel-specific events.
	 *
	 * @author eso
	 */
	class StackPanelEventDispatcher extends ComponentEventDispatcher
		implements SelectionHandler<Integer>, SelectionChangeEvent.Handler
	{
		//~ Methods ------------------------------------------------------------

		/***************************************
		 * @see SelectionHandler#onSelection(SelectionEvent)
		 */
		@Override
		public void onSelection(SelectionEvent<Integer> rEvent)
		{
			Timer aAnimationWaitTimer =
				new Timer()
				{
					@Override
					public void run()
					{
						handleSelection();
					}
				};

			// event needs to be postponed until the stack open animation
			// has finished to prevent update problems in child widgets
			aAnimationWaitTimer.schedule(((StackLayoutPanel) getWidget())
										 .getAnimationDuration() + 250);
		}

		/***************************************
		 * {@inheritDoc}
		 */
		@Override
		public void onSelectionChange(SelectionChangeEvent rEvent)
		{
			handleSelection();
		}

		/***************************************
		 * {@inheritDoc}
		 */
		@Override
		@SuppressWarnings("unchecked")
		protected HandlerRegistration initEventDispatching(
			Widget    rWidget,
			EventType eEventType)
		{
			HandlerRegistration rHandler = null;

			if (eEventType == EventType.SELECTION)
			{
				if (rWidget instanceof HasSelectionHandlers)
				{
					rHandler =
						((HasSelectionHandlers<Integer>) rWidget)
						.addSelectionHandler(this);
				}
				else if (rWidget instanceof HasSelectionChangedHandlers)
				{
					rHandler =
						((HasSelectionChangedHandlers) rWidget)
						.addSelectionChangeHandler(this);
				}
			}

			if (rHandler == null)
			{
				rHandler = super.initEventDispatching(rWidget, eEventType);
			}

			return rHandler;
		}

		/***************************************
		 * Performs the deferred handling of selection events. Invoked by the
		 * {@link #onSelection(SelectionEvent)} method.
		 */
		void handleSelection()
		{
			int nSelection = getSelectionIndex();

			if (nSelection >= 0)
			{
				Component rComponent = getComponents().get(nSelection);

				final Widget rWidget = rComponent.getWidget();

				if (rWidget instanceof RequiresResize)
				{
					Scheduler.get()
							 .scheduleDeferred(new ScheduledCommand()
						{
							@Override
							public void execute()
							{
								((RequiresResize) rWidget).onResize();
							}
						});
				}
			}

			notifyEventHandler(EventType.SELECTION);
		}
	}
}
