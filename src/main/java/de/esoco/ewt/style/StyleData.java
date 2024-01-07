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

import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasHorizontalAlignment.HorizontalAlignmentConstant;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment.VerticalAlignmentConstant;
import com.google.gwt.user.client.ui.TextBoxBase;
import com.google.gwt.user.client.ui.ValueBoxBase.TextAlignment;
import de.esoco.lib.property.AbstractStringProperties;
import de.esoco.lib.property.Alignment;
import de.esoco.lib.property.HasProperties;
import de.esoco.lib.property.PropertyName;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import static de.esoco.lib.property.StyleProperties.CSS_STYLES;

/**
 * This class contains style data that controls the display of a component. It
 * consists of several appearance attributes and visual constraints. Which of
 * these attributes are actually applied depends on the respective component and
 * the layout of it's parent container.
 *
 * <p>Instances of this class are immutable so that it is possible to create
 * constants from it and to re-use StyleData objects. There are also several
 * copy methods that return a new instance which has certain fields modified
 * (e.g. the methods {@link #merge(StyleData)},
 * {@link #setFlags(StyleFlag...)})). The possible flags in a style data object
 * are defined as constants in the class {@link StyleFlag}.</p>
 *
 * <p>The style data constant {@link #DEFAULT} should be used in all cases
 * where no specific style is needed. This constant is also used internally by
 * GEWT and in cases where a NULL value is set as the style data.</p>
 *
 * @author eso
 */
public class StyleData extends AbstractStringProperties {

	/**
	 * Default StyleData constant
	 */
	public static final StyleData DEFAULT = new StyleData();

	/**
	 * Style name: The HTML style of a component
	 */
	public static final PropertyName<String> WEB_STYLE =
		PropertyName.newStringName("WEB_STYLE");

	/**
	 * A space-separated list of additional HTML styles of a component
	 */
	public static final PropertyName<String> WEB_ADDITIONAL_STYLES =
		PropertyName.newStringName("WEB_ADDITIONAL_STYLES");

	/**
	 * Style name: The dependent HTML style of a component (will be appended to
	 * the main style of a component to create an additional derived style
	 * name)
	 */
	public static final PropertyName<String> WEB_DEPENDENT_STYLE =
		PropertyName.newStringName("WEB_DEPENDENT_STYLE");

	private static final long serialVersionUID = 1L;

	private Alignment horizontalAlign = Alignment.FILL;

	private Alignment verticalAlign = Alignment.FILL;

	private double x = 0;

	private double y = 0;

	private double w = 1;

	private double h = 1;

	private Set<StyleFlag> flags = EnumSet.noneOf(StyleFlag.class);

	/**
	 * Creates a style data object with particular horizontal and vertical
	 * alignments.
	 *
	 * @param horizontalAlignment The horizontal Alignment
	 * @param verticalAlignment   The vertical Alignment
	 */
	public StyleData(Alignment horizontalAlignment,
		Alignment verticalAlignment) {
		this.horizontalAlign = horizontalAlignment;
		this.verticalAlign = verticalAlignment;
	}

	/**
	 * Internal constructor for a new style data object with certain flags.
	 */
	StyleData() {
	}

	/**
	 * Internal copy constructor.
	 *
	 * @param other The StyleData object to copy
	 */
	private StyleData(StyleData other) {
		x = other.x;
		y = other.y;
		w = other.w;
		h = other.h;
		horizontalAlign = other.horizontalAlign;
		verticalAlign = other.verticalAlign;
		flags = other.flags;

		setPropertyMap(other.getPropertyMap());
	}

	/**
	 * Convenience method that uses a space character as the separator.
	 *
	 * @see #append(PropertyName, String, String)
	 */
	public StyleData append(PropertyName<String> name, String value) {
		return append(name, value, " ");
	}

	/**
	 * Appends (or sets) a value to a certain string property in a copy of this
	 * style data object. If the value to append contains the given separator
	 * character it's distinct parts will be appended separately and only if
	 * they do not already occur in the current value.
	 *
	 * @param name      The name of the string property to append to
	 * @param value     The string to append
	 * @param separator The separator string between an existing value and the
	 *                  new one
	 * @return A copy of this instance with the given property changed
	 */
	public StyleData append(PropertyName<String> name, String value,
		String separator) {
		String currentValue = getProperty(name, "");

		value = value.trim();

		if (currentValue.length() > 0) {
			Set<String> currentValues = new LinkedHashSet<>(5);

			Collections.addAll(currentValues, currentValue.split(separator));

			StringBuilder result = new StringBuilder(currentValue);

			for (String newValue : value.split(separator)) {
				if (!currentValues.contains(newValue)) {
					result.append(separator).append(newValue);
				}
			}

			value = result.toString();
		}

		return set(name, value);
	}

