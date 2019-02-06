//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// chaintag source file
// Copyright (c) 2019 by Elmar Sonnenschein / esoco GmbH
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
package de.esoco.ewt.js;

import de.esoco.ewt.js.html.HTMLElement;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;


/********************************************************************
 * JSType declaration for the FileSelect web component.
 *
 * @author eso
 */
@JsType(
		isNative  = true, namespace = JsPackage.GLOBAL,
		name	  = "sdack-file-select"
	   )
public class FileSelect extends HTMLElement
{
	//~ Methods ----------------------------------------------------------------

	/***************************************
	 * Returns the button text.
	 *
	 * @return The button text
	 */
	public native String getButtonText();

	/***************************************
	 * Returns the drop area text.
	 *
	 * @return The drop area text
	 */
	public native String getDropAreaText();

	/***************************************
	 * Checks whether this instance allows the selection of multiple files.
	 *
	 * @return TRUE if multiple selections are possible
	 */
	public native boolean isMultiple();

	/***************************************
	 * Checks the layout of this instance.
	 *
	 * @return TRUE if the layout is vertical, FALSE if horizontal
	 */
	public native boolean isVertical();

	/***************************************
	 * Sets the button text.
	 *
	 * @param sText The new button text
	 */
	public native void setButtonText(String sText);

	/***************************************
	 * Sets the drop area text.
	 *
	 * @param sText The new drop area text
	 */
	public native void setDropAreaText(String sText);

	/***************************************
	 * Enables or disables the selection of multiple files.
	 *
	 * @param bMultiple TRUE to enable multiple selections
	 */
	public native void setMultiple(boolean bMultiple);

	/***************************************
	 * Switches between horizontal and vertical layout of this component.
	 *
	 * @param bVertical TRUE for a vertical layout
	 */
	public native void setVertical(boolean bVertical);
}
