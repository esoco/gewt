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
package de.esoco.ewt.impl.gwt;

import de.esoco.ewt.event.EventType;

import com.google.gwt.dom.client.NativeEvent;


/********************************************************************
 * An internal interface to be used by GWT widget implementations to dispatch
 * GEWT events.
 *
 * @author eso
 */
public interface GewtEventDispatcher
{
	//~ Methods ----------------------------------------------------------------

	/***************************************
	 * Dispatches an event without a native event.
	 *
	 * @param rEventType The event type
	 */
	default public void dispatchEvent(EventType rEventType)
	{
		dispatchEvent(rEventType, null);
	}

	/***************************************
	 * Dispatches an event of a certain type to the registered listeners. If the
	 * native event argument is NULL because no native GWT event is available
	 * only a simple event notification will be dispatched.
	 *
	 * @param rEventType The event type
	 * @param rEvent     The native GWT event that occurred or NULL if not
	 *                   available
	 */
	public void dispatchEvent(EventType rEventType, NativeEvent rEvent);
}
