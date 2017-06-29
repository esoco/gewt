//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// This file is a part of the 'gewt' project.
// Copyright 2017 Elmar Sonnenschein, esoco GmbH, Flensburg, Germany
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
package de.esoco.ewt.style;

import de.esoco.lib.property.Alignment;


/********************************************************************
 * StyleData subclass for the definition of static constants for all possible
 * aligned positions that can be defined by a combination of horizontal and
 * vertical alignment.
 */
public final class AlignedPosition extends StyleData
{
	//~ Static fields/initializers ---------------------------------------------

	private static final long serialVersionUID = 1L;

	/** center position */
	public static final AlignedPosition CENTER =
		new AlignedPosition(Alignment.CENTER, Alignment.CENTER);

	/** top position */
	public static final AlignedPosition TOP =
		new AlignedPosition(Alignment.CENTER, Alignment.BEGIN);

	/** bottom position */
	public static final AlignedPosition BOTTOM =
		new AlignedPosition(Alignment.CENTER, Alignment.END);

	/** left position */
	public static final AlignedPosition LEFT =
		new AlignedPosition(Alignment.BEGIN, Alignment.CENTER);

	/** right position */
	public static final AlignedPosition RIGHT =
		new AlignedPosition(Alignment.END, Alignment.CENTER);

	/** top left position */
	public static final AlignedPosition TOP_LEFT =
		new AlignedPosition(Alignment.BEGIN, Alignment.BEGIN);

	/** top right position */
	public static final AlignedPosition TOP_RIGHT =
		new AlignedPosition(Alignment.END, Alignment.BEGIN);

	/** bottom left position */
	public static final AlignedPosition BOTTOM_LEFT =
		new AlignedPosition(Alignment.BEGIN, Alignment.END);

	/** bottom right position */
	public static final AlignedPosition BOTTOM_RIGHT =
		new AlignedPosition(Alignment.END, Alignment.END);

	//~ Constructors -----------------------------------------------------------

	/***************************************
	 * Creates a new instance with particular alignment.
	 *
	 * @param rHorizontal The horizontal alignment
	 * @param rVertical   The vertical alignment
	 */
	private AlignedPosition(Alignment rHorizontal, Alignment rVertical)
	{
		super(rHorizontal, rVertical);
	}

	//~ Static methods ---------------------------------------------------------

	/***************************************
	 * Returns the instance that corresponds to the given combination of
	 * horizontal and vertical alignment
	 *
	 * @param  rHorizontal The horizontal alignment
	 * @param  rVertical   The vertical alignment
	 *
	 * @return The corresponding aligned position
	 */
	public static AlignedPosition valueOf(
		Alignment rHorizontal,
		Alignment rVertical)
	{
		if (rHorizontal == Alignment.BEGIN)
		{
			if (rVertical == Alignment.BEGIN)
			{
				return TOP_LEFT;
			}

			if (rVertical == Alignment.END)
			{
				return BOTTOM_LEFT;
			}
			else
			{
				return LEFT;
			}
		}
		else if (rHorizontal == Alignment.END)
		{
			if (rVertical == Alignment.BEGIN)
			{
				return TOP_RIGHT;
			}

			if (rVertical == Alignment.END)
			{
				return BOTTOM_RIGHT;
			}
			else
			{
				return RIGHT;
			}
		}
		else
		{
			if (rVertical == Alignment.BEGIN)
			{
				return TOP;
			}

			if (rVertical == Alignment.END)
			{
				return BOTTOM;
			}
			else
			{
				return CENTER;
			}
		}
	}
}
