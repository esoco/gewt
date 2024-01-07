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

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HasHTML;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HorizontalPanel;

/**
 * A form-based implementation of a widget that allows the user to select a file
 * for processing.
 *
 * @author eso
 */
public class GwtFileChooser extends Composite
	implements Focusable, HasText, HasHTML, HasValueChangeHandlers<String>,
	ClickHandler, ChangeHandler {

	private FormPanel formPanel = new FormPanel();

	private FileUpload fileUpload = new FileUpload();

	private Button submitButton = new Button();

	/**
	 * Creates a new instance. The action argument must be a module-relative
	 * URL
	 * for the submit target of the executed POST request. It will be appended
	 * to the result of {@link GWT#getModuleBaseURL()}.
	 */
	public GwtFileChooser() {
		HorizontalPanel panel = new HorizontalPanel();

		formPanel.setEncoding(FormPanel.ENCODING_MULTIPART);
		formPanel.setMethod(FormPanel.METHOD_POST);

		fileUpload.addChangeHandler(this);

		submitButton.setEnabled(false);
		submitButton.addClickHandler(this);

		panel.add(fileUpload);
		panel.add(submitButton);

		formPanel.add(panel);

		initWidget(formPanel);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public HandlerRegistration addValueChangeHandler(
		ValueChangeHandler<String> handler) {
		return addHandler(handler, ValueChangeEvent.getType());
	}

	/**
	 * Returns the name of the selected file.
	 *
	 * @return The file name
	 */
	public String getFilename() {
		return fileUpload.getFilename();
	}

	/**
	 * Returns the form value.
	 *
	 * @return The form value
	 */
	public final FormPanel getFormPanel() {
		return formPanel;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getHTML() {
		return submitButton.getHTML();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getTabIndex() {
		return submitButton.getTabIndex();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getText() {
		return submitButton.getText();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onChange(ChangeEvent event) {
		String file = fileUpload.getFilename();

		submitButton.setEnabled(file != null && file.length() > 0);

		ValueChangeEvent.fire(this, file);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onClick(ClickEvent event) {
		formPanel.submit();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setAccessKey(char key) {
		submitButton.setAccessKey(key);
	}

	/**
	 * Sets the action name for this instance.
	 *
	 * @param action The action name
	 */
	public void setAction(String action) {
		formPanel.setAction(GWT.getModuleBaseURL() + action);
		fileUpload.setName(action);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setFocus(boolean focused) {
		submitButton.setFocus(focused);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setHTML(String html) {
		submitButton.setHTML(html);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setTabIndex(int index) {
		submitButton.setTabIndex(index);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setText(String text) {
		submitButton.setText(text);
	}
}
