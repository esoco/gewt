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

import de.esoco.ewt.EWT;
import de.esoco.ewt.UserInterfaceContext;
import de.esoco.ewt.event.EWTEvent;
import de.esoco.ewt.event.EWTEventHandler;
import de.esoco.ewt.event.EventType;
import de.esoco.ewt.graphics.Image;
import de.esoco.ewt.impl.gwt.EventMulticaster;
import de.esoco.ewt.impl.gwt.GewtEventDispatcher;
import de.esoco.ewt.impl.gwt.WidgetFactory;
import de.esoco.ewt.impl.gwt.WidgetWrapper;
import de.esoco.ewt.property.ImageAttribute;
import de.esoco.ewt.style.AlignedPosition;
import de.esoco.ewt.style.Alignment;
import de.esoco.ewt.style.StyleData;

import de.esoco.lib.property.HasId;
import de.esoco.lib.property.TextAttribute;
import de.esoco.lib.property.UserInterfaceProperties;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.HasAllFocusHandlers;
import com.google.gwt.event.dom.client.HasAllKeyHandlers;
import com.google.gwt.event.dom.client.HasAllMouseHandlers;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.HasDoubleClickHandlers;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.event.dom.client.MouseWheelHandler;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.logical.shared.AttachEvent.Handler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.CellPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasHorizontalAlignment.HorizontalAlignmentConstant;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment.VerticalAlignmentConstant;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.TextBoxBase;
import com.google.gwt.user.client.ui.ValueBoxBase.TextAlignment;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;


/********************************************************************
 * This is the base class for all GEWT components.
 */
public abstract class Component implements HasId<String>
{
	//~ Static fields/initializers ---------------------------------------------

	/**
	 * The prefix chars for properties that define a compound of multiple
	 * derived properties.
	 */
	public static final String COMPOUND_PROPERTY_CHARS = "+%@";

	private static final String PROPERTY_PREFIX_CHARS =
		"~#" + COMPOUND_PROPERTY_CHARS;

	private static int nNextId = 1;

	//~ Instance fields --------------------------------------------------------

	private WidgetWrapper rWidgetWrapper;
	private Container     rParent;
	private StyleData     rStyle;

	private String sId = null;

	private Map<EventType, EWTEventHandler> aEventListenerMap;

	private String[] rAdditionalStyles;

	//~ Static methods ---------------------------------------------------------

	/***************************************
	 * Helper method to create the HTML string for a label that contains a
	 * string and an image.
	 *
	 * @param  sText                The text of the label
	 * @param  rImage               The image of the label
	 * @param  rTextPosition        The aligned position of the text
	 * @param  eHorizontalAlignment The horizontal alignment of the label cells
	 * @param  sWidth               The HTML width of the label or NULL for the
	 *                              default
	 *
	 * @return The HTML string that describes the given label composition
	 */
	public static String createImageLabel(
		String						sText,
		Image						rImage,
		AlignedPosition				rTextPosition,
		HorizontalAlignmentConstant eHorizontalAlignment,
		String						sWidth)
	{
		com.google.gwt.user.client.ui.Image rGwtImage    = rImage.getGwtImage();
		Widget							    rLabelWidget = rGwtImage;

		boolean bHorizontal =
			rTextPosition.getVerticalAlignment() == Alignment.CENTER;

		if (sText != null && sText.length() > 0)
		{
			HTML	  aHtml  = new HTML(sText);
			Alignment rAlign;
			CellPanel aPanel;

			if (bHorizontal)
			{
				rAlign = rTextPosition.getHorizontalAlignment();
				aPanel = new HorizontalPanel();
			}
			else
			{
				rAlign = rTextPosition.getVerticalAlignment();
				aPanel = new VerticalPanel();
			}

			if (rAlign == Alignment.BEGIN)
			{
				aPanel.add(aHtml);
				aPanel.add(rGwtImage);
			}
			else
			{
				aPanel.add(rGwtImage);
				aPanel.add(aHtml);
			}

			aPanel.setCellHorizontalAlignment(rGwtImage, eHorizontalAlignment);
			aPanel.setCellVerticalAlignment(rGwtImage,
											HasVerticalAlignment.ALIGN_MIDDLE);
			aHtml.setWidth("100%");
			aPanel.setCellHorizontalAlignment(aHtml, eHorizontalAlignment);
			aPanel.setCellVerticalAlignment(aHtml,
											HasVerticalAlignment.ALIGN_MIDDLE);

			if (sWidth != null)
			{
				aPanel.setWidth(sWidth);
			}

			aPanel.setHeight("100%");

			rLabelWidget = aPanel;
		}

		return rLabelWidget.getElement().getString();
	}

