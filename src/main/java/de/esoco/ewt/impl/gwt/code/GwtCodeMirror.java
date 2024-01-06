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
package de.esoco.ewt.impl.gwt.code;

import de.esoco.ewt.component.TextArea.IsTextArea;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.impl.FocusImpl;

/**
 * A variation of the CodeMirror wrapper, adapted for GEWT.
 *
 * @author Matthew Horridge, Stanford University, Bio-Medical Informatics
 * Research Group, Date: 18/03/2014
 * @see "https://github.com/protegeproject/codemirror-gwt"
 */
public class GwtCodeMirror extends Composite
	implements Focusable, IsTextArea, HasValueChangeHandlers<String> {

	private static final FocusImpl aFocusImpl =
		FocusImpl.getFocusImplForWidget();

	private static final AutoCompletionHandler NO_AUTO_COMPLETION_HANDLER =
		new AutoCompletionHandler() {
			@Override
			public void getCompletions(String sText,
				EditorPosition rCaretPosition, int nCaretIndex,
				AutoCompletionCallback rCallback) {
				rCallback.completionsReady(AutoCompletionResult.emptyResult());
			}
		};

	private static final String ELEMENT_ID_PREFIX = "cm-editor-";

	private static final boolean DEFAULT_READ_ONLY = false;

	private static final boolean DEFAULT_LINE_NUMBERS = true;

	private static final boolean DEFAULT_LINE_WRAPPING = true;

	private static int nInstanceCounter = 0;

	private boolean bLoaded = false;

	private boolean bIsSettingValue = false;

	private JavaScriptObject aCodeMirror;

	private TextMarker aErrorMarker = null;

	private CodeMirrorOptions aOptions = new CodeMirrorOptions();

	private AutoCompletionHandler rAutoCompletionHandler =
		NO_AUTO_COMPLETION_HANDLER;

	/**
	 * Creates a new instance.
	 *
	 * @param sMode The CodeMirror mode of this instance
	 */
	public GwtCodeMirror(String sMode) {
		aOptions.setMode(sMode);

		initWidget(new SimplePanel());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public HandlerRegistration addDoubleClickHandler(
		DoubleClickHandler rHandler) {
		return addHandler(rHandler, DoubleClickEvent.getType());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public HandlerRegistration addKeyDownHandler(KeyDownHandler rHandler) {
		return addHandler(rHandler, KeyDownEvent.getType());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public HandlerRegistration addKeyPressHandler(KeyPressHandler rHandler) {
		return addHandler(rHandler, KeyPressEvent.getType());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public HandlerRegistration addValueChangeHandler(
		ValueChangeHandler<String> rHandler) {
		return addHandler(rHandler, ValueChangeEvent.getType());
	}

	/**
	 * Clears the previously set {@link AutoCompletionHandler}.
	 */
	public void clearAutoCompletionHandler() {
		setAutoCompletionHandler(NO_AUTO_COMPLETION_HANDLER);
	}

	/**
	 * Clears the current error range.
	 */
	public void clearErrorRange() {
		if (aCodeMirror != null && aErrorMarker != null) {
			aErrorMarker.clear();
			aErrorMarker = null;
		}
	}

	/**
	 * Returns the current caret position.
	 *
	 * @return The caret position
	 */
	public EditorPosition getCaretPosition() {
		return aCodeMirror != null ?
		       EditorPosition.fromJavaScriptObject(
			       getEditorPosition(aCodeMirror)) :
		       new EditorPosition(0, 0);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getCursorPos() {
		return getIndexFromEditorPosition(getCaretPosition());
	}

	/**
	 * Returns the index for a caret position.
	 *
	 * @param rPosition The editor position
	 * @return The index for the editor position
	 */
	public int getIndexFromEditorPosition(EditorPosition rPosition) {
		return aCodeMirror != null ?
		       calcIndexFromPosition(aCodeMirror,
			       rPosition.toJavaScriptObject()) :
		       0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getSelectedText() {
		return "";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getTabIndex() {
		return aFocusImpl.getTabIndex(getElement());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getText() {
		return aCodeMirror != null ?
		       getValue(aCodeMirror) :
		       aOptions.getValue();
	}

	/**
	 * Corrects the indentation of all lines.
	 */
	public void indentAllLines() {
		if (aCodeMirror != null) {
			indentAllLines(aCodeMirror);
		}
	}

	/**
	 * @see com.google.gwt.user.client.ui.HasEnabled#isEnabled()
	 */
	@Override
	public boolean isEnabled() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isReadOnly() {
		return aCodeMirror != null ?
		       isReadOnly(aCodeMirror) :
		       aOptions.isReadOnly();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setAccessKey(char cKey) {
		getElement().setPropertyString("accessKey", "" + cKey);
	}

	/**
	 * Sets the {@link AutoCompletionHandler}.
	 *
	 * @param rHandler The handler. Not {@code null}.
	 * @throws java.lang.NullPointerException if {@code autoCompletionHandler}
	 *                                        is {@code null}.
	 */
	public void setAutoCompletionHandler(AutoCompletionHandler rHandler) {
		rAutoCompletionHandler = rHandler;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setCharacterWidth(int nColumns) {
		// unsupported
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setCursorPos(int nPosition) {
		JavaScriptObject rPosition = aCodeMirror != null ?
		                             calcPositionFromIndex(aCodeMirror,
			                             nPosition) :
		                             null;

		if (rPosition != null) {
			setEditorPosition(aCodeMirror, rPosition);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setEnabled(boolean bEnabled) {
		// unsupported
	}

	/**
	 * Sets the error range.
	 *
	 * @param rStart The new error range
	 * @param rEnd   The new error range
	 */
	public void setErrorRange(EditorPosition rStart, EditorPosition rEnd) {
		if (aCodeMirror == null) {
			return;
		}

		clearErrorRange();

		JavaScriptObject rMark =
			markText(aCodeMirror, rStart.toJavaScriptObject(),
				rEnd.toJavaScriptObject(), "error");

		aErrorMarker = new TextMarker(rMark);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setFocus(boolean bFocused) {
		if (bFocused) {
			aFocusImpl.focus(getElement());
		} else {
			aFocusImpl.blur(getElement());
		}
	}

	/**
	 * Sets the line wrapping.
	 *
	 * @param bWrap The new line wrapping
	 */
	public void setLineWrapping(boolean bWrap) {
		if (aCodeMirror != null) {
			setLineWrapping(aCodeMirror, bWrap);
		} else {
			aOptions.setLineWrapping(bWrap);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setReadOnly(boolean bReadOnly) {
		if (aCodeMirror == null) {
			aOptions.setReadOnly(bReadOnly);
		} else {
			setReadOnly(aCodeMirror, bReadOnly);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setSelectionRange(int nStart, int nLength) {
		// unsupported
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setTabIndex(int nIndex) {
		aFocusImpl.setTabIndex(getElement(), nIndex);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setText(String sText) {
		if (aCodeMirror == null) {
			aOptions.setValue(sText);
		} else {
			try {
				bIsSettingValue = true;
				setValue(aCodeMirror, sText);
				refresh();
			} finally {
				bIsSettingValue = false;
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setVisibleLength(int nColumns) {
		// unsupported
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setVisibleLines(int nRows) {
		// unsupported
	}

	/**
	 * Overrides the onLoad() to set up the CodeMirror instance.
	 */
	@Override
	protected void onLoad() {
		super.onLoad();

		if (!bLoaded) {
			Element rElement = getElement();
			String sId = ELEMENT_ID_PREFIX + nInstanceCounter++;

			rElement.setId(sId);
			aCodeMirror = setup(this, sId, aOptions.toJavaScriptObject());
			bLoaded = true;
		}
	}

	/**
	 * Adds a JavaScriptObject to a JavaScript array.
	 *
	 * @param jsListObject The JavaScriptObject that represents the array.
	 * @param elementToAdd The JavaScriptObject that represents the element to
	 *                     be added.
	 */
	private native void addElement(JavaScriptObject jsListObject,
		JavaScriptObject elementToAdd) /*-{
		jsListObject.push(elementToAdd);
	}-*/;

	/**
	 * Calculates the character index from an editor position.
	 *
	 * @param rCodeMirror The CodeMirror instance
	 * @param rPosition   The editor position
	 * @return The index for the editor position
	 */
	private native int calcIndexFromPosition(JavaScriptObject rCodeMirror,
		JavaScriptObject rPosition) /*-{
		return rCodeMirror.indexFromPos(rPosition);
	}-*/;

	/**
	 * Calculates the editor position from a character index.
	 *
	 * @param rCodeMirror The CodeMirror instance
	 * @param nIndex      The character index
	 * @return The editor position for the index
	 */
	private native JavaScriptObject calcPositionFromIndex(
		JavaScriptObject rCodeMirror, int nIndex) /*-{
		return rCodeMirror.posFromIndex(nIndex);
	}-*/;

	/**
	 * Creates an auto-completion result from an {@link AutoCompletionChoice}.
	 *
	 * @param rChoice The auto-completion choice
	 * @return The result
	 */
	private JavaScriptObject createAutoCompletionResult(
		AutoCompletionChoice rChoice) {
		JavaScriptObject from =
			rChoice.getReplaceTextFrom().toJavaScriptObject();
		JavaScriptObject to = rChoice.getReplaceTextTo().toJavaScriptObject();

		return createAutoCompletionResult(rChoice.getText(),
			rChoice.getDisplayText(), rChoice.getCssClassName(), from, to);
	}

	/**
	 * Creates a JavaScriptObject that has the appropriate properties to
	 * describe the auto-completion result.
	 *
	 * @param text        The text to insert.
	 * @param displayText The text to display.
	 * @param className   The CSS class name of the item in the list.
	 * @return The JavaScriptObject that specified the given properties.
	 */
	private native JavaScriptObject createAutoCompletionResult(String text,
		String displayText, String className, JavaScriptObject from,
		JavaScriptObject to) /*-{
		return {
			'text': text,
			'displayText': displayText,
			'className': className,
			'from': from,
			'to': to

		}
	}-*/;

	/**
	 * Calls the auto-complete callback with the specified argument.
	 *
	 * @param rCallbackFunction The actual function to call.
	 * @param rArgument         The argument to pass to the function.
	 * @param nLine             The line of the completion (zero based index).
	 * @param nIndex            The character index on the line of the
	 *                          completion (zero based index).
	 */
	private native void doAutoCompleteCallback(
		JavaScriptObject rCallbackFunction, JavaScriptObject rArgument,
		int nLine, int nIndex) /*-{
		rCallbackFunction({
			list: rArgument,
			from: {'line': nLine, 'ch': nIndex}
		});
	}-*/;

	/**
	 * Fires a {@link ValueChangeEvent}. Will be invoked from CodeMirror as set
	 * in the {@link #setup(GwtCodeMirror, String, JavaScriptObject)} method.
	 */
	private void fireValueChangeEvent() {
		if (!bIsSettingValue) {
			ValueChangeEvent.fire(this, getText());
		}
	}

	/**
	 * Called by CodeMirror to retrieve completions.
	 *
	 * @param sEditorText     The current editor text
	 * @param nLine           The line that the caret is at (zero based)
	 * @param nColumn         The column that the caret is at (zero based)
	 * @param nIndex          The caret index relative to the editor text
	 * @param rCompletionList A JavaScriptObject that is an array and should be
	 *                        populated with lists of completions. This can be
	 *                        done by calling
	 *                        {@link #addElement(JavaScriptObject,
	 *                        JavaScriptObject)}.
	 */
	private void getCompletions(final String sEditorText, final int nLine,
		final int nColumn, final int nIndex,
		final JavaScriptObject rCompletionList,
		final JavaScriptObject rCallback) {
		rAutoCompletionHandler.getCompletions(sEditorText,
			new EditorPosition(nLine, nColumn), nIndex,
			new AutoCompletionCallback() {
				@Override
				public void completionsReady(AutoCompletionResult rResult) {
					for (AutoCompletionChoice rChoice : rResult.getChoices()) {
						addElement(rCompletionList,
							createAutoCompletionResult(rChoice));
					}

					int nFromLine = rResult.getFromPosition().getLineNumber();
					int nFromColumn =
						rResult.getFromPosition().getColumnNumber();

					doAutoCompleteCallback(rCallback, rCompletionList,
						nFromLine, nFromColumn);
				}
			});
	}

	/**
	 * Returns the editor position.
	 *
	 * @param rCodeMirror The editor position
	 * @return The editor position
	 */
	private native JavaScriptObject getEditorPosition(
		JavaScriptObject rCodeMirror) /*-{
		return rCodeMirror.getCursor("start");
	}-*/;

	/**
	 * Returns the value.
	 *
	 * @param rCodeMirror The value
	 * @return The value
	 */
	private native String getValue(JavaScriptObject rCodeMirror) /*-{
		return rCodeMirror.getValue();
	}-*/;

	/**
	 * Indents all lines.
	 *
	 * @param rCodeMirror The CodeMirror instance
	 */
	private native void indentAllLines(JavaScriptObject rCodeMirror) /*-{
		var lineCount = rCodeMirror.lineCount();
		for(i = 0; i < lineCount; i++) {
			rCodeMirror.indentLine(i);
		}
	}-*/;

	/**
	 * Returns the enabled state of the given CodeMirror object.
	 *
	 * @param rCodeMirror The JavaScript object
	 * @return The enabled state
	 */
	private native boolean isReadOnly(JavaScriptObject rCodeMirror) /*-{
		return rCodeMirror.getOption("readOnly");
	}-*/;

	/**
	 * Marks a range of text.
	 *
	 * @param rCodeMirror   The CodeMirror instance
	 * @param rStart        The range start
	 * @param rEnd          The range end
	 * @param sCssClassName The name of the CSS class for the range *
	 * @return A text marker JavaScriptObject
	 */
	private native JavaScriptObject markText(JavaScriptObject rCodeMirror,
		JavaScriptObject rStart, JavaScriptObject rEnd, String sCssClassName)
	/*-{
		return rCodeMirror.markText(rStart, rEnd, {
			className: sCssClassName
		});
	}-*/;

	/**
	 * Performs a deferred refresh of the CodeMirror element.
	 */
	private void refresh() {
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				refresh(aCodeMirror);
			}
		});
	}

	/**
	 * Calculates the editor position from a character index.
	 *
	 * @param rCodeMirror The CodeMirror instance
	 */
	private native void refresh(JavaScriptObject rCodeMirror) /*-{
		rCodeMirror.refresh();
	}-*/;

	/**
	 * Sets the editor position.
	 *
	 * @param rCodeMirror The CodeMirror instance
	 * @param rPosition   The new position
	 */
	private native void setEditorPosition(JavaScriptObject rCodeMirror,
		JavaScriptObject rPosition) /*-{
			rCodeMirror.setCursor(rPosition);
	}-*/;

	/**
	 * Sets the line wrapping.
	 *
	 * @param rCodeMirror The CodeMirror instance
	 * @param bWrap       b The new line wrapping
	 */
	private native void setLineWrapping(JavaScriptObject rCodeMirror,
		boolean bWrap) /*-{
		rCodeMirror.setOption("lineWrapping", bWrap);
	}-*/;

	/**
	 * Sets the read only state.
	 *
	 * @param rCodeMirror The CodeMirror instance
	 * @param bReadOnly   The new read only state
	 */
	private native void setReadOnly(JavaScriptObject rCodeMirror,
		boolean bReadOnly) /*-{
		rCodeMirror.setOption("readOnly", bReadOnly);
	}-*/;

	/**
	 * Implementation of setting the CodeMirror text.
	 *
	 * @param rCodeMirror The new value
	 * @param sText       The new value
	 */
	private native void setValue(JavaScriptObject rCodeMirror, String sText)
	/*-{
		rCodeMirror.setValue(sText);
	}-*/;

	/**
	 * Sets up the CodeMirror instance in native JavaScript.
	 *
	 * @param rGwtCodeMirror A pointer to the this instance which is used to
	 *                       call out from native code to methods on this.
	 * @param sId            The id of the element which the CodeMirror editor
	 *                       should be appended to.
	 * @param rOptions       The options for the CodeMirror instance
	 * @return The native CodeMirror object that was created and set up. Other
	 * functions can use this as a pointer for calls into native code.
	 */
	private native JavaScriptObject setup(GwtCodeMirror rGwtCodeMirror,
		String sId, JavaScriptObject rOptions) /*-{
		// We install an instance of the CodeMirror editor by assigning an
		id to
		// the intended parent element and then asking code mirror to create
		 the
		// editor for that element.
		var rElement = $doc.getElementById(sId);
		var aCodeMirror = $wnd.CodeMirror(
			rElement,
			{
				mode: rOptions["mode"],
				readOnly: rOptions["readOnly"],
				lineNumbers: rOptions["lineNumbers"],
				lineWrapping: rOptions["lineWrapping"],
				theme: "eclipse",
				viewportMargin: Infinity,
				extraKeys: {
					"Ctrl-Space": "autocomplete"
	//															function
	(editor) {
	//															   $wnd
	.CodeMirror.showHint(editor, function (editor, callback) {
	//																   var
	result = [];
	//																   var
	cursor = editor.doc.getCursor();
	//																   var
	index = editor.indexFromPos(cursor);
	//																   $entry
	(rGwtCodeMirror.@de.esoco.ewt.impl.gwt.code.GwtCodeMirror::getCompletions
	(Ljava/lang/String;IIILcom/google/gwt/core/client/JavaScriptObject;
	Lcom/google/gwt/core/client/JavaScriptObject;)(editor.getValue(), cursor
	.line, cursor.ch, index, result, callback));
	//															   }, {async:
	true});
	//														   }
				}
			}
		);
		// Listener for changes and propagate them back into the GWT compiled
		code
		aCodeMirror.on("change", function () {
			$entry(rGwtCodeMirror.@de.esoco.ewt.impl.gwt.code
			.GwtCodeMirror::fireValueChangeEvent()());
		});
		return aCodeMirror;


	}-*/;

	/**
	 * A data object holding the options for the initialization of a CodeMirror
	 * instance.
	 *
	 * @author eso
	 */
	private static class CodeMirrorOptions {

		private String sMode = "text/x-groovy";

		private String sValue = "";

		private boolean bReadOnly = DEFAULT_READ_ONLY;

		private boolean bLineNumbers = DEFAULT_LINE_NUMBERS;

		private boolean bLineWrapping = DEFAULT_LINE_WRAPPING;

		/**
		 * Add a property.
		 */
		private static native void addProperty(
			JavaScriptObject javaScriptObject, String property, String value)
		/*-{
			javaScriptObject[property] = value;
		}-*/;

		/**
		 * Add a boolean property.
		 */
		private static native void addProperty(
			JavaScriptObject javaScriptObject, String property, boolean value) /*-{
			javaScriptObject[property] = value;
		}-*/;

		/**
		 * Returns the mode.
		 *
		 * @return The mode
		 */
		public String getMode() {
			return sMode;
		}

		/**
		 * Returns the value.
		 *
		 * @return The value
		 */
		public String getValue() {
			return sValue;
		}

		/**
		 * Returns the line numbers.
		 *
		 * @return The line numbers
		 */
		public boolean isLineNumbers() {
			return bLineNumbers;
		}

		/**
		 * Returns the line wrapping.
		 *
		 * @return The line wrapping
		 */
		public boolean isLineWrapping() {
			return bLineWrapping;
		}

		/**
		 * Returns the read only.
		 *
		 * @return The read only
		 */
		public boolean isReadOnly() {
			return bReadOnly;
		}

		/**
		 * Sets the line numbers.
		 *
		 * @param lineNumbers The new line numbers
		 */
		public void setLineNumbers(boolean lineNumbers) {
			this.bLineNumbers = lineNumbers;
		}

		/**
		 * Sets the line wrapping.
		 *
		 * @param lineWrapping The new line wrapping
		 */
		public void setLineWrapping(boolean lineWrapping) {
			this.bLineWrapping = lineWrapping;
		}

		/**
		 * Sets the mode.
		 *
		 * @param mode The new mode
		 */
		public void setMode(String mode) {
			this.sMode = mode;
		}

		/**
		 * Sets the read only.
		 *
		 * @param readOnly The new read only
		 */
		public void setReadOnly(boolean readOnly) {
			this.bReadOnly = readOnly;
		}

		/**
		 * Sets the value.
		 *
		 * @param value The new value
		 */
		public void setValue(String value) {
			this.sValue = value;
		}

		/**
		 * Converts this instance into a JavaScript object.
		 *
		 * @return The JavaScript object
		 */
		public JavaScriptObject toJavaScriptObject() {
			JavaScriptObject result = JavaScriptObject.createObject();

			addProperty(result, "value", sValue);
			addProperty(result, "mode", sMode);
			addProperty(result, "readOnly", bReadOnly);
			addProperty(result, "lineNumbers", bLineNumbers);
			addProperty(result, "lineWrapping", bLineWrapping);

			return result;
		}
	}

	/**
	 * Java representation of a text marker.
	 *
	 * @author eso
	 */
	private static class TextMarker {

		private JavaScriptObject rJavaScriptObject;

		/**
		 * Creates a new instance.
		 *
		 * @param rJavaScriptObject The wrapped JavaScript object
		 */
		public TextMarker(JavaScriptObject rJavaScriptObject) {
			this.rJavaScriptObject = rJavaScriptObject;
		}

		/**
		 * Clears this marker.
		 */
		public void clear() {
			clear(rJavaScriptObject);
		}

		/**
		 * Implementation of {@link #clear()}.
		 *
		 * @param rObject rJavaScriptObject The wrapped JavaScript object
		 */
		private native void clear(JavaScriptObject rObject) /*-{
			rObject.clear();
		}-*/;
	}
}
