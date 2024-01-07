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

/**
 * The main entry point into EWT. Contains only static methods that allow to
 * acquire a user interface context or to manipulate global settings.
 *
 * @author eso
 */
public class EWT {

	/**
	 * The GEWT CSS (for package-internal use)
	 */
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

	private static Map<String, String> cssClassMap = null;

	private static ChildViewFactory childViewFactory = new ChildViewFactory();

	private static LayoutFactory layoutFactory = new DefaultLayoutFactory();

	private static LayoutMapper layoutMapper = new IdentityLayoutMapper();

	private static Map<Class<? extends Component>, WidgetFactory<?>>
		widgetFactories = new HashMap<>();

	/**
	 * Private - only static method access.
	 */
	private EWT() {
	}

	/**
	 * Adds a mapping for a CSS class name. Applications can then use their own
	 * CSS class which will then automatically be translated into the
	 * respective
	 * target CSS class.
	 *
	 * @param applicationCssClass The CSS class used by the application
	 * @param targetCssClass      The CSS class to map the application class to
	 */
	public static void addCssClassMapping(String applicationCssClass,
		String targetCssClass) {
		if (cssClassMap == null) {
			cssClassMap = new HashMap<>();
		}

		cssClassMap.put(applicationCssClass, targetCssClass);
	}

	/**
	 * Adds several CSS class mappings.
	 *
	 * @param mappings The mappings to add
	 * @see #addCssClassMapping(String, String)
	 */
	public static void addCssClassMappings(Map<String, String> mappings) {
		for (Entry<String, String> mapping : mappings.entrySet()) {
			addCssClassMapping(mapping.getKey(), mapping.getValue());
		}
	}

	/**
	 * Converts an HTML string so that it can be used as the inner HTML of a
	 * widget. If the input is a complete HTML page this method then first
	 * extracts the content of the body tag. Then it converts all relative URL
	 * references with absolute URLs of the given base URL.
	 *
	 * @param html    The HTML to convert
	 * @param baseUrl The base URL for the conversion of relative URLs
	 * @return The converted HTML string
	 */
	public static String convertToInnerHtml(String html, String baseUrl) {
		MatchResult result =
			RegExp.compile("<body[^>]*>[\\s\\S]*<\\/body>").exec(html);

		if (result != null && result.getGroupCount() > 0) {
			// use only body content if the request returns a full HTML page
			html = result.getGroup(0);
		}

		SplitResult srcRefs = RegExp.compile("src=\"(.*?)\"").split(html);
		int refCount = srcRefs.length();

		if (refCount > 0) {
			StringBuilder expandedText = new StringBuilder(srcRefs.get(0));

			for (int i = 1; i < refCount; i += 2) {
				String url = srcRefs.get(i);

				if (!url.startsWith("http")) {
					url = baseUrl + url;
				}

				expandedText.append("src=\"");
				expandedText.append(url);
				expandedText.append('"');
				expandedText.append(srcRefs.get(i + 1));
			}

			html = expandedText.toString();
		}

		return html;
	}

	/**
	 * Places a text string in the system clipboard. This requires that the
	 * input focus is moved to a hidden component. As there is no reliable way
	 * to find the previously focused element it is recommended that invoking
	 * code resets the focus to the previously active input component after
	 * this
	 * method returns.
	 *
	 * @param text The text string
	 */
	public static native void copyTextToClipboard(String text) /*-{
		var textArea = document.createElement("textarea");

		textArea.style.opacity = 0;
		textArea.value = text;

		document.body.appendChild(textArea);

		textArea.select();

		try {
			var successful = document.execCommand('copy');
		} catch (err) {
			console.log('Copy failed: ' + err);
		}
		document.body.removeChild(textArea);
	}-*/;

	/**
	 * Creates a new user interface context.
	 *
	 * @param resource The context resource or NULL for none
	 * @return A new user interface context instance
	 */
	public static UserInterfaceContext createUserInterfaceContext(
		Resource resource) {
		return new UserInterfaceContext(resource);
	}

	/**
	 * Expands a resource for a particular user interface component. Checks
	 * if a
	 * string starts with the character '$' and therefore represents a resource
	 * key. If so, the corresponding resource value will be returned after
	 * performing a lookup from the application resource. If the string
	 * argument
	 * is not prefixed and therefore not a resource key it will be returned
	 * unchanged.
	 *
	 * @param component The Component to perform the resource lookup for
	 * @param resource  The string to check and (if necessary) expand
	 * @return The resource string (NULL if no resource entry could be found)
	 */
	public static String expandResource(Component component, String resource) {
		return component.getContext().expandResource(resource);
	}

	/**
	 * Returns the factory for child view widgets.
	 *
	 * @return The child view factory
	 */
	public static ChildViewFactory getChildViewFactory() {
		return childViewFactory;
	}

	/**
	 * Returns the default interval for the detection of double clicks.
	 *
	 * @return The default double click interval
	 */
	public static int getDoubleClickInterval() {
		return DOUBLE_CLICK_INTERVAL;
	}

