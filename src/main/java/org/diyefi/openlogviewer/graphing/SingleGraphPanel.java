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
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.HierarchyBoundsListener;
import java.awt.event.HierarchyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JPanel;
import org.diyefi.openlogviewer.OpenLogViewer;
import org.diyefi.openlogviewer.genericlog.GenericDataElement;

/**
 * SingleGraphPanel is a JPanel that uses a transparent background.
 * The graph trace is drawn to this panel and used in conjunction with a JLayeredPane
 * to give the appearance of all the graph traces drawn together.
 *
 * This layer listens for window resizes and property changes.
 * @author Bryan Harris and Ben Fenner
 */
public class SingleGraphPanel extends JPanel implements HierarchyBoundsListener, PropertyChangeListener {
	private static final long serialVersionUID = 1L;

	private static final double GRAPH_TRACE_SIZE_AS_PERCENTAGE_OF_TOTAL_GRAPH_SIZE = 0.95;
	private GenericDataElement GDE;
	private double[] dataPointsToDisplay;
	private int availableDataRecords;

	public SingleGraphPanel() {
		this.setOpaque(false);
		this.setLayout(null);
		this.GDE = null;
		dataPointsToDisplay = null;
	}

	@Override
	public void ancestorMoved(final HierarchyEvent e) {
	}

	@Override
	public final void ancestorResized(final HierarchyEvent e) {
		if (e.getID() == HierarchyEvent.ANCESTOR_RESIZED) {
			sizeGraph();
		}
	}

	@Override
	public final void propertyChange(final PropertyChangeEvent evt) {
		if (evt.getPropertyName().equalsIgnoreCase("Split")) {
			sizeGraph();
		}
	}

	@Override
	public final void paint(final Graphics g) { // overridden paint because there will be no other painting other than this
		boolean zoomedOut = OpenLogViewer.getInstance().getEntireGraphingPanel().isZoomedOutBeyondOneToOne();
		if(zoomedOut){
			initGraphZoomedOut();
		} else{
			initGraphZoomed();
		}
		if (hasDataPointToDisplay()) {
			paintDataPoints(g);
		}
	}

	private void paintDataPoints(final Graphics g) {
		// Setup graphics stuff
		final Graphics2D g2d = (Graphics2D) g;
		g2d.setColor(GDE.getDisplayColor());

		// Setup current, previous and next graph trace data points
		boolean atGraphBeginning = false;
		boolean atGraphEnd = false;
		double leftOfTraceData = -Double.MAX_VALUE;
		double traceData = dataPointsToDisplay[0];
		double rightOfTraceData = -Double.MAX_VALUE;

		// Setup data point screen location stuff
		final boolean zoomedOut = OpenLogViewer.getInstance().getEntireGraphingPanel().isZoomedOutBeyondOneToOne();
		int zoom = OpenLogViewer.getInstance().getEntireGraphingPanel().getZoom();
		if(zoomedOut){
			zoom = 1;
		}
		final double graphPosition = OpenLogViewer.getInstance().getEntireGraphingPanel().getGraphPosition();
		final double offset = (graphPosition % 1) * zoom;
		int screenPositionXCoord = 0 - (int) offset;
		int screenPositionYCoord = getScreenPositionYCoord(traceData, GDE.getDisplayMinValue(), GDE.getDisplayMaxValue());
		int prevScreenPositionYCoord = Integer.MIN_VALUE;

		// Draw data points and trace lines
		for (int i = 0; i < dataPointsToDisplay.length; i++) {

			// Setup current, previous and next graph trace data points
			try{
				leftOfTraceData = dataPointsToDisplay[i - 1];
			} catch (ArrayIndexOutOfBoundsException e){
				leftOfTraceData = -Double.MAX_VALUE;
				atGraphBeginning = true;
			}
			traceData = dataPointsToDisplay[i];
			try{
				rightOfTraceData = dataPointsToDisplay[i + 1];
			} catch (ArrayIndexOutOfBoundsException e){
				rightOfTraceData = -Double.MAX_VALUE;
				atGraphEnd = true;
			}

			// Draw data point
			if (zoom > 5 && !zoomedOut && (traceData != leftOfTraceData || traceData != rightOfTraceData)) {
				g2d.fillOval(screenPositionXCoord - 2, screenPositionYCoord - 2, 4, 4);
			}

			// Draw graph trace line
			g2d.drawLine(screenPositionXCoord, screenPositionYCoord, screenPositionXCoord - zoom, prevScreenPositionYCoord);

			// Reconfigure data point screen location stuff
			prevScreenPositionYCoord = screenPositionYCoord;
			screenPositionYCoord = getScreenPositionYCoord(traceData, GDE.getDisplayMinValue(), GDE.getDisplayMaxValue());
			screenPositionXCoord += zoom;
		}
	}

