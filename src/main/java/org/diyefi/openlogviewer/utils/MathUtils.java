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
package org.diyefi.openlogviewer.utils;

/**
 * Math is used to provide math functions specific to the project.
 * @author Ben Fenner
 */
public class MathUtils {

	private MathUtils() {
	}

	public static double roundToSignificantFigures(final double input, final int sigFigs) {
		double absoluteInput = 0;
		if (input == 0) {
			return 0;
		} else if (input < 0) {
			absoluteInput = -input;
		} else {
			absoluteInput = input;
		}

		final double d = Math.ceil(Math.log10(absoluteInput));
		final int power = sigFigs - (int) d;

		final double magnitude = Math.pow(10, power);
		final long shifted = Math.round(input * magnitude);

		return shifted / magnitude;
	}
}
