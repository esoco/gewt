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
package de.esoco.ewt.impl.gwt;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Widget;


/********************************************************************
 * Implementation of several GWT widget animations.
 *
 * @author eso
 */
public class WidgetAnimation extends Animation
{
	/********************************************************************
	 * Enumeration of the available animation types.
	 */
	public enum AnimationType
	{
		FADE_IN
		{
			@Override
			void animate(Element rElement, double fProgress)
			{
				rElement.getStyle().setOpacity(fProgress);
			}
		},
		FADE_OUT
		{
			@Override
			void animate(Element rElement, double fProgress)
			{
				rElement.getStyle().setOpacity(1.0d - fProgress);
			}
		},
		VERTICAL_SHRINK
		{
			@Override
			void animate(Element rElement, double fProgress)
			{
				double fHeight = rElement.getOffsetHeight();

				rElement.getStyle()
						.setHeight(fHeight * (1.0d - fProgress), Unit.PX);
			}
		},
		VERTICAL_GROW
		{
			@Override
			void animate(Element rElement, double fProgress)
			{
				double fHeight = rElement.getOffsetHeight();

				rElement.getStyle().setHeight(fHeight * fProgress, Unit.PX);
			}
		},
		HORIZONTAL_SHRINK
		{
			@Override
			void animate(Element rElement, double fProgress)
			{
				double fWidth = rElement.getOffsetWidth();

				rElement.getStyle()
						.setHeight(fWidth * (1.0d - fProgress), Unit.PX);
			}
		},
		HORIZONTAL_GROW
		{
			@Override
			void animate(Element rElement, double fProgress)
			{
				double fWidth = rElement.getOffsetWidth();

				rElement.getStyle().setHeight(fWidth * fProgress, Unit.PX);
			}
		};

		/***************************************
		 * Animates a certain element with this animation type.
		 *
		 * @param rElement  The element to animate
		 * @param fProgress The current animation progress
		 */
		abstract void animate(Element rElement, double fProgress);
	}

	private final Widget				  rWidget;
	private WidgetAnimation.AnimationType eCurrentType;

	/***************************************
	 * Creates a new instance for a certain element.
	 *
	 * @param rWidget rElement The element
	 */
	public WidgetAnimation(Widget rWidget)
	{
		this.rWidget = rWidget;
	}

	/***************************************
	 * Returns the widget of this animation.
	 *
	 * @return The widget
	 */
	public final Widget getWidget()
	{
		return rWidget;
	}

	/***************************************
	 * Performs a certain animation for the element of this instance.
	 *
	 * @param eType     The animation type
	 * @param nDuration The duration of the animation in milliseconds
	 */
	public void perform(WidgetAnimation.AnimationType eType, int nDuration)
	{
		this.eCurrentType = eType;

		cancel();
		run(nDuration);
	}

	/***************************************
	 * @see Animation#onUpdate(double)
	 */
	@Override
	protected void onUpdate(double fProgress)
	{
		eCurrentType.animate(rWidget.getElement(), fProgress);
	}
}
