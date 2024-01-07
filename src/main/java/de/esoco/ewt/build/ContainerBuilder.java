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
package de.esoco.ewt.build;

import de.esoco.ewt.EWT;
import de.esoco.ewt.UserInterfaceContext;
import de.esoco.ewt.component.Button;
import de.esoco.ewt.component.Calendar;
import de.esoco.ewt.component.CheckBox;
import de.esoco.ewt.component.ComboBox;
import de.esoco.ewt.component.Component;
import de.esoco.ewt.component.Composite;
import de.esoco.ewt.component.Container;
import de.esoco.ewt.component.DateField;
import de.esoco.ewt.component.DeckPanel;
import de.esoco.ewt.component.FileChooser;
import de.esoco.ewt.component.Label;
import de.esoco.ewt.component.List;
import de.esoco.ewt.component.ListBox;
import de.esoco.ewt.component.Panel;
import de.esoco.ewt.component.ProgressBar;
import de.esoco.ewt.component.RadioButton;
import de.esoco.ewt.component.ScrollPanel;
import de.esoco.ewt.component.Spinner;
import de.esoco.ewt.component.SplitPanel;
import de.esoco.ewt.component.StackPanel;
import de.esoco.ewt.component.TabPanel;
import de.esoco.ewt.component.Table;
import de.esoco.ewt.component.TextArea;
import de.esoco.ewt.component.TextField;
import de.esoco.ewt.component.ToggleButton;
import de.esoco.ewt.component.Tree;
import de.esoco.ewt.component.TreeTable;
import de.esoco.ewt.component.Website;
import de.esoco.ewt.layout.EdgeLayout;
import de.esoco.ewt.layout.GenericLayout;
import de.esoco.ewt.style.StyleData;
import de.esoco.lib.property.LayoutType;

import java.util.Date;

/**
 * A container builder implementation that provides methods to create components
 * and add them to a container.
 *
 * @author eso
 */
public class ContainerBuilder<C extends Container> {

	private C container;

	private ContainerBuilder<?> parent = null;

	private Label formLabel;

	/**
	 * Creates a new instance that builds in a certain container.
	 *
	 * @param container The container builder
	 */
	public ContainerBuilder(C container) {
		this.container = container;
	}

	/**
	 * Internal constructor to create a new instance with a certain parent
	 * builder.
	 *
	 * @param container The container to build components in
	 * @param parent    The parent container builder
	 */
	ContainerBuilder(C container, ContainerBuilder<?> parent) {
		this(container);

		this.parent = parent;
	}

	/**
	 * Adds a text-only button.
	 *
	 * @see #addButton(StyleData, String, Object)
	 */
	public Button addButton(StyleData style, String text) {
		return addButton(style, text, null);
	}

	/**
	 * Adds a new {@link Button} instance.
	 *
	 * @param style The style data
	 * @param text  The button text
	 * @param image The button image or NULL for none
	 * @return The new component
	 */
	public Button addButton(StyleData style, String text, Object image) {
		Button component = new Button();

		addComponent(component, style, text, image);

		return component;
	}

	/**
	 * Creates a new {@link Calendar} component.
	 *
	 * @param style The style data
	 * @param date  The initial date of the component
	 * @return The new component
	 */
	public Calendar addCalendar(StyleData style, Date date) {
		Calendar component = new Calendar();

		addComponent(component, style, null, null);
		component.setDate(date);

		return component;
	}

	/**
	 * Creates a new {@link CheckBox}.
	 *
	 * @param style The style data
	 * @param text  The check box text
	 * @param image The button image or NULL for none
	 * @return The new component
	 */
	public CheckBox addCheckBox(StyleData style, String text, Object image) {
		CheckBox component = new CheckBox();

		addComponent(component, style, text, image);

		return component;
	}

	/**
	 * Creates a new {@link ComboBox}.
	 *
	 * @param style The style data
	 * @param text  The initial text of the combo box text field
	 * @return The new component
	 */
	public ComboBox addComboBox(StyleData style, String text) {
		ComboBox component = new ComboBox();

		addComponent(component, style, text, null);

		return component;
	}

