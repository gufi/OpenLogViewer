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
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

public class NavBarPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private static final int PREFERRED_WIDTH = 800;
	private static final int PREFERRED_HEIGHT = 18;
	private static final int SPACER_WIDTH = 16;

	private static final String BUTTON_DIR = "buttons/";

	private static final Icon PLAY_ICON = new ImageIcon(NavBarPanel.class.getResource(BUTTON_DIR + Images.PLAY));
	private static final Icon PAUSE_ICON = new ImageIcon(NavBarPanel.class.getResource(BUTTON_DIR + Images.PAUSE));

	private final JButton playAndPause = new JButton();

	public NavBarPanel() {
		setName(NavBarPanel.class.getSimpleName());
		setPreferredSize(new Dimension(PREFERRED_WIDTH, PREFERRED_HEIGHT));
		setLayout(new FlowLayout(FlowLayout.RIGHT, 0, 0));

		setupZoomButtons();
		add(Box.createHorizontalStrut(SPACER_WIDTH));
		setupPlayButtons();
		add(Box.createHorizontalStrut(SPACER_WIDTH));
		setupNavigationButtons();
		add(Box.createHorizontalStrut(SPACER_WIDTH));
		setupFileButtons();
	}

	private void setupZoomButtons() {
		final JButton zoomIn = new JButton();
		zoomIn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(final MouseEvent e) {
				if (zoomIn.contains(e.getPoint())) {
					zoomInButtonMouseReleased();
				}
			}
		});
		setupAndAddButton(zoomIn, Images.ZOOM_IN);

		final JButton zoomResetRatio = new JButton();
		zoomResetRatio.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(final MouseEvent e) {
				if (zoomResetRatio.contains(e.getPoint())) {
					zoomResetRatioButtonMouseReleased();
				}
			}
		});
		setupAndAddButton(zoomResetRatio, Images.ZOOM_RESET_RATIO);

		final JButton zoomResetFit = new JButton();
		zoomResetFit.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(final MouseEvent e) {
				if (zoomResetFit.contains(e.getPoint())) {
					zoomResetFitButtonMouseReleased();
				}
			}
		});
		setupAndAddButton(zoomResetFit, Images.ZOOM_RESET_FIT);

		final JButton zoomOut = new JButton();
		zoomOut.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(final MouseEvent e) {
				if (zoomOut.contains(e.getPoint())) {
					zoomOutButtonMouseReleased();
				}
			}
		});
		setupAndAddButton(zoomOut, Images.ZOOM_OUT);
	}

	private void setupPlayButtons() {
		final JButton slowDown = new JButton();
		slowDown.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(final MouseEvent e) {
				if (slowDown.contains(e.getPoint())) {
					slowDownButtonMouseReleased();
				}
			}
		});
		setupAndAddButton(slowDown, Images.SLOW_DOWN);

		final JButton resetPlaySpeed = new JButton();
		resetPlaySpeed.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(final MouseEvent e) {
				if (resetPlaySpeed.contains(e.getPoint())) {
					resetPlaySpeedButtonMouseReleased();
				}
			}
		});
		setupAndAddButton(resetPlaySpeed, Images.RESET_SPEED);

		playAndPause.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(final MouseEvent e) {
				if (playAndPause.contains(e.getPoint())) {
					pausePlayButtonMouseReleased();
				}
			}
		});
		setupAndAddButton(playAndPause, Images.PLAY);

		final JButton speedUp = new JButton();
		speedUp.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(final MouseEvent e) {
				if (speedUp.contains(e.getPoint())) {
					speedUpButtonMouseReleased();
				}
			}
		});
		setupAndAddButton(speedUp, Images.SPEED_UP);
	}

	private void setupNavigationButtons() {
		final JButton moveToBeginning = new JButton();
		moveToBeginning.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(final MouseEvent e) {
				if (moveToBeginning.contains(e.getPoint())) {
					moveToBeginningButtonMouseReleased();
				}
			}
		});
		setupAndAddButton(moveToBeginning, Images.MOVE_TO_BEGINNING);

		final JButton moveBackwardCoarse = new JButton();
		moveBackwardCoarse.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(final MouseEvent e) {
				if (moveBackwardCoarse.contains(e.getPoint())) {
					moveBackwardCoarseButtonMouseReleased();
				}
			}
		});
		setupAndAddButton(moveBackwardCoarse, Images.MOVE_BACKWARD_COARSE);

		final JButton moveBackward = new JButton();
		moveBackward.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(final MouseEvent e) {
				if (moveBackward.contains(e.getPoint())) {
					moveBackwardButtonMouseReleased();
				}
			}
		});
		setupAndAddButton(moveBackward, Images.MOVE_BACKWARD);

		final JButton moveToCenter = new JButton();
		moveToCenter.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(final MouseEvent e) {
				if (moveToCenter.contains(e.getPoint())) {
					moveToCenterButtonMouseReleased();
				}
			}
		});
		setupAndAddButton(moveToCenter, Images.MOVE_TO_CENTER);

		final JButton moveForward = new JButton();
		moveForward.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(final MouseEvent e) {
				if (moveForward.contains(e.getPoint())) {
					moveForwardButtonMouseReleased();
				}
			}
		});
		setupAndAddButton(moveForward, Images.MOVE_FORWARD);

		final JButton moveForwardCoarse = new JButton();
		moveForwardCoarse.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(final MouseEvent e) {
				if (moveForwardCoarse.contains(e.getPoint())) {
					moveForwardCoarseButtonMouseReleased();
				}
			}
		});
		setupAndAddButton(moveForwardCoarse, Images.MOVE_FORWARD_COARSE);

		final JButton moveToEnd = new JButton();
		moveToEnd.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(final MouseEvent e) {
				if (moveToEnd.contains(e.getPoint())) {
					moveToEndButtonMouseReleased();
				}
			}
		});
		setupAndAddButton(moveToEnd, Images.MOVE_TO_END);
	}

	private void setupFileButtons() {
		final JButton openFile = new JButton();
		openFile.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(final MouseEvent e) {
				if (openFile.contains(e.getPoint())) {
					openButtonMouseReleased();
				}
			}
		});
		setupAndAddButton(openFile, Images.OPEN_FILE);

		final JButton openLast = new JButton();
		openLast.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(final MouseEvent e) {
				if (openLast.contains(e.getPoint())) {
					openLastButtonMouseReleased();
				}
			}
		});
		setupAndAddButton(openLast, Images.OPEN_LAST);
	}

	private void setupAndAddButton(final JButton button, final String iconFileName) {
		button.setIcon(new ImageIcon(getClass().getResource(BUTTON_DIR + iconFileName)));
		button.setBorder(null);
		button.setRequestFocusEnabled(false);
		add(button);
	}

	public final void updatePausePlayButton() {
		final boolean playing = OpenLogViewer.getInstance().getEntireGraphingPanel().isPlaying();
		if (playing) {
			playAndPause.setIcon(PAUSE_ICON);
		} else {
			playAndPause.setIcon(PLAY_ICON);
		}
		playAndPause.setBorder(null);
	}

	/**
	 * Modify the state of the PlayableLog zoom in 1 pixel up to 512 pixels
	 * @param evt
	 */
	private void zoomInButtonMouseReleased() {
		OpenLogViewer.getInstance().getEntireGraphingPanel().zoomInCoarse();
	}

	/**
	 * Modify the state of the PlayableLog zoom in 1 pixel down to -1024 pixels
	 * @param evt
	 */
	private void zoomOutButtonMouseReleased() {
		OpenLogViewer.getInstance().getEntireGraphingPanel().zoomOutCoarse();
	}

	/**
	 * Modify the state of the PlayableLog zoom to exactly 1
	 * @param evt
	 */
	private void zoomResetRatioButtonMouseReleased() {
		OpenLogViewer.getInstance().getEntireGraphingPanel().zoomResetRatio();
	}

	/**
	 * Modify the state of the PlayableLog zoom to exactly 1
	 * @param evt
	 */
	private void zoomResetFitButtonMouseReleased() {
		OpenLogViewer.getInstance().getEntireGraphingPanel().zoomGraphToFit();
	}

	/**
	 * Slows down the play back speed of the Playable Log
	 * @param evt
	 */
	private void slowDownButtonMouseReleased() {
		OpenLogViewer.getInstance().getEntireGraphingPanel().slowDown();
	}

	/**
	 * Sets the play back speed of the Playable Log to the original base speed
	 * @param evt
	 */
	private void resetPlaySpeedButtonMouseReleased() {
		OpenLogViewer.getInstance().getEntireGraphingPanel().resetPlaySpeed();
	}

	/**
	 * Modifies the state of the PlayableLog to begin playing if paused, or pause if playing
	 * @param evt
	 */
	private void pausePlayButtonMouseReleased() {
		final boolean playing = OpenLogViewer.getInstance().getEntireGraphingPanel().isPlaying();
		if (playing) {
			OpenLogViewer.getInstance().getEntireGraphingPanel().pause();
		} else {
			OpenLogViewer.getInstance().getEntireGraphingPanel().play();
		}
	}

	/**
	 * Speeds up the play back speed of the PlayableLog
	 * @param evt
	 */
	private void speedUpButtonMouseReleased() {
		OpenLogViewer.getInstance().getEntireGraphingPanel().speedUp();
	}

	/**
	 * Moves the Playable Log to the beginning position
	 * @param evt
	 */
	private void moveToBeginningButtonMouseReleased() {
		OpenLogViewer.getInstance().getEntireGraphingPanel().moveToBeginning();
	}

	/**
	 * Moves the Playable Log backward a large amount
	 * @param evt
	 */
	private void moveBackwardCoarseButtonMouseReleased() {
		OpenLogViewer.getInstance().getEntireGraphingPanel().moveBackwardCoarse();
	}

	/**
	 * Moves the Playable Log backward a small amount
	 * @param evt
	 */
	private void moveBackwardButtonMouseReleased() {
		OpenLogViewer.getInstance().getEntireGraphingPanel().moveBackward();
	}

	/**
	 * Centers the Playable Log in the display
	 * @param evt
	 */
	private void moveToCenterButtonMouseReleased() {
		OpenLogViewer.getInstance().getEntireGraphingPanel().centerGraphPosition();
	}

	/**
	 * Moves the Playable Log forward a small amount
	 * @param evt
	 */
	private void moveForwardButtonMouseReleased() {
		OpenLogViewer.getInstance().getEntireGraphingPanel().moveForward();
	}

	/**
	 * Moves the Playable Log forward a large amount
	 * @param evt
	 */
	private void moveForwardCoarseButtonMouseReleased() {
		OpenLogViewer.getInstance().getEntireGraphingPanel().moveForwardCoarse();
	}

	/**
	 * Moves the Playable Log to the ending position
	 * @param evt
	 */
	private void moveToEndButtonMouseReleased() {
		OpenLogViewer.getInstance().getEntireGraphingPanel().moveToEnd();
	}

	/**
	 * Allow the user to choose a new log to open
	 * @param evt
	 */
	private void openButtonMouseReleased() {
		OpenLogViewer.getInstance().exitFullScreen();
		OpenLogViewer.getInstance().openChosenFile();
	}

	/**
	 * Re-open the last log file that was opened/loaded
	 * @param evt
	 */
	private void openLastButtonMouseReleased() {
		OpenLogViewer.getInstance().exitFullScreen();
		OpenLogViewer.getInstance().openLastFile();
	}
}
