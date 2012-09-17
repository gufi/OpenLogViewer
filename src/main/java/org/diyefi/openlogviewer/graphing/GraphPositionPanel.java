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
import java.math.BigDecimal;

import javax.swing.JPanel;

import org.diyefi.openlogviewer.FramesPerSecondPanel;
import org.diyefi.openlogviewer.OpenLogViewer;
import org.diyefi.openlogviewer.genericlog.GenericLog;
import org.diyefi.openlogviewer.utils.MathUtils;

public class GraphPositionPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private static final int TEXT_Y_OFFSET = 18;
	private static final double TENTHS_DISPLAY_THRESHOLD = 0.5;
	private static final double HUNDRETHS_DISPLAY_THRESHOLD = 0.05;
	private static final double INITIAL_MAJOR_GRADUATION_SPACING = 100.0;

	private GenericLog genLog;
	private final Color majorGraduationColor;
	private final Color positionDataColor;
	private final Color backgroundColor;
	private boolean[] validSnappingPositions;
	private final double[] graduationSpacingMultiplier;
	private double majorGraduationSpacing;

	public GraphPositionPanel() {
		setOpaque(true);
		setLayout(null);
		backgroundColor = Color.BLACK;

		majorGraduationColor = Color.GRAY;
		positionDataColor = majorGraduationColor;
		validSnappingPositions = new boolean[this.getWidth()];
		graduationSpacingMultiplier = new double[] {2.0, 2.5, 2.0};
		setGraduationSpacing();
	}

	@Override
	public final void paintComponent(final Graphics g) {
		super.paintComponent(g);

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
			if (genLog.getLogStatus() == GenericLog.LogState.LOG_LOADING) {
				paintPositionBar(g2d, false);
			} else if (genLog.getLogStatus() == GenericLog.LogState.LOG_LOADED) {
				final int zoom = OpenLogViewer.getInstance().getEntireGraphingPanel().getZoom();
				final boolean zoomedOut = OpenLogViewer.getInstance().getEntireGraphingPanel().isZoomedOutBeyondOneToOne();
				if (!zoomedOut || zoom == 1) {
					setupMouseCursorLineSnappingPositions();
				}
				paintPositionBar(g2d, zoomedOut);
				paintPositionData(g2d, zoomedOut);
			}
		}
		FramesPerSecondPanel.increaseFrameCount();
	}

	private void paintPositionBar(final Graphics2D g2d, final boolean zoomedOut) {
		final double graphPosition = OpenLogViewer.getInstance().getEntireGraphingPanel().getGraphPosition();
		final int zoom = OpenLogViewer.getInstance().getEntireGraphingPanel().getZoom();
		double offset = 0d;
		double margin = 0d;
		if (zoomedOut) {
			offset = majorGraduationSpacing / zoom;
			offset = Math.ceil(offset);
			margin = (1d / zoom) / 2d;
		} else {
			offset = majorGraduationSpacing * zoom;
			offset = Math.round(offset);
			margin = (1d / zoom) / 2d;
		}

		g2d.setColor(majorGraduationColor);

		// Find first position marker placement
		double nextPositionMarker = getFirstPositionMarkerPlacement();

		// Paint left to right
		double position = graphPosition - majorGraduationSpacing;

		// TODO It's ugly having the - on the left side of the cast,
		// but moving it *could* change behavior, so leaving it alone and
		// adding this instead!
		for (int i = -(int) offset; i < this.getWidth() + (int) offset; i++) {
			if (position >= nextPositionMarker - margin) {
				int xCoord = i;
				if (xCoord >= 0 && xCoord < validSnappingPositions.length && !validSnappingPositions[xCoord]) {
					if (xCoord + 1 < validSnappingPositions.length && validSnappingPositions[xCoord + 1]) {
						xCoord++;
					} else if (xCoord > 0 && validSnappingPositions[xCoord - 1]) {
						xCoord--;
					}
				}
				g2d.drawLine(xCoord, 0, xCoord, 6);
				nextPositionMarker += majorGraduationSpacing;
			}
			if (zoomedOut) {
				position += zoom;
			} else {
				position += (1d / zoom);
			}
		}
		g2d.drawLine(0, 0, this.getWidth(), 0);
	}

	private void paintPositionData(final Graphics2D g2d, final boolean zoomedOut) {
		final double graphPosition = OpenLogViewer.getInstance().getEntireGraphingPanel().getGraphPosition();
		final int zoom = OpenLogViewer.getInstance().getEntireGraphingPanel().getZoom();
		double offset = 0d;
		double margin = 0d;
		if (zoomedOut) {
			offset = majorGraduationSpacing / zoom;
			offset = Math.ceil(offset);
			margin = (1d / zoom) / 2d;
		} else {
			offset = majorGraduationSpacing * zoom;
			offset = Math.round(offset);
			margin = (1d / zoom) / 2d;
		}
		g2d.setColor(positionDataColor);
		final FontMetrics fm = this.getFontMetrics(this.getFont()); // For getting string width

		// Find first position marker placement
		double nextPositionMarker = getFirstPositionMarkerPlacement();

		// Paint left to right
		double position = graphPosition - majorGraduationSpacing;
		for (int i = -(int) offset; i < this.getWidth() + (int) offset; i++) { // TODO Ditto!
			if (position >= nextPositionMarker - margin) {
				int xCoord = i;
				if (xCoord >= 0 && xCoord < validSnappingPositions.length) {
					// Check this first to see if there is no need to modify xCoord.
					if (!validSnappingPositions[xCoord]) {
						if (xCoord + 1 < validSnappingPositions.length && validSnappingPositions[xCoord + 1]) {
							xCoord++;
						} else if (xCoord > 0 && validSnappingPositions[xCoord - 1]) {
							xCoord--;
						}
					}
				}
				String positionDataString = "";
				if (majorGraduationSpacing > TENTHS_DISPLAY_THRESHOLD) {
					final BigDecimal positionData = new BigDecimal(nextPositionMarker);
					positionDataString = positionData.toPlainString();
				} else if (majorGraduationSpacing > HUNDRETHS_DISPLAY_THRESHOLD) {
					positionDataString = MathUtils.roundDecimalPlaces(nextPositionMarker, 1);
				} else {
					positionDataString = MathUtils.roundDecimalPlaces(nextPositionMarker, 2);
				}
				final int stringWidth = fm.stringWidth(positionDataString);
				g2d.drawString(positionDataString, xCoord - (stringWidth / 2), TEXT_Y_OFFSET);

				nextPositionMarker += majorGraduationSpacing;
			}
			if (zoomedOut) {
				position += zoom;
			} else {
				position += (1.0 / zoom);
			}
		}
	}

	private double getFirstPositionMarkerPlacement() {
		final double graphPosition = OpenLogViewer.getInstance().getEntireGraphingPanel().getGraphPosition();

		double nextPositionMarker = 0d;
		if (graphPosition < 0d) {
			while (nextPositionMarker - graphPosition >= majorGraduationSpacing) {
				nextPositionMarker -= majorGraduationSpacing;
			}
		} else {
			while (nextPositionMarker - graphPosition < 0.0) {
				nextPositionMarker += majorGraduationSpacing;
			}
		}

		nextPositionMarker -= majorGraduationSpacing; // Start with one graduation off-screen to the left
		return nextPositionMarker;
	}

	private void setupMouseCursorLineSnappingPositions() {
		validSnappingPositions = new boolean[this.getWidth()];
		final double graphPosition = OpenLogViewer.getInstance().getEntireGraphingPanel().getGraphPosition();
		final int zoom = OpenLogViewer.getInstance().getEntireGraphingPanel().getZoom();
		final MultiGraphLayeredPane multiGraph = OpenLogViewer.getInstance().getEntireGraphingPanel().getMultiGraphLayeredPane();
		final int availableData = (multiGraph.graphSize() - 1) * zoom;
		long count = Math.round(graphPosition * zoom);

		// Fill array with valid snapping points from left to right
		for (int i = 0; i < this.getWidth(); i++) {
			if (count < -1 || count > availableData + 1 || count % zoom == 0) {
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

		majorGraduationSpacing = INITIAL_MAJOR_GRADUATION_SPACING;
		final int count = (int) (Math.log((double) zoom) / Math.log(2.0));  // Base-2 logarithm of zoom

		if (zoomedOut) {
			for (int i = 0; i < count; i++) {
				majorGraduationSpacing *= graduationSpacingMultiplier[i % 3];
			}
		} else {
			for (int i = 0; i < count; i++) {
				majorGraduationSpacing /= graduationSpacingMultiplier[i % 3];
			}
		}
	}

	public final int getBestSnappingPosition(final int xMouseCoord) {
		int bestPosition = xMouseCoord;
		if (xMouseCoord < validSnappingPositions.length && !validSnappingPositions[xMouseCoord]) {
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
