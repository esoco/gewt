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
package de.esoco.ewt;

import de.esoco.ewt.app.ChainedResource;
import de.esoco.ewt.app.GwtResource;
import de.esoco.ewt.app.Resource;
import de.esoco.ewt.component.ChildView;
import de.esoco.ewt.component.DialogView;
import de.esoco.ewt.component.MainView;
import de.esoco.ewt.component.View;
import de.esoco.ewt.event.EWTEvent;
import de.esoco.ewt.event.EWTEventHandler;
import de.esoco.ewt.event.EventType;
import de.esoco.ewt.graphics.Image;
import de.esoco.ewt.graphics.Screen;
import de.esoco.ewt.impl.gwt.GewtStrings;
import de.esoco.ewt.style.AlignedPosition;
import de.esoco.ewt.style.ViewStyle;

import java.util.ArrayList;
import java.util.List;

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


/********************************************************************
 * This is the main interface for creating and manipulating EWT components. It
 * provides access to all methods that are necessary to create and modify user
 * interface elements.
 *
 * @author eso
 */
public class UserInterfaceContext
{
	//~ Static fields/initializers ---------------------------------------------

	private static final Screen DEFAULT_SCREEN = new Screen();

	private static final GwtResource GEWT_RESOURCE =
		new GwtResource((ConstantsWithLookup) GWT.create(GewtStrings.class));

	//~ Instance fields --------------------------------------------------------

	private HandlerRegistration   rGlobalKeyHandlerRegistration;
	private List<EWTEventHandler> aGlobalKeyListeners;
	private Resource			  rResource;

	//~ Constructors -----------------------------------------------------------

	/***************************************
	 * Creates a new instance that uses a certain resource.
	 *
	 * @param rResource The context resource
	 */
	public UserInterfaceContext(Resource rResource)
	{
		this.rResource =
			rResource != null ? new ChainedResource(rResource, GEWT_RESOURCE)
							  : GEWT_RESOURCE;
	}

	//~ Static methods ---------------------------------------------------------

	/***************************************
	 * Internal method to set the bounding rectangle of a {@link PopupPanel}.
	 *
	 * @param rPopupPanel  The popup panel to set the bounds of
	 * @param x            The horizontal coordinate to display the popup at
	 * @param y            The vertical coordinate to display the popup at
	 * @param rOrigin      An {@link AlignedPosition} instance that defines the
	 *                     location of the origin for the view alignment
	 * @param bCheckBounds TRUE to check that the view's bounding rectangle is
	 *                     placed completely inside the screen area
	 */
	public static void setPopupBounds(PopupPanel	  rPopupPanel,
									  int			  x,
									  int			  y,
									  AlignedPosition rOrigin,
									  boolean		  bCheckBounds)
	{
		int nRootX	    = 0;
		int nRootY	    = 0;
		int nRootWidth  = Window.getClientWidth();
		int nRootHeight = Window.getClientHeight();
		int w		    = rPopupPanel.getOffsetWidth();
		int h		    = rPopupPanel.getOffsetHeight();

		w = Math.min(w, Math.max(nRootWidth / 4, 200));

		// calculate aligned position with negative size to position the view
		// relative to the origin
		x = rOrigin.getHorizontalAlignment().calcAlignedPosition(x, -w, true);
		y = rOrigin.getVerticalAlignment().calcAlignedPosition(y, -h, true);

		if (bCheckBounds)
		{
			if (x + w > nRootX + nRootWidth)
			{
				x = nRootX + nRootWidth - w;
			}

			if (x < nRootX)
			{
				w = w - (nRootX - x);
				x = nRootX;
			}

			if (y + h > nRootY + nRootHeight)
			{
				y = nRootY + nRootHeight - h;
			}

			if (y < nRootY)
			{
				h = h - (nRootY - y);
				y = nRootY;
			}
		}

		rPopupPanel.setPopupPosition(x, y);
		rPopupPanel.setPixelSize(w, h);
	}

