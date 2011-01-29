/* DataReader
 *
 * Copyright 2011
 *
 * This file is part of the DataReader project.
 *
 * DataReader software is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * DataReader software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with any DataReader software.  If not, see http://www.gnu.org/licenses/
 *
 * I ask that if you make any changes to this file you fork the code on github.com!
 *
 */

package datareader;

import Decoder.FreeEMSBin;
import javax.swing.JPanel;

/**
 *
 * @author Bryan
 */
public class PlayBarPanel extends JPanel {

    public PlayBarPanel() {
        super();
        reverseButton = new javax.swing.JButton();
        playButton = new javax.swing.JButton();
        pauseButton = new javax.swing.JButton();
        stopButton = new javax.swing.JButton();
        fastForwardButton = new javax.swing.JButton();
        ejectButton = new javax.swing.JButton();
        initComponents();
    }

    private void initComponents() {
        this.setName("this"); // NOI18N
        this.setPreferredSize(new java.awt.Dimension(857, 40));
        this.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 0, 0));

        reverseButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/datareader/resources/Playbar_01.png"))); // NOI18N
        reverseButton.setAlignmentY(0.0F);
        reverseButton.setBorder(null);
        reverseButton.setBorderPainted(false);
        reverseButton.setContentAreaFilled(false);
        reverseButton.setName("reverseButton"); // NOI18N
        reverseButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                reverseButtonMouseReleased(evt);
            }
        });
        this.add(reverseButton);

        playButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/datareader/resources/Playbar_02.png"))); // NOI18N
        playButton.setAlignmentY(0.0F);
        playButton.setBorder(null);
        playButton.setBorderPainted(false);
        playButton.setContentAreaFilled(false);
        playButton.setName("playButton"); // NOI18N
        playButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                playButtonMouseReleased(evt);
            }
        });
        this.add(playButton);

        pauseButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/datareader/resources/Playbar_03.png"))); // NOI18N
        pauseButton.setAlignmentY(0.0F);
        pauseButton.setBorder(null);
        pauseButton.setBorderPainted(false);
        pauseButton.setContentAreaFilled(false);
        pauseButton.setName("pauseButton"); // NOI18N
        pauseButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                pauseButtonMouseReleased(evt);
            }
        });
        this.add(pauseButton);

        stopButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/datareader/resources/Playbar_04.png"))); // NOI18N
        stopButton.setAlignmentY(0.0F);
        stopButton.setBorder(null);
        stopButton.setBorderPainted(false);
        stopButton.setContentAreaFilled(false);
        stopButton.setName("stopButton"); // NOI18N
        stopButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                stopButtonMouseReleased(evt);
            }
        });
        this.add(stopButton);

        fastForwardButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/datareader/resources/Playbar_05.png"))); // NOI18N
        fastForwardButton.setAlignmentY(0.0F);
        fastForwardButton.setBorder(null);
        fastForwardButton.setBorderPainted(false);
        fastForwardButton.setContentAreaFilled(false);
        fastForwardButton.setName("fastForwardButton"); // NOI18N
        fastForwardButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                fastForwardButtonMouseReleased(evt);
            }
        });
        this.add(fastForwardButton);

        ejectButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/datareader/resources/Playbar_06.png"))); // NOI18N
        ejectButton.setAlignmentY(0.0F);
        ejectButton.setBorder(null);
        ejectButton.setBorderPainted(false);
        ejectButton.setContentAreaFilled(false);
        ejectButton.setName("ejectButton"); // NOI18N
        ejectButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                ejectButtonMouseReleased(evt);
            }
        });
        this.add(ejectButton);
    }

    private void playButtonMouseReleased( java.awt.event.MouseEvent evt) {
        DataLogReaderApp.getInstance().getPlayableLog().play();
    }
    private void pauseButtonMouseReleased( java.awt.event.MouseEvent evt) {
        DataLogReaderApp.getInstance().getPlayableLog().pause();
    }
    private void stopButtonMouseReleased( java.awt.event.MouseEvent evt) {
        DataLogReaderApp.getInstance().getPlayableLog().stop();
    }
    private void fastForwardButtonMouseReleased( java.awt.event.MouseEvent evt) {
        DataLogReaderApp.getInstance().getPlayableLog().fastForward();
    }
    private void reverseButtonMouseReleased( java.awt.event.MouseEvent evt) {
        DataLogReaderApp.getInstance().getPlayableLog().slowDown();
    }

    private void ejectButtonMouseReleased(java.awt.event.MouseEvent evt) {

         fems = new FreeEMSBin("C:\\Users\\Bryan\\Desktop\\inputlog.bin");
        
    }
    private FreeEMSBin fems;
    private javax.swing.JButton playButton;
    private javax.swing.JButton reverseButton;
    private javax.swing.JButton stopButton;
    private javax.swing.JButton ejectButton;
    private javax.swing.JButton fastForwardButton;
    private javax.swing.JButton pauseButton;
}
