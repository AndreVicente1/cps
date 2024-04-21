package fr.sorbonne_u.cps.replication.components;

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

import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.ports.OutboundPortI;
import fr.sorbonne_u.cps.replication.connection.NotificationsInboundPort;
import fr.sorbonne_u.cps.replication.examples.components.MonitoredServer.ServerState;
import fr.sorbonne_u.cps.replication.interfaces.NotificationsCI;
import fr.sorbonne_u.cps.replication.interfaces.PortFactoryI;
import fr.sorbonne_u.cps.replication.interfaces.ReplicableCI;
import fr.sorbonne_u.cps.replication.interfaces.NotificationsCI.StateI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

// -----------------------------------------------------------------------------
/**
 * The class <code>StateBasedDispatcher</code> implements a request dispatcher
 * that selects for each request the server which has the shortest waiting
 * queue.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * To select the dispatcher with the shortest queue, this component will need
 * to get the information from its attached servers. Doing this by calling
 * each server each time a request must be dispatched would impose an
 * unreasonable delay to the dispatching going against the very idea of
 * dispatching by making the dispatcher a bottleneck.
 * </p>
 * <p>
 * Here, the component implements an asynchronous notification for the queue
 * sizes. Each server will transmit regularly their queue size using an
 * asynchronous task executed at fixed rate. These tasks call the method
 * <code>acceptNewServerState</code> passing their current state as
 * parameter. With this information, this method updates the currently
 * selected server, the one with the shortest queue. The, the <code>call</code>
 * method has only to call this server when it processes a request.
 * </p>
 * <p>
 * In order to allow an unlimited parallel processing of requests, the
 * <code>call</code> method is not executed by threads own by this component
 * but rather by the threads of the caller component. To achieve this, the
 * inbound port simply calls the method instead of submitting it to a pool of
 * threads own by this component). Of course, this imposes that the
 * <code>call</code> method be thread safe.
 * </p>
 * <p>
 * In order to make some comparisons, it is possible to run this dispatcher
 * in a round-robin mode <i>i.e.</i>, the servers will be called using a
 * round-robin dispatching policy. When all servers are of the same processing
 * power, the results are similar to the shortest-queue policy, but when the
 * servers are not of the same power, the shortest-queue policy will make the
 * average queue size be more equal by sending less requests to the slower
 * servers (hence, faster ones process more requests).
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
 * <p>Created on : 2020-03-20</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
// -----------------------------------------------------------------------------
@OfferedInterfaces(offered = {ReplicableCI.class,NotificationsCI.class})
@RequiredInterfaces(required = {ReplicableCI.class})
// -----------------------------------------------------------------------------
public class			StateBasedDispatcher<T>
extends		ReplicationManagerNonBlocking<T>
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	/** URI of the notification inbound port to receive state (queue sizes)
	 *  information from the servers.										*/
	public static final String				NOTIFICATION_INBOUNDPORT_URI =
													"notification ipURI";
	/** URI of the pool of threads used to process the notifications.		*/
	public static final String				NOTIFICATIONS_POOL_URI =
													"notification pool URI";
	/** the number of threads used by the notification processing pool of
	 *  threads.															*/
	protected static final int				NOTIFICATIONS_POOL_SIZE = 3;

	/** the notification inbound port to receive state (queue sizes)
	 *  information from the servers.										*/
	protected NotificationsInboundPort		notificationsInboundPort;
	/** map from server inbound port URIs to the outbound port that must
	 *  be used to call this server.									 	*/
	protected Map<String,OutboundPortI>		inboundPortURI2outboundPort;

	/** the outbound port of the currently selected server, the one which
	 *  queue is the shortest.												*/
	protected OutboundPortI					selectedOut;
	/** the URI of the inbound port of the currently selected server.		*/
	protected String						selectedInURI;
	/** the size of the queue of the currently selected server.				*/
	protected int							selectedSize;
	/** map from server's inbound port URI to their latest reported queue
	 *  size; an invariant connect queuSizes, selectedOut, selectedInURI
	 *  and selectedSize: the three latter represents the server which has
	 *  the shortest queue in queueSizes so the intrinsic lock of queueSizes
	 *  will be used to enforce the mutual exclusion in the access to these
	 *  four pieces of information.											*/
	protected HashMap<String,Integer>		queueSizes = new HashMap<>();

	/** true to force a run with round robin dispatching policy and false
	 *  to have a shortest-queue dispatching policy.						*/
	protected static final boolean			TEST_ROUND_ROBIN = false;
	/** number of available servers.										*/
	protected final int						numberOfServers;
	/** in a round robin run, gives the index of the next server to be
	 *  called.																*/
	protected AtomicInteger					next;

	/**
	 * return a string representing the content of {@code queueSizes}.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code queueSizes != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param queueSizes	the map of inbound port URIs to queue sizes.
	 * @return				a string representing the content of {@code queueSizes}.
	 */
	protected static String		printCurrentStates(
		HashMap<String,Integer> queueSizes
		)
	{
		StringBuffer sb = new StringBuffer("[");
		int i = 0;
		for (Entry<String,Integer> e : queueSizes.entrySet()) {
			sb.append("(");
			sb.append(e.getKey());
			sb.append(" -> ");
			sb.append(e.getValue());
			sb.append(")");
			i++;
			if (i < queueSizes.size() - 1) {
				sb.append(", ");
			}
 		}
		sb.append("]");
		return sb.toString();
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * creating a dispatcher.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code ownInboundPortURI != null}
	 * pre	{@code portCreator != null}
	 * pre	{@code serverInboundPortURIs != null && serverInboundPortURIs.length > 0}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param ownInboundPortURI			URI of the inbound port of this component.
	 * @param portCreator				a port factory to create inbound and outbound ports.
	 * @param serverInboundPortURIs		URIs of the inbound ports of the server to connect this component.
	 * @throws Exception				<i>to do</i>.
	 */
	@SuppressWarnings("unchecked")
	protected			StateBasedDispatcher(
		String ownInboundPortURI,
		PortFactoryI portCreator,
		String[] serverInboundPortURIs
		) throws Exception
	{
		super(1, ownInboundPortURI, (o -> o), CallMode.SINGLE,
			  (o -> (T)new Object[]{o}), portCreator, serverInboundPortURIs);
		this.numberOfServers = serverInboundPortURIs.length;
		this.initialise();
	}

	/**
	 * creating a dispatcher.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code ownInboundPortURI != null}
	 * pre	{@code portCreator != null}
	 * pre	{@code serverInboundPortURIs != null && serverInboundPortURIs.length > 0}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param reflectionInboundPortURI	URI of the reflection inbound port of this component.
	 * @param ownInboundPortURI			URI of the inbound port of this component.
	 * @param portCreator				a port factory to create inbound and outbound ports.
	 * @param serverInboundPortURIs		URIs of the inbound ports of the server to connect this component.
	 * @throws Exception				<i>to do</i>.
	 */
	@SuppressWarnings("unchecked")
	protected			StateBasedDispatcher(
		String reflectionInboundPortURI,
		String ownInboundPortURI,
		PortFactoryI portCreator,
		String[] serverInboundPortURIs
		) throws Exception
	{
		super(reflectionInboundPortURI, 1, ownInboundPortURI,
			  // the next three are not used in this class as we totally
			  // redefine the method call
			  (o -> o), CallMode.SINGLE, (o -> (T)new Object[] {o}),
			  portCreator, serverInboundPortURIs);
		this.numberOfServers = serverInboundPortURIs.length;
		this.initialise();
	}

	/**
	 * initialise the dispatcher.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @throws Exception	<i>to do</i>.
	 */
	protected void		initialise() throws Exception
	{
		this.createNewExecutorService(NOTIFICATIONS_POOL_URI,
									  NOTIFICATIONS_POOL_SIZE,
									  false);
		this.notificationsInboundPort =
			new NotificationsInboundPort(NOTIFICATION_INBOUNDPORT_URI, this);
		this.notificationsInboundPort.publishPort();

	}

	// -------------------------------------------------------------------------
	// Component life-cycle
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.cps.replication.components.ReplicationManager#start()
	 */
	@Override
	public void			start() throws ComponentStartException
	{
		super.start();

		this.next = new AtomicInteger(0);

		// Initialise the map from server inbound port URI to the outbound ports
		// used to call them.
		HashMap<String,OutboundPortI> temp =
										new HashMap<String,OutboundPortI>();
		for (int i = 0; i < this.outboundPorts.length; i++) {
			try {
				temp.put(this.outboundPorts[i].getServerPortURI(),
						 this.outboundPorts[i]);
			} catch (Exception e) {
				throw new ComponentStartException(e);
			}
		}
		// As this information will not change during the execution, make it
		// immutable.
		this.inboundPortURI2outboundPort = Collections.unmodifiableMap(temp);

		// Initialise the queue sizes to MAX_VALUE, so the selected server will
		// change with the first sizes transmitted by servers.
		for (int i = 0; i < this.serverInboundPortURIs.length; i++) {
			this.queueSizes.put(this.serverInboundPortURIs[i],
								Integer.MAX_VALUE);
		}
		// Arbitrarily choose the first server to send the first requests.
		// As the execution of servers have not begun, there is no need to
		// enforce a mutual exclusion here.
		this.selectedOut = this.outboundPorts[0];
		try {
			this.selectedInURI = this.selectedOut.getServerPortURI();
		} catch (Exception e) {
			throw new ComponentStartException(e);
		}
		this.selectedSize = Integer.MAX_VALUE;
	}

	/**
	 * @see fr.sorbonne_u.cps.replication.components.ReplicationManager#shutdown()
	 */
	@Override
	public void			shutdown() throws ComponentShutdownException
	{
		try {
			this.notificationsInboundPort.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e);
		}
		super.shutdown();
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * receive the current state (size of the queue) from the servers and
	 * update the selected one (the one with the shortest queue).
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code s != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param s	the current state of a server.
	 */
	public void			acceptNewServerState(StateI s)
	{
		assert	s != null;

		ServerState serverState = (ServerState) s;
		// Extract the information from the received state object.
		String uri = serverState.getInboundPortURI();
		OutboundPortI p = this.inboundPortURI2outboundPort.get(uri);
		int size = serverState.getQueueSize();

		// the intrinsic lock of this.queueSizes is used to enforce the
		// access to the information about the server selection in mutual
		// exclusion (queueSizes, selectedOut, selectedInURI, selectedSize.
		synchronized (this.queueSizes) {
			// update the size of the queue of the server which state has just
			// been received.
			int old = this.queueSizes.put(uri, size);
			if (uri.equals(this.selectedInURI)) {
				// the server is already the selected one so update the size.
				this.selectedSize = size;
				if (size > old) {
					// the current best may not be the best anymore, look up
					// for a possibly new champion.
					for(Entry<String,Integer> entry :
												this.queueSizes.entrySet()) {
						if (entry.getValue() < this.selectedSize) {
							this.selectedInURI = entry.getKey();
							this.selectedOut =
									this.inboundPortURI2outboundPort.
													get(this.selectedInURI);
							this.selectedSize = entry.getValue();
						}
					}

				}
			} else {
				// !uri.equals(this.selectedInURI)
				if (size < this.selectedSize) {
					// we have a new best, update the information
					this.selectedOut = p;
					this.selectedInURI = uri;
					this.selectedSize = size;
				}
				// otherwise the current champion remains the best, so no other
				// change is needed.
			}
		}
	}

	/**
	 * @see fr.sorbonne_u.cps.replication.components.ReplicationManagerNonBlocking#call(java.lang.Object[])
	 */
	@SuppressWarnings("unchecked")
	@Override
	public T			call(Object... parameters) throws Exception
	{
		// This method is meant to be executed by the thread of the caller
		// component (the inbound port does not call handleRequest by directly
		// this method.

		// The next lines get in mutual exclusion the currently selected server. 
		ReplicableCI<T> p =  null;
		if (!TEST_ROUND_ROBIN) {
			synchronized (this.queueSizes) {
				p = (ReplicableCI<T>) this.selectedOut;
			}
		} else {
			int n = this.next.updateAndGet(a -> (a + 1) % this.numberOfServers);
			this.traceMessage("selected = " + n + "\n");
			p = (ReplicableCI<T>)this.outboundPorts[n];
		}

		// The call, made by the caller thread.
		T result = p.call(parameters);

		return result;
	}
}
// -----------------------------------------------------------------------------
