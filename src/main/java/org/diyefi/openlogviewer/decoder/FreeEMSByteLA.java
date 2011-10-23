/* Open Log Viewer
 *
 * Copyright 2011
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
package org.diyefi.openlogviewer.decoder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import org.diyefi.openlogviewer.genericlog.GenericLog;

/**
 * To be replaced soon, ignore this code...
 */
public class FreeEMSByteLA implements Runnable { // implements runnable to make this class theadable

	private final short ESCAPE_BYTE = 0xBB;         // for unsigned byte
	private final short START_BYTE = 0xAA;          // for unsigned byte
	private final short STOP_BYTE = 0xCC;           // for unsigned byte
	private final short ESCAPED_ESCAPE_BYTE = 0x44; // for unsigned byte
	private final short ESCAPED_START_BYTE = 0x55;  // for unsigned byte
	private final short ESCAPED_STOP_BYTE = 0x33;   // for unsigned byte

	private short[] wholePacket;
	private File logFile;
	private FileInputStream logStream;
	private boolean startFound;
	private GenericLog decodedLog;
	private Thread t;

	String[] headers = {"PTIT", "T0", "T1", "T2", "T3", "T4", "T5", "T6", "T7"};

	/**
	 * FreeEmsBin Constructor: <code>String</code> path to your binary log
	 * @param file The log file to parse and load.
	 */
	public FreeEMSByteLA(File file) {
		logFile = file;
		startFound = false;
		wholePacket = new short[3000];
		decodedLog = new GenericLog(headers);

		t = new Thread(this, "FreeEMSByteLA Loading");
		t.setPriority(Thread.MAX_PRIORITY);
		t.start();
	}

	/**
	 * DecodeLog will use the current <code>logFile</code> parse through it and when required pass the job <br>
	 * to the required method of this class such as decodePacket or checksum.
	 *
	 * TODO integrate into other decoder...
	 */
	public void run() {
		try {
			byte[] readByte = new byte[1];
			short uByte = 0;

			startFound = false;
			logStream = new FileInputStream(logFile);
			decodedLog.setLogStatus(GenericLog.LOG_LOADING);
			int packetLength = 0;
			while (logStream.read(readByte) != -1) {
				uByte = (short) (readByte[0] & 0xff); // mask the byte in case something screwey happens
				if (uByte == START_BYTE) {
					if (!startFound) {
						startFound = true;
					}
				} else if (startFound) { // do NOTHING until a start is found
					if (uByte == STOP_BYTE) {
						if (checksum(wholePacket, packetLength)) {
							decodePacket(wholePacket, packetLength);
							startFound = false;
						}
						packetLength = 0;
					} else if (uByte == ESCAPE_BYTE) {
						if (logStream.read(readByte) != -1) { // read in the next byte, as it is to be escaped
							uByte = unEscapeByte((short) (readByte[0] & 0xff)); // unescape this byte
							if (uByte != (short) -1) {
								wholePacket[packetLength] = uByte; // add the escaped byte to a temp storage area for processing later
								packetLength++;

							} else {
								startFound = false; // Data was bad, the rest of the data should be ignored
							}
						}
					} else {
						wholePacket[packetLength] = uByte; // add the info to a temp storage area for processing later
						packetLength++;
					}
				}
			}
			decodedLog.setLogStatus(1);
		} catch (IOException IOE) {
			System.out.println(IOE.getMessage());
		}
	}

	/**
	 * This method decodes a packet by splitting up the data into larger datatypes to keep the unsigned info <br>
	 * This method could probably use a litle work
	 * @param packet is a <code>short</code> array containing 1 full packet
	 *
	 */
	private void decodePacket(short[] packet, int length) {
		int PTIT = (int) packet[3];
		int T0 = 0;
		int T1 = 0;
		int T2 = 0;
		int T3 = 0;
		int T4 = 0;
		int T5 = 0;
		int T6 = 0;
		int T7 = 0;
		decodedLog.addValue("PTIT", PTIT);

		if ((PTIT % 2) == 1) {
			T0 = 1;
			PTIT -= 1;
		}
		if ((PTIT % 4) == 2) {
			T1 = 1;
			PTIT -= 2;
		}
		if ((PTIT % 8) == 4) {
			T2 = 1;
			PTIT -= 4;
		}
		if ((PTIT % 16) == 8) {
			T3 = 1;
			PTIT -= 8;
		}
		if ((PTIT % 32) == 16) {
			T4 = 1;
			PTIT -= 16;
		}
		if ((PTIT % 64) == 32) {
			T5 = 1;
			PTIT -= 32;
		}
		if ((PTIT % 128) == 64) {
			T6 = 1;
			PTIT -= 64;
		}
		if (PTIT == 128) {
			T7 = 1;
		}

		decodedLog.addValue("T0", T0);
		decodedLog.addValue("T1", T1);
		decodedLog.addValue("T2", T2);
		decodedLog.addValue("T3", T3);
		decodedLog.addValue("T4", T4);
		decodedLog.addValue("T5", T5);
		decodedLog.addValue("T6", T6);
		decodedLog.addValue("T7", T7);
	}

	/**
	 * performs a check sum based on the packet data <br>
	 * the checksum needs to be improved however
	 * @param packet
	 * @return true or false based on if the checksum passes
	 */
	private boolean checksum(short[] packet, int length) {
		if (length > 0) {
			short includedSum = packet[length - 1]; // sum is last byte
			long veryBIGsum = 0;
			for (int x = 0; x < length - 1; x++) {
				veryBIGsum += packet[x];
			}
			short calculatedSum = (short) (veryBIGsum % 256);
			return (calculatedSum == includedSum);
		} else {
			return false;
		}
	}

	/**
	 * takes the byte to be escaped and returns the proper value
	 * @param uByte - byte to be Un-escaped
	 * @return -1 if bad data or the proper value of the escaped byte
	 */
	private short unEscapeByte(short uByte) {
		if (uByte == ESCAPED_START_BYTE) {
			return START_BYTE;
		} else if (uByte == ESCAPED_STOP_BYTE) {
			return STOP_BYTE;
		} else if (uByte == ESCAPED_ESCAPE_BYTE) {
			return ESCAPE_BYTE;
		} else {
			return (short) -1;
		}
	}

	/**
	 *
	 * @return Misc data about this log
	 * <br> to be implemented in full later
	 */
	@Override
	public String toString() {
		return super.toString();
	}
}
