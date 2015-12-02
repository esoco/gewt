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
package de.esoco.ewt.dialog;

import de.esoco.ewt.EWT;
import de.esoco.ewt.UserInterfaceContext;
import de.esoco.ewt.component.Component;
import de.esoco.ewt.component.View;
import de.esoco.ewt.graphics.Image;
import de.esoco.ewt.impl.gwt.GewtCss;
import de.esoco.ewt.impl.gwt.GewtResources;
import de.esoco.ewt.style.AlignedPosition;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.CellPanel;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.DockPanel.DockLayoutConstant;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.Widget;


/********************************************************************
 * A message box class that allows to display a message in a standardized format
 * to the user. Instances of this class cannot be created directly. Instead, it
 * provides several static "showMessage" methods to display a message together
 * with an image and several types of standard buttons centered on the screen.
 *
 * <p>The following keys must exist in a application resource for message boxes:
 * "$btnRetry", "$btnIgnore", "$btnYes", "$btnNo", "$btnOk", "$btnCancel" for
 * the buttons and "$imQuestion", "$imInfo", "$imWarning", "$imError" for the
 * images.</p>
 *
 * @author eso
 */
public class MessageBox implements ClickHandler
{
	//~ Static fields/initializers ---------------------------------------------

	/** Question icon */
	public static final int ICON_QUESTION = 0;

	/** Information icon */
	public static final int ICON_INFORMATION = 1;

	/** Warning icon */
	public static final int ICON_WARNING = 2;

	/** Error icon */
	public static final int ICON_ERROR = 3;

	/** Retry button */
	public static final int BUTTON_RETRY = 0x01;

	/** Ignore button */
	public static final int BUTTON_IGNORE = 0x02;

	/** Yes button */
	public static final int BUTTON_YES = 0x04;

	/** No button */
	public static final int BUTTON_NO = 0x08;

	/** OK button */
	public static final int BUTTON_OK = 0x10;

	/** Cancel button */
	public static final int BUTTON_CANCEL = 0x20;

	private static final GewtResources RES = GewtResources.INSTANCE;
	private static final GewtCss	   CSS = RES.css();

	//- Button combinations ----------------------------------------------------
	/** OK and Cancel buttons */
	private static final int BUTTONS_OK_CANCEL = BUTTON_OK | BUTTON_CANCEL;

	/** Yes and No buttons */
	private static final int BUTTONS_YES_NO = BUTTON_YES | BUTTON_NO;

	/** Yes, No, and Cancel buttons */
	private static final int BUTTONS_YES_NO_CANCEL =
		BUTTON_YES | BUTTON_NO | BUTTON_CANCEL;

	private static final Object[] ICON_IMAGES   =
		new Object[] { "$imQuestion", "$imInfo", "$imWarning", "$imError" };
	private static final String[] BUTTON_LABELS =
		new String[]
		{
			"$btnRetry", "$btnIgnore", "$btnYes", "$btnNo", "$btnOk",
			"$btnCancel"
		};

	//~ Instance fields --------------------------------------------------------

	private DialogBox     aDialog;
	private CellPanel     aButtonPanel;
	private ResultHandler rResultHandler;

	//~ Constructors -----------------------------------------------------------

	/***************************************
	 * Creates a new instance with a certain parent view.
	 *
	 * @param rParent        The parent view
	 * @param sTitle         The message box title
	 * @param sMessage       The message text to display or NULL for none
	 * @param sSubMessage    A message to be displayed below image and message
	 *                       text or NULL for none
	 * @param rImage         An image to display or NULL for none
	 * @param nButtons       The bit combination for the message box buttons
	 * @param rResultHandler The handler for the message box result
	 */
	MessageBox(View			 rParent,
			   String		 sTitle,
			   String		 sMessage,
			   String		 sSubMessage,
			   Image		 rImage,
			   int			 nButtons,
			   ResultHandler rResultHandler)
	{
		this.rResultHandler = rResultHandler;

		init(rParent, sTitle, sMessage, sSubMessage, rImage, nButtons);
	}

	//~ Static methods ---------------------------------------------------------

	/***************************************
	 * Shows a question message dialog with three buttons: Yes, No, and Cancel.
	 *
	 * @param rParent        The parent view
	 * @param sTitle         The message box title
	 * @param sMessage       The message text to display
	 * @param nIcon          The type constant of the icon to display
	 * @param rResultHandler The handler for the message box result
	 */
	public static void showCancelQuestion(View			rParent,
										  String		sTitle,
										  String		sMessage,
										  int			nIcon,
										  ResultHandler rResultHandler)
	{
		showMessage(rParent,
					sTitle,
					sMessage,
					null,
					nIcon,
					BUTTONS_YES_NO_CANCEL,
					rResultHandler);
	}

