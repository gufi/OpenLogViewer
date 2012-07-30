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

import org.diyefi.openlogviewer.OpenLogViewer;
import org.diyefi.openlogviewer.genericlog.GenericDataElement;
import org.diyefi.openlogviewer.genericlog.GenericLog;

public class MultiGraphLayeredPane extends JLayeredPane {
	private static final long serialVersionUID = 1L;

	private GenericLog genLog;
	private InfoPanel infoPanel;
	private int totalSplits;
	private int layer;

	public MultiGraphLayeredPane() {
		super();
		init();
	}

	private void init() {
		this.setOpaque(true);
		this.setLayout(null);
		this.setBackground(Color.BLACK);

		layer = 999;
		totalSplits = 1;

		infoPanel = new InfoPanel();
		infoPanel.setSize(600, 400);
		this.setLayer(infoPanel, new Integer(layer--));
		this.add(infoPanel);
	}

	public final void addGraph(final String header) {
		final boolean p = OpenLogViewer.getInstance().getEntireGraphingPanel().isPlaying();
		if (p) {
			OpenLogViewer.getInstance().getEntireGraphingPanel().pause();
		}
		boolean found = false;
		for (int i = 0; i < this.getComponentCount() && !found; i++) {
			if (this.getComponent(i) instanceof SingleGraphPanel) {
				final SingleGraphPanel gl = (SingleGraphPanel) this.getComponent(i);
				if (gl.getName().equals(header)) {
					found = true;
				}
			}
		}

		if (!found) {
			final SingleGraphPanel graph = new SingleGraphPanel();
			graph.setSize(this.getSize());
			graph.setName(header);
			this.setLayer(graph, new Integer(layer--));
			this.add(graph);
			this.addHierarchyBoundsListener(graph); // updates graph size automatically
			genLog.get(header).addPropertyChangeListener("Split", graph);
			graph.setData(genLog.get(header));
			graph.repaint();
		}

		this.revalidate();
		this.repaint();

		if (p) {
			OpenLogViewer.getInstance().getEntireGraphingPanel().play();
		}
	}

	public final void removeGraph(final String header) {
		final GenericDataElement temp = genLog.get(header);
		for (int i = 0; i < this.getComponentCount(); i++) {
			if (this.getComponent(i) instanceof SingleGraphPanel) {
				final SingleGraphPanel t = (SingleGraphPanel) this.getComponent(i);
				if (t.getData() == temp) {
					this.remove(t);
					this.removeHierarchyBoundsListener(t);
					this.revalidate();
					this.repaint();
				}
			}
		}
	}

	private void removeAllGraphs() {
		int componentIndex = 0;
		while (this.getComponentCount() > 1) {  // Leave InfoPanel in component count
			if (this.getComponent(componentIndex) instanceof SingleGraphPanel) {
				this.removeHierarchyBoundsListener((SingleGraphPanel) getComponent(componentIndex));
				this.remove(getComponent(componentIndex));
			} else {
				componentIndex++;
			}
		}
		repaint();
	}

	public final void setLog(final GenericLog log) {
		removeAllGraphs();
		genLog = log;
		infoPanel.setLog(genLog);
		repaint();
	}

	public final InfoPanel getInfoPanel() {
		return infoPanel;
	}

	public final void setColor(final String header, final Color newColor) {
		for (int i = 0; i < this.getComponentCount(); i++) {
			if (this.getComponent(i) instanceof SingleGraphPanel && this.getComponent(i).getName().equals(header)) {
				final SingleGraphPanel gl = (SingleGraphPanel) this.getComponent(i);
				gl.setColor(newColor);
			}
		}
	}

	public final int getTotalSplits() {
		return totalSplits;
	}

	public final void setTotalSplits(final int totalSplits) {
		if (totalSplits > 0) {
			this.totalSplits = totalSplits;
			for (int i = 0; i < this.getComponentCount(); i++) {
				if (this.getComponent(i) instanceof SingleGraphPanel) {
					final SingleGraphPanel gl = (SingleGraphPanel) this.getComponent(i);
					gl.sizeGraph();
				}
			}
		}
	}

	/**
	 * Graph total size
	 * @return GDE.size()
	 */
	public final int graphSize() {
		int availableData = 0;
		for (int i = 0; i < this.getComponentCount(); i++) {
			if (this.getComponent(i) instanceof SingleGraphPanel) {
				final SingleGraphPanel singleGraph = (SingleGraphPanel) this.getComponent(i);
				availableData = singleGraph.graphSize();
			}
		}
		return availableData;
	}
}