	/***************************************
	 * A simple message formatting implementation. It will replace placeholder
	 * tokens in the string of the form %x with the corresponding entries from
	 * the object array in the second argument. %x can be an arbitrary
	 * single-char token as in the standard java string formatting but without
	 * additional format parameters. The array values will always be converted
	 * with their toString() method. The escape %% will insert a single percent
	 * character.
	 *
	 * @param  sPattern The pattern string with tokens in the form %x
	 * @param  rArgs    The list of arguments to replace the format tokens
	 *
	 * @return String The formatted string
	 */
	static final String formatMessage(String sPattern, Object[] rArgs)
	{
		StringBuilder aBuilder	    = new StringBuilder(sPattern);
		int			  nTokenPos     = 0;
		int			  nCurrentPos   = 0;
		int			  nPrevTokenPos = 0;
		int			  nArg		    = 0;

		while ((nTokenPos = sPattern.indexOf('%', nTokenPos)) >= 0)
		{
			if (sPattern.charAt(nTokenPos + 1) == '%')
			{
				aBuilder.deleteCharAt(nTokenPos);
			}
			else
			{
				// if valid position then replace token with corresponding
				// value (i.e., an argument object converted to a string)
				String sValue = rArgs[nArg].toString();

				nCurrentPos += (nTokenPos - nPrevTokenPos);
				aBuilder.delete(nCurrentPos, nCurrentPos + 2);
				aBuilder.insert(nCurrentPos, sValue);

				// compensate insertion of the value string and the removal of
				// 2 chars from the placeholder token
				nCurrentPos   += (sValue.length() - 2);
				nPrevTokenPos = nTokenPos;
			}

			nTokenPos++;
		}

		return aBuilder.toString();
	}

	//~ Methods ----------------------------------------------------------------

	/***************************************
	 * Adds an event listener that will be notified of global key events.
	 *
	 * @param rListener The key listener to register
	 */
	public void addGlobalKeyListener(EWTEventHandler rListener)
	{
		if (rGlobalKeyHandlerRegistration == null)
		{
			rGlobalKeyHandlerRegistration =
				Event.addNativePreviewHandler(new GlobalKeyEventHandler());
		}

		if (aGlobalKeyListeners == null)
		{
			aGlobalKeyListeners = new ArrayList<EWTEventHandler>();
		}

		aGlobalKeyListeners.add(rListener);
	}

	/***************************************
	 * Creates a new child view that belongs to a parent view. It's default
	 * layout will be an EdgeLayout.
	 *
	 * @param  rParent    The parent view
	 * @param  rViewStyle The view style (ViewStyle.DEFAULT for a default style)
	 *
	 * @return The new child view
	 */
	public ChildView createChildView(View rParent, ViewStyle rViewStyle)
	{
		return new ChildView(this, rViewStyle);
	}

	/***************************************
	 * Creates a new dialog view. If the parent view is NULL a new top-level
	 * dialog will be created.
	 *
	 * @param  rParent    The parent view or NULL for a top-level dialog
	 * @param  rViewStyle The view style (ViewStyle.DEFAULT for a default style)
	 *
	 * @return The new dialog
	 */
	public DialogView createDialog(View rParent, ViewStyle rViewStyle)
	{
		return new DialogView(this, rViewStyle);
	}

