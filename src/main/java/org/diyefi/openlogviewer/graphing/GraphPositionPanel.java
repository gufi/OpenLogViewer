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
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.JPanel;

import org.diyefi.openlogviewer.OpenLogViewerApp;
import org.diyefi.openlogviewer.genericlog.GenericLog;
import org.diyefi.openlogviewer.graphing.MultiGraphLayeredPane.Zoom;

/**
 *
 * @author Ben Fenner
 */
public class GraphPositionPanel extends JPanel implements MouseMotionListener, MouseListener {

	public GraphPositionPanel() {
		super();
        init();
    }
	
	private void init(){
        this.setOpaque(true);
        this.setLayout(null);
        addMouseListener(this);
        addMouseMotionListener(this);
        genLog = new GenericLog();
        majorGraduationColor = Color.GRAY;
        minorGraduationColor = majorGraduationColor.darker();
        positionDataColor = majorGraduationColor;
        backgroundColor = Color.black;
        resetGraphPosition();
        majorGraduationSpacing = 60;
        minorGraduationSpacing = majorGraduationSpacing / 2;
	}

    @Override
    public void paint(Graphics g) { // override paint because there will be no components in this pane
    	if (!this.getSize().equals(this.getParent().getSize())) {
            this.setSize(this.getParent().getSize());
        }
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
        g2d.setColor(minorGraduationColor);
        for(int i = center; i > 0; i-=minorGraduationSpacing){
        	//zoom.getZoom();
        	g2d.drawLine(i, 0, i, 2);
        }
        for(int i = center; i < this.getWidth(); i+=minorGraduationSpacing){
        	//zoom.getZoom();
        	g2d.drawLine(i, 0, i, 2);
        }
        g2d.setColor(majorGraduationColor);
        for(int i = center; i > 0; i-=majorGraduationSpacing){
        	g2d.drawLine(i, 0, i, 6);
        }
        for(int i = center; i < this.getWidth(); i+=majorGraduationSpacing){
        	g2d.drawLine(i, 0, i, 6);
        }
        g2d.drawLine(0, 0, this.getWidth(), 0);
    }
    
    private void paintPositionData(Graphics2D g2d){
    	int center = this.getWidth() / 2;
    	int count = graphPosition;
    	g2d.setColor(positionDataColor);
    	for(int i = center; i > 0; i-=majorGraduationSpacing){
    		String positionDataString = Integer.toString(count);
    		g2d.drawString(positionDataString, i - 10, 18);
    		count -= (majorGraduationSpacing / zoom.getZoom());
        }
    	count = graphPosition;
    	for(int i = center; i < this.getWidth(); i+=majorGraduationSpacing){
    		String positionDataString = Integer.toString(count);
    		g2d.drawString(positionDataString, i - 10, 18);
    		count += (majorGraduationSpacing / zoom.getZoom());
        }
    }
    
    public void setLog(GenericLog log) {
       genLog = log;
        repaint();
    }

    public void setZoom(Zoom z) {
        zoom = z;
    }

    public void resetDragCoords() {

    }

    public void moveGraphPositionBar(int amount){
    	setGraphPosition(graphPosition + amount);
    }
    
    public void moveGraphPositionBar(MouseEvent e){
    	int amount = e.getX() - (this.getWidth() / 2);
    	moveGraphPositionBar(amount);
    }
    
    public void setGraphPosition(int pos){
    	graphPosition = pos;
    	repaint();
    }
    
    public void resetGraphPosition(){
    	setGraphPosition(0);
    }
    
    public void zoomIn(){
    	repaint();
    }

    public void zoomOut(){
    	repaint();
    }
    
    public void play(){
    	repaint();
    }
    
    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {
        //repaint();
    }

//MOUSE LISTENER FUNCTIONALITY
    @Override
    public void mouseClicked(MouseEvent e) {
    	mouseClicked(e.getX());
    	MultiGraphLayeredPane lg = OpenLogViewerApp.getInstance().getMultiGraphLayeredPane();
        InfoPanel ip = lg.getInfoPanel();
        ip.mouseClicked(e.getX());
    }
    
    public void mouseClicked(int xPosition){
    	MultiGraphLayeredPane lg = OpenLogViewerApp.getInstance().getMultiGraphLayeredPane();
        int move = (xPosition / zoom.getZoom()) - ((this.getWidth() / 2) / zoom.getZoom());
        if (move + lg.getCurrent() < lg.getCurrentMax()) {
            if (move + lg.getCurrent() < 0) {
                resetGraphPosition();
            } else {
                moveGraphPositionBar(move);
            }
        } else {
        	setGraphPosition(lg.getCurrentMax());
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
       
    }

    @Override
    public void mouseExited(MouseEvent e) {
        
    }

    @Override
    public void mousePressed(MouseEvent e) {
        
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    private MultiGraphLayeredPane.Zoom zoom;
    private GenericLog genLog;
    private Color majorGraduationColor;
    private Color minorGraduationColor;
    private Color positionDataColor;
    private Color backgroundColor;
    private int graphPosition;
    private int majorGraduationSpacing;
    private int minorGraduationSpacing;
	private static final long serialVersionUID = -7808475370693818838L;
    
}
