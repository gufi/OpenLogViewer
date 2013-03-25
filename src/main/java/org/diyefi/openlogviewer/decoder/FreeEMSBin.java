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
package org.diyefi.openlogviewer.decoder;


import java.io.File;
import java.io.FileInputStream;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.ResourceBundle;

import org.diyefi.openlogviewer.OpenLogViewer;
import org.diyefi.openlogviewer.decoder.LogField.types;
import org.diyefi.openlogviewer.genericlog.GenericLog;


/**
 * This function takes a binary log file, plucks FreeEMS packets out of it,
 * filters for standard packets and parses them into fields with appropriate scaling.
 */
public class FreeEMSBin extends AbstractDecoder {
	private static final int initialLength = 75000;
	private static final int loadFactor = 2;

	private static final int MINIMUM_PACKET_LENGTH = 3; // Flag byte, payload id word, no payload - defined by protocol
	private static final int MAXIMUM_PACKET_LENGTH = 0x0820; // Buffer size on FreeEMS vanilla, take this from config file eventually
	// NOTE, standard supported log types require their own structure definition in the code,
	// override-able via files, matched looked for first, then local, then fall back to code

	private static final short ESCAPE_BYTE = 0xBB;         // Used as an unsigned byte
	private static final short START_BYTE = 0xAA;          // Used as an unsigned byte
	private static final short STOP_BYTE = 0xCC;           // Used as an unsigned byte
	private static final short ESCAPED_ESCAPE_BYTE = 0x44; // Used as an unsigned byte
	private static final short ESCAPED_START_BYTE = 0x55;  // Used as an unsigned byte
	private static final short ESCAPED_STOP_BYTE = 0x33;   // Used as an unsigned byte

	private boolean startFound;
	private final ResourceBundle labels;
	private final File logFile;
	private final short[] packetBuffer; // For use as an unsigned byte
	private final GenericLog decodedLog;
	//private final Thread t;
	private int packetLength; // Track packet length
	private int firstPayloadIDFound = -1;

	private final long startTime; // Low tech profiling var

	// Sequential number for reset detection
	private int tempClockIndex;
	private int lastTempClockValue;
	private boolean firstTempClockStored = false;

	// Medium-high resolution time counter with good accuracy
	private int clockInMilliSecondsIndex;
	private int lastClockInMilliSecondsValue;
	private boolean firstClockInMilliSecondsStored = false;
	private long accurateClock; // Convert to floating point just before storing.

	private static String[] coreStatusAFlagNames = {
		"CS-FuelPumpPrime",
		"CS-unused1",
		"CS-unused2",
		"CS-unused3",
		"CS-unused4",
		"CS-unused5",
		"CS-unused6",
		"CS-unused7"
	};

	private static String[] decoderFlagsFlagNames = {
		"DF-CombustionSync",
		"DF-CrankSync",
		"DF-CamSync",
		"DF-LAST_TIMESTAMP_VALID",
		"DF-LAST_PERIOD_VALID",
		"DF-LAST_MATCH_VALID",
		"DF-Spare-6",
		"DF-Spare-7"
	};

	private static String[] ignitionLimiterFlagsNames = {
		"LIG-RPM",
		"LIG-OverBoost",
		"LIG-Spare0",
		"LIG-Spare1",
		"LIG-Spare2",
		"LIG-Spare3",
		"LIG-Spare4",
		"LIG-Spare5"
	};

	private static String[] injectionLimiterFlagsNames = {
		"LIN-RPM",
		"LIN-OverBoost",
		"LIN-Spare0",
		"LIN-Spare1",
		"LIN-Spare2",
		"LIN-Spare3",
		"LIN-Spare4",
		"LIN-Spare5"
	};

