package src.fr.sorbonne_u.cps.sensor_network.requests.interfaces;

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

import java.util.Set;
import src.fr.sorbonne_u.cps.sensor_network.interfaces.NodeInfoI;
import src.fr.sorbonne_u.cps.sensor_network.interfaces.PositionI;
import src.fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import src.fr.sorbonne_u.cps.sensor_network.interfaces.RequestContinuationI;
import src.fr.sorbonne_u.cps.sensor_network.interfaces.SensorDataI;

// -----------------------------------------------------------------------------
/**
 * The interface <code>ProcessingNodeI</code> declares methods for an object
 * that will bridge the gap between the request language interpreter and the
 * underlying sensor node that executes a request in order to make the former
 * access information and cooperate with the latter.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Black-box Invariant</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p>Created on : 2023-12-19</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public interface		ProcessingNodeI
{
	/**
	 * return	the identifier of the current node.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code return != null && !return.isEmpty()}
	 * </pre>
	 *
	 * @return	the identifier of the current node.
	 */
	public String		getNodeIdentifier();

	/**
	 * return the position of the processing sensor node.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code return != null}
	 * </pre>
	 *
	 * @return	the position of the sensor node.
	 */
	public PositionI	getPosition();

	/**
	 * return the set of neighbours node information of the processing sensor node.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code return != null}
	 * </pre>
	 *
	 * @return	the set of neighbours node information of the processing sensor node.
	 */
	public Set<NodeInfoI>	getNeighbours();

	/**
	 * return the current sensor data from the requested sensor of the
	 * processing sensor node or null if no such sensor exists.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code sensorIdentifier != null && !sensorIdentifier.isEmpty()}
	 * post	{@code return != null}
	 * </pre>
	 *
	 * @param sensorIdentifier	a sensor identifier.
	 * @return					the current sensor data from the requested sensor.
	 */
	public SensorDataI	getSensorData(String sensorIdentifier);

	/**
	 * propagate the current request in its current execution state to the
	 * neighbour {@code nodeIdentifier} and return the result of this
	 * propagation call.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code nodeIdentifier != null && !nodeIdentifier.isEmpty()}
	 * pre	{@code requestContinuation != null}
	 * post	{@code return != null}
	 * </pre>
	 *
	 * @param nodeIdentifier		identifier of a neighbour of the processing sensor node.
	 * @param requestContinuation	continuation of the request.
	 * @return						the result of the request called on {@code nodeIdentifier}.
	 * @throws Exception			<i>to do</i>.
	 */
	public QueryResultI	propagateRequest(
		String nodeIdentifier,
		RequestContinuationI requestContinuation
		) throws Exception;


	/**
	 * propagate the current request in its current execution state to the
	 * neighbour {@code nodeIdentifier} and return the result of this
	 * propagation call.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code nodeIdentifier != null && !nodeIdentifier.isEmpty()}
	 * pre	{@code requestContinuation != null && requestContinuation.isAsynchronous()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param nodeIdentifier		identifier of a neighbour of the processing sensor node.
	 * @param requestContinuation	continuation of the request.
	 * @throws Exception			<i>to do</i>.
	 */
	public void			propagateRequestAsync(
		String nodeIdentifier,
		RequestContinuationI requestContinuation
		) throws Exception;
}
// -----------------------------------------------------------------------------
