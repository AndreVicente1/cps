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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.cps.caching.interfaces.ComputableAllCI;
import fr.sorbonne_u.cps.caching.ports.ComputableAllInboundPort;
import fr.sorbonne_u.cps.caching.utils.ComputableFI;

// -----------------------------------------------------------------------------
/**
 * The class <code>CachedComputerMoreParallelism</code> implements a cached
 * computer that provides a method to compute many values and do so in parallel.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * The component defined by {@code CachedComputer<A,V>} already allows many
 * values to be computed in parallel when multiple clients call the method
 * <code>compute</code> in parallel. Here, a method <code>computeAll</code>
 * takes many arguments and compute their results in parallel, showing how to
 * "generate" parallelism using executor service tools within a BCM component.
 * </p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant		true
 * </pre>
 * 
 * <p>Created on : 2020-03-11</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
//-----------------------------------------------------------------------------
@OfferedInterfaces(offered = {ComputableAllCI.class})
//-----------------------------------------------------------------------------
public class			CachedComputerMoreParallelism<A,V>
extends		CachedComputer<A,V>
{

	/**
	 * create a cached computer that provides a method to compute many values
	 * and do so in parallel.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param nbThreads					number of threads used to serve requests.
	 * @param computedFunction			the function that this server compoennts offers.
	 * @param computableInboundPortURI	the URI of the inbound port offering the <code>ComputableCI</code> interface.
	 * @throws Exception				<i>to do</i>.
	 */
	protected			CachedComputerMoreParallelism(
		int nbThreads,
		ComputableFI<A,V> computedFunction,
		String computableInboundPortURI
		) throws Exception
	{
		super(nbThreads, computedFunction, computableInboundPortURI) ;

		// As computeAll tasks are synchronous and use other threads to perform
		// the computations of each of their list of arguments, the component
		// must have at least 2 threads not to block on such requests.
		assert	nbThreads > 1 ;
	}

	/**
	 * create and publish the computable inbound port.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
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
			new ComputableAllInboundPort<A,V>(computableInboundPortURI, this) ;
		this.iport.publishPort() ;
	}

	// -------------------------------------------------------------------------
	// Services implementation
	// -------------------------------------------------------------------------

	/**
	 * calls <code>compute</code> for each of the arguments in the passed array
	 * doing this with several parallel tasks if possible.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param args			arguments to be computed by <code>compute</code> one at a time.
	 * @return				array of results in the same order sa the arguments.
	 * @throws Exception	<i>to do</i>.
	 */
	@SuppressWarnings("unchecked")
	public V[]			computeAll(A[] args) throws	Exception
	{
		// The list of requests that will be submitted to the pool of threads
		// of the component.
		ArrayList<AbstractComponent.AbstractService<V>> requests =
															new ArrayList<>() ;
		for (int i = 0 ; i < args.length ; i++) {
			final int index = i ;
			// To respect the BCM protocol, component requests are created.
			AbstractComponent.AbstractService<V> request =
					new AbstractComponent.AbstractService<V>() {
						@Override
						public V call() throws Exception {
							return ((ComputableFI<A,V>)this.getServiceOwner()).
														compute(args[index]) ;
						}
					} ;
			// Usually, setting the reference to the object representing the
			// component is done in 'handleRequest' methods, but as the call
			// will bypass these, it must be done manually.
			request.setOwnerReference(this) ;
			requests.add(request) ;
		}

		// This is the important call that submits the list of requests to be
		// executed in parallel if enough threads have been provisioned in the
		// pool; it returns a list of futures allowing to synchronise on the
		// availability of each result.
		List<Future<V>> tempResults =
				this.getExecutorService(STANDARD_REQUEST_HANDLER_URI).
														invokeAll(requests) ;

		// Then, each result will be collected in turn. This code may produce
		// the blocking of the component if there is any of the request can not
		// terminate for any reason. Also, to keep things simple, any exception
		// thrown by one of the task will be passed to the caller.
		V[] results = (V[]) new Object[tempResults.size()][] ;
		for (int i = 0 ; i < tempResults.size() ; i++) {
			results[i] = tempResults.get(i).get() ;
		}
		return results ;
	}
}
// -----------------------------------------------------------------------------
