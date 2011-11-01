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

public class EntireGraphingPanel extends JPanel implements ActionListener, MouseMotionListener, MouseListener, MouseWheelListener, KeyListener {
	private static final long serialVersionUID = 6880240079754110792L;
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
	private boolean zoomedOutBeyondOneToOne;

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
		zoomedOutBeyondOneToOne = false;
	}

	public final void actionPerformed(final ActionEvent e) {
		if (playing && graphPosition < graphPositionMax) {
			if(zoomedOutBeyondOneToOne){
				moveGraphPosition(zoom);
			} else {
				moveGraphPosition(1);
			}
		} else if ((flinging && graphPosition < graphPositionMax) && (graphPosition > 0)) {
			if (flingInertia == 0) {
				stopFlinging();
			} else {
				final int center = this.getWidth() / 2;
				moveEntireGraphingPanel(center + flingInertia);
				if (flingInertia > 0) {
					flingInertia--;
				} else {
					flingInertia++;
				}
			}
		}
	}

	public final MultiGraphLayeredPane getMultiGraphLayeredPane() {
		return multiGraph;
	}

	public final GraphPositionPanel getGraphPositionPanel() {
		return graphPositionPanel;
	}

	public final void setLog(final GenericLog genLog) {
		playing = false;
		resetGraphPosition();
		multiGraph.setLog(genLog);
		graphPositionPanel.setLog(genLog);
	}

	public final void zoomIn() {
		if(zoomedOutBeyondOneToOne) {
			if (zoom == 2) {
				zoomedOutBeyondOneToOne = false;
			}
			zoom--;
		} else if (zoom < 512) {
			zoom++;
		}

		repaint();
	}

	public final void zoomOut() {
		if (!zoomedOutBeyondOneToOne) {
			if (zoom == 1) {
				zoomedOutBeyondOneToOne = true;
				zoom = 2;
			} else {
				zoom--;
			}
		} else if (zoom < 1024){
			zoom++;
		}

		repaint();
	}

	public boolean isZoomedOutBeyondOneToOne(){
		return zoomedOutBeyondOneToOne;
	}

	public final void play() {
		if (playing) {
			pause();
		} else {
			playing = true;
			stopDragging();
			stopFlinging();
			playTimer.start();
		}
	}

	public final void pause() {
		playing = false;
		playTimer.stop();
		stopDragging();
		stopFlinging();
	}

	/**
	 * Increases the speed of the graph by 1 ms until 0, at which speed cannot be advanced any further and will essentially update as fast as possible.
	 */
	public final void fastForward() {
		final int currentDelay = playTimer.getDelay();
		if (currentDelay > 0) {
			playTimer.setDelay(currentDelay - 1);
		}
	}

	public final void eject() {
		resetGraphPosition();
	}

	public final void stop() {
		playing = false;
		wasPlaying = false;
		playTimer.stop();
		resetGraphPosition();
	}

	/**
	 * Slows the speed of playback by 1 ms
	 */
	public final void slowDown() {
		final int currentDelay = playTimer.getDelay();
		playTimer.setDelay(currentDelay + 1);
	}

	public final void fling() {
		flinging = true;
		flingTimer.start();
	}

	public final double getGraphPosition() {
		return graphPosition;
	}

	public final int getGraphPositionMax() {
		return graphPositionMax;
	}

	public final int getZoom() {
		return zoom;
	}

	private void moveGraphPosition(final double amount) {
		final double newPos = graphPosition + amount;
		if (newPos > graphPositionMax){
			goToLastGraphPosition();
		} else if (newPos < 0){
			resetGraphPosition();
		} else {
			setGraphPosition(newPos);
		}
	}

	public final void setGraphPosition(final double newPos) {
		graphPosition = newPos;
		repaint();
	}

	public final void setGraphPositionMax(final int graphPositionMax) {
		this.graphPositionMax = graphPositionMax;
	}

	private void resetGraphPosition() {
		setGraphPosition(0);
	}

	private void goToLastGraphPosition() {
		setGraphPosition(graphPositionMax);
	}

	public final boolean isPlaying() {
		return playing;
	}

	private void moveEntireGraphingPanel(final double newPosition) {
		final double center = this.getWidth() / 2;
		double move = -1.0;
		if(zoomedOutBeyondOneToOne){
			move = (newPosition - center) * zoom;
		} else {
			move = (newPosition - center) / zoom;
		}
		if (move + graphPosition < graphPositionMax) {
			if (move + graphPosition < 0) {
				resetGraphPosition();
			} else {
				moveGraphPosition(move);
			}
		} else {
			setGraphPosition(graphPositionMax);
		}
	}

	private void stopDragging() {
		dragging = false;
		prevDragXCoord = -1;
	}

	private void stopFlinging() {
		flinging = false;
		flingInertia = 0;
	}

	// Mouse listener functionality
	@Override
	public final void mouseClicked(final MouseEvent e) {
		if (!dragging) {
			moveEntireGraphingPanel(e.getX());
		} else {
			stopDragging();
			stopFlinging();
		}
	}

	@Override
	public final void mouseDragged(final MouseEvent e) {
		dragging = true;
		final int center = this.getWidth() / 2;
		final int xMouseCoord = e.getX();
		if ((prevDragXCoord > 0) && (prevDragXCoord != xMouseCoord)) {
			moveEntireGraphingPanel(center + (prevDragXCoord - xMouseCoord));
			flingInertia = ((prevDragXCoord - xMouseCoord) * 2);
			thePast = System.currentTimeMillis();
		}
		prevDragXCoord = xMouseCoord;
	}

	@Override
	public final void mouseMoved(final MouseEvent e) {
		// What should be here?
		// Ben says eventually there might be stuff here, and it is required implementation for the MouseMovementListener interface.
	}

	@Override
	public final void mouseEntered(final MouseEvent e) {
		// What should be here?
		// Ben says eventually there might be stuff here, and it is required implementation for the MouseMovementListener interface.
	}

	@Override
	public final void mouseExited(final MouseEvent e) {
		// What should be here?
		// Ben says eventually there might be stuff here, and it is required implementation for the MouseMovementListener interface.
	}

	@Override
	public final void mousePressed(final MouseEvent e) {
		wasPlaying = playing;
		if (playing) {
			pause();
		}

		stopDragging();
		stopFlinging();
	}

	@Override
	public final void mouseReleased(final MouseEvent e) {
		stopDragging();

		final long now = System.currentTimeMillis();
		if ((now - thePast) > 50) {
			stopFlinging(); // If over 50 milliseconds since dragging then don't fling
		}

		if (flingInertia != 0) {
			fling();
		}

		if (wasPlaying) {
			play();
		}
	}

	@Override
	public final void mouseWheelMoved(final MouseWheelEvent e) {
		final double center = this.getWidth() / 2.0;
		final int notches = e.getWheelRotation();
		double move = 0;
		if (notches < 0) {
			if(zoomedOutBeyondOneToOne){
				move = center + ((e.getX() - center) / (zoom - 1.0));
			} else {
				move = center + ((e.getX() - center) / zoom);
			}
			zoomIn();
		} else {
			if(zoomedOutBeyondOneToOne || zoom == 1){
				move = center - ((e.getX() - center) / (zoom + 1.0));
			} else {
				move = center - ((e.getX() - center) / zoom);
			}
			zoomOut();
		}
		moveEntireGraphingPanel(move);
	}

	// Key listener functionality
	@Override
	public final void keyPressed(final KeyEvent e) {
		switch (e.getKeyCode()) {
			// Play key bindings
			case KeyEvent.VK_SPACE: {
				play();
				break;
			}

			// Play key bindings
			case KeyEvent.VK_ESCAPE: {
				OpenLogViewerApp.getInstance().exitFullScreen();
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
				int localZoom = zoom;
				if(zoomedOutBeyondOneToOne){
					localZoom = 1;
				}
				//Big scroll
				moveEntireGraphingPanel(-(this.getWidth() / 4) / localZoom);
				break;
			}

			case KeyEvent.VK_LEFT: {
				int localZoom = zoom;
				if(zoomedOutBeyondOneToOne){
					localZoom = 1;
				}
				if (e.getModifiers() == InputEvent.CTRL_MASK) {
					//Big scroll
					moveEntireGraphingPanel(-(this.getWidth() / 4) / localZoom);
				} else {
					final int center = this.getWidth() / 2;
					moveEntireGraphingPanel(center - localZoom);
				}
				break;
			}

			case KeyEvent.VK_KP_LEFT: {
				int localZoom = zoom;
				if(zoomedOutBeyondOneToOne){
					localZoom = 1;
				}
				if (e.getModifiers() == InputEvent.CTRL_MASK) {
					//Big scroll
					moveEntireGraphingPanel(-(this.getWidth() / 4) / localZoom);
				} else {
					final int center = this.getWidth() / 2;
					moveEntireGraphingPanel(center - localZoom);
				}
				break;
			}

			// Scroll right key bindings
			case KeyEvent.VK_PAGE_DOWN: {
				int localZoom = zoom;
				if(zoomedOutBeyondOneToOne){
					localZoom = 1;
				}
				//Big scroll
				moveEntireGraphingPanel(this.getWidth() + (this.getWidth() / 4) / localZoom);
				break;
			}

			case KeyEvent.VK_RIGHT: {
				int localZoom = zoom;
				if(zoomedOutBeyondOneToOne){
					localZoom = 1;
				}
				if (e.getModifiers() == InputEvent.CTRL_MASK) {
					//Big scroll
					moveEntireGraphingPanel(this.getWidth() + (this.getWidth() / 4) / localZoom);
				} else {
					final int center = this.getWidth() / 2;
					moveEntireGraphingPanel(center + localZoom);
				}
				break;
			}

			case KeyEvent.VK_KP_RIGHT: {
				int localZoom = zoom;
				if(zoomedOutBeyondOneToOne){
					localZoom = 1;
				}
				if (e.getModifiers() == InputEvent.CTRL_MASK) {
					//Big scroll
					moveEntireGraphingPanel(this.getWidth() + (this.getWidth() / 4) / localZoom);
				} else {
					final int center = this.getWidth() / 2;
					moveEntireGraphingPanel(center + localZoom);
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
				if (e.getModifiers() == InputEvent.CTRL_MASK) {
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
				if (e.getModifiers() == InputEvent.CTRL_MASK) {
					zoomOut();
				}
				break;
			}
		}
	}

	@Override
	public final void keyReleased(final KeyEvent e) {
		// What should be here?
		// Ben says eventually there might be stuff here, and it is required implementation for the KeyListener interface.
	}

	@Override
	public final void keyTyped(final KeyEvent e) {
		// What should be here?
		// Ben says eventually there might be stuff here, and it is required implementation for the KeyListener interface.
	}
}
