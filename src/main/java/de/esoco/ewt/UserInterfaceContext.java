//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// This file is a part of the 'gewt' project.
// Copyright 2018 Elmar Sonnenschein, esoco GmbH, Flensburg, Germany
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
package de.esoco.ewt;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.ConstantsWithLookup;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import de.esoco.ewt.app.ChainedResource;
import de.esoco.ewt.app.GwtResource;
import de.esoco.ewt.app.Resource;
import de.esoco.ewt.component.ChildView;
import de.esoco.ewt.component.Component;
import de.esoco.ewt.component.DialogView;
import de.esoco.ewt.component.MainView;
import de.esoco.ewt.component.View;
import de.esoco.ewt.event.EventType;
import de.esoco.ewt.event.EwtEvent;
import de.esoco.ewt.event.EwtEventHandler;
import de.esoco.ewt.graphics.Icon;
import de.esoco.ewt.graphics.Image;
import de.esoco.ewt.graphics.ImageRef;
import de.esoco.ewt.graphics.Screen;
import de.esoco.ewt.impl.gwt.GewtStrings;
import de.esoco.ewt.style.AlignedPosition;
import de.esoco.ewt.style.ViewStyle;

import java.util.ArrayList;
import java.util.List;

/**
 * This is the main interface for creating and manipulating EWT components. It
 * provides access to all methods that are necessary to create and modify user
 * interface elements.
 *
 * @author eso
 */
public class UserInterfaceContext {

	private static final Screen DEFAULT_SCREEN = new Screen();

	private static final GwtResource GEWT_RESOURCE =
		new GwtResource(GWT.create(GewtStrings.class));

	private HandlerRegistration globalKeyHandlerRegistration;

	private List<EwtEventHandler> globalKeyListeners;

	private final Resource resource;

	/**
	 * Creates a new instance that uses a certain resource.
	 *
	 * @param resource The context resource
	 */
	public UserInterfaceContext(Resource resource) {
		this.resource = resource != null ?
		                new ChainedResource(resource, GEWT_RESOURCE) :
		                GEWT_RESOURCE;
	}

	/**
	 * A simple message formatting implementation. It will replace placeholder
	 * tokens in the string of the form %x with the corresponding entries from
	 * the object array in the second argument. %x can be an arbitrary
	 * single-char token as in the standard java string formatting but without
	 * additional format parameters. The array values will always be converted
	 * with their toString() method. The escape %% will insert a single percent
	 * character.
	 *
	 * @param pattern The pattern string with tokens in the form %x
	 * @param args    The list of arguments to replace the format tokens
	 * @return String The formatted string
	 */
	static final String formatMessage(String pattern, Object[] args) {
		StringBuilder builder = new StringBuilder(pattern);
		int tokenPos = 0;
		int currentPos = 0;
		int prevTokenPos = 0;
		int arg = 0;

		while ((tokenPos = pattern.indexOf('%', tokenPos)) >= 0) {
			if (pattern.charAt(tokenPos + 1) == '%') {
				builder.deleteCharAt(tokenPos);
			} else {
				// if valid position then replace token with corresponding
				// value (i.e., an argument object converted to a string)
				String value = args[arg].toString();

				currentPos += (tokenPos - prevTokenPos);
				builder.delete(currentPos, currentPos + 2);
				builder.insert(currentPos, value);

				// compensate insertion of the value string and the removal of
				// 2 chars from the placeholder token
				currentPos += (value.length() - 2);
				prevTokenPos = tokenPos;
			}

			tokenPos++;
		}

		return builder.toString();
	}

	/**
	 * Internal method to set the bounding rectangle of a {@link PopupPanel}.
	 *
	 * @param popupPanel  The popup panel to set the bounds of
	 * @param x           The horizontal coordinate to display the popup at
	 * @param y           The vertical coordinate to display the popup at
	 * @param origin      An {@link AlignedPosition} instance that defines the
	 *                    location of the origin for the view alignment
	 * @param checkBounds TRUE to check that the view's bounding rectangle is
	 *                    placed completely inside the screen area
	 */
	public static void setPopupBounds(PopupPanel popupPanel, int x, int y,
		AlignedPosition origin, boolean checkBounds) {
		int rootX = 0;
		int rootY = 0;
		int rootWidth = Window.getClientWidth();
		int rootHeight = Window.getClientHeight();
		int w = popupPanel.getOffsetWidth();
		int h = popupPanel.getOffsetHeight();

		w = Math.min(w, Math.max(rootWidth / 4, 200));

		// calculate aligned position with negative size to position the view
		// relative to the origin
		x = origin.getHorizontalAlignment().calcAlignedPosition(x, -w, true);
		y = origin.getVerticalAlignment().calcAlignedPosition(y, -h, true);

		if (checkBounds) {
			if (x + w > rootX + rootWidth) {
				x = rootX + rootWidth - w;
			}

			if (x < rootX) {
				w = w - (rootX - x);
				x = rootX;
			}

			if (y + h > rootY + rootHeight) {
				y = rootY + rootHeight - h;
			}

			if (y < rootY) {
				h = h - (rootY - y);
				y = rootY;
			}
		}

		popupPanel.setPopupPosition(x, y);
		popupPanel.setPixelSize(w, h);
	}

