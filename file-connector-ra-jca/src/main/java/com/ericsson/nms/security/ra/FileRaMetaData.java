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

import javax.resource.cci.ResourceAdapterMetaData;

/**
 * FileRaMetaData
 * 
 * @version $Revision: $
 */
public class FileRaMetaData implements ResourceAdapterMetaData {
	/**
	 * Default constructor
	 */
	public FileRaMetaData() {

	}

	/**
	 * Gets the version of the resource adapter.
	 * 
	 * @return String representing version of the resource adapter
	 */
	public String getAdapterVersion() {
		return "1.0.1-SNAPSHOT";
	}

	/**
	 * Gets the name of the vendor that has provided the resource adapter.
	 * 
	 * @return String representing name of the vendor
	 */
	public String getAdapterVendorName() {
		return "ercisson software limited";
	}

	/**
	 * Gets a tool displayable name of the resource adapter.
	 * 
	 * @return String representing the name of the resource adapter
	 */
	public String getAdapterName() {
		return "file connection rar";
	}

	/**
	 * Gets a tool displayable short desription of the resource adapter.
	 * 
	 * @return String describing the resource adapter
	 */
	public String getAdapterShortDescription() {
		return "This is a file connection resource adapter";
	}

	/**
	 * Returns a string representation of the version
	 * 
	 * @return String representing the supported version of the connector
	 *         architecture
	 */
	public String getSpecVersion() {
		return "1.6";
	}

	/**
	 * Returns an array of fully-qualified names of InteractionSpec
	 * 
	 * @return Array of fully-qualified class names of InteractionSpec classes
	 */
	public String[] getInteractionSpecsSupported() {
		return null;
	}

	/**
	 * Returns true if the implementation class for the Interaction
	 * 
	 * @return boolean Depending on method support
	 */
	public boolean supportsExecuteWithInputAndOutputRecord() {
		return false;
	}

	/**
	 * Returns true if the implementation class for the Interaction
	 * 
	 * @return boolean Depending on method support
	 */
	public boolean supportsExecuteWithInputRecordOnly() {
		return false;
	}

	/**
	 * Returns true if the resource adapter implements the LocalTransaction
	 * 
	 * @return true If resource adapter supports resource manager local
	 *         transaction demarcation
	 */
	public boolean supportsLocalTransactionDemarcation() {
		return false;
	}

}
