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
import de.esoco.ewt.event.EventType;
import de.esoco.ewt.impl.gwt.GwtChildView;
import de.esoco.ewt.style.ViewStyle;

import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.HasCloseHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * A view that is a child of the {@link MainView}.
 *
 * @author eso
 */
public class ChildView extends View {

	/**
	 * Creates a new instance.
	 *
	 * @param rParent The parent view
	 * @param rStyle  The style of the child view
	 */
	public ChildView(View rParent, ViewStyle rStyle) {
		this(rParent,
			EWT.getChildViewFactory().createChildViewWidget(rParent, rStyle),
			rStyle);
	}

	/**
	 * Creates a new instance.
	 *
	 * @param rParent     The parent view
	 * @param rViewWidget The widget for this view
	 * @param rStyle      The style of the child view
	 */
	ChildView(View rParent, IsChildViewWidget rViewWidget, ViewStyle rStyle) {
		super(rParent.getContext(), rViewWidget, rStyle);

		getWidget().addStyleName(EWT.CSS.ewtChildView());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isVisible() {
		return getChildViewWidget().isShown();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setVisible(boolean bVisible) {
		IsChildViewWidget rPanel = getChildViewWidget();

		if (bVisible) {
			rPanel.show();
		} else {
			rPanel.hide();
		}
	}

	/**
	 * Internal method to return the GWT {@link PopupPanel} of this instance.
	 *
	 * @return The popup panel
	 */
	protected IsChildViewWidget getChildViewWidget() {
		return (IsChildViewWidget) getWidget();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	ComponentEventDispatcher createEventDispatcher() {
		return new ChildViewEventDispatcher();
	}

	/**
	 * The interface to be implemented by child view widgets.
	 *
	 * @author eso
	 */
	public static interface IsChildViewWidget extends IsWidget, HasWidgets {

		/**
		 * Hides the child view.
		 */
		public void hide();

		/**
		 * Checks whether the child view is currently displayed.
		 *
		 * @return TRUE if the child view is displayed
		 */
		public boolean isShown();

		/**
		 * Sets the view title.
		 *
		 * @param sTitle The title text
		 */
		public void setViewTitle(String sTitle);

		/**
		 * Shows the child view.
		 */
		public void show();
	}

	/**
	 * Interface and implementation of a factory for the main panel of a child
	 * view. Can be overridden by subclasses that define different display
	 * types
	 * for child views.
	 *
	 * @author eso
	 */
	public static class ChildViewFactory {

		/**
		 * Creates a GWT child view implementation.
		 *
		 * @param rParent The parent of the new child view
		 * @param rStyle  The child view style
		 * @return The child view widget
		 */
		public IsChildViewWidget createChildViewWidget(View rParent,
			ViewStyle rStyle) {
			boolean bAutoHide = rStyle.hasFlag(ViewStyle.Flag.AUTO_HIDE);
			boolean bModal = rStyle.hasFlag(ViewStyle.Flag.MODAL);

			return new GwtChildView(bAutoHide, bModal);
		}
	}

	/**
	 * Dispatcher for view events.
	 *
	 * @author eso
	 */
	class ChildViewEventDispatcher extends ComponentEventDispatcher
		implements CloseHandler<Widget> {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void onClose(CloseEvent<Widget> rEvent) {
			notifyEventHandler(EventType.VIEW_CLOSING);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		@SuppressWarnings({ "unchecked", "rawtypes" })
		protected HandlerRegistration initEventDispatching(Widget rWidget,
			EventType eEventType) {
			if (eEventType == EventType.VIEW_CLOSING &&
				rWidget instanceof HasCloseHandlers) {
				return ((HasCloseHandlers) rWidget).addCloseHandler(this);
			} else {
				return super.initEventDispatching(rWidget, eEventType);
			}
		}
	}
}
