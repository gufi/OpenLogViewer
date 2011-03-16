/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.diyefi.openlogviewer.propertypanel;

import java.awt.Color;

/**
 *
 * @author Owner
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
