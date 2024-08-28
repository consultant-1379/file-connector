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
package com.ericsson.nms.security.aicore.ra.test.smoke.testclasses;

import javax.ejb.EJB;

import junit.framework.Assert;

import org.jboss.arquillian.container.test.api.*;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.nms.security.aicore.ra.test.IntegrationTestDeploymentFactory;
import com.ericsson.nms.security.aicore.ra.test.smoke.deployment.SmokeTestDeploymentFactory;
import com.ericsson.nms.security.aicore.ra.test.smoke.deployment.mock.EJBCallingResourceAdapter;
import com.ericsson.nms.security.aicore.test.smoke.dependencies.SmokeTestDependencies;

/**
 * Rollback transaction test case, This test will make sure the resource <br/>
 * can rollback the transaction it has started.
 * 
 * @author Abdullah Sindhu
 * 
 */
@RunWith(Arquillian.class)
public class RollbackTransactionTest {

	/**
	 * Since we want different scenarios, we will control arq deployment
	 * manually
	 * 
	 */
	@ArquillianResource
	private ContainerController controller;

	@ArquillianResource
	private Deployer deployer;

	@EJB
	EJBCallingResourceAdapter injectedEjb;

	private static final Logger log = LoggerFactory
			.getLogger(RollbackTransactionTest.class);

	/**
	 * Create rar deployment from built code
	 * 
	 * @return rar deployment
	 */
	@Deployment(name = "file-connection-rar-deployment-rollback", managed = false, testable = false)
	public static Archive<?> createResourceAdapter() {
		return IntegrationTestDeploymentFactory
				.createRARDeploymentFromMavenCoordinates(SmokeTestDependencies.COM_ERICSSON_NMS_SECURITY_FILE_JCA);
	}

	/**
	 * Create war deployment containing this test and simple ejb used to invoke
	 * ra method under transaction
	 * 
	 * @return war deployment
	 */
	@Deployment(name = "file-connection-war-ejb-rollback", managed = false, testable = true)
	public static Archive<?> createWarWithEjb() {
		return SmokeTestDeploymentFactory.createWarTestDeployment();
	}

	/**
	 * Start executing tests, deploy rar
	 */
	@Test
	@InSequence(1)
	@OperateOnDeployment("file-connection-rar-deployment-rollback")
	public void testDeployRar() throws Exception {
		log.info("-----------Rollback transaction test case, deploy rar--------------");
		this.deployer.deploy("file-connection-rar-deployment-rollback");
	}

	@Test
	@InSequence(2)
	@OperateOnDeployment("file-connection-war-ejb-rollback")
	public void deployWarWithEjb() throws Exception {
		log.info("-----------Rollback transaction test case, deploy test.war--------------");
		this.deployer.deploy("file-connection-war-ejb-rollback");
	}

	@Ignore
	@Test(expected = RuntimeException.class)
	@InSequence(3)
	@OperateOnDeployment("file-connection-war-ejb-rollback")
	public void testInjectedRarUnderTransactionWithRollback() throws Exception {
		log.info("-----------Rollback transaction test case, invoke invokeRarMethodCauseRollback() ------------------first Transaction--------------");
		this.injectedEjb.rollbackTransactionWriteMethod("RollBackTransactionFile", "RollBackTransactionFile".getBytes());
		
		
		log.info("-----------Rollback transaction test case, invoke invokeRarMethodCauseRollback()--------------------second Transaction--------------");
		byte [] actual = this.injectedEjb.readFileWithinTransaction("RollBackTransactionFile");
		
		Assert.assertNull("Should be no content written to the file system as the transaction was rollback ", actual);
	}

	@Ignore
	@Test
	@InSequence(4)
	@OperateOnDeployment("file-connection-war-ejb-rollback")
	public void undeployWarWithEjb() throws Exception {
		log.info("-----------Rollback transaction test case, undeploy test.war--------------");
		this.deployer.undeploy("file-connection-war-ejb-rollback");
	}

	@Ignore
	@Test
	@InSequence(5)
	@OperateOnDeployment("file-connection-rar-deployment-rollback")
	public void undeployRar() {
		log.info("-----------Rollback transaction test case, undeploy rar--------------");
		this.deployer.undeploy("file-connection-rar-deployment-rollback");
	}
}
