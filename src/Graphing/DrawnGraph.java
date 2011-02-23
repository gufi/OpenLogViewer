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

/**
 * This Class is no longer used for Graphing purposes, implementation has been moved to LayeredGraph GraphLayer and InfoLayer
 */
package Graphing;

import GenericLog.GenericDataElement;
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
import javax.swing.JLayeredPane;
import javax.swing.Timer;

/**
 *
 * @author Bryan
 */
public class DrawnGraph extends JLayeredPane implements ActionListener, Serializable, MouseMotionListener, MouseListener {

    private Timer timer;
    private GenericLog genLog;
    private int delay;
    private int current; // startpoint of where to start the graph ( data-wise )
    private boolean play; // true = play graph, false = pause graph
    private int xMouseCoord;
    private int yMouseCoord;
    private GenericLog yAxisData;
    private ArrayList<String> activeHeaders;// used to suplement the graph being drawn from a loop
    private Dimension prevD;
    private int currentMax;
    private boolean antiAliasing;
    //MouseMotion Flags
    //MouseListener Flags
    private boolean mouseEntered;
    private double FPS;
    private int FPScounter;
    private long currentTime;
    private long builtTime;
    private boolean showFPS;
    private ArrayList<Color> color;

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
        timer = new Timer(1000, this);
        timer.setInitialDelay(0);
        timer.start();
        play = false;
        current = 0;
        delay = 10;
        FPScounter = 0;
        FPS = 0;
        showFPS = false;
        //mouseinformation
        mouseEntered = false;
        xMouseCoord = -100;
        yMouseCoord = -100;
        prevD = new Dimension(this.getSize());
        currentTime = System.currentTimeMillis();
        antiAliasing = false;
        activeHeaders = new ArrayList();
        color = new ArrayList<Color>();
        addMouseListener(this);
        addMouseMotionListener(this);
    }

    private int getCurrentMax() {
        Iterator i = genLog.keySet().iterator();
        String head = "";
        if (i.hasNext()) {
            head = (String) i.next();
            GenericDataElement temp = genLog.get(head);
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
            advanceGraph(this.getSize());

        }
        repaint();
    }

    /**
     * This is an overridden method that does the painting of the graph based on a key
     * @param g
     */
    @Override
    public void paintComponent(Graphics g) {
        Dimension d = this.getSize();
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
        Graphics2D g2d = (Graphics2D) g;
        if (antiAliasing) { // turns on and off depending on graphics card and number of graphs, anti aliasing looks great but takes a toll on speed
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); // if statement is for performance during redraws
        }
        g2d.setColor(Color.BLACK); // set color of background of graph
        g2d.fillRect(0, 0, d.width, d.height); // Draw the background which is just flat black
        g2d.setColor(Color.GRAY);
        g2d.drawLine((int) (d.width / 2), 0, (int) (d.width / 2), d.height); // middle vertical divider,
        g2d.drawLine(0, (int) (d.height / 2), d.width, (int) (d.height / 2)); // middle horizontal divider
        g2d.setColor(Color.green);
        if (showFPS) {
            g2d.drawString("FPS: " + Double.toString(FPS), 30, 60);
            g2d.drawString("Delay:" + Integer.toString(delay), 30, 90);
        }
        // begin Graph drawing
        if (genLog.getLogStatus() == 1) {
            if (this.yAxisData == null) {
                currentMax = getCurrentMax();
                initGraph(d);
            } else {
                if (yAxisData.size() > 0) {
                    if (!prevD.equals(d)) {
                        this.initGraph(d); // here because of screen resizing
                    }
                    g2d.setColor(Color.RED);
                    g2d.drawString(Integer.toString((int) ((double) (current + 1) / (double) currentMax * 100)) + "%", 30, 30);

                    for (int y = 0; y < activeHeaders.size(); y++) {
                        GenericDataElement GDE = yAxisData.get(activeHeaders.get(y));
                        int[] xPoints;
                        g2d.setColor(color.get(y)); // set color of the drawn graph
                        xPoints = xAxis(GDE.size() - 1); // get the x Points
                        int[] yPoints = yAxis(activeHeaders.get(y), d); // get the y Points
                        g2d.drawPolyline(xPoints, yPoints, GDE.size() - 1); // draw the graph
                        //Draw Mouse location information
                        if (mouseEntered && this.xMouseCoord < GDE.size()) {
                            g2d.drawLine(this.xMouseCoord, 0, (int) this.xMouseCoord, d.height); // middle vertical divider,
                            g2d.setColor(color.get(y));

                            g2d.drawString(activeHeaders.get(y) + ": " + GDE.get(this.xMouseCoord).toString(), this.xMouseCoord + 15, this.yMouseCoord + 20 + (20 * y));
                        }
                    }
                    prevD.setSize(d); // remember last size incase screen gets resized will be used to properly modify yAxisData
                }


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
    private int[] yAxis(String key, Dimension d) {
        GenericDataElement GDE = yAxisData.get(key);
        int x = 0;
        int[] yAxis = new int[d.width];
        if (yAxisData != null && GDE.size() > 0) {
            while ((x < d.width) && (x < GDE.size() - 1)) {

                yAxis[x] = chartNumber(GDE.get(x), d.height, genLog.get(key).getMinValue(), genLog.get(key).getMaxValue());
                x++;
            }
        }
        return yAxis;

    }

    public void setAntiAlising(boolean tf) {
        this.antiAliasing = tf;
    }

    public boolean getAntiAliasing() {
        return this.antiAliasing;
    }

    private void initGraph(Dimension d) {


        // initialize GenericDataElement for first time
        if (genLog != null) {

            if (current < currentMax) {
                yAxisData = new GenericLog();// init
                for (int y = 0; y < activeHeaders.size(); y++) {


                    yAxisData.put(activeHeaders.get(y), new GenericDataElement());
                    GenericDataElement GDE = yAxisData.get(activeHeaders.get(y));
                    if (current < d.width / 2) {// starting of the graph
                        int x = 0;
                        while (x < (d.width / 2) - current) {
                            GDE.add(0.0);
                            x++;
                        }
                        GDE.addAll(genLog.get(activeHeaders.get(y)).subList(0, d.width - x));
                    } else if ((d.width / 2 + current) < currentMax) {// middle areas of the graph
                        GDE.addAll(genLog.get(activeHeaders.get(y)).subList(current - d.width / 2, d.width / 2 + current)); // set Data index
                    } else { // ending parts of the graph
                        GDE.addAll(genLog.get(activeHeaders.get(y)).subList(current - d.width / 2, genLog.get(activeHeaders.get(y)).size() - 1));
                    }
                    GDE.setMaxValue(genLog.get(activeHeaders.get(y)).getMaxValue());
                    GDE.setMinValue(genLog.get(activeHeaders.get(y)).getMinValue());
                }

            }
            System.gc();
            this.repaint();
        }
    }

    public void reInitGraph() {
        initGraph(this.getSize());
        currentMax = getCurrentMax();
    }

    private void advanceGraph(Dimension d) {
        for (int y = 0; y < activeHeaders.size(); y++) {
            if ((current + d.width / 2) < currentMax) {
                yAxisData.addValue(activeHeaders.get(y), genLog.get(activeHeaders.get(y)).get(current + d.width / 2));
            }
            //if (yAxisData.size() > 0 ) {
            if ((yAxisData.get(activeHeaders.get(y)).size() > d.width / 2 + 1)) {
                yAxisData.get(activeHeaders.get(y)).remove(0);
            } else {
                play = false;
            }
        }
    }

    /**
     * chartNumber converts the raw data given to it to a graphable point
     * @param <code>elemData</code> - data to be converted
     * @param <code>height</code> - height of the JPanel
     * @return <code>int</code> converted data from (height / (elemData / height ))
     */
    private int chartNumber(Double elemData, int height, double minValue, double maxValue) {
        int point = 0;
        //if (elemData != 0) {
        point = (int) (height - (height * ((elemData - minValue) / (maxValue - minValue))));
        //min value = this.height
        //max value = 0
        // conversion would be (elemdata - minValue ) / (maxValue-minValue)
        //}
        //else point = height;
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
        activeHeaders = new ArrayList<String>();
        color = new ArrayList<Color>();

    }

    public void addActiveHeader(String header) {
        activeHeaders.add(header);
        color.add(new Color(255, 255, 255));
    }

    public boolean removeActiveHeader(String header) {
        for (int i = 0; i < activeHeaders.size(); i++) {
            if (header.equals(activeHeaders.get(i))) {
                activeHeaders.remove(i);
                color.remove(i);
                return true;
            }
        }
        return false;
    }

    public void setColor(int i, Color c) {
        color.set(i, c);
        this.repaint();
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
        timer.setDelay(1000);

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
        timer.setDelay(1000);
    }

    public void reset() {
        current = 0;
        initGraph(this.getSize());
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
            this.initGraph(this.getSize());
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