	/**
	 * Returns the current layout factory.
	 *
	 * @return The layout factory
	 */
	public static final LayoutFactory getLayoutFactory() {
		return layoutFactory;
	}

	/**
	 * Returns the current layout mapper.
	 *
	 * @return The layout mapper
	 */
	public static final LayoutMapper getLayoutMapper() {
		return layoutMapper;
	}

	/**
	 * Returns the widget factory for a certain component or layout instance.
	 *
	 * @param componentClass The component class for which to return the
	 *                          factory
	 *                       for
	 * @return The widget factory or NULL if no factory has been registered
	 * @see #registerWidgetFactory(Class, WidgetFactory, boolean)
	 */
	public static WidgetFactory<?> getWidgetFactory(
		Class<? extends Component> componentClass) {
		if (widgetFactories.isEmpty()) {
			registerDefaultWidgetFactories(true);
		}

		return widgetFactories.get(componentClass);
	}

	/**
	 * An enhanced GWT logging method that allows to use a (simple) format
	 * string and message arguments. Based on {@link GWT#log(String)} and
	 * {@link TextConvert#format(String, java.util.Collection)}.
	 *
	 * @param format The message format string
	 * @param args   The message arguments
	 */
	public static void log(String format, Object... args) {
		GWT.log(TextConvert.format(format, args));
	}

	/**
	 * Measures and logs the time of a profiled execution.
	 *
	 * @param info  An info string describing the profiling
	 * @param name  The name of the profiled context
	 * @param start The starting time of the measuring in milliseconds
	 */
	public static void logTime(String info, String name, long start) {
		logTime(info, name, start, 0);
	}

	/**
	 * Measures and logs the time of a profiled execution if it exceeds a
	 * certain threshold.
	 *
	 * @param info      An info string describing the profiling
	 * @param name      The name of the profiled context
	 * @param start     The starting time of the measuring in milliseconds
	 * @param threshold The threshold in milliseconds
	 */
	public static void logTime(String info, String name, long start,
		int threshold) {
		long t = System.currentTimeMillis() - start;

		if (t > threshold) {
			GWT.log(
				t / 1000 + "." + t % 1000 / 100 + t % 100 / 10 + t % 10 + ":" +
					" " + info + " " + name);
		}
	}

	/**
	 * Maps a certain CSS class name if a corresponding mapping has been
	 * registered through {@link #addCssClassMapping(String, String)}. If no
	 * mapping exists the input name will be returned unchanged.
	 *
	 * @param cssClass The CSS class name to map
	 * @return The mapped CSS class name or the input name if no mapping exists
	 */
	public static String mapCssClass(String cssClass) {
		if (cssClassMap != null) {
			String mappedCssClass = cssClassMap.get(cssClass);

			if (mappedCssClass != null) {
				cssClass = mappedCssClass;
			}
		}

		return cssClass;
	}

	/**
	 * A helper method that measures the execution time of a {@link Runnable}
	 * object.
	 *
	 * @param description  The description of the measured code
	 * @param profiledCode The code to measure the execution time of
	 */
	public static void measure(String description, Runnable profiledCode) {
		long t = System.currentTimeMillis();

		profiledCode.run();

		t = System.currentTimeMillis() - t;
		EWT.log("[TIME] %s: %ss", description,
			t / 1000 + "." + t % 1000 / 100 + t % 100 / 10 + t % 10);
	}

	/**
	 * Opens a URL that is relative to the current web application in an
	 * invisible frame. This can be used to initiate downloads.
	 *
	 * @param relativeUrl A URL that is relative to the current module's base
	 *                    URL
	 */
	public static void openHiddenUrl(String relativeUrl) {
		final RootPanel rootPanel = RootPanel.get(HIDDEN_URL_FRAME);

		if (rootPanel != null) {
			Frame frame = new Frame(GWT.getModuleBaseURL() + relativeUrl);

			frame.setVisible(false);
			frame.setSize("0px", "0px");
			rootPanel.clear();
			rootPanel.add(frame);
		}
	}

	/**
	 * Opens a URL in a new browser window. If the URL is not starting with
	 * 'http' (i.e. is not absolute) the current module base URL will be
	 * prepended. The possible values for name and features are described <a
	 * href="https://developer.mozilla.org/en-US/docs/Web/API/window.open">
	 * here</a>.
	 *
	 * @param url      The URL to open
	 * @param name     The name of the new window or NULL for none
	 * @param features The desired Features of the new windows or NULL for none
	 */
	public static void openUrl(String url, String name, String features) {
		if (!url.startsWith("http")) {
			url = GWT.getModuleBaseURL() + url;
		}

		Window.open(url, name != null ? name : "",
			features != null ? features : "");
	}

