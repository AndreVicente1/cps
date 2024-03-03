package fr.sorbonne_u.cps.caching.components;

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

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.cps.caching.interfaces.ComputableCI;
import fr.sorbonne_u.cps.caching.ports.ComputableInboundPort;
import fr.sorbonne_u.cps.caching.utils.ComputableFI;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;

// -----------------------------------------------------------------------------
/**
 * The class <code>CachedComputer</code>  implements a cached computer
 * component that allows several threads to compute results of a computation
 * caching the results using appropriate concurrency control mechanisms.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * This example is taken and adapted from the following book:
 * </p>
 * <p>
 * B. Goetz et al., Java Concurrency in Practice, Addison-Wesley (Pearson
 * Education), 2006.
 * </p>
 * 
 * <p>
 * This component implements in a generic way how a server component can
 * accelerate lengthy computations by caching results. This is a good
 * implementation to which a management of cached results over time should be
 * added not to let the cache grow indefinitely over time.
 * </p>
 * <p>
 * The basic idea is to use a hash map to cache already computed results.
 * However, managing concurrency is a bit complex if the objective is to favor
 * parallelism. The first decision is to use a <code>ConcurrentHashMap</code>
 * to store the computed results. The internal concurrency management of this
 * class already provides enough parallelism in the access to the hash map
 * for this example.
 * </p>
 * <p>
 * However, the computations must guard themselves from computing the same
 * results many times. This could happen when two threads call the
 * <code>compute</code> method almost at the same time with the same argument.
 * In this case, both could see that their argument is not already in the hash
 * map and start parallel computations for it. Here, the proposed solution is
 * to store as soon as possible a future in the hash map, and allow subsequent
 * tasks to share the future with the first task computing the result for the
 * same argument and wait for this result before returning it to their caller.
 * </p>
 * <p>
 * The code of the method <code>compute</code> also caters for the possibility
 * that multiple threads may try to add the same future task in the hash map
 * when they all see that their argument is not present in the hash map.
 * Hence, after preparing the the future task, it is added to the hash map using
 * the atomic operation <code>putIfAbsent</code> that will return null if it
 * was not present and the already stored one otherwise, avoiding to have
 * several future tasks for the same arguments at the same time.
 * </p>
 * <p>
 * The results are kept in the hash map as futures even after the computation
 * ends. They will simply be accessed using the method <code>get</code>.
 * However, cancellations are still possible, so when cancelled the future
 * task associated with the computation is removed from the hash map and the
 * method <code>compute</code> retries all of the calls after removing the
 * particular cancelled future task from the hash map.
 * </p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	{@code true}
 * </pre>
 * 
 * <p>Created on : 2020-03-10</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
//-----------------------------------------------------------------------------
@OfferedInterfaces(offered = {ComputableCI.class})
//-----------------------------------------------------------------------------
public class			CachedComputer<A,V>
extends		AbstractComponent
implements	ComputableFI<A, V>
{
	// -------------------------------------------------------------------------
	// Component variables and constants
	// -------------------------------------------------------------------------

	/** The cache for the results.											*/
	private final ConcurrentMap<A, Future<V>>	cache =
										new ConcurrentHashMap<A, Future<V>>();
	/** The function that is computed by the component.						*/
	private final ComputableFI<A,V>				computedFunction;
	/** The inbound port through which the component receives requests.		*/
	protected ComputableInboundPort<A,V>		iport;

	// -------------------------------------------------------------------------
	// Static methods
	// -------------------------------------------------------------------------

	/**
	 * coercing an unchecked <code>Throwable</code> to a
	 * <code>RuntimeException</code>, possibly unwrapping exceptions from the
	 * one thrown by a future task to pass to the calling code an exception that
	 * is the same as if the call was made directly rather than passing by a
	 * future task.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param t	exception thrown by the <code>get</code> method of a future task.
	 * @return	the exception thrown or the one that caused the future task exception if any.
	 */
	public static RuntimeException	launderThrowable(Throwable t)
	{
		if (t instanceof RuntimeException) {
			return (RuntimeException) t;
		} else if (t instanceof Error) {
			throw (Error) t;
		} else {
			throw new IllegalStateException("Not unchecked", t);
		}
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a cached computer component with the given number of threads,
	 * computed function and computation request uinbound port URI.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param nbThreads					number of threads used to serve requests.
	 * @param computedFunction			the function that this server compoennts offers.
	 * @param computableInboundPortURI	the URI of the inbound port offering the <code>ComputableCI</code> interface.
	 * @throws Exception				<i>to do</i>.
	 */
	protected			CachedComputer(
		int nbThreads,
		ComputableFI<A,V> computedFunction,
		String computableInboundPortURI
		) throws Exception
	{
		super(nbThreads, 0);

		this.computedFunction = computedFunction;
		this.createAndPublishComputableInboundPort(computableInboundPortURI);

		this.getTracer().setTitle("CachedComputer");
		this.getTracer().setRelativePosition(0, 0);
		this.toggleTracing();
	}

	/**
	 * create and publish the computable inbound port.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param computableInboundPortURI	the URI of the inbound port offering the <code>ComputableCI</code> interface.
	 * @throws Exception				<i>to do</i>.
	 */
	protected void		createAndPublishComputableInboundPort(
		String computableInboundPortURI
		) throws Exception
	{
		this.iport =
			new ComputableInboundPort<A,V>(computableInboundPortURI, this);
		this.iport.publishPort();
	}

	// -------------------------------------------------------------------------
	// Life cycle
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#shutdown()
	 */
	@Override
	public void			shutdown() throws ComponentShutdownException
	{
		try {
			this.iport.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e);
		}
		super.shutdown();
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#shutdownNow()
	 */
	@Override
	public void			shutdownNow() throws ComponentShutdownException
	{
		try {
			this.iport.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e);
		}
		super.shutdownNow();
	}

	// -------------------------------------------------------------------------
	// Services implementation
	// -------------------------------------------------------------------------
	
	/**
	 * @see fr.sorbonne_u.cps.caching.utils.ComputableFI#compute(java.lang.Object)
	 */
	@Override
	public V			compute(A arg) throws InterruptedException
	{
		// The method is not made "synchronized" to allow more than one
		// thread to execute computations in parallel.

		// this is only for statistics, to see the effect of cache hits.
		long start = System.currentTimeMillis();

		// 
		while (true) {
			// Look for an already running or terminated computation for the
			// same argument.
			Future<V> f = cache.get(arg);
			if (f == null) {
				// If the result has not been computed for the current argument,
				// create the task to do so. This creation is speculative work
				// as many threads may see approximately at the same time that
				// the the value has not been computed yet. Hence, a race
				// condition exists until the future task is inserted in the
				// hash map.
				Callable<V> computation =
						new Callable<V>() {
							@Override
							public V call() throws Exception {
								return computedFunction.compute(arg);
							}
						};
				FutureTask<V> ft = new FutureTask<V>(computation);
				// Try to add the task to the hash map, so if another thread is
				// doing the same thing at the same time, the first task added
				// will be run and shared among all threads.
				f = cache.putIfAbsent(arg, ft);
				if (f == null) {
					// This is executed by the thread that added the task first
					// in the hash map. If f!= null, it will refer to the first
					// future task inserted in the hash map anyway, and the task
					// will already be running.
					f = ft;
					ft.run();
				};
			} else {
				// This is to show when a cache hit happens for pedagogical
				// purposes; a trace would be a bad idea in production...
				this.traceMessage("cache hit for " + arg + ".\n");
			}
			try {
				// This call to get can block if the result is not yet computed
				// and exceptions can be thrown if the computation is faulty or
				// if the executor service has to cancel the task.
				V ret = f.get();
				// this is only for statistics and pedagogical purposes, to see
				// the effect of cache hits.
				long end = System.currentTimeMillis();
				this.traceMessage("Result for " + arg + " computed in " +
													(end - start) + "ms.\n");
				return ret;
			} catch (CancellationException e) {
				// If the task is cancelled, all of the threads will get the
				// exception and arrive here. The first will remove the
				// cancelled future task from the hash map, others will not,
				// even if another thread already put a new future task for
				// the same argument because of the test on f before removing
				// i.e., it is crucial here that the entry is removed only if
				// it exists a mapping for the key arg and the value is equal
				// to f.
				cache.remove(arg, f);
			} catch (ExecutionException e) {
				// This is to recover the right exception before throwing it
				// to the caller.
				throw launderThrowable(e.getCause());
			}
		}
	}
}
// -----------------------------------------------------------------------------
