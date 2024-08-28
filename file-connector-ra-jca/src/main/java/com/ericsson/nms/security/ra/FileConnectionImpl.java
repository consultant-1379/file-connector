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

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.FileTime;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

import javax.resource.ResourceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.nms.security.ra.api.FileConnection;

/**
 * 
 FileConnectionImpl is the main connection to file resources for FileConnector ResourceAdapter
 * 
 * <p>
 * <b>WARNING:</b> <i>Extra care to be taken when changing the configuration of this
 * class as that can cause trouble for multithreading writing and or reading the files</i> 
 * 
 * <p>Following features are supported, FileConnectionImpl is a fully
 * <li>transactional class 
 * <li> have write, delete and read functionality as per interface
 * <li> supports concurrent and multithreaded access to file system
 * <li> if two concurrent threads try to write to the same file the later thread will overwrite
 * <li> supports caching and multi connections 
 * <li> supports concurrent access per connection and across multiple connections 
 * 
 * @author Abdullah Sindhu
 * 
 * @version $Revision: $
 */


public class FileConnectionImpl implements FileConnection {
	/** The logger */
	private static Logger logger = LoggerFactory
			.getLogger(FileConnectionImpl.class.getName());

	/** ManagedConnection */
	private FileManagedConnection mc;

	/** ManagedConnectionFactory */
	@SuppressWarnings("unused")
	private FileManagedConnectionFactory mcf;

	
	private String rootDirectory;
	private ConcurrentHashMap<String, byte[]> txCache;
	private Set<String> deletedFiles;
	private Closeable closeable;
	public static int totalConnections = 0;

	/**
	 * Default constructor
	 * 
	 * @param mc
	 *            FileManagedConnection
	 * @param mcf
	 *            FileManagedConnectionFactory
	 */
	public FileConnectionImpl(final FileManagedConnection mc,
			final FileManagedConnectionFactory mcf) {
		this.mc = mc;
		this.mcf = mcf;
        this.txCache = new ConcurrentHashMap<>();
        this.deletedFiles = new ConcurrentSkipListSet<>();
        totalConnections++;
        logger.info(Thread.currentThread().getName()+"...ConnectionNo................"+totalConnections++);
        
	}

	/**
	 * Close
	 */
	public void close() {
		logger.info(Thread.currentThread().getName()+"....**FileConnection.close " + toString());
		mc.closeHandle(this);
	}


	void createParentDir(String fileName) {
		logger.debug("Creating parent folder for file if not exists: " + fileName);
		File parentDirectory = new File(fileName).getParentFile();
		if (parentDirectory != null) {
			boolean success = parentDirectory.mkdirs();
			logger.debug("Creation of path: " + parentDirectory + " is: " + success);
		}
	}

	@Override
	public void write(String fileName, byte[] content) {
		logger.trace(Thread.currentThread().getName()+"....**FileConnection.write " + fileName + " " + new String (content)); 
	    final byte[] existingContent = this.txCache.get(fileName);
    	 logger.trace(Thread.currentThread().getName()+"....**FileConnection.write ...existingContent " + existingContent);
    	 if (existingContent == null) {
             this.txCache.put(fileName, content);
        } else {
            this.txCache.put(fileName, concat(existingContent, content));
        }
	}
	
	private byte[] concat(byte[] a, byte[] b) {
		final byte[] result = new byte[a.length + b.length];
		System.arraycopy(a, 0, result, 0, a.length);
		System.arraycopy(b, 0, result, a.length, b.length);
		return result;
	}

	public void begin() throws ResourceException {
		logger.info(Thread.currentThread().getName()+"....**FileConnection.begin " + toString()+"........"+totalConnections);
		
	}

	public void commit() throws ResourceException {
		logger.info(Thread.currentThread().getName()+"....**FileConnection.commit " + toString());
		try {
			processDeletions();
		} catch (IOException ex) {
			throw new IllegalStateException("Cannot delete files: " + ex, ex);
		}
		flushChanges();
	}

	public void rollback() throws ResourceException {
		logger.info(Thread.currentThread().getName()+"....**FileConnection.rollback....** " + toString());
		this.clear();
	}

	public void destroy() {
		logger.info(Thread.currentThread().getName()+"....**FileConnection.cleanup for connection..." + totalConnections);
		this.clear();
	}

	private void flushChanges() throws ResourceException {
		logger.info(Thread.currentThread().getName()+"....**FileConnection.flushing changes...**");
		//logger.info("**FileConnection.creating directory structure");
		
		Set<Entry<String, byte[]>> txSet = this.txCache.entrySet();
		for (Entry<String, byte[]> entry : txSet) {
			String fileName = entry.getKey();
			byte[] value = entry.getValue();
			this.createParentDir(fileName);				
			logger.info("....**FileConnection writing the file....**" + fileName);
			writeFile(fileName, value);
			this.txCache.remove(fileName);
		}
	}
	void writeFile(String fileName, byte[] content) throws ResourceException {
		logger.info(Thread.currentThread().getName()+"....**FileConnection.writeFile changes.....**");
		try (FileOutputStream fileOutputStream = new FileOutputStream(
				fileName, false)) {
			fileOutputStream.write(content);
			fileOutputStream.flush();
		} catch (IOException ex) {
			throw new ResourceException(ex);
		}
	}


	void processDeletions() throws IOException {
		for (String fileName : deletedFiles) {
			deleteFile(fileName);
		}
	}

	
	String getAbsoluteName(String fileName) {
		return this.rootDirectory + fileName;
		
	}

	@Override
	public byte[] fetch(String file) {
		logger.info(Thread.currentThread().getName()+"....**FileConnection.fetch ....**");
		try {

			final byte[] fileContent = readFromFile(file);
			final byte[] txContent = this.txCache.get(file);
			if (fileContent == null) {
				return txContent;
			} else {
				if (txContent == null) {
					return fileContent;
				} else {
					return concat(fileContent, txContent);
				}
			}
		} catch (IOException ex) {
			throw new IllegalStateException("Cannot access file: "
					+ getAbsoluteName(file), ex);
		}
	}

	public FileTime lastModified(String file) {
		logger.info(Thread.currentThread().getName()+"....**FileConnection.lastModified changes**....");
		try {
			return Files.getLastModifiedTime(Paths.get(getAbsoluteName(file)));
		} catch (IOException ex) {
			throw new IllegalStateException("Cannot access file: "
					+ getAbsoluteName(file), ex);
		}
	}

	byte[] readFromFile(String fileName) throws IOException {
		Path file = Paths.get(fileName);
	
		if (!Files.exists(file, LinkOption.NOFOLLOW_LINKS)) {
			return null;
		}
		return Files.readAllBytes(file);
	}

	void deleteFile(String absoluteName) throws IOException {
		Path file = Paths.get(absoluteName);
		if (!Files.exists(file, LinkOption.NOFOLLOW_LINKS)) {
			return;
		}
		Files.deleteIfExists(file);
	}

	@Override
	public void delete(String file) {
		logger.info(Thread.currentThread().getName()+"....**FileConnection.delete .....**");

		this.txCache.remove(file);
		this.deletedFiles.add(file);
	}

	public void clear() {
		logger.info(Thread.currentThread().getName()+"....**FileConnection.clear.............. **"+totalConnections);
		this.txCache.clear();
		this.deletedFiles.clear();
	}

	@Override
	public String toString() {
		return "FileConnection{" + "rootDirectory=" + rootDirectory + ", txCache="
				+ txCache + ", genericManagedConnection=" + closeable + '}';
	}
}
