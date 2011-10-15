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
import java.io.IOException;
import java.util.Arrays;

import org.diyefi.openlogviewer.decoder.LogField.types;
import org.diyefi.openlogviewer.genericlog.GenericLog;


/**
 * This function takes a binary log file, plucks FreeEMS packets out of it,
 * filters for standard packets and parses them into fields with appropriate scaling.
 * 
 * @todo 
 */
public class FreeEMSBin implements Runnable { // implements runnable to make this class theadable

	private final int minimumPacketLength = 3; // Flag byte, payload id word, no payload - defined by protocol
	private final int maximumPacketLength = 0x0820; // Buffer size on FreeEMS vanilla, take this from config file eventually
	private final int payloadIDToParse = 0x0191; // Default freeems basic log, get this from config file and maybe default to this
	// NOTE, standard supported log types require their own structure definition in the code, override-able via files, matched looked for first, then local, then fall back to code

	private final short ESCAPE_BYTE = 0xBB;         // Used as an unsigned byte
	private final short START_BYTE = 0xAA;          // Used as an unsigned byte
	private final short STOP_BYTE = 0xCC;           // Used as an unsigned byte
	private final short ESCAPED_ESCAPE_BYTE = 0x44; // Used as an unsigned byte
	private final short ESCAPED_START_BYTE = 0x55;  // Used as an unsigned byte
	private final short ESCAPED_STOP_BYTE = 0x33;   // Used as an unsigned byte

	private boolean startFound;
	private File logFile;
	private FileInputStream logStream;
	private short[] packetBuffer; // For use as an unsigned byte
	private GenericLog decodedLog;
	private Thread t;
	private int packetLength; // Track packet length

	String[] coreStatusAFlagNames = {
			"CS-FuelPumpPrime",
			"CS-unused1",
			"CS-unused2",
			"CS-unused3",
			"CS-unused4",
			"CS-unused5",
			"CS-unused6",
			"CS-unused7"
		};

	String[] decoderFlagsFlagNames = {
			"DF-CombustionSync",
			"DF-CrankSync",
			"DF-CamSync",
			"DF-LAST_TIMESTAMP_VALID",
			"DF-LAST_PERIOD_VALID",
			"DF-unused5",
			"DF-unused6",
			"DF-unused7"
		};
	
