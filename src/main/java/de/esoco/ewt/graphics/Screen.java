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
package de.esoco.ewt.graphics;

import de.esoco.ewt.geometry.Rectangle;

import com.google.gwt.user.client.Window;

/**
 * Interface for screen devices.
 *
 * @author eso
 */
public class Screen {

	/**
	 * Returns the bounding rectangle of this device.
	 *
	 * @return The bounding rectangle
	 */
	public Rectangle getBounds() {
		return new Rectangle(0, 0, Window.getClientWidth(),
			Window.getClientHeight());
	}

	/**
	 * Returns the rectangle of the area that is available to a client on this
	 * device. This may be the same or smaller than the bounding rectangle.
	 *
	 * @return The rectangle of the client area
	 */
	public Rectangle getClientArea() {
		return new Rectangle(0, 0, Window.getClientWidth(),
			Window.getClientHeight());
	}
}
