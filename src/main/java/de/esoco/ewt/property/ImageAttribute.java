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
package de.esoco.ewt.property;

import de.esoco.ewt.graphics.Image;

/**
 * Property interface for user interface elements that implement an image
 * attribute.
 *
 * @author eso
 */
public interface ImageAttribute {
	/**
	 * Returns the element's image.
	 *
	 * @return The image
	 */
	public Image getImage();

	/**
	 * Sets the element's image.
	 *
	 * @param image The new image
	 */
	public void setImage(Image image);
}
