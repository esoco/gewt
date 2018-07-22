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
package de.esoco.ewt.component;

import de.esoco.ewt.EWT;
import de.esoco.ewt.build.ContainerBuilder;
import de.esoco.ewt.event.EventType;
import de.esoco.ewt.event.EwtEvent;
import de.esoco.ewt.event.KeyCode;
import de.esoco.ewt.event.ModifierKeys;
import de.esoco.ewt.layout.GridLayout;

import de.esoco.lib.datatype.Pair;
import de.esoco.lib.math.MathUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import static de.esoco.ewt.style.StyleData.DEFAULT;

import static de.esoco.lib.property.LayoutProperties.COLUMN;
import static de.esoco.lib.property.LayoutProperties.COLUMN_SPAN;
import static de.esoco.lib.property.LayoutProperties.LAYOUT_AREA;

import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.TEN;
import static java.math.BigDecimal.ZERO;


/********************************************************************
 * A calculator composite that allows the user to interactively calculate a
 * {@link BigDecimal} value.
 *
 * @author eso
 */
public class Calculator extends Composite
{
	//~ Enums ------------------------------------------------------------------

	/********************************************************************
	 * Enumeration of calculator actions.
	 */
	enum CalculatorAction implements CalculatorFunction
	{
		BACK("⌫", CalculatorState::backOneDigit, KeyCode.BACKSPACE),
		CLEAR_ALL("C", CalculatorState::clearAll, KeyCode.ESCAPE),
		CLEAR_ENTRY("CE",
					CalculatorState::clearEntry,
					ModifierKeys.SHIFT,
					KeyCode.BACKSPACE),
		DOT(".", CalculatorState::startFractionInput, KeyCode.PERIOD),
		EQUALS("=", CalculatorState::calculate, KeyCode.EQUALS);

		//~ Instance fields ----------------------------------------------------

		private final String				    sSymbol;
		private final Consumer<CalculatorState> fPerformAction;
		private Pair<ModifierKeys, KeyCode>     aKey;

		//~ Constructors -------------------------------------------------------

		/***************************************
		 * Creates a new instance.
		 *
		 * @param sSymbol        The action symbol
		 * @param fPerformAction The function to perform this action
		 * @param eKeyCode       The key code for this action
		 */
		private CalculatorAction(String					   sSymbol,
								 Consumer<CalculatorState> fPerformAction,
								 KeyCode				   eKeyCode)
		{
			this(sSymbol, fPerformAction, ModifierKeys.NONE, eKeyCode);
		}

		/***************************************
		 * Creates a new instance.
		 *
		 * @param sSymbol        The action symbol
		 * @param fPerformAction The function to perform this action
		 * @param rModifiers     The modifier keys for the key code
		 * @param eKeyCode       The key code for this action
		 */
		private CalculatorAction(String					   sSymbol,
								 Consumer<CalculatorState> fPerformAction,
								 ModifierKeys			   rModifiers,
								 KeyCode				   eKeyCode)
		{
			this.sSymbol	    = sSymbol;
			this.fPerformAction = fPerformAction;

			aKey = key(rModifiers, eKeyCode);
		}

		//~ Methods ------------------------------------------------------------

		/***************************************
		 * {@inheritDoc}
		 */
		@Override
		public void accept(CalculatorState rState)
		{
			fPerformAction.accept(rState);
		}

		/***************************************
		 * {@inheritDoc}
		 */
		@Override
		public Pair<ModifierKeys, KeyCode> getKey()
		{
			return aKey;
		}

		/***************************************
		 * {@inheritDoc}
		 */
		@Override
		public String getSymbol()
		{
			return sSymbol;
		}
	}

