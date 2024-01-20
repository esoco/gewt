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

import static de.esoco.lib.property.ContentProperties.ELEMENT_ID;
import static de.esoco.lib.property.StateProperties.ACTION_EVENT_ON_ACTIVATION_ONLY;
import static de.esoco.lib.property.StateProperties.NO_EVENT_PROPAGATION;
import static de.esoco.lib.property.StyleProperties.CSS_STYLES;

/**
 * This is the base class for all GEWT components.
 */
public abstract class Component implements HasId<String> {

	/**
	 * The prefix chars for properties that define a compound of multiple
	 * derived properties.
	 */
	public static final String COMPOUND_PROPERTY_CHARS = "+%@";

	private static final String PROPERTY_PREFIX_CHARS =
		"~#" + COMPOUND_PROPERTY_CHARS;

	private static int nextId = 1;

	private static WidgetStyleHandler widgetStyleHandler = null;

	private static BiConsumer<Component, String> applyComponentErrorState =
		Component::applyComponentErrorState;

	private Container parent;

	private StyleData style;

	private IsWidget isWidget;

	private UserInterfaceContext context;

	private String id = null;

	private String toolTip = "";

	private ComponentEventDispatcher eventDispatcher;

	private String[] additionalStyles;

	/**
	 * The default implementation to set or remove the error state for a
	 * certain
	 * widget.
	 *
	 * @param component widget The widget to change the error state of
	 * @param message   The error message to display or NULL to remove the
	 *                     error
	 *                  state
	 */
	public static void applyComponentErrorState(Component component,
		String message) {
		UserInterfaceContext context = component.getContext();
		Widget widget = component.getWidget();

		if (message != null) {
			component.addStyleName(EWT.CSS.ewtError());
			widget.setTitle(context.expandResource(message));
		} else {
			component.removeStyleName(EWT.CSS.ewtError());
			widget.setTitle(context.expandResource(component.getToolTip()));
		}
	}

	/**
	 * Helper method to create the HTML string for a label that contains a
	 * string and an image.
	 *
	 * @param text                The text of the label
	 * @param image               The image of the label
	 * @param textPosition        The aligned position of the text
	 * @param horizontalAlignment The horizontal alignment of the label cells
	 * @param width               The HTML width of the label or NULL for the
	 *                            default
	 * @return The HTML string that describes the given label composition
	 */
	public static String createImageLabel(String text, ImageRef image,
		AlignedPosition textPosition,
		HorizontalAlignmentConstant horizontalAlignment, String width) {
		com.google.gwt.user.client.ui.Image gwtImage = image.getGwtImage();
		Widget labelWidget = gwtImage;

		boolean horizontal =
			textPosition.getVerticalAlignment() == Alignment.CENTER;

		if (text != null && text.length() > 0) {
			HTML html = new HTML(text);
			Alignment align;
			CellPanel panel;

			if (horizontal) {
				align = textPosition.getHorizontalAlignment();
				panel = new HorizontalPanel();
			} else {
				align = textPosition.getVerticalAlignment();
				panel = new VerticalPanel();
			}

			if (align == Alignment.BEGIN) {
				panel.add(html);
				panel.add(gwtImage);
			} else {
				panel.add(gwtImage);
				panel.add(html);
			}

			panel.setCellHorizontalAlignment(gwtImage, horizontalAlignment);
			panel.setCellVerticalAlignment(gwtImage,
				HasVerticalAlignment.ALIGN_MIDDLE);
			html.setWidth("100%");
			panel.setCellHorizontalAlignment(html, horizontalAlignment);
			panel.setCellVerticalAlignment(html,
				HasVerticalAlignment.ALIGN_MIDDLE);

			if (width != null) {
				panel.setWidth(width);
			}

			panel.setHeight("100%");

			labelWidget = panel;
		}

		return labelWidget.getElement().getString();
	}

	/**
	 * Sets a global handler to apply error states to component widgets.
	 *
	 * @param applyErrorState The new widget error state handler
	 */
	public static void setWidgetErrorStateHandler(
		BiConsumer<Component, String> applyErrorState) {
		applyComponentErrorState = applyErrorState;
	}

	/**
	 * Sets a global handler that will be invoked to apply component styles to
	 * widgets. This can be used by extensions of the base GEWT implementation
	 * to be notified of style changes.
	 *
	 * @param handler The widget style handler
	 */
	public static void setWidgetStyleHandler(WidgetStyleHandler handler) {
		widgetStyleHandler = handler;
	}