	/**
	 * Convenience method to calculate an aligned position for the horizontal
	 * alignment.
	 *
	 * @see Alignment#calcAlignedPosition(int, int, int)
	 */
	public final int calcHorizontalPosition(int start, int fullSize,
		int alignSize) {
		return horizontalAlign.calcAlignedPosition(start, fullSize, alignSize);
	}

	/**
	 * Convenience method to calculate an aligned position for the vertical
	 * alignment.
	 *
	 * @see Alignment#calcAlignedPosition(int, int, int)
	 */
	public final int calcVerticalPosition(int start, int fullSize,
		int alignSize) {
		return verticalAlign.calcAlignedPosition(start, fullSize, alignSize);
	}

	/**
	 * Sets a CSS style property.
	 *
	 * @param cssProperty The name of the CSS property
	 * @param value       The value of the CSS property or NULL to clear
	 * @return A new style data instance with the modified CSS property
	 */
	@SuppressWarnings("unchecked")
	public final StyleData css(String cssProperty, String value) {
		StyleData copy = new StyleData(this);

		Map<String, String> cssStyles = copy.getProperty(CSS_STYLES, null);

		if (cssStyles == null && value != null) {
			cssStyles = new HashMap<>();
		}

		if (cssStyles != null) {
			if (value != null) {
				cssStyles.put(cssProperty, value);
			} else {
				cssStyles.remove(cssProperty);
			}
		}

		copy.setProperty(CSS_STYLES, cssStyles);

		return copy;
	}

	/**
	 * Returns the count of vertical cells that the component spans.
	 *
	 * @return The vertical span
	 */
	public final double getHeight() {
		return h;
	}

	/**
	 * Returns the horizontal alignment. The default value is
	 * {@link Alignment#FILL}.
	 *
	 * @return The horizontal alignment
	 */
	public final Alignment getHorizontalAlignment() {
		return horizontalAlign;
	}

	/**
	 * Returns the vertical alignment. The default value is
	 * {@link Alignment#FILL}.
	 *
	 * @return The vertical alignment
	 */
	public final Alignment getVerticalAlignment() {
		return verticalAlign;
	}

	/**
	 * Returns the width of the component. The default value is 1.
	 *
	 * @return The width
	 */
	public final double getWidth() {
		return w;
	}

	/**
	 * Returns the horizontal position. The default value is 0.
	 *
	 * @return The horizontal position
	 */
	public final double getX() {
		return x;
	}

	/**
	 * Returns the vertical position. The default value is 0.
	 *
	 * @return The vertical position
	 */
	public final double getY() {
		return y;
	}

	/**
	 * Returns a copy of this instance with the height set to the argument
	 * value.
	 *
	 * @param h The new height
	 * @return A new StyleData instance
	 */
	public final StyleData h(double h) {
		return xywh(x, y, w, h);
	}

	/**
	 * Returns TRUE the argument style flag has been set in this style data.
	 *
	 * @param flag The flag to check for
	 * @return TRUE if the flags is set
	 */
	public final boolean hasFlag(StyleFlag flag) {
		return flags.contains(flag);
	}

	/**
	 * Maps the horizontal alignment style flags in this instance to the
	 * corresponding GWT constant as defined in {@link HasHorizontalAlignment}.
	 *
	 * @return The corresponding GWT horizontal alignment constant or NULL if
	 * this instance contains no horizontal alignment flag
	 */
	public HorizontalAlignmentConstant mapHorizontalAlignment() {
		HorizontalAlignmentConstant alignment = null;

		if (hasFlag(StyleFlag.HORIZONTAL_ALIGN_LEFT)) {
			alignment = HasHorizontalAlignment.ALIGN_LEFT;
		} else if (hasFlag(StyleFlag.HORIZONTAL_ALIGN_CENTER)) {
			alignment = HasHorizontalAlignment.ALIGN_CENTER;
		} else if (hasFlag(StyleFlag.HORIZONTAL_ALIGN_RIGHT)) {
			alignment = HasHorizontalAlignment.ALIGN_RIGHT;
		}

		return alignment;
	}