	/**
	 * Adds a custom {@link Component} component to the container.
	 *
	 * @param component The component to add
	 * @param style     The style data
	 * @return The input component, attached to the container
	 */
	public <T extends Component> T addComponent(T component, StyleData style) {
		addComponent(component, style, null, null);

		return component;
	}

	/**
	 * Adds an instance of a certain {@link Composite} implementation to the
	 * container.
	 *
	 * @param composite The composite to add
	 * @param style     The style data
	 * @return The input composite, attached to the container
	 */
	public <T extends Composite> T addComposite(T composite, StyleData style) {
		addComponent(composite, style, null, null);

		return composite;
	}

	/**
	 * Creates a new {@link DateField}.
	 *
	 * @param style The style data
	 * @param date  The initial date of the component
	 * @return The new component
	 */
	public DateField addDateField(StyleData style, Date date) {
		DateField component = new DateField();

		addComponent(component, style, null, null);
		component.setDate(date);

		return component;
	}

	/**
	 * Creates a new {@link DeckPanel}.
	 *
	 * @param style The style data
	 * @return The new component
	 */
	public ContainerBuilder<DeckPanel> addDeckPanel(StyleData style) {
		DeckPanel component = new DeckPanel(container, style);

		addComponent(component, style, null, null);

		return new ContainerBuilder<DeckPanel>(component, this);
	}

	/**
	 * Creates a new {@link FileChooser}.
	 *
	 * @param style      The style data
	 * @param action     The identifying name of the file chooser
	 * @param buttonText The text for the action button of the file chooser
	 * @return The new component
	 */
	public FileChooser addFileChooser(StyleData style, String action,
		String buttonText) {
		FileChooser component = new FileChooser(action);

		addComponent(component, style, buttonText, null);

		return component;
	}

	/**
	 * Adds a text-only label.
	 *
	 * @see #addLabel(StyleData, String, Object)
	 */
	public Label addLabel(StyleData style, String text) {
		return addLabel(style, text, null);
	}

	/**
	 * Creates a new {@link Label}.
	 *
	 * @param style The style data
	 * @param text  The label text
	 * @param image The label image or NULL for none
	 * @return The new component
	 */
	public Label addLabel(StyleData style, String text, Object image) {
		Label label = new Label();

		addComponent(label, style, text, image);

		if (label.isFormLabel()) {
			formLabel = label;
		}

		return label;
	}

	/**
	 * Creates a new {@link List}.
	 *
	 * @param style The style data
	 * @return The new component
	 */
	public List addList(StyleData style) {
		List component = new List();

		addComponent(component, style, null, null);

		return component;
	}

	/**
	 * Creates a new {@link ListBox}.
	 *
	 * @param style The style data
	 * @return The new component
	 */
	public ListBox addListBox(StyleData style) {
		ListBox component = new ListBox();

		addComponent(component, style, null, null);

		return component;
	}

	/**
	 * Creates a {@link Panel} with a default {@link EdgeLayout} and returns a
	 * container builder for the new panel.
	 *
	 * @param style The style data
	 * @return A container builder wrapping the new panel
	 */
	public ContainerBuilder<Panel> addPanel(StyleData style) {
		return addPanel(style, new EdgeLayout(0));
	}

	/**
	 * Creates a new {@link Panel} with a layout created through the layout
	 * factory returned by {@link EWT#getLayoutFactory()} and returns a
	 * container builder for the new panel.
	 *
	 * @param style      The style data
	 * @param layoutType The panel layout
	 * @return A container builder wrapping the new panel
	 */
	public ContainerBuilder<Panel> addPanel(StyleData style,
		LayoutType layoutType) {
		GenericLayout layout =
			EWT.getLayoutFactory().createLayout(container, style, layoutType);

		return addPanel(style, layout);
	}

