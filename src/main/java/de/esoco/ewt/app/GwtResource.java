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

import java.util.Map;
import java.util.MissingResourceException;

import com.google.gwt.i18n.client.ConstantsWithLookup;
import com.google.gwt.resources.client.ImageResource;


/********************************************************************
 * A GWT-specific resource class that performs string lookups in a resource of
 * type {@link ConstantsWithLookup} and image lookups in a map with {@link
 * ImageResource} entries.
 *
 * @author eso
 */
public class GwtResource implements Resource
{
	//~ Instance fields --------------------------------------------------------

	private ConstantsWithLookup[]			 rStringContants;
	private final Map<String, ImageResource> rImages;

	//~ Constructors -----------------------------------------------------------

	/***************************************
	 * Creates a new instance that only performs string lookups.
	 *
	 * @param rConstants The constants to lookup resources from
	 */
	public GwtResource(ConstantsWithLookup rConstants)
	{
		this(new ConstantsWithLookup[] { rConstants }, null);
	}

	/***************************************
	 * Creates a new instance performs string and image lookups.
	 *
	 * @param rStringConstants The constants to lookup strings from
	 * @param rImages          A mapping from string keys to image resources
	 */
	public GwtResource(
		ConstantsWithLookup[]	   rStringConstants,
		Map<String, ImageResource> rImages)
	{
		this.rStringContants = rStringConstants;
		this.rImages		 = rImages;
	}

	//~ Methods ----------------------------------------------------------------

	/***************************************
	 * @see Resource#getImage(String)
	 */
	@Override
	public Image getImage(String sKey)
	{
		Image rImage = null;

		if (rImages != null)
		{
			ImageResource rImageResource = rImages.get(sKey);

			if (rImageResource != null)
			{
				rImage = new Image(rImageResource);
			}
		}

		return rImage;
	}

	/***************************************
	 * @see Resource#getString(String)
	 */
	@Override
	public String getString(String sKey)
	{
		String sValue = null;

		if (rStringContants != null)
		{
			for (ConstantsWithLookup rStrings : rStringContants)
			{
				try
				{
					sValue = rStrings.getString(sKey);
				}
				catch (MissingResourceException e)
				{
					// ignore and return NULL
				}

				if (sValue != null)
				{
					break;
				}
			}
		}

		return sValue;
	}
}
