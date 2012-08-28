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
	// ALMOST_ONE is used to seed colorList with a red that will stay at the end of
	// the list. If you create a Color with a hue of 1F then it actually gets
	// created as a Color with a hue of 0F which is identical to the red at the
	// beginning of the list, which prevents it working as a good book-end.
	// 0.999F must be used instead of 0.9999F or more because it will get rounded
	// by the Color constructor to 1F.
	private static final float ALMOST_ONE = 0.999F;
	private static final float ONE_THIRD = 1F/3F;
	private static final float TWO_THIRDS = 2F/3F;

	private final List<Color> colorList;
	private final Color bookEndRed = Color.getHSBColor(ALMOST_ONE, 1.0F, 1.0F);

	private InitialLineColoring() {
		colorList = new LinkedList<Color>();
		colorList.add(0, bookEndRed); // Seed with high value red
	}

	public Color getBestAvailableColor() {
		Color newColor = Color.GRAY;
		int index = 0;

		final Color seedRed = Color.getHSBColor(0.0F, 1.0F, 1.0F);
		final Color seedGreen = Color.getHSBColor(ONE_THIRD, 1.0F, 1.0F);
		final Color seedBlue = Color.getHSBColor(TWO_THIRDS, 1.0F, 1.0F);
		if (!colorList.contains(seedRed)) { // Seed with low value red
			newColor = seedRed;
			index = 0;
		} else if (!colorList.contains(seedGreen)) {  // Seed with green
			newColor = seedGreen;
			index = 1;
		} else if (!colorList.contains(seedBlue)) {  // Seed with blue
			newColor = seedBlue;
			index = 2;
		} else {

			float hue = 0.0F;
			float maxDistance = 0.0F;
			final ListIterator<Color> i = colorList.listIterator();
			Color c2 = Color.RED;

			while (i.hasNext()) {
				final Color c1 = i.next();

				if (i.hasNext()) {
					c2 = i.next();
					i.previous();
				} else {
					c2 = colorList.get(colorList.size() - 1);
				}
				final float[] hsbValues1 = Color.RGBtoHSB(c1.getRed(), c1.getGreen(), c1.getBlue(), null);
				final float[] hsbValues2 = Color.RGBtoHSB(c2.getRed(), c2.getGreen(), c2.getBlue(), null);
				final float distance = hsbValues2[0] - hsbValues1[0];
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
	
	public void giveBackAllColors(){
		while (colorList.size() > 0) {
			colorList.remove(0);
		}
		colorList.add(0, bookEndRed);
	}
}
