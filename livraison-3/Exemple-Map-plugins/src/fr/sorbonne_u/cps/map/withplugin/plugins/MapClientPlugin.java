package fr.sorbonne_u.cps.map.withplugin.plugins;

// Copyright Jacques Malenfant, Sorbonne Universite.
// Jacques.Malenfant@lip6.fr
//
// This software is a computer program whose purpose is to provide an example of
// the BCM component model that aims to define a basic component model for Java.
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

import fr.sorbonne_u.components.AbstractPlugin;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.reflection.connectors.ReflectionConnector;
import fr.sorbonne_u.components.reflection.interfaces.ReflectionCI;
import fr.sorbonne_u.components.reflection.ports.ReflectionOutboundPort;
import fr.sorbonne_u.cps.map.connectors.MapConnector;
import fr.sorbonne_u.cps.map.deployments.CVM;
import fr.sorbonne_u.cps.map.interfaces.MapCI;
import fr.sorbonne_u.cps.map.ports.MapOutboundPort;

import java.io.Serializable;

// -----------------------------------------------------------------------------
/**
 * The class <code>MapClientPlugin</code> implements the client-side plug-in
 * for the <code>MapCI</code> component interface and associated ports and
 * connectors.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant		true
 * </pre>
 * 
 * <p>Created on : 2019-03-21</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			MapClientPlugin<K extends Serializable,
										V extends Serializable>
extends		AbstractPlugin
{
	// -------------------------------------------------------------------------
	// Plug-in variables and constants
	// -------------------------------------------------------------------------

	private static final long		serialVersionUID = 1L;
	/** the outbound port used to connect to the hash map component.		*/
	protected MapOutboundPort<K,V>	mop;

	// -------------------------------------------------------------------------
	// Life cycle
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.AbstractPlugin#installOn(fr.sorbonne_u.components.ComponentI)
	 */
	@Override
	public void			installOn(ComponentI owner)
	throws Exception
	{
		super.installOn(owner);

		// Add interfaces and create ports
		this.addRequiredInterface(MapCI.class);
		this.mop = new MapOutboundPort<K,V>(this.getOwner());
		this.mop.publishPort();
	}

	/**
	 * assume that the plug-in on the server component has already been
	 * installed and initialised.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true		// no preconditions.
	 * post	true		// no postconditions.
	 * </pre>
	 * 
	 * @see fr.sorbonne_u.components.AbstractPlugin#initialise()
	 */
	@Override
	public void			initialise() throws Exception
	{
		// Use the reflection approach to get the URI of the inbound port
		// of the hash map component.
		this.addRequiredInterface(ReflectionCI.class);
		ReflectionOutboundPort rop = new ReflectionOutboundPort(this.getOwner());
		rop.publishPort();
		this.getOwner().doPortConnection(
				rop.getPortURI(),
				CVM.MAP_COMPONENT_RIBP_URI,
				ReflectionConnector.class.getCanonicalName());
		String[] uris = rop.findPortURIsFromInterface(MapCI.class);
		assert	uris != null && uris.length == 1;

		this.getOwner().doPortDisconnection(rop.getPortURI());
		rop.unpublishPort();
		rop.destroyPort();
		this.removeRequiredInterface(ReflectionCI.class);

		// connect the outbound port.
		this.getOwner().doPortConnection(
				this.mop.getPortURI(),
				uris[0],
				MapConnector.class.getCanonicalName());

		super.initialise();
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractPlugin#finalise()
	 */
	@Override
	public void			finalise() throws Exception
	{
		this.getOwner().doPortDisconnection(this.mop.getPortURI());
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractPlugin#uninstall()
	 */
	@Override
	public void			uninstall() throws Exception
	{
		this.mop.unpublishPort();
		this.mop.destroyPort();
		this.removeRequiredInterface(MapCI.class);
	}

	// -------------------------------------------------------------------------
	// Plug-in services implementation
	// -------------------------------------------------------------------------

	/**
	 * return a reference of the given type allowing the client to call the
	 * services of the map.
	 * 
	 * <p><strong>Description</strong></p>
	 * 
	 * This method returns the reference to the outbound port, but hiding this
	 * representation to the client. Contrary to other examples of plug-ins,
	 * this one would just pass the calls to the outbound port, so using this
	 * way to return a reference avoids reimplementing the whole
	 * {@code MapI<K, V>} interface in this plug-in, in a sort of delegation
	 * pattern.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true		// no precondition.
	 * post	true		// no postcondition.
	 * </pre>
	 *
	 * @return	a reference that can be used by the client to call the services of the map server component.
	 */
	public MapCI<K, V>	getMapServicesReference()
	{
		return this.mop;
	}

}
// -----------------------------------------------------------------------------
