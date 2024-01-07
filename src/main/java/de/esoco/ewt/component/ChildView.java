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
	 * @param parent The parent view
	 * @param style  The style of the child view
	 */
	public ChildView(View parent, ViewStyle style) {
		this(parent,
			EWT.getChildViewFactory().createChildViewWidget(parent, style),
			style);
	}

	/**
	 * Creates a new instance.
	 *
	 * @param parent     The parent view
	 * @param viewWidget The widget for this view
	 * @param style      The style of the child view
	 */
	ChildView(View parent, IsChildViewWidget viewWidget, ViewStyle style) {
		super(parent.getContext(), viewWidget, style);

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
	public void setVisible(boolean visible) {
		IsChildViewWidget panel = getChildViewWidget();

		if (visible) {
			panel.show();
		} else {
			panel.hide();
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
		 * @param title The title text
		 */
		public void setViewTitle(String title);

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
		 * @param parent The parent of the new child view
		 * @param style  The child view style
		 * @return The child view widget
		 */
		public IsChildViewWidget createChildViewWidget(View parent,
			ViewStyle style) {
			boolean autoHide = style.hasFlag(ViewStyle.Flag.AUTO_HIDE);
			boolean modal = style.hasFlag(ViewStyle.Flag.MODAL);

			return new GwtChildView(autoHide, modal);
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
		public void onClose(CloseEvent<Widget> event) {
			notifyEventHandler(EventType.VIEW_CLOSING);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		@SuppressWarnings({ "unchecked", "rawtypes" })
		protected HandlerRegistration initEventDispatching(Widget widget,
			EventType eventType) {
			if (eventType == EventType.VIEW_CLOSING &&
				widget instanceof HasCloseHandlers) {
				return ((HasCloseHandlers) widget).addCloseHandler(this);
			} else {
				return super.initEventDispatching(widget, eventType);
			}
		}
	}
}
