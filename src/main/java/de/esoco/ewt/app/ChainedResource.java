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
 * A resource implementation that chains two resources. If the lookup in the
 * primary resource fails a lookup in the secondary resource will be performed.
 *
 * @author eso
 */
public class ChainedResource implements Resource {
	private final Resource primaryResource;

	private final Resource secondaryResource;

	/**
	 * Creates a new instance.
	 *
	 * @param primary   The primary resource to lookup first
	 * @param secondary The secondary resource to lookup if the primary
	 *                     resource
	 *                  lookup fails
	 */
	public ChainedResource(Resource primary, Resource secondary) {
		if (primary == null || secondary == null) {
			throw new IllegalArgumentException("Arguments must no be NULL");
		}

		primaryResource = primary;
		secondaryResource = secondary;
	}

	/**
	 * @see Resource#getImage(String)
	 */
	@Override
	public Image getImage(String key) {
		Image image = primaryResource.getImage(key);

		if (image == null) {
			image = secondaryResource.getImage(key);
		}

		return image;
	}

	/**
	 * @see Resource#getString(String)
	 */
	@Override
	public String getString(String key) {
		String value = primaryResource.getString(key);

		if (value == null) {
			value = secondaryResource.getString(key);
		}

		return value;
	}
}
