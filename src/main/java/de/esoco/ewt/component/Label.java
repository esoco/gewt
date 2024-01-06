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

	private String sText;

	private Image rImage;

	private AlignedPosition rTextPosition;

	private boolean bContainsHtml;

	/**
	 * @see Control#applyStyle(StyleData)
	 */
	@Override
	public void applyStyle(StyleData rStyle) {
		super.applyStyle(rStyle);

		rTextPosition = getTextPosition(rStyle);
		bContainsHtml =
			rStyle.getProperty(CONTENT_TYPE, null) == ContentType.HTML;

		if (bContainsHtml) {
			addStyleName("contains-html");
		}
	}

	/**
	 * @see ImageAttribute#getImage()
	 */
	@Override
	public Image getImage() {
		return rImage;
	}

	/**
	 * @see TextAttribute#getText()
	 */
	@Override
	public String getText() {
		return sText;
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
	 * @param rComponent rWidget The target widget for this label
	 */
	public void setAsLabelFor(Component rComponent) {
		Widget rLabelWidget = getWidget();

		if (rLabelWidget instanceof GwtFormLabel) {
			((GwtFormLabel) rLabelWidget).setAsLabelFor(rComponent.getWidget());
		}
	}

	/**
	 * @see ImageAttribute#setImage(Image)
	 */
	@Override
	public void setImage(Image rImage) {
		this.rImage = rImage;

		Widget rWidget = getWidget();

		if (rWidget instanceof ImageAttribute) {
			((ImageAttribute) rWidget).setImage(rImage);
		} else {
			setLabelContent(sText);
		}
	}

	/**
	 * @see TextAttribute#setText(String)
	 */
	@Override
	public void setText(String sNewText) {
		sText = sNewText != null ? getContext().expandResource(sNewText) :
		        null;

		if (sText != null && sText.startsWith(URL_TEXT_PREFIX)) {
			EWT.requestUrlContent(sText.substring(URL_TEXT_PREFIX.length()),
				(t, u) -> setLabelContent(EWT.convertToInnerHtml(t, u)),
				this::handleUrlAccessError);
		} else {
			setLabelContent(sText);
		}
	}

	/**
	 * Logs and displays an error upon querying the label text from a URL.
	 *
	 * @param e The error exception
	 */
	private void handleUrlAccessError(Throwable e) {
		GWT.log("Error reading " + sText, e);
		sText = getContext().expandResource("$msgUrlTextNotAvailable");
		setLabelContent(sText);
	}

	/**
	 * Sets the label html.
	 *
	 * @param sLabelText The text to set the content from (may be NULL for
	 *                   image-only labels)
	 */
	private void setLabelContent(String sLabelText) {
		Widget rWidget = getWidget();
		String sLabel = sLabelText != null ? sLabelText : "";
		boolean bImageLabel = rImage instanceof ImageRef;

		if (bImageLabel) {
			rWidget.addStyleName(EWT.CSS.ewtImageLabel());

			sLabel =
				createImageLabel(sLabelText, (ImageRef) rImage, rTextPosition,
					HasHorizontalAlignment.ALIGN_CENTER, "100%");
		}

		if (bContainsHtml && rWidget instanceof HasHTML) {
			((HasHTML) rWidget).setHTML(sLabel);
		} else if (bImageLabel) {
			rWidget.getElement().setInnerHTML(sLabel);
		} else if (rWidget instanceof HasText) {
			((HasText) rWidget).setText(sLabel);
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
		 * @param rComponent  The component to create the widget for
		 * @param eLabelStyle The label style
		 * @param rStyle      The style data
		 * @return The new label widget
		 */
		public Widget createLabelWidget(Component rComponent,
			LabelStyle eLabelStyle, StyleData rStyle) {
			Widget aWidget = null;

			switch (eLabelStyle) {
				case DEFAULT:
				case IMAGE:
				case BRAND:
					aWidget = new HTML("", rStyle.hasFlag(StyleFlag.WRAP));
					break;

				case INLINE:
					aWidget = new InlineHTML();
					break;

				case FORM:
					aWidget = new GwtFormLabel();
					break;

				case TITLE:
					aWidget = new GwtLegendLabel();
					break;

				case ICON:
					aWidget = new GwtIconLabel();
					break;
			}

			return aWidget;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		@SuppressWarnings("unchecked")
		public W createWidget(Component rComponent, StyleData rStyle) {
			Widget aWidget = null;

			if (rStyle.hasFlag(StyleFlag.HYPERLINK)) {
				aWidget = new Hyperlink();
			} else {
				LabelStyle eLabelStyle =
					rStyle.getProperty(UserInterfaceProperties.LABEL_STYLE,
						LabelStyle.DEFAULT);

				aWidget = createLabelWidget(rComponent, eLabelStyle, rStyle);
			}

			return (W) aWidget;
		}
	}

	/**
	 * A GWT widget implementation that wraps a certain DOM text element.
	 *
	 * @author eso
	 */
	static abstract class LabelWidget<E extends Element> extends Widget
		implements HasText, HasHTML {

		private final E rElement;

		/**
		 * Creates a new instance.
		 *
		 * @param rElement The element for this label widget
		 */
		LabelWidget(E rElement) {
			this.rElement = rElement;
			setElement(rElement);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getHTML() {
			return rElement.getInnerHTML();
		}

		/**
		 * Returns the label element wrapped by this instance.
		 *
		 * @return The label element
		 */
		public final E getLabelElement() {
			return rElement;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getText() {
			return rElement.getInnerText();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void setHTML(String sHtml) {
			rElement.setInnerHTML(sHtml);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void setText(String sText) {
			rElement.setInnerText(sText);
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
		 * @param rWidget The target widget for this label
		 */
		public void setAsLabelFor(Widget rWidget) {
			String sId = rWidget.getElement().getId();

			if (sId == null || sId.isEmpty()) {
				sId = DOM.createUniqueId();
				rWidget.getElement().setId(sId);
			}

			getLabelElement().setHtmlFor(sId);
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
		public void setHTML(String sText) {
			String[] aBaseStyles = sText.split(" ");
			StringBuilder aStyle = new StringBuilder("fa");

			for (String sBaseStyle : aBaseStyles) {
				aStyle.append(" fa-").append(sBaseStyle);
			}

			setStyleName(aStyle.toString());
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
