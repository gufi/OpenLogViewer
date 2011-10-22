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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JPanel;
import javax.swing.Timer;

import org.diyefi.openlogviewer.OpenLogViewerApp;
import org.diyefi.openlogviewer.genericlog.GenericLog;
import org.diyefi.openlogviewer.graphing.MultiGraphLayeredPane;

public class EntireGraphingPanel extends JPanel implements ActionListener, MouseMotionListener, MouseListener, MouseWheelListener, KeyListener {
	private MultiGraphLayeredPane multiGraph;
	private GraphPositionPanel graphPositionPanel;
	private double graphPosition;
	private int graphPositionMax;
	private boolean playing;
	private boolean wasPlaying;
	private Timer playTimer;
	private Timer flingTimer;
	private boolean dragging;
	private boolean flinging;
	private long thePast;
	private int prevDragXCoord;
	private int flingInertia;
	private int zoom;
	private static final long serialVersionUID = 6880240079754110792L;

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
		graphPositionPanel = new GraphPositionPanel();
		graphPositionPanel.setPreferredSize(new Dimension(600, 20));
		this.add(graphPositionPanel, java.awt.BorderLayout.SOUTH);
		resetGraphPosition();
		setGraphPositionMax();
		playing = false;
		wasPlaying = false;
		playTimer = new Timer(10, this);
		playTimer.setInitialDelay(0);
		flingTimer = new Timer(10, this);
		flingTimer.setInitialDelay(0);
		addMouseListener(this);
		addMouseMotionListener(this);
		addMouseWheelListener(this);
		addMouseListener(multiGraph.getInfoPanel());
		addMouseMotionListener(multiGraph.getInfoPanel());
		stopDragging();
		stopFlinging();
		thePast = System.currentTimeMillis();
		zoom = 1;
	}

	public void actionPerformed(ActionEvent e) {
		if (playing && graphPosition < graphPositionMax) {
			moveGraphPosition(1);
		} else if (flinging && graphPosition < graphPositionMax && graphPosition > 0){
			if(flingInertia == 0){
				stopFlinging();
			} else{
				int center = this.getWidth() / 2;
				moveEntireGraphingPanel(center + flingInertia);
				if(flingInertia > 0){
					flingInertia--;
				} else {
					flingInertia++;
				}
			}
		}
	}

	public MultiGraphLayeredPane getMultiGraphLayeredPane(){
		return multiGraph;
	}

	public GraphPositionPanel getGraphPositionPanel(){
		return graphPositionPanel;
	}

	public void setLog(GenericLog genLog) {
		playing = false;
		resetGraphPosition();
		multiGraph.setLog(genLog);
		graphPositionPanel.setLog(genLog);
	}

	public void zoomIn() {
		if (zoom < 500) {
			zoom++;
		}
		repaint();
	}

	public void zoomOut() {
		if (zoom > 1) {
			zoom--;
		}
		repaint();
	}

	public void play(){
		if (playing) {
			pause();
		} else {
			playing = true;
			stopDragging();
			stopFlinging();
			playTimer.start();
		}
	}

	public void pause(){
		playing = false;
		playTimer.stop();
		stopDragging();
		stopFlinging();
	}

	/**
	 * Increases the speed of the graph by 1 ms until 0, at which speed cannot be advanced any further and will essentially update as fast as possible.
	 */
	public void fastForward() {
		int currentDelay = playTimer.getDelay();
		if(currentDelay > 0){
			playTimer.setDelay(currentDelay - 1);
		}
	}

	public void eject() {
		resetGraphPosition();
	}

	public void stop(){
		playing = false;
		wasPlaying = false;
		playTimer.stop();
		resetGraphPosition();
	}

	/**
	 * Slows the speed of playback by 1 ms
	 */
	public void slowDown() {
		int currentDelay = playTimer.getDelay();
		playTimer.setDelay(currentDelay + 1);
	}

	public void fling(){
		flinging = true;
		flingTimer.start();
	}

	public double getGraphPosition(){
		return graphPosition;
	}

	public int getGraphPositionMax(){
		return graphPositionMax;
	}

	public int getZoom(){
		return zoom;
	}

	private void moveGraphPosition(double amount){
		double newPos = graphPosition + amount;
		setGraphPosition(newPos);
	}

	public void setGraphPosition(double newPos){
		graphPosition = newPos;
		repaint();
	}

	public void setGraphPositionMax(){
		boolean found = false;
		for (int i = 0; i < multiGraph.getComponentCount() && !found; i++) {
			if (multiGraph.getComponent(i) instanceof SingleGraphPanel) {
				SingleGraphPanel gl = (SingleGraphPanel) multiGraph.getComponent(i);
				graphPositionMax = gl.graphSize() - 1;
				found = true;
			}
		}
	}

	private void resetGraphPosition(){
		setGraphPosition(0);
	}

	private void goToLastGraphPosition(){
		setGraphPosition(graphPositionMax);
	}

	public boolean isPlaying(){
		return playing;
	}

	private void moveEntireGraphingPanel(double newPosition){
		double graphPosition = OpenLogViewerApp.getInstance().getEntireGraphingPanel().getGraphPosition();
		int graphPositionMax = OpenLogViewerApp.getInstance().getEntireGraphingPanel().getGraphPositionMax();
		double center = this.getWidth() / 2;
		double move = (newPosition - center) / zoom;
		if (move + graphPosition < graphPositionMax) {
			if (move + graphPosition < 0) {
				OpenLogViewerApp.getInstance().getEntireGraphingPanel().resetGraphPosition();
			} else {
				OpenLogViewerApp.getInstance().getEntireGraphingPanel().moveGraphPosition(move);
			}
		} else {
			OpenLogViewerApp.getInstance().getEntireGraphingPanel().setGraphPosition(graphPositionMax);
		}
	}

	private void stopDragging(){
		dragging = false;
		prevDragXCoord = -1;
	}

	private void stopFlinging(){
		flinging = false;
		flingInertia = 0;
	}

	//MOUSE LISTENER FUNCTIONALITY
	@Override
	public void mouseClicked(MouseEvent e) {
		if (!dragging) {
			moveEntireGraphingPanel(e.getX());
		} else {
			stopDragging();
			stopFlinging();
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		dragging = true;
		int center = this.getWidth() / 2;
		int xMouseCoord = e.getX();
		if(prevDragXCoord > 0 && prevDragXCoord != xMouseCoord){
			moveEntireGraphingPanel(center + (prevDragXCoord - xMouseCoord));
			flingInertia = ((prevDragXCoord - xMouseCoord) * 2);
			thePast = System.currentTimeMillis();
		}
		prevDragXCoord = xMouseCoord;
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// What should be here?
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// What should be here?
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// What should be here?
	}

	@Override
	public void mousePressed(MouseEvent e) {
		wasPlaying = playing;
		if(playing){
			pause();
		}

		stopDragging();
		stopFlinging();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		stopDragging();

		long now = System.currentTimeMillis();
		if(now - thePast > 50){
			stopFlinging(); // If over 50 milliseconds since dragging then don't fling
		}

		if(flingInertia != 0){
			fling();
		}

		if(wasPlaying){
			play();
		}
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		int center = this.getWidth() / 2;
		int zoomInMove = center + ((e.getX() - center) / (zoom));
		int zoomOutMove = center - ((e.getX() - center) / (zoom));
		int notches = e.getWheelRotation();
		if(notches < 0){
			zoomIn();
			moveEntireGraphingPanel(zoomInMove);
		} else if (zoom > 1){
			zoomOut();
			moveEntireGraphingPanel(zoomOutMove);
		}
	}

	// Key listener functionality
	@Override
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
			// Play key bindings
			case KeyEvent.VK_SPACE: {
				play();
				break;
			}

			// Home key bindings
			case KeyEvent.VK_HOME: {
				resetGraphPosition();
				break;
			}

			// End key bindings
			case KeyEvent.VK_END: {
				goToLastGraphPosition();
				break;
			}

			// Scroll left key bindings
			case KeyEvent.VK_PAGE_UP: {
				//Big scroll
				moveEntireGraphingPanel(-(this.getWidth() / 4) / zoom);
				break;
			}

			case KeyEvent.VK_LEFT: {
				if(e.getModifiers() == InputEvent.CTRL_MASK){
					//Big scroll
					moveEntireGraphingPanel(-(this.getWidth() / 4) / zoom);
				} else {
					int center = this.getWidth() / 2;
					moveEntireGraphingPanel(center - (1 * zoom));
				}
				break;
			}

			case KeyEvent.VK_KP_LEFT: {
				if(e.getModifiers() == InputEvent.CTRL_MASK){
					//Big scroll
					moveEntireGraphingPanel(-(this.getWidth() / 4) / zoom);
				} else {
					int center = this.getWidth() / 2;
					moveEntireGraphingPanel(center - (1 * zoom));
				}
				break;
			}

			// Scroll right key bindings
			case KeyEvent.VK_PAGE_DOWN: {
				//Big scroll
				moveEntireGraphingPanel(this.getWidth() + (this.getWidth() / 4) / zoom);
				break;
			}

			case KeyEvent.VK_RIGHT: {
				if(e.getModifiers() == InputEvent.CTRL_MASK){
					//Big scroll
					moveEntireGraphingPanel(this.getWidth() + (this.getWidth() / 4) / zoom);
				} else {
					int center = this.getWidth() / 2;
					moveEntireGraphingPanel(center + (1 * zoom));
				}
				break;
			}

			case KeyEvent.VK_KP_RIGHT: {
				if(e.getModifiers() == InputEvent.CTRL_MASK){
					//Big scroll
					moveEntireGraphingPanel(this.getWidth() + (this.getWidth() / 4) / zoom);
				} else {
					int center = this.getWidth() / 2;
					moveEntireGraphingPanel(center + (1 * zoom));
				}
				break;
			}

			// Zoom in key bindings
			case KeyEvent.VK_UP: {
				zoomIn();
				break;
			}

			case KeyEvent.VK_KP_UP: {
				zoomIn();
				break;
			}

			case KeyEvent.VK_ADD: {
				if(e.getModifiers() == InputEvent.CTRL_MASK){
					zoomIn();
				}
				break;
			}

			// Zoom out key bindings
			case KeyEvent.VK_DOWN: {
				zoomOut();
				break;
			}

			case KeyEvent.VK_KP_DOWN: {
				zoomOut();
				break;
			}

			case KeyEvent.VK_SUBTRACT: {
				if(e.getModifiers() == InputEvent.CTRL_MASK){
					zoomOut();
				}
				break;
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// What should be here?
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// What should be here?
	}
}
