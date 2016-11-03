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
package de.esoco.ewt.component;

import de.esoco.ewt.event.EventType;
import de.esoco.ewt.impl.gwt.GwtFileChooser;
import de.esoco.ewt.impl.gwt.WidgetFactory;
import de.esoco.ewt.style.StyleData;

import de.esoco.lib.property.TextAttribute;

import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.Widget;


/********************************************************************
 * A control that allows the user to select a file for server-side processing.
 * If the control action is performed the file will be uploaded to the server.
 * The following event types are supported:
 *
 * <ul>
 *   <li>VALUE_CHANGED: when the selected file changes.</li>
 *   <li>ACTION: when the file selection has been confirmed by the user.</li>
 * </ul>
 *
 * @author eso
 */
public class FileChooser extends Control implements TextAttribute
{
	//~ Instance fields --------------------------------------------------------

	private String sAction;

	//~ Constructors -----------------------------------------------------------

	/***************************************
	 * Creates a new instance.
	 *
	 * @param sAction The action to be performed when a file is selected
	 */
	public FileChooser(String sAction)
	{
		this.sAction = sAction;
	}

	//~ Methods ----------------------------------------------------------------

	/***************************************
	 * Returns the name of the selected file. May be NULL if nothing has been
	 * selected yet. A VALUE_CHANGED event will occur if the selection changes.
	 *
	 * @return The filename
	 */
	public String getFilename()
	{
		return getGwtFileChooser().getFilename();
	}

	/***************************************
	 * {@inheritDoc}
	 */
	@Override
	public String getText()
	{
		return getGwtFileChooser().getText();
	}

	/***************************************
	 * {@inheritDoc}
	 */
	@Override
	public void initWidget(Container rParent, StyleData rStyle)
	{
		super.initWidget(rParent, rStyle);

		getGwtFileChooser().setAction(sAction);
	}

	/***************************************
	 * {@inheritDoc}
	 */
	@Override
	public void setText(String sText)
	{
		getGwtFileChooser().setText(getContext().expandResource(sText));
	}

	/***************************************
	 * {@inheritDoc}
	 */
	@Override
	ComponentEventDispatcher createEventDispatcher()
	{
		return new FileChooserEventDispatcher();
	}

	/***************************************
	 * Returns the GWT file chooser of this instance.
	 *
	 * @return The GWT file chooser
	 */
	private GwtFileChooser getGwtFileChooser()
	{
		return (GwtFileChooser) getWidget();
	}

	//~ Inner Classes ----------------------------------------------------------

	/********************************************************************
	 * Widget factory for this component.
	 *
	 * @author eso
	 */
	public static class FileChooserWidgetFactory
		implements WidgetFactory<GwtFileChooser>
	{
		//~ Methods ------------------------------------------------------------

		/***************************************
		 * {@inheritDoc}
		 */
		@Override
		public GwtFileChooser createWidget(
			Component rComponent,
			StyleData rStyle)
		{
			return new GwtFileChooser();
		}
	}

	/********************************************************************
	 * Extended event dispatcher that converts the {@link SubmitCompleteEvent}
	 * of the underlying GWT widget's form panel to {@link EventType#ACTION}
	 * events.
	 *
	 * @author eso
	 */
	class FileChooserEventDispatcher extends ComponentEventDispatcher
		implements SubmitCompleteHandler
	{
		//~ Methods ------------------------------------------------------------

		/***************************************
		 * {@inheritDoc}
		 */
		@Override
		public void onSubmitComplete(SubmitCompleteEvent rEvent)
		{
			notifyEventHandler(EventType.ACTION);
		}

		/***************************************
		 * {@inheritDoc}
		 */
		@Override
		protected void initEventDispatching(
			Widget    rWidget,
			EventType eEventType)
		{
			super.initEventDispatching(rWidget, eEventType);

			if (eEventType == EventType.ACTION &&
				rWidget instanceof GwtFileChooser)
			{
				((GwtFileChooser) rWidget).getFormPanel()
										  .addSubmitCompleteHandler(this);
			}
		}
	}
}
