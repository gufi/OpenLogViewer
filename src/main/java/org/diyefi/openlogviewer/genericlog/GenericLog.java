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
import java.util.Arrays;
import java.util.ResourceBundle;

import org.diyefi.openlogviewer.Keys;
import org.diyefi.openlogviewer.OpenLogViewer;
import org.diyefi.openlogviewer.Text;
import org.diyefi.openlogviewer.coloring.InitialLineColoring;

public class GenericLog extends LinkedHashMap<String, GenericDataElement> {
	public static final String RECORD_COUNT_KEY = "OLV Record Count"; // Fixed references, not for translation
	public static final String tempResetKey = "OLV Temp Resets";      // Fixed references, not for translation
	public static final String elapsedTimeKey = "OLV Elapsed Time";   // Fixed references, not for translation

	// TODO this is no good, get rid of it, show some sort of status indicator in the GUI showing that log loading is not complete
	// For streams, always show that as a way of saying "still streaming!"
	public static enum LogState { LOG_NOT_LOADED, LOG_LOADING, LOG_LOADED }

	private static final long serialVersionUID = 1L;

	// Info to populate built-in fields efficiently, likely to be done differently in future, but if not, put this in some structure.
	private static final int NUMBER_OF_BUILTIN_FIELDS = 3; // See below:
	private static final int RECORD_COUNT_OFFSET = 0;
	private static final int TEMP_RESET_OFFSET   = 1;
	private static final int ELAPSED_TIME_OFFSET = 2;
	private static final int SIZE_OF_DOUBLE = 8;
	private static final int NUMBER_OF_BYTES_IN_A_MEG = 1000000;

	private final ResourceBundle labels;
	private final GenericDataElement recordCountElement;
	private final PropertyChangeSupport pcs;
	private final int ourLoadFactor;
	private final int numberOfInternalHeaders;

	private LogState logStatus;
	private String logStatusMessage;

	// Track the size of our children so that we can bump them up one by one where required
	private int currentCapacity;
	private int currentPosition = -1;
	// ^ TODO if we end up limiting memory usage by some configurable amount and recycling positions, for live streaming, then add count

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
				InitialLineColoring.INSTANCE.giveBackAllColors();
			}
		}
	};

	/**
	 * provide a <code>String</code> array of headers<br>
	 * each header will be used as a HashMap key, the data related to each header will be added to an <code>ArrayList</code>.
	 * @param headers - of the data to be converted
	 */
	public GenericLog(final String[] headers, final int initialCapacity, final int ourLoadFactor, final ResourceBundle labels) {
		super(1 + (headers.length + NUMBER_OF_BUILTIN_FIELDS), 1.0f); // refactor to use (capacityRequired+1, 1.0) for maximum performance (no rehashing)

		this.labels = labels;

		GenericDataElement.resetPosition(); // Kinda ugly, but...
		logStatus = LogState.LOG_NOT_LOADED;
		pcs = new PropertyChangeSupport(this);
		addPropertyChangeListener(Keys.LOG_LOADED, autoLoad);

		this.ourLoadFactor = ourLoadFactor;
		currentCapacity = initialCapacity;

		// A bit dirty, but not too bad.
		numberOfInternalHeaders = headers.length + NUMBER_OF_BUILTIN_FIELDS;
		final String[] internalHeaders = Arrays.copyOf(headers, numberOfInternalHeaders);

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
	 */
	public final void addValue(final String key, final double value) {
		get(key).add(value);
	}

	public final void incrementPosition() {
		currentPosition++;
		GenericDataElement.incrementPosition(); // Kinda ugly but...
		if (currentPosition >= currentCapacity) {
			System.out.println(OpenLogViewer.NEWLINE + labels.getString(Text.MEMORY_RESIZE_WARNING));
			final long startResizes = System.currentTimeMillis();
			System.out.println(labels.getString(Text.MEMORY_OLD_CAPACITY) + currentCapacity);
			final Runtime ourRuntime = Runtime.getRuntime();

			System.out.println(labels.getString(Text.MEMORY_BEFORE)
					+ labels.getString(Text.MEMORY_MAX) + ourRuntime.maxMemory()
					+ labels.getString(Text.MEMORY_FREE) + ourRuntime.freeMemory()
					+ labels.getString(Text.MEMORY_TOTAL) + ourRuntime.totalMemory());

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
					System.out.println(labels.getString(Text.DETECTED_OOME_MEMORY_DOOM));
					System.out.println(labels.getString(Text.MEMORY_TOTAL_AVAILABLE) + availableMemory
							+ labels.getString(Text.MEMORY_TOTAL_REQUIRED) + requiredMemory
							+ labels.getString(Text.MEMORY_TOTAL_INCREASE) + increaseInMemory
							+ labels.getString(Text.MEMORY_TOTAL_OVERHEAD) + overheadInMemory);
					System.out.println(labels.getString(Text.JVM_HELP_MESSAGE));
					final long allocatedMemory = (ourRuntime.maxMemory() / NUMBER_OF_BYTES_IN_A_MEG);
					throw new RuntimeException(allocatedMemory + labels.getString(Text.MEG_INSUFFICIENT_MEMORY_FOR_INCREASE)
							+ labels.getString(Text.JVM_HELP_MESSAGE) + OpenLogViewer.NEWLINE
							+ labels.getString(Text.RESIZE_COMPLETED_PART1) + numberResized
							+ labels.getString(Text.RESIZE_COMPLETED_PART2) + numberOfInternalHeaders
							+ labels.getString(Text.RESIZE_COMPLETED_PART3) + currentCapacity
							+ labels.getString(Text.RESIZE_COMPLETED_PART4) + (currentCapacity * ourLoadFactor)
							+ labels.getString(Text.RESIZE_COMPLETED_PART5));
				}

				final GenericDataElement dataElement = genLogIterator.next();
				dataElement.increaseCapacity(ourLoadFactor);
				numberResized++;
			}
			currentCapacity *= ourLoadFactor;

			System.out.println(labels.getString(Text.MEMORY_AFTER)
					+ labels.getString(Text.MEMORY_MAX) + ourRuntime.maxMemory()
					+ labels.getString(Text.MEMORY_FREE) + ourRuntime.freeMemory()
					+ labels.getString(Text.MEMORY_TOTAL) + ourRuntime.totalMemory());

			final long finishResizes = System.currentTimeMillis();
			System.out.println(labels.getString(Text.MEMORY_NEW_CAPACITY) + currentCapacity);
			System.out.println(labels.getString(Text.MEMORY_RESIZES_TOOK)
					+ (finishResizes - startResizes)
					+ labels.getString(Text.MEMORY_RESIZES_UNIT));
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
		pcs.firePropertyChange(Keys.LOG_LOADED, oldLogStatus, newLogStatus);
	}

	/**
	 *
	 * @return -1 if log not loaded 0 if loading or 1 if log is loaded
	 */
	public final LogState getLogStatus() {
		return this.logStatus;
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