	/********************************************************************
	 * Enumeration of calculator actions.
	 */
	enum BinaryCalculation
		implements CalculatorFunction,
				   BiFunction<BigDecimal, BigDecimal, BigDecimal>
	{
		ADD("+", 1, '+', BigDecimal::add),
		SUBTRACT("-", 1, '-', BigDecimal::subtract),
		MULTIPLY("×", 2, '*', BigDecimal::multiply),
		DIVIDE("÷", 2, '/', BigDecimal::divide),
		PERCENT("%",
				1,
				'%',
				(d1, d2) -> d1.multiply(d2).divide(MathUtil.HUNDRED));

		//~ Instance fields ----------------------------------------------------

		private final String sSymbol;
		private final int    nPriority;

		private Pair<ModifierKeys, KeyCode> aKey;

		private final BiFunction<BigDecimal, BigDecimal, BigDecimal> fCalc;

		//~ Constructors -------------------------------------------------------

		/***************************************
		 * Creates a new instance.
		 *
		 * @param sSymbol   The function symbol
		 * @param nPriority The priority of this calculation in relation to
		 *                  other calculations
		 * @param cKey      The character of the key to invoke this function
		 * @param fCalc     The calculation function
		 */
		private BinaryCalculation(
			String										   sSymbol,
			int											   nPriority,
			char										   cKey,
			BiFunction<BigDecimal, BigDecimal, BigDecimal> fCalc)
		{
			this.sSymbol   = sSymbol;
			this.nPriority = nPriority;
			this.fCalc     = fCalc;

			aKey = key(KeyCode.forChar(cKey));
		}

		//~ Methods ------------------------------------------------------------

		/***************************************
		 * {@inheritDoc}
		 */
		@Override
		public void accept(CalculatorState rState)
		{
			rState.addOperation(this);
		}

		/***************************************
		 * {@inheritDoc}
		 */
		@Override
		public BigDecimal apply(BigDecimal dLeft, BigDecimal dRight)
		{
			return fCalc.apply(dLeft, dRight);
		}

		/***************************************
		 * {@inheritDoc}
		 */
		@Override
		public Pair<ModifierKeys, KeyCode> getKey()
		{
			return aKey;
		}

		/***************************************
		 * Returns the calculation priority (precedence).
		 *
		 * @return The priority
		 */
		public int getPriority()
		{
			return nPriority;
		}

		/***************************************
		 * {@inheritDoc}
		 */
		@Override
		public String getSymbol()
		{
			return sSymbol;
		}
	}

	/********************************************************************
	 * Enumeration of calculator actions.
	 */
	enum UnaryCalculation implements CalculatorFunction,
									 Function<BigDecimal, BigDecimal>
	{
		INVERT("¹/x", ONE::divide), SIGN("±", BigDecimal::negate),
		SQUARE("x²", d -> d.multiply(d)), SQUARE_ROOT("√", MathUtil::sqrt);

		//~ Instance fields ----------------------------------------------------

		private final String					 sSymbol;
		private Function<BigDecimal, BigDecimal> fCalc;

		//~ Constructors -------------------------------------------------------

		/***************************************
		 * Creates a new instance.
		 *
		 * @param sSymbol The function symbol
		 * @param fCalc   The calculation function
		 */
		private UnaryCalculation(
			String							 sSymbol,
			Function<BigDecimal, BigDecimal> fCalc)
		{
			this.sSymbol = sSymbol;
			this.fCalc   = fCalc;
		}

		//~ Methods ------------------------------------------------------------

		/***************************************
		 * {@inheritDoc}
		 */
		@Override
		public void accept(CalculatorState rState)
		{
			rState.dCurrentValue = fCalc.apply(rState.dCurrentValue);
		}

		/***************************************
		 * {@inheritDoc}
		 */
		@Override
		public BigDecimal apply(BigDecimal dValue)
		{
			return fCalc.apply(dValue);
		}

		/***************************************
		 * {@inheritDoc}
		 */
		@Override
		public String getSymbol()
		{
			return sSymbol;
		}
	}

