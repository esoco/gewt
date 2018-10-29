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
package de.esoco.ewt.app;

import de.esoco.ewt.EWT;
import de.esoco.ewt.UserInterfaceContext;
import de.esoco.ewt.component.View;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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

		EWTModule rModule = getApplicationModule();

		GwtResource aResource =
			new GwtResource(getStringResources(), getImageResources());

		UserInterfaceContext rContext =
			EWT.createUserInterfaceContext(aResource);

		View rModuleView = rModule.createModuleView(rContext);

		rModule.showModuleView(rContext, rModuleView);
	}

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
	 * Replaced with {@link #getImageResources()}.
	 *
	 * @return     The image map
	 *
	 * @deprecated
	 */
	@Deprecated
	protected Map<String, ImageResource> getApplicationImages()
	{
		return new HashMap<>();
	}

	/***************************************
	 * Replaced with {@link #getStringResources()}.
	 *
	 * @return     The image map
	 *
	 * @deprecated
	 */
	@Deprecated
	protected ConstantsWithLookup[] getApplicationStrings()
	{
		return new ConstantsWithLookup[0];
	}

	/***************************************
	 * Returns a mapping from resource identifiers to image resources. The
	 * default implementation returns an empty map that can be modified by
	 * subclasses.
	 *
	 * @return The image resources for this application
	 */
	protected Map<String, ImageResource> getImageResources()
	{
		return getApplicationImages();
	}

	/***************************************
	 * Returns a list of {@link ConstantsWithLookup} instances with the
	 * application's resource string. The default implementation returns an
	 * empty list that can be modified by subclasses.
	 *
	 * @return The resource strings for this application
	 */
	protected List<ConstantsWithLookup> getStringResources()
	{
		return new ArrayList<>(Arrays.asList(getApplicationStrings()));
	}
}
