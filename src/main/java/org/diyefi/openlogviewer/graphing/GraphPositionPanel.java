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
import java.math.MathContext;

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
			if (genLog.getLogStatus() == GenericLog.LogState.LOG_LOADING) {
				paintPositionBar(g2d, false);
			} else if (genLog.getLogStatus() == GenericLog.LogState.LOG_LOADED) {
				final int zoom = OpenLogViewer.getInstance().getEntireGraphingPanel().getZoom();
				final boolean zoomedOut = OpenLogViewer.getInstance().getEntireGraphingPanel().isZoomedOutBeyondOneToOne();
				if(!zoomedOut || zoom == 1){
					setupMouseCursorLineSnappingPositions();
				}
				paintPositionBar(g2d, zoomedOut);
				paintPositionData(g2d, zoomedOut);

			}
		}
	}

	private void paintPositionBar(final Graphics2D g2d, final boolean zoomedOut) {
		final double graphPosition = OpenLogViewer.getInstance().getEntireGraphingPanel().getGraphPosition();
		final int zoom = OpenLogViewer.getInstance().getEntireGraphingPanel().getZoom();
		double offset = 0d;
		double margin = 0d;
		if(zoomedOut){
			offset = majorGraduationSpacing / zoom;
			offset = Math.ceil(offset);
			margin = (1d / zoom) / 2d;
		} else {
			offset = majorGraduationSpacing * zoom;
			offset = Math.round(offset);
			margin = (1d / zoom) / 2d;
		}

		g2d.setColor(majorGraduationColor);

		//Find first position marker placement
		double nextPositionMarker = getFirstPositionMarkerPlacement();

		//Paint left to right
		double position = graphPosition - majorGraduationSpacing;
		for (int i = -(int)offset; i < this.getWidth() + (int)offset; i++) {
			if (position >= nextPositionMarker - margin){
				int xCoord = i;
				if(xCoord >= 0 && xCoord < validSnappingPositions.length){
					if (validSnappingPositions[xCoord]){
						//Check this first to see if there is no need to modify xCoord.
					} else if (xCoord + 1 < validSnappingPositions.length && validSnappingPositions[xCoord + 1]){
						xCoord ++;
					} else if (xCoord > 0 && validSnappingPositions[xCoord - 1]){
						xCoord --;
					}
				}
				g2d.drawLine(xCoord, 0, xCoord, 6);
				nextPositionMarker += majorGraduationSpacing;
			}
			if(zoomedOut){
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
		if(zoomedOut){
			offset = majorGraduationSpacing / zoom;
			offset = Math.ceil(offset);
			margin = (1d / zoom) / 2d;
		} else {
			offset = majorGraduationSpacing * zoom;
			offset = Math.round(offset);
			margin = (1d / zoom) / 2d;
		}
		g2d.setColor(positionDataColor);
		FontMetrics fm = this.getFontMetrics(this.getFont());  //For getting string width

		//Find first position marker placement
		double nextPositionMarker = getFirstPositionMarkerPlacement();

		//Paint left to right
		double position = graphPosition - majorGraduationSpacing;
		for (int i = -(int)offset; i < this.getWidth() + (int)offset; i++) {
			if (position >= nextPositionMarker - margin){
				int xCoord = i;
				if(xCoord >= 0 && xCoord < validSnappingPositions.length){
					if (validSnappingPositions[xCoord]){
						//Check this first to see if there is no need to modify xCoord.
					} else if (xCoord + 1 < validSnappingPositions.length && validSnappingPositions[xCoord + 1]){
						xCoord ++;
					} else if (xCoord > 0 && validSnappingPositions[xCoord - 1]){
						xCoord --;
					}
				}
				String positionDataString = "";
				BigDecimal positionData = new BigDecimal(nextPositionMarker);
				if(majorGraduationSpacing > 0.5){
					positionDataString = positionData.toPlainString();
				} else {
					positionDataString = roundDecimalsOnlyToTwoSignificantFigures(positionData);
				}
				int stringWidth = fm.stringWidth(positionDataString);
				g2d.drawString(positionDataString, xCoord - (stringWidth / 2), 18);

				nextPositionMarker += majorGraduationSpacing;
			}
			if(zoomedOut){
				position += zoom;
			} else {
				position += (1.0 / zoom);
			}
		}
	}

	private final double getFirstPositionMarkerPlacement(){
		final double graphPosition = OpenLogViewer.getInstance().getEntireGraphingPanel().getGraphPosition();

		double nextPositionMarker = 0d;
		if(graphPosition < 0d){
			while (nextPositionMarker - graphPosition >= majorGraduationSpacing){
				nextPositionMarker -= majorGraduationSpacing;
			}
		} else {
			while (nextPositionMarker - graphPosition < 0.0){
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
		MultiGraphLayeredPane multiGraph = OpenLogViewer.getInstance().getEntireGraphingPanel().getMultiGraphLayeredPane();
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

		majorGraduationSpacing = 100.0;
		int count = (int)(Math.log((double)zoom) / Math.log(2.0));  // Base-2 logarithm of zoom

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
		int bestPosition = xMouseCoord;
		if (!validSnappingPositions[xMouseCoord]) {
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

	/**
	* Use this if you want to avoid BigDecimal in the future:
	* http://stackoverflow.com/questions/202302/rounding-to-an-arbitrary-number-of-significant-digits
	*
	* Update: That algorithm has now been implemented in MathUtils.roundToSignificantFigures()
	*
	* @param BigDecimal input - The BigDecimal you're interested in rounding.
	* @return String - A string representation of input rounded to two significant figures to the right of the decimal.
	*/

	private final String roundDecimalsOnlyToTwoSignificantFigures(final BigDecimal input) {
		String result = "";
		int indexOfMostSignificantDigit = 0;

		// Find out if negative or not.
		result = input.toPlainString();
		if(result.substring(0, 1).equalsIgnoreCase("-")){
			indexOfMostSignificantDigit++;
		}

		// If there are decimals, then count the number of whole integers and leading zeros
		// to find out what rounding precision is needed to end up with two significant
		// figures for the decimal portion only.
		int amountOfIntegerDigits = 0;
		int amountOfLeadingZerosInDecimalPortion = 0;
		final int indexOfDecimalPoint = result.indexOf('.');
		if (indexOfDecimalPoint != -1){
			amountOfIntegerDigits = result.substring(indexOfMostSignificantDigit, indexOfDecimalPoint).length();
			boolean done = false;
			for (int i = indexOfDecimalPoint + 1; i < result.length() && !done; i++){
				if (result.substring(i, i).equalsIgnoreCase("0")){
					amountOfLeadingZerosInDecimalPortion++;
				} else {
					done = true;
				}
			}

		} else {
			amountOfIntegerDigits = result.substring(indexOfMostSignificantDigit).length();

		}
		final int amountOfDesiredDecimalDigits = amountOfLeadingZerosInDecimalPortion + 2;
		final int sigFigs = amountOfIntegerDigits + amountOfDesiredDecimalDigits;
		BigDecimal roundedInput = input.round(new MathContext(sigFigs));
		roundedInput = roundedInput.stripTrailingZeros();
		result = roundedInput.toPlainString();

		// Add on decimal point and trailing zero if doesn't exist.
		if (result.indexOf('.') == -1){
			result = result + ".0";
		}

		// Check for result extremely close to zero.
		if(result.length() > 16){
			if (result.substring(0, 16).equalsIgnoreCase("-0.0000000000000")
					|| result.substring(0, 15).equalsIgnoreCase("0.0000000000000")){
				result = "0.0";
			}
		}

		return result;
	}
}
