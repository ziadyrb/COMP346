/**
 * Class Monitor
 * To synchronize dining philosophers.
 *
 * @author Serguei A. Mokhov, mokhov@cs.concordia.ca
 */
public class Monitor
{
	/*
	 * ------------
	 * Data members
	 * ------------
	 */
	 int phTalkingId = 0;
	 int piNumberOfPhilosophers;
	 public enum PhState {Eating, Thinking, Talking};
	 PhState phSates[];
	/**
	 * Constructor
	 */
	public Monitor(int piNumberOfPhilosophers)
	{
		// TODO: set appropriate number of chopsticks based on the # of philosophers
		this.piNumberOfPhilosophers = piNumberOfPhilosophers;
		phSates = new PhState[piNumberOfPhilosophers];
		for (int i = 0; i < piNumberOfPhilosophers; i++) {
			phSates[i] = PhState.Thinking;
	    }
	}

	/*
	 * -------------------------------
	 * User-defined monitor procedures
	 * -------------------------------
	 */

	/**
	 * Grants request (returns) to eat when both chopsticks/forks are available.
	 * Else forces the philosopher to wait()
	 * @throws InterruptedException 
	 */
	public synchronized void pickUp(final int piTID) throws InterruptedException
	{
		// Check if the left or right philosophers are eating, if they're then wait for them to be finish
		int index = piTID - 1;
		while (phSates[Math.abs((index - 1)) % piNumberOfPhilosophers ] == PhState.Eating || 
				phSates[(index + 1) % piNumberOfPhilosophers] == PhState.Eating)
		{
			wait();
		}
		phSates[index] = PhState.Eating; 		
	}

	/**
	 * When a given philosopher's done eating, they put the chopstiks/forks down
	 * and let others know they are available.
	 */
	public synchronized void putDown(final int piTID)
	{
		phSates[piTID - 1] = PhState.Thinking; // Put down the chopsticks by going back to the thinking state
		notifyAll();
	}

	/**
	 * Only one philopher at a time is allowed to philosophy
	 * (while she is not eating).
	 * @throws InterruptedException 
	 */
	public synchronized void requestTalk(final int piTID) throws InterruptedException 
	{
		while(phTalkingId != 0) // Check to see if anyone else is talking
			wait();
		phTalkingId = piTID; // Save the Id so we don't have to search the array to see if another philosopher is currently talking
		phSates[piTID - 1] = PhState.Talking;
	}

	/**
	 * When one philosopher is done talking stuff, others
	 * can feel free to start talking.
	 */
	public synchronized void endTalk(final int piTID)
	{
		phTalkingId = 0;
		phSates[piTID - 1] = PhState.Thinking;
		notifyAll();
	}
}

// EOF
