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
import java.util.ResourceBundle;

import javax.swing.JPanel;

import org.diyefi.openlogviewer.OpenLogViewer;
import org.diyefi.openlogviewer.Text;
import org.diyefi.openlogviewer.decoder.DecoderProgressListener;
import org.diyefi.openlogviewer.genericlog.GenericLog;
import org.diyefi.openlogviewer.utils.MathUtils;

public class InfoPanel extends JPanel implements MouseMotionListener, MouseListener, DecoderProgressListener {
	private static final long serialVersionUID = 1L;

	private static final int LEFT_MARGIN_OFFSET = 10;
	private static final int ONE_TEXTUAL_HEIGHT = 20;
	private static final int FONT_SIZE = 12;
	private static final int INFO_DISPLAY_OFFSET = 4;
	private static final Font HOVER_FONT = new Font(Font.MONOSPACED, Font.PLAIN, FONT_SIZE);

	private final ResourceBundle labels;
	private final Color vertBar = new Color(255, 255, 255, 100);
	private final Color textBackground = new Color(0, 0, 0, 170);

	private GenericLog genLog;
	private int xMouseCoord;
	private int yMouseCoord;
        private int lineOf, lineTotal, percentLoaded;
	private boolean shouldDraw;

	public InfoPanel(final ResourceBundle labels) {
		this.labels = labels;
		setOpaque(false);
	}

	@Override
	public final void paintComponent(final Graphics g) {
		super.paintComponent(g);

		if (!this.getSize().equals(this.getParent().getSize())) {
			this.setSize(this.getParent().getSize());
		}

		g.setFont(HOVER_FONT); // Required to keep font consistent when using Mac L&F
		if (genLog == null) {
			g.setColor(Color.RED);
			g.drawString(labels.getString(Text.NO_LOG_LOADED), LEFT_MARGIN_OFFSET, ONE_TEXTUAL_HEIGHT);
		} else {
			if (genLog.getLogStatus() == GenericLog.LogState.LOG_LOADING) {
				g.setColor(Color.red);
				g.drawString(labels.getString(Text.LOADING_LOG), LEFT_MARGIN_OFFSET, ONE_TEXTUAL_HEIGHT);
                                g.drawString(labels.getString(Text.PERCENT_LOADED) + Integer.toString(percentLoaded), LEFT_MARGIN_OFFSET, ONE_TEXTUAL_HEIGHT*4);
                                g.drawString(labels.getString(Text.LINE_OF_LOADED) + Integer.toString(lineOf) + ":" +Integer.toString(lineTotal), LEFT_MARGIN_OFFSET, ONE_TEXTUAL_HEIGHT*5);
			} else if (genLog.getLogStatus() == GenericLog.LogState.LOG_LOADED) {
				if (genLog.getLogStatusMessage() != null) {
					g.setColor(Color.RED);
					g.drawString(labels.getString(Text.DECODER_CRASHED_PART1), LEFT_MARGIN_OFFSET, ONE_TEXTUAL_HEIGHT);
					g.drawString(genLog.getLogStatusMessage(), LEFT_MARGIN_OFFSET, ONE_TEXTUAL_HEIGHT * 2);
					g.drawString(labels.getString(Text.DECODER_CRASHED_PART2), LEFT_MARGIN_OFFSET, ONE_TEXTUAL_HEIGHT * 3);
                                       
				}
				if (shouldDraw) {
					final int dataWidth = getWidestDataWidth();
					final Dimension d = this.getSize();
					final Graphics2D g2d = (Graphics2D) g;
					final FontMetrics fm = g.getFontMetrics(g.getFont());  // For getting string width
					final int fontHeight = fm.getHeight();
					final GraphPositionPanel graphPositionPanel = OpenLogViewer.getInstance().getEntireGraphingPanel().getGraphPositionPanel();
					final int zoom = OpenLogViewer.getInstance().getEntireGraphingPanel().getZoom();
					final boolean zoomedOut = OpenLogViewer.getInstance().getEntireGraphingPanel().isZoomedOutBeyondOneToOne();
					int snappedDataPosition = xMouseCoord;
					if (!zoomedOut && zoom > 1) {
						snappedDataPosition = graphPositionPanel.getBestSnappingPosition(xMouseCoord);
					}
					g2d.setColor(vertBar);
					g2d.drawLine(d.width / 2, 0, d.width / 2, d.height);  // center position line
					g2d.drawLine(snappedDataPosition, 0, snappedDataPosition, d.height);  // mouse cursor line

					final MultiGraphLayeredPane multigGraph = OpenLogViewer.getInstance().getMultiGraphLayeredPane();
					for (int i = 0; i < multigGraph.getComponentCount(); i++) {
						if (multigGraph.getComponent(i) instanceof SingleGraphPanel) {
							final SingleGraphPanel singleGraph = (SingleGraphPanel) multigGraph.getComponent(i);
							g2d.setColor(textBackground);
							String mouseData = singleGraph.getMouseInfo(snappedDataPosition, dataWidth);
							mouseData = mouseData + "  " + singleGraph.getData().getName();
							final int stringWidth = fm.stringWidth(mouseData);
							g2d.fillRect(snappedDataPosition - 2 + INFO_DISPLAY_OFFSET,
									yMouseCoord + 2 + (fontHeight * i),
									stringWidth + 4,
									fontHeight);
							g2d.setColor(singleGraph.getColor());
							g2d.drawString(mouseData,
									snappedDataPosition + INFO_DISPLAY_OFFSET,
									yMouseCoord + fontHeight + (fontHeight * i));
						}
					}
				}
			}
		}
                
	}

	public final void setLog(final GenericLog log) {
		genLog = log;
		this.repaint();
	}

	private int getWidestDataWidth() {
		String widestDataToDisplay = "";
		final MultiGraphLayeredPane multigGraph = OpenLogViewer.getInstance().getMultiGraphLayeredPane();
		for (int i = 0; i < multigGraph.getComponentCount(); i++) {
			if (multigGraph.getComponent(i) instanceof SingleGraphPanel) {
				final SingleGraphPanel singleGraph = (SingleGraphPanel) multigGraph.getComponent(i);
				final String minValue = MathUtils.roundDecimalPlaces(singleGraph.getData().getMinValue(), SingleGraphPanel.DECIMAL_PLACES);
				final String maxValue = MathUtils.roundDecimalPlaces(singleGraph.getData().getMaxValue(), SingleGraphPanel.DECIMAL_PLACES);
				if (minValue.length() > widestDataToDisplay.length()) {
					widestDataToDisplay = minValue;
				}
				if (maxValue.length() > widestDataToDisplay.length()) {
					widestDataToDisplay = maxValue;
				}
			}
		}
		return widestDataToDisplay.length();
	}

	@Override
	public final void mouseEntered(final MouseEvent e) {
		shouldDraw = true;
	}

	@Override
	public final void mouseExited(final MouseEvent e) {
		if (!e.isShiftDown()) {
			// Old default behaviour
			shouldDraw = false;
			repaint();
		} // else leave coordinates alone
	}

	@Override
	public final void mouseMoved(final MouseEvent e) {
		if (!e.isShiftDown()) {
			xMouseCoord = e.getX();
			yMouseCoord = e.getY();
			repaint();
		} // else hold position
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

    @Override
    public void onProgressChanged(int percentage) {
        percentLoaded = percentage;
        repaint();
    }

    @Override
    public void onProgressLinesOf(int line, int total) {
        lineOf = line;
        lineTotal = total;
        repaint();
    }
}