	/********************************************************************
	 * Enumeration of calculator memory manipulation functions. All instances
	 * implement a binary function that take the current display and memory
	 * values and return a pair that contains these value (in that order) after
	 * the function execution.
	 */
	enum MemoryFunction
		implements CalculatorFunction,
				   BiFunction<BigDecimal,
							  BigDecimal, Pair<BigDecimal, BigDecimal>>
	{
		MEMORY_EXCHANCE("MX", (v, m) -> Pair.of(m, v)),
		MEMORY_CLEAR("MC", (v, m) -> Pair.of(v, ZERO)),
		MEMORY_RECALL("MR", (v, m) -> Pair.of(m, m)),
		MEMORY_STORE("MS", (v, m) -> Pair.of(v, v)),
		MEMORY_ADD("M+", (v, m) -> Pair.of(v, m.add(v))),
		MEMORY_SUBTRACT("M-", (v, m) -> Pair.of(v, m.subtract(v)));

		//~ Instance fields ----------------------------------------------------

		private final String sSymbol;

		private final BiFunction<BigDecimal,
								 BigDecimal, Pair<BigDecimal, BigDecimal>> fMemory;

		//~ Constructors -------------------------------------------------------

		/***************************************
		 * Creates a new instance.
		 *
		 * @param sSymbol The function symbol
		 * @param fMemory The function that performs the actual memory function
		 */
		private MemoryFunction(
			String															 sSymbol,
			BiFunction<BigDecimal, BigDecimal, Pair<BigDecimal, BigDecimal>> fMemory)
		{
			this.sSymbol = sSymbol;
			this.fMemory = fMemory;
		}

		//~ Methods ------------------------------------------------------------

		/***************************************
		 * {@inheritDoc}
		 */
		@Override
		public void accept(CalculatorState rState)
		{
			Pair<BigDecimal, BigDecimal> aResult =
				fMemory.apply(rState.dCurrentValue, rState.dMemoryValue);

			rState.updateValues(aResult.first(), aResult.second());
		}

		/***************************************
		 * {@inheritDoc}
		 */
		@Override
		public Pair<BigDecimal, BigDecimal> apply(
			BigDecimal dValue,
			BigDecimal dMemory)
		{
			return fMemory.apply(dValue, dMemory);
		}

		/***************************************
		 * {@inheritDoc}
		 */
		@Override
		public String getSymbol()
		{
			return sSymbol;
		}
	}

	//~ Static fields/initializers ---------------------------------------------

	private static CalculatorFunction[][] STANDARD_LAYOUT =
		new CalculatorFunction[][]
		{
			{
				MemoryFunction.MEMORY_CLEAR, MemoryFunction.MEMORY_STORE,
				MemoryFunction.MEMORY_RECALL, MemoryFunction.MEMORY_EXCHANCE
			},
			{
				BinaryCalculation.PERCENT, UnaryCalculation.SQUARE_ROOT,
				UnaryCalculation.SQUARE, UnaryCalculation.INVERT
			},
			{
				CalculatorAction.CLEAR_ALL, CalculatorAction.CLEAR_ENTRY,
				CalculatorAction.BACK, BinaryCalculation.DIVIDE
			},
			{ digit('7'), digit('8'), digit('9'), BinaryCalculation.MULTIPLY },
			{ digit('4'), digit('5'), digit('6'), BinaryCalculation.SUBTRACT },
			{ digit('1'), digit('2'), digit('3'), BinaryCalculation.ADD },
			{
				UnaryCalculation.SIGN, digit('0'), CalculatorAction.DOT,
				CalculatorAction.EQUALS
			}
		};

	//~ Instance fields --------------------------------------------------------

	private CalculatorState   aState   = new CalculatorState();
	private CalculatorDisplay aDisplay;

	private Map<Pair<ModifierKeys, KeyCode>, CalculatorFunction> aFunctionKeys =
		new HashMap<>();

	private boolean bKeyHandled = false;

	//~ Constructors -----------------------------------------------------------

	/***************************************
	 * Creates a new instance.
	 */
	public Calculator()
	{
		super(new GridLayout().columns("repeat(4, 1fr)").gaps("0.5rem"), true);
	}

	//~ Static methods ---------------------------------------------------------

	/***************************************
	 * Returns a new {@link CalculatorDigit} function for calculator input.
	 *
	 * @param  cDigit The digit character
	 *
	 * @return The new input function
	 */
	static CalculatorDigit digit(char cDigit)
	{
		return new CalculatorDigit(cDigit);
	}

	//~ Methods ----------------------------------------------------------------

	/***************************************
	 * Returns the currentValue value.
	 *
	 * @return The currentValue value
	 */
	public final BigDecimal getValue()
	{
		return aState.dCurrentValue;
	}

	/***************************************
	 * Sets the value of this calculator.
	 *
	 * @param dValue The new value
	 */
	public final void setValue(BigDecimal dValue)
	{
		aState.dCurrentValue = dValue;
		update(true);
	}