	/***************************************
	 * Creates a new application-managed EWT image object. Application-managed
	 * means that the application is responsible for the disposal of any images
	 * that have been created by this method. This is necessary because on some
	 * platforms images are heavy-weight resources that need to be released when
	 * they are no longer needed. Alternatively, an image can be associated with
	 * a component and automatically managed.
	 *
	 * <p>The argument to this method is either the direct name of an image or a
	 * resource key (prefixed with '$') for it.</p>
	 *
	 * @param  rImageReference sImage The image name
	 *
	 * @return A new EWT image instance
	 *
	 * @throws IllegalArgumentException If the given image reference is invalid
	 */
	public Image createImage(Object rImageReference)
	{
		Image rImage = null;

		if (rImageReference instanceof String)
		{
			String sImage = expandResource((String) rImageReference);

			if (sImage.startsWith("data:"))
			{
				rImage = new Image(sImage);
			}
			else if (sImage.startsWith("file:"))
			{
				rImage =
					new Image(GWT.getModuleBaseForStaticFiles() +
							  sImage.substring(5));
			}
			else
			{
				rImage = rResource.getImage(sImage);
			}

			if (rImage == null && sImage.length() > 0)
			{
				GWT.log("No image for " +
						(sImage.charAt(0) == '$' ? sImage.substring(1)
												 : sImage));
			}
		}
		else
		{
			rImage = new Image(rImageReference);
		}

		return rImage;
	}

	/***************************************
	 * Creates a new top-level view width an EdgeLayout as the default layout.
	 *
	 * @param  rViewStyle The view style (ViewStyle.DEFAULT for a default style)
	 *
	 * @return The new main view
	 */
	public MainView createMainView(ViewStyle rViewStyle)
	{
		return new MainView(this);
	}

	/***************************************
	 * This is a helper method to display a view at a certain location on the
	 * screen. For modal views like dialogs this call blocks until the view is
	 * closed. If the bCheckCoords parameter is TRUE this method also checks
	 * whether the view's bounding rectangle edges are inside the screen area
	 * and modifies the view's size and location if necessary. If multiple
	 * screens exist in the current system the coordinate check will be applied
	 * to the screen that contains the given display coordinate.
	 *
	 * <p>The {@link View#pack()} method of the argument view will not be
	 * invoked by this method, this must be done by the code that calls it. This
	 * allows the caller to modify the size of a view after packing but before
	 * displaying it.</p>
	 *
	 * <p>The origin parameter defines where the location coordinate lies in the
	 * view's coordinate system. For example, {@link AlignedPosition#CENTER}
	 * means that the view will be centered around the given coordinate, while
	 * {@link AlignedPosition#BOTTOM_RIGHT} would align the view to the left and
	 * upwards of the location (because the origin is at the bottom right corner
	 * of the view).</p>
	 *
	 * @param rView        The view to display
	 * @param x            The horizontal coordinate to display the view at
	 * @param y            The vertical coordinate to display the view at
	 * @param rOrigin      An {@link AlignedPosition} instance that defines the
	 *                     location of the origin for the view alignment
	 * @param bCheckBounds TRUE to check that the view's bounding rectangle is
	 *                     placed completely inside the screen area
	 */
	public void displayView(final View			  rView,
							final int			  x,
							final int			  y,
							final AlignedPosition rOrigin,
							final boolean		  bCheckBounds)
	{
		if (rView instanceof ChildView)
		{
			// delay positioning until all children are attached
			Scheduler.get()
					 .scheduleDeferred(new ScheduledCommand()
				{
					@Override
					public void execute()
					{
						rView.setVisible(true);
						setPopupBounds((PopupPanel) rView.getWidget(),
									   x,
									   y,
									   rOrigin,
									   bCheckBounds);
					}
				});
		}
	}

	/***************************************
	 * Displays the given view centered on the screen. The application code
	 * should invoke the method {@link View#pack()} before to ensure
	 * compatibility with other EWT platforms.
	 *
	 * @param rView The view to display
	 */
	public void displayViewCentered(final View rView)
	{
		if (rView instanceof ChildView)
		{
			// delay centering until all children are attached
			Scheduler.get()
					 .scheduleDeferred(new ScheduledCommand()
				{
					@Override
					public void execute()
					{
						rView.setVisible(true);
						((PopupPanel) rView.getWidget()).center();
					}
				});
		}
	}

