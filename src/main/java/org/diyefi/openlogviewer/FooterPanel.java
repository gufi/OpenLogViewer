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
package org.diyefi.openlogviewer;

import java.awt.BorderLayout;
import java.util.ResourceBundle;

import javax.swing.JPanel;



public class FooterPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private final NavBarPanel navBarPanel;

	public FooterPanel(final ResourceBundle labels) {
		setLayout(new BorderLayout());
		add(new FramesPerSecondPanel(labels), BorderLayout.WEST);
		navBarPanel = new NavBarPanel();
		add(navBarPanel, BorderLayout.EAST);
	}

	public final NavBarPanel getNavBarPanel() {
		return navBarPanel;
	}
}
