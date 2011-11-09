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
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import org.diyefi.openlogviewer.OpenLogViewer;
import org.diyefi.openlogviewer.genericlog.GenericLog;

public class GraphPositionPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private GenericLog genLog;
	private Color majorGraduationColor;
	private Color positionDataColor;
	private Color backgroundColor;
	private boolean[] validSnappingPositions;
	private double[] graduationSpacingMultiplier;
	private double majorGraduationSpacing;

	public GraphPositionPanel() {
		super();
		init();
	}

	private void init() {
		this.setOpaque(true);
		this.setLayout(null);
		majorGraduationColor = Color.GRAY;
		positionDataColor = majorGraduationColor;
		backgroundColor = Color.BLACK;
		validSnappingPositions = new boolean[this.getWidth()];
		graduationSpacingMultiplier = new double[] {2.0, 2.5, 2.0};
		setGraduationSpacing();
	}

	@Override
	public final void paint(final Graphics g) { // override paint because there will be no components in this pane
		if (!this.getSize().equals(this.getParent().getSize())) {
			this.setSize(this.getParent().getSize());
		}
		setGraduationSpacing();
		final Graphics2D g2d = (Graphics2D) g;
		g2d.setColor(backgroundColor);
		g2d.fillRect(0, 0, this.getWidth(), this.getHeight());
		if (genLog ==  null) {
			paintPositionBar(g2d, false);
		} else {
			if (genLog.getLogStatus() == GenericLog.LOG_LOADING) {
				paintPositionBar(g2d, false);
			} else if (genLog.getLogStatus() == GenericLog.LOG_LOADED) {
				final int zoom = OpenLogViewer.getInstance().getEntireGraphingPanel().getZoom();
				final boolean zoomedOut = OpenLogViewer.getInstance().getEntireGraphingPanel().isZoomedOutBeyondOneToOne();
				paintPositionBar(g2d, zoomedOut);
				paintPositionData(g2d, zoomedOut);
				if(!zoomedOut && zoom > 1){
					setupMouseCursorLineSnappingPositions();
				}
			}
		}
	}

	private void paintPositionBar(final Graphics2D g2d, final boolean zoomedOut) {
		final double graphPosition = OpenLogViewer.getInstance().getEntireGraphingPanel().getGraphPosition();
		final int zoom = OpenLogViewer.getInstance().getEntireGraphingPanel().getZoom();
		g2d.setColor(majorGraduationColor);

		//Find first position marker placement
		double nextPositionMarker = 0.0;
		if(graphPosition < 0){
			while (nextPositionMarker - graphPosition > majorGraduationSpacing){
				nextPositionMarker -= majorGraduationSpacing;
			}
		} else {
			while (nextPositionMarker - graphPosition < 0.0){
				nextPositionMarker += majorGraduationSpacing;
			}
		}

		//Paint left to right
		double position = graphPosition;
		for (int i = 0; i < this.getWidth(); i++) {
			if (position >= nextPositionMarker){
				g2d.drawLine(i, 0, i, 6);
				nextPositionMarker += majorGraduationSpacing;
			}
			if(zoomedOut){
				position += zoom;
			} else {
				position += (1.0 / zoom);
			}
		}
		g2d.drawLine(0, 0, this.getWidth(), 0);
	}

	private void paintPositionData(final Graphics2D g2d, final boolean zoomedOut) {
		final double graphPosition = OpenLogViewer.getInstance().getEntireGraphingPanel().getGraphPosition();
		final int zoom = OpenLogViewer.getInstance().getEntireGraphingPanel().getZoom();
		g2d.setColor(positionDataColor);
		FontMetrics fm = this.getFontMetrics(this.getFont());  //For getting string width

		//Find first position marker placement
		double nextPositionMarker = 0.0;
		if(graphPosition < 0){
			while (nextPositionMarker - graphPosition > majorGraduationSpacing){
				nextPositionMarker -= majorGraduationSpacing;
			}
		} else {
			while (nextPositionMarker - graphPosition < 0.0){
				nextPositionMarker += majorGraduationSpacing;
			}
		}

		//Paint left to right
		double position = graphPosition;

		for (int i = 0; i < this.getWidth(); i++) {
			if (position >= nextPositionMarker){
				String positionDataString =  Double.toString(nextPositionMarker);
				if(majorGraduationSpacing > 0.5){
					positionDataString = positionDataString.substring(0, positionDataString.length() - 2);
				}

				int stringWidth = fm.stringWidth(positionDataString);
				g2d.drawString(positionDataString, i - (stringWidth / 2), 18);

				nextPositionMarker += majorGraduationSpacing;
			}
			if(zoomedOut){
				position += zoom;
			} else {
				position += (1.0 / zoom);
			}
		}
	}

	private void setupMouseCursorLineSnappingPositions() {
		validSnappingPositions = new boolean[this.getWidth()];
		final double graphPosition = OpenLogViewer.getInstance().getEntireGraphingPanel().getGraphPosition();
		final int zoom = OpenLogViewer.getInstance().getEntireGraphingPanel().getZoom();
		double count = Math.round(graphPosition * zoom);

		// Fill array with valid snapping points from left to right
		for (int i = 0; i < this.getWidth(); i++) {
			if (count % zoom == 0) {
				validSnappingPositions[i] = true;
			}
			count++;
		}
	}

	public final void setLog(final GenericLog log) {
		genLog = log;
		repaint();
	}

	private void setGraduationSpacing() {
		int zoom = 1;
		boolean zoomedOut = false;
		if (OpenLogViewer.getInstance() != null) {
			zoom = OpenLogViewer.getInstance().getEntireGraphingPanel().getZoom();
			zoomedOut = OpenLogViewer.getInstance().getEntireGraphingPanel().isZoomedOutBeyondOneToOne();
		}

		majorGraduationSpacing = 100.0;
		int count = (int)(Math.log((double)zoom) / Math.log(2.0));  // Base-2 logorithm of zoom

		if (zoomedOut){
			for (int i = 0; i < count; i++){
				majorGraduationSpacing *= graduationSpacingMultiplier[i % 3];
			}
		} else {
			for (int i = 0; i < count; i++){
				majorGraduationSpacing /= graduationSpacingMultiplier[i % 3];
			}
		}
	}

	public final int getBestSnappingPosition(final int xMouseCoord) {
		int bestPosition = 0;
		if (validSnappingPositions[xMouseCoord]) {
			bestPosition = xMouseCoord;
		} else {
			boolean found = false;
			final int startPosition = xMouseCoord;
			for (int distance = 1; !found; distance++) {
				final int next = startPosition + distance;
				final int prev = startPosition - distance;
				if (next > validSnappingPositions.length - 1 || prev < 0) {
					bestPosition = xMouseCoord;
					found = true;
				} else if (validSnappingPositions[next]) {
					bestPosition = next;
					found = true;
				} else if (validSnappingPositions[prev]) {
					bestPosition = prev;
					found = true;
				}
			}
		}
		return bestPosition;
	}
}
