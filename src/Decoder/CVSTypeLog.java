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
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

/**
 *
 * @author Bryan
 */
public class CVSTypeLog extends BaseDecoder {

    public CVSTypeLog(String path) {

        this(new File(path));

    }

    public CVSTypeLog(File f) {
        this.setLogFile(f);
        this.setDecodedLog(new GenericLog());
        this.setT(new Thread(this, "CVS Type Log Loading"));
        this.getT().setPriority(Thread.MAX_PRIORITY);
        this.getT().start();

    }




    protected void decodeLog() throws IOException {
        Scanner scan = new Scanner(new FileReader(getLogFile()));

            String line = "";
            String delimiter = "";
            String[] splitLine = new String[1];
            String[] headers = new String[1];
            if (scan.hasNextLine()) {
                scan.nextLine(); // jump past first line as its just pointless shit like "/Null" or the ecu revision
                
            }
            while (scan.hasNextLine()) {
                line = scan.nextLine();
                
                if (delimiter.isEmpty()) {
                    delimiter = scanForDelimiter(line);
                }
                splitLine = line.split(delimiter);
                if (splitLine[0].matches("[a-zA-Z]*") ) {
                    this.getDecodedLog().setHeaders(splitLine);
                    headers = splitLine;
                } else {
                    for (int x = 0; x < splitLine.length; x++) {
                        this.getDecodedLog().addValue(headers[x], Double.parseDouble(splitLine[x]));
                    }
                }

            }

    }

    private String scanForDelimiter(String line) {
        if (line.contains("\t")) {
            return "\t";
        } else if (line.contains(",")) {
            return ",";
        } else if (line.contains(":")) {
            return ":";
        } else if (line.contains("/")) {
            return "/";
        } else if (line.contains("\\")) {
            return "\\";
        }
        return "\t";
    }
}