	/**
	 * Registers an event listener for a certain event type.
	 *
	 * @param eventType The event type the listener shall be registered for
	 * @param listener  The event listener to be notified of events
	 */
	public void addEventListener(EventType eventType,
		EwtEventHandler listener) {
		if (eventDispatcher == null) {
			eventDispatcher = createEventDispatcher();
		}

		eventDispatcher.setupEventDispatching(getWidget(), eventType,
			listener);
	}

	/**
	 * Adds a style name to a component. This is typically only considered by
	 * HTML-based EWT implementations.
	 *
	 * @param style The style name to add
	 * @see #removeStyleName(String)
	 */
	public void addStyleName(String style) {
		getWidget().addStyleName(style);
	}

	/**
	 * Applies a certain style to this component underlying GWT widget. This
	 * method is normally invoked by the framework when a component is created.
	 * Not all EWT implementations may support the application of (all) styles
	 * to components that already exist.
	 *
	 * <p>This method should be overridden by subclasses that need to apply
	 * subcomponent-specific styles.</p>
	 *
	 * @param newStyle The style to apply to this instance
	 */
	public void applyStyle(StyleData newStyle) {
		style = newStyle;

		String id = style.getProperty(ELEMENT_ID, null);

		if (id != null) {
			getWidget().getElement().setId(id);
		}

		applyStyleNames(style);
		applyAlignments(style);
		applyCssStyles(style);

		if (widgetStyleHandler != null) {
			widgetStyleHandler.applyWidgetStyle(this, newStyle);
		}
	}

	/**
	 * Creates a new image object that is associated with this component. In
	 * certain EWT implementations images are based on limited system resources
	 * and the implementation therefore needs to perform cleanup operations on
	 * such resources when a component's lifecycle ends. This is done
	 * automatically for images if they are created through this method. If the
	 * component's lifecycle ends all associated images will be disposed.
	 *
	 * <p>Applications that need to manage image objects independently from
	 * component lifecycles must create these images through the context method
	 * {@link UserInterfaceContext#createImage(Object)} instead. For an
	 * overview
	 * of the supported types of image data see the documentation of that
	 * method.</p>
	 *
	 * <p>Because of the ID parameter multiple images can be managed for the
	 * same component. If a new image is created with an identifier that has
	 * already been used for a previous image that previous image will be
	 * disposed. If an application decides that an component-associated
	 * image is
	 * no longer needed it may invoke the image's dispose() method any time
	 * .</p>
	 *
	 * @param iD             The identifier for the new image
	 * @param imageReference image imageData The input stream that will provide
	 *                       the image data
	 * @return A new Image instance that is associated with this component
	 */
	public Image createImage(Object iD, Object imageReference) {
		return getContext().createImage(imageReference);
	}

	/**
	 * Returns the background color of this component.
	 *
	 * @return The background color
	 */
	public Color getBackgroundColor() {
		String color =
			getWidget().getElement().getStyle().getBackgroundColor();

		return color != null ? Color.valueOf(color) : Color.WHITE;
	}

	/**
	 * Returns the user interface context this component belongs to.
	 *
	 * @return This component's user interface context
	 */
	public UserInterfaceContext getContext() {
		return context;
	}

	/**
	 * Returns the DOM element of this component's widget. This is a
	 * GWT-specific method.
	 *
	 * @return The DOM element
	 */
	public Element getElement() {
		return getWidget().getElement();
	}

	/**
	 * Returns the foreground (text) color of this component.
	 *
	 * @return The foreground color
	 */
	public Color getForegroundColor() {
		String color = getWidget().getElement().getStyle().getColor();

		return color != null ? Color.valueOf(color) : Color.BLACK;
	}

	/**
	 * Returns the height of this component in pixel.
	 *
	 * @return The height
	 */
	public int getHeight() {
		return getWidget().getOffsetHeight();
	}

	/**
	 * Returns a unique identifier for this component.
	 *
	 * @return The component ID
	 */
	@Override
	public String getId() {
		if (id == null) {
			id = toString() + nextId++;
		}

		return id;
	}

	/**
	 * Returns the widget that is wrapped by this component.
	 *
	 * @return The wrapped widget
	 */
	public Object getImplementation() {
		return getWidget();
	}

	/**
	 * Returns the parent container of this component or NULL if this is a
	 * top-level component.
	 *
	 * @return The container instance of which this component is a child
	 */
	public Container getParent() {
		return parent;
	}

	/**
	 * Returns the current style data of this instance.
	 *
	 * @return The style data
	 */
	public final StyleData getStyle() {
		return style;
	}

	/**
	 * Returns the original tool-tip text as it has been set with
	 * {@link #setToolTip(String)}.
	 *
	 * @return The tool tip text
	 */
	public String getToolTip() {
		return toolTip;
	}

