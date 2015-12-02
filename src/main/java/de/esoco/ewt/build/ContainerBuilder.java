//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// This file is a part of the 'GEWT' project.
// Copyright 2015 Elmar Sonnenschein, esoco GmbH, Flensburg, Germany
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
package de.esoco.ewt.build;

import de.esoco.ewt.UserInterfaceContext;
import de.esoco.ewt.component.Button;
import de.esoco.ewt.component.Calendar;
import de.esoco.ewt.component.CheckBox;
import de.esoco.ewt.component.ComboBox;
import de.esoco.ewt.component.Component;
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
import de.esoco.ewt.style.StyleFlag;

import java.util.Date;


/********************************************************************
 * A container builder implementation that provides methods to create components
 * and add them to a container.
 *
 * @author eso
 */
public
class ContainerBuilder<C extends Container>
{
	private C                   rContainer;
	private ContainerBuilder<?> rParent = null;

	/***************************************
	 * Creates a new instance that builds in a certain container.
	 *
	 * @param rContainer The container builder
	 */
	public ContainerBuilder(C rContainer)
	{
		this.rContainer = rContainer;
	}

	/***************************************
	 * Internal constructor to create a new instance with a certain parent
	 * builder.
	 *
	 * @param rContainer The container to build components in
	 * @param rParent    The parent container builder
	 */
	ContainerBuilder(C rContainer, ContainerBuilder<?> rParent)
	{
		this(rContainer);

		this.rParent = rParent;
	}

	/***************************************
	 * Creates a new {@link Button}.
	 *
	 * @param  rStyleData The style data
	 * @param  sText      The button text
	 * @param  rImage     The button image or NULL for none
	 *
	 * @return The new component
	 */
	public Button addButton(StyleData rStyleData, String sText, Object rImage)
	{
		Button aComponent = new Button();

		addComponent(aComponent, rStyleData, sText, rImage);

		return aComponent;
	}

	/***************************************
	 * Creates a new {@link Calendar} component.
	 *
	 * @param  rStyle The style data
	 * @param  rDate  The initial date of the component
	 *
	 * @return The new component
	 */
	public Calendar addCalendar(StyleData rStyle, Date rDate)
	{
		Calendar aComponent = new Calendar(rContainer.getContext(), rStyle.hasFlag(StyleFlag.DATE_TIME));

		aComponent.setDate(rDate);
		addComponent(aComponent, rStyle, null, null);

		return aComponent;
	}

	/***************************************
	 * Creates a new {@link CheckBox}.
	 *
	 * @param  rStyleData The style data
	 * @param  sText      The check box text
	 * @param  rImage     The button image or NULL for none
	 *
	 * @return The new component
	 */
	public CheckBox addCheckBox(StyleData rStyleData, String sText,
	                            Object rImage)
	{
		CheckBox aComponent = new CheckBox();

		addComponent(aComponent, rStyleData, sText, rImage);

		return aComponent;
	}

	/***************************************
	 * Creates a new {@link ComboBox}.
	 *
	 * @param  rStyleData The style data
	 * @param  sText      The initial text of the combo box text field
	 *
	 * @return The new component
	 */
	public ComboBox addComboBox(StyleData rStyleData, String sText)
	{
		ComboBox aComponent = new ComboBox(rStyleData);

		addComponent(aComponent, rStyleData, sText, null);

		return aComponent;
	}

	/***************************************
	 * Creates a new {@link DateField}.
	 *
	 * @param  rStyle The style data
	 * @param  rDate  The initial date of the component
	 *
	 * @return The new component
	 */
	public DateField addDateField(StyleData rStyle, Date rDate)
	{
		DateField aComponent = new DateField(getContext(), rStyle.hasFlag(StyleFlag.DATE_TIME));

		aComponent.setDate(rDate);
		addComponent(aComponent, rStyle, null, null);

		return aComponent;
	}

