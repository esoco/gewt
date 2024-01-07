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
package de.esoco.ewt.app;

import de.esoco.ewt.graphics.Image;
import de.esoco.ewt.graphics.ImageRef;

import java.util.Collection;
import java.util.Map;
import java.util.MissingResourceException;

import com.google.gwt.i18n.client.ConstantsWithLookup;
import com.google.gwt.resources.client.ImageResource;

/**
 * A GWT-specific resource class that performs string lookups in a resource of
 * type {@link ConstantsWithLookup} and image lookups in a map with
 * {@link ImageResource} entries.
 *
 * @author eso
 */
public class GwtResource implements Resource {

	private final Collection<ConstantsWithLookup> strings;

	private final Map<String, ImageResource> images;

	/**
	 * Creates a new instance that only performs string lookups.
	 *
	 * @param constants The constants to lookup resources from
	 */
	public GwtResource(ConstantsWithLookup constants) {
		this(null, null);
	}

	/**
	 * Creates a new instance performs string and image lookups.
	 *
	 * @param strings The constants to lookup strings from
	 * @param images  A mapping from string keys to image resources
	 */
	public GwtResource(Collection<ConstantsWithLookup> strings,
		Map<String, ImageResource> images) {
		this.strings = strings;
		this.images = images;
	}

	/**
	 * @see Resource#getImage(String)
	 */
	@Override
	public Image getImage(String key) {
		Image image = null;

		if (images != null) {
			ImageResource imageResource = images.get(key);

			if (imageResource != null) {
				image = new ImageRef(imageResource);
			}
		}

		return image;
	}

	/**
	 * @see Resource#getString(String)
	 */
	@Override
	public String getString(String key) {
		String value = null;

		if (strings != null) {
			for (ConstantsWithLookup strings : strings) {
				try {
					value = strings.getString(key);
				} catch (MissingResourceException e) {
					// ignore and continue checking or return NULL
				}

				if (value != null) {
					break;
				}
			}
		}

		return value;
	}
}
