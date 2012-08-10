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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.Timer;

import org.diyefi.openlogviewer.OpenLogViewer;
import org.diyefi.openlogviewer.genericlog.GenericLog;

public class EntireGraphingPanel extends JPanel implements ActionListener,
		MouseMotionListener,
		MouseListener,
		MouseWheelListener,
		ComponentListener {
	public static final int LEFT_OFFSCREEN_POINTS_ZOOMED_IN = 0;
	public static final int RIGHT_OFFSCREEN_POINTS_ZOOMED_IN = 3;
	public static final int LEFT_OFFSCREEN_POINTS_ZOOMED_OUT = 2;
	public static final int RIGHT_OFFSCREEN_POINTS_ZOOMED_OUT = 2;
	private static final long serialVersionUID = 1L;
	private static final int BASE_PLAY_SPEED = 10;
	private static final int BASE_FLING_SPEED = 10;
	private static final double COARSE_MOVEMENT_PERCENTAGE = 0.50;
	private static final int GRAPH_DISPLAY_WIDTH = 600;
	private static final int GRAPH_DISPLAY_HEIGHT = 400;
	private static final int BUTTON_DISPLAY_HEIGHT = 20;

	private final Action play = new AbstractAction() {
		private static final long serialVersionUID = 1L;
		public void actionPerformed(final ActionEvent e) {
			play();
	    }
	};

	private final Action enterFullScreen = new AbstractAction() {
		private static final long serialVersionUID = 1L;
		public void actionPerformed(final ActionEvent e) {
			OpenLogViewer.getInstance().enterFullScreen();
	    }
	};

	private final Action exitFullScreen = new AbstractAction() {
		private static final long serialVersionUID = 1L;
		public void actionPerformed(final ActionEvent e) {
			OpenLogViewer.getInstance().exitFullScreen();
	    }
	};

	private final Action toggleFullScreen = new AbstractAction() {
		private static final long serialVersionUID = 1L;
		public void actionPerformed(final ActionEvent e) {
			OpenLogViewer.getInstance().toggleFullScreen();
	    }
	};

	private final Action moveToBeginning = new AbstractAction() {
		private static final long serialVersionUID = 1L;
		public void actionPerformed(final ActionEvent e) {
			moveToBeginning();
	    }
	};

	private final Action moveToEnd = new AbstractAction() {
		private static final long serialVersionUID = 1L;
		public void actionPerformed(final ActionEvent e) {
			moveToEnd();
	    }
	};

	private final Action moveBackward = new AbstractAction() {
		private static final long serialVersionUID = 1L;
		public void actionPerformed(final ActionEvent e) {
			moveBackward();
	    }
	};

	private final Action moveBackwardCoarse = new AbstractAction() {
		private static final long serialVersionUID = 1L;
		public void actionPerformed(final ActionEvent e) {
			moveBackwardCoarse();
	    }
	};

	private final Action moveForward = new AbstractAction() {
		private static final long serialVersionUID = 1L;
		public void actionPerformed(final ActionEvent e) {
			moveForward();
	    }
	};

	private final Action moveForwardCoarse = new AbstractAction() {
		private static final long serialVersionUID = 1L;
		public void actionPerformed(final ActionEvent e) {
			moveForwardCoarse();
	    }
	};

	private final Action zoomInCoarse = new AbstractAction() {
		private static final long serialVersionUID = 1L;
		public void actionPerformed(final ActionEvent e) {
			zoomInCoarse();
	    }
	};

	private final Action zoomOutCoarse = new AbstractAction() {
		private static final long serialVersionUID = 1L;
		public void actionPerformed(final ActionEvent e) {
			zoomOutCoarse();
	    }
	};

	private final MultiGraphLayeredPane multiGraph;
	private final GraphPositionPanel graphPositionPanel;
	private double graphPosition;
	private int graphSize;
	private boolean playing;
	private boolean wasPlaying;
	private final Timer playTimer;
	private final Timer flingTimer;
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
		setName("graphinPanel");
		setLayout(new BorderLayout());
		multiGraph = new MultiGraphLayeredPane();
		multiGraph.setPreferredSize(new Dimension(GRAPH_DISPLAY_WIDTH, GRAPH_DISPLAY_HEIGHT));
		add(multiGraph, BorderLayout.CENTER);
		graphPositionPanel = new GraphPositionPanel();
		graphPositionPanel.setPreferredSize(new Dimension(GRAPH_DISPLAY_WIDTH, BUTTON_DISPLAY_HEIGHT));
		add(graphPositionPanel, BorderLayout.SOUTH);
		zoom = 1;
		zoomedOutBeyondOneToOne = false;
		oldComponentWidth = this.getWidth();
		resetGraphPosition();
		setGraphSize(0);
		playing = false;
		wasPlaying = false;
		playTimer = new Timer(BASE_PLAY_SPEED, this);
		playTimer.setInitialDelay(0);
		flingTimer = new Timer(BASE_FLING_SPEED, this);
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

		initKeyBindings();
	}

	public final void actionPerformed(final ActionEvent e) {

		//Play timer event fires
		if (e.getSource().equals(playTimer)) {
			doPlayAction();
		}

		//Fling timer event fires
		if (e.getSource().equals(flingTimer)) {
			doFlingAction();
		}
	}

	private void doPlayAction() {
		if (playing && graphPosition < getGraphPositionMax()) {
			if (zoomedOutBeyondOneToOne) {
				moveGraphPosition(zoom);
			} else {
				moveGraphPosition(1);
			}
		} else {
			pause();
		}
	}

	private void doFlingAction() {
		if ((flinging && graphPosition < getGraphPositionMax()) && (graphPosition > getGraphPositionMin())) {
			if (flingInertia == 0) {
				stopFlinging();
			} else {
				moveEntireGraphingPanel(flingInertia);
				if (flingInertia > 0) {
					flingInertia--;
				} else if (flingInertia < 0) {
					flingInertia++;
				} else {
					stopFlinging();
				}
			}
		} else {
			stopFlinging();
		}
	}

	public final MultiGraphLayeredPane getMultiGraphLayeredPane() {
		return multiGraph;
	}

	public final GraphPositionPanel getGraphPositionPanel() {
		return graphPositionPanel;
	}

	public final void setLog(final GenericLog genLog) {
		pause();
		multiGraph.setLog(genLog);
		graphPositionPanel.setLog(genLog);
	}


	/**
	 * The tightest the user should be allowed to zoom in.
	 */
	private int getTightestZoom() {
		return this.getWidth() - 1;
	}

	/**
	 * The widest the user should be allowed to zoom out.
	 */
	private int getWidestZoom() {
		return ((graphSize + 1) / 2);
	}

	/**
	 * Zoom in using steps larger the further away from 1:1 you are.
	 * This assumes you are zooming in on the data centered in the screen.
	 * If you need to zoom in on a different location then you must use
	 * zoomIn() repeatedly coupled with a move each time.
	 */
	public final void zoomInCoarse() {
		final int zoomAmount = (int) Math.sqrt(zoom);
		for (int i = 0; i < zoomAmount; i++) {
			zoomIn();
		}
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

		if (zoomedOutBeyondOneToOne) {
			if (zoom == 2) {
				zoomedOutBeyondOneToOne = false;
			}
			zoom--;
			move = graphWidth / (double) (zoom * 2);
		} else if (zoom < getTightestZoom()) {
			move = graphWidth / (double) (zoom * 2);
			zoom++;
		}

		moveEntireGraphingPanel(move);
	}

	/**
	 * Zoom the graph to a 1:1 pixel-to-data-point ratio.
	 */
	public final void zoomResetRatio() {
		if (zoomedOutBeyondOneToOne) {
			for (int i = zoom; i > 1; i--) {
				zoomIn();
			}
		} else {
			for (int i = zoom; i > 1; i--) {
				zoomOut();
			}
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
	public final void zoomGraphToFit(final int dataPointsToFit) {
		final int graphWindowWidth = this.getWidth() - 8; //Remove 4 pixels per side.
		int dataPointsThatFitInDisplay = 0;
		if (zoomedOutBeyondOneToOne) {
			dataPointsThatFitInDisplay = graphWindowWidth * zoom;
		} else {
			dataPointsThatFitInDisplay =  graphWindowWidth / zoom;
		}

		// Zoom in until the data no longer fits in the display.
		while (dataPointsToFit < dataPointsThatFitInDisplay && zoom != getTightestZoom()) {
			zoomIn();
			if (zoomedOutBeyondOneToOne) {
				dataPointsThatFitInDisplay = graphWindowWidth * zoom;
			} else {
				dataPointsThatFitInDisplay =  graphWindowWidth / zoom;
			}
		}

		// Zoom out one or more times until the data just fits in the display.
		while (dataPointsToFit > dataPointsThatFitInDisplay && zoom != getWidestZoom()) {
			zoomOut();
			if (zoomedOutBeyondOneToOne) {
				dataPointsThatFitInDisplay = graphWindowWidth * zoom;
			} else {
				dataPointsThatFitInDisplay =  graphWindowWidth / zoom;
			}
		}
	}

	/**
	 * Used by external sources that don't know or care about the size of the graph.
	 */
	public final void zoomGraphToFit() {
		zoomGraphToFit(graphSize);
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
				move = graphWidth / (double) (zoom * 2);
			} else {
				move = graphWidth / (double) (zoom * 2);
				zoom--;
			}
		} else if (zoom < getWidestZoom()) {
			zoom++;
			move = graphWidth / (double) (zoom * 2);
		}

		moveEntireGraphingPanel(-move);
	}

	/**
	 * Zoom out using steps larger the further away from 1:1 you are.
	 * This assumes you are zooming out with the data centered in the screen.
	 * If you need to zoom out on a different location then you must use
	 * zoomOut() repeatedly coupled with a move each time.
	 */
	public final void zoomOutCoarse() {
		final int zoomAmount = (int) Math.sqrt(zoom);
		for (int i = 0; i < zoomAmount; i++) {
			zoomOut();
		}
	}

	public final boolean isZoomedOutBeyondOneToOne() {
		return zoomedOutBeyondOneToOne;
	}

	/**
	 * Slows the speed of playback (exponentially)
	 */
	public final void slowDown() {
		final int currentDelay = playTimer.getDelay();
		long newDelay = currentDelay + (currentDelay / 6) + 1;
		if (newDelay > Integer.MAX_VALUE) {
			newDelay = Integer.MAX_VALUE;
		}
		playTimer.setDelay((int) newDelay);
	}

	/**
	 * Resets the speed of playback to the original speed
	 */
	public final void resetPlaySpeed() {
		playTimer.setDelay(BASE_PLAY_SPEED);
	}

	public final void play() {
		if (playing) {
			pause();
		} else {
			playing = true;
			playTimer.start();
		}
		OpenLogViewer.getInstance().getNavBarPanel().updatePausePlayButton();
	}

	public final void pause() {
		playing = false;
		playTimer.stop();
		OpenLogViewer.getInstance().getNavBarPanel().updatePausePlayButton();
	}

	/**
	 * Increases the speed of the graph exponentially until the delay is zero,
	 * at which speed cannot be advanced any further and will essentially update
	 * as fast as possible.
	 */
	public final void speedUp() {
		final int currentDelay = playTimer.getDelay();
		int newDelay = currentDelay - (currentDelay / 6) - 1;
		if (newDelay < 0) {
			newDelay = 0;
		}
		playTimer.setDelay(newDelay);
	}

	public final void fling() {
		flinging = true;
		flingTimer.start();
	}

	private double getGraphPositionMin() {
		double min = 0.0;
		if (zoomedOutBeyondOneToOne) {
			min = -((this.getWidth() - 1) * zoom);
		} else {
			min = -(((double) this.getWidth() - 1.0) / (double) zoom);
		}
		return min;
	}

	public final double getGraphPosition() {
		return graphPosition;
	}

	private int getGraphPositionMax() {
		if (zoom == getWidestZoom()) {
			int size = graphSize - (LEFT_OFFSCREEN_POINTS_ZOOMED_OUT * zoom);
			if (size < 0) {
				size = 0;
			}
			return size;
		}
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
		if (graphSize > 0) {
			zoomGraphToFit(graphSize);
			centerGraphPosition(0, graphSize);
		}
	}

	/**
	 * Move the graph to the right so that only one valid
	 * data point shows on the right-most part of the display.
	 */
	private void resetGraphPosition() {
		setGraphPosition(getGraphPositionMin());
	}

	/**
	 * Move the graph to the beginning with the first data point centered.
	 */
	public final void moveToBeginning() {
		resetGraphPosition();
		moveForwardPercentage(0.50);
	}

	/**
	 * Move the graph backward a small amount (with acceleration).
	 */
	public final void moveBackward() {
		int localZoom = zoom;
		if (zoomedOutBeyondOneToOne) {
			localZoom = 1;
		}
		final long now = System.currentTimeMillis();
		final long delay = now - thePastLeftArrow;
		if (delay < 50) {
			scrollAcceleration++;
			moveEntireGraphingPanel(-localZoom - (scrollAcceleration * localZoom));
		} else {
			scrollAcceleration = 0;
			moveEntireGraphingPanel(-localZoom);
		}
		thePastLeftArrow = System.currentTimeMillis();
	}

	/**
	 * Move the graph backward a large amount.
	 */
	public final void moveBackwardCoarse() {
		moveBackwardPercentage(COARSE_MOVEMENT_PERCENTAGE);
	}

	/**
	 * Move the graph backward by a percentage (amount) of the screen width.
	 * Percentages are expected in decimal form. For example, 0.50 for 50%.
	 */
	public final void moveBackwardPercentage(final double amount) {
		moveEntireGraphingPanel(-(this.getWidth() * amount));
	}

	/**
	 * Move the graph to the center of the two provided graph positions
	 * so that there are equal data points to the left and to the right.
	 *
	 * Right now the method is expecting to get integer data points as
	 * it should be impossible to select fractions of a data point.
	 */
	private void centerGraphPosition(final int beginPosition, final int endPosition) {
		final int halfScreen = this.getWidth() / 2;
		double pointsThatFitInHalfScreen = 0;
		if (zoomedOutBeyondOneToOne) {
			pointsThatFitInHalfScreen = halfScreen * zoom;
		} else {
			pointsThatFitInHalfScreen = halfScreen / (double) zoom;
		}
		final int distanceBetweenPositions = endPosition - beginPosition;
		final double halfwayBetweenTwoPositions = distanceBetweenPositions / 2d;
		final double centerPosition = (beginPosition + halfwayBetweenTwoPositions) - pointsThatFitInHalfScreen;
		setGraphPosition(centerPosition);
	}

	/**
	 * Used by external sources that don't know or care about the size of the graph.
	 */
	public final void centerGraphPosition() {
		centerGraphPosition(0, graphSize);
	}

	/**
	 * Move the graph forward a small amount (with acceleration).
	 */
	public final void moveForward() {
		int localZoom = zoom;
		if (zoomedOutBeyondOneToOne) {
			localZoom = 1;
		}
		final long now = System.currentTimeMillis();
		final long delay = now - thePastRightArrow;
		if (delay < 50) {
			scrollAcceleration++;
			moveEntireGraphingPanel(localZoom + (scrollAcceleration * localZoom));
		} else {
			scrollAcceleration = 0;
			moveEntireGraphingPanel(localZoom);
		}
		thePastRightArrow = System.currentTimeMillis();
	}

	/**
	 * Move the graph forward by a percentage (amount) of the screen width.
	 * Percentages are expected in decimal form. For example, 0.50 for 50%.
	 */
	private void moveForwardPercentage(final double amount) {
		moveEntireGraphingPanel(this.getWidth() * amount);
	}

	/**
	 * Move the graph forward a large amount.
	 */
	public final void moveForwardCoarse() {
		moveForwardPercentage(COARSE_MOVEMENT_PERCENTAGE);
	}

	/**
	 * Move the graph to the end with the last data point centered.
	 */
	public final void moveToEnd() {
		goToLastGraphPosition();
		moveBackwardPercentage(0.50);
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
	 * When the windows is resized, the graph needs to move to maintain the centering.
	 */
	public final void moveGraphDueToResize() {
		final int newWidth = this.getWidth();
		if (newWidth != oldComponentWidth) {
			double move = 0.0;
			final int amount = newWidth - oldComponentWidth;
			if (zoomedOutBeyondOneToOne) {
				move = -(amount * zoom);
			} else {
				move = -((double) amount / (double) zoom);
			}
			move /= 2.0;
			oldComponentWidth = newWidth;
			moveGraphPosition(move);
		}
	}

	/**
	 * Take the current graph position and move amount positions forward.
	 */
	private void moveGraphPosition(final double amount) {
		final double newPos = graphPosition + amount;
		if (newPos > getGraphPositionMax()) {
			goToLastGraphPosition();
		} else if (newPos < getGraphPositionMin()) {
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
		if (zoomedOutBeyondOneToOne) {
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
		flingTimer.stop();
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
		// Fred says thanks! :-)
	}

	@Override
	public final void mouseEntered(final MouseEvent e) {
		// What should be here?
		// Ben says eventually there might be stuff here, and it is required implementation for the MouseMovementListener interface.
		// Fred says thanks! :-)
	}

	@Override
	public final void mouseExited(final MouseEvent e) {
		// What should be here?
		// Ben says eventually there might be stuff here, and it is required implementation for the MouseMovementListener interface.
		// Fred says thanks! :-)
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
		final int notches = e.getWheelRotation();
		if (notches < 0) {
			mouseWheelMovedForward(e.getX());
		} else {
			mouseWheelMovedBackward(e.getX());
		}
	}

	private void mouseWheelMovedForward(final int xMouseCoord) {
		final double center = this.getWidth() / 2.0;
		double move = 0;
		final int zoomAmount = (int) Math.sqrt(zoom);
		for (int i = 0; i < zoomAmount; i++) {
			if (zoomedOutBeyondOneToOne) {
				move = (xMouseCoord - center) / (zoom - 1.0);
			} else {
				move = (xMouseCoord - center) / zoom;
			}
			if (!(!zoomedOutBeyondOneToOne && zoom == getTightestZoom())) {
				zoomIn();
				moveEntireGraphingPanel(move);
			}
		}
	}

	private void mouseWheelMovedBackward(final int xMouseCoord) {
		final double center = this.getWidth() / 2.0;
		double move = 0;
		final int zoomAmount = (int) Math.sqrt(zoom);
		for (int i = 0; i < zoomAmount; i++) {
			if (zoomedOutBeyondOneToOne || zoom == 1) {
				move = -(xMouseCoord - center) / (zoom + 1.0);
			} else {
				move = -(xMouseCoord - center) / zoom;
			}
			if (!(zoomedOutBeyondOneToOne && zoom == getWidestZoom())) {
				zoomOut();
				moveEntireGraphingPanel(move);
			}
		}
	}

	@Override
	public void componentHidden(final ComponentEvent e) {
		// Ben says eventually there might be stuff here, and it is required implementation for the ComponentListener interface.
		// Fred says thanks! :-)
	}

	// Call resize event handler because the Mac sort of treats resizing as a move
	@Override
	public final void componentMoved(final ComponentEvent e) {
		moveGraphDueToResize();
	}

	@Override
	public final void componentResized(final ComponentEvent e) {
		moveGraphDueToResize();
	}

	@Override
	public void componentShown(final ComponentEvent e) {
		// Ben says eventually there might be stuff here, and it is required implementation for the ComponentListener interface.
		// Fred says thanks! :-)
	}

	private void initKeyBindings() {
		// Play key bindings
		getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("SPACE"), "play");
		getActionMap().put("play", play);

		// Enter full screen key bindings
		getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("alt ENTER"), "enterFullScreen");
		getActionMap().put("enterFullScreen", enterFullScreen);

		// Exit full screen key bindings
		getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ESCAPE"), "exitFullScreen");
		getActionMap().put("exitFullScreen", exitFullScreen);

		// Toggle full screen key bindings
		getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("F11"), "toggleFullScreen");
		getActionMap().put("toggleFullScreen", toggleFullScreen);

		// Move to beginning key bindings
		getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("HOME"), "moveToBeginning");
		getActionMap().put("moveToBeginning", moveToBeginning);

		// Move to end key bindings
		getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("END"), "moveToEnd");
		getActionMap().put("moveToEnd", moveToEnd);

		// Move backward key bindings
		getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("LEFT"), "moveBackward");
		getActionMap().put("moveBackward", moveBackward);
		getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("PAGE_UP"), "moveBackwardCoarse");
		getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ctrl LEFT"), "moveBackwardCoarse");
		getActionMap().put("moveBackwardCoarse", moveBackwardCoarse);

		// Move forward key bindings
		getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("RIGHT"), "moveForward");
		getActionMap().put("moveForward", moveForward);
		getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("PAGE_DOWN"), "moveForwardCoarse");
		getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ctrl RIGHT"), "moveForwardCoarse");
		getActionMap().put("moveForwardCoarse", moveForwardCoarse);

		// Zoom in key bindings
		getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("UP"), "zoomInCoarse");
		getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ctrl ADD"), "zoomInCoarse");
		getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ctrl EQUALS"), "zoomInCoarse");
		getActionMap().put("zoomInCoarse", zoomInCoarse);

		// Zoom out key bindings
		getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("DOWN"), "zoomOutCoarse");
		getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ctrl MINUS"), "zoomOutCoarse");
		getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ctrl SUBTRACT"), "zoomOutCoarse");
		getActionMap().put("zoomOutCoarse", zoomOutCoarse);
	}
}