	/***************************************
	 * Builds this calculator panel with the given builder.
	 *
	 * @param rBuilder The builder to create the panel with
	 */
	@Override
	protected void build(ContainerBuilder<?> rBuilder)
	{
		addStyleName(EWT.CSS.ewtCalculator());

		addEventListener(EventType.KEY_TYPED, this::handleKey);
		addEventListener(EventType.KEY_RELEASED, this::handleKey);

		aDisplay =
			rBuilder.addComponent(new CalculatorDisplay(),
								  DEFAULT.set(COLUMN, 1).set(COLUMN_SPAN, 4));

		for (CalculatorFunction[] rRow : STANDARD_LAYOUT)
		{
			for (CalculatorFunction rFunction : rRow)
			{
				addFunctionButton(rBuilder, rFunction);
			}
		}

		update(true);
	}

	/***************************************
	 * Adds a button for a function with a container builder.
	 *
	 * @param rBuilder  The container builder
	 * @param rFunction rLabels The button labels
	 */
	void addFunctionButton(
		ContainerBuilder<?> rBuilder,
		CalculatorFunction  rFunction)
	{
		Button aButton = rBuilder.addButton(DEFAULT, rFunction.getSymbol());

		aButton.addEventListener(EventType.ACTION,
								 e -> rFunction.accept(aState));

		aFunctionKeys.put(rFunction.getKey(), rFunction);
	}

	/***************************************
	 * Handles all keyboard input events.
	 *
	 * @param rEvent The keyboard event
	 */
	void handleKey(EwtEvent rEvent)
	{
		if (!bKeyHandled)
		{
			Pair<ModifierKeys, KeyCode> aKey =
				Pair.of(rEvent.getModifiers(), rEvent.getKeyCode());

			CalculatorFunction rFunction = aFunctionKeys.get(aKey);

			if (rFunction != null)
			{
				rFunction.accept(aState);
				bKeyHandled = (rEvent.getType() == EventType.KEY_TYPED);

				EWT.log("Handled %s: %s", rEvent, aKey);
			}
		}
		else
		{
			bKeyHandled = false;
		}
	}

	/***************************************
	 * Updates the display text field with the current value.
	 *
	 * @param bReset TRUE to reset all input parameters
	 */
	void update(boolean bReset)
	{
		if (bReset)
		{
			aState.aInputDigit    = ONE;
			aState.bFractionInput = false;
			aState.bEnterNewValue = true;
		}

		if (ZERO.equals(aState.dCurrentValue))
		{
			// remove a possible negative sign on a zero value
			aState.dCurrentValue = ZERO;
		}

		aDisplay.update(aState);
	}

	//~ Inner Interfaces -------------------------------------------------------

	/********************************************************************
	 * An interface for all calculator function enums.
	 *
	 * @author eso
	 */
	static interface CalculatorFunction extends Consumer<CalculatorState>
	{
		//~ Static fields/initializers -----------------------------------------

		/** Default value of {@link #getKey()}. */
		public static final Pair<ModifierKeys, KeyCode> NO_KEY =
			Pair.of(ModifierKeys.NONE, KeyCode.NONE);

		//~ Methods ------------------------------------------------------------

		/***************************************
		 * Returns the key combination that can be pressed to invoke this
		 * function. The default implementation returns {@link #NO_KEY}.
		 *
		 * @return The function key code
		 */
		default Pair<ModifierKeys, KeyCode> getKey()
		{
			return NO_KEY;
		}

		/***************************************
		 * The symbol string to be displayed for this function.
		 *
		 * @return The symbol string
		 */
		String getSymbol();

		/***************************************
		 * A helper method for implementations that returns a key definition for
		 * a single key that needs to be pressed without modifiers.
		 *
		 * @param  rKey The key code
		 *
		 * @return The key combination pair
		 */
		default Pair<ModifierKeys, KeyCode> key(KeyCode rKey)
		{
			return key(ModifierKeys.NONE, rKey);
		}

		/***************************************
		 * A helper method for implementations that returns a key definition for
		 * key combinations.
		 *
		 * @param  rModifiers The modifier keys
		 * @param  rKey       The key code
		 *
		 * @return The key combination pair
		 */
		default Pair<ModifierKeys, KeyCode> key(
			ModifierKeys rModifiers,
			KeyCode		 rKey)
		{
			return Pair.of(rModifiers, rKey);
		}
	}

	//~ Inner Classes ----------------------------------------------------------

	/********************************************************************
	 * A calculator function for the input of digits.
	 *
	 * @author eso
	 */
	static class CalculatorDigit implements CalculatorFunction
	{
		//~ Instance fields ----------------------------------------------------

