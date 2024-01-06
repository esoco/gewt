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
import de.esoco.ewt.impl.gwt.GewtResources;
import de.esoco.ewt.layout.FillLayout;
import de.esoco.ewt.layout.GenericLayout;
import de.esoco.ewt.style.StyleData;
import de.esoco.ewt.style.ViewStyle;

import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * A GWT-specific main view implementation that wraps a root panel.
 *
 * @author eso
 */
public class MainView extends View {

	/**
	 * Creates a new instance that wraps a certain root panel.
	 *
	 * @param rContext The user interface context this view belongs to
	 * @param rStyle   The view style
	 */
	public MainView(UserInterfaceContext rContext, ViewStyle rStyle) {
		super(rContext, rStyle);

		setLayout(new MainViewLayout());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public View getView() {
		return this;
	}

	/**
	 * @see Container#setLayout(GenericLayout)
	 */
	@Override
	public void setLayout(GenericLayout rLayout) {
		boolean bFullSize = getViewStyle().hasFlag(ViewStyle.Flag.FULL_SIZE);
		Widget rWidget = getWidget();

		Panel rRootPanel = bFullSize ? RootLayoutPanel.get() : RootPanel.get();

		if (rWidget != null) {
			rRootPanel.remove(rWidget);
		}

		super.setLayout(rLayout);
		setWidget(createWidget(StyleData.DEFAULT));

		rWidget = getWidget();
		setDefaultStyleName(GewtResources.INSTANCE.css().ewtMainView());
		rRootPanel.add(rWidget);
	}

	/**
	 * The default layout for the {@link MainView} of an application. This is
	 * just a sub-class of {@link FillLayout} that can be detected by layout
	 * factories if necessary.
	 *
	 * @author eso
	 */
	public static class MainViewLayout extends FillLayout {
	}
}
