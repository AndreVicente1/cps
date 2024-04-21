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
import fr.sorbonne_u.cps.replication.components.StateBasedDispatcher;
import fr.sorbonne_u.cps.replication.connection.ReplicableConnector;
import fr.sorbonne_u.cps.replication.connection.ReplicableOutboundPort;
import fr.sorbonne_u.cps.replication.examples.components.Client;
import fr.sorbonne_u.cps.replication.examples.components.MonitoredServer;
import fr.sorbonne_u.cps.replication.interfaces.PortFactoryI;
import fr.sorbonne_u.cps.replication.ports.ReplicableInboundPortNonBlocking;

// -----------------------------------------------------------------------------
/**
 * The class <code>CVM_Dispatcher</code>
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
 * <p>Created on : 2020-03-20</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			CVM_Dispatcher
extends		AbstractCVM
{

	public 				CVM_Dispatcher() throws Exception
	{
	}

	/** the port factory used by the dispatcher to create its ports.		*/
	public static final PortFactoryI PC =
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
			public String getConnectorClassName()
			{
				return ReplicableConnector.class.getCanonicalName();
			}
		};

	/**
	 * @see fr.sorbonne_u.components.cvm.AbstractCVM#deploy()
	 */
	@Override
	public void			deploy() throws Exception
	{
		// Create the servers
		for (int i = 0; i < CVM_NonBlocking.SERVER_INBOUND_PORT_URIS.length;
																		i++) {
			double mean = 60.0 + ((double)i) * 5.0;
			double standardDeviation = ((double)i+1) * 5.0;
			AbstractComponent.createComponent(
					MonitoredServer.class.getCanonicalName(),
					new Object[]{"server" + (i + 1),
								 CVM_NonBlocking.SERVER_INBOUND_PORT_URIS[i],
								 i,
								 mean,
								 standardDeviation});
		}

		// Create the dispatcher
		AbstractComponent.createComponent(
				StateBasedDispatcher.class.getCanonicalName(),
				new Object[]{
						CVM_NonBlocking.MANAGER_INBOUND_PORT_URI,
						PC,
						CVM_NonBlocking.SERVER_INBOUND_PORT_URIS
					});

		// Create some clients, enough to produce some waiting on the servers
		for (int i = 1; i <= CVM_NonBlocking.NUMBER_OF_CLIENTS; i++) {
			AbstractComponent.createComponent(
							Client.class.getCanonicalName(),
							new Object[]{
									CVM_NonBlocking.MANAGER_INBOUND_PORT_URI,
									i*1000});
		}

		super.deploy();
	}

	public static void	main(String[] args)
	{
		try {
			CVM_Dispatcher cvm = new CVM_Dispatcher();
			cvm.startStandardLifeCycle(30000L);
			Thread.sleep(1000000L);
			System.exit(0);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
// -----------------------------------------------------------------------------
