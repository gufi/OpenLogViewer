/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package OpenLogViewer.Properties;

import java.awt.Color;

/**
 *
 * @author Owner
 */
public class SingleProperty {

    Color color;
    private String header;
    private double min;
    private double max;
    private int split;

    public SingleProperty() {
        color = Color.RED;
        header = "";
        min = 0;
        max = 0;
        split = 0;
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
        this.split = split;
    }


    public String toString() {
        return header + "="
                + color.getRed()
                + ","+ color.getGreen()
                + ","+ color.getBlue()
                + ","+ min
                + ","+ max
                + ","+ split;
    }
}
