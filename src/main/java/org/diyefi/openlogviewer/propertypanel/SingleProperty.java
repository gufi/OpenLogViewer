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

/**
 *
 * @author Bryan Harris
 */
public class SingleProperty implements Comparable {

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

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public double getMax() {
        return max;
    }

    public void setMax(double max) {
        this.max = max;
    }

    public double getMin() {
        return min;
    }

    public void setMin(double min) {
        this.min = min;
    }

    public int getSplit() {
        return split;
    }

    public void setSplit(int split) {
        if(split < 1){
            split = 1;
        }
        this.split = split;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String toString() {
        return header + "="
                + color.getRed()
                + "," + color.getGreen()
                + "," + color.getBlue()
                + "," + min
                + "," + max
                + "," + split
                + "," + Boolean.toString(active);
    }

    public int compareTo(Object o) {
        if (o instanceof SingleProperty) {
            SingleProperty sp = (SingleProperty) o;
            return this.getHeader().compareToIgnoreCase(sp.getHeader());
        } else {
            return -1;
        }
    }

    public boolean equals(String header) {
        return header.toLowerCase().equals(this.getHeader().toLowerCase());
    }
}
