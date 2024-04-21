package fr.sorbonne_u.components.ext.sync.examples.semaphore;

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
import fr.sorbonne_u.components.ext.sync.plugins.SemaphoreClientPlugin;

// -----------------------------------------------------------------------------
/**
 * The class <code>ClientComponent</code> implements a simple client for the
 * semaphore component to show how to use it.
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
 * <p>Created on : 2019-04-02</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			ClientComponent
extends		AbstractComponent
{
	// -------------------------------------------------------------------------
	// Component variables and constants
	// -------------------------------------------------------------------------

	/** URI of the plug-in used in this component.							*/
	protected final static String	SEMAPHORE_PLUGIN_URI =
													"semaphore-client-plugin" ;
	/** a number uniquely identifying the client components.				*/
	protected int		number ;
	/** URI of the reflection inbound port of the semaphore component.		*/
	protected String	semaphoreReflectionInboundPortURI ;

	// -------------------------------------------------------------------------
	// Component constructors
	// -------------------------------------------------------------------------
	/**
	 * create a client component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code semaphoreReflectionInboundPortURI != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param semaphoreReflectionInboundPortURI	URI of the reflection inbound port of the semaphore component.
	 * @param number							a number uniquely identifying the client components.
	 */
	protected			ClientComponent(
		String semaphoreReflectionInboundPortURI,
		int number
		)
	{
		super(1, 0) ;

		assert	semaphoreReflectionInboundPortURI != null ;

		this.number = number ;
		this.semaphoreReflectionInboundPortURI =
								semaphoreReflectionInboundPortURI ;

		this.getTracer().setRelativePosition(1, this.number) ;
		this.toggleTracing() ;
	}

	// -------------------------------------------------------------------------
	// Component life-cycle methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#execute()
	 */
	@Override
	public void			execute() throws Exception
	{
		SemaphoreClientPlugin plugin =
			new SemaphoreClientPlugin(SEMAPHORE_PLUGIN_URI,
									  this.semaphoreReflectionInboundPortURI) ;
		this.installPlugin(plugin) ;

		this.logMessage("client " + this.number + " acquires.") ;
		plugin.acquire() ;
		this.logMessage("client " + this.number + " passes.") ;
		Thread.sleep(10000L) ;
		plugin.release() ;
	}
}
// -----------------------------------------------------------------------------