	/**
	 * Adds an event listener that will be notified of global key events.
	 *
	 * @param listener The key listener to register
	 */
	public void addGlobalKeyListener(EwtEventHandler listener) {
		if (globalKeyHandlerRegistration == null) {
			globalKeyHandlerRegistration =
				Event.addNativePreviewHandler(new GlobalKeyEventHandler());
		}

		if (globalKeyListeners == null) {
			globalKeyListeners = new ArrayList<EwtEventHandler>();
		}

		globalKeyListeners.add(listener);
	}

	/**
	 * Creates a new child view that belongs to a parent view. It's default
	 * layout will be an EdgeLayout.
	 *
	 * @param parent    The parent view
	 * @param viewStyle The view style (ViewStyle.DEFAULT for a default style)
	 * @return The new child view
	 */
	public ChildView createChildView(View parent, ViewStyle viewStyle) {
		ChildView childView = new ChildView(parent, viewStyle);

		return childView;
	}

	/**
	 * Creates a new dialog view. If the parent view is NULL a new top-level
	 * dialog will be created.
	 *
	 * @param parent    The parent view or NULL for a top-level dialog
	 * @param viewStyle The view style (ViewStyle.DEFAULT for a default style)
	 * @return The new dialog
	 */
	public DialogView createDialog(View parent, ViewStyle viewStyle) {
		return new DialogView(parent, viewStyle);
	}

	/**
	 * Creates a new application-managed EWT image object. Application-managed
	 * means that the application is responsible for the disposal of any images
	 * that have been created by this method. This is necessary because on some
	 * platforms images are heavy-weight resources that need to be released
	 * when
	 * they are no longer needed. Alternatively, an image can be associated
	 * with
	 * a component and automatically managed.
	 *
	 * <p>The argument to this method is either the direct name of an image
	 * or a resource key (prefixed with '$') for it.</p>
	 *
	 * @param imageReference image The image name
	 * @return A new EWT image instance
	 * @throws IllegalArgumentException If the given image reference is invalid
	 */
	public Image createImage(Object imageReference) {
		Image image = null;

		if (imageReference instanceof String) {
			String imgResource = expandResource((String) imageReference);

			if (imgResource.length() > 2) {
				if (imgResource.charAt(1) == Image.IMAGE_PREFIX_SEPARATOR) {
					String imageUri = imgResource.substring(2);

					if (imgResource.charAt(0) == Image.IMAGE_ICON_PREFIX) {
						image = new Icon(imageUri);
					} else if (imgResource.charAt(0) ==
						Image.IMAGE_DATA_PREFIX) {
						image = new ImageRef(imageUri);
					} else if (imgResource.charAt(0) ==
						Image.IMAGE_FILE_PREFIX) {
						if (!imageUri.startsWith("http")) {
							imageUri =
								GWT.getModuleBaseForStaticFiles() + imageUri;
						}

						image = new ImageRef(imageUri);
					}
				} else {
					image = resource.getImage(imgResource);
				}

				if (image == null) {
					GWT.log("No image for " + (imgResource.charAt(0) == '$' ?
					                           imgResource.substring(1) :
					                           image));
				}
			}
		} else {
			image = new ImageRef(imageReference);
		}

		return image;
	}

	/**
	 * Creates a new top-level view width an EdgeLayout as the default layout.
	 *
	 * @param viewStyle The view style (ViewStyle.DEFAULT for a default style)
	 * @return The new main view
	 */
	public MainView createMainView(ViewStyle viewStyle) {
		return new MainView(this, viewStyle);
	}

