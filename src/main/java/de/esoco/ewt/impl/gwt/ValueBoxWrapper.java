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
package de.esoco.ewt.impl.gwt;

import de.esoco.ewt.component.TextControl.IsTextControlWidget;

import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.ValueBoxBase;
import com.google.gwt.user.client.ui.Widget;

/**
 * A wrapper for {@link ValueBoxBase} implementations that implements the
 * interface {@link IsTextControlWidget}.
 *
 * @author eso
 */
public class ValueBoxWrapper implements IsTextControlWidget {

	private final ValueBoxBase<?> rValueBox;

	/**
	 * Creates a new instance.
	 *
	 * @param rValueBox The value box to wrap
	 */
	public ValueBoxWrapper(ValueBoxBase<?> rValueBox) {
		this.rValueBox = rValueBox;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public HandlerRegistration addDoubleClickHandler(
		DoubleClickHandler rHandler) {
		return rValueBox.addDoubleClickHandler(rHandler);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public HandlerRegistration addKeyDownHandler(KeyDownHandler rHandler) {
		return rValueBox.addKeyDownHandler(rHandler);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public HandlerRegistration addKeyPressHandler(KeyPressHandler rHandler) {
		return rValueBox.addKeyPressHandler(rHandler);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Widget asWidget() {
		return rValueBox;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void fireEvent(GwtEvent<?> rEvent) {
		rValueBox.fireEvent(rEvent);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getCursorPos() {
		return rValueBox.getCursorPos();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getSelectedText() {
		return rValueBox.getSelectedText();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getTabIndex() {
		return rValueBox.getTabIndex();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getText() {
		return rValueBox.getText();
	}

	/**
	 * @see com.google.gwt.user.client.ui.HasEnabled#isEnabled()
	 */
	@Override
	public boolean isEnabled() {
		return rValueBox.isEnabled();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isReadOnly() {
		return rValueBox.isReadOnly();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setAccessKey(char cKey) {
		rValueBox.setAccessKey(cKey);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setCursorPos(int nPosition) {
		rValueBox.setCursorPos(nPosition);
	}

	/**
	 * @see com.google.gwt.user.client.ui.HasEnabled#setEnabled(boolean)
	 */
	@Override
	public void setEnabled(boolean bEnabled) {
		rValueBox.setEnabled(bEnabled);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setFocus(boolean bFocused) {
		rValueBox.setFocus(bFocused);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setReadOnly(boolean bReadOnly) {
		rValueBox.setReadOnly(bReadOnly);
	}

	/**
	 * {@inheritDoc} int)
	 */
	@Override
	public void setSelectionRange(int nStart, int nLength) {
		rValueBox.setSelectionRange(nStart, nLength);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setTabIndex(int nIndex) {
		rValueBox.setTabIndex(nIndex);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setText(String sText) {
		rValueBox.setText(sText);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setVisibleLength(int nColumns) {
		if (rValueBox instanceof TextBox) {
			((TextBox) rValueBox).setVisibleLength(nColumns);
		}
	}
}
