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
import java.util.Scanner;

import org.diyefi.openlogviewer.OpenLogViewer;
import org.diyefi.openlogviewer.genericlog.GenericLog;

public class CSVTypeLog extends AbstractDecoder {
	private static final int initialLength = 75000;
	private static final int loadFactor = 2;
	private int fieldCount = -1;

	/**
	 * This constructor is called when a string path is provided
	 * @param f
	 */
	public CSVTypeLog(final File f) {
		this.setLogFile(f);
		this.setT(new Thread(this, "CSV Type Log Loading"));
		this.getT().setPriority(Thread.MAX_PRIORITY);
		this.getT().start();
	}

	@Override
	public final void run() {
		try {
			final long startTime = System.currentTimeMillis();
			decodeLog();
			OpenLogViewer.getInstance().getEntireGraphingPanel().setGraphPositionMax(this.getDecodedLog().getRecordCount());
			this.getDecodedLog().setLogStatus(GenericLog.LOG_LOADED);
			System.out.println("Loaded " + (this.getDecodedLog().getRecordCount() + 1) + " records in " + (System.currentTimeMillis() - startTime) + " millis!");
		} catch (Exception e) {
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

		String[] splitLine = new String[1];
		String[] headers = new String[1];

		String line = "";
		boolean headerSet = false;
		while (scan.hasNextLine() && !headerSet) {
			line = scan.nextLine();
			splitLine = line.split(delimiter);

			if (splitLine.length == fieldCount) {
				headers = splitLine;
				this.setDecodedLog(new GenericLog(splitLine, initialLength, loadFactor));
				headerSet = true;
			}
		}

		while (scan.hasNextLine()) {
			line = scan.nextLine();
			splitLine = line.split(delimiter);
			this.getDecodedLog().incrementPosition();
			if (splitLine.length == fieldCount) {
				for (int x = 0; x < splitLine.length; x++) {
					this.getDecodedLog().addValue(headers[x], Double.parseDouble(splitLine[x]));
				}
			}
		}
	}

	/**
	 * detects the delimiter and if it finds a suitable delimiter it will return it.
	 * @return delimiter in string format
	 * @throws IOException
	 */
	private String scanForDelimiter() throws IOException {
		final String[] delim = {"\t", ",", ":", "/", "\\\\"};
		final FileReader reader = new FileReader(getLogFile());
		final Scanner scan = new Scanner(reader);

		final int magicNumber = 10; // Remove the MAGIC!
		String delimiterFind = "";
		String[] split = new String[1];
		int delimNum = -1;

		for (int i = 0; i < magicNumber && scan.hasNext(); i++) {
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
		reader.close();
		return delim[delimNum];
	}
}