	//~ Methods ----------------------------------------------------------------

	/***************************************
	 * Registers an event listener for a certain event type.
	 *
	 * @param rEventType The event type the listener shall be registered for
	 * @param rListener  The event listener to be notified of events
	 */
	public void addEventListener(
		EventType		rEventType,
		EWTEventHandler rListener)
	{
		if (aEventListenerMap == null)
		{
			aEventListenerMap = new HashMap<EventType, EWTEventHandler>(1);
		}

		rListener =
			EventMulticaster.add(aEventListenerMap.get(rEventType), rListener);

		aEventListenerMap.put(rEventType, rListener);
	}

	/***************************************
	 * Adds a style name to a component. This is typically only considered by
	 * HTML-based EWT implementations.
	 *
	 * @param sStyle The style name to add
	 *
	 * @see   #removeStyleName(String)
	 */
	public void addStyleName(String sStyle)
	{
		getWidget().addStyleName(sStyle);
	}

	/***************************************
	 * Applies a certain style to this component underlying GWT widget. This
	 * method is normally invoked by the framework when a component is created.
	 * Not all EWT implementations may support the application of (all) styles
	 * to components that already exist.
	 *
	 * <p>This method should be overridden by subclasses that need to apply
	 * subcomponent-specific styles.</p>
	 *
	 * @param rNewStyle The style to apply to this instance
	 */
	public void applyStyle(StyleData rNewStyle)
	{
		rStyle = rNewStyle;

		applyStyleNames(rStyle);
		applyAlignments(rStyle);
		applyCssStyles(rStyle);
	}

	/***************************************
	 * Creates a new image object that is associated with this component. In
	 * certain EWT implementations images are based on limited system resources
	 * and the implementation therefore needs to perform cleanup operations on
	 * such resources when a component's lifecycle ends. This is done
	 * automatically for images if they are created through this method. If the
	 * component's lifecycle ends all associated images will be disposed.
	 *
	 * <p>Applications that need to manage image objects independently from
	 * component lifecycles must create these images through the context method
	 * {@link UserInterfaceContext#createImage(Object)} instead. For an overview
	 * of the supported types of image data see the documentation of that
	 * method.</p>
	 *
	 * <p>Because of the ID parameter multiple images can be managed for the
	 * same component. If a new image is created with an identifier that has
	 * already been used for a previous image that previous image will be
	 * disposed. If an application decides that an component-associated image is
	 * no longer needed it may invoke the image's dispose() method any time.</p>
	 *
	 * @param  rID             The identifier for the new image
	 * @param  rImageReference sImage rImageData The input stream that will
	 *                         provide the image data
	 *
	 * @return A new Image instance that is associated with this component
	 */
	public Image createImage(Object rID, Object rImageReference)
	{
		return getContext().createImage(rImageReference);
	}

	/***************************************
	 * Returns the user interface context this component belongs to.
	 *
	 * @return This component's user interface context
	 */
	public UserInterfaceContext getContext()
	{
		return rParent.getContext();
	}

	/***************************************
	 * Returns the DOM element of this component's widget. This is a
	 * GWT-specific method.
	 *
	 * @return   The DOM element
	 *
	 * @category GEWT
	 */
	public Element getElement()
	{
		return getWidget().getElement();
	}

	/***************************************
	 * Returns the height of this component in pixel.
	 *
	 * @return The height
	 */
	public int getHeight()
	{
		return getWidget().getOffsetHeight();
	}

	/***************************************
	 * Returns a unique identifier for this component.
	 *
	 * @return The component ID
	 */
	@Override
	public String getId()
	{
		if (sId == null)
		{
			sId = toString() + nNextId++;
		}

		return sId;
	}

	/***************************************
	 * Returns the widget that is wrapped by this component.
	 *
	 * @return The wrapped widget
	 */
	public Object getImplementation()
	{
		return getWidget();
	}

	/***************************************
	 * Returns the parent container of this component or NULL if this is a
	 * top-level component.
	 *
	 * @return The container instance of which this component is a child
	 */
	public Container getParent()
	{
		return rParent;
	}

	/***************************************
	 * Returns the current style data of this instance.
	 *
	 * @return The style data
	 */
	public final StyleData getStyle()
	{
		return rStyle;
	}

	/***************************************
	 * Returns the text of the component's tool tip.
	 *
	 * @return The tool tip text string
	 */
	public String getToolTip()
	{
		return getWidget().getTitle();
	}

