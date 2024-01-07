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

import de.esoco.ewt.event.EventType;
import de.esoco.ewt.graphics.ImageRef;
import de.esoco.ewt.graphics.Image;
import de.esoco.ewt.impl.gwt.WidgetFactory;
import de.esoco.ewt.property.ImageAttribute;
import de.esoco.ewt.style.AlignedPosition;
import de.esoco.ewt.style.StyleData;
import de.esoco.ewt.style.StyleFlag;

import de.esoco.lib.property.ButtonStyle;
import de.esoco.lib.property.TextAttribute;

import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HasHTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.Widget;

import static de.esoco.lib.property.StyleProperties.BUTTON_STYLE;

/**
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
public class Button extends Control implements TextAttribute, ImageAttribute {

	private String text;

	private Image image;

	private AlignedPosition textPosition = AlignedPosition.RIGHT;

	/**
	 * @see Control#applyStyle(StyleData)
	 */
	@Override
	public void applyStyle(StyleData style) {
		super.applyStyle(style);

		textPosition = getTextPosition(style);
	}

	/**
	 * @see ImageAttribute#getImage()
	 */
	@Override
	public Image getImage() {
		return image;
	}

	/**
	 * Returns the button text.
	 *
	 * @return The button text
	 */
	@Override
	public String getText() {
		return text;
	}

	/**
	 * @see ImageAttribute#setImage(Image)
	 */
	@Override
	public void setImage(Image image) {
		this.image = image;

		HasText widget = (HasText) getWidget();

		if (widget instanceof ImageAttribute) {
			((ImageAttribute) widget).setImage(image);
		} else {
			setButtonImage(widget, getText(), image, textPosition);
		}
	}

	/**
	 * Sets the button text.
	 *
	 * @param text The new button text
	 */
	@Override
	public void setText(String text) {
		text = getContext().expandResource(text);
		this.text = text;

		HasText widget = (HasText) getWidget();

		widget.setText(text);

		if (widget instanceof PushButton) {
			// GWT bug: text of other states will not be set sometimes
			PushButton pushButton = (PushButton) widget;

			pushButton.getUpFace().setText(text);
			pushButton.getDownFace().setText(text);
			pushButton.getUpHoveringFace().setText(text);
			pushButton.getDownHoveringFace().setText(text);
			pushButton.getUpDisabledFace().setText(text);
			pushButton.getDownDisabledFace().setText(text);
		}
	}

	/**
	 * Sets an image and/or text on a button widget.
	 *
	 * @param widget       The button widget
	 * @param text         The button text (NULL or empty for none)
	 * @param image        The button image
	 * @param textPosition The position of the text relative to the image
	 */
	void setButtonImage(HasText widget, String text, Image image,
		AlignedPosition textPosition) {
		if (image instanceof ImageRef) {
			ImageRef bitmap = (ImageRef) image;

			if (widget instanceof PushButton &&
				(text == null || text.length() == 0)) {
				com.google.gwt.user.client.ui.Image gwtImage =
					bitmap.getGwtImage();

				PushButton pushButton = (PushButton) widget;

				pushButton.getUpFace().setImage(gwtImage);
				pushButton.getUpDisabledFace().setImage(gwtImage);
				pushButton.getUpHoveringFace().setImage(gwtImage);
				pushButton.getDownFace().setImage(gwtImage);
				pushButton.getDownDisabledFace().setImage(gwtImage);
				pushButton.getDownHoveringFace().setImage(gwtImage);
			} else {
				String imageLabel = createImageLabel(text, bitmap,
					textPosition,
					HasHorizontalAlignment.ALIGN_CENTER, "100%");

				if (widget instanceof PushButton) {
					PushButton pushButton = (PushButton) widget;

					pushButton.getUpFace().setHTML(imageLabel);
					pushButton.getUpDisabledFace().setHTML(imageLabel);
					pushButton.getUpHoveringFace().setHTML(imageLabel);
					pushButton.getDownFace().setHTML(imageLabel);
					pushButton.getDownDisabledFace().setHTML(imageLabel);
					pushButton.getDownHoveringFace().setHTML(imageLabel);
				} else if (widget instanceof HasHTML) {
					((HasHTML) widget).setHTML(imageLabel);
				}
			}
		} else {
			widget.setText(text);
		}
	}

	/**
	 * The {@link WidgetFactory} for button widgets.
	 *
	 * @author eso
	 */
	public static class ButtonWidgetFactory<W extends Widget & Focusable & HasText>
		implements WidgetFactory<W> {

		/**
		 * {@inheritDoc}
		 */
		@Override
		@SuppressWarnings("unchecked")
		public W createWidget(Component component, StyleData style) {
			ButtonStyle buttonStyle =
				style.getProperty(BUTTON_STYLE, ButtonStyle.DEFAULT);

			IsWidget buttonWidget;

			if (style.hasFlag(StyleFlag.HYPERLINK) ||
				buttonStyle == ButtonStyle.LINK) {
				buttonWidget = new Anchor();
			} else {
				buttonWidget = new PushButton();
			}

			return (W) buttonWidget;
		}
	}
}
