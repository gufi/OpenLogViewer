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
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.Timer;

import javax.swing.JPanel;

/**
 *
 * @author Bryan
 */
public class DrawnGraph extends JPanel implements ActionListener, Serializable, MouseMotionListener, MouseListener {

    private Timer timer;
    private GenericLog genLog;
    private int delay;
    private int current; // startpoint of where to start the graph ( data-wise )
    private boolean play; // true = play graph, false = pause graph
    private int xMouseCoord;
    private int yMouseCoord;
    private ArrayList<Double> yAxisData;
    private String[] headers;// used to suplement the graph being drawn from a loop
    private Dimension prevD;
    private int currentMax;
    //MouseMotion Flags
    //MouseListener Flags
    private boolean mouseEntered;

    /**
     * Create a blank JPanel of Playable Log type
     * typical usage of this would be to instantiate the object and then <br>
     * <code>playableLog.setLog(GenericLog log);</code>
     */
    public DrawnGraph() {
        super();
        genLog = new GenericLog();
        currentMax = 0;
        yAxisData = null;
        timer = new Timer(0, this);
        timer.setInitialDelay(0);
        timer.start();
        play = false;
        current = 0;
        delay = 10;
        //mouseinformation
        mouseEntered = false;
        xMouseCoord = -100;
        yMouseCoord = -100;
        prevD = new Dimension(this.getSize());
        addMouseListener(this);

        addMouseMotionListener(this);


    }

    private int getCurrentMax() {
        Iterator i = genLog.keySet().iterator();
        ArrayList al;
        String head = "";
        if (i.hasNext()) {
            head = (String) i.next();
            ArrayList temp = genLog.get(head);
            return temp.size();

        } else {
            return 0;
        }
    }

    /**
     * used by a swing.Timer object to change variables dealing with the actual animation of the class
     * @param e
     */
    public void actionPerformed(ActionEvent e) {
        if (play) {
            current++;
            advanceGraph("SP5", this.getSize());
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
        g2d.setColor(Color.BLACK); // set color of background of graph
        g2d.fillRect(0, 0, d.width, d.height); // Draw the background which is just flat black
        g2d.setColor(Color.GRAY);
        g2d.drawLine((int) (d.width / 2), 0, (int) (d.width / 2), d.height); // middle vertical divider,
        g2d.drawLine(0, (int) (d.height / 2), d.width, (int) (d.height / 2)); // middle horizontal divider




        // begin Graph drawing


        if (genLog.getLogStatus() == 1) {
            if (this.yAxisData == null) {
                currentMax = getCurrentMax();
                initGraph("SP5", d);
            }
            //Draw Mouse location information
            if (mouseEntered && this.xMouseCoord < yAxisData.size()) {
                g2d.drawLine(this.xMouseCoord, 0, (int) this.xMouseCoord, d.height); // middle vertical divider,
                g2d.setColor(Color.red);
                g2d.drawString(yAxisData.get(this.xMouseCoord).toString(), this.xMouseCoord + 10, this.yMouseCoord + 10);
            }
            if (yAxisData.size() > 0) {
                if (!prevD.equals(d)) {
                    this.initGraph("SP5", d); // here because of screen resizing
                }

                g2d.drawString(Integer.toString(current), 30, 30);
                int[] xPoints;
                g2d.setColor(Color.BLUE); // set color of the drawn graph
                xPoints = xAxis(yAxisData.size() - 1); // get the x Points
                int[] yPoints = yAxis(d); // get the y Points
                g2d.drawPolyline(xPoints, yPoints, yAxisData.size() - 1); // draw the graph

                prevD.setSize(d); // remember last size incase screen gets resized will be used to properly modify yAxisData
            }
        } else if (genLog.getLogStatus() == 0) {
            g2d.setColor(Color.white);
            g2d.drawString("Loading Log Please wait...", 10, 10);
        } else if (genLog.getLogStatus() == -1) {
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

        int[] xAxis = new int[width];// fix this
        int x = 0;
        while (x < xAxis.length) {
            xAxis[x] = x;
            x++;
        }
        return xAxis;
    }

    /**
     * This will control the Y coordinates of the graph
     * @param key - Data that is to be converted the graph points
     * @param d - Dimensions of the graph
     * @return yAxis will return an <code>int[]</code> based on the data given
     */
    private int[] yAxis(Dimension d) {

        //System.out.println(yAxisData.size());
        int x = 0;
        int[] yAxis = new int[d.width];
        if (yAxisData != null && yAxisData.size() > 0) {
            while ((x < d.width) && (x < yAxisData.size() - 1)) {
                yAxis[x] = chartNumber(yAxisData.get(x), d.height);
                x++;
            }
        }
        return yAxis;

    }

    private void initGraph(String key, Dimension d) {


        // initialize arraylist for first time
        if (genLog != null) {
            ArrayList<Double> data = genLog.get(key); // get data array reference
            if (current < currentMax) {

                yAxisData = new ArrayList<Double>();// init
                if (current < d.width / 2) {// starting of the graph
                    int x = 0;
                    while (x < (d.width / 2) - current) {
                        yAxisData.add(0.0);
                        x++;
                    }
                    yAxisData.addAll(data.subList(0, d.width - x));
                } else if ((d.width / 2 + current) < currentMax) {// middle areas of the graph
                    yAxisData.addAll(data.subList(current - d.width / 2, d.width / 2 + current)); // set Data index
                } else { // ending parts of the graph
                    yAxisData.addAll(data.subList(current - d.width / 2, data.size() - 1));
                }
            }
            this.repaint();
        }
    }

    private void advanceGraph(String key, Dimension d) {
        ArrayList<Double> data = genLog.get(key); // get data array refernce

        if ((current + d.width / 2) < currentMax) {
            yAxisData.add(data.get(current + d.width / 2));
        }
        //if (yAxisData.size() > 0 ) {
        if ((yAxisData.size() > d.width / 2 + 1)) {
            yAxisData.remove(0);
        } else {
            play = false;
        }

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
        play = false;
        current = 0;
        this.genLog = genLog;

    }

    /**
     * Allows the graph to begin animating
     */
    public void play() {
        setTimerDelay();
        if (this.play) {
            play = false;
        } else {
            play = true;
        }
    }

    /**
     * pauses the graphing playback
     */
    public void pause() {
        play = false;
    }

    /**
     * increases speed of the graph by 1 ms untill 0, at which speed cannot be advanced any further and will essentially update as fast as possible
     */
    public void fastForward() {
        if ((delay - 1) >= 0) {
            delay--;
            setTimerDelay();
        }
    }

    /**
     * Slows the speed of playback by 1 ms
     */
    public void slowDown() {
        delay++;
        setTimerDelay();
    }

    /**
     * Stops the graph from playing back
     */
    public void stop() {
        play = false;
    }

    public void reset() {
        current = 0;
        initGraph("SP5", this.getSize());
    }

    private void setTimerDelay() {
        timer.setDelay(this.delay);
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

        int move = e.getX() - (int) (this.getSize().width / 2);
        if (move + current < currentMax) {
            if (move + current < 0) {
                current = 0;
            } else {
                current += move;
            }
            this.initGraph("SP5", this.getSize());
        }
    }

    public void mouseEntered(MouseEvent e) {
        mouseEntered = true;
    }

    public void mouseExited(MouseEvent e) {
        mouseEntered = false;
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }
}