	/***************************************
	 * Returns the parent view of this component.
	 *
	 * @return The parent view
	 */
	public View getView()
	{
		Container rView = rParent;

		while (rView != null && !(rView instanceof View))
		{
			rView = rView.getParent();
		}

		return (View) rView;
	}

	/***************************************
	 * Returns the widget that is wrapped by this component. This method is
	 * specific to GEWT. The method {@link #getImplementation()} should be used
	 * to access the widget in a generic EWT way.
	 *
	 * @return   The widget
	 *
	 * @category GEWT
	 */
	public final Widget getWidget()
	{
		return rWidgetWrapper.asWidget();
	}

	/***************************************
	 * Returns the width of this component in pixel.
	 *
	 * @return The width
	 */
	public int getWidth()
	{
		return getWidget().getOffsetWidth();
	}

	/***************************************
	 * Returns the horizontal position of the component's left edge.
	 *
	 * @return The x coordinate of the component location
	 */
	public int getX()
	{
		return getWidget().getAbsoluteLeft();
	}

	/***************************************
	 * Returns the vertical position of the component's top edge.
	 *
	 * @return The y coordinate of the component location
	 */
	public int getY()
	{
		return getWidget().getAbsoluteTop();
	}

	/***************************************
	 * Internal method to create and initialize the GWT widget of this instance
	 * with the widget factory from {@link EWT#getWidgetFactory(Component)}.
	 *
	 * @param    rContext The context the component has been created in
	 * @param    rStyle   The style data of this instance
	 *
	 * @throws   IllegalStateException If no widget factory has been registered
	 *                                 for the class of this component instance
	 *
	 * @category GEWT
	 * @category Internal
	 */
	public void initWidget(UserInterfaceContext rContext, StyleData rStyle)
	{
		this.rStyle = rStyle;

		setWidgetWrapper(createWidget(rContext, rStyle));
		createEventDispatcher().initEventDispatching(getWidget());
	}

	/***************************************
	 * Returns the enabled state of this component.
	 *
	 * @return TRUE if the element is enabled, FALSE if disabled
	 */
	public boolean isEnabled()
	{
		Widget rWidget = getWidget();

		return (rWidget instanceof HasEnabled) &&
			   ((HasEnabled) rWidget).isEnabled();
	}

	/***************************************
	 * Check if the component is visible.
	 *
	 * @return TRUE if visible
	 */
	public boolean isVisible()
	{
		return getWidget().isVisible();
	}

	/***************************************
	 * Removes an event handler for a certain event type.
	 *
	 * @param rEventType The event type the listener shall be unregistered for
	 * @param rListener  The event listener to be removed
	 */
	public void removeEventListener(
		EventType		rEventType,
		EWTEventHandler rListener)
	{
		if (aEventListenerMap != null)
		{
			EWTEventHandler rHandler = getEventListener(rEventType);

			if (rHandler != null)
			{
				rHandler = EventMulticaster.remove(rHandler, rListener);

				if (rHandler != null)
				{
					aEventListenerMap.put(rEventType, rHandler);
				}
				else
				{
					aEventListenerMap.remove(rEventType);
				}
			}
		}
	}

	/***************************************
	 * Removes a style name to a component. This is typically only considered by
	 * HTML-based EWT implementations.
	 *
	 * @param sStyle The style name to remove
	 *
	 * @see   #addStyleName(String)
	 */
	public void removeStyleName(String sStyle)
	{
		getWidget().removeStyleName(sStyle);
	}

	/***************************************
	 * Requests a repaint of this component.
	 */
	public void repaint()
	{
	}

	/***************************************
	 * Sets the background color of this component. Currently not supported on
	 * GWT.
	 *
	 * @param nColor The new background color
	 */
	public void setBackground(int nColor)
	{
	}

	/***************************************
	 * Sets the enabled state of this component. This state controls whether the
	 * element will allow and react to user input.
	 *
	 * @param bEnabled TRUE to enable the component, FALSE to disable it
	 */
	public void setEnabled(boolean bEnabled)
	{
		Widget rWidget = getWidget();

		if (rWidget instanceof HasEnabled)
		{
			((HasEnabled) rWidget).setEnabled(bEnabled);
		}
	}

	/***************************************
	 * Sets the foreground color of this component. Currently not supported on
	 * GWT.
	 *
	 * @param nColor The new foreground color
	 */
	public void setForeground(int nColor)
	{
	}

