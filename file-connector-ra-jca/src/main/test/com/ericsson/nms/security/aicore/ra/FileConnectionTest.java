package com.ericsson.nms.security.aicore.ra;

//import static org.mockito.Mockito.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import java.io.Closeable;

import javax.resource.ResourceException;

import org.junit.Before;
import org.junit.Test;

import com.ericsson.nms.security.ra.FileConnectionImpl;

/**
 * A unit test to test the fileConnection implementations, please note that <br/>
 * <li> more complex scenarios are tested in arquillian tests 
 * @author Abdullah Sindhu
 *
 */
public class FileConnectionTest {

    FileConnectionImpl cut;
    Closeable closeable;

    @Before
    public void initialize() {
       this.cut = new FileConnectionImpl(null, null);
    }

    
    @Test
    public void writeAndRollback() throws Exception{
        final String key = "hey";
        this.cut.begin();
        final byte[] content = "duke".getBytes();
        this.cut.write(key, content);
        byte[] actual = this.cut.fetch(key);
        assertThat(actual,is(content));
        this.cut.rollback();
        actual = this.cut.fetch(key);
        assertNull(actual);
        
        clear(key);
    
    }
    
    @Test
    public void writeAndCommit() throws Exception{
        final String key = "hey";
        this.cut.begin();
        final byte[] content = "duke".getBytes();
        this.cut.write(key, content);
        byte[] actual = this.cut.fetch(key);
        assertThat(actual,is(content));
        this.cut.commit();
        actual = this.cut.fetch(key);
        assertThat(actual,is(content));
        clear(key);
    }
    
    @Test
    public void deleteAndCommit() throws Exception{
        final String key = "hey";
        this.cut.begin();
        final byte[] content = "duke".getBytes();
        this.cut.write(key, content);
        byte[] actual = this.cut.fetch(key);
        assertThat(actual,is(content));
        this.cut.commit();
        this.cut.begin();
        clear(key);
        actual = this.cut.fetch(key);
        assertNull(actual);
    }

    @Test
    public void fetchInTransactionShouldNotTriggerWriteOnCommit() throws Exception {
        final String key = "fetch";

        // given
        final byte[] existingContent = "hello".getBytes();
        this.cut.begin();
        this.cut.write(key, existingContent);
        this.cut.commit();

        // when
        this.cut.begin();
        this.cut.fetch(key);
        this.cut.commit();

        // then
        final byte[] actual = this.cut.fetch(key);
        assertThat(actual, is(existingContent));
        clear(key);
    }

    
	public void clear(final String key) throws ResourceException {
		this.cut.delete(key);
        this.cut.commit();
	}
    
 @Test
    public void writeMultipleTimes() throws Exception {
        final String key = "multiWrite";

        this.cut.begin();
        final byte[] firstEntry = "hello ".getBytes();
        this.cut.write(key, firstEntry);

        final byte[] secondEntry = "world!".getBytes();
        this.cut.write(key, secondEntry);
        this.cut.commit();

        final byte[] actual = this.cut.fetch(key);
        assertThat(actual, is("hello world!".getBytes()));
        
        clear(key);
    }    
}
