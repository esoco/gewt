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
package de.esoco.ewt.layout;

import de.esoco.ewt.style.StyleData;

import de.esoco.lib.property.ContentAlignment;
import de.esoco.lib.property.HasCssName;
import de.esoco.lib.property.Orientation;
import de.esoco.lib.property.PropertyName;

import com.google.gwt.dom.client.Style;

import static de.esoco.lib.property.LayoutProperties.ORDER;


/********************************************************************
 * A CSS flex layout implementation. It provides fluent builder methods like
 * {@link #direction(Orientation)} that can be invoked successively to set
 * optional grid properties.
 *
 * @author eso
 */
public class FlexLayout extends FluentCssLayout<FlexLayout>
{
	//~ Enums ------------------------------------------------------------------

	/********************************************************************
	 * Enumeration of the flex layout item alignments.
	 */
	public enum ItemAlignment implements HasCssName
	{
		START("flex-start"), END("flex-end"), CENTER("center"),
		BASELINE("baseline"), STRETCH("stretch");

		//~ Instance fields ----------------------------------------------------

		private final String sCssName;

		//~ Constructors -------------------------------------------------------

		/***************************************
		 * Creates a new instance.
		 *
		 * @param sCssName The CSS name
		 */
		private ItemAlignment(String sCssName)
		{
			this.sCssName = sCssName;
		}

		//~ Methods ------------------------------------------------------------

		/***************************************
		 * {@inheritDoc}
		 */
		@Override
		public String getCssName()
		{
			return sCssName;
		}
	}

	/********************************************************************
	 * Enumeration of the flex layout wrapping modes.
	 */
	public enum Wrapping implements HasCssName
	{
		NONE("nowrap"), NORMAL("wrap"), REVERSE("wrap-reverse");

		//~ Instance fields ----------------------------------------------------

		private final String sCssName;

		//~ Constructors -------------------------------------------------------

		/***************************************
		 * Creates a new instance.
		 *
		 * @param sCssName The CSS name
		 */
		private Wrapping(String sCssName)
		{
			this.sCssName = sCssName;
		}

		//~ Methods ------------------------------------------------------------

		/***************************************
		 * {@inheritDoc}
		 */
		@Override
		public String getCssName()
		{
			return sCssName;
		}
	}

	//~ Static fields/initializers ---------------------------------------------

	/** Integer: flex layout growth factor of an item. */
	public static final PropertyName<Integer> FLEX_GROW =
		PropertyName.newIntegerName("FLEX_GROW");

	/** Integer: flex layout shrink factor of an item. */
	public static final PropertyName<Integer> FLEX_SHRINK =
		PropertyName.newIntegerName("FLEX_SHRINK");

	/** String: flex base size of an item. */
	public static final PropertyName<String> FLEX_BASIS =
		PropertyName.newStringName("FLEX_BASIS");

	/** Alignment of an item along the secondary layout axis. */
	public static final PropertyName<ItemAlignment> FLEX_ALIGN =
		PropertyName.newEnumName("FLEX_ALIGN", ItemAlignment.class);

	//~ Instance fields --------------------------------------------------------

	private boolean bInline;

	private Orientation eDirection = null;
	private boolean     bReverse   = false;

	private Wrapping	  eWrapping		 = null;
	private ItemAlignment eItemAlignment = null;

	private ContentAlignment eJustifyContent = null;
	private ContentAlignment eAlignContent   = null;

	//~ Constructors -----------------------------------------------------------

	/***************************************
	 * Creates a new instance.
	 */
	public FlexLayout()
	{
	}

	//~ Static methods ---------------------------------------------------------

	/***************************************
	 * A factory method to create a new instance with a horizontal layout
	 * direction for fluent invocations.
	 *
	 * @return The new flex layout
	 */
	public static FlexLayout flexHorizontal()
	{
		return new FlexLayout();
	}

	/***************************************
	 * A factory method to create a new instance with a vertical layout
	 * direction for fluent invocations.
	 *
	 * @return The new flex layout
	 */
	public static FlexLayout flexVertical()
	{
		return new FlexLayout().direction(Orientation.VERTICAL);
	}

	//~ Methods ----------------------------------------------------------------

