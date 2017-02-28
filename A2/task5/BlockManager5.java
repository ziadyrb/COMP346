// Import (aka include) some stuff.
package task5;
import common.*;

/**
 * Class BlockManager
 * Implements character block "manager" and does twists with threads.
 *
 * @author Serguei A. Mokhov, mokhov@cs.concordia.ca;
 * Inspired by previous code by Prof. D. Probst
 *
 * $Revision: 1.4 $
 * $Last Revision Date: 2012/11/10 $

 */
public class BlockManager5
{
	/**
	 * The stack itself
	 */
	private static BlockStack soStack = new BlockStack();

	/**
	 * Number of threads dumping stack
	 */
	private static final int NUM_PROBERS = 4;

	/**
	 * Number of steps they take
	 */
	private static int siThreadSteps = 5;

	/**
	 * For atomicity
	 */
	private static Semaphore mutex = new Semaphore(1);

	/*
	 * For synchronization
	 */

	/**
	 * s1 is to make sure phase I for all is done before any phase II begins
	 */
	private static Semaphore s1 = new Semaphore(-9); //require 10 signal operations before any thread's phase two starts

	/**
	 * s2 is for use in conjunction with Thread.turnTestAndSet() for phase II proceed
	 * in the thread creation order
	 */
	private static Semaphore s2 = new Semaphore(1);


	// The main()
	public static void main(String[] argv)
	{
		try
		{
			// Some initial stats...
			System.out.println("Main thread starts executing.");
			System.out.println("Initial value of top = " + soStack.getITop() + ".");
			System.out.println("Initial value of stack top = " + soStack.pick() + ".");
			System.out.println("Main thread will now fork several threads.");

			/*
			 * The birth of threads
			 */
			AcquireBlock ab1 = new AcquireBlock();
			AcquireBlock ab2 = new AcquireBlock();
			AcquireBlock ab3 = new AcquireBlock();

			System.out.println("main(): Two AcquireBlock threads have been created.");

			ReleaseBlock rb1 = new ReleaseBlock();
			ReleaseBlock rb2 = new ReleaseBlock();
			ReleaseBlock rb3 = new ReleaseBlock();

			System.out.println("main(): Two ReleaseBlock threads have been created.");

			// Create an array object first
			CharStackProber	aStackProbers[] = new CharStackProber[NUM_PROBERS];

			// Then the CharStackProber objects
			for(int i = 0; i < NUM_PROBERS; i++)
				aStackProbers[i] = new CharStackProber();

			System.out.println("main(): CharStackProber threads have been created: " + NUM_PROBERS);

			/*
			 * Twist 'em all
			 */
			BaseThread.setInitialTurn(10);
			ab1.start();
			aStackProbers[0].start();
			rb1.start();
			aStackProbers[1].start();
			ab2.start();
			aStackProbers[2].start();
			rb2.start();
			ab3.start();
			aStackProbers[3].start();
			rb3.start();

			System.out.println("main(): All the threads are ready.");

			/*
			 * Wait by here for all forked threads to die
			 */
			ab1.join();
			ab2.join();

			rb1.join();
			rb2.join();

			for(int i = 0; i < NUM_PROBERS; i++)
				aStackProbers[i].join();

			// Some final stats after all the child threads terminated...
			System.out.println("System terminates normally.");
			System.out.println("Final value of top = " + soStack.getITop() + ".");
			System.out.println("Final value of stack top = " + soStack.pick() + ".");
			System.out.println("Final value of stack top-1 = " + soStack.getAt(soStack.getITop() - 1) + ".");
			System.out.println("Stack access count = " + soStack.getAccessCounter());

			System.exit(0);
		}
		catch(InterruptedException e)
		{
			System.err.println("Caught InterruptedException (internal error): " + e.getMessage());
			e.printStackTrace(System.err);
		}
		catch(Exception e)
		{
			reportException(e);
		}
		finally
		{
			System.exit(1);
		}
	} // main()


	/**
	 * Inner AcquireBlock thread class.
	 */
	static class AcquireBlock extends BaseThread
	{
		/**
		 * A copy of a block returned by pop().
                 * @see BlocStack#pop()
		 */
		private char cCopy;

