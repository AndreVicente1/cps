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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.Semaphore;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.ports.OutboundPortI;
import fr.sorbonne_u.cps.replication.interfaces.CombinatorI;
import fr.sorbonne_u.cps.replication.interfaces.PortFactoryI;
import fr.sorbonne_u.cps.replication.interfaces.ReplicableCI;
import fr.sorbonne_u.cps.replication.interfaces.SelectorI;

// -----------------------------------------------------------------------------
/**
 * The class <code>ReplicationManagerNonBlocking</code> introduces the
 * possibility to call many replicas but returning the first result while
 * letting the other (repeated) computations terminating (not possible with
 * <code>invokeAny</code>, an important behaviour when using replication for
 * fault-tolerance.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * The call mode <code>FIRST</code> and its implementation in the method
 * <code>call</code> shows how to perform this kind of waiting. Some care is
 * taken to partition the exceptions between the ones that result from a
 * malfunctioning server and the ones that result from a wrong computation.
 * The latter must be thrown to the caller but the former should trigger
 * fault-tolerance actions which are not implemented here.
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
 * <p>Created on : 2020-03-05</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
// -----------------------------------------------------------------------------
@OfferedInterfaces(offered = {ReplicableCI.class})
@RequiredInterfaces(required = {ReplicableCI.class})
// -----------------------------------------------------------------------------
public class			ReplicationManagerNonBlocking<T>
extends		ReplicationManager<T>
{
	/** URI of the thread pool that will handle the execution of the
	 *  calls to the service {@code call}.									*/
	protected static final String	CALL_POOL_URI = "call-pool";

	/**
	 * The enumeration <code>CallMode</code> defines the different call
	 * semantics for the replicas by the replication manager.
	 *
	 * <p><strong>Description</strong></p>
	 * 
	 * 
	 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
	 */
	public static enum	CallMode {
		/** only one (selected) server is called.							*/
		SINGLE,
		/** all selected servers are called, the result of the first to
		 *  respond is returned while others are cancelled.					*/
		ANY,
		/** all selected servers are called, the result of the first to
		 *  respond is returned but others finish their execution.			*/
		FIRST,
		/** all selected servers are called and all of the results are
		 *  collected.	*/
		ALL
	}

	/** asynchronous call mode of the replication manager.					*/
	protected CallMode	callMode;
	/** semaphore used to ensure mutual exclusion on the computations when
	 *  necessary.															*/
	protected Semaphore	s = new Semaphore(1);

	/**
	 * creating an asynchronous replication manager.
	 * 
	 * <p><strong>Description</strong></p>
	 * 
	 * <p>
	 * If the call mode is <code>ANY</code>, it is better to have
	 * {@code nbThreads == serverInboundPortURIs.length} <i>i.e.</i>, the
	 * number of threads is equal to the number of servers, so that all of
	 * them have a chance to respond otherwise the "race" will be only among
	 * the first bunch of servers called with the number of available threads.
	 * If the call mode is <code>FIRST</code> (return the first result to come
	 * in but let the other servers execute the request), the same may apply,
	 * but not necessarily.
	 * </p>
	 * <p>
	 * If the call mode is <code>SINGLE</code> or <code>ALL</code>, the number
	 * of threads determines respectively the number of different requests or
	 * copies of a request that can be executed in parallel.
	 * </p>
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code nbThreads > 0}
	 * pre	{@code ownInboundPortURI != null && !ownInboundPortURI.isEmpty()}
	 * pre	{@code selector != null && ThreadSafe(selector)}
	 * pre	{@code mode != null}
	 * pre	{@code combinator != null && ThreadSafe(combinator)}
	 * pre	{@code portCreator != null}
	 * pre	{@code serverInboundPortURIs != null && serverInboundPortURIs.length > 0}
	 * pre	{@code for (String uri : serverInboundPortURIs) { serverInboundPortURIs[i] != null && !serverInboundPortURIs[i].isEmpty()}}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param nbThreads					number of threads used to execute the client calls.
	 * @param ownInboundPortURI			URI of the inbound port of this component.
	 * @param selector					a function that selects outbound ports among the available ones.
	 * @param mode						asynchronous call mode of the replication manager.
	 * @param combinator				a function that combines results from servers to give one result returned to the caller.
	 * @param portCreator				a port factory to create inbound and outbound ports.
	 * @param serverInboundPortURIs		URIs of the inbound ports of the server to connect this component.
	 * @throws Exception				<i>to do</i>.
	 */
	protected			ReplicationManagerNonBlocking(
		int nbThreads,
		String ownInboundPortURI,
		SelectorI selector,
		CallMode mode,
		CombinatorI<T> combinator,
		PortFactoryI portCreator,
		String[] serverInboundPortURIs
		) throws Exception
	{
		// the unique thread created by the superclass will be used to execute
		// the method finishComputations in the case when the call mode FIRST
		// is used
		super(1, ownInboundPortURI, selector, combinator,
			  portCreator, serverInboundPortURIs);

		assert	mode != null;

		this.initialise(mode, nbThreads);
	}

	/**
	 * creating an asynchronous replication manager.
	 * 
	 * <p><strong>Description</strong></p>
	 * 
	 * <p>
	 * If the call mode is <code>ANY</code>, it is better to have
	 * {@code nbThreads == serverInboundPortURIs.length} <i>i.e.</i>, the
	 * number of threads is equal to the number of servers, so that all of
	 * them have a chance to respond otherwise the "race" will be only among
	 * the first bunch of servers called with the number of available threads.
	 * If the call mode is <code>FIRST</code> (return the first result to come
	 * in but let the other servers execute the request), the same may apply,
	 * but not necessarily.
	 * </p>
	 * <p>
	 * If the call mode is <code>SINGLE</code> or <code>ALL</code>, the number
	 * of threads determines respectively the number of different requests or
	 * copies of a request that can be executed in parallel.
	 * </p>
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code nbThreads > 0}
	 * pre	{@code ownInboundPortURI != null && !ownInboundPortURI.isEmpty()}
	 * pre	{@code selector != null && ThreadSafe(selector)}
	 * pre	{@code mode != null}
	 * pre	{@code combinator != null && ThreadSafe(combinator)}
	 * pre	{@code portCreator != null}
	 * pre	{@code serverInboundPortURIs != null && serverInboundPortURIs.length > 0}
	 * pre	{@code for (String uri : serverInboundPortURIs) { serverInboundPortURIs[i] != null && !serverInboundPortURIs[i].isEmpty()}}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param reflectionInboundPortURI	URI of the reflection inbound port of this component.
	 * @param nbThreads					number of threads used to execute the client calls.
	 * @param ownInboundPortURI			URI of the inbound port of this component.
	 * @param selector					a function that selects outbound ports among the available ones.
	 * @param mode						asynchronous call mode of the replication manager.
	 * @param combinator				a function that combines results from servers to give one result returned to the caller.
	 * @param portCreator				a port factory to create inbound and outbound ports.
	 * @param serverInboundPortURIs		URIs of the inbound ports of the server to connect this component.
	 * @throws Exception				<i>to do</i>.
	 */
	protected			ReplicationManagerNonBlocking(
		String reflectionInboundPortURI,
		int nbThreads,
		String ownInboundPortURI,
		SelectorI selector,
		CallMode mode,
		CombinatorI<T> combinator,
		PortFactoryI portCreator,
		String[] serverInboundPortURIs
		) throws Exception
	{
		// the unique thread created by the superclass will be used to execute
		// the method finishComputations in the case when the call mode FIRST
		// is used
		super(reflectionInboundPortURI, 1, ownInboundPortURI,
			  selector, combinator, portCreator, serverInboundPortURIs);

		assert	mode != null;

		this.initialise(mode, nbThreads);
	}

	/**
	 * initialise the replication manager.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param mode		asynchronous call mode of the replication manager.
	 * @param nbThreads	number of threads used to execute the calls to the servers.
	 */
	protected void		initialise(CallMode mode, int nbThreads)
	{
		this.callMode = mode;
		this.createNewExecutorService(CALL_POOL_URI, nbThreads, false);
	}

	/**
	 * @see fr.sorbonne_u.cps.replication.components.ReplicationManager#call(java.lang.Object[])
	 */
	@SuppressWarnings("unchecked")
	@Override
	public T			call(Object... parameters) throws Exception
	{
		// This method is meant to be executed by the thread of the caller
		// component (the inbound port does not call handleRequest by directly
		// this method.

		// The method select must be thread safe.
		OutboundPortI[]	selected = this.selector.select(this.outboundPorts);

		// The next lines are for pedagogical and debugging purposes.
		StringBuffer mes = new StringBuffer();;
		for (int i = 0; i < selected.length; i++) {
			mes.append(this.numbers.get(selected[i]));
			if (i < selected.length - 1) {
				mes.append(" ,");
			}
		}
		mes.append("\n");
		this.traceMessage(mes.toString());

		// The next lines create all of the request objects that will execute
		// the calls to the server components.
		ArrayList<AbstractComponent.AbstractService<T>> requests =
															new ArrayList<>();
		for (int i = 0; i <  selected.length; i++) {
			OutboundPortI p = selected[i];
			AbstractComponent.AbstractService<T> request =
					new AbstractComponent.AbstractService<T>() {
						@Override
						public T call() throws Exception {
							return (T)((ReplicableCI<T>)p).call(parameters);
						}
					};
			// the next is normally done within the handleRequest and runTask
			// methods of AbstractComponent, as we bypass these methods here
			// to submit the tasks directly to the executor services, we must
			// put the reference manually.
			request.setOwnerReference(this);
			requests.add(request);
		}

		// After this point, the calls to the servers will be done.
		// Results of the calls.
		List<T> results = new ArrayList<T>();
		// Exception raised during the calls.
		ExecutionException raised = null;
		// The four call mode are mutually exclusive for the whole life-time of
		// the replication manager, as it is a creation-time choice.
		if (this.callMode == CallMode.SINGLE || this.callMode == CallMode.ANY) {
			assert	selected.length == 1 && this.callMode == CallMode.SINGLE
					|| selected.length >= 1 && this.callMode == CallMode.ANY;
			try {
				if (this.callMode == CallMode.SINGLE) {
					// The server is called directly using handleRequest.
					results.add(this.handleRequest(CALL_POOL_URI,
												   requests.get(0)));
				} else {
					assert	this.callMode == CallMode.ANY;
					// The selected servers are called and the first result
					// will be returned and the other requests are cancelled.
					results.add(this.getExecutorService(CALL_POOL_URI).
														invokeAny(requests));
				}
			} catch (RejectedExecutionException|AssertionError|
					 		InterruptedException|NullPointerException|
					 							IllegalArgumentException e) {
				// These are thrown by handleRequest or invokeAny, hence concern
				// more the replication manager, but in this implementation we
				// choose to propagate them as ExecutionException.
				throw new ExecutionException(e);
			} catch (ExecutionException e) {
				// In this case the computation itself has thrown an exception
				// which is the cause of e, so the it is propagated to the
				// caller as such.
				throw e;
			}
		} else {
			assert	this.callMode == CallMode.FIRST ||
												this.callMode == CallMode.ALL;
			// For the call modes that require every server to execute the call,
			// it is preferable to serialise the calls from clients, which is
			// done by using a semaphore.
			try {
				this.s.acquire();
			} catch (InterruptedException e) {
				throw new ExecutionException(e);
			}
			// The two next cases of call mode are kept separated despite the
			// similarity in their coding, mainly to implement the mutual
			// exclusion between requests correctly.
			if (this.callMode == CallMode.FIRST) {
				ExecutorCompletionService<T> ecs =
						new ExecutorCompletionService<T>(
										this.getExecutorService(CALL_POOL_URI));
				List<Future<T>> tempResults = new ArrayList<Future<T>>();
				try {
					// submit the requests one by one
					for (int i = 0; i < requests.size(); i++) {
						tempResults.add(ecs.submit(requests.get(i)));
					}
					// get the first answer to return it, this will wait for
					// the first to return
					results.add(ecs.take().get());
				} catch (RejectedExecutionException|NullPointerException e) {
					// RejectedExecutionException is thrown by submit when the
					// request could not be accepted by the executor service.
					// In these cases, the caller is not in fault, so the
					// replication manager would normally have to look for its
					// executor service to make sure that it has failed.
					// However, for this implementation, we choose to propagate
					// it to the caller as an execution exception.
					// NullPointerException is also thrown by submit when the
					// task is null, should this never happen here.
					raised = new ExecutionException(e);
				} catch (InterruptedException|CancellationException e) {
					// InterruptedException is thrown either by take of get
					// when the computation is interrupted while waiting. We
					// also choose to propagate it to the caller as an
					// execution exception.
					// CancellationExceptionis thrown by get when the
					// computation is cancelled while waiting. We also choose
					// to propagate it to the caller as an execution exception.
					raised = new ExecutionException(e);
				} catch (ExecutionException e) {
					// ExecutionException is thrown by get when the computation
					// itself has thrown an exception that can be retrieved as
					// the cause of e, so it needs to be propagated to the
					// caller as such.
					raised = e;
				}
				// Unless an exception has been raised, here the first answer
				// has been received; it remains to be returned but first we
				// must take care of waiting for all other results to be
				// received aside
				try {
					// Wait for all the results to be returned before
					// releasing the semaphore, but do this in a separate thread
					// so that the first result can be returned immediately.
					this.runTask(
						(o -> { try {
									((ReplicationManagerNonBlocking<T>)o).
											finishComputations(
													ecs, requests.size() - 1);
								} catch (ExecutionException e) {
									throw new RuntimeException(e);
								}
							  }));
					// Observe that the results if the remaining servers are
					// not retrieved, as only the first result is needed to be
					// returned to the client.
				} catch (RejectedExecutionException|AssertionError e) {
					// This means that no request have been executed but rather
					// rejected by the executor service or some preconditions to
					// the execution of requests of the components have been
					// violated or the request has been interrupted while
					// waiting to execute. In these three cases, the caller is
					// not in fault, so the replication manager would normally have
					// to look for its servers and make sure that none has failed.
					raised = new ExecutionException(e);
					// In this case, the release cannot have been done in the
					// method finishComputations as it was not called.
					this.s.release();
				} catch (RuntimeException e) {
					// In this case the computation has thrown an exception.
					// This exception is the cause of the ExecutionException
					// that itself is the cause of e, so the caller must be
					// informed of the original ExecutionException.
					raised = (ExecutionException) e.getCause();
					// Observe that the semaphore has been released in
					// finishComputations.
				}
			} else {
				assert	this.callMode == CallMode.ALL;
				try {
					// submit the requests all at once
					List<Future<T>> tempResults =
							this.getExecutorService(CALL_POOL_URI).
														invokeAll(requests);
					// get all of the results one by one
					for (int i = 0; i < tempResults.size(); i++) {
						results.add(tempResults.get(i).get());
					}
				} catch (RejectedExecutionException|NullPointerException|
													InterruptedException e) {
					// These exceptions are thrown by invokeAll, hence concern
					// more the replication manager itself, but in this
					// implementation, we choose to propagate them as execution
					// exceptions.
					raised = new ExecutionException(e);
				} catch (ExecutionException e) {
					// In this case the computation itself has thrown an
					// exception that is the cause of e, so the caller must be
					// informed if no result has been produced.
					raised = e;
				} finally {
					// always release the semaphore to allow the next client
					// call to be executed.
					this.s.release();
				}
			}
		}

		if (results.size() == 0 && raised != null) {
			throw raised;
		}
		return this.combinator.combine((T[])results.toArray());
	}

	/**
	 * finish the computations after the first has returned its result to
	 * release the semaphore only after all have finished or when an exception
	 * has been raised.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code ecs != null}
	 * pre	{@code ecs n >= 0}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param ecs					the executor completion service to wait for the results.
	 * @param n						the number of awaited results.
	 * @throws ExecutionException	if one of the computation has thrown such an exception.	
	 */
	protected void		finishComputations(
		ExecutorCompletionService<T> ecs,
		int n
		) throws ExecutionException
	{
		ExecutionException raised = null;
		try {
			// get the results one by one as they are produced by the servers
			for (int i = 0; i < n; i++) {
				ecs.take().get();
			}
		} catch (InterruptedException|CancellationException e) {
			// In this case, a problem occurred with the execution service of
			// the replication manager or one of the servers; as a result has
			// already been returned, we choose not to propagate it.
		} catch (ExecutionException e) {
			// In this case, it is the execution of the call that raised
			// the exception, the caller must be informed.
			raised = e;
		} finally {
			this.s.release();
		}
		if (raised != null) throw raised;
	}
}
// -----------------------------------------------------------------------------