	private static String[] flaggableFlagsNames = {
		"FF-callsToUISRs",               // to ensure we aren't accidentally triggering unused ISRs.
		"FF-lowVoltageConditions",       // low voltage conditions.
		"FF-decoderSyncLosses",          // Number of times cam, crank or combustion sync is lost.
		"FF-decoderSyncCorrections",     // Definite decoder syncs found while already synced in a different position.
		"FF-decoderSyncStateClears",     // Sync loss called when not synced yet, thus discarding data and preventing sync.
		"FF-serialNoiseErrors",          // Incremented when noise is detected
		"FF-serialFramingErrors",        // Incremented when a framing error occurs
		"FF-serialParityErrors",         // Incremented when a parity error occurs
		"FF-serialOverrunErrors",        // Incremented when overrun occurs (duplicated in KeyUserDebug below)
		"FF-serialEscapePairMismatches", // Incremented when an escape is found but not followed by an escapee
		"FF-serialStartsInsideAPacket",  // Incremented when a start byte is found inside a packet
		"FF-serialPacketsOverLength",    // Incremented when the buffer fills up before the end
		"FF-serialChecksumMismatches",   // Incremented when calculated checksum did not match the received one
		"FF-serialPacketsUnderLength",   // Incremented when a packet is found that is too short
		"FF-commsDebugMessagesNotSent",  // Incremented when a debug message can't be sent due to the TX buffer
		"FF-commsErrorMessagesNotSent"   // Incremented when an error message can't be sent due to the TX buffer
	};

