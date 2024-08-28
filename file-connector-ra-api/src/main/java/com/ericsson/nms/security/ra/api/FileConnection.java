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

import java.nio.file.attribute.FileTime;

/**
 * Interface representing file connection in JCA terms <p>
 * The root directory where all subsequent files with sub-paths <p> 
 * needs to be written.
 * 
 * <p><b> This is important :</b> for one deployed File Connection JCA Adapter 
 * <br> the root directory would be one, but can be different folders for all<br>
 * subsequent applications, where they gonna read/write/delete files, the <br>
 * client can supply the name of the file in the calling api</p>
 * 
 * @author Abdullah Sindhu
 * 
 */
public interface FileConnection {

	/**
	 * To write to a file with supplied name 
	 * 
	 * @param file 
	 * 			the file to write
	 * @param content
	 * 			the content to write
	 */
	void write(String file, byte[] content);
	/**
	 * To delete a file with the supplied name
	 * @param file
	 * 			the file to delete
	 */
	void delete(String file);

	/**
	 * To read a file with the given name 
	 * @param file
	 * 			the file to read 
	 * @return
	 * 			the contents of the file in bytes
	 */
	byte[] fetch(String file);

	/**
	 * The last time file was modified
	 * 
	 * @param file
	 * 			the name of the file which is modified
	 * @return
	 * 			the FileTime when it was modified
	 */
	FileTime lastModified(String file);

	
	/**
	 * Close connection in jca terms
	 */
	void close();
}
