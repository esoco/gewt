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
package de.esoco.ewt.event;

/**
 * This is the event handling interface for GEWT. I only consists of one method
 * that will be invoked for all events that the handler instance has been
 * registered for on components and other event-generating objects (like menu
 * items).
 *
 * @author eso
 */
@FunctionalInterface
public interface EwtEventHandler {

	/**
	 * This method will be invoked to handle events.
	 *
	 * @param rEvent The event that occurred
	 */
	public void handleEvent(EwtEvent rEvent);
}
