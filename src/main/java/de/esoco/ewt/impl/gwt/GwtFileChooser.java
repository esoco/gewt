//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// GEWT source file
// Copyright (c) 2014 by Elmar Sonnenschein / esoco GmbH
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


/********************************************************************
 * A form-based implementation of a widget that allows the user to select a file
 * for processing.
 *
 * @author eso
 */
public
class GwtFileChooser extends Composite
    implements Focusable, HasText, HasHTML, HasValueChangeHandlers<String>,
               ClickHandler, ChangeHandler
{
	private FormPanel aFormPanel = new FormPanel();

	private FileUpload aFileUpload   = new FileUpload();
	private Button     aSubmitButton = new Button();

	/***************************************
	 * Creates a new instance. The action argument must be a module-relative URL
	 * for the submit target of the executed POST request. It will be appended
	 * to the result of {@link GWT#getModuleBaseURL()}.
	 *
	 * @param sAction The submit action to be performed when the user confirm
	 *                the file selection
	 */
	public GwtFileChooser(String sAction)
	{
		HorizontalPanel aPanel = new HorizontalPanel();

		aFormPanel.setEncoding(FormPanel.ENCODING_MULTIPART);
		aFormPanel.setMethod(FormPanel.METHOD_POST);
		aFormPanel.setAction(GWT.getModuleBaseURL() + sAction);

		aFileUpload.setName(sAction);
		aFileUpload.addChangeHandler(this);

		aSubmitButton.setEnabled(false);
		aSubmitButton.addClickHandler(this);

		aPanel.add(aFileUpload);
		aPanel.add(aSubmitButton);

		aFormPanel.add(aPanel);

		initWidget(aFormPanel);
	}

	/***************************************
	 * {@inheritDoc}
	 */
	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<String> rHandler)
	{
		return addHandler(rHandler, ValueChangeEvent.getType());
	}

	/***************************************
	 * Returns the name of the selected file.
	 *
	 * @return The file name
	 */
	public String getFilename()
	{
		return aFileUpload.getFilename();
	}

	/***************************************
	 * Returns the form value.
	 *
	 * @return The form value
	 */
	public final FormPanel getFormPanel()
	{
		return aFormPanel;
	}

	/***************************************
	 * {@inheritDoc}
	 */
	@Override
	public String getHTML()
	{
		return aSubmitButton.getHTML();
	}

	/***************************************
	 * {@inheritDoc}
	 */
	@Override
	public int getTabIndex()
	{
		return aSubmitButton.getTabIndex();
	}

	/***************************************
	 * {@inheritDoc}
	 */
	@Override
	public String getText()
	{
		return aSubmitButton.getText();
	}

	/***************************************
	 * {@inheritDoc}
	 */
	@Override
	public void onChange(ChangeEvent rEvent)
	{
		String sFile = aFileUpload.getFilename();

		aSubmitButton.setEnabled(sFile != null && sFile.length() > 0);

		ValueChangeEvent.fire(this, sFile);
	}

	/***************************************
	 * {@inheritDoc}
	 */
	@Override
	public void onClick(ClickEvent rEvent)
	{
		aFormPanel.submit();
	}

	/***************************************
	 * {@inheritDoc}
	 */
	@Override
	public void setAccessKey(char cKey)
	{
		aSubmitButton.setAccessKey(cKey);
	}

	/***************************************
	 * {@inheritDoc}
	 */
	@Override
	public void setFocus(boolean bFocused)
	{
		aSubmitButton.setFocus(bFocused);
	}

	/***************************************
	 * {@inheritDoc}
	 */
	@Override
	public void setHTML(String sHtml)
	{
		aSubmitButton.setHTML(sHtml);
	}

	/***************************************
	 * {@inheritDoc}
	 */
	@Override
	public void setTabIndex(int nIndex)
	{
		aSubmitButton.setTabIndex(nIndex);
	}

	/***************************************
	 * {@inheritDoc}
	 */
	@Override
	public void setText(String sText)
	{
		aSubmitButton.setText(sText);
	}
}