	private int getScreenPositionYCoord(final Double traceData, final double minValue, final double maxValue) {
		int point = 0;
		final int height = (int) (this.getHeight() * GRAPH_TRACE_SIZE_AS_PERCENTAGE_OF_TOTAL_GRAPH_SIZE);
		if (maxValue != minValue) {
			point = (int) (height - (height * ((traceData - minValue) / (maxValue - minValue))));
		}
		return point;
	}

	private boolean hasDataPointToDisplay() {
		boolean result = false;
		if ((dataPointsToDisplay != null) && (dataPointsToDisplay.length > 0)) {
			result = true;
		}
		return result;
	}

	/**
	 * this is where the GDE is referenced and the graph gets initialized for the first time
	 * @param GDE
	 */
	public final void setData(final GenericDataElement GDE) {
		this.GDE = GDE;
		this.availableDataRecords = GDE.size() + 1; // Size is currently position, this will need cleaning up later, leave it to me.
		// The main thing is to take away 10 calls to the GDE per view on something that is fairly static and cache it internally
		sizeGraph();
	}

	public final GenericDataElement getData() {
		return GDE;
	}

	/**
	 * Used for InfoLayer to get the data from the single graphs for data under the mouse
	 *
	 * @param pointerDistanceFromCenter
	 * @return Double representation of info at the mouse cursor line which snaps to data points or null if no data under cursor
	 */
	public final Double getMouseInfo(final int cursorDistanceFromCenter) {
		boolean zoomedOut = OpenLogViewer.getInstance().getEntireGraphingPanel().isZoomedOutBeyondOneToOne();
		Double info = null;
		if(zoomedOut){
			info = getMouseInfoZoomedOut(cursorDistanceFromCenter);
		} else {
			info = getMouseInfoZoomed(cursorDistanceFromCenter);
		}
		return info;
	}

	/**
	 * Used for InfoLayer to get the data from the single graphs for data under the mouse when not zoomed out
	 *
	 * @param pointerDistanceFromCenter
	 * @return Double representation of info at the mouse cursor line which snaps to data points or null if no data under cursor
	 */
	private final Double getMouseInfoZoomed(final int cursorDistanceFromCenter){
		final double graphPosition = OpenLogViewer.getInstance().getEntireGraphingPanel().getGraphPosition();
		final int zoom = OpenLogViewer.getInstance().getEntireGraphingPanel().getZoom();
		final double offset = (graphPosition % 1) * zoom;
		final int cursorDistanceFromCenterPlusOffset = cursorDistanceFromCenter + (int) offset;
		double numSnapsFromCenter = ((double) cursorDistanceFromCenterPlusOffset / (double) zoom);
		numSnapsFromCenter = Math.round(numSnapsFromCenter);
		final int cursorPosition = (int) graphPosition + (int) numSnapsFromCenter;
		if ((cursorPosition >= 0) && (cursorPosition < availableDataRecords)) {
			return GDE.get(cursorPosition);
		} else {
			return null;
		}
	}

	/**
	 * Used for InfoLayer to get the data from the single graphs for data under the mouse when zoomed out
	 *
	 * @param pointerDistanceFromCenter
	 * @return Double representation of info at the mouse cursor line which snaps to data points or null if no data under cursor
	 */
	private final Double getMouseInfoZoomedOut(int cursorDistanceFromCenter){
		final double graphPosition = OpenLogViewer.getInstance().getEntireGraphingPanel().getGraphPosition();
		final int zoom = OpenLogViewer.getInstance().getEntireGraphingPanel().getZoom();
		final int cursorPosition = (int) graphPosition + (cursorDistanceFromCenter * zoom);
		if ((cursorPosition >= 0) && (cursorPosition < availableDataRecords)) {
			return GDE.get(cursorPosition);
		} else {
			return null;
		}
	}

	public final Color getColor() {
		return GDE.getDisplayColor();
	}

	public final void setColor(final Color c) {
		GDE.setDisplayColor(c);
	}

