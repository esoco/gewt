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
package de.esoco.ewt.impl.gwt;

import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.impl.FocusImpl;

/**
 * A image subclass that displays different image resources depending on the
 * image state.
 *
 * @author eso
 */
public class GwtImageButton extends Image implements Focusable, HasEnabled {

	private static final FocusImpl focusImpl =
		FocusImpl.getFocusImplForWidget();

	private final ImageResource defaultImage;

	private final ImageResource pressedImage;

	private final ImageResource hoverImage;

	private final ImageResource disabledImage;

	/**
	 * Creates a new instance.
	 *
	 * @param defaultImage  The default image
	 * @param pressedImage  The image to be displayed if the mouse button is
	 *                      pressed on the image
	 * @param hoverImage    pressedImage The image to be displayed if the mouse
	 *                      hovers over the image
	 * @param disabledImage The image to be displayed if the image is disabled
	 */
	public GwtImageButton(ImageResource defaultImage,
		ImageResource pressedImage, ImageResource hoverImage,
		ImageResource disabledImage) {
		super(defaultImage);

		this.defaultImage = defaultImage;
		this.pressedImage = pressedImage;
		this.hoverImage = hoverImage;
		this.disabledImage = disabledImage;

		addMouseDownHandler(new MouseDownHandler() {
			@Override
			public void onMouseDown(MouseDownEvent event) {
				if (isEnabled()) {
					setResource(GwtImageButton.this.pressedImage);
				}
			}
		});

		addMouseOverHandler(new MouseOverHandler() {
			@Override
			public void onMouseOver(MouseOverEvent event) {
				if (isEnabled()) {
					setResource(GwtImageButton.this.pressedImage);
				}
			}
		});

		addMouseOutHandler(new MouseOutHandler() {
			@Override
			public void onMouseOut(MouseOutEvent event) {
				if (isEnabled()) {
					setResource(GwtImageButton.this.defaultImage);
				}
			}
		});

		addMouseUpHandler(new MouseUpHandler() {
			@Override
			public void onMouseUp(MouseUpEvent event) {
				if (isEnabled()) {
					setResource(GwtImageButton.this.hoverImage);
				}
			}
		});
	}

	/**
	 * @see Focusable#setAccessKey(char)
	 */
	@Override
	public int getTabIndex() {
		return focusImpl.getTabIndex(getElement());
	}

	/**
	 * @see HasEnabled#isEnabled()
	 */
	@Override
	public boolean isEnabled() {
		return !getElement().getPropertyBoolean("disabled");
	}

	/**
	 * @see Focusable#setAccessKey(char)
	 */
	@Override
	public void setAccessKey(char key) {
		getElement().setPropertyString("accessKey", "" + key);
	}

	/**
	 * @see HasEnabled#setEnabled(boolean)
	 */
	@Override
	public void setEnabled(boolean enabled) {
		getElement().setPropertyBoolean("disabled", !enabled);

		setResource(enabled ? defaultImage : disabledImage);
	}

	/**
	 * @see Focusable#setFocus(boolean)
	 */
	@Override
	public void setFocus(boolean focused) {
		if (focused) {
			focusImpl.focus(getElement());
		} else {
			focusImpl.blur(getElement());
		}
	}

	/**
	 * @see Focusable#setTabIndex(int)
	 */
	@Override
	public void setTabIndex(int index) {
		focusImpl.setTabIndex(getElement(), index);
	}

	/**
	 * @see Image#onAttach()
	 */
	@Override
	protected void onAttach() {
		super.onAttach();

		// from FocusWidget
		int tabIndex = getTabIndex();

		if (tabIndex == -1) {
			setTabIndex(0);
		}
	}
}
