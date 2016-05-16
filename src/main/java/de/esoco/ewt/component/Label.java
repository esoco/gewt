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
import de.esoco.ewt.graphics.Image;
import de.esoco.ewt.impl.gwt.WidgetFactory;
import de.esoco.ewt.property.ImageAttribute;
import de.esoco.ewt.style.AlignedPosition;
import de.esoco.ewt.style.StyleData;
import de.esoco.ewt.style.StyleFlag;
import de.esoco.lib.property.LabelStyle;
import de.esoco.lib.property.TextAttribute;
import de.esoco.lib.property.UserInterfaceProperties;

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


/********************************************************************
 * A GWT-specific EWT label implementation.
 *
 * @author eso
 */
public class Label extends Component implements TextAttribute, ImageAttribute
{
	//~ Instance fields --------------------------------------------------------

	private String		    sText;
	private Image		    rImage;
	private AlignedPosition rTextPosition;

	//~ Methods ----------------------------------------------------------------

	/***************************************
	 * @see Control#applyStyle(StyleData)
	 */
	@Override
	public void applyStyle(StyleData rStyle)
	{
		super.applyStyle(rStyle);

		rTextPosition = getTextPosition(rStyle);
	}

	/***************************************
	 * @see ImageAttribute#getImage()
	 */
	@Override
	public Image getImage()
	{
		return rImage;
	}

	/***************************************
	 * @see TextAttribute#getText()
	 */
	@Override
	public String getText()
	{
		return sText;
	}

	/***************************************
	 * Checks whether this instance has the style {@link LabelStyle#FORM}.
	 *
	 * @return TRUE if this instance is a form label
	 */
	public final boolean isFormLabel()
	{
		return getWidget() instanceof GwtFormLabel;
	}

	/***************************************
	 * Indicates that this label refers to another component in a form-like
	 * panel. This is only supported for labels with the {@link LabelStyle#FORM}
	 * style.
	 *
	 * @param rComponent rWidget The target widget for this label
	 */
	public void setAsLabelFor(Component rComponent)
	{
		Widget rLabelWidget = getWidget();

		if (rLabelWidget instanceof GwtFormLabel)
		{
			((GwtFormLabel) rLabelWidget).setAsLabelFor(rComponent.getWidget());
		}
	}

	/***************************************
	 * @see ImageAttribute#setImage(Image)
	 */
	@Override
	public void setImage(Image rImage)
	{
		this.rImage = rImage;

		setLabelContent();
	}

	/***************************************
	 * @see TextAttribute#setText(String)
	 */
	@Override
	public void setText(String sText)
	{
		this.sText = sText != null ? getContext().expandResource(sText) : null;

		setLabelContent();
	}

	/***************************************
	 * Sets the label html.
	 */
	private void setLabelContent()
	{
		Widget rWidget = getWidget();

		if (rWidget instanceof HasHTML)
		{
			HasHTML rHtml = (HasHTML) rWidget;

			if (rImage != null)
			{
				rWidget.addStyleName(EWT.CSS.ewtImageLabel());
				rHtml.setHTML(createImageLabel(sText,
											   rImage,
											   rTextPosition,
											   HasHorizontalAlignment.ALIGN_CENTER,
											   "100%"));
			}
			else
			{
				rHtml.setHTML(sText != null ? sText : "");
			}
		}
		else
		{
			((HasText) rWidget).setText(sText);
		}
	}

	//~ Inner Classes ----------------------------------------------------------

