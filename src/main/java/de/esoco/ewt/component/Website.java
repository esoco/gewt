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
import de.esoco.ewt.impl.gwt.WidgetFactory;
import de.esoco.ewt.style.StyleData;

import de.esoco.lib.property.TextAttribute;

import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.Widget;


/********************************************************************
 * A component that displays a website with a certain URL. The URL of the
 * component can be queried in text format through the implemented methods of
 * the {@link TextAttribute} interface.
 *
 * @author eso
 */
public class Website extends Component implements TextAttribute
{
	//~ Static fields/initializers ---------------------------------------------

	static
	{
		EWT.registerWidgetFactory(Website.class,
										   new WebsiteWidgetFactory(),
										   false);
	}

	//~ Methods ----------------------------------------------------------------

	/***************************************
	 * Returns the URL that is display by this component.
	 *
	 * @return The URL
	 */
	@Override
	public String getText()
	{
		return ((Frame) getWidget()).getUrl();
	}

	/***************************************
	 * Sets the URL that is display by this component.
	 *
	 * @param sUrl The URL
	 */
	@Override
	public void setText(String sUrl)
	{
		((Frame) getWidget()).setUrl(sUrl);
	}

	//~ Inner Classes ----------------------------------------------------------

	/********************************************************************
	 * Widget factory for this component.
	 *
	 * @author eso
	 */
	public static class WebsiteWidgetFactory implements WidgetFactory<Widget>
	{
		//~ Methods ------------------------------------------------------------

		/***************************************
		 * {@inheritDoc}
		 */
		@Override
		public Widget createWidget(
			UserInterfaceContext rContext,
			StyleData			 rStyle)
		{
			return new Frame();
		}
	}
}
