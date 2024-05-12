package components;

/**
 * Mainly used to modify parameters to create components for each CVM 
 */
public final class Config {

		// Register
		public static final String uriInPortRegister="URI_RegisterPortIn"; /* Register InboundPort URI for Node-Register connexion */
		public static final String registerClInURI = "URI_Register_ClientPortIn"; /* Register Ports for Client-Register connexion */
		public static String uriRegistration = "URI_Register";
		
		// Clock
		public static final String TEST_CLOCK_URI = "test-clock";
		public static final double ACCELERATION_FACTOR = 60.0;
		
		/** Number of threads for each pool */
		/* Node */
		public static final int NTHREADS_NEW_REQ_POOL = 7;
		public static final int NTHREADS_CONT_REQ_POOL = 7;
		public static final int NTHREADS_SYNC_REQ_POOL = 7;
		public static final int NTHREADS_CONNECTION_POOL = 7;
		/* Register */
		public static final int NTHREADS_REGISTER_POOL = 5;
		public static final int NTHREADS_LOOKUP_POOL = 5;

		public static final int nbNodes = 50;
		/** Node waiting time before creation */
		public static final int timeN = 60;
		/** Client waiting time before creation */
		public static final int timeC = 20 * nbNodes;
		
		/* Delay after which the Client will merge and print the results */
	    public static final long CLIENT_DELAY = 5; 
}
