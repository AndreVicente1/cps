package fr.sorbonne_u.cps.replication.examples.components;

// Copyright Jacques Malenfant, Sorbonne Universite.
// Jacques.Malenfant@lip6.fr
//
// This software is a computer program whose purpose is to provide a
// basic component programming model to program with components
// distributed applications in the Java programming language.
//
// This software is governed by the CeCILL-C license under French law and
// abiding by the rules of distribution of free software.  You can use,
// modify and/ or redistribute the software under the terms of the
// CeCILL-C license as circulated by CEA, CNRS and INRIA at the following
// URL "http://www.cecill.info".
//
// As a counterpart to the access to the source code and  rights to copy,
// modify and redistribute granted by the license, users are provided only
// with a limited warranty  and the software's author,  the holder of the
// economic rights,  and the successive licensors  have only  limited
// liability. 
//
// In this respect, the user's attention is drawn to the risks associated
// with loading,  using,  modifying and/or developing or reproducing the
// software by the user in light of its specific status of free software,
// that may mean  that it is complicated to manipulate,  and  that  also
// therefore means  that it is reserved for developers  and  experienced
// professionals having in-depth computer knowledge. Users are therefore
// encouraged to load and test the software's suitability as regards their
// requirements in conditions enabling the security of their systems and/or 
// data to be ensured and,  more generally, to use and operate it in the 
// same conditions as regards security. 
//
// The fact that you are presently reading this means that you have had
// knowledge of the CeCILL-C license and that you accept its terms.

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.cps.replication.components.StateBasedDispatcher;
import fr.sorbonne_u.cps.replication.connection.NotificationsConnector;
import fr.sorbonne_u.cps.replication.connection.NotificationsOutboundPort;
import fr.sorbonne_u.cps.replication.connection.ReplicableInboundPortForThreadPool;
import fr.sorbonne_u.cps.replication.interfaces.NotificationsCI;
import fr.sorbonne_u.cps.replication.interfaces.ReplicaI;
import fr.sorbonne_u.cps.replication.interfaces.ReplicableCI;
import fr.sorbonne_u.cps.replication.interfaces.NotificationsCI.StateI;
import java.util.ArrayList;
import java.util.Random;

