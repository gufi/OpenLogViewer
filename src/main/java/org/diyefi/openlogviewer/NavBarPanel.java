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

package org.diyefi.openlogviewer;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

public class NavBarPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private static final int PREFERRED_WIDTH = 800;
	private static final int PREFERRED_HEIGHT = 18;
	private static final int SPACER_WIDTH = 16;

	private final JButton zoomInButton;
	private final JButton zoomResetRatioButton;
	private final JButton zoomResetFitButton;
	private final JButton zoomOutButton;
	private final JButton pausePlayButton;
	private final JButton slowDownButton;
	private final JButton resetPlaySpeedButton;
	private final JButton speedUpButton;
	private final JButton moveToBeginningButton;
	private final JButton moveBackwardCoarseButton;
	private final JButton moveBackwardButton;
	private final JButton moveToCenterButton;
	private final JButton moveForwardButton;
	private final JButton moveForwardCoarseButton;
	private final JButton moveToEndButton;
	private final JButton openButton;
	private final JButton openLastButton;


	public NavBarPanel() {
		zoomInButton = new JButton();
		zoomResetRatioButton = new JButton();
		zoomResetFitButton = new JButton();
		zoomOutButton = new JButton();
		pausePlayButton = new JButton();
		slowDownButton = new JButton();
		resetPlaySpeedButton = new JButton();
		speedUpButton = new JButton();
		moveToBeginningButton = new JButton();
		moveBackwardCoarseButton = new JButton();
		moveBackwardButton = new JButton();
		moveToCenterButton = new JButton();
		moveForwardButton = new JButton();
		moveForwardCoarseButton = new JButton();
		moveToEndButton = new JButton();
		openButton = new JButton();
		openLastButton = new JButton();
		initComponents();
	}

	private void initComponents() {
		this.setName("navBarPanel");
		this.setPreferredSize(new Dimension(PREFERRED_WIDTH, PREFERRED_HEIGHT));
		this.setLayout(new FlowLayout(FlowLayout.RIGHT, 0, 0));

		zoomInButton.setIcon(new ImageIcon(getClass().getResource("zoomIn.png"))); // NOI18N
		zoomInButton.setAlignmentY(0.0F);
		zoomInButton.setBorder(null);
		zoomInButton.setBorderPainted(false);
		zoomInButton.setContentAreaFilled(false);
		zoomInButton.setName("zoomInButton");
		zoomInButton.setRequestFocusEnabled(false);

		zoomInButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(final MouseEvent e) {
				zoomInButtonMouseReleased(e);
			}
		});
		this.add(zoomInButton);

		zoomResetRatioButton.setIcon(new ImageIcon(getClass().getResource("zoomResetRatio.png"))); // NOI18N
		zoomResetRatioButton.setAlignmentY(0.0F);
		zoomResetRatioButton.setBorder(null);
		zoomResetRatioButton.setBorderPainted(false);
		zoomResetRatioButton.setContentAreaFilled(false);
		zoomResetRatioButton.setName("zoomResetRatioButton"); // NOI18N
		zoomResetRatioButton.setRequestFocusEnabled(false);
		zoomResetRatioButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(final MouseEvent e) {
				zoomResetRatioButtonMouseReleased(e);
			}
		});
		this.add(zoomResetRatioButton);

		zoomResetFitButton.setIcon(new ImageIcon(getClass().getResource("zoomResetFit.png"))); // NOI18N
		zoomResetFitButton.setAlignmentY(0.0F);
		zoomResetFitButton.setBorder(null);
		zoomResetFitButton.setBorderPainted(false);
		zoomResetFitButton.setContentAreaFilled(false);
		zoomResetFitButton.setName("zoomResetFitButton"); // NOI18N
		zoomResetFitButton.setRequestFocusEnabled(false);
		zoomResetFitButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(final MouseEvent e) {
				zoomResetFitButtonMouseReleased(e);
			}
		});
		this.add(zoomResetFitButton);

		zoomOutButton.setIcon(new ImageIcon(getClass().getResource("zoomOut.png"))); // NOI18N
		zoomOutButton.setAlignmentY(0.0F);
		zoomOutButton.setBorder(null);
		zoomOutButton.setBorderPainted(false);
		zoomOutButton.setContentAreaFilled(false);
		zoomOutButton.setName("zoomOutButton");
		zoomOutButton.setRequestFocusEnabled(false);
		zoomOutButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(final MouseEvent e) {
				zoomOutButtonMouseReleased(e);
			}
		});
		this.add(zoomOutButton);

		this.add(Box.createHorizontalStrut(SPACER_WIDTH));

		slowDownButton.setIcon(new ImageIcon(getClass().getResource("slowDown.png"))); // NOI18N
		slowDownButton.setAlignmentY(0.0F);
		slowDownButton.setBorder(null);
		slowDownButton.setBorderPainted(false);
		slowDownButton.setContentAreaFilled(false);
		slowDownButton.setName("slowDownButton");
		slowDownButton.setRequestFocusEnabled(false);
		slowDownButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(final MouseEvent e) {
				slowDownButtonMouseReleased(e);
			}
		});
		this.add(slowDownButton);

		resetPlaySpeedButton.setIcon(new ImageIcon(getClass().getResource("resetPlaySpeed.png"))); // NOI18N
		resetPlaySpeedButton.setAlignmentY(0.0F);
		resetPlaySpeedButton.setBorder(null);
		resetPlaySpeedButton.setBorderPainted(false);
		resetPlaySpeedButton.setContentAreaFilled(false);
		resetPlaySpeedButton.setName("resetPlaySpeedButton");
		resetPlaySpeedButton.setRequestFocusEnabled(false);
		resetPlaySpeedButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(final MouseEvent e) {
				resetPlaySpeedButtonMouseReleased(e);
			}
		});
		this.add(resetPlaySpeedButton);

		pausePlayButton.setIcon(new ImageIcon(getClass().getResource("play.png"))); // NOI18N
		pausePlayButton.setAlignmentY(0.0F);
		pausePlayButton.setBorder(null);
		pausePlayButton.setBorderPainted(false);
		pausePlayButton.setContentAreaFilled(false);
		pausePlayButton.setName("pausePlayButton");
		pausePlayButton.setRequestFocusEnabled(false);
		pausePlayButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(final MouseEvent e) {
				pausePlayButtonMouseReleased(e);
			}
		});
		this.add(pausePlayButton);

		speedUpButton.setIcon(new ImageIcon(getClass().getResource("speedUp.png"))); // NOI18N
		speedUpButton.setAlignmentY(0.0F);
		speedUpButton.setBorder(null);
		speedUpButton.setBorderPainted(false);
		speedUpButton.setContentAreaFilled(false);
		speedUpButton.setName("speedUpButton"); // NOI18N
		speedUpButton.setRequestFocusEnabled(false);
		speedUpButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(final MouseEvent e) {
				speedUpButtonMouseReleased(e);
			}
		});
		this.add(speedUpButton);

		this.add(Box.createHorizontalStrut(SPACER_WIDTH));

		moveToBeginningButton.setIcon(new ImageIcon(getClass().getResource("moveToBeginning.png"))); // NOI18N
		moveToBeginningButton.setAlignmentY(0.0F);
		moveToBeginningButton.setBorder(null);
		moveToBeginningButton.setBorderPainted(false);
		moveToBeginningButton.setContentAreaFilled(false);
		moveToBeginningButton.setName("moveToBeginningButton"); // NOI18N
		moveToBeginningButton.setRequestFocusEnabled(false);
		moveToBeginningButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(final MouseEvent e) {
				moveToBeginningButtonMouseReleased(e);
			}
		});
		this.add(moveToBeginningButton);

		moveBackwardCoarseButton.setIcon(new ImageIcon(getClass().getResource("moveBackwardCoarse.png"))); // NOI18N
		moveBackwardCoarseButton.setAlignmentY(0.0F);
		moveBackwardCoarseButton.setBorder(null);
		moveBackwardCoarseButton.setBorderPainted(false);
		moveBackwardCoarseButton.setContentAreaFilled(false);
		moveBackwardCoarseButton.setName("moveBackwardCoarseButton"); // NOI18N
		moveBackwardCoarseButton.setRequestFocusEnabled(false);
		moveBackwardCoarseButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(final MouseEvent e) {
				moveBackwardCoarseButtonMouseReleased(e);
			}
		});
		this.add(moveBackwardCoarseButton);

		moveBackwardButton.setIcon(new ImageIcon(getClass().getResource("moveBackward.png"))); // NOI18N
		moveBackwardButton.setAlignmentY(0.0F);
		moveBackwardButton.setBorder(null);
		moveBackwardButton.setBorderPainted(false);
		moveBackwardButton.setContentAreaFilled(false);
		moveBackwardButton.setName("moveBackwardButton"); // NOI18N
		moveBackwardButton.setRequestFocusEnabled(false);
		moveBackwardButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(final MouseEvent e) {
				moveBackwardButtonMouseReleased(e);
			}
		});
		this.add(moveBackwardButton);

		moveToCenterButton.setIcon(new ImageIcon(getClass().getResource("moveToCenter.png"))); // NOI18N
		moveToCenterButton.setAlignmentY(0.0F);
		moveToCenterButton.setBorder(null);
		moveToCenterButton.setBorderPainted(false);
		moveToCenterButton.setContentAreaFilled(false);
		moveToCenterButton.setName("moveToCenterButton"); // NOI18N
		moveToCenterButton.setRequestFocusEnabled(false);
		moveToCenterButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(final MouseEvent e) {
				moveToCenterButtonMouseReleased(e);
			}
		});
		this.add(moveToCenterButton);

		moveForwardButton.setIcon(new ImageIcon(getClass().getResource("moveForward.png"))); // NOI18N
		moveForwardButton.setAlignmentY(0.0F);
		moveForwardButton.setBorder(null);
		moveForwardButton.setBorderPainted(false);
		moveForwardButton.setContentAreaFilled(false);
		moveForwardButton.setName("moveForwardButton"); // NOI18N
		moveForwardButton.setRequestFocusEnabled(false);
		moveForwardButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(final MouseEvent e) {
				moveForwardButtonMouseReleased(e);
			}
		});
		this.add(moveForwardButton);

		moveForwardCoarseButton.setIcon(new ImageIcon(getClass().getResource("moveForwardCoarse.png"))); // NOI18N
		moveForwardCoarseButton.setAlignmentY(0.0F);
		moveForwardCoarseButton.setBorder(null);
		moveForwardCoarseButton.setBorderPainted(false);
		moveForwardCoarseButton.setContentAreaFilled(false);
		moveForwardCoarseButton.setName("moveForwardCoarseButton"); // NOI18N
		moveForwardCoarseButton.setRequestFocusEnabled(false);
		moveForwardCoarseButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(final MouseEvent e) {
				moveForwardCoarseButtonMouseReleased(e);
			}
		});
		this.add(moveForwardCoarseButton);

		moveToEndButton.setIcon(new ImageIcon(getClass().getResource("moveToEnd.png"))); // NOI18N
		moveToEndButton.setAlignmentY(0.0F);
		moveToEndButton.setBorder(null);
		moveToEndButton.setBorderPainted(false);
		moveToEndButton.setContentAreaFilled(false);
		moveToEndButton.setName("moveToEndButton"); // NOI18N
		moveToEndButton.setRequestFocusEnabled(false);
		moveToEndButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(final MouseEvent e) {
				moveToEndButtonMouseReleased(e);
			}
		});
		this.add(moveToEndButton);

		this.add(Box.createHorizontalStrut(SPACER_WIDTH));

		openButton.setIcon(new ImageIcon(getClass().getResource("open.png"))); // NOI18N
		openButton.setAlignmentY(0.0F);
		openButton.setBorder(null);
		openButton.setBorderPainted(false);
		openButton.setContentAreaFilled(false);
		openButton.setName("openButton"); // NOI18N
		openButton.setRequestFocusEnabled(false);
		openButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(final MouseEvent e) {
				openButtonMouseReleased(e);
			}
		});
		this.add(openButton);

		openLastButton.setIcon(new ImageIcon(getClass().getResource("openLast.png"))); // NOI18N
		openLastButton.setAlignmentY(0.0F);
		openLastButton.setBorder(null);
		openLastButton.setBorderPainted(false);
		openLastButton.setContentAreaFilled(false);
		openLastButton.setName("openLastButton"); // NOI18N
		openLastButton.setRequestFocusEnabled(false);
		openLastButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(final MouseEvent e) {
				openLastButtonMouseReleased(e);
			}
		});
		this.add(openLastButton);
	}

	public void updatePausePlayButton(){
		boolean playing = OpenLogViewer.getInstance().getEntireGraphingPanel().isPlaying();
		if (playing) {
			pausePlayButton.setIcon(new ImageIcon(getClass().getResource("pause.png"))); // NOI18N
		} else {
			pausePlayButton.setIcon(new ImageIcon(getClass().getResource("play.png"))); // NOI18N
		}
	}

	/**
	 * Modify the state of the PlayableLog zoom in 1 pixel up to 512 pixels
	 * @param evt
	 */
	private void zoomInButtonMouseReleased(final MouseEvent e) {
		OpenLogViewer.getInstance().getEntireGraphingPanel().zoomInCoarse();
	}

	/**
	 * Modify the state of the PlayableLog zoom in 1 pixel down to -1024 pixels
	 * @param evt
	 */
	private void zoomOutButtonMouseReleased(final MouseEvent e) {
		OpenLogViewer.getInstance().getEntireGraphingPanel().zoomOutCoarse();
	}

	/**
	 * Modify the state of the PlayableLog zoom to exactly 1
	 * @param evt
	 */
	private void zoomResetRatioButtonMouseReleased(final MouseEvent e) {
		OpenLogViewer.getInstance().getEntireGraphingPanel().zoomResetRatio();
	}

	/**
	 * Modify the state of the PlayableLog zoom to exactly 1
	 * @param evt
	 */
	private void zoomResetFitButtonMouseReleased(final MouseEvent e) {
		OpenLogViewer.getInstance().getEntireGraphingPanel().zoomGraphToFit();
	}

	/**
	 * Slows down the play back speed of the Playable Log
	 * @param evt
	 */
	private void slowDownButtonMouseReleased(final MouseEvent e) {
		OpenLogViewer.getInstance().getEntireGraphingPanel().slowDown();
	}

	/**
	 * Sets the play back speed of the Playable Log to the original base speed
	 * @param evt
	 */
	private void resetPlaySpeedButtonMouseReleased(final MouseEvent e) {
		OpenLogViewer.getInstance().getEntireGraphingPanel().resetPlaySpeed();
	}

	/**
	 * Modifies the state of the PlayableLog to begin playing if paused, or pause if playing
	 * @param evt
	 */
	private void pausePlayButtonMouseReleased(final MouseEvent e) {
		boolean playing = OpenLogViewer.getInstance().getEntireGraphingPanel().isPlaying();
		if (playing) {
			OpenLogViewer.getInstance().getEntireGraphingPanel().pause();
		} else {
			OpenLogViewer.getInstance().getEntireGraphingPanel().play();
		}
		this.updatePausePlayButton();
	}

	/**
	 * Speeds up the play back speed of the PlayableLog
	 * @param evt
	 */
	private void speedUpButtonMouseReleased(final MouseEvent e) {
		OpenLogViewer.getInstance().getEntireGraphingPanel().speedUp();
	}

	/**
	 * Moves the Playable Log to the beginning position
	 * @param evt
	 */
	private void moveToBeginningButtonMouseReleased(final MouseEvent e) {
		OpenLogViewer.getInstance().getEntireGraphingPanel().moveToBeginning();
	}

	/**
	 * Moves the Playable Log backward a large amount
	 * @param evt
	 */
	private void moveBackwardCoarseButtonMouseReleased(final MouseEvent e) {
		OpenLogViewer.getInstance().getEntireGraphingPanel().moveBackwardCoarse();
	}

	/**
	 * Moves the Playable Log backward a small amount
	 * @param evt
	 */
	private void moveBackwardButtonMouseReleased(final MouseEvent e) {
		OpenLogViewer.getInstance().getEntireGraphingPanel().moveBackward();
	}

	/**
	 * Centers the Playable Log in the display
	 * @param evt
	 */
	private void moveToCenterButtonMouseReleased(final MouseEvent e) {
		OpenLogViewer.getInstance().getEntireGraphingPanel().centerGraphPosition();
	}

	/**
	 * Moves the Playable Log forward a small amount
	 * @param evt
	 */
	private void moveForwardButtonMouseReleased(final MouseEvent e) {
		OpenLogViewer.getInstance().getEntireGraphingPanel().moveForward();
	}

	/**
	 * Moves the Playable Log forward a large amount
	 * @param evt
	 */
	private void moveForwardCoarseButtonMouseReleased(final MouseEvent e) {
		OpenLogViewer.getInstance().getEntireGraphingPanel().moveForwardCoarse();
	}

	/**
	 * Moves the Playable Log to the ending position
	 * @param evt
	 */
	private void moveToEndButtonMouseReleased(final MouseEvent e) {
		OpenLogViewer.getInstance().getEntireGraphingPanel().moveToEnd();
	}

	/**
	 * Allow the user to choose a new log to open
	 * @param evt
	 */
	private void openButtonMouseReleased(final MouseEvent e) {
		OpenLogViewer.getInstance().openChosenFile();
	}

	/**
	 * Re-open the last log file that was opened/loaded
	 * @param evt
	 */
	private void openLastButtonMouseReleased(final MouseEvent e) {
		OpenLogViewer.getInstance().openLastFile();
	}
}