	// This should be read from a file at some point, as it's going to be flexible. See FreeEMS/src/inc/structs.h for definitive answers.
	private static LogField[] fields = {
		// CoreVars struct contents:
		new LogField("IAT",  100, -273.15),   // Inlet Air Temperature           : 0.0 -   655.35       (0.01 Kelvin (/100))
		new LogField("CHT",  100, -273.15),   // Coolant / Head Temperature      : 0.0 -   655.35       (0.01 Kelvin (/100))
		new LogField("TPS",  640),   // Throttle Position Sensor        : 0.0 -   102.398438   (0.0015625 % (/640))
		new LogField("EGO",  32768), // Exhaust Gas Oxygen              : 0.0 -     1.99996948 (0.0000305175781 lambda (/32768))
		new LogField("MAP",  100),   // Manifold Absolute Pressure      : 0.0 -   655.35       (0.01 kPa (/100))
		new LogField("AAP",  100),   // Atmospheric Absolute Pressure   : 0.0 -   655.35       (0.01 kPa (/100))
		new LogField("BRV",  1000),  // Battery Reference Voltage       : 0.0 -    65.535      (0.001 Volts (/1000))
		new LogField("MAT",  100, -273.15),   // Manifold Air Temperature        : 0.0 -   655.35       (0.01 Kelvin (/100))
		new LogField("EGO2", 32768), // Exhaust Gas Oxygen              : 0.0 -     1.99996948 (0.0000305175781 lambda (/32768))
		new LogField("IAP",  100),   // Intercooler Absolute Pressure   : 0.0 -   655.35       (0.01 kPa (/100))
		new LogField("MAF"),         // Mass Air Flow                   : 0.0 - 65535.0        (raw units from lookup)
		new LogField("DMAP"),        // Delta MAP kPa/second or similar : 0.0 - 65535.0        (raw units from lookup)
		new LogField("DTPS"),        // Delta TPS %/second or similar   : 0.0 - 65535.0        (raw units from lookup)
		new LogField("RPM", 2),      // Revolutions Per Minute (Calced) : 0.0 - 32767.5        (0.5 RPM (/2))
		new LogField("DRPM"),        // Delta RPM (Calced)              : 0.0 - 65535.0        (raw units from lookup)
		new LogField("DDRPM"),       // Delta Delta RPM (Calced)        : 0.0 - 65535.0        (raw units from lookup)

		// DerivedVars struct contents:
		new LogField("LoadMain", 512),          // Configurable unit of load, scale same as map by default
		new LogField("VEMain", 512),            // Volumetric Efficiency in 0 - 128%
		new LogField("Lambda", 32768),          // Integral Lambda 0 - 2.0
		new LogField("AirFlow"),                // raw intermediate, remove
		new LogField("densityAndFuel"),         // raw intermediate, remove
		new LogField("BasePW", 1250),           // Raw PW before corrections 0 - ~52ms
		new LogField("ETE", (100.0 / 16384.0)), // Engine Temperature Enrichment percentage correction 0 - 400%
		new LogField("TFCTotal", 1250),         // Transient fuel correction PW (+/-)  0 - ~52ms
		new LogField("EffectivePW", 1250),      // Actual PW of fuel delivery 0 - ~52ms
		new LogField("IDT", 1250),              // PW duration before fuel flow begins 0 - ~52ms
		new LogField("RefPW", 1250),            // Reference electrical PW 0 - ~52ms
		new LogField("Advance", 50),            // Ignition advance (scaled degrees / oneDegree(currently 50) = degrees) 0 - ~64*
		new LogField("Dwell", 1250),            // Dwell period, s 0 - ~52ms

		// KeyUserDebugs struct contents
		new LogField("tempClock", types.UINT8),     // Incremented once per log sent, to be moved to a char TODO
		new LogField("spareChar", types.UINT8),     // Incremented once per log sent, to be moved to a char TODO
		new LogField("coreStatusA",  types.BITS8, coreStatusAFlagNames),    // Duplicated, migrate here, remove global var
		new LogField("decoderFlags", types.BITS8, decoderFlagsFlagNames),   // Various decoder state flags
		new LogField("flaggableFlags", types.BITS16, flaggableFlagsNames),  // Flags to go with our flaggables struct.
		new LogField("currentEvent",             types.UINT8), // Which input event was last to come in
		new LogField("syncLostWithThisID",       types.UINT8), // A unique identifier for the reason behind a loss of sync
		new LogField("syncLostOnThisEvent",      types.UINT8), // Where in the input pattern it all went very badly wrong
		new LogField("syncCaughtOnThisEvent",    types.UINT8), // Where in the input pattern that things started making sense
		new LogField("syncResetCalls",           types.UINT8), // Sum of losses, corrections and state clears
		new LogField("primaryTeethSeen",         types.UINT8), // Free running counters for number of input events, useful at lower RPM
		new LogField("secondaryTeethSeen",       types.UINT8), // Free running counters for number of input events, useful at lower RPM
		new LogField("serialOverrunErrors",      types.UINT8), // Incremented when an overrun occurs due to high ISR load, just a fact of life at high RPM
		new LogField("serialHardwareErrors",     types.UINT8), // Sum of noise, parity, and framing errors
		new LogField("serialAndCommsCodeErrors", types.UINT8), // Sum of checksum, escape mismatches, starts inside, and over/under length
		new LogField("inputEventTimeTolerance"),   // Required to tune noise rejection over RPM TODO add to LT1 and MissingTeeth
		new LogField("zsp10"), // Spare US variable
		new LogField("zsp9"),  // Spare US variable
		new LogField("zsp8"),  // Spare US variable
		new LogField("zsp7"),  // Spare US variable
		new LogField("zsp6"),  // Spare US variable
		new LogField("zsp5"),  // Spare US variable
		new LogField("zsp4"),  // Spare US variable
		new LogField("zsp3"),  // Spare US variable
		new LogField("clockInMilliSeconds"),       // Migrate to start of all large datalogs once analysed
		new LogField("clock8thMSsInMillis", 8), // Migrate to start of all large datalogs once analysed
		new LogField("ignitionLimiterFlags", types.BITS8, ignitionLimiterFlagsNames),
		new LogField("injectionLimiterFlags", types.BITS8, injectionLimiterFlagsNames)
	};

