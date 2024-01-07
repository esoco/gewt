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

/**
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
 * <p>If the display mode is {@link DisplayMode#INTERACTIVE} the user can
 * select an active format which can then be rendered differently (by providing
 * the appropriate CSS). It is also possible to set and query the currently
 * active format ({@link #getActiveFormat()}) and to register listeners for
 * changes of the active format ({@link #onFormatChanged(Consumer)}).</p>
 *
 * @author eso
 */
public class MultiFormatDisplay<T, F extends Function<T, String>>
	extends Composite {

	/**
	 * The modes for {@link MultiFormatDisplay#setDisplayMode(DisplayMode)}.
	 */
	public enum DisplayMode {INTERACTIVE, ACTIVE_MODE_ONLY, ACTIVE_VALUE_ONLY}

	/**
	 * Example display formats for the rendering of {@link BigDecimal} values.
	 * All formats other than {@link #DECIMAL} only render the integer part
	 * of a
	 * value. Therefore, for a consistent representation the displayed input
	 * value should have no fraction.
	 */
	public enum NumberDisplayFormat implements Function<BigDecimal, String> {
		DECIMAL(BigDecimal::toPlainString), HEXADECIMAL(d -> format(d, 16,
			-4)),
		OCTAL(d -> format(d, 8, -3)), BINARY(d -> format(d, 2, -4));

		private final Function<BigDecimal, String> formatValue;

		/**
		 * Creates a new instance.
		 *
		 * @param format The value formatting function
		 */
		NumberDisplayFormat(Function<BigDecimal, String> format) {
			formatValue = format;
		}

		/**
		 * Helper method to format decimal values without a scale with a
		 * certain
		 * (non-decimal) radix.
		 *
		 * @param value     The value to format
		 * @param radix     The radix
		 * @param chunkSize The chunk size at which to interleave the formatted
		 *                  string with spaces
		 * @return The formatted string value or NULL if the scale of the input
		 * value is not zero
		 */
		private static String format(BigDecimal value, int radix,
			int chunkSize) {
			String formatted = null;

			if (value.scale() == 0) {
				formatted =
					interleave(value.toBigInteger().toString(radix), " ",
						chunkSize);
			}

			return formatted;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String apply(BigDecimal value) {
			return formatValue.apply(value);
		}
	}

	private static final String STYLE_NAME_ACTIVE = "active";

	private static final StyleData MODE_BUTTON_STYLE = DEFAULT
		.set(BUTTON_STYLE, ButtonStyle.LINK)
		.set(HORIZONTAL_ALIGN, Alignment.CENTER);

	private static final StyleData VALUE_STYLE =
		DEFAULT.set(HORIZONTAL_ALIGN, Alignment.END);

	private final F[] formats;

	private int activeFormat = 0;

	private DisplayMode displayMode = DisplayMode.INTERACTIVE;

	private Button[] formatButtons;

	private Label[] valueLabels;

	private Set<Consumer<F>> formatChangeListeners;

	/**
	 * Creates a new instance.
	 *
	 * @param modes The conversion functions of the displays modes
	 */
	@SafeVarargs
	public MultiFormatDisplay(F... modes) {
		super(grid("auto 1fr"));

		this.formats = modes;
	}

	/**
	 * Returns the format that is currently active.
	 *
	 * @return The active format
	 */
	public F getActiveFormat() {
		return formats[activeFormat];
	}

	/**
	 * Returns the string value that of the currently active format.
	 *
	 * @return The string value for the active format
	 */
	public String getActiveValue() {
		return valueLabels[activeFormat].getText();
	}

	/**
	 * Returns the current display mode.
	 *
	 * @return The display mode
	 */
	public DisplayMode getDisplayMode() {
		return displayMode;
	}

	/**
	 * Registers a handler for changes of the active format. The argument
	 * function will receive the new active format as it's input.
	 *
	 * @param listener The format change event handler
	 */
	public void onFormatChanged(Consumer<F> listener) {
		if (formatChangeListeners == null) {
			formatChangeListeners = new HashSet<>(1);
		}

		formatChangeListeners.add(listener);
	}

	/**
	 * Removes a format change listener that has previously been registered
	 * with
	 * {@link #onFormatChanged(Consumer)}.
	 *
	 * @param listener The listener to remove
	 */
	public void removeFormatChangeListener(Consumer<F> listener) {
		if (formatChangeListeners != null) {
			formatChangeListeners.remove(listener);
		}
	}

	/**
	 * Sets the active format by it's index.
	 *
	 * @param format The index of the new active format
	 */
	public void setActive(int format) {
		formatButtons[activeFormat].removeStyleName(STYLE_NAME_ACTIVE);
		valueLabels[activeFormat].removeStyleName(STYLE_NAME_ACTIVE);

		activeFormat = format;

		formatButtons[activeFormat].addStyleName(STYLE_NAME_ACTIVE);
		valueLabels[activeFormat].addStyleName(STYLE_NAME_ACTIVE);

		if (formatChangeListeners != null) {
			F activeMode = getActiveFormat();

			formatChangeListeners.forEach(l -> l.accept(activeMode));
		}
	}

	/**
	 * Sets the active format.
	 *
	 * @param format The new active format
	 */
	public void setActiveFormat(F format) {
		for (int i = 0; i < formats.length; i++) {
			if (formats[i] == format) {
				setActive(i);

				break;
			}
		}
	}

	/**
	 * Sets the mode of this display. The default is
	 * {@link DisplayMode#INTERACTIVE}.
	 *
	 * @param mode The display mode
	 */
	public void setDisplayMode(DisplayMode mode) {
		displayMode = mode;

		for (int i = 0; i < formats.length; i++) {
			boolean visible =
				mode == DisplayMode.INTERACTIVE || i == activeFormat;

			formatButtons[i].setVisible(
				visible && mode != DisplayMode.ACTIVE_VALUE_ONLY);
			valueLabels[i].setVisible(visible);

			if (i == activeFormat && mode == DisplayMode.ACTIVE_VALUE_ONLY) {
				// only set visibility to keep layout structure
				formatButtons[i].setVisible(true);
				formatButtons[i].setVisibility(false);
			}
		}
	}

	/**
	 * Updates the display from a new value.
	 *
	 * @param newValue The new value
	 */
	public void update(T newValue) {
		for (int i = 0; i < formats.length; i++) {
			String formatted = formats[i].apply(newValue);
			boolean visible = formatted != null;

			formatButtons[i].setVisible(visible);
			valueLabels[i].setVisible(visible);
			valueLabels[i].setText(formatted);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void build(ContainerBuilder<?> builder) {
		int count = formats.length;

		formatButtons = new Button[count];
		valueLabels = new Label[count];

		String labelPrefix = "$btn" + formats[0].getClass().getSimpleName();

		for (int i = 0; i < count; i++) {
			Button formatButton = builder.addButton(MODE_BUTTON_STYLE,
				labelPrefix + capitalizedIdentifier(formats[i].toString()));

			Label valueLabel = builder.addLabel(VALUE_STYLE, "");
			int mode = i;

			formatButtons[i] = formatButton;
			valueLabels[i] = valueLabel;

			valueLabel.addStyleName("value");
			formatButton.addStyleName("mode");
			formatButton.addEventListener(EventType.ACTION,
				e -> setActive(mode));
		}

		setActive(activeFormat);
	}
}