	/**
	 * Returns the parent view of this component.
	 *
	 * @return The parent view
	 */
	public View getView() {
		Container view = parent;

		while (view != null && !(view instanceof View)) {
			view = view.getParent();
		}

		return (View) view;
	}

	/**
	 * Returns the widget that is wrapped by this component. This method is
	 * specific to GEWT. The method {@link #getImplementation()} should be used
	 * to access the widget in a generic EWT way.
	 *
	 * @return The widget
	 */
	public final Widget getWidget() {
		return isWidget != null ? isWidget.asWidget() : null;
	}

	/**
	 * Returns the width of this component in pixel.
	 *
	 * @return The width
	 */
	public int getWidth() {
		return getWidget().getOffsetWidth();
	}

	/**
	 * Returns the horizontal position of the component's left edge.
	 *
	 * @return The x coordinate of the component location
	 */
	public int getX() {
		return getWidget().getAbsoluteLeft();
	}

	/**
	 * Returns the vertical position of the component's top edge.
	 *
	 * @return The y coordinate of the component location
	 */
	public int getY() {
		return getWidget().getAbsoluteTop();
	}

	/**
	 * Internal method to create and initialize the GWT widget of this instance
	 * with the widget factory from {@link EWT#getWidgetFactory(Class)}.
	 *
	 * @param parent The parent container of the widget
	 * @param style  The style data of this instance
	 * @throws IllegalStateException If no widget factory has been registered
	 *                               for the class of this component instance
	 */
	public void initWidget(Container parent, StyleData style) {
		this.context = parent.getContext();
		this.parent = parent;
		this.style = style;

		setWidget(createWidget(style));
	}

	/**
	 * Returns the enabled state of this component.
	 *
	 * @return TRUE if the element is enabled, FALSE if disabled
	 */
	public boolean isEnabled() {
		Widget widget = getWidget();

		return (widget instanceof HasEnabled) &&
			((HasEnabled) widget).isEnabled();
	}

	/**
	 * Check if the component is visible.
	 *
	 * @return TRUE if visible
	 */
	public boolean isVisible() {
		return getWidget().isVisible();
	}

	/**
	 * Removes an event handler for a certain event type.
	 *
	 * @param eventType The event type the listener shall be unregistered for
	 * @param listener  The event listener to be removed
	 */
	public void removeEventListener(EventType eventType,
		EwtEventHandler listener) {
		if (eventDispatcher != null) {
			eventDispatcher.stopEventDispatching(eventType, listener);
		}
	}

	/**
	 * Removes a style name to a component. This is typically only
	 * considered by
	 * HTML-based EWT implementations.
	 *
	 * @param style The style name to remove
	 * @see #addStyleName(String)
	 */
	public void removeStyleName(String style) {
		getWidget().removeStyleName(style);
	}

	/**
	 * Requests a repaint of this component.
	 */
	public void repaint() {
	}

	/**
	 * Sets the background color of this component.
	 *
	 * @param color The new background color
	 */
	public void setBackgroundColor(Color color) {
		if (color != null) {
			getWidget()
				.getElement()
				.getStyle()
				.setBackgroundColor(color.toHtml());
		}
	}

	/**
	 * Sets the enabled state of this component. This state controls whether
	 * the
	 * element will allow and react to user input.
	 *
	 * @param enabled TRUE to enable the component, FALSE to disable it
	 */
	public void setEnabled(boolean enabled) {
		Widget widget = getWidget();

		if (widget instanceof HasEnabled) {
			((HasEnabled) widget).setEnabled(enabled);
		}
	}

	/**
	 * Sets or removes an error state for this component. Depending on the
	 * underlying implementation this will modify the component style to be
	 * rendered in an error state (if a message is provided) and display the
	 * error message (e.g. as a tool-tip).
	 *
	 * @param errorMessage The error message to display or NULL to remove the
	 *                     error state
	 */
	public void setError(String errorMessage) {
		applyComponentErrorState.accept(this, errorMessage);
	}

	/**
	 * Sets the foreground color of this component.
	 *
	 * @param color The new foreground color
	 */
	public void setForegroundColor(Color color) {
		if (color != null) {
			getWidget().getElement().getStyle().setColor(color.toHtml());
		}
	}

	/**
	 * Sets the height of this component in string format. How this value is
	 * interpreted may depend on the underlying implementation.
	 *
	 * @param height The new height
	 */
	public void setHeight(String height) {
		getWidget().setHeight(height);
	}

