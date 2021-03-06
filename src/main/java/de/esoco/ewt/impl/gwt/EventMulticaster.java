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


/********************************************************************
 * An event multicaster implementation that chains multiple event handlers and
 * propagates incoming events to all registered handlers. Works similar to the
 * AWT event multicaster. It is intended to be used internally by GEWT only.
 *
 * @author eso
 */
public class EventMulticaster implements EwtEventHandler
{
	//~ Instance fields --------------------------------------------------------

	private EwtEventHandler rFirst;
	private EwtEventHandler rSecond;

	//~ Constructors -----------------------------------------------------------

	/***************************************
	 * Creates a new instance that wraps two event listeners.
	 *
	 * @param rFirst  The first listener of this multicaster
	 * @param rSecond The second listener of this multicaster
	 */
	public EventMulticaster(EwtEventHandler rFirst, EwtEventHandler rSecond)
	{
		this.rFirst  = rFirst;
		this.rSecond = rSecond;
	}

	//~ Static methods ---------------------------------------------------------

	/***************************************
	 * A static method to concatenate two event listeners by means of event
	 * multicaster instances, thus building a tree of multicaster instances and
	 * listeners.
	 *
	 * @param  rFirst  The first listener to concatenate (may be NULL)
	 * @param  rSecond The second listener to concatenate (may be NULL)
	 *
	 * @return The resulting event listener which may be a multicaster instance
	 */
	public static EwtEventHandler add(
		EwtEventHandler rFirst,
		EwtEventHandler rSecond)
	{
		if (rFirst == null)
		{
			return rSecond;
		}

		if (rSecond == null)
		{
			return rFirst;
		}

		return new EventMulticaster(rFirst, rSecond);
	}

	/***************************************
	 * Static method that removes an event listener from a tree of multicaster
	 * instances. If the given listener isn't part of the tree the call will
	 * have no effect.
	 *
	 * @param  rListener The listener (tree) to remove the other listener from
	 * @param  rToRemove The listener to remove (NULL will be ignored)
	 *
	 * @return The resulting event listener which may be a multicaster instance
	 */
	public static EwtEventHandler remove(
		EwtEventHandler rListener,
		EwtEventHandler rToRemove)
	{
		if (rListener == rToRemove || rListener == null)
		{
			return null;
		}
		else if (rListener instanceof EventMulticaster)
		{
			return ((EventMulticaster) rListener).remove(rToRemove);
		}
		else
		{
			return rListener;
		}
	}

	//~ Methods ----------------------------------------------------------------

	/***************************************
	 * @see EwtEventHandler#handleEvent(EwtEvent)
	 */
	@Override
	public void handleEvent(EwtEvent rEvent)
	{
		rFirst.handleEvent(rEvent);
		rSecond.handleEvent(rEvent);
	}

	/***************************************
	 * Recursively removes a certain event listener from this multicaster
	 * (sub)tree and returns the resulting event listener (which may be a
	 * multicaster).
	 *
	 * @param  rToRemove The listener to remove
	 *
	 * @return The resulting event listener which may be a multicaster instance
	 */
	protected EwtEventHandler remove(EwtEventHandler rToRemove)
	{
		if (rFirst == rToRemove)
		{
			return rSecond;
		}

		if (rSecond == rToRemove)
		{
			return rFirst;
		}

		EwtEventHandler rRemoveFirst  = remove(rFirst, rToRemove);
		EwtEventHandler rRemoveSecond = remove(rSecond, rToRemove);

		if (rRemoveFirst != rFirst || rRemoveSecond != rSecond)
		{
			return add(rRemoveFirst, rRemoveSecond);
		}

		return this;
	}
}