	// NO default constructor, a file or path to a file MUST be given
	// Reason: File()'s constructors are ambiguous cannot give a null value
	/**
	 * FreeEmsBin Constructor: <code>String</code> path to your binary log
	 * @param path The file system path of the log file.
	 *
	 */
	public FreeEMSBin(final String path, final ResourceBundle labels) {
		this(new File(path), labels);
	}

	/**
	 * FreeEmsBin Constructor: <code>File</code> object of your Binary log
	 * @param f The file reference to the log file.
	 */
	public FreeEMSBin(final File f, final ResourceBundle labels) {
		this.labels = labels;
		startTime = System.currentTimeMillis(); // Let's profile this bitch! OS style :-)
		logFile = f;
		startFound = false;
		packetBuffer = new short[MAXIMUM_PACKET_LENGTH];
		packetLength = 0;

		// TODO put matching name grabber here

		// Eventually pull this in from files and config etc instead of default, if it makes sense to.
		final String[] headers = new String[fields.length * 32]; // Hack to make it plenty big, trim afterwards...
		int headersPosition = 0;
		for (int i = 0; i < fields.length; i++) {
			if ((fields[i].getType() == types.BITS8) || (fields[i].getType() == types.BITS16) || (fields[i].getType() == types.BITS32)) {
				for (int j = 0; j < fields[i].getBitFieldNames().length; j++) {
					final String flagID = getFlagName(fields[i], j);
					headers[headersPosition] = flagID;
					headersPosition++;
				}
			} else {
				final String fieldID = fields[i].getID();
				headers[headersPosition] = fieldID;
				if("clockInMilliSeconds".equals(fieldID)){
					clockInMilliSecondsIndex = i;
				}else if("tempClock".equals(fieldID)){
					tempClockIndex = i;
				}
				headersPosition++;
			}
		}

		decodedLog = new GenericLog(Arrays.copyOfRange(headers, 0, headersPosition), initialLength, loadFactor, labels);

		//t = new Thread(this, "FreeEMSBin Loading");
		//t.setPriority(Thread.MAX_PRIORITY);
		//t.start();
	}