	/**
	 * Allows to set a component property or even multiple properties. This
	 * is a
	 * limited implementation of the standard EWT method. The standard method
	 * accepts a variable argument list that can contain multiple properties.
	 * This implementation only supports a single argument (due to the lack of
	 * variable argument lists in GWT). But it is still possible to set
	 * multiple
	 * similar named properties with the corresponding prefix (see below).
	 *
	 * <p>The method argument can be either an {@link Image} object or a string
	 * that describes the property. An image object will be set as the
	 * component
	 * image if the component implements the {@link ImageAttribute}
	 * interface. A
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
	 *   <li>@&lt;$key&gt;: a resource key for image (im) and tool tip (tt)
	 *   </li>
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
	 *   <li>"Cancel": a simple text string to be set as the component
	 *   text</li>
	 *   <li>"$btOk": a resource key for the component text</li>
	 *   <li>"#/res/img/info.png": a path to an image file</li>
	 *   <li>"#$imInfo": a resource key referencing an image file</li>
	 *   <li>"+$Copy": a resource key for component text and image (expanded to
	 *     $imCopy)</li>
	 *   <li>"~!$string": the escaped text "!$string"</li>
	 * </ul>
	 *
	 * @param property The property to set on the component
	 */
	public void setProperties(Object property) {
		if (property instanceof String) {
			setProperty((String) property);
		} else if (this instanceof ImageAttribute) {
			if (property instanceof ImageResource) {
				property = new ImageRef(property);
			}

			if (property instanceof Image) {
				((ImageAttribute) this).setImage((Image) property);
			}
		}
	}

	/**
	 * Sets the size of this component.
	 *
	 * @param w The width
	 * @param h The height
	 */
	public void setSize(int w, int h) {
		getWidget().setPixelSize(w, h);
	}

	/**
	 * Sets the text of the tool-tip. If the string contains a resource key it
	 * will be expanded.
	 *
	 * @param text The new tool tip text or NULL for no tool-tip
	 */
	public void setToolTip(String text) {
		toolTip = text;
		getWidget().setTitle(getContext().expandResource(text));
	}

	/**
	 * Set the visibility of this component. This will only affect the display
	 * of the component but not it's rendering in the parent container's
	 * layout.
	 * To prevent the rendering completely {@link #setVisible(boolean)} should
	 * be used instead.
	 *
	 * @param visible TRUE if visible
	 */
	public void setVisibility(boolean visible) {
		Style widgetStyle = getWidget().getElement().getStyle();
		String currentVisibility = widgetStyle.getVisibility();

		Visibility visibility =
			visible ? Visibility.VISIBLE : Visibility.HIDDEN;

		if (visibility == Visibility.VISIBLE && !currentVisibility.equals("") ||
			!visibility.getCssName().equals(currentVisibility)) {
			widgetStyle.setVisibility(visibility);
		}
	}

	/**
	 * Set the component's display status that also affects layout of the
	 * parent
	 * container. To only affect the rendering of the component itself the
	 * method {@link #setVisibility(boolean)} can be used instead.
	 *
	 * @param visible render TRUE to display the component, FALSE to hide it
	 */
	public void setVisible(boolean visible) {
		Widget widget = getWidget();

		if (widget.isVisible() != visible) {
			widget.setVisible(visible);
		}
	}

	/**
	 * Sets the width of this component in string format. How this value is
	 * interpreted may depend on the underlying implementation.
	 *
	 * @param width The new width
	 */
	public void setWidth(String width) {
		getWidget().setWidth(width);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		String name = getClass().getName();
		String styleName = getWidget().getStyleName();

		name = name.substring(name.lastIndexOf('.') + 1);

		if (styleName.length() > 0) {
			name += "(" + styleName + ")";
		}

		return name;
	}

	/**
	 * Applies the CSS styles from the given map to the DOM element of this
	 * component.
	 *
	 * @param cssStyles A mapping from CSS style names to style values
	 */
	protected void applyCssStyles(Map<String, String> cssStyles) {
		Style elementStyle = getElement().getStyle();

		for (Entry<String, String> css : cssStyles.entrySet()) {
			elementStyle.setProperty(css.getKey(), css.getValue());
		}
	}

	/**
	 * Creates the GWT widget for this instance with the corresponding widget
	 * factory returned by {@link EWT#getWidgetFactory(Class)}. Subclasses that
	 * override this method or layouts that need information about this
	 * component's hierarchy can invoke {@link #getParent()} to access the
	 * parent container but should be aware that this component hasn't yet been
	 * added to the parent at this point. The method {@link #getContext()} can
	 * also be invoked to access the {@link UserInterfaceContext}.
	 *
	 * @param style The component style
	 * @return The new widget
	 */
	protected IsWidget createWidget(StyleData style) {
		WidgetFactory<?> widgetFactory = EWT.getWidgetFactory(getClass());

		if (widgetFactory != null) {
			return widgetFactory.createWidget(this, style);
		} else {
			throw new IllegalStateException(
				"No widget factory for " + getClass());
		}
	}