	/***************************************
	 * Sets the alignment of the layout content along the secondary layout axis
	 * (#see method {@link #direction(Orientation, boolean)}). This property
	 * will only have an effect if the layout has multiple lines because when
	 * wrapping occurs. Almost all content alignment values with the exception
	 * of {@link ContentAlignment#SPACE_EVENLY SPACE_EVENLY} are supported.
	 *
	 * @param  eAlignment The horizontal content alignment
	 *
	 * @return This instance for fluent invocation
	 *
	 * @throws IllegalArgumentException If an unsupported aligment is provided
	 */
	public FlexLayout align(ContentAlignment eAlignment)
	{
		if (eAlignment == ContentAlignment.SPACE_EVENLY)
		{
			throw new IllegalArgumentException(
				"SPACE_EVENLY alignment is not supported for the secondary layout axis");
		}

		return _with(() -> eAlignContent = eAlignment);
	}

	/***************************************
	 * Sets the alignment of layout items along the secondary layout axis.
	 *
	 * @param  eAlignment The item alignment
	 *
	 * @return This instance for fluent invocation
	 */
	public FlexLayout alignItems(ItemAlignment eAlignment)
	{
		return _with(() -> eItemAlignment = eAlignment);
	}

	/***************************************
	 * Sets the direction of the item flow. This defines the main axis of the
	 * flex layout flow. The secondary axis runs in the other direction,
	 * perpendicular to the main axis.
	 *
	 * @see #direction(Orientation, boolean)
	 */
	public FlexLayout direction(Orientation eDirection)
	{
		return direction(eDirection, false);
	}

	/***************************************
	 * Sets the direction of the item flow. This defines the main axis of the
	 * flex layout flow. The secondary axis runs in the other direction,
	 * perpendicular to the main axis.
	 *
	 * @param  eDirection The flow direction
	 * @param  bReverse   TRUE to reverse the flow direction
	 *
	 * @return This instance for fluent invocation
	 */
	public FlexLayout direction(Orientation eDirection, boolean bReverse)
	{
		return _with(
			() ->
			{
				this.eDirection = eDirection;
				this.bReverse   = bReverse;
			});
	}

	/***************************************
	 * Enables inline rendering (display = 'inline-flex').
	 *
	 * @return This instance for fluent invocation
	 */
	public FlexLayout inline()
	{
		return _with(() -> bInline = true);
	}

	/***************************************
	 * Sets the alignment of the layout content along the main layout axis (#see
	 * {@link #direction(Orientation, boolean)}). Almost all content alignment
	 * values with the exception of {@link ContentAlignment#STRETCH STRETCH} are
	 * supported.
	 *
	 * @param  eAlignment The horizontal content alignment
	 *
	 * @return This instance for fluent invocation
	 *
	 * @throws IllegalArgumentException If an unsupported alignment is provided
	 */
	public FlexLayout justify(ContentAlignment eAlignment)
	{
		if (eAlignment == ContentAlignment.STRETCH)
		{
			throw new IllegalArgumentException(
				"STRETCH alignment is not supported for the main layout axis");
		}

		return _with(() -> eJustifyContent = eAlignment);
	}

	/***************************************
	 * {@inheritDoc}
	 */
	@Override
	protected void applyLayoutStyle(StyleData rStyleData, Style rStyle)
	{
		rStyle.setProperty("display", bInline ? "inline-flex" : "flex");

		setStyleProperty("flexWrap", rStyle, eWrapping);
		setStyleProperty("justifyContent", rStyle, eJustifyContent);
		setStyleProperty("alignContent", rStyle, eAlignContent);
		setStyleProperty("alignItems", rStyle, eItemAlignment);

		if (eDirection != null)
		{
			String sFlexDirection =
				eDirection == Orientation.HORIZONTAL ? "row" : "column";

			if (bReverse)
			{
				sFlexDirection += "-reverse";
			}

			setStyleProperty("flexDirection", rStyle, sFlexDirection);
		}
	}

	/***************************************
	 * {@inheritDoc}
	 */
	@Override
	protected void applyWidgetStyle(StyleData rStyleData, Style rStyle)
	{
		setStyleProperty(ORDER, rStyleData, "order", rStyle);
		setStyleProperty(FLEX_GROW, rStyleData, "flexGrow", rStyle);
		setStyleProperty(FLEX_SHRINK, rStyleData, "flexShrink", rStyle);
		setStyleProperty(FLEX_BASIS, rStyleData, "flexBasis", rStyle);
		setStyleProperty(FLEX_ALIGN, rStyleData, "alignSelf", rStyle);
	}
}
