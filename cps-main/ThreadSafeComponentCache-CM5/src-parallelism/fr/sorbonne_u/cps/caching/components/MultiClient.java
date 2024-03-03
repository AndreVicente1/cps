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

import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.cps.caching.connectors.ComputableAllConnector;
import fr.sorbonne_u.cps.caching.interfaces.ComputableAllCI;
import fr.sorbonne_u.cps.caching.ports.ComputableAllOutboundPort;

// -----------------------------------------------------------------------------
/**
 * The class <code>MultiClient</code> implements a client component for the
 * cached computation example testing the use of the method
 * <code>computeAll</code>.
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
@RequiredInterfaces(required = {ComputableAllCI.class})
// -----------------------------------------------------------------------------
public class			MultiClient
extends Client
{
	// -------------------------------------------------------------------------
	// Component variables and constants
	// -------------------------------------------------------------------------

	/** values to be computed; passed at creation time.	*/
	protected int[]		values ;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

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
	 * @param number					a unique number associated to the client.
	 * @param computerInboundPortURI	URI of the inbound port of the cached computer component.
	 * @param values					the values for which the client will call the computations service.
	 */
	protected			MultiClient(
		int number,
		String computerInboundPortURI,
		int[] values
		)
	{
		super(number, computerInboundPortURI, 0) ;
		this.values = values ;
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
	 * @param values					the values for which the client will call the computations service.
	 */
	protected			MultiClient(
		String reflectionInboundPortURI,
		int number,
		String computerInboundPortURI,
		int[] values
		)
	{
		super(reflectionInboundPortURI, number, computerInboundPortURI, 0);
		this.values = values ;
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
		this.oport = new ComputableAllOutboundPort<Integer,Integer[]>(this) ;
		this.oport.publishPort() ;
	}

	protected String	connectorClassName()
	{
		return ComputableAllConnector.class.getCanonicalName() ;
	}

	// -------------------------------------------------------------------------
	// Life cycle
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.cps.caching.components.Client#execute()
	 */
	@Override
	public void			execute() throws Exception
	{
		this.traceMessage("MultiClient executing...\n") ;
		Thread.sleep(10000L) ;
		this.traceMessage("MultiClient starting...\n") ;
		Integer[] args = new Integer[this.values.length] ;
		for (int i = 0 ; i < args.length ; i++) {
			args[i] = this.values[i] ;
		}
		this.traceMessage("MultiClient calling...\n") ;
		@SuppressWarnings("unchecked")
		Object[][] results =
			((ComputableAllCI<Integer,Integer[]>)this.oport).computeAll(args) ;
		this.traceMessage("MultiClient processing results...\n") ;
		StringBuffer msg = new StringBuffer() ;
		msg.append("\n") ;
		for (int i = 0; i < results.length ; i++) {
			msg.append("    Factors of " + args[i] + " = [") ;
			for (int j = 0 ; j < results[i].length ; j++) {
				msg.append(results[i][j]) ;
				if (j < results[i].length - 1) {
					msg.append(", ") ;
				}
			}
			msg.append("]\n") ;
		}
		this.traceMessage(msg.toString()) ;
	}
}
// -----------------------------------------------------------------------------