	/***************************************
	 * Creates a new {@link DeckPanel}.
	 *
	 * @param  rStyleData The style data
	 *
	 * @return The new component
	 */
	public ContainerBuilder<DeckPanel> addDeckPanel(StyleData rStyleData)
	{
		DeckPanel aComponent = new DeckPanel();

		addComponent(aComponent, rStyleData, null, null);

		return new ContainerBuilder<DeckPanel>(aComponent, this);
	}

	/***************************************
	 * Creates a new {@link FileChooser}.
	 *
	 * @param  rStyleData  The style data
	 * @param  sAction     The identifying name of the file chooser
	 * @param  sButtonText The text for the action button of the file chooser
	 *
	 * @return The new component
	 */
	public FileChooser addFileChooser(StyleData rStyleData, String sAction,
	                                  String sButtonText)
	{
		FileChooser aComponent = new FileChooser(sAction);

		addComponent(aComponent, rStyleData, sButtonText, null);

		return aComponent;
	}

	/***************************************
	 * Creates a new {@link Label}.
	 *
	 * @param  rStyleData The style data
	 * @param  sText      The label text
	 * @param  rImage     The label image or NULL for none
	 *
	 * @return The new component
	 */
	public Label addLabel(StyleData rStyleData, String sText, Object rImage)
	{
		Label aComponent = new Label(rStyleData);

		addComponent(aComponent, rStyleData, sText, rImage);

		return aComponent;
	}

	/***************************************
	 * Creates a new {@link List}.
	 *
	 * @param  rStyleData The style data
	 *
	 * @return The new component
	 */
	public List addList(StyleData rStyleData)
	{
		List aComponent = new List(rStyleData);

		addComponent(aComponent, rStyleData, null, null);

		return aComponent;
	}

	/***************************************
	 * Creates a new {@link ListBox}.
	 *
	 * @param  rStyleData The style data
	 *
	 * @return The new component
	 */
	public ListBox addListBox(StyleData rStyleData)
	{
		ListBox aComponent = new ListBox(rStyleData);

		addComponent(aComponent, rStyleData, null, null);

		return aComponent;
	}

	/***************************************
	 * Creates a {@link Panel} with the default layout
	 * {@link EdgeLayout#NO_GAP_LAYOUT} and returns a container builder for the
	 * new panel.
	 *
	 * @param  rStyleData The style data
	 *
	 * @return A container builder wrapping the new panel
	 */
	public ContainerBuilder<Panel> addPanel(StyleData rStyleData)
	{
		return addPanel(rStyleData, new EdgeLayout(0));
	}

	/***************************************
	 * Creates a new {@link Panel} with a certain layout and returns a container
	 * builder for the new panel.
	 *
	 * @param  rStyleData The style data
	 * @param  rLayout    The panel layout
	 *
	 * @return A container builder wrapping the new panel
	 */
	public ContainerBuilder<Panel> addPanel(StyleData rStyleData,
	                                        GenericLayout rLayout)
	{
		Panel aComponent = new Panel(rLayout);

		addComponent(aComponent, rStyleData, null, null);

		return new ContainerBuilder<Panel>(aComponent, this);
	}

	/***************************************
	 * Creates a new {@link ProgressBar}.
	 *
	 * @param  rStyleData The style data
	 *
	 * @return The new component
	 */
	public ProgressBar addProgressBar(StyleData rStyleData)
	{
		ProgressBar aComponent = new ProgressBar();

		addComponent(aComponent, rStyleData, null, null);

		return aComponent;
	}

	/***************************************
	 * Creates a new {@link RadioButton}.
	 *
	 * @param  rStyleData The style data
	 * @param  sText      The button text
	 * @param  rImage     The button image or NULL for none
	 *
	 * @return The new component
	 */
	public RadioButton addRadioButton(StyleData rStyleData, String sText,
	                                  Object rImage)
	{
		RadioButton aComponent = new RadioButton();

		addComponent(aComponent, rStyleData, sText, rImage);

		return aComponent;
	}

