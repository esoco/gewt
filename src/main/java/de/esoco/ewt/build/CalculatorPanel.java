//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// This file is a part of the 'gewt' project.
// Copyright 2016 Elmar Sonnenschein, esoco GmbH, Flensburg, Germany
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

import de.esoco.ewt.component.Button;
import de.esoco.ewt.component.Label;
import de.esoco.ewt.component.TextField;
import de.esoco.ewt.event.EWTEvent;
import de.esoco.ewt.event.EWTEventHandler;
import de.esoco.ewt.event.EventType;
import de.esoco.ewt.event.KeyCode;
import de.esoco.ewt.graphics.Color;
import de.esoco.ewt.layout.EdgeLayout;
import de.esoco.ewt.layout.FillLayout;
import de.esoco.ewt.layout.GridLayout;
import de.esoco.ewt.style.AlignedPosition;
import de.esoco.ewt.style.StyleData;
import de.esoco.ewt.style.StyleFlag;

import java.math.BigDecimal;
import java.math.RoundingMode;

import java.util.Stack;


/********************************************************************
 * A builder object that creates a calculator panel with a builder. The method
 * {@link #buildPanel(ContainerBuilder, StyleData)} must be invoked to create
 * the panel for the instance.
 *
 * @author eso
 */
public class CalculatorPanel implements EWTEventHandler
{
	//~ Static fields/initializers ---------------------------------------------

	private static final BigDecimal TEN     = new BigDecimal(10);
	private static final BigDecimal HUNDRED = new BigDecimal(100);

	private static final String CLEAR		  = "C";
	private static final String CLEAR_CURRENT = "CE";
	private static final String BACK		  = "<<";

	private static final String ADD		    = "+";
	private static final String SUBTRACT    = "-";
	private static final String MULTIPLY    = "*";
	private static final String DIVIDE	    = "/";
	private static final String PERCENT     = "%";
	private static final String INVERT	    = "1/x";
	private static final String SIGN	    = "+/-";
	private static final String SQUARE_ROOT = "sr";
	private static final String EQUALS	    = "=";
	private static final String DOT		    = ".";

	private static final String MEMORY_EXCHANCE = "MX";
	private static final String MEMORY_CLEAR    = "MC";
	private static final String MEMORY_RECALL   = "MR";
	private static final String MEMORY_STORE    = "MS";
	private static final String MEMORY_SUBTRACT = "M-";
	private static final String MEMORY_ADD	    = "M+";

	private static final String[] CONTROL_BUTTONS =
	{ MEMORY_EXCHANCE, MEMORY_CLEAR, CLEAR_CURRENT, CLEAR, BACK };

	private static final String[] MEMORY_BUTTONS =
	{ MEMORY_RECALL, MEMORY_STORE, MEMORY_SUBTRACT, MEMORY_ADD };

	private static final String[] DIGIT_BUTTONS =
	{ "7", "4", "1", "0", "8", "5", "2", DOT, "9", "6", "3" };

	private static final String[] FUNCTION_BUTTONS =
	{
		SIGN, DIVIDE, MULTIPLY, SUBTRACT, ADD, PERCENT, INVERT, SQUARE_ROOT,
		EQUALS
	};

	//~ Instance fields --------------------------------------------------------

	private TextField aDisplay;
	private Label     aMemoryLabel;

	private BigDecimal aCurrentValue = BigDecimal.ZERO;

	private BigDecimal aMemoryValue = BigDecimal.ZERO;
	private BigDecimal aInputDigit  = BigDecimal.ONE;

	private boolean bFractionInput;
	private boolean bEnterNewValue;

	private Stack<Operation> aOperationStack = new Stack<Operation>();

	//~ Static methods ---------------------------------------------------------

