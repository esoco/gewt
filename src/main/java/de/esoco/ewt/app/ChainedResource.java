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


/********************************************************************
 * A resource implementation that chains two resources. If the lookup in the
 * primary resource fails a lookup in the secondary resource will be performed.
 *
 * @author eso
 */
public class ChainedResource implements Resource
{
	private final Resource rPrimaryResource;
	private final Resource rSecondaryResource;

	/***************************************
	 * Creates a new instance.
	 *
	 * @param rPrimary   The primary resource to lookup first
	 * @param rSecondary The secondary resource to lookup if the primary
	 *                   resource lookup fails
	 */
	public ChainedResource(Resource rPrimary, Resource rSecondary)
	{
		if (rPrimary == null || rSecondary == null)
		{
			throw new IllegalArgumentException("Arguments must no be NULL");
		}

		rPrimaryResource   = rPrimary;
		rSecondaryResource = rSecondary;
	}

	/***************************************
	 * @see Resource#getImage(String)
	 */
	@Override
	public Image getImage(String sKey)
	{
		Image rImage = rPrimaryResource.getImage(sKey);

		if (rImage == null)
		{
			rImage = rSecondaryResource.getImage(sKey);
		}

		return rImage;
	}

	/***************************************
	 * @see Resource#getString(String)
	 */
	@Override
	public String getString(String sKey)
	{
		String sValue = rPrimaryResource.getString(sKey);

		if (sValue == null)
		{
			sValue = rSecondaryResource.getString(sKey);
		}

		return sValue;
	}
}
