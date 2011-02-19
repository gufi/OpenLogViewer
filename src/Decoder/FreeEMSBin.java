/* DataReader
 *
 * Copyright 2011
 *
 * This file is part of the DataReader project.
 *
 * DataReader software is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * DataReader software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with any DataReader software.  If not, see http://www.gnu.org/licenses/
 *
 * I ask that if you make any changes to this file you fork the code on github.com!
 *
 */
package Decoder;

import GenericLog.GenericLog;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

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
    private final int CHECKSUM_VAL = 256;
    private short[] wholePacket;// for unsigned byte
    private File logFile;
    private FileInputStream logStream;
    private boolean startFound;
    private GenericLog decodedLog;
    int packetLength;//track packet length
    private Thread t;
    String[] headers = {
        "IAT", "CHT", "TPS", "EGO", "MAP", "AAP", "BRV", "MAT", "EGO2", "IAP", "MAF", "DMAP", "DTPS", "RPM", "DRPM", "DDRPM",
        "LMain", "VEM", "Lambda", "AirFlo", "DensFu", "BasePW", "ETE", "TFCTot", "EffPW", "IDT", "RefPW", "SP1", "SP2", "SP3", "SP4", "SP5",
        "IAT V", "CHT V", "TPS V", "EGO V", "MAP V", "AAP V", "BRV V", "MAT V", "EGO2 v", "IAP V", "MAF V", "ADC3", "ADC4", "ADC5", "ADC6", "ADC7"
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
        1.0, //DTPS
        2.0, //RPM
        2.0, // Delta RPM
        2.0, // Delta Delta RPM

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
        1.0, // SP1
        1.0, // SP2
        1.0, // SP3
        1.0, // SP4
        1.0, // SP5

        //ADC ARRAY

        204.8, // IAT V
        204.8, // CHT V
        204.8, // TPS V
        204.8, // EGO V
        204.8, // MAP V
        204.8, // AAP V
        204.8, // BRV V
        204.8, // MAT V
        204.8, // EGO2 V
        204.8, // IAP V
        204.8, // MAF V
        204.8, // ADC3 V
        204.8, // ADC4 V
        204.8, // ADC5 V
        204.8, // ADC6 V
        204.8 // ADC7 V
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
        wholePacket = new short[3000];
        packetLength = 0;
        decodedLog = new GenericLog(headers);

        t = new Thread(this, "FreeEMSBin Loading");
        t.setPriority(Thread.MAX_PRIORITY);
        t.start();
        // decodeLog();

    }

    

    /**
     * DecodeLog will use the current <code>logFile</code> parse through it and when required pass the job <br>
     * to the required method of this class such as decodePacket or checksum.
     */
    //public void decodeLog() {
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
                            decodePacket(wholePacket);
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
     * This method decodes a packet by splitting up the data into larger datatypes to keep the unsigned info <br>
     * This method could probably use a litle work
     * @param packet is a <code>short</code> array containing 1 full packet
     *
     */
    private void decodePacket(short[] packet) {
        // int flags = (int)( packet[0] & 0xff); // used for parsing packets, need to find this info
        int payLoadId = (int) (((packet[1] & 0xff) * 256) + (packet[2] & 0xff));
        //int seq = (int) packet[3]; // unused
        int size = 0; // unused if not a 401 payLoadId

        int x = 0;
        int leadingBytes = 0;
        // set size according to payload
        if (payLoadId == 401) {
            size = (int) (((packet[4] & 0xff) * 256) + (packet[5] & 0xff));
            leadingBytes = 6;
        } else {
            leadingBytes = 5; // size is not included for non 401 payload id's
        }


        int offset = packetLength - size;

        while (x < packetLength - offset) {
            if (payLoadId == 401) {
                if (x < size) {
                    double d = 0;
                    if (headers[x / 2].equalsIgnoreCase("TFCTot")) {
                        d = ((short) (packet[x + leadingBytes] * 256) + packet[x + leadingBytes + 1]) / conversionFactor[x / 2];// special case signed short
                    } else {
                        d = (int) ((packet[x + leadingBytes] * 256) + packet[x + leadingBytes + 1]) / conversionFactor[x / 2];// unsigned shorts
                    }
                    decodedLog.addValue(headers[x / 2], d); // unsigned shorts
                    x = x + 2;
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
        if (packetLength > 0) {
            int payLoadId = (int) ((packet[1] * 256) + packet[2]);
            short checksum = (short) (packet[packetLength - 1]);
            int size = 0;
            byte sum = 0;
            //checksum
            if (payLoadId == 401) {
                int x = 6; // start at index 6
                size = (int) ((packet[4] * 256) + packet[5]);
                int offset = packetLength - size;
                while (x < packetLength - offset - 2) {

                    sum += packet[x];
                    x++;
                }
            } else {
                return true; // only checksums for payloadid's of 401
            }
            if ((short) (((short) (sum & 0xff) - checksum) & 0xff) == 188) { /// im sure this could be done better

                return true;
            } else {
                return false;
            }
        }
        return false;
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
