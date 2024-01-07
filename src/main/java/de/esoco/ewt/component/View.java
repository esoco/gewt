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

import de.esoco.ewt.UserInterfaceContext;
import de.esoco.ewt.style.ViewStyle;

import de.esoco.lib.property.TitleAttribute;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * The base class for all EWT views.
 *
 * @author eso
 */
public abstract class View extends Container implements TitleAttribute {

	private final UserInterfaceContext context;

	private final ViewStyle viewStyle;

	/**
	 * Creates a new instance.
	 *
	 * @param context   The user interface context
	 * @param viewPanel The panel to place the view component in
	 * @param style     The view style
	 */
	public View(UserInterfaceContext context, IsWidget viewPanel,
		ViewStyle style) {
		this(context, style);

		setWidget(viewPanel);
	}

	/**
	 * Creates a new instance.
	 *
	 * @param context The user interface context
	 * @param style   viewStyle The view style
	 */
	View(UserInterfaceContext context, ViewStyle style) {
		this.context = context;
		this.viewStyle = style;
	}

	/**
	 * Overridden to return the actual UI context.
	 *
	 * @see Container#getContext()
	 */
	@Override
	public UserInterfaceContext getContext() {
		return context;
	}

	/**
	 * Returns the view title.
	 *
	 * @return The title string
	 */
	@Override
	public String getTitle() {
		return Window.getTitle();
	}

	/**
	 * Returns the style of this view.
	 *
	 * @return The view style
	 */
	public final ViewStyle getViewStyle() {
		return viewStyle;
	}

	/**
	 * Packs this view for display on the screen. This method exists mainly for
	 * compatibility with other EWT implementations and has no function on GWT.
	 */
	public void pack() {
	}

	/**
	 * Sets the view title. Should be overridden by view implementations that
	 * can display a view title.
	 *
	 * @param title The new title
	 */
	@Override
	public void setTitle(String title) {
	}
}
