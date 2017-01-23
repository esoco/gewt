//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// This file is a part of the 'gewt' project.
// Copyright 2017 Elmar Sonnenschein, esoco GmbH, Flensburg, Germany
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
import de.esoco.ewt.impl.gwt.ValueBoxConstraint.RegExConstraint;
import de.esoco.ewt.style.StyleData;
import de.esoco.ewt.style.StyleFlag;

import de.esoco.lib.property.TextAttribute;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.HasDoubleClickHandlers;
import com.google.gwt.event.dom.client.HasKeyDownHandlers;
import com.google.gwt.event.dom.client.HasKeyPressHandlers;
import com.google.gwt.event.dom.client.HasKeyUpHandlers;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;


/********************************************************************
 * A base class for components that handle text editing.
 *
 * <p>Supported style flags:</p>
 *
 * <ul>
 *   <li>{@link StyleFlag#RESOURCE}: if text should be treated as a resource and
 *     expanded with {@link UserInterfaceContext#expandResource(String)}.</li>
 * </ul>
 *
 * <p>Supported event types:</p>
 *
 * <ul>
 *   <li>{@link EventType#ACTION ACTION}: when the text input is
 *     finished/confirmed through the keyboard.</li>
 *   <li>{@link EventType#VALUE_CHANGED VALUE_CHANGED}: when the contents of the
 *     text component is edited.</li>
 * </ul>
 *
 * @author eso
 */
public abstract class TextControl extends Control implements TextAttribute
{
	//~ Instance fields --------------------------------------------------------

	private HandlerRegistration rConstraintHandler = null;
	private boolean			    bResourceValue     = false;

	//~ Methods ----------------------------------------------------------------

	/***************************************
	 * Allows to set a minimum width by providing the corresponding text column
	 * count. This may not be supported by all implementations.
	 *
	 * @param nColumns The minimum number of text columns to display
	 */
	public abstract void setColumns(int nColumns);

	/***************************************
	 * {@inheritDoc}
	 */
	@Override
	public void applyStyle(StyleData rStyle)
	{
		super.applyStyle(rStyle);

		bResourceValue = rStyle.hasFlag(StyleFlag.RESOURCE);
	}

	/***************************************
	 * Returns the position of the input caret.
	 *
	 * @return The caret position
	 */
	public int getCaretPosition()
	{
		return getTextBox().getCursorPos();
	}

	/***************************************
	 * Returns the text of this component.
	 *
	 * @return The text
	 */
	@Override
	public String getText()
	{
		return getTextBox().getText();
	}

	/***************************************
	 * Returns the editable state of this component.
	 *
	 * @return TRUE if the component allows editing, FALSE if it is readonly
	 */
	public boolean isEditable()
	{
		return !getTextBox().isReadOnly();
	}

	/***************************************
	 * Sets the position of the input caret.
	 *
	 * @param nPosition The new caret position
	 */
	public void setCaretPosition(int nPosition)
	{
		getTextBox().setCursorPos(nPosition);
	}

	/***************************************
	 * Sets the editable state of this component.
	 *
	 * @param bEditable TRUE if the object shall be editable, FALSE to set it to
	 *                  be readonly
	 */
	public void setEditable(boolean bEditable)
	{
		getTextBox().setReadOnly(!bEditable);
	}

	/***************************************
	 * Sets a regular expression as an input constraint. Any input that doesn't
	 * match the expression will be rejected.
	 *
	 * @param sConstraint The input constraint or NULL for none
	 */
	public void setInputConstraint(String sConstraint)
	{
		if (rConstraintHandler != null)
		{
			rConstraintHandler.removeHandler();
			rConstraintHandler = null;
		}

		if (sConstraint != null)
		{
			rConstraintHandler =
				getTextBox().addKeyPressHandler(new RegExConstraint(sConstraint));
		}
	}

	/***************************************
	 * Sets the placeholder string that will be display in an empty text
	 * component.
	 *
	 * @param sPlaceholder The placeholder string
	 */
	public void setPlaceholder(String sPlaceholder)
	{
		getWidget().getElement()
				   .setPropertyString("placeholder",
									  getContext().expandResource(sPlaceholder));
	}

	/***************************************
	 * Sets the text of this component.
	 *
	 * @param sText The new text
	 */
	@Override
	public final void setText(String sText)
	{
		if (bResourceValue)
		{
			sText = getContext().expandResource(sText);
		}

		setWidgetText(sText);
	}

	/***************************************
	 * Returns the {@link IsTextControlWidget} implementation of this instance.
	 *
	 * @return The text box
	 */
	protected IsTextControlWidget getTextBox()
	{
		return (IsTextControlWidget) getWidget();
	}

