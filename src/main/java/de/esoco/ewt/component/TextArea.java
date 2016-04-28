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

import de.esoco.ewt.UserInterfaceContext;
import de.esoco.ewt.event.EventType;
import de.esoco.ewt.impl.gwt.WidgetFactory;
import de.esoco.ewt.impl.gwt.code.GwtCodeMirror;
import de.esoco.ewt.style.StyleData;
import de.esoco.ewt.style.StyleFlag;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.TextBoxBase;
import com.google.gwt.user.client.ui.Widget;

import static de.esoco.lib.property.UserInterfaceProperties.MIME_TYPE;


/********************************************************************
 * A multiline text input component.
 *
 * @author eso
 */
public class TextArea extends TextComponent
{
	//~ Methods ----------------------------------------------------------------

	/***************************************
	 * Returns the position of the input caret.
	 *
	 * @return The caret position
	 */
	@Override
	public int getCaretPosition()
	{
		return getTextArea().getCursorPos();
	}

	/***************************************
	 * Returns the text of this component.
	 *
	 * @return The text
	 */
	@Override
	public String getText()
	{
		return getTextArea().getText();
	}

	/***************************************
	 * {@inheritDoc}
	 */
	@Override
	public void initWidget(UserInterfaceContext rContext, StyleData rStyle)
	{
		super.initWidget(rContext, rStyle);

		if (rStyle.hasFlag(StyleFlag.WRAP))
		{
			setLineWrapping(true);
		}
		else if (rStyle.hasFlag(StyleFlag.NO_WRAP))
		{
			setLineWrapping(false);
		}
	}

	/***************************************
	 * Returns the editable state of this component.
	 *
	 * @return TRUE if the component allows editing, FALSE if it is readonly
	 */
	@Override
	public boolean isEditable()
	{
		return !getTextArea().isReadOnly();
	}

	/***************************************
	 * Checks whether text lines are wrapped automatically by this component.
	 *
	 * @return The line wrapping state
	 */
	public boolean isLineWrapping()
	{
		String sWrapAttr = getWidget().getElement().getAttribute("wrap");

		// return TRUE if on or not set
		return !"off".equals(sWrapAttr);
	}

	/***************************************
	 * Sets the position of the input caret.
	 *
	 * @param nPosition The new caret position
	 */
	@Override
	public void setCaretPosition(int nPosition)
	{
		getTextArea().setCursorPos(nPosition);
	}

	/***************************************
	 * @see TextComponent#setColumns(int)
	 */
	@Override
	public void setColumns(int nColumns)
	{
		getTextArea().setCharacterWidth(nColumns);
	}

	/***************************************
	 * Sets the editable state of this component.
	 *
	 * @param bEditable TRUE if the object shall be editable, FALSE to set it to
	 *                  be readonly
	 */
	@Override
	public void setEditable(boolean bEditable)
	{
		getTextArea().setReadOnly(!bEditable);
	}

	/***************************************
	 * Sets whether text lines should be wrapped automatically by this
	 * component.
	 *
	 * @param bWrap TRUE to enabled automatic wrapping
	 */
	public void setLineWrapping(boolean bWrap)
	{
		getWidget().getElement().setAttribute("wrap", bWrap ? "on" : "off");
	}

	/***************************************
	 * Allows to set a minimum height by providing the corresponding text row
	 * count. This may not be supported by all implementations.
	 *
	 * @param nRows The minimum number of text rows to display
	 */
	public void setRows(int nRows)
	{
		getTextArea().setVisibleLines(nRows);
	}

	/***************************************
	 * {@inheritDoc}
	 */
	@Override
	protected TextBoxBase getTextBox()
	{
		if (getWidget() instanceof TextBoxBase)
		{
			return super.getTextBox();
		}
		else
		{
			throw new UnsupportedOperationException("TextArea not implemented " +
													"as TextBox but with" +
													getWidget().getClass());
		}
	}

	/***************************************
	 * Implements setting the widget text and will be invoked from {@link
	 * #setText(String)}.
	 *
	 * @param sText
	 */
	@Override
	protected void setWidgetText(String sText)
	{
		getTextArea().setText(sText);
	}

	/***************************************
	 * @see Component#createEventDispatcher()
	 */
	@Override
	ComponentEventDispatcher createEventDispatcher()
	{
		return new TextAreaEventDispatcher();
	}

	/***************************************
	 * Returns the widget of this component as a {@link IsTextArea} interface.
	 *
	 * @return The text area widget
	 */
	IsTextArea getTextArea()
	{
		return (IsTextArea) getWidget();
	}