	/**
	 * initialize the graph any time you need to paint
	 */
	public final void initGraphZoomed() {
		if (GDE != null) {
			final int graphPosition = (int)OpenLogViewer.getInstance().getEntireGraphingPanel().getGraphPosition();
			int graphWindowWidth = OpenLogViewer.getInstance().getEntireGraphingPanel().getWidth();
			dataPointsToDisplay = new double[graphWindowWidth + 1];  // Add one data point for off-screen to the right
			final int zoom = OpenLogViewer.getInstance().getEntireGraphingPanel().getZoom();
			int numberOfPointsThatFitInDisplay = graphWindowWidth / zoom;
			numberOfPointsThatFitInDisplay += 1; // Add one data point for off-screen to the right

			// Setup data points.
			for (int i = 0; i < numberOfPointsThatFitInDisplay; i++) {
				if (i + graphPosition < availableDataRecords) {
					dataPointsToDisplay[i] = GDE.get(i + graphPosition);
				}
			}
		}
	}

	/**
	 * initialize the graph any time you need to paint
	 */
	public final void initGraphZoomedOut() {
		if (GDE != null) {
			final int graphPosition = (int)OpenLogViewer.getInstance().getEntireGraphingPanel().getGraphPosition();
			int graphWindowWidth = OpenLogViewer.getInstance().getEntireGraphingPanel().getWidth();
			dataPointsToDisplay = new double[graphWindowWidth + 1];  // Add one data point for off-screen to the right
			final int zoom = OpenLogViewer.getInstance().getEntireGraphingPanel().getZoom();
			final int numberOfPointsThatFitInDisplay = WIDTH * zoom;

			/*
			* Setup data points.
			*
			* The data point to display is calculated by taking the average of
			* the data point spread and comparing it to the previous calculated
			* data point. If the average is higher, then the highest value of
			* the data spread is used. If the average is lower, then the lowest
			* value of the data point spread is used.
			*
			* In other words, if the graph is trending upward, the peak is used.
			* If the graph is trending downward, the valley is used.
			* This keeps the peaks and valleys intact and the middle stuff is
			* lost. This maintains the general shape of the graph, and assumes
			* that local peaks and valleys are the most interesting parts of the
			* graph to display.
			*/
			double leftOfNewData = dataPointsToDisplay[0];
			for (int i = 0; i < numberOfPointsThatFitInDisplay; i+=zoom) {

				if (i < availableDataRecords) {
					double minData = Double.MAX_VALUE;
					double maxData = -Double.MAX_VALUE;
					double newData = 0.0;
					double acummulateData = 0.0;
					int divisor = 0;

					for (int j = 0; j < zoom; j++){
						if (i + j + graphPosition < availableDataRecords) {
							newData = GDE.get(i + j + graphPosition);
							acummulateData += newData;
							divisor++;
							if (newData < minData){
								minData = newData;
							}
							if (newData > maxData){
								maxData = newData;
							}
						}
					}
					double averageData = acummulateData / divisor;
					if (averageData > leftOfNewData){
						dataPointsToDisplay[i] = maxData;
						leftOfNewData = maxData;
					} else if (averageData < leftOfNewData){
						dataPointsToDisplay[i] = minData;
						leftOfNewData = minData;
					} else {
						dataPointsToDisplay[i] = averageData;
						leftOfNewData = averageData;
					}
				}
			}
		}
	}

	/**
	 * maintains the size of the graph when applying divisions
	 */
	public final void sizeGraph() {
		final MultiGraphLayeredPane lg = OpenLogViewer.getInstance().getMultiGraphLayeredPane();
		int wherePixel = 0;
		if (lg.getTotalSplits() > 1) {
			if (GDE.getSplitNumber() <= lg.getTotalSplits()) {
				wherePixel += lg.getHeight() / lg.getTotalSplits() * GDE.getSplitNumber() - (lg.getHeight() / lg.getTotalSplits());
			} else {
				wherePixel += lg.getHeight() / lg.getTotalSplits() * lg.getTotalSplits() - (lg.getHeight() / lg.getTotalSplits());
			}
		}

		this.setBounds(0, wherePixel, lg.getWidth(), lg.getHeight() / (lg.getTotalSplits()));
		final boolean zoomedOut = OpenLogViewer.getInstance().getEntireGraphingPanel().isZoomedOutBeyondOneToOne();
		if(zoomedOut){
			initGraphZoomedOut();
		} else {
			initGraphZoomed();
		}
	}

	/**
	 * Graph total size
	 * @return GDE.size()
	 */
	public final int graphSize() {
		return availableDataRecords;
	}
}
