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
import java.util.LinkedHashMap;
import java.util.Iterator;
import org.diyefi.openlogviewer.OpenLogViewer;

public class GenericLog extends LinkedHashMap<String, GenericDataElement> {
	private static final long serialVersionUID = 1L;

	// TODO this is no good, get rid of it, show some sort of status indicator in the GUI showing that log loading is not complete
	// For streams, always show that as a way of saying "still streaming!"
	private static final String LOG_LOADED_TEXT = "LogLoaded";
	public static enum LogState { LOG_NOT_LOADED, LOG_LOADING, LOG_LOADED }

	// Info to populate built-in fields efficiently, likely to be done differently in future, but if not, put this in some structure.
	private static final int NUMBER_OF_BUILTIN_FIELDS = 3; // See below:
	private static final int RECORD_COUNT_OFFSET = 0;
	private static final int TEMP_RESET_OFFSET   = 1;
	private static final int ELAPSED_TIME_OFFSET = 2;
	private static final int SIZE_OF_DOUBLE = 8;
	private static final int NUMBER_OF_BYTES_IN_A_MEG = 1000000;
	public static final String RECORD_COUNT_KEY = "OLV Record Count";
	public static final String tempResetKey = "OLV Temp Resets";
	public static final String elapsedTimeKey = "OLV Elapsed Time";
	private GenericDataElement recordCountElement;

	private String metaData;
	private final PropertyChangeSupport pcs;
	private LogState logStatus;
	private String logStatusMessage;

	// Track the size of our children so that we can bump them up one by one where required
	private int currentCapacity;
	private int currentPosition = -1;
	// ^ TODO if we end up limiting memory usage by some configurable amount and recycling positions, for live streaming, then add count
	private int ourLoadFactor;
	private int numberOfInternalHeaders;

