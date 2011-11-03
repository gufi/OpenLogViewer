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

public class PlayBarPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private static final int PLAY_BAR_PREFERRED_WIDTH = 888;
	private static final int PLAY_BAR_PREFERRED_HEIGHT = 40;

	private JButton zoomInButton;
	private JButton zoomResetButton;
	private JButton zoomOutButton;
	private JButton playButton;
	private JButton slowDownButton;
	private JButton stopButton;
	private JButton ejectButton;
	private JButton fastForwardButton;
	private JButton pauseButton;

	/**
	 * Default JPanel constructor initializing the playbar buttons
	 */
	public PlayBarPanel() {
		super();
		zoomInButton = new JButton();
		zoomResetButton = new JButton();
		zoomOutButton = new JButton();
		slowDownButton = new JButton();
		playButton = new JButton();
		pauseButton = new JButton();
		stopButton = new JButton();
		fastForwardButton = new JButton();
		ejectButton = new JButton();
		initComponents();
	}

	/**
	 * Method to control and setup the components of the playbar
	 */
	private void initComponents() {
		this.setName("this");
		this.setPreferredSize(new Dimension(PLAY_BAR_PREFERRED_WIDTH, PLAY_BAR_PREFERRED_HEIGHT));
		this.setLayout(new FlowLayout(FlowLayout.RIGHT, 0, 0));

		zoomInButton.setIcon(new ImageIcon(getClass().getResource("Playbar_+.png"))); // NOI18N
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

		zoomResetButton.setIcon(new ImageIcon(getClass().getResource("Playbar_o.png"))); // NOI18N
		zoomResetButton.setAlignmentY(0.0F);
		zoomResetButton.setBorder(null);
		zoomResetButton.setBorderPainted(false);
		zoomResetButton.setContentAreaFilled(false);
		zoomResetButton.setName("zoomResetButton");
		zoomResetButton.setRequestFocusEnabled(false);
		zoomResetButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(final MouseEvent e) {
				zoomResetButtonMouseReleased(e);
			}
		});
		this.add(zoomResetButton);

		zoomOutButton.setIcon(new ImageIcon(getClass().getResource("Playbar_-.png"))); // NOI18N
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

		slowDownButton.setIcon(new ImageIcon(getClass().getResource("Playbar_01.png"))); // NOI18N
		slowDownButton.setAlignmentY(0.0F);
		slowDownButton.setBorder(null);
		slowDownButton.setBorderPainted(false);
		slowDownButton.setContentAreaFilled(false);
		slowDownButton.setName("slowDownButton");
		slowDownButton.setRequestFocusEnabled(false);
		slowDownButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(final MouseEvent e) {
				reverseButtonMouseReleased(e);
			}
		});
		this.add(slowDownButton);

		playButton.setIcon(new ImageIcon(getClass().getResource("Playbar_02.png"))); // NOI18N
		playButton.setAlignmentY(0.0F);
		playButton.setBorder(null);
		playButton.setBorderPainted(false);
		playButton.setContentAreaFilled(false);
		playButton.setName("playButton");
		playButton.setRequestFocusEnabled(false);
		playButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(final MouseEvent e) {
				playButtonMouseReleased(e);
			}
		});
		this.add(playButton);

		pauseButton.setIcon(new ImageIcon(getClass().getResource("Playbar_03.png"))); // NOI18N
		pauseButton.setAlignmentY(0.0F);
		pauseButton.setBorder(null);
		pauseButton.setBorderPainted(false);
		pauseButton.setContentAreaFilled(false);
		pauseButton.setName("pauseButton");
		pauseButton.setRequestFocusEnabled(false);
		pauseButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(final MouseEvent e) {
				pauseButtonMouseReleased(e);
			}
		});
		this.add(pauseButton);

		stopButton.setIcon(new ImageIcon(getClass().getResource("Playbar_04.png"))); // NOI18N
		stopButton.setAlignmentY(0.0F);
		stopButton.setBorder(null);
		stopButton.setBorderPainted(false);
		stopButton.setContentAreaFilled(false);
		stopButton.setName("stopButton"); // NOI18N
		stopButton.setRequestFocusEnabled(false);
		stopButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(final MouseEvent e) {
				stopButtonMouseReleased(e);
			}
		});
		this.add(stopButton);

		fastForwardButton.setIcon(new ImageIcon(getClass().getResource("Playbar_05.png"))); // NOI18N
		fastForwardButton.setAlignmentY(0.0F);
		fastForwardButton.setBorder(null);
		fastForwardButton.setBorderPainted(false);
		fastForwardButton.setContentAreaFilled(false);
		fastForwardButton.setName("fastForwardButton"); // NOI18N
		fastForwardButton.setRequestFocusEnabled(false);
		fastForwardButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(final MouseEvent e) {
				fastForwardButtonMouseReleased(e);
			}
		});
		this.add(fastForwardButton);

		ejectButton.setIcon(new ImageIcon(getClass().getResource("Playbar_06.png"))); // NOI18N
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

	/**
	 * Modify the state of the PlayableLog zoom in 1 pixel up to 512 pixels
	 * @param evt
	 */
	private void zoomInButtonMouseReleased(final MouseEvent e) {
		OpenLogViewer.getInstance().getEntireGraphingPanel().zoomIn();
	}

	/**
	 * Modify the state of the PlayableLog zoom in 1 pixel down to -1024 pixels
	 * @param evt
	 */
	private void zoomOutButtonMouseReleased(final MouseEvent e) {
		OpenLogViewer.getInstance().getEntireGraphingPanel().zoomOut();
	}

	/**
	 * Modify the state of the PlayableLog zoom to exactly 1
	 * @param evt
	 */
	private void zoomResetButtonMouseReleased(final MouseEvent e) {
		int zoom = OpenLogViewer.getInstance().getEntireGraphingPanel().getZoom();
		boolean zoomedOut = OpenLogViewer.getInstance().getEntireGraphingPanel().isZoomedOutBeyondOneToOne();
		if(zoomedOut){
			for(int i = zoom; i > 1; i--){
				OpenLogViewer.getInstance().getEntireGraphingPanel().zoomIn();
			}
		} else {
			for(int i = zoom; i > 1; i--){
				OpenLogViewer.getInstance().getEntireGraphingPanel().zoomOut();
			}
		}
	}

	/**
	 * modifys the state of the PlayableLog to begin playing
	 * @param evt
	 */
	private void playButtonMouseReleased(final MouseEvent e) {
		OpenLogViewer.getInstance().getEntireGraphingPanel().play();
		OpenLogViewer.getInstance().getEntireGraphingPanel();
	}

	/**
	 * Modifys the state of the PlayableLog to pause
	 * @param evt
	 */
	private void pauseButtonMouseReleased(final MouseEvent e) {
		OpenLogViewer.getInstance().getEntireGraphingPanel().pause();
	}

	/**
	 * Modifys the state of the PlayableLog to stop and reset to the beginning
	 * @param evt
	 */
	private void stopButtonMouseReleased(final MouseEvent e) {
		OpenLogViewer.getInstance().getEntireGraphingPanel().stop();
	}

	/**
	 * Speeds up the play back speed of the PlayableLog
	 * @param evt
	 */
	private void fastForwardButtonMouseReleased(final MouseEvent e) {
		OpenLogViewer.getInstance().getEntireGraphingPanel().fastForward();
	}

	/**
	 * Slows down the play back speed of the Playable Log
	 * @param evt
	 */
	private void reverseButtonMouseReleased(final MouseEvent e) {
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
