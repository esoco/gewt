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

import de.esoco.ewt.build.ContainerBuilder;
import de.esoco.ewt.event.EWTEvent;
import de.esoco.ewt.event.EWTEventHandler;
import de.esoco.ewt.event.EventType;
import de.esoco.ewt.event.KeyCode;
import de.esoco.ewt.layout.FillLayout;
import de.esoco.ewt.layout.GridLayout;

import de.esoco.lib.datatype.Pair;
import de.esoco.lib.math.MathUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;

import java.util.Stack;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import static de.esoco.ewt.style.StyleData.DEFAULT;

import static de.esoco.lib.property.LayoutProperties.COLUMN;
import static de.esoco.lib.property.LayoutProperties.COLUMN_SPAN;


/********************************************************************
 * A calculator composite that allows the user to interactively calculate a
 * {@link BigDecimal} value.
 *
 * @author eso
 */
public class Calculator extends Composite implements EWTEventHandler
{
	//~ Enums ------------------------------------------------------------------

	/********************************************************************
	 * Enumeration of calculator actions.
	 */
	enum CalculatorAction implements CalculatorFunction
	{
		BACK("⌫", CalculatorState::backOneDigit),
		CLEAR_ALL("C", CalculatorState::clearAll),
		CLEAR_ENTRY("CE", CalculatorState::clearEntry),
		DOT(".", CalculatorState::startFractionInput),
		EQUALS("=", CalculatorState::calculate);

		//~ Instance fields ----------------------------------------------------

		private final String				    sSymbol;
		private final Consumer<CalculatorState> fPerformAction;

		//~ Constructors -------------------------------------------------------

