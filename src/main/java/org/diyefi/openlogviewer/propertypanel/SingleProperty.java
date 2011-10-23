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
package org.diyefi.openlogviewer.propertypanel;

import java.awt.Color;
import org.diyefi.openlogviewer.genericlog.GenericDataElement;

public class SingleProperty implements Comparable<SingleProperty> {
	private Color color;
	private String header;
	private double min;
	private double max;
	private int split;
	private boolean active;

	public SingleProperty() {
		color = Color.RED;
		header = "";
		min = 0;
		max = 0;
		split = 1;
		active = false;
	}

	public SingleProperty(GenericDataElement GDE) {
		color = GDE.getColor();
		header = GDE.getName();
		min = GDE.getMinValue();
		max = GDE.getMaxValue();
		split = GDE.getSplitNumber();
		active = false;
	}

	public final Color getColor() {
		return color;
	}

	public final void setColor(final Color color) {
		this.color = color;
	}

	public final String getHeader() {
		return header;
	}

	public final void setHeader(final String header) {
		this.header = header;
	}

	public final double getMax() {
		return max;
	}

	public final void setMax(final double max) {
		this.max = max;
	}

	public final double getMin() {
		return min;
	}

	public final void setMin(final double min) {
		this.min = min;
	}

	public final int getSplit() {
		return split;
	}

	/**
	 * TODO add final to parameter and make work with this change.
	 *
	 * @param split
	 */
	public final void setSplit(int split) {
		if (split < 1) {
			split = 1;
		}
		this.split = split;
	}

	public final boolean isActive() {
		return active;
	}

	public final void setActive(final boolean active) {
		this.active = active;
	}

	public final String toString() {
		return header + "="
			+ color.getRed()
			+ "," + color.getGreen()
			+ "," + color.getBlue()
			+ "," + min
			+ "," + max
			+ "," + split
			+ "," + Boolean.toString(active);
	}

	public final int compareTo(final SingleProperty sp) {
		return this.getHeader().compareToIgnoreCase(sp.getHeader());
	}

	public final boolean equals(final String otherHeader) {
		return otherHeader.toLowerCase().equals(this.getHeader().toLowerCase());
	}
}
