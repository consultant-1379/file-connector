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

import org.jboss.arquillian.container.test.api.*;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.ericsson.nms.security.aicore.ra.test.IntegrationTestDeploymentFactory;
import com.ericsson.nms.security.aicore.test.smoke.dependencies.SmokeTestDependencies;
/**
 * Deployment test will make sure the resource adapter can be deployed <br/>
 * to jboss deployment directory
 * @author Abdullah Sindhu
 *
 */
@RunWith(Arquillian.class)
public class DeploymentTest {

	/**
	 * Since we want different scenarios, we will control arq deployment
	 * manually
	 * 
	 */
	@ArquillianResource
	private ContainerController controller;

	@ArquillianResource
	private Deployer deployer;

	/**
	 * Create ra deployment from built code
	 * 
	 * @return rar deployment
	 */
	@Deployment(name = "file-connection-rar", testable = false, managed = false)
	public static Archive<?> depoloyAICoreRARService() {
		return IntegrationTestDeploymentFactory
				.createRARDeploymentFromMavenCoordinates(SmokeTestDependencies.COM_ERICSSON_NMS_SECURITY_FILE_JCA);
	}

	/**
	 * Start executing tests
	 */

	@Test
	@InSequence(1)
	@OperateOnDeployment("file-connection-rar")
	public void deployAIConnectionCore() throws Exception {
		this.deployer.deploy("file-connection-rar");
	}

	@Test
	@InSequence(2)
	@OperateOnDeployment("file-connection-rar")
	public void unDeployAIConnectionCore() {
		this.deployer.undeploy("file-connection-rar");
	}

}
