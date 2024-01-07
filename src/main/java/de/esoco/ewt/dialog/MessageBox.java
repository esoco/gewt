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
package de.esoco.ewt.dialog;

import de.esoco.ewt.EWT;
import de.esoco.ewt.UserInterfaceContext;
import de.esoco.ewt.component.ChildView.IsChildViewWidget;
import de.esoco.ewt.component.Component;
import de.esoco.ewt.component.View;
import de.esoco.ewt.graphics.Image;
import de.esoco.ewt.graphics.ImageRef;
import de.esoco.ewt.impl.gwt.GewtCss;
import de.esoco.ewt.impl.gwt.GewtResources;
import de.esoco.ewt.style.AlignedPosition;
import de.esoco.ewt.style.ViewStyle;
import de.esoco.ewt.style.ViewStyle.Flag;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.CellPanel;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.DockPanel.DockLayoutConstant;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.Widget;

/**
 * A message box class that allows to display a message in a standardized format
 * to the user. Instances of this class cannot be created directly. Instead, it
 * provides several static "showMessage" methods to display a message together
 * with an image and several types of standard buttons centered on the screen.
 *
 * <p>The following keys must exist in a application resource for message
 * boxes: "$btnRetry", "$btnIgnore", "$btnYes", "$btnNo", "$btnOk", "$btnCancel"
 * for the buttons and "$imQuestion", "$imInfo", "$imWarning", "$imError" for
 * the images.</p>
 *
 * @author eso
 */
public class MessageBox implements ClickHandler {

	/**
	 * Question icon
	 */
	public static final int ICON_QUESTION = 0;

	/**
	 * Information icon
	 */
	public static final int ICON_INFORMATION = 1;

	/**
	 * Warning icon
	 */
	public static final int ICON_WARNING = 2;

	/**
	 * Error icon
	 */
	public static final int ICON_ERROR = 3;

	/**
	 * Retry button
	 */
	public static final int BUTTON_RETRY = 0x01;

	/**
	 * Ignore button
	 */
	public static final int BUTTON_IGNORE = 0x02;

	/**
	 * Yes button
	 */
	public static final int BUTTON_YES = 0x04;

	/**
	 * No button
	 */
	public static final int BUTTON_NO = 0x08;

	/**
	 * OK button
	 */
	public static final int BUTTON_OK = 0x10;

	/**
	 * Cancel button
	 */
	public static final int BUTTON_CANCEL = 0x20;

	private static final GewtResources RES = GewtResources.INSTANCE;

	private static final GewtCss CSS = RES.css();

	//- Button combinations
	// ----------------------------------------------------

	/**
	 * OK and Cancel buttons
	 */
	private static final int BUTTONS_OK_CANCEL = BUTTON_OK | BUTTON_CANCEL;

	/**
	 * Yes and No buttons
	 */
	private static final int BUTTONS_YES_NO = BUTTON_YES | BUTTON_NO;

	/**
	 * Yes, No, and Cancel buttons
	 */
	private static final int BUTTONS_YES_NO_CANCEL =
		BUTTON_YES | BUTTON_NO | BUTTON_CANCEL;

	private static final Object[] ICON_IMAGES =
		new Object[] { "$imQuestion", "$imInfo", "$imWarning", "$imError" };

	private static final String[] BUTTON_LABELS =
		new String[] { "$btnRetry", "$btnIgnore", "$btnYes", "$btnNo",
			"$btnOk",
			"$btnCancel" };

	private IsChildViewWidget dialog;

	private CellPanel buttonPanel;

	private ResultHandler resultHandler;

	/**
	 * Creates a new instance with a certain parent view.
	 *
	 * @param parent        The parent view
	 * @param title         The message box title
	 * @param message       The message text to display or NULL for none
	 * @param subMessage    A message to be displayed below image and message
	 *                      text or NULL for none
	 * @param image         An image to display or NULL for none
	 * @param buttons       The bit combination for the message box buttons
	 * @param resultHandler The handler for the message box result
	 */
	MessageBox(View parent, String title, String message, String subMessage,
		Image image, int buttons, ResultHandler resultHandler) {
		this.resultHandler = resultHandler;

		init(parent, title, message, subMessage, image, buttons);
	}