	/********************************************************************
	 * Widget factory for this component.
	 *
	 * @author eso
	 */
	public static class LabelWidgetFactory<W extends Widget & HasText>
		implements WidgetFactory<W>
	{
		//~ Methods ------------------------------------------------------------

		/***************************************
		 * Create the widget for a certain label style.
		 *
		 * @param  rComponent  The component to create the widget for
		 * @param  eLabelStyle The label style
		 * @param  rStyle      The style data
		 *
		 * @return The new label widget
		 */
		public Widget createLabelWidget(Component  rComponent,
										LabelStyle eLabelStyle,
										StyleData  rStyle)
		{
			Widget aWidget = null;

			switch (eLabelStyle)
			{
				case DEFAULT:
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

		/***************************************
		 * {@inheritDoc}
		 */
		@Override
		@SuppressWarnings("unchecked")
		public W createWidget(Component rComponent, StyleData rStyle)
		{
			Widget aWidget = null;

			if (rStyle.hasFlag(StyleFlag.HYPERLINK))
			{
				aWidget = new Hyperlink();
			}
			else
			{
				LabelStyle eLabelStyle =
					rStyle.getProperty(UserInterfaceProperties.LABEL_STYLE,
									   LabelStyle.DEFAULT);

				aWidget = createLabelWidget(rComponent, eLabelStyle, rStyle);
			}

			return (W) aWidget;
		}
	}

	/********************************************************************
	 * A GWT widget implementation that wraps a certain DOM text element.
	 *
	 * @author eso
	 */
	static abstract class LabelWidget<E extends Element> extends Widget
		implements HasText, HasHTML
	{
		//~ Instance fields ----------------------------------------------------

		private final E rElement;

		//~ Constructors -------------------------------------------------------

		/***************************************
		 * Creates a new instance.
		 *
		 * @param rElement The element for this label widget
		 */
		LabelWidget(E rElement)
		{
			this.rElement = rElement;
			setElement(rElement);
		}

		//~ Methods ------------------------------------------------------------

		/***************************************
		 * {@inheritDoc}
		 */
		@Override
		public String getHTML()
		{
			return rElement.getInnerHTML();
		}

		/***************************************
		 * Returns the label element wrapped by this instance.
		 *
		 * @return The label element
		 */
		public final E getLabelElement()
		{
			return rElement;
		}

		/***************************************
		 * {@inheritDoc}
		 */
		@Override
		public String getText()
		{
			return rElement.getInnerText();
		}

		/***************************************
		 * {@inheritDoc}
		 */
		@Override
		public void setHTML(String sHtml)
		{
			rElement.setInnerHTML(sHtml);
		}

		/***************************************
		 * {@inheritDoc}
		 */
		@Override
		public void setText(String sText)
		{
			setStyleName(sText);
		}
	}

	/********************************************************************
	 * A GWT widget implementation that wraps a label DOM element.
	 *
	 * @author eso
	 */
	static class GwtFormLabel extends LabelWidget<LabelElement>
	{
		//~ Constructors -------------------------------------------------------

		/***************************************
		 * Creates a new instance.
		 */
		GwtFormLabel()
		{
			super(Document.get().createLabelElement());
		}

		//~ Methods ------------------------------------------------------------

		/***************************************
		 * Associates this label with another widget by referencing it's ID in
		 * the HTML 'for' attribute.
		 *
		 * @param rWidget The target widget for this label
		 */
		public void setAsLabelFor(Widget rWidget)
		{
			String sId = rWidget.getElement().getId();

			if (sId == null || sId.isEmpty())
			{
				sId = DOM.createUniqueId();
				rWidget.getElement().setId(sId);
			}

			getLabelElement().setHtmlFor(sId);
		}
	}

	/********************************************************************
	 * A GWT widget implementation that wraps a label DOM element.
	 *
	 * @author eso
	 */
	static class GwtIconLabel extends LabelWidget<Element>
	{
		//~ Constructors -------------------------------------------------------

		/***************************************
		 * Creates a new instance.
		 */
		GwtIconLabel()
		{
			super(Document.get().createElement("i"));
		}

		//~ Methods ------------------------------------------------------------

		/***************************************
		 * Overridden to set the style name instead.
		 *
		 * @see LabelWidget#setText(String)
		 */
		@Override
		public void setHTML(String sText)
		{
			String[]	  aBaseStyles = sText.split(" ");
			StringBuilder aStyle	  = new StringBuilder("fa");

			for (String sBaseStyle : aBaseStyles)
			{
				aStyle.append(" fa-").append(sBaseStyle);
			}

			setStyleName(aStyle.toString());
		}
	}

	/********************************************************************
	 * A GWT widget implementation that wraps a label DOM element.
	 *
	 * @author eso
	 */
	static class GwtLegendLabel extends LabelWidget<LegendElement>
	{
		//~ Constructors -------------------------------------------------------

		/***************************************
		 * Creates a new instance.
		 */
		GwtLegendLabel()
		{
			super(Document.get().createLegendElement());
		}
	}
}
