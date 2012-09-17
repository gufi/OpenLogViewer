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
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;

/**
 * MathUtils is used to provide math functions specific to the project.
 * @author Ben Fenner
 */
public final class MathUtils {
	private static final char DS = DecimalFormatSymbols.getInstance().getDecimalSeparator();
	private static final DecimalFormat custom = (DecimalFormat) NumberFormat.getNumberInstance();
	private static final DecimalFormat standy = (DecimalFormat) NumberFormat.getNumberInstance();
	static {
		custom.setGroupingUsed(false);
		standy.setGroupingUsed(false);
	}

	private MathUtils() {
	}

	/**
	 *
	 * @param input - The double you'd like to round the decimal places for
	 * @param decimalPlaces - The number of decimal places you'd like
	 * @return the formatted number
	 */
	public static String roundDecimalPlaces(final double input, final int decimalPlaces) {
		// Deal with zero or negative decimal places requested
		if (decimalPlaces <= 0) {
			return standy.format(Math.round(input));
		}

		final StringBuilder format = new StringBuilder("###0" + DS);
		final StringBuilder negativeZero = new StringBuilder("-0" + DS);

		for (int i = 0; i < decimalPlaces; i++) {
			format.append('0');
			negativeZero.append('0');
		}

		custom.applyLocalizedPattern(format.toString());
		final StringBuilder output = new StringBuilder(custom.format(input));

		// Deal with negative zero
		if (output.toString().equals(negativeZero.toString())) {
			output.deleteCharAt(0);
		}

		return output.toString();
	}
}
