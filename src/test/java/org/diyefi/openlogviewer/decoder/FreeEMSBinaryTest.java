/**
 * 
 */
package org.diyefi.openlogviewer.decoder;

import junit.framework.TestCase;

/**
 * @author fred
 *
 */
public class FreeEMSBinaryTest extends TestCase {

	/**
	 * @param name
	 */
	public FreeEMSBinaryTest(String name) {
		super(name);
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testChecksum(){
		assertEquals(true, true);
	}
	
	public void testFail(){
		assertTrue(true);
	}
	
	public void testPass(){
		assertTrue(true);
	}

}
