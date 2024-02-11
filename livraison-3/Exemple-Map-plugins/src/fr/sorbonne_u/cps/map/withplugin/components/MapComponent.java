package fr.sorbonne_u.cps.map.withplugin.components;

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

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.cps.map.deployments.CVM;
import fr.sorbonne_u.cps.map.withplugin.plugins.MapPlugin;
import java.io.Serializable;

// -----------------------------------------------------------------------------
/**
 * The class <code>MapComponent</code> implements a hash map component that
 * offers map services through a plug-in that manages the offering of the
 * component interface <code>MapI</code>.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * Instead of including all the required code to implement the hash map
 * component connection, this code is factored into the <code>MapPlugin</code>
 * to be reusable and this component has only to install an instance of this
 * plug-in.
 * </p>
 * <p>
 * Note that this aims at showing how to use plug-ins in BCM components.
 * However, this example is a little overshoot as reusing the code
 * implementing a hash map is less useful that reusing the code to call
 * the hash map (see <code>MapClientPlugin</code>).
 * </p>
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
public class			MapComponent<K extends Serializable,
									 V extends Serializable>
extends		AbstractComponent
{
	// -------------------------------------------------------------------------
	// Component variables and constants
	// -------------------------------------------------------------------------

	/** URI used to create the map plug-in in the component.				*/
	public static final String		MAP_PLUGIN_URI = "map-plugin-uri";

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a hash map component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true		// no precondition.
	 * post	true		// no postcondition.
	 * </pre>
	 *
	 * @throws Exception	<i>to do</i>.
	 */
	protected			MapComponent() throws Exception
	{
		super(CVM.MAP_COMPONENT_RIBP_URI, 1, 0);

		MapPlugin<K,V> plugin = new MapPlugin<>();
		plugin.setPluginURI(MAP_PLUGIN_URI);
		this.installPlugin(plugin);

		assert	this.isInstalled(MAP_PLUGIN_URI);
		assert	this.getPlugin(MAP_PLUGIN_URI) == plugin;
	}
}
// -----------------------------------------------------------------------------
