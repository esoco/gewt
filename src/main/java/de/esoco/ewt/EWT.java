//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// This file is a part of the 'gewt' project.
// Copyright 2017 Elmar Sonnenschein, esoco GmbH, Flensburg, Germany
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
import de.esoco.ewt.component.Button;
import de.esoco.ewt.component.Button.ButtonWidgetFactory;
import de.esoco.ewt.component.Calendar;
import de.esoco.ewt.component.Calendar.CalendarWidgetFactory;
import de.esoco.ewt.component.CheckBox;
import de.esoco.ewt.component.CheckBox.CheckBoxWidgetFactory;
import de.esoco.ewt.component.ChildView.ChildViewFactory;
import de.esoco.ewt.component.ComboBox;
import de.esoco.ewt.component.ComboBox.ComboBoxWidgetFactory;
import de.esoco.ewt.component.Component;
import de.esoco.ewt.component.DateField;
import de.esoco.ewt.component.DateField.DateFieldWidgetFactory;
import de.esoco.ewt.component.FileChooser;
import de.esoco.ewt.component.FileChooser.FileChooserWidgetFactory;
import de.esoco.ewt.component.Label;
import de.esoco.ewt.component.Label.LabelWidgetFactory;
import de.esoco.ewt.component.List;
import de.esoco.ewt.component.ListBox;
import de.esoco.ewt.component.ListControl.ListControlWidgetFactory;
import de.esoco.ewt.component.ProgressBar;
import de.esoco.ewt.component.ProgressBar.ProgressBarWidgetFactory;
import de.esoco.ewt.component.RadioButton;
import de.esoco.ewt.component.RadioButton.RadioButtonWidgetFactory;
import de.esoco.ewt.component.Spinner;
import de.esoco.ewt.component.Spinner.SpinnerWidgetFactory;
import de.esoco.ewt.component.Table;
import de.esoco.ewt.component.TableControl.TableControlWidgetFactory;
import de.esoco.ewt.component.TextArea;
import de.esoco.ewt.component.TextArea.TextAreaWidgetFactory;
import de.esoco.ewt.component.TextField;
import de.esoco.ewt.component.TextField.TextFieldWidgetFactory;
import de.esoco.ewt.component.ToggleButton;
import de.esoco.ewt.component.ToggleButton.ToggleButtonWidgetFactory;
import de.esoco.ewt.component.Tree;
import de.esoco.ewt.component.Tree.TreeWidgetFactory;
import de.esoco.ewt.component.TreeTable;
import de.esoco.ewt.component.Website;
import de.esoco.ewt.component.Website.WebsiteWidgetFactory;
import de.esoco.ewt.impl.gwt.GewtCss;
import de.esoco.ewt.impl.gwt.GewtResources;
import de.esoco.ewt.impl.gwt.WidgetFactory;
import de.esoco.ewt.layout.DefaultLayoutFactory;
import de.esoco.ewt.layout.LayoutFactory;
import de.esoco.ewt.layout.LayoutMapper;
import de.esoco.ewt.layout.LayoutMapper.IdentityLayoutMapper;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Frame;
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

	private static ChildViewFactory aChildViewFactory = new ChildViewFactory();

	private static LayoutFactory aLayoutFactory = new DefaultLayoutFactory();
	private static LayoutMapper  aLayoutMapper  = new IdentityLayoutMapper();

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
	 * Returns the factory for child view widgets.
	 *
	 * @return The child view factory
	 */
	public static ChildViewFactory getChildViewFactory()
	{
		return aChildViewFactory;
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
	 * Returns the current layout factory.
	 *
	 * @return The layout factory
	 */
	public static final LayoutFactory getLayoutFactory()
	{
		return aLayoutFactory;
	}

	/***************************************
	 * Returns the current layout mapper.
	 *
	 * @return The layout mapper
	 */
	public static final LayoutMapper getLayoutMapper()
	{
		return aLayoutMapper;
	}

	/***************************************
	 * Returns the widget factory for a certain component or layout instance.
	 *
	 * @param  rComponent The object for which to return the factory for
	 *
	 * @return The widget factory or NULL if no factory has been registered
	 *
	 * @see    #registerWidgetFactory(Class, WidgetFactory, boolean)
	 */
	public static WidgetFactory<?> getWidgetFactory(Component rComponent)
	{
		if (aWidgetFactories.isEmpty())
		{
			registerDefaultWidgetFactories(true);
		}

		return aWidgetFactories.get(rComponent.getClass());
	}

	/***************************************
	 * A simplified GWT debug logging method with a variable list of message
	 * parameters. Simplified because the only formatting placeholder that is
	 * considered in the template is '%s' and all parameters will be converted
	 * by means of their toString() method. There's also no error handling
	 * regarding the message format and parameter count as this method is only
	 * intended for debugging purposes.
	 *
	 * @param sTemplate The message template string
	 * @param rParams   The message parameters
	 */
	public static void log(String sTemplate, Object... rParams)
	{
		String[]	  aLiterals = sTemplate.split("%s", -1);
		StringBuilder aMessage  = new StringBuilder(aLiterals[0]);

		for (int i = 1; i < aLiterals.length; i++)
		{
			aMessage.append(rParams[i - 1]);
			aMessage.append(aLiterals[i]);
		}

		GWT.log(aMessage.toString());
	}

	/***************************************
	 * Measures and logs the time of a profiled execution.
	 *
	 * @param sInfo  An info string describing the profiling
	 * @param sName  The name of the profiled context
	 * @param nStart The starting time of the measuring in milliseconds
	 */
	public static void logTime(String sInfo, String sName, long nStart)
	{
		long t = System.currentTimeMillis() - nStart;

		if (t > 50)
		{
			GWT.log(t / 1000 + "." + t % 1000 / 100 + t % 100 / 10 + t % 10 +
					": " + sInfo + " " + sName);
		}
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
	 * Registers all default GEWT widget factories.
	 *
	 * @param bReplaceExisting TRUE to replace existing mappings with the
	 *                         default factory, FALSE to keep current factories
	 */
	public static void registerDefaultWidgetFactories(boolean bReplaceExisting)
	{
		registerWidgetFactory(Button.class,
							  new ButtonWidgetFactory<>(),
							  bReplaceExisting);
		registerWidgetFactory(Calendar.class,
							  new CalendarWidgetFactory(),
							  bReplaceExisting);
		registerWidgetFactory(CheckBox.class,
							  new CheckBoxWidgetFactory<>(),
							  bReplaceExisting);
		registerWidgetFactory(ComboBox.class,
							  new ComboBoxWidgetFactory(),
							  bReplaceExisting);
		registerWidgetFactory(DateField.class,
							  new DateFieldWidgetFactory(),
							  bReplaceExisting);
		registerWidgetFactory(FileChooser.class,
							  new FileChooserWidgetFactory(),
							  bReplaceExisting);
		registerWidgetFactory(Label.class,
							  new LabelWidgetFactory<>(),
							  bReplaceExisting);
		registerWidgetFactory(List.class,
							  new ListControlWidgetFactory(),
							  bReplaceExisting);
		registerWidgetFactory(ListBox.class,
							  new ListControlWidgetFactory(),
							  bReplaceExisting);
		registerWidgetFactory(ProgressBar.class,
							  new ProgressBarWidgetFactory(),
							  bReplaceExisting);
		registerWidgetFactory(RadioButton.class,
							  new RadioButtonWidgetFactory(),
							  bReplaceExisting);
		registerWidgetFactory(Spinner.class,
							  new SpinnerWidgetFactory(),
							  bReplaceExisting);
		registerWidgetFactory(Table.class,
							  new TableControlWidgetFactory(false),
							  bReplaceExisting);
		registerWidgetFactory(TextArea.class,
							  new TextAreaWidgetFactory<>(),
							  bReplaceExisting);
		registerWidgetFactory(TextField.class,
							  new TextFieldWidgetFactory<>(),
							  bReplaceExisting);
		registerWidgetFactory(ToggleButton.class,
							  new ToggleButtonWidgetFactory<>(),
							  bReplaceExisting);
		registerWidgetFactory(Tree.class,
							  new TreeWidgetFactory(),
							  bReplaceExisting);
		registerWidgetFactory(TreeTable.class,
							  new TableControlWidgetFactory(true),
							  bReplaceExisting);
		registerWidgetFactory(Website.class,
							  new WebsiteWidgetFactory(),
							  bReplaceExisting);
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

	/***************************************
	 * Sets the child view factory to be used by the GEWT framework. This will
	 * override the default factory.
	 *
	 * @param rFactory The new child view factory
	 */
	public static void setChildViewFactory(ChildViewFactory rFactory)
	{
		aChildViewFactory = rFactory;
	}

	/***************************************
	 * Sets a new layout factory. EWT extensions can set a factory to create
	 * their own layouts instead of the defaults.
	 *
	 * @param rFactory The new layout factory or NULL to reset to the default
	 */
	public static void setLayoutFactory(LayoutFactory rFactory)
	{
		if (rFactory != null)
		{
			aLayoutFactory = rFactory;
		}
		else
		{
			aLayoutFactory = new DefaultLayoutFactory();
		}
	}

	/***************************************
	 * Sets a new layout mapper. EWT extensions can use such a mapper to replace
	 * default layouts with their own instances.
	 *
	 * @param rMapper The new layout mapper or NULL to reset to the default
	 */
	public static void setLayoutMapper(LayoutMapper rMapper)
	{
		if (rMapper != null)
		{
			aLayoutMapper = rMapper;
		}
		else
		{
			aLayoutMapper = new IdentityLayoutMapper();
		}
	}
}
