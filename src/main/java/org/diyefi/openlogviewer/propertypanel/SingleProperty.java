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
	private int trackIndex;
	private boolean active;

	public SingleProperty() {
		color = Color.GRAY;
		header = "";
		min = 0;
		max = 0;
		trackIndex = 0;
		active = false;
	}

	public SingleProperty(final GenericDataElement gde) {
		color = gde.getDisplayColor();
		header = gde.getName();
		min = gde.getDisplayMinValue();
		max = gde.getDisplayMaxValue();
		trackIndex = gde.getTrackIndex();
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

	public final int getTrackIndex() {
		return trackIndex;
	}

	public final void setTrackIndex(final int newIndex) {
		if (newIndex < 0) {
			trackIndex = 0;
		} else {
			trackIndex = newIndex;
		}
	}

	public final boolean isActive() {
		return active;
	}

	public final void setActive(final boolean active) {
		this.active = active;
	}

	public final String toString() {
		final String seperator = ",";
		final StringBuilder output = new StringBuilder(header);
		output.append("=");
		output.append(color.getRed());
		output.append(seperator);
		output.append(color.getGreen());
		output.append(seperator);
		output.append(color.getBlue());
		output.append(seperator);
		output.append(min);
		output.append(seperator);
		output.append(max);
		output.append(seperator);
		output.append(trackIndex);
		output.append(seperator);
		output.append(Boolean.toString(active));
		return output.toString();
	}

	public final int compareTo(final SingleProperty sp) {
		return this.getHeader().compareToIgnoreCase(sp.getHeader());
	}

	/*
	 * TODO investigate this further:
	 *
	 * In real use, this gets a SingleProperty passed in, which therefore means
	 * that we're comparing header with toString output, when toString is header
	 * concatenated with other fields. IE, it seems as though this is always
	 * false, however in my second round of testing, it never got called, so I
	 * just don't know.
	 */
	public final boolean equals(final String otherHeader) {
		return getHeader().equalsIgnoreCase(otherHeader);
	}
}
