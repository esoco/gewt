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
package de.esoco.ewt.graphics;

/********************************************************************
 * The interface for GEWT images.
 *
 * @author eso
 */
public interface Image
{
	//~ Static fields/initializers ---------------------------------------------

	/** The separator between image prefix and image name */
	public static final char IMAGE_PREFIX_SEPARATOR = ':';

	/** A prefix for base64 encoded image data */
	public static final char IMAGE_DATA_PREFIX = 'd';

	/** A prefix for an image file name */
	public static final char IMAGE_FILE_PREFIX = 'f';

	/** A prefix for an icon image */
	public static final char IMAGE_ICON_PREFIX = 'i';

	/** A MINE type declaration for a base64 encoded PNG image. */
	public static final String BASE64_PNG_IMAGE_DATA_TYPE = "image/png;base64";
}
