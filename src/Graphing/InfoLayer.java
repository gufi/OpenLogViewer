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
package Graphing;

import GenericLog.GenericLog;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.JPanel;

/**
 *
 * @author Bryan
 */
public class InfoLayer extends JPanel implements MouseMotionListener, MouseListener {

    private LayeredGraph.Zoom zoom;
    private int logStatus;
    private int xMouseCoord;
    private int yMouseCoord;
    boolean mouseOver;
    private Color vertBar = new Color(255, 255, 255, 100);
    private Color textBackground = new Color(0, 0, 0, 170);


    public InfoLayer() {
        logStatus = GenericLog.LOG_NOT_LOADED;
        xMouseCoord = -100;
        xMouseCoord = -100;
        mouseOver = false;
        this.setOpaque(false);
        addMouseListener(this);
        addMouseMotionListener(this);
    }

    @Override
    public void paint(Graphics g) { // overriden paint because there will be no components in this pane

        if (!this.getSize().equals(this.getParent().getSize())) {
            this.setSize(this.getParent().getSize());
        }
        //if (this.getParent() instanceof LayeredGraph) {
        if (logStatus == GenericLog.LOG_NOT_LOADED) {
            g.setColor(Color.RED);
            g.drawString("No Log Loaded, Please select a log from the File menu.", 20, 20);
        } else if (logStatus == GenericLog.LOG_LOADING) {
            g.setColor(Color.red);
            g.drawString("loading Log, Please wait...", 20, 20);
        } else if (logStatus == GenericLog.LOG_LOADED) {
            Dimension d = this.getSize();
            LayeredGraph lp = (LayeredGraph) this.getParent();
            Graphics2D g2d = (Graphics2D) g;

            if (mouseOver) {
                g2d.setColor(vertBar);
                g2d.drawLine(d.width / 2, 0, d.width / 2, d.height);
                g2d.drawLine(this.xMouseCoord, 0, (int) this.xMouseCoord, d.height); // middle vertical divider,

                for (int i = 0; i < lp.getComponentCount(); i++) {
                    if (lp.getComponent(i) instanceof GraphLayer) {
                        GraphLayer gl = (GraphLayer) lp.getComponent(i);
                        g2d.setColor(textBackground);
                        g2d.fillRect(xMouseCoord , yMouseCoord + 2 + (15 * i), gl.getMouseInfo(xMouseCoord).toString().length() * 8, 15);
                        g2d.setColor(gl.getColor());
                        g2d.drawString(gl.getMouseInfo(xMouseCoord).toString(), xMouseCoord +2, yMouseCoord + 15 + (15 * i));
                    }
                }
            }
        }
    }

    public void setGraphStatus(int logStatus) {
        this.logStatus = logStatus;
        repaint();
    }

    
    public void setZoom(LayeredGraph.Zoom z){
        zoom = z;
    }

    //MOUSE MOTION LISTENER FUNCTIONALITY
    private void getMouseCoords(MouseEvent e) {
        xMouseCoord = e.getX();
        yMouseCoord = e.getY();
    }

    public void mouseDragged(MouseEvent e) {
        mouseMoved(e);
    }

    public void mouseMoved(MouseEvent e) {
        getMouseCoords(e);
        repaint(); // call repaint because otherwise we are at the whim of the speed of playback to update mouse info
    }

//MOUSE LISTENER FUNCTIONALITY
    public void mouseClicked(MouseEvent e) {
        LayeredGraph lg = (LayeredGraph) this.getParent();

        int move = (e.getX()/zoom.getZoom()) - (int) ((this.getSize().width / 2)/zoom.getZoom());
        if (move + lg.getCurrent() < lg.getCurrentMax()) {
            if (move + lg.getCurrent() < 0) {
                lg.setCurrent(0);
            } else {
                lg.setCurrent(lg.getCurrent() + move);
            }
            lg.initGraph();
            lg.repaint();
        }
    }

    public void mouseEntered(MouseEvent e) {
        mouseOver = true;
    }

    public void mouseExited(MouseEvent e) {
        mouseOver = false;
        repaint();
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }
}
