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
package OpenLogViewer.GenericLog;

import java.awt.Color;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author Bryan
 */
public class GenericDataElement extends ArrayList<Double> implements Comparable, Serializable, Transferable {

    private Double maxValue;
    private Double newMaxValue;
    private Double minValue;
    private Double newMinValue;
    private Color color;
    private Color newColor;
    private String name;
    private int splitNumber;
    private PropertyChangeSupport PCS;
    private DataFlavor[] dataFlavor;

    public GenericDataElement() {
        super(50000);
        PCS = new PropertyChangeSupport(this);
        maxValue = Double.MIN_VALUE;
        newMaxValue = maxValue;
        minValue = Double.MAX_VALUE;
        newMinValue = minValue;
        Random r = new Random();
        color = Color.getHSBColor(r.nextFloat(), 1.0F, 1.0F);
        newColor = color;
        splitNumber = 1;
        addFlavors();
    }

    private void addFlavors() {
        dataFlavor = new DataFlavor[3];
        //try {
        dataFlavor[0] = new DataFlavor(DataFlavor.javaSerializedObjectMimeType+
                ";class=\"" + GenericDataElement.class.getName() + "\"",
                "OLV GenericDataElement");
        dataFlavor[1] = DataFlavor.stringFlavor;
        dataFlavor[2] = DataFlavor.getTextPlainUnicodeFlavor();
        //}catch (ClassNotFoundException CNFE) {
        //    System.out.println(CNFE.getMessage());
       // }
    }

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

    public void setSplitNumber(int splitNumber) {
        if(splitNumber < 1 ) {
            splitNumber = 1;
        }
        int old = this.splitNumber;
        this.splitNumber = splitNumber;
        PCS.firePropertyChange("Split", old, this.splitNumber);

    }

    public void reset() {
        newMinValue = minValue;
        newMaxValue = maxValue;
        newColor = color;
    }

    public void addPropertyChangeListener(String property, PropertyChangeListener PCL) {
        PCS.addPropertyChangeListener(property, PCL);
    }

    public void removePropertyChangeListener(String property, PropertyChangeListener PCL) {
        PCS.removePropertyChangeListener(property, PCL);
    }
    ///Object
    @Override
    public String toString() {
        return this.name;
    }
    //Comparable
    @Override
    public int compareTo(Object o) {
        if (o instanceof GenericDataElement) {
            GenericDataElement GDE = (GenericDataElement) o;
            return this.getName().compareToIgnoreCase(GDE.getName());
        } else {
            return -1;
        }
    }
    //Transferable
    @Override
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        if(flavor.equals(dataFlavor[0])) {
            return this;
        }else if(flavor.equals(dataFlavor[1])){
            return "Unsupported";
        }else if(flavor.equals(dataFlavor[2])){
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
        for(int i = 0; i<dataFlavor.length;i++){
            if(flavor.equals(dataFlavor[i])){
                return true;
            }
        }
        return false;
    }

    //Seralizable

}
