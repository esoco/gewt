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

/**
 * Implementation of several GWT widget animations.
 *
 * @author eso
 */
public class WidgetAnimation extends Animation {
	/**
	 * Enumeration of the available animation types.
	 */
	public enum AnimationType {
		FADE_IN {
			@Override
			void animate(Element element, double progress) {
				element.getStyle().setOpacity(progress);
			}
		}, FADE_OUT {
			@Override
			void animate(Element element, double progress) {
				element.getStyle().setOpacity(1.0d - progress);
			}
		}, VERTICAL_SHRINK {
			@Override
			void animate(Element element, double progress) {
				double height = element.getOffsetHeight();

				element
					.getStyle()
					.setHeight(height * (1.0d - progress), Unit.PX);
			}
		}, VERTICAL_GROW {
			@Override
			void animate(Element element, double progress) {
				double height = element.getOffsetHeight();

				element.getStyle().setHeight(height * progress, Unit.PX);
			}
		}, HORIZONTAL_SHRINK {
			@Override
			void animate(Element element, double progress) {
				double width = element.getOffsetWidth();

				element
					.getStyle()
					.setHeight(width * (1.0d - progress), Unit.PX);
			}
		}, HORIZONTAL_GROW {
			@Override
			void animate(Element element, double progress) {
				double width = element.getOffsetWidth();

				element.getStyle().setHeight(width * progress, Unit.PX);
			}
		};

		/**
		 * Animates a certain element with this animation type.
		 *
		 * @param element  The element to animate
		 * @param progress The current animation progress
		 */
		abstract void animate(Element element, double progress);
	}

	private final Widget widget;

	private WidgetAnimation.AnimationType currentType;

	/**
	 * Creates a new instance for a certain element.
	 *
	 * @param widget rElement The element
	 */
	public WidgetAnimation(Widget widget) {
		this.widget = widget;
	}

	/**
	 * Returns the widget of this animation.
	 *
	 * @return The widget
	 */
	public final Widget getWidget() {
		return widget;
	}

	/**
	 * Performs a certain animation for the element of this instance.
	 *
	 * @param type     The animation type
	 * @param duration The duration of the animation in milliseconds
	 */
	public void perform(WidgetAnimation.AnimationType type, int duration) {
		this.currentType = type;

		cancel();
		run(duration);
	}

	/**
	 * @see Animation#onUpdate(double)
	 */
	@Override
	protected void onUpdate(double progress) {
		currentType.animate(widget.getElement(), progress);
	}
}