	/***************************************
	 * Calculates the square root of a {@link BigDecimal} value.
	 *
	 * @param  nValue  The value to calculate the square root of
	 * @param  nDigits The number of digits to calculate
	 *
	 * @return A big decimal containing the square root value
	 */
	public static BigDecimal sqrt(BigDecimal nValue, int nDigits)
	{
		BigDecimal zero		  = BigDecimal.ZERO.setScale(nDigits + 10);
		BigDecimal one		  = BigDecimal.ONE.setScale(nDigits + 10);
		BigDecimal two		  = new BigDecimal(2).setScale(nDigits + 10);
		BigDecimal nFinalDiff = one.movePointLeft(nDigits);
		BigDecimal nUpper     = nValue.compareTo(one) <= 0 ? one : nValue;
		BigDecimal nLower     = zero;
		BigDecimal nMiddle;
		boolean    bContinue;

		do
		{
			nMiddle = nLower.add(nUpper).divide(two, BigDecimal.ROUND_HALF_UP);

			BigDecimal nSquare = nMiddle.multiply(nMiddle);
			BigDecimal nDiff   = nValue.subtract(nSquare).abs();

			bContinue = nDiff.compareTo(nFinalDiff) > 0;

			if (bContinue)
			{
				if (nSquare.compareTo(nValue) < 0)
				{
					nLower = nMiddle;
				}
				else
				{
					nUpper = nMiddle;
				}
			}
		}
		while (bContinue);

		return nMiddle;
	}

	//~ Methods ----------------------------------------------------------------

	/***************************************
	 * Builds this calculator panel with the given builder.
	 *
	 * @param rBuilder The builder to create the panel with
	 * @param rStyle   The base style of the panel
	 */
	public void buildPanel(ContainerBuilder<?> rBuilder, StyleData rStyle)
	{
		int nGap = 3;

		rBuilder = rBuilder.addPanel(rStyle);

		StyleData rDisplayStyle     =
			StyleData.DEFAULT.setFlags(StyleFlag.HORIZONTAL_ALIGN_RIGHT,
									   StyleFlag.BEVEL_LOWERED,
									   StyleFlag.NO_BORDER);
		StyleData rMemoryLabelStyle =
			StyleData.DEFAULT.setFlags(StyleFlag.HORIZONTAL_ALIGN_CENTER,
									   StyleFlag.ETCHED_OUT);

		rBuilder =
			rBuilder.addPanel(AlignedPosition.TOP.setFlags(StyleFlag.BEVEL_RAISED),
							  new FillLayout(nGap));

		aDisplay     = rBuilder.addTextField(rDisplayStyle, "0.");
		aMemoryLabel = rBuilder.addLabel(rMemoryLabelStyle, "", null);

		aDisplay.setEditable(false);
		aDisplay.addEventListener(EventType.KEY_PRESSED, this);
		rBuilder = rBuilder.getParent();

		rBuilder =
			rBuilder.addPanel(AlignedPosition.CENTER,
							  new EdgeLayout(nGap, nGap * 2));
		rBuilder =
			rBuilder.addPanel(AlignedPosition.TOP,
							  new GridLayout(1, false, nGap));

		addButtons(rBuilder, CONTROL_BUTTONS, Color.toRGB(Color.RED));
		rBuilder = rBuilder.getParent();

		rBuilder =
			rBuilder.addPanel(AlignedPosition.CENTER,
							  new GridLayout(4, false, nGap));
		addButtons(rBuilder, MEMORY_BUTTONS, Color.toRGB(Color.RED));
		addButtons(rBuilder, DIGIT_BUTTONS, Integer.MIN_VALUE);
		addButtons(rBuilder, FUNCTION_BUTTONS, Color.toRGB(Color.BLUE));

		update(true);
	}

	/***************************************
	 * Returns the currentValue value.
	 *
	 * @return The currentValue value
	 */
	public final BigDecimal getValue()
	{
		return aCurrentValue;
	}

	/***************************************
	 * Handles all button events.
	 *
	 * @param rEvent The button event
	 */
	@Override
	public void handleEvent(EWTEvent rEvent)
	{
		String sFunction = null;

		if (rEvent.getType() == EventType.KEY_PRESSED)
		{
			if (rEvent.getKeyCode() != KeyCode.NONE)
			{
				sFunction = getKeyFunction(rEvent);
			}
		}
		else
		{
			sFunction = ((Button) rEvent.getSource()).getText();
		}

		if (sFunction != null)
		{
			performFunction(sFunction);
		}
	}

	/***************************************
	 * Sets the value of this calculator.
	 *
	 * @param rCurrentValue The new value
	 */
	public final void setValue(BigDecimal rCurrentValue)
	{
		aCurrentValue = rCurrentValue;
		update(true);
	}

