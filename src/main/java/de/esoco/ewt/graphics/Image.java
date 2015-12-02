//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// GEWT source file
// Copyright (c) 2012 by Elmar Sonnenschein / esoco GmbH
//
// Last Change: 09.02.2012 by eso
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
package de.esoco.ewt.graphics;

import com.google.gwt.resources.client.ImageResource;

import de.esoco.ewt.UserInterfaceContext;


/********************************************************************
 * This class contains EWT images. For platform compatibility image instances
 * should not be created by invoking the constructor but by using the factory
 * method {@link UserInterfaceContext#createImage(String)}.
 *
 * @author eso
 */
public
class Image
{
	private com.google.gwt.user.client.ui.Image rGwtImage;

	/***************************************
	 * Creates a new instance from an arbitrary image object. The supported
	 * argument types are either a {@link String} with the image name or one of
	 * the GEWT-specific types {@link com.google.gwt.user.client.ui.Image} or
	 * {@link ImageResource}.
	 *
	 * @param  rImageDefinition The image object
	 *
	 * @throws IllegalArgumentException If the argument type is not supported
	 */
	public Image(Object rImageDefinition)
	{
		if (rImageDefinition instanceof ImageResource)
		{
			rGwtImage = new com.google.gwt.user.client.ui.Image((ImageResource) rImageDefinition);
		}
		else if (rImageDefinition instanceof
		         com.google.gwt.user.client.ui.Image)
		{
			rGwtImage = (com.google.gwt.user.client.ui.Image) rImageDefinition;
		}
		else if (rImageDefinition instanceof String)
		{
			rGwtImage = new com.google.gwt.user.client.ui.Image((String) rImageDefinition);
		}
		else
		{
			throw new IllegalArgumentException("Invalid image parameter: " +
			                                   rImageDefinition);
		}
	}

	/***************************************
	 * Returns the internal GWT image.
	 *
	 * @return   The GWT image
	 *
	 * @category GEWT
	 */
	public com.google.gwt.user.client.ui.Image getGwtImage()
	{
		return rGwtImage;
	}

	/***************************************
	 * Returns the image height.
	 *
	 * @return The height in pixels
	 */
	public int getHeight()
	{
		return rGwtImage.getHeight();
	}

	/***************************************
	 * Returns the image width.
	 *
	 * @return The width in pixels
	 */
	public int getWidth()
	{
		return rGwtImage.getWidth();
	}
}
