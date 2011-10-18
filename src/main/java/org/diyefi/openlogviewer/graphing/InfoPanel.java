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
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JPanel;

import org.diyefi.openlogviewer.OpenLogViewerApp;
import org.diyefi.openlogviewer.genericlog.GenericLog;

/**
 * @author Bryan Harris and Ben Fenner
 */
public class InfoPanel extends JPanel implements MouseMotionListener, MouseListener {

    public InfoPanel() {
    	genLog = new GenericLog();
        xMouseCoord = -100;
        yMouseCoord = -100;
        mouseOver = false;
        this.setOpaque(false);
    }

    @Override
    public void paint(Graphics g) { // override paint because there will be no components in this pane
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
        if (genLog.getLogStatus() == GenericLog.LOG_NOT_LOADED) {
            g.setColor(Color.RED);
            g.drawString("No log loaded, please select a log from the file menu.", 20, 20);
        } else if (genLog.getLogStatus() == GenericLog.LOG_LOADING) {
            g.setColor(Color.red);
            g.drawString("Loading log, please wait...", 20, 20);
        } else if (genLog.getLogStatus() == GenericLog.LOG_LOADED) {
            Dimension d = this.getSize();
            MultiGraphLayeredPane lg = OpenLogViewerApp.getInstance().getMultiGraphLayeredPane();
            Graphics2D g2d = (Graphics2D) g;
            g2d.drawString("FPS: " + Double.toString(FPS), 30, 60);
            if (mouseOver) {
            	int zoom = OpenLogViewerApp.getInstance().getEntireGraphingPanel().getZoom();
            	int halfZoom = zoom / 2;
                int lineDraw = (((xMouseCoord - halfZoom) / zoom) * zoom) + zoom;  //divide by zoom then multiply by zoom effectively drops the remainder from the mouse coords
                g2d.setColor(vertBar);
                g2d.drawLine(d.width / 2, 0, d.width / 2, d.height);  //center position line 
                g2d.drawLine(lineDraw, 0, lineDraw, d.height);  //mouse cursor line

                for (int i = 0; i < lg.getComponentCount(); i++) {
                    if (lg.getComponent(i) instanceof SingleGraphPanel) {
                        SingleGraphPanel gl = (SingleGraphPanel) lg.getComponent(i);
                        g2d.setColor(textBackground);
                        g2d.fillRect(lineDraw, yMouseCoord + 2 + (15 * i), gl.getMouseInfo(xMouseCoord).toString().length() * 8, 15);
                        g2d.setColor(gl.getColor());
                        g2d.drawString(gl.getMouseInfo(xMouseCoord).toString(), lineDraw + 2, yMouseCoord + 15 + (15 * i));
                    }
                }
            }
        }
    }

    public void setLog(GenericLog log) {
        genLog = log;
        repaint();
    }
    

	@Override
	public void mouseEntered(MouseEvent e) {
		mouseOver = true;
	}

	@Override
	public void mouseExited(MouseEvent e) {
		mouseOver = false;
		repaint();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		xMouseCoord = e.getX();
		yMouseCoord = e.getY();
		repaint();
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseDragged(MouseEvent e) {

	}

    int FPScounter = 0;
    int FPS = 0;
    private long currentTime;
    private long builtTime;
    private GenericLog genLog;
    private Color vertBar = new Color(255, 255, 255, 100);
    private Color textBackground = new Color(0, 0, 0, 170);
    private int xMouseCoord;
    private int yMouseCoord;
    boolean mouseOver;
    private static final long serialVersionUID = -6657156551430700622L;
    
}
