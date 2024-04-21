package fr.sorbonne_u.components.ext.sync.interfaces;

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

import fr.sorbonne_u.components.ext.sync.components.SemaphoreI;
import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;

// -----------------------------------------------------------------------------
/**
 * The component interface <code>SemaphoreServicesCI</code> declares the
 * services offered by the semaphore component.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * This component interface uses the standard BCM pattern to share identical
 * signatures for component offered services and their implementation as
 * methods in the component.
 * </p>
 * <p>
 * Nota: since Java 8, Oracle jdk release 241, methods inherited by a remote
 * interface are no longer considered remote if the inherited interface is
 * not itself remote; to bypass this, methods must be redefined in the remote
 * method. Marking them with {@code @Override} stresses the design objective to
 * share identical signatures between the implementation interface and the
 * component interface.
 * </p>
 * 
 * <p><strong>White-box Invariant</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p>Created on : 2019-04-02</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public interface		SemaphoreServicesCI
extends		OfferedCI,
			RequiredCI,
			SemaphoreI
{
	@Override
	public void			acquire() throws Exception;

	@Override
	public void			acquire(int permits) throws Exception;

	@Override
	public int			availablePermits() throws Exception;

	@Override
	public boolean		hasQueuedThreads() throws Exception;

	@Override
	public void			release() throws Exception;

	@Override
	public void			release(int permits) throws Exception;

	@Override
	public void			tryAcquire() throws Exception;

	@Override
	public void			tryAcquire(int permits) throws Exception;
}
// -----------------------------------------------------------------------------
