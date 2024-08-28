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

import org.jboss.arquillian.container.test.api.ContainerController;
import org.jboss.arquillian.container.test.api.Deployer;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
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
 * 
 * FileResourceAdapterTransactionTest is end to end test to check overall behaviour of FileConnector ResourceAdapter
 * and related services.
 * <p>
 * <b>WARNING:</b> <i>Extra care to be taken when changing the config of these
 * tests as that can cause trouble writing or reading the files</i> 
 * 
 * <p>Following test cases are written
 * <li>testDeployRar
 * <li>deployWarWithEjb
 * <li>deleteFile
 * <li>testInjectedRarUnderTransaction 
 * <li>testInjectedRarUnderTransactionTwoSimultaneousOrSerialisedWrites
 * <li>testInjectedRarUnderTransactionTwoConcurrentWrites 
 * <li>testInjectedRarUnderTransaction100ConcurrentWritesDifferentFileNames 
 * <li>testInjectedRarUnderTransactionAsynchronouslyWrite
 * <li>testInjectedRarUnderTransactionAsynchronouslyDelete
 * 
 * @author Abdullah Sindhu
 * 
 */
@RunWith(Arquillian.class)
public class FileResourceAdapterTransactionTest {

	private static final String FILE_NAME = "./store/RaceDoesNotMatter";

	public static final String TEXT_INSERTED = "RaceDoesNotMatter RaceDoesNotMatter RaceDoesNotMatter";
	
	public static final String TEXT_INSERTED_SECOND = "RaceDoesNotMatter RaceDoesNotMatter RaceDoesNotMatter RaceDoesNotMatter";

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
			.getLogger(FileResourceAdapterTransactionTest.class);

