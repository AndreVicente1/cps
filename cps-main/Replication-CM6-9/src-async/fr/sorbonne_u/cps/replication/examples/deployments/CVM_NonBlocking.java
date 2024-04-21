package fr.sorbonne_u.cps.replication.examples.deployments;

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
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.components.ports.InboundPortI;
import fr.sorbonne_u.components.ports.OutboundPortI;
import fr.sorbonne_u.cps.replication.combinators.FixedCombinator;
import fr.sorbonne_u.cps.replication.combinators.LoneCombinator;
import fr.sorbonne_u.cps.replication.combinators.MajorityVoteCombinator;
import fr.sorbonne_u.cps.replication.combinators.RandomCombinator;
import fr.sorbonne_u.cps.replication.components.ReplicationManagerNonBlocking;
import fr.sorbonne_u.cps.replication.components.ReplicationManagerNonBlocking.CallMode;
import fr.sorbonne_u.cps.replication.connection.ReplicableConnector;
import fr.sorbonne_u.cps.replication.connection.ReplicableOutboundPort;
import fr.sorbonne_u.cps.replication.examples.components.Client;
import fr.sorbonne_u.cps.replication.examples.components.ConstantServer;
import fr.sorbonne_u.cps.replication.examples.components.RandomServer;
import fr.sorbonne_u.cps.replication.examples.components.Server;
import fr.sorbonne_u.cps.replication.interfaces.PortFactoryI;
import fr.sorbonne_u.cps.replication.ports.ReplicableInboundPortNonBlocking;
import fr.sorbonne_u.cps.replication.selectors.RandomSelector;
import fr.sorbonne_u.cps.replication.selectors.RoundRobinSelector;
import fr.sorbonne_u.cps.replication.selectors.WholeSelector;

