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
package de.esoco.ewt;

import de.esoco.ewt.app.Resource;
import de.esoco.ewt.component.Component;
import de.esoco.ewt.impl.gwt.GewtCss;
import de.esoco.ewt.impl.gwt.GewtResources;
import de.esoco.ewt.impl.gwt.WidgetFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

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

	private static Map<String, String> aCssClassMap = null;

	private static Map<Class<? extends Component>, WidgetFactory<?>> aWidgetFactories =
		new HashMap<>();

	//~ Constructors -----------------------------------------------------------

	/***************************************
	 * Private - only static method access.
	 */
	private EWT()
	{
	}

	//~ Static methods ---------------------------------------------------------

	/***************************************
	 * Adds a mapping for a CSS class name. Applications can then use their own
	 * CSS class which will then automatically be translated into the respective
	 * target CSS class.
	 *
	 * @param sApplicationCssClass The CSS class used by the application
	 * @param sTargetCssClass      The CSS class to map the application class to
	 */
	public static void addCssClassMapping(
		String sApplicationCssClass,
		String sTargetCssClass)
	{
		if (aCssClassMap == null)
		{
			aCssClassMap = new HashMap<>();
		}

		aCssClassMap.put(sApplicationCssClass, sTargetCssClass);
	}

	/***************************************
	 * Adds several CSS class mappings.
	 *
	 * @param rMappings The mappings to add
	 *
	 * @see   #addCssClassMapping(String, String)
	 */
	public static void addCssClassMappings(Map<String, String> rMappings)
	{
		for (Entry<String, String> rMapping : rMappings.entrySet())
		{
			addCssClassMapping(rMapping.getKey(), rMapping.getValue());
		}
	}

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
	 * Returns the widget factory for a certain component or layout instance.
	 *
	 * @param  rComponent The object for which to return the factory for
	 *
	 * @return The widget factory or NULL if no factory has been registered
	 *
	 * @see    #registerWidgetFactory(Class, WidgetFactory)
	 */
	public static WidgetFactory<?> getWidgetFactory(Component rComponent)
	{
		return aWidgetFactories.get(rComponent.getClass());
	}

	/***************************************
	 * Maps a certain CSS class name if a corresponding mapping has been
	 * registered through {@link #addCssClassMapping(String, String)}. If no
	 * mapping exists the input name will be returned unchanged.
	 *
	 * @param  sCssClass The CSS class name to map
	 *
	 * @return The mapped CSS class name or the input name if no mapping exists
	 */
	public static String mapCssClass(String sCssClass)
	{
		if (aCssClassMap != null)
		{
			String sMappedCssClass = aCssClassMap.get(sCssClass);

			if (sMappedCssClass != null)
			{
				sCssClass = sMappedCssClass;
			}
		}

		return sCssClass;
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

	/***************************************
	 * Registers a widget factory for a certain component type.
	 *
	 * @param rComponentClass  The type of component to register the factory for
	 * @param rFactory         The widget factory
	 * @param bReplaceExisting TRUE to replace an existing mapping with the
	 *                         given factory, FALSE to keep the current factory
	 *
	 * @see   #getWidgetFactory(Component)
	 */
	public static void registerWidgetFactory(
		Class<? extends Component> rComponentClass,
		WidgetFactory<?>		   rFactory,
		boolean					   bReplaceExisting)
	{
		if (bReplaceExisting || !aWidgetFactories.containsKey(rComponentClass))
		{
			aWidgetFactories.put(rComponentClass, rFactory);
		}
	}
}
