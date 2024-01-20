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
import de.esoco.ewt.graphics.Image;
import de.esoco.ewt.graphics.ImageRef;
import de.esoco.ewt.impl.gwt.WidgetFactory;
import de.esoco.ewt.property.ImageAttribute;
import de.esoco.ewt.style.AlignedPosition;
import de.esoco.ewt.style.StyleData;
import de.esoco.ewt.style.StyleFlag;

import de.esoco.lib.property.ContentType;
import de.esoco.lib.property.LabelStyle;
import de.esoco.lib.property.TextAttribute;
import de.esoco.lib.property.UserInterfaceProperties;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.LabelElement;
import com.google.gwt.dom.client.LegendElement;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.Widget;

import static de.esoco.lib.property.ContentProperties.CONTENT_TYPE;

/**
 * A GWT-specific EWT label implementation.
 *
 * @author eso
 */
public class Label extends Component implements TextAttribute, ImageAttribute {

	private static final String URL_TEXT_PREFIX = "url:";

	private String text;

	private Image image;

	private AlignedPosition textPosition;

	private boolean containsHtml;

	/**
	 * @see Control#applyStyle(StyleData)
	 */
	@Override
	public void applyStyle(StyleData style) {
		super.applyStyle(style);

		textPosition = getTextPosition(style);
		containsHtml =
			style.getProperty(CONTENT_TYPE, null) == ContentType.HTML;

		if (containsHtml) {
			addStyleName("contains-html");
		}
	}

	/**
	 * @see ImageAttribute#getImage()
	 */
	@Override
	public Image getImage() {
		return image;
	}

	/**
	 * @see TextAttribute#getText()
	 */
	@Override
	public String getText() {
		return text;
	}

	/**
	 * Checks whether this instance has the style {@link LabelStyle#FORM}.
	 *
	 * @return TRUE if this instance is a form label
	 */
	public final boolean isFormLabel() {
		return getWidget() instanceof GwtFormLabel;
	}

	/**
	 * Indicates that this label refers to another component in a form-like
	 * panel. This is only supported for labels with the
	 * {@link LabelStyle#FORM}
	 * style.
	 *
	 * @param component widget The target widget for this label
	 */
	public void setAsLabelFor(Component component) {
		Widget labelWidget = getWidget();

		if (labelWidget instanceof GwtFormLabel) {
			((GwtFormLabel) labelWidget).setAsLabelFor(component.getWidget());
		}
	}

	/**
	 * @see ImageAttribute#setImage(Image)
	 */
	@Override
	public void setImage(Image image) {
		this.image = image;

		Widget widget = getWidget();

		if (widget instanceof ImageAttribute) {
			((ImageAttribute) widget).setImage(image);
		} else {
			setLabelContent(text);
		}
	}

	/**
	 * @see TextAttribute#setText(String)
	 */
	@Override
	public void setText(String newText) {
		text = newText != null ? getContext().expandResource(newText) : null;

		if (text != null && text.startsWith(URL_TEXT_PREFIX)) {
			EWT.requestUrlContent(text.substring(URL_TEXT_PREFIX.length()),
				(t, u) -> setLabelContent(EWT.convertToInnerHtml(t, u)),
				this::handleUrlAccessError);
		} else {
			setLabelContent(text);
		}
	}

	/**
	 * Logs and displays an error upon querying the label text from a URL.
	 *
	 * @param e The error exception
	 */
	private void handleUrlAccessError(Throwable e) {
		GWT.log("Error reading " + text, e);
		text = getContext().expandResource("$msgUrlTextNotAvailable");
		setLabelContent(text);
	}

	/**
	 * Sets the label html.
	 *
	 * @param labelText The text to set the content from (may be NULL for
	 *                  image-only labels)
	 */
	private void setLabelContent(String labelText) {
		Widget widget = getWidget();
		String label = labelText != null ? labelText : "";
		boolean imageLabel = image instanceof ImageRef;

		if (imageLabel) {
			widget.addStyleName(EWT.CSS.ewtImageLabel());

			label = createImageLabel(labelText, (ImageRef) image, textPosition,
				HasHorizontalAlignment.ALIGN_CENTER, "100%");
		}

		if (containsHtml && widget instanceof HasHTML) {
			((HasHTML) widget).setHTML(label);
		} else if (imageLabel) {
			widget.getElement().setInnerHTML(label);
		} else if (widget instanceof HasText) {
			((HasText) widget).setText(label);
		}
	}

