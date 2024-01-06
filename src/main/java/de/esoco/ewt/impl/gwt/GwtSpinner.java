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

import de.esoco.ewt.impl.gwt.ValueBoxConstraint.IntRangeConstraint;

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

/**
 * A component that allows to enter or modify integer values.
 *
 * @author eso
 */
public class GwtSpinner extends Composite
	implements Focusable, HasEnabled, HasValueChangeHandlers<Integer> {
	private static final GewtResources RES = GewtResources.INSTANCE;

	private static final GewtCss CSS = RES.css();

	private int nMinimumValue;

	private int nMaximumValue;

	private int nValueIncrement;

	private ValueBox<Integer> aInputField;

	private Timer aEventDelayTimer;

	/**
	 * Creates a new instance.
	 *
	 * @param nMinimum   The minimum input value
	 * @param nMaximum   The maximum input value
	 * @param nIncrement The increment or decrement for value modifications
	 */
	public GwtSpinner(int nMinimum, int nMaximum, int nIncrement) {
		this(nMinimum, nMaximum, nIncrement, new IntegerBox());
	}	private PushButton aIncrementButton =
		createSpinButton(RES.imArrowUp(), RES.imArrowUpPressed(),
			RES.imArrowUpHover(), RES.imArrowUpDisabled());

	/**
	 * Creates a new instance.
	 *
	 * @param nMinimum    The minimum input value
	 * @param nMaximum    The maximum input value
	 * @param nIncrement  The increment or decrement for value modifications
	 * @param rInputField The value input box
	 */
	@SuppressWarnings("boxing")
	GwtSpinner(int nMinimum, int nMaximum, int nIncrement,
		ValueBox<Integer> rInputField) {
		this.nMinimumValue = nMinimum;
		this.nMaximumValue = nMaximum;
		this.nValueIncrement = nIncrement;
		this.aInputField = rInputField;

		HorizontalPanel aMainPanel = new HorizontalPanel();
		VerticalPanel aButtonPanel = new VerticalPanel();

		aButtonPanel.setStyleName(CSS.ewtSpinnerButtons());
		aInputField.setStyleName(CSS.ewtSpinnerInput());
		aInputField.setValue(nMinimum);
		aInputField.setAlignment(TextAlignment.CENTER);

		// subtract 1 to reduce size
		aInputField.setVisibleLength(Integer.toString(nMaximum).length() - 1);
		aInputField.addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent rEvent) {
				handleKeySpin(rEvent);
			}
		});
		aInputField.addValueChangeHandler(new ValueChangeHandler<Integer>() {
			@Override
			public void onValueChange(ValueChangeEvent<Integer> rEvent) {
				checkValue(rEvent.getValue(), nValueIncrement, false);
			}
		});

		aInputField.addKeyPressHandler(
			new IntRangeConstraint(nMinimum, nMaximum));
		aButtonPanel.add(aIncrementButton);
		aButtonPanel.add(aDecrementButton);
		aMainPanel.add(aInputField);
		aMainPanel.add(aButtonPanel);

		aButtonPanel.setHorizontalAlignment(VerticalPanel.ALIGN_CENTER);
		aButtonPanel.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
		aMainPanel.setHorizontalAlignment(VerticalPanel.ALIGN_CENTER);
		aMainPanel.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
		aMainPanel.setCellHorizontalAlignment(aButtonPanel,
			HorizontalPanel.ALIGN_RIGHT);

		aMainPanel.setCellWidth(aInputField, "100%");

		initWidget(aMainPanel);
		setStylePrimaryName(CSS.ewtSpinner());
	}	private PushButton aDecrementButton =
		createSpinButton(RES.imArrowDown(), RES.imArrowDownPressed(),
			RES.imArrowDownHover(), RES.imArrowDownDisabled());

	/**
	 * @see HasValueChangeHandlers#addValueChangeHandler(ValueChangeHandler)
	 */
	@Override
	public HandlerRegistration addValueChangeHandler(
		ValueChangeHandler<Integer> rHandler) {
		return addHandler(rHandler, ValueChangeEvent.getType());
	}

	/**
	 * Returns the increment for value modifications.
	 *
	 * @return The increment value
	 */
	public final int getIncrement() {
		return nValueIncrement;
	}

	/**
	 * Returns the maximum value.
	 *
	 * @return The maximum value
	 */
	public final int getMaximum() {
		return nMaximumValue;
	}

	/**
	 * Returns the minimum value.
	 *
	 * @return The minimum value
	 */
	public final int getMinimum() {
		return nMinimumValue;
	}

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
		return aInputField.getValue().intValue();
	}

	/**
	 * @see HasEnabled#isEnabled()
	 */
	@Override
	public boolean isEnabled() {
		return aInputField.isEnabled();
	}

	/**
	 * @see Focusable#setAccessKey(char)
	 */
	@Override
	public void setAccessKey(char cKey) {
		aInputField.setAccessKey(cKey);
	}

	/**
	 * @see HasEnabled#setEnabled(boolean)
	 */
	@Override
	public void setEnabled(boolean bEnabled) {
		aInputField.setEnabled(bEnabled);
		aIncrementButton.setEnabled(bEnabled);
		aDecrementButton.setEnabled(bEnabled);
	}

	/**
	 * @see Focusable#setFocus(boolean)
	 */
	@Override
	public void setFocus(boolean bFocused) {
		aInputField.setFocus(bFocused);
	}

	/**
	 * Sets the increment for value modifications.
	 *
	 * @param rIncrement The increment value
	 */
	public final void setIncrement(int rIncrement) {
		nValueIncrement = rIncrement;
	}

	/**
	 * Sets the maximum value.
	 *
	 * @param rMaximum The maximum value
	 */
	public final void setMaximum(int rMaximum) {
		nMaximumValue = rMaximum;
	}

	/**
	 * Sets the minimum value.
	 *
	 * @param rMinimum The minimum value
	 */
	public final void setMinimum(int rMinimum) {
		nMinimumValue = rMinimum;
	}

	/**
	 * @see Focusable#setTabIndex(int)
	 */
	@Override
	public void setTabIndex(int nIndex) {
		aInputField.setTabIndex(nIndex);
	}

	/**
	 * Sets the value of this widget.
	 *
	 * @param nValue The new value
	 */
	@SuppressWarnings("boxing")
	public final void setValue(int nValue) {
		aInputField.setValue(nValue);
	}

	/**
	 * Checks and performs value spinning based on a pressed key.
	 *
	 * @param rEvent The key event
	 */
	protected void handleKeySpin(KeyUpEvent rEvent) {
		int nValueRange = nMaximumValue - nMinimumValue + 1;
		int nIncrement = 1;

		if (rEvent.isAltKeyDown()) {
			nIncrement = nValueRange / 2;
		} else if (rEvent.isControlKeyDown()) {
			nIncrement = nValueRange / 4;
		} else if (rEvent.isShiftKeyDown()) {
			nIncrement = nValueIncrement;

			if (rEvent.isControlKeyDown()) {
				nIncrement *= 2;
			}
		}

		if (rEvent.isUpArrow()) {
			spinValue(nIncrement, true);
		} else if (rEvent.isDownArrow()) {
			spinValue(nIncrement, false);
		}
	}

	/**
	 * Checks a value against the minimum and maximum values of this instance,
	 * corrects it if necessary, and sets it on the input field.
	 *
	 * @param rValue     The value to check
	 * @param nIncrement The increment the value has been changed by
	 * @param bWrap      TRUE if the value should be wrapped around to the
	 *                   respective other boundary or just be limited by the
	 *                   boundary values
	 */
	@SuppressWarnings("boxing")
	private void checkValue(Integer rValue, int nIncrement, boolean bWrap) {
		int nValue = 0;

		if (rValue != null) {
			nValue = rValue.intValue();
		} else {
			nValue = bWrap ? nMaximumValue + 1 : nMinimumValue;
		}

		if (nValue > nMaximumValue) {
			nValue = bWrap ? nMinimumValue : nMaximumValue;
		} else if (nValue < nMinimumValue) {
			nValue =
				bWrap ? nMaximumValue / nIncrement * nIncrement :
				nMinimumValue;
		}

		aInputField.setValue(nValue);

		if (aEventDelayTimer == null) {
			aEventDelayTimer = new Timer() {
				@Override
				public void run() {
					ValueChangeEvent.fire(GwtSpinner.this,
						aInputField.getValue());
				}
			};
		}

		aEventDelayTimer.schedule(200);
	}

	/**
	 * Helper method to create a new button with certain images.
	 *
	 * @param rImUp       The up (normal) state image
	 * @param rImPressed  The pressed state image
	 * @param rImHover    The hover state image
	 * @param rImDisabled The disabled state image
	 * @return The new button
	 */
	private PushButton createSpinButton(ImageResource rImUp,
		ImageResource rImPressed, ImageResource rImHover,
		ImageResource rImDisabled) {
		PushButton aButton =
			new PushButton(new Image(rImUp), new Image(rImPressed));

		Image rHoverImage = new Image(rImHover);
		Image rDisabledImage = new Image(rImDisabled);

		aButton.getUpHoveringFace().setImage(rHoverImage);
		aButton.getDownHoveringFace().setImage(rHoverImage);
		aButton.getUpDisabledFace().setImage(rDisabledImage);
		aButton.getDownDisabledFace().setImage(rDisabledImage);

		aButton.setStyleName(CSS.ewtSpinnerButton());
		aButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent rEvent) {
				spinValue(nValueIncrement,
					rEvent.getSource() == aIncrementButton);
			}
		});

		return aButton;
	}

	/**
	 * Performs the value changed caused by spinner buttons or keys.
	 *
	 * @param nIncrement The increment to spin the value by
	 * @param bUp        TRUE to spin up, FALSE for down
	 */
	@SuppressWarnings("boxing")
	private void spinValue(int nIncrement, boolean bUp) {
		int nValue = aInputField.getValue();

		nValue += bUp ? nIncrement : -nIncrement;

		checkValue(Integer.valueOf(nValue), nIncrement, true);
	}




}
