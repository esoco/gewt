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
import de.esoco.ewt.style.ViewStyle;

import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.HasCloseHandlers;
import com.google.gwt.user.client.ui.DecoratedPopupPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;


/********************************************************************
 * A view that is a child of the {@link MainView}.
 *
 * @author eso
 */
public class ChildView extends View
{
	//~ Instance fields --------------------------------------------------------

	private final UserInterfaceContext rContext;

	//~ Constructors -----------------------------------------------------------

	/***************************************
	 * Creates a new instance.
	 *
	 * @param rContext The user interface context this dialog view belongs to
	 * @param rStyle   The style of the view
	 */
	public ChildView(UserInterfaceContext rContext, ViewStyle rStyle)
	{
		this(rContext, createChildViewPanel(rStyle));
	}

	/***************************************
	 * Constructor for subclasses to create a new instance for a certain popup
	 * panel.
	 *
	 * @param rContext   The user interface context this dialog view belongs to
	 * @param rViewPanel The popup panel of this view
	 */
	protected ChildView(UserInterfaceContext rContext, PopupPanel rViewPanel)
	{
		super(rViewPanel);

		this.rContext = rContext;
	}

	//~ Static methods ---------------------------------------------------------

	/***************************************
	 * Creates the GWT popup panel for this instance.
	 *
	 * @param  rStyle The view style
	 *
	 * @return The popup panel
	 */
	private static PopupPanel createChildViewPanel(ViewStyle rStyle)
	{
		boolean    bAutoHide  = rStyle.hasFlag(ViewStyle.Flag.AUTO_HIDE);
		boolean    bModal     = rStyle.hasFlag(ViewStyle.Flag.MODAL);
		PopupPanel aViewPanel;

		if (rStyle.hasFlag(ViewStyle.Flag.UNDECORATED))
		{
			aViewPanel = new PopupPanel(bAutoHide, bModal);
		}
		else
		{
			aViewPanel = new DecoratedPopupPanel(bAutoHide, bModal);
		}

		return aViewPanel;
	}

	//~ Methods ----------------------------------------------------------------

	/***************************************
	 * {@inheritDoc}
	 */
	@Override
	public UserInterfaceContext getContext()
	{
		return rContext;
	}

	/***************************************
	 * {@inheritDoc}
	 */
	@Override
	public boolean isVisible()
	{
		return getPopupPanel().isShowing();
	}

	/***************************************
	 * Sets the position of this view.
	 *
	 * @param x The horizontal coordinate
	 * @param y The vertical coordinate
	 */
	public void setLocation(int x, int y)
	{
		getPopupPanel().setPopupPosition(x, y);
	}

	/***************************************
	 * {@inheritDoc}
	 */
	@Override
	public void setVisible(boolean bVisible)
	{
		PopupPanel rPopupPanel = getPopupPanel();

		if (bVisible)
		{
			rPopupPanel.show();
		}
		else
		{
			rPopupPanel.hide();
		}
	}

	/***************************************
	 * Internal method to return the GWT {@link PopupPanel} of this instance.
	 *
	 * @return The popup panel
	 */
	protected PopupPanel getPopupPanel()
	{
		return (PopupPanel) getWidget();
	}

	/***************************************
	 * {@inheritDoc}
	 */
	@Override
	ComponentEventDispatcher createEventDispatcher()
	{
		return new ChildViewEventDispatcher();
	}

	//~ Inner Classes ----------------------------------------------------------

	/********************************************************************
	 * Dispatcher for view events.
	 *
	 * @author eso
	 */
	class ChildViewEventDispatcher extends ComponentEventDispatcher
		implements CloseHandler<PopupPanel>
	{
		//~ Methods ------------------------------------------------------------

		/***************************************
		 * {@inheritDoc}
		 */
		@Override
		public void onClose(CloseEvent<PopupPanel> rEvent)
		{
			notifyEventHandler(EventType.VIEW_CLOSING);
		}

		/***************************************
		 * {@inheritDoc}
		 */
		@Override
		@SuppressWarnings("unchecked")
		void initEventDispatching(Widget rWidget)
		{
			super.initEventDispatching(rWidget);

			((HasCloseHandlers<PopupPanel>) rWidget).addCloseHandler(this);
		}
	}
}
