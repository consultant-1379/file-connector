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
package com.ericsson.nms.security.aicore.ra.test;

import java.io.File;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.ResourceAdapterArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * IntegrationTestDeploymentFactory will create the jee artifacts like rar and ear<br/>
 * @author Abdullah Sindhu
 *
 */
public class IntegrationTestDeploymentFactory {

	private static final Logger log = LoggerFactory
			.getLogger(IntegrationTestDeploymentFactory.class);

	/**
	 * Create deployment from given maven coordinates
	 * 
	 * @param mavenCoordinates
	 *            Maven coordinates in form of groupId:artifactId:type
	 * @return Deployment archive represented by this maven artifact
	 */
	public static ResourceAdapterArchive createRARDeploymentFromMavenCoordinates(
			final String mavenCoordinates) {
		log.debug("******Creating deployment {} for test******",
				mavenCoordinates);
		final File archiveFile = IntegrationTestDependencies
				.resolveArtifactWithoutDependencies(mavenCoordinates);
		if (archiveFile == null) {
			throw new IllegalStateException("Unable to resolve artifact "
					+ mavenCoordinates);
		}
		final ResourceAdapterArchive rar = ShrinkWrap.createFromZipFile(
				ResourceAdapterArchive.class, archiveFile);

		log.debug(
				"******Created from maven artifact with coordinates {} ******",
				mavenCoordinates);
		return rar;
	}

	/**
	 * Create web archive
	 * 
	 * @param name
	 * @return web archive
	 */
	public static WebArchive createWarDeployment(final String name) {
		log.debug("******Creating war deployment {} for test******", name);
		final WebArchive war = ShrinkWrap.create(WebArchive.class, name);
		log.debug(
				"******Created from maven artifact with coordinates {} ******",
				name);
		return war;
	}

}
