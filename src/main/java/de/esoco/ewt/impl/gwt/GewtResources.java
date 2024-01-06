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

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ImageResource.ImageOptions;
import com.google.gwt.resources.client.ImageResource.RepeatStyle;

/**
 * A client bundle containing the resources used by GEWT.
 *
 * @author eso
 */
public interface GewtResources extends ClientBundle {

	/**
	 * The singleton instance of this interface.
	 */
	public static final GewtResources INSTANCE =
		GWT.create(GewtResources.class);

	/**
	 * The GEWT CSS resource.
	 *
	 * @return The GEWT CSS resource
	 */
	@Source("gewt.css")
	GewtCss css();

	/**
	 * Image.
	 *
	 * @return Image
	 */
	@Source("img/add.png")
	ImageResource imAdd();

	/**
	 * Image.
	 *
	 * @return Image
	 */
	@Source("img/arrowDown.png")
	ImageResource imArrowDown();

	/**
	 * Image.
	 *
	 * @return Image
	 */
	@Source("img/arrowDownDisabled.png")
	ImageResource imArrowDownDisabled();

	/**
	 * Image.
	 *
	 * @return Image
	 */
	@Source("img/arrowDownHover.png")
	ImageResource imArrowDownHover();

	/**
	 * Image.
	 *
	 * @return Image
	 */
	@Source("img/arrowDownPressed.png")
	ImageResource imArrowDownPressed();

	/**
	 * Image.
	 *
	 * @return Image
	 */
	@Source("img/arrowUp.png")
	ImageResource imArrowUp();

	/**
	 * Image.
	 *
	 * @return Image
	 */
	@Source("img/arrowUpDisabled.png")
	ImageResource imArrowUpDisabled();

	/**
	 * Image.
	 *
	 * @return Image
	 */
	@Source("img/arrowUpHover.png")
	ImageResource imArrowUpHover();

	/**
	 * Image.
	 *
	 * @return Image
	 */
	@Source("img/arrowUpPressed.png")
	ImageResource imArrowUpPressed();

	/**
	 * Image.
	 *
	 * @return Image
	 */
	@Source("img/back.png")
	ImageResource imBack();

	/**
	 * Image.
	 *
	 * @return Image
	 */
	@Source("img/busy.gif")
	ImageResource imBusy();

	/**
	 * Image.
	 *
	 * @return Image
	 */
	@Source("img/cancel.png")
	ImageResource imCancel();

	/**
	 * Image.
	 *
	 * @return Image
	 */
	@Source("img/clearSelection.png")
	ImageResource imClearSelection();

	/**
	 * Image.
	 *
	 * @return Image
	 */
	@Source("img/clock.png")
	ImageResource imClock();

	/**
	 * Image.
	 *
	 * @return Image
	 */
	@Source("img/download.png")
	ImageResource imDownload();

	/**
	 * Image.
	 *
	 * @return Image
	 */
	@Source("img/filter.png")
	ImageResource imFilter();

	/**
	 * Image.
	 *
	 * @return Image
	 */
	@Source("img/forward.png")
	ImageResource imForward();

	/**
	 * Image.
	 *
	 * @return Image
	 */
	@Source("img/left.png")
	ImageResource imLeft();

	/**
	 * Image.
	 *
	 * @return Image
	 */
	@Source("img/left-xl.png")
	ImageResource imLeftXL();

	/**
	 * Image.
	 *
	 * @return Image
	 */
	@Source("img/less.png")
	ImageResource imLess();

	/**
	 * Image.
	 *
	 * @return Image
	 */
	@Source("img/lock.png")
	ImageResource imLock();

	/**
	 * Image.
	 *
	 * @return Image
	 */
	@Source("img/more.png")
	ImageResource imMore();

	/**
	 * Image.
	 *
	 * @return Image
	 */
	@Source("img/ok.png")
	ImageResource imOk();

	/**
	 * Image.
	 *
	 * @return Image
	 */
	@Source("img/right.png")
	ImageResource imRight();

	/**
	 * Image.
	 *
	 * @return Image
	 */
	@Source("img/right-xl.png")
	ImageResource imRightXL();

	/**
	 * Image.
	 *
	 * @return Image
	 */
	@Source("img/sortAscending.png")
	ImageResource imSortAscending();

	/**
	 * Image.
	 *
	 * @return Image
	 */
	@Source("img/sortDescending.png")
	ImageResource imSortDescending();

	/**
	 * Image for CSS.
	 *
	 * @return Image
	 */
	@ImageOptions(repeatStyle = RepeatStyle.Horizontal)
	@Source("img/tableBack.png")
	ImageResource imTableBack();

	/**
	 * Image.
	 *
	 * @return Image
	 */
	@Source("img/today.png")
	ImageResource imToday();

	/**
	 * Image.
	 *
	 * @return Image
	 */
	@Source("img/treeOpen.gif")
	ImageResource imTreeCollapse();

	/**
	 * Image.
	 *
	 * @return Image
	 */
	@Source("img/treeClosed.gif")
	ImageResource imTreeExpand();

	/**
	 * Image.
	 *
	 * @return Image
	 */
	@Source("img/treeLeaf.gif")
	ImageResource imTreeLeaf();

	/**
	 * Image.
	 *
	 * @return Image
	 */
	@Source("img/unlock.png")
	ImageResource imUnlock();
}
