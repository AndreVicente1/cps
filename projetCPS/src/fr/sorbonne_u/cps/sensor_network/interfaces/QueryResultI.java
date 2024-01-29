package src.fr.sorbonne_u.cps.sensor_network.interfaces;

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

import java.io.Serializable;
import java.util.ArrayList;

// -----------------------------------------------------------------------------
/**
 * The interface <code>QueryResultI</code> defines the methods that the result
 * of a query must implement.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Black-box Invariant</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p>Created on : 2024-01-08</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public interface		QueryResultI
extends		Serializable
{
	/**
	 * return true if this represents the result of a boolean request.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return	true if this represents the result of a boolean request.
	 */
	public boolean		isBooleanRequest();

	/**
	 * return the list of sensor nodes identifiers where the boolean request
	 * evaluated to true.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code isBooleanRequest()}
	 * post	{@code return != null}
	 * </pre>
	 *
	 * @return	the list of sensor nodes identifiers where the boolean request evaluated to true.
	 */
	public ArrayList<String>	positiveSensorNodes();

	/**
	 * return true if this represents the result of a sensors values gathering
	 * request.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return	true if this represents the result of a sensors values gathering request.
	 */
	public boolean		isGatherRequest();

	/**
	 * return the gathered sensors values.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code isGatherRequest()}
	 * post	{@code return != null}
	 * </pre>
	 *
	 * @return	the gathered sensors values.
	 */
	public ArrayList<SensorDataI>	gatheredSensorsValues();
}
// -----------------------------------------------------------------------------
