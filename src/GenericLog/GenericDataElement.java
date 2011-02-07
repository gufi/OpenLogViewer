/* DataReader
 *
 * Copyright 2011
 *
 * This file is part of the DataReader project.
 *
 * DataReader software is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * DataReader software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with any DataReader software.  If not, see http://www.gnu.org/licenses/
 *
 * I ask that if you make any changes to this file you fork the code on github.com!
 *
 */

package GenericLog;

import java.util.ArrayList;

/**
 *
 * @author Bryan
 */
public class GenericDataElement extends ArrayList<Double> {
    
    private Double maxValue;
    private Double minValue;

    public GenericDataElement() {
        super();
        maxValue = Double.MIN_VALUE;
        minValue = Double.MAX_VALUE;
    }

    @Override
    public boolean add(Double d) {
        if(maxValue < d) maxValue = d;
        if(minValue > d) minValue = d;
        return super.add(d);
    }

    public Double getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(Double highValue) {
        this.maxValue = highValue;
    }

    public Double getMinValue() {
        return minValue;
    }

    public void setMinValue(Double lowValue) {
        this.minValue = lowValue;
    }




}