	/***************************************
	 * Sets the height of this component in string format. How this value is
	 * interpreted may depend on the underlying implementation.
	 *
	 * @param sHeight The new height
	 */
	public void setHeight(String sHeight)
	{
		getWidget().setHeight(sHeight);
	}

	/***************************************
	 * Allows to set a component property or even multiple properties. This is a
	 * limited implementation of the standard EWT method. The standard method
	 * accepts a variable argument list that can contain multiple properties.
	 * This implementation only supports a single argument (due to the lack of
	 * variable argument lists in GWT). But it is still possible to set multiple
	 * similar named properties with the corresponding prefix (see below).
	 *
	 * <p>The method argument can be either an {@link Image} object or a string
	 * that describes the property. An image object will be set as the component
	 * image if the component implements the {@link ImageAttribute} interface. A
	 * string argument will be parsed according to several criteria: If it
	 * doesn't start with a special prefix char it will simply be set as the
	 * component's text if the component implements the {@link TextAttribute}
	 * interface.</p>
	 *
	 * <p>Property strings that are prefixed with a dollar sign ('$') will be
	 * automatically expanded from the context resource by means of the method
	 * {@link UserInterfaceContext#expandResource(String)}. If a component
	 * doesn't implement the attribute interfaces that are necessary to set a
	 * property the property will be ignored.</p>
	 *
	 * <p>A property string that starts with a special prefix character will be
	 * treated as described in the following list which shows the subset of
	 * prefixes that are recognized in GEWT:</p>
	 *
	 * <ul>
	 *   <li>#&lt;file&gt;: The remaining string will be used as the name (and
	 *     path) of an image file to be used as the element's image. If the
	 *     string is preceded with the resource prefix '$' the filename will be
	 *     retrieved from the context resource. If the component implements the
	 *     {@link ImageAttribute} interface an image will be created from the
	 *     file and set on the component.</li>
	 *   <li>+&lt;$key&gt;: The trailing string must be a resource key (i.e.
	 *     prefixed with '$') that will be used to look up the component text
	 *     and, with the automatically added prefix 'im', the component image.
	 *   </li>
	 *   <li>@&lt;$key&gt;: a resource key for image (im) and tool tip (tt)</li>
	 *   <li>%&lt;$key&gt;: a resource key for text, image (im), and tool tip
	 *     (tt)</li>
	 *   <li>~&lt;string&gt;: This is an escape that allows to prevent strings
	 *     that start with prefixed characters from being transformed. The '~'
	 *     character will be stripped from the string and the remaining string
	 *     will be used as a plain string that will be set as the component's
	 *     text.</li>
	 * </ul>
	 *
	 * <p>The following list contains some examples of property strings:</p>
	 *
	 * <ul>
	 *   <li>"Cancel": a simple text string to be set as the component text</li>
	 *   <li>"$btOk": a resource key for the component text</li>
	 *   <li>"#/res/img/info.png": a path to an image file</li>
	 *   <li>"#$imInfo": a resource key referencing an image file</li>
	 *   <li>"+$Copy": a resource key for component text and image (expanded to
	 *     $imCopy)</li>
	 *   <li>"~!$string": the escaped text "!$string"</li>
	 * </ul>
	 *
	 * @param rProperty The property to set on the component
	 */
	public void setProperties(Object rProperty)
	{
		if (rProperty instanceof String)
		{
			setProperty((String) rProperty);
		}
		else if (this instanceof ImageAttribute)
		{
			if (rProperty instanceof ImageResource)
			{
				rProperty = new Image(rProperty);
			}

			if (rProperty instanceof Image)
			{
				((ImageAttribute) this).setImage((Image) rProperty);
			}
		}
	}

	/***************************************
	 * Sets the size of the component.
	 *
	 * @param w The width
	 * @param h The height
	 */
	public void setSize(int w, int h)
	{
		getWidget().setPixelSize(w, h);
	}

	/***************************************
	 * Sets the text of the component's tool tip.
	 *
	 * @param sText The new tool tip text (NULL for no tool tip)
	 */
	public void setToolTip(String sText)
	{
		getWidget().setTitle(getContext().expandResource(sText));
	}

	/***************************************
	 * Set the component's visibility.
	 *
	 * @param bVisible TRUE if visible
	 */
	public void setVisible(boolean bVisible)
	{
		getWidget().setVisible(bVisible);
	}

	/***************************************
	 * Sets the width of this component in string format. How this value is
	 * interpreted may depend on the underlying implementation.
	 *
	 * @param sWidth The new width
	 */
	public void setWidth(String sWidth)
	{
		getWidget().setWidth(sWidth);
	}

