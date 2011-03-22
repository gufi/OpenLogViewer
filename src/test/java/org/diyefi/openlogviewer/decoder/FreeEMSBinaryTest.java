/**
 * 
 */
package org.diyefi.openlogviewer.decoder;

import org.diyefi.openlogviewer.decoder.FreeEMSBin;

import junit.framework.TestCase;

/**
 * @author fred
 *
 */
public class FreeEMSBinaryTest extends TestCase {
	private FreeEMSBin bin;
	private short[] goodPacket = {1,2,3,6};
	private short[] badPacket = {1,2,3,66};
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
		bin = new FreeEMSBin("/Users/fred/file"); // bogus, passing a path to something like this is wrong, the file descriptor should be passed, or something else
		bin.packetLength = 4;
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testFail(){
		assertFalse(bin.checksum(badPacket));
	}
	
	public void testPass(){
		assertTrue(bin.checksum(goodPacket));
	}

}
