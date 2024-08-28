/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2012
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.aicore.ra.test.smoke.deployment.mock;

import javax.ejb.Local;
/**
 * This is used only for testing purpses
 * @author eabdsin
 *
 */
@Local
public interface EJBCallingResourceAdapter {

	/**
	 * To delete the file within a transaction
	 * @param fileName - fileName
	 */
	public void deleteFileWithinTransaction(String fileName);
	
	/**
	 * To read a file within a transaction
	 * @param fileName - fileName
	 * @return the bytes read
	 */
	public byte[] readFileWithinTransaction(String fileName);
	
	/**
	 * To write a file within a transaction
	 * @param fileName - the fileName
	 * @param textToWrite - the text to write
	 */
	public void writeFileWithinTransaction(String fileName,  byte[] textToWrite);
	/**
	 * To check the rolling back of the transaction.
	 * @param fileName - fileName
	 * @param content - content needs to be written
	 */
	public void rollbackTransactionWriteMethod(String fileName, byte[] content);
	
	/**
	 * To write the file using resource adapter asynchronously within a transaction
	 * @param fileName
	 * @param textToWrite - the textToWrite
	 */
	public void writeFileToResourceAdapterAsynchronosulyWithinTransaction(String fileName,  byte[] textToWrite);
	
	/**
	 * To delete a file using resource adapter aysnchronously within a transaction
	 * @param fileName fileName
	 */
	public void deleteFileToResourceAdapterAsynchronosulyWithinTransaction(String fileName);
	
}