	String[] flaggableFlagsNames = {
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

	private LogField[] fields = { // This should be read from a file at some point, as it's going to be flexible. See FreeEMS/src/inc/structs.h for definitive answers.
		// CoreVars struct contents:
		new LogField("IAT",  100),   // Inlet Air Temperature           : 0.0 -   655.35       (0.01 Kelvin (/100))
		new LogField("CHT",  100),   // Coolant / Head Temperature      : 0.0 -   655.35       (0.01 Kelvin (/100))
		new LogField("TPS",  640),   // Throttle Position Sensor        : 0.0 -   102.398438   (0.0015625 % (/640))
		new LogField("EGO",  32768), // Exhaust Gas Oxygen              : 0.0 -     1.99996948 (0.0000305175781 lambda (/32768))
		new LogField("MAP",  100),   // Manifold Absolute Pressure      : 0.0 -   655.35       (0.01 kPa (/100))
		new LogField("AAP",  100),   // Atmospheric Absolute Pressure   : 0.0 -   655.35       (0.01 kPa (/100))
		new LogField("BRV",  1000),  // Battery Reference Voltage       : 0.0 -    65.535      (0.001 Volts (/1000))
		new LogField("MAT",  100),   // Manifold Air Temperature        : 0.0 -   655.35       (0.01 Kelvin (/100))
		new LogField("EGO2", 32768), // Exhaust Gas Oxygen              : 0.0 -     1.99996948 (0.0000305175781 lambda (/32768))
		new LogField("IAP",  100),   // Intercooler Absolute Pressure   : 0.0 -   655.35       (0.01 kPa (/100))
		new LogField("MAF"),         // Mass Air Flow                   : 0.0 - 65535.0        (raw units from lookup)
		new LogField("DMAP"),        // Delta MAP kPa/second or similar : 0.0 - 65535.0        (raw units from lookup)
		new LogField("DTPS"),        // Delta TPS %/second or similar   : 0.0 - 65535.0        (raw units from lookup)
		new LogField("RPM", 2),      // Revolutions Per Minute (Calced) : 0.0 - 32767.5        (0.5 RPM (/2))
		new LogField("DRPM"),        // Delta RPM (Calced)              : 0.0 - 65535.0        (raw units from lookup)
		new LogField("DDRPM"),       // Delta Delta RPM (Calced)        : 0.0 - 65535.0        (raw units from lookup)

		// DerivedVars struct contents:
		new LogField("LoadMain", 512),        // Configurable unit of load, scale same as map by default
		new LogField("VEMain", 512),          // Volumetric Efficiency in 0 - 128%
		new LogField("Lambda", 32768),        // Integral Lambda 0 - 2.0
		new LogField("AirFlow"),              // raw intermediate, remove
		new LogField("densityAndFuel"),       // raw intermediate, remove
		new LogField("BasePW", 1250),         // Raw PW before corrections 0 - ~52ms
		new LogField("ETE", (100.0/32768.0)), // Engine Temperature Enrichment percentage correction 0 - 200%
		new LogField("TFCTotal", 1250),       // Transient fuel correction PW (+/-)  0 - ~52ms
		new LogField("EffectivePW", 1250),    // Actual PW of fuel delivery 0 - ~52ms
		new LogField("IDT", 1250),            // PW duration before fuel flow begins 0 - ~52ms
		new LogField("RefPW", 1250),          // Reference electrical PW 0 - ~52ms
		new LogField("Advance", 50),          // Ignition advance (scaled degrees / oneDegree(currently 50) = degrees) 0 - ~64*
		new LogField("Dwell", 1250),          // Dwell period, s 0 - ~52ms

		// KeyUserDebugs struct contents
		new LogField("tempClock"),     // Incremented once per log sent, to be moved to a char TODO
		new LogField("coreStatusA",  types.BITS8, coreStatusAFlagNames),    // Duplicated, migrate here, remove global var
		new LogField("decoderFlags", types.BITS8, decoderFlagsFlagNames),   // Various decoder state flags
		new LogField("flaggableFlags", types.BITS16, flaggableFlagsNames),///< Flags to go with our flaggables struct.
		new LogField("currentEvent",             types.UINT8), // Which input event was last to come in
		new LogField("syncLostWithThisID",       types.UINT8), // A unique identifier for the reason behind a loss of sync
		new LogField("syncLostOnThisEvent",      types.UINT8), // Where in the input pattern it all went very badly wrong
		new LogField("syncCaughtOnThisEvent",    types.UINT8), // Where in the input pattern that things started making sense
		new LogField("syncResetCalls",           types.UINT8), // Sum of losses, corrections and state clears
		new LogField("primaryTeethSeen",         types.UINT8), // Free running counters for number of input events, useful at lower RPM
		new LogField("secondaryTeethSeen",       types.UINT8), // Free running counters for number of input events, useful at lower RPM
		new LogField("serialOverrunErrors",      types.UINT8), // Incremented when an overrun occurs due to high interrupt load, not a fault, just a fact of life at high RPM
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
		new LogField("clock8thMSsInMillis", 8) // Migrate to start of all large datalogs once analysed
	};

	// NO default constructor, a file or path to a file MUST be given
	// Reason: File()'s constructors are ambiguous cannot give a null value
	/**
	 * FreeEmsBin Constructor: <code>String</code> path to your binary log
	 * @param String path
	 *
	 */
	public FreeEMSBin(String path) {
		this(new File(path));
	}

	/**
	 * FreeEmsBin Constructor: <code>File</code> object of your Binary log
	 * @param File f
	 */
	public FreeEMSBin(File f) {
		logFile = f;
		startFound = false;
		packetBuffer = new short[6000];
		packetLength = 0;
		
		// Eventually pull this in from files and config etc instead of default, if it makes sense to.
		String[] headers = new String[fields.length * 32]; // Hack to make it plenty big, trim afterwards...
		int headersPosition = 0;
		for(int i=0;i<fields.length;i++){
			if((fields[i].type == types.BITS8) || (fields[i].type == types.BITS16) || (fields[i].type == types.BITS32)){
				for(int j=0;j<fields[i].bitFieldNames.length;j++){
					String flagID = fields[i].bitFieldNames[j] + "-" + fields[i].ID + "-B"+ j;
					headers[headersPosition] = flagID;
					headersPosition++;
				}
			}else{
				headers[headersPosition] = fields[i].ID;
				headersPosition++;
			}
		}

		decodedLog = new GenericLog(Arrays.copyOfRange(headers, 0, headersPosition));

		t = new Thread(this, "FreeEMSBin Loading");
		t.setPriority(Thread.MAX_PRIORITY);
		t.start();
	}


	/**
	 * DecodeLog will use the current <code>logFile</code> parse through it and when required pass the job <br>
	 * to the required method of this class such as decodePacket or checksum.
	 */
	@Override
	public void run() {
		try {
			// file setup
			byte[] readByte = new byte[1];
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
			logStream = new FileInputStream(logFile);
			decodedLog.setLogStatus(GenericLog.LOG_LOADING);
			while (logStream.read(readByte) != -1) {
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
						if(packetLength < minimumPacketLength){
							packetsUnderLength++;
						}else if(packetLength > maximumPacketLength){
							packetsOverLength++;
						}else{
							short[] justThePacket = new short[packetLength];
							for(int i=0;i<packetLength;i++){
								justThePacket[i] = packetBuffer[i];
							}
							if (checksum(justThePacket)){//, packetLength)) {
								if(decodeBasicLogPacket(justThePacket, payloadIDToParse)){
									packetsParsedFully++;
								}else{
									packetLengthWrong++;
									payloadIDWrong++; // TODO maybe handle various possibilities post valid packet being parsed
								}
							}else{
								checksumMismatches++;
							}
						}
						startFound = false;
					} else if (uByte == ESCAPE_BYTE) {
						if (logStream.read(readByte) != -1) { // Read in the byte to be un-escaped
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
						packetBuffer[packetLength] = uByte; // Store the data as-is for processing later
						packetLength++;
					}
				}else{
					strayBytesLost++;
				}
			}
			decodedLog.setLogStatus(GenericLog.LOG_LOADED);

			System.out.println(System.getProperty("line.separator") + "Binary Parsing Statistics:" + System.getProperty("line.separator"));

			System.out.println("Value: " + escapePairMismatches + " Incremented when an escape is found but not followed by an escapee");
			System.out.println("Value: " + startsInsideAPacket  + " Incremented when a start byte is found inside a packet");
			System.out.println("Value: " + packetsOverLength    + " Incremented when the buffer fills up before the end");
			System.out.println("Value: " + packetsUnderLength   + " Incremented when a packet is found that is too short");
			System.out.println("Value: " + checksumMismatches   + " Incremented when calculated checksum did not match the received one");
			System.out.println("Value: " + packetsParsedFully   + " Number of packets that matched all requirements and got into the log");
			System.out.println("Value: " + packetLengthWrong    + " The length should match the ID passed in, if not, fail.");
			System.out.println("Value: " + strayBytesLost       + " How many bytes were not in a packet! (should be low, ie, under one packet");
			System.out.println("Value: " + payloadIDWrong       + " Requests to parse packet as A when packet was of type B");

			System.out.println(System.getProperty("line.separator") + "Thank you for choosing FreeEMS!");

		} catch (IOException IOE) {
			System.out.println(IOE.getMessage());
			// TODO Add code to handle or warn of the error
		} 
	}

