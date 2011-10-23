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
import java.util.ArrayList;

import org.diyefi.openlogviewer.coloring.InitialLineColoring;

/**
 * GenericDataElement is Comparable Serializable and Transferable and supports property change events
 * it was built this way in order to be copy/pasteable later in the future
 * when constructed this is the meat and potatoes of the program, the graphs and data
 * displayed are pulled from these objects.
 * @author Bryan Harris
 */
public class GenericDataElement extends ArrayList<Double> implements Comparable<GenericDataElement>, Serializable, Transferable {
	private static final long serialVersionUID = 1823741455455439887L;

	/**
	 * default max value determined while loading up data
	 */
	private Double maxValue;

	/**
	 * newMaxValue is the value returned at any time getMaxValue() is called.
	 * it can be modified to change the graphing display
	 */

	private Double newMaxValue;
	/**
	 * default min value determined while loading up data
	 */

	private Double minValue;
	/**
	 * newMinValue is the value returned at any time getMinValue() is called.
	 * it can be modified to change the graphing display
	 */

	private Double newMinValue;
	/**
	 * default color created when building the GDE
	 */
	private Color color;

	/**
	 * Color that can be modified
	 */

	private Color newColor;

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
	public GenericDataElement() {
		super(50000); // TODO remove MAGIC
		PCS = new PropertyChangeSupport(this);
		maxValue = Double.MIN_VALUE;
		newMaxValue = maxValue;
		minValue = Double.MAX_VALUE;
		newMinValue = minValue;
		color = InitialLineColoring.INSTANCE.getBestAvailableColor();
		newColor = color;
		splitNumber = 1;
		addFlavors();
	}

	/**
	 * Data type support for Transferable
	 */
	private void addFlavors() {
		dataFlavor = new DataFlavor[3];
		//try {
		dataFlavor[0] = new DataFlavor(DataFlavor.javaSerializedObjectMimeType +
				";class=\"" + GenericDataElement.class.getName() + "\"",
		"OLV GenericDataElement");
		dataFlavor[1] = DataFlavor.stringFlavor;
		dataFlavor[2] = DataFlavor.getTextPlainUnicodeFlavor();
	}

	/**
	 * override add(<T> t) of ArrayList to find min and max values before adding to the List
	 * @param d Double - Double.parseDouble(String) value to add to the array
	 * @return true on success, false if unable
	 */
	@Override
	public boolean add(Double d) {
		if (newMaxValue < d) {
			maxValue = d;
			newMaxValue = d;
		}

		if (newMinValue > d) {
			minValue = d;
			newMinValue = d;
		}

		return super.add(d);
	}

	/**
	 * sets the splitNumber or division of the graph in the graphing screen
	 * if its the same a property change event is fired called "Split"
	 * @param splitNumber
	 */
	public void setSplitNumber(int splitNumber) {
		if (splitNumber < 1) {
			splitNumber = 1;
		}

		int old = this.splitNumber;
		this.splitNumber = splitNumber;
		PCS.firePropertyChange("Split", old, this.splitNumber);

	}

	/**
	 * this will set the min and max to the default min/max values
	 */
	public void reset() {
		newMinValue = minValue;
		newMaxValue = maxValue;
	}

	public void addPropertyChangeListener(String property, PropertyChangeListener PCL) {
		PCS.addPropertyChangeListener(property, PCL);
	}

	public void removePropertyChangeListener(String property, PropertyChangeListener PCL) {
		PCS.removePropertyChangeListener(property, PCL);
	}

	@Override
	public String toString() {
		return this.name;
	}

	@Override
	public int compareTo(GenericDataElement otherGDE) {
		return this.getName().compareToIgnoreCase(otherGDE.getName());
	}

	@Override
	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
		if (flavor.equals(dataFlavor[0])) {
			return this;
		} else if (flavor.equals(dataFlavor[1])) {
			return "Unsupported";
		} else if (flavor.equals(dataFlavor[2])) {
			return "Unsupported";
		}
		else {
			throw new UnsupportedFlavorException(flavor);
		}
	}

	@Override
	public DataFlavor[] getTransferDataFlavors() {
		return dataFlavor;
	}

	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor) {
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
	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}
	public Double getMaxValue() {
		return newMaxValue;
	}
	public void setMaxValue(Double highValue) {
		this.newMaxValue = highValue;
	}
	public Double getMinValue() {
		return newMinValue;
	}
	public void setMinValue(Double lowValue) {
		this.newMinValue = lowValue;
	}
	public Color getColor() {
		return newColor;
	}
	public void setColor(Color c) {
		newColor = c;
	}
	public int getSplitNumber() {
		return splitNumber;
	}
}
