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
import de.esoco.ewt.property.ImageAttribute;
import de.esoco.ewt.style.AlignedPosition;
import de.esoco.ewt.style.StyleData;
import de.esoco.ewt.style.StyleFlag;

import de.esoco.lib.property.TextAttribute;
import de.esoco.lib.property.UserInterfaceProperties;
import de.esoco.lib.property.UserInterfaceProperties.LabelStyle;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
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

	//~ Constructors -----------------------------------------------------------

	/***************************************
	 * Creates a new instance.
	 *
	 * @param rStyleData The label style
	 */
	public Label(StyleData rStyleData)
	{
		super(createWidget(rStyleData));
	}

	//~ Static methods ---------------------------------------------------------

	/***************************************
	 * Creates the widget for a new label.
	 *
	 * @param  rStyleData The label's style data
	 *
	 * @return The new widget
	 */
	private static Widget createWidget(StyleData rStyleData)
	{
		Widget aWidget = null;

		if (rStyleData.hasFlag(StyleFlag.HYPERLINK))
		{
			aWidget = new Hyperlink();
		}
		else
		{
			switch (rStyleData.getProperty(UserInterfaceProperties.LABEL_STYLE,
										   LabelStyle.DEFAULT))
			{
				case DEFAULT:
					aWidget = new HTML("", rStyleData.hasFlag(StyleFlag.WRAP));
					break;

				case INLINE:
					aWidget = new InlineHTML();
					break;

				case TITLE:
					aWidget =
						new LabelWidget(Document.get().createLegendElement());
					break;
			}
		}

		return aWidget;
	}

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
	 * @see ImageAttribute#setImage(Image)
	 */
	@Override
	public void setImage(Image rImage)
	{
		this.rImage = rImage;

		setLabelHtml();
	}

	/***************************************
	 * @see TextAttribute#setText(String)
	 */
	@Override
	public void setText(String sText)
	{
		this.sText = sText != null ? getContext().expandResource(sText) : null;

		setLabelHtml();
	}

	/***************************************
	 * Sets the label html.
	 */
	private void setLabelHtml()
	{
		HasHTML rHtml = (HasHTML) getWidget();

		if (rImage != null)
		{
			getWidget().addStyleName(EWT.CSS.ewtImageLabel());
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

	//~ Inner Classes ----------------------------------------------------------

	/********************************************************************
	 * A GWT widget implementation that wraps a a specific DOM text element like
	 * label or legend.
	 *
	 * @author eso
	 */
	static class LabelWidget extends Widget implements HasText, HasHTML
	{
		//~ Instance fields ----------------------------------------------------

		private final Element rElement;

		//~ Constructors -------------------------------------------------------

		/***************************************
		 * Creates a new instance.
		 *
		 * @param rElement The element for this label widget
		 */
		LabelWidget(Element rElement)
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
			rElement.setInnerText(sText);
		}
	}
}
