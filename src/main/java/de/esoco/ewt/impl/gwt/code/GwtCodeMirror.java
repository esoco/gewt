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
import de.esoco.ewt.component.TextArea.IsTextArea;

/**
 * A variation of the CodeMirror wrapper, adapted for GEWT.
 *
 * @author Matthew Horridge, Stanford University, Bio-Medical Informatics
 * Research Group, Date: 18/03/2014
 * @see "https://github.com/protegeproject/codemirror-gwt"
 */
public class GwtCodeMirror extends Composite
	implements Focusable, IsTextArea, HasValueChangeHandlers<String> {

	private static final FocusImpl focusImpl =
		FocusImpl.getFocusImplForWidget();

	private static final AutoCompletionHandler NO_AUTO_COMPLETION_HANDLER =
		new AutoCompletionHandler() {
			@Override
			public void getCompletions(String text,
				EditorPosition caretPosition, int caretIndex,
				AutoCompletionCallback callback) {
				callback.completionsReady(AutoCompletionResult.emptyResult());
			}
		};

	private static final String ELEMENT_ID_PREFIX = "cm-editor-";

	private static final boolean DEFAULT_READ_ONLY = false;

	private static final boolean DEFAULT_LINE_NUMBERS = true;

	private static final boolean DEFAULT_LINE_WRAPPING = true;

	private static int instanceCounter = 0;

	private boolean loaded = false;

	private boolean isSettingValue = false;

	private JavaScriptObject codeMirror;

	private TextMarker errorMarker = null;

	private final CodeMirrorOptions options = new CodeMirrorOptions();

	private AutoCompletionHandler autoCompletionHandler =
		NO_AUTO_COMPLETION_HANDLER;

	/**
	 * Creates a new instance.
	 *
	 * @param mode The CodeMirror mode of this instance
	 */
	public GwtCodeMirror(String mode) {
		options.setMode(mode);

		initWidget(new SimplePanel());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public HandlerRegistration addDoubleClickHandler(
		DoubleClickHandler handler) {
		return addHandler(handler, DoubleClickEvent.getType());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public HandlerRegistration addKeyDownHandler(KeyDownHandler handler) {
		return addHandler(handler, KeyDownEvent.getType());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public HandlerRegistration addKeyPressHandler(KeyPressHandler handler) {
		return addHandler(handler, KeyPressEvent.getType());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public HandlerRegistration addValueChangeHandler(
		ValueChangeHandler<String> handler) {
		return addHandler(handler, ValueChangeEvent.getType());
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
		if (codeMirror != null && errorMarker != null) {
			errorMarker.clear();
			errorMarker = null;
		}
	}

	/**
	 * Returns the current caret position.
	 *
	 * @return The caret position
	 */
	public EditorPosition getCaretPosition() {
		return codeMirror != null ?
		       EditorPosition.fromJavaScriptObject(
			       getEditorPosition(codeMirror)) :
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
	 * @param position The editor position
	 * @return The index for the editor position
	 */
	public int getIndexFromEditorPosition(EditorPosition position) {
		return codeMirror != null ?
		       calcIndexFromPosition(codeMirror,
			       position.toJavaScriptObject()) :
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
		return focusImpl.getTabIndex(getElement());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getText() {
		return codeMirror != null ? getValue(codeMirror) : options.getValue();
	}

	/**
	 * Corrects the indentation of all lines.
	 */
	public void indentAllLines() {
		if (codeMirror != null) {
			indentAllLines(codeMirror);
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
		return codeMirror != null ?
		       isReadOnly(codeMirror) :
		       options.isReadOnly();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setAccessKey(char key) {
		getElement().setPropertyString("accessKey", "" + key);
	}

	/**
	 * Sets the {@link AutoCompletionHandler}.
	 *
	 * @param handler The handler. Not {@code null}.
	 * @throws java.lang.NullPointerException if {@code autoCompletionHandler}
	 *                                        is {@code null}.
	 */
	public void setAutoCompletionHandler(AutoCompletionHandler handler) {
		autoCompletionHandler = handler;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setCharacterWidth(int columns) {
		// unsupported
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setCursorPos(int pos) {
		JavaScriptObject position =
			codeMirror != null ? calcPositionFromIndex(codeMirror, pos) : null;

		if (position != null) {
			setEditorPosition(codeMirror, position);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setEnabled(boolean enabled) {
		// unsupported
	}

	/**
	 * Sets the error range.
	 *
	 * @param start The new error range
	 * @param end   The new error range
	 */
	public void setErrorRange(EditorPosition start, EditorPosition end) {
		if (codeMirror == null) {
			return;
		}

		clearErrorRange();

		JavaScriptObject mark = markText(codeMirror,
			start.toJavaScriptObject(),
			end.toJavaScriptObject(), "error");

		errorMarker = new TextMarker(mark);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setFocus(boolean focused) {
		if (focused) {
			focusImpl.focus(getElement());
		} else {
			focusImpl.blur(getElement());
		}
	}

	/**
	 * Sets the line wrapping.
	 *
	 * @param wrap The new line wrapping
	 */
	public void setLineWrapping(boolean wrap) {
		if (codeMirror != null) {
			setLineWrapping(codeMirror, wrap);
		} else {
			options.setLineWrapping(wrap);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setReadOnly(boolean readOnly) {
		if (codeMirror == null) {
			options.setReadOnly(readOnly);
		} else {
			setReadOnly(codeMirror, readOnly);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setSelectionRange(int start, int length) {
		// unsupported
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setTabIndex(int index) {
		focusImpl.setTabIndex(getElement(), index);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setText(String text) {
		if (codeMirror == null) {
			options.setValue(text);
		} else {
			try {
				isSettingValue = true;
				setValue(codeMirror, text);
				refresh();
			} finally {
				isSettingValue = false;
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setVisibleLength(int columns) {
		// unsupported
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setVisibleLines(int rows) {
		// unsupported
	}

	/**
	 * Overrides the onLoad() to set up the CodeMirror instance.
	 */
	@Override
	protected void onLoad() {
		super.onLoad();

		if (!loaded) {
			Element element = getElement();
			String id = ELEMENT_ID_PREFIX + instanceCounter++;

			element.setId(id);
			codeMirror = setup(this, id, options.toJavaScriptObject());
			loaded = true;
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
	 * @param codeMirror The CodeMirror instance
	 * @param position   The editor position
	 * @return The index for the editor position
	 */
	private native int calcIndexFromPosition(JavaScriptObject codeMirror,
		JavaScriptObject position) /*-{
		return codeMirror.indexFromPos(position);
	}-*/;

	/**
	 * Calculates the editor position from a character index.
	 *
	 * @param codeMirror The CodeMirror instance
	 * @param index      The character index
	 * @return The editor position for the index
	 */
	private native JavaScriptObject calcPositionFromIndex(
		JavaScriptObject codeMirror, int index) /*-{
		return codeMirror.posFromIndex(index);
	}-*/;

	/**
	 * Creates an auto-completion result from an {@link AutoCompletionChoice}.
	 *
	 * @param choice The auto-completion choice
	 * @return The result
	 */
	private JavaScriptObject createAutoCompletionResult(
		AutoCompletionChoice choice) {
		JavaScriptObject from =
			choice.getReplaceTextFrom().toJavaScriptObject();
		JavaScriptObject to = choice.getReplaceTextTo().toJavaScriptObject();

		return createAutoCompletionResult(choice.getText(),
			choice.getDisplayText(), choice.getCssClassName(), from, to);
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
	 * @param callbackFunction The actual function to call.
	 * @param argument         The argument to pass to the function.
	 * @param line             The line of the completion (zero based index).
	 * @param index            The character index on the line of the
	 *                            completion
	 *                         (zero based index).
	 */
	private native void doAutoCompleteCallback(
		JavaScriptObject callbackFunction, JavaScriptObject argument, int line,
		int index) /*-{
		callbackFunction({
			list: argument,
			from: {'line': line, 'ch': nIndex}
		});
	}-*/;

	/**
	 * Fires a {@link ValueChangeEvent}. Will be invoked from CodeMirror as set
	 * in the {@link #setup(GwtCodeMirror, String, JavaScriptObject)} method.
	 */
	private void fireValueChangeEvent() {
		if (!isSettingValue) {
			ValueChangeEvent.fire(this, getText());
		}
	}

	/**
	 * Called by CodeMirror to retrieve completions.
	 *
	 * @param editorText     The current editor text
	 * @param line           The line that the caret is at (zero based)
	 * @param column         The column that the caret is at (zero based)
	 * @param index          The caret index relative to the editor text
	 * @param completionList A JavaScriptObject that is an array and should be
	 *                       populated with lists of completions. This can be
	 *                       done by calling
	 *                       {@link #addElement(JavaScriptObject,
	 *                       JavaScriptObject)}.
	 */
	private void getCompletions(final String editorText, final int line,
		final int column, final int index,
		final JavaScriptObject completionList,
		final JavaScriptObject callback) {
		autoCompletionHandler.getCompletions(editorText,
			new EditorPosition(line, column), index,
			new AutoCompletionCallback() {
				@Override
				public void completionsReady(AutoCompletionResult result) {
					for (AutoCompletionChoice choice : result.getChoices()) {
						addElement(completionList,
							createAutoCompletionResult(choice));
					}

					int fromLine = result.getFromPosition().getLineNumber();
					int fromColumn =
						result.getFromPosition().getColumnNumber();

					doAutoCompleteCallback(callback, completionList, fromLine,
						fromColumn);
				}
			});
	}

	/**
	 * Returns the editor position.
	 *
	 * @param codeMirror The editor position
	 * @return The editor position
	 */
	private native JavaScriptObject getEditorPosition(
		JavaScriptObject codeMirror) /*-{
		return codeMirror.getCursor("start");
	}-*/;

	/**
	 * Returns the value.
	 *
	 * @param codeMirror The value
	 * @return The value
	 */
	private native String getValue(JavaScriptObject codeMirror) /*-{
		return codeMirror.getValue();
	}-*/;

	/**
	 * Indents all lines.
	 *
	 * @param codeMirror The CodeMirror instance
	 */
	private native void indentAllLines(JavaScriptObject codeMirror) /*-{
		var lineCount = codeMirror.lineCount();
		for(i = 0; i < lineCount; i++) {
			codeMirror.indentLine(i);
		}
	}-*/;

	/**
	 * Returns the enabled state of the given CodeMirror object.
	 *
	 * @param codeMirror The JavaScript object
	 * @return The enabled state
	 */
	private native boolean isReadOnly(JavaScriptObject codeMirror) /*-{
		return codeMirror.getOption("readOnly");
	}-*/;

	/**
	 * Marks a range of text.
	 *
	 * @param codeMirror   The CodeMirror instance
	 * @param start        The range start
	 * @param end          The range end
	 * @param cssClassName The name of the CSS class for the range *
	 * @return A text marker JavaScriptObject
	 */
	private native JavaScriptObject markText(JavaScriptObject codeMirror,
		JavaScriptObject start, JavaScriptObject end, String cssClassName)
	/*-{
		return codeMirror.markText(start, end, {
			className: cssClassName
		});
	}-*/;

	/**
	 * Performs a deferred refresh of the CodeMirror element.
	 */
	private void refresh() {
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				refresh(codeMirror);
			}
		});
	}

	/**
	 * Calculates the editor position from a character index.
	 *
	 * @param codeMirror The CodeMirror instance
	 */
	private native void refresh(JavaScriptObject codeMirror) /*-{
		codeMirror.refresh();
	}-*/;

	/**
	 * Sets the editor position.
	 *
	 * @param codeMirror The CodeMirror instance
	 * @param position   The new position
	 */
	private native void setEditorPosition(JavaScriptObject codeMirror,
		JavaScriptObject position) /*-{
			codeMirror.setCursor(position);
	}-*/;

	/**
	 * Sets the line wrapping.
	 *
	 * @param codeMirror The CodeMirror instance
	 * @param wrap       b The new line wrapping
	 */
	private native void setLineWrapping(JavaScriptObject codeMirror,
		boolean wrap) /*-{
		codeMirror.setOption("lineWrapping", wrap);
	}-*/;

	/**
	 * Sets the read only state.
	 *
	 * @param codeMirror The CodeMirror instance
	 * @param readOnly   The new read only state
	 */
	private native void setReadOnly(JavaScriptObject codeMirror,
		boolean readOnly) /*-{
		codeMirror.setOption("readOnly", readOnly);
	}-*/;

	/**
	 * Implementation of setting the CodeMirror text.
	 *
	 * @param codeMirror The new value
	 * @param text       The new value
	 */
	private native void setValue(JavaScriptObject codeMirror, String text)
	/*-{
		codeMirror.setValue(text);
	}-*/;

	/**
	 * Sets up the CodeMirror instance in native JavaScript.
	 *
	 * @param gwtCodeMirror A pointer to the this instance which is used to
	 *                         call
	 *                      out from native code to methods on this.
	 * @param id            The id of the element which the CodeMirror editor
	 *                      should be appended to.
	 * @param options       The options for the CodeMirror instance
	 * @return The native CodeMirror object that was created and set up. Other
	 * functions can use this as a pointer for calls into native code.
	 */
	private native JavaScriptObject setup(GwtCodeMirror gwtCodeMirror,
		String id, JavaScriptObject options) /*-{
		// We install an instance of the CodeMirror editor by assigning an
		id to
		// the intended parent element and then asking code mirror to create
		 the
		// editor for that element.
		var element = $doc.getElementById(id);
		var codeMirror = $wnd.CodeMirror(
			element,
			{
				mode: options["mode"],
				readOnly: options["readOnly"],
				lineNumbers: options["lineNumbers"],
				lineWrapping: options["lineWrapping"],
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
	(gwtCodeMirror.@de.esoco.ewt.impl.gwt.code.GwtCodeMirror::getCompletions
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
		codeMirror.on("change", function () {
			$entry(gwtCodeMirror.@de.esoco.ewt.impl.gwt.code
			.GwtCodeMirror::fireValueChangeEvent()());
		});
		return codeMirror;


	}-*/;

	/**
	 * A data object holding the options for the initialization of a CodeMirror
	 * instance.
	 *
	 * @author eso
	 */
	private static class CodeMirrorOptions {

		private String mode = "text/x-groovy";

		private String value = "";

		private boolean readOnly = DEFAULT_READ_ONLY;

		private boolean lineNumbers = DEFAULT_LINE_NUMBERS;

		private boolean lineWrapping = DEFAULT_LINE_WRAPPING;

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
			return mode;
		}

		/**
		 * Returns the value.
		 *
		 * @return The value
		 */
		public String getValue() {
			return value;
		}

		/**
		 * Returns the line numbers.
		 *
		 * @return The line numbers
		 */
		public boolean isLineNumbers() {
			return lineNumbers;
		}

		/**
		 * Returns the line wrapping.
		 *
		 * @return The line wrapping
		 */
		public boolean isLineWrapping() {
			return lineWrapping;
		}

		/**
		 * Returns the read only.
		 *
		 * @return The read only
		 */
		public boolean isReadOnly() {
			return readOnly;
		}

		/**
		 * Sets the line numbers.
		 *
		 * @param lineNumbers The new line numbers
		 */
		public void setLineNumbers(boolean lineNumbers) {
			this.lineNumbers = lineNumbers;
		}

		/**
		 * Sets the line wrapping.
		 *
		 * @param lineWrapping The new line wrapping
		 */
		public void setLineWrapping(boolean lineWrapping) {
			this.lineWrapping = lineWrapping;
		}

		/**
		 * Sets the mode.
		 *
		 * @param mode The new mode
		 */
		public void setMode(String mode) {
			this.mode = mode;
		}

		/**
		 * Sets the read only.
		 *
		 * @param readOnly The new read only
		 */
		public void setReadOnly(boolean readOnly) {
			this.readOnly = readOnly;
		}

		/**
		 * Sets the value.
		 *
		 * @param value The new value
		 */
		public void setValue(String value) {
			this.value = value;
		}

		/**
		 * Converts this instance into a JavaScript object.
		 *
		 * @return The JavaScript object
		 */
		public JavaScriptObject toJavaScriptObject() {
			JavaScriptObject result = JavaScriptObject.createObject();

			addProperty(result, "value", value);
			addProperty(result, "mode", mode);
			addProperty(result, "readOnly", readOnly);
			addProperty(result, "lineNumbers", lineNumbers);
			addProperty(result, "lineWrapping", lineWrapping);

			return result;
		}
	}

	/**
	 * Java representation of a text marker.
	 *
	 * @author eso
	 */
	private static class TextMarker {

		private final JavaScriptObject javaScriptObject;

		/**
		 * Creates a new instance.
		 *
		 * @param javaScriptObject The wrapped JavaScript object
		 */
		public TextMarker(JavaScriptObject javaScriptObject) {
			this.javaScriptObject = javaScriptObject;
		}

		/**
		 * Clears this marker.
		 */
		public void clear() {
			clear(javaScriptObject);
		}

		/**
		 * Implementation of {@link #clear()}.
		 *
		 * @param object rJavaScriptObject The wrapped JavaScript object
		 */
		private native void clear(JavaScriptObject object) /*-{
			object.clear();
		}-*/;
	}
}
