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

import de.esoco.lib.text.TextConvert;


/********************************************************************
 * This class contains several predefined color constants and provides static
 * methods for the creation and modification of color values. Other than it's
 * desktop EWT equivalent the microEWT code doesn't use Color objects to
 * represent color values. Instead, it uses integer RGB values. To achieve
 * compatibility with desktop EWT, applications must not use integer color
 * values as method arguments directly. This can be achieved by using the
 * constants and static methods in this class which are available on both EWT
 * platforms. The following code is compatible with both EWT variants:
 *
 * <pre>
   int nAppColor = Color.toRGB(Color.darker(Color.WHITE));
   rGraphicsContext.setColor(Color.valueOf(nAppColor));
   </pre>
 *
 * <p>Of course, if desktop compatibility is not required, the additional method
 * calls can be omitted. In that case, the code would look like the following:
 * </p>
 *
 * <pre>
   int nAppColor = Color.darker(Color.WHITE));
   rGraphicsContext.setColor(nAppColor); </pre>
 *
 * @author eso
 */
public class Color
{
	//~ Static fields/initializers ---------------------------------------------

	/** black */
	public static final int BLACK = 0x00;

	/** dark gray */
	public static final int DARK_GRAY = 0x0404040;

	/** medium gray */
	public static final int MEDIUM_GRAY = 0x0808080;

	/** light gray */
	public static final int LIGHT_GRAY = 0x0C0C0C0;

	/** 10 percent gray */
	public static final int GRAY_10 = 0x01A1A1A;

	/** 20 percent gray */
	public static final int GRAY_20 = 0x0333333;

	/** 30 percent gray */
	public static final int GRAY_30 = 0x04D4D4D;

	/** 33 percent gray */
	public static final int GRAY_33 = 0x0555555;

	/** 40 percent gray */
	public static final int GRAY_40 = 0x0666666;

	/** 50 percent gray */
	public static final int GRAY_50 = MEDIUM_GRAY;

	/** 60 percent gray */
	public static final int GRAY_60 = 0x0999999;

	/** 66 percent gray */
	public static final int GRAY_66 = 0x0AAAAAA;

	/** 70 percent gray */
	public static final int GRAY_70 = 0x0B3B3B3;

	/** 80 percent gray */
	public static final int GRAY_80 = 0x0CCCCCC;

	/** 90 percent gray */
	public static final int GRAY_90 = 0x0E6E6E6;

	/** Color white */
	public static final int WHITE = 0x0FFFFFF;

	/** blue */
	public static final int BLUE = 0x0000FF;

	/** dark blue */
	public static final int DARK_BLUE = 0x000080;

	/** red */
	public static final int RED = 0x0FF0000;

	/** dark red */
	public static final int DARK_RED = 0x0800000;

	/** green */
	public static final int GREEN = 0x00FF00;

	/** dark green */
	public static final int DARK_GREEN = 0x008000;

	/** cyan */
	public static final int CYAN = 0x00FFFF;

	/** dark cyan */
	public static final int DARK_CYAN = 0x008080;

	/** yellow */
	public static final int YELLOW = 0x0FFFF00;

	/** dark yellow */
	public static final int DARK_YELLOW = 0x0808000;

	/** magenta */
	public static final int MAGENTA = 0x0FF00FF;

	/** dark magenta */
	public static final int DARK_MAGENTA = 0x0800080;

	//~ Constructors -----------------------------------------------------------

	/***************************************
	 * Private, only static use.
	 */
	private Color()
	{
	}

	//~ Static methods ---------------------------------------------------------

	/***************************************
	 * Returns a color value that is 10% brighter than the argument color.
	 * Invokes the {@link #changeBrightness(int, int)} method.
	 *
	 * @param  nColor The color value to brighten
	 *
	 * @return The brighter color value
	 */
	public static int brighter(int nColor)
	{
		return changeBrightness(nColor, 10);
	}

