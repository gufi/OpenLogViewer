/* OpenLogViewer
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
package org.diyefi.openlogviewer.genericlog;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashMap;
import org.diyefi.openlogviewer.OpenLogViewerApp;

@SuppressWarnings("serial")
public class GenericLog extends HashMap<String, GenericDataElement> {

	// Stuff for showing a line count in all files loaded, if we ever do exports, either don't include this, and/or check for it before doing this
	private final String lineKey = "OLV Line Count";
	private String firstKey;
	private int lineCount = 0;
	private GenericDataElement lineCountElement;

	final public static int LOG_NOT_LOADED = -1;
	final public static int LOG_LOADING = 0;
	final public static int LOG_LOADED = 1;

	private String metaData;
	protected final PropertyChangeSupport PCS;
	private int logStatus;

	private final PropertyChangeListener autoLoad = new PropertyChangeListener() {
		public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
			if ((Integer) propertyChangeEvent.getNewValue() == 0) {
				GenericLog genLog = (GenericLog) propertyChangeEvent.getSource();
				genLog.setLogStatus(GenericLog.LOG_LOADING);
				OpenLogViewerApp.getInstance().setLog(genLog);
			} else if ((Integer) propertyChangeEvent.getNewValue() == 1) {
				GenericLog genLog = (GenericLog) propertyChangeEvent.getSource();
				genLog.setLogStatus(GenericLog.LOG_LOADED);
				OpenLogViewerApp.getInstance().setLog(genLog);
				OpenLogViewerApp.getInstance().getOptionFrame().updateFromLog(genLog);
			}
		}
	};

	public GenericLog() {
		super();
		logStatus = LOG_NOT_LOADED;
		PCS = new PropertyChangeSupport(this);
		addPropertyChangeListener("LogLoaded", autoLoad);
		metaData = "";
	}

	/**
	 * provide a <code>String</code> array of headers<br>
	 * each header will be used as a HashMap key, the data related to each header will be added to an <code>ArrayList</code>.
	 * @param headers - of the data to be converted
	 */
	public GenericLog(String[] headers) {
		super();

		logStatus = LOG_NOT_LOADED;
		PCS = new PropertyChangeSupport(this);
		addPropertyChangeListener("LogLoaded", autoLoad);

		metaData = "";

		// A bit dirty, but not too bad.
		String[] internalHeaders = new String[headers.length + 1];
		for(int i=0;i<headers.length;i++){
			internalHeaders[i] = headers[i];
		}

		internalHeaders[headers.length] = lineKey;
		this.setHeaders(internalHeaders);
		lineCountElement = this.get(lineKey);
		firstKey = headers[0];
	}

	/**
	 * Add a piece of data to the <code>ArrayList</code> associated with the <code>key</code>
	 * @param key - header
	 * @param value - data to be added
	 * @return true or false if it was successfully added
	 */
	public boolean addValue(String key, double value) {
		GenericDataElement logElement = this.get(key);
		if(key.equals(firstKey)){
			lineCountElement.add((double)lineCount);
			lineCount++;
		}
		return logElement.add(value);
	}

	/**
	 * Set the state of the log
	 * @param newLogStatus GenericLog.LOG_NOT_LOADED / GenericLog.LOG_LOADING / GenericLog.LOG_LOADED
	 */
	public void setLogStatus(int newLogStatus) {
		int oldLogStatus = this.logStatus;
		this.logStatus = newLogStatus;
		PCS.firePropertyChange("LogLoaded", oldLogStatus, newLogStatus);
	}

	/**
	 *
	 * @return -1 if log not loaded 0 if loading or 1 if log is loaded
	 */
	public int getLogStatus() {
		return this.logStatus;
	}

	/**
	 * sets the names of the headers for the Comparable interface of GenericDataElement
	 * @param headers
	 */
	public void setHeaders(String[] headers) {
		for (int x = 0; x < headers.length; x++) {
			GenericDataElement GDE = new GenericDataElement();
			GDE.setName(headers[x]);
			this.put(headers[x], GDE);
		}
	}

	/**
	 * Add metadata This is information about the log being converted such as the location it was from or the date<br>
	 * This method does not add to its self so in order to add more info you must VAR.addMetaData(VAR.getMetaData() + NEWINFO)
	 * @param md meta data to be added
	 */
	public void setMetaData(String md) {
		metaData = md;
	}

	/**
	 *
	 * @return String containing the current meta data
	 */
	public String getMetadata() {
		return metaData;
	}

	/**
	 * Add a property change listener to the generic log, REQUIRED!!
	 * GenericLog.LOG_STATUS is the name of the status property
	 * @param name
	 * @param listener
	 * <code>new OBJECT.addPropertyChangeListener("LogLoaded", new PropertyChangeListener() {<br>
	 *       public void propertyChange( final PropertyChangeEvent propertyChangeEvent) {<br>
	 *           OpenLogViewerApp.getInstance().setLog((GenericLog) propertyChangeEvent.getSource());
	 *           ...Insert code here...<br>
	 *
	 *       }<br>
	 *   });</code>
	 */
	public void addPropertyChangeListener(final String name, final PropertyChangeListener listener) {
		PCS.addPropertyChangeListener(name, listener);
	}

	/**
	 * Remove a PropertyChangeListener
	 * @param propertyName name of listener
	 * @param listener listener
	 */
	public void removePropertyChangeListener(final String propertyName, final PropertyChangeListener listener) {
		PCS.removePropertyChangeListener(propertyName, listener);
	}
}