		public void run()
		{
			mutex.P(); // Add line. 
			System.out.println("AcquireBlock thread [TID=" + this.iTID + "] starts executing.");


			phase1();


			try
			{
				System.out.println("AcquireBlock thread [TID=" + this.iTID + "] requests Ms block.");

				this.cCopy = soStack.pop();

				System.out.println
				(
					"AcquireBlock thread [TID=" + this.iTID + "] has obtained Ms block " + this.cCopy +
					" from position " + (soStack.getITop() + 1) + "."
				);


				System.out.println
				(
					"Acq[TID=" + this.iTID + "]: Current value of top = " +
					soStack.getITop() + "."
				);

				System.out.println
				(
					"Acq[TID=" + this.iTID + "]: Current value of stack top = " +
					soStack.pick() + "."
				);
			}
			catch(Exception e)
			{
				reportException(e);
				System.exit(1);
			}
			finally
			{
				mutex.V(); //release
			}
			
			s1.V(); 
			s1.P(); //  assure phase 2 cannot start before all phase 1's have completed
			s1.V(); // signal to ensure that all phase one's  have passed
			s2.P(); // wait for access to binary semaphore
			while (!turnTestAndSet(false)) {

				s2.V();
				s2.P();
			}
			phase2();
			s2.V(); // release access to binary semaphore

			System.out.println("AcquireBlock thread [TID=" + this.iTID + "] terminates.");
		}
	} // class AcquireBlock


	/**
	 * Inner class ReleaseBlock.
	 */
	static class ReleaseBlock extends BaseThread
	{
		/**
		 * Block to be returned. Default is 'a' if the stack is empty.
		 */
		private char cBlock = 'a';

		public void run()
		{
			mutex.P();	// Add line
			System.out.println("ReleaseBlock thread [TID=" + this.iTID + "] starts executing.");


			phase1();


			try
			{
				if(soStack.isEmpty() == false)
					this.cBlock = (char)(soStack.pick() + 1);


				System.out.println
				(
					"ReleaseBlock thread [TID=" + this.iTID + "] returns Ms block " + this.cBlock +
					" to position " + (soStack.getITop() + 1) + "."
				);

				soStack.push(this.cBlock);

				System.out.println
				(
					"Rel[TID=" + this.iTID + "]: Current value of top = " +
					soStack.getITop() + "."
				);

				System.out.println
				(
					"Rel[TID=" + this.iTID + "]: Current value of stack top = " +
					soStack.pick() + "."
				);
			}
			catch(Exception e)
			{
				reportException(e);
				System.exit(1);
			}
			finally 
			{
				mutex.V(); //release
			}
			
			s1.V(); 
			s1.P(); //  assure phase 2 cannot start before all phase 1's have completed
			s1.V(); // signal to ensure that all phase one's  have passed
			s2.P(); // wait for access to binary semaphore
			while (!turnTestAndSet(false)) {

				s2.V();
				s2.P();
			}
			phase2();
			s2.V(); // release access to binary semaphore
			
			System.out.println("ReleaseBlock thread [TID=" + this.iTID + "] terminates.");
		}
	} // class ReleaseBlock


	/**
	 * Inner class CharStackProber to dump stack contents.
	 */
	static class CharStackProber extends BaseThread
	{
		public void run()
		{
			mutex.P(); // Add line.
			phase1();


			try
			{
				for(int i = 0; i < siThreadSteps; i++)
				{
					System.out.print("Stack Prober [TID=" + this.iTID + "]: Stack state: ");

					// [s] - means ordinay slot of a stack
					// (s) - current top of the stack
					for(int s = 0; s < soStack.getISize(); s++)
						System.out.print
						(
							(s == BlockManager5.soStack.getITop() ? "(" : "[") +
							BlockManager5.soStack.getAt(s) +
							(s == BlockManager5.soStack.getITop() ? ")" : "]")
						);

					System.out.println(".");

				}
			}
			catch(Exception e)
			{
				reportException(e);
				System.exit(1);
			}
			finally 
			{
				mutex.V(); //release
			}

			s1.V(); 
			s1.P(); //  assure phase 2 cannot start before all phase 1's have completed
			s1.V(); // signal to ensure that all phase one's  have passed
			s2.P(); // wait for access to binary semaphore
			while (!turnTestAndSet(false)) {

				s2.V();
				s2.P();
			}
			phase2();
			s2.V(); // release access to binary semaphore

		}
	} // class CharStackProber


	/**
	 * Outputs exception information to STDERR
	 * @param poException Exception object to dump to STDERR
	 */
	private static void reportException(Exception poException)
	{
		System.err.println("Caught exception : " + poException.getClass().getName());
		System.err.println("Message          : " + poException.getMessage());
		System.err.println("Stack Trace      : ");
		poException.printStackTrace(System.err);
	}
} // class BlockManager

// EOF
