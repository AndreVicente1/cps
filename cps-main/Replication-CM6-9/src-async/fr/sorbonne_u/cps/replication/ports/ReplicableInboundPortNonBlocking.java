package fr.sorbonne_u.cps.replication.ports;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.cps.replication.connection.ReplicableInboundPort;
import fr.sorbonne_u.cps.replication.interfaces.ReplicaI;

// -----------------------------------------------------------------------------
/**
 * The class <code>ReplicableInboundPortNonBlocking</code> implements an inbound
 * port for the <code>ReplicableCI</code> component interface, making the
 * the <code>call</code> method of the component executed by the thread of the
 * caller component.
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
 * <p>Created on : 2020-03-05</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			ReplicableInboundPortNonBlocking<T>
extends		ReplicableInboundPort<T>
{
	private static final long serialVersionUID = 1L;

	public				ReplicableInboundPortNonBlocking(
		ComponentI owner
		) throws Exception
	{
		super(owner);
	}

	public				ReplicableInboundPortNonBlocking(
		String uri,
		ComponentI owner
		) throws Exception
	{
		super(uri, owner);
	}

	/**
	 * @see fr.sorbonne_u.cps.replication.connection.ReplicableInboundPort#call(java.lang.Object[])
	 */
	@SuppressWarnings("unchecked")
	@Override
	public T			call(Object... parameters) throws Exception
	{
		// The call is made directly on the object representing the component
		// so that the code of the method call will be executed by the thread
		// of the calling component. Indeed, the method call must be
		// thread-safe, as it would be necessary if many thread are used
		// in the component.
		return ((ReplicaI<T>)this.getOwner()).call(parameters);
	}
}
// -----------------------------------------------------------------------------
