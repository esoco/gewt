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
package de.esoco.ewt.app;

import de.esoco.ewt.graphics.Image;

/**
 * A generic interface for objects that provide access to resources.
 *
 * @author eso
 */
public interface Resource {

	/**
	 * Returns an image that is associated with a certain key.
	 *
	 * @param sKey The key to return the image for
	 * @return The resulting image or NULL if not found
	 */
	public Image getImage(String sKey);

	/**
	 * Returns a string that is associated with a certain key.
	 *
	 * @param sKey The key to return the string for
	 * @return The resulting string or NULL if not found
	 */
	public String getString(String sKey);
}
