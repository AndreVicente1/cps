package fr.sorbonne_u.cps.sensor_network.registry.interfaces;

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

import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;
import fr.sorbonne_u.cps.sensor_network.interfaces.ConnectionInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.GeographicalZoneI;
import java.util.Set;

// -----------------------------------------------------------------------------
/**
 * The component interface <code>LookupCI</code> declares the methods offered
 * by the registry to look up for sensor nodes existing in the sensor network.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Black-box Invariant</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p>Created on : 2023-12-12</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public interface		LookupCI
extends		OfferedCI,
			RequiredCI
{
	/**
	 * return the connection information of the node with {@code sensorNodeId}
	 * or null if no such node exists.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code sensorNodeId != null && !sensorNodeId.isEmpty()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param sensorNodeId	identifier of the sought node.
	 * @return				the connection information of the sought node or null if it does not exist.
	 * @throws Exception	<i>to do</i>.
	 */
	public ConnectionInfoI	findByIdentifier(String sensorNodeId)
	throws Exception;

	/**
	 * return a set of all registered sensor nodes in {@code z}.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code z != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param z				a geographical zone.
	 * @return				a set of the connection information for all registered sensor nodes in {@code z}.
	 * @throws Exception	<i>to do</i>.
	 */
	public Set<ConnectionInfoI>	findByZone(GeographicalZoneI z)
	throws Exception;
}
// -----------------------------------------------------------------------------