// -----------------------------------------------------------------------------
/**
 * The class <code>DispatcherCVM</code> implements an example of replication
 * to dispatch calls among servers.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * Several combinations can be tested by properly choosing the configuration
 * of the variables {@code currentSelector}, {@code currentCallMode} and
 * {@code currentCombinator}. However, not all combinations are useful.
 * The following table provides the useful combinations to be explored.
 * </p>
 * <table>
 * <caption>Useful combinations</caption>
 * <tr><td>SelectorType</td><td>CallMode</td><td>CombinatorType</td></tr>
 * <tr><td>SINGLE_ROUND_ROBIN</td><td>SINGLE</td><td>LONE</td></tr>
 * <tr><td>SINGLE_RANDOM</td>     <td>SINGLE</td><td>LONE</td></tr>
 * <tr><td>MANY_SUBSET</td>       <td>ANY</td>   <td>LONE</td></tr>
 * <tr><td>MANY_SUBSET</td>       <td>FIRST</td> <td>LONE</td></tr>
 * <tr><td>MANY_SUBSET</td>       <td>ALL</td>   <td>FIXED</td></tr>
 * <tr><td>MANY_SUBSET</td>       <td>ALL</td>   <td>MAJORITY_VOTE</td></tr>
 * <tr><td>MANY_SUBSET</td>       <td>ALL</td>   <td>RANDOM</td></tr>
 * <tr><td>MANY_ALL</td>          <td>ANY</td>   <td>LONE</td></tr>
 * <tr><td>MANY_ALL</td>          <td>FIRST</td> <td>LONE</td></tr>
 * <tr><td>MANY_ALL</td>          <td>ALL</td>   <td>FIXED</td></tr>
 * <tr><td>MANY_ALL</td>          <td>ALL</td>   <td>MAJORITY_VOTE</td></tr>
 * <tr><td>MANY_ALL</td>          <td>ALL</td>    <td>RANDOM</td></tr>
 * </table>
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
 * <p>Created on : 2020-02-28</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			CVM_NonBlocking
extends 	AbstractCVM
{
	// -------------------------------------------------------------------------
	// Inner types and classes
	// -------------------------------------------------------------------------

	/**
	 * The enumeration <code>SelectorType</code> allows to choose the selection
	 * logic to be tested.
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
	 * <p>Created on : 2022-04-07</p>
	 * 
	 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
	 */
	public static enum SelectorType {
		/** one server is selected in a round-robin fashion.				*/
		SINGLE_ROUND_ROBIN,
		/** one server is selected at random.								*/
		SINGLE_RANDOM,
		/** several servers are selected but only a (strict) subset of the
		 *  available ones.													*/
		MANY_SUBSET,
		/** all of the servers are selected.								*/
		MANY_ALL
	}

	/**
	 * The enumeration <code>SelectorType</code> allows to choose the selection
	 * logic to be tested.
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
	 * <p>Created on : 2022-04-07</p>
	 * 
	 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
	 */
	public static enum CombinatorType {
		/** the uniquely computed result is returned.						*/
		LONE,
		/** a fixed result among the computed ones is returned.				*/
		FIXED,
		/** the result that appears in majority among the computed ones is
		 *  returned.	*/
		MAJORITY_VOTE,
		/** a randomly chosen result among the computed ones is returned. 	*/
		RANDOM
	}

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	/** URI of the inbound ports of the server components.					*/
	public static final String[]		SERVER_INBOUND_PORT_URIS =
													new String[]{
														"server-service-1",
														"server-service-2",
														"server-service-3"
													};
	/** URI of the replication manager inbound port.						*/
	public static final String			MANAGER_INBOUND_PORT_URI = "manager";
	/** number of client compoenents to be created and used.				*/
	public static final int				NUMBER_OF_CLIENTS = 10;

	/** select among the above values, respecting the above useful choices.	*/
	protected final SelectorType	currentSelector = SelectorType.MANY_ALL;
	/** select among call modes, respecting the above useful choices.		*/
	protected final CallMode		currentCallMode = CallMode.ALL;
	/** select among the above values, respecting the above useful choices.	*/
	protected final CombinatorType	currentCombinator =
												CombinatorType.MAJORITY_VOTE;
	/** if the current combinator is {@code CombinatorType.FIXED}, then
	 *  provide the index to be used to select the value to be returned.	*/
	protected final int				fixedIndex = 0;
	/** define the port factory to create ports in the replication manager.	*/
	public static final PortFactoryI PF =
			new PortFactoryI() {
				@Override
				public InboundPortI createInboundPort(ComponentI c)
						throws Exception
				{
					return new ReplicableInboundPortNonBlocking<String>(c);
				}

				@Override
				public InboundPortI createInboundPort(String uri, ComponentI c)
						throws Exception
				{
					return new ReplicableInboundPortNonBlocking<String>(uri, c);
				}

				@Override
				public OutboundPortI createOutboundPort(ComponentI c)
						throws Exception
				{
					return new ReplicableOutboundPort<String>(c);
				}

				@Override
				public OutboundPortI createOutboundPort(String uri, ComponentI c)
						throws Exception
				{
					return new ReplicableOutboundPort<String>(uri, c);
				}

				@Override
				public String getConnectorClassName() {
					return ReplicableConnector.class.getCanonicalName();
				}
			};

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	public			CVM_NonBlocking() throws Exception
	{
		// Verify the selection of the user before execution.
		assert		this.currentSelector == SelectorType.SINGLE_ROUND_ROBIN &&
					this.currentCallMode == CallMode.SINGLE &&
					this.currentCombinator == CombinatorType.LONE
				||	this.currentSelector == SelectorType.SINGLE_RANDOM &&
					this.currentCallMode == CallMode.SINGLE &&
					this.currentCombinator == CombinatorType.LONE
				||	this.currentSelector == SelectorType.MANY_SUBSET &&
					this.currentCallMode == CallMode.ANY &&
					this.currentCombinator == CombinatorType.LONE
				||	this.currentSelector == SelectorType.MANY_SUBSET &&
					this.currentCallMode == CallMode.FIRST &&
					this.currentCombinator == CombinatorType.LONE
				||	this.currentSelector == SelectorType.MANY_SUBSET &&
					this.currentCallMode == CallMode.ALL &&
					this.currentCombinator == CombinatorType.FIXED
				||	this.currentSelector == SelectorType.MANY_SUBSET &&
					this.currentCallMode == CallMode.ALL &&
					this.currentCombinator == CombinatorType.MAJORITY_VOTE
				||	this.currentSelector == SelectorType.MANY_SUBSET &&
					this.currentCallMode == CallMode.ALL &&
					this.currentCombinator == CombinatorType.RANDOM
				||	this.currentSelector == SelectorType.MANY_ALL &&
					this.currentCallMode == CallMode.ANY &&
					this.currentCombinator == CombinatorType.LONE
				||	this.currentSelector == SelectorType.MANY_ALL &&
					this.currentCallMode == CallMode.FIRST &&
					this.currentCombinator == CombinatorType.LONE
				||	this.currentSelector == SelectorType.MANY_ALL &&
					this.currentCallMode == CallMode.ALL &&
					this.currentCombinator == CombinatorType.FIXED
				||	this.currentSelector == SelectorType.MANY_ALL &&
					this.currentCallMode == CallMode.ALL &&
					this.currentCombinator == CombinatorType.MAJORITY_VOTE
				||	this.currentSelector == SelectorType.MANY_ALL &&
					this.currentCallMode == CallMode.ALL &&
					this.currentCombinator == CombinatorType.RANDOM;
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.cvm.AbstractCVM#deploy()
	 */
	@Override
	public void		deploy() throws Exception
	{
		for (int i = 1; i < SERVER_INBOUND_PORT_URIS.length; i++) {
			AbstractComponent.createComponent(
					(currentCombinator != CombinatorType.MAJORITY_VOTE ?
						Server.class.getCanonicalName()
					:	ConstantServer.class.getCanonicalName()
					),
					new Object[]{"server" + i + "-",
								 SERVER_INBOUND_PORT_URIS[i],
								 i});
		}
		AbstractComponent.createComponent(
							RandomServer.class.getCanonicalName(),
							new Object[]{"random-server-",
										 SERVER_INBOUND_PORT_URIS[0],
										 0});
		
		this.createReplicationManager();

		for (int i = 1; i <= NUMBER_OF_CLIENTS; i++) {
			AbstractComponent.createComponent(
							Client.class.getCanonicalName(),
							new Object[]{MANAGER_INBOUND_PORT_URI, i*1000});
		}

		super.deploy();
	}

	protected void	createReplicationManager() throws Exception
	{
		AbstractComponent.createComponent(
			ReplicationManagerNonBlocking.class.getCanonicalName(),
			new Object[]{
					currentSelector == SelectorType.MANY_ALL ?
						10
					:	SERVER_INBOUND_PORT_URIS.length,
					MANAGER_INBOUND_PORT_URI,
					(currentSelector == SelectorType.SINGLE_ROUND_ROBIN ?
						new RoundRobinSelector(
									SERVER_INBOUND_PORT_URIS.length)
					:	currentSelector == SelectorType.SINGLE_RANDOM ?
							new RandomSelector()
						:	new WholeSelector()
					),
					this.currentCallMode,
					(currentCombinator == CombinatorType.FIXED) ?
						new FixedCombinator<String>(this.fixedIndex)
					:	currentCombinator == CombinatorType.LONE ?
							new LoneCombinator<String>()
						:	currentCombinator == CombinatorType.MAJORITY_VOTE ?
							new MajorityVoteCombinator<String>(
												(o1,o2) -> o1.equals(o2),
												RuntimeException.class
												)
							:	new RandomCombinator<String>(),
					PF,
					SERVER_INBOUND_PORT_URIS
				});
	}

	public static void	main(String[] args)
	{
		try {
			CVM_NonBlocking cvm = new CVM_NonBlocking();
			cvm.startStandardLifeCycle(10000L);
			Thread.sleep(100000L);
			System.exit(0);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
// -----------------------------------------------------------------------------
