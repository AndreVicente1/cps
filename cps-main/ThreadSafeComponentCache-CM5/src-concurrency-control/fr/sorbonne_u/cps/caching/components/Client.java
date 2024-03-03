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
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.cps.caching.connectors.ComputableConnector;
import fr.sorbonne_u.cps.caching.interfaces.ComputableCI;
import fr.sorbonne_u.cps.caching.ports.ComputableOutboundPort;
import java.util.Random;

// -----------------------------------------------------------------------------
/**
 * The class <code>Client</code> defines a client component for the cached
 * computer component is order to show its use and effects.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant		true
 * </pre>
 * 
 * <p>Created on : 2020-03-10</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
// -----------------------------------------------------------------------------
@RequiredInterfaces(required = {ComputableCI.class})
// -----------------------------------------------------------------------------
public class			Client
extends		AbstractComponent
{
	// -------------------------------------------------------------------------
	// Component variables and constants
	// -------------------------------------------------------------------------

	/** a unique number associated to the client.							*/
	protected int										number ;
	/** URI of the inbound port of the cached computer component.			*/
	protected String									computerInboundPortURI ;
	/** the outbound port used to call the cached computer component.		*/
	protected ComputableOutboundPort<Integer,Integer[]>	oport ;
	/** the value for which the client will call the computations service.	*/
	protected int										value ;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a client component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param number					a unique number associated to the client.
	 * @param computerInboundPortURI	URI of the inbound port of the cached computer component.
	 * @param value						the value for which the client will call the computations service.
	 */
	protected			Client(
		int number,
		String computerInboundPortURI,
		int value
		)
	{
		super(1, 0) ;
		this.initialise(number, computerInboundPortURI, value) ;
	}

	/**
	 * create a client component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code number >= 0}
	 * pre	{@code computerInboundPortURI != null}
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param reflectionInboundPortURI	URI of the reflection inbound port of this component.
	 * @param number					a unique number associated to the client.
	 * @param computerInboundPortURI	URI of the inbound port of the cached computer component.
	 * @param value						the value for which the client will call the computations service.
	 */
	protected			Client(
		String reflectionInboundPortURI,
		int number,
		String computerInboundPortURI,
		int value
		)
	{
		super(reflectionInboundPortURI, 1, 0) ;
		this.initialise(number, computerInboundPortURI, value) ;
	}

	/**
	 * initialise a client component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code number >= 0}
	 * pre	{@code computerInboundPortURI != null}
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param number					a unique number associated to the client.
	 * @param computerInboundPortURI	URI of the inbound port of the cached computer component.
	 * @param value						the value for which the client will call the computations service.
	 */
	protected void		initialise(
		int number,
		String computerInboundPortURI,
		int value
		)
	{
		this.number = number ;
		this.computerInboundPortURI = computerInboundPortURI ;
		this.value = value ;
		this.getTracer().setTitle("Client " + value) ;
		this.getTracer().setRelativePosition(number/5 + 1, number % 5) ;
		this.toggleTracing() ;
	}

	/**
	 * create and publish the computable outbound port.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @throws Exception	<i>to do</i>.
	 */
	protected void		createAndPublishComputableOutboundPort()
	throws Exception
	{
		this.oport = new ComputableOutboundPort<Integer,Integer[]>(this) ;
		this.oport.publishPort() ;
	}

	// -------------------------------------------------------------------------
	// Life cycle
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#start()
	 */
	@Override
	public void			start() throws ComponentStartException
	{
		super.start() ;
		try {
			this.createAndPublishComputableOutboundPort() ;
			this.doPortConnection(
							this.oport.getPortURI(),
							computerInboundPortURI,
							this.connectorClassName()) ;
		} catch (Exception e) {
			throw new ComponentStartException(e) ;
		}
	}

	protected String	connectorClassName()
	{
		return ComputableConnector.class.getCanonicalName() ;
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#execute()
	 */
	@Override
	public void			execute() throws Exception
	{
		// wait for some random time.
		Random rg = new Random(System.currentTimeMillis()) ;
		long waitTime = 0 ;
		if (this.number < 1) {
			waitTime = 1000 ;
		} else {
			waitTime = (rg.nextInt(this.number) + 1) * 1000 ;
		}
		this.traceMessage("Starting for " + this.value + " in " +
														waitTime + " ms.\n") ;
		Thread.sleep(waitTime) ;

		// call the service.
		Integer[] factors = this.oport.compute(this.value) ;

		// print out the result.
		StringBuffer msg = new StringBuffer() ;
		msg.append("Factors of " + this.value + " = [") ;
		for (int i = 0 ; i < factors.length ; i++) {
			msg.append(factors[i]) ;
			if (i < factors.length - 1) {
				msg.append(", ") ;
			}
		}
		this.traceMessage(msg.append("]\n").toString()) ;
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#finalise()
	 */
	@Override
	public void			finalise() throws Exception
	{
		this.doPortDisconnection(this.oport.getPortURI()) ;
		super.finalise() ;
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#shutdown()
	 */
	@Override
	public void			shutdown() throws ComponentShutdownException
	{
		try {
			this.oport.unpublishPort() ;
		} catch (Exception e) {
			throw new ComponentShutdownException(e) ;
		}
		super.shutdown() ;
	}
}
// -----------------------------------------------------------------------------
