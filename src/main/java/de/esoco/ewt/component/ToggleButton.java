//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// This file is a part of the 'gewt' project.
// Copyright 2015 Elmar Sonnenschein, esoco GmbH, Flensburg, Germany
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

import de.esoco.ewt.graphics.Image;


/********************************************************************
 * A button component that can be toggled between two visual states.
 *
 * @author eso
 */
public class ToggleButton extends SelectableButton
{
	/***************************************
	 * @see
	 */
	public ToggleButton()
	{
		super(new com.google.gwt.user.client.ui.ToggleButton());
	}

	/***************************************
	 * @see SelectableButton#isSelected()
	 */
	@Override
	public boolean isSelected()
	{
		return ((com.google.gwt.user.client.ui.ToggleButton) getWidget())
			   .isDown();
	}

	/***************************************
	 * Sets the image for the down/selected state of this button.
	 *
	 * @param rImage The image or NULL for none
	 */
	public void setDownImage(Image rImage)
	{
		com.google.gwt.user.client.ui.ToggleButton rButton =
			(com.google.gwt.user.client.ui.ToggleButton) getWidget();

		if (rImage != null)
		{
			rButton.getDownFace().setImage(rImage.getGwtImage());
		}
		else
		{
			rButton.getDownFace().setText("");
		}
	}

	/***************************************
	 * @see SelectableButton#setSelected(boolean)
	 */
	@Override
	public void setSelected(boolean bSelected)
	{
		((com.google.gwt.user.client.ui.ToggleButton) getWidget()).setDown(bSelected);
	}
}
