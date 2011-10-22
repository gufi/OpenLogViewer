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
public class SingleGraphPanel extends JPanel implements HierarchyBoundsListener,PropertyChangeListener {


	private GenericDataElement GDE;
    private LinkedList<Double> leftDataPointsToDisplay;
    private LinkedList<Double> rightDataPointsToDisplay;
    private static final double GRAPH_TRACE_SIZE_AS_PERCENTAGE_OF_TOTAL_GRAPH_SIZE = 0.95;
    private static final long serialVersionUID = -7808406950399781712L;

    public SingleGraphPanel() {
        this.setOpaque(false);
        this.setLayout(null);
        this.GDE = null;
        leftDataPointsToDisplay = new LinkedList<Double>();
        rightDataPointsToDisplay = new LinkedList<Double>();
    }

    @Override
    public void ancestorMoved(HierarchyEvent e) {
    }

    @Override
    public void ancestorResized(HierarchyEvent e) {
        if (e.getID() == HierarchyEvent.ANCESTOR_RESIZED) {
            sizeGraph();
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if(evt.getPropertyName().equalsIgnoreCase("Split")){
            sizeGraph();
        }
    }



    @Override
    public void paint(Graphics g) { // overridden paint because there will be no other painting other than this
    	initGraph();
        if (hasDataPointToDisplay()) {
        	paintLeftDataPoints(g);
        	paintRightDataPoints(g);
        }
    }

    private void paintLeftDataPoints(Graphics g){
    	// Setup graphics stuff
    	Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(GDE.getColor());

        // Setup current, previous and next graph trace data points
    	boolean atGraphBeginning = false;
        boolean firstDataPoint = true;
    	ListIterator<Double> it = (ListIterator<Double>) leftDataPointsToDisplay.iterator();
    	Double traceData = it.next();
    	Double leftOfTraceData = null;
    	Double rightOfTraceData = null;
    	if(it.hasPrevious()){
    		rightOfTraceData = it.previous();
    		it.next();
    	} else {
    		if(rightDataPointsToDisplay.size() > 1){
    			rightOfTraceData = rightDataPointsToDisplay.get(1);
    		} else {
    			// At graph end
    		}
    	}
    	if(it.hasNext()){
    		leftOfTraceData = it.next();
    		it.previous();
    	} else {
    		atGraphBeginning = true;
    	}

    	// Setup data point screen location stuff
    	int zoom = OpenLogViewerApp.getInstance().getEntireGraphingPanel().getZoom();
    	double graphPosition = OpenLogViewerApp.getInstance().getEntireGraphingPanel().getGraphPosition();
    	double offset = (graphPosition % 1) * zoom;
    	int screenPositionXCoord = (this.getWidth() / 2) - (int)offset;
    	int screenPositionYCoord = getScreenPositionYCoord(traceData, GDE.getMinValue(), GDE.getMaxValue());
    	int prevScreenPositionYCoord = -1;

    	// Draw data points and trace lines
    	while(it.hasNext()){

    		// Draw data point
            if(zoom > 5 && (!traceData.equals(leftOfTraceData) || !traceData.equals(rightOfTraceData))){
                g2d.fillOval(screenPositionXCoord - 2, screenPositionYCoord - 2, 4, 4);
            }

        	// Draw graph trace line
            if(!firstDataPoint){
            	g2d.drawLine(screenPositionXCoord, screenPositionYCoord, screenPositionXCoord + zoom, prevScreenPositionYCoord);
            }

            // Move to next trace data in the list
            rightOfTraceData = traceData;
            traceData = it.next();
        	if(it.hasNext()){
        		leftOfTraceData = it.next();
        		it.previous();
        	} else {
        		atGraphBeginning = true;
        	}

        	// Reconfigure data point screen location stuff
            prevScreenPositionYCoord = screenPositionYCoord;
            screenPositionYCoord = getScreenPositionYCoord(traceData, GDE.getMinValue(), GDE.getMaxValue());
            screenPositionXCoord -= zoom;
            firstDataPoint = false;
    	}

    	// Always draw one last data point and trace line at the end of the list. This is usually off screen but can also be the beginning of the graph.
    	if(atGraphBeginning){
    		screenPositionYCoord = getScreenPositionYCoord(traceData, GDE.getMinValue(), GDE.getMaxValue());
    		// Draw data point
    		if (zoom > 5) {
            	g2d.fillOval(screenPositionXCoord - 2, screenPositionYCoord - 2, 4, 4);
            }
        	// Draw graph trace line
            if(!firstDataPoint){
            	g2d.drawLine(screenPositionXCoord, screenPositionYCoord, screenPositionXCoord + zoom, prevScreenPositionYCoord);
            }
    	}
    }

    private void paintRightDataPoints(Graphics g){
    	// Setup graphics stuff
    	Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(GDE.getColor());

        // Setup current, previous and next graph trace data points
    	boolean atGraphEnd = false;
        boolean firstDataPoint = true;
    	ListIterator<Double> it = (ListIterator<Double>) rightDataPointsToDisplay.iterator();
    	Double traceData = it.next();
    	Double leftOfTraceData = null;
    	Double rightOfTraceData = null;
    	if(it.hasPrevious()){
    		leftOfTraceData = it.previous();
    		it.next();
    	} else {
    		if(leftDataPointsToDisplay.size() > 1){
    			leftOfTraceData = leftDataPointsToDisplay.get(1);
    		} else {
    			// At graph beginning
    		}
    	}
    	if(it.hasNext()){
    		rightOfTraceData = it.next();
    		it.previous();
    	} else {
    		atGraphEnd = true;
    	}

    	// Setup data point screen location stuff
    	int zoom = OpenLogViewerApp.getInstance().getEntireGraphingPanel().getZoom();
    	double graphPosition = OpenLogViewerApp.getInstance().getEntireGraphingPanel().getGraphPosition();
    	double offset = (graphPosition % 1) * zoom;
    	int screenPositionXCoord = (this.getWidth() / 2) - (int) offset;
    	int screenPositionYCoord = getScreenPositionYCoord(traceData, GDE.getMinValue(), GDE.getMaxValue());
    	int prevScreenPositionYCoord = -1;

    	// Draw data points and trace lines
    	while(it.hasNext()){

    		// Draw data point
            if(zoom > 5 && (!traceData.equals(leftOfTraceData) || !traceData.equals(rightOfTraceData))){
                g2d.fillOval(screenPositionXCoord - 2, screenPositionYCoord - 2, 4, 4);
            }

        	// Draw graph trace line
            if(!firstDataPoint){
            	g2d.drawLine(screenPositionXCoord, screenPositionYCoord, screenPositionXCoord - zoom, prevScreenPositionYCoord);
            }

            // Move to next trace data in the list
            leftOfTraceData = traceData;
            traceData = it.next();
        	if(it.hasNext()){
        		rightOfTraceData = it.next();
        		it.previous();
        	} else {
        		atGraphEnd = true;
        	}

        	// Reconfigure data point screen location stuff
            prevScreenPositionYCoord = screenPositionYCoord;
            screenPositionYCoord = getScreenPositionYCoord(traceData, GDE.getMinValue(), GDE.getMaxValue());
            screenPositionXCoord += zoom;
            firstDataPoint = false;
    	}

    	// Draw one last data point (if needed) and trace line (always) at the end of the list. This is usually off screen but can also be the end of the graph.
    	if(atGraphEnd){
    		screenPositionYCoord = getScreenPositionYCoord(traceData, GDE.getMinValue(), GDE.getMaxValue());
    		// Draw data point
    		if (zoom > 5 && !traceData.equals(leftOfTraceData)) {
            	g2d.fillOval(screenPositionXCoord - 2, screenPositionYCoord - 2, 4, 4);
            }
        	// Draw graph trace line
            if(!firstDataPoint){
            	g2d.drawLine(screenPositionXCoord, screenPositionYCoord, screenPositionXCoord - zoom, prevScreenPositionYCoord);
            }
    	}
    }

    private int getScreenPositionYCoord(Double traceData, double minValue, double maxValue) {
        int point = 0;
        int height = (int)(this.getHeight() * GRAPH_TRACE_SIZE_AS_PERCENTAGE_OF_TOTAL_GRAPH_SIZE);
        if (maxValue != minValue) {
            point = (int) (height - (height * ((traceData - minValue) / (maxValue - minValue))));
        }
        return point;
    }

    private boolean hasDataPointToDisplay(){
    	boolean result = false;
    	if(leftDataPointsToDisplay != null && leftDataPointsToDisplay.size() > 0){
    		result = true;
    	}
    	return result;
    }

    /**
     * this is where the GDE is referenced and the graph gets initialized for the first time
     * @param GDE
     */
    public void setData(GenericDataElement GDE) {
        this.GDE = GDE;
        sizeGraph();
    }

    public GenericDataElement getData() {
        return GDE;
    }
    /**
     * used for InfoLayer to get the data from the GraphLayers for data under the mouse
     * needs to be rewritten.
     * @param i
     * @return Double representation of info at the mouse pointer
     */
    public Double getMouseInfo(int cursorDistanceFromCenter) {
    	double graphPosition = OpenLogViewerApp.getInstance().getEntireGraphingPanel().getGraphPosition();
    	int zoom = OpenLogViewerApp.getInstance().getEntireGraphingPanel().getZoom();
    	double offset = (graphPosition % 1) * zoom;
    	cursorDistanceFromCenter += (int)offset;
    	double numSnapsFromCenter = ((double)cursorDistanceFromCenter / (double)zoom);
    	numSnapsFromCenter = Math.round(numSnapsFromCenter);
    	int cursorPosition = (int)graphPosition + (int)numSnapsFromCenter;
        if (cursorPosition >= 0 && cursorPosition < GDE.size()) {
            return GDE.get(cursorPosition);
        } else {
            return -1.0;
        }
    }
    /**
     *
     * @return GDE.getColor()
     */
    public Color getColor() {
        return GDE.getColor();
    }
    /**
     * setter
     * @param c
     */
    public void setColor(Color c) {
        GDE.setColor(c);
    }
    /**
     * initialize the graph when the width of the graph parent changes or any time a major update happens
     * such as changing current
     */
    public void initGraph() {
        if (GDE != null) {
        	leftDataPointsToDisplay = new LinkedList<Double>();
        	rightDataPointsToDisplay = new LinkedList<Double>();
        	int graphPosition = (int)OpenLogViewerApp.getInstance().getEntireGraphingPanel().getGraphPosition();
        	int zoom = OpenLogViewerApp.getInstance().getEntireGraphingPanel().getZoom();
        	int numPointsThatFitInDisplay = this.getWidth() / zoom;
        	numPointsThatFitInDisplay += 6; //Add six data points for off-screen (not just two, because of zoom stupidity)
        	int halfNumPoints = numPointsThatFitInDisplay / 2;
        	int leftGraphPosition = graphPosition - halfNumPoints;
        	int rightGraphPosition = graphPosition + halfNumPoints;
        	for(int i = graphPosition; i > leftGraphPosition; i--){
        		if(i >= 0 && i < GDE.size()){
        			leftDataPointsToDisplay.add(GDE.get(i));
        		}
        	}
        	for(int i = graphPosition; i < rightGraphPosition; i++){
        		if (i >= 0 && i < GDE.size()){
        			rightDataPointsToDisplay.add(GDE.get(i));
        		}
        	}
        }
    }
    /**
     * maintains the size of the graph when applying divisions
     */
    public void sizeGraph() {
        MultiGraphLayeredPane lg = OpenLogViewerApp.getInstance().getMultiGraphLayeredPane();
        int wherePixel = 0 ;
        if (lg.getTotalSplits() > 1) {
            if (GDE.getSplitNumber() <= lg.getTotalSplits()) {
                wherePixel += lg.getHeight() / lg.getTotalSplits() * GDE.getSplitNumber() - (lg.getHeight() / lg.getTotalSplits());
            } else {
                wherePixel += lg.getHeight() / lg.getTotalSplits() * lg.getTotalSplits() - (lg.getHeight() / lg.getTotalSplits());
            }
        }

        this.setBounds(0, wherePixel, lg.getWidth(), lg.getHeight() / (lg.getTotalSplits()));
        initGraph();
    }

    /**
     * Graph total size
     * @return GDE.size()
     */
    public int graphSize() {
        return GDE.size();
    }

}
