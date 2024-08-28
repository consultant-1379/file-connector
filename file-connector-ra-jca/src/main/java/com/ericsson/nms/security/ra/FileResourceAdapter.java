/*
 * IronJacamar, a Java EE Connector Architecture implementation
 * Copyright 2013, Red Hat Inc, and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.ericsson.nms.security.ra;

import javax.resource.ResourceException;
import javax.resource.spi.*;
import javax.resource.spi.endpoint.MessageEndpointFactory;
import javax.transaction.TransactionSynchronizationRegistry;
import javax.transaction.xa.XAResource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * FileResourceAdapter
 * 
 * @version $Revision: $
 */
@Connector(reauthenticationSupport = false, vendorName = "Ericsson", version = "0.0.1-SNAPSHOT", licenseRequired = false)
public class FileResourceAdapter implements ResourceAdapter,
		java.io.Serializable {

	/** The serial version UID */
	private static final long serialVersionUID = 1L;

	/** The logger */
	private static Logger log = LoggerFactory
			.getLogger(FileResourceAdapter.class.getName());

	private TransactionSynchronizationRegistry txRegistry;

	private XATerminator xaTerminator;

	/**
	 * Default constructor
	 */
	public FileResourceAdapter() {

	}

	/**
	 * This is called during the activation of a message endpoint.
	 * 
	 * @param endpointFactory
	 *            A message endpoint factory instance.
	 * @param spec
	 *            An activation spec JavaBean instance.
	 * @throws ResourceException
	 *             generic exception
	 */
	public void endpointActivation(
			final MessageEndpointFactory endpointFactory,
			final ActivationSpec spec) throws ResourceException {
		log.trace("endpointActivation({}, {})", new Object[] { endpointFactory,
				spec });

	}

	/**
	 * This is called when a message endpoint is deactivated.
	 * 
	 * @param endpointFactory
	 *            A message endpoint factory instance.
	 * @param spec
	 *            An activation spec JavaBean instance.
	 */
	public void endpointDeactivation(
			final MessageEndpointFactory endpointFactory,
			final ActivationSpec spec) {
		log.trace("endpointDeactivation({})", endpointFactory);

	}

	/**
	 * This is called when a resource adapter instance is bootstrapped.
	 * 
	 * @param ctx
	 *            A bootstrap context containing references
	 * @throws ResourceAdapterInternalException
	 *             indicates bootstrap failure.
	 */
	public void start(final BootstrapContext ctx)
			throws ResourceAdapterInternalException {
		log.trace("start({})", ctx);
		this.txRegistry = ctx.getTransactionSynchronizationRegistry();
		this.xaTerminator = ctx.getXATerminator();
	}

	/**
	 * This is called when a resource adapter instance is undeployed or during
	 * application server shutdown.
	 */
	public void stop() {
		log.trace("stop()");

	}

	/**
	 * This method is called by the application server during crash recovery.
	 * 
	 * @param specs
	 *            An array of ActivationSpec JavaBeans
	 * @throws ResourceException
	 *             generic exception
	 * @return An array of XAResource objects
	 */
	public XAResource[] getXAResources(final ActivationSpec[] specs)
			throws ResourceException {
		log.trace("getXAResources({})", specs.toString());
		return null;
	}

	/**
	 * @return the txRegistry
	 */
	public TransactionSynchronizationRegistry getTxRegistry() {
		return txRegistry;
	}

	/**
	 * @return the xaTerminator
	 */
	public XATerminator getXaTerminator() {
		return xaTerminator;
	}

	/**
	 * Returns a hash code value for the object.
	 * 
	 * @return A hash code value for this object.
	 */
	@Override
	public int hashCode() {
		int result = 17;
		result += 31 * result + 7;
		return result;
	}

	/**
	 * Indicates whether some other object is equal to this one.
	 * 
	 * @param other
	 *            The reference object with which to compare.
	 * @return true if this object is the same as the obj argument, false
	 *         otherwise.
	 */
	@Override
	public boolean equals(final Object other) {
		if (other == null) {
			return false;
		}
		if (other == this) {
			return true;
		}
		if (!(other instanceof FileResourceAdapter)) {
			return false;
		}
		boolean result = true;
		return result;
	}

}
