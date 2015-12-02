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
package de.esoco.ewt.style;

/********************************************************************
 * Enumeration of alignment constants.
 *
 * @author eso
 */
public enum Alignment
{
	BEGIN
	{
		@Override
		public int calcAlignedPosition(int	   nStart,
									   int	   nFreeSpace,
									   boolean bLeftToRight)
		{
			return nStart + (bLeftToRight ? 0 : nFreeSpace);
		}
	},
	CENTER
	{
		@Override
		public int calcAlignedPosition(int	   nStart,
									   int	   nFreeSpace,
									   boolean bLeftToRight)
		{
			return nStart + nFreeSpace / 2;
		}
	},
	END
	{
		@Override
		public int calcAlignedPosition(int	   nStart,
									   int	   nFreeSpace,
									   boolean bLeftToRight)
		{
			return nStart + (bLeftToRight ? nFreeSpace : 0);
		}
	},
	FILL
	{
		@Override
		public int calcAlignedPosition(int	   nStart,
									   int	   nFreeSpace,
									   boolean bLeftToRight)
		{
			return nStart + (bLeftToRight ? 0 : nFreeSpace);
		}
	};

	/***************************************
	 * Calculates and returns a coordinate aligned according to this alignment
	 * based on the starting position and the remaining free space of the area
	 * to align in.
	 *
	 * @param  nStart       The starting coordinate of the area to align in
	 * @param  nFreeSpace   The free space available for alignment
	 * @param  bLeftToRight TRUE for left-to-right display
	 *
	 * @return The calculated aligned coordinate
	 */
	public abstract int calcAlignedPosition(int		nStart,
											int		nFreeSpace,
											boolean bLeftToRight);

	/***************************************
	 * Returns the alignment instance that is identified by a certain character.
	 * The character must be the first letter of the instance name.
	 *
	 * @param  cChar The alignment character (case-sensitive)
	 *
	 * @return The matching alignment instance
	 *
	 * @throws IllegalArgumentException If the character doesn't represent an
	 *                                  alignment instance
	 */
	public static Alignment valueOf(char cChar)
	{
		for (Alignment a : values())
		{
			if (a.getCharacter() == cChar)
			{
				return a;
			}
		}

		throw new IllegalArgumentException("No valid Alignment char: " + cChar);
	}

	/***************************************
	 * Calculates and returns a coordinate aligned according to this alignment
	 * based on the starting position, the size of the element to align, and the
	 * full size of the are to align the element in.
	 *
	 * @param  nStart     The starting coordinate of the area to align in
	 * @param  nFullSize  The full size (i.e. width or height) of the area
	 * @param  nAlignSize The size of the element to align
	 *
	 * @return The calculated aligned coordinate
	 */
	public int calcAlignedPosition(int nStart, int nFullSize, int nAlignSize)
	{
		return calcAlignedPosition(nStart, nFullSize - nAlignSize, true);
	}

	/***************************************
	 * Returns the character representation of this instance.
	 *
	 * @return The character describing this instance
	 */
	public final char getCharacter()
	{
		return name().charAt(0);
	}
}
