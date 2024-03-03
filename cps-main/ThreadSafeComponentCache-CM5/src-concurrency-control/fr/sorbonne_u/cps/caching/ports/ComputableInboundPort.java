package fr.sorbonne_u.cps.caching.ports;

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

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.cps.caching.interfaces.ComputableCI;
import fr.sorbonne_u.cps.caching.utils.ComputableFI;

// -----------------------------------------------------------------------------
/**
 * The class <code>ComputableInboundPort</code> implements an inbound port for
 * the <code>ComputableCI</code> component interface.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * This example is taken and adapted from the following book:
 * </p>
 * <p>
 * B. Goetz et al., Java Concurrency in Practice, Addison-Wesley (Pearson
 * Education), 2006.
 * </p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant		true
 * </pre>
 * 
 * <p>Created on : 2020-03-10</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			ComputableInboundPort<A,V>
extends		AbstractInboundPort
implements	ComputableCI<A,V>
{
	private static final long serialVersionUID = 1L;

	public				ComputableInboundPort(
		ComponentI owner
		) throws Exception
	{
		super(ComputableCI.class, owner) ;
		assert	owner instanceof ComputableFI<?,?> ;
	}

	public				ComputableInboundPort(String uri, ComponentI owner)
	throws Exception
	{
		super(uri, ComputableCI.class, owner) ;
		assert	owner instanceof ComputableFI<?,?> ;
	}

	/**
	 * @see fr.sorbonne_u.cps.caching.utils.ComputableFI#compute(java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public V			compute(A arg) throws Exception
	{
		return this.getOwner().handleRequest(
									o -> ((ComputableFI<A,V>)o).compute(arg)) ;
	}
}
// -----------------------------------------------------------------------------