	/***************************************
	 * Shows a confirmation dialog. The dialog will have the buttons OK and
	 * Cancel.
	 *
	 * @param rParent        The parent view
	 * @param sTitle         The message box title
	 * @param sMessage       The message text to display
	 * @param nIcon          The type constant of the icon to display
	 * @param rResultHandler The handler for the message box result
	 */
	public static void showConfirmation(View		  rParent,
										String		  sTitle,
										String		  sMessage,
										int			  nIcon,
										ResultHandler rResultHandler)
	{
		showMessage(rParent,
					sTitle,
					sMessage,
					null,
					nIcon,
					BUTTONS_OK_CANCEL,
					rResultHandler);
	}

	/***************************************
	 * Shows a notification dialog. The dialog will have a single OK button and
	 * therefore doesn't need a result handler.
	 *
	 * @param rParent  The parent view
	 * @param sTitle   The message box title
	 * @param sMessage The message text to display
	 * @param nIcon    The type constant of the icon to display
	 */
	public static void showNotification(View   rParent,
										String sTitle,
										String sMessage,
										int    nIcon)
	{
		showMessage(rParent, sTitle, sMessage, null, nIcon, BUTTON_OK, null);
	}

	/***************************************
	 * Shows a question message dialog. The dialog will have the buttons 'Yes'
	 * and 'No'.
	 *
	 * @param rParent        The parent view
	 * @param sTitle         The message box title
	 * @param sMessage       The message text to display
	 * @param nIcon          The type constant of the icon to display
	 * @param rResultHandler The handler for the message box result
	 */
	public static void showQuestion(View		  rParent,
									String		  sTitle,
									String		  sMessage,
									int			  nIcon,
									ResultHandler rResultHandler)
	{
		showMessage(rParent,
					sTitle,
					sMessage,
					null,
					nIcon,
					BUTTONS_YES_NO,
					rResultHandler);
	}

	/***************************************
	 * Internal helper method to initialize the message box image for a certain
	 * icon index.
	 *
	 * @param  rComponent The component to be used to create the image
	 * @param  nIndex     The icon index
	 *
	 * @return The icon image
	 */
	private static Image getIconImage(Component rComponent, int nIndex)
	{
		Object rImage = ICON_IMAGES[nIndex];

		if (rImage instanceof String)
		{
			rImage = rComponent.getContext().createImage(rImage);

			ICON_IMAGES[nIndex] = rImage;
		}

		return (Image) rImage;
	}

	/***************************************
	 * Shows a message dialog with a predefined icon and buttons. The icon and
	 * the button(s) to be displayed must be defined with one of the constants
	 * in this class (like {@link #ICON_INFORMATION}, {@link #BUTTON_OK} or
	 * {@link #BUTTONS_OK_CANCEL}).
	 *
	 * @param    rParent        The parent view
	 * @param    sTitle         The message box title
	 * @param    sMessage       The message text to display or NULL for none
	 * @param    sSubMessage    A message to be displayed below image and
	 *                          message text or NULL for none
	 * @param    nIcon          The type constant of the icon to display
	 * @param    nButtons       The bit combination for the buttons to display
	 * @param    rResultHandler The handler for the message box result
	 *
	 * @category mEWT
	 */
	private static void showMessage(View		  rParent,
									String		  sTitle,
									String		  sMessage,
									String		  sSubMessage,
									int			  nIcon,
									int			  nButtons,
									ResultHandler rResultHandler)
	{
		showMessage(rParent,
					sTitle,
					sMessage,
					sSubMessage,
					getIconImage(rParent, nIcon),
					nButtons,
					rResultHandler);
	}

	/***************************************
	 * Shows a message dialog with arbitrary image and buttons. The button(s) to
	 * be displayed must be defined with one of the button constants in this
	 * class (like {@link #BUTTON_OK} or {@link #BUTTONS_OK_CANCEL}).
	 *
	 * @param    rParent        The parent view
	 * @param    sTitle         The message box title
	 * @param    sMessage       The message text to display or NULL for none
	 * @param    sSubMessage    A message to be displayed below image and
	 *                          message text or NULL for none
	 * @param    rImage         The image to display or NULL for none
	 * @param    nButtons       The bit combination for the buttons to display
	 * @param    rResultHandler The handler for the message box result
	 *
	 * @category mEWT
	 */
	private static void showMessage(View		  rParent,
									String		  sTitle,
									String		  sMessage,
									String		  sSubMessage,
									Image		  rImage,
									int			  nButtons,
									ResultHandler rResultHandler)
	{
		MessageBox mb =
			new MessageBox(rParent,
						   sTitle,
						   sMessage,
						   sSubMessage,
						   rImage,
						   nButtons,
						   rResultHandler);

		mb.show();
	}

	//~ Methods ----------------------------------------------------------------

