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
import de.esoco.ewt.UserInterfaceContext;
import de.esoco.ewt.event.EventType;
import de.esoco.ewt.graphics.Image;
import de.esoco.ewt.impl.gwt.WidgetFactory;
import de.esoco.ewt.property.ImageAttribute;
import de.esoco.ewt.style.AlignedPosition;
import de.esoco.ewt.style.StyleData;
import de.esoco.ewt.style.StyleFlag;

import de.esoco.lib.property.TextAttribute;

import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HasHTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.Widget;


/********************************************************************
 * A button object that sends action events on selection.
 *
 * <p>Supported event types:</p>
 *
 * <ul>
 *   <li>{@link EventType#ACTION ACTION}: when the button is pressed</li>
 * </ul>
 *
 * @author eso
 */
public class Button extends Control implements TextAttribute, ImageAttribute
{
	//~ Static fields/initializers ---------------------------------------------

	static
	{
		EWT.registerWidgetFactory(Button.class,
										   new ButtonWidgetFactory<>(),
										   false);
	}

	//~ Instance fields --------------------------------------------------------

	private String sText;
	private Image  rImage;

	private AlignedPosition rTextPosition = AlignedPosition.RIGHT;

	//~ Methods ----------------------------------------------------------------

	/***************************************
	 * @see Control#applyStyle(StyleData)
	 */
	@Override
	public void applyStyle(StyleData rStyle)
	{
		super.applyStyle(rStyle);

		rTextPosition = getTextPosition(rStyle);
	}

	/***************************************
	 * @see ImageAttribute#getImage()
	 */
	@Override
	public Image getImage()
	{
		return rImage;
	}

	/***************************************
	 * Returns the button text.
	 *
	 * @return The button text
	 */
	@Override
	public String getText()
	{
		return sText;
	}

	/***************************************
	 * @see ImageAttribute#setImage(Image)
	 */
	@Override
	public void setImage(Image rImage)
	{
		this.rImage = rImage;

		HasText rWidget = (HasText) getWidget();
		String  sText   = getText();

		if (rImage != null)
		{
			if (rWidget instanceof PushButton &&
				(sText == null || sText.length() == 0))
			{
				com.google.gwt.user.client.ui.Image rGwtImage =
					rImage.getGwtImage();

				PushButton rPushButton = (PushButton) rWidget;

				rPushButton.getUpFace().setImage(rGwtImage);
				rPushButton.getUpDisabledFace().setImage(rGwtImage);
				rPushButton.getUpHoveringFace().setImage(rGwtImage);
				rPushButton.getDownFace().setImage(rGwtImage);
				rPushButton.getDownDisabledFace().setImage(rGwtImage);
				rPushButton.getDownHoveringFace().setImage(rGwtImage);
			}
			else
			{
				String sImageLabel =
					createImageLabel(sText,
									 rImage,
									 rTextPosition,
									 HasHorizontalAlignment.ALIGN_CENTER,
									 "100%");

				if (rWidget instanceof PushButton)
				{
					PushButton rPushButton = (PushButton) rWidget;

					rPushButton.getUpFace().setHTML(sImageLabel);
					rPushButton.getUpDisabledFace().setHTML(sImageLabel);
					rPushButton.getUpHoveringFace().setHTML(sImageLabel);
					rPushButton.getDownFace().setHTML(sImageLabel);
					rPushButton.getDownDisabledFace().setHTML(sImageLabel);
					rPushButton.getDownHoveringFace().setHTML(sImageLabel);
				}
				else if (rWidget instanceof HasHTML)
				{
					((HasHTML) rWidget).setHTML(sImageLabel);
				}
			}
		}
		else
		{
			rWidget.setText(sText);
		}
	}

	/***************************************
	 * Sets the button text.
	 *
	 * @param sText The new button text
	 */
	@Override
	public void setText(String sText)
	{
		sText	   = getContext().expandResource(sText);
		this.sText = sText;

		HasText rWidget = (HasText) getWidget();

		rWidget.setText(sText);

		if (rWidget instanceof PushButton)
		{
			// GWT bug: text of other states will not be set sometimes
			PushButton rPushButton = (PushButton) rWidget;

			rPushButton.getUpFace().setText(sText);
			rPushButton.getDownFace().setText(sText);
			rPushButton.getUpHoveringFace().setText(sText);
			rPushButton.getDownHoveringFace().setText(sText);
			rPushButton.getUpDisabledFace().setText(sText);
			rPushButton.getDownDisabledFace().setText(sText);
		}
	}

	//~ Inner Classes ----------------------------------------------------------

	/********************************************************************
	 * The {@link WidgetFactory} for button widgets.
	 *
	 * @author eso
	 */
	public static class ButtonWidgetFactory<W extends Widget & Focusable & HasText>
		implements WidgetFactory<W>
	{
		//~ Methods ------------------------------------------------------------

		/***************************************
		 * {@inheritDoc}
		 */
		@Override
		@SuppressWarnings("unchecked")
		public W createWidget(UserInterfaceContext rContext, StyleData rStyle)
		{
			W aWidget;

			if (rStyle.hasFlag(StyleFlag.HYPERLINK))
			{
				aWidget = (W) new Anchor();
			}
			else
			{
				aWidget = (W) new PushButton();
			}

			return aWidget;
		}
	}
}
