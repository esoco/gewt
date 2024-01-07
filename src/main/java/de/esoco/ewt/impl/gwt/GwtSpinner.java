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
package de.esoco.ewt.impl.gwt;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IntegerBox;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.ValueBox;
import com.google.gwt.user.client.ui.ValueBoxBase.TextAlignment;
import com.google.gwt.user.client.ui.VerticalPanel;
import de.esoco.ewt.impl.gwt.ValueBoxConstraint.IntRangeConstraint;

/**
 * A component that allows to enter or modify integer values.
 *
 * @author eso
 */
public class GwtSpinner extends Composite
	implements Focusable, HasEnabled, HasValueChangeHandlers<Integer> {
	private static final GewtResources RES = GewtResources.INSTANCE;

	private static final GewtCss CSS = RES.css();

	private int minimumValue;

	private int maximumValue;

	private int valueIncrement;

	private final ValueBox<Integer> inputField;

	private Timer eventDelayTimer;

	/**
	 * Creates a new instance.
	 *
	 * @param minimum   The minimum input value
	 * @param maximum   The maximum input value
	 * @param increment The increment or decrement for value modifications
	 */
	public GwtSpinner(int minimum, int maximum, int increment) {
		this(minimum, maximum, increment, new IntegerBox());
	}

	/**
	 * Creates a new instance.
	 *
	 * @param minimum    The minimum input value
	 * @param maximum    The maximum input value
	 * @param increment  The increment or decrement for value modifications
	 * @param inputField The value input box
	 */
	@SuppressWarnings("boxing")
	GwtSpinner(int minimum, int maximum, int increment,
		ValueBox<Integer> inputField) {
		this.minimumValue = minimum;
		this.maximumValue = maximum;
		this.valueIncrement = increment;
		this.inputField = inputField;

		HorizontalPanel mainPanel = new HorizontalPanel();
		VerticalPanel buttonPanel = new VerticalPanel();

		buttonPanel.setStyleName(CSS.ewtSpinnerButtons());
		inputField.setStyleName(CSS.ewtSpinnerInput());
		inputField.setValue(minimum);
		inputField.setAlignment(TextAlignment.CENTER);

		// subtract 1 to reduce size
		inputField.setVisibleLength(Integer.toString(maximum).length() - 1);
		inputField.addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				handleKeySpin(event);
			}
		});
		inputField.addValueChangeHandler(new ValueChangeHandler<Integer>() {
			@Override
			public void onValueChange(ValueChangeEvent<Integer> event) {
				checkValue(event.getValue(), valueIncrement, false);
			}
		});

		inputField.addKeyPressHandler(new IntRangeConstraint(minimum,
			maximum));
		buttonPanel.add(incrementButton);
		buttonPanel.add(decrementButton);
		mainPanel.add(inputField);
		mainPanel.add(buttonPanel);

		buttonPanel.setHorizontalAlignment(VerticalPanel.ALIGN_CENTER);
		buttonPanel.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
		mainPanel.setHorizontalAlignment(VerticalPanel.ALIGN_CENTER);
		mainPanel.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
		mainPanel.setCellHorizontalAlignment(buttonPanel,
			HorizontalPanel.ALIGN_RIGHT);

		mainPanel.setCellWidth(inputField, "100%");

		initWidget(mainPanel);
		setStylePrimaryName(CSS.ewtSpinner());
	}

	/**
	 * @see HasValueChangeHandlers#addValueChangeHandler(ValueChangeHandler)
	 */
	@Override
	public HandlerRegistration addValueChangeHandler(
		ValueChangeHandler<Integer> handler) {
		return addHandler(handler, ValueChangeEvent.getType());
	}	private final PushButton incrementButton =
		createSpinButton(RES.imArrowUp(), RES.imArrowUpPressed(),
			RES.imArrowUpHover(), RES.imArrowUpDisabled());

	/**
	 * Returns the increment for value modifications.
	 *
	 * @return The increment value
	 */
	public final int getIncrement() {
		return valueIncrement;
	}

	/**
	 * Returns the maximum value.
	 *
	 * @return The maximum value
	 */
	public final int getMaximum() {
		return maximumValue;
	}

	/**
	 * Returns the minimum value.
	 *
	 * @return The minimum value
	 */
	public final int getMinimum() {
		return minimumValue;
	}	private final PushButton decrementButton =
		createSpinButton(RES.imArrowDown(), RES.imArrowDownPressed(),
			RES.imArrowDownHover(), RES.imArrowDownDisabled());

	/**
	 * @see Focusable#getTabIndex()
	 */
	@Override
	public int getTabIndex() {
		return 0;
	}

	/**
	 * Returns the current value.
	 *
	 * @return The current value
	 */
	public final int getValue() {
		return inputField.getValue().intValue();
	}

	/**
	 * @see HasEnabled#isEnabled()
	 */
	@Override
	public boolean isEnabled() {
		return inputField.isEnabled();
	}

	/**
	 * @see Focusable#setAccessKey(char)
	 */
	@Override
	public void setAccessKey(char key) {
		inputField.setAccessKey(key);
	}

	/**
	 * @see HasEnabled#setEnabled(boolean)
	 */
	@Override
	public void setEnabled(boolean enabled) {
		inputField.setEnabled(enabled);
		incrementButton.setEnabled(enabled);
		decrementButton.setEnabled(enabled);
	}

	/**
	 * @see Focusable#setFocus(boolean)
	 */
	@Override
	public void setFocus(boolean focused) {
		inputField.setFocus(focused);
	}

	/**
	 * Sets the increment for value modifications.
	 *
	 * @param increment The increment value
	 */
	public final void setIncrement(int increment) {
		valueIncrement = increment;
	}

	/**
	 * Sets the maximum value.
	 *
	 * @param maximum The maximum value
	 */
	public final void setMaximum(int maximum) {
		maximumValue = maximum;
	}

	/**
	 * Sets the minimum value.
	 *
	 * @param minimum The minimum value
	 */
	public final void setMinimum(int minimum) {
		minimumValue = minimum;
	}

	/**
	 * @see Focusable#setTabIndex(int)
	 */
	@Override
	public void setTabIndex(int index) {
		inputField.setTabIndex(index);
	}

	/**
	 * Sets the value of this widget.
	 *
	 * @param value The new value
	 */
	@SuppressWarnings("boxing")
	public final void setValue(int value) {
		inputField.setValue(value);
	}

	/**
	 * Checks and performs value spinning based on a pressed key.
	 *
	 * @param event The key event
	 */
	protected void handleKeySpin(KeyUpEvent event) {
		int valueRange = maximumValue - minimumValue + 1;
		int increment = 1;

		if (event.isAltKeyDown()) {
			increment = valueRange / 2;
		} else if (event.isControlKeyDown()) {
			increment = valueRange / 4;
		} else if (event.isShiftKeyDown()) {
			increment = valueIncrement;

			if (event.isControlKeyDown()) {
				increment *= 2;
			}
		}

		if (event.isUpArrow()) {
			spinValue(increment, true);
		} else if (event.isDownArrow()) {
			spinValue(increment, false);
		}
	}

	/**
	 * Checks a value against the minimum and maximum values of this instance,
	 * corrects it if necessary, and sets it on the input field.
	 *
	 * @param value     The value to check
	 * @param increment The increment the value has been changed by
	 * @param wrap      TRUE if the value should be wrapped around to the
	 *                  respective other boundary or just be limited by the
	 *                  boundary values
	 */
	@SuppressWarnings("boxing")
	private void checkValue(Integer value, int increment, boolean wrap) {
		int val = 0;

		if (value != null) {
			val = value;
		} else {
			val = wrap ? maximumValue + 1 : minimumValue;
		}

		if (val > maximumValue) {
			val = wrap ? minimumValue : maximumValue;
		} else if (val < minimumValue) {
			val = wrap ? maximumValue / increment * increment : minimumValue;
		}

		inputField.setValue(val);

		if (eventDelayTimer == null) {
			eventDelayTimer = new Timer() {
				@Override
				public void run() {
					ValueChangeEvent.fire(GwtSpinner.this,
						inputField.getValue());
				}
			};
		}

		eventDelayTimer.schedule(200);
	}

	/**
	 * Helper method to create a new button with certain images.
	 *
	 * @param imUp       The up (normal) state image
	 * @param imPressed  The pressed state image
	 * @param imHover    The hover state image
	 * @param imDisabled The disabled state image
	 * @return The new button
	 */
	private PushButton createSpinButton(ImageResource imUp,
		ImageResource imPressed, ImageResource imHover,
		ImageResource imDisabled) {
		PushButton button =
			new PushButton(new Image(imUp), new Image(imPressed));

		Image hoverImage = new Image(imHover);
		Image disabledImage = new Image(imDisabled);

		button.getUpHoveringFace().setImage(hoverImage);
		button.getDownHoveringFace().setImage(hoverImage);
		button.getUpDisabledFace().setImage(disabledImage);
		button.getDownDisabledFace().setImage(disabledImage);

		button.setStyleName(CSS.ewtSpinnerButton());
		button.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				spinValue(valueIncrement,
					event.getSource() == incrementButton);
			}
		});

		return button;
	}

	/**
	 * Performs the value changed caused by spinner buttons or keys.
	 *
	 * @param increment The increment to spin the value by
	 * @param up        TRUE to spin up, FALSE for down
	 */
	@SuppressWarnings("boxing")
	private void spinValue(int increment, boolean up) {
		int value = inputField.getValue();

		value += up ? increment : -increment;

		checkValue(Integer.valueOf(value), increment, true);
	}





}
