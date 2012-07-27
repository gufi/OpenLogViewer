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
	private static final int PPREFERRED_HEIGHT = 16;
	private static final int TIMER_RATE = 250; //milliseconds - changes display/update speed
	private static final DecimalFormat df = new DecimalFormat("#,##0.0");

	private JLabel output;
	private Timer sampleTimer;
	private static long frameCount;
	private Double FPS;
	private static long thePast;
	private static long currentTime;
	private static long previousCount;

	public FramesPerSecondPanel() {
		super();
		sampleTimer = new Timer(TIMER_RATE, this);
		sampleTimer.setInitialDelay(0);
		sampleTimer.start();
		currentTime = System.currentTimeMillis();
		thePast = currentTime;

		this.setName("fpsPanel");
		this.setPreferredSize(new Dimension(PREFERRED_WIDTH, PPREFERRED_HEIGHT));
		this.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));

		output = new JLabel("FPS: 0.0");
		output.setVerticalTextPosition(JLabel.CENTER);
		output.setHorizontalTextPosition(JLabel.CENTER);
		this.add(output);
	}

	public static void increaseFrameCount(){
		currentTime = System.currentTimeMillis();
		frameCount++;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(sampleTimer)) {
			final long sampleWindow = currentTime - thePast;
			final long sampleCount = frameCount - previousCount;
			if (sampleWindow == 0L) { // Avoid division by zero
				FPS = 0d;
			} else {
				FPS = ((double)sampleCount / (double)sampleWindow) * 1000d;
			}
			output.setText("FPS: " + df.format(FPS));
			previousCount = frameCount;
			thePast = currentTime;
		}
	}

}
