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
package org.diyefi.openlogviewer.coloring;

import java.awt.Color;
import java.util.LinkedList;
import java.util.ListIterator;

import org.diyefi.openlogviewer.coloring.MarkedColor;

/**
 * InitialLineColoring is used to provide the coloring for the GenericDataElements.
 * The colors provided should be the most contrasting colors possible.
 * @author Ben Fenner
 */
public enum InitialLineColoring{

	INSTANCE;
	
	private InitialLineColoring(){
		colorList = new LinkedList<MarkedColor>();
		colorList.addFirst(new MarkedColor(Color.getHSBColor(1.0F, 1.0F, 1.0F), true, 0.0));
		colorList.add(new MarkedColor(Color.getHSBColor(0.25F, 1.0F, 1.0F), true, 0.0));
		colorList.add(new MarkedColor(Color.getHSBColor(0.5F, 1.0F, 1.0F), true, 0.0));
		colorList.add(new MarkedColor(Color.getHSBColor(0.75F, 1.0F, 1.0F), true, 0.0));
		for(long i = 8; i < 128; i*=2) {
			this.addColors(i);
		}
	}

	private void addColors(long hueOffsetDinominator){
		long numColors = hueOffsetDinominator/2;
		double hueOffset = 1.0/hueOffsetDinominator;
		for(long i = 0; i < (numColors - 1); i++){
			double hue = 0.0;
			hue += hueOffset;	//Always skip pure red and go to the next hue.
			for(int j = 0; j < 4; j++){
				MarkedColor newColor = new MarkedColor(Color.getHSBColor((float)hue, 1.0F, 1.0F), true, hue);
				if(!colorList.contains(newColor)){
					colorList.add(newColor);
				}
				hue += 0.25;
			}
		}
	}
	
	public Color getBestAvailableColor(){
		Color nextColor = Color.gray;
		ListIterator<MarkedColor> i = colorList.listIterator();
		for(boolean found = false; i.hasNext() && !found;){
			MarkedColor c = i.next();
			if(c.isAvailable()){
				c.setAvailability(false);
				nextColor = c.getColor();				
				found = true;
			}
		}
		return nextColor;
	}
	
	public void giveBackColor(Color c){
		if(colorList.contains(c)){
			colorList.get(colorList.indexOf(c)).setAvailability(true);
		}
	}

	private final LinkedList<MarkedColor> colorList;
}