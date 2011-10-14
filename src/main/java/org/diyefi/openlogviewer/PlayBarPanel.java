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

/**
 *
 * @author Bryan
 */
public class PlayBarPanel extends JPanel {

	/**
     * Default JPanel constructor initializing the playbar buttons
     */
    public PlayBarPanel() {
        super();
        zoomInButton = new JButton();
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
        this.setName("this"); // NOI18N
        this.setPreferredSize(new Dimension(857, 40));
        this.setLayout(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        
        zoomInButton.setIcon(new ImageIcon(getClass().getResource("Playbar_+.png"))); // NOI18N
        zoomInButton.setAlignmentY(0.0F);
        zoomInButton.setBorder(null);
        zoomInButton.setBorderPainted(false);
        zoomInButton.setContentAreaFilled(false);
        zoomInButton.setName("zoomInButton"); // NOI18N
        zoomInButton.setRequestFocusEnabled(false);
        zoomInButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                zoomInButtonMouseReleased(e);
            }
        });
        this.add(zoomInButton);

        zoomOutButton.setIcon(new ImageIcon(getClass().getResource("Playbar_-.png"))); // NOI18N
        zoomOutButton.setAlignmentY(0.0F);
        zoomOutButton.setBorder(null);
        zoomOutButton.setBorderPainted(false);
        zoomOutButton.setContentAreaFilled(false);
        zoomOutButton.setName("zoomOutButton"); // NOI18N
        zoomOutButton.setRequestFocusEnabled(false);
        zoomOutButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                zoomOutButtonMouseReleased(e);
            }
        });
        this.add(zoomOutButton);

        slowDownButton.setIcon(new ImageIcon(getClass().getResource("Playbar_01.png"))); // NOI18N
        slowDownButton.setAlignmentY(0.0F);
        slowDownButton.setBorder(null);
        slowDownButton.setBorderPainted(false);
        slowDownButton.setContentAreaFilled(false);
        slowDownButton.setName("slowDownButton"); // NOI18N
        slowDownButton.setRequestFocusEnabled(false);
        slowDownButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                reverseButtonMouseReleased(e);
            }
        });
        this.add(slowDownButton);

        playButton.setIcon(new ImageIcon(getClass().getResource("Playbar_02.png"))); // NOI18N
        playButton.setAlignmentY(0.0F);
        playButton.setBorder(null);
        playButton.setBorderPainted(false);
        playButton.setContentAreaFilled(false);
        playButton.setName("playButton"); // NOI18N
        playButton.setRequestFocusEnabled(false);
        playButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                playButtonMouseReleased(e);
            }
        });
        this.add(playButton);

        pauseButton.setIcon(new ImageIcon(getClass().getResource("Playbar_03.png"))); // NOI18N
        pauseButton.setAlignmentY(0.0F);
        pauseButton.setBorder(null);
        pauseButton.setBorderPainted(false);
        pauseButton.setContentAreaFilled(false);
        pauseButton.setName("pauseButton"); // NOI18N
        pauseButton.setRequestFocusEnabled(false);
        pauseButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
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
            public void mouseReleased(MouseEvent e) {
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
            public void mouseReleased(MouseEvent e) {
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
            public void mouseReleased(MouseEvent e) {
                ejectButtonMouseReleased(e);
            }
        });
        this.add(ejectButton);
    }
    /**
     * modifys the state of the PlayableLog zoom in 1 pixel up to 10 pixels
     * @param evt
     */
    private void zoomInButtonMouseReleased(MouseEvent e){
    	OpenLogViewerApp.getInstance().getEntireGraphingPanel().zoomIn();
    }
    /**
     * modifys the state of the PlayableLog zoom in 1 pixel down to 1 pixel
     * @param evt
     */
    private void zoomOutButtonMouseReleased(MouseEvent e){
        OpenLogViewerApp.getInstance().getEntireGraphingPanel().zoomOut();
    }
    /**
     * modifys the state of the PlayableLog to begin playing
     * @param evt
     */
    private void playButtonMouseReleased(MouseEvent e) {
        OpenLogViewerApp.getInstance().getEntireGraphingPanel().play();
        OpenLogViewerApp.getInstance().getEntireGraphingPanel();
    }
    /**
     * Modifys the state of the PlayableLog to pause
     * @param evt
     */
    private void pauseButtonMouseReleased(MouseEvent e) {
        OpenLogViewerApp.getInstance().getEntireGraphingPanel().pause();
    }
    /**
     * Modifys the state of the PlayableLog to stop and reset to the beginning
     * @param evt
     */
    private void stopButtonMouseReleased(MouseEvent e) {
        OpenLogViewerApp.getInstance().getEntireGraphingPanel().stop();
    }
    /**
     * Speeds up the play back speed of the PlayableLog
     * @param evt
     */
    private void fastForwardButtonMouseReleased(MouseEvent e) {
        OpenLogViewerApp.getInstance().getEntireGraphingPanel().fastForward();
    }
    /**
     * Slows down the play back speed of the Playable Log
     * @param evt
     */
    private void reverseButtonMouseReleased(MouseEvent e) {
        OpenLogViewerApp.getInstance().getEntireGraphingPanel().slowDown();
    }
    /**
     * Un-Implimented currently, future plans are to have this as an alternate to open a new log
     * @param evt
     */
    private void ejectButtonMouseReleased(MouseEvent e) {
        
         //OpenLogViewerApp.openFile();
         OpenLogViewerApp.getInstance().getEntireGraphingPanel().eject();
    }
   // private FreeEMSBin fems;
    private JButton zoomInButton;
    private JButton zoomOutButton;
    private JButton playButton;
    private JButton slowDownButton;
    private JButton stopButton;
    private JButton ejectButton;
    private JButton fastForwardButton;
    private JButton pauseButton;
	private static final long serialVersionUID = 1294732662423188903L;
}