		private final char						  cDigit;
		private final Pair<ModifierKeys, KeyCode> aKey;

		//~ Constructors -------------------------------------------------------

		/***************************************
		 * Creates a new instance.
		 *
		 * @param cDigit sSymbol The input digit
		 */
		public CalculatorDigit(char cDigit)
		{
			this.cDigit = cDigit;
			aKey	    = key(KeyCode.forChar(cDigit));
		}

		//~ Methods ------------------------------------------------------------

		/***************************************
		 * {@inheritDoc}
		 */
		@Override
		public void accept(CalculatorState rState)
		{
			rState.input(cDigit - '0');
		}

		/***************************************
		 * {@inheritDoc}
		 */
		@Override
		public Pair<ModifierKeys, KeyCode> getKey()
		{
			return aKey;
		}

		/***************************************
		 * {@inheritDoc}
		 */
		@Override
		public String getSymbol()
		{
			return Character.toString(cDigit);
		}
	}

	/********************************************************************
	 * A composite that contains the components of the calculator display.
	 *
	 * @author eso
	 */
	static class CalculatorDisplay extends Composite
	{
		//~ Instance fields ----------------------------------------------------

		private Label aOperationsChain;
		private Label aMemoryIndicator;
		private Label aValue;

		//~ Constructors -------------------------------------------------------

		/***************************************
		 * Creates a new instance.
		 */
		protected CalculatorDisplay()
		{
			super(new GridLayout().columns("auto 1fr")
				  .areas("'operation operation''state value'")
				  .colGap("6px"),
				  false);
		}

		//~ Methods ------------------------------------------------------------

		/***************************************
		 * {@inheritDoc}
		 */
		@Override
		protected void build(ContainerBuilder<?> rBuilder)
		{
			aOperationsChain =
				rBuilder.addLabel(DEFAULT.set(LAYOUT_AREA, "operation")
								  .css("minHeight", "2em")
								  .css("background", "#def"),
								  "");
			aMemoryIndicator =
				rBuilder.addLabel(DEFAULT.set(LAYOUT_AREA, "state")
								  .css("minWidth", "2em")
								  .css("background", "#fed")
								  .css("textAlign", "center"),
								  "");
			aValue			 =
				rBuilder.addLabel(DEFAULT.set(LAYOUT_AREA, "value")
								  .css("fontSize", "250%")
								  .css("textAlign", "right"),
								  "");
		}

		/***************************************
		 * Updates the display based on the given state.
		 *
		 * @param rState The state to update from
		 */
		void update(CalculatorState rState)
		{
			StringBuilder aOperations = new StringBuilder();

			for (Operation rOperation : rState.aOperationsStack)
			{
				aOperations.append(rOperation);
			}

			aOperationsChain.setText(aOperations.toString());
			aMemoryIndicator.setText(rState.dMemoryValue == ZERO ? "" : "M");
			aValue.setText(rState.dCurrentValue.toString());
		}
	}

	/********************************************************************
	 * Inner class to encapsulate a mathematical operation.
	 */
	static class Operation
	{
		//~ Instance fields ----------------------------------------------------

		private BinaryCalculation eCalculation;
		private BigDecimal		  rLeftValue;

		//~ Constructors -------------------------------------------------------

		/***************************************
		 * Creates a new instance with a certain operator and left value for the
		 * operation.
		 *
		 * @param eCalculation The operator character
		 * @param rLeftValue   The left value for the operation
		 */
		Operation(BinaryCalculation eCalculation, BigDecimal rLeftValue)
		{
			this.eCalculation = eCalculation;
			this.rLeftValue   = rLeftValue;
		}

		//~ Methods ------------------------------------------------------------

		/***************************************
		 * {@inheritDoc}
		 */
		@Override
		public String toString()
		{
			return rLeftValue.toString() + " " + eCalculation.getSymbol() + " ";
		}

		/***************************************
		 * Executes this operation with a right value and returns the result.
		 *
		 * @param  rRightValue The right value to perform this operation with
		 *
		 * @return The result of performing this operation with the left and
		 *         right values
		 */
		BigDecimal execute(BigDecimal rRightValue)
		{
			return eCalculation.apply(rLeftValue, rRightValue);
		}

		/***************************************
		 * Returns the left value for this operations.
		 *
		 * @return The left value
		 */
		final BigDecimal getLeftValue()
		{
			return rLeftValue;
		}

