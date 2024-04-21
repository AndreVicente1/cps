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

import fr.sorbonne_u.components.cvm.AbstractDistributedCVM;
import fr.sorbonne_u.cps.replication.combinators.FixedCombinator;
import fr.sorbonne_u.cps.replication.combinators.LoneCombinator;
import fr.sorbonne_u.cps.replication.combinators.MajorityVoteCombinator;
import fr.sorbonne_u.cps.replication.combinators.RandomCombinator;
import fr.sorbonne_u.cps.replication.components.ReplicationManager;
import fr.sorbonne_u.cps.replication.examples.components.Client;
import fr.sorbonne_u.cps.replication.examples.components.ConstantServer;
import fr.sorbonne_u.cps.replication.examples.components.RandomServer;
import fr.sorbonne_u.cps.replication.examples.components.Server;
import fr.sorbonne_u.cps.replication.examples.deployments.CVM.CombinatorType;
import fr.sorbonne_u.cps.replication.examples.deployments.CVM.SelectorType;
import fr.sorbonne_u.cps.replication.selectors.RandomSelector;
import fr.sorbonne_u.cps.replication.selectors.RoundRobinSelector;
import fr.sorbonne_u.cps.replication.selectors.WholeSelector;
import fr.sorbonne_u.components.AbstractComponent;

// -----------------------------------------------------------------------------
/**
 * The class <code>DCVM</code> implements a distributed example of replication
 * to dispatch calls among servers.
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
 * <p>Created on : 2020-03-02</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			DCVM
extends		AbstractDistributedCVM
{
	// See the class CVM to choose the next values correctly.
	protected final SelectorType	currentSelector = SelectorType.WHOLE;
	protected final CombinatorType	currentCombinator =
												CombinatorType.MAJORITY_VOTE;

	public				DCVM(String[] args) throws Exception
	{
		super(args);
	}

	/**
	 * @see fr.sorbonne_u.components.cvm.AbstractDistributedCVM#instantiateAndPublish()
	 */
	@Override
	public void			instantiateAndPublish() throws Exception
	{
		if (thisJVMURI.equals("server1")) {

			AbstractComponent.createComponent(
					(currentCombinator != CombinatorType.MAJORITY_VOTE ?
						Server.class.getCanonicalName()
					:	ConstantServer.class.getCanonicalName()
					),
					new Object[]{"server" + 1 + "-",
								 CVM.SERVER_INBOUND_PORT_URIS[1],
								 1});

		} else if (thisJVMURI.equals("server2")) {
			
			AbstractComponent.createComponent(
					(currentCombinator != CombinatorType.MAJORITY_VOTE ?
						Server.class.getCanonicalName()
					:	ConstantServer.class.getCanonicalName()
					),
					new Object[]{"server" + 2 + "-",
								 CVM.SERVER_INBOUND_PORT_URIS[2],
								 2});

		} else if (thisJVMURI.equals("server3")) {

			AbstractComponent.createComponent(
				RandomServer.class.getCanonicalName(),
				new Object[]{"random-server-",
							 CVM.SERVER_INBOUND_PORT_URIS[0],
							 0});

		} else if (thisJVMURI.equals("manager")) {

			AbstractComponent.createComponent(
				ReplicationManager.class.getCanonicalName(),
				new Object[]{
					1, CVM.MANAGER_INBOUND_PORT_URI,
					(currentSelector == SelectorType.ROUND_ROBIN ?
						new RoundRobinSelector(
								CVM.SERVER_INBOUND_PORT_URIS.length)
					:	currentSelector == SelectorType.RANDOM ?
							new RandomSelector()
						:	new WholeSelector()
					),
					(currentCombinator == CombinatorType.FIXED) ?
						new FixedCombinator<String>(1)
					:	currentCombinator == CombinatorType.LONE ?
							new LoneCombinator<String>()
						:	currentCombinator == CombinatorType.MAJORITY_VOTE ?
							new MajorityVoteCombinator<String>(
								(o1,o2) -> o1.equals(o2),
								RuntimeException.class
							)
							:	new RandomCombinator<String>()
					,
					CVM.PF,
					CVM.SERVER_INBOUND_PORT_URIS
				});

		} else if (thisJVMURI.equals("client")) {

			for (int i = 1; i <= 5; i++) {
				AbstractComponent.createComponent(
							Client.class.getCanonicalName(),
							new Object[]{CVM.MANAGER_INBOUND_PORT_URI,
										 i*1000});
			}

		} else {
			
			System.out.println("unknown JVM: " + thisJVMURI);
			throw new Exception();

		}

		super.instantiateAndPublish();
	}

	public static void main(String[] args)
	{
		try {
			DCVM dcvm = new DCVM(args);
			dcvm.startStandardLifeCycle(5000L);
			Thread.sleep(10000L);
			System.exit(0);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
// -----------------------------------------------------------------------------