// -----------------------------------------------------------------------------
/**
 * The class <code>MonitoredServer</code> implements a request server that
 * monitors its request queue size and notify a dispatcher that selects
 * servers to dispatch them request depending on their queue sizes.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * In order to get the queue size, this component creates a customised pool
 * of thread with a known queue object. This queue object will be sensed by the
 * method <code>notifyState</code> that will send the information to the
 * dispatcher. <code>notifyState</code> is called by a task that is scheduled
 * on a dedicated schedulable pool of one thread that will executed it at a
 * fixed rate during its whole execution.
 * </p>
 * 
 * <p><strong>White-box Invariant</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p><strong>Black-box Invariant</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p>Created on : 2020-03-23</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
// -----------------------------------------------------------------------------
@OfferedInterfaces(offered = {ReplicableCI.class})
@RequiredInterfaces(required = {NotificationsCI.class})
// -----------------------------------------------------------------------------
public class			MonitoredServer
extends		Server
implements	ReplicaI<String>
{
	// -------------------------------------------------------------------------
	// Inner classes
	// -------------------------------------------------------------------------

	/**
	 * The class <code>MonitoredServerExecutorServiceFactory</code> implements
	 * the executor service factory that will be used to create the pool
	 * of threads used to executed the requests on this server.
	 *
	 * <p><strong>Description</strong></p>
	 * 
	 * <p>
	 * At creation time, this factory is passed a reference to a queue that
	 * will be used to queue requests for execution; this reference will also
	 * be used by the monitoring process sending regularly its size to the
	 * request dispatcher.
	 * </p>
	 * 
	 * <p><strong>White-box Invariant</strong></p>
	 * 
	 * <pre>
	 * invariant	{@code true}	// no more invariant
	 * </pre>
	 * 
	 * <p><strong>Black-box Invariant</strong></p>
	 * 
	 * <pre>
	 * invariant	{@code true}	// no more invariant
	 * </pre>
	 * 
	 * <p>Created on : 2020-03-24</p>
	 * 
	 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
	 */
	protected static class	MonitoredServerExecutorServiceFactory
	implements	ExecutorServiceFactory
	{
		/** the queue object used to queue tasks for the pool.				*/
		protected BlockingQueue<Runnable>	poolQueue;

		/**
		 * create the factory with the given queue object.
		 * 
		 * <p><strong>Contract</strong></p>
		 * 
		 * <pre>
		 * pre	{@code poolQueue != null && poolQueue.isEmpty()}
		 * post	{@code true}	// no postcondition.
		 * </pre>
		 *
		 * @param poolQueue	the queue object to be used by the pool.
		 */
		public			MonitoredServerExecutorServiceFactory(
			BlockingQueue<Runnable> poolQueue
			)
		{
			super();
			assert	poolQueue != null && poolQueue.isEmpty();
			this.poolQueue = poolQueue;
		}

		/**
		 * @see fr.sorbonne_u.components.AbstractComponent.ExecutorServiceFactory#createExecutorService(int)
		 */
		@Override
		public			ExecutorService createExecutorService(int nbThreads)
		{
			return new ThreadPoolExecutor(
							nbThreads,		// Fixed number of threads
							nbThreads,
							0L,				// No destructions of threads
							TimeUnit.MILLISECONDS,
							this.poolQueue	// the task queue for this pool
							);
		}
	}

	/**
	 * The class <code>ServerState</code> implements the way the state
	 * information about this server will be sent to the dispatcher.
	 *
	 * <p><strong>Description</strong></p>
	 * 
	 * <p><strong>White-box Invariant</strong></p>
	 * 
	 * <pre>
	 * invariant	{@code true}	// no more invariant
	 * </pre>
	 * 
	 * <p><strong>Black-box Invariant</strong></p>
	 * 
	 * <pre>
	 * invariant	{@code true}	// no more invariant
	 * </pre>
	 * 
	 * <p>Created on : 2020-03-24</p>
	 * 
	 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
	 */
	public final class	ServerState
	implements	StateI
	{
		/** URI of the inbound port of this server (known to the dispatcher,
		 *  so it is used to uniquely identify the server).					*/
		protected final String	inboundPortURI;
		/** current size of the tasks queue.								*/
		protected final int		queueSize;

		/**
		 * create the state object.
		 * 
		 * <p><strong>Contract</strong></p>
		 * 
		 * <pre>
		 * pre	{@code inboundPortURI != null && !inboundPortURI.isEmpty()}
		 * pre	{@code queueSize >= 0}
		 * post	{@code true}	// no postcondition.
		 * </pre>
		 *
		 * @param inboundPortURI	URI of the inbound port of this server (known to the dispatcher).
		 * @param queueSize			current size of the tasks queue.
		 */
		public				ServerState(
			String inboundPortURI,
			int queueSize
			)
		{
			super();

			assert	inboundPortURI != null && !inboundPortURI.isEmpty();
			assert	queueSize >= 0;

			this.inboundPortURI = inboundPortURI;
			this.queueSize = queueSize;
		}

		/**
		 * return the URI of the inbound port of this server.
		 * 
		 * <p><strong>Contract</strong></p>
		 * 
		 * <pre>
		 * pre	{@code true}	// no precondition.
		 * post	{@code ret != null && !ret.isEmpty()}
		 * </pre>
		 *
		 * @return	the URI of the inbound port of this server.
		 */
		public String 		getInboundPortURI()	{ return inboundPortURI; }

		/**
		 * return the current size of the tasks queue.
		 * 
		 * <p><strong>Contract</strong></p>
		 * 
		 * <pre>
		 * pre	{@code true}	// no precondition.
		 * post	{@code ret >= 0}
		 * </pre>
		 *
		 * @return	the current size of the tasks queue.
		 */
		public int			getQueueSize()		{ return this.queueSize; }
	}

	// Statistics

	/**
	 * The class <code>SizeChange</code> records a size change in the request
	 * queue with the time at which this change happen.
	 * 
	 * <p><strong>Description</strong></p>
	 * 
	 * <p><strong>White-box Invariant</strong></p>
	 * 
	 * <pre>
	 * invariant	{@code true}	// no more invariant
	 * </pre>
	 * 
	 * <p><strong>Black-box Invariant</strong></p>
	 * 
	 * <pre>
	 * invariant	{@code true}	// no more invariant
	 * </pre>
	 * 
	 * <p>Created on : 2020-03-25</p>
	 * 
	 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
	 */
	protected static class	SizeChange
	{
		public int	size;
		public long	time;

		public SizeChange(int size, long time) {
			super();
			this.size = size;
			this.time = time;
		}
	}

	// -------------------------------------------------------------------------
	// Component constants and variables
	// -------------------------------------------------------------------------

	/** URI of the pool of threads used to execute the requests.			*/
	protected final String			EXECUTION_POOL_URI = "tasks";
	/** URI of the pool of threads used to execute the notifications.		*/
	protected final String			NOTIFICATION_POOL_URI = "notifications";
	/** the period (in milliseconds) of the notifications.					*/
	protected final long			NOTIFICATION_PERIOD = 100L;

	/** outbound port used to send the notifications to the dispatcher.		*/
	protected NotificationsOutboundPort notificationsOutboundPort;
	/** scheduled future allowing to cancel the notification task.	*/
	protected ScheduledFuture<?>		sf;

	/** the queue used by the pool of threads executing the requests.		*/
	protected BlockingQueue<Runnable>	poolQueue;

	// Service time simulation

	/** a random number generator to produce long and varying request
	 *  processing durations to simulate real executions.					*/
	protected Random					rg;
	/** mean processing time of a request for this server.					*/
	protected final	double				mean;
	/** standard deviation of the processing time of a request for this
	 *  server.																*/
	protected final double				standardDeviation;

	// Statistics

	/** list of size changes that will be accumulated during the execution;
	 *  beware that recording all data may be impracticable when a too large
	 *  number of data is generated.									 	*/
	protected final ArrayList<SizeChange>	data = new ArrayList<>();
	/** current size of the request queue.									*/
	protected int							currentSize = 0;
	/** number of serviced requests during the current run.					*/
	protected AtomicLong					numberOfServicedRequests;

	// -------------------------------------------------------------------------
	// Component constructors
	// -------------------------------------------------------------------------

	/**
	 * create a monitored server.
	 * 
	 * <p><strong>Description</strong></p>
	 * 
	 * <p>
	 * Generating a gaussian random duration is not very good because there is
	 * always a possibility to get a negative duration. The following assertion
	 * tries to force a distribution far from 0 to avoid negative random
	 * durations:
	 * </p>
	 * <p>{@code mean > 2.0 * standardDeviation}</p>
	 * <p>
	 * Yet the code must still test and correct the generated number if it is
	 * negative. These corrections make the actual distribution not to be
	 * Gaussian anymore, so results cannot be considered a statistically good
	 * enough for analysis. Another non-negative distribution would need to be
	 * used.
	 * </p>
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code inboundPortURI != null && !inboundPortURI.isEmpty()}
	 * pre	{@code position > 0}
	 * pre	{@code mean > 0.0}
	 * pre	{@code standardDeviation > 0.0}
	 * pre	{@code mean > 2.0 * standardDeviation}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param inboundPortURI	URI of the inbound port used to offer the service.
	 * @param position			a relative position for the trace window of the component.
	 * @param mean				mean processing time of a request for this server.
	 * @param standardDeviation	standard deviation of the processing time of a request for this server.
	 * @throws Exception		<i>to do</i>.
	 */
	protected			MonitoredServer(
		String inboundPortURI,
		int position,
		double mean,
		double standardDeviation
		) throws Exception
	{
		super(inboundPortURI, position);

		assert	mean > 0.0;
		assert	standardDeviation > 0.0;
		assert	mean > 2.0 * standardDeviation;

		this.mean = mean;
		this.standardDeviation = standardDeviation;
	}

	/**
	 * create a monitored server.
	 * 
	 * <p>
	 * Generating a gaussian random duration is not very good because there is
	 * always a possibility to get a negative duration. The following assertion
	 * tries to force a distribution far from 0 to avoid negative random
	 * durations:
	 * </p>
	 * <p>{@code mean > 2.0 * standardDeviation}</p>
	 * <p>
	 * Yet the code must still test and correct the generated number if it is
	 * negative. These corrections make the actual distribution not to be
	 * Gaussian anymore, so results cannot be considered a statistically good
	 * enough for analysis. Another non-negative distribution would need to be
	 * used.
	 * </p>
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code reflectionInboundPortURI != null && !reflectionInboundPortURI.isEmpty()}
	 * pre	{@code inboundPortURI != null && !inboundPortURI.isEmpty()}
	 * pre	{@code position > 0}
	 * pre	{@code mean > 0.0}
	 * pre	{@code standardDeviation > 0.0}
	 * pre	{@code mean > 2.0 * standardDeviation}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param reflectionInboundPortURI	URI of the reflection inbound port of this component.
	 * @param inboundPortURI	URI of the inbound port used to offer the service.
	 * @param position			a relative position for the trace window of the component.
	 * @param mean				mean processing time of a request for this server.
	 * @param standardDeviation	standard deviation of the processing time of a request for this server.
	 * @throws Exception		<i>to do</i>.
	 */
	protected			MonitoredServer(
		String reflectionInboundPortURI,
		String inboundPortURI,
		int position,
		double mean,
		double standardDeviation
		) throws Exception
	{
		super(reflectionInboundPortURI, inboundPortURI, position);

		assert	mean > 0.0;
		assert	standardDeviation > 0.0;
		assert	mean > 2.0 * standardDeviation;

		this.mean = mean;
		this.standardDeviation = standardDeviation;
	}

	/**
	 * @see fr.sorbonne_u.cps.replication.examples.components.Server#initialise(java.lang.String, int)
	 */
	@Override
	protected void		initialise(String inboundPortURI, int position)
	throws Exception
	{
		// Create the queue for the pool of threads executing the requests with
		// a redefinition of the methods offer and tale to gather statistics
		this.poolQueue =
			new LinkedBlockingQueue<Runnable>() {
				private static final long serialVersionUID = 1L;

				/**
				 * @see java.util.concurrent.LinkedBlockingQueue#offer(java.lang.Object)
				 */
				@Override
				public boolean offer(Runnable e) {
					long t = System.currentTimeMillis();
					synchronized (data) {
						data.add(new SizeChange(++currentSize, t));
					}
					return super.offer(e);
				}

				/**
				 * @see java.util.concurrent.LinkedBlockingQueue#take()
				 */
				@Override
				public Runnable take() throws InterruptedException {
					long t = System.currentTimeMillis();
					synchronized (data) {
						currentSize--;
						data.add(new SizeChange(
									currentSize < 0 ? 0 : currentSize, t));
					}
					return super.take();
				}
			};

		// Create the factory that will create the pool.
		ExecutorServiceFactory esf =
					new MonitoredServerExecutorServiceFactory(this.poolQueue);
		// Create the pool of threads using the above factory.
		this.createNewExecutorService(EXECUTION_POOL_URI, 1, esf);

		// Create a schedulable pool of threads for the notifications.
		this.createNewExecutorService(NOTIFICATION_POOL_URI, 1, true);

		// Initialise the random number generator
		this.rg = new Random(System.currentTimeMillis());

		this.numberOfServicedRequests = new AtomicLong(0L);

		// Create and publish the notification outbound port
		this.notificationsOutboundPort = new NotificationsOutboundPort(this);
		this.notificationsOutboundPort.publishPort();

		super.initialise(inboundPortURI, position);
	}

	/**
	 * @see fr.sorbonne_u.cps.replication.examples.components.Server#createReplicableInboundPort(java.lang.String)
	 */
	@Override
	protected void		createReplicableInboundPort(String inboundPortURI)
	throws Exception
	{
		this.rip = new ReplicableInboundPortForThreadPool<String>(
									inboundPortURI, this, EXECUTION_POOL_URI);
		this.rip.publishPort();
	}

	// -------------------------------------------------------------------------
	// Component life-cycle
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#start()
	 */
	@Override
	public void			start() throws ComponentStartException
	{
		super.start();

		this.data.clear();
		this.data.add(
				new SizeChange(this.currentSize, System.currentTimeMillis()));

		try {
			this.doPortConnection(
					this.notificationsOutboundPort.getPortURI(),
					StateBasedDispatcher.NOTIFICATION_INBOUNDPORT_URI,
					NotificationsConnector.class.getCanonicalName());
		} catch (Exception e) {
			throw new ComponentStartException(e);
		}

		// Schedule the notifications
		this.sf =
			this.scheduleTaskAtFixedRateOnComponent(
					NOTIFICATION_POOL_URI,
					new AbstractComponent.AbstractTask() {
						@Override
						public void run() {
							try {
								((MonitoredServer)
										this.getTaskOwner()).notifyState();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					},
					NOTIFICATION_PERIOD,
					NOTIFICATION_PERIOD,
					TimeUnit.MILLISECONDS);
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#finalise()
	 */
	@Override
	public void			finalise() throws Exception
	{
		this.traceMessage(
				"number of serviced requests = " +
						this.numberOfServicedRequests.get() + "\n");

		long accu = 0L;
		SizeChange last = this.data.get(0);
		for (int i = 1; i < this.data.size() - 1; i++) {
			SizeChange sc = this.data.get(i);
			accu += (sc.time - last.time) * last.size;
			last = sc;
		}
		this.traceMessage("mean queue size = " +
				((double)accu)/
					((double)(this.data.get(this.data.size()-1).time -
													this.data.get(0).time)));

		// Stop the notifications before disconnecting the port.
		if (this.sf != null && !this.sf.isCancelled()) {
			this.sf.cancel(true);
		}
		this.doPortDisconnection(this.notificationsOutboundPort.getPortURI());

		super.finalise();
	}

	/**
	 * @see fr.sorbonne_u.cps.replication.examples.components.Server#shutdown()
	 */
	@Override
	public void			shutdown() throws ComponentShutdownException
	{
		try {
			this.notificationsOutboundPort.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e);
		}

		super.shutdown();
	}

	// -------------------------------------------------------------------------
	// Component internal methods
	// -------------------------------------------------------------------------

	protected void		notifyState() throws Exception
	{
		int size = 0;
		// The result of this call may not be perfectly accurate, but here we
		// do not need an accurate value, just a guide to make a relatively
		// good server selection by the dispatcher.
		size = this.poolQueue.size();
		this.notificationsOutboundPort.
				notifyState(new ServerState(this.rip.getPortURI(), size));
	}

	// -------------------------------------------------------------------------
	// Component services
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.cps.replication.examples.components.Server#call(java.lang.Object[])
	 */
	@Override
	public String		call(Object... parameters) throws Exception
	{
		// Generate a waiting time to simulate a longer duration of the
		// processing for this request.
		long temp = Math.round(this.rg.nextGaussian() * this.standardDeviation
																+ this.mean);
		// Can't be negative or even too small
		long r = (temp < 10L ? 10L : temp);

		Thread.sleep(r);
		String result = super.call(parameters);
		this.numberOfServicedRequests.incrementAndGet();

		// The calling thread will wait here until the processing is terminated.
		return result;
	}
}
// -----------------------------------------------------------------------------
