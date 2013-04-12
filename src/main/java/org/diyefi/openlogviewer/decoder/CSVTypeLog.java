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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ResourceBundle;
import java.util.Scanner;

import org.diyefi.openlogviewer.OpenLogViewer;
import org.diyefi.openlogviewer.genericlog.GenericLog;
import org.diyefi.openlogviewer.utils.Utilities;
import org.diyefi.openlogviewer.Text;

public class CSVTypeLog extends AbstractDecoder {
	private static final int LOAD_FACTOR = 2;
	private static final String[] DELIMITERS = {"\t", ",", ":", "/", "\\\\", ";"};
	private final ResourceBundle labels;
	private int fieldCount = -1;

	/**
	 * @param f The CSV file to examine and attempt to load.
	 */
	public CSVTypeLog(final File f, final ResourceBundle labels) {
		this.labels = labels;
		this.setLogFile(f);
		//this.setT(new Thread(this, CSVTypeLog.class.getSimpleName()));
		//this.getT().setPriority(Thread.MAX_PRIORITY);
		//this.getT().start();
	}

	@Override
	public final void run() {
		try {
			final long startTime = System.currentTimeMillis();
                        
			decodeLog();
			OpenLogViewer.getInstance().getEntireGraphingPanel().setGraphSize(this.getDecodedLog().getRecordCount());
			this.getDecodedLog().setLogStatus(GenericLog.LogState.LOG_LOADED);
			System.out.println(labels.getString(Text.CSV_LOADED_MSG_PART1)
					+ (this.getDecodedLog().getRecordCount() + 1) + labels.getString(Text.CSV_LOADED_MSG_PART2)
					+ (System.currentTimeMillis() - startTime) + labels.getString(Text.CSV_LOADED_MSG_PART3));
		} catch (IOException e) {
			e.printStackTrace();
			if (this.getDecodedLog() != null) {
				this.getDecodedLog().setLogStatusMessage(e.getMessage());
			} else { // TODO create a new log object and set the status...
				System.out.println("FIXME!!! CSVTypeLog");
			}
		}
	}

	/**
	 * Decodes a CSV type of text file,
	 * the first ten lines are parsed individually to detect the delimiter type
	 *
	 * accepted types of delimiters are TAB, comma, ; , : and \
	 * this decoder does not yet support markers, it will skip them
	 *
	 * @throws IOException
	 */
	protected final void decodeLog() throws IOException {
		final Scanner scan = new Scanner(new BufferedReader(new FileReader(getLogFile())));
		final String delimiter = scanForDelimiter();

		String[] splitLine;
		String[] headers = new String[1];

		int finalAndInitialLength = Utilities.countBytes(getLogFile(), (byte) '\n');

		String line = "";
		boolean headerSet = false;
		while (scan.hasNextLine() && !headerSet) {
			line = scan.nextLine();
			splitLine = line.split(delimiter);

			if (splitLine.length == fieldCount) {
				headers = splitLine;
				this.setDecodedLog(new GenericLog(splitLine, finalAndInitialLength, LOAD_FACTOR, labels));
				headerSet = true;
			}
		}
                this.getDecodedLog().setLogStatus(GenericLog.LogState.LOG_LOADING);
		while (scan.hasNextLine()) {
			line = scan.nextLine();
			splitLine = line.split(delimiter);
			this.getDecodedLog().incrementPosition();
                        this.decoderLineOfChanged(getDecodedLog().getRecordCount(), finalAndInitialLength);
                        this.decoderProgressChanged((int)(getDecodedLog().getRecordCount()/(float)finalAndInitialLength*100));
			if (splitLine.length == fieldCount) {
				for (int x = 0; x < splitLine.length; x++) { // not reasonable to use foreach loop here
                                    if(splitLine[x].length() > 0)
                                    {
                                        try{
                                            this.getDecodedLog().addValue(headers[x], Double.parseDouble(splitLine[x]));
                                        }
                                        catch (Exception e)
                                        {
                                            this.getDecodedLog().addValue(headers[x], 0.0);
                                        }
                                    }
                                    else
                                    {
                                        this.getDecodedLog().addValue(headers[x], 0.0);
                                    }
                                }
                           // }
			}
		}
	}

	/**
	 * detects the delimiter and if it finds a suitable delimiter it will return it.
	 * @return delimiter in string format
	 * @throws IOException
	 */
	private String scanForDelimiter() throws IOException {
		final FileReader reader = new FileReader(getLogFile());
		final Scanner scan = new Scanner(reader);

		final int checkLines = 10; // How many lines to check to find count
		String delimiterFind = "";
		String[] split;
		int index = -1;

		for (int i = 0; i < checkLines && scan.hasNext(); i++) {
			delimiterFind = scan.nextLine();
			for (int j = 0; j < DELIMITERS.length; j++) {
				split = delimiterFind.split(DELIMITERS[j]);
				if (split.length > fieldCount) {
					fieldCount = split.length;
					index = j;
				}
			}
		}

		scan.close();
		reader.close();
		return DELIMITERS[index];
	}
}