	/***************************************
	 * This method expands a resource string if necessary. It checks whether the
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
	 * <p>This method is used internally by EWT to expand resource keys that are
	 * used when creating new components or setting component properties with
	 * {@link Component#setProperties(Object...)}. But it should also be used by
	 * application code that needs to query attributes from resources.</p>
	 *
	 * @param  sResource The string to check and (if necessary) expand
	 *
	 * @return The (expanded) resource string
	 */
	public String expandResource(String sResource)
	{
		if (sResource != null &&
			sResource.length() > 1 &&
			sResource.charAt(0) == '$')
		{
			if (sResource.charAt(1) == '$')
			{
				sResource = replaceResources(sResource.substring(2));
			}
			else
			{
				sResource =
					getResourceString(sResource.substring(1), null).trim();
			}
		}

		return sResource;
	}

	/***************************************
	 * Returns the default screen of this context.
	 *
	 * @return A Screen instance that describes the default screen
	 */
	public Screen getDefaultScreen()
	{
		return DEFAULT_SCREEN;
	}

	/***************************************
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
	 * @param  sKey The key that identifies the resource object
	 *
	 * @return The matching resource object or NULL if no matching resource
	 *         could be found
	 */
	public Object getResourceObject(String sKey)
	{
		return rResource.getString(sKey);
	}

	/***************************************
	 * Returns a string from the context resource. See the comment for method
	 * {@link #getResourceObject(String)} for details regarding the resource
	 * handling. The second argument is an optional list of arguments for the
	 * case that the resource string contains a message pattern that can be
	 * formatted. The placeholders in the pattern will then be replaced with the
	 * values from the format argument list. The format for the message string
	 * must be that of the {@link java.text.MessageFormat} class. If the format
	 * argument list is empty or NULL, the string will not be formatted at all.
	 *
	 * <p>If assertions are enabled (i.e. during development) this method will
	 * throw an AssertionError if no matching resource could be found for a
	 * resource key. Without assertions the resource key itself will be returned
	 * in such a case, thus preventing deployed applications from terminating
	 * because of missing resources but instead giving a hint at the missing
	 * resource string.</p>
	 *
	 * @param  sKey        The key that identifies the resource string
	 * @param  rFormatArgs The optional arguments if the resource string
	 *                     contains placeholders that shall be replaced with the
	 *                     given values
	 *
	 * @return The matching resource string or the resource key if no matching
	 *         resource could be found
	 *
	 * @throws IllegalArgumentException If the list of format arguments contains
	 *                                  values that are illegal for the format
	 *                                  pattern
	 */
	public String getResourceString(String sKey, Object[] rFormatArgs)
	{
		String sResource = rResource.getString(sKey);

		if (sResource == null)
		{
			sResource = sKey;

			// ignore images because they are normally not mapped
			if (!sKey.startsWith("im"))
			{
				GWT.log("No resource for key " + sKey);
			}
		}

		if (rFormatArgs != null && rFormatArgs.length > 0)
		{
			sResource = formatMessage(sResource, rFormatArgs);
		}

		return sResource;
	}

	/***************************************
	 * Removes an event listener that will be notified of global key events.
	 *
	 * @param rListener The key listener to remove
	 */
	public void removeGlobalKeyListener(EWTEventHandler rListener)
	{
		if (aGlobalKeyListeners != null)
		{
			aGlobalKeyListeners.remove(rListener);

			if (aGlobalKeyListeners.isEmpty())
			{
				rGlobalKeyHandlerRegistration.removeHandler();
				rGlobalKeyHandlerRegistration = null;
			}
		}
	}

