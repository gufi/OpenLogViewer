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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

public class FramesPerSecondPanel extends JPanel implements ActionListener {
	private static final long serialVersionUID = 1L;
	private static final int PREFERRED_WIDTH = 80;
	private static final int PPREFERRED_HEIGHT = 40;
	private static final int SAMPLE_WINDOW = 250; //milliseconds - changes display/update speed
	private static final DecimalFormat df = new DecimalFormat("#,##0.0");

	private JLabel output;
	private Timer sampleTimer;
	private static long frames;
	private Double FPS;
	private double samplesPerSecond;
	private long thePast;
	private static long currentTime;

	public FramesPerSecondPanel() {
		super();
		init();
	}

	private void init() {
		frames = 0;
		sampleTimer = new Timer(SAMPLE_WINDOW, this);
		sampleTimer.setInitialDelay(0);
		sampleTimer.start();
		thePast = System.currentTimeMillis();
		currentTime = thePast;

		this.setName("fpsPanel");
		this.setPreferredSize(new Dimension(PREFERRED_WIDTH, PPREFERRED_HEIGHT));
		this.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));

		output = new JLabel("FPS: 0.0");
		output.setVerticalTextPosition(JLabel.CENTER);
		output.setHorizontalTextPosition(JLabel.CENTER);
		this.add(output);
	}

	public static void increaseFrameCount(){
		frames++;
		currentTime = System.currentTimeMillis();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		samplesPerSecond = 1000.0 / (currentTime - thePast);
		FPS = frames * samplesPerSecond;
		if(FPS.isNaN() || FPS.isInfinite() || frames == 0){
			output.setText("FPS: 0.0");
		} else {
			output.setText("FPS: " + df.format(FPS));
		}
		frames = 0;
		thePast = System.currentTimeMillis();
	}

}
