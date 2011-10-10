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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JLayeredPane;
import javax.swing.Timer;
import org.diyefi.openlogviewer.genericlog.GenericDataElement;
import org.diyefi.openlogviewer.genericlog.GenericLog;

/**
 *
 * @author Bryan Harris
 */
public class MultiGraphLayeredPane extends JLayeredPane implements ActionListener {
	
	private Timer timer;
    private GenericLog genLog;
    private int delay;
    private int current; // startpoint of where to start the graph ( data-wise )
    private boolean play; // true = play graph, false = pause graph
    private InfoPanel infoPanel;
    int currentMax;
    private int totalSplits;
    Zoom zoom;
    private static final long serialVersionUID = 1213851792900715691L;

    public MultiGraphLayeredPane() {
        super();
        play = false;
        delay = 10;
        current = 0;
        currentMax = 0;
        genLog = new GenericLog();
        timer = new Timer(delay, this);
        timer.setInitialDelay(0);
        totalSplits = 1;
        infoPanel = new InfoPanel();
        infoPanel.setLog(genLog);
        zoom = new Zoom();

        init();
    }

    private void init() {
        infoPanel.setSize(400, 600);
        infoPanel.setZoom(zoom);
        this.setLayer(infoPanel, 99);
        this.setBackground(Color.BLACK);
        this.setOpaque(true);
        this.add(infoPanel);
    }

    public void actionPerformed(ActionEvent e) {

        if (play && current < currentMax && genLog.size() > 0) {
            current++;
            advanceGraph();
            repaint();
        } else {
            stop();
        }

    }

    public void addGraph(String header) {
        boolean p = play;
        if (p) {
            stop();
        }
        boolean found = false;
        for (int i = 0; i < this.getComponentCount() && !found; i++) {
            if (this.getComponent(i) instanceof SingleGraphPanel) {
                SingleGraphPanel gl = (SingleGraphPanel)this.getComponent(i);
                if(gl.getName().equals(header)){
                    found = true;
                }
            }
        }
        if (!found) {
            SingleGraphPanel graph = new SingleGraphPanel();
            graph.setZoom(zoom);
            graph.setSize(this.getSize());
            graph.setName(header);
            this.add(graph);
            this.addHierarchyBoundsListener(graph);// updates graph size automatically
            genLog.get(header).addPropertyChangeListener("Split", graph);
            graph.setData(genLog.get(header));

            setCurrentMax();
        }
        if (p) {
            play();
        }
    }

    public boolean removeGraph(String header) {
        GenericDataElement temp = genLog.get(header);
        for (int i = 0; i < this.getComponentCount(); i++) {
            if (this.getComponent(i) instanceof SingleGraphPanel) {
                SingleGraphPanel t = (SingleGraphPanel) this.getComponent(i);
                if (t.getData() == temp) {
                    this.remove(t);
                    this.removeHierarchyBoundsListener(t);
                    return true;
                }
            }
        }
        return false;
    }

    private void removeAllGraphs() {
        for (int i = 0; this.getComponentCount() > 2;) {  //Leave InfoLayer and PositionLayer in component count
            if (this.getComponent(i) instanceof SingleGraphPanel) {
                this.removeHierarchyBoundsListener((SingleGraphPanel) getComponent(i));
                this.remove(getComponent(i));

            } else {
                i++;
            }
        }
        repaint();
    }

    public void setLog(GenericLog log) {
        play = false;
        current = 0;
        removeAllGraphs();
        genLog = log;
        infoPanel.setLog(genLog);
        repaint();
    }

    public void setCurrentMax() {
        for (int i = 0; i < this.getComponentCount(); i++) {
            if (getComponent(i) instanceof SingleGraphPanel) {
                SingleGraphPanel gl = (SingleGraphPanel) getComponent(i);
                currentMax = gl.graphSize();
                break;
            }
        }
    }

    public int getCurrentMax() {
        return currentMax;
    }

    public int getCurrent() {
        return current;
    }
    
    public InfoPanel getInfoPanel(){
    	return infoPanel;
    }

    public void setCurrent(int c) {
        current = c;
    }

    public void initGraph() {
        for (int i = 0; i < this.getComponentCount(); i++) {
            if (this.getComponent(i) instanceof SingleGraphPanel) {
                SingleGraphPanel gl = (SingleGraphPanel) this.getComponent(i);
                gl.initGraph();
            }
        }
    }

    public void advanceGraph() {
        for (int i = 0; i < this.getComponentCount(); i++) {
            if (this.getComponent(i) instanceof SingleGraphPanel) {
                SingleGraphPanel gl = (SingleGraphPanel) this.getComponent(i);
                gl.advanceGraph();
            }
        }
    }

    public void zoomIn() {
        infoPanel.resetDragCoords();
        if (zoom.getZoom() <= 50) {
            zoom.setZoom(zoom.getZoom() + 1);
        }
        this.initGraph();
        repaint();
    }

    public void zoomOut() {
        infoPanel.resetDragCoords();
        if (zoom.getZoom() > 1) {
            zoom.setZoom(zoom.getZoom() - 1);
        }
        this.initGraph();
        repaint();
    }
    
    public Zoom getZoom(){
    	return zoom;
    }

    /**
     * Allows the graph to begin animating
     */
    public void play() {
        if (this.play) {
            pause();
        } else {
            infoPanel.resetDragCoords();
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

    public boolean isPlaying() {
        return this.play;
    }

    public void reset() {
        current = 0;
        this.initGraph();
    }

    private void setTimerDelay() {
        timer.setDelay(this.delay);
    }

    public void setColor(String header, Color newColor) {
        for (int i = 0; i < this.getComponentCount(); i++) {
            if (this.getComponent(i) instanceof SingleGraphPanel && this.getComponent(i).getName().equals(header)) {
                SingleGraphPanel gl = (SingleGraphPanel) this.getComponent(i);
                gl.setColor(newColor);

            }
        }
    }

    public int getTotalSplits() {
        return totalSplits;
    }

    public void setTotalSplits(int totalSplits) {
        if (totalSplits > 0) {
            this.totalSplits = totalSplits;
            for (int i = 0; i < this.getComponentCount(); i++) {
                if (this.getComponent(i) instanceof SingleGraphPanel) {
                    SingleGraphPanel gl = (SingleGraphPanel) this.getComponent(i);
                    gl.sizeGraph();
                }
            }
        }
    }

    public class Zoom {

        private int zoom = 1;

        public int getZoom() {
            return zoom;
        }

        public void setZoom(int z) {
            zoom = z;
        }
    }

    
}
