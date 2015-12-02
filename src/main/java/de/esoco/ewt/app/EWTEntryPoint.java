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
package de.esoco.ewt.app;

import de.esoco.ewt.EWT;
import de.esoco.ewt.UserInterfaceContext;
import de.esoco.ewt.component.View;

import java.util.Map;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.GWT.UncaughtExceptionHandler;
import com.google.gwt.event.shared.UmbrellaException;
import com.google.gwt.i18n.client.ConstantsWithLookup;
import com.google.gwt.resources.client.ImageResource;


/********************************************************************
 * The base class for GEWT applications.
 *
 * @author eso
 */
public abstract class EWTEntryPoint implements EntryPoint
{
	//~ Methods ----------------------------------------------------------------

	/***************************************
	 * Creates a new user interface context for GWT and invokes the method
	 * {@link EWTModule#showModuleView(UserInterfaceContext)} with it.
	 */
	@Override
	public void onModuleLoad()
	{
		EWT.CSS.ensureInjected();
//		GWT.setUncaughtExceptionHandler(new SuperDevModeUncaughtExceptionHandler());

		EWTModule rModule = getApplicationModule();

		GwtResource aResource =
			new GwtResource(getApplicationStrings(), getApplicationImages());

		UserInterfaceContext rContext =
			EWT.createUserInterfaceContext(aResource);

		View rModuleView = rModule.createModuleView(rContext);

		rModule.showModuleView(rContext, rModuleView);
	}

	/***************************************
	 * Must be implemented to return a mapping from resource identifiers to
	 * image resources defining the application images. Subclasses must also
	 * take into account any superclass implementation and add the superclass
	 * images to the returned map unless it wants to overriden the parent
	 * completely.
	 *
	 * @return The image resources for this application or NULL for none
	 */
	protected abstract Map<String, ImageResource> getApplicationImages();

	/***************************************
	 * Must be implemented by subclasses to return the application module that
	 * creates and manages the user interface of this GEWT entry point. This
	 * method will only be invoked once. Therefore the implementation may create
	 * the module instance on the fly if it doesn't need to keep a reference to
	 * it.
	 *
	 * @return The application module
	 */
	protected abstract EWTModule getApplicationModule();

	/***************************************
	 * Must be implemented to return an arrays that contains instance of the
	 * {@link ConstantsWithLookup} interface that contains the application's
	 * resource string. This method will only be invoked once. Therefore the
	 * implementation may create the resource on request and doesn't need to
	 * keep a reference to it.
	 *
	 * @return The resource strings for this application or NULL for none
	 */
	protected abstract ConstantsWithLookup[] getApplicationStrings();

	//~ Inner Classes ----------------------------------------------------------

	/********************************************************************
	 * An implementation of {@link GWT#uncaughtExceptionHandler} that prints
	 * readable client stack traces.
	 *
	 * <p>{@link https://gist.github.com/jnehlmeier/cddbc476fd330b1d4999}</p>
	 *
	 * @author eso
	 */
	public static class SuperDevModeUncaughtExceptionHandler
		implements UncaughtExceptionHandler
	{
		//~ Methods ------------------------------------------------------------

		/***************************************
		 * {@inheritDoc}
		 */
		@Override
		public void onUncaughtException(final Throwable t)
		{
			logException(t, false);
		}

		/***************************************
		 * Ends a group.
		 */
		private native void groupEnd() /*-{
		var groupEnd = console.groupEnd || function(){};
		groupEnd.call(console);
		}-*/;

		/***************************************
		 * Starts a group.
		 *
		 * @param sMsg The group message
		 */
		private native void groupStart(String sMsg) /*-{
		var groupStart = console.groupCollapsed || console.group || console.error || console.log;
		groupStart.call(console, sMsg);
		}-*/;

		/***************************************
		 * Logs an exception
		 *
		 * @param t The exception
		 */
		private native void log(Throwable t) /*-{
		var logError = console.error || console.log;
		var backingError = t.__gwt$backingJsError;
		logError.call(console, backingError && backingError.stack);
		}-*/;

		/***************************************
		 * Logs an exception to the browser console.
		 *
		 * @param t        The exception
		 * @param bIsCause TRUE if the exception is part of the cause stack of
		 *                 the original exception
		 */
		private void logException(Throwable t, boolean bIsCause)
		{
			String msg = t.toString();

			if (bIsCause)
			{
				msg = "caused by: " + msg;
			}

			groupStart(msg);
			log(t);

			if (t instanceof UmbrellaException)
			{
				UmbrellaException umbrella = (UmbrellaException) t;

				for (Throwable cause : umbrella.getCauses())
				{
					logException(cause, true);
				}
			}
			else if (t.getCause() != null)
			{
				logException(t.getCause(), true);
			}

			groupEnd();
		}
	}
}