	/***************************************
	 * {@inheritDoc}
	 */
	@Override
	public String toString()
	{
		String sName	  = getClass().getName();
		String sStyleName = getWidget().getStyleName();

		sName = sName.substring(sName.lastIndexOf('.') + 1);

		if (sStyleName.length() > 0)
		{
			sName += "(" + sStyleName + ")";
		}

		return sName;
	}

	/***************************************
	 * Applies the CSS styles from the given map to the DOM element of this
	 * component.
	 *
	 * @param rCssStyles A mapping from CSS style names to style values
	 */
	protected void applyCssStyles(Map<String, String> rCssStyles)
	{
		Style rElementStyle = getElement().getStyle();

		for (Entry<String, String> rCss : rCssStyles.entrySet())
		{
			rElementStyle.setProperty(rCss.getKey(), rCss.getValue());
		}
	}

	/***************************************
	 * Creates the GWT widget for this instance by performing a lookup of the
	 * widget factory through {@link EWT#getWidgetFactory(Component)}.
	 *
	 * @param  rContext The context of this component
	 * @param  rStyle   The component style
	 *
	 * @return The new widget
	 */
	protected WidgetWrapper createWidget(
		UserInterfaceContext rContext,
		StyleData			 rStyle)
	{
		WidgetFactory<?> rWidgetFactory = EWT.getWidgetFactory(this);

		if (rWidgetFactory != null)
		{
			return wrapIsWidget(rWidgetFactory.createWidget(rContext, rStyle));
		}
		else
		{
			throw new IllegalStateException("No widget factory for " +
											getClass());
		}
	}

	/***************************************
	 * Determines the aligned position for text from the text alignment stored
	 * in a style data object. If no text alignment is set the returned value
	 * will be {@link AlignedPosition#RIGHT}.
	 *
	 * @param  rStyle The style data object
	 *
	 * @return The aligned position of the text
	 */
	protected AlignedPosition getTextPosition(StyleData rStyle)
	{
		TextAlignment   eTextAlignment = rStyle.mapTextAlignment();
		AlignedPosition aTextPosition  = AlignedPosition.RIGHT;

		if (eTextAlignment != null)
		{
			switch (eTextAlignment)
			{
				case LEFT:
					aTextPosition = AlignedPosition.LEFT;

					break;

				case RIGHT:
					aTextPosition = AlignedPosition.RIGHT;

					break;

				default:
					aTextPosition = AlignedPosition.BOTTOM;
			}
		}

		return aTextPosition;
	}

	/***************************************
	 * This method must be overridden by subclasses that support additional
	 * event types. The return value must be an instance of a subclass of the
	 * class {@link ComponentEventDispatcher} which will handle the mapping from
	 * GWT to EWT events for this widget instance. This default implementation
	 * returns an instance of the base class that will handle default events.
	 *
	 * @return A new event dispatcher instance
	 */
	ComponentEventDispatcher createEventDispatcher()
	{
		return new ComponentEventDispatcher();
	}

	/***************************************
	 * Returns the event listener for a certain event type. If multiple
	 * listeners are registered for the given type the returned listener will be
	 * an event multicaster that will notify all listeners on invocation.
	 *
	 * @param  rEventType The event type to return the listener for
	 *
	 * @return The event listener for the given type or NULL for none
	 */
	EWTEventHandler getEventListener(EventType rEventType)
	{
		if (aEventListenerMap != null)
		{
			return aEventListenerMap.get(rEventType);
		}
		else
		{
			return null;
		}
	}

	/***************************************
	 * Notifies the event handler of a certain event type without a native
	 * event.
	 *
	 * @param rEventType The event type
	 */
	void notifyEventHandler(EventType rEventType)
	{
		notifyEventHandler(rEventType, null, null);
	}

	/***************************************
	 * Notifies the event handler of a certain event type for a GWT {@link
	 * DomEvent}.
	 *
	 * @param rEventType The event type
	 * @param rDomEvent  The DOM event
	 */
	void notifyEventHandler(EventType rEventType, DomEvent<?> rDomEvent)
	{
		notifyEventHandler(rEventType, null, rDomEvent.getNativeEvent());
	}

	/***************************************
	 * Notifies the event handler of a certain event.
	 *
	 * @param rEventType The event type
	 * @param rElement   The element that is affected by the event
	 */
	void notifyEventHandler(EventType rEventType, Object rElement)
	{
		notifyEventHandler(rEventType, rElement, null);
	}