	//~ Inner Interfaces -------------------------------------------------------

	/********************************************************************
	 * An interface that describes the properties of text areas.
	 *
	 * @author eso
	 */
	public static interface IsTextArea
	{
		//~ Methods ------------------------------------------------------------

		/***************************************
		 * Returns the position of the input caret.
		 *
		 * @return The caret position
		 */
		public int getCursorPos();

		/***************************************
		 * Returns the text of this component.
		 *
		 * @return The text
		 */
		public String getText();

		/***************************************
		 * Returns the editable state of this component.
		 *
		 * @return TRUE if the component is readonly
		 */
		public boolean isReadOnly();

		/***************************************
		 * @see TextComponent#setColumns(int)
		 */
		public void setCharacterWidth(int nColumns);

		/***************************************
		 * Sets the position of the input caret.
		 *
		 * @param nPosition The new caret position
		 */
		public void setCursorPos(int nPosition);

		/***************************************
		 * Sets the editable state of this component.
		 *
		 * @param bReadOnly TRUE if the object shall be readonly
		 */
		public void setReadOnly(boolean bReadOnly);

		/***************************************
		 * Sets the text of this component.
		 *
		 * @param sText The new text
		 */
		public void setText(String sText);

		/***************************************
		 * Sets the row count.
		 *
		 * @param nRows The minimum number of text rows to display
		 */
		public void setVisibleLines(int nRows);
	}

	//~ Inner Classes ----------------------------------------------------------

	/********************************************************************
	 * Widget factory for this component.
	 *
	 * @author eso
	 */
	public static class TextAreaWidgetFactory implements WidgetFactory<Widget>
	{
		//~ Methods ------------------------------------------------------------

		/***************************************
		 * {@inheritDoc}
		 */
		@Override
		public Widget createWidget(Component rComponent, StyleData rStyle)
		{
			String sMimeType = rStyle.getProperty(MIME_TYPE, null);
			Widget aWidget   = null;

			if (sMimeType != null && !sMimeType.equalsIgnoreCase("text/plain"))
			{
				String sMode  = null;
				int    nIndex = sMimeType.indexOf("x-");

				if (nIndex >= 0)
				{
					sMode = sMimeType.substring(nIndex + 2);
				}
				else if (sMimeType.toLowerCase().startsWith("text/"))
				{
					sMode = sMimeType.substring(5);
				}

				if (sMode != null)
				{
					aWidget = new GwtCodeMirror(sMode);
				}
			}

			if (aWidget == null)
			{
				aWidget = new GwtTextArea();
			}

			return aWidget;
		}
	}

	/********************************************************************
	 * A text area subclass that propagates the on paste event.
	 *
	 * @author eso
	 */
	static class GwtTextArea extends com.google.gwt.user.client.ui.TextArea
		implements IsTextArea
	{
		//~ Constructors -------------------------------------------------------

		/***************************************
		 * Creates a new instance.
		 */
		GwtTextArea()
		{
			sinkEvents(Event.ONPASTE);
		}

		//~ Methods ------------------------------------------------------------

		/***************************************
		 * Overridden to fire a value change event if text is pasted in to this
		 * text area.
		 *
		 * @see com.google.gwt.user.client.ui.TextArea#onBrowserEvent(Event)
		 */
		@Override
		public void onBrowserEvent(Event rEvent)
		{
			super.onBrowserEvent(rEvent);

			switch (DOM.eventGetType(rEvent))
			{
				case Event.ONPASTE:
					Scheduler.get()
							 .scheduleDeferred(new ScheduledCommand()
						{
							@Override
							public void execute()
							{
								ValueChangeEvent.fire(GwtTextArea.this,
													  getText());
							}
						});


					break;
			}
		}
	}

	/********************************************************************
	 * Dispatcher for text-specific events.
	 *
	 * @author eso
	 */
	class TextAreaEventDispatcher extends TextEventDispatcher
	{
		//~ Methods ------------------------------------------------------------

		/***************************************
		 * @see TextEventDispatcher#handleKeyUp(KeyUpEvent)
		 */
		@Override
		protected void handleKeyUp(KeyUpEvent rEvent)
		{
			if (rEvent.getNativeKeyCode() == KeyCodes.KEY_ENTER)
			{
				if (rEvent.isControlKeyDown())
				{
					notifyEventHandler(EventType.ACTION, rEvent);
				}
			}
			else
			{
				super.handleKeyUp(rEvent);
			}
		}
	}
}
