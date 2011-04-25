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
 *
 * @author Bryan
 */
public class FreeEMSBin implements Runnable { // implements runnable to make this class theadable
//public class FreeEMSBin extends Thread {

    private final short ESCAPE_BYTE = 0xBB;// for unsigned byte
    private final short START_BYTE = 0xAA;// for unsigned byte
    private final short STOP_BYTE = 0xCC;// for unsigned byte
    private final short ESCAPED_ESCAPE_BYTE = 0x44;// for unsigned byte
    private final short ESCAPED_START_BYTE = 0x55;// for unsigned byte
    private final short ESCAPED_STOP_BYTE = 0x33;// for unsigned byte
    private short[] wholePacket;// for unsigned byte
    private File logFile;
    private FileInputStream logStream;
    private boolean startFound;
    private GenericLog decodedLog;
    int packetLength;//track packet length
    private Thread t;
    String[] headers = {
        "IAT", "CHT", "TPS", "EGO", "MAP", "AAP", "BRV", "MAT", "EGO2", "IAP", "MAF", "DMAP", "DTPS", "RPM", "DRPM", "DDRPM",
        "LMain", "VEM", "Lambda", "AirFlo", "DensFu", "BasePW", "ETE", "TFCTot", "EffPW", "IDT", "RefPW", 
    	"Advance",
    	"Dwell",
    	"zsp19",
    	"zsp18",
    	"zsp17",
    	"zsp16",
    	"zsp15",
    	"zsp14",
    	"zsp13",
    	"zsp12",
    	"zsp11",
    	"zsp10",
    	"zsp9",
    	"zsp8",
    	"zsp7",
    	"zsp6",
    	"zsp5",
    	"zsp4",
    	"zsp3",
    	"zsp2",
    	"zsp1",
    	"coreSA0",
    	"coreSA1",
    	"coreSA2",
    	"coreSA3",
    	"coreSA4",
    	"coreSA5",
    	"coreSA6",
    	"coreSA7",
    	"decFlgs0",
    	"decFlgs1",
    	"decFlgs2",
    	"decFlgs3",
    	"decFlgs4",
    	"decFlgs5",
    	"decFlgs6",
    	"decFlgs7"
    }; //This needs to be converted to resourses or gathered externally at some point
    private double[] conversionFactor = { // no value in this shall == 0, you cannot divide by 0 ( divide by 1 if you need raw value )

        // CORE VARS
        100.0, // IAT
        100.0, // CHT
        640.0, // TPS
        32768.0, // EGO %
        100.0, // MAP
        100.0, // AAP
        1000.0, // Battery Voltage
        100.0, // MAT
        32768.0, // EGO2
        100.0, // IAP
        1.0, // MAF
        1.0, // DMAP
        1.0, // DTPS
        2.0, // RPM
        1.0, // Delta RPM
        1.0, // Delta Delta RPM

        // DERIVED VARS
        1.0, // Load Main
        512.0, // VEMain
        32768.0, // Lambda
        1.0, // Airflow
        1.0, // Density and Fuel
        1250.0, // Base PW
        1.0, // ETE
        1.0, // TFCTotal If math for this is incorrect its because i'm currently using a short instead of signed short to hold the value, special case var
        1.0, // Effective PW
        1.0, // IDT
        1.0, // RefPW
        1.0, // Advance
        1250.0, // Dwell
        
        // Spares
        1.0, // SP?
        1.0, // SP?
        1.0, // SP?
        1.0, // SP?
        1.0, // SP?
        1.0, // SP?
        1.0, // SP?
        1.0, // SP?
        1.0, // SP?
        1.0, // SP?
        1.0, // SP?
        1.0, // SP?
        1.0, // SP?
        1.0, // SP?
        1.0, // SP?
        1.0, // SP?
        1.0, // SP?
        1.0, // SP?
        1.0, // SP?
        1.0, // SP?
        1.0, // SP?
        1.0, // SP?
        1.0, // SP?
        1.0, // SP?
        1.0, // SP?
        1.0, // SP?
        1.0, // SP?
        1.0, // SP?
        1.0, // SP?
        1.0, // SP?
        1.0, // SP?
        1.0, // SP?
        1.0, // SP?
        1.0, // SP?
        1.0  // SP?
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
        wholePacket = new short[6000];
        packetLength = 0;
        decodedLog = new GenericLog(headers);

        t = new Thread(this, "FreeEMSBin Loading");
        t.setPriority(Thread.MAX_PRIORITY);
        t.start();
    }

    

