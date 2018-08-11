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
package de.esoco.ewt.composite;

import de.esoco.ewt.build.ContainerBuilder;
import de.esoco.ewt.component.Button;
import de.esoco.ewt.component.Composite;
import de.esoco.ewt.component.Label;
import de.esoco.ewt.event.EventType;
import de.esoco.ewt.style.StyleData;

import de.esoco.lib.property.Alignment;
import de.esoco.lib.property.ButtonStyle;

import java.math.BigDecimal;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

import static de.esoco.ewt.layout.GridLayout.grid;
import static de.esoco.ewt.style.StyleData.DEFAULT;

import static de.esoco.lib.property.LayoutProperties.HORIZONTAL_ALIGN;
import static de.esoco.lib.property.StyleProperties.BUTTON_STYLE;
import static de.esoco.lib.text.TextConvert.capitalizedIdentifier;
import static de.esoco.lib.text.TextConvert.interleave;


/********************************************************************
 * A composite that can display a value in multiple formats. The formats are
 * provided as functions that convert the current value (of type T) into a
 * string representation according to the format. If the format returns NULL the
 * value will not be displayed in that format. The format functions should also
 * provide a toString() implementation that returns a descriptive name of the
 * format which will be used to generate a resource key for the format label.
 * This can most easily be achieved by using enumeration constants that also
 * implement the format function interface. An example is provided with the
 * {@link NumberDisplayFormat} enum.
 *
 * <p>If the display mode is {@link DisplayMode#INTERACTIVE} the user can select
 * an active format which can then be rendered differently (by providing the
 * appropriate CSS). It is also possible to set and query the currently active
 * format ({@link #getActiveFormat()}) and to register listeners for changes of
 * the active format ({@link #onFormatChanged(Consumer)}).</p>
 *
 * @author eso
 */
