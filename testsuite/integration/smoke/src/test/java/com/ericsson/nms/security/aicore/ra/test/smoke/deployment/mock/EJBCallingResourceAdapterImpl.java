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

import javax.annotation.Resource;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.resource.ResourceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.nms.security.ra.api.FileConnection;
import com.ericsson.nms.security.ra.api.FileConnectionFactory;
/**
 * This ejb is only used as a trigger point for resource adapter, in other words <br/>
 * only for testing purposes
 * @author eabdsin
 *
 */

@Stateless
public class EJBCallingResourceAdapterImpl implements EJBCallingResourceAdapter {

	private static final Logger log = LoggerFactory
			.getLogger(EJBCallingResourceAdapterImpl.class);

	@Resource(lookup = "java:/eis/FileContextFactory")
	private FileConnectionFactory dfContext;

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void writeFileWithinTransaction(String fileName, byte[] textToWrite) {
		log.trace("<------------------Called writeFileWithinTransaction() method--------------->");
		try {
			FileConnection flow = dfContext.getFileConnection();

			flow.write(fileName, textToWrite);
			
			flow.close();

		log.trace("Exiting<------------------Called writeFileWithinTransaction() method--------------->");
		} catch (ResourceException re) {
			log.error("Caught exception during flow invocation test:", re);
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public byte[] readFileWithinTransaction(String fileName) {
		log.trace("<------------------Called readFileWithinTransaction() method--------------->");
		try {
			FileConnection flow = dfContext.getFileConnection();
			byte[] actualExtracted = flow.fetch(fileName);
			flow.close();
			
			log.info("Exiting <------------------Called readFileWithinTransaction() method--------------->");
			return actualExtracted;
		} catch (ResourceException re) {
			log.error("Caught exception during flow invocation test:", re);
		}
		return null;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void deleteFileWithinTransaction(String fileName) {

		FileConnection flow = null;
		log.trace("<------------------Called deleteFileWithinTransaction() method--------------->");
		try {
			flow = dfContext.getFileConnection();

			flow.delete(fileName);
			flow.close();
			log.trace("Exiting <------------------Called deleteFileWithinTransaction() method--------------->");
		} catch (ResourceException re) {
			log.error("Caught exception during flow invocation test:", re);

		}
		
	}
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void rollbackTransactionWriteMethod(String fileName, byte[] content) {

		FileConnection flow = null;
		log.info("<------------------Called deleteFileWithinTransaction() method--------------->");
		try {
			flow = dfContext.getFileConnection();
			
			flow.write(fileName, content );
			flow.close();
			log.info("Exiting invokeRarMethodCauseRollback() method");
		} catch (ResourceException re) {
			log.error("Caught exception during flow invocation test:", re);

		}
		throw new RuntimeException("Rollback transaction please...");

	}
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	@Asynchronous
	public void writeFileToResourceAdapterAsynchronosulyWithinTransaction(String fileName,  byte[] textToWrite){
		
		log.info("<------------------Called writeFileToResourceAdapterAsynchronosulyWithinTransaction() method--------------->");
		try {
			FileConnection flow = dfContext.getFileConnection();

			flow.write(fileName, textToWrite);
			flow.close();

		log.info("Exiting<------------------Called writeFileToResourceAdapterAsynchronosulyWithinTransaction() method--------------->");
		} catch (ResourceException re) {
			log.error("Caught exception during flow invocation test:", re);
		}
	}

	@Override
	public void deleteFileToResourceAdapterAsynchronosulyWithinTransaction(
			String fileName) {
		FileConnection flow = null;
		log.info("<------------------Called deleteFileToResourceAdapterAsynchronosulyWithinTransaction() method--------------->");
		try {
			flow = dfContext.getFileConnection();

			flow.delete(fileName);
			flow.close();
			log.info("Exiting <------------------Called deleteFileToResourceAdapterAsynchronosulyWithinTransaction() method--------------->");
		} catch (ResourceException re) {
			log.error("Caught exception during flow invocation test:", re);

		}
		
	}


}
