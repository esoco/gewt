//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// This file is a part of the 'gewt' project.
// Copyright 2018 Elmar Sonnenschein, esoco GmbH, Flensburg, Germany
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
package de.esoco.ewt.graphics;

import de.esoco.ewt.UserInterfaceContext;

import com.google.gwt.resources.client.ImageResource;

/**
 * An image implementation that contains some kind of reference to the actual
 * image data or file. For platform compatibility image instances should not be
 * created by invoking the constructor of this class but by using the factory
 * method {@link UserInterfaceContext#createImage(Object)}.
 *
 * @author eso
 */
public class ImageRef implements Image {

	private Object imageDefinition;

	private com.google.gwt.user.client.ui.Image gwtImage;

	/**
	 * Creates a new instance from an arbitrary image object. The supported
	 * argument types are either a {@link String} with the image name or one of
	 * the GEWT-specific types {@link com.google.gwt.user.client.ui.Image} or
	 * {@link ImageResource}.
	 *
	 * @param imageDefinition The image object
	 * @throws IllegalArgumentException If the argument type is not supported
	 */
	public ImageRef(Object imageDefinition) {
		this.imageDefinition = imageDefinition;

		if (imageDefinition instanceof ImageResource) {
			gwtImage = new com.google.gwt.user.client.ui.Image(
				(ImageResource) imageDefinition);
		} else if (imageDefinition instanceof com.google.gwt.user.client.ui.Image) {
			gwtImage = (com.google.gwt.user.client.ui.Image) imageDefinition;
		} else if (imageDefinition instanceof String) {
			gwtImage = new com.google.gwt.user.client.ui.Image(
				(String) imageDefinition);
		} else {
			throw new IllegalArgumentException(
				"Invalid image parameter: " + imageDefinition);
		}
	}

	/**
	 * Returns the internal GWT image.
	 *
	 * @return The GWT image
	 */
	public com.google.gwt.user.client.ui.Image getGwtImage() {
		return gwtImage;
	}

	/**
	 * Returns the image height.
	 *
	 * @return The height in pixels
	 */
	public int getHeight() {
		return gwtImage.getHeight();
	}

	/**
	 * Returns the original input value this instance has been created from.
	 *
	 * @return The image definition value
	 */
	public Object getImageDefinition() {
		return imageDefinition;
	}

	/**
	 * Returns the image width.
	 *
	 * @return The width in pixels
	 */
	public int getWidth() {
		return gwtImage.getWidth();
	}
}
