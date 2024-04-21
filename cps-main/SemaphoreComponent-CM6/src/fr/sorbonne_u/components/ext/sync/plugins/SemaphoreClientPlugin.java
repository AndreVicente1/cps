package fr.sorbonne_u.components.ext.sync.plugins;

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

import fr.sorbonne_u.components.AbstractPlugin;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ext.sync.components.SemaphoreI;
import fr.sorbonne_u.components.ext.sync.connections.SemaphoreServicesConnector;
import fr.sorbonne_u.components.ext.sync.connections.SemaphoreServicesOutboundPort;
import fr.sorbonne_u.components.ext.sync.interfaces.SemaphoreServicesCI;
import fr.sorbonne_u.components.reflection.connectors.ReflectionConnector;
import fr.sorbonne_u.components.reflection.interfaces.ReflectionCI;
import fr.sorbonne_u.components.reflection.ports.ReflectionOutboundPort;

// -----------------------------------------------------------------------------
/**
 * The class <code>SemaphoreClientPlugin</code> implements a client-side plug-in
 * to be used by components that need to use a semaphore component.
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
 * <p>Created on : 2019-04-03</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			SemaphoreClientPlugin
extends		AbstractPlugin
implements	SemaphoreI
{
	// -------------------------------------------------------------------------
	// Plug-in variables and constants
	// -------------------------------------------------------------------------

	private static final long				serialVersionUID = 1L;
	/** the URI of the reflection inbound port of the semaphore component.	*/
	protected String						semaphoreReflectionInboundPortURI;
	protected SemaphoreServicesOutboundPort	ssop;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a plug-in instance.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code pluginURI != null}
	 * pre	{@code semaphoreReflectionInboundPortURI != null}
	 * post	{@code true}	// no more postcondition.
	 * </pre>
	 *
	 * @param pluginURI							URI of this plug-in instance.
	 * @param semaphoreReflectionInboundPortURI	URI of the semaphore component inbound port URI.
	 * @throws Exception						<i>to do</i>.
	 */
	public				SemaphoreClientPlugin(
		String pluginURI,
		String semaphoreReflectionInboundPortURI
		) throws Exception
	{
		super();

		assert	pluginURI != null;
		assert	semaphoreReflectionInboundPortURI != null;

		this.setPluginURI(pluginURI);
		this.semaphoreReflectionInboundPortURI =
										semaphoreReflectionInboundPortURI;
	}

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

		this.addRequiredInterface(SemaphoreServicesCI.class);
		this.ssop = new SemaphoreServicesOutboundPort(owner);
		this.ssop.publishPort();
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractPlugin#initialise()
	 */
	@Override
	public void			initialise() throws Exception
	{
		this.addRequiredInterface(ReflectionCI.class);
		ReflectionOutboundPort rop =
					new ReflectionOutboundPort(this.getOwner());
		rop.publishPort();
		this.getOwner().doPortConnection(
				rop.getPortURI(),
				this.semaphoreReflectionInboundPortURI,
				ReflectionConnector.class.getCanonicalName());

		String[] uris =
			rop.findInboundPortURIsFromInterface(SemaphoreServicesCI.class);
		assert	uris != null && uris.length == 1;
		this.getOwner().doPortDisconnection(rop.getPortURI());
		rop.unpublishPort();
		rop.destroyPort();
		this.removeRequiredInterface(ReflectionCI.class);

		this.getOwner().doPortConnection(
				this.ssop.getPortURI(),
				uris[0],
				SemaphoreServicesConnector.class.getCanonicalName());
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractPlugin#finalise()
	 */
	@Override
	public void			finalise() throws Exception
	{
		this.getOwner().doPortDisconnection(this.ssop.getPortURI());
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractPlugin#uninstall()
	 */
	@Override
	public void			uninstall() throws Exception
	{
		this.ssop.unpublishPort();
		this.ssop.destroyPort();
		this.removeRequiredInterface(SemaphoreServicesCI.class);
	}

	// -------------------------------------------------------------------------
	// Plug-in services implementation
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.ext.sync.components.SemaphoreI#acquire()
	 */
	@Override
	public void			acquire() throws Exception {
		this.ssop.acquire();
	}

	/**
	 * @see fr.sorbonne_u.components.ext.sync.components.SemaphoreI#acquire(int)
	 */
	@Override
	public void			acquire(int permits) throws Exception {
		this.ssop.acquire(permits);
	}

	/**
	 * @see fr.sorbonne_u.components.ext.sync.components.SemaphoreI#availablePermits()
	 */
	@Override
	public int			availablePermits() throws Exception {
		return this.ssop.availablePermits();
	}

	/**
	 * @see fr.sorbonne_u.components.ext.sync.components.SemaphoreI#hasQueuedThreads()
	 */
	@Override
	public boolean		hasQueuedThreads() throws Exception {
		return this.ssop.hasQueuedThreads();
	}

	/**
	 * @see fr.sorbonne_u.components.ext.sync.components.SemaphoreI#release()
	 */
	@Override
	public void			release() throws Exception {
		this.ssop.release();
	}

	/**
	 * @see fr.sorbonne_u.components.ext.sync.components.SemaphoreI#release(int)
	 */
	@Override
	public void			release(int permits) throws Exception {
		this.ssop.release(permits);
	}

	/**
	 * @see fr.sorbonne_u.components.ext.sync.components.SemaphoreI#tryAcquire()
	 */
	@Override
	public void			tryAcquire() throws Exception {
		this.ssop.tryAcquire();
	}

	/**
	 * @see fr.sorbonne_u.components.ext.sync.components.SemaphoreI#tryAcquire(int)
	 */
	@Override
	public void			tryAcquire(int permits) throws Exception {
		this.ssop.tryAcquire(permits);
	}
}
// -----------------------------------------------------------------------------