    /**
     * DecodeLog will use the current <code>logFile</code> parse through it and when required pass the job <br>
     * to the required method of this class such as decodePacket or checksum.
     */
    //public void decodeLog() {
    @Override
    public void run() {
        try {
            // file setup
            byte[] readByte = new byte[1];
            short uByte = 0;// uByte stands for UNSIGNED BYTE

            startFound = false;
            logStream = new FileInputStream(logFile);
            decodedLog.setLogStatus(0);
            while (logStream.read(readByte) != -1) {
                uByte = (short) (readByte[0] & 0xff); // mask the byte in case something screwey happens
                if (uByte == START_BYTE) {
                    if (!startFound) {
                        startFound = true;
                    } else {
                        //TO-DO: find something to put here
                    }
                } else if (startFound) { // do NOTHING untill a start is found

                    if (uByte == STOP_BYTE) {
                        if (checksum(wholePacket)) {
                        	decodeBasicLogPacket(wholePacket);
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
                //else-> No else because if the start byte or start found conditions
                // were not met then ignore data untill start is found due to a packet being bad
            }
            decodedLog.setLogStatus(1);
            

        } catch (IOException IOE) {
            System.out.println(IOE.getMessage());
            //TO-DO: Add code to handle or warn of the error
            
        } 

    }

    /**
     * This method decodes a packet by splitting up the data into larger data types to keep the unsigned info <br>
     * This method could probably use a little work
     * @param packet is a <code>short</code> array containing 1 full packet
     *
     */
    private void decodeBasicLogPacket(short[] packet) {
        // int flags = (int)( packet[0] & 0xff); // used for parsing packets, need to find this info
        int payLoadId = (int) (((packet[1] & 0xff) * 256) + (packet[2] & 0xff));
        //int seq = (int) packet[3]; // unused
        int size = 0; // unused if not a 401 payLoadId

        int x = 0;
        int leadingBytes = 0;
        // set size according to payload
        if (payLoadId == 401) {
            size = (int) (((packet[3] & 0xff) * 256) + (packet[4] & 0xff));
            leadingBytes = 5;
        } else {
            leadingBytes = 5; // size is not included for non 401 payload id's
        }
        int offset = packetLength - size;
        while (x < packetLength - offset) {
            if (payLoadId == 401) {
                if (x < size) {
                	if((x/2) < headers.length){
	                    double d = 0;
	                    if (headers[x / 2].equalsIgnoreCase("TFCTot")) {
	                        d = ((short) (packet[x + leadingBytes] * 256) + packet[x + leadingBytes + 1]) / conversionFactor[x / 2];// special case signed short
	                    } else {
	                        d = (int) ((packet[x + leadingBytes] * 256) + packet[x + leadingBytes + 1]) / conversionFactor[x / 2];// unsigned shorts
	                        // Hack for bit flags
	                        if(headers[x / 2].equalsIgnoreCase("zsp4")){
		                        int coreSA0 = 0;
		                        int coreSA1 = 0;
		                        int coreSA2 = 0;
		                        int coreSA3 = 0;
		                        int coreSA4 = 0;
		                        int coreSA5 = 0;
		                        int coreSA6 = 0;
		                        int coreSA7 = 0;
		                        int decFlgs0 = 0;
		                        int decFlgs1 = 0;
		                        int decFlgs2 = 0;
		                        int decFlgs3 = 0;
		                        int decFlgs4 = 0;
		                        int decFlgs5 = 0;
		                        int decFlgs6 = 0;
		                        int decFlgs7 = 0;
	                            if((d % 2) == 1){
	                            	coreSA0 = 1;
	                            	d -= 1;
	                            }
	                            if((d % 4) == 2){
	                            	coreSA1 = 1;
	                            	d -= 2;
	                            }
	                            if((d % 8) == 4){
	                            	coreSA2 = 1;
	                            	d -= 4;
	                            }
	                            if((d % 16) == 8){
	                            	coreSA3 = 1;
	                            	d -= 8;
	                            }
	                            if((d % 32) == 16){
	                            	coreSA4 = 1;
	                            	d -= 16;
	                            }
	                            if((d % 64) == 32){
	                            	coreSA5 = 1;
	                            	d -= 32;
	                            }
	                            if((d % 128) == 64){
	                            	coreSA6 = 1;
	                            	d -= 64;
	                            }
	                            if((d % 256) == 128){
	                            	coreSA7 = 1;
	                            	d -= 128;
	                            }
	                            if((d % 512) == 256){
	                            	coreSA0 = 1;
	                            	d -= 256;
	                            }
	                            if((d % 1024) == 512){
	                            	decFlgs1 = 1;
	                            	d -= 512;
	                            }
	                            if((d % 2048) == 1024){
	                            	decFlgs2 = 1;
	                            	d -= 1024;
	                            }
	                            if((d % 4096) == 2048){
	                            	decFlgs3 = 1;
	                            	d -= 2048;
	                            }
	                            if((d % 8192) == 4096){
	                            	decFlgs4 = 1;
	                            	d -= 4096;
	                            }
	                            if((d % 16384) == 8192){
	                            	decFlgs5 = 1;
	                            	d -= 8192;
	                            }
	                            if((d % 32768) == 16384){
	                            	decFlgs6 = 1;
	                            	d -= 16384;
	                            }
	                            if(d == 32768){
	                            	decFlgs7 = 1;
	                            }

	                        	decodedLog.addValue("coreSA0", coreSA0);
	                        	decodedLog.addValue("coreSA1", coreSA1);
	                        	decodedLog.addValue("coreSA2", coreSA2);
	                        	decodedLog.addValue("coreSA3", coreSA3);
	                        	decodedLog.addValue("coreSA4", coreSA4);
	                        	decodedLog.addValue("coreSA5", coreSA5);
	                        	decodedLog.addValue("coreSA6", coreSA6);
	                        	decodedLog.addValue("coreSA7", coreSA7);
	                        	decodedLog.addValue("decFlgs0", decFlgs0);
	                        	decodedLog.addValue("decFlgs1", decFlgs1);
	                        	decodedLog.addValue("decFlgs2", decFlgs2);
	                        	decodedLog.addValue("decFlgs3", decFlgs3);
	                        	decodedLog.addValue("decFlgs4", decFlgs4);
	                        	decodedLog.addValue("decFlgs5", decFlgs5);
	                        	decodedLog.addValue("decFlgs6", decFlgs6);
	                        	decodedLog.addValue("decFlgs7", decFlgs7);
	                        }
	                    }
                    	decodedLog.addValue(headers[x / 2], d); // unsigned shorts
                    }
                    x += 2;
                } else {
                    x++;
                }
            } else {
                x++; // rare cases where the packet gets ignored
            }
        }
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
