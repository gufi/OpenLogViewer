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

package org.diyefi.openlogviewer.filefilters;

import java.io.File;
import javax.swing.filechooser.FileFilter;
import org.diyefi.openlogviewer.utils.Utilities;

public class FreeEMSBinFileFilter extends FileFilter {
	public FreeEMSBinFileFilter() {
		super();
	}

	@Override
	public final String getDescription() {
		return "*.bin";
	}

	@Override
	public final boolean accept(final File file) {
		if (file.isDirectory()) {
			return true;
		}

		final String extension = Utilities.getExtension(file);

		return "bin".equals(extension);
	}
}