	/**
	 * DecodeLog will use the current <code>logFile</code> parse through it and when required pass the job <br>
	 * to the required method of this class such as decodePacket or checksum.
	 */
	@Override
	public final void run() {
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		try {
			// file setup
			final byte[] readByte = new byte[1];
			short uByte = 0;

			// These can be caused by noise, but if there is no noise, then it's a code issue with the firmware!
			int escapePairMismatches = 0; // Incremented when an escape is found but not followed by an escapee
			int startsInsideAPacket  = 0; // Incremented when a start byte is found inside a packet
			int packetsOverLength    = 0; // Incremented when the buffer fills up before the end
			int packetsUnderLength   = 0; // Incremented when a packet is found that is too short
			int checksumMismatches   = 0; // Incremented when calculated checksum did not match the received one
			int packetsParsedFully   = 0; // Number of packets that matched all requirements and got into the log
			int packetLengthWrong    = 0; // The length should match the ID passed in, if not, fail.
			int strayBytesLost       = 0; // How many bytes were not in a packet! (should be low, ie, under one packet)
			int payloadIDWrong       = 0; // Requests to parse packet as A when packet was of type B

			startFound = false;
			fis = new FileInputStream(logFile);
			bis = new BufferedInputStream(fis);
			decodedLog.setLogStatus(GenericLog.LogState.LOG_LOADING);
			while (bis.read(readByte) != -1) {
				uByte = unsignedValueOf(readByte[0]);
				if (uByte == START_BYTE) {
					if (!startFound) {
						startFound = true;
					} else {
						startsInsideAPacket++;
					}
					packetLength = 0; // Reset array position, always (discard existing data recorded, if any)
				} else if (startFound) { // Skip stray bytes until a start is found
					if (uByte == STOP_BYTE) {
						if (packetLength < MINIMUM_PACKET_LENGTH) {
							packetsUnderLength++;
						} else if (packetLength > MAXIMUM_PACKET_LENGTH) {
							packetsOverLength++;
						} else {
							decodedLog.incrementPosition(); // Do this before attempting to load data
							final short[] justThePacket = Arrays.copyOfRange(packetBuffer, 0, packetLength);
							if (checksum(justThePacket)) {
								if (decodeBasicLogPacket(justThePacket)) {
									packetsParsedFully++;
								} else {
									packetLengthWrong++;
									payloadIDWrong++; // TODO maybe handle various possibilities post valid packet being parsed
								}
							} else {
								checksumMismatches++;
							}
						}
						startFound = false;
					} else if (uByte == ESCAPE_BYTE) {
						if (bis.read(readByte) != -1) { // Read in the byte to be un-escaped
							uByte = unEscape(unsignedValueOf(readByte[0])); // un-escape this byte
							if (uByte != (short) -1) {
								packetBuffer[packetLength] = uByte; // Store the un-escaped data for processing later
								packetLength++;
							} else {
								startFound = false; // The rest of the data should be ignored
								escapePairMismatches++;
							}
						}
					} else {
						if (packetLength < MAXIMUM_PACKET_LENGTH) {
							packetBuffer[packetLength] = uByte; // Store the data as-is for processing later
							packetLength++;
						} else {
							startFound = false;
							packetsOverLength++;
						}
					}
				} else {
					strayBytesLost++;
				}
			}
			decodedLog.setLogStatus(GenericLog.LogState.LOG_LOADED);

			System.out.println(OpenLogViewer.NEWLINE + "Binary Parsing Statistics:" + OpenLogViewer.NEWLINE);

			System.out.println("EscapePairMismatches: " + escapePairMismatches + " Incremented when an escape is found but not followed by an escapee");
			System.out.println("StartsInsideAPacket:  " + startsInsideAPacket  + " Incremented when a start byte is found inside a packet");
			System.out.println("PacketsOverLength:    " + packetsOverLength    + " Incremented when the buffer fills up before the end");
			System.out.println("PacketsUnderLength:   " + packetsUnderLength   + " Incremented when a packet is found that is too short");
			System.out.println("ChecksumMismatches:   " + checksumMismatches   + " Incremented when calculated checksum did not match the received one");
			System.out.println("PacketsParsedFully:   " + packetsParsedFully   + " Number of packets that matched all requirements and got into the log");
			System.out.println("PacketLengthWrong:    " + packetLengthWrong    + " The length should match the ID passed in, if not, fail.");
			System.out.println("StrayBytesLost:       " + strayBytesLost       + " How many bytes were not in a packet! (should be low, ie, under one packet");
			System.out.println("PayloadIDWrong:       " + payloadIDWrong       + " Requests to parse packet as A when packet was of type B");

			System.out.println(OpenLogViewer.NEWLINE + "Thank you for choosing FreeEMS!");

		} catch (Exception e) {
			e.printStackTrace();
			decodedLog.setLogStatusMessage(e.getMessage());
		} finally { // Setup the log to be displayed TODO in future it will just display as it goes
			OpenLogViewer.getInstance().getEntireGraphingPanel().setGraphSize(decodedLog.getRecordCount());
			decodedLog.setLogStatus(GenericLog.LogState.LOG_LOADED);
			System.out.println("Loaded " + (decodedLog.getRecordCount() + 1) + " records in " + (System.currentTimeMillis() - startTime) + " millis!");

			try {
				if (bis != null) {
					bis.close();
				}
			} catch (IOException ioe) {
				ioe.printStackTrace();
				System.out.println("Failed To Close BIS Stream!");
			}

			try {
				if (fis != null) {
					fis.close();
				}
			} catch (IOException ioe) {
				ioe.printStackTrace();
				System.out.println("Failed To Close FIS Stream!");
			}
		}
	}