	/**
	 * Creates a new {@link Panel} with a certain layout and returns a
	 * container
	 * builder for the new panel.
	 *
	 * @param style  The style data
	 * @param layout The panel layout
	 * @return A container builder wrapping the new panel
	 */
	public ContainerBuilder<Panel> addPanel(StyleData style,
		GenericLayout layout) {
		Panel panel = new Panel();

		panel.setLayout(layout);

		addComponent(panel, style, null, null);

		return new ContainerBuilder<Panel>(panel, this);
	}

	/**
	 * Creates a new {@link ProgressBar}.
	 *
	 * @param style The style data
	 * @return The new component
	 */
	public ProgressBar addProgressBar(StyleData style) {
		ProgressBar component = new ProgressBar();

		addComponent(component, style, null, null);

		return component;
	}

	/**
	 * Creates a new {@link RadioButton}.
	 *
	 * @param style The style data
	 * @param text  The button text
	 * @param image The button image or NULL for none
	 * @return The new component
	 */
	public RadioButton addRadioButton(StyleData style, String text,
		Object image) {
		RadioButton component = new RadioButton();

		addComponent(component, style, text, image);

		return component;
	}

	/**
	 * Creates a new {@link ScrollPanel}. In the GWT implementation, scroll
	 * panels can only contain a single component. Therefore the returned
	 * container builder must only be used to add a single panel (or other
	 * component) to the scroll panel. For compatibility with other EWT
	 * implementations the same pattern must then be applied when creating
	 * portable code.
	 *
	 * @param style The style data
	 * @return A container builder wrapping the new panel
	 */
	public ContainerBuilder<ScrollPanel> addScrollPanel(StyleData style) {
		ScrollPanel component = new ScrollPanel();

		addComponent(component, style, null, null);

		return new ContainerBuilder<ScrollPanel>(component, this);
	}

	/**
	 * Creates a new {@link Spinner} component.
	 *
	 * @param style     The style data
	 * @param minimum   The minimum input value
	 * @param maximum   The maximum input value
	 * @param increment The increment or decrement for value modifications
	 * @return The new component
	 */
	public Spinner addSpinner(StyleData style, int minimum, int maximum,
		int increment) {
		Spinner spinner = new Spinner();

		addComponent(spinner, style, null, null);

		spinner.setMinimum(minimum);
		spinner.setMaximum(maximum);
		spinner.setIncrement(increment);

		return spinner;
	}

	/**
	 * Creates a new {@link SplitPanel} with a certain layout.
	 *
	 * @param style The style data
	 * @return A container builder wrapping the new panel
	 */
	public ContainerBuilder<SplitPanel> addSplitPanel(StyleData style) {
		SplitPanel component = new SplitPanel(container, style);

		addComponent(component, style, null, null);

		return new ContainerBuilder<SplitPanel>(component, this);
	}

	/**
	 * Creates a new {@link StackPanel} with a certain layout.
	 *
	 * @param style The style data
	 * @return A container builder wrapping the new panel
	 */
	public ContainerBuilder<StackPanel> addStackPanel(StyleData style) {
		StackPanel component = new StackPanel(container, style);

		addComponent(component, style, null, null);

		return new ContainerBuilder<StackPanel>(component, this);
	}

	/**
	 * Creates a new {@link TabPanel}.
	 *
	 * @param style The style data
	 * @return The new component
	 */
	public ContainerBuilder<TabPanel> addTabPanel(StyleData style) {
		TabPanel component = new TabPanel(container, style);

		addComponent(component, style, null, null);

		return new ContainerBuilder<TabPanel>(component, this);
	}

	/**
	 * Creates a new {@link Table}.
	 *
	 * @param style The style data
	 * @return The new component
	 */
	public Table addTable(StyleData style) {
		Table component = new Table();

		addComponent(component, style, null, null);

		return component;
	}

	/**
	 * Creates a new {@link TextArea}.
	 *
	 * @param style The style data
	 * @param text  The initial text
	 * @return The new component
	 */
	public TextArea addTextArea(StyleData style, String text) {
		TextArea component = new TextArea();

		addComponent(component, style, text, null);

		return component;
	}