	private final PropertyChangeListener autoLoad = new PropertyChangeListener() {
		public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
			if ((LogState) propertyChangeEvent.getNewValue() == LogState.LOG_LOADING) {
				final GenericLog genLog = (GenericLog) propertyChangeEvent.getSource();
				genLog.setLogStatus(LogState.LOG_LOADING);
				OpenLogViewer.getInstance().setLog(genLog);
			} else if ((LogState) propertyChangeEvent.getNewValue() == LogState.LOG_LOADED) {
				final GenericLog genLog = (GenericLog) propertyChangeEvent.getSource();
				genLog.setLogStatus(LogState.LOG_LOADED);
				OpenLogViewer.getInstance().setLog(genLog);
				OpenLogViewer.getInstance().getOptionFrame().updateFromLog(genLog);
			}
		}
	};

	/**
	 * provide a <code>String</code> array of headers<br>
	 * each header will be used as a HashMap key, the data related to each header will be added to an <code>ArrayList</code>.
	 * @param headers - of the data to be converted
	 */
	public GenericLog(final String[] headers, final int initialCapacity, final int ourLoadFactor) {
		super(1 + (headers.length + NUMBER_OF_BUILTIN_FIELDS), 1.0f); // refactor to use (capacityRequired+1, 1.0) for maximum performance (no rehashing)

		GenericDataElement.resetPosition(); // Kinda ugly, but...
		logStatus = LogState.LOG_NOT_LOADED;
		pcs = new PropertyChangeSupport(this);
		addPropertyChangeListener(LOG_LOADED_TEXT, autoLoad);
		metaData = "";

		this.ourLoadFactor = ourLoadFactor;
		currentCapacity = initialCapacity;

		// A bit dirty, but not too bad.
		numberOfInternalHeaders = headers.length + NUMBER_OF_BUILTIN_FIELDS;
		final String[] internalHeaders = new String[numberOfInternalHeaders];
		for (int i = 0; i < headers.length; i++) {
			internalHeaders[i] = headers[i];
		}

		// If this stays like this, move it to a structure and small loop...
		internalHeaders[headers.length + RECORD_COUNT_OFFSET] = RECORD_COUNT_KEY;
		internalHeaders[headers.length + TEMP_RESET_OFFSET] = tempResetKey;
		internalHeaders[headers.length + ELAPSED_TIME_OFFSET] = elapsedTimeKey;

		for (int x = 0; x < internalHeaders.length; x++) {
			final GenericDataElement gde = new GenericDataElement(initialCapacity);
			gde.setName(internalHeaders[x]);
			this.put(internalHeaders[x], gde);
		}

		recordCountElement = this.get(RECORD_COUNT_KEY);
	}

	/**
	 * Add a piece of data to the <code>ArrayList</code> associated with the <code>key</code>
	 * @param key - header
	 * @param value - data to be added
	 * @return true or false if it was successfully added
	 */
	public final void addValue(final String key, final double value) {
		this.get(key).add(value);
	}

	public final void incrementPosition() {
		currentPosition++;
		GenericDataElement.incrementPosition(); // Kinda ugly but...
		if (currentPosition >= currentCapacity) {
			System.out.println(OpenLogViewer.NEWLINE + "############## Memory about to be resized! ##############");
			final long startResizes = System.currentTimeMillis();
			System.out.println("Old capacity = " + currentCapacity);
			final Runtime ourRuntime = Runtime.getRuntime();

			System.out.println("Memory Before = Max: "
					+ ourRuntime.maxMemory()
					+ ", Free: "
					+ ourRuntime.freeMemory()
					+ ", Total: "
					+ ourRuntime.totalMemory());

			int numberResized = 0;
			final Iterator<GenericDataElement> genLogIterator = this.values().iterator();
			while (genLogIterator.hasNext()) {
				// Take a stab at detecting impending doom and letting the user know
				final long overheadInMemory = currentCapacity * SIZE_OF_DOUBLE;
				final long increaseInMemory =  overheadInMemory * ourLoadFactor;

				// In order to expand our array we need oldArray bytes + newArray bytes.
				// oldArray is already excluded from free memory, so we just need memory for newArray and a fudge factor
				final long requiredMemory = 2 * (increaseInMemory + overheadInMemory); // Magic to account for late GC
				final long availableMemory = ourRuntime.freeMemory();

				if (availableMemory < requiredMemory) {
					currentPosition--; // Back out the change because we never achieved it for all fields!
					final String jvmHelp = "Get more with -Xms and -Xmx JVM options!";
					System.out.println("Detected impending out-of-memory doom! Details below! :-(");
					System.out.println("Total Available: "
							+ availableMemory
							+ " Required: "
							+ requiredMemory
							+ " Increase: "
							+ increaseInMemory
							+ " Overhead: "
							+ overheadInMemory);
					System.out.println(jvmHelp);
					final long allocatedMemory = (ourRuntime.maxMemory() / NUMBER_OF_BYTES_IN_A_MEG);
					throw new RuntimeException(allocatedMemory + "MB is insufficent memory to increase log size! " + jvmHelp + OpenLogViewer.NEWLINE
							+ "Completed " + numberResized + " of " + numberOfInternalHeaders
							+ " while increasing from " + currentCapacity + " records to " + (currentCapacity * ourLoadFactor) + " records!");
				}

				final GenericDataElement dataElement = genLogIterator.next();
				dataElement.increaseCapacity(ourLoadFactor);
				numberResized++;
			}
			currentCapacity *= ourLoadFactor;

			System.out.println("Memory After = Max: " + ourRuntime.maxMemory() + ", Free: " + ourRuntime.freeMemory() + ", Total: " + ourRuntime.totalMemory());

			final long finishResizes = System.currentTimeMillis();
			System.out.println("New capacity = " + currentCapacity);
			System.out.println("Resizes took " + (finishResizes - startResizes) + " ms");
		}

		recordCountElement.add((double) currentPosition);
	}

	/**
	 * Set the state of the log
	 * @param newLogStatus GenericLog.LOG_NOT_LOADED / GenericLog.LOG_LOADING / GenericLog.LOG_LOADED
	 */
	public final void setLogStatus(final LogState newLogStatus) {
		final LogState oldLogStatus = this.logStatus;
		this.logStatus = newLogStatus;
		pcs.firePropertyChange(LOG_LOADED_TEXT, oldLogStatus, newLogStatus);
	}

	/**
	 *
	 * @return -1 if log not loaded 0 if loading or 1 if log is loaded
	 */
	public final LogState getLogStatus() {
		return this.logStatus;
	}

	/**
	 * Add metadata This is information about the log being converted such as the location it was from or the date<br>
	 * This method does not add to its self so in order to add more info you must VAR.addMetaData(VAR.getMetaData() + NEWINFO)
	 * @param md meta data to be added
	 */
	public final void setMetaData(final String md) {
		metaData = md;
	}

	/**
	 *
	 * @return String containing the current meta data
	 */
	public final String getMetadata() {
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
	public final void addPropertyChangeListener(final String name, final PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(name, listener);
	}

	/**
	 * Remove a PropertyChangeListener
	 * @param propertyName name of listener
	 * @param listener listener
	 */
	public final void removePropertyChangeListener(final String propertyName, final PropertyChangeListener listener) {
		pcs.removePropertyChangeListener(propertyName, listener);
	}

	public final String getLogStatusMessage() {
		return logStatusMessage;
	}

	public final void setLogStatusMessage(final String message) {
		this.logStatusMessage = message;
	}

	public final int getRecordCount() {
		return currentPosition;
	}

	public final void clearOut() {
		final Iterator<GenericDataElement> lastRound = this.values().iterator();
		while (lastRound.hasNext()) {
			lastRound.next().clearOut();
		}
		this.clear();
	}
}
