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
import java.io.IOException;
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
 * @version 0.1.3
 */
public abstract class BaseDecoder implements Runnable {

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
	 * Overriden Run from the Runnable Interface to do the work for us in a threaded fashion.
	 */
	@Override
	public void run() {
		try {
			this.getDecodedLog().setLogStatus(GenericLog.LOG_LOADING);
			decodeLog();
			this.getDecodedLog().setLogStatus(GenericLog.LOG_LOADED);
		} catch (IOException IOE) {
			this.getDecodedLog().setLogStatus(GenericLog.LOG_NOT_LOADED);
			System.out.println("Error Loading Log: " +IOE.getMessage());
		}
	}

	/**
	 * BaseDecoder.decodeLog() is an abstract method. Override this method write your parsing code within it, when creating an object that
	 * extends BaseDecoder the rest will be taken care of automatically
	 * @throws IOException
	 */
	abstract void decodeLog() throws IOException;

	/**
	 * used for getting the decided log for injection to the main pieces of the program that will use it
	 * @return GenericLog
	 */
	public GenericLog getDecodedLog() {
		return decodedLog;
	}

	/**
	 * sets the GenericLog
	 * @param decodedLog
	 */
	public void setDecodedLog(GenericLog decodedLog) {
		this.decodedLog = decodedLog;
	}

	/**
	 * get the log File
	 * @return File
	 */
	public File getLogFile() {
		return logFile;
	}

	/**
	 * set the log File
	 * @param logFile
	 */
	public void setLogFile(File logFile) {
		this.logFile = logFile;
	}

	/**
	 * get the thread, use this if you would like to give the thread a name such as "TYPEOFLOG Thread"<br>
	 * can also be used to set the thread priority
	 * after initialization of all variables required by the extended class you <b>MUST</b> call:<br>
	 * this.getT().start();
	 * @return the thread that this decoder is running in.
	 */
	public Thread getT() {
		return t;
	}

	/**
	 * set the Thread
	 * @param t
	 */
	public void setT(Thread t) {
		this.t = t;
	}
}
