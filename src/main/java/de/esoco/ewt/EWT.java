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

import de.esoco.lib.text.TextConvert;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.regexp.shared.SplitResult;
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
	 * Converts an HTML string so that it can be used as the inner HTML of a
	 * widget. If the input is a complete HTML page this method then first
	 * extracts the content of the body tag. Then it converts all relative URL
	 * references with absolute URLs of the given base URL.
	 *
	 * @param  sHtml    The HTML to convert
	 * @param  sBaseUrl The base URL for the conversion of relative URLs
	 *
	 * @return The converted HTML string
	 */
	public static String convertToInnerHtml(String sHtml, String sBaseUrl)
	{
		MatchResult aResult =
			RegExp.compile("<body[^>]*>[\\s\\S]*<\\/body>").exec(sHtml);

		if (aResult != null && aResult.getGroupCount() > 0)
		{
			// use only body content if the request returns a full HTML page
			sHtml = aResult.getGroup(0);
		}

		SplitResult aSrcRefs  = RegExp.compile("src=\"(.*?)\"").split(sHtml);
		int		    nRefCount = aSrcRefs.length();

		if (nRefCount > 0)
		{
			StringBuilder aExpandedText = new StringBuilder(aSrcRefs.get(0));

			for (int i = 1; i < nRefCount; i += 2)
			{
				String sUrl = aSrcRefs.get(i);

				if (!sUrl.startsWith("http"))
				{
					sUrl = sBaseUrl + sUrl;
				}

				aExpandedText.append("src=\"");
				aExpandedText.append(sUrl);
				aExpandedText.append('"');
				aExpandedText.append(aSrcRefs.get(i + 1));
			}

			sHtml = aExpandedText.toString();
		}

		return sHtml;
	}

	/***************************************
	 * Places a text string in the system clipboard.
	 *
	 * @param sText The text string
	 */
	public static native void copyTextToClipboard(String sText) /*-{
		var textArea = document.createElement("textarea");

		textArea.style.opacity = 0;
		textArea.value = sText;

		document.body.appendChild(textArea);

		textArea.select();

		try {
			var successful = document.execCommand('copy');
		} catch (err) {
			console.log('Copy failed: ' + err);
		}
		document.body.removeChild(textArea);
	}-*/;

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
	 * @param  rComponentClass The component class for which to return the
	 *                         factory for
	 *
	 * @return The widget factory or NULL if no factory has been registered
	 *
	 * @see    #registerWidgetFactory(Class, WidgetFactory, boolean)
	 */
	public static WidgetFactory<?> getWidgetFactory(
		Class<? extends Component> rComponentClass)
	{
		if (aWidgetFactories.isEmpty())
		{
			registerDefaultWidgetFactories(true);
		}

		return aWidgetFactories.get(rComponentClass);
	}

	/***************************************
	 * An enhanced GWT logging method that allows to use a (simple) format
	 * string and message arguments. Based on {@link GWT#log(String)} and {@link
	 * TextConvert#format(String, java.util.Collection)}.
	 *
	 * @param sFormat The message format string
	 * @param rArgs   The message arguments
	 */
	public static void log(String sFormat, Object... rArgs)
	{
		GWT.log(TextConvert.format(sFormat, rArgs));
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
		logTime(sInfo, sName, nStart, 0);
	}

	/***************************************
	 * Measures and logs the time of a profiled execution if it exceeds a
	 * certain threshold.
	 *
	 * @param sInfo      An info string describing the profiling
	 * @param sName      The name of the profiled context
	 * @param nStart     The starting time of the measuring in milliseconds
	 * @param nThreshold The threshold in milliseconds
	 */
	public static void logTime(String sInfo,
							   String sName,
							   long   nStart,
							   int    nThreshold)
	{
		long t = System.currentTimeMillis() - nStart;

		if (t > nThreshold)
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
	 * A helper method that measures the execution time of a {@link Runnable}
	 * object.
	 *
	 * @param sDescription  The description of the measured code
	 * @param rProfiledCode The code to measure the execution time of
	 */
	public static void measure(String sDescription, Runnable rProfiledCode)
	{
		long t = System.currentTimeMillis();

		rProfiledCode.run();

		t = System.currentTimeMillis() - t;
		EWT.log("[TIME] %s: %ss",
				sDescription,
				t / 1000 + "." + t % 1000 / 100 + t % 100 / 10 + t % 10);
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
	 * Executes an HTTP request that retrieves the content of the argument URL.
	 * If the URL is relative (i.e. doesn't start with 'http') it will be
	 * retrieved relative to the GWT base path for static resources.
	 *
	 * @param sUrl            The URL to retrieve
	 * @param fProcessContent a binary function that receives the retrieved URL
	 *                        content and the base URL of the content as it's
	 *                        argument
	 * @param fHandleError    A function that handles errors
	 */
	public static void requestUrlContent(
		String					   sUrl,
		BiConsumer<String, String> fProcessContent,
		Consumer<Throwable>		   fHandleError)
	{
		if (!sUrl.startsWith("http"))
		{
			sUrl = GWT.getModuleBaseForStaticFiles() + sUrl;
		}

		String sBaseUrl = sUrl.substring(0, sUrl.lastIndexOf('/') + 1);

		RequestBuilder aRequestBuilder =
			new RequestBuilder(RequestBuilder.GET, sUrl);

		aRequestBuilder.setCallback(new RequestCallback()
			{
				@Override
				public void onResponseReceived(
					Request  rRequest,
					Response rResponse)
				{
					fProcessContent.accept(rResponse.getText(), sBaseUrl);
				}

				@Override
				public void onError(Request rRequest, Throwable e)
				{
					fHandleError.accept(e);
				}
			});

		try
		{
			aRequestBuilder.send();
		}
		catch (RequestException e)
		{
			fHandleError.accept(e);
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
