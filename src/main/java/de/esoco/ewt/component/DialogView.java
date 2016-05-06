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
import de.esoco.ewt.style.ViewStyle;

import com.google.gwt.user.client.ui.DialogBox;
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
	 * @param rContext The user interface context this dialog view belongs to
	 * @param rStyle   The style of the view
	 */
	public DialogView(UserInterfaceContext rContext, ViewStyle rStyle)
	{
		super(rContext, createDialogBox(rStyle), rStyle);
	}

	//~ Static methods ---------------------------------------------------------

	/***************************************
	 * Creates the GWT dialog for this instance.
	 *
	 * @param  rStyle The view style
	 *
	 * @return The dialog
	 */
	private static DialogBox createDialogBox(ViewStyle rStyle)
	{
		boolean bAutoHide = rStyle.hasFlag(ViewStyle.Flag.AUTO_HIDE);
		boolean bModal    = rStyle.hasFlag(ViewStyle.Flag.MODAL);

		return new DialogBox(bAutoHide, bModal);
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
		PopupPanel rPopupPanel = getPopupPanel();

		if (rPopupPanel instanceof DialogBox)
		{
			((DialogBox) rPopupPanel).setText(getContext().expandResource(sTitle));
		}
	}

	/***************************************
	 * @see Component#setVisible(boolean)
	 */
	@Override
	public void setVisible(boolean bVisible)
	{
		PopupPanel rPopupPanel = getPopupPanel();

		if (bVisible && getViewStyle().hasFlag(ViewStyle.Flag.MODAL))
		{
			rPopupPanel.setGlassEnabled(true);
		}

		super.setVisible(bVisible);
	}
}
