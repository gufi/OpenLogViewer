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

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

public class NavBarPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private static final int PREFERRED_WIDTH = 800;
	private static final int PREFERRED_HEIGHT = 18;

	private JButton zoomInButton;
	private JButton zoomResetButton;
	private JButton zoomOutButton;
	private JButton pausePlayButton;
	private JButton slowDownButton;
	private JButton ejectButton;
	private JButton speedUpButton;

	public NavBarPanel() {
		super();
		zoomInButton = new JButton();
		zoomResetButton = new JButton();
		zoomOutButton = new JButton();
		slowDownButton = new JButton();
		pausePlayButton = new JButton();
		speedUpButton = new JButton();
		ejectButton = new JButton();
		initComponents();
	}

	private void initComponents() {
		this.setName("this");
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

		zoomResetButton.setIcon(new ImageIcon(getClass().getResource("zoomReset.png"))); // NOI18N
		zoomResetButton.setAlignmentY(0.0F);
		zoomResetButton.setBorder(null);
		zoomResetButton.setBorderPainted(false);
		zoomResetButton.setContentAreaFilled(false);
		zoomResetButton.setName("zoomResetButton"); // NOI18N
		zoomResetButton.setRequestFocusEnabled(false);
		zoomResetButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(final MouseEvent e) {
				zoomResetButtonMouseReleased(e);
			}
		});
		this.add(zoomResetButton);

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

		ejectButton.setIcon(new ImageIcon(getClass().getResource("eject.png"))); // NOI18N
		ejectButton.setAlignmentY(0.0F);
		ejectButton.setBorder(null);
		ejectButton.setBorderPainted(false);
		ejectButton.setContentAreaFilled(false);
		ejectButton.setName("ejectButton"); // NOI18N
		ejectButton.setRequestFocusEnabled(false);
		ejectButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(final MouseEvent e) {
				ejectButtonMouseReleased(e);
			}
		});
		this.add(ejectButton);
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
	private void zoomResetButtonMouseReleased(final MouseEvent e) {
		final int zoom = OpenLogViewer.getInstance().getEntireGraphingPanel().getZoom();
		final boolean zoomedOut = OpenLogViewer.getInstance().getEntireGraphingPanel().isZoomedOutBeyondOneToOne();
		if (zoomedOut) {
			for (int i = zoom; i > 1; i--) {
				OpenLogViewer.getInstance().getEntireGraphingPanel().zoomIn();
			}
		} else {
			for (int i = zoom; i > 1; i--) {
				OpenLogViewer.getInstance().getEntireGraphingPanel().zoomOut();
			}
		}
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
	 * Slows down the play back speed of the Playable Log
	 * @param evt
	 */
	private void slowDownButtonMouseReleased(final MouseEvent e) {
		OpenLogViewer.getInstance().getEntireGraphingPanel().slowDown();
	}

	/**
	 * Un-Implimented currently, future plans are to have this as an alternate to open a new log
	 * @param evt
	 */
	private void ejectButtonMouseReleased(final MouseEvent e) {
		OpenLogViewer.getInstance().getEntireGraphingPanel().eject();
	}
}