		/***************************************
		 * Returns the priority of this operation's operator.
		 *
		 * @return The operator priority
		 */
		final int getPriority()
		{
			return eCalculation.nPriority;
		}
	}

	/********************************************************************
	 * Encapsulates the current calculator state.
	 *
	 * @author eso
	 */
	class CalculatorState
	{
		//~ Instance fields ----------------------------------------------------

		private BigDecimal dCurrentValue = ZERO;
		private BigDecimal dMemoryValue  = ZERO;
		private BigDecimal aInputDigit   = ONE;

		private boolean bFractionInput;
		private boolean bEnterNewValue;

		private Stack<Operation> aOperationsStack = new Stack<Operation>();

		//~ Methods ------------------------------------------------------------

		/***************************************
		 * Adds a new operation to the stack.
		 *
		 * @param eCalculation The calculation to be performed by the operation.
		 */
		void addOperation(BinaryCalculation eCalculation)
		{
			dCurrentValue =
				executeOperations(dCurrentValue, eCalculation.getPriority());

			Operation aOperation = new Operation(eCalculation, dCurrentValue);

			aOperationsStack.push(aOperation);

			update(true);
		}

		/***************************************
		 * Removes one digit from the current input.
		 */
		void backOneDigit()
		{
			if (!bEnterNewValue)
			{
				if (bFractionInput)
				{
					if (aInputDigit.compareTo(ONE) < 0)
					{
						aInputDigit   = aInputDigit.multiply(TEN);
						dCurrentValue =
							dCurrentValue.divide(aInputDigit,
												 RoundingMode.HALF_UP);
						dCurrentValue = dCurrentValue.multiply(aInputDigit);
					}
					else
					{
						bFractionInput = false;
					}
				}
				else
				{
					dCurrentValue =
						dCurrentValue.divide(TEN, RoundingMode.FLOOR);
				}
			}

			update(false);
		}

		/***************************************
		 * Calculates the current operations stack and resets for the input of a
		 * new value.
		 */
		void calculate()
		{
			dCurrentValue = executeOperations(dCurrentValue, 0);
			update(true);
		}

		/***************************************
		 * Clears the complete operations stack and displayed values (but not
		 * the memory).
		 */
		void clearAll()
		{
			aOperationsStack.removeAllElements();
			clearEntry();
		}

		/***************************************
		 * Clears the currently entered value.
		 */
		void clearEntry()
		{
			dCurrentValue = ZERO;
			update(true);
		}

		/***************************************
		 * Executes the topmost operations on the operation stack that have a
		 * certain minimum priority, starting with the given right value and
		 * returning the resulting value.
		 *
		 * @param  dRightValue  The right value to calculate with
		 * @param  nMinPriority The minimum priority an operation must have
		 *
		 * @return
		 */
		BigDecimal executeOperations(BigDecimal dRightValue, int nMinPriority)
		{
			while (!aOperationsStack.isEmpty() &&
				   aOperationsStack.peek().getPriority() >= nMinPriority)
			{
				dRightValue = aOperationsStack.pop().execute(dRightValue);
			}

			update(true);

			return dRightValue;
		}

		/***************************************
		 * Performs the input of a single digit.
		 *
		 * @param nDigit The digit value
		 */
		void input(int nDigit)
		{
			BigDecimal aDigit = new BigDecimal(nDigit);

			if (bEnterNewValue)
			{
				dCurrentValue  = ZERO;
				bEnterNewValue = false;
			}

			if (bFractionInput)
			{
				aInputDigit   = aInputDigit.divide(TEN);
				dCurrentValue = dCurrentValue.add(aInputDigit.multiply(aDigit));
			}
			else
			{
				dCurrentValue = dCurrentValue.multiply(TEN).add(aDigit);
			}

			update(false);
		}

		/***************************************
		 * Updates this state to perform the input of fraction digits.
		 */
		void startFractionInput()
		{
			bFractionInput = true;

			update(false);
		}

		/***************************************
		 * Updates the current and memory values.
		 *
		 * @param dCurrent The new current value
		 * @param dMemory  The new memory value
		 */
		void updateValues(BigDecimal dCurrent, BigDecimal dMemory)
		{
			dCurrentValue = dCurrent;
			dMemoryValue  = dMemory;

			update(true);
		}
	}
}