	/**
	 * Determines the aligned position for text from the text alignment stored
	 * in a style data object. If no text alignment is set the returned value
	 * will be {@link AlignedPosition#RIGHT}.
	 *
	 * @param style The style data object
	 * @return The aligned position of the text
	 */
	protected AlignedPosition getTextPosition(StyleData style) {
		TextAlignment textAlignment = style.mapTextAlignment();
		AlignedPosition textPosition = AlignedPosition.RIGHT;

		if (textAlignment != null) {
			switch (textAlignment) {
				case LEFT:
					textPosition = AlignedPosition.LEFT;

					break;

				case RIGHT:
					textPosition = AlignedPosition.RIGHT;

					break;

				default:
					textPosition = AlignedPosition.BOTTOM;
			}
		}

		return textPosition;
	}

	/**
	 * This method must be overridden by subclasses that support additional
	 * event types. The return value must be an instance of a subclass of the
	 * class {@link ComponentEventDispatcher} which will handle the mapping
	 * from
	 * GWT to EWT events for this widget instance. This default implementation
	 * returns an instance of the base class that will handle default events.
	 *
	 * @return A new event dispatcher instance
	 */
	ComponentEventDispatcher createEventDispatcher() {
		return new ComponentEventDispatcher();
	}

	/**
	 * Returns the event listener for a certain event type. If multiple
	 * listeners are registered for the given type the returned listener
	 * will be
	 * an event multicaster that will notify all listeners on invocation.
	 *
	 * @param eventType The event type to return the listener for
	 * @return The event listener for the given type or NULL for none
	 */
	EwtEventHandler getEventListener(EventType eventType) {
		if (eventDispatcher != null) {
			return eventDispatcher.getEventHandler(eventType);
		} else {
			return null;
		}
	}

	/**
	 * Notifies the event handler of a certain event type without a native
	 * event.
	 *
	 * @param eventType The event type
	 */
	void notifyEventHandler(EventType eventType) {
		notifyEventHandler(eventType, null, null);
	}

	/**
	 * Notifies the event handler of a certain event type for a GWT
	 * {@link DomEvent}.
	 *
	 * @param eventType The event type
	 * @param domEvent  The DOM event
	 */
	void notifyEventHandler(EventType eventType, DomEvent<?> domEvent) {
		notifyEventHandler(eventType, null, domEvent.getNativeEvent());

		if (style != null && style.hasFlag(NO_EVENT_PROPAGATION)) {
			domEvent.stopPropagation();
		}
	}

	/**
	 * Notifies the event handler of a certain event.
	 *
	 * @param eventType The event type
	 * @param element   The element that is affected by the event
	 */
	void notifyEventHandler(EventType eventType, Object element) {
		notifyEventHandler(eventType, element, null);
	}

	/**
	 * Notifies the event handler of a certain event.
	 *
	 * @param eventType   The event type
	 * @param element     The element that is affected by the event
	 * @param nativeEvent pointerX The horizontal pointer coordinate
	 */
	void notifyEventHandler(EventType eventType, Object element,
		NativeEvent nativeEvent) {
		EwtEventHandler handler = getEventListener(eventType);

		if (handler != null) {
			handler.handleEvent(
				EwtEvent.getEvent(this, element, eventType, nativeEvent));
		}
	}

	/**
	 * Sets the default style name of this component.
	 *
	 * @param defaultStyleName The default style name
	 */
	void setDefaultStyleName(String defaultStyleName) {
		getWidget().setStylePrimaryName(defaultStyleName);
	}

	/**
	 * Internal method to set properties from a property string.
	 *
	 * @param property The property string
	 */
	void setProperty(String property) {
		UserInterfaceContext context = getContext();
		String image = null;
		String toolTip = null;
		char prefix = 0;

		if (property.length() > 0) {
			prefix = property.charAt(0);
		}

		// remove escape prefix
		if (PROPERTY_PREFIX_CHARS.indexOf(prefix) >= 0 &&
			property.length() > 1) {
			property = property.substring(1);

			if (prefix == '#') {
				image = property;
				property = null;
			} else if (prefix == '+' || prefix == '%' || prefix == '@') {
				StringBuffer sb = new StringBuffer(property);
				int pos = property.lastIndexOf('.') + 1;

				// if no '.' separators exist insert image prefix after '$'
				// prefix which must always exist for the prefixes +,%,@
				pos = pos > 0 ? pos : 1;

				image = sb.insert(pos, "im").toString();

				if (prefix == '%' || prefix == '@') {
					toolTip = sb.replace(pos, pos + 2, "tt").toString();

					if (prefix == '@') {
						property = null;
					}
				}
			}
		}

		if (property != null && this instanceof TextAttribute) {
			((TextAttribute) this).setText(property);
		}

		if (image != null && this instanceof ImageAttribute) {
			((ImageAttribute) this).setImage(context.createImage(image));
		}

		if (toolTip != null) {
			setToolTip(toolTip);
		}
	}

