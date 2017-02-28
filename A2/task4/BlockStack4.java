package task4;
import common.*;

/**
 * Class BlockStack
 * Implements character block stack and operations upon it.
 *
 * $Revision: 1.4 $
 * $Last Revision Date: 2012/10/11 $
 *
 * @author Serguei A. Mokhov, mokhov@cs.concordia.ca;
 * Inspired by an earlier code by Prof. D. Probst

 */
class BlockStack
{
	/**
	 * # of letters in the English alphabet + 2
	 */
	public static final int MAX_SIZE = 28;

	/**
	 * Default stack size
	 */
	public static final int DEFAULT_SIZE = 6;

	/**
	 * Current size of the stack
	 */
	private int iSize = DEFAULT_SIZE; //change to private

	/**
	 * Current top of the stack
	 */
	private int iTop  = 3; // change to private
	
	/**
     * Stack Access Counter, increments with each stack access
     */
	private int accessCounter = 0; //change to private
	
	/**
	 * accessor method
	 */
	public int getAccessCounter() {
		return accessCounter;
	}
	
	/**
	 * accessor method
	 */
	public int getITop() {
		return iTop;
	}
	
	/**
	 * accessor method
	 */
	public int getISize() {
		return iSize;
	}
	
	/**
	 * method to determine if stack is empty.
	 * @return True if stack is empty, false otherwise.
	 */
	public boolean isEmpty()
	{
		return (this.iTop == -1);
	}

	/**
	 * stack[0:5] with four defined values
	 */
	private char acStack[] = new char[] {'a', 'b', 'c', 'd', '$', '$'}; //change to private

	/**
	 * Default constructor
	 */
	public BlockStack()
	{
	}

	/**
	 * Supplied size
	 */
	public BlockStack(final int piSize)
	{


                if(piSize != DEFAULT_SIZE)
		{
			this.acStack = new char[piSize];

			// Fill in with letters of the alphabet and keep
			// 2 free blocks
			for(int i = 0; i < piSize - 2; i++)
				this.acStack[i] = (char)('a' + i);

			this.acStack[piSize - 2] = this.acStack[piSize - 1] = '$';

			this.iTop = piSize - 3;
                        this.iSize = piSize;
		}
	}

	/**
	 * Picks a value from the top without modifying the stack
	 * @return top element of the stack, char
	 */
	public char pick() throws DefinedException
	{
		accessCounter++;
		if ( !isEmpty())	
			return this.acStack[this.iTop];
		else
			throw new DefinedException("Error: Stack is empty."); // Stack empty exception
	}

	/**
	 * Returns arbitrary value from the stack array
	 * @return the element, char
	 */
	public char getAt(final int piPosition) throws DefinedException
		{
			accessCounter++;
		if ( piPosition <= iSize - 1)
			return this.acStack[piPosition];
		else
			throw new DefinedException("Error: Can't access a position greater than the stack size."); //index out of bound exception
		
	}

	/**
	 * Standard push operation
	 */
	public void push(final char pcBlock) throws DefinedException
	{
		accessCounter++;
		if ( iTop < iSize)
		{
			if (isEmpty())
				this.acStack[++this.iTop] = 'a';
			else
				this.acStack[++this.iTop] = pcBlock;
		}
		else
			throw new DefinedException("Error: Stack is full."); // Stack full exception
	}

	/**
	 * Standard pop operation
	 * @return ex-top element of the stack, char
	 */
	public char pop() throws DefinedException
	{
		accessCounter++;
		if ( !isEmpty())
		{
			char cBlock = this.acStack[this.iTop];
			this.acStack[this.iTop--] = '$'; // Leave prev. value undefined
			return cBlock;
		}
		else
			throw new DefinedException("Error: Stack is empty."); //Empty stack exception
	}
}

// EOF