	/***************************************
	 * Creates a new {@link ScrollPanel}. In the GWT implementation, scroll
	 * panels can only contain a single component. Therefore the returned
	 * container builder must only be used to add a single panel (or other
	 * component) to the scroll panel. For compatibility with other EWT
	 * implementations the same pattern must then be applied when creating
	 * portable code.
	 *
	 * @param  rStyleData The style data
	 *
	 * @return A container builder wrapping the new panel
	 */
	public ContainerBuilder<ScrollPanel> addScrollPanel(StyleData rStyleData)
	{
		ScrollPanel aComponent = new ScrollPanel();

		addComponent(aComponent, rStyleData, null, null);

		return new ContainerBuilder<ScrollPanel>(aComponent, this);
	}

	/***************************************
	 * Creates a new {@link Spinner} component.
	 *
	 * @param  rStyleData The style data
	 * @param  nMinimum   The minimum input value
	 * @param  nMaximum   The maximum input value
	 * @param  nIncrement The increment or decrement for value modifications
	 *
	 * @return The new component
	 */
	public Spinner addSpinner(StyleData rStyleData, int nMinimum, int nMaximum,
	                          int nIncrement)
	{
		Spinner aComponent = new Spinner(nMinimum, nMaximum, nIncrement);

		addComponent(aComponent, rStyleData, null, null);

		return aComponent;
	}

	/***************************************
	 * Creates a new {@link SplitPanel} with a certain layout.
	 *
	 * @param  rStyleData The style data
	 *
	 * @return A container builder wrapping the new panel
	 */
	public ContainerBuilder<SplitPanel> addSplitPanel(StyleData rStyleData)
	{
		SplitPanel aComponent = new SplitPanel();

		addComponent(aComponent, rStyleData, null, null);

		return new ContainerBuilder<SplitPanel>(aComponent, this);
	}

	/***************************************
	 * Creates a new {@link StackPanel} with a certain layout.
	 *
	 * @param  rStyleData The style data
	 *
	 * @return A container builder wrapping the new panel
	 */
	public ContainerBuilder<StackPanel> addStackPanel(StyleData rStyleData)
	{
		StackPanel aComponent = new StackPanel();

		addComponent(aComponent, rStyleData, null, null);

		return new ContainerBuilder<StackPanel>(aComponent, this);
	}

	/***************************************
	 * Creates a new {@link Table}.
	 *
	 * @param  rStyleData The style data
	 *
	 * @return The new component
	 */
	public Table addTable(StyleData rStyleData)
	{
		Table aComponent = new Table();

		addComponent(aComponent, rStyleData, null, null);

		return aComponent;
	}

	/***************************************
	 * Creates a new {@link TabPanel}.
	 *
	 * @param  rStyleData The style data
	 *
	 * @return The new component
	 */
	public ContainerBuilder<TabPanel> addTabPanel(StyleData rStyleData)
	{
		TabPanel aComponent = new TabPanel();

		addComponent(aComponent, rStyleData, null, null);

		return new ContainerBuilder<TabPanel>(aComponent, this);
	}

	/***************************************
	 * Creates a new {@link TextArea}.
	 *
	 * @param  rStyleData The style data
	 * @param  sText      The initial text
	 *
	 * @return The new component
	 */
	public TextArea addTextArea(StyleData rStyleData, String sText)
	{
		TextArea aComponent = new TextArea(rStyleData);

		addComponent(aComponent, rStyleData, sText, null);

		return aComponent;
	}

	/***************************************
	 * Creates a new {@link TextField}.
	 *
	 * @param  rStyleData The style data
	 * @param  sText      The initial text
	 *
	 * @return The new component
	 */
	public TextField addTextField(StyleData rStyleData, String sText)
	{
		TextField aComponent = new TextField(rStyleData.hasFlag(StyleFlag.PASSWORD));

		addComponent(aComponent, rStyleData, sText, null);

		return aComponent;
	}

