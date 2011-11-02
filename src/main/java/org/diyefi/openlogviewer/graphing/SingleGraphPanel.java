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
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import javax.swing.JPanel;
import org.diyefi.openlogviewer.OpenLogViewerApp;
import org.diyefi.openlogviewer.genericlog.GenericDataElement;

/**
 *  GraphLayer is a JPanel that uses a transparent background.
 * the graph is drawn to this panel and used in conjunction with a JLayeredPane
 * to give the appearance of the graphs drawn together.
 *
 * this Layer listens for window resizes and property changes
 * @author Bryan Harris
 */
public class SingleGraphPanel extends JPanel implements HierarchyBoundsListener, PropertyChangeListener {
	private static final long serialVersionUID = 1L;

	private static final double GRAPH_TRACE_SIZE_AS_PERCENTAGE_OF_TOTAL_GRAPH_SIZE = 0.95;
	private GenericDataElement GDE;
	private List<Double> leftDataPointsToDisplay;
	private List<Double> rightDataPointsToDisplay;
	private int length;

	public SingleGraphPanel() {
		this.setOpaque(false);
		this.setLayout(null);
		this.GDE = null;
		leftDataPointsToDisplay = new ArrayList<Double>();
		rightDataPointsToDisplay = new ArrayList<Double>();
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
		boolean zoomedOut = OpenLogViewerApp.getInstance().getEntireGraphingPanel().isZoomedOutBeyondOneToOne();
		if(zoomedOut){
			initGraphZoomedOut();
			if (hasDataPointToDisplay()) {
				paintLeftDataPoints(g);
				paintRightDataPoints(g);
			}
		} else{
			initGraphZoomed();
			if (hasDataPointToDisplay()) {
				// TODO Candidates for refactoring into single class, maybe. Did not look in detail.
				// Ben says they should be left alone.
				paintLeftDataPoints(g);
				paintRightDataPoints(g);
			}
		}
	}

	private void paintLeftDataPoints(final Graphics g) {
		// Setup graphics stuff
		final Graphics2D g2d = (Graphics2D) g;
		g2d.setColor(GDE.getDisplayColor());

		// Setup current, previous and next graph trace data points
		boolean atGraphBeginning = false;
		boolean firstDataPoint = true;
		final ListIterator<Double> it = leftDataPointsToDisplay.listIterator();
		Double traceData = it.next();
		Double leftOfTraceData = null;
		Double rightOfTraceData = null;

		if (it.hasPrevious()) {
			rightOfTraceData = it.previous();
			it.next();
		} else {
			if (rightDataPointsToDisplay.size() > 1) {
				rightOfTraceData = rightDataPointsToDisplay.get(1);
			} // else at graph end
		}
		if (it.hasNext()) {
			leftOfTraceData = it.next();
			it.previous();
		} else {
			atGraphBeginning = true;
		}

		// Setup data point screen location stuff
		final boolean zoomedOut = OpenLogViewerApp.getInstance().getEntireGraphingPanel().isZoomedOutBeyondOneToOne();
		int zoom = OpenLogViewerApp.getInstance().getEntireGraphingPanel().getZoom();
		if(zoomedOut){
			zoom = 1;
		}
		final double graphPosition = OpenLogViewerApp.getInstance().getEntireGraphingPanel().getGraphPosition();
		final double offset = (graphPosition % 1) * zoom;
		int screenPositionXCoord = (this.getWidth() / 2) - (int) offset;
		int screenPositionYCoord = getScreenPositionYCoord(traceData, GDE.getDisplayMinValue(), GDE.getDisplayMaxValue());
		int prevScreenPositionYCoord = -1;

		// Draw data points and trace lines
		while (it.hasNext()) {

			// Draw data point
			if (zoom > 5 && !zoomedOut && (!traceData.equals(leftOfTraceData) || !traceData.equals(rightOfTraceData))) {
				g2d.fillOval(screenPositionXCoord - 2, screenPositionYCoord - 2, 4, 4);
			}

			// Draw graph trace line
			if (!firstDataPoint) {
				g2d.drawLine(screenPositionXCoord, screenPositionYCoord, screenPositionXCoord + zoom, prevScreenPositionYCoord);
			}

			// Move to next trace data in the list
			rightOfTraceData = traceData;
			traceData = it.next();
			if (it.hasNext()) {
				leftOfTraceData = it.next();
				it.previous();
			} else {
				atGraphBeginning = true;
			}

			// Reconfigure data point screen location stuff
			prevScreenPositionYCoord = screenPositionYCoord;
			screenPositionYCoord = getScreenPositionYCoord(traceData, GDE.getDisplayMinValue(), GDE.getDisplayMaxValue());
			screenPositionXCoord -= zoom;
			firstDataPoint = false;
		}

		// Always draw one last data point and trace line at the end of the list. This is usually off screen but can also be the beginning of the graph.
		if (atGraphBeginning) {
			screenPositionYCoord = getScreenPositionYCoord(traceData, GDE.getDisplayMinValue(), GDE.getDisplayMaxValue());

			// Draw data point
			if (zoom > 5 && !zoomedOut) {
				g2d.fillOval(screenPositionXCoord - 2, screenPositionYCoord - 2, 4, 4);
			}

			// Draw graph trace line
			if (!firstDataPoint) {
				g2d.drawLine(screenPositionXCoord, screenPositionYCoord, screenPositionXCoord + zoom, prevScreenPositionYCoord);
			}
		}
	}