	/***************************************
	 * Notifies the event handler of a certain event.
	 *
	 * @param rEventType   The event type
	 * @param rElement     The element that is affected by the event
	 * @param rNativeEvent nPointerX The horizontal pointer coordinate
	 */
	void notifyEventHandler(EventType   rEventType,
							Object		rElement,
							NativeEvent rNativeEvent)
	{
		EWTEventHandler rHandler = getEventListener(rEventType);

		if (rHandler != null)
		{
			rHandler.handleEvent(EWTEvent.getEvent(this,
												   rElement,
												   rEventType,
												   rNativeEvent));
		}
	}

	/***************************************
	 * Sets the default style name of this component.
	 *
	 * @param sDefaultStyleName The default style name
	 */
	void setDefaultStyleName(String sDefaultStyleName)
	{
		getWidget().setStylePrimaryName(sDefaultStyleName);
	}

	/***************************************
	 * Internal method to set the parent container of this component.
	 *
	 * @param rParent The new parent
	 */
	void setParent(Container rParent)
	{
		this.rParent = rParent;
	}

	/***************************************
	 * Internal method to set properties from a property string.
	 *
	 * @param sProperty The property string
	 */
	void setProperty(String sProperty)
	{
		UserInterfaceContext rContext = getContext();
		String				 sImage   = null;
		String				 sToolTip = null;
		char				 cPrefix  = 0;

		if (sProperty.length() > 0)
		{
			cPrefix = sProperty.charAt(0);
		}

		// remove escape prefix
		if (PROPERTY_PREFIX_CHARS.indexOf(cPrefix) >= 0 &&
			sProperty.length() > 1)
		{
			sProperty = sProperty.substring(1);

			if (cPrefix == '#')
			{
				sImage    = sProperty;
				sProperty = null;
			}
			else if (cPrefix == '+' || cPrefix == '%' || cPrefix == '@')
			{
				StringBuffer sb   = new StringBuffer(sProperty);
				int			 nPos = sProperty.lastIndexOf('.') + 1;

				// if no '.' separators exist insert image prefix after '$'
				// prefix which must always exist for the prefixes +,%,@
				nPos = nPos > 0 ? nPos : 1;

				sImage = sb.insert(nPos, "im").toString();

				if (cPrefix == '%' || cPrefix == '@')
				{
					sToolTip = sb.replace(nPos, nPos + 2, "tt").toString();

					if (cPrefix == '@')
					{
						sProperty = null;
					}
				}
			}
		}

		if (sProperty != null && this instanceof TextAttribute)
		{
			((TextAttribute) this).setText(sProperty);
		}

		if (sImage != null && this instanceof ImageAttribute)
		{
			((ImageAttribute) this).setImage(rContext.createImage(sImage));
		}

		if (sToolTip != null)
		{
			setToolTip(sToolTip);
		}
	}

	/***************************************
	 * Internal method to set the widget of this component.
	 *
	 * @param rWidgetWrapper The wrapper for the component widget
	 */
	void setWidgetWrapper(WidgetWrapper rWidgetWrapper)
	{
		this.rWidgetWrapper = rWidgetWrapper;
	}

	/***************************************
	 * Wraps an instance of {@link IsWidget} inside a WidgetWrapper if it is not
	 * such already.
	 *
	 * @param  aIsWidget The IsWidget instance to check for wrapping
	 *
	 * @return A {@link WidgetWrapper} instance
	 */
	WidgetWrapper wrapIsWidget(IsWidget aIsWidget)
	{
		WidgetWrapper aWrapper;

		if (aIsWidget instanceof WidgetWrapper)
		{
			aWrapper = (WidgetWrapper) aIsWidget;
		}
		else
		{
			aWrapper = new WidgetWrapper(aIsWidget);
		}

		return aWrapper;
	}

	/***************************************
	 * Applies any alignments that are set in the given {@link StyleData} to the
	 * underlying GWT widget.
	 *
	 * @param rStyle The style data
	 */
	private void applyAlignments(StyleData rStyle)
	{
		Widget rWidget = getWidget();

		if (rWidget instanceof HasHorizontalAlignment)
		{
			HorizontalAlignmentConstant rAlignment =
				rStyle.mapHorizontalAlignment();

			if (rAlignment != null)
			{
				((HasHorizontalAlignment) rWidget).setHorizontalAlignment(rAlignment);
			}
		}
		else if (rWidget instanceof TextBoxBase)
		{
			TextAlignment rAlignment = rStyle.mapTextAlignment();

			if (rAlignment != null)
			{
				((TextBoxBase) rWidget).setAlignment(rAlignment);
			}
		}

		if (rWidget instanceof HasVerticalAlignment)
		{
			VerticalAlignmentConstant rAlignment =
				rStyle.mapVerticalAlignment();

			if (rAlignment != null)
			{
				((HasVerticalAlignment) rWidget).setVerticalAlignment(rAlignment);
			}
		}
	}

