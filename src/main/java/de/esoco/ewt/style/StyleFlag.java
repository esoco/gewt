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
package de.esoco.ewt.style;

import de.esoco.ewt.component.TabPanel;

/**
 * An enumeration of integer style flags that can be set on a style data
 * instance to control the appearance and/or behavior of a component.
 *
 * @author eso
 */
public enum StyleFlag {
	/**
	 * Default attribute, e.g. for default buttons
	 */
	DEFAULT,

	/**
	 * Signals that a component is not resizable
	 */
	FIXED_SIZE,

	/**
	 * Signals that a component is read only/not editable
	 */
	READ_ONLY,

	/**
	 * Text or sub-elements may wrap or not wrap
	 */
	WRAP, NO_WRAP,

	/**
	 * Enables multi-selection for components that support item selection
	 */
	MULTISELECT,

	/**
	 * Allow dragging inside the component (e.g. in ScrollPanels)
	 */
	DRAGGING,

	//- Render styles ---------------------------

	/**
	 * Enables flat appearance (e.g. ToolBars)
	 */
	FLAT,

	/**
	 * Disables the default border around a component (if supported)
	 */
	NO_BORDER,

	/**
	 * Enables a default border around a component (if supported)
	 */
	BORDER,

	//- Border styles ---------------------------

	/**
	 * Border style: raised bevel (as used for buttons)
	 */
	BEVEL_RAISED,

	/**
	 * Border style: lowered bevel (as used for pressed buttons)
	 */
	BEVEL_LOWERED,

	/**
	 * Border style: etched-out line
	 */
	ETCHED_OUT,

	/**
	 * Border style: etched-in line
	 */
	ETCHED_IN,

	//- ScrollBar styles ------------------------

	/**
	 * Horizontal scrollbar always on
	 */
	SCROLLBAR_HORIZONTAL_ON,

	/**
	 * Horizontal scrollbar always off
	 */
	SCROLLBAR_HORIZONTAL_OFF,

	/**
	 * Vertical scrollbar always on
	 */
	SCROLLBAR_VERTICAL_ON,

	/**
	 * Vertical scrollbars always off
	 */
	SCROLLBAR_VERTICAL_OFF,

	//- Rendering alignments ----------
	/**
	 * Horizontally left-aligned
	 */
	HORIZONTAL_ALIGN_LEFT,

	/**
	 * Horizontally centered
	 */
	HORIZONTAL_ALIGN_CENTER,

	/**
	 * Horizontally right-aligned
	 */
	HORIZONTAL_ALIGN_RIGHT,

	/**
	 * Vertically top-aligned
	 */
	VERTICAL_ALIGN_TOP,

	/**
	 * Vertically centered
	 */
	VERTICAL_ALIGN_CENTER,

	/**
	 * Vertically bottom-aligned
	 */
	VERTICAL_ALIGN_BOTTOM,

	//- Component-specific styles ---------------

	/**
	 * Renders a label as a hyperlink
	 */
	HYPERLINK,

	/**
	 * A text should be treated as a resource string
	 */
	RESOURCE,

	/**
	 * Bottom placement of the tabs in a {@link TabPanel} (currently not
	 * supported on GEWT)
	 */
	TAB_BOTTOM,

	/**
	 * When set on a date component defines that also time adjustment controls
	 * should be available.
	 */
	DATE_TIME;
}