	/**
	 * Internal method to set the widget of this component.
	 *
	 * @param isWidget The component widget
	 */
	void setWidget(IsWidget isWidget) {
		this.isWidget = isWidget;
	}

	/**
	 * Applies any alignments that are set in the given {@link StyleData} to
	 * the
	 * underlying GWT widget.
	 *
	 * @param style The style data
	 */
	private void applyAlignments(StyleData style) {
		Widget widget = getWidget();

		if (widget instanceof HasHorizontalAlignment) {
			HorizontalAlignmentConstant alignment =
				style.mapHorizontalAlignment();

			if (alignment != null) {
				((HasHorizontalAlignment) widget).setHorizontalAlignment(
					alignment);
			}
		} else if (widget instanceof TextBoxBase) {
			TextAlignment alignment = style.mapTextAlignment();

			if (alignment != null) {
				((TextBoxBase) widget).setAlignment(alignment);
			}
		}

		if (widget instanceof HasVerticalAlignment) {
			VerticalAlignmentConstant alignment = style.mapVerticalAlignment();

			if (alignment != null) {
				((HasVerticalAlignment) widget).setVerticalAlignment(alignment);
			}
		}
	}

	/**
	 * Applies any CSS styles that are set in the given {@link StyleData} to
	 * the
	 * DOM element of the underlying GWT widget.
	 *
	 * @param style The style data
	 */
	private void applyCssStyles(StyleData style) {
		final Map<String, String> cssStyles =
			style.getProperty(CSS_STYLES, null);

		if (cssStyles != null) {
			if (getWidget().isAttached()) {
				applyCssStyles(cssStyles);
			} else {
				getWidget().addAttachHandler(new Handler() {
					@Override
					public void onAttachOrDetach(AttachEvent event) {
						applyCssStyles(cssStyles);
					}
				});
			}
		}
	}

	/**
	 * Applies any style names that are set in the given {@link StyleData} to
	 * the underlying GWT widget.
	 *
	 * @param styleData The style data
	 */
	private void applyStyleNames(StyleData styleData) {
		Widget widget = getWidget();
		assert widget != null;

		String webStyle = styleData.getProperty(StyleData.WEB_STYLE, null);
		String webDependentStyle =
			styleData.getProperty(StyleData.WEB_DEPENDENT_STYLE, null);
		String webAdditionalStyles =
			styleData.getProperty(StyleData.WEB_ADDITIONAL_STYLES, null);

		if (webStyle != null) {
			widget.setStylePrimaryName(webStyle);
		}

		if (additionalStyles != null) {
			for (String style : additionalStyles) {
				widget.removeStyleName(EWT.mapCssClass(style));
			}

			additionalStyles = null;
		}

		if (webAdditionalStyles != null) {
			additionalStyles = webAdditionalStyles.split("\\s+");

			for (String style : additionalStyles) {
				widget.addStyleName(EWT.mapCssClass(style));
			}
		}

		if (webDependentStyle != null) {
			widget.addStyleDependentName(webDependentStyle);
		}
	}

