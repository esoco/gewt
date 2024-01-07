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

import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;

/**
 * Instances of this class describe the appearance of views. It also contains
 * several predefined constants for standard view styles.
 *
 * @author eso
 */
public class ViewStyle {

	/**
	 * Enumeration of the available view style flags.
	 *
	 * <ul>
	 *   <li>{@link #FULL_SIZE}: a view that fills the available area.
	 *   Typically
	 *     used for the main view of an application.</li>
	 *   <li>{@link #MODAL}: a modal window on top of other content that needs
	 *     to be dismissed before the underlying UI can be accessed.</li>
	 *   <li>{@link #AUTO_HIDE}: a view that hides automatically when the user
	 *     clicks outside of it.</li>
	 *   <li>{@link #BOTTOM}: the view should be displayed at the bottom of the
	 *     available area.</li>
	 * </ul>
	 */
	public enum Flag {FULL_SIZE, MODAL, AUTO_HIDE, BOTTOM}

	/**
	 * Constant for the default view style
	 */
	public static final ViewStyle DEFAULT = new ViewStyle();

	/**
	 * Constant for the default view style
	 */
	public static final ViewStyle FULL_SIZE = new ViewStyle(Flag.FULL_SIZE);

	/**
	 * Constant for modal views
	 */
	public static final ViewStyle MODAL = new ViewStyle(Flag.MODAL);

	/**
	 * Constant for view that hide automatically if the user clicks outside.
	 */
	public static final ViewStyle AUTO_HIDE = new ViewStyle(Flag.AUTO_HIDE);

	/**
	 * Constant for modal views that hide automatically if the user clicks
	 * outside.
	 */
	public static final ViewStyle MODAL_AUTO_HIDE =
		new ViewStyle(Flag.MODAL, Flag.AUTO_HIDE);

	private final EnumSet<Flag> flags = EnumSet.noneOf(Flag.class);

	/**
	 * Internal constructor that creates an instance which contains certain
	 * flags.
	 *
	 * @param flags The flags the new instance shall contain
	 */
	private ViewStyle(Flag... flags) {
		if (flags != null) {
			Collections.addAll(this.flags, flags);
		}
	}

	/**
	 * Checks if a certain style flag is set in this instance.
	 *
	 * @param flag The flag to check
	 * @return TRUE if the flag is set
	 */
	public boolean hasFlag(Flag flag) {
		return flags.contains(flag);
	}

	/**
	 * Creates a copy of this instance that has additional flags set.
	 *
	 * @param additionalFlags The additional flags to set
	 * @return The new instance
	 */
	public ViewStyle withFlags(Flag... additionalFlags) {
		ViewStyle viewStyle = new ViewStyle(additionalFlags);

		viewStyle.flags.addAll(flags);

		return viewStyle;
	}

	/**
	 * Creates a copy of this instance that has additional flags set.
	 *
	 * @param additionalFlags The additional flags to set
	 * @return The new instance
	 */
	public ViewStyle withFlags(Collection<Flag> additionalFlags) {
		ViewStyle viewStyle = new ViewStyle();

		viewStyle.flags.addAll(flags);
		viewStyle.flags.addAll(additionalFlags);

		return viewStyle;
	}
}