	/**
	 * This is a helper method to display a view at a certain location on the
	 * screen. For modal views like dialogs this call blocks until the view is
	 * closed. If the checkCoords parameter is TRUE this method also checks
	 * whether the view's bounding rectangle edges are inside the screen area
	 * and modifies the view's size and location if necessary. If multiple
	 * screens exist in the current system the coordinate check will be applied
	 * to the screen that contains the given display coordinate.
	 *
	 * <p>The {@link View#pack()} method of the argument view will not be
	 * invoked by this method, this must be done by the code that calls it.
	 * This
	 * allows the caller to modify the size of a view after packing but before
	 * displaying it.</p>
	 *
	 * <p>The origin parameter defines where the location coordinate lies in
	 * the view's coordinate system. For example,
	 * {@link AlignedPosition#CENTER}
	 * means that the view will be centered around the given coordinate, while
	 * {@link AlignedPosition#BOTTOM_RIGHT} would align the view to the left
	 * and
	 * upwards of the location (because the origin is at the bottom right
	 * corner
	 * of the view).</p>
	 *
	 * @param view        The view to display
	 * @param x           The horizontal coordinate to display the view at
	 * @param y           The vertical coordinate to display the view at
	 * @param origin      An {@link AlignedPosition} instance that defines the
	 *                    location of the origin for the view alignment
	 * @param checkBounds TRUE to check that the view's bounding rectangle is
	 *                    placed completely inside the screen area
	 */
	public void displayView(final View view, final int x, final int y,
		final AlignedPosition origin, final boolean checkBounds) {
		if (view instanceof ChildView) {
			// delay positioning until all children are attached
			Scheduler.get().scheduleDeferred(new ScheduledCommand() {
				@Override
				public void execute() {
					Widget viewWidget = view.getWidget();

					view.setVisible(true);

					if (viewWidget instanceof PopupPanel) {
						setPopupBounds((PopupPanel) viewWidget, x, y, origin,
							checkBounds);
					}
				}
			});
		}
	}

	/**
	 * Displays the given view centered on the screen. The application code
	 * should invoke the method {@link View#pack()} before to ensure
	 * compatibility with other EWT platforms.
	 *
	 * @param view The view to display
	 */
	public void displayViewCentered(final View view) {
		if (view instanceof ChildView) {
			// delay centering until all children are attached
			Scheduler.get().scheduleDeferred(new ScheduledCommand() {
				@Override
				public void execute() {
					Widget viewWidget = view.getWidget();

					view.setVisible(true);

					if (viewWidget instanceof PopupPanel) {
						((PopupPanel) view.getWidget()).center();
					}
				}
			});
		}
	}

	/**
	 * This method expands a resource string if necessary. It checks whether
	 * the
	 * resource argument starts with a '$' prefix and thus contains a resource
	 * key. If so, the matching resource will be looked up by means of the
	 * method {@link #getResourceString(String, Object...)}. If the resource
	 * string is not prefixed and therefore not a resource key it will be
	 * returned unchanged. Missing resources will be signaled with assertions
	 * (if enabled).
	 *
	 * <p>If a resource string starts with a double dollar sign prefix it will
	 * be parsed for resource tokens of the form {$[token]} and all of these
	 * tokens will be replaced with the corresponding string from a resource
	 * lookup.</p>
	 *
	 * <p>This method is used internally by EWT to expand resource keys that
	 * are used when creating new components or setting component properties
	 * with {@link Component#setProperties(Object)}. But it should also be used
	 * by application code that needs to query attributes from resources.</p>
	 *
	 * @param resource The string to check and (if necessary) expand
	 * @return The (expanded) resource string
	 */
	public String expandResource(String resource) {
		if (resource != null && resource.length() > 1 &&
			resource.charAt(0) == '$') {
			if (resource.charAt(1) == '$') {
				resource = replaceResources(resource.substring(2));
			} else {
				resource =
					getResourceString(resource.substring(1), null).trim();
			}
		}

		return resource;
	}

	/**
	 * Returns the default screen of this context.
	 *
	 * @return A Screen instance that describes the default screen
	 */
	public Screen getDefaultScreen() {
		return DEFAULT_SCREEN;
	}

	/**
	 * Returns an arbitrary object from the resource that is associated with
	 * this context. How the application resource is set depends on the EWT
	 * implementation and is typically done by the application framework of the
	 * respective implementation (please check the corresponding documentation
	 * for details).
	 *
	 * <p>If assertions are enabled (i.e. during development) this method will
	 * throw an AssertionError if no matching resource could be found for a
	 * resource key. Without assertions NULL will be returned.</p>
	 *
	 * @param key The key that identifies the resource object
	 * @return The matching resource object or NULL if no matching resource
	 * could be found
	 */
	public Object getResourceObject(String key) {
		return resource.getString(key);
	}