	/**
	 * Creates a new {@link TextField}.
	 *
	 * @param style The style data
	 * @param text  The initial text
	 * @return The new component
	 */
	public TextField addTextField(StyleData style, String text) {
		TextField component = new TextField();

		addComponent(component, style, text, null);

		return component;
	}

	/**
	 * Creates a new {@link ToggleButton}.
	 *
	 * @param style The style data
	 * @param text  The button text
	 * @param image The button image or NULL for none
	 * @return The new component
	 */
	public ToggleButton addToggleButton(StyleData style, String text,
		Object image) {
		ToggleButton component = new ToggleButton();

		addComponent(component, style, text, image);

		return component;
	}

	/**
	 * Creates a new {@link Tree}.
	 *
	 * @param style The style data
	 * @return The new component
	 */
	public Tree addTree(StyleData style) {
		Tree component = new Tree();

		addComponent(component, style, null, null);

		return component;
	}

	/**
	 * Creates a new {@link TreeTable}.
	 *
	 * @param style The style data
	 * @return The new component
	 */
	public TreeTable addTreeTable(StyleData style) {
		TreeTable component = new TreeTable();

		addComponent(component, style, null, null);

		return component;
	}

	/**
	 * Creates a new {@link Website} component.
	 *
	 * @param style The style data
	 * @param url   The URL of the website to display
	 * @return The new component
	 */
	public Website addWebsite(StyleData style, String url) {
		Website component = new Website();

		addComponent(component, style, null, null);
		component.setText(url);

		return component;
	}

	/**
	 * Returns the container this builder is initialized with.
	 *
	 * @return The container of this instance
	 */
	public final C getContainer() {
		return container;
	}

	/**
	 * Returns the user interface context of this builder's container.
	 *
	 * @return The user interface context of the container
	 */
	public UserInterfaceContext getContext() {
		return container.getContext();
	}

	/**
	 * Returns the parent container builder of this instance. The parent
	 * reference will only be valid for builders that have been created through
	 * the respective parent builder. It allows to access the parent builder
	 * later without the need to keep a separate reference to it.
	 *
	 * @return The parent builder of this instance or NULL for none
	 */
	public ContainerBuilder<?> getParent() {
		return parent;
	}

	/**
	 * Convenience method to remove all components from this builder's
	 * container. See {@link Container#clear()} for details.
	 */
	public void removeAllComponents() {
		container.clear();
	}

	/**
	 * Convenience method to remove a certain component from this builder's
	 * container. See {@link Container#removeComponent(Component)} for details.
	 *
	 * @param component The component to remove
	 */
	public void removeComponent(Component component) {
		container.removeComponent(component);
	}

	/**
	 * Convenience method to set the layout on this builder's container.
	 *
	 * @param layout The new layout of the container
	 */
	public void setLayout(GenericLayout layout) {
		container.setLayout(layout);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return getClass().getSimpleName() + "[" + getContainer() + "]";
	}

	/**
	 * Internal method to add a component to the container.
	 *
	 * @param component The component to add
	 * @param style     The style data for the component
	 * @param text      The component text or NULL for none
	 * @param image     The component image, image resource key, or NULL
	 */
	void addComponent(Component component, StyleData style, String text,
		Object image) {
		component.initWidget(container, style);
		container.internalAddComponent(component, style);

		if (text != null) {
			component.setProperties(text);
		}

		if (image != null) {
			component.setProperties(image);
		}

		if (formLabel != null) {
			formLabel.setAsLabelFor(component);
			formLabel = null;
		}
	}

	/**
	 * Internal method to set the container of this instance.
	 *
	 * @param container The new container
	 */
	void setContainer(C container) {
		this.container = container;
	}

	/**
	 * Internal method to set the parent builder of this instance.
	 *
	 * @param parent The parent builder
	 */
	final void setParent(ContainerBuilder<?> parent) {
		this.parent = parent;
	}
}
