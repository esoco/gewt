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
package de.esoco.ewt.impl.gwt;

import de.esoco.ewt.component.ChildView.IsChildViewWidget;

import com.google.gwt.user.client.ui.DecoratedPopupPanel;
import com.google.gwt.user.client.ui.DialogBox;


/********************************************************************
 * Default GWT child view implementation.
 *
 * @author eso
 */
public class GwtChildView extends DialogBox implements IsChildViewWidget
{
	//~ Constructors -----------------------------------------------------------

	/***************************************
	 * @see DecoratedPopupPanel#DecoratedPopupPanel(boolean, boolean)
	 */
	public GwtChildView(boolean bAutoHide, boolean bModal)
	{
		super(bAutoHide, bModal);
	}

	//~ Methods ----------------------------------------------------------------

	/***************************************
	 * {@inheritDoc}
	 */
	@Override
	public boolean isShown()
	{
		return isShowing();
	}

	/***************************************
	 * {@inheritDoc}
	 */
	@Override
	public void setViewTitle(String sTitle)
	{
		getCaption().asWidget().setVisible(sTitle != null);

		setText(sTitle != null ? sTitle : "");
	}
}
