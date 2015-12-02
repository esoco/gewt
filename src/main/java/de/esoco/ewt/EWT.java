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
package de.esoco.ewt;

import de.esoco.ewt.app.Resource;
import de.esoco.ewt.component.Component;
import de.esoco.ewt.impl.gwt.GewtCss;
import de.esoco.ewt.impl.gwt.GewtResources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;


/********************************************************************
 * The main entry point into EWT. Contains only static methods that allow to
 * acquire a user interface context or to manipulate global settings.
 *
 * @author eso
 */
public class EWT
{
	//~ Static fields/initializers ---------------------------------------------

	/** The GEWT CSS (for package-internal use) */
	public static final GewtCss CSS = GewtResources.INSTANCE.css();

	/**
	 * The name of the download frame in the root HTML of the current
	 * application.
	 */
	public static final String HIDDEN_URL_FRAME = "__gewt_downloadFrame";

	/**
	 * A global constant that defines whether the GEWT layout is based on the
	 * GWT {@link LayoutPanel} or the classic table-based GWT panels.
	 */
	public static final boolean USE_LAYOUT_PANELS = true;

	private static final int DOUBLE_CLICK_INTERVAL = 500;

	//~ Constructors -----------------------------------------------------------

	/***************************************
	 * Private - only static method access.
	 */
	private EWT()
	{
	}

	//~ Static methods ---------------------------------------------------------

	/***************************************
	 * Creates a new user interface context.
	 *
	 * @param  rResource The context resource or NULL for none
	 *
	 * @return A new user interface context instance
	 */
	public static UserInterfaceContext createUserInterfaceContext(
		Resource rResource)
	{
		return new UserInterfaceContext(rResource);
	}

	/***************************************
	 * Expands a resource for a particular user interface component. Checks if a
	 * string starts with the character '$' and therefore represents a resource
	 * key. If so, the corresponding resource value will be returned after
	 * performing a lookup from the application resource. If the string argument
	 * is not prefixed and therefore not a resource key it will be returned
	 * unchanged.
	 *
	 * @param  rComponent The Component to perform the resource lookup for
	 * @param  sResource  The string to check and (if necessary) expand
	 *
	 * @return The resource string (NULL if no resource entry could be found)
	 */
	public static String expandResource(Component rComponent, String sResource)
	{
		return rComponent.getContext().expandResource(sResource);
	}

	/***************************************
	 * Returns the default interval for the detection of double clicks.
	 *
	 * @return The default double click interval
	 */
	public static int getDoubleClickInterval()
	{
		return DOUBLE_CLICK_INTERVAL;
	}

	/***************************************
	 * Opens a URL that is relative to the current web application in an
	 * invisible frame. This can be used to initiate downloads.
	 *
	 * @param sRelativeUrl A URL that is relative to the current module's base
	 *                     URL
	 */
	public static void openHiddenUrl(String sRelativeUrl)
	{
		final RootPanel rRootPanel = RootPanel.get(HIDDEN_URL_FRAME);

		if (rRootPanel != null)
		{
			Frame aFrame = new Frame(GWT.getModuleBaseURL() + sRelativeUrl);

			aFrame.setVisible(false);
			aFrame.setSize("0px", "0px");
			rRootPanel.clear();
			rRootPanel.add(aFrame);
		}
	}

	/***************************************
	 * Opens a URL in a new browser window. If the URL is not starting with
	 * 'http' (i.e. is not absolute) the current module base URL will be
	 * prepended. The possible values for name and features are described <a
	 * href="https://developer.mozilla.org/en-US/docs/Web/API/window.open">
	 * here</a>.
	 *
	 * @param sUrl      The URL to open
	 * @param sName     The name of the new window or NULL for none
	 * @param sFeatures The desired Features of the new windows or NULL for none
	 */
	public static void openUrl(String sUrl, String sName, String sFeatures)
	{
		if (!sUrl.startsWith("http"))
		{
			sUrl = GWT.getModuleBaseURL() + sUrl;
		}

		Window.open(sUrl,
					sName != null ? sName : "",
					sFeatures != null ? sFeatures : "");
	}
}
