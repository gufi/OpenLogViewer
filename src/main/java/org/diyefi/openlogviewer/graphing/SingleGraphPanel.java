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
import java.util.List;
import java.util.LinkedList;
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
	private static final long serialVersionUID = -7808406950399781712L;
	private static final double GRAPH_TRACE_SIZE_AS_PERCENTAGE_OF_TOTAL_GRAPH_SIZE = 0.95;
	private GenericDataElement GDE;
	private List<Double> leftDataPointsToDisplay;
	private List<Double> rightDataPointsToDisplay;

	public SingleGraphPanel() {
		this.setOpaque(false);
		this.setLayout(null);
		this.GDE = null;
		leftDataPointsToDisplay = new LinkedList<Double>();
		rightDataPointsToDisplay = new LinkedList<Double>();
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
		boolean zoomedOut = OpenLogViewerApp.getInstance().getEntireGraphingPanel().isZoomedBeyondOneToOne();
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
		g2d.setColor(GDE.getColor());

		// Setup current, previous and next graph trace data points
		boolean atGraphBeginning = false;
		boolean firstDataPoint = true;
		final ListIterator<Double> it = (ListIterator<Double>) leftDataPointsToDisplay.iterator();
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
		final boolean zoomedOut = OpenLogViewerApp.getInstance().getEntireGraphingPanel().isZoomedBeyondOneToOne();
		final int zoom = OpenLogViewerApp.getInstance().getEntireGraphingPanel().getZoom();
		final double graphPosition = OpenLogViewerApp.getInstance().getEntireGraphingPanel().getGraphPosition();
		final double offset = (graphPosition % 1) * zoom;
		int screenPositionXCoord = (this.getWidth() / 2) - (int) offset;
		int screenPositionYCoord = getScreenPositionYCoord(traceData, GDE.getMinValue(), GDE.getMaxValue());
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
			screenPositionYCoord = getScreenPositionYCoord(traceData, GDE.getMinValue(), GDE.getMaxValue());
			if(zoomedOut){
				screenPositionXCoord--;
			} else{
				screenPositionXCoord -= zoom;
			}
			firstDataPoint = false;
		}

		// Always draw one last data point and trace line at the end of the list. This is usually off screen but can also be the beginning of the graph.
		if (atGraphBeginning) {
			screenPositionYCoord = getScreenPositionYCoord(traceData, GDE.getMinValue(), GDE.getMaxValue());

			// Draw data point
			if (zoom > 5 && !zoomedOut) {
				g2d.fillOval(screenPositionXCoord - 2, screenPositionYCoord - 2, 4, 4);
			}

			// Draw graph trace line
			if (!firstDataPoint) {
				if(zoomedOut){
					g2d.drawLine(screenPositionXCoord, screenPositionYCoord, screenPositionXCoord + 1, prevScreenPositionYCoord);
				} else {
					g2d.drawLine(screenPositionXCoord, screenPositionYCoord, screenPositionXCoord + zoom, prevScreenPositionYCoord);
				}
			}
		}
	}

	private void paintRightDataPoints(final Graphics g) {
		// Setup graphics stuff
		final Graphics2D g2d = (Graphics2D) g;
		g2d.setColor(GDE.getColor());

		// Setup current, previous and next graph trace data points
		boolean atGraphEnd = false;
		boolean firstDataPoint = true;
		final ListIterator<Double> it = (ListIterator<Double>) rightDataPointsToDisplay.iterator();
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
		final boolean zoomedOut = OpenLogViewerApp.getInstance().getEntireGraphingPanel().isZoomedBeyondOneToOne();
		final int zoom = OpenLogViewerApp.getInstance().getEntireGraphingPanel().getZoom();
		final double graphPosition = OpenLogViewerApp.getInstance().getEntireGraphingPanel().getGraphPosition();
		final double offset = (graphPosition % 1) * zoom;
		int screenPositionXCoord = (this.getWidth() / 2) - (int) offset;
		int screenPositionYCoord = getScreenPositionYCoord(traceData, GDE.getMinValue(), GDE.getMaxValue());
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
			screenPositionYCoord = getScreenPositionYCoord(traceData, GDE.getMinValue(), GDE.getMaxValue());
			if(zoomedOut){
				screenPositionXCoord++;
			} else{
				screenPositionXCoord += zoom;
			}
			firstDataPoint = false;
		}

		// Draw one last data point (if needed) and trace line (always) at the end of the list. This is usually off screen but can also be the end of the graph.
		if (atGraphEnd) {
			screenPositionYCoord = getScreenPositionYCoord(traceData, GDE.getMinValue(), GDE.getMaxValue());
			// Draw data point
			if (zoom > 5 && !zoomedOut && !traceData.equals(leftOfTraceData)) {
				g2d.fillOval(screenPositionXCoord - 2, screenPositionYCoord - 2, 4, 4);
			}
			// Draw graph trace line
			if (!firstDataPoint) {
				if(zoomedOut){
					g2d.drawLine(screenPositionXCoord, screenPositionYCoord, screenPositionXCoord - 1, prevScreenPositionYCoord);
				} else {
					g2d.drawLine(screenPositionXCoord, screenPositionYCoord, screenPositionXCoord - zoom, prevScreenPositionYCoord);
				}
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
		sizeGraph();
	}

	public final GenericDataElement getData() {
		return GDE;
	}

	/**
	 * Used for InfoLayer to get the data from the single graphs for data under the mouse
	 *
	 * @param pointerDistanceFromCenter
	 * @return Double representation of info at the mouse cursor line which snaps to data points
	 */
	public final Double getMouseInfo(final int cursorDistanceFromCenter) {
		boolean zoomedOut = OpenLogViewerApp.getInstance().getEntireGraphingPanel().isZoomedBeyondOneToOne();
		Double info = -1.0;
		if(zoomedOut){
			info = getMouseInfoZoomedOut(cursorDistanceFromCenter);
		} else {
			info = getMouseInfoZoomed(cursorDistanceFromCenter);
		}
		return info;
	}

	private final Double getMouseInfoZoomed(int cursorDistanceFromCenter){
		final double graphPosition = OpenLogViewerApp.getInstance().getEntireGraphingPanel().getGraphPosition();
		final int zoom = OpenLogViewerApp.getInstance().getEntireGraphingPanel().getZoom();
		final double offset = (graphPosition % 1) * zoom;
		cursorDistanceFromCenter += (int) offset;
		double numSnapsFromCenter = ((double) cursorDistanceFromCenter / (double) zoom);
		numSnapsFromCenter = Math.round(numSnapsFromCenter);
		final int cursorPosition = (int) graphPosition + (int) numSnapsFromCenter;
		if ((cursorPosition >= 0) && (cursorPosition < GDE.size())) {
			return GDE.get(cursorPosition);
		} else {
			return -1.0;
		}
	}

	private final Double getMouseInfoZoomedOut(int cursorDistanceFromCenter){
		final double graphPosition = OpenLogViewerApp.getInstance().getEntireGraphingPanel().getGraphPosition();
		final int zoom = OpenLogViewerApp.getInstance().getEntireGraphingPanel().getZoom();
		final int cursorPosition = (int) graphPosition + (cursorDistanceFromCenter * zoom);
		if ((cursorPosition >= 0) && (cursorPosition < GDE.size())) {
			return GDE.get(cursorPosition);
		} else {
			return -1.0;
		}
	}

	public final Color getColor() {
		return GDE.getColor();
	}

	public final void setColor(final Color c) {
		GDE.setColor(c);
	}

	/**
	 * initialize the graph any time you need to paint
	 */
	public final void initGraphZoomed() {
		if (GDE != null) {
			leftDataPointsToDisplay = new LinkedList<Double>();
			rightDataPointsToDisplay = new LinkedList<Double>();
			final int graphPosition = (int) OpenLogViewerApp.getInstance().getEntireGraphingPanel().getGraphPosition();
			final int zoom = OpenLogViewerApp.getInstance().getEntireGraphingPanel().getZoom();
			int numPointsThatFitInDisplay = this.getWidth() / zoom;
			numPointsThatFitInDisplay += 6; // Add six data points for off-screen (not just two, because of zoom stupidity) = LOL
			final int halfNumPoints = numPointsThatFitInDisplay / 2;
			final int leftGraphPosition = graphPosition - halfNumPoints;
			final int rightGraphPosition = graphPosition + halfNumPoints;

			for (int i = graphPosition; i > leftGraphPosition; i--) {
				if (i >= 0 && i < GDE.size()) {
					leftDataPointsToDisplay.add(GDE.get(i));
				}
			}

			for (int i = graphPosition; i < rightGraphPosition; i++) {
				if (i >= 0 && i < GDE.size()) {
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
			leftDataPointsToDisplay = new LinkedList<Double>();
			rightDataPointsToDisplay = new LinkedList<Double>();
			final int graphPosition = (int) OpenLogViewerApp.getInstance().getEntireGraphingPanel().getGraphPosition();
			final int zoom = OpenLogViewerApp.getInstance().getEntireGraphingPanel().getZoom();
			int numPointsThatFitInDisplay = this.getWidth() * zoom;
			numPointsThatFitInDisplay += 6; // Add six data points for off-screen (not just two, because of zoom stupidity) = LOL
			final int halfNumPoints = numPointsThatFitInDisplay / 2;
			final int leftGraphPosition = graphPosition - halfNumPoints;
			final int rightGraphPosition = graphPosition + halfNumPoints;

			for (int i = graphPosition; i > leftGraphPosition; i-=zoom) {
				if (i >= 0 && i < GDE.size()) {
					leftDataPointsToDisplay.add(GDE.get(i));
				}
			}

			for (int i = graphPosition; i < rightGraphPosition; i+=zoom) {
				if (i >= 0 && i < GDE.size()) {
					rightDataPointsToDisplay.add(GDE.get(i));
				}
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
		initGraphZoomed();
	}

	/**
	 * Graph total size
	 * @return GDE.size()
	 */
	public final int graphSize() {
		return GDE.size();
	}
}
