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
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
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

import org.diyefi.openlogviewer.OpenLogViewer;
import org.diyefi.openlogviewer.genericlog.GenericLog;

public class EntireGraphingPanel extends JPanel implements ActionListener, MouseMotionListener, MouseListener, MouseWheelListener, KeyListener, ComponentListener {
	private static final long serialVersionUID = 1L;

	private MultiGraphLayeredPane multiGraph;
	private GraphPositionPanel graphPositionPanel;
	private double graphPosition;
	private int graphSize;
	private boolean playing;
	private boolean wasPlaying;
	private Timer playTimer;
	private Timer flingTimer;
	private boolean dragging;
	private boolean flinging;
	private long thePastMouseDragged;
	private long thePastLeftArrow;
	private long thePastRightArrow;
	private int scrollAcceleration;
	private int prevDragXCoord;
	private int flingInertia;
	private int zoom;
	private boolean zoomedOutBeyondOneToOne;
	private int oldComponentWidth;

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
		zoom = 1;
		zoomedOutBeyondOneToOne = false;
		oldComponentWidth = this.getWidth();
		resetGraphPosition();
		setGraphSize(0);
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
		thePastMouseDragged = System.currentTimeMillis();
		thePastLeftArrow = System.currentTimeMillis();
		thePastRightArrow = System.currentTimeMillis();
		scrollAcceleration = 0;
	}

	public final void actionPerformed(final ActionEvent e) {
		if (playing && graphPosition < getGraphPositionMax()) {
			if(zoomedOutBeyondOneToOne){
				moveGraphPosition(zoom);
			} else {
				moveGraphPosition(1);
			}
		} else if ((flinging && graphPosition < getGraphPositionMax()) && (graphPosition > getGraphPositionMin())) {
			if (flingInertia == 0) {
				stopFlinging();
			} else {
				moveEntireGraphingPanel(flingInertia);
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


	/**
	 * The tightest the user should be allowed to zoom in.
	 */
	private final int getTightestZoom(){
		return this.getWidth() - 1;
	}

	/**
	 * The widest the user should be allowed to zoom out.
	 */
	private final int getWidestZoom(){
		return Integer.MAX_VALUE;
	}

	/**
	 * Zoom in by one. This control zooms finer than the coarse zoom control.
	 * This assumes you are zooming in on the data centered in the screen.
	 * If you need to zoom in on a different location then you must move
	 * the graph accordingly.
	 */
	public final void zoomIn() {
		final double graphWidth = this.getWidth();
		double move = 0;

		if(zoomedOutBeyondOneToOne) {
			if (zoom == 2) {
				zoomedOutBeyondOneToOne = false;
			}
			zoom--;
			move = graphWidth / (double)(zoom * 2);
		} else if (zoom < getTightestZoom()) {
			move = graphWidth / (double)(zoom * 2);
			zoom++;
		}

		moveEntireGraphingPanel(move);
	}

	/**
	 * Zoom in using steps larger the further away from 1:1 you are.
	 * This assumes you are zooming in on the data centered in the screen.
	 * If you need to zoom in on a different location then you must use
	 * zoomIn() repeatedly coupled with a move each time.
	 */
	public final void zoomInCoarse(){
		final int zoomAmount = (int)Math.sqrt(zoom);
		for (int i = 0; i < zoomAmount; i++){
			zoomIn();
		}
	}

	/**
	 * Zoom out by one. This control zooms finer than the coarse zoom control.
	 * This assumes you are zooming out from the data centered in the screen.
	 * If you need to zoom out from a different location then you must move
	 * the graph accordingly.
	 */
	public final void zoomOut() {
		final double graphWidth = this.getWidth();
		double move = 0;

		if (!zoomedOutBeyondOneToOne) {
			if (zoom == 1) {
				zoomedOutBeyondOneToOne = true;
				zoom = 2;
				move = graphWidth / (double)(zoom * 2);
			} else {
				move = graphWidth / (double)(zoom * 2);
				zoom--;
			}
		} else if (zoom < getWidestZoom()){
			zoom++;
			move = graphWidth / (double)(zoom * 2);
		}

		moveEntireGraphingPanel(-move);
	}

	/**
	 * Zoom out using steps larger the further away from 1:1 you are.
	 * This assumes you are zooming out with the data centered in the screen.
	 * If you need to zoom out on a different location then you must use
	 * zoomOut() repeatedly coupled with a move each time.
	 */
	public final void zoomOutCoarse(){
		final int zoomAmount = (int)Math.sqrt(zoom);
		for (int i = 0; i < zoomAmount; i++){
			zoomOut();
		}
	}

	/**
	 * Zoom the graph so that if it is centered, then the
	 * entire graph will fit within the display. Usually
	 * this will result in ultimately zooming out, but if the
	 * graph is small enough and/or the display is large enough
	 * then zooming in will be more appropriate.
	 *
	 * If the graph will fit perfectly inside the display
	 * then it will be sized down one more time so that
	 * there is always at least 4 pixels of blank space to
	 * the left and right of the graph so the user will
	 * know they are seeing the entire graph trace.
	 */
	private final void zoomGraphToFit(final int dataPointsToFit){
		final int graphWindowWidth = this.getWidth() - 8; //Remove 4 pixels per side.
		int dataPointsThatFitInDisplay = 0;
		if (zoomedOutBeyondOneToOne){
			dataPointsThatFitInDisplay = graphWindowWidth * zoom;
		} else {
			dataPointsThatFitInDisplay =  graphWindowWidth / zoom;
		}

		// Zoom in until the data no longer fits in the display.
		while (dataPointsToFit < dataPointsThatFitInDisplay && zoom != getTightestZoom()){
			zoomIn();
			if (zoomedOutBeyondOneToOne){
				dataPointsThatFitInDisplay = graphWindowWidth * zoom;
			} else {
				dataPointsThatFitInDisplay =  graphWindowWidth / zoom;
			}
		}

		// Zoom out one or more times until the data just fits in the display.
		while (dataPointsToFit > dataPointsThatFitInDisplay && zoom != getWidestZoom()){
			zoomOut();
			if (zoomedOutBeyondOneToOne){
				dataPointsThatFitInDisplay = graphWindowWidth * zoom;
			} else {
				dataPointsThatFitInDisplay =  graphWindowWidth / zoom;
			}
		}
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

	private final double getGraphPositionMin(){
		double min = 0.0;
		if(zoomedOutBeyondOneToOne){
			min = -((this.getWidth() - 1) * zoom);
		} else {
			min = -(((double)this.getWidth() - 1.0) / (double)zoom);
		}
		return min;
	}

	public final double getGraphPosition() {
		return graphPosition;
	}

	private final int getGraphPositionMax() {
		return graphSize;
	}

	public final void setGraphPosition(final double newPos) {
		graphPosition = newPos;
		repaint();
	}

	/**
	 * How many available data records we are dealing with.
	 */
	public final void setGraphSize(final int newGraphSize) {
		graphSize = newGraphSize;
		if(graphSize > 0){
			centerGraphPosition(0, graphSize);
			zoomGraphToFit(graphSize);
		}
	}

	/**
	 * Move the graph to the right so that only one valid
	 * data point shows on the right-most part of the display.
	 */
	private final void resetGraphPosition() {
		setGraphPosition(getGraphPositionMin());
	}

	/**
	 * Move the graph to the center of the two provided graph positions
	 * so that there are equal data points to the left and to the right.
	 *
	 * Right now the method is expecting to get integer data points as
	 * it should be impossible to select fractions of a data point.
	 */
	private final void centerGraphPosition(final int beginPosition, final int endPosition) {
		final int halfScreen = this.getWidth() / 2;
		double pointsThatFitInHalfScreen = 0;
		if (zoomedOutBeyondOneToOne){
			pointsThatFitInHalfScreen = halfScreen * zoom;
		} else {
			pointsThatFitInHalfScreen = halfScreen / zoom;
		}
		final int distanceBetweenPositions = endPosition - beginPosition;
		final double halfwayBetweenTwoPositions = distanceBetweenPositions / 2;
		final double centerPosition = (beginPosition + halfwayBetweenTwoPositions) - pointsThatFitInHalfScreen;
		setGraphPosition(centerPosition);
	}

	/**
	 * Move the graph to the left so that only one valid
	 * data point shows on the left-most part of the display.
	 */
	private void goToLastGraphPosition() {
		setGraphPosition(getGraphPositionMax());
	}

	public final boolean isPlaying() {
		return playing;
	}

	public final int getZoom() {
		return zoom;
	}

	/**
	 * Take the current graph position and move amount positions forward.
	 */
	private void moveGraphPosition(final double amount) {
		final double newPos = graphPosition + amount;
		if (newPos > getGraphPositionMax()){
			goToLastGraphPosition();
		} else if (newPos < getGraphPositionMin()){
			resetGraphPosition();
		} else {
			setGraphPosition(newPos);
		}
	}

	/**
	 * Move the graph position to newPosition where newPosition is dictated by
	 * an x screen coordinate.
	 */
	private void moveEntireGraphingPanel(final double newPosition) {
		double move = -1.0;
		if(zoomedOutBeyondOneToOne){
			move = newPosition * zoom;
		} else {
			move = newPosition / zoom;
		}
		if (graphPosition + move < getGraphPositionMax()) {
			if (graphPosition + move < getGraphPositionMin()) {
				resetGraphPosition();
			} else {
				moveGraphPosition(move);
			}
		} else {
			goToLastGraphPosition();
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
			final int half = this.getWidth() / 2;
			moveEntireGraphingPanel(e.getX() - half);
		} else {
			stopDragging();
			stopFlinging();
		}
	}

	@Override
	public final void mouseDragged(final MouseEvent e) {
		dragging = true;
		final int xMouseCoord = e.getX();
		if ((prevDragXCoord > 0) && (prevDragXCoord != xMouseCoord)) {
			moveEntireGraphingPanel(prevDragXCoord - xMouseCoord);
			flingInertia = ((prevDragXCoord - xMouseCoord) * 2);
			thePastMouseDragged = System.currentTimeMillis();
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
		if ((now - thePastMouseDragged) > 50) {
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
		final int xMouseCoord = e.getX();
		final double center = this.getWidth() / 2.0;
		final int notches = e.getWheelRotation();
		double move = 0;
		final int zoomAmount = (int)Math.sqrt(zoom);
		if (notches < 0) {
			for (int i = 0; i < zoomAmount; i++){
				if(zoomedOutBeyondOneToOne){
					move = (xMouseCoord - center) / (zoom - 1.0);
				} else {
					move = (xMouseCoord - center) / zoom;
				}
				if (!(!zoomedOutBeyondOneToOne && zoom == getTightestZoom())){
					zoomIn();
					moveEntireGraphingPanel(move);
				}
			}
		} else {
			for (int i = 0; i < zoomAmount; i++){
				if(zoomedOutBeyondOneToOne || zoom == 1){
					move = -(xMouseCoord - center) / (zoom + 1.0);
				} else {
					move = -(xMouseCoord - center) / zoom;
				}
				if (!(zoomedOutBeyondOneToOne && zoom == getWidestZoom())){
					zoomOut();
					moveEntireGraphingPanel(move);
				}
			}
		}
	}

	// Key listener functionality
	@Override
	public final void keyPressed(final KeyEvent e) {
		switch (e.getKeyCode()) {
			// Play key binding
			case KeyEvent.VK_SPACE: {
				play();
				break;
			}

			// Enter full screen key binding
			case KeyEvent.VK_ENTER: {
				if (e.getModifiers() == InputEvent.ALT_MASK
						&& e.getKeyLocation() == KeyEvent.KEY_LOCATION_STANDARD) {
					OpenLogViewer.getInstance().enterFullScreen();
				}
				break;
			}

			// Exit full screen key binding
			case KeyEvent.VK_ESCAPE: {
				OpenLogViewer.getInstance().exitFullScreen();
				break;
			}

			// Toggle full screen key binding
			case KeyEvent.VK_F11: {
				OpenLogViewer.getInstance().toggleFullScreen();
				break;
			}

			// Home key binding
			case KeyEvent.VK_HOME: {
				resetGraphPosition();
				break;
			}

			// End key binding
			case KeyEvent.VK_END: {
				goToLastGraphPosition();
				break;
			}

			// Scroll left key bindings
			case KeyEvent.VK_PAGE_UP: {
				//Big scroll
				moveEntireGraphingPanel(-(this.getWidth() * 0.75));
				break;
			}

			case KeyEvent.VK_LEFT: {
				int localZoom = zoom;
				if(zoomedOutBeyondOneToOne){
					localZoom = 1;
				}
				if (e.getModifiers() == InputEvent.CTRL_MASK) {
					//Big scroll
					moveEntireGraphingPanel(-(this.getWidth() * 0.75));
				} else {
					final long now = System.currentTimeMillis();
					final long delay = now - thePastLeftArrow;
					if(delay < 50){
						scrollAcceleration++;
						moveEntireGraphingPanel(-localZoom - (scrollAcceleration * localZoom));
					} else {
						scrollAcceleration = 0;
						moveEntireGraphingPanel(-localZoom);
					}
					thePastLeftArrow = System.currentTimeMillis();
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
					moveEntireGraphingPanel(-(this.getWidth() * 0.75));
				} else {
					final long now = System.currentTimeMillis();
					final long delay = now - thePastLeftArrow;
					if(delay < 50){
						scrollAcceleration++;
						moveEntireGraphingPanel(-localZoom - (scrollAcceleration * localZoom));
					} else {
						scrollAcceleration = 0;
						moveEntireGraphingPanel(-localZoom);
					}
					thePastLeftArrow = System.currentTimeMillis();
				}
				break;
			}

			// Scroll right key bindings
			case KeyEvent.VK_PAGE_DOWN: {
				//Big scroll
				moveEntireGraphingPanel(this.getWidth() * 0.75);
				break;
			}

			case KeyEvent.VK_RIGHT: {
				int localZoom = zoom;
				if(zoomedOutBeyondOneToOne){
					localZoom = 1;
				}
				if (e.getModifiers() == InputEvent.CTRL_MASK) {
					//Big scroll
					moveEntireGraphingPanel(this.getWidth() * 0.75);
				} else {
					final long now = System.currentTimeMillis();
					final long delay = now - thePastRightArrow;
					if(delay < 50){
						scrollAcceleration++;
						moveEntireGraphingPanel(localZoom + (scrollAcceleration * localZoom));
					} else {
						scrollAcceleration = 0;
						moveEntireGraphingPanel(localZoom);
					}
					thePastRightArrow = System.currentTimeMillis();
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
					moveEntireGraphingPanel(this.getWidth() * 0.75);
				} else {
					final long now = System.currentTimeMillis();
					final long delay = now - thePastRightArrow;
					if(delay < 50){
						scrollAcceleration++;
						moveEntireGraphingPanel(localZoom + (scrollAcceleration * localZoom));
					} else {
						scrollAcceleration = 0;
						moveEntireGraphingPanel(localZoom);
					}
					thePastRightArrow = System.currentTimeMillis();
				}
				break;
			}

			// Zoom in key bindings
			case KeyEvent.VK_UP:
			case KeyEvent.VK_KP_UP: {
				zoomInCoarse();
				break;
			}

			case KeyEvent.VK_ADD: {
				if (e.getModifiers() == InputEvent.CTRL_MASK) {
					zoomInCoarse();
				}
				break;
			}

			// Zoom out key bindings
			case KeyEvent.VK_DOWN:
			case KeyEvent.VK_KP_DOWN: {
				zoomOutCoarse();
				break;
			}

			case KeyEvent.VK_SUBTRACT: {
				if (e.getModifiers() == InputEvent.CTRL_MASK) {
					zoomOutCoarse();
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

	@Override
	public void componentHidden(ComponentEvent e) {
		// Ben says eventually there might be stuff here, and it is required implementation for the ComponentListener interface.
	}

	@Override
	public void componentMoved(ComponentEvent e) {
		// Same code as resize event because the Mac sort of treats resizing as a move
		int newWidth = this.getWidth();
		if(newWidth != oldComponentWidth){
			double move = 0.0;
			int amount = newWidth - oldComponentWidth;
			if(zoomedOutBeyondOneToOne){
				move = -(amount * zoom);
			} else {
				move = -((double)amount / (double)zoom);
			}
			move /= 2.0;
			oldComponentWidth = newWidth;
			moveGraphPosition(move);
		}
	}

	@Override
	public void componentResized(ComponentEvent e) {
		int newWidth = this.getWidth();
		if(newWidth != oldComponentWidth){
			double move = 0.0;
			int amount = newWidth - oldComponentWidth;
			if(zoomedOutBeyondOneToOne){
				move = -(amount * zoom);
			} else {
				move = -((double)amount / (double)zoom);
			}
			move /= 2.0;
			oldComponentWidth = newWidth;
			moveGraphPosition(move);
		}
	}

	@Override
	public void componentShown(ComponentEvent e) {
		// Ben says eventually there might be stuff here, and it is required implementation for the ComponentListener interface.
	}
}
