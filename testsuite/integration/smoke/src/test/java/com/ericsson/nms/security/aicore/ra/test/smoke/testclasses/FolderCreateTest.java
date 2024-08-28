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

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import javax.ejb.EJB;

import org.codehaus.plexus.util.FileUtils;
import org.jboss.arquillian.container.test.api.*;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.ericsson.nms.security.aicore.ra.test.IntegrationTestDeploymentFactory;
import com.ericsson.nms.security.aicore.ra.test.smoke.deployment.SmokeTestDeploymentFactory;
import com.ericsson.nms.security.aicore.ra.test.smoke.deployment.mock.EJBCallingResourceAdapter;
import com.ericsson.nms.security.aicore.test.smoke.dependencies.SmokeTestDependencies;

@RunWith(Arquillian.class)
public class FolderCreateTest {

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
	
	/**
	 * Create ra deployment from built code
	 * 
	 * @return rar deployment
	 */
	@Deployment(name = "file-connection-rar-deployment-commit", managed = false, testable = false)
	public static Archive<?> createResourceAdapter() {
		return IntegrationTestDeploymentFactory
				.createRARDeploymentFromMavenCoordinates(SmokeTestDependencies.COM_ERICSSON_NMS_SECURITY_FILE_JCA);
	}

	/**
	 * Create war deployment containing this test case and simple ejb used to
	 * triger transactional call to rar
	 * 
	 * @return war deployment
	 */
	@Deployment(name = "file-connection-war-ejb-commit", managed = false, testable = true)
	public static Archive<?> createWarWithEjb() {
		return SmokeTestDeploymentFactory.createWarTestDeployment();
	}

	/**
	 * Start executing tests, deploy rar
	 */
	@Test
	@InSequence(1)
	@OperateOnDeployment("file-connection-rar-deployment-commit")
	public void testDeployRar() throws Exception {
		this.deployer.deploy("file-connection-rar-deployment-commit");
	}
	
	@Test
	@InSequence(2)
	@OperateOnDeployment("file-connection-war-ejb-commit")
	public void deployWarWithEjb() throws Exception {
		this.deployer.deploy("file-connection-war-ejb-commit");
	}

	@Test
	@InSequence(3)
	@OperateOnDeployment("file-connection-war-ejb-commit")
	public void testFolderCreate() throws IOException {
		
		String dataString = "textToWrite";
		byte[] data = dataString.getBytes();
		
		String file1 = "./storeFolder/file1";
		this.injectedEjb.writeFileWithinTransaction(file1, data);
		assertEquals(dataString, new String(this.injectedEjb.readFileWithinTransaction(file1)));
		
		String file2 = "./storeFolder/newfolder/file2";
		this.injectedEjb.writeFileWithinTransaction(file2, data);
		assertEquals(dataString, new String(this.injectedEjb.readFileWithinTransaction(file2)));
		
		String file3 = "./storeFolder/newfolder2/newfolder3/file3";
		this.injectedEjb.writeFileWithinTransaction(file3, data);
		assertEquals(dataString, new String(this.injectedEjb.readFileWithinTransaction(file3)));
	}
	
	@Test
	@InSequence(4)
	public void removeTestFolder() throws IOException {
		FileUtils.deleteDirectory(new File("./storeFolder/"));
	}
}