	/**
	 * Internal helper method to initialize the message box image for a certain
	 * icon index.
	 *
	 * @param component The component to be used to create the image
	 * @param index     The icon index
	 * @return The icon image
	 */
	private static Image getIconImage(Component component, int index) {
		Object image = ICON_IMAGES[index];

		if (image instanceof String) {
			image = component.getContext().createImage(image);

			ICON_IMAGES[index] = image;
		}

		return (Image) image;
	}

	/**
	 * Shows a question message dialog with three buttons: Yes, No, and Cancel.
	 *
	 * @param parent        The parent view
	 * @param title         The message box title
	 * @param message       The message text to display
	 * @param icon          The type constant of the icon to display
	 * @param resultHandler The handler for the message box result
	 */
	public static void showCancelQuestion(View parent, String title,
		String message, int icon, ResultHandler resultHandler) {
		showMessage(parent, title, message, null, icon, BUTTONS_YES_NO_CANCEL,
			resultHandler);
	}

	/**
	 * Shows a confirmation dialog. The dialog will have the buttons OK and
	 * Cancel.
	 *
	 * @param parent        The parent view
	 * @param title         The message box title
	 * @param message       The message text to display
	 * @param icon          The type constant of the icon to display
	 * @param resultHandler The handler for the message box result
	 */
	public static void showConfirmation(View parent, String title,
		String message, int icon, ResultHandler resultHandler) {
		showMessage(parent, title, message, null, icon, BUTTONS_OK_CANCEL,
			resultHandler);
	}

	/**
	 * Shows a message dialog with a predefined icon and buttons. The icon and
	 * the button(s) to be displayed must be defined with one of the constants
	 * in this class (like {@link #ICON_INFORMATION}, {@link #BUTTON_OK} or
	 * {@link #BUTTONS_OK_CANCEL}).
	 *
	 * @param parent        The parent view
	 * @param title         The message box title
	 * @param message       The message text to display or NULL for none
	 * @param subMessage    A message to be displayed below image and message
	 *                      text or NULL for none
	 * @param icon          The type constant of the icon to display
	 * @param buttons       The bit combination for the buttons to display
	 * @param resultHandler The handler for the message box result
	 */
	private static void showMessage(View parent, String title, String message,
		String subMessage, int icon, int buttons,
		ResultHandler resultHandler) {
		showMessage(parent, title, message, subMessage,
			getIconImage(parent, icon), buttons, resultHandler);
	}

	/**
	 * Shows a message dialog with arbitrary image and buttons. The button (s)
	 * to be displayed must be defined with one of the button constants in this
	 * class (like {@link #BUTTON_OK} or {@link #BUTTONS_OK_CANCEL}).
	 *
	 * @param parent        The parent view
	 * @param title         The message box title
	 * @param message       The message text to display or NULL for none
	 * @param subMessage    A message to be displayed below image and message
	 *                      text or NULL for none
	 * @param image         The image to display or NULL for none
	 * @param buttons       The bit combination for the buttons to display
	 * @param resultHandler The handler for the message box result
	 */
	private static void showMessage(View parent, String title, String message,
		String subMessage, Image image, int buttons,
		ResultHandler resultHandler) {
		MessageBox mb =
			new MessageBox(parent, title, message, subMessage, image, buttons,
				resultHandler);

		mb.show();
	}

	/**
	 * Shows a notification dialog. The dialog will have a single OK button and
	 * therefore doesn't need a result handler.
	 *
	 * @param parent  The parent view
	 * @param title   The message box title
	 * @param message The message text to display
	 * @param icon    The type constant of the icon to display
	 */
	public static void showNotification(View parent, String title,
		String message, int icon) {
		showMessage(parent, title, message, null, icon, BUTTON_OK, null);
	}

	/**
	 * Shows a question message dialog. The dialog will have the buttons 'Yes'
	 * and 'No'.
	 *
	 * @param parent        The parent view
	 * @param title         The message box title
	 * @param message       The message text to display
	 * @param icon          The type constant of the icon to display
	 * @param resultHandler The handler for the message box result
	 */
	public static void showQuestion(View parent, String title, String message,
		int icon, ResultHandler resultHandler) {
		showMessage(parent, title, message, null, icon, BUTTONS_YES_NO,
			resultHandler);
	}

