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

package GenericLog;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author Bryan
 */
public class GenericDataElement extends ArrayList<Double> {
    
    private Double maxValue;
    private Double newMaxValue;
    private Double minValue;
    private Double newMinValue;
    private Color color;
    private Color newColor;
    private String name;
    public GenericDataElement() {
        super();
        maxValue = Double.MIN_VALUE;
        newMaxValue = maxValue;
        minValue = Double.MAX_VALUE;
        newMinValue = minValue;
        Random r = new Random();
        color = Color.getHSBColor(r.nextFloat(), 1.0F, 1.0F);
        newColor = color;
    }

    @Override
    public boolean add(Double d) {
        if(newMaxValue < d) newMaxValue = d;
        if(newMinValue > d) newMinValue = d;
        return super.add(d);
    }

    public void setName(String name){
        this.name = name;
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

    public void reset() {
        newMinValue = minValue;
        newMaxValue = maxValue;
        newColor = color;
    }

    @Override
    public String toString() {
        return this.name;
    }





}