	/**
	 * This method decodes a packet by splitting up the data into larger data types to keep the unsigned info <br>
	 * This method could probably use a little work
	 * @param packet is a <code>short</code> array containing 1 full packet
	 *
	 */
	private boolean decodeBasicLogPacket(short[] packet, int payloadIDToParse) {
		final int HEADER_HAS_LENGTH_INDEX   = 0;
		final int HEADER_IS_NACK_INDEX      = 1;
		final int HEADER_HAS_SEQUENCE_INDEX = 2;
		final int HEADER_RESERVED_E_INDEX   = 3;
		final int HEADER_RESERVED_D_INDEX   = 4;
		final int HEADER_RESERVED_C_INDEX   = 5;
		final int HEADER_RESERVED_B_INDEX   = 6;
		final int HEADER_RESERVED_A_INDEX   = 7;

		// Increment post use
		int position = 0;

		short flags = packet[position];
		position++;

		short payloadIdUpper = packet[position];
		position++;
		short payloadIdLower = packet[position];
		position++;
		int payloadId = (payloadIdUpper * 256) + payloadIdLower;

		if(payloadId != payloadIDToParse){
			return false; // TODO make this a code, or throw exception, but it's not exceptional at all for this to occur...
		}

		int[] flagValues = processFlagBytes(flags, 8);

		if(flagValues[HEADER_HAS_SEQUENCE_INDEX] == 1){
			position++; // Skip this!
		}

		if(flagValues[HEADER_HAS_LENGTH_INDEX] == 1){
			position += 2; // Ignore this for now, it's the payload length, check it vs actual length and check actual vs required.
		}

		int lengthOfFields = 0; // warn if length in config is not equal, but allow both sides of wrong as it is reasonable to not care about some and also to truncate shorter on the ECU side.
		for(int i=0;i<fields.length;i++){
			lengthOfFields += fields[i].type.width;
		}

		int payloadLength = ((packet.length - position) - 1);
		if(payloadLength != lengthOfFields){ // First run through to find out what the lengths are.
			System.out.print(" Fields length is: " + lengthOfFields);
			System.out.print(" Packet length is: " + packet.length);
			System.out.println(" Payload length is: " + payloadLength);
			return false;
		}

		for(int i=0;i<fields.length;i++){
			LogField field = fields[i];

			int rawValue = 0;
			for(int j=0;j<field.type.width;j++){
				rawValue = (rawValue * 256) + packet[position];
				position++;
			}

			if((field.type == types.UINT8) || (field.type == types.UINT16) || (field.type == types.UINT32)){
				double scaledValue = (double)rawValue/field.divBy;
				double finalValue = scaledValue + field.addTo;
				decodedLog.addValue(field.ID, finalValue);
			}else if((field.type == types.BITS8) || (field.type == types.BITS16) || (field.type == types.BITS32)){
				int[] processedFlags = processFlagBytes(rawValue, (8 * field.type.width));
				for(int j=0;j<processedFlags.length;j++){
					String flagID = field.bitFieldNames[j] + "-" + field.ID + "-B"+ j;
					decodedLog.addValue(flagID, processedFlags[j]);
				}
			}else if(field.type == types.SINT8){ //??
				decodedLog.addValue(field.ID, rawValue);
			}else if(field.type == types.SINT16){
				decodedLog.addValue(field.ID, rawValue);
			}else if(field.type == types.SINT32){
				decodedLog.addValue(field.ID, rawValue);
			}
		}
		return true; // TODO FIXME : Default to all things being good till I attack this!
	}