	/***************************************
	 * Allows to run arbitrary code asynchronously on the user interface thread.
	 * In most toolkits the manipulation of user interface elements must be done
	 * on the user interface thread, else threading issues may arise.
	 *
	 * <p>EWT handles access from different threads automatically and
	 * transparently but for large user interface operations (like building a
	 * view or dialog, for example) it is more efficient to invoke either this
	 * method or the {@link #runNow(ResultRunner)} method. This is because the
	 * implementation must create a Runnable instance for each single call and
	 * invoke one of these methods implicitly. Therefore creating only one large
	 * Runnable and invoking it once will have better performance.</p>
	 *
	 * <p>How the invocation is actually performed depends on the underlying
	 * implementation but the application must not expect that the code in the
	 * Runnable object has already been executed when this method returns.</p>
	 *
	 * @param rRunnable The object of which the run() method will be invoked
	 *                  asynchronously
	 */
	public void runLater(final Runnable rRunnable)
	{
		Scheduler.get()
				 .scheduleDeferred(new ScheduledCommand()
			{
				@Override
				public void execute()
				{
					rRunnable.run();
				}
			});
	}

	/***************************************
	 * Replaces all occurrences of resources in the format {$[token]} with the
	 * expanded version of [token] as returned by {@link
	 * #getResourceString(String, Object[])}.
	 *
	 * @param  sText The text to replace resource tokens
	 *
	 * @return A string containing the original text with the resource tokens
	 *         expanded
	 */
	private String replaceResources(String sText)
	{
		StringBuilder aResult = new StringBuilder();
		int			  nStart;

		while ((nStart = sText.indexOf("{$")) >= 0)
		{
			aResult.append(sText.substring(0, nStart));
			nStart += 2;

			int    nEnd		 = sText.indexOf('}', nStart);
			String sResource = sText.substring(nStart, nEnd);

			aResult.append(getResourceString(sResource, null));
			sText = sText.substring(nEnd + 1);
		}

		aResult.append(sText);

		return aResult.toString();
	}

	//~ Inner Classes ----------------------------------------------------------

	/********************************************************************
	 * Handles global key events.
	 *
	 * @author eso
	 */
	private final class GlobalKeyEventHandler implements NativePreviewHandler
	{
		//~ Methods ------------------------------------------------------------

		/***************************************
		 * @see NativePreviewHandler#onPreviewNativeEvent(NativePreviewEvent)
		 */
		@Override
		public void onPreviewNativeEvent(NativePreviewEvent rPreviewEvent)
		{
			NativeEvent rEvent = rPreviewEvent.getNativeEvent();
			String	    sType  = rEvent.getType();
			char	    nChar  = (char) rEvent.getCharCode();

			if (sType.equalsIgnoreCase("keypress"))
			{
				if (nChar != 0 && rEvent.getAltKey() && rEvent.getCtrlKey())
				{
					rPreviewEvent.consume();

					EWTEvent rEwtEvent =
						EWTEvent.getEvent(UserInterfaceContext.this,
										  null,
										  EventType.KEY_TYPED,
										  rEvent);

					Scheduler.get()
							 .scheduleDeferred(new NotifyGlobalKeyListeners(rEwtEvent));
				}
			}
		}

		//~ Inner Classes ------------------------------------------------------

		/********************************************************************
		 * Command implementation for the handling of global key events.
		 *
		 * @author eso
		 */
		private final class NotifyGlobalKeyListeners implements ScheduledCommand
		{
			//~ Instance fields ------------------------------------------------

			private final EWTEvent rEwtEvent;

			//~ Constructors ---------------------------------------------------

			/***************************************
			 * Creates a new instance.
			 *
			 * @param rEwtEvent The EWT event to send
			 */
			private NotifyGlobalKeyListeners(EWTEvent rEwtEvent)
			{
				this.rEwtEvent = rEwtEvent;
			}

			//~ Methods --------------------------------------------------------

			/***************************************
			 * @see ScheduledCommand#execute()
			 */
			@Override
			public void execute()
			{
				for (EWTEventHandler rListener : aGlobalKeyListeners)
				{
					rListener.handleEvent(rEwtEvent);
				}
			}
		}
	}
}
