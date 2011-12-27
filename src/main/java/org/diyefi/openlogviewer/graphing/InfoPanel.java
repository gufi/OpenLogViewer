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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JPanel;

import org.diyefi.openlogviewer.OpenLogViewer;
import org.diyefi.openlogviewer.genericlog.GenericLog;

public class InfoPanel extends JPanel implements MouseMotionListener, MouseListener {
	private static final long serialVersionUID = 1L;

	private static final int LEFT_MARGIN_OFFSET = 10;
	private static final int ONE_TEXTUAL_HEIGHT = 20;
	private int FPScounter = 0;
	private int FPS = 0;
	private long currentTime;
	private long builtTime;
	private GenericLog genLog;
	private Color vertBar = new Color(255, 255, 255, 100);
	private Color textBackground = new Color(0, 0, 0, 170);
	private int xMouseCoord;
	private int yMouseCoord;
	private boolean mouseOver;

	public InfoPanel() {
		xMouseCoord = -100;
		yMouseCoord = -100;
		mouseOver = false;
		this.setOpaque(false);
	}

	@Override
	public final void paint(final Graphics g) { // override paint because there will be no components in this pane
		builtTime += System.currentTimeMillis() - currentTime;
		currentTime = System.currentTimeMillis();
		if (builtTime <= 1000) {
			FPScounter++;
		} else {
			FPS = FPScounter;
			if (FPScounter != 0) {
				FPS += (1000 % FPScounter) * 0.001;
			}
			FPScounter = 0;
			builtTime = 0;
		}

		if (!this.getSize().equals(this.getParent().getSize())) {
			this.setSize(this.getParent().getSize());
		}

		g.setFont(new Font(Font.DIALOG, Font.PLAIN, 12)); // Required to keep font consistent when using Mac L&F
		if (genLog == null) {
			g.setColor(Color.RED);
			g.drawString("No log loaded, please select a log from the file menu.", LEFT_MARGIN_OFFSET, ONE_TEXTUAL_HEIGHT);
		} else {
			if (genLog.getLogStatus() == GenericLog.LogState.LOG_LOADING) {
				g.setColor(Color.red);
				g.drawString("Loading log, please wait...", LEFT_MARGIN_OFFSET, ONE_TEXTUAL_HEIGHT);
			} else if (genLog.getLogStatus() == GenericLog.LogState.LOG_LOADED) {
				int fpsHeight = 0;
				if (genLog.getLogStatusMessage() != null) {
					g.setColor(Color.RED);
					g.drawString(":-( The very sad decoder crashed! Last words: ", LEFT_MARGIN_OFFSET, ONE_TEXTUAL_HEIGHT);
					g.drawString(genLog.getLogStatusMessage(), LEFT_MARGIN_OFFSET, ONE_TEXTUAL_HEIGHT * 2);
					g.drawString("Displaying what we managed to read in before the catastrophy anyway...", LEFT_MARGIN_OFFSET, ONE_TEXTUAL_HEIGHT * 3);
					fpsHeight = ONE_TEXTUAL_HEIGHT * 4;
				} else {
					fpsHeight = ONE_TEXTUAL_HEIGHT;
				}

				final Dimension d = this.getSize();
				final MultiGraphLayeredPane multigGraph = OpenLogViewer.getInstance().getMultiGraphLayeredPane();
				final Graphics2D g2d = (Graphics2D) g;
				g.setColor(Color.GRAY);
				g2d.drawString("FPS: " + Double.toString(FPS), LEFT_MARGIN_OFFSET, fpsHeight);

				if (mouseOver) {
					final FontMetrics fm = this.getFontMetrics(this.getFont());  //For getting string width
					final int fontHeight = fm.getHeight();
					final GraphPositionPanel graphPositionPanel = OpenLogViewer.getInstance().getEntireGraphingPanel().getGraphPositionPanel();
					final int zoom = OpenLogViewer.getInstance().getEntireGraphingPanel().getZoom();
					final boolean zoomedOut = OpenLogViewer.getInstance().getEntireGraphingPanel().isZoomedOutBeyondOneToOne();
					int snappedDataPosition = xMouseCoord;
					if (!zoomedOut && zoom > 1) {
						snappedDataPosition = graphPositionPanel.getBestSnappingPosition(xMouseCoord);
					}
					g2d.setColor(vertBar);
					g2d.drawLine(d.width / 2, 0, d.width / 2, d.height);  //center position line
					g2d.drawLine(snappedDataPosition, 0, snappedDataPosition, d.height);  //mouse cursor line

					for (int i = 0; i < multigGraph.getComponentCount(); i++) {
						if (multigGraph.getComponent(i) instanceof SingleGraphPanel) {
							final SingleGraphPanel singleGraph = (SingleGraphPanel) multigGraph.getComponent(i);
							g2d.setColor(textBackground);
							final String mouseDataString = singleGraph.getMouseInfo(snappedDataPosition);
							final int stringWidth = fm.stringWidth(mouseDataString);
							g2d.fillRect(snappedDataPosition + 1, yMouseCoord + 2 + (fontHeight * i), stringWidth + 3, fontHeight);
							g2d.setColor(singleGraph.getColor());
							g2d.drawString(mouseDataString, snappedDataPosition + 3, yMouseCoord + fontHeight + (fontHeight * i));
						}
					}
				}
			}
		}
	}

	public final void setLog(final GenericLog log) {
		genLog = log;
		repaint();
	}

	@Override
	public final void mouseEntered(final MouseEvent e) {
		mouseOver = true;
	}

	@Override
	public final void mouseExited(final MouseEvent e) {
		mouseOver = false;
		repaint();
	}

	@Override
	public final void mouseMoved(final MouseEvent e) {
		xMouseCoord = e.getX();
		yMouseCoord = e.getY();
		repaint();
	}

	@Override
	public void mouseClicked(final MouseEvent e) {
	}

	@Override
	public void mousePressed(final MouseEvent e) {
	}

	@Override
	public void mouseReleased(final MouseEvent e) {
	}

	@Override
	public void mouseDragged(final MouseEvent e) {

	}
}
