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
package de.esoco.ewt.impl.gwt;

import de.esoco.ewt.event.EwtEvent;
import de.esoco.ewt.event.EwtEventHandler;

/**
 * An event multicaster implementation that chains multiple event handlers and
 * propagates incoming events to all registered handlers. Works similar to the
 * AWT event multicaster. It is intended to be used internally by GEWT only.
 *
 * @author eso
 */
public class EventMulticaster implements EwtEventHandler {

	private EwtEventHandler first;

	private EwtEventHandler second;

	/**
	 * Creates a new instance that wraps two event listeners.
	 *
	 * @param first  The first listener of this multicaster
	 * @param second The second listener of this multicaster
	 */
	public EventMulticaster(EwtEventHandler first, EwtEventHandler second) {
		this.first = first;
		this.second = second;
	}

	/**
	 * A static method to concatenate two event listeners by means of event
	 * multicaster instances, thus building a tree of multicaster instances and
	 * listeners.
	 *
	 * @param first  The first listener to concatenate (may be NULL)
	 * @param second The second listener to concatenate (may be NULL)
	 * @return The resulting event listener which may be a multicaster instance
	 */
	public static EwtEventHandler add(EwtEventHandler first,
		EwtEventHandler second) {
		if (first == null) {
			return second;
		}

		if (second == null) {
			return first;
		}

		return new EventMulticaster(first, second);
	}

	/**
	 * Static method that removes an event listener from a tree of multicaster
	 * instances. If the given listener isn't part of the tree the call will
	 * have no effect.
	 *
	 * @param listener The listener (tree) to remove the other listener from
	 * @param toRemove The listener to remove (NULL will be ignored)
	 * @return The resulting event listener which may be a multicaster instance
	 */
	public static EwtEventHandler remove(EwtEventHandler listener,
		EwtEventHandler toRemove) {
		if (listener == toRemove || listener == null) {
			return null;
		} else if (listener instanceof EventMulticaster) {
			return ((EventMulticaster) listener).remove(toRemove);
		} else {
			return listener;
		}
	}

	/**
	 * @see EwtEventHandler#handleEvent(EwtEvent)
	 */
	@Override
	public void handleEvent(EwtEvent event) {
		first.handleEvent(event);
		second.handleEvent(event);
	}

	/**
	 * Recursively removes a certain event listener from this multicaster
	 * (sub)tree and returns the resulting event listener (which may be a
	 * multicaster).
	 *
	 * @param toRemove The listener to remove
	 * @return The resulting event listener which may be a multicaster instance
	 */
	protected EwtEventHandler remove(EwtEventHandler toRemove) {
		if (first == toRemove) {
			return second;
		}

		if (second == toRemove) {
			return first;
		}

		EwtEventHandler removeFirst = remove(first, toRemove);
		EwtEventHandler removeSecond = remove(second, toRemove);

		if (removeFirst != first || removeSecond != second) {
			return add(removeFirst, removeSecond);
		}

		return this;
	}
}
