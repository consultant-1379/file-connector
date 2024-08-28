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

import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Set;

import javax.resource.ResourceException;
import javax.resource.spi.*;
import javax.security.auth.Subject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.nms.security.ra.api.FileConnection;
import com.ericsson.nms.security.ra.api.FileConnectionFactory;

/**
 * FileManagedConnectionFactory
 * 
 * @version $Revision: $
 * @author eabdsin
 */
@ConnectionDefinition(connectionFactory = FileConnectionFactory.class, connectionFactoryImpl = FileConnectionFactoryImpl.class, connection = FileConnection.class, connectionImpl = FileConnectionImpl.class)
public class FileManagedConnectionFactory implements ManagedConnectionFactory,
		ResourceAdapterAssociation {

	/** The serial version UID */
	private static final long serialVersionUID = 1L;

	/** The logger */
	private static Logger log = LoggerFactory
			.getLogger(FileManagedConnectionFactory.class.getName());

	/** The resource adapter */
	private FileResourceAdapter ra;

	/** The logwriter */
	private PrintWriter logwriter;

	/**
	 * Default constructor
	 */
	public FileManagedConnectionFactory() {
		this.logwriter = new PrintWriter(System.out);
	}

	/**
	 * Creates a Connection Factory instance.
	 * 
	 * @param cxManager
	 *            ConnectionManager to be associated with created EIS connection
	 *            factory instance
	 * @return EIS-specific Connection Factory instance or
	 *         javax.resource.cci.ConnectionFactory instance
	 * @throws ResourceException
	 *             Generic exception
	 */
	public Object createConnectionFactory(final ConnectionManager cxManager)
			throws ResourceException {
		log.trace("createConnectionFactory({})", cxManager);
		return new FileConnectionFactoryImpl(this, cxManager);
	}

	/**
	 * Creates a Connection Factory instance.
	 * 
	 * @return EIS-specific Connection Factory instance or
	 *         javax.resource.cci.ConnectionFactory instance
	 * @throws ResourceException
	 *             Generic exception
	 */
	public Object createConnectionFactory() throws ResourceException {
		throw new ResourceException(
				"This resource adapter doesn't support non-managed environments");
	}

	/**
	 * Creates a new physical connection to the underlying EIS resource manager.
	 * 
	 * @param subject
	 *            Caller's security information
	 * @param cxRequestInfo
	 *            Additional resource adapter specific connection request
	 *            information
	 * @throws ResourceException
	 *             generic exception
	 * @return ManagedConnection instance
	 */
	public ManagedConnection createManagedConnection(final Subject subject,
			final ConnectionRequestInfo cxRequestInfo) throws ResourceException {
		log.trace("createManagedConnection({}, {})", new Object[] { subject,
				cxRequestInfo });
		return new FileManagedConnection(this);
	}

	/**
	 * Returns a matched connection from the candidate set of connections.
	 * 
	 * @param connectionSet
	 *            Candidate connection set
	 * @param subject
	 *            Caller's security information
	 * @param cxRequestInfo
	 *            Additional resource adapter specific connection request
	 *            information
	 * @throws ResourceException
	 *             generic exception
	 * @return ManagedConnection if resource adapter finds an acceptable match
	 *         otherwise null
	 */
	@SuppressWarnings("rawtypes")
	public ManagedConnection matchManagedConnections(final Set connectionSet,
			final Subject subject, final ConnectionRequestInfo cxRequestInfo)
			throws ResourceException {
		log.trace("matchManagedConnections({}, {}, {})", new Object[] {
				connectionSet, subject, cxRequestInfo });
		ManagedConnection result = null;
		Iterator it = connectionSet.iterator();
		while (result == null && it.hasNext()) {
			ManagedConnection mc = (ManagedConnection) it.next();
			if (mc instanceof FileManagedConnection) {
				if (((FileManagedConnection) mc).getFlow() != null) {
					result = mc;
				}
			}

		}
		return result;
	}

	/**
	 * Get the log writer for this ManagedConnectionFactory instance.
	 * 
	 * @return PrintWriter
	 * @throws ResourceException
	 *             generic exception
	 */
	public PrintWriter getLogWriter() throws ResourceException {
		log.trace("getLogWriter()");
		return logwriter;
	}

	/**
	 * Set the log writer for this ManagedConnectionFactory instance.
	 * 
	 * @param out
	 *            PrintWriter - an out stream for error logging and tracing
	 * @throws ResourceException
	 *             generic exception
	 */
	public void setLogWriter(final PrintWriter out) throws ResourceException {
		log.trace("setLogWriter({})", out);
		logwriter = out;
	}

	/**
	 * Get the resource adapter
	 * 
	 * @return The handle
	 */
	public ResourceAdapter getResourceAdapter() {
		log.trace("getResourceAdapter()");
		return ra;
	}

	/**
	 * Set the resource adapter
	 * 
	 * @param ra
	 *            The handle
	 */
	public void setResourceAdapter(final ResourceAdapter ra) {
		log.trace("setResourceAdapter({})", ra);
		this.ra = (FileResourceAdapter) ra;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ra == null) ? 0 : ra.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final FileManagedConnectionFactory other = (FileManagedConnectionFactory) obj;
		if (ra == null) {
			if (other.ra != null) {
				return false;
			}
		} else if (!ra.equals(other.ra)) {
			return false;
		}
		return true;
	}

}
