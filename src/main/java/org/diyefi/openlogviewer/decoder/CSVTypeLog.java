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
package main.java.org.diyefi.openlogviewer.decoder;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
import main.java.org.diyefi.openlogviewer.genericlog.GenericLog;

/**
 *
 * @author Bryan
 */
public class CSVTypeLog extends BaseDecoder {

    public CSVTypeLog(String path) {

        this(new File(path));

    }

    public CSVTypeLog(File f) {
        this.setLogFile(f);
        this.setDecodedLog(new GenericLog());
        this.setT(new Thread(this, "CVS Type Log Loading"));
        this.getT().setPriority(Thread.MAX_PRIORITY);
        this.getT().start();

    }

    @Override
    protected void decodeLog() throws IOException {
        Scanner scan = new Scanner(new FileReader(getLogFile()));

        String line = "";
        String delimiter = "";
        String[] splitLine = new String[1];
        String[] headers = new String[1];
        delimiter = scanForDelimiter();

        boolean headerSet = false;
        while (scan.hasNextLine() && !headerSet) {
            line = scan.nextLine();
            splitLine = line.split(delimiter);
            if (splitLine.length == fieldCount) {
                headers = splitLine;
                this.getDecodedLog().setHeaders(splitLine);
                headerSet = true;
            }
        }
        while (scan.hasNextLine()) {
            line = scan.nextLine();

            splitLine = line.split(delimiter);
            if (splitLine.length == fieldCount) {
                for (int x = 0; x < splitLine.length; x++) {
                    this.getDecodedLog().addValue(headers[x], Double.parseDouble(splitLine[x]));
                }
            }
        }

    }
    int fieldCount = -1;

    private String scanForDelimiter() throws IOException {
        String delim[] = {"\t", ",", ":", "/", "\\\\"};
        Scanner scan = new Scanner(new FileReader(getLogFile()));
        String delimiterFind = "";
        String[] split = new String[1];
        int delimNum = -1;
        for (int i = 0; i < 10 && scan.hasNext(); i++) {
            delimiterFind = scan.nextLine();
            for (int j = 0; j < delim.length; j++) {
                split = delimiterFind.split(delim[j]);
                if (split.length > fieldCount) {
                    fieldCount = split.length;
                    delimNum = j;
                }
            }
        }
        scan.close();
        return delim[delimNum];
    }
}