	/**
	 * Returns a string from the context resource. See the comment for method
	 * {@link #getResourceObject(String)} for details regarding the resource
	 * handling. The second argument is an optional list of arguments for the
	 * case that the resource string contains a message pattern that can be
	 * formatted. The placeholders in the pattern will then be replaced with
	 * the
	 * values from the format argument list. The format for the message string
	 * must be that of the {@code java.text.MessageFormat} class. If the format
	 * argument list is empty or NULL, the string will not be formatted at all.
	 *
	 * <p>If assertions are enabled (i.e. during development) this method will
	 * throw an AssertionError if no matching resource could be found for a
	 * resource key. Without assertions the resource key itself will be
	 * returned
	 * in such a case, thus preventing deployed applications from terminating
	 * because of missing resources but instead giving a hint at the missing
	 * resource string.</p>
	 *
	 * @param key        The key that identifies the resource string
	 * @param formatArgs The optional arguments if the resource string contains
	 *                   placeholders that shall be replaced with the given
	 *                   values
	 * @return The matching resource string or the resource key if no matching
	 * resource could be found
	 * @throws IllegalArgumentException If the list of format arguments
	 * contains
	 *                                  values that are illegal for the format
	 *                                  pattern
	 */
	public String getResourceString(String key, Object[] formatArgs) {
		String resourceString = resource.getString(key);

		if (resourceString == null) {
			resourceString = key;

			// ignore images because they are normally not mapped
			if (!key.startsWith("im")) {
				GWT.log("No resource for key " + key);
			}
		}

		if (formatArgs != null && formatArgs.length > 0) {
			resourceString = formatMessage(resourceString, formatArgs);
		}

		return resourceString;
	}

	/**
	 * Removes an event listener that will be notified of global key events.
	 *
	 * @param listener The key listener to remove
	 */
	public void removeGlobalKeyListener(EwtEventHandler listener) {
		if (globalKeyListeners != null) {
			globalKeyListeners.remove(listener);

			if (globalKeyListeners.isEmpty()) {
				globalKeyHandlerRegistration.removeHandler();
				globalKeyHandlerRegistration = null;
			}
		}
	}

	/**
	 * Allows to run arbitrary code asynchronously on the user interface
	 * thread.
	 * In most toolkits the manipulation of user interface elements must be
	 * done
	 * on the user interface thread, else threading issues may arise.
	 *
	 * <p>EWT handles access from different threads automatically and
	 * transparently but for large user interface operations (like building a
	 * view or dialog, for example) it is more efficient to invoke this method.
	 * This is because the implementation must create a Runnable instance for
	 * each single call and invoke one of these methods implicitly. Therefore
	 * creating only one large Runnable and invoking it once will have better
	 * performance.</p>
	 *
	 * <p>How the invocation is actually performed depends on the underlying
	 * implementation but the application must not expect that the code in the
	 * Runnable object has already been executed when this method returns.</p>
	 *
	 * @param runnable The object of which the run() method will be invoked
	 *                 asynchronously
	 */
	public void runLater(final Runnable runnable) {
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				runnable.run();
			}
		});
	}

	/**
	 * Replaces all occurrences of resources in the format {$[token]} with the
	 * expanded version of [token] as returned by
	 * {@link #getResourceString(String, Object[])}.
	 *
	 * @param text The text to replace resource tokens
	 * @return A string containing the original text with the resource tokens
	 * expanded
	 */
	private String replaceResources(String text) {
		StringBuilder result = new StringBuilder();
		int start;

		while ((start = text.indexOf("{$")) >= 0) {
			result.append(text, 0, start);
			start += 2;

			int end = text.indexOf('}', start);
			String resource = text.substring(start, end);

			result.append(getResourceString(resource, null));
			text = text.substring(end + 1);
		}

		result.append(text);

		return result.toString();
	}

	/**
	 * Handles global key events.
	 *
	 * @author eso
	 */
	private final class GlobalKeyEventHandler implements NativePreviewHandler {

		/**
		 * @see NativePreviewHandler#onPreviewNativeEvent(NativePreviewEvent)
		 */
		@Override
		public void onPreviewNativeEvent(NativePreviewEvent previewEvent) {
			NativeEvent event = previewEvent.getNativeEvent();
			String type = event.getType();
			char c = (char) event.getCharCode();

			if (type.equalsIgnoreCase("keypress")) {
				if (c != 0 && event.getAltKey() && event.getCtrlKey()) {
					previewEvent.consume();

					EwtEvent ewtEvent =
						EwtEvent.getEvent(UserInterfaceContext.this, null,
							EventType.KEY_TYPED, event);

					Scheduler
						.get()
						.scheduleDeferred(
							new NotifyGlobalKeyListeners(ewtEvent));
				}
			}
		}

		/**
		 * Command implementation for the handling of global key events.
		 *
		 * @author eso
		 */
		private final class NotifyGlobalKeyListeners
			implements ScheduledCommand {

			private final EwtEvent ewtEvent;

			/**
			 * Creates a new instance.
			 *
			 * @param ewtEvent The EWT event to send
			 */
			private NotifyGlobalKeyListeners(EwtEvent ewtEvent) {
				this.ewtEvent = ewtEvent;
			}

			/**
			 * @see ScheduledCommand#execute()
			 */
			@Override
			public void execute() {
				for (EwtEventHandler listener : globalKeyListeners) {
					listener.handleEvent(ewtEvent);
				}
			}
		}
	}
}
