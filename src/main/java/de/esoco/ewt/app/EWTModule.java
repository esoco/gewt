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

import de.esoco.ewt.UserInterfaceContext;
import de.esoco.ewt.component.View;

/**
 * An EWT module is the part of an EWT application that contains the user
 * interface code. The intention behind this is that the module code should be
 * as independent from a particular EWT platform as possible. This makes it
 * easier to port application code between different EWT implementations. Any
 * platform-specific application code should be put in a subclass of the
 * respective application class which is different for several EWT variants. For
 * GEWT, the base class for applications is {@link EWTEntryPoint}.
 *
 * <p>Implementing an EWT application typically means to providing at least two
 * classes: an application class containing the bootstrap and platform-specific
 * code and a module that contains the platform-independent user interface
 * code.
 * </p>
 *
 * @author eso
 */
public interface EWTModule {

	/**
	 * This method must be implemented by subclasses to create and return the
	 * module's main view. If required by the platform this method will be
	 * invoked on the user interface thread so the implementation doesn't need
	 * to care about thread synchronization.
	 *
	 * <p>The implementation should neither invoke {@link View#pack()} nor make
	 * the view visible already. Packing and displaying the view should only be
	 * done in the method {@link #showModuleView(UserInterfaceContext, View)}
	 * which will be invoked by the framework after the creation. This
	 * separation allows EWT implementations to apply any platform-specific
	 * attributes to the main application view if necessary.</p>
	 *
	 * <p>This method should not set a menu bar on the view because menus are
	 * handled differently</p>
	 *
	 * <p>If the module view cannot be created for some reason this method
	 * should throw a RuntimeException. It must not return NULL.</p>
	 *
	 * @param context The user interface context to create the view in
	 * @return The module's main view
	 */
	public View createModuleView(UserInterfaceContext context);

	/**
	 * This method will be invoked by the EWT framework to display a view
	 * previously created by {@link #createModuleView(UserInterfaceContext)}.
	 * Typically it should prepare the view by invoking {@link View#pack()} and
	 * then display it on the screen, e.g. with one of the display methods in
	 * the {@link UserInterfaceContext}.
	 *
	 * @param context The user interface context of the view
	 * @param view    The view to display
	 */
	public void showModuleView(UserInterfaceContext context, View view);
}