	/***************************************
	 * Applies any CSS styles that are set in the given {@link StyleData} to the
	 * DOM element of the underlying GWT widget.
	 *
	 * @param rStyle The style data
	 */
	private void applyCssStyles(StyleData rStyle)
	{
		final Map<String, String> rCssStyles =
			rStyle.getProperty(UserInterfaceProperties.CSS_STYLES, null);

		if (rCssStyles != null)
		{
			if (getWidget().isAttached())
			{
				applyCssStyles(rCssStyles);
			}
			else
			{
				getWidget().addAttachHandler(new Handler()
					{
						@Override
						public void onAttachOrDetach(AttachEvent rEvent)
						{
							applyCssStyles(rCssStyles);
						}
					});
			}
		}
	}

	/***************************************
	 * Applies any style names that are set in the given {@link StyleData} to
	 * the underlying GWT widget.
	 *
	 * @param rStyle The style data
	 */
	private void applyStyleNames(StyleData rStyle)
	{
		Widget rWidget			    = getWidget();
		String sWebStyle		    =
			rStyle.getProperty(StyleData.WEB_STYLE, null);
		String sWebDependentStyle   =
			rStyle.getProperty(StyleData.WEB_DEPENDENT_STYLE, null);
		String sWebAdditionalStyles =
			rStyle.getProperty(StyleData.WEB_ADDITIONAL_STYLES, null);

		if (sWebStyle != null)
		{
			rWidget.setStylePrimaryName(sWebStyle);
		}

		if (rAdditionalStyles != null)
		{
			for (String sStyle : rAdditionalStyles)
			{
				rWidget.removeStyleName(EWT.mapCssClass(sStyle));
			}

			rAdditionalStyles = null;
		}

		if (sWebAdditionalStyles != null)
		{
			rAdditionalStyles = sWebAdditionalStyles.split(" ");

			for (String sStyle : rAdditionalStyles)
			{
				rWidget.addStyleName(EWT.mapCssClass(sStyle));
			}
		}

		if (sWebDependentStyle != null)
		{
			rWidget.addStyleDependentName(sWebDependentStyle);
		}
	}

	//~ Inner Classes ----------------------------------------------------------

