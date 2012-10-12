/* OpenLogViewer
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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;


public final class Utilities {
	/**
	 * Prevent instantiation.
	 */
	private Utilities() {
	}

	public static String getExtension(final File f) {
		String ext = null;
		final String s = f.getName();
		final int i = s.lastIndexOf('.');

		if (i > 0 && i < s.length() - 1) {
			ext = s.substring(i + 1).toLowerCase();
		}
		return ext;
	}

	public static int countBytes(final File f, final byte b) throws IOException {
		InputStream is = new BufferedInputStream(new FileInputStream(f));
		try {
			byte[] data = new byte[8192];
			int lines = 0;
			int readCount = 0;
			boolean empty = true;
			while ((readCount = is.read(data)) != -1) {
				empty = false;
				for (int i = 0; i < readCount; ++i) {
					if (data[i] == b) {
						lines++;
					}
				}
			}
			return (lines == 0 && !empty) ? 1 : lines;
		} finally {
			is.close();
		}
	}

}
