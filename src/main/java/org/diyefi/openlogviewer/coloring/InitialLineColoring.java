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
import java.util.List;
import java.util.LinkedList;
import java.util.ListIterator;

/**
 * InitialLineColoring is used to provide the coloring for the GenericDataElements.
 * The colors provided should be the most contrasting colors possible.
 * @author Ben Fenner
 */
public enum InitialLineColoring {

	INSTANCE;

	private final List<Color> colorList;

	private InitialLineColoring() {
		colorList = new LinkedList<Color>();
	}

	public Color getBestAvailableColor() {
		Color newColor = Color.GRAY;
		int index = 0;

		if(colorList.size() == 0){
			newColor = Color.getHSBColor(0.0F, 1.0F, 1.0F); //Seed with low value red
			index = 0;
		} else if(colorList.size() == 1){
			newColor = Color.getHSBColor(0.333F, 1.0F, 1.0F); //Seed with green
			index = 1;
		} else if(colorList.size() == 2){
			index = 2;
			Color bookEndRed = Color.getHSBColor(0.999F, 1.0F, 1.0F); //Seed with high value red
			colorList.add(index, bookEndRed);
			newColor = Color.getHSBColor(0.666F, 1.0F, 1.0F); //Seed with blue
		} else {

			float hue = 0.0F;
			float maxDistance = 0.0F;
			final ListIterator<Color> i = colorList.listIterator();
			Color c2 = Color.RED;

			while (i.hasNext()) {
				final Color c1 = i.next();

				if(i.hasNext()) {
					c2 = i.next();
					i.previous();
				} else {
					c2 = colorList.get(colorList.size() - 1);
				}
				float[] hsbValues1 = Color.RGBtoHSB(c1.getRed(), c1.getGreen(), c1.getBlue(), null);
				float[] hsbValues2 = Color.RGBtoHSB(c2.getRed(), c2.getGreen(), c2.getBlue(), null);
				float distance = hsbValues2[0] - hsbValues1[0];
				if (distance > maxDistance) {
					maxDistance = distance;
					index = colorList.indexOf(c2);
					hue = hsbValues1[0] + (distance / 2.0F);
				}
			}
			newColor = Color.getHSBColor(hue, 1.0F, 1.0F);
		}
		colorList.add(index, newColor);
		return newColor;
	}

	public boolean giveBackColor(final Color c) {
		return colorList.remove(c);
	}
}
