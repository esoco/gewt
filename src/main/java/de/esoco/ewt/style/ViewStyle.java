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
package de.esoco.ewt.style;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


/********************************************************************
 * Instances of this class describe the appearance of views. It also contains
 * several predefined constants for standard view styles.
 *
 * @author eso
 */
public class ViewStyle
{
	//~ Enums ------------------------------------------------------------------

	/********************************************************************
	 * Enumeration of view style flags.
	 */
	public enum Flag { MODAL, FIXED_SIZE, FULL_SIZE, UNDECORATED, AUTO_HIDE }

	//~ Static fields/initializers ---------------------------------------------

	/** Constant for the default view style */
	public static final ViewStyle DEFAULT = new ViewStyle();

	/** Constant for the default view style */
	public static final ViewStyle FULL_SIZE = new ViewStyle(Flag.FULL_SIZE);

	/** Constant for modal views */
	public static final ViewStyle MODAL = new ViewStyle(Flag.MODAL);

	/** Constant for view that hide automatically if the user clicks outside. */
	public static final ViewStyle AUTO_HIDE = new ViewStyle(Flag.AUTO_HIDE);

	/**
	 * Constant for modal views that hide automatically if the user clicks
	 * outside.
	 */
	public static final ViewStyle MODAL_AUTO_HIDE =
		new ViewStyle(Flag.MODAL, Flag.AUTO_HIDE);

	/** Constant for fixed-size views */
	public static final ViewStyle FIXED_SIZE = new ViewStyle(Flag.FIXED_SIZE);

	/** Constant for fixed-size modal views */
	public static final ViewStyle FIXED_SIZE_MODAL =
		new ViewStyle(Flag.FIXED_SIZE, Flag.MODAL);

	/** Constant for undecorated views */
	public static final ViewStyle UNDECORATED = new ViewStyle(Flag.UNDECORATED);

	//~ Instance fields --------------------------------------------------------

	private Set<Flag> aFlags = Collections.emptySet();

	//~ Constructors -----------------------------------------------------------

	/***************************************
	 * Internal constructor that creates an instance which contains certain
	 * flags.
	 *
	 * @param rFlags The flags the new instance shall contain
	 */
	private ViewStyle(Flag... rFlags)
	{
		if (rFlags != null)
		{
			aFlags = new HashSet<Flag>(rFlags.length);

			for (Flag rFlag : rFlags)
			{
				aFlags.add(rFlag);
			}
		}
	}

	//~ Methods ----------------------------------------------------------------

	/***************************************
	 * Checks if a certain style flag is set in this instance.
	 *
	 * @param  rFlag The flag to check
	 *
	 * @return TRUE if the flag is set
	 */
	public boolean hasFlag(Flag rFlag)
	{
		return aFlags.contains(rFlag);
	}
}