public class MultiFormatDisplay<T, F extends Function<T, String>>
	extends Composite
{
	//~ Enums ------------------------------------------------------------------

	/********************************************************************
	 * Example display formats for the rendering of {@link BigDecimal} values.
	 * All formats other than {@link #DECIMAL} only render the integer part of a
	 * value. Therefore, for a consistent representation the displayed input
	 * value should have no fraction.
	 */
	public enum NumberDisplayFormat implements Function<BigDecimal, String>
	{
		DECIMAL(BigDecimal::toPlainString), HEXADECIMAL(d -> format(d, 16, -4)),
		OCTAL(d -> format(d, 8, -3)), BINARY(d -> format(d, 2, -4));

		//~ Instance fields ----------------------------------------------------

		private final Function<BigDecimal, String> fFormatValue;

		//~ Constructors -------------------------------------------------------

		/***************************************
		 * Creates a new instance.
		 *
		 * @param fFormat The value formatting function
		 */
		private NumberDisplayFormat(Function<BigDecimal, String> fFormat)
		{
			fFormatValue = fFormat;
		}

		//~ Static methods -----------------------------------------------------

		/***************************************
		 * Helper method to format decimal values without a scale with a certain
		 * (non-decimal) radix.
		 *
		 * @param  dValue     The value to format
		 * @param  nRadix     The radix
		 * @param  nChunkSize The chunk size at which to interleave the
		 *                    formatted string with spaces
		 *
		 * @return The formatted string value or NULL if the scale of the input
		 *         value is not zero
		 */
		private static String format(BigDecimal dValue,
									 int		nRadix,
									 int		nChunkSize)
		{
			String sFormatted = null;

			if (dValue.scale() == 0)
			{
				sFormatted =
					interleave(dValue.toBigInteger().toString(nRadix),
							   " ",
							   nChunkSize);
			}

			return sFormatted;
		}

		//~ Methods ------------------------------------------------------------

		/***************************************
		 * {@inheritDoc}
		 */
		@Override
		public String apply(BigDecimal dValue)
		{
			return fFormatValue.apply(dValue);
		}
	}

	/********************************************************************
	 * The modes for {@link MultiFormatDisplay#setDisplayMode(DisplayMode)}.
	 */
	public enum DisplayMode { INTERACTIVE, ACTIVE_MODE_ONLY, ACTIVE_VALUE_ONLY }

	//~ Static fields/initializers ---------------------------------------------

	private static final String STYLE_NAME_ACTIVE = "active";

	private static final StyleData MODE_BUTTON_STYLE =
		DEFAULT.set(BUTTON_STYLE, ButtonStyle.LINK)
			   .set(HORIZONTAL_ALIGN, Alignment.CENTER);

	private static final StyleData VALUE_STYLE =
		DEFAULT.set(HORIZONTAL_ALIGN, Alignment.END);

	//~ Instance fields --------------------------------------------------------

	private F[]		    aFormats;
	private int		    nActiveFormat = 0;
	private DisplayMode eDisplayMode  = DisplayMode.INTERACTIVE;

	private Button[] aFormatButtons;
	private Label[]  aValueLabels;

	private Set<Consumer<F>> aFormatChangeListeners;

	//~ Constructors -----------------------------------------------------------

	/***************************************
	 * Creates a new instance.
	 *
	 * @param rModes The conversion functions of the displays modes
	 */
	@SafeVarargs
	public MultiFormatDisplay(F... rModes)
	{
		super(grid("auto 1fr"), false);

		this.aFormats = rModes;
	}

	//~ Methods ----------------------------------------------------------------

	/***************************************
	 * Returns the format that is currently active.
	 *
	 * @return The active format
	 */
	public F getActiveFormat()
	{
		return aFormats[nActiveFormat];
	}

	/***************************************
	 * Returns the current display mode.
	 *
	 * @return The display mode
	 */
	public DisplayMode getDisplayMode()
	{
		return eDisplayMode;
	}

	/***************************************
	 * Registers a handler for changes of the active format. The argument
	 * function will receive the new active format as it's input.
	 *
	 * @param fListener The format change event handler
	 */
	public void onFormatChanged(Consumer<F> fListener)
	{
		if (aFormatChangeListeners == null)
		{
			aFormatChangeListeners = new HashSet<>(1);
		}

		aFormatChangeListeners.add(fListener);
	}

	/***************************************
	 * Removes a format change listener that has previously been registered with
	 * {@link #onFormatChanged(Consumer)}.
	 *
	 * @param fListener The listener to remove
	 */
	public void removeFormatChangeListener(Consumer<F> fListener)
	{
		if (aFormatChangeListeners != null)
		{
			aFormatChangeListeners.remove(fListener);
		}
	}

	/***************************************
	 * Sets the active format by it's index.
	 *
	 * @param nFormat The index of the new active format
	 */
	public void setActive(int nFormat)
	{
		aFormatButtons[nActiveFormat].removeStyleName(STYLE_NAME_ACTIVE);
		aValueLabels[nActiveFormat].removeStyleName(STYLE_NAME_ACTIVE);

		nActiveFormat = nFormat;

		aFormatButtons[nActiveFormat].addStyleName(STYLE_NAME_ACTIVE);
		aValueLabels[nActiveFormat].addStyleName(STYLE_NAME_ACTIVE);

		if (aFormatChangeListeners != null)
		{
			F rActiveMode = getActiveFormat();

			aFormatChangeListeners.forEach(l -> l.accept(rActiveMode));
		}
	}

	/***************************************
	 * Sets the active format.
	 *
	 * @param rFormat The new active format
	 */
	public void setActiveFormat(F rFormat)
	{
		for (int i = 0; i < aFormats.length; i++)
		{
			if (aFormats[i] == rFormat)
			{
				setActive(i);

				break;
			}
		}
	}

	/***************************************
	 * Sets the mode of this display. The default is {@link
	 * DisplayMode#INTERACTIVE}.
	 *
	 * @param eMode The display mode
	 */
	public void setDisplayMode(DisplayMode eMode)
	{
		eDisplayMode = eMode;

		for (int i = 0; i < aFormats.length; i++)
		{
			boolean bVisible =
				eMode == DisplayMode.INTERACTIVE || i == nActiveFormat;

			aFormatButtons[i].setVisible(bVisible &&
										 eMode != DisplayMode.ACTIVE_VALUE_ONLY);
			aValueLabels[i].setVisible(bVisible);

			if (i == nActiveFormat && eMode == DisplayMode.ACTIVE_VALUE_ONLY)
			{
				// only set visibility to keep layout structure
				aFormatButtons[i].setVisible(true);
				aFormatButtons[i].setVisibility(false);
			}
		}
	}

	/***************************************
	 * Updates the display from a new value.
	 *
	 * @param rNewValue The new value
	 */
	public void update(T rNewValue)
	{
		for (int i = 0; i < aFormats.length; i++)
		{
			String  sFormatted = aFormats[i].apply(rNewValue);
			boolean bVisible   = sFormatted != null;

			aFormatButtons[i].setVisible(bVisible);
			aValueLabels[i].setVisible(bVisible);
			aValueLabels[i].setText(sFormatted);
		}
	}

	/***************************************
	 * {@inheritDoc}
	 */
	@Override
	protected void build(ContainerBuilder<?> rBuilder)
	{
		int nCount = aFormats.length;

		aFormatButtons = new Button[nCount];
		aValueLabels   = new Label[nCount];

		String sLabelPrefix = "$btn" + aFormats[0].getClass().getSimpleName();

		for (int i = 0; i < nCount; i++)
		{
			Button aFormatButton =
				rBuilder.addButton(MODE_BUTTON_STYLE,
								   sLabelPrefix +
								   capitalizedIdentifier(aFormats[i].toString()));

			Label aValueLabel = rBuilder.addLabel(VALUE_STYLE, "");
			int   nMode		  = i;

			aFormatButtons[i] = aFormatButton;
			aValueLabels[i]   = aValueLabel;

			aValueLabel.addStyleName("value");
			aFormatButton.addStyleName("mode");
			aFormatButton.addEventListener(EventType.ACTION,
										   e -> setActive(nMode));
		}

		setActive(nActiveFormat);
	}
}
