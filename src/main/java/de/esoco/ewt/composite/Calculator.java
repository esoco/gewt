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
package de.esoco.ewt.composite;

import de.esoco.ewt.EWT;
import de.esoco.ewt.build.ContainerBuilder;
import de.esoco.ewt.component.Button;
import de.esoco.ewt.component.Composite;
import de.esoco.ewt.component.FocusableComposite;
import de.esoco.ewt.component.Label;
import de.esoco.ewt.component.Panel;
import de.esoco.ewt.composite.MultiFormatDisplay.NumberDisplayFormat;
import de.esoco.ewt.event.EventType;
import de.esoco.ewt.event.EwtEvent;
import de.esoco.ewt.event.KeyCode;
import de.esoco.ewt.event.ModifierKeys;
import de.esoco.ewt.layout.GridLayout;

import de.esoco.lib.datatype.Pair;
import de.esoco.lib.math.MathUtil;
import de.esoco.lib.text.TextConvert;

import java.math.BigDecimal;
import java.math.RoundingMode;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import com.google.gwt.i18n.client.LocaleInfo;

import static de.esoco.ewt.layout.FlexLayout.flexHorizontal;
import static de.esoco.ewt.layout.GridLayout.grid;
import static de.esoco.ewt.style.StyleData.DEFAULT;

import static de.esoco.lib.property.LayoutProperties.COLUMN;
import static de.esoco.lib.property.LayoutProperties.COLUMN_SPAN;
import static de.esoco.lib.property.LayoutProperties.LAYOUT_AREA;

import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.TEN;
import static java.math.BigDecimal.ZERO;

/**
 * A calculator composite that allows the user to interactively calculate a
 * {@link BigDecimal} value.
 *
 * @author eso
 */
public class Calculator extends FocusableComposite {