	/**
	 * This is the base class for event dispatcher implementations that forward
	 * GWT events to GEWT listeners. This base class implementation already
	 * supports mouse events.
	 *
	 * @author eso
	 */
	public class ComponentEventDispatcher
		implements ClickHandler, DoubleClickHandler, KeyDownHandler,
		KeyUpHandler, KeyPressHandler, FocusHandler, BlurHandler,
		MouseDownHandler, MouseUpHandler, MouseMoveHandler, MouseOutHandler,
		MouseOverHandler, MouseWheelHandler, ValueChangeHandler<Object> {

		private final Map<EventType, EwtEventHandler> eventHandlers =
			new HashMap<>(1);

		private final Map<EventType, HandlerRegistration> handlerRegistrations =
			new HashMap<>(1);

		private boolean actionEventOnActivationOnly = false;

		/**
		 * Creates a new instance.
		 */
		public ComponentEventDispatcher() {
			actionEventOnActivationOnly =
				style != null && style.hasFlag(ACTION_EVENT_ON_ACTIVATION_ONLY);
		}

		/**
		 * @see BlurHandler#onBlur(BlurEvent)
		 */
		@Override
		public void onBlur(BlurEvent event) {
			notifyEventHandler(EventType.FOCUS_LOST, event);
		}

		/**
		 * @see ClickHandler#onClick(ClickEvent)
		 */
		@Override
		public void onClick(ClickEvent event) {
			if (!actionEventOnActivationOnly ||
				!(getWidget() instanceof ActiveState) ||
				!((ActiveState) getWidget()).isActive()) {
				notifyEventHandler(EventType.ACTION, event);
			}
		}

		/**
		 * @see DoubleClickHandler#onDoubleClick(DoubleClickEvent)
		 */
		@Override
		public void onDoubleClick(DoubleClickEvent event) {
			notifyEventHandler(EventType.POINTER_DOUBLE_CLICKED, event);
		}

		/**
		 * @see FocusHandler#onFocus(FocusEvent)
		 */
		@Override
		public void onFocus(FocusEvent event) {
			notifyEventHandler(EventType.FOCUS_GAINED, event);
		}

		/**
		 * @see KeyDownHandler#onKeyDown(KeyDownEvent)
		 */
		@Override
		public void onKeyDown(KeyDownEvent event) {
			notifyEventHandler(EventType.KEY_PRESSED, event);
		}

		/**
		 * @see KeyPressHandler#onKeyPress(KeyPressEvent)
		 */
		@Override
		public void onKeyPress(KeyPressEvent event) {
			notifyEventHandler(EventType.KEY_TYPED, event);
		}

		/**
		 * @see KeyUpHandler#onKeyUp(KeyUpEvent)
		 */
		@Override
		public void onKeyUp(KeyUpEvent event) {
			notifyEventHandler(EventType.KEY_RELEASED, event);
		}

		/**
		 * @see MouseDownHandler#onMouseDown(MouseDownEvent)
		 */
		@Override
		public void onMouseDown(MouseDownEvent event) {
			notifyEventHandler(EventType.POINTER_PRESSED, event);
		}

		/**
		 * @see MouseMoveHandler#onMouseMove(MouseMoveEvent)
		 */
		@Override
		public void onMouseMove(MouseMoveEvent event) {
			notifyEventHandler(EventType.POINTER_MOVED, event);
		}

		/**
		 * @see MouseOutHandler#onMouseOut(MouseOutEvent)
		 */
		@Override
		public void onMouseOut(MouseOutEvent event) {
			notifyEventHandler(EventType.POINTER_EXITED, event);
		}

		/**
		 * @see MouseOverHandler#onMouseOver(MouseOverEvent)
		 */
		@Override
		public void onMouseOver(MouseOverEvent event) {
			notifyEventHandler(EventType.POINTER_ENTERED, event);
		}

		/**
		 * @see MouseUpHandler#onMouseUp(MouseUpEvent)
		 */
		@Override
		public void onMouseUp(MouseUpEvent event) {
			notifyEventHandler(EventType.POINTER_RELEASED, event);
		}

		/**
		 * @see MouseWheelHandler#onMouseWheel(MouseWheelEvent)
		 */
		@Override
		public void onMouseWheel(MouseWheelEvent event) {
			notifyEventHandler(EventType.POINTER_WHEEL, event);
		}

		/**
		 * @see ValueChangeHandler#onValueChange(ValueChangeEvent)
		 */
		@Override
		public void onValueChange(ValueChangeEvent<Object> event) {
			notifyEventHandler(EventType.VALUE_CHANGED);
		}

		/**
		 * Allows to check whether a handler has been registered for a certain
		 * event type.
		 *
		 * @param eventType The event type to check
		 * @return TRUE if a handler has been registered for the event type
		 */
		protected boolean hasHandlerFor(EventType eventType) {
			return getEventHandler(eventType) != null;
		}

		/**
		 * This method can be overridden by subclasses to initialize the event
		 * dispatching for a certain event type and it's specific widget
		 * subclass. The superclass method must alsways be invoked.
		 *
		 * @param widget    The widget to initialize the event dispatching for
		 * @param eventType The event type
		 * @return The event handler registration or NULL if no handler has
		 * been
		 * registered
		 */
		@SuppressWarnings("incomplete-switch")
		protected HandlerRegistration initEventDispatching(Widget widget,
			EventType eventType) {
			HandlerRegistration handler = null;

			if (widget instanceof HasAllMouseHandlers) {
				HasAllMouseHandlers mouseWidget = (HasAllMouseHandlers) widget;

				switch (eventType) {
					case POINTER_PRESSED:
						handler = mouseWidget.addMouseDownHandler(this);
						break;

					case POINTER_RELEASED:
						handler = mouseWidget.addMouseUpHandler(this);
						break;

					case POINTER_MOVED:
						handler = mouseWidget.addMouseMoveHandler(this);
						break;

					case POINTER_EXITED:
						handler = mouseWidget.addMouseOutHandler(this);
						break;

					case POINTER_ENTERED:
						handler = mouseWidget.addMouseOverHandler(this);
						break;

					case POINTER_WHEEL:
						handler = mouseWidget.addMouseWheelHandler(this);
						break;
				}
			}

			if (widget instanceof HasAllFocusHandlers) {
				HasAllFocusHandlers focusWidget = (HasAllFocusHandlers) widget;

				if (eventType == EventType.FOCUS_GAINED) {
					handler = focusWidget.addFocusHandler(this);
				} else if (eventType == EventType.FOCUS_LOST) {
					handler = focusWidget.addBlurHandler(this);
				}
			}

			if (widget instanceof HasAllKeyHandlers) {
				HasAllKeyHandlers keyWidget = (HasAllKeyHandlers) widget;

				switch (eventType) {
					case KEY_PRESSED:
						handler = keyWidget.addKeyDownHandler(this);
						break;

					case KEY_RELEASED:
						handler = keyWidget.addKeyUpHandler(this);
						break;

					case KEY_TYPED:
						handler = keyWidget.addKeyPressHandler(this);
						break;
				}
			}

			if (eventType == EventType.ACTION) {
				if (widget instanceof HasClickHandlers) {
					handler =
						((HasClickHandlers) widget).addClickHandler(this);
				}
			} else if (eventType == EventType.POINTER_DOUBLE_CLICKED) {
				if (widget instanceof HasDoubleClickHandlers) {
					handler =
						((HasDoubleClickHandlers) widget).addDoubleClickHandler(
							this);
				}
			} else if (eventType == EventType.VALUE_CHANGED) {
				if (widget instanceof HasValueChangeHandlers) {
					@SuppressWarnings("unchecked")
					HasValueChangeHandlers<Object> hasValueChangeHandlers =
						(HasValueChangeHandlers<Object>) widget;

					handler =
						hasValueChangeHandlers.addValueChangeHandler(this);
				}
			}

			return handler;
		}

		/**
		 * This method performs the setup for the dispatching of a certain
		 * event
		 * type. It also invokes
		 * {@link #initEventDispatching(Widget, EventType)} which subclasses
		 * may
		 * implement to perform additional event dispatch initializations-
		 *
		 * @param widget    The widget to initialize the dispatching for
		 * @param eventType The event type
		 * @param handler   The event listener to be notified of events
		 */
		protected void setupEventDispatching(Widget widget,
			EventType eventType,
			EwtEventHandler handler) {
			eventHandlers.put(eventType,
				EventMulticaster.add(eventHandlers.get(eventType), handler));

			if (handlerRegistrations.get(eventType) == null) {
				HandlerRegistration registration =
					initEventDispatching(widget, eventType);

				if (registration != null) {
					handlerRegistrations.put(eventType, registration);
				}
			}
		}

		/**
		 * Returns the event handler for a certain event type. If multiple
		 * handlers are registered for the given type the returned hadnler will
		 * be an event multicaster that will notify all handlers on invocation.
		 *
		 * @param eventType The event type to return the handler for
		 * @return The event handler for the given type or NULL for none
		 */
		EwtEventHandler getEventHandler(EventType eventType) {
			return eventHandlers.get(eventType);
		}

		/**
		 * Removes an event handler for a certain event type.
		 *
		 * @param eventType The event type the handler shall be unregistered
		 *                  for
		 * @param handler   The event handler to be removed
		 */
		void stopEventDispatching(EventType eventType,
			EwtEventHandler handler) {
			{
				EwtEventHandler handlerChain = getEventHandler(eventType);

				if (handlerChain != null) {
					handlerChain =
						EventMulticaster.remove(handlerChain, handler);

					if (handlerChain != null) {
						eventHandlers.put(eventType, handlerChain);
					} else {
						eventHandlers.remove(eventType);
					}
				}
			}
		}
	}

	/**
	 * Implements the GEWT event dispatcher interface for GWT widget
	 * implementations.
	 *
	 * @author eso
	 */
	class GewtEventDispatcherImpl implements GewtEventDispatcher {

		/**
		 * @see GewtEventDispatcher#dispatchEvent(EventType, NativeEvent)
		 */
		@Override
		public void dispatchEvent(EventType eventType, NativeEvent event) {
			notifyEventHandler(eventType, null, event);
		}
	}
}
