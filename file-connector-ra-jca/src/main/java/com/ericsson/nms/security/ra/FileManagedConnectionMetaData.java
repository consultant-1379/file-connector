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
import javax.resource.spi.ManagedConnectionMetaData;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * FileManagedConnectionMetaData
 * 
 * @version $Revision: $
 */
public class FileManagedConnectionMetaData implements
		ManagedConnectionMetaData {
	/** The logger */
	private static Logger log = LoggerFactory
			.getLogger(FileManagedConnectionMetaData.class.getName());

	/**
	 * Default constructor
	 */
	public FileManagedConnectionMetaData() {

	}

	/**
	 * Returns Product name of the underlying EIS instance connected through the
	 * ManagedConnection.
	 * 
	 * @return Product name of the EIS instance
	 * @throws ResourceException
	 *             Thrown if an error occurs
	 */
	public String getEISProductName() throws ResourceException {
		log.trace("getEISProductName()");
		return "File Connection";
	}

	/**
	 * Returns Product version of the underlying EIS instance connected through
	 * the ManagedConnection.
	 * 
	 * @return Product version of the EIS instance
	 * @throws ResourceException
	 *             Thrown if an error occurs
	 */
	public String getEISProductVersion() throws ResourceException {
		log.trace("getEISProductVersion()");
		return "2.9.0";
	}

	/**
	 * Returns maximum limit on number of active concurrent connections
	 * 
	 * @return Maximum limit for number of active concurrent connections
	 * @throws ResourceException
	 *             Thrown if an error occurs
	 */
	public int getMaxConnections() throws ResourceException {
		log.trace("getMaxConnections()");
		return 20;
	}

	/**
	 * Returns name of the user associated with the ManagedConnection instance
	 * 
	 * @return Name of the user
	 * @throws ResourceException
	 *             Thrown if an error occurs
	 */
	public String getUserName() throws ResourceException {
		log.trace("getUserName()");
		return "camel";
	}

}
