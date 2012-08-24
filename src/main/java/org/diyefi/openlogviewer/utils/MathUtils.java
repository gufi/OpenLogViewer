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

import java.text.DecimalFormat;

/**
 * MathUtils is used to provide math functions specific to the project.
 * @author Ben Fenner
 */
public final class MathUtils {

	private MathUtils() {
	}

	/**
	 *
	 * @param inputNum - The double you'd like to round the decimal places for
	 * @param sigDecFigs - The number of decimal places you'd like
	 * @return
	 */
	public static String roundDecimalPlaces(final double inputNum, final int numDecPlaces) {
		// Deal with zero or negative decimal places requested
		if (numDecPlaces <= 0){
			return String.valueOf(Math.round(inputNum));
		}

		final StringBuilder format = new StringBuilder("###0.");
		final StringBuilder negativeZero = new StringBuilder("-0.");
		for (int i = 0; i < numDecPlaces; i++) {
			format.append('0');
			negativeZero.append('0');
		}
		final DecimalFormat df = new DecimalFormat(format.toString());
		final StringBuilder output = new StringBuilder(df.format(inputNum));

		// Deal with negative zero
		if (output.toString().equals(negativeZero.toString())){
			output.deleteCharAt(0);
		}
		return output.toString();
	}
}
