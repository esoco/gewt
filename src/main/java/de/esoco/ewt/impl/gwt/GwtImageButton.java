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

	private static final FocusImpl rFocusImpl =
		FocusImpl.getFocusImplForWidget();

	private final ImageResource rDefaultImage;

	private final ImageResource rPressedImage;

	private final ImageResource rHoverImage;

	private final ImageResource rDisabledImage;

	/**
	 * Creates a new instance.
	 *
	 * @param rDefaultImage  The default image
	 * @param rPressedImage  The image to be displayed if the mouse button is
	 *                       pressed on the image
	 * @param rHoverImage    rPressedImage The image to be displayed if the
	 *                       mouse hovers over the image
	 * @param rDisabledImage The image to be displayed if the image is disabled
	 */
	public GwtImageButton(ImageResource rDefaultImage,
		ImageResource rPressedImage, ImageResource rHoverImage,
		ImageResource rDisabledImage) {
		super(rDefaultImage);

		this.rDefaultImage = rDefaultImage;
		this.rPressedImage = rPressedImage;
		this.rHoverImage = rHoverImage;
		this.rDisabledImage = rDisabledImage;

		addMouseDownHandler(new MouseDownHandler() {
			@Override
			public void onMouseDown(MouseDownEvent event) {
				if (isEnabled()) {
					setResource(GwtImageButton.this.rPressedImage);
				}
			}
		});

		addMouseOverHandler(new MouseOverHandler() {
			@Override
			public void onMouseOver(MouseOverEvent event) {
				if (isEnabled()) {
					setResource(GwtImageButton.this.rPressedImage);
				}
			}
		});

		addMouseOutHandler(new MouseOutHandler() {
			@Override
			public void onMouseOut(MouseOutEvent event) {
				if (isEnabled()) {
					setResource(GwtImageButton.this.rDefaultImage);
				}
			}
		});

		addMouseUpHandler(new MouseUpHandler() {
			@Override
			public void onMouseUp(MouseUpEvent event) {
				if (isEnabled()) {
					setResource(GwtImageButton.this.rHoverImage);
				}
			}
		});
	}

	/**
	 * @see Focusable#setAccessKey(char)
	 */
	@Override
	public int getTabIndex() {
		return rFocusImpl.getTabIndex(getElement());
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
	public void setEnabled(boolean bEnabled) {
		getElement().setPropertyBoolean("disabled", !bEnabled);

		setResource(bEnabled ? rDefaultImage : rDisabledImage);
	}

	/**
	 * @see Focusable#setFocus(boolean)
	 */
	@Override
	public void setFocus(boolean bFocused) {
		if (bFocused) {
			rFocusImpl.focus(getElement());
		} else {
			rFocusImpl.blur(getElement());
		}
	}

	/**
	 * @see Focusable#setTabIndex(int)
	 */
	@Override
	public void setTabIndex(int nIndex) {
		rFocusImpl.setTabIndex(getElement(), nIndex);
	}

	/**
	 * @see Image#onAttach()
	 */
	@Override
	protected void onAttach() {
		super.onAttach();

		// from FocusWidget
		int nTabIndex = getTabIndex();

		if (nTabIndex == -1) {
			setTabIndex(0);
		}
	}
}
