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
import java.util.ResourceBundle;

import javax.swing.JLayeredPane;

import org.diyefi.openlogviewer.Keys;
import org.diyefi.openlogviewer.OpenLogViewer;
import org.diyefi.openlogviewer.genericlog.GenericDataElement;
import org.diyefi.openlogviewer.genericlog.GenericLog;

public class MultiGraphLayeredPane extends JLayeredPane {
	private static final long serialVersionUID = 1L;
	private static final int BOTTOM_LAYER = 999;  // Not really the bottom, but plenty far enough
	private static final int PANEL_WIDTH = 600;
	private static final int PANEL_HEIGHT = 400;

	private GenericLog genLog;
	private final InfoPanel infoPanel;
	private int trackCount;
	private int layer;

	public MultiGraphLayeredPane(final ResourceBundle labels) {
		setOpaque(true);
		setLayout(null);
		setBackground(Color.BLACK);

		layer = BOTTOM_LAYER;
		trackCount = 1;

		infoPanel = new InfoPanel(labels);
		infoPanel.setSize(PANEL_WIDTH, PANEL_HEIGHT);
		setLayer(infoPanel, layer);
		layer--;
		add(infoPanel);
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
			this.setLayer(graph, layer);
			layer--;
			this.add(graph);
			this.addHierarchyBoundsListener(graph); // updates graph size automatically
			genLog.get(header).addPropertyChangeListener(Keys.SPLIT, graph);
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
				final SingleGraphPanel sgp = (SingleGraphPanel) getComponent(componentIndex);
				this.removeHierarchyBoundsListener(sgp);
				sgp.getData().setDisplayColor(null);
				this.remove(sgp);
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

	public final int getTrackCount() {
		return trackCount;
	}

	public final void setTrackCount(final int newTrackCount) {
		trackCount = newTrackCount;
		for (int i = 0; i < getComponentCount(); i++) {
			if (getComponent(i) instanceof SingleGraphPanel) {
				final SingleGraphPanel gl = (SingleGraphPanel) getComponent(i);
				gl.sizeGraph();
			}
		}
	}

	/**
	 * Graph total size
	 * @return GDE.size()
	 */
	public final int graphSize() {
		int availableData = 0;
		for (int i = 0; i < getComponentCount(); i++) {
			if (getComponent(i) instanceof SingleGraphPanel) {
				final SingleGraphPanel singleGraph = (SingleGraphPanel) getComponent(i);
				availableData = singleGraph.graphSize();
			}
		}
		return availableData;
	}
}
