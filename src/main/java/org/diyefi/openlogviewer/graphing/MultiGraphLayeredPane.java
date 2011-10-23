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
package org.diyefi.openlogviewer.graphing;

import java.awt.Color;
import javax.swing.JLayeredPane;

import org.diyefi.openlogviewer.OpenLogViewerApp;
import org.diyefi.openlogviewer.genericlog.GenericDataElement;
import org.diyefi.openlogviewer.genericlog.GenericLog;

public class MultiGraphLayeredPane extends JLayeredPane {
	private GenericLog genLog;
	private InfoPanel infoPanel;
	private int totalSplits;
	private static final long serialVersionUID = 1213851792900715691L;

	public MultiGraphLayeredPane() {
		super();
		init();
	}

	private void init() {
		genLog = new GenericLog();
		totalSplits = 1;
		infoPanel = new InfoPanel();
		infoPanel.setLog(genLog);
		infoPanel.setSize(400, 600);
		this.setLayer(infoPanel, 99);
		this.setBackground(Color.BLACK);
		this.setOpaque(true);
		this.add(infoPanel);
	}

	public void addGraph(String header) {
		boolean p = OpenLogViewerApp.getInstance().getEntireGraphingPanel().isPlaying();
		if (p) {
			OpenLogViewerApp.getInstance().getEntireGraphingPanel().pause();
		}
		boolean found = false;
		for (int i = 0; i < this.getComponentCount() && !found; i++) {
			if (this.getComponent(i) instanceof SingleGraphPanel) {
				SingleGraphPanel gl = (SingleGraphPanel) this.getComponent(i);
				if (gl.getName().equals(header)) {
					found = true;
				}
			}
		}
		if (!found) {
			SingleGraphPanel graph = new SingleGraphPanel();
			graph.setSize(this.getSize());
			graph.setName(header);
			this.add(graph);
			this.addHierarchyBoundsListener(graph); // updates graph size automatically
			genLog.get(header).addPropertyChangeListener("Split", graph);
			graph.setData(genLog.get(header));
			OpenLogViewerApp.getInstance().getEntireGraphingPanel().setGraphPositionMax();
		}

		if (p) {
			OpenLogViewerApp.getInstance().getEntireGraphingPanel().play();
		}
	}

	public boolean removeGraph(String header) {
		GenericDataElement temp = genLog.get(header);
		for (int i = 0; i < this.getComponentCount(); i++) {
			if (this.getComponent(i) instanceof SingleGraphPanel) {
				SingleGraphPanel t = (SingleGraphPanel) this.getComponent(i);
				if (t.getData() == temp) {
					this.remove(t);
					this.removeHierarchyBoundsListener(t);
					return true;
				}
			}
		}
		return false;
	}

	private void removeAllGraphs() {
		for (int i = 0; this.getComponentCount() > 1;) {  //Leave InfoPanel in component count
			if (this.getComponent(i) instanceof SingleGraphPanel) {
				this.removeHierarchyBoundsListener((SingleGraphPanel) getComponent(i));
				this.remove(getComponent(i));
			} else {
				i++;
			}
		}
		repaint();
	}

	public void setLog(GenericLog log) {
		removeAllGraphs();
		genLog = log;
		infoPanel.setLog(genLog);
		repaint();
	}

	public InfoPanel getInfoPanel() {
		return infoPanel;
	}

	public void setColor(String header, Color newColor) {
		for (int i = 0; i < this.getComponentCount(); i++) {
			if (this.getComponent(i) instanceof SingleGraphPanel && this.getComponent(i).getName().equals(header)) {
				SingleGraphPanel gl = (SingleGraphPanel) this.getComponent(i);
				gl.setColor(newColor);
			}
		}
	}

	public int getTotalSplits() {
		return totalSplits;
	}

	public void setTotalSplits(int totalSplits) {
		if (totalSplits > 0) {
			this.totalSplits = totalSplits;
			for (int i = 0; i < this.getComponentCount(); i++) {
				if (this.getComponent(i) instanceof SingleGraphPanel) {
					SingleGraphPanel gl = (SingleGraphPanel) this.getComponent(i);
					gl.sizeGraph();
				}
			}
		}
	}
}
