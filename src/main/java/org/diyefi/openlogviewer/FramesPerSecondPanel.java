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
	private static final int TIMER_RATE = 250; // milliseconds - changes display/update speed
	private static final double MILLISECONDS_PER_SECOND = 1000d;
	private static final DecimalFormat DF = new DecimalFormat("#,##0.0");

	private static long frameCount;
	private static long thePast;
	private static long currentTime;
	private static long previousCount;

	private final JLabel output;
	private final Timer sampleTimer;

	private Double fps;

	public FramesPerSecondPanel() {
		sampleTimer = new Timer(TIMER_RATE, this);
		sampleTimer.setInitialDelay(0);
		sampleTimer.start();
		currentTime = System.currentTimeMillis();
		thePast = currentTime;

		setName("fpsPanel");
		setPreferredSize(new Dimension(PREFERRED_WIDTH, PPREFERRED_HEIGHT));
		setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));

		output = new JLabel("FPS: 0.0");
		output.setVerticalTextPosition(JLabel.CENTER);
		output.setHorizontalTextPosition(JLabel.CENTER);
		this.add(output);
	}

	public static void increaseFrameCount() {
		currentTime = System.currentTimeMillis();
		frameCount++;
	}

	@Override
	public final void actionPerformed(final ActionEvent e) {
		if (e.getSource().equals(sampleTimer)) {
			final long sampleWindow = currentTime - thePast;
			final long sampleCount = frameCount - previousCount;
			if (sampleWindow == 0L) { // Avoid division by zero
				fps = 0d;
			} else {
				fps = ((double) sampleCount / (double) sampleWindow) * MILLISECONDS_PER_SECOND;
			}
			output.setText("FPS: " + DF.format(fps));
			previousCount = frameCount;
			thePast = currentTime;
		}
	}

}
