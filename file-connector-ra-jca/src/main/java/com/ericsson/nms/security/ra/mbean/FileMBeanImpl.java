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
package com.ericsson.nms.security.ra.mbean;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.naming.InitialContext;

import com.ericsson.nms.security.ra.api.FileConnection;
import com.ericsson.nms.security.ra.api.FileConnectionFactory;

/**
 * FileMBeanImpl
 * 
 * @version $Revision: $
 */
public class FileMBeanImpl implements FileMBean {
	/** JNDI name */
	private static final String JNDI_NAME = "java:/eis/File";

	/** MBeanServer instance */
	private MBeanServer mbeanServer;

	/** Object Name */
	private String objectName;

	/** The actual ObjectName instance */
	private ObjectName on;

	/** Registered */
	private boolean registered;

	/**
	 * Set the MBean server
	 * 
	 * @param v
	 *            The value
	 */
	public void setMBeanServer(final MBeanServer v) {
		mbeanServer = v;
	}

	/**
	 * Start
	 * 
	 * @exception Throwable
	 *                Thrown in case of an error
	 */
	public void start() throws Throwable {
		if (mbeanServer == null)
			throw new IllegalArgumentException("MBeanServer is null");
		on = new ObjectName(mbeanServer.getDefaultDomain() + objectName);
		mbeanServer.registerMBean(this, on);
		registered = true;
	}

	/**
	 * Stop
	 * 
	 * @exception Throwable
	 *                Thrown in case of an error
	 */
	public void stop() throws Throwable {
		if (registered)
			mbeanServer.unregisterMBean(on);
	}

	/**
	 * GetConnection
	 * 
	 * @return DataFlow
	 */
	private FileConnection getConnection() throws Exception {
		InitialContext context = new InitialContext();
		FileConnectionFactory factory = (FileConnectionFactory) context
				.lookup(JNDI_NAME);
		FileConnection conn = factory.getFileConnection();
		if (conn == null) {
			throw new RuntimeException("No connection");
		}
		return conn;
	}

}
