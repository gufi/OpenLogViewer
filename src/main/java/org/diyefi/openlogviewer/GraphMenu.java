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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

public class GraphMenu extends JMenu {
	private static final long serialVersionUID = 894830948284092834L;

	private JMenuItem optionPaneItem;

	public GraphMenu() {
		super();
		initMenu();
	}

	private void initMenu() {
		this.setText("Graphing");
		this.setName("Graphing Menu");

		optionPaneItem = new JMenuItem();
		optionPaneItem.setText("Option Pane");
		optionPaneItem.setName("Option Pane");
		optionPaneItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!OpenLogViewerApp.getInstance().getOptionFrame().isVisible()) {
					OpenLogViewerApp.getInstance().getOptionFrame().setVisible(true);
				}
				OpenLogViewerApp.getInstance().getOptionFrame().setAlwaysOnTop(true);
				OpenLogViewerApp.getInstance().getOptionFrame().setAlwaysOnTop(false);
			}
		});

		this.add(optionPaneItem);
	}
}

/* 22 October 2011 Left Gufi's menu in place for future dev's enjoyment.
 *
 * 5 February 2011 meal for the night DO NOT EDIT MENU!
 * Sesame chicken alacarte
 * chicken lo mein alacarte
 * orange chicken x2
 */
