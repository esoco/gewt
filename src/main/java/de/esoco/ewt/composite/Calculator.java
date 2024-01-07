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

import com.google.gwt.i18n.client.LocaleInfo;
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
import java.util.Objects;
import java.util.Stack;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

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

		private final String symbol;

		private final int priority;

		private final BiFunction<BigDecimal, BigDecimal, BigDecimal> calc;

		private final Pair<ModifierKeys, KeyCode> keyCode;

		/**
		 * Creates a new instance.
		 *
		 * @param symbol   The function symbol
		 * @param priority The priority of this calculation in relation to
		 *                       other
		 *                 calculations
		 * @param key      The character of the key to invoke this function
		 * @param calc     The calculation function
		 */
		BinaryCalculation(String symbol, int priority, char key,
			BiFunction<BigDecimal, BigDecimal, BigDecimal> calc) {
			this.symbol = symbol;
			this.priority = priority;
			this.calc = calc;

			keyCode = key(KeyCode.forChar(key));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void accept(CalculatorState state) {
			state.addOperation(this);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public BigDecimal apply(BigDecimal left, BigDecimal right) {
			return calc.apply(left, right);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Pair<ModifierKeys, KeyCode> getKey() {
			return keyCode;
		}

		/**
		 * Returns the calculation priority (precedence).
		 *
		 * @return The priority
		 */
		public int getPriority() {
			return priority;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getSymbol() {
			return symbol;
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

		private final String symbol;

		private final Consumer<CalculatorState> performAction;

		private final Pair<ModifierKeys, KeyCode> key;

		/**
		 * Creates a new instance.
		 *
		 * @param symbol        The action symbol
		 * @param performAction The function to perform this action
		 * @param keyCode       The key code for this action
		 */
		CalculatorAction(String symbol,
			Consumer<CalculatorState> performAction,
			KeyCode keyCode) {
			this(symbol, performAction, ModifierKeys.NONE, keyCode);
		}

		/**
		 * Creates a new instance.
		 *
		 * @param symbol        The action symbol
		 * @param performAction The function to perform this action
		 * @param modifiers     The modifier keys for the key code
		 * @param keyCode       The key code for this action
		 */
		CalculatorAction(String symbol,
			Consumer<CalculatorState> performAction,
			ModifierKeys modifiers, KeyCode keyCode) {
			this.symbol = symbol;
			this.performAction = performAction;

			key = key(modifiers, keyCode);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void accept(CalculatorState state) {
			performAction.accept(state);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Pair<ModifierKeys, KeyCode> getKey() {
			return key;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getSymbol() {
			return symbol;
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

		private final String symbol;

		private final BiFunction<BigDecimal, BigDecimal, Pair<BigDecimal,
			BigDecimal>>
			memoryAccess;

		/**
		 * Creates a new instance.
		 *
		 * @param symbol       The function symbol
		 * @param memoryAccess The function that performs the actual memory
		 *                     function
		 */
		MemoryFunction(String symbol,
			BiFunction<BigDecimal, BigDecimal, Pair<BigDecimal, BigDecimal>> memoryAccess) {
			this.symbol = symbol;
			this.memoryAccess = memoryAccess;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void accept(CalculatorState state) {
			Pair<BigDecimal, BigDecimal> result =
				memoryAccess.apply(state.currentValue, state.memoryValue);

			state.updateValues(result.first(), result.second(), true);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Pair<BigDecimal, BigDecimal> apply(BigDecimal value,
			BigDecimal memory) {
			return memoryAccess.apply(value, memory);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getSymbol() {
			return symbol;
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

		private final String symbol;

		private final Function<BigDecimal, BigDecimal> calc;

		/**
		 * Creates a new instance.
		 *
		 * @param symbol The function symbol
		 * @param calc   The calculation function
		 */
		UnaryCalculation(String symbol,
			Function<BigDecimal, BigDecimal> calc) {
			this.symbol = symbol;
			this.calc = calc;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void accept(CalculatorState state) {
			state.updateValues(calc.apply(state.currentValue),
				state.memoryValue, this != SIGN);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getSymbol() {
			return symbol;
		}
	}

	private static final String DECIMAL_SEPARATOR =
		LocaleInfo.getCurrentLocale().getNumberConstants().decimalSeparator();

	private static final GridLayout STANDARD_LAYOUT =
		grid().columns("repeat(4, 1fr)");

	private static final CalculatorFunction[][] STANDARD_KEYS =
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

	private final CalculatorState state = new CalculatorState();

	private final Map<Pair<ModifierKeys, KeyCode>, CalculatorFunction>
		functionKeys = new HashMap<>();

	private CalculatorDisplay display;

	private boolean keyHandled = false;

	/**
	 * Creates a new instance.
	 */
	public Calculator() {
		super(STANDARD_LAYOUT);
	}

	/**
	 * Returns a new {@link CalculatorDigit} function for calculator input.
	 *
	 * @param digit The digit character
	 * @return The new input function
	 */
	static CalculatorDigit digit(char digit) {
		return new CalculatorDigit(digit);
	}

	/**
	 * Returns the currentValue value.
	 *
	 * @return The currentValue value
	 */
	public final BigDecimal getValue() {
		return state.currentValue;
	}

	/**
	 * Sets the value of this calculator.
	 *
	 * @param value The new value
	 */
	public final void setValue(BigDecimal value) {
		state.currentValue = value;
		update(true);
	}

	/**
	 * Builds this calculator panel with the given builder.
	 *
	 * @param builder The builder to create the panel with
	 */
	@Override
	protected void build(ContainerBuilder<?> builder) {
		addStyleName(EWT.CSS.ewtCalculator());

		addEventListener(EventType.KEY_TYPED, this::handleKey);
		addEventListener(EventType.KEY_RELEASED, this::handleKey);

		display = builder.addComponent(new CalculatorDisplay(),
			DEFAULT.set(COLUMN, 1).set(COLUMN_SPAN, 4));

		for (CalculatorFunction[] row : STANDARD_KEYS) {
			for (CalculatorFunction function : row) {
				addFunctionButton(builder, function);
			}
		}

		update(true);
	}

	/**
	 * Adds a button for a function with a container builder.
	 *
	 * @param builder  The container builder
	 * @param function labels The button labels
	 */
	void addFunctionButton(ContainerBuilder<?> builder,
		CalculatorFunction function) {
		Button button = builder.addButton(DEFAULT, function.getSymbol());

		button.addStyleName(function.getClass().getSimpleName());

		if (function.getClass() != CalculatorDigit.class) {
			button.addStyleName(
				TextConvert.capitalizedIdentifier(function.toString()));
		}

		button.addEventListener(EventType.ACTION, e -> function.accept(state));

		functionKeys.put(function.getKey(), function);
	}

	/**
	 * Copies the current value as a string in the active display format to the
	 * system clipboard.
	 */
	void copyCurrentValueToClipboard() {
		EWT.copyTextToClipboard(display.value.getActiveValue());
		requestFocus();
	}

	/**
	 * Handles all keyboard input events.
	 *
	 * @param event The keyboard event
	 */
	void handleKey(EwtEvent event) {
		EventType eventType = event.getType();

		if (event.getModifiers() == ModifierKeys.CTRL) {
			if (event.getKeyCode() == KeyCode.C) {
				copyCurrentValueToClipboard();
			}

			keyHandled = true;
		} else if (!keyHandled || eventType == EventType.KEY_TYPED) {
			Pair<ModifierKeys, KeyCode> key =
				Pair.of(event.getModifiers(), event.getKeyCode());

			CalculatorFunction function = functionKeys.get(key);

			if (function != null) {
				function.accept(state);
				keyHandled = (eventType == EventType.KEY_TYPED);
			}
		} else {
			keyHandled = false;
		}
	}

	/**
	 * Updates the display text field with the current value.
	 *
	 * @param reset TRUE to reset all input parameters
	 */
	void update(boolean reset) {
		if (reset) {
			state.inputDigit = ONE;
			state.fractionInput = false;
			state.enterNewValue = true;
		}

		if (ZERO.equals(state.currentValue)) {
			// remove a possible negative sign on a zero value
			state.currentValue = ZERO;
		}

		display.update(state);
	}

	/**
	 * An interface for all calculator function enums.
	 *
	 * @author eso
	 */
	interface CalculatorFunction extends Consumer<CalculatorState> {

		/**
		 * Default value of {@link #getKey()}.
		 */
		Pair<ModifierKeys, KeyCode> NO_KEY =
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
		 * @param key The key code
		 * @return The key combination pair
		 */
		default Pair<ModifierKeys, KeyCode> key(KeyCode key) {
			return key(ModifierKeys.NONE, key);
		}

		/**
		 * A helper method for implementations that returns a key definition
		 * for
		 * key combinations.
		 *
		 * @param modifiers The modifier keys
		 * @param key       The key code
		 * @return The key combination pair
		 */
		default Pair<ModifierKeys, KeyCode> key(ModifierKeys modifiers,
			KeyCode key) {
			return Pair.of(modifiers, key);
		}
	}

	/**
	 * A calculator function for the input of digits.
	 *
	 * @author eso
	 */
	static class CalculatorDigit implements CalculatorFunction {

		private final char digit;

		private final Pair<ModifierKeys, KeyCode> key;

		/**
		 * Creates a new instance.
		 *
		 * @param digit symbol The input digit
		 */
		public CalculatorDigit(char digit) {
			this.digit = digit;
			key = key(KeyCode.forChar(digit));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void accept(CalculatorState state) {
			state.input(digit - '0');
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Pair<ModifierKeys, KeyCode> getKey() {
			return key;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getSymbol() {
			return Character.toString(digit);
		}
	}

	/**
	 * Inner class to encapsulate a mathematical operation.
	 */
	static class Operation {

		private final BinaryCalculation calculation;

		private final BigDecimal leftValue;

		/**
		 * Creates a new instance with a certain operator and left value for
		 * the
		 * operation.
		 *
		 * @param calculation The operator character
		 * @param leftValue   The left value for the operation
		 */
		Operation(BinaryCalculation calculation, BigDecimal leftValue) {
			this.calculation = calculation;
			this.leftValue = leftValue;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return leftValue.toString() + " " + calculation.getSymbol() + " ";
		}

		/**
		 * Executes this operation with a right value and returns the result.
		 *
		 * @param rightValue The right value to perform this operation with
		 * @return The result of performing this operation with the left and
		 * right values
		 */
		BigDecimal execute(BigDecimal rightValue) {
			return calculation.apply(leftValue, rightValue);
		}

		/**
		 * Returns the left value for this operations.
		 *
		 * @return The left value
		 */
		final BigDecimal getLeftValue() {
			return leftValue;
		}

		/**
		 * Returns the priority of this operation's operator.
		 *
		 * @return The operator priority
		 */
		final int getPriority() {
			return calculation.priority;
		}
	}

	/**
	 * A composite that contains the components of the calculator display.
	 *
	 * @author eso
	 */
	class CalculatorDisplay extends Composite {

		private Label operationsChain;

		private Label stateIndicator;

		private MultiFormatDisplay<BigDecimal, NumberDisplayFormat> value;

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
		protected void build(ContainerBuilder<?> builder) {
			addStyleName("CalculatorDisplay");

			ContainerBuilder<Panel> operationsBuilder =
				builder.addPanel(DEFAULT.set(LAYOUT_AREA, "operations"),
					flexHorizontal());

			operationsChain = operationsBuilder.addLabel(DEFAULT, "");
			operationsBuilder
				.addButton(DEFAULT, "@$CalcCopyButton")
				.addEventListener(EventType.ACTION,
					e -> copyCurrentValueToClipboard());
			stateIndicator =
				builder.addLabel(DEFAULT.set(LAYOUT_AREA, "state"), "");

			operationsChain.setWidth("100%");
			operationsChain.addStyleName("calcOps");
			stateIndicator.addStyleName("calcState");

			value = new MultiFormatDisplay<>(NumberDisplayFormat.DECIMAL,
				NumberDisplayFormat.HEXADECIMAL, NumberDisplayFormat.BINARY);
			builder.addComponent(value, DEFAULT);
			value.addStyleName("CalculatorValue");
		}

		/**
		 * Updates the display based on the given state.
		 *
		 * @param state The state to update from
		 */
		void update(CalculatorState state) {
			StringBuilder operations = new StringBuilder();

			for (Operation operation : state.operationsStack) {
				operations.append(operation);
			}

			operationsChain.setText(operations.toString());
			stateIndicator.setText(
				Objects.equals(state.memoryValue, ZERO) ? "" : "M");
			value.update(state.currentValue);
		}
	}

	/**
	 * Encapsulates the current calculator state.
	 *
	 * @author eso
	 */
	class CalculatorState {

		private final Stack<Operation> operationsStack =
			new Stack<Operation>();

		private BigDecimal currentValue = ZERO;

		private BigDecimal memoryValue = ZERO;

		private BigDecimal inputDigit = ONE;

		private boolean fractionInput;

		private boolean enterNewValue;

		/**
		 * Adds a new operation to the stack.
		 *
		 * @param calculation The calculation to be performed by the operation.
		 */
		void addOperation(BinaryCalculation calculation) {
			currentValue =
				executeOperations(currentValue, calculation.getPriority());

			Operation operation = new Operation(calculation, currentValue);

			operationsStack.push(operation);

			update(true);
		}

		/**
		 * Removes one digit from the current input.
		 */
		void backOneDigit() {
			if (!enterNewValue) {
				if (fractionInput) {
					int scale = currentValue.scale();

					if (scale > 0) {
						inputDigit = inputDigit.multiply(TEN);
						currentValue =
							currentValue.setScale(--scale, RoundingMode.FLOOR);
					}

					if (scale == 0) {
						fractionInput = false;
						inputDigit = ONE;
					}
				} else {
					currentValue = currentValue.divide(TEN,
						RoundingMode.FLOOR);
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
			currentValue = executeOperations(currentValue, 0);
			update(true);
		}

		/**
		 * Clears the complete operations stack and displayed values (but not
		 * the memory).
		 */
		void clearAll() {
			operationsStack.removeAllElements();
			clearEntry();
		}

		/**
		 * Clears the currently entered value.
		 */
		void clearEntry() {
			currentValue = ZERO;
			update(true);
		}

		/**
		 * Executes the topmost operations on the operation stack that have a
		 * certain minimum priority, starting with the given right value and
		 * returning the resulting value.
		 *
		 * @param rightValue  The right value to calculate with
		 * @param minPriority The minimum priority an operation must have
		 */
		BigDecimal executeOperations(BigDecimal rightValue, int minPriority) {
			while (!operationsStack.isEmpty() &&
				operationsStack.peek().getPriority() >= minPriority) {
				rightValue = operationsStack.pop().execute(rightValue);
			}

			update(true);

			return rightValue;
		}

		/**
		 * Performs the input of a single digit.
		 *
		 * @param digitValue The digit value
		 */
		void input(int digitValue) {
			BigDecimal digit = new BigDecimal(digitValue);

			if (enterNewValue) {
				currentValue = ZERO;
				enterNewValue = false;
			}

			if (fractionInput) {
				inputDigit = inputDigit.divide(TEN);
				currentValue = currentValue.add(inputDigit.multiply(digit));
			} else {
				currentValue = currentValue.multiply(TEN).add(digit);
			}

			update(false);
		}

		/**
		 * Updates this state to perform the input of fraction digits.
		 */
		void startFractionInput() {
			fractionInput = true;

			update(false);
		}

		/**
		 * Updates the current and memory values.
		 *
		 * @param current The new current value
		 * @param memory  The new memory value
		 * @param reset   TRUE to reset all input parameters
		 */
		void updateValues(BigDecimal current, BigDecimal memory,
			boolean reset) {
			currentValue = current;
			memoryValue = memory;

			update(reset);
		}
	}
}