	/**
	 * Maps the horizontal alignment style flags in this instance to the
	 * corresponding GWT constant as defined in {@link TextBoxBase}.
	 *
	 * @return The corresponding GWT horizontal alignment constant or NULL if
	 * this instance contains no horizontal alignment flag
	 */
	public TextAlignment mapTextAlignment() {
		TextAlignment alignment = null;

		if (hasFlag(StyleFlag.HORIZONTAL_ALIGN_LEFT)) {
			alignment = TextAlignment.LEFT;
		} else if (hasFlag(StyleFlag.HORIZONTAL_ALIGN_CENTER)) {
			alignment = TextAlignment.CENTER;
		} else if (hasFlag(StyleFlag.HORIZONTAL_ALIGN_RIGHT)) {
			alignment = TextAlignment.RIGHT;
		}

		return alignment;
	}

	/**
	 * Maps the vertical alignment style flags in this instance to the
	 * corresponding GWT constant as defined in {@link HasVerticalAlignment}.
	 *
	 * @return The corresponding GWT vertical alignment constant or NULL if
	 * this
	 * instance contains no vertical alignment flag
	 */
	public VerticalAlignmentConstant mapVerticalAlignment() {
		VerticalAlignmentConstant alignment = null;

		if (hasFlag(StyleFlag.VERTICAL_ALIGN_BOTTOM)) {
			alignment = HasVerticalAlignment.ALIGN_BOTTOM;
		} else if (hasFlag(StyleFlag.VERTICAL_ALIGN_CENTER)) {
			alignment = HasVerticalAlignment.ALIGN_MIDDLE;
		} else if (hasFlag(StyleFlag.VERTICAL_ALIGN_TOP)) {
			alignment = HasVerticalAlignment.ALIGN_TOP;
		}

		return alignment;
	}

	/**
	 * Returns a copy of this instance where all default values in the copy
	 * will
	 * be merged in from the other instance.
	 *
	 * @param other The other instance to merge the values from
	 * @return A new StyleData instance
	 */
	public final StyleData merge(StyleData other) {
		StyleData copy = new StyleData(this);

		if (horizontalAlign == DEFAULT.horizontalAlign) {
			copy.horizontalAlign = other.horizontalAlign;
		}

		if (verticalAlign == DEFAULT.verticalAlign) {
			copy.verticalAlign = other.verticalAlign;
		}

		if (x == DEFAULT.x) {
			copy.x = other.x;
		}

		if (y == DEFAULT.y) {
			copy.y = other.y;
		}

		if (w == DEFAULT.w) {
			copy.w = other.w;
		}

		if (h == DEFAULT.h) {
			copy.h = other.h;
		}

		copy.addStyleFlags(other.flags);
		copy.addPropertyMap(other.getPropertyMap());

		return copy;
	}

	/**
	 * Returns a copy of this style data object with an implementation-specific
	 * style value set. The meaning of such styles depends on the actual user
	 * interface toolkit of the current EWT implementation. A style that is not
	 * known by a certain implementation will be ignored by it.
	 *
	 * <p>To prevent different EWT implementations from confusing their styles
	 * the keys for a certain implementations should always start with an
	 * implementation-specific prefixes that are separated by dots, like
	 * 'Swing.LayoutKey'.</p>
	 *
	 * @param name  The property name of the style
	 * @param value The style value
	 * @return A copy of this instance with the given style set
	 */
	public <T> StyleData set(PropertyName<T> name, T value) {
		StyleData copy = new StyleData(this);

		Map<PropertyName<?>, String> propertyMap = getPropertyMap();

		if (propertyMap != null) {
			copy.setPropertyMap(
				new HashMap<PropertyName<?>, String>(propertyMap));
		}

		copy.setProperty(name, value);

		return copy;
	}

	/**
	 * Returns a Copy of this style data object that has a certain style flag
	 * set. For compatibility with standard EWT this method exists in variants
	 * with different argument counts. This is because the argument type in EWT
	 * is a variable length array of StyleFlag objects instead of integers.
	 *
	 * <p>To retain compatibility with EWT it is therefore recommended to use
	 * multiple arguments instead of using a single integer that is the logical
	 * combination of multiple integer flags (which would only work in
	 * microEWT).</p>
	 *
	 * @param styleFlags The style flag to set
	 * @return A new instance that as a copy of this one that contains the
	 * given
	 * flag
	 */
	public final StyleData setFlags(StyleFlag... styleFlags) {
		StyleData copy = new StyleData(this);

		copy.flags = EnumSet.copyOf(flags);
		copy.flags.addAll(Arrays.asList(styleFlags));
		return copy;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		Map<PropertyName<?>, String> propertyMap = getPropertyMap();

		String properties = propertyMap != null && propertyMap.size() > 0 ?
		                    "," + propertyMap :
		                    "";

		String flags = this.flags.size() > 0 ? "," + this.flags : "";

		return getClass().getSimpleName() + "[" + x + "," + y + "," + w + "," +
			h + "," + horizontalAlign + "," + verticalAlign + flags +
			properties + "]";
	}

