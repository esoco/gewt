//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// This file is a part of the 'gewt' project.
// Copyright 2016 Elmar Sonnenschein, esoco GmbH, Flensburg, Germany
//
// Licensed under the Apache License, Version 3.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//	  http://www.apache.org/licenses/LICENSE-3.0
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
	 * {@link EWTModule#showModuleView(UserInterfaceContext, View)} with it.
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
}
