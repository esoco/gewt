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
package de.esoco.ewt.impl.gwt;

/**
 * An interface that can be implemented by widgets that need a delay before
 * events are handled. This can be useful to let animations finish before event
 * handling performs UI updates.
 *
 * @author eso
 */
public interface HasEventHandlingDelay {

	/**
	 * Returns the time in milliseconds by which the event handling for a
	 * widget
	 * should be delayed. This can be used by widgets that have some UI
	 * animation that should be finished before the event handling starts.
	 *
	 * @return The event handling delay in milliseconds
	 */
	public int getEventHandlingDelay();
}
