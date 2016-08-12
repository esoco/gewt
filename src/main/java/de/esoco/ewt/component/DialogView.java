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
import de.esoco.ewt.style.ViewStyle;

import com.google.gwt.user.client.ui.PopupPanel;


/********************************************************************
 * A child view subclass that represents dialogs.
 *
 * @author eso
 */
public class DialogView extends ChildView
{
	//~ Constructors -----------------------------------------------------------

	/***************************************
	 * Creates a new instance.
	 *
	 * @param rParent rContext The parent view
	 * @param rStyle  The style of the dialog
	 */
	public DialogView(View rParent, ViewStyle rStyle)
	{
		super(rParent,
			  EWT.getChildViewFactory().createDialogWidget(rParent, rStyle),
			  rStyle);

		getWidget().addStyleName(EWT.CSS.ewtDialogView());
	}

	//~ Methods ----------------------------------------------------------------

	/***************************************
	 * Sets the dialog title.
	 *
	 * @param sTitle The new title
	 */
	@Override
	public void setTitle(String sTitle)
	{
		getChildViewWidget().setViewTitle(getContext().expandResource(sTitle));
	}

	/***************************************
	 * @see Component#setVisible(boolean)
	 */
	@Override
	public void setVisible(boolean bVisible)
	{
		if (bVisible && getViewStyle().hasFlag(ViewStyle.Flag.MODAL))
		{
			IsChildViewWidget rPanel = getChildViewWidget();

			if (rPanel instanceof PopupPanel)
			{
				((PopupPanel) rPanel).setGlassEnabled(true);
			}
		}

		super.setVisible(bVisible);
	}
}
