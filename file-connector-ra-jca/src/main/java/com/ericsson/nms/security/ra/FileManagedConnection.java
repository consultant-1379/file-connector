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
import java.util.*;

import javax.resource.ResourceException;
import javax.resource.spi.*;
import javax.security.auth.Subject;
import javax.transaction.xa.XAResource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.nms.security.ra.api.FileConnection;

/**
 * FileManagedConnection
 * 
 * @version $Revision: $
 */
public class FileManagedConnection implements ManagedConnection, 
		LocalTransaction {

	/** The logger */
	private static Logger log = LoggerFactory
			.getLogger(FileManagedConnection.class);

	private PrintWriter logwriter;

	/** ManagedConnectionFactory */
	private FileManagedConnectionFactory mcf;

	/** Listeners */
	private List<ConnectionEventListener> listeners;

	/** Connection */
	private FileConnectionImpl fileConnectionImpl;
	
	// Not required for now but can be provided later on.
	//private ConnectionRequestInfo connectionRequestInfo;

	

	/**
	 * Default constructor
	 * 
	 * @param mcf
	 *            mcf
	 */
	public FileManagedConnection(final FileManagedConnectionFactory mcf)
			throws ResourceException {
		log.debug("#FileManagedConnection()");
		this.mcf = mcf;
		this.logwriter = null;
		this.listeners = Collections
				.synchronizedList(new ArrayList<ConnectionEventListener>(1));
		this.fileConnectionImpl = new FileConnectionImpl(this, mcf);
	}

	/**
	 * Creates a new flow handle for the underlying physical flow represented by
	 * the ManagedConnection instance.
	 * 
	 * @param subject
	 *            Security context as JAAS subject
	 * @param cxRequestInfo
	 *            ConnectionRequestInfo instance
	 * @return generic Object instance representing the flow handle.
	 * @throws ResourceException
	 *             generic exception if operation fails
	 */
	public Object getConnection(final Subject subject,
			final ConnectionRequestInfo cxRequestInfo) throws ResourceException {
		log.debug("#FileManagedConnection.getConnection()");
		fileConnectionImpl = new FileConnectionImpl(this, mcf);
		return fileConnectionImpl;
	}

	/**
	 * Used by the container to change the association of an application-level
	 * flow handle with a ManagedConneciton instance.
	 * 
	 * @param flow
	 *            Application-level flow handle
	 * @throws ResourceException
	 *             generic exception if operation fails
	 */
	public void associateConnection(final Object flow) throws ResourceException {
		log.debug("#FileManagedConnection.associateConnection({})", flow);

		if (flow == null) {
			throw new ResourceException("Null flow handle");
		}

		if (!(flow instanceof FileConnectionImpl)) {
			throw new ResourceException("Wrong flow handle");
		}

		this.fileConnectionImpl = (FileConnectionImpl) flow;
	}

	/**
	 * Application server calls this method to force any cleanup on the
	 * ManagedConnection instance.
	 * 
	 * @throws ResourceException
	 *             generic exception if operation fails
	 */
	public void cleanup() throws ResourceException {
		log.debug("#FileManagedConnection.cleanup()");
		this.fileConnectionImpl.clear();

	}

	/**
	 * Destroys the physical flow to the underlying resource manager.
	 * 
	 * @throws ResourceException
	 *             generic exception if operation fails
	 */
	public void destroy() throws ResourceException {
		log.debug("#FileManagedConnection.destroy()");
		this.fileConnectionImpl.destroy();

	}

	/**
	 * Adds a flow event listener to the ManagedConnection instance.
	 * 
	 * @param listener
	 *            A new ConnectionEventListener to be registered
	 */
	public void addConnectionEventListener(
			final ConnectionEventListener listener) {
		log.debug("#FileManagedConnection.addConnectionEventListener({})", listener);
		if (listener == null)
			throw new IllegalArgumentException("Listener is null");
		listeners.add(listener);
	}

	/**
	 * Removes an already registered flow event listener from the
	 * ManagedConnection instance.
	 * 
	 * @param listener
	 *            already registered flow event listener to be removed
	 */
	public void removeConnectionEventListener(
			final ConnectionEventListener listener) {
		log.debug("#FileManagedConnection.removeConnectionEventListener({})", listener);
		if (listener == null)
			throw new IllegalArgumentException("Listener is null");
		listeners.remove(listener);
	}

	/**
	 * Close handle
	 * 
	 * @param handle
	 *            The handle
	 */
	void closeHandle(final FileConnection handle) {
		final ConnectionEvent event = new ConnectionEvent(this,
				ConnectionEvent.CONNECTION_CLOSED);
		event.setConnectionHandle(handle);
		for (ConnectionEventListener cel : listeners) {
			cel.connectionClosed(event);
		}

	}

	/**
	 * Gets the log writer for this ManagedConnection instance.
	 * 
	 * @return Character output stream associated with this Managed-Connection
	 *         instance
	 * @throws ResourceException
	 *             generic exception if operation fails
	 */
	public PrintWriter getLogWriter() throws ResourceException {
		log.debug("#FileManagedConnection.getLogWriter()");
		return logwriter;
	}

	/**
	 * Sets the log writer for this ManagedConnection instance.
	 * 
	 * @param out
	 *            Character Output stream to be associated
	 * @throws ResourceException
	 *             generic exception if operation fails
	 */
	public void setLogWriter(final PrintWriter out) throws ResourceException {
		log.debug("#FileManagedConnection.setLogWriter({})", out);
		logwriter = out;
	}

	/**
	 * Gets the metadata information for this flow's underlying EIS resource
	 * manager instance.
	 * 
	 * @return ManagedConnectionMetaData instance
	 * @throws ResourceException
	 *             generic exception if operation fails
	 */
	public ManagedConnectionMetaData getMetaData() throws ResourceException {
		log.debug("#FileManagedConnection.getMetaData()");
		return new FileManagedConnectionMetaData();
	}

	/**
	 * @return the flow
	 */
	public FileConnectionImpl getFlow() {
		return fileConnectionImpl;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.resource.spi.ManagedConnection#getXAResource()
	 */
	public XAResource getXAResource() throws ResourceException {
		log.debug("#FileManagedConnection.getXAResource called, returning {}", (XAResource) this);
        throw new ResourceException("XA protocol is not supported by the file-jca adapter");
		//return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.resource.spi.ManagedConnection#getLocalTransaction()
	 */
	public LocalTransaction getLocalTransaction() throws ResourceException {
		log.debug("#FileManagedConnection.getLocalTransaction called, returning this");
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.resource.spi.LocalTransaction#begin()
	 */
	public void begin() throws ResourceException {
		log.debug("#FileManagedConnection.begin method called...");
		fileConnectionImpl.begin();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.resource.spi.LocalTransaction#commit()
	 */
	public void commit() throws ResourceException {
		log.debug("#FileManagedConnection.commit method called...");
		fileConnectionImpl.commit();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.resource.spi.LocalTransaction#rollback()
	 */
	public void rollback() throws ResourceException {
		log.debug("#FileManagedConnection.rollback method called...");
		fileConnectionImpl.rollback();

	}

}