	/***************************************
	 * Calculates the brightest RGB value that can be created from a certain RGB
	 * value. That will be the color value where all color components have been
	 * multiplied with the factor that converts the brightest RGB component to
	 * the maximum value of 255.
	 *
	 * @param  nColor The color to calculate the brightest variant of
	 *
	 * @return The resulting RGB color value
	 */
	public static int brightest(int nColor)
	{
		// normalize components to color value * 256 in the range 1 to 256
		int r    = ((nColor & 0x00FF0000) >> 8) + 0x100;
		int g    = (nColor & 0x0000FF00) + 0x100;
		int b    = ((nColor & 0x000000FF) << 8) + 0x100;
		int nMax = Math.max(r, Math.max(g, b)) >> 8;

		// scale value and reset to range 0 to 255
		r = r / nMax - 1;
		g = g / nMax - 1;
		b = b / nMax - 1;

		return valueOf(r, g, b);
	}

	/***************************************
	 * Returns a color value with a brightness that has been changed by the
	 * given factor. The factor is used to calculate the brightness difference
	 * relative to the brightest possible value (255) and it's sign determines
	 * the direction of brightness change. Example: to increase the brightness
	 * by 10%, the factor would be 10. To reduce the brightness by 15% the
	 * factor would be -15. The maximum and minimum values are 0x00FFFFFF and
	 * 0x00000000, respectively.
	 *
	 * @param  nRGB     The color to change the brightness of
	 * @param  nPercent The brightness change factor in percent (positive or
	 *                  negative)
	 *
	 * @return A color value with the newly calculated brightness
	 */
	public static int changeBrightness(int nRGB, int nPercent)
	{
		int nChange = 255 * nPercent / 100;
		int r	    = ((nRGB & 0x00FF0000) >> 16) + nChange;
		int g	    = ((nRGB & 0x0000FF00) >> 8) + nChange;
		int b	    = (nRGB & 0x000000FF) + nChange;

		r = r < 0 ? 0 : (r > 255 ? 255 : r);
		g = g < 0 ? 0 : (g > 255 ? 255 : g);
		b = b < 0 ? 0 : (b > 255 ? 255 : b);

		return (r << 16) | (g << 8) | b;
	}

	/***************************************
	 * Returns a color value that is 10% darker than the argument color. Invokes
	 * the {@link #changeBrightness(int, int)} method.
	 *
	 * @param  nColor The color value to darken
	 *
	 * @return The darker color value
	 */
	public static int darker(int nColor)
	{
		return changeBrightness(nColor, -10);
	}

	/***************************************
	 * Converts an integer color value to a hexadecimal RGB string as used in
	 * HTML and CSS.
	 *
	 * @param  nColor The integer color value
	 *
	 * @return The hex color string
	 */
	public static String toHtml(int nColor)
	{
		return "#" + TextConvert.padLeft(Integer.toHexString(nColor), 6, '0');
	}

	/***************************************
	 * Converts a color into the corresponding RGB integer value.
	 *
	 * <p>This method exists only for compatibility with desktop EWT and the
	 * implementation simply returns the argument value. For a further
	 * description please see the class documentation.</p>
	 *
	 * @param  c The color to convert
	 *
	 * @return The RGB value
	 */
	public static int toRGB(int c)
	{
		return c;
	}

	/***************************************
	 * Creates a color instance from a combined RGB integer value. The single
	 * color components are considered to be in the range of (0 - 255) and are
	 * therefore assigned internally as (value &amp; 0x00FF).
	 *
	 * <p>This method exists only for compatibility with desktop EWT and the
	 * implementation simply returns the argument value. For a further
	 * description please see the class documentation.</p>
	 *
	 * @param  nRGB The RGB value
	 *
	 * @return The RGB value
	 */
	public static int valueOf(int nRGB)
	{
		return nRGB;
	}

	/***************************************
	 * Converts an HTML color value into an integer.
	 *
	 * @param  sHtmlColor The HTML color
	 *
	 * @return The integer color
	 */
	public static int valueOf(String sHtmlColor)
	{
		return Integer.parseInt(sHtmlColor, 16);
	}

	/***************************************
	 * Creates a color instance from it's RGB integer values. The color
	 * components are considered to be in the range of (0 - 255) and are
	 * therefore masked as (value &amp; 0x00FF).
	 *
	 * @param  nRed   The red component
	 * @param  nGreen The green component
	 * @param  nBlue  The blue component
	 *
	 * @return The single integer color value
	 */
	public static int valueOf(int nRed, int nGreen, int nBlue)
	{
		int mask = 0x00FF;

		return ((nRed & mask) << 16) | ((nGreen & mask) << 8) | (nBlue & mask);
	}
}
