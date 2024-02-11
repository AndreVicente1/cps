package fr.sorbonne_u.cps.sensor_network.network.interfaces;

// Copyright Jacques Malenfant, Sorbonne Universite.
// Jacques.Malenfant@lip6.fr
//
// This software is a computer program whose purpose is to implement
// a simulation of a sensor network in BCM4Java.
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

import fr.sorbonne_u.cps.sensor_network.interfaces.NodeInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestContinuationI;
import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;

// -----------------------------------------------------------------------------
/**
 * The interface <code>SensorNodeP2PImplI</code> defines the methods that a
 * sensor node class must implement to act as a part of the sensor network.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Black-box Invariant</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p>Created on : 2023-12-14</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public interface		SensorNodeP2PImplI
{
	/**
	 * accept a new neighbour and connect back to it.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code neighbour != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param neighbour		node information of a new sensor node that connected to the receiver.
	 * @throws Exception	<i>to do</i>.
	 */
	public void			ask4Connection(NodeInfoI neighbour)
	throws Exception;

	/**
	 * disconnect from a neighbour.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code neighbour != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param neighbour	node information of a sensor node that will disconnected from the reveiver.
	 * @throws Exception	<i>to do</i>.
	 */
	public void			ask4Disconnection(NodeInfoI neighbour)
	throws Exception;

	// -------------------------------------------------------------------------
	// Synchronous version
	// -------------------------------------------------------------------------

	/**
	 * propagate a query to the receiving sensor node.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code request != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param request		a request continuation to be continued on this node. 
	 * @return				the result of the request.
	 * @throws Exception	<i>to do</i>.
	 */
	public QueryResultI	execute(RequestContinuationI request)
	throws Exception;

	// -------------------------------------------------------------------------
	// Asynchronous version
	// -------------------------------------------------------------------------

	/**
	 * propagate a query to the receiving sensor node to pursue its execution
	 * given the current execution state given by {@code requestContinuation}.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code requestContinuation != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param requestContinuation	a request with the current state of execution to be continued on this node. 
	 * @throws Exception			<i>to do</i>.
	 */
	public void			executeAsync(
		RequestContinuationI requestContinuation
		) throws Exception;
}
// -----------------------------------------------------------------------------