	private String bIG_FILE;

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
		log.info("------------Commit transaction test case, deploying rar--------------");
		this.deployer.deploy("file-connection-rar-deployment-commit");
	}
	
	@Test
	@InSequence(2)
	@OperateOnDeployment("file-connection-war-ejb-commit")
	public void deployWarWithEjb() throws Exception {
		log.info("-----------Commit transaction test case, deploying test.war--------------");
		this.deployer.deploy("file-connection-war-ejb-commit");
	}
	
	
	public void deleteFile(String fileName)
	{
		this.injectedEjb.deleteFileWithinTransaction(fileName);
	}
	
	
	@Test
	@InSequence(3)
	@OperateOnDeployment("file-connection-war-ejb-commit")
	public void testInjectedRarUnderTransaction() throws Exception {
		log.info("-----------invoking testInjectedRarUnderTransaction--------------");
		
		log.info("----------- writeFileWithinTransaction ejb is invoked for file named--------------", FILE_NAME);
		this.injectedEjb.writeFileWithinTransaction(FILE_NAME, TEXT_INSERTED.getBytes());
		
		
		
		
		log.info("----------- readFileWithinTransaction ejb method is invoked file--------------");
		byte[] actualInserted = this.injectedEjb.readFileWithinTransaction(FILE_NAME);
		
		log.info("----------- This is what is in the file --------------", actualInserted.toString());
		
		
		log.info("actualInserted = {} TEXT_INSERTED = {}", new String (actualInserted), TEXT_INSERTED );
		
		Assert.assertEquals("Actually Inserted is not equal to what is read", new String (actualInserted), TEXT_INSERTED);
		
		deleteFile(FILE_NAME);
	}
	
	/**
	 * This test will fire off two serialized writes to the same file and check, the file should be 
	 * overwritten by the second serialised request, shouldn't merge. 
	 * @throws Exception
	 */
	
	@Test
	@InSequence(4)
	@OperateOnDeployment("file-connection-war-ejb-commit")
	public void testInjectedRarUnderTransactionTwoSimultaneousOrSerialisedWrites() throws Exception {
		log.info("-----------invoking testInjectedRarUnderTransactionTwoSimultaneousOrSerialisedWrites--------------");
		
		log.info("----------- writeFileWithinTransaction ejb is invoked file is written--------------", FILE_NAME);
		this.injectedEjb.writeFileWithinTransaction(FILE_NAME, TEXT_INSERTED.getBytes());
		
		log.info("----------- writeFileWithinTransaction ejb is invoked file is written--------------", FILE_NAME);
		this.injectedEjb.writeFileWithinTransaction(FILE_NAME, TEXT_INSERTED_SECOND.getBytes());
		
		
		
		
		byte[] actualInserted = this.injectedEjb.readFileWithinTransaction(FILE_NAME);
		
		
		log.info("----------- readFileWithinTransaction ejb was invoked file is now read, content is--------------", actualInserted.toString());
		
		log.info("actualInserted = {} TEXT_INSERTED = {}", new String (actualInserted), TEXT_INSERTED_SECOND );
		
		Assert.assertEquals("Actually Inserted is not equal to what is read", new String (actualInserted), TEXT_INSERTED_SECOND);
		
		deleteFile(FILE_NAME);
	}
	
	/**
	 * This test will fire off two concurrent writes to the same file and check, the file shouldn't be 
	 * overwritten by the second concurrent request, shouldn't be merge. The second thread should rollback. 
	 * @throws Exception
	 */
	
	@Ignore
	@Test
	@InSequence(5)
	@OperateOnDeployment("file-connection-war-ejb-commit")
	public void testInjectedRarUnderTransactionTwoConcurrentWrites() throws Exception {
		log.info("-----------invoking testInjectedRarUnderTransactionTwoConcurrentWrites--------------");
		
		log.info("----------- writeFileWithinTransaction ejb is invoked file is being written, named --------------", FILE_NAME);
		this.injectedEjb.writeFileToResourceAdapterAsynchronosulyWithinTransaction(FILE_NAME, TEXT_INSERTED.getBytes());
		this.injectedEjb.writeFileToResourceAdapterAsynchronosulyWithinTransaction(FILE_NAME, TEXT_INSERTED_SECOND.getBytes());
		
		
		log.info("----------- sleeping the thread to allow previous two to complete --------------");
		Thread.sleep(1000);
		
		
		log.info("----------- reading the file after the previous threaeds are completed --------------");
		byte[] actualInserted = this.injectedEjb.readFileWithinTransaction(FILE_NAME);
		
		
		log.info("----------- readFileWithinTransaction ejb is invoked file is read--------------", actualInserted.toString());
		
		log.info("actualInserted = {} TEXT_INSERTED = {}", new String (actualInserted), TEXT_INSERTED );
		
		Assert.assertEquals("Actually Inserted is not equal to what is read", new String (actualInserted), TEXT_INSERTED_SECOND);
		
		deleteFile(FILE_NAME);
	}

	
	@Test
	@InSequence(6)
	@OperateOnDeployment("file-connection-war-ejb-commit")
	public void testInjectedRarUnderTransaction100ConcurrentWritesDifferentFileNames() throws Exception {
		log.info("-----------invoking testInjectedRarUnderTransaction100ConcurrentWritesDifferentFileNames--------------");
		
		log.info("----------- writeFileWithinTransaction ejb is invoked file is being written, named --------------", FILE_NAME);
		
		bIG_FILE = null;
		for (int i = 0; i < 5000; i++)
		{
			bIG_FILE += TEXT_INSERTED;
			
			
		}
		
		for (int i = 0; i < 100; i++)
		{
			this.injectedEjb.writeFileToResourceAdapterAsynchronosulyWithinTransaction(FILE_NAME+i, (bIG_FILE+i).getBytes());
		}
		
		
		log.info("----------- sleeping the thread for 10 seconds to allow previous 100 to complete --------------");
		Thread.sleep(5000);
		
		
		log.info("----------- reading the file after the previous threaeds are completed --------------");
		
		for (int i = 0; i < 100; i++)
		{
			byte[] actualInserted = this.injectedEjb.readFileWithinTransaction(FILE_NAME+i);
		
			String actualWritten =  actualInserted != null ? new String (actualInserted) : "Empty";
			log.info("----------- readFileWithinTransaction ejb is invoked file is read--------------", actualWritten);
			
			log.info("actualInserted = {} TEXT_INSERTED = {}", actualWritten, bIG_FILE+i );
			
			Assert.assertEquals("Actually Inserted is not equal to what is read", actualWritten, bIG_FILE+i);
			//deleteFile(FILE_NAME+i);
		}
		
		
	}

	
	@Test
	@InSequence(7)
	@OperateOnDeployment("file-connection-war-ejb-commit")
	public void testInjectedRarUnderTransaction100ConcurrentDeletesDifferentFileNames() throws Exception {
		log.info("-----------invoking testInjectedRarUnderTransaction100DeletesDifferentFileNames--------------");
		
		log.info("----------- writeFileWithinTransaction ejb is invoked file is being written, named --------------", FILE_NAME);
		
		
		
		
		log.info("----------- reading the file after the previous threaeds are completed --------------");
		
		for (int i = 0; i < 100; i++)
		{
			byte[] actualInserted = this.injectedEjb.readFileWithinTransaction(FILE_NAME+i);
		
			StringBuffer actualWritten =  actualInserted.toString() != null ? new StringBuffer (new String(actualInserted)) : new StringBuffer("Empty");
			log.info("----------- readFileWithinTransaction ejb is invoked file is read--------------", actualWritten);
			
			log.info("actualInserted = {} TEXT_INSERTED = {}", actualWritten, bIG_FILE+i );
			
			//Assert.assertEquals("Actually Inserted is not equal to what is read", actualWritten, bIG_FILE+i);
			deleteFile(FILE_NAME+i);
		}
		
		
	}

	/**
	 * In this test, we are trying to write to the same file from two concurrent threads.<br/>
	 * Because these are two concurrent threads, so, the second thread should not fail, why ? <br/>
	 * The requirement is to allow the second thread to overwrite the content of first.  <br/>
	 * 
	 * <p>
	 * We can also allow two concurrent threads to block the second thread until the first is done <br/>
	 * and/or to merge the contents and to harmoniously write their merge contents, but this is really<br/>
	 * an append, not a write. The write requirements is or should be in our opinion to only allow the<br/>
	 * one thread to write to the file. <br/>
	 * @throws Exception
	 */
	
	@Test
	@InSequence(8)
	@OperateOnDeployment("file-connection-war-ejb-commit")
	public void testInjectedRarUnderTransactionAsynchronously50ConcurrentWriteSameFileName() throws Exception {
		log.info("-----------invoking testInjectedRarUnderTransactionAsynchronously--------------");
		
		
		for (int i = 0; i < 50; i++)
		{
			this.injectedEjb.writeFileToResourceAdapterAsynchronosulyWithinTransaction(FILE_NAME, (TEXT_INSERTED+i).getBytes());
		}
		
		
		Thread.sleep(9000);
		log.info("----------- writeFileWithinTransaction ejb is invoked file is written--------------", FILE_NAME);
		byte[] actualInserted = this.injectedEjb.readFileWithinTransaction(FILE_NAME);
		log.info("----------- readFileWithinTransaction ejb is invoked file is read--------------", actualInserted.toString());
		
		log.info("actualInserted = {} TEXT_INSERTED = {}", new String (actualInserted), TEXT_INSERTED+49 );
		
		
		deleteFile(FILE_NAME);
	}
	
	
	@Test
	@InSequence(9)
	@OperateOnDeployment("file-connection-war-ejb-commit")
	public void testInjectedRarUnderTransactionAsynchronously50ConcurrentDelete() throws Exception {
		log.info("-----------invoking testInjectedRarUnderTransactionAsynchronously50ConcurrentDeletes--------------");
		
		
		log.info("----------- writeFileWithinTransaction ejb is invoked for file named--------------", FILE_NAME);
		this.injectedEjb.writeFileWithinTransaction(FILE_NAME, TEXT_INSERTED.getBytes());
		
		for (int i = 0; i < 50; i++)
		{
			this.injectedEjb.deleteFileToResourceAdapterAsynchronosulyWithinTransaction(FILE_NAME);
		}
		
	}
	
	@Ignore
	@Test
	@InSequence(10)
	@OperateOnDeployment("file-connection-war-ejb-commit")
	public void undeployWarWithEjb() throws Exception {
		log.info("-----------Commit transaction test case, undeploy war--------------");
		this.deployer.undeploy("file-connection-war-ejb-commit");
	}

	@Ignore
	@Test
	@InSequence(11)
	@OperateOnDeployment("file-connection-rar-deployment-commit")
	public void undeployRar() {
		log.info("-----------Commit transaction test case, undeploy rar--------------");
		this.deployer.undeploy("file-connection-rar-deployment-commit");
	}
}
