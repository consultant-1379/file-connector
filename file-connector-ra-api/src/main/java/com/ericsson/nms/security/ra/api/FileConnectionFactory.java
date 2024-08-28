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
package com.ericsson.nms.security.ra.api;

import java.io.Serializable;

import javax.resource.Referenceable;
import javax.resource.ResourceException;

/**
 * Representation of file connection factory in JCA terms <br/>
 * The factory will return an instance of the FileConnection at <br/>
 * any point in time, the connection is transactional and supports <br/>
 * committing  and rolling back for a specific set of operations <br/>
 * @author Abdullah Sindhu
 * 
 */
public interface FileConnectionFactory extends Serializable, Referenceable {

	/**
	 * Get connection from connection factory
	 * 
	 * @return FileConneciton instance
	 * @exception ResourceException
	 *                Thrown if a connection can't be obtained
	 */
	FileConnection getFileConnection() throws ResourceException;

}