	/**
	 * Widget factory for this component.
	 *
	 * @author eso
	 */
	public static class LabelWidgetFactory<W extends Widget & HasText>
		implements WidgetFactory<W> {

		/**
		 * Create the widget for a certain label style.
		 *
		 * @param component  The component to create the widget for
		 * @param labelStyle The label style
		 * @param style      The style data
		 * @return The new label widget
		 */
		public Widget createLabelWidget(Component component,
			LabelStyle labelStyle, StyleData style) {
			Widget widget = null;

			switch (labelStyle) {
				case DEFAULT:
				case IMAGE:
				case BRAND:
					widget = new HTML("", style.hasFlag(StyleFlag.WRAP));
					break;

				case INLINE:
					widget = new InlineHTML();
					break;

				case FORM:
					widget = new GwtFormLabel();
					break;

				case TITLE:
					widget = new GwtLegendLabel();
					break;

				case ICON:
					widget = new GwtIconLabel();
					break;
			}

			return widget;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		@SuppressWarnings("unchecked")
		public W createWidget(Component component, StyleData style) {
			Widget widget = null;

			if (style.hasFlag(StyleFlag.HYPERLINK)) {
				widget = new Hyperlink();
			} else {
				LabelStyle labelStyle =
					style.getProperty(UserInterfaceProperties.LABEL_STYLE,
						LabelStyle.DEFAULT);

				widget = createLabelWidget(component, labelStyle, style);
			}

			return (W) widget;
		}
	}

	/**
	 * A GWT widget implementation that wraps a certain DOM text element.
	 *
	 * @author eso
	 */
	static abstract class LabelWidget<E extends Element> extends Widget
		implements HasText, HasHTML {

		private final E element;

		/**
		 * Creates a new instance.
		 *
		 * @param element The element for this label widget
		 */
		LabelWidget(E element) {
			this.element = element;
			setElement(element);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getHTML() {
			return element.getInnerHTML();
		}

		/**
		 * Returns the label element wrapped by this instance.
		 *
		 * @return The label element
		 */
		public final E getLabelElement() {
			return element;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getText() {
			return element.getInnerText();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void setHTML(String html) {
			element.setInnerHTML(html);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void setText(String text) {
			element.setInnerText(text);
		}
	}

	/**
	 * A GWT widget implementation that wraps a label DOM element.
	 *
	 * @author eso
	 */
	static class GwtFormLabel extends LabelWidget<LabelElement> {

		/**
		 * Creates a new instance.
		 */
		GwtFormLabel() {
			super(Document.get().createLabelElement());
		}

		/**
		 * Associates this label with another widget by referencing it's ID in
		 * the HTML 'for' attribute.
		 *
		 * @param widget The target widget for this label
		 */
		public void setAsLabelFor(Widget widget) {
			String id = widget.getElement().getId();

			if (id == null || id.isEmpty()) {
				id = DOM.createUniqueId();
				widget.getElement().setId(id);
			}

			getLabelElement().setHtmlFor(id);
		}
	}

	/**
	 * A GWT widget implementation that wraps a label DOM element.
	 *
	 * @author eso
	 */
	static class GwtIconLabel extends LabelWidget<Element> {

		/**
		 * Creates a new instance.
		 */
		GwtIconLabel() {
			super(Document.get().createElement("i"));
		}

		/**
		 * Overridden to set the style name instead.
		 *
		 * @see LabelWidget#setText(String)
		 */
		@Override
		public void setHTML(String text) {
			String[] baseStyles = text.split(" ");
			StringBuilder style = new StringBuilder("fa");

			for (String baseStyle : baseStyles) {
				style.append(" fa-").append(baseStyle);
			}

			setStyleName(style.toString());
		}
	}

	/**
	 * A GWT widget implementation that wraps a label DOM element.
	 *
	 * @author eso
	 */
	static class GwtLegendLabel extends LabelWidget<LegendElement> {

		/**
		 * Creates a new instance.
		 */
		GwtLegendLabel() {
			super(Document.get().createLegendElement());
		}
	}
}