	/**
	 * Registers all default GEWT widget factories.
	 *
	 * @param replaceExisting TRUE to replace existing mappings with the
	 *                           default
	 *                        factory, FALSE to keep current factories
	 */
	public static void registerDefaultWidgetFactories(boolean replaceExisting) {
		registerWidgetFactory(Button.class, new ButtonWidgetFactory<>(),
			replaceExisting);
		registerWidgetFactory(Calendar.class, new CalendarWidgetFactory(),
			replaceExisting);
		registerWidgetFactory(CheckBox.class, new CheckBoxWidgetFactory<>(),
			replaceExisting);
		registerWidgetFactory(ComboBox.class, new ComboBoxWidgetFactory(),
			replaceExisting);
		registerWidgetFactory(DateField.class, new DateFieldWidgetFactory(),
			replaceExisting);
		registerWidgetFactory(FileChooser.class,
			new FileChooserWidgetFactory(),
			replaceExisting);
		registerWidgetFactory(Label.class, new LabelWidgetFactory<>(),
			replaceExisting);
		registerWidgetFactory(List.class, new ListControlWidgetFactory(),
			replaceExisting);
		registerWidgetFactory(ListBox.class, new ListControlWidgetFactory(),
			replaceExisting);
		registerWidgetFactory(ProgressBar.class,
			new ProgressBarWidgetFactory(),
			replaceExisting);
		registerWidgetFactory(RadioButton.class,
			new RadioButtonWidgetFactory(),
			replaceExisting);
		registerWidgetFactory(Spinner.class, new SpinnerWidgetFactory(),
			replaceExisting);
		registerWidgetFactory(Table.class,
			new TableControlWidgetFactory(false),
			replaceExisting);
		registerWidgetFactory(TextArea.class, new TextAreaWidgetFactory<>(),
			replaceExisting);
		registerWidgetFactory(TextField.class, new TextFieldWidgetFactory<>(),
			replaceExisting);
		registerWidgetFactory(ToggleButton.class,
			new ToggleButtonWidgetFactory<>(), replaceExisting);
		registerWidgetFactory(Tree.class, new TreeWidgetFactory(),
			replaceExisting);
		registerWidgetFactory(TreeTable.class,
			new TableControlWidgetFactory(true), replaceExisting);
		registerWidgetFactory(Website.class, new WebsiteWidgetFactory(),
			replaceExisting);
	}

	/**
	 * Registers a widget factory for a certain component type.
	 *
	 * @param componentClass  The type of component to register the factory for
	 * @param factory         The widget factory
	 * @param replaceExisting TRUE to replace an existing mapping with the
	 *                           given
	 *                        factory, FALSE to keep the current factory
	 * @see #getWidgetFactory(Class)
	 */
	public static void registerWidgetFactory(
		Class<? extends Component> componentClass, WidgetFactory<?> factory,
		boolean replaceExisting) {
		if (replaceExisting || !widgetFactories.containsKey(componentClass)) {
			widgetFactories.put(componentClass, factory);
		}
	}

	/**
	 * Executes an HTTP request that retrieves the content of the argument URL.
	 * If the URL is relative (i.e. doesn't start with 'http') it will be
	 * retrieved relative to the GWT base path for static resources.
	 *
	 * @param url            The URL to retrieve
	 * @param processContent a binary function that receives the retrieved URL
	 *                       content and the base URL of the content as it's
	 *                       argument
	 * @param handleError    A function that handles errors
	 */
	public static void requestUrlContent(String url,
		BiConsumer<String, String> processContent,
		Consumer<Throwable> handleError) {
		if (!url.startsWith("http")) {
			url = GWT.getModuleBaseForStaticFiles() + url;
		}

		String baseUrl = url.substring(0, url.lastIndexOf('/') + 1);

		RequestBuilder requestBuilder =
			new RequestBuilder(RequestBuilder.GET, url);

		requestBuilder.setCallback(new RequestCallback() {
			@Override
			public void onError(Request request, Throwable e) {
				handleError.accept(e);
			}

			@Override
			public void onResponseReceived(Request request,
				Response response) {
				processContent.accept(response.getText(), baseUrl);
			}
		});

		try {
			requestBuilder.send();
		} catch (RequestException e) {
			handleError.accept(e);
		}
	}

	/**
	 * Sets the child view factory to be used by the GEWT framework. This will
	 * override the default factory.
	 *
	 * @param factory The new child view factory
	 */
	public static void setChildViewFactory(ChildViewFactory factory) {
		childViewFactory = factory;
	}

	/**
	 * Sets a new layout factory. EWT extensions can set a factory to create
	 * their own layouts instead of the defaults.
	 *
	 * @param factory The new layout factory or NULL to reset to the default
	 */
	public static void setLayoutFactory(LayoutFactory factory) {
		if (factory != null) {
			layoutFactory = factory;
		} else {
			layoutFactory = new DefaultLayoutFactory();
		}
	}

	/**
	 * Sets a new layout mapper. EWT extensions can use such a mapper to
	 * replace
	 * default layouts with their own instances.
	 *
	 * @param mapper The new layout mapper or NULL to reset to the default
	 */
	public static void setLayoutMapper(LayoutMapper mapper) {
		if (mapper != null) {
			layoutMapper = mapper;
		} else {
			layoutMapper = new IdentityLayoutMapper();
		}
	}
}