	/********************************************************************
	 * This is the base class for event dispatcher implementations that forward
	 * GWT events to GEWT listeners. This base class implementation already
	 * supports mouse events.
	 *
	 * @author eso
	 */
	class ComponentEventDispatcher implements ClickHandler, DoubleClickHandler,
											  KeyDownHandler, KeyUpHandler,
											  KeyPressHandler, FocusHandler,
											  BlurHandler, MouseDownHandler,
											  MouseUpHandler, MouseMoveHandler,
											  MouseOutHandler, MouseOverHandler,
											  MouseWheelHandler
	{
		//~ Methods ------------------------------------------------------------

		/***************************************
		 * @see BlurHandler#onBlur(BlurEvent)
		 */
		@Override
		public void onBlur(BlurEvent rEvent)
		{
			notifyEventHandler(EventType.FOCUS_LOST, rEvent);
		}

		/***************************************
		 * @see ClickHandler#onClick(ClickEvent)
		 */
		@Override
		public void onClick(ClickEvent rEvent)
		{
			notifyEventHandler(EventType.ACTION, rEvent);
		}

		/***************************************
		 * @see DoubleClickHandler#onDoubleClick(DoubleClickEvent)
		 */
		@Override
		public void onDoubleClick(DoubleClickEvent rEvent)
		{
			notifyEventHandler(EventType.POINTER_DOUBLE_CLICKED, rEvent);
		}

		/***************************************
		 * @see FocusHandler#onFocus(FocusEvent)
		 */
		@Override
		public void onFocus(FocusEvent rEvent)
		{
			notifyEventHandler(EventType.FOCUS_GAINED, rEvent);
		}

		/***************************************
		 * @see KeyDownHandler#onKeyDown(KeyDownEvent)
		 */
		@Override
		public void onKeyDown(KeyDownEvent rEvent)
		{
			notifyEventHandler(EventType.KEY_PRESSED, rEvent);
		}

		/***************************************
		 * @see KeyPressHandler#onKeyPress(KeyPressEvent)
		 */
		@Override
		public void onKeyPress(KeyPressEvent rEvent)
		{
			notifyEventHandler(EventType.KEY_TYPED, rEvent);
		}

		/***************************************
		 * @see KeyUpHandler#onKeyUp(KeyUpEvent)
		 */
		@Override
		public void onKeyUp(KeyUpEvent rEvent)
		{
			notifyEventHandler(EventType.KEY_RELEASED, rEvent);
		}

		/***************************************
		 * @see MouseDownHandler#onMouseDown(MouseDownEvent)
		 */
		@Override
		public void onMouseDown(MouseDownEvent rEvent)
		{
			notifyEventHandler(EventType.POINTER_PRESSED, rEvent);
		}

		/***************************************
		 * @see MouseMoveHandler#onMouseMove(Widget, int, int)
		 */
		@Override
		public void onMouseMove(MouseMoveEvent rEvent)
		{
			notifyEventHandler(EventType.POINTER_MOVED, rEvent);
		}

		/***************************************
		 * @see MouseOutHandler#onMouseOut(MouseOutEvent)
		 */
		@Override
		public void onMouseOut(MouseOutEvent rEvent)
		{
			notifyEventHandler(EventType.POINTER_EXITED, rEvent);
		}

		/***************************************
		 * @see MouseOverHandler#onMouseOver(MouseOverEvent)
		 */
		@Override
		public void onMouseOver(MouseOverEvent rEvent)
		{
			notifyEventHandler(EventType.POINTER_ENTERED, rEvent);
		}

		/***************************************
		 * @see MouseUpHandler#onMouseUp(MouseUpEvent)
		 */
		@Override
		public void onMouseUp(MouseUpEvent rEvent)
		{
			notifyEventHandler(EventType.POINTER_RELEASED, rEvent);
		}

		/***************************************
		 * @see MouseWheelHandler#onMouseWheel(MouseWheelEvent)
		 */
		@Override
		public void onMouseWheel(MouseWheelEvent rEvent)
		{
			notifyEventHandler(EventType.POINTER_WHEEL, rEvent);
		}

		/***************************************
		 * This method must be overridden by subclasses to initialize the event
		 * dispatching for a certain widget. The overriding method should always
		 * invoke the superclass method to inherit the default event
		 * dispatching.
		 *
		 * @param rWidget The widget to initialize the dispatching for
		 */
		void initEventDispatching(Widget rWidget)
		{
			if (rWidget instanceof HasAllMouseHandlers)
			{
				HasAllMouseHandlers rMouseWidget =
					(HasAllMouseHandlers) rWidget;

				rMouseWidget.addMouseDownHandler(this);
				rMouseWidget.addMouseUpHandler(this);
				rMouseWidget.addMouseMoveHandler(this);
				rMouseWidget.addMouseOutHandler(this);
				rMouseWidget.addMouseOverHandler(this);
				rMouseWidget.addMouseWheelHandler(this);
			}

			if (rWidget instanceof HasAllFocusHandlers)
			{
				HasAllFocusHandlers rFocusWidget =
					(HasAllFocusHandlers) rWidget;

				rFocusWidget.addFocusHandler(this);
				rFocusWidget.addBlurHandler(this);
			}

			if (rWidget instanceof HasAllKeyHandlers)
			{
				HasAllKeyHandlers rKeyWidget = (HasAllKeyHandlers) rWidget;

				rKeyWidget.addKeyDownHandler(this);
				rKeyWidget.addKeyUpHandler(this);
				rKeyWidget.addKeyPressHandler(this);
			}

			if (rWidget instanceof HasClickHandlers)
			{
				((HasClickHandlers) rWidget).addClickHandler(this);
			}

			if (rWidget instanceof HasDoubleClickHandlers)
			{
				((HasDoubleClickHandlers) rWidget).addDoubleClickHandler(this);
			}
		}
	}

	/********************************************************************
	 * Implements the GEWT event dispatcher interface for GWT widget
	 * implementations.
	 *
	 * @author eso
	 */
	class GewtEventDispatcherImpl implements GewtEventDispatcher
	{
		//~ Methods ------------------------------------------------------------

		/***************************************
		 * @see GewtEventDispatcher#dispatchEvent(EventType, NativeEvent)
		 */
		@Override
		public void dispatchEvent(EventType rEventType, NativeEvent rEvent)
		{
			notifyEventHandler(rEventType, null, rEvent);
		}
	}
}
