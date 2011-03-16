/* Open Log Viewer
 *
 * Copyright 2011 Fred Cooke
 *
 * This file is part of the OpenLogViewer project.
 *
 * OpenLogViewer software is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenLogViewer software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with any OpenLogViewer software.  If not, see http://www.gnu.org/licenses/
 *
 * I ask that if you make any changes to this file you fork the code on github.com!
 *
 */

package main.java.org.diyefi.openlogviewer.decoder;

/**
 * Data storage for a generic FreeEMS packet.
 *
 * @author Fred Cooke
 */
public class FreeEmsPacket {

	// Flags from header flags byte
    private boolean hasLength;
    private boolean isNack;
    private boolean hasSequence;
    private boolean reserved3;
    private boolean reserved4;
    private boolean reserved5;
    private boolean reserved6;
    private boolean reserved7;

    // Fields from header
    private short length; // original packet length, which by now, is correct - always valid, regardless of flag
    private short sequence;
    private short payloadId;

    // Data from payload
    private short errorId; // only populated if isNack is true;
    private short payload[];
    
    // Checksum from footer
    private short checksum; // included and calculated were the same, or this object would not have been created    
    
	public FreeEmsPacket(short rawPacket[]) {
		throw new RuntimeException("Not implemented!");
	}

	public boolean hasLength() {
		return hasLength;
	}

	public boolean isNack() {
		return isNack;
	}

	public boolean hasSequence() {
		return hasSequence;
	}

	public boolean reserved3() {
		return reserved3;
	}

	public boolean reserved4() {
		return reserved4;
	}

	public boolean reserved5() {
		return reserved5;
	}

	public boolean reserved6() {
		return reserved6;
	}

	public boolean reserved7() {
		return reserved7;
	}

	public short getLength() {
		return length;
	}

	public short getSequence() {
		if(hasSequence){
			return sequence;
		}else{
			throw new RuntimeException("Has no sequence!");
		}
	}

	public short getPayloadId() {
		return payloadId;
	}

	public short getErrorId() {
		if(isNack){
			return errorId;
		}else{
			throw new RuntimeException("Not a nack!");
		}
	}

	public short[] getPayload() {
		return payload;
	}

	public int getPayloadLength() {
		return payload.length;
	}

	public short getChecksum() {
		return checksum;
	}

}
