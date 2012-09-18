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
import org.diyefi.openlogviewer.genericlog.GenericLog;

/**
 *  Typical constructor for this class would look like this <br>
 * <code>
 *  public CSVTypeLog(File f) {<br>
        this.setLogFile(f);<br>
        this.setDecodedLog(new GenericLog());<br>
        this.setT(new Thread(this, "CSV Type Log Loading"));<br>
        this.getT().setPriority(Thread.MAX_PRIORITY);<br>
        this.getT().start();<br>

    }</code>
 * @author Bryan Harris
 */
public abstract class AbstractDecoder implements Runnable {

	/**
	 * logFile is the <code>File</code> object that points to the file you are
	 * attempting to open.
	 */
	private File logFile;

	/**
	 * decodedLog is the outcome of parsing the log file, this will be injected into the program
	 * through property change listeners and this object will be come null when finished or fail.
	 */
	private GenericLog decodedLog;

	/**
	 * this object is threaded so that the gui does not freeze while parsing
	 */
	private Thread t;

	/**
	 * used for getting the decided log for injection to the main pieces of the program that will use it
	 * @return GenericLog
	 */
	public final GenericLog getDecodedLog() {
		return decodedLog;
	}

	/**
	 * sets the GenericLog
	 * @param decodedLog
	 */
	public final void setDecodedLog(final GenericLog decodedLog) {
		this.decodedLog = decodedLog;
	}

	/**
	 * get the log File
	 * @return File
	 */
	public final File getLogFile() {
		return logFile;
	}

	/**
	 * set the log File
	 * @param logFile
	 */
	public final void setLogFile(final File logFile) {
		this.logFile = logFile;
	}

	/**
	 * get the thread, use this if you would like to give the thread a name such as "TYPEOFLOG Thread"<br>
	 * can also be used to set the thread priority
	 * after initialization of all variables required by the extended class you <b>MUST</b> call:<br>
	 * this.getT().start();
	 * @return the thread that this decoder is running in.
	 */
	public final Thread getT() {
		return t;
	}

	/**
	 * set the Thread
	 * @param t
	 */
	public final void setT(final Thread t) {
		this.t = t;
	}
}