	/***************************************
	 * Creates a new {@link ToggleButton}.
	 *
	 * @param  rStyleData The style data
	 * @param  sText      The button text
	 * @param  rImage     The button image or NULL for none
	 *
	 * @return The new component
	 */
	public ToggleButton addToggleButton(StyleData rStyleData, String sText,
	                                    Object rImage)
	{
		ToggleButton aComponent = new ToggleButton();

		addComponent(aComponent, rStyleData, sText, rImage);

		return aComponent;
	}

	/***************************************
	 * Creates a new {@link Tree}.
	 *
	 * @param  rStyleData The style data
	 *
	 * @return The new component
	 */
	public Tree addTree(StyleData rStyleData)
	{
		Tree aComponent = new Tree();

		addComponent(aComponent, rStyleData, null, null);

		return aComponent;
	}

	/***************************************
	 * Creates a new {@link TreeTable}.
	 *
	 * @param  rStyleData The style data
	 *
	 * @return The new component
	 */
	public TreeTable addTreeTable(StyleData rStyleData)
	{
		TreeTable aComponent = new TreeTable();

		addComponent(aComponent, rStyleData, null, null);

		return aComponent;
	}

	/***************************************
	 * Creates a new {@link Website} component.
	 *
	 * @param  rStyleData The style data
	 * @param  sUrl       The URL of the website to display
	 *
	 * @return The new component
	 */
	public Website addWebsite(StyleData rStyleData, String sUrl)
	{
		Website aComponent = new Website(sUrl);

		addComponent(aComponent, rStyleData, null, null);

		return aComponent;
	}

	/***************************************
	 * Returns the container this builder is initialized with.
	 *
	 * @return The container of this instance
	 */
	public final C getContainer()
	{
		return rContainer;
	}

	/***************************************
	 * Returns the user interface context of this builder's container.
	 *
	 * @return The user interface context of the container
	 */
	public UserInterfaceContext getContext()
	{
		return rContainer.getContext();
	}

	/***************************************
	 * Returns the parent container builder of this instance. The parent
	 * reference will only be valid for builders that have been created through
	 * the respective parent builder. It allows to access the parent builder
	 * later without the need to keep a separate reference to it.
	 *
	 * @return The parent builder of this instance or NULL for none
	 */
	public ContainerBuilder<?> getParent()
	{
		return rParent;
	}

	/***************************************
	 * Convenience method to remove a certain component from this builder's
	 * container. See {@link Container#removeComponent(Component)} for details.
	 *
	 * @param rComponent The component to remove
	 */
	public void removeComponent(Component rComponent)
	{
		rContainer.removeComponent(rComponent);
	}

	/***************************************
	 * Convenience method to set the layout on this builder's container.
	 *
	 * @param rLayout The new layout of the container
	 */
	public void setLayout(GenericLayout rLayout)
	{
		rContainer.setLayout(rLayout);
	}

	/***************************************
	 * {@inheritDoc}
	 */
	@Override
	public String toString()
	{
		return getClass().getSimpleName() + "[" + getContainer() + "]";
	}

	/***************************************
	 * Internal method to add a component to the container.
	 *
	 * @param rComponent The component to add
	 * @param rStyleData The style data for the component
	 * @param sText      The component text or NULL for none
	 * @param rImage     The component image, image resource key, or NULL
	 */
	void addComponent(Component rComponent, StyleData rStyleData, String sText,
	                  Object rImage)
	{
		rContainer.internalAddComponent(rComponent, rStyleData);

		if (sText != null)
		{
			rComponent.setProperties(sText);
		}

		if (rImage != null)
		{
			rComponent.setProperties(rImage);
		}
	}

	/***************************************
	 * Internal method to set the container of this instance.
	 *
	 * @param rContainer The new container
	 */
	void setContainer(C rContainer)
	{
		this.rContainer = rContainer;
	}

	/***************************************
	 * Internal method to set the parent builder of this instance.
	 *
	 * @param rParent The parent builder
	 */
	final void setParent(ContainerBuilder<?> rParent)
	{
		this.rParent = rParent;
	}
}
