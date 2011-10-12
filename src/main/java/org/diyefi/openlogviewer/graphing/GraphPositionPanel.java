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
import javax.swing.JPanel;

import org.diyefi.openlogviewer.OpenLogViewerApp;
import org.diyefi.openlogviewer.genericlog.GenericLog;

/**
 *
 * @author Ben Fenner
 */
public class GraphPositionPanel extends JPanel {

	public GraphPositionPanel() {
		super();
        init();
    }
	
	private void init(){
        this.setOpaque(true);
        this.setLayout(null);
        genLog = new GenericLog();
        majorGraduationColor = Color.GRAY;
        minorGraduationColor = majorGraduationColor.darker();
        positionDataColor = majorGraduationColor;
        backgroundColor = Color.black;
        setGraduationSpacing();
	}

    @Override
    public void paint(Graphics g) { // override paint because there will be no components in this pane
    	if (!this.getSize().equals(this.getParent().getSize())) {
            this.setSize(this.getParent().getSize());
        }
    	setGraduationSpacing();
    	Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(backgroundColor);
        g2d.fillRect(0, 0, this.getWidth(), this.getHeight());
        if (genLog.getLogStatus() == GenericLog.LOG_NOT_LOADED) {
        	paintPositionBar(g2d);
        } else if (genLog.getLogStatus() == GenericLog.LOG_LOADING) {
        	paintPositionBar(g2d);
        } else if (genLog.getLogStatus() == GenericLog.LOG_LOADED) {
        	paintPositionBar(g2d);
        	paintPositionData(g2d);
        }
    }
    
    private void paintPositionBar(Graphics2D g2d){
    	int center = this.getWidth() / 2;
    	int graphPosition = OpenLogViewerApp.getInstance().getEntireGraphingPanel().getGraphPosition();
    	int zoom = OpenLogViewerApp.getInstance().getEntireGraphingPanel().getZoom();
    	int count = graphPosition * zoom;
    	g2d.setColor(minorGraduationColor);
    	for(int i = center; i > 0; i--){  //paint left of center
        	if(count % (minorGraduationSpacing * zoom) == 0){
        		g2d.drawLine(i, 0, i, 2);
        	}
        	count--;
        }
        count = graphPosition * zoom;
        for(int i = center; i < this.getWidth(); i++){  //paint right of center
        	if(count % (minorGraduationSpacing * zoom) == 0){
        		g2d.drawLine(i, 0, i, 2);
        	}
        	count++;
        }
        g2d.setColor(majorGraduationColor);
        count = graphPosition * zoom;
        for(int i = center; i > 0; i--){  //paint left of center
        	if(count % (majorGraduationSpacing * zoom) == 0){
        		g2d.drawLine(i, 0, i, 6);
        	}
        	count--;
        }
        count = graphPosition * zoom;
        for(int i = center; i < this.getWidth(); i++){  //paint right of center
        	if(count % (majorGraduationSpacing * zoom) == 0){
        		g2d.drawLine(i, 0, i, 6);
        	}
        	count++;
        }
        g2d.drawLine(0, 0, this.getWidth(), 0);
    }
    
    private void paintPositionData(Graphics2D g2d){
    	int center = this.getWidth() / 2;
    	int graphPosition = OpenLogViewerApp.getInstance().getEntireGraphingPanel().getGraphPosition();
    	int zoom = OpenLogViewerApp.getInstance().getEntireGraphingPanel().getZoom();
    	int count = graphPosition * zoom;
    	g2d.setColor(positionDataColor);
    	for(int i = center; i > 0; i--){  //paint left of center
        	if(count % (majorGraduationSpacing * zoom) == 0){
        		String positionDataString = Integer.toString(count / zoom);
        		g2d.drawString(positionDataString, i - 10, 18);
        	}
        	count--;
        }
        count = graphPosition * zoom;
        for(int i = center; i < this.getWidth(); i++){  //paint right of center
        	if(count % (majorGraduationSpacing * zoom) == 0){
        		String positionDataString = Integer.toString(count / zoom);
        		g2d.drawString(positionDataString, i - 10, 18);
        	}
        	count++;
        }
    }
    
    public void setLog(GenericLog log) {
       genLog = log;
        repaint();
    }
    
    private void setGraduationSpacing(){
    	int zoom = 1;
    	if(OpenLogViewerApp.getInstance() != null){
    		zoom = OpenLogViewerApp.getInstance().getEntireGraphingPanel().getZoom();
    	}
    	if(zoom > 5){
    		majorGraduationSpacing = 10;
    	} else{
    		majorGraduationSpacing = 50;
    	}
        minorGraduationSpacing = majorGraduationSpacing / 2;
    }

    private GenericLog genLog;
    private Color majorGraduationColor;
    private Color minorGraduationColor;
    private Color positionDataColor;
    private Color backgroundColor;
    private int majorGraduationSpacing;
    private int minorGraduationSpacing;
	private static final long serialVersionUID = -7808475370693818838L;
    
}
