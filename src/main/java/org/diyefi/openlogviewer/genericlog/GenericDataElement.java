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
package org.diyefi.openlogviewer.genericlog;

import java.awt.Color;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;

import org.diyefi.openlogviewer.coloring.InitialLineColoring;

/**
 * GenericDataElement is Comparable Serializable and Transferable and supports property change events
 * it was built this way in order to be copy/pasteable later in the future
 * when constructed this is the meat and potatoes of the program, the graphs and data
 * displayed are pulled from these objects.
 * @author Bryan Harris
 */
public final class GenericDataElement implements Comparable<GenericDataElement>, Serializable, Transferable {
	private static final long serialVersionUID = 1L;

	private static int currentRecord;

	/**
	 * The meat of this object! Previously in a slow fat ArrayList.
	 */
	private double[] values;

	// These two fields belong here:
	private double minValue;
	private double maxValue;
	private boolean realMinAndMaxFound = false;

	// These three do not - move them into some graphics object and keep the data separated from the look...
	private double displayMinValue;
	private double displayMaxValue;
	private Color displayColor;
	private boolean displayMinAndMaxSet = false;

	/**
	 * GDE Header name
	 */
	private String name;

	/**
	 * Division on the Graphing layer
	 */
	private int splitNumber;
	private PropertyChangeSupport PCS;
	private DataFlavor[] dataFlavor;

	/**
	 * Constructor brings the GDE up to speed, defaulting with an available 50,000 datapoints
	 * in order to reduce the number of times the Array list has to copy its contents
	 * in order to increase size.
	 */
	protected GenericDataElement(final int initialLength) {
		values = new double[initialLength];

		PCS = new PropertyChangeSupport(this);

		maxValue = -Double.MAX_VALUE;
		minValue = Double.MAX_VALUE;

		// this should just be white and should become something at drop time. Drop has an event, let's use it.
		displayColor = InitialLineColoring.INSTANCE.getBestAvailableColor();
		splitNumber = 1;
		addFlavors();
	}

	protected void increaseCapacity(final int ourLoadFactor) {
		values = Arrays.copyOf(values, (values.length * ourLoadFactor));
	}

	protected static void incrementPosition() {
		currentRecord++;
	}
	protected static void resetPosition() {
		currentRecord = -1;
	}

	/**
	 * Data type support for Transferable
	 */
	private void addFlavors() {
		dataFlavor = new DataFlavor[3];
		final String supportedFlavour = DataFlavor.javaSerializedObjectMimeType + ";class=\"" + GenericDataElement.class.getName() + "\"";
		dataFlavor[0] = new DataFlavor(supportedFlavour, "OLV GenericDataElement");
		dataFlavor[1] = DataFlavor.stringFlavor;
		dataFlavor[2] = DataFlavor.getTextPlainUnicodeFlavor();
	}

	/**
	 * override add(<T> t) of ArrayList to find min and max values before adding to the List
	 * @param d Double - Double.parseDouble(String) value to add to the array
	 * @return true on success, false if unable
	 */
	public void add(final double value) { //  TODO simplify the shit out of this, and/or remove it.
		values[currentRecord] = value;
	}

	// TODO maybe make these a direct reference to the array for efficiency sake, and above ^
	public double get(final int index) {
		return values[index];
	}
	public int size() { // TODO ^
		return currentRecord; // was values.length; array length is longer than data in it.
	}

	/**
	 * sets the splitNumber or division of the graph in the graphing screen
	 * if its the same a property change event is fired called "Split"
	 *
	 * @param splitNumber
	 */
	public void setSplitNumber(final int splitNumber) {
		int newSplitNumber = 1;
		if (splitNumber > 1) {
			newSplitNumber = splitNumber;
		}

		final int old = this.splitNumber;
		this.splitNumber = newSplitNumber;
		PCS.firePropertyChange("Split", old, this.splitNumber);
	}

	/**
	 * TODO move this into GUI space and out of this class!!
	 */
	public void reset() {
		displayMinValue = getMinValue();
		displayMaxValue = getMaxValue();
	}

	public void addPropertyChangeListener(final String property, final PropertyChangeListener PCL) {
		PCS.addPropertyChangeListener(property, PCL);
	}

	public void removePropertyChangeListener(final String property, final PropertyChangeListener PCL) {
		PCS.removePropertyChangeListener(property, PCL);
	}

	@Override
	public String toString() {
		return this.name;
	}

	@Override
	public int compareTo(final GenericDataElement otherGDE) {
		return this.getName().compareToIgnoreCase(otherGDE.getName());
	}

	@Override
	public Object getTransferData(final DataFlavor flavor) throws UnsupportedFlavorException, IOException {
		if (flavor.equals(dataFlavor[0])) {
			return this;
		} else if (flavor.equals(dataFlavor[1])) {
			return "Unsupported";
		} else if (flavor.equals(dataFlavor[2])) {
			return "Unsupported";
		} else {
			throw new UnsupportedFlavorException(flavor);
		}
	}

	@Override
	public DataFlavor[] getTransferDataFlavors() {
		return dataFlavor;
	}

	@Override
	public boolean isDataFlavorSupported(final DataFlavor flavor) {
		for (int i = 0; i < dataFlavor.length; i++) {
			if (flavor.equals(dataFlavor[i])) {
				return true;
			}
		}
		return false;
	}

	/**
	 * set header name, called during GenericLog construction
	 * @param name
	 */
	public void setName(final String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}

	public double getMaxValue() {
		findMinAndMaxValues();
		return maxValue;
	}
	public double getMinValue() {
		findMinAndMaxValues();
		return minValue;
	}

	private void findMinAndMaxValues() {
		if (!realMinAndMaxFound) {
			for (int i = 0; i < currentRecord; i++) {
				final double value = values[i];
				if (maxValue < value) {
					maxValue = value;
				}
				if (minValue > value) {
					minValue = value;
				}
			}
			// System.out.println("MinMaxFor: " + this.name); // Used to check for option pane running this code inappropriately...

			realMinAndMaxFound = true;
		}
	}

	public double getDisplayMinValue() {
		setDisplayMinAndMaxDefaultsIfRequired();
		return displayMinValue;
	}
	public double getDisplayMaxValue() {
		setDisplayMinAndMaxDefaultsIfRequired();
		return displayMaxValue;
	}
	private void setDisplayMinAndMaxDefaultsIfRequired() {
		if (!displayMinAndMaxSet) {
			displayMinValue = getMinValue();
			displayMaxValue = getMaxValue();
			displayMinAndMaxSet = true;
		}
	}

	public void setDisplayMaxValue(final double highValue) {
		if (!displayMinAndMaxSet) {
			displayMinValue = getMinValue();
			displayMinAndMaxSet = true;
		}
		this.displayMaxValue = highValue;
	}
	public void setDisplayMinValue(final double lowValue) {
		if (!displayMinAndMaxSet) {
			displayMaxValue = getMaxValue();
			displayMinAndMaxSet = true;
		}
		this.displayMinValue = lowValue;
	}

	public Color getDisplayColor() {
		return displayColor;
	}
	public void setDisplayColor(final Color c) {
		displayColor = c;
	}
	public int getSplitNumber() {
		return splitNumber;
	}

	public void clearOut() {
		values = null;
	}
}
