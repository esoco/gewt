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
package de.esoco.ewt.component;

import de.esoco.ewt.EWT;
import de.esoco.ewt.UserInterfaceContext;
import de.esoco.ewt.event.EventType;
import de.esoco.ewt.event.EwtEvent;
import de.esoco.ewt.event.EwtEventHandler;
import de.esoco.ewt.graphics.Image;
import de.esoco.ewt.graphics.ImageRef;
import de.esoco.ewt.impl.gwt.EventMulticaster;
import de.esoco.ewt.impl.gwt.GewtEventDispatcher;
import de.esoco.ewt.impl.gwt.WidgetFactory;
import de.esoco.ewt.impl.gwt.WidgetStyleHandler;
import de.esoco.ewt.property.ImageAttribute;
import de.esoco.ewt.style.AlignedPosition;
import de.esoco.ewt.style.StyleData;

import de.esoco.lib.property.ActiveState;
import de.esoco.lib.property.Alignment;
import de.esoco.lib.property.Color;
import de.esoco.lib.property.HasId;
import de.esoco.lib.property.TextAttribute;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Visibility;
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
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
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

import static de.esoco.lib.property.ContentProperties.ELEMENT_ID;
import static de.esoco.lib.property.StateProperties.ACTION_EVENT_ON_ACTIVATION_ONLY;
import static de.esoco.lib.property.StateProperties.NO_EVENT_PROPAGATION;
import static de.esoco.lib.property.StyleProperties.CSS_STYLES;


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

	private static WidgetStyleHandler rWidgetStyleHandler = null;

	private static BiConsumer<Component, String> fApplyComponentErrorState =
		Component::applyComponentErrorState;

	//~ Instance fields --------------------------------------------------------

	private Container			 rParent;
	private StyleData			 rStyle;
	private IsWidget			 rIsWidget;
	private UserInterfaceContext rContext;

	private String sId	    = null;
	private String sToolTip = "";

	private ComponentEventDispatcher aEventDispatcher;

	private String[] rAdditionalStyles;

	//~ Static methods ---------------------------------------------------------

	/***************************************
	 * The default implementation to set or remove the error state for a certain
	 * widget.
	 *
	 * @param rComponent rWidget The widget to change the error state of
	 * @param sMessage   The error message to display or NULL to remove the
	 *                   error state
	 */
	public static void applyComponentErrorState(
		Component rComponent,
		String    sMessage)
	{
		UserInterfaceContext rContext = rComponent.getContext();
		Widget				 rWidget  = rComponent.getWidget();

		if (sMessage != null)
		{
			rComponent.addStyleName(EWT.CSS.ewtError());
			rWidget.setTitle(rContext.expandResource(sMessage));
		}
		else
		{
			rComponent.removeStyleName(EWT.CSS.ewtError());
			rWidget.setTitle(rContext.expandResource(rComponent.getToolTip()));
		}
	}

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
		ImageRef					rImage,
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
			aPanel.setCellVerticalAlignment(
				rGwtImage,
				HasVerticalAlignment.ALIGN_MIDDLE);
			aHtml.setWidth("100%");
			aPanel.setCellHorizontalAlignment(aHtml, eHorizontalAlignment);
			aPanel.setCellVerticalAlignment(
				aHtml,
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

	/***************************************
	 * Sets a global handler to apply error states to component widgets.
	 *
	 * @param fApplyErrorState The new widget error state handler
	 */
	public static void setWidgetErrorStateHandler(
		BiConsumer<Component, String> fApplyErrorState)
	{
		fApplyComponentErrorState = fApplyErrorState;
	}

	/***************************************
	 * Sets a global handler that will be invoked to apply component styles to
	 * widgets. This can be used by extensions of the base GEWT implementation
	 * to be notified of style changes.
	 *
	 * @param rHandler The widget style handler
	 */
	public static void setWidgetStyleHandler(WidgetStyleHandler rHandler)
	{
		rWidgetStyleHandler = rHandler;
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
		EwtEventHandler rListener)
	{
		if (aEventDispatcher == null)
		{
			aEventDispatcher = createEventDispatcher();
		}

		aEventDispatcher.setupEventDispatching(
			getWidget(),
			rEventType,
			rListener);
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

		String sId = rStyle.getProperty(ELEMENT_ID, null);

		if (sId != null)
		{
			getWidget().getElement().setId(sId);
		}

		applyStyleNames(rStyle);
		applyAlignments(rStyle);
		applyCssStyles(rStyle);

		if (rWidgetStyleHandler != null)
		{
			rWidgetStyleHandler.applyWidgetStyle(this, rNewStyle);
		}
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
	 * Returns the background color of this component.
	 *
	 * @return The background color
	 */
	public Color getBackgroundColor()
	{
		String sColor =
			getWidget().getElement().getStyle().getBackgroundColor();

		return sColor != null ? Color.valueOf(sColor) : Color.WHITE;
	}

	/***************************************
	 * Returns the user interface context this component belongs to.
	 *
	 * @return This component's user interface context
	 */
	public UserInterfaceContext getContext()
	{
		return rContext;
	}

	/***************************************
	 * Returns the DOM element of this component's widget. This is a
	 * GWT-specific method.
	 *
	 * @return The DOM element
	 */
	public Element getElement()
	{
		return getWidget().getElement();
	}

	/***************************************
	 * Returns the foreground (text) color of this component.
	 *
	 * @return The foreground color
	 */
	public Color getForegroundColor()
	{
		String sColor = getWidget().getElement().getStyle().getColor();

		return sColor != null ? Color.valueOf(sColor) : Color.BLACK;
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
	 * Returns the original tool-tip text as it has been set with {@link
	 * #setToolTip(String)}.
	 *
	 * @return The tool tip text
	 */
	public String getToolTip()
	{
		return sToolTip;
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
	 * @return The widget
	 */
	public final Widget getWidget()
	{
		return rIsWidget != null ? rIsWidget.asWidget() : null;
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
	 * with the widget factory from {@link EWT#getWidgetFactory(Class)}.
	 *
	 * @param  rParent The parent container of the widget
	 * @param  rStyle  The style data of this instance
	 *
	 * @throws IllegalStateException If no widget factory has been registered
	 *                               for the class of this component instance
	 */
	public void initWidget(Container rParent, StyleData rStyle)
	{
		this.rContext = rParent.getContext();
		this.rParent  = rParent;
		this.rStyle   = rStyle;

		setWidget(createWidget(rStyle));
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
		EwtEventHandler rListener)
	{
		if (aEventDispatcher != null)
		{
			aEventDispatcher.stopEventDispatching(rEventType, rListener);
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
	 * Sets the background color of this component.
	 *
	 * @param rColor The new background color
	 */
	public void setBackgroundColor(Color rColor)
	{
		if (rColor != null)
		{
			getWidget().getElement()
					   .getStyle()
					   .setBackgroundColor(rColor.toHtml());
		}
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
	 * Sets or removes an error state for this component. Depending on the
	 * underlying implementation this will modify the component style to be
	 * rendered in an error state (if a message is provided) and display the
	 * error message (e.g. as a tool-tip).
	 *
	 * @param sErrorMessage The error message to display or NULL to remove the
	 *                      error state
	 */
	public void setError(String sErrorMessage)
	{
		fApplyComponentErrorState.accept(this, sErrorMessage);
	}

	/***************************************
	 * Sets the foreground color of this component.
	 *
	 * @param rColor The new foreground color
	 */
	public void setForegroundColor(Color rColor)
	{
		if (rColor != null)
		{
			getWidget().getElement().getStyle().setColor(rColor.toHtml());
		}
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
				rProperty = new ImageRef(rProperty);
			}

			if (rProperty instanceof Image)
			{
				((ImageAttribute) this).setImage((Image) rProperty);
			}
		}
	}

	/***************************************
	 * Sets the size of this component.
	 *
	 * @param w The width
	 * @param h The height
	 */
	public void setSize(int w, int h)
	{
		getWidget().setPixelSize(w, h);
	}

	/***************************************
	 * Sets the text of the tool-tip. If the string contains a resource key it
	 * will be expanded.
	 *
	 * @param sText The new tool tip text or NULL for no tool-tip
	 */
	public void setToolTip(String sText)
	{
		sToolTip = sText;
		getWidget().setTitle(getContext().expandResource(sText));
	}

	/***************************************
	 * Set the visibility of this component. This will only affect the display
	 * of the component but not it's rendering in the parent container's layout.
	 * To prevent the rendering completely {@link #setVisible(boolean)} should
	 * be used instead.
	 *
	 * @param bVisible TRUE if visible
	 */
	public void setVisibility(boolean bVisible)
	{
		Style  rWidgetStyle		  = getWidget().getElement().getStyle();
		String sCurrentVisibility = rWidgetStyle.getVisibility();

		Visibility eVisibility =
			bVisible ? Visibility.VISIBLE : Visibility.HIDDEN;

		if (eVisibility == Visibility.VISIBLE &&
			!sCurrentVisibility.equals("") ||
			!eVisibility.getCssName().equals(sCurrentVisibility))
		{
			rWidgetStyle.setVisibility(eVisibility);
		}
	}

	/***************************************
	 * Set the component's display status that also affects layout of the parent
	 * container. To only affect the rendering of the component itself the
	 * method {@link #setVisibility(boolean)} can be used instead.
	 *
	 * @param bVisible bRender TRUE to display the component, FALSE to hide it
	 */
	public void setVisible(boolean bVisible)
	{
		Widget rWidget = getWidget();

		if (rWidget.isVisible() != bVisible)
		{
			rWidget.setVisible(bVisible);
		}
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
	 * Creates the GWT widget for this instance with the corresponding widget
	 * factory returned by {@link EWT#getWidgetFactory(Class)}. Subclasses that
	 * override this method or layouts that need information about this
	 * component's hierarchy can invoke {@link #getParent()} to access the
	 * parent container but should be aware that this component hasn't yet been
	 * added to the parent at this point. The method {@link #getContext()} can
	 * also be invoked to access the {@link UserInterfaceContext}.
	 *
	 * @param  rStyle The component style
	 *
	 * @return The new widget
	 */
	protected IsWidget createWidget(StyleData rStyle)
	{
		WidgetFactory<?> rWidgetFactory = EWT.getWidgetFactory(getClass());

		if (rWidgetFactory != null)
		{
			return rWidgetFactory.createWidget(this, rStyle);
		}
		else
		{
			throw new IllegalStateException(
				"No widget factory for " +
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
	 * @param  eEventType The event type to return the listener for
	 *
	 * @return The event listener for the given type or NULL for none
	 */
	EwtEventHandler getEventListener(EventType eEventType)
	{
		if (aEventDispatcher != null)
		{
			return aEventDispatcher.getEventHandler(eEventType);
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

		if (rStyle != null && rStyle.hasFlag(NO_EVENT_PROPAGATION))
		{
			rDomEvent.stopPropagation();
		}
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
		EwtEventHandler rHandler = getEventListener(rEventType);

		if (rHandler != null)
		{
			rHandler.handleEvent(
				EwtEvent.getEvent(this, rElement, rEventType, rNativeEvent));
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
	 * @param rIsWidget The component widget
	 */
	void setWidget(IsWidget rIsWidget)
	{
		this.rIsWidget = rIsWidget;
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
				((HasHorizontalAlignment) rWidget).setHorizontalAlignment(
					rAlignment);
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
				((HasVerticalAlignment) rWidget).setVerticalAlignment(
					rAlignment);
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
			rStyle.getProperty(CSS_STYLES, null);

		if (rCssStyles != null)
		{
			if (getWidget().isAttached())
			{
				applyCssStyles(rCssStyles);
			}
			else
			{
				getWidget().addAttachHandler(
					new Handler()
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
			rAdditionalStyles = sWebAdditionalStyles.split("\\s+");

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
	public class ComponentEventDispatcher implements ClickHandler,
													 DoubleClickHandler,
													 KeyDownHandler,
													 KeyUpHandler,
													 KeyPressHandler,
													 FocusHandler, BlurHandler,
													 MouseDownHandler,
													 MouseUpHandler,
													 MouseMoveHandler,
													 MouseOutHandler,
													 MouseOverHandler,
													 MouseWheelHandler,
													 ValueChangeHandler<Object>
	{
		//~ Instance fields ----------------------------------------------------

		private boolean bActionEventOnActivationOnly = false;

		private Map<EventType, EwtEventHandler> aEventHandlers =
			new HashMap<>(1);

		private Map<EventType, HandlerRegistration> aHandlerRegistrations =
			new HashMap<>(1);

		//~ Constructors -------------------------------------------------------

		/***************************************
		 * Creates a new instance.
		 */
		public ComponentEventDispatcher()
		{
			bActionEventOnActivationOnly =
				rStyle != null &&
				rStyle.hasFlag(ACTION_EVENT_ON_ACTIVATION_ONLY);
		}

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
			if (!bActionEventOnActivationOnly ||
				!(getWidget() instanceof ActiveState) ||
				!((ActiveState) getWidget()).isActive())
			{
				notifyEventHandler(EventType.ACTION, rEvent);
			}
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
		 * @see MouseMoveHandler#onMouseMove(MouseMoveEvent)
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
		 * @see ValueChangeHandler#onValueChange(ValueChangeEvent)
		 */
		@Override
		public void onValueChange(ValueChangeEvent<Object> rEvent)
		{
			notifyEventHandler(EventType.VALUE_CHANGED);
		}

		/***************************************
		 * Allows to check whether a handler has been registered for a certain
		 * event type.
		 *
		 * @param  eEventType The event type to check
		 *
		 * @return TRUE if a handler has been registered for the event type
		 */
		protected boolean hasHandlerFor(EventType eEventType)
		{
			return getEventHandler(eEventType) != null;
		}

		/***************************************
		 * This method can be overridden by subclasses to initialize the event
		 * dispatching for a certain event type and it's specific widget
		 * subclass. The superclass method must alsways be invoked.
		 *
		 * @param  rWidget    The widget to initialize the event dispatching for
		 * @param  eEventType The event type
		 *
		 * @return The event handler registration or NULL if no handler has been
		 *         registered
		 */
		@SuppressWarnings("incomplete-switch")
		protected HandlerRegistration initEventDispatching(
			Widget    rWidget,
			EventType eEventType)
		{
			HandlerRegistration rHandler = null;

			if (rWidget instanceof HasAllMouseHandlers)
			{
				HasAllMouseHandlers rMouseWidget =
					(HasAllMouseHandlers) rWidget;

				switch (eEventType)
				{
					case POINTER_PRESSED:
						rHandler = rMouseWidget.addMouseDownHandler(this);
						break;

					case POINTER_RELEASED:
						rHandler = rMouseWidget.addMouseUpHandler(this);
						break;

					case POINTER_MOVED:
						rHandler = rMouseWidget.addMouseMoveHandler(this);
						break;

					case POINTER_EXITED:
						rHandler = rMouseWidget.addMouseOutHandler(this);
						break;

					case POINTER_ENTERED:
						rHandler = rMouseWidget.addMouseOverHandler(this);
						break;

					case POINTER_WHEEL:
						rHandler = rMouseWidget.addMouseWheelHandler(this);
						break;
				}
			}

			if (rWidget instanceof HasAllFocusHandlers)
			{
				HasAllFocusHandlers rFocusWidget =
					(HasAllFocusHandlers) rWidget;

				if (eEventType == EventType.FOCUS_GAINED)
				{
					rHandler = rFocusWidget.addFocusHandler(this);
				}
				else if (eEventType == EventType.FOCUS_LOST)
				{
					rHandler = rFocusWidget.addBlurHandler(this);
				}
			}

			if (rWidget instanceof HasAllKeyHandlers)
			{
				HasAllKeyHandlers rKeyWidget = (HasAllKeyHandlers) rWidget;

				switch (eEventType)
				{
					case KEY_PRESSED:
						rHandler = rKeyWidget.addKeyDownHandler(this);
						break;

					case KEY_RELEASED:
						rHandler = rKeyWidget.addKeyUpHandler(this);
						break;

					case KEY_TYPED:
						rHandler = rKeyWidget.addKeyPressHandler(this);
						break;
				}
			}

			if (eEventType == EventType.ACTION)
			{
				if (rWidget instanceof HasClickHandlers)
				{
					rHandler =
						((HasClickHandlers) rWidget).addClickHandler(this);
				}
			}
			else if (eEventType == EventType.POINTER_DOUBLE_CLICKED)
			{
				if (rWidget instanceof HasDoubleClickHandlers)
				{
					rHandler =
						((HasDoubleClickHandlers) rWidget)
						.addDoubleClickHandler(this);
				}
			}
			else if (eEventType == EventType.VALUE_CHANGED)
			{
				if (rWidget instanceof HasValueChangeHandlers)
				{
					@SuppressWarnings("unchecked")
					HasValueChangeHandlers<Object> rHasValueChangeHandlers =
						(HasValueChangeHandlers<Object>) rWidget;

					rHandler =
						rHasValueChangeHandlers.addValueChangeHandler(this);
				}
			}

			return rHandler;
		}

		/***************************************
		 * This method performs the setup for the dispatching of a certain event
		 * type. It also invokes {@link #initEventDispatching(Widget,
		 * EventType)} which subclasses may implement to perform additional
		 * event dispatch initializations-
		 *
		 * @param rWidget    The widget to initialize the dispatching for
		 * @param eEventType The event type
		 * @param rHandler   The event listener to be notified of events
		 */
		protected void setupEventDispatching(Widget			 rWidget,
											 EventType		 eEventType,
											 EwtEventHandler rHandler)
		{
			aEventHandlers.put(
				eEventType,
				EventMulticaster.add(aEventHandlers.get(eEventType), rHandler));

			if (aHandlerRegistrations.get(eEventType) == null)
			{
				HandlerRegistration rRegistration =
					initEventDispatching(rWidget, eEventType);

				if (rRegistration != null)
				{
					aHandlerRegistrations.put(eEventType, rRegistration);
				}
			}
		}

		/***************************************
		 * Returns the event handler for a certain event type. If multiple
		 * handlers are registered for the given type the returned hadnler will
		 * be an event multicaster that will notify all handlers on invocation.
		 *
		 * @param  eEventType The event type to return the handler for
		 *
		 * @return The event handler for the given type or NULL for none
		 */
		EwtEventHandler getEventHandler(EventType eEventType)
		{
			return aEventHandlers.get(eEventType);
		}

		/***************************************
		 * Removes an event handler for a certain event type.
		 *
		 * @param eEventType The event type the handler shall be unregistered
		 *                   for
		 * @param rHandler   The event handler to be removed
		 */
		void stopEventDispatching(
			EventType		eEventType,
			EwtEventHandler rHandler)
		{
			{
				EwtEventHandler rHandlerChain = getEventHandler(eEventType);

				if (rHandlerChain != null)
				{
					rHandlerChain =
						EventMulticaster.remove(rHandlerChain, rHandler);

					if (rHandlerChain != null)
					{
						aEventHandlers.put(eEventType, rHandlerChain);
					}
					else
					{
						aEventHandlers.remove(eEventType);
					}
				}
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