	/**
	 * Handles clicks on message box buttons or on the close button of the
	 * message box view and invokes the result handler with the button number.
	 *
	 * @see ClickHandler#onClick(ClickEvent)
	 */
	@Override
	public void onClick(ClickEvent event) {
		dialog.hide();

		Widget source = (Widget) event.getSource();
		int buttonCount = buttonPanel.getWidgetCount();
		int button;

		if (source == dialog) {
			// view close button clicked = Cancel/No
			button = buttonCount;
		} else {
			// calculate button index from right to left!
			button = buttonPanel.getWidgetIndex(source) + 1;
		}

		if (button > 0) {
			if (resultHandler != null) {
				resultHandler.handleResult(buttonCount - button);
			}
		}
	}

	/**
	 * Initializes this instance.
	 *
	 * @param title      The message box title
	 * @param message    The message text to display or NULL for none
	 * @param subMessage A message to be displayed below image and text or NULL
	 *                   for none
	 * @param image      The image to display or NULL for none
	 * @param buttons    The combined bits of the message box buttons
	 */
	void init(View parent, String title, String message, String subMessage,
		Image image, int buttons) {
		dialog = EWT
			.getChildViewFactory()
			.createChildViewWidget(parent,
				ViewStyle.MODAL.withFlags(Flag.BOTTOM));

		title = EWT.expandResource(parent, title);
		message = EWT.expandResource(parent, message);

		DockPanel mainPanel = new DockPanel();
		DockPanel messagePanel = new DockPanel();

		buttonPanel = new HorizontalPanel();

		mainPanel.setSpacing(10);
		buttonPanel.setSpacing(10);

		mainPanel.add(messagePanel, DockPanel.CENTER);
		mainPanel.add(buttonPanel, DockPanel.SOUTH);

		dialog.setViewTitle(title);

		if (image instanceof ImageRef) {
			com.google.gwt.user.client.ui.Image gwtImage =
				((ImageRef) image).getGwtImage();

			gwtImage.addStyleName(CSS.ewtMessageIcon());

			messagePanel.add(gwtImage, DockPanel.WEST);
		}

		if (message != null) {
			Label label =
				addMessageLabel(messagePanel, message, DockPanel.CENTER);

			label.addStyleName(CSS.ewtMain());
		}

		if (subMessage != null) {
			addMessageLabel(messagePanel, subMessage, DockPanel.SOUTH);
		}

		boolean first = true;

		for (int i = 0; i < BUTTON_LABELS.length; i++) {
			if ((buttons & 0x1) != 0) {
				PushButton button = new PushButton(
					EWT.expandResource(parent, BUTTON_LABELS[i]));

				button.addClickHandler(this);
				buttonPanel.add(button);

				// TODO: implement automatic sizing
				button.setWidth("6EM");

				if (first) {
					final PushButton firstButton = button;

					firstButton.addStyleName("ewt-Default");

					Scheduler.get().scheduleDeferred(new ScheduledCommand() {
						@Override
						public void execute() {
							firstButton.setFocus(true);
						}
					});
					first = false;
				}
			}

			buttons >>= 1;
		}

		if (dialog instanceof PopupPanel) {
			((PopupPanel) dialog).setWidget(mainPanel);
		} else {
			dialog.add(mainPanel);
		}

		dialog.asWidget().addStyleName(CSS.ewtMessageBox());
	}

	/**
	 * Displays this message box instance centered on the screen.
	 */
	void show() {
		dialog.show();

		if (dialog instanceof PopupPanel) {
			PopupPanel popupPanel = (PopupPanel) dialog;

			popupPanel.setGlassEnabled(true);
			UserInterfaceContext.setPopupBounds(popupPanel,
				Window.getClientWidth() / 2, Window.getClientHeight() / 3,
				AlignedPosition.CENTER, true);
		}
	}

	/**
	 * Adds a message label to the given panel
	 *
	 * @param panel    The panel
	 * @param message  The message string
	 * @param position The dock layout position constant
	 * @return The new label component
	 */
	private Label addMessageLabel(DockPanel panel, String message,
		DockLayoutConstant position) {
		Label label = new Label(message, true);

		panel.add(label, position);
		label.addStyleName(CSS.ewtMessageLabel());

		return label;
	}

	/**
	 * Interface for the handling of message box results.
	 */
	public interface ResultHandler {

		/**
		 * Must be implemented to handle the message box result. The return
		 * value is the number of the message box button that has been
		 * selected,
		 * from right to left (!), starting at 0. This means that the cancel or
		 * no buttons which are always the rightmost buttons will have a button
		 * number of 0 (zero).
		 *
		 * @param button The number of the button used to close the message box
		 */
		public void handleResult(int button);
	}
}