	/***************************************
	 * Adds the buttons from a string array to the given container builder.
	 *
	 * @param  rBuilder The container builder
	 * @param  rLabels  The button labels
	 * @param  nColor   The button foreground color or {@link Integer#MIN_VALUE}
	 *                  for the default
	 *
	 * @return An array containing the buttons that have been created
	 */
	Button[] addButtons(ContainerBuilder<?> rBuilder,
						String[]			rLabels,
						int					nColor)
	{
		Button[] aButtons = new Button[rLabels.length];

		for (int i = 0; i < rLabels.length; i++)
		{
			// escape all labels to prevent expansion of property prefixes
			aButtons[i] =
				rBuilder.addButton(StyleData.DEFAULT, "~" + rLabels[i], null);

			aButtons[i].addEventListener(EventType.ACTION, this);
			aButtons[i].addEventListener(EventType.KEY_PRESSED, this);

			if (nColor != Integer.MIN_VALUE)
			{
				aButtons[i].setForegroundColor(Color.valueOf(nColor));
			}
		}

		return aButtons;
	}

	/***************************************
	 * Executes the topmost operations on the operation stack that have a
	 * certain minimum priority, starting with the given right value and
	 * returning the resulting value.
	 *
	 * @param  rRightValue  The right value to start with
	 * @param  nMinPriority The minimum priority
	 *
	 * @return The result of executing all operations
	 */
	BigDecimal executeOperations(BigDecimal rRightValue, int nMinPriority)
	{
		while (!aOperationStack.isEmpty() &&
			   aOperationStack.peek().getPriority() >= nMinPriority)
		{
			Operation rOperation = aOperationStack.pop();

			rRightValue = rOperation.execute(rRightValue);
		}

		return rRightValue;
	}

	/***************************************
	 * Returns the function associated with a certain key code.
	 *
	 * @param  rEvent The event containing the key code
	 *
	 * @return The function string for the event's key code
	 */
	String getKeyFunction(EWTEvent rEvent)
	{
		String sFunction;

		if (rEvent.getKeyCode() == KeyCode.STAR)
		{
			sFunction = DOT;
		}
		else if (rEvent.getKeyCode() == KeyCode.POUND)
		{
			sFunction = BACK;
		}
		else
		{
			sFunction = "" + rEvent.getKeyCode().getChar();
		}

		return sFunction;
	}

	/***************************************
	 * Performs a calculator function, which ranges from simple number input to
	 * mathematical operations.
	 *
	 * @param sFunction A string describing the function as defined in the class
	 *                  constants
	 */
	void performFunction(String sFunction)
	{
		char    cChar	    = sFunction.charAt(0);
		boolean bResetInput = true;

		if (sFunction.length() == 1 && cChar >= '0' && cChar <= '9')
		{
			BigDecimal aDigit = new BigDecimal(sFunction);

			if (bEnterNewValue)
			{
				aCurrentValue  = BigDecimal.ZERO;
				bEnterNewValue = false;
			}

			if (bFractionInput)
			{
				aInputDigit   = aInputDigit.divide(TEN);
				aCurrentValue = aCurrentValue.add(aInputDigit.multiply(aDigit));
			}
			else
			{
				aCurrentValue = aCurrentValue.multiply(TEN).add(aDigit);
			}

			bResetInput = false;
		}
		else if ("+-*/".indexOf(sFunction) >= 0)
		{
			aCurrentValue =
				executeOperations(aCurrentValue,
								  Operation.getOperatorPriority(cChar));

			Operation aOperation = new Operation(cChar, aCurrentValue);

			aOperationStack.push(aOperation);
		}
		else if (SIGN.equals(sFunction))
		{
			aCurrentValue = aCurrentValue.negate();
			bResetInput   = false;
		}
		else if (PERCENT.equals(sFunction))
		{
			if (aOperationStack.size() > 0)
			{
				Operation rTop = aOperationStack.peek();

				aCurrentValue =
					rTop.getLeftValue().multiply(aCurrentValue).divide(HUNDRED);
			}
		}
		else if (INVERT.equals(sFunction))
		{
			aCurrentValue = BigDecimal.ONE.divide(aCurrentValue);
		}
		else if (SQUARE_ROOT.equals(sFunction))
		{
			aCurrentValue = sqrt(aCurrentValue, 10);
		}
		else if (EQUALS.equals(sFunction))
		{
			aCurrentValue = executeOperations(aCurrentValue, 0);
		}
		else if (CLEAR.equals(sFunction) || CLEAR_CURRENT.equals(sFunction))
		{
			if (CLEAR.equals(sFunction))
			{
				aOperationStack.removeAllElements();
			}

			aCurrentValue = BigDecimal.ZERO;
		}
		else if (BACK.equals(sFunction))
		{
			if (!bEnterNewValue)
			{
				if (bFractionInput)
				{
					if (aInputDigit.compareTo(BigDecimal.ONE) < 0)
					{
						aInputDigit   = aInputDigit.multiply(TEN);
						aCurrentValue =
							aCurrentValue.divide(aInputDigit,
												 RoundingMode.HALF_UP);
						aCurrentValue = aCurrentValue.multiply(aInputDigit);
					}
					else
					{
						bFractionInput = false;
					}
				}
				else
				{
					aCurrentValue =
						aCurrentValue.divide(TEN, RoundingMode.HALF_UP);
				}
			}

			bResetInput = false;
		}
		else if (DOT.equals(sFunction))
		{
			bFractionInput = true;
			bResetInput    = false;
		}
		else if (MEMORY_STORE.equals(sFunction))
		{
			aMemoryValue = aCurrentValue;
		}
		else if (MEMORY_ADD.equals(sFunction))
		{
			aMemoryValue = aMemoryValue.add(aCurrentValue);
		}
		else if (MEMORY_SUBTRACT.equals(sFunction))
		{
			aMemoryValue = aMemoryValue.subtract(aCurrentValue);
		}
		else if (MEMORY_RECALL.equals(sFunction))
		{
			aCurrentValue = aMemoryValue;
		}
		else if (MEMORY_EXCHANCE.equals(sFunction))
		{
			BigDecimal tmp = aCurrentValue;

			aCurrentValue = aMemoryValue;
			aMemoryValue  = tmp;
		}
		else if (MEMORY_CLEAR.equals(sFunction))
		{
			aMemoryValue = BigDecimal.ZERO;
		}
		else
		{
			bResetInput = false;
		}

		update(bResetInput);
	}

