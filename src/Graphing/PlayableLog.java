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
package Graphing;

import GenericLog.GenericLog;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;
import java.util.ArrayList;
import javax.swing.Timer;

import javax.swing.JPanel;

/**
 *
 * @author Bryan
 */
public class PlayableLog extends JPanel implements ActionListener, Serializable {

    Timer timer;
    GenericLog genLog;
    int delay;
    int current; // startpoint of where to start the graph ( data-wise )
    boolean play; // true = play graph, false = pause graph

    /**
     * Create a blank JPanel of Playable Log type
     * typical usage of this would be to instantiate the object and then <br>
     * <code>playableLog.setLog(GenericLog log);</code>
     */
    public PlayableLog() {
        super();
        genLog = new GenericLog();
        timer = new Timer(0, this);
        timer.setInitialDelay(0);
        timer.start();
        play = false;
        current = 0;
        delay = 10;

    }

    /**
     * used by a swing.Timer object to change variables dealing with the actual animation of the class
     * @param e
     */
    public void actionPerformed(ActionEvent e) {

        if (play) {
            current++;
        }
        repaint();
    }

    /**
     * This is an overriden method that does the painting of the graph based on a key
     * @param g
     */
    @Override
    public void paintComponent(Graphics g) {
        //super.paintComponents(g);
        /* TO-DO:
         * Alot of this code needs to be moved to a on change listener and only have this function redraw the background from a imagebuffer
         * of sorts, in otherwords this way only the decorations have to be drawn when the screen is resized
         */

        Dimension d = this.getSize();
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.clearRect(0, 0, d.width, d.height); // clear the view otherwise previously drawn objects will continue to persist
        g2d.setBackground(Color.YELLOW); // this does nothing unsure why
        g2d.setColor(Color.BLACK); // set color of background of graph
        g2d.fillRect(0, 0, d.width, d.height); // Draw the background which is just flat black
        g2d.setColor(Color.GRAY);
        g2d.drawLine((int)(d.width/2), 0, (int)(d.width/2), d.height); // middle vertical divider,
        //g2d.setColor(Color.WHITE);
        g2d.drawLine(0, (int)(d.height/2), d.width,(int)(d.height/2)); // middle horizontal divider
        g2d.setColor(Color.BLUE); // set color of the drawn graph

        if (genLog.isLogLoaded() == 1) {
            int[] xPoints = xAxis(d.width); // get the x Points
            int[] yPoints = yAxis("SP5", d); // get the y Points
            g2d.drawPolyline(xPoints, yPoints, d.width); // draw the graph
        }
        else if(genLog.isLogLoaded() == 0) {
            g2d.setColor(Color.white);
            g2d.drawString("Loading Log Please wait...", 10, 10);
        }
        else if(genLog.isLogLoaded() == -1) {
            g2d.setColor(Color.white);
            g2d.drawString("Please Load a Log.", 10, 10);
        }


    }

    /**
     * This takes the width of <code>this</code> and creates a <code>int[]</code> with the x Coordinates of the graph
     * @param width - width of the JPanel
     * @return if the width of the JPanel is 800px the returned <code>int[]</code> will be int[width] with the contents of 0-799
     */
    private int[] xAxis(int width) {
        int[] xAxis = new int[width];
        int x = 0;
        while (x < xAxis.length) {
            xAxis[x] = x;
            x++;
        }
        return xAxis;
    }

    /**
     * This will control the Y coordinates of the graph, iterating through the data based on the <code>key</code>
     * @param key - Data that is to be converted the graph points
     * @param d - Dimensions of the graph
     * @return yAxis will return an <code>int[]</code> based on the data given
     */
    private int[] yAxis(String key, Dimension d) {
        int x = 0;
        ArrayList<Double> data = genLog.get(key);
        int[] yAxis = new int[d.width];
        while ((x < d.width) && (current + x < data.size() - 1)) {
            yAxis[x] = chartNumber(data.get(current + x), d.height);
            x++;
        }
        return yAxis;
    }

    /**
     * chartNumber converts the raw data given to it to a graphable point
     * @param <code>elemData</code> - data to be converted
     * @param <code>height</code> - height of the JPanel
     * @return <code>int</code> convted data from (height / (elemData / height ))
     */
    private int chartNumber(Double elemData, int height) {
        int point = 0;
        if (elemData != 0) {
            point = (int) (height / (elemData / height));
        }
        return point;
    }

    /**
     * Set the GenericLog of the Playablegraph, this is the dataset that all graphing will be based on
     * @param genLog - GenericLog
     */
    public void setLog(GenericLog genLog) {
        this.genLog = genLog;
    }

    /**
     * Allows the graph to begin animating
     */
    public void play() {
        timer.setDelay(delay);
        play = true;
    }

    public void pause() {
        play = false;
    }

    public void fastForward() {
       if((delay-1) >= 0) {
           delay--;
           timer.setDelay(delay);
       }
    }

    public void slowDown() {
       delay++;
       timer.setDelay(delay);
    }

    /**
     * Stops the graph from playing back
     */
    public void stop() {
        play = false;
    }
}
