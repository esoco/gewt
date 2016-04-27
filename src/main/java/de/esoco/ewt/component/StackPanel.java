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

import de.esoco.ewt.UserInterfaceContext;
import de.esoco.ewt.event.EventType;
import de.esoco.ewt.graphics.Image;
import de.esoco.ewt.impl.gwt.GewtResources;
import de.esoco.ewt.style.AlignedPosition;
import de.esoco.ewt.style.StyleData;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.StackLayoutPanel;
import com.google.gwt.user.client.ui.Widget;


/********************************************************************
 * A panel that can consist of multiple components that are arranged in a
 * vertical stack. Only one of the components will be visible at a time while
 * for the other components only a collapsed title will be displayed. By click
 * on such a title the corresponding component can be made visible.
 *
 * <p>Components that are added to a stack panel must have been created with the
 * panel as their parent, else unpredictable results may occur. They must be
 * added to this panel through the {@link #addGroup(Component, String, boolean)}
 * method afterwards, else they may not appear at all or the stack may be
 * displayed incorrectly.</p>
 *
 * <p>Although a stack panel is a container it doesn't make sense to set a
 * layout on it because the contained components are laid out in stacks
 * automatically. Setting a layout may cause unpredictable results.</p>
 *
 * @author eso
 */
public class StackPanel extends GroupPanel
{
	//~ Constructors -----------------------------------------------------------

	/***************************************
	 * Creates a new instance.
	 */
	public StackPanel()
	{
		super(new StackPanelLayout());
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
	public static class StackPanelLayout extends GroupPanelLayout
	{
		//~ Instance fields ----------------------------------------------------

		private StackLayoutPanel     aStackLayoutPanel;
		private UserInterfaceContext rContext;

		//~ Methods ------------------------------------------------------------

		/***************************************
		 * {@inheritDoc}
		 */
		@Override
		public void addGroup(Component rGroupComponent,
							 String    sGroupTitle,
							 boolean   bCloseable)
		{
			Widget rWidget = rGroupComponent.getWidget();
			String rHeader = createStackHeader(sGroupTitle);

			aStackLayoutPanel.add(rWidget, rHeader, true, 2);

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
			UserInterfaceContext rContext,
			StyleData			 rStyle)
		{
			this.rContext     = rContext;
			aStackLayoutPanel = new StackLayoutPanel(Unit.EM);

			return aStackLayoutPanel;
		}

		/***************************************
		 * {@inheritDoc}
		 */
		@Override
		public int getGroupCount()
		{
			return aStackLayoutPanel.getWidgetCount();
		}

		/***************************************
		 * {@inheritDoc}
		 */
		@Override
		public int getGroupIndex(Component rGroupComponent)
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
		public void setGroupTitle(int nIndex, String sTitle)
		{
			aStackLayoutPanel.setHeaderHTML(nIndex, createStackHeader(sTitle));
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
		 * Creates the HTML for a stack title string.
		 *
		 * @param  sStackTitle The stack title
		 *
		 * @return The HTML string for the stack title
		 */
		protected String createStackHeader(String sStackTitle)
		{
			String sTitle = rContext.expandResource(sStackTitle);
			Image  rImage =
				rContext.createImage(GewtResources.INSTANCE.imRight());

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
		implements SelectionHandler<Integer>
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

		/***************************************
		 * @see ControlEventDispatcher#initEventDispatching(Widget)
		 */
		@Override
		void initEventDispatching(Widget rWidget)
		{
			super.initEventDispatching(rWidget);

			((StackLayoutPanel) rWidget).addSelectionHandler(this);
		}
	}
}