	/**
	 * Returns a copy of this instance with the width set to the argument
	 * value.
	 *
	 * @param w The new width
	 * @return A new StyleData instance
	 */
	public final StyleData w(double w) {
		return xywh(x, y, w, h);
	}

	/**
	 * Returns a copy of this instance with size set to the argument values.
	 *
	 * @param w The new width
	 * @param h The new height
	 * @return A new StyleData instance
	 */
	public final StyleData wh(double w, double h) {
		return xywh(x, y, w, h);
	}

	/**
	 * Returns a copy of this style data object with the give properties copied
	 * from an instance {@link HasProperties}.
	 *
	 * @param source        The properties object to copy the properties from
	 * @param propertyNames The names of the properties to copy
	 * @return A copy of this instance that contains the given properties (if
	 * they are available in the source properties)
	 */
	@SuppressWarnings("unchecked")
	public StyleData withProperties(HasProperties source,
		Collection<PropertyName<?>> propertyNames) {
		StyleData copy = new StyleData(this);

		Map<PropertyName<?>, String> propertyMap = getPropertyMap();

		if (propertyMap != null) {
			copy.setPropertyMap(
				new HashMap<PropertyName<?>, String>(propertyMap));
		}

		for (PropertyName<?> propertyName : propertyNames) {
			Object property = source.getProperty(propertyName, null);

			if (property != null) {
				copy.setProperty((PropertyName<Object>) propertyName,
					property);
			}
		}

		return copy;
	}

	/**
	 * Returns a copy of this instance with the X coordinate set to the
	 * argument
	 * value.
	 *
	 * @param x The new horizontal position
	 * @return A new StyleData instance
	 */
	public final StyleData x(double x) {
		return xywh(x, y, w, h);
	}

	/**
	 * Returns a copy of this instance with the position set to the argument
	 * values.
	 *
	 * @param x The new horizontal position
	 * @param y The new vertical position
	 * @return A new StyleData instance
	 */
	public final StyleData xy(double x, double y) {
		return xywh(x, y, w, h);
	}

	/**
	 * Returns a copy of this instance with the position and size set to the
	 * argument values. <b>Attention</b>: the argument values will be converted
	 * to short values and must therefore not exceed the range of the short
	 * type.
	 *
	 * @param x The new horizontal position
	 * @param y The new vertical position
	 * @param w The new width
	 * @param h The new height
	 * @return A new StyleData instance
	 */
	public final StyleData xywh(double x, double y, double w, double h) {
		StyleData copy = new StyleData(this);

		copy.x = x;
		copy.y = y;
		copy.w = w;
		copy.h = h;

		return copy;
	}

	/**
	 * Returns a copy of this instance with the Y coordinate set to the
	 * argument
	 * value.
	 *
	 * @param y The new vertical position
	 * @return A new StyleData instance
	 */
	public final StyleData y(double y) {
		return xywh(x, y, w, h);
	}

	/**
	 * Internal method to add properties to this instance. It automatically
	 * creates a new property map if necessary to ensure the immutability of
	 * style data instances.
	 *
	 * @param propertyMap A map of properties to be added to this instance
	 */
	private final void addPropertyMap(
		Map<PropertyName<?>, String> propertyMap) {
		if (getPropertyCount() == 0) {
			setPropertyMap(propertyMap);
		} else if (propertyMap != null && propertyMap.size() > 0) {
			HashMap<PropertyName<?>, String> newPropertyMap =
				new HashMap<PropertyName<?>, String>(getPropertyMap());

			newPropertyMap.putAll(propertyMap);
			setPropertyMap(newPropertyMap);
		}
	}

	/**
	 * Internal method to add style flags to this instance. It automatically
	 * creates a new set of flags if necessary to ensure the immutability of
	 * style data instances.
	 *
	 * @param styleFlags A set of style flags to be added to this instance
	 */
	private final void addStyleFlags(Set<StyleFlag> styleFlags) {
		if (flags.size() == 0) {
			flags = styleFlags;
		} else if (styleFlags.size() > 0) {
			flags = new HashSet<StyleFlag>(flags);
			flags.addAll(styleFlags);
			flags = EnumSet.copyOf(flags);
		}
	}
}