	/**
	 * This method decodes a packet by splitting up the data into larger data types to keep the unsigned info <br>
	 * This method could probably use a little work
	 * @param packet is a <code>short</code> array containing 1 full packet
	 * @param payloadIDToParse The ID of the payload type to expect and accept.
	 */
	private boolean decodeBasicLogPacket(final short[] packet) {
		final int HEADER_HAS_LENGTH_INDEX   = 0;
//		final int HEADER_IS_NACK_INDEX      = 1;
		final int HEADER_HAS_SEQUENCE_INDEX = 2;
//		final int HEADER_RESERVED_E_INDEX   = 3;
//		final int HEADER_RESERVED_D_INDEX   = 4;
//		final int HEADER_RESERVED_C_INDEX   = 5;
//		final int HEADER_RESERVED_B_INDEX   = 6;
//		final int HEADER_RESERVED_A_INDEX   = 7;
// TODO use a class to hold a packet in and provide getters for each of the above

		// Increment post use
		int position = 0;

		final short flags = packet[position];
		position++;

		final short payloadIdUpper = packet[position];
		position++;
		final short payloadIdLower = packet[position];
		position++;
		final int payloadId = (payloadIdUpper * 256) + payloadIdLower;

		if (firstPayloadIDFound < 0) {
			firstPayloadIDFound = payloadId;
		} else if (payloadId != firstPayloadIDFound) {
			return false; // TODO make this a code, or throw exception, but it's not exceptional at all for this to occur...
		}
		// record packet IDs that don't match desired in a Other Packets field, eventually put the packet
		// itself into meta data with an index number. how to invalidate these when looping? if looping...

		final int[] flagValues = processFlagBytes(flags, 8);

		if (flagValues[HEADER_HAS_SEQUENCE_INDEX] == 1) {
			position++; // Skip this!
		}

		if (flagValues[HEADER_HAS_LENGTH_INDEX] == 1) {
			position += 2; // Ignore this for now, it's the payload length, check it vs actual length and check actual vs required.
		}

		int lengthOfFields = 0;
		for (int i = 0; i < fields.length; i++) {
			lengthOfFields += fields[i].getType().getWidth();
		}
		// TODO warn if length in config is not equal, but allow both sides of wrong as it
		// is reasonable to not care about some and also to truncate shorter on the ECU side.

		final int payloadLength = ((packet.length - position) - 1);
		if (payloadLength != lengthOfFields) { // First run through to find out what the lengths are.
			System.out.print(" Fields length is: " + lengthOfFields);
			System.out.print(" Packet length is: " + packet.length);
			System.out.println(" Payload length is: " + payloadLength);
			return false;
		}

		for (int i = 0; i < fields.length; i++) {
			final LogField field = fields[i];

			int rawValue = 0;
			for (int j = 0; j < field.getType().getWidth(); j++) {
				rawValue = (rawValue * 256) + packet[position];
				position++;
			}

			if (i == clockInMilliSecondsIndex) {
				if (firstClockInMilliSecondsStored) {
					final int increase = rawValue - lastClockInMilliSecondsValue;

					if(increase < 0) {
						final int actualIncrease = rawValue + (65536 - lastClockInMilliSecondsValue);
						accurateClock += actualIncrease;
					} else {
						accurateClock += increase;
					}

					final double currentClock = (double) accurateClock / 1000d;
					decodedLog.addValue(GenericLog.elapsedTimeKey, currentClock);
				} else {
					firstClockInMilliSecondsStored = true;
				}

				lastClockInMilliSecondsValue = rawValue;
			} else if (i == tempClockIndex) {
				if (firstTempClockStored) {
					if (rawValue == 0) {
						if (lastTempClockValue != 255) {
							decodedLog.addValue(GenericLog.tempResetKey, 1); // 1 = reset
						} // Else all is well.
					} else {
						final int dropOutLength = (rawValue - lastTempClockValue);
						if (dropOutLength != 1) {
							final double resetValue = (double) (1000 + dropOutLength) / -1000d;
							decodedLog.addValue(GenericLog.tempResetKey, resetValue); // -1 to -1.254 = lost comms
						} // Else all is well.
					}
				} else {
					firstTempClockStored = true;
				}

				lastTempClockValue = rawValue;
			}

			if ((field.getType() == types.UINT8) || (field.getType() == types.UINT16) || (field.getType() == types.UINT32)) {
				final double scaledValue = (double) rawValue / field.getDivBy();
				final double finalValue = scaledValue + field.getAddTo();
				decodedLog.addValue(field.getID(), finalValue);
			} else if ((field.getType() == types.BITS8) || (field.getType() == types.BITS16) || (field.getType() == types.BITS32)) {
				final int[] processedFlags = processFlagBytes(rawValue, (8 * field.getType().getWidth()));
				for (int j = 0; j < processedFlags.length; j++) {
					final String flagID = getFlagName(field, j);
					decodedLog.addValue(flagID, processedFlags[j]);
				}
			} else if (field.getType() == types.SINT8) { // TODO handle signed ints...
				decodedLog.addValue(field.getID(), rawValue);
			} else if (field.getType() == types.SINT16) {
				decodedLog.addValue(field.getID(), rawValue);
			} else if (field.getType() == types.SINT32) {
				decodedLog.addValue(field.getID(), rawValue);
			}
			// TODO handle floats
		}
		return true; // TODO FIXME : Default to all things being good till I attack this!
	}

