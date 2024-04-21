package fr.sorbonne_u.components.ext.sync.components;

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

import java.util.concurrent.Semaphore;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.ext.sync.connections.SemaphoreServicesInboundPort;
import fr.sorbonne_u.components.ext.sync.interfaces.SemaphoreServicesCI;

// -----------------------------------------------------------------------------
/**
 * The class <code>SemaphoreComponent</code> implements the famous synchronizer
 * by embedding a Java standard semaphore object into a component exposing the
 * core part of its interface as a component interface.
 *
 * <p><strong>Description</strong></p>
 * 
 * The major implementation choice is to have a passive component, without any
 * thread, so that its code is directly executed by the threads of its client
 * components. When a call comes from the same JVM, the thread of the client
 * may be blocked. When the call comes from another JVM through RMI, the
 * internal RMI thread used to call the semaphore component may be blocked and
 * in such a case the thread of the client will also be blocked.
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
 * <p>Created on : 2019-04-02</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
//-----------------------------------------------------------------------------
@OfferedInterfaces(offered = {SemaphoreServicesCI.class})
//-----------------------------------------------------------------------------
public class			SemaphoreComponent
extends		AbstractComponent
implements	SemaphoreI
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	/** The Java semaphore object to which all calls are delegated.			*/
	protected Semaphore						semaphore;
	/** The inbound port exposing the semaphore services.					*/
	protected SemaphoreServicesInboundPort	ssip;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a semaphore component with the given number of permits as a
	 * passive BCM component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code permits > 0}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param permits		number of permits in the semaphore.
	 * @throws Exception	<i>to do</i>.
	 */
	protected			SemaphoreComponent(
		int permits
		) throws Exception
	{
		super(0, 0);

		assert	permits > 0;
		this.init(permits);
	}

	/**
	 * create a semaphore component with the given reflection inbound port URI
	 * and number of permits as a passive BCM component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code permits > 0}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param reflectionInboundPortURI	URI of the reflection inbound port of the component.
	 * @param permits					number of permits in the semaphore.
	 * @throws Exception				<i>to do</i>.
	 */
	protected			SemaphoreComponent(
		String reflectionInboundPortURI,
		int permits
		) throws Exception
	{
		super(reflectionInboundPortURI, 0, 0);

		assert	permits > 0;
		this.init(permits);
	}

	/**
	 * initialise the semaphore component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code permits > 0}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param permits		number of permits in the semaphore.
	 * @throws Exception	<i>to do</i>.
	 */
	protected void		init(int permits) throws Exception
	{
		this.semaphore = new Semaphore(permits);
		this.ssip = new SemaphoreServicesInboundPort(this);
		this.ssip.publishPort();
	}

	// -------------------------------------------------------------------------
	// Life-cycle methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#shutdown()
	 */
	@Override
	public void			shutdown() throws ComponentShutdownException
	{
		try {
			this.ssip.unpublishPort();
			this.removeOfferedInterface(SemaphoreServicesCI.class);
		} catch (Exception e) {
			throw new ComponentShutdownException(e);
		}

		super.shutdown();
	}

	// -------------------------------------------------------------------------
	// Service methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.ext.sync.components.SemaphoreI#acquire()
	 */
	@Override
	public void			acquire() throws Exception
	{
		this.semaphore.acquire();
	}

	/**
	 * @see fr.sorbonne_u.components.ext.sync.components.SemaphoreI#acquire(int)
	 */
	@Override
	public void			acquire(int permits) throws Exception
	{
		this.semaphore.acquire(permits);
	}

	/**
	 * @see fr.sorbonne_u.components.ext.sync.components.SemaphoreI#availablePermits()
	 */
	@Override
	public int			availablePermits() throws Exception
	{
		return this.semaphore.availablePermits();
	}

	/**
	 * @see fr.sorbonne_u.components.ext.sync.components.SemaphoreI#hasQueuedThreads()
	 */
	@Override
	public boolean		hasQueuedThreads() throws Exception
	{
		return this.semaphore.hasQueuedThreads();
	}

	/**
	 * @see fr.sorbonne_u.components.ext.sync.components.SemaphoreI#release()
	 */
	@Override
	public void			release() throws Exception
	{
		this.semaphore.release();
	}

	/**
	 * @see fr.sorbonne_u.components.ext.sync.components.SemaphoreI#release(int)
	 */
	@Override
	public void			release(int permits) throws Exception
	{
		this.semaphore.release(permits);
	}

	/**
	 * @see fr.sorbonne_u.components.ext.sync.components.SemaphoreI#tryAcquire()
	 */
	@Override
	public void			tryAcquire() throws Exception
	{
		this.semaphore.tryAcquire();
	}

	/**
	 * @see fr.sorbonne_u.components.ext.sync.components.SemaphoreI#tryAcquire(int)
	 */
	@Override
	public void			tryAcquire(int permits) throws Exception
	{
		this.semaphore.tryAcquire(permits);
	}
}
// -----------------------------------------------------------------------------
