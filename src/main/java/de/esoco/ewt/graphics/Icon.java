//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// This file is a part of the 'gewt' project.
// Copyright 2016 Elmar Sonnenschein, esoco GmbH, Flensburg, Germany
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

/**
 * An image implementation that represents an icon which is identified by it's
 * name.
 *
 * @author eso
 */
public class Icon implements Image {

	private final String name;

	/**
	 * Creates a new instance.
	 *
	 * @param iconName name
	 */
	public Icon(String iconName) {
		name = iconName;
	}

	/**
	 * Returns the name of this icon.
	 *
	 * @return The icon name
	 */
	public final String getName() {
		return name;
	}
}