	/***************************************
	 * Implements setting the widget text and will be invoked from {@link
	 * #setText(String)}.
	 *
	 * @param sText
	 */
	protected void setWidgetText(String sText)
	{
		getTextBox().setText(sText);
	}

	/***************************************
	 * {@inheritDoc}
	 */
	@Override
	ComponentEventDispatcher createEventDispatcher()
	{
		return new TextEventDispatcher();
	}

	/***************************************
	 * {@inheritDoc}
	 */
	@Override
	void setProperty(String sProperty)
	{
		if (bResourceValue)
		{
			super.setProperty(sProperty);
		}
		else
		{
			setText(sProperty);
		}
	}

	//~ Inner Interfaces -------------------------------------------------------

	/********************************************************************
	 * The interface that needs to be provided by all text control widgets
	 * implementations.
	 *
	 * @author eso
	 */
	public static interface IsTextControlWidget extends IsWidget, HasText,
														Focusable, HasEnabled,
														HasKeyPressHandlers,
														HasKeyDownHandlers,
														HasDoubleClickHandlers
	{
		//~ Methods ------------------------------------------------------------

		/***************************************
		 * Returns the position of the input caret.
		 *
		 * @return The caret position
		 */
		public int getCursorPos();

		/***************************************
		 * Returns the currently selected text. The returned string will be
		 * empty if no selection exists.
		 *
		 * @return The selected text
		 */
		public String getSelectedText();

		/***************************************
		 * Returns the editable state of this component.
		 *
		 * @return TRUE if the component is readonly
		 */
		public boolean isReadOnly();

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
		 * Sets the selection of the text. A length of zero will remove the
		 * selection.
		 *
		 * @param  nStart  The start index of the selection
		 * @param  nLength The length of the selection
		 *
		 * @throws IndexOutOfBoundsException If the given selection doesn't fit
		 *                                   the current text
		 */
		public void setSelectionRange(int nStart, int nLength);

		/***************************************
		 * Sets the number of text columns to be displayed by this instance.
		 *
		 * @param nColumns The column count
		 */
		public void setVisibleLength(int nColumns);
	}

	//~ Inner Classes ----------------------------------------------------------

	/********************************************************************
	 * Dispatcher for text-specific events.
	 *
	 * @author eso
	 */
	class TextEventDispatcher extends ComponentEventDispatcher
		implements ValueChangeHandler<Object>
	{
		//~ Methods ------------------------------------------------------------

		/***************************************
		 * Overridden to send {@link EventType#POINTER_CLICKED} instead of
		 * ACTION.
		 *
		 * @see ComponentEventDispatcher#onClick(ClickEvent)
		 */
		@Override
		public void onClick(ClickEvent rEvent)
		{
			notifyEventHandler(EventType.POINTER_CLICKED, rEvent);
		}

		/***************************************
		 * Overridden to do nothing.
		 *
		 * @see ComponentEventDispatcher#onDoubleClick(DoubleClickEvent)
		 */
		@Override
		public void onDoubleClick(DoubleClickEvent rEvent)
		{
		}

		/***************************************
		 * {@inheritDoc}
		 */
		@Override
		public void onKeyUp(KeyUpEvent rEvent)
		{
			if (hasHandlerFor(EventType.KEY_RELEASED))
			{
				super.onKeyUp(rEvent);
			}

			handleKeyUp(rEvent);
		}

		/***************************************
		 * {@inheritDoc}
		 */
		@Override
		public void onValueChange(ValueChangeEvent<Object> rEvent)
		{
			notifyEventHandler(EventType.VALUE_CHANGED);
		}

		/***************************************
		 * Handles a key up event for the text component.
		 *
		 * @param rEvent The key up event
		 */
		protected void handleKeyUp(KeyUpEvent rEvent)
		{
			if (rEvent.getNativeKeyCode() == KeyCodes.KEY_ENTER)
			{
				notifyEventHandler(EventType.ACTION, rEvent);
			}
		}

		/***************************************
		 * {@inheritDoc}
		 */
		@Override
		@SuppressWarnings("unchecked")
		protected HandlerRegistration initEventDispatching(
			Widget    rWidget,
			EventType eEventType)
		{
			if (eEventType == EventType.ACTION &&
				rWidget instanceof HasKeyUpHandlers)
			{
				return ((HasKeyUpHandlers) rWidget).addKeyUpHandler(this);
			}
			else if (eEventType == EventType.VALUE_CHANGED &&
					 rWidget instanceof HasValueChangeHandlers)
			{
				// register generic event handler to support subclasses like DateField
				return ((HasValueChangeHandlers<Object>) rWidget)
					   .addValueChangeHandler(this);
			}
			else
			{
				return super.initEventDispatching(rWidget, eEventType);
			}
		}
	}
}
