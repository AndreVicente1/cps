package fr.sorbonne_u.components.ext.sync.connections;

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

import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.components.ext.sync.interfaces.SemaphoreServicesCI;

// -----------------------------------------------------------------------------
/**
 * The class <code>SemaphoreServicesConnector</code> implements a connector for
 * the <code>SemaphoreServicesI</code> component interface.
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
 * <p>Created on : 2019-04-02</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			SemaphoreServicesConnector
extends		AbstractConnector
implements	SemaphoreServicesCI
{
	/**
	 * @see fr.sorbonne_u.components.ext.sync.components.SemaphoreI#acquire()
	 */
	@Override
	public void			acquire() throws Exception
	{
		((SemaphoreServicesCI)this.offering).acquire();
	}

	/**
	 * @see fr.sorbonne_u.components.ext.sync.components.SemaphoreI#acquire(int)
	 */
	@Override
	public void			acquire(int permits) throws Exception
	{
		((SemaphoreServicesCI)this.offering).acquire(permits);
	}

	/**
	 * @see fr.sorbonne_u.components.ext.sync.components.SemaphoreI#availablePermits()
	 */
	@Override
	public int			availablePermits() throws Exception
	{
		return ((SemaphoreServicesCI)this.offering).availablePermits();
	}

	/**
	 * @see fr.sorbonne_u.components.ext.sync.components.SemaphoreI#hasQueuedThreads()
	 */
	@Override
	public boolean		hasQueuedThreads() throws Exception
	{
		return ((SemaphoreServicesCI)this.offering).hasQueuedThreads();
	}

	/**
	 * @see fr.sorbonne_u.components.ext.sync.components.SemaphoreI#release()
	 */
	@Override
	public void			release() throws Exception
	{
		((SemaphoreServicesCI)this.offering).release();
	}

	/**
	 * @see fr.sorbonne_u.components.ext.sync.components.SemaphoreI#release(int)
	 */
	@Override
	public void			release(int permits) throws Exception
	{
		((SemaphoreServicesCI)this.offering).release(permits);
	}

	/**
	 * @see fr.sorbonne_u.components.ext.sync.components.SemaphoreI#tryAcquire()
	 */
	@Override
	public void			tryAcquire() throws Exception
	{
		((SemaphoreServicesCI)this.offering).tryAcquire();
	}

	/**
	 * @see fr.sorbonne_u.components.ext.sync.components.SemaphoreI#tryAcquire(int)
	 */
	@Override
	public void			tryAcquire(int permits) throws Exception
	{
		((SemaphoreServicesCI)this.offering).tryAcquire(permits);
	}
}
// -----------------------------------------------------------------------------
