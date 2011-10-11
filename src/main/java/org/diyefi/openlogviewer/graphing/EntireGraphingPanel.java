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

import java.awt.Dimension;
import javax.swing.JPanel;
import org.diyefi.openlogviewer.genericlog.GenericLog;
import org.diyefi.openlogviewer.graphing.MultiGraphLayeredPane;

/**
 *
 * @author Ben Fenner
 */
public class EntireGraphingPanel extends JPanel{

	public EntireGraphingPanel() {
        super();
        init();
    }

    private void init() {
    	this.setName("Graphing Panel");
        this.setLayout(new java.awt.BorderLayout());
        multiGraph = new MultiGraphLayeredPane();
        multiGraph.setPreferredSize(new Dimension(600, 400));
        this.add(multiGraph, java.awt.BorderLayout.CENTER);
        graphPosition = new GraphPositionPanel();
        graphPosition.setPreferredSize(new Dimension(600, 20));
        graphPosition.setZoom(multiGraph.getZoom());
        this.add(graphPosition, java.awt.BorderLayout.SOUTH);
    }
    
    public void setLog(GenericLog genLog) {
    	multiGraph.setLog(genLog);
    	graphPosition.setLog(genLog);
    }
    
    public MultiGraphLayeredPane getMultiGraphLayeredPane(){
    	return multiGraph;
    }
    
    public GraphPositionPanel getGraphPositionPanel(){
    	return graphPosition;
    }
    
    public void zoomIn(){
    	multiGraph.zoomIn();
    	graphPosition.zoomIn();
    }
    
    public void zoomOut(){
    	multiGraph.zoomOut();
    	graphPosition.zoomOut();
    }
    
    public void play(){
    	multiGraph.play();
    	graphPosition.play();
    }

    private MultiGraphLayeredPane multiGraph;
    private GraphPositionPanel graphPosition;
    private static final long serialVersionUID = 6880240079754110792L;
}