	private int[] processFlagBytes(final long valueOfFlags, final int numberOfFlags) {
		if ((numberOfFlags != 8) && (numberOfFlags != 16) && (numberOfFlags != 32)) {
			throw new IllegalArgumentException("Basic units of computer sciene apply, embedded flags are never " + numberOfFlags + " wide!");
			// Unless they are 64, but shhhh...
		}

		final int[] flagValues = new int[numberOfFlags];
		int comparison = 1;
		long remainingValueOfFlags = valueOfFlags;
		for (int i = 0; i < numberOfFlags; i++) {
			if ((remainingValueOfFlags % (2 * comparison)) == comparison) {
				flagValues[i] = 1;
				remainingValueOfFlags -= comparison;
			}
			comparison *= 2;
		}

		return flagValues;
	}

	/**
	 * performs a check sum based on the packet data <br>
	 * the checksum needs to be improved however
	 * @param packet
	 * @return true or false based on if the checksum passes
	 */
	private boolean checksum(final short[] packet) {
		if (packetLength > 0) {
			final short includedSum = packet[packetLength - 1]; // sum is last byte
			long veryBIGsum = 0;
			for (int x = 0; x < packetLength - 1; x++) {
				veryBIGsum += packet[x];
			}
			final short calculatedSum = (short) (veryBIGsum % 256);
			return (calculatedSum == includedSum);
		} else {
			return false;
		}
	}

	/**
	 * Transforms an escape-encoded byte back into what it was before transmission
	 *
	 * @param uByte - byte to be Un-escaped
	 * @return -1 if bad data or the proper value of the escaped byte
	 */
	private short unEscape(final short uByte) {
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
	 * Transforms an unsigned char into a format suitable for Java, a short.
	 *
	 * @param uInt8 the raw signed byte representation of our raw unsigned char
	 * @return the value of the unsigned byte stored in a short
	 */
	private short unsignedValueOf(final byte uInt8) {
		return (short) (0xFF & uInt8);
	}

	/**
	 *
	 * @return Misc data about this log
	 * <br> to be implemented in full later
	 */
	@Override
	public final String toString() {
		return super.toString();
	}

	private String getFlagName(final LogField field, final int flagIndex) {
		return field.getBitFieldNames()[flagIndex] + "-" + field.getID() + "-B" + flagIndex;
	}
}
