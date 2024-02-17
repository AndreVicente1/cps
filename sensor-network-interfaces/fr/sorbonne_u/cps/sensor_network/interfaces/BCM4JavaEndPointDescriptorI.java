package fr.sorbonne_u.cps.sensor_network.interfaces;

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

// -----------------------------------------------------------------------------
/**
 * The interface <code>BCM4JavaEndPointDescriptorI</code> defines the methods
 * that a BCM4Java end point descriptor must implement.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * End points of interest in BCM4Java are the inbound ports, hence the two
 * important aspects of them are the URI of the port and its offered interface.
 * </p>
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
public interface		BCM4JavaEndPointDescriptorI
extends		EndPointDescriptorI
{
	/**
	 * return the URI of an inbound port to which a connection can be made.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code return != null && !return.isEmpty()}
	 * </pre>
	 *
	 * @return	the URI of an inbound port to which a connection can be made.
	 */
	public String		getInboundPortURI();

	/**
	 * return true if {@code inter} is compatible with the offered interface
	 * of the endPoint; compatibility is defined as being the same interface
	 * ({@code equals}) or that all of the methods of the requested interface
	 * appear in the offered interface of the inbound port.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code inter != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param inter	an offered interface to be tested.
	 * @return		true if {@code inter} is compatible with the offered interface of the endPoint.
	 */
	public boolean		isOfferedInterface(Class<? extends OfferedCI> inter);
}
// -----------------------------------------------------------------------------
