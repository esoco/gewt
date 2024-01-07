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
package de.esoco.ewt.layout;

import de.esoco.ewt.component.Container;
import de.esoco.ewt.style.StyleData;

import de.esoco.lib.property.StandardProperties;

import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.HasWidgets;

/**
 * A layout that groups components with an optional title/caption. The title
 * must be set in the container's style data with the name
 * {@link StandardProperties#TITLE}.
 *
 * @author eso
 */
public class GroupLayout extends TwoLayerLayout {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected HasWidgets createLayoutWidget(Container container,
		StyleData containerStyle) {
		CaptionPanel captionPanel = new CaptionPanel();

		String title = containerStyle.getProperty(StandardProperties.TITLE,
			"");

		if (title.length() > 0) {
			captionPanel.setCaptionText(
				container.getContext().expandResource(title));
		}

		return captionPanel;
	}
}
