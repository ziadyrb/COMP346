/**
 * Class DiningPhilosophers
 * The main starter.
 *
 * @author Serguei A. Mokhov, mokhov@cs.concordia.ca
 */
public class DiningPhilosophers
{
	/*
	 * ------------
	 * Data members
	 * ------------
	 */

	/**
	 * This default may be overridden from the command line
	 */
	public static final int DEFAULT_NUMBER_OF_PHILOSOPHERS = 4;

	/**
	 * Dining "iterations" per philosopher thread
	 * while they are socializing there
	 */
	public static final int DINING_STEPS = 10;

	/**
	 * Our shared monitor for the philosphers to consult
	 */
	public static Monitor soMonitor = null;

	/*
	 * -------
	 * Methods
	 * -------
	 */

	/**
	 * Main system starts up right here
	 */
	public static void main(String[] argv)
	{
		try
		{
			/*
			 * TODO:
			 * Should be settable from the command line
			 * or the default if no arguments supplied.
			 */
			//TASK 3 follows: changes to make the program accept a variable number of philosophers at the table
			int iPhilosophers;
			boolean validInput = true; //to keep track if the input is valid or not (# of philosophers at the table)
			try  
			{ 
				if (argv.length != 0) //if the value is changed in the command line
					if (Integer.parseInt(argv[0]) < 0)//using parseInt to check if the input is a valid positive integer
						throw new Exception();//throw an exception if the value is not valid (positive integer)
			}
			catch (Exception e) {
				validInput = false; //setting the validInput to false
				System.out.println("The # of philosophers should be a positive integer. Invalid input.");//Telling the user that the # of philosophers in invalid
				System.out.println("% java DiningPhilosophers " + argv + " is not a positive decimal integer Usage: java DiningPhilosophers [4] %"); 
				//printing that the default value will be used
			}  
			if (validInput) //if the input is valid, continue with the rest of the code
			{
				if(argv.length == 0)
					iPhilosophers = DEFAULT_NUMBER_OF_PHILOSOPHERS;//setting it to the default number if it was not changed from the command line
				else
					iPhilosophers = Integer.parseInt(argv[0]); //if the input was changed, parsing it into integer and setting iPhilosophers to that int value
			}
			else 
				iPhilosophers = DEFAULT_NUMBER_OF_PHILOSOPHERS;//if the input was false, setting it to the default value after telling the user above

			// Make the monitor aware of how many philosophers there are
			soMonitor = new Monitor(iPhilosophers);

			// Space for all the philosophers
			Philosopher aoPhilosophers[] = new Philosopher[iPhilosophers];


			System.out.println
			(
					iPhilosophers +
					" philosopher(s) came in for a dinner." //printing out the # of philosophers to the table today
					); 

			// sit down
			for(int j = 0; j < iPhilosophers; j++)
			{
				aoPhilosophers[j] = new Philosopher();
				aoPhilosophers[j].start();
			}


			// Main waits for all its children to die...
			// I mean, philosophers to finish their dinner.
			for(int j = 0; j < iPhilosophers; j++)
				aoPhilosophers[j].join();

			System.out.println("All philosophers have left. System terminates normally.");

		}
		catch(InterruptedException e)
		{
			System.err.println("main():");
			reportException(e);
			System.exit(1);
		}	

	} // main()

	/**
	 * Outputs exception information to STDERR
	 * @param poException Exception object to dump to STDERR
	 */
	public static void reportException(Exception poException)
	{
		System.err.println("Caught exception : " + poException.getClass().getName());
		System.err.println("Message          : " + poException.getMessage());
		System.err.println("Stack Trace      : ");
		poException.printStackTrace(System.err);
	}
}

// EOF