	int[] processFlagBytes(long valueOfFlags, int numberOfFlags){
		if((numberOfFlags != 8) && (numberOfFlags != 16) && (numberOfFlags != 32)){
			throw new IllegalArgumentException("Basic units of computer sciene apply, embedded flags are never " + numberOfFlags + " wide!"); // Unless they are 64, but shhhh...
		}

		System.out.println(System.getProperty("line.separator") + "Processing flag values for value: " + valueOfFlags + System.getProperty("line.separator"));
		int[] flagValues = new int[numberOfFlags];
		int comparison = 1;
		for(int i=0;i<numberOfFlags;i++){
			if((valueOfFlags % (2 * comparison)) == comparison){
				flagValues[i] = 1;
				valueOfFlags -= comparison;
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
	private boolean checksum(short[] packet) {
		if(packetLength > 0){
			short includedSum = packet[packetLength -1]; // sum is last byte
			long veryBIGsum = 0;
			for(int x=0;x<packetLength-1;x++){
				veryBIGsum += packet[x];
			}
			short calculatedSum = (short)(veryBIGsum % 256);
			return (calculatedSum == includedSum);
		}else{
			return false;
		}
	}

	/**
	 * Transforms an escape-encoded byte back into what it was before transmission
	 *
	 * @param uByte - byte to be Un-escaped
	 * @return -1 if bad data or the proper value of the escaped byte
	 */
	private short unEscape(short uByte) {
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
	private short unsignedValueOf(byte uInt8){
		return (short)(0xFF & uInt8);
	}

	/**
	 *
	 * @return getGenericLog() returns the reference to the generic log the binary data has been converted to
	 */
	/*public GenericLog getGenericLog() {
		if (logLoaded) {
			return decodedLog;
		} else {
			return null;
		}
	}*/


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