	/**
	 * Enumeration of calculator actions.
	 */
	enum BinaryCalculation implements CalculatorFunction,
		BiFunction<BigDecimal, BigDecimal, BigDecimal> {
		ADD("+", 1, '+', BigDecimal::add),
		SUBTRACT("-", 1, '-', BigDecimal::subtract),
		MULTIPLY("×", 2, '*', BigDecimal::multiply), DIVIDE("÷", 2, '/',
			(d1, d2) -> d1
				.divide(d2, 16, RoundingMode.HALF_UP)
				.stripTrailingZeros()),
		MODULO("Mod", 2, '~', (d1, d2) -> d1.remainder(d2)), AND("And", 2, '&',
			(d1, d2) -> new BigDecimal(
				d1.toBigInteger().and(d2.toBigInteger()))), OR("Or", 1, '|',
			(d1, d2) -> new BigDecimal(
				d1.toBigInteger().or(d2.toBigInteger()))), XOR("Xor", 1, '^',
			(d1, d2) -> new BigDecimal(
				d1.toBigInteger().xor(d2.toBigInteger()))), PERCENT("%", 1,
			'%',
			(d1, d2) -> d1.multiply(d2).divide(MathUtil.HUNDRED)),
		LEFT_SHIFT("<<", 1, '<', (d1, d2) -> new BigDecimal(
			d1.toBigInteger().shiftLeft(d2.intValue()))),
		RIGHT_SHIFT(">>", 1, '>', (d1, d2) -> new BigDecimal(
			d1.toBigInteger().shiftRight(d2.intValue())));

		private final String sSymbol;

		private final int nPriority;

		private final BiFunction<BigDecimal, BigDecimal, BigDecimal> fCalc;

		private Pair<ModifierKeys, KeyCode> aKey;

		/**
		 * Creates a new instance.
		 *
		 * @param sSymbol   The function symbol
		 * @param nPriority The priority of this calculation in relation to
		 *                  other calculations
		 * @param cKey      The character of the key to invoke this function
		 * @param fCalc     The calculation function
		 */
		private BinaryCalculation(String sSymbol, int nPriority, char cKey,
			BiFunction<BigDecimal, BigDecimal, BigDecimal> fCalc) {
			this.sSymbol = sSymbol;
			this.nPriority = nPriority;
			this.fCalc = fCalc;

			aKey = key(KeyCode.forChar(cKey));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void accept(CalculatorState rState) {
			rState.addOperation(this);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public BigDecimal apply(BigDecimal dLeft, BigDecimal dRight) {
			return fCalc.apply(dLeft, dRight);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Pair<ModifierKeys, KeyCode> getKey() {
			return aKey;
		}

		/**
		 * Returns the calculation priority (precedence).
		 *
		 * @return The priority
		 */
		public int getPriority() {
			return nPriority;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getSymbol() {
			return sSymbol;
		}
	}

	/**
	 * Enumeration of calculator actions.
	 */
	enum CalculatorAction implements CalculatorFunction {
		BACK("⌫", CalculatorState::backOneDigit, KeyCode.BACKSPACE),
		CLEAR_ALL("C", CalculatorState::clearAll, KeyCode.ESCAPE),
		CLEAR_ENTRY("CE", CalculatorState::clearEntry, ModifierKeys.SHIFT,
			KeyCode.BACKSPACE), FRACTION_INPUT(Calculator.DECIMAL_SEPARATOR,
			CalculatorState::startFractionInput,
			KeyCode.forChar(Calculator.DECIMAL_SEPARATOR.charAt(0))),
		EQUALS("=", CalculatorState::calculate, KeyCode.ENTER);

		private final String sSymbol;

		private final Consumer<CalculatorState> fPerformAction;

		private Pair<ModifierKeys, KeyCode> aKey;

		/**
		 * Creates a new instance.
		 *
		 * @param sSymbol        The action symbol
		 * @param fPerformAction The function to perform this action
		 * @param eKeyCode       The key code for this action
		 */
		private CalculatorAction(String sSymbol,
			Consumer<CalculatorState> fPerformAction, KeyCode eKeyCode) {
			this(sSymbol, fPerformAction, ModifierKeys.NONE, eKeyCode);
		}

		/**
		 * Creates a new instance.
		 *
		 * @param sSymbol        The action symbol
		 * @param fPerformAction The function to perform this action
		 * @param rModifiers     The modifier keys for the key code
		 * @param eKeyCode       The key code for this action
		 */
		private CalculatorAction(String sSymbol,
			Consumer<CalculatorState> fPerformAction, ModifierKeys rModifiers,
			KeyCode eKeyCode) {
			this.sSymbol = sSymbol;
			this.fPerformAction = fPerformAction;

			aKey = key(rModifiers, eKeyCode);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void accept(CalculatorState rState) {
			fPerformAction.accept(rState);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Pair<ModifierKeys, KeyCode> getKey() {
			return aKey;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getSymbol() {
			return sSymbol;
		}
	}

	/**
	 * Enumeration of calculator memory manipulation functions. All instances
	 * implement a binary function that take the current display and memory
	 * values and return a pair that contains these value (in that order) after
	 * the function execution.
	 */
	enum MemoryFunction implements CalculatorFunction,
		BiFunction<BigDecimal, BigDecimal, Pair<BigDecimal, BigDecimal>> {
		MEMORY_EXCHANCE("MX", (v, m) -> Pair.of(m, v)),
		MEMORY_CLEAR("MC", (v, m) -> Pair.of(v, ZERO)),
		MEMORY_RECALL("MR", (v, m) -> Pair.of(m, m)),
		MEMORY_STORE("MS", (v, m) -> Pair.of(v, v)),
		MEMORY_ADD("M+", (v, m) -> Pair.of(v, m.add(v))),
		MEMORY_SUBTRACT("M-", (v, m) -> Pair.of(v, m.subtract(v)));

		private final String sSymbol;

		private final BiFunction<BigDecimal, BigDecimal, Pair<BigDecimal,
			BigDecimal>>
			fMemory;

		/**
		 * Creates a new instance.
		 *
		 * @param sSymbol The function symbol
		 * @param fMemory The function that performs the actual memory function
		 */
		private MemoryFunction(String sSymbol,
			BiFunction<BigDecimal, BigDecimal, Pair<BigDecimal, BigDecimal>> fMemory) {
			this.sSymbol = sSymbol;
			this.fMemory = fMemory;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void accept(CalculatorState rState) {
			Pair<BigDecimal, BigDecimal> aResult =
				fMemory.apply(rState.dCurrentValue, rState.dMemoryValue);

			rState.updateValues(aResult.first(), aResult.second(), true);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Pair<BigDecimal, BigDecimal> apply(BigDecimal dValue,
			BigDecimal dMemory) {
			return fMemory.apply(dValue, dMemory);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getSymbol() {
			return sSymbol;
		}
	}

	/**
	 * Enumeration of calculator actions.
	 */
	enum UnaryCalculation implements CalculatorFunction {
		INVERT("¹/x",
			d -> ONE.divide(d, 16, RoundingMode.HALF_UP).stripTrailingZeros()),
		SIGN("±", BigDecimal::negate), SQUARE("x²", d -> d.multiply(d)),
		SQUARE_ROOT("√", MathUtil::sqrt),
		NOT("Not", d -> new BigDecimal(d.toBigInteger().not()));

		private final String sSymbol;

		private Function<BigDecimal, BigDecimal> fCalc;

		/**
		 * Creates a new instance.
		 *
		 * @param sSymbol The function symbol
		 * @param fCalc   The calculation function
		 */
		private UnaryCalculation(String sSymbol,
			Function<BigDecimal, BigDecimal> fCalc) {
			this.sSymbol = sSymbol;
			this.fCalc = fCalc;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void accept(CalculatorState rState) {
			rState.updateValues(fCalc.apply(rState.dCurrentValue),
				rState.dMemoryValue, this != SIGN);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getSymbol() {
			return sSymbol;
		}
	}

	private static final String DECIMAL_SEPARATOR =
		LocaleInfo.getCurrentLocale().getNumberConstants().decimalSeparator();

	private static final GridLayout STANDARD_LAYOUT =
		grid().columns("repeat(4, 1fr)");

	private static CalculatorFunction[][] STANDARD_KEYS =
		new CalculatorFunction[][] {
			{ MemoryFunction.MEMORY_CLEAR, MemoryFunction.MEMORY_STORE,
				MemoryFunction.MEMORY_RECALL, MemoryFunction.MEMORY_EXCHANCE },
			{ BinaryCalculation.PERCENT, UnaryCalculation.SQUARE_ROOT,
				UnaryCalculation.SQUARE, UnaryCalculation.INVERT },
			{ CalculatorAction.CLEAR_ALL, CalculatorAction.CLEAR_ENTRY,
				CalculatorAction.BACK, BinaryCalculation.DIVIDE },
			{ digit('7'), digit('8'), digit('9'), BinaryCalculation.MULTIPLY },
			{ digit('4'), digit('5'), digit('6'), BinaryCalculation.SUBTRACT },
			{ digit('1'), digit('2'), digit('3'), BinaryCalculation.ADD },
			{ UnaryCalculation.SIGN, digit('0'),
				CalculatorAction.FRACTION_INPUT, CalculatorAction.EQUALS } };

//	private static final GridLayout DEVELOPER_LAYOUT =
//		grid().columns("repeat(6, 1fr)");
//
//	private static CalculatorFunction[][] DEVELOPER_KEYS =
//		new CalculatorFunction[][]
//		{
//			{
//				MemoryFunction.MEMORY_CLEAR, MemoryFunction.MEMORY_STORE,
//				MemoryFunction.MEMORY_RECALL, MemoryFunction.MEMORY_EXCHANCE
//			},
//			{
//				BinaryCalculation.PERCENT, UnaryCalculation.SQUARE_ROOT,
//				UnaryCalculation.SQUARE, UnaryCalculation.INVERT
//			},
//			{
//				CalculatorAction.CLEAR_ALL, CalculatorAction.CLEAR_ENTRY,
//				CalculatorAction.BACK, BinaryCalculation.DIVIDE
//			},
//			{
//				digit('E'), digit('F'), digit('7'), digit('8'), digit('9'),
//				BinaryCalculation.MULTIPLY
//			},
//			{
//				digit('C'), digit('D'), digit('4'), digit('5'), digit('6'),
//				BinaryCalculation.SUBTRACT
//			},
//			{
//				digit('A'), digit('B'), digit('1'), digit('2'), digit('3'),
//				BinaryCalculation.ADD
//			},
//			{
//				UnaryCalculation.SIGN, digit('0'),
//				CalculatorAction.FRACTION_INPUT, CalculatorAction.EQUALS
//			}
//		};
//

	private CalculatorState aState = new CalculatorState();

	private CalculatorDisplay aDisplay;

	private Map<Pair<ModifierKeys, KeyCode>, CalculatorFunction> aFunctionKeys =
		new HashMap<>();

	private boolean bKeyHandled = false;

	/**
	 * Creates a new instance.
	 */
	public Calculator() {
		super(STANDARD_LAYOUT);
	}

	/**
	 * Returns a new {@link CalculatorDigit} function for calculator input.
	 *
	 * @param cDigit The digit character
	 * @return The new input function
	 */
	static CalculatorDigit digit(char cDigit) {
		return new CalculatorDigit(cDigit);
	}

	/**
	 * Returns the currentValue value.
	 *
	 * @return The currentValue value
	 */
	public final BigDecimal getValue() {
		return aState.dCurrentValue;
	}

	/**
	 * Sets the value of this calculator.
	 *
	 * @param dValue The new value
	 */
	public final void setValue(BigDecimal dValue) {
		aState.dCurrentValue = dValue;
		update(true);
	}

	/**
	 * Builds this calculator panel with the given builder.
	 *
	 * @param rBuilder The builder to create the panel with
	 */
	@Override
	protected void build(ContainerBuilder<?> rBuilder) {
		addStyleName(EWT.CSS.ewtCalculator());

		addEventListener(EventType.KEY_TYPED, this::handleKey);
		addEventListener(EventType.KEY_RELEASED, this::handleKey);

		aDisplay = rBuilder.addComponent(new CalculatorDisplay(),
			DEFAULT.set(COLUMN, 1).set(COLUMN_SPAN, 4));

		for (CalculatorFunction[] rRow : STANDARD_KEYS) {
			for (CalculatorFunction rFunction : rRow) {
				addFunctionButton(rBuilder, rFunction);
			}
		}

		update(true);
	}

	/**
	 * Adds a button for a function with a container builder.
	 *
	 * @param rBuilder  The container builder
	 * @param rFunction rLabels The button labels
	 */
	void addFunctionButton(ContainerBuilder<?> rBuilder,
		CalculatorFunction rFunction) {
		Button aButton = rBuilder.addButton(DEFAULT, rFunction.getSymbol());

		aButton.addStyleName(rFunction.getClass().getSimpleName());

		if (rFunction.getClass() != CalculatorDigit.class) {
			aButton.addStyleName(
				TextConvert.capitalizedIdentifier(rFunction.toString()));
		}

		aButton.addEventListener(EventType.ACTION,
			e -> rFunction.accept(aState));

		aFunctionKeys.put(rFunction.getKey(), rFunction);
	}

	/**
	 * Copies the current value as a string in the active display format to the
	 * system clipboard.
	 */
	void copyCurrentValueToClipboard() {
		EWT.copyTextToClipboard(aDisplay.aValue.getActiveValue());
		requestFocus();
	}

	/**
	 * Handles all keyboard input events.
	 *
	 * @param rEvent The keyboard event
	 */
	void handleKey(EwtEvent rEvent) {
		EventType eEventType = rEvent.getType();

		if (rEvent.getModifiers() == ModifierKeys.CTRL) {
			if (rEvent.getKeyCode() == KeyCode.C) {
				copyCurrentValueToClipboard();
			}

			bKeyHandled = true;
		} else if (!bKeyHandled || eEventType == EventType.KEY_TYPED) {
			Pair<ModifierKeys, KeyCode> aKey =
				Pair.of(rEvent.getModifiers(), rEvent.getKeyCode());

			CalculatorFunction rFunction = aFunctionKeys.get(aKey);

			if (rFunction != null) {
				rFunction.accept(aState);
				bKeyHandled = (eEventType == EventType.KEY_TYPED);
			}
		} else {
			bKeyHandled = false;
		}
	}

	/**
	 * Updates the display text field with the current value.
	 *
	 * @param bReset TRUE to reset all input parameters
	 */
	void update(boolean bReset) {
		if (bReset) {
			aState.dInputDigit = ONE;
			aState.bFractionInput = false;
			aState.bEnterNewValue = true;
		}

		if (ZERO.equals(aState.dCurrentValue)) {
			// remove a possible negative sign on a zero value
			aState.dCurrentValue = ZERO;
		}

		aDisplay.update(aState);
	}

	/**
	 * An interface for all calculator function enums.
	 *
	 * @author eso
	 */
	static interface CalculatorFunction extends Consumer<CalculatorState> {

		/**
		 * Default value of {@link #getKey()}.
		 */
		public static final Pair<ModifierKeys, KeyCode> NO_KEY =
			Pair.of(ModifierKeys.NONE, KeyCode.NONE);

		/**
		 * Returns the key combination that can be pressed to invoke this
		 * function. The default implementation returns {@link #NO_KEY}.
		 *
		 * @return The function key code
		 */
		default Pair<ModifierKeys, KeyCode> getKey() {
			return NO_KEY;
		}

		/**
		 * The symbol string to be displayed for this function.
		 *
		 * @return The symbol string
		 */
		String getSymbol();

		/**
		 * A helper method for implementations that returns a key definition
		 * for
		 * a single key that needs to be pressed without modifiers.
		 *
		 * @param rKey The key code
		 * @return The key combination pair
		 */
		default Pair<ModifierKeys, KeyCode> key(KeyCode rKey) {
			return key(ModifierKeys.NONE, rKey);
		}

		/**
		 * A helper method for implementations that returns a key definition
		 * for
		 * key combinations.
		 *
		 * @param rModifiers The modifier keys
		 * @param rKey       The key code
		 * @return The key combination pair
		 */
		default Pair<ModifierKeys, KeyCode> key(ModifierKeys rModifiers,
			KeyCode rKey) {
			return Pair.of(rModifiers, rKey);
		}
	}

	/**
	 * A calculator function for the input of digits.
	 *
	 * @author eso
	 */
	static class CalculatorDigit implements CalculatorFunction {

		private final char cDigit;

		private final Pair<ModifierKeys, KeyCode> aKey;

		/**
		 * Creates a new instance.
		 *
		 * @param cDigit sSymbol The input digit
		 */
		public CalculatorDigit(char cDigit) {
			this.cDigit = cDigit;
			aKey = key(KeyCode.forChar(cDigit));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void accept(CalculatorState rState) {
			rState.input(cDigit - '0');
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Pair<ModifierKeys, KeyCode> getKey() {
			return aKey;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getSymbol() {
			return Character.toString(cDigit);
		}
	}

	/**
	 * Inner class to encapsulate a mathematical operation.
	 */
	static class Operation {

		private BinaryCalculation eCalculation;

		private BigDecimal rLeftValue;

		/**
		 * Creates a new instance with a certain operator and left value for
		 * the
		 * operation.
		 *
		 * @param eCalculation The operator character
		 * @param rLeftValue   The left value for the operation
		 */
		Operation(BinaryCalculation eCalculation, BigDecimal rLeftValue) {
			this.eCalculation = eCalculation;
			this.rLeftValue = rLeftValue;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return rLeftValue.toString() + " " + eCalculation.getSymbol() +
				" ";
		}

		/**
		 * Executes this operation with a right value and returns the result.
		 *
		 * @param rRightValue The right value to perform this operation with
		 * @return The result of performing this operation with the left and
		 * right values
		 */
		BigDecimal execute(BigDecimal rRightValue) {
			return eCalculation.apply(rLeftValue, rRightValue);
		}

		/**
		 * Returns the left value for this operations.
		 *
		 * @return The left value
		 */
		final BigDecimal getLeftValue() {
			return rLeftValue;
		}

		/**
		 * Returns the priority of this operation's operator.
		 *
		 * @return The operator priority
		 */
		final int getPriority() {
			return eCalculation.nPriority;
		}
	}

	/**
	 * A composite that contains the components of the calculator display.
	 *
	 * @author eso
	 */
	class CalculatorDisplay extends Composite {

		private Label aOperationsChain;

		private Label aStateIndicator;

		private MultiFormatDisplay<BigDecimal, NumberDisplayFormat> aValue;

		/**
		 * Creates a new instance.
		 */
		protected CalculatorDisplay() {
			super(grid("auto 1fr").areas("operations operations",
				"state " + "value"));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void build(ContainerBuilder<?> rBuilder) {
			addStyleName("CalculatorDisplay");

			ContainerBuilder<Panel> aOperationsBuilder =
				rBuilder.addPanel(DEFAULT.set(LAYOUT_AREA, "operations"),
					flexHorizontal());

			aOperationsChain = aOperationsBuilder.addLabel(DEFAULT, "");
			aOperationsBuilder
				.addButton(DEFAULT, "@$CalcCopyButton")
				.addEventListener(EventType.ACTION,
					e -> copyCurrentValueToClipboard());
			aStateIndicator =
				rBuilder.addLabel(DEFAULT.set(LAYOUT_AREA, "state"), "");

			aOperationsChain.setWidth("100%");
			aOperationsChain.addStyleName("calcOps");
			aStateIndicator.addStyleName("calcState");

			aValue = new MultiFormatDisplay<>(NumberDisplayFormat.DECIMAL,
				NumberDisplayFormat.HEXADECIMAL, NumberDisplayFormat.BINARY);
			rBuilder.addComponent(aValue, DEFAULT);
			aValue.addStyleName("CalculatorValue");
		}

		/**
		 * Updates the display based on the given state.
		 *
		 * @param rState The state to update from
		 */
		void update(CalculatorState rState) {
			StringBuilder aOperations = new StringBuilder();

			for (Operation rOperation : rState.aOperationsStack) {
				aOperations.append(rOperation);
			}

			aOperationsChain.setText(aOperations.toString());
			aStateIndicator.setText(rState.dMemoryValue == ZERO ? "" : "M");
			aValue.update(rState.dCurrentValue);
		}
	}

	/**
	 * Encapsulates the current calculator state.
	 *
	 * @author eso
	 */
	class CalculatorState {

		private BigDecimal dCurrentValue = ZERO;

		private BigDecimal dMemoryValue = ZERO;

		private BigDecimal dInputDigit = ONE;

		private boolean bFractionInput;

		private boolean bEnterNewValue;

		private Stack<Operation> aOperationsStack = new Stack<Operation>();

		/**
		 * Adds a new operation to the stack.
		 *
		 * @param eCalculation The calculation to be performed by the
		 *                     operation.
		 */
		void addOperation(BinaryCalculation eCalculation) {
			dCurrentValue =
				executeOperations(dCurrentValue, eCalculation.getPriority());

			Operation aOperation = new Operation(eCalculation, dCurrentValue);

			aOperationsStack.push(aOperation);

			update(true);
		}

		/**
		 * Removes one digit from the current input.
		 */
		void backOneDigit() {
			if (!bEnterNewValue) {
				if (bFractionInput) {
					int nScale = dCurrentValue.scale();

					if (nScale > 0) {
						dInputDigit = dInputDigit.multiply(TEN);
						dCurrentValue = dCurrentValue.setScale(--nScale,
							RoundingMode.FLOOR);
					}

					if (nScale == 0) {
						bFractionInput = false;
						dInputDigit = ONE;
					}
				} else {
					dCurrentValue =
						dCurrentValue.divide(TEN, RoundingMode.FLOOR);
				}
			}

			update(false);
		}

		/**
		 * Calculates the current operations stack and resets for the input
		 * of a
		 * new value.
		 */
		void calculate() {
			dCurrentValue = executeOperations(dCurrentValue, 0);
			update(true);
		}

		/**
		 * Clears the complete operations stack and displayed values (but not
		 * the memory).
		 */
		void clearAll() {
			aOperationsStack.removeAllElements();
			clearEntry();
		}

		/**
		 * Clears the currently entered value.
		 */
		void clearEntry() {
			dCurrentValue = ZERO;
			update(true);
		}

		/**
		 * Executes the topmost operations on the operation stack that have a
		 * certain minimum priority, starting with the given right value and
		 * returning the resulting value.
		 *
		 * @param dRightValue  The right value to calculate with
		 * @param nMinPriority The minimum priority an operation must have
		 */
		BigDecimal executeOperations(BigDecimal dRightValue,
			int nMinPriority) {
			while (!aOperationsStack.isEmpty() &&
				aOperationsStack.peek().getPriority() >= nMinPriority) {
				dRightValue = aOperationsStack.pop().execute(dRightValue);
			}

			update(true);

			return dRightValue;
		}

		/**
		 * Performs the input of a single digit.
		 *
		 * @param nDigit The digit value
		 */
		void input(int nDigit) {
			BigDecimal aDigit = new BigDecimal(nDigit);

			if (bEnterNewValue) {
				dCurrentValue = ZERO;
				bEnterNewValue = false;
			}

			if (bFractionInput) {
				dInputDigit = dInputDigit.divide(TEN);
				dCurrentValue =
					dCurrentValue.add(dInputDigit.multiply(aDigit));
			} else {
				dCurrentValue = dCurrentValue.multiply(TEN).add(aDigit);
			}

			update(false);
		}

		/**
		 * Updates this state to perform the input of fraction digits.
		 */
		void startFractionInput() {
			bFractionInput = true;

			update(false);
		}

		/**
		 * Updates the current and memory values.
		 *
		 * @param dCurrent The new current value
		 * @param dMemory  The new memory value
		 * @param bReset   TRUE to reset all input parameters
		 */
		void updateValues(BigDecimal dCurrent, BigDecimal dMemory,
			boolean bReset) {
			dCurrentValue = dCurrent;
			dMemoryValue = dMemory;

			update(bReset);
		}
	}
}