	/***************************************
	 * Updates the display text field with the current value.
	 *
	 * @param bResetInput TRUE to reset all input parameters
	 */
	void update(boolean bResetInput)
	{
		if (bResetInput)
		{
			aInputDigit    = BigDecimal.ONE;
			bFractionInput = false;
			bEnterNewValue = true;
		}

		if (BigDecimal.ZERO.equals(aCurrentValue))
		{
			// remove a possible negative sign on a zero value
			aCurrentValue = BigDecimal.ZERO;
		}

		aDisplay.setText(aCurrentValue.toString());
		aMemoryLabel.setText(BigDecimal.ZERO.equals(aMemoryValue) ? "" : "M");
		aMemoryLabel.repaint();
		aDisplay.repaint();
	}

	//~ Inner Classes ----------------------------------------------------------

	/********************************************************************
	 * Inner class to encapsulate a mathematical operation.
	 */
	static class Operation
	{
		//~ Instance fields ----------------------------------------------------

		private char	   cOperator;
		private int		   nPriority;
		private BigDecimal rLeftValue;

		//~ Constructors -------------------------------------------------------

		/***************************************
		 * Creates a new instance with a certain operator and left value for the
		 * operation.
		 *
		 * @param cOperator  The operator character
		 * @param rLeftValue The left value for the operation
		 */
		Operation(char cOperator, BigDecimal rLeftValue)
		{
			this.cOperator  = cOperator;
			this.rLeftValue = rLeftValue;
			nPriority	    = getOperatorPriority(cOperator);
		}

		//~ Static methods -----------------------------------------------------

		/***************************************
		 * Returns the priority for a certain operator character.
		 *
		 * @param  cOperator The operator character
		 *
		 * @return The operation priority
		 *
		 * @throws IllegalArgumentException If the operator character is unknown
		 */
		static int getOperatorPriority(char cOperator)
		{
			switch (cOperator)
			{
				case '-':
				case '+':
					return 1;

				case '/':
				case '*':
					return 2;

				default:
					throw new IllegalArgumentException("Unknown operator: " +
													   cOperator);
			}
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
			switch (cOperator)
			{
				case '/':
					return rLeftValue.divide(rRightValue);

				case '*':
					return rLeftValue.multiply(rRightValue);

				case '-':
					return rLeftValue.subtract(rRightValue);

				case '+':
					return rLeftValue.add(rRightValue);

				default:
					throw new IllegalStateException("Undefined operation: " +
													cOperator);
			}
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
			return nPriority;
		}
	}
}
