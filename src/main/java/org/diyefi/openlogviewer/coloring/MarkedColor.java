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

/**
 * MarkedColor is a couplet used to allow the marking of colors as "unavailable" or "available".
 * @author Ben Fenner
 */
public class MarkedColor{

	public MarkedColor(){
		color = Color.gray;
		availability = true;
		hue = -1.0;
	}
	
	public MarkedColor(Color color){
		this.color = color;
		availability = true;
		hue = -1.0;
	}
	
	public MarkedColor(boolean availability){
		color = Color.gray;
		this.availability = availability;
		hue = -1.0;
	}
	
	public MarkedColor(Color color, boolean availability){
		this.color = color;
		this.availability = availability;
		hue = -1.0;
	}
	
	public MarkedColor(Color color, boolean availability, double hue){
		this.color = color;
		this.availability = availability;
		this.hue = hue;
	}

	public Color getColor(){
		return color;
	}
	
	public void setColor(Color color){
		this.color = color;
	}
	
	public boolean isAvailable(){
		return availability;
	}
	
	public void setAvailability(boolean availability){
		this.availability = availability;
	}
	
	public double getHue(){
		return hue;
	}
	
	public void setHue(double hue){
		this.hue = hue;
	}
	
	public boolean equals(MarkedColor c){
		return c.getColor().equals(this.color) || c.getHue() == this.hue;
	}
	
	public boolean equals(Color c){
		return c.equals(this.color);
	}

	private Color color;
	private boolean availability;
	double hue;
}