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

import GenericLog.GenericDataElement;
import GenericLog.GenericLog;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JLayeredPane;
import javax.swing.Timer;

/**
 *
 * @author Bryan
 */
public class LayeredGraph extends JLayeredPane implements ActionListener {

    private Timer timer;
    private GenericLog genLog;
    private int delay;
    private int current; // startpoint of where to start the graph ( data-wise )
    private boolean play; // true = play graph, false = pause graph
    private boolean antiAliasing;
    private InfoLayer infoLayer;
    int currentMax;
    //MouseMotion Flags
    //MouseListener Flags

    public LayeredGraph() {
        super();
        delay = 10;
        currentMax = 0;
        genLog = new GenericLog();
        timer = new Timer(delay, this);
        timer.setInitialDelay(0);
        play = false;
        current = 0;
        antiAliasing = false;
        infoLayer = new InfoLayer();
        
        init();
    }
    private void init() {
        infoLayer.setSize(400,600);
        this.setLayer(infoLayer, 99);
        this.setBackground(Color.BLACK);
        this.setOpaque(true);
        this.add(infoLayer);
    }

    public void actionPerformed(ActionEvent e) {
        if (play && current < currentMax && genLog.size() > 0) {
            current++;
            advanceGraph();
            repaint();
        }else {
            stop();
        }

    }

    public void addGraph(String header) {
        boolean p = play;
        if (p) {
            stop();
        }
        GraphLayer graph = new GraphLayer();
        graph.setSize(this.getSize());
        
        graph.setName(header);
        this.add(graph);
        graph.setData(genLog.get(header));
        getCurrentMax();
        if (p) {
            play();
        }
    }

    public boolean removeGraph(String header) {
        GenericDataElement temp = genLog.get(header);
        for (int i = 0; i < this.getComponentCount(); i++) {
            if (this.getComponent(i) instanceof GraphLayer) {
                GraphLayer t = (GraphLayer) this.getComponent(i);
                if (t.getData() == temp) {
                    this.remove(t);
                    return true;
                }
            }
        }
        return false;
    }

    private void removeAllGraphs() {
        for (int i = 0; this.getComponentCount() > 1; ) {
            if (this.getComponent(i) instanceof GraphLayer) {
                this.remove(getComponent(i));
            }
            else i++;
        }
        repaint();
    }

    public void setLog(GenericLog genLog) {
        play = false;
        current = 0;
        removeAllGraphs();
        this.genLog = genLog;
        if (genLog != null) {
            infoLayer.setGraphStatus(GenericLog.LOG_LOADING);
        }

    }

    public int getCurrentMax() {
        for(int i = 0; i < this.getComponentCount(); i++) {
            if(getComponent(i) instanceof GraphLayer){
                GraphLayer gl = (GraphLayer) getComponent(i);
                currentMax = gl.graphSize();
                return currentMax;
            }
        }
        return 0;
    }

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int c) {
        current = c;
    }

    public void initGraph() {
        for (int i = 0; i < this.getComponentCount(); i++) {
            if (this.getComponent(i) instanceof GraphLayer) {
                GraphLayer gl = (GraphLayer) this.getComponent(i);
                gl.initGraph();
            }
        }
    }

    public void advanceGraph() {
        for (int i = 0; i < this.getComponentCount(); i++) {
            if (this.getComponent(i) instanceof GraphLayer) {
                GraphLayer gl = (GraphLayer) this.getComponent(i);
                gl.advanceGraph();
            }
        }
    }

    /**
     * Allows the graph to begin animating
     */
    public void play() {

        if (this.play) {
            pause();
        } else {
            play = true;
            timer.start();
        }
    }

    /**
     * pauses the graphing playback
     */
    public void pause() {
        play = false;
        timer.stop();
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
        timer.stop();
        play = false;
    }

    public void reset() {
        current = 0;
        ///initGraph(this.getSize());
    }

    private void setTimerDelay() {
        timer.setDelay(this.delay);
    }

    public void setAntiAliasing(boolean tf) {
        antiAliasing = tf;
    }
    public void setStatus(int status) {
        infoLayer.setGraphStatus(status);
    }
    public void setColor(String header, Color newColor) {
        for(int i = 0; i < this.getComponentCount(); i++) {
            if(this.getComponent(i) instanceof GraphLayer && this.getComponent(i).getName().equals(header)) {
                GraphLayer gl = (GraphLayer)this.getComponent(i);
                gl.setColor(newColor);

            }
        }
    }

}