	private void paintRightDataPoints(final Graphics g) {
		// Setup graphics stuff
		final Graphics2D g2d = (Graphics2D) g;
		g2d.setColor(GDE.getDisplayColor());

		// Setup current, previous and next graph trace data points
		boolean atGraphEnd = false;
		boolean firstDataPoint = true;
		final ListIterator<Double> it = rightDataPointsToDisplay.listIterator();
		Double traceData = it.next();
		Double leftOfTraceData = null;
		Double rightOfTraceData = null;

		if (it.hasPrevious()) {
			leftOfTraceData = it.previous();
			it.next();
		} else {
			if (leftDataPointsToDisplay.size() > 1) {
				leftOfTraceData = leftDataPointsToDisplay.get(1);
			} // else at graph beginning
		}

		if (it.hasNext()) {
			rightOfTraceData = it.next();
			it.previous();
		} else {
			atGraphEnd = true;
		}

		// Setup data point screen location stuff
		final boolean zoomedOut = OpenLogViewerApp.getInstance().getEntireGraphingPanel().isZoomedOutBeyondOneToOne();
		int zoom = OpenLogViewerApp.getInstance().getEntireGraphingPanel().getZoom();
		if(zoomedOut){
			zoom = 1;
		}
		final double graphPosition = OpenLogViewerApp.getInstance().getEntireGraphingPanel().getGraphPosition();
		final double offset = (graphPosition % 1) * zoom;
		int screenPositionXCoord = (this.getWidth() / 2) - (int) offset;
		int screenPositionYCoord = getScreenPositionYCoord(traceData, GDE.getDisplayMinValue(), GDE.getDisplayMaxValue());
		int prevScreenPositionYCoord = -1;

		// Draw data points and trace lines
		while (it.hasNext()) {

			// Draw data point
			if (zoom > 5 && !zoomedOut && (!traceData.equals(leftOfTraceData) || !traceData.equals(rightOfTraceData))) {
				g2d.fillOval(screenPositionXCoord - 2, screenPositionYCoord - 2, 4, 4);
			}

			// Draw graph trace line
			if (!firstDataPoint) {
				g2d.drawLine(screenPositionXCoord, screenPositionYCoord, screenPositionXCoord - zoom, prevScreenPositionYCoord);
			}

			// Move to next trace data in the list
			leftOfTraceData = traceData;
			traceData = it.next();
			if (it.hasNext()) {
				rightOfTraceData = it.next();
				it.previous();
			} else {
				atGraphEnd = true;
			}

			// Reconfigure data point screen location stuff
			prevScreenPositionYCoord = screenPositionYCoord;
			screenPositionYCoord = getScreenPositionYCoord(traceData, GDE.getDisplayMinValue(), GDE.getDisplayMaxValue());
			screenPositionXCoord += zoom;
			firstDataPoint = false;
		}

		// Draw one last data point (if needed) and trace line (always) at the end of the list. This is usually off screen but can also be the end of the graph.
		if (atGraphEnd) {
			screenPositionYCoord = getScreenPositionYCoord(traceData, GDE.getDisplayMinValue(), GDE.getDisplayMaxValue());
			// Draw data point
			if (zoom > 5 && !zoomedOut && !traceData.equals(leftOfTraceData)) {
				g2d.fillOval(screenPositionXCoord - 2, screenPositionYCoord - 2, 4, 4);
			}
			// Draw graph trace line
			if (!firstDataPoint) {
				g2d.drawLine(screenPositionXCoord, screenPositionYCoord, screenPositionXCoord - zoom, prevScreenPositionYCoord);
			}
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
		if ((leftDataPointsToDisplay != null) && (leftDataPointsToDisplay.size() > 0)) {
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
		this.length = GDE.size() + 1; // Size is currently position, this will need cleaning up later, leave it to me.
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
		boolean zoomedOut = OpenLogViewerApp.getInstance().getEntireGraphingPanel().isZoomedOutBeyondOneToOne();
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
		final double graphPosition = OpenLogViewerApp.getInstance().getEntireGraphingPanel().getGraphPosition();
		final int zoom = OpenLogViewerApp.getInstance().getEntireGraphingPanel().getZoom();
		final double offset = (graphPosition % 1) * zoom;
		final int cursorDistanceFromCenterPlusOffset = cursorDistanceFromCenter + (int) offset;
		double numSnapsFromCenter = ((double) cursorDistanceFromCenterPlusOffset / (double) zoom);
		numSnapsFromCenter = Math.round(numSnapsFromCenter);
		final int cursorPosition = (int) graphPosition + (int) numSnapsFromCenter;
		if ((cursorPosition >= 0) && (cursorPosition < length)) {
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
		final double graphPosition = OpenLogViewerApp.getInstance().getEntireGraphingPanel().getGraphPosition();
		final int zoom = OpenLogViewerApp.getInstance().getEntireGraphingPanel().getZoom();
		final int cursorPosition = (int) graphPosition + (cursorDistanceFromCenter * zoom);
		if ((cursorPosition >= 0) && (cursorPosition < length)) {
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
			leftDataPointsToDisplay = new ArrayList<Double>();
			rightDataPointsToDisplay = new ArrayList<Double>();
			final int graphPosition = (int) OpenLogViewerApp.getInstance().getEntireGraphingPanel().getGraphPosition();
			final int zoom = OpenLogViewerApp.getInstance().getEntireGraphingPanel().getZoom();
			int numPointsThatFitInDisplay = this.getWidth() / zoom;
			numPointsThatFitInDisplay += 6; // Add six data points for off-screen (not just two, because of zoom stupidity) = LOL
			final int halfNumPoints = numPointsThatFitInDisplay / 2;
			final int leftGraphPosition = graphPosition - halfNumPoints;
			final int rightGraphPosition = graphPosition + halfNumPoints;

			// Setup left data points.
			for (int i = graphPosition; i > leftGraphPosition; i--) {
				if (i >= 0 && i < length) {
					leftDataPointsToDisplay.add(GDE.get(i));
				}
			}

			// Setup right data points.
			for (int i = graphPosition; i < rightGraphPosition; i++) {
				if (i >= 0 && i < length) {
					rightDataPointsToDisplay.add(GDE.get(i));
				}
			}
		}
	}

	/**
	 * initialize the graph any time you need to paint
	 */
	public final void initGraphZoomedOut() {
		if (GDE != null) {
			leftDataPointsToDisplay = new ArrayList<Double>();
			rightDataPointsToDisplay = new ArrayList<Double>();
			final int graphPosition = (int) OpenLogViewerApp.getInstance().getEntireGraphingPanel().getGraphPosition();
			final int zoom = OpenLogViewerApp.getInstance().getEntireGraphingPanel().getZoom();
			final int numPointsThatFitInDisplay = this.getWidth() * zoom;
			final int halfNumPoints = numPointsThatFitInDisplay / 2;
			final int leftGraphPosition = graphPosition - halfNumPoints - zoom;
			final int rightGraphPosition = graphPosition + halfNumPoints + zoom;
			double leftOfNewData = Double.MIN_VALUE;

			/*
			* Setup left data points.
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

			// Get first leftOfNewData since we need it to start with and it is not explicit
			if (leftGraphPosition <= 0){
				// The beginning of the graph is still visible so there is no earlier data
				// Use earliest data available instead
				leftOfNewData = GDE.get(0);
			} else {
				// Calculate leftOfNewData with points that are off the screen
				// There is no previous point to compare to, so just use the average instead of min or max
				double newData = 0.0;
				double acummulateData = 0.0;
				int divisor = 0;
				for (int i = leftGraphPosition - zoom; i < leftGraphPosition ; i++){
					if(i >= 0){
						newData = GDE.get(i);
						acummulateData += newData;
						divisor++;
					}
				}
				leftOfNewData = (acummulateData / divisor);
			}

			// Populate leftDataPointsToDisplay
			for (int i = leftGraphPosition; i <= graphPosition; i+=zoom) {

				if (i >= 0 && i < length) {
					double minData = Double.MAX_VALUE;
					double maxData = Double.MIN_VALUE;
					double newData = 0.0;
					double acummulateData = 0.0;
					int divisor = 0;

					for (int j = 0; j < zoom; j++){
						if (i + j >= 0 && i + j < length) {
							newData = GDE.get(i + j);
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
						leftDataPointsToDisplay.add(0, maxData);
						leftOfNewData = maxData;
					} else if (averageData < leftOfNewData){
						leftDataPointsToDisplay.add(0, minData);
						leftOfNewData = minData;
					} else {
						leftDataPointsToDisplay.add(0, averageData);
						leftOfNewData = averageData;
					}
				}
			}
			if (leftDataPointsToDisplay.isEmpty()){
				leftDataPointsToDisplay.add(GDE.get(0));
			}

			/*
			* Setup right data points.
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
			leftOfNewData = leftDataPointsToDisplay.get(0);
			for (int i = graphPosition; i < rightGraphPosition; i+=zoom) {

				if (i >= 0 && i < length) {
					double minData = Double.MAX_VALUE;
					double maxData = Double.MIN_VALUE;
					double newData = 0.0;
					double acummulateData = 0.0;
					int divisor = 0;

					for (int j = 0; j < zoom; j++){
						if (i + j >= 0 && i + j < length) {
							newData = GDE.get(i + j);
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
						rightDataPointsToDisplay.add(maxData);
						leftOfNewData = maxData;
					} else if (averageData < leftOfNewData){
						rightDataPointsToDisplay.add(minData);
						leftOfNewData = minData;
					} else {
						rightDataPointsToDisplay.add(averageData);
						leftOfNewData = averageData;
					}
				}
			}

			// Reconcile  situations where the first point to draw from each list does not match
			double left = leftDataPointsToDisplay.get(0);
			double right = rightDataPointsToDisplay.get(0);
			if (left != right){
				double average = (left + right) / 2;
				leftDataPointsToDisplay.set(0, average);
				rightDataPointsToDisplay.set(0, average);
			}
		}
	}

	/**
	 * maintains the size of the graph when applying divisions
	 */
	public final void sizeGraph() {
		final MultiGraphLayeredPane lg = OpenLogViewerApp.getInstance().getMultiGraphLayeredPane();
		int wherePixel = 0;
		if (lg.getTotalSplits() > 1) {
			if (GDE.getSplitNumber() <= lg.getTotalSplits()) {
				wherePixel += lg.getHeight() / lg.getTotalSplits() * GDE.getSplitNumber() - (lg.getHeight() / lg.getTotalSplits());
			} else {
				wherePixel += lg.getHeight() / lg.getTotalSplits() * lg.getTotalSplits() - (lg.getHeight() / lg.getTotalSplits());
			}
		}

		this.setBounds(0, wherePixel, lg.getWidth(), lg.getHeight() / (lg.getTotalSplits()));
		final boolean zoomedOut = OpenLogViewerApp.getInstance().getEntireGraphingPanel().isZoomedOutBeyondOneToOne();
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
		return length;
	}
}