	/***************************************
	 * Handles clicks on message box buttons or on the close button of the
	 * message box view and invokes the result handler with the button number.
	 *
	 * @see ClickHandler#onClick(ClickEvent)
	 */
	@Override
	public void onClick(ClickEvent rEvent)
	{
		aDialog.hide();

		Widget rSource	    = (Widget) rEvent.getSource();
		int    nButtonCount = aButtonPanel.getWidgetCount();
		int    nButton;

		if (rSource == aDialog)
		{
			// view close button clicked = Cancel/No
			nButton = nButtonCount;
		}
		else
		{
			// calculate button index from right to left!
			nButton = aButtonPanel.getWidgetIndex(rSource) + 1;
		}

		if (nButton > 0)
		{
			if (rResultHandler != null)
			{
				rResultHandler.handleResult(nButtonCount - nButton);
			}
		}
	}

	/***************************************
	 * Initializes this instance.
	 *
	 * @param rParent
	 * @param sTitle      The message box title
	 * @param sMessage    The message text to display or NULL for none
	 * @param sSubMessage A message to be displayed below image and text or NULL
	 *                    for none
	 * @param rImage      The image to display or NULL for none
	 * @param nButtons    The combined bits of the message box buttons
	 */
	void init(View   rParent,
			  String sTitle,
			  String sMessage,
			  String sSubMessage,
			  Image  rImage,
			  int    nButtons)
	{
		aDialog = new DialogBox(false, true);

		sTitle   = EWT.expandResource(rParent, sTitle);
		sMessage = EWT.expandResource(rParent, sMessage);

		DockPanel aMainPanel    = new DockPanel();
		DockPanel aMessagePanel = new DockPanel();

		aButtonPanel = new HorizontalPanel();

		aMainPanel.setSpacing(10);
		aButtonPanel.setSpacing(10);

		aMainPanel.add(aMessagePanel, DockPanel.CENTER);
		aMainPanel.add(aButtonPanel, DockPanel.SOUTH);

		aDialog.setText(sTitle);

		if (rImage != null)
		{
			aMessagePanel.add(rImage.getGwtImage(), DockPanel.WEST);
		}

		if (sMessage != null)
		{
			Label rLabel =
				addMessageLabel(aMessagePanel, sMessage, DockPanel.CENTER);

			rLabel.addStyleName(CSS.main());
		}

		if (sSubMessage != null)
		{
			addMessageLabel(aMessagePanel, sSubMessage, DockPanel.SOUTH);
		}

		boolean bFirst = true;

		for (int i = 0; i < BUTTON_LABELS.length; i++)
		{
			if ((nButtons & 0x1) != 0)
			{
				PushButton aButton =
					new PushButton(EWT.expandResource(rParent,
													  BUTTON_LABELS[i]));

				aButton.addClickHandler(this);
				aButtonPanel.add(aButton);

				// TODO: implement automatic sizing
				aButton.setWidth("6EM");

				if (bFirst)
				{
					final PushButton aFirstButton = aButton;

					aFirstButton.addStyleName("ewt-Default");

					Scheduler.get()
							 .scheduleDeferred(new ScheduledCommand()
						{
							@Override
							public void execute()
							{
								aFirstButton.setFocus(true);
							}
						});
					bFirst = false;
				}
			}

			nButtons >>= 1;
		}

		aDialog.setWidget(aMainPanel);
		aDialog.addStyleDependentName(CSS.ewtMessageBox());
	}

	/***************************************
	 * Displays this message box instance centered on the screen.
	 */
	void show()
	{
		aDialog.setGlassEnabled(true);
		aDialog.show();
		UserInterfaceContext.setPopupBounds(aDialog,
											Window.getClientWidth() / 2,
											Window.getClientHeight() / 3,
											AlignedPosition.CENTER,
											true);
	}

	/***************************************
	 * Adds a message label to the given panel
	 *
	 * @param  rPanel    The panel
	 * @param  sMessage  The message string
	 * @param  rPosition The dock layout position constant
	 *
	 * @return The new label component
	 */
	private Label addMessageLabel(DockPanel			 rPanel,
								  String			 sMessage,
								  DockLayoutConstant rPosition)
	{
		Label aLabel = new Label(sMessage, true);

		rPanel.add(aLabel, rPosition);
		aLabel.addStyleName(CSS.ewtMessageLabel());

		return aLabel;
	}

	//~ Inner Interfaces -------------------------------------------------------

	/********************************************************************
	 * Interface for the handling of message box results.
	 */
	public interface ResultHandler
	{
		//~ Methods ------------------------------------------------------------

		/***************************************
		 * Must be implemented to handle the message box result. The return
		 * value is the number of the message box button that has been selected,
		 * from right to left (!), starting at 0. This means that the cancel or
		 * no buttons which are always the rightmost buttons will have a button
		 * number of 0 (zero).
		 *
		 * @param nButton The number of the button used to close the message box
		 */
		public void handleResult(int nButton);
	}
}