		/***************************************
		 * Creates a new instance.
		 *
		 * @param sSymbol        The action symbol
		 * @param fPerformAction The function to perform this action
		 */
		private CalculatorAction(
			String					  sSymbol,
			Consumer<CalculatorState> fPerformAction)
		{
			this.sSymbol	    = sSymbol;
			this.fPerformAction = fPerformAction;
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
		ADD("+", 1, BigDecimal::add), SUBTRACT("-", 1, BigDecimal::subtract),
		MULTIPLY("×", 1, BigDecimal::multiply),
		DIVIDE("÷", 1, BigDecimal::divide),
		PERCENT("%", 1, (d1, d2) -> d1.multiply(d2).divide(MathUtil.HUNDRED));

		//~ Instance fields ----------------------------------------------------

		private final String sSymbol;
		private final int    nPriority;

		private final BiFunction<BigDecimal, BigDecimal, BigDecimal> fCalc;

		//~ Constructors -------------------------------------------------------

		/***************************************
		 * Creates a new instance.
		 *
		 * @param sSymbol   The function symbol
		 * @param nPriority The priority of this calculation in relation to
		 *                  other calculations
		 * @param fCalc     The calculation function
		 */
		private BinaryCalculation(
			String										   sSymbol,
			int											   nPriority,
			BiFunction<BigDecimal, BigDecimal, BigDecimal> fCalc)
		{
			this.sSymbol   = sSymbol;
			this.fCalc     = fCalc;
			this.nPriority = nPriority;
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
		INVERT("¹/x", BigDecimal.ONE::divide), SIGN("±", BigDecimal::negate),
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
		MEMORY_CLEAR("MC", (v, m) -> Pair.of(v, BigDecimal.ZERO)),
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

			rState.dCurrentValue = aResult.first();
			rState.dMemoryValue  = aResult.second();
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

	private TextField aDisplay;
	private Label     aMemoryLabel;

	private CalculatorState aState = new CalculatorState();

	//~ Constructors -----------------------------------------------------------

	/***************************************
	 * Creates a new instance.
	 */
	public Calculator()
	{
		super(new GridLayout().columns("repeat(4, 1fr)")
			  .rows("repeat(8, 1fr)")
			  .colGap("6px")
			  .rowGap("6px"));
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
	 * Handles all button events.
	 *
	 * @param rEvent The button event
	 */
	@Override
	public void handleEvent(EWTEvent rEvent)
	{
		if (rEvent.getType() == EventType.KEY_PRESSED)
		{
			if (rEvent.getKeyCode() != KeyCode.NONE)
			{
			}
		}
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
		int nGap = 3;

		rBuilder =
			rBuilder.addPanel(DEFAULT.set(COLUMN, 1).set(COLUMN_SPAN, 4),
							  new FillLayout(nGap));

		aDisplay     = rBuilder.addTextField(DEFAULT, "0.");
		aMemoryLabel = rBuilder.addLabel(DEFAULT, "", null);

		aDisplay.setEditable(true);
		aDisplay.addEventListener(EventType.KEY_PRESSED, this);

		rBuilder = rBuilder.getParent();

		for (CalculatorFunction[] rRow : STANDARD_LAYOUT)
		{
			for (CalculatorFunction rFunction : rRow)
			{
				addButtons(rBuilder, rFunction);
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
	void addButtons(ContainerBuilder<?> rBuilder, CalculatorFunction rFunction)
	{
		Button aButton =
			rBuilder.addButton(DEFAULT, rFunction.getSymbol(), null);

		aButton.addEventListener(EventType.ACTION, this);
		aButton.addEventListener(EventType.KEY_PRESSED, this);
	}

	/***************************************
	 * Returns the function associated with a certain key code.
	 *
	 * @param  rEvent The event containing the key code
	 *
	 * @return The function string for the event's key code
	 */
	CalculatorFunction getKeyFunction(EWTEvent rEvent)
	{
		CalculatorFunction rFunction = null;

		if (rEvent.getKeyCode() == KeyCode.STAR)
		{
			rFunction = CalculatorAction.DOT;
		}
		else if (rEvent.getKeyCode() == KeyCode.POUND)
		{
			rFunction = CalculatorAction.BACK;
		}
		else
		{
//			rFunction = "" + rEvent.getKeyCode().getChar();
		}

		return rFunction;
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
			aState.aInputDigit    = BigDecimal.ONE;
			aState.bFractionInput = false;
			aState.bEnterNewValue = true;
		}

		if (BigDecimal.ZERO.equals(aState.dCurrentValue))
		{
			// remove a possible negative sign on a zero value
			aState.dCurrentValue = BigDecimal.ZERO;
		}

		aDisplay.setText(aState.dCurrentValue.toString());
		aMemoryLabel.setText(BigDecimal.ZERO.equals(aState.dMemoryValue) ? ""
																		 : "M");
		aMemoryLabel.repaint();
		aDisplay.repaint();
	}

	//~ Inner Interfaces -------------------------------------------------------

	/********************************************************************
	 * An interface for all calculator function enums.
	 *
	 * @author eso
	 */
	static interface CalculatorFunction extends Consumer<CalculatorState>
	{
		//~ Methods ------------------------------------------------------------

		/***************************************
		 * Returns the symbol string for this function.
		 *
		 * @return The symbol string
		 */
		String getSymbol();
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

		private final char cDigit;

		//~ Constructors -------------------------------------------------------

		/***************************************
		 * Creates a new instance.
		 *
		 * @param cDigit sSymbol The input digit
		 */
		public CalculatorDigit(char cDigit)
		{
			this.cDigit = cDigit;
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
		public String getSymbol()
		{
			return Character.toString(cDigit);
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

		private BigDecimal dCurrentValue = BigDecimal.ZERO;
		private BigDecimal dMemoryValue  = BigDecimal.ZERO;
		private BigDecimal aInputDigit   = BigDecimal.ONE;

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
					if (aInputDigit.compareTo(BigDecimal.ONE) < 0)
					{
						aInputDigit   = aInputDigit.multiply(BigDecimal.TEN);
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
						dCurrentValue.divide(BigDecimal.TEN,
											 RoundingMode.FLOOR);
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
			executeOperations(dCurrentValue, 0);
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
			dCurrentValue = BigDecimal.ZERO;
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
				dCurrentValue  = BigDecimal.ZERO;
				bEnterNewValue = false;
			}

			if (bFractionInput)
			{
				aInputDigit   = aInputDigit.divide(BigDecimal.TEN);
				dCurrentValue = dCurrentValue.add(aInputDigit.multiply(aDigit));
			}
			else
			{
				dCurrentValue =
					dCurrentValue.multiply(BigDecimal.TEN).add(aDigit);
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
	}
}
