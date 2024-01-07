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

	private final ValueBoxBase<?> valueBox;

	/**
	 * Creates a new instance.
	 *
	 * @param valueBox The value box to wrap
	 */
	public ValueBoxWrapper(ValueBoxBase<?> valueBox) {
		this.valueBox = valueBox;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public HandlerRegistration addDoubleClickHandler(
		DoubleClickHandler handler) {
		return valueBox.addDoubleClickHandler(handler);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public HandlerRegistration addKeyDownHandler(KeyDownHandler handler) {
		return valueBox.addKeyDownHandler(handler);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public HandlerRegistration addKeyPressHandler(KeyPressHandler handler) {
		return valueBox.addKeyPressHandler(handler);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Widget asWidget() {
		return valueBox;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void fireEvent(GwtEvent<?> event) {
		valueBox.fireEvent(event);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getCursorPos() {
		return valueBox.getCursorPos();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getSelectedText() {
		return valueBox.getSelectedText();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getTabIndex() {
		return valueBox.getTabIndex();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getText() {
		return valueBox.getText();
	}

	/**
	 * @see com.google.gwt.user.client.ui.HasEnabled#isEnabled()
	 */
	@Override
	public boolean isEnabled() {
		return valueBox.isEnabled();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isReadOnly() {
		return valueBox.isReadOnly();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setAccessKey(char key) {
		valueBox.setAccessKey(key);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setCursorPos(int position) {
		valueBox.setCursorPos(position);
	}

	/**
	 * @see com.google.gwt.user.client.ui.HasEnabled#setEnabled(boolean)
	 */
	@Override
	public void setEnabled(boolean enabled) {
		valueBox.setEnabled(enabled);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setFocus(boolean focused) {
		valueBox.setFocus(focused);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setReadOnly(boolean readOnly) {
		valueBox.setReadOnly(readOnly);
	}

	/**
	 * {@inheritDoc} int)
	 */
	@Override
	public void setSelectionRange(int start, int length) {
		valueBox.setSelectionRange(start, length);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setTabIndex(int index) {
		valueBox.setTabIndex(index);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setText(String text) {
		valueBox.setText(text);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setVisibleLength(int columns) {
		if (valueBox instanceof TextBox) {
			((TextBox) valueBox).setVisibleLength(columns);
		}
	}
